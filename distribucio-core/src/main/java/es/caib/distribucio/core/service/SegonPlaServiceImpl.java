/**
 * 
 */
package es.caib.distribucio.core.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.BackofficeTipusEnumDto;
import es.caib.distribucio.core.api.service.SegonPlaService;
import es.caib.distribucio.core.entity.ContingutMovimentEmailEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.ReglaEntity;
import es.caib.distribucio.core.helper.BustiaHelper;
import es.caib.distribucio.core.helper.EmailHelper;
import es.caib.distribucio.core.helper.PropertiesHelper;
import es.caib.distribucio.core.helper.RegistreHelper;
import es.caib.distribucio.core.repository.ContingutMovimentEmailRepository;
import es.caib.distribucio.core.repository.RegistreRepository;

/**
 * Implementació dels mètodes per a gestionar accions en segon pla.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class SegonPlaServiceImpl implements SegonPlaService {
	
	@Autowired
	private EmailHelper emailHelper;
	@Autowired
	private ContingutMovimentEmailRepository contingutMovimentEmailRepository;
	@Autowired
	private BustiaHelper bustiaHelper;
	@Autowired
	private RegistreRepository registreRepository;
	@Autowired
	private RegistreHelper registreHelper;

	private static Map<Long, String> errorsMassiva = new HashMap<Long, String>();

	/**
	 * Tries to save anotacions and annexos in arxiu specified number of times 
	 */
	@Override
	@Scheduled(
			fixedDelayString = "${config:es.caib.distribucio.tasca.guardar.annexos.temps.espera.execucio}")
	public void guardarAnotacionsPendentsEnArxiu() {
		if (bustiaHelper.isProcessamentAsincronProperty()) {
			logger.debug("Execució de tasca programada: guardar annexos pendents a l'arxiu");
			int maxReintents = getGuardarAnnexosMaxReintentsProperty();
			List<RegistreEntity> pendents = registreRepository.findGuardarAnnexPendents(maxReintents);
			if (pendents != null && !pendents.isEmpty()) {
				logger.debug("Processant annexos pendents de guardar a l'arxiu de " + pendents.size() + " anotacions de registre");
				Exception excepcio = null;
				for (RegistreEntity pendent: pendents)
					try {
						logger.debug("Processant anotacio pendent de guardar a l'arxiu (pendentId=" + pendent.getId() +", pendentNom=" + pendent.getNom() + ")");
						
						excepcio = registreHelper.processarAnotacioPendentArxiu(pendent.getId());
						
					} catch (Exception e) {
						excepcio = e;
					} finally {
						if (excepcio != null)
							logger.error("Error processant l'anotacio pendent de l'arxiu (pendentId=" + pendent.getId() + ", pendentNom=" + pendent.getNom() + "): " + excepcio.getMessage(), excepcio);
					}
			} else {
				logger.debug("No hi ha anotacions amb annexos pendents de guardar a l'arxiu");
			}
		}
	}

	@Override
	@Scheduled(fixedDelayString = "${config:es.caib.distribucio.tasca.enviar.anotacions.backoffice.temps.espera.execucio}")
	public void enviarIdsAnotacionsPendentsBackoffice() {
		logger.debug("Execució de tasca programada: enviar ids del anotacions pendents al backoffice");
		// getting annotacions pendents to send to backoffice with active regla and past retry time, grouped by regla
		List<RegistreEntity> pendents = registreRepository.findAmbEstatPendentEnviarBackoffice(new Date());
		List<Long> pendentsIdsGroupedByRegla = new ArrayList<>();
		if (pendents != null && !pendents.isEmpty()) {
			ReglaEntity previousRegla = pendents.get(0).getRegla();
			for (RegistreEntity pendent : pendents) {
				ReglaEntity currentRegla = pendent.getRegla();
				// if next group of anotacions is detected
				if (!currentRegla.equals(previousRegla)) {
					logger.debug(">>> Enviant grup d'anotacions " + pendentsIdsGroupedByRegla.size());
					previousRegla = currentRegla;
					registreHelper.enviarIdsAnotacionsBackUpdateDelayTime(pendentsIdsGroupedByRegla);
					pendentsIdsGroupedByRegla.clear();
				}
				pendentsIdsGroupedByRegla.add(pendent.getId());
				// if it is last iteration
				if (pendent.equals(pendents.get(pendents.size() - 1))) {
					logger.debug(">>> Enviant darrer grup d'anotacions " + pendentsIdsGroupedByRegla.size());
					registreHelper.enviarIdsAnotacionsBackUpdateDelayTime(pendentsIdsGroupedByRegla);
				}
			}
		}
	}
	
	@Override
	@Scheduled(fixedDelayString = "${config:es.caib.distribucio.tasca.aplicar.regles.temps.espera.execucio}")
	public void aplicarReglesPendents() {
		logger.debug("Execució de tasca programada: aplicar regles pendents");
		int maxReintents = getAplicarReglesMaxReintentsProperty();
		List<RegistreEntity> pendents = registreRepository.findAmbReglaPendentAplicar(maxReintents);
		logger.debug("Aplicant regles a " + pendents.size() + " anotacions de registre pendents");
		if (pendents != null && !pendents.isEmpty()) {
			Calendar properProcessamentCal = Calendar.getInstance();
			for (RegistreEntity pendent : pendents) {
				if (pendent.getRegla().getBackofficeTipus() == BackofficeTipusEnumDto.SISTRA) { //only for sistra
					// comprova si ha passat el temps entre reintents o ha
					// d'esperar
					boolean esperar = false;
					Date darrerProcessament = pendent.getProcesData();
					Integer minutsEntreReintents = pendent.getRegla().getBackofficeTempsEntreIntents();
					if (darrerProcessament != null && minutsEntreReintents != null) {
						// Calcula el temps pel proper intent
						properProcessamentCal.setTime(darrerProcessament);
						properProcessamentCal.add(Calendar.MINUTE,
								minutsEntreReintents);
						esperar = new Date().before(properProcessamentCal.getTime());
					}
					if (!esperar) {
						registreHelper.processarAnotacioPendentRegla(pendent.getId());
					}
				} else {
					registreHelper.processarAnotacioPendentRegla(pendent.getId());
				}
			}
		} else {
			logger.debug("No hi ha anotacions de registre amb regles pendents de processar");
		}
	}

	@Override
	@Scheduled(
			fixedDelayString = "${config:es.caib.distribucio.tasca.tancar.contenidors.temps.espera.execucio}")
	//@Scheduled(fixedRate = 120000)
	public void tancarContenidorsArxiuPendents() {
		logger.debug("Execució de tasca programada: tancar contenidors arxiu pendents");
		List<RegistreEntity> pendents = registreRepository.findPendentsTancarArxiu(new Date());
		if (pendents != null && !pendents.isEmpty()) {
			logger.debug("Tancant contenidors d'arxiu de " + pendents.size() + " anotacions de registre pendents");
			for (RegistreEntity registre: pendents) {
				registreHelper.tancarExpedientArxiu(registre.getId());
			}
		} else {
			logger.debug("No hi ha anotacions de registre amb contenidors d'arxiu pendents de tancar");
		}
	}

	/** Tasca periòdica en segon pla per consultar la taula d'enviament d'avisos de moviments pendents no agrupats per enviar
	 * els emails als destinataris dels avisos.
	 */
	@Override
	@Transactional
	@Scheduled(fixedDelayString = "${config:es.caib.distribucio.segonpla.email.bustia.periode.enviament.no.agrupat}")
	public void enviarEmailsPendentsNoAgrupats() {
		int movimentsEnviats = 0;
		int errors = 0;
		logger.debug("Execució de tasca segon pla enviament emails avis nous elemenents bustia no agrupats");
		List<ContingutMovimentEmailEntity> moviments = contingutMovimentEmailRepository.findByEnviamentAgrupatFalseOrderByDestinatariAscBustiaAsc();
		for (ContingutMovimentEmailEntity moviment : moviments) {
			try {
				this.enviarEmailPendent(
						moviment.getEmail(), 
						Arrays.asList(moviment));
				logger.debug("Enviat l'email d'avis del moviment " + moviment.getId() + " al destinatari " + moviment.getEmail());
				movimentsEnviats++;
			} catch (Exception e) {
				logger.error("Error enviant l'email d'avis del moviment " + moviment.getId() + " al destinatari " + moviment.getEmail() + ": " + e.getMessage(), e);
				errors++;
			}
		}
		logger.debug("Fi tasca segon pla enviament emails avis nous elemenents bustia no agrupats. Moviments enviats: " + movimentsEnviats + ". Errors: " + errors);
	}

	/** Tasca periòdica en segon pla per consultar la taula d'enviament d'avisos de moviments pendents agrupats per enviar
	 * els emails als destinataris dels avisos amb els moviments pendents agrupats.
	 */
	@Override
	@Transactional
	@Scheduled(cron = "${config:es.caib.distribucio.segonpla.email.bustia.cron.enviament.agrupat}")
	public void enviarEmailsPendentsAgrupats() {
		int movimentsEnviats = 0;
		int errors = 0;
		logger.debug("Execució de tasca segon pla enviament emails avis nous elemenents bustia agrupats");
		List<ContingutMovimentEmailEntity> moviments = contingutMovimentEmailRepository.findByEnviamentAgrupatTrueOrderByDestinatariAscBustiaAsc();
		// Agrupa per destinataris Map<email, continguts> 
		Map<String, List<ContingutMovimentEmailEntity>> contingutsEmail = new HashMap<String, List<ContingutMovimentEmailEntity>>();
		for (ContingutMovimentEmailEntity contingutEmail : moviments) {
			if (contingutsEmail.containsKey(contingutEmail.getEmail())) {
				contingutsEmail.get(contingutEmail.getEmail()).add(contingutEmail);
			} else {
				List<ContingutMovimentEmailEntity> lContingutEmails = new ArrayList<ContingutMovimentEmailEntity>();
				lContingutEmails.add(contingutEmail);
				contingutsEmail.put(contingutEmail.getEmail(), lContingutEmails);
			}
		}
		// Envia i esborra per agrupació
		for (String email: contingutsEmail.keySet()) {
			moviments = contingutsEmail.get(email);
			try {
				this.enviarEmailPendent(
						email, 
						moviments);
				logger.debug("Enviat l'email d'avis de " + moviments.size() + " moviments agrupats al destinatari " + email);
				movimentsEnviats += moviments.size();
			} catch (Exception e) {
				logger.error("Error enviant l'email d'avis de " + moviments.size() + " moviments agrupats al destinatari " + email + ": " + e.getMessage(), e);
				errors++;
			}
		}
		logger.debug("Fi tasca segon pla enviament emails avis nous elemenents bustia agrupats. Agrupacions:" + contingutsEmail.keySet().size() + ". Moviments enviats: " + movimentsEnviats + ". Errors: " + errors);
	}
	
	/** Mètode comú per enviar dins d'un email l'avís amb els moviments pendents a un destinatari. En acabar esborra els movimentsp
	 * pendents de notificar.
	 * @param email
	 * @param moviments
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	private void enviarEmailPendent(String email, List<ContingutMovimentEmailEntity> moviments) {
		// Envia l'email
		if (moviments.size() > 1) {
			emailHelper.sendEmailAvisAgrupatNousElementsBustia(
					email,
					moviments); 
		} else {
			emailHelper.sendEmailAvisSimpleNouElementBustia(
					email, 
					moviments.get(0));
		}
		// Esborra els moviments pendents de notificar
		contingutMovimentEmailRepository.delete(moviments);
	}

	private int getGuardarAnnexosMaxReintentsProperty() {
		//String maxReintents = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.tasca.dist.anotacio.pendent.max.reintents");
		String maxReintents = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.tasca.guardar.annexos.max.reintents");
		if (maxReintents != null) {
			return Integer.parseInt(maxReintents);
		} else {
			return 0;
		}
	}

	private int getAplicarReglesMaxReintentsProperty() {
		String maxReintents = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.tasca.aplicar.regles.max.reintents");
		if (maxReintents != null) {
			return Integer.parseInt(maxReintents);
		} else {
			return 0;
		}
	}

	public static void saveError(Long execucioMassivaContingutId, Throwable error) {
		StringWriter out = new StringWriter();
		error.printStackTrace(new PrintWriter(out));
		errorsMassiva.put(execucioMassivaContingutId, out.toString());
	}

	private static final Logger logger = LoggerFactory.getLogger(SegonPlaServiceImpl.class);

}
