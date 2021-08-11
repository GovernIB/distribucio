/**
 * 
 */
package es.caib.distribucio.core.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import es.caib.distribucio.core.api.service.SegonPlaService;
import es.caib.distribucio.core.entity.ContingutMovimentEmailEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.ReglaEntity;
import es.caib.distribucio.core.helper.BustiaHelper;
import es.caib.distribucio.core.helper.ConfigHelper;
import es.caib.distribucio.core.helper.EmailHelper;
import es.caib.distribucio.core.helper.HistogramPendentsHelper;
import es.caib.distribucio.core.helper.RegistreHelper;
import es.caib.distribucio.core.helper.WorkerThread;
import es.caib.distribucio.core.repository.ContingutMovimentEmailRepository;

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
	private MetricRegistry metricRegistry;
	@Autowired
	private HistogramPendentsHelper historicsPendentHelper;
	@Autowired
	private ConfigHelper configHelper;
	
	
	private static Map<Long, String> errorsMassiva = new HashMap<Long, String>();
	/**
	 * Mètode per guardar anotacions de registre amb estat pendent de guardar a l'arxiu. Es consulten les anotacions amb un màxim
	 * de reintents.
	 */
	@Override
	public void guardarAnotacionsPendentsEnArxiu() {

		if (bustiaHelper.isProcessamentAsincronProperty()) {
			int maxReintents = getGuardarAnnexosMaxReintentsProperty();
			int maxThreadsParallel = registreHelper.getMaxThreadsParallelProperty();
			
			List<RegistreEntity> pendents;
			// Consulta sincronitzada amb l'arribada d'anotacions per evitar problemes de sincronisme
			synchronized (SemaphoreDto.getSemaphore()) {
				pendents = registreHelper.findGuardarAnnexPendents(maxReintents);
			}
			if (pendents != null && !pendents.isEmpty()) {
				
				long startTime = new Date().getTime();
				ExecutorService executor = Executors.newFixedThreadPool(maxThreadsParallel);
				for (RegistreEntity pendent : pendents) {

					Runnable worker = new WorkerThread(pendent.getId(), registreHelper);
					executor.execute(worker);
				}
				
		        executor.shutdown();
		        while (!executor.isTerminated()) {
		        }
		        long stopTime = new Date().getTime();
				logger.trace("Finished processing annotacions with " + maxThreadsParallel + " threads. " + pendents.size() + " annotacions processed in " + (stopTime - startTime) + "ms");
				
			}

		}
	}
	
	@Override
	@Scheduled(fixedDelayString = "60000")
	public void addNewEntryToHistogram() {

		int maxReintents = getGuardarAnnexosMaxReintentsProperty();
		int pendentsArxiu = registreHelper.findGuardarAnnexPendents(maxReintents).size();
		
		historicsPendentHelper.addNewEntryToHistogram(pendentsArxiu);

	}
	

	@Override
	public void enviarIdsAnotacionsPendentsBackoffice() {
		
		long startTime = new Date().getTime();
		
		logger.trace("Execució de tasca programada (" + startTime + "): enviar ids del anotacions pendents al backoffice");
		
		int maxReintents = getEnviarIdsAnotacionsMaxReintentsProperty();
		// getting annotacions pendents to send to backoffice with active regla and past retry time, grouped by regla
		List<RegistreEntity> pendents = registreHelper.findAmbEstatPendentEnviarBackoffice(new Date(), maxReintents);
		List<Long> pendentsIdsGroupedByRegla = new ArrayList<>();
		if (pendents != null && !pendents.isEmpty()) {

			ReglaEntity previousRegla = pendents.get(0).getRegla();
			for (RegistreEntity pendent : pendents) {
				final Timer timer = metricRegistry.timer(MetricRegistry.name(SegonPlaServiceImpl.class, "enviarIdsAnotacionsPendentsBackoffice"));
				Timer.Context context = timer.time();
				ReglaEntity currentRegla = pendent.getRegla();
				// if next group of anotacions is detected
				if (!currentRegla.equals(previousRegla)) {
					logger.trace(">>> Enviant grup d'anotacions " + pendentsIdsGroupedByRegla.size());
					previousRegla = currentRegla;
					registreHelper.enviarIdsAnotacionsBackUpdateDelayTime(pendentsIdsGroupedByRegla);
					pendentsIdsGroupedByRegla.clear();
				}
				pendentsIdsGroupedByRegla.add(pendent.getId());
				// if it is last iteration
				if (pendent.equals(pendents.get(pendents.size() - 1))) {
					logger.trace(">>> Enviant darrer grup d'anotacions " + pendentsIdsGroupedByRegla.size());
					registreHelper.enviarIdsAnotacionsBackUpdateDelayTime(pendentsIdsGroupedByRegla);
				}
				context.stop();
			}
		}
		
		long stopTime = new Date().getTime();
		logger.trace("Fi de tasca programada (" + startTime + "): enviar ids del anotacions pendents al backoffice " + (stopTime - startTime) + "ms");
	}
	
	
	@Override
	public void aplicarReglesPendentsBackoffice() {
		
		long startTime = new Date().getTime();
		logger.trace("Execució de tasca programada (" + startTime + "): aplicar regles pendents");
		
		
		int maxReintents = getAplicarReglesMaxReintentsProperty();
		List<RegistreEntity> pendents = registreHelper.findAmbReglaPendentAplicar(maxReintents);
		
		if (pendents != null && !pendents.isEmpty()) {
			
			logger.trace("Aplicant regles a " + pendents.size() + " anotacions de registre pendents");

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
	//@Scheduled(fixedRate = 120000)
	public void tancarContenidorsArxiuPendents() {
		
		long startTime = new Date().getTime();
		
		logger.trace("Execució de tasca programada (" + startTime + "): tancar contenidors arxiu pendents");
		List<RegistreEntity> pendents = registreHelper.findPendentsTancarArxiu(new Date());
		if (pendents != null && !pendents.isEmpty()) {
			logger.trace("Tancant contenidors d'arxiu de " + pendents.size() + " anotacions de registre pendents");
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
				
				logger.trace("Enviat l'email d'avis del moviment " + moviment.getId() + " al destinatari " + moviment.getEmail());
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
				logger.trace("Enviat l'email d'avis de " + moviments.size() + " moviments agrupats al destinatari " + email);
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
					moviments.get(0).getId());
		}
		// Esborra els moviments pendents de notificar
		contingutMovimentEmailRepository.delete(moviments);
	}

	private int getGuardarAnnexosMaxReintentsProperty() {
		String maxReintents = configHelper.getConfig("es.caib.distribucio.tasca.guardar.annexos.max.reintents");
		if (maxReintents != null) {
			return Integer.parseInt(maxReintents);
		} else {
			return 0;
		}
	}
	
	private int getEnviarIdsAnotacionsMaxReintentsProperty() {
		String maxReintents = configHelper.getConfig("es.caib.distribucio.tasca.enviar.anotacions.max.reintents");
		if (maxReintents != null) {
			return Integer.parseInt(maxReintents);
		} else {
			return 0;
		}
	}

	private int getAplicarReglesMaxReintentsProperty() {
		String maxReintents = configHelper.getConfig("es.caib.distribucio.tasca.aplicar.regles.max.reintents");
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
