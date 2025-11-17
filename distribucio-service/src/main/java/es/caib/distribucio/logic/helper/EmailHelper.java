/**
 * 
 */
package es.caib.distribucio.logic.helper;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import es.caib.distribucio.logic.intf.dto.ArbreDto;
import es.caib.distribucio.logic.intf.dto.ArbreNodeDto;
import es.caib.distribucio.logic.intf.dto.PermisDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.exception.EmptyMailException;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.persist.entity.BustiaEntity;
import es.caib.distribucio.persist.entity.ContingutEntity;
import es.caib.distribucio.persist.entity.ContingutMovimentEmailEntity;
import es.caib.distribucio.persist.entity.ContingutMovimentEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.RegistreEntity;
import es.caib.distribucio.persist.entity.UsuariEntity;
import es.caib.distribucio.persist.repository.ContingutMovimentEmailRepository;
import es.caib.distribucio.persist.repository.ContingutMovimentRepository;
import es.caib.distribucio.persist.repository.UsuariRepository;
import es.caib.distribucio.plugin.usuari.DadesUsuari;

/**
 * Mètodes per a l'enviament de correus.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class EmailHelper {

	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private ContingutMovimentEmailRepository contingutMovimentEmailRepository;
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired 
	private ContingutMovimentRepository contingutMovimentRepository;
	@Autowired
	private ContingutHelper contingutHelper;

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
		for (UsuariDto destinatari : destinataris) {
			String emailToSend;
			if ((destinatari.getEmailAlternatiu()!=null)&&(!destinatari.getEmailAlternatiu().equals(""))) {
				emailToSend = destinatari.getEmailAlternatiu();
			} else {
				emailToSend = destinatari.getEmail();
			}
			sb.append(destinatari.getCodi() + " " + emailToSend).append(", ");
		}
		sb.append("})");
		logger.debug(sb.toString());
		// Comprova que hi hagi un moviment
		if (contenidorMoviment == null && bustia != null && contingut != null) {
			contenidorMoviment = contingut.getDarrerMoviment();
			if (contenidorMoviment == null) {
				logger.warn("El contingut amb id=" + contingut + " no té el darrer moviment informat. Es buscarà el darrer moviment a la taula de moviments.");
				// Cerca el darrer moviment
				List<ContingutMovimentEntity> moviments = contingutMovimentRepository.findByContingutOrderByCreatedDateAsc(contingut);
				if (!moviments.isEmpty()) {
					contenidorMoviment = moviments.get(moviments.size() - 1);
				} else {
					logger.warn("No s'ha trobat cap moviment pel contingut amb id=" + contingut.getId() + ". S'en crearà un de relacionat amb la bústia");
					contenidorMoviment = contingutHelper.ferIEnregistrarMoviment(contingut, bustia, null, false, null);
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
			String unitatOrganitzativa = getUnitatOrganitzativaNom(
					bustia.getEntitat(),
					bustia.getUnitatOrganitzativa().getCodi());
			List<ContingutMovimentEmailEntity> movEmails = new ArrayList<ContingutMovimentEmailEntity>();
			for (UsuariDto destinatari: destinataris) {
				String emailToSend;
				if ((destinatari.getEmailAlternatiu()!=null)&&(!destinatari.getEmailAlternatiu().equals(""))) {
					emailToSend = destinatari.getEmailAlternatiu();
				} else {
					emailToSend = destinatari.getEmail();
				}
				ContingutMovimentEmailEntity contingutMovimentEmail = ContingutMovimentEmailEntity.getBuilder(
						destinatari.getCodi(), 
						emailToSend,
						destinatari.getRebreEmailsAgrupats(),
						bustia, 
						contenidorMoviment, 
						contingut, 
						unitatOrganitzativa).build();
				movEmails.add(contingutMovimentEmail);
			}
			contingutMovimentEmailRepository.saveAll(movEmails);
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
		logger.trace("Enviament emails nou contenidor a bústies");
		String appBaseUrl = configHelper.getConfig("es.caib.distribucio.app.base.url");
		SimpleMailMessage missatge = new SimpleMailMessage();
		missatge.setTo(emailDestinatari);
		missatge.setFrom(getRemitent());
		missatge.setSubject(this.getPrefixDistribucio() + " Nous elements rebuts a les bústies");
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

	private String getPrefixDistribucio() {
		String entorn = configHelper.getConfig("es.caib.distribucio.default.user.entorn");
		String prefix;
		if (entorn != null) {
			prefix = "[DISTRIBUCIO-" + entorn + "]";
		}else {
			prefix = "[DISTRIBUCIO]";
		}
		return prefix;
	}

    public void sendEmailsAnotacionsErrorProcessament (Map<UsuariDto, Map<BustiaEntity, List<RegistreEntity>>> content) {
        for (UsuariDto usuariDto : content.keySet()) {
            if (usuariDto.getEmailErrorAnotacio()!=null && usuariDto.getEmailErrorAnotacio()) {
                sendEmailAnotacionsErrorProcessament(usuariDto, content.get(usuariDto));
            }
        }
    }
    public void sendEmailAnotacionsErrorProcessament (UsuariDto user, Map<BustiaEntity, List<RegistreEntity>> content) {
        try {
            SimpleMailMessage missatge = new SimpleMailMessage();
            missatge.setTo(user.getEmailAlternatiu() != null ? user.getEmailAlternatiu() : user.getEmail());
            missatge.setFrom(getRemitent());
            missatge.setSubject(this.getPrefixDistribucio() + " Anotacións amb error de processament");

            String mssg = "";
            for (BustiaEntity bustia: content.keySet()) {
                mssg += "Anotacions amb error de la bustia '" + bustia.getNom() + "': \n";
                for (RegistreEntity registre: content.get(bustia)) {
                    mssg += " - " + registre.getNom() + "\n";
                }
                mssg += "\n";
            }

            missatge.setText(mssg);
//            mailSender.send(missatge);
        } catch (Exception e) {
            logger.error("S'ha produit un error al intentar enviar correu de les anotacions amb error de processament a l'usuari " + user.getNom(), e.getMessage());
        }
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
			Long contingutEmailId) {
		logger.trace("Enviament email moviment a destinatari");
		ContingutMovimentEmailEntity contingutEmail = contingutMovimentEmailRepository.getReferenceById(contingutEmailId);
		String appBaseUrl = configHelper.getConfig("es.caib.distribucio.app.base.url");
		SimpleMailMessage missatge = new SimpleMailMessage();
		missatge.setTo(emailDestinatari);
		missatge.setFrom(getRemitent());
		missatge.setSubject(this.getPrefixDistribucio() + " Nou element rebut a la bústia: " + (contingutEmail.getBustia() != null ? contingutEmail.getBustia().getNom() : ""));
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
		logger.trace("Enviament email comentari a destinatari");
		String appBaseUrl = configHelper.getConfig("es.caib.distribucio.app.base.url");
		BustiaEntity bustia = null;
		ContingutEntity pare = contingut.getPare();
		if (pare != null && pare instanceof BustiaEntity)
			bustia = (BustiaEntity) pare;
		SimpleMailMessage missatge = new SimpleMailMessage();
		missatge.setTo(emailDestinatari);
		missatge.setFrom(getRemitent());
		missatge.setSubject(this.getPrefixDistribucio() + " Mencionat al comentari d'una anotació [" + contingut.getNom() + "]");
		EntitatEntity entitat = contingut.getEntitat();
		missatge.setText(
				"L'usuari " + usuariActual.getNom() + "(" + usuariActual.getCodi() + ") t'ha mencionat al comentari d'una anotació [" + contingut.getNom() + "]: \n" +
				"\tEntitat: " + (entitat != null ? entitat.getNom() : "") + "\n" +
				"\tNom anotació: " + (contingut != null ? contingut.getNom() : "") + "\n" +
				(bustia != null ? "\tEnllaç: " + this.getEnllacContingut(appBaseUrl, bustia, contingut, entitat) + "\n" : "") +
				"\tComentari: " + comentari + "\n");
		mailSender.send(missatge);
	}

	/** Envia un email d'avís de que s'ha assignat una anotació a un usuari.
	 * 
	 * @param emailDestinatari
	 * 			Email a qui s'enviarà l'email.
	 * @param contingutEmail
	 */
	public void sendEmailAvisAssignacio(
			String emailDestinatari,
			UsuariEntity usuariActual,
			ContingutEntity contingut,
			String comentari) {
		logger.trace("Enviament email comentari a destinatari");
		String appBaseUrl = configHelper.getConfig("es.caib.distribucio.app.base.url");
		BustiaEntity bustia = null;
		ContingutEntity pare = contingut.getPare();
		if (pare != null && pare instanceof BustiaEntity)
			bustia = (BustiaEntity) pare;
		SimpleMailMessage missatge = new SimpleMailMessage();
		missatge.setTo(emailDestinatari);
		missatge.setFrom(getRemitent());
		missatge.setSubject(this.getPrefixDistribucio() + " Assignat a una anotació [" + contingut.getNom() + "]");
		EntitatEntity entitat = contingut.getEntitat();
		missatge.setText(
				"L'usuari " + usuariActual.getNom() + "(" + usuariActual.getCodi() + ") t'ha assignat l'anotació [" + contingut.getNom() + "]: \n" +
				"\tEntitat: " + (entitat != null ? entitat.getNom() : "") + "\n" +
				"\tNom anotació: " + (contingut != null ? contingut.getNom() : "") + "\n" +
				(bustia != null ? "\tEnllaç: " + this.getEnllacContingut(appBaseUrl, bustia, contingut, entitat) + "\n" : "") +
				(comentari != null ? "\tComentari: " + comentari + "\n" : ""));
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
	public void contingutAlliberatPerAltreUsuari(
			RegistreEntity registreEntity, 
			UsuariEntity usuariActual,
			UsuariEntity usuariResponsableBloqueig) {
		if (usuariActual.getEmail() == null || usuariActual.getEmail().isEmpty())
			throw new EmptyMailException("L'usuari que té agafada l'anotació no disposa d'un correu electrònic assigant. Contacti amb l'administrador per alliberar l'anotació.");
		SimpleMailMessage missatge = new SimpleMailMessage();
		missatge.setFrom(getRemitent());
		missatge.setTo(usuariActual.getEmail());
		missatge.setSubject(this.getPrefixDistribucio() + " Registre alliberat per un altre usuari: [" + registreEntity.getNom() + "]");
		EntitatEntity entitat = registreEntity.getEntitat();
		missatge.setText("Informació del registre:\n" +
				"\tEntitat: " + entitat.getNom() + "\n" +
				"\tNúmero: " + registreEntity.getNumero() + "\n" +
				"\tNom: " + registreEntity.getNom() + "\n\n" + 
				"\tPersona responsable de l'alliberament: " + usuariResponsableBloqueig.getNom() + "(" + usuariResponsableBloqueig.getCodi() + ").");
		mailSender.send(missatge);
	}

	/**
	 * Avisa de la reactivació d'una anotació processada
	 * 
	 * @param registrePerReenviar
	 * 			El registre a reactivar/sobresciure amb el nou contingut (comentari, remitent)
	 * @param contingutMoviment
	 * 			El darrer moviment que correspon amb el registre a reactivar
	 * @param procesEstatActual
	 * 			El nou estat del registre
	 * @param procesEstatAnterior
	 * 			L'estat anterior del registre
	 */
	public void sendEmailReactivacioAnotacio(
			RegistreEntity registrePerReenviar,
			ContingutMovimentEntity contingutMoviment,
			RegistreProcesEstatEnum procesEstatActual,
			RegistreProcesEstatEnum procesEstatAnterior) {
		logger.trace("Enviament email avís reactivació anotació");
		String appBaseUrl = configHelper.getConfig("es.caib.distribucio.app.base.url");
		BustiaEntity bustia = null;
		ContingutEntity pare = registrePerReenviar.getPare();
		if (pare != null && pare instanceof BustiaEntity)
			bustia = (BustiaEntity) pare;
		List<UsuariDto> destinataris = obtenirCodiDestinatarisPerEmail(bustia);
		if (appBaseUrl != null) {
			for (UsuariDto usuariDto : destinataris) {
				String emailDestinatari = usuariDto.getEmail();
				if (emailDestinatari != null) {
				SimpleMailMessage missatge = new SimpleMailMessage();
					missatge.setTo(emailDestinatari);
					missatge.setFrom(getRemitent());
					missatge.setSubject(this.getPrefixDistribucio() + " L'anotació " + registrePerReenviar.getNom() + " ha canviat d'estat.");
					EntitatEntity entitat = registrePerReenviar.getEntitat();
					missatge.setText(
							"\tEntitat: " + (entitat != null ? entitat.getNom() : "") + "\n" +
							"\tNom anotació: " + (contingutMoviment != null ? registrePerReenviar.getNom() : "") + "\n" +
							(bustia != null ? "\tEnllaç: " + this.getEnllacContingut(appBaseUrl, bustia, registrePerReenviar, entitat) + "\n" : "") +
							"\tEstat anterior: " + messageHelper.getMessage("registre.proces.estat.enum." + procesEstatAnterior) + "\n" + 
							"\tNou estat: " + messageHelper.getMessage("registre.proces.estat.enum." + procesEstatActual) + "\n");
					mailSender.send(missatge);
				}
			}
		} else {
			throw new RuntimeException("Falta configurar la propietat base url per l'enviament de correus es.caib.distribucio.app.base.url");
		}
	}

	/**
	 * Avisa de la duplicitat dels registres i les vegades que arriba per duplicat
	 * 
	 * @param contingutMoviments
	 * 			Moviments duplicats
	 * @param bustia 
	 * 			Bústia on arriben les anotacions duplicades
	 * 
	 */
	public void sendEmailDuplicacioAnotacio(List<ContingutMovimentEntity> contingutMoviments, BustiaEntity bustia) {
		logger.trace("Enviament email avís duplicitat anotació");
		String appBaseUrl = configHelper.getConfig("es.caib.distribucio.app.base.url");
		if (appBaseUrl != null && !contingutMoviments.isEmpty()) {
			List<UsuariDto> destinataris = obtenirCodiDestinatarisPerEmail(bustia);
			if (!destinataris.isEmpty()) {
				for (UsuariDto usuariDto : destinataris) {
					SimpleMailMessage missatge = new SimpleMailMessage();
					missatge.setSubject(this.getPrefixDistribucio() + " S'han rebut anotacions duplicades de la bústia: " + bustia.getNom());
					missatge.setText("Les següents anotacions han estat modificades amb un nou contingut: \n\n");
					for (ContingutMovimentEntity contingutMoviment : contingutMoviments) {
						BustiaEntity bustiaMoviment = null;
						RegistreEntity registrePerReenviar = (RegistreEntity) contingutMoviment.getContingut();
						ContingutEntity pare = registrePerReenviar.getPare();
						if (pare != null && pare instanceof BustiaEntity)
							bustiaMoviment = (BustiaEntity) pare;
							String emailDestinatari = usuariDto.getEmail();
							if (emailDestinatari != null) {
								missatge.setTo(emailDestinatari);
								missatge.setFrom(getRemitent());
								EntitatEntity entitat = registrePerReenviar.getEntitat();
								missatge.setText(
										missatge.getText() + 
										"\tEntitat: " + (entitat != null ? entitat.getNom() : "") + "\n" +
										"\tNom anotació: " + (contingutMoviment != null ? registrePerReenviar.getNom() : "") + "\n" +
										(bustiaMoviment != null ? "\tEnllaç: " + this.getEnllacContingut(appBaseUrl, bustiaMoviment, registrePerReenviar, entitat) + "\n" : "") +
										"\tEstat: " + messageHelper.getMessage("registre.proces.estat.enum." + registrePerReenviar.getProcesEstat()) + "\n" + 
										"\tNº duplicat: " + contingutMoviment.getNumDuplicat() + "\n" +
										"\t-------------------------------------------------------\n");
							}
					}
					mailSender.send(missatge);
				}
			}
		} else {
			throw new RuntimeException("Falta configurar la propietat base url per l'enviament de correus es.caib.distribucio.app.base.url");
		}
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

	/** Mètode per consultar tots els usuaris amb permís sobre la bústia ja sigui per rol o per
	 * permís directe. Si falla la consulta al plugin d'usuaris llavors no es propagar l'excepció per 
	 * no interrompre la creació de l'anotació i es posa un missatge d'error als logs.
	 * 
	 * @param bustia
	 * @return
	 */
	public List<UsuariDto> obtenirCodiDestinatarisPerEmail(BustiaEntity bustia) {
		List<UsuariDto> destinataris = new ArrayList<UsuariDto>();
		try {
			Set<String> usuaris = findUsuarisCodisAmbPermisReadPerContenidor(bustia);
			for (String usuari: usuaris) {
				DadesUsuari dadesUsuari = cacheHelper.findUsuariAmbCodi(usuari);
				if (dadesUsuari != null && dadesUsuari.getEmail() != null) {
					UsuariEntity user = usuariRepository.findById(usuari).orElse(null);
					UsuariDto u = new UsuariDto();
					if (user != null) {
						if (user.isRebreEmailsBustia()) {
							u = conversioTipusHelper.convertir(user,UsuariDto.class);
							if ((user.getEmailAlternatiu()!=null)&&(!user.getEmailAlternatiu().equals(""))) {
								u.setEmailAlternatiu(user.getEmailAlternatiu());
							} else {
								u.setEmailAlternatiu(dadesUsuari.getEmail());
							}
							u.setRebreEmailsAgrupats(user.isRebreEmailsAgrupats());
							destinataris.add(u);
						}
					} else {
						u = new UsuariDto();
						u.setCodi(usuari);
						u.setEmail(dadesUsuari.getEmail());
						u.setEmailAlternatiu(dadesUsuari.getEmail());
						u.setRebreEmailsAgrupats(true);
						destinataris.add(u);
					}
				}
			}
		} catch(Exception e) {
			logger.error("Error consultant els usuaris amb permís per la bústia " + bustia.getId() + bustia.getNom() + " " + 
						 	(bustia.getPare() != null ? "(" + bustia.getPare().getNom() + ") " : "") + ": " + e.toString(), 
						 e);
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
		return configHelper.getConfig("es.caib.distribucio.email.remitent");
	}

	private Set<String> findUsuarisCodisAmbPermisReadPerContenidor(
			ContingutEntity contingut) {
		List<PermisDto> permisos = new ArrayList<PermisDto>();
		if (contingut instanceof BustiaEntity) {
			permisos = permisosHelper.findPermisos(
					contingut.getId(),
					BustiaEntity.class);
		}
		Set<String> usuaris = new HashSet<String>();
		for (PermisDto permis: permisos) {
			switch (permis.getPrincipalTipus()) {
			case USUARI:
				usuaris.add(permis.getPrincipalNom());
				break;
			case ROL:
				if (!"tothom".equals(permis.getPrincipalNom())) {
					List<DadesUsuari> usuarisGrup = pluginHelper.dadesUsuariFindAmbGrup(
							permis.getPrincipalNom());
					if (usuarisGrup != null) {
						for (DadesUsuari usuariGrup: usuarisGrup) {
							usuaris.add(usuariGrup.getCodi());
						}
					}
				}
				break;
			}
		}
		return usuaris;
	}

	private static final Logger logger = LoggerFactory.getLogger(EmailHelper.class);

}
