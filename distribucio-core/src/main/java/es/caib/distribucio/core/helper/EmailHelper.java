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
import org.springframework.mail.MailMessage;
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

	public void emailBustiaPendentContingut(
			BustiaEntity bustia,
			ContingutEntity contingut,
			ContingutMovimentEntity contenidorMoviment) {
		List<UsuariDto> destinataris = obtenirCodiDestinatarisPerEmail(bustia);
		
		StringBuilder sb = new StringBuilder("Desant emails nou contenidor a la bustia per a enviament (" +
				"bustiaId=" + (bustia != null ? bustia.getId() : "") + ")" +
				"contingutId=" + (contingut != null ? contingut.getId() : "") + " destinataris={");
		for (UsuariDto destinatari : destinataris)
			sb.append(destinatari.getCodi() + " " + destinatari.getEmail()).append(", ");
		sb.append("})");
		logger.debug(sb.toString());
		
		String unitatOrganitzativa = ""; 
		if (bustia != null && !destinataris.isEmpty())
			unitatOrganitzativa = getUnitatOrganitzativaNom(
					bustia.getEntitat(),
					bustia.getUnitatCodi());
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
	
	public void sendEmailBustiaPendentContingut(
			String emailDestinatari,
			boolean enviarAgrupat,
			List<ContingutMovimentEmailEntity> contingutMovimentEmails) {
		logger.debug("Enviament emails nou contenidor a bústies");
		
		String appBaseUrl = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.app.base.url");
		if (enviarAgrupat && contingutMovimentEmails.size() > 1) {
			
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
						"\t\tEnllaç: " + this.getEnllacContingut(appBaseUrl, bustia, contingut, entitat) + "\n\n";
			}
			missatge.setText(text);
			mailSender.send(missatge);
		} else {
			for (ContingutMovimentEmailEntity contingutEmail: contingutMovimentEmails) {
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
						"\tEnllaç: " + this.getEnllacContingut(appBaseUrl, bustia, contingut, entitat) + "\n");
				
				mailSender.send(missatge);
			}
		}
		
	}

	/** Mètode per construir un enllaç per accedir directament al contingut. L'enllaç és del tipus "http://localhost:8080/distribucio/contingut/642/registre/2669"
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

		StringBuilder url = new StringBuilder(appBaseUrl).append("/contingut/").append(bustia.getId()).append("/registre/").append(contingut.getId());
		if (entitat != null)
			url.append("?canviEntitat=").append(entitat.getId());
		return url.toString();
	}

	public void emailBustiaPendentRegistre(
			BustiaEntity bustia,
			RegistreEntity registre) {
		logger.debug("Enviament emails nova anotació de registre a la bústia (" +
				"bustiaId=" + bustia.getId() + ")" +
				"registreId=" + registre.getId() + ")");
		SimpleMailMessage missatge = new SimpleMailMessage();
		if (emplenarDestinataris(
				missatge,
				bustia)) {
			missatge.setFrom(getRemitent());
//			String unitatOrganitzativa = getUnitatOrganitzativaNom(
//					bustia.getEntitat(),
//					bustia.getUnitatCodi());
			String unitatOrganitzativa = bustia.getUnitatOrganitzativa().getDenominacio();
			missatge.setSubject(PREFIX_DISTRIBUCIO + " Nova anotació de registre rebuda a la bústia: " + bustia.getNom());
			missatge.setText(
					"Nova anotació de registre pendent de processar a la bústia:\n" +
					"\tEntitat: " + bustia.getEntitat().getNom() + "\n" +
					"\tUnitat organitzativa: " + unitatOrganitzativa + "\n" +
					"\tBústia: " + bustia.getNom() + "\n\n" +
					"Dades de l'anotació: \n" +
					"\tTipus: " + registre.getTipus() + "\n" +
					"\tNúmero: " + registre.getIdentificador() + "\n" +
					"\tData: " + registre.getData() + "\n" +
					"\tExtracte: " + registre.getExtracte() + "\n");
			mailSender.send(missatge);
		}
	}

	public List<UsuariDto> obtenirCodiDestinatarisPerEmail(BustiaEntity bustia) {
		List<UsuariDto> destinataris = new ArrayList<UsuariDto>();
		Set<String> usuaris = contenidorHelper.findUsuarisAmbPermisReadPerContenidor(bustia);
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
	
	private boolean emplenarDestinataris(
			MailMessage mailMessage,
			BustiaEntity bustia) {
		List<String> destinataris = new ArrayList<String>();
		Set<String> usuaris = contenidorHelper.findUsuarisAmbPermisReadPerContenidor(bustia);
		for (String usuari: usuaris) {
			DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(usuari);
			if (dadesUsuari != null && dadesUsuari.getEmail() != null)
				destinataris.add(dadesUsuari.getEmail());
		}
		if (!destinataris.isEmpty()) {
			mailMessage.setTo(destinataris.get(0));
			destinataris.remove(0);
			if (!destinataris.isEmpty()) {
				mailMessage.setCc(destinataris.toArray(new String[destinataris.size()]));
			}
			return true;
		} else {
			return false;
		}
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
