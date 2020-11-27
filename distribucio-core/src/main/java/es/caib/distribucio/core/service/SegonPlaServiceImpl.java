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

import javax.xml.namespace.QName;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import es.caib.distribucio.core.api.dto.SemaphoreDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.exception.AplicarReglaException;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.service.SegonPlaService;
import es.caib.distribucio.core.api.service.bantel.wsClient.v2.BantelFacadeException;
import es.caib.distribucio.core.api.service.bantel.wsClient.v2.BantelFacadeWsClient;
import es.caib.distribucio.core.api.service.bantel.wsClient.v2.model.ReferenciaEntrada;
import es.caib.distribucio.core.api.service.bantel.wsClient.v2.model.ReferenciasEntrada;
import es.caib.distribucio.core.entity.ContingutMovimentEmailEntity;
import es.caib.distribucio.core.entity.RegistreAnnexEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.ReglaEntity;
import es.caib.distribucio.core.helper.BustiaHelper;
import es.caib.distribucio.core.helper.EmailHelper;
import es.caib.distribucio.core.helper.PropertiesHelper;
import es.caib.distribucio.core.helper.RegistreHelper;
import es.caib.distribucio.core.helper.ReglaHelper;
import es.caib.distribucio.core.helper.WsClientHelper;
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
	private RegistreHelper registreHelper;
	@Autowired
	private ReglaHelper reglaHelper;
	@Autowired
	private MetricRegistry metricRegistry;
	@Autowired
	private RegistreRepository registreRepository;
	
	
	private static Map<Long, String> errorsMassiva = new HashMap<Long, String>();
	/**
	 * Mètode per guardar anotacions de registre amb estat pendent de guardar a l'arxiu. Es consulten les anotacions amb un màxim
	 * de reintents.
	 */
	@Override
	@Scheduled(fixedDelayString = "${config:es.caib.distribucio.tasca.guardar.annexos.temps.espera.execucio}")
	public void guardarAnotacionsPendentsEnArxiu() {

		long startTime = new Date().getTime();

		if (bustiaHelper.isProcessamentAsincronProperty()) {
			logger.trace("Execució de tasca programada (" + startTime + "): guardar annexos pendents a l'arxiu");
			int maxReintents = getGuardarAnnexosMaxReintentsProperty();
			
			List<RegistreEntity> pendents;
			// Consulta sincronitzada amb l'arribada d'anotacions per evitar problemes de sincronisme
			synchronized (SemaphoreDto.getSemaphore()) {
				pendents = registreHelper.findGuardarAnnexPendents(maxReintents);
			}
			if (pendents != null && !pendents.isEmpty()) {
				Exception excepcio = null;
				for (RegistreEntity pendent : pendents) {

					final Timer timer = metricRegistry.timer(MetricRegistry.name(SegonPlaServiceImpl.class, "guardarAnotacionsPendentsEnArxiu"));
					Timer.Context context = timer.time();
					try {
						logger.debug("Processant anotacio pendent de guardar a l'arxiu (pendentId=" + pendent.getId() + ", pendentNom=" + pendent.getNom() + ")");
						excepcio = registreHelper.processarAnotacioPendentArxiu(pendent.getId());
					} catch (NotFoundException e) {
						if (e.getObjectClass() == UnitatOrganitzativaDto.class) {
							excepcio = null;
						}
					} catch (Exception e) {
						excepcio = e;
					} finally {
						if (excepcio != null)
							logger.error("Error processant l'anotacio pendent de l'arxiu (pendentId=" + pendent.getId() + ", pendentNom=" + pendent.getNom() + "): " + excepcio.getMessage(), excepcio);
					}
					context.stop();

				}
			} else {
				logger.trace("No hi ha anotacions amb annexos pendents de guardar a l'arxiu");
			}
			
			long stopTime = new Date().getTime();
			logger.trace("Fi de tasca programada (" + startTime + "): guardar annexos pendents a l'arxiu " + (stopTime - startTime) + "ms");

		}
	}

	@Override
	@Scheduled(fixedDelayString = "${config:es.caib.distribucio.tasca.enviar.anotacions.backoffice.temps.espera.execucio}")
	public void enviarIdsAnotacionsPendentsBackoffice() {
		
		long startTime = new Date().getTime();
		
		logger.debug("Execució de tasca programada (" + startTime + "): enviar ids del anotacions pendents al backoffice");
		// getting annotacions pendents to send to backoffice with active regla and past retry time, grouped by regla
		List<RegistreEntity> pendents = registreHelper.findAmbEstatPendentEnviarBackoffice(new Date());
		List<Long> pendentsIdsGroupedByRegla = new ArrayList<>();
		if (pendents != null && !pendents.isEmpty()) {

			ReglaEntity previousRegla = pendents.get(0).getRegla();
			for (RegistreEntity pendent : pendents) {
				final Timer timer = metricRegistry.timer(MetricRegistry.name(SegonPlaServiceImpl.class, "enviarIdsAnotacionsPendentsBackoffice"));
				Timer.Context context = timer.time();
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
				context.stop();
			}
		}
		
		long stopTime = new Date().getTime();
		logger.debug("Fi de tasca programada (" + startTime + "): enviar ids del anotacions pendents al backoffice " + (stopTime - startTime) + "ms");
	}
	
	
	@Override
	@Scheduled(fixedDelayString = "${config:es.caib.distribucio.tasca.aplicar.regles.temps.espera.execucio}")
	public void aplicarReglesPendentsBackoffice() {
		
		long startTime = new Date().getTime();
		logger.trace("Execució de tasca programada (" + startTime + "): aplicar regles pendents");
		
		
		int maxReintents = getAplicarReglesMaxReintentsProperty();
		List<RegistreEntity> pendents = registreHelper.findAmbReglaPendentAplicar(maxReintents);
		
		if (pendents != null && !pendents.isEmpty()) {
			
			logger.debug("Aplicant regles a " + pendents.size() + " anotacions de registre pendents");

			for (RegistreEntity pendent : pendents) {
				
				final Timer timer = metricRegistry.timer(MetricRegistry.name(SegonPlaServiceImpl.class, "aplicarReglesPendents"));
				Timer.Context context = timer.time();
				
				registreHelper.processarAnotacioPendentRegla(pendent.getId());
				
				context.stop();
			}
		} else {
			logger.trace("No hi ha anotacions de registre amb regles pendents de processar");
		}
		
		
		long stopTime = new Date().getTime();
		logger.trace("Fi de de tasca programada (" + stopTime + "): aplicar regles pendents " + (stopTime - startTime) + "ms");
		
	}
	
	
	
	
	@Override
	@Scheduled(fixedDelayString = "${config:es.caib.distribucio.tasca.aplicar.regles.temps.espera.execucio}")
	public void backofficeSistra() {

		String error = null;

		List<RegistreEntity> pendents = registreRepository.findAmbEstatPendentBackofficeSistra();

		if (pendents != null && !pendents.isEmpty()) {

			try {
				Calendar properProcessamentCal = Calendar.getInstance();
				for (RegistreEntity pendent : pendents) {

					ReglaEntity regla = pendent.getRegla();

					// comprova si ha passat el temps entre reintents o ha d'esperar
					boolean esperar = false;
					Date darrerProcessament = pendent.getProcesData();
					Integer minutsEntreReintents = pendent.getRegla().getBackofficeDesti().getTempsEntreIntents();
					if (darrerProcessament != null && minutsEntreReintents != null) {
						// Calcula el temps pel proper intent
						properProcessamentCal.setTime(darrerProcessament);
						properProcessamentCal.add(Calendar.MINUTE,
								minutsEntreReintents);
						esperar = new Date().before(properProcessamentCal.getTime());
					}
					if (!esperar) {
						for (RegistreAnnexEntity annex : pendent.getAnnexos()) {
							if (annex.getFitxerNom().equals("DatosPropios.xml") || annex.getFitxerNom().equals("Asiento.xml"))
								reglaHelper.processarAnnexSistra(pendent,
										annex);
						}
						BantelFacadeWsClient backofficeSistraClient = new WsClientHelper<BantelFacadeWsClient>().generarClientWs(
							getClass().getResource("/es/caib/distribucio/core/service/ws/backofficeSistra/BantelFacade.wsdl"),
							regla.getBackofficeDesti().getUrl(),
							new QName(
									"urn:es:caib:bantel:ws:v2:services",
									"BantelFacadeService"),
							regla.getBackofficeDesti().getUsuari(),
							regla.getBackofficeDesti().getContrasenya(),
							null,
							BantelFacadeWsClient.class);
						// Crea la llista de referències d'entrada
						ReferenciasEntrada referenciesEntrades = new ReferenciasEntrada();
						ReferenciaEntrada referenciaEntrada = new ReferenciaEntrada();
						referenciaEntrada.setNumeroEntrada(pendent.getNumero());
						referenciaEntrada.setClaveAcceso(ReglaHelper.encrypt(pendent.getNumero()));
						referenciesEntrades.getReferenciaEntrada().add(referenciaEntrada);
						// Invoca el backoffice sistra
						try {
							backofficeSistraClient.avisoEntradas(referenciesEntrades);
						} catch (BantelFacadeException bfe) {
							error = "[" + bfe.getFaultInfo() + "] " + bfe.getLocalizedMessage();
						}
					}
				}
			} catch (Exception ex) {
				Throwable t = ExceptionUtils.getRootCause(ex);
				if (t == null)
					t = ex.getCause();
				if (t == null)
					t = ex;
				error = ExceptionUtils.getStackTrace(t);
			}
			if (error != null) {
				throw new AplicarReglaException(error);
			}
		}

	}
	
	
	

	@Override
	@Scheduled(fixedDelayString = "${config:es.caib.distribucio.tasca.tancar.contenidors.temps.espera.execucio}")
	//@Scheduled(fixedRate = 120000)
	public void tancarContenidorsArxiuPendents() {
		
		long startTime = new Date().getTime();
		
		logger.trace("Execució de tasca programada (" + startTime + "): tancar contenidors arxiu pendents");
		List<RegistreEntity> pendents = registreHelper.findPendentsTancarArxiu(new Date());
		if (pendents != null && !pendents.isEmpty()) {
			logger.debug("Tancant contenidors d'arxiu de " + pendents.size() + " anotacions de registre pendents");
			for (RegistreEntity registre: pendents) {
				final Timer timer = metricRegistry.timer(MetricRegistry.name(SegonPlaServiceImpl.class, "tancarContenidorsArxiuPendents"));
				Timer.Context context = timer.time();
				registreHelper.tancarExpedientArxiu(registre.getId());
				context.stop();
			}
		} else {
			logger.trace("No hi ha anotacions de registre amb contenidors d'arxiu pendents de tancar");
		}
		
		long stopTime = new Date().getTime();
		logger.trace("Fi de tasca programada (" + startTime + "): tancar contenidors arxiu pendents " + (stopTime - startTime) + "ms");
		
		
	}

	/** Tasca periòdica en segon pla per consultar la taula d'enviament d'avisos de moviments pendents no agrupats per enviar
	 * els emails als destinataris dels avisos.
	 */
	@Override
	@Transactional
	@Scheduled(fixedDelayString = "${config:es.caib.distribucio.segonpla.email.bustia.periode.enviament.no.agrupat}")
	public void enviarEmailsPendentsNoAgrupats() {
		
		long startTime = new Date().getTime();
		
		int movimentsEnviats = 0;
		int errors = 0;
		int anticsEsborrats = 0;
		logger.trace("Execució de tasca segon pla enviament emails avis nous elemenents bustia no agrupats (" + startTime + ")");
		List<ContingutMovimentEmailEntity> moviments = contingutMovimentEmailRepository.findByEnviamentAgrupatFalseOrderByDestinatariAscBustiaAsc();
		for (ContingutMovimentEmailEntity moviment : moviments) {
			
			final Timer timer = metricRegistry.timer(MetricRegistry.name(SegonPlaServiceImpl.class, "enviarEmailsPendentsNoAgrupats"));
			Timer.Context context = timer.time();
			
			try {
				this.enviarEmailPendent(
						moviment.getEmail(), 
						Arrays.asList(moviment));
				
				logger.debug("Enviat l'email d'avis del moviment " + moviment.getId() + " al destinatari " + moviment.getEmail());
				movimentsEnviats++;
			} catch (Exception e) {
				logger.error("Error enviant l'email d'avis del moviment " + moviment.getId() + " al destinatari " + moviment.getEmail() + ": " + e.getMessage());
				errors++;
				
				// remove pending email if it is older that one week
				Date formattedToday = new Date();
				Date formattedExpired = moviment.getCreatedDate().toDate();
				int diffInDays = (int)( (formattedToday.getTime() - formattedExpired.getTime()) / (1000 * 60 * 60 * 24) );
				if (diffInDays > 7) {
					contingutMovimentEmailRepository.delete(moviment);
					anticsEsborrats++;
				}
				
			}
			context.stop();
		}
		long stopTime = new Date().getTime();
		logger.trace("Fi tasca segon pla enviament emails avis nous elemenents bustia no agrupats (" + startTime + "). Moviments enviats: " + movimentsEnviats + ". Errors: " + errors + ". Antics esborrats: " + anticsEsborrats + " en " + (stopTime - startTime) + "ms");
		

	}

	/** Tasca periòdica en segon pla per consultar la taula d'enviament d'avisos de moviments pendents agrupats per enviar
	 * els emails als destinataris dels avisos amb els moviments pendents agrupats.
	 */
	@Override
	@Transactional
	@Scheduled(cron = "${config:es.caib.distribucio.segonpla.email.bustia.cron.enviament.agrupat}")
	public void enviarEmailsPendentsAgrupats() {
		
		long startTime = new Date().getTime();
		
		int movimentsEnviats = 0;
		int errors = 0;
		int anticsEsborrats = 0;
		logger.trace("Execució de tasca segon pla enviament emails avis nous elemenents bustia agrupats (" + startTime + ")");
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
			
			final Timer timer = metricRegistry.timer(MetricRegistry.name(SegonPlaServiceImpl.class, "enviarEmailsPendentsAgrupats"));
			Timer.Context context = timer.time();
			
			moviments = contingutsEmail.get(email);
			try {
				this.enviarEmailPendent(
						email, 
						moviments);
				logger.debug("Enviat l'email d'avis de " + moviments.size() + " moviments agrupats al destinatari " + email);
				movimentsEnviats += moviments.size();
			} catch (Exception e) {
				logger.error("Error enviant l'email d'avis de " + moviments.size() + " moviments agrupats al destinatari " + email + ": " + e.getMessage());
				errors++;
				
				for (ContingutMovimentEmailEntity moviment : moviments) {
					// remove pending email if it is older that one week
					Date formattedToday = new Date();
					Date formattedExpired = moviment.getCreatedDate().toDate();
					int diffInDays = (int)( (formattedToday.getTime() - formattedExpired.getTime()) / (1000 * 60 * 60 * 24) );
					if (diffInDays > 7) {
						contingutMovimentEmailRepository.delete(moviment);
						anticsEsborrats++;
					}
				}
			}
			context.stop();
		}
		
		long stopTime = new Date().getTime();
		logger.trace("Fi tasca segon pla enviament emails avis nous elemenents bustia agrupats (" + startTime + "). Agrupacions:" + contingutsEmail.keySet().size() + ". Moviments enviats: " + movimentsEnviats + ". Errors: " + errors + ". Antics esborrats: " + anticsEsborrats + " en " + (stopTime - startTime) + "ms");
		
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