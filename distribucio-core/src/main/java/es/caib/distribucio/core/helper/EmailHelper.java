/**
 * 
 */
package es.caib.distribucio.core.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import es.caib.distribucio.core.api.dto.ArbreDto;
import es.caib.distribucio.core.api.dto.ArbreNodeDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.dto.UsuariDto;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.ContingutEntity;
import es.caib.distribucio.core.entity.ContingutMovimentEmailEntity;
import es.caib.distribucio.core.entity.ContingutMovimentEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.UsuariEntity;
import es.caib.distribucio.core.repository.ContingutMovimentEmailRepository;
import es.caib.distribucio.core.repository.ContingutMovimentRepository;
import es.caib.distribucio.core.repository.UsuariRepository;
import es.caib.distribucio.plugin.usuari.DadesUsuari;

/**
 * Mètodes per a l'enviament de correus.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EmailHelper {

	private static final String PREFIX_DISTRIBUCIO = "[DISTRIBUCIO]";

	@Resource
	private UsuariRepository usuariRepository;
	@Resource
	private ContingutMovimentEmailRepository contingutMovimentEmailRepository;
	@Resource 
	private ContingutMovimentRepository contingutMovimentRepository;
	
	@Resource
	private JavaMailSender mailSender;
	
	@Resource
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;

	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private ContingutHelper contenidorHelper;
	@Resource
	private PermisosHelper permisosHelper;

	public void createEmailsPendingToSend(
			BustiaEntity bustia,
			ContingutEntity contingut,
			ContingutMovimentEntity contenidorMoviment) {
		List<UsuariDto> destinataris = obtenirCodiDestinatarisPerEmail(bustia);
		
		StringBuilder sb = new StringBuilder("Desant emails nou contenidor a la bustia per a enviament (" +
				"bustiaId=" + (bustia != null ? bustia.getId() : "") + "" +
				", contingutId=" + (contingut != null ? contingut.getId() : "") +
				", contenidorMovimentId=" + (contenidorMoviment != null ? contenidorMoviment.getId() : "null") +
				", destinataris={");
		for (UsuariDto destinatari : destinataris)
			sb.append(destinatari.getCodi() + " " + destinatari.getEmail()).append(", ");
		sb.append("})");
		logger.debug(sb.toString());

		// Comprova que hi hagi un moviment
		if (contenidorMoviment == null && bustia != null && contingut != null) {
			contenidorMoviment = contingut.getDarrerMoviment();
			if (contenidorMoviment == null) {
				logger.warn("El contingut amb id=" + contingut + " no té el darrer moviment informat. Es buscarà el darrer moviment a la taula de moviments.");
				// Cerca el darrer moviment
				List<ContingutMovimentEntity> moviments = contingutMovimentRepository.findByContingutOrderByCreatedDateAsc(contingut);
				if (!moviments.isEmpty()) 
					contenidorMoviment = moviments.get(moviments.size() - 1);
				else {
					logger.warn("No s'ha trobat cap moviment pel contingut amb id=" + contingut.getId() + ". S'en crearà un de relacionat amb la bústia");
					contenidorMoviment = contenidorHelper.ferIEnregistrarMoviment(contingut, bustia, null, false);
				}
			}			
		}
		// Validació
		if (bustia == null 
				|| contingut == null 
				|| contenidorMoviment == null ) {
			logger.error("No es pot enviar un email de notificacio si la bustia, el contingut o el moviment son nulls: " + sb.toString());
			return;
		}
		if (!destinataris.isEmpty()) {
			String unitatOrganitzativa = getUnitatOrganitzativaNom( bustia.getEntitat(),
																	bustia.getUnitatOrganitzativa().getCodi());			
			List<ContingutMovimentEmailEntity> movEmails = new ArrayList<ContingutMovimentEmailEntity>();
			for (UsuariDto destinatari: destinataris) {
				ContingutMovimentEmailEntity contingutMovimentEmail = ContingutMovimentEmailEntity.getBuilder(
						destinatari.getCodi(), 
						destinatari.getEmail(),
						destinatari.getRebreEmailsAgrupats(),
						bustia, 
						contenidorMoviment, 
						contingut, 
						unitatOrganitzativa).build();
				movEmails.add(contingutMovimentEmail);
			}
			contingutMovimentEmailRepository.save(movEmails);
		}
	}
	
	/** Envia un email d'avís amb els continguts pendents de notificar agrupats en el mateix email.
	 * 
	 * @param emailDestinatari
	 * 			Email a qui s'enviarà l'email.
	 * @param contingutMovimentEmails
	 * 			Llista de contiguts pendents d'avís per email.
	 */
	public void sendEmailAvisAgrupatNousElementsBustia(
			String emailDestinatari,
			List<ContingutMovimentEmailEntity> contingutMovimentEmails) {
		
		logger.debug("Enviament emails nou contenidor a bústies");
		
		String appBaseUrl = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.app.base.url");
			
		SimpleMailMessage missatge = new SimpleMailMessage();
		missatge.setTo(emailDestinatari);
		missatge.setFrom(getRemitent());
		missatge.setSubject(PREFIX_DISTRIBUCIO + " Nous elements rebuts a les bústies");
		
		BustiaEntity bustia = null;
		EntitatEntity entitat = null;
		String text = "";
		Integer contadorElement = 1;

		for (ContingutMovimentEmailEntity contingutEmail: contingutMovimentEmails) {
			if (bustia == null || !contingutEmail.getBustia().getId().equals(bustia.getId())) { 
				bustia = contingutEmail.getBustia();
				entitat = bustia != null ? bustia.getEntitat() : null;
				text += "\nNous elements rebuts a la bústia:\n" +
						"\tEntitat: " + (entitat != null ? entitat.getNom() : "") + "\n" +
						"\tUnitat organitzativa: " + contingutEmail.getUnitatOrganitzativa() + "\n" +
						"\tBústia: " + (bustia != null ? bustia.getNom() : "") + "\n\n";
				contadorElement = 1;
			}
			ContingutEntity contingut = contingutEmail.getContingut();
			ContingutMovimentEntity contenidorMoviment = contingutEmail.getContingutMoviment();
			String tipus = contingut.getContingutType();
			text += "\t" + contadorElement++ + ". Dades de l'element: \n" +
					"\t\tTipus: " + tipus + "\n" +
					"\t\tNom: " + (contingut != null ? contingut.getNom() : "") + "\n" +
					"\t\tRemitent: " + ((contenidorMoviment != null && contenidorMoviment.getRemitent() != null) ? contenidorMoviment.getRemitent().getNom() : "") + "\n" +
					"\t\tComentari: " + ((contenidorMoviment != null && contenidorMoviment.getComentari() != null) ? contenidorMoviment.getComentari() : "") + "\n" +
					"\t\tEnllaç: " + this.getEnllacContingut(appBaseUrl, bustia, contingut, entitat) + "\n\n" +
					(contenidorMoviment.getComentariDestins() != null ? contenidorMoviment.getComentariDestins() : "");
		}
		missatge.setText(text);
		mailSender.send(missatge);
	}
	
	/** Envia un email d'avís amb un contingut pendent de notificar per email. Es diferencia del mètode agrupat perquè només envia
	 * un moviment i canvia l'assumpte i el cos del missatge.
	 * 
	 * @param emailDestinatari
	 * 			Email a qui s'enviarà l'email.
	 * @param contingutEmail
	 */
	public void sendEmailAvisSimpleNouElementBustia(
			String emailDestinatari,
			ContingutMovimentEmailEntity contingutEmail) {
		logger.debug("Enviament email moviment a destinatari");
		
		String appBaseUrl = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.app.base.url");

		SimpleMailMessage missatge = new SimpleMailMessage();
		missatge.setTo(emailDestinatari);
		missatge.setFrom(getRemitent());
		missatge.setSubject(PREFIX_DISTRIBUCIO + " Nou element rebut a la bústia: " + (contingutEmail.getBustia() != null ? contingutEmail.getBustia().getNom() : ""));
		BustiaEntity bustia = contingutEmail.getBustia();
		EntitatEntity entitat = bustia != null ? bustia.getEntitat() : null;
		ContingutEntity contingut = contingutEmail.getContingut();
		ContingutMovimentEntity contenidorMoviment = contingutEmail.getContingutMoviment();
		String tipus = contingut.getContingutType();
		missatge.setText(
				"Nou element rebut a la bústia:\n" +
				"\tEntitat: " + (entitat != null ? entitat.getNom() : "") + "\n" +
				"\tUnitat organitzativa: " + contingutEmail.getUnitatOrganitzativa() + "\n" +
				"\tBústia: " + (bustia != null ? bustia.getNom() : "") + "\n\n" +
				"Dades de l'element: \n" +
				"\tTipus: " + tipus + "\n" +
				"\tNom: " + (contingut != null ? contingut.getNom() : "") + "\n" +
				"\tRemitent: " + ((contenidorMoviment != null && contenidorMoviment.getRemitent() != null) ? contenidorMoviment.getRemitent().getNom() : "") + "\n" +
				"\tComentari: " + ((contenidorMoviment != null && contenidorMoviment.getComentari() != null) ? contenidorMoviment.getComentari() : "") + "\n" +
				"\tEnllaç: " + this.getEnllacContingut(appBaseUrl, bustia, contingut, entitat) + "\n\n" +
				(contenidorMoviment.getComentariDestins() != null ? contenidorMoviment.getComentariDestins() : ""));
		
		mailSender.send(missatge);		
	}
	
	/** Envia un email d'avís amb un contingut pendent de notificar per email. Es diferencia del mètode agrupat perquè només envia
	 * un moviment i canvia l'assumpte i el cos del missatge.
	 * 
	 * @param emailDestinatari
	 * 			Email a qui s'enviarà l'email.
	 * @param contingutEmail
	 */
	public void sendEmailAvisMencionatComentari(
			String emailDestinatari,
			UsuariEntity usuariActual,
			ContingutEntity contingut,
			String comentari) {
		logger.debug("Enviament email comentari a destinatari");
		String appBaseUrl = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.app.base.url");
		BustiaEntity bustia = null;
		ContingutEntity pare = contingut.getPare();
		if (pare != null && pare instanceof BustiaEntity)
			bustia = (BustiaEntity) pare;
		
		SimpleMailMessage missatge = new SimpleMailMessage();
		missatge.setTo(emailDestinatari);
		missatge.setFrom(getRemitent());
		missatge.setSubject(PREFIX_DISTRIBUCIO + " Mencionat al comentari d'una anotació [" + contingut.getNom() + "]");
		EntitatEntity entitat = contingut.getEntitat();
		missatge.setText(
				"L'usuari " + usuariActual.getNom() + "(" + usuariActual.getCodi() + ") t'ha mencionat al comentari d'una anotació [" + contingut.getNom() + "]: \n" +
				"\tEntitat: " + (entitat != null ? entitat.getNom() : "") + "\n" +
				"\tNom anotació: " + (contingut != null ? contingut.getNom() : "") + "\n" +
				(bustia != null ? "\tEnllaç: " + this.getEnllacContingut(appBaseUrl, bustia, contingut, entitat) + "\n" : "") +
				"\tComentari: " + comentari + "\n");
		
		mailSender.send(missatge);		
	}
	
	/**
	 * Envia un email d'avís quan un usuari agafa una anotació que ja està reservada per un altre usuari prèviament.
	 * 
	 * @param registreEntity
	 * 			La informació del registre agafat
	 * @param usuariActual
	 * 			Usuari que té agafada l'anotació
	 * @param usuariNou
	 * 			L'usuari nou que agafa l'anotació
	 */
	public void contingutAlliberatPerAltreUsusari(
			RegistreEntity registreEntity, 
			UsuariEntity usuariActual,
			UsuariEntity usuariResponsableBloqueig) {
		SimpleMailMessage missatge = new SimpleMailMessage();
		missatge.setFrom(getRemitent());
		missatge.setTo(usuariActual.getEmail());
		missatge.setSubject(PREFIX_DISTRIBUCIO + " Registre alliberat per un altre usuari: [" + registreEntity.getNom() + "]");
		EntitatEntity entitat = registreEntity.getEntitat();
		missatge.setText("Informació del registre:\n" +
				"\tEntitat: " + entitat.getNom() + "\n" +
				"\tNúmero: " + registreEntity.getNumero() + "\n" +
				"\tNom: " + registreEntity.getNom() + "\n\n" + 
				"\tPersona responsable de l'alliberament: " + usuariResponsableBloqueig.getNom() + "(" + usuariResponsableBloqueig.getCodi() + ").");
		
		mailSender.send(missatge);		
	}


	/** Mètode per construir un enllaç per accedir directament al contingut. L'enllaç és del tipus "http://localhost:8080/distribucio/registreUser/bustia/642/registre/2669"
	 * 
	 * @param appBaseUrl
	 * @param bustia
	 * @param contingut
	 * @param entitat
	 * @return
	 */
	private String getEnllacContingut(
			String appBaseUrl, 
			BustiaEntity bustia, 
			ContingutEntity contingut,
			EntitatEntity entitat) {

		StringBuilder url = new StringBuilder(appBaseUrl).append("/registreUser/bustia/").append(bustia.getId()).append("/registre/").append(contingut.getId());
		if (entitat != null)
			url.append("/?canviEntitat=").append(entitat.getId());
		return url.toString();
	}

	public List<UsuariDto> obtenirCodiDestinatarisPerEmail(BustiaEntity bustia) {
		List<UsuariDto> destinataris = new ArrayList<UsuariDto>();
		Set<String> usuaris = contenidorHelper.findUsuarisCodisAmbPermisReadPerContenidor(bustia);
		for (String usuari: usuaris) {
			DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(usuari);
			if (dadesUsuari != null && dadesUsuari.getEmail() != null) {
				UsuariEntity user = usuariRepository.findOne(usuari);
				if (user == null || user.isRebreEmailsBustia()) {
					UsuariDto u = new UsuariDto();
					u.setCodi(usuari);
					u.setEmail(dadesUsuari.getEmail());
					u.setRebreEmailsAgrupats(user == null ? true : user.isRebreEmailsAgrupats());
					destinataris.add(u);
				}
			}
		}
		return destinataris;
	}
	

	
	private String getUnitatOrganitzativaNom(
			EntitatEntity entitat,
			String unitatOrganitzativaCodi) {
		ArbreDto<UnitatOrganitzativaDto> arbreUnitats = unitatOrganitzativaHelper.unitatsOrganitzativesFindArbreByPare(
				entitat.getCodiDir3());
		for (ArbreNodeDto<UnitatOrganitzativaDto> node: arbreUnitats.toList()) {
			UnitatOrganitzativaDto unitat = node.getDades();
			if (unitat.getCodi().equals(unitatOrganitzativaCodi)) {
				return unitat.getDenominacio();
			}
		}
		return null;
	}
	public String getRemitent() {
		return PropertiesHelper.getProperties().getProperty("es.caib.distribucio.email.remitent");
	}

	private static final Logger logger = LoggerFactory.getLogger(EmailHelper.class);

}
