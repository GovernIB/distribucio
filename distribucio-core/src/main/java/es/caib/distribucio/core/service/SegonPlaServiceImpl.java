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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.SemaphoreDto;
import es.caib.distribucio.core.api.service.MonitorIntegracioService;
import es.caib.distribucio.core.api.service.ProcedimentService;
import es.caib.distribucio.core.api.service.SegonPlaService;
import es.caib.distribucio.core.entity.ContingutMovimentEmailEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.ReglaEntity;
import es.caib.distribucio.core.helper.BustiaHelper;
import es.caib.distribucio.core.helper.ConfigHelper;
import es.caib.distribucio.core.helper.ConversioTipusHelper;
import es.caib.distribucio.core.helper.EmailHelper;
import es.caib.distribucio.core.helper.HistogramPendentsHelper;
import es.caib.distribucio.core.helper.HistoricHelper;
import es.caib.distribucio.core.helper.RegistreHelper;
import es.caib.distribucio.core.repository.ContingutMovimentEmailRepository;
import es.caib.distribucio.core.repository.EntitatRepository;

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
	@Autowired
	private HistoricHelper historicHelper;
	@Autowired
	private MonitorIntegracioService monitorIntegracioService;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;	
	@Autowired
	private ProcedimentService procedimentService;
	
	private static Map<Long, String> errorsMassiva = new HashMap<Long, String>();
	/**
	 * Mètode per guardar anotacions de registre amb estat pendent de guardar a l'arxiu. Es consulten les anotacions amb un màxim
	 * de reintents.
	 */
	@Override
	public void guardarAnotacionsPendentsEnArxiu() {
		// Comprova si la data actual es troba dins del període d'innactivitat
		if (this.isGuardarAnotacionsPendentsEnArxiuInactive()) {
			logger.trace("Tasca guardar anotacions pendents arxiu dins del període d'innactivitat, no s'executarà");
			return;
		}
		
		if (bustiaHelper.isProcessamentAsincronProperty()) {
			for (EntitatEntity entitat : entitatRepository.findByActiva(true)) {

				EntitatDto entitatDto = new EntitatDto();
				entitatDto.setCodi(entitat.getCodi());
				ConfigHelper.setEntitat(entitatDto);
				
				int maxReintents = getGuardarAnnexosMaxReintentsProperty(entitat);
				int maxResultats = 200;
				int maxThreadsParallel = registreHelper.getMaxThreadsParallelProperty();
				
				List<RegistreEntity> pendents;
				// Consulta sincronitzada amb l'arribada d'anotacions per evitar problemes de sincronisme
				synchronized (SemaphoreDto.getSemaphore()) {
					pendents = registreHelper.findGuardarAnnexPendents(entitat, maxReintents, maxResultats);
				}
				if (pendents != null && !pendents.isEmpty()) {
					
					long startTime = new Date().getTime();
					ExecutorService executor = Executors.newFixedThreadPool(maxThreadsParallel);
					for (RegistreEntity pendent : pendents) {

						Runnable thread =
								new GuardarAnotacioPendentThread(
										ConfigHelper.getEntitat(),
										pendent.getId(), 
										registreHelper);
						executor.execute(thread);
					}
					
			        executor.shutdown();
			        while (!executor.isTerminated()) {
			        	try {
			        		executor.awaitTermination(100, TimeUnit.MILLISECONDS);
			        	} catch (InterruptedException e) {}
			        }
			        long stopTime = new Date().getTime();
					logger.trace("Finished processing annotacions with " + maxThreadsParallel + " threads. " + pendents.size() + " annotacions processed in " + (stopTime - startTime) + "ms");
					
				}				
			}
			

		}
	}
	
	/** Classe runable per guardar un annex a l'Arxiu en un thread independent.
	 * 
	 */
	public class GuardarAnotacioPendentThread implements Runnable {

		private EntitatDto entitatActual;
		private RegistreHelper registreHelper;		
	    private Long registreId;
		
		/** Constructor amb els objectes de consulta i el zip per actualitzar. 
		 * @param registreHelper 
		 * @param errors 
		 * @param errors */
		public GuardarAnotacioPendentThread(
				EntitatDto entitatActual,
				Long registreId, 
				RegistreHelper registreHelper) {
			this.entitatActual = entitatActual;
			this.registreId = registreId;
			this.registreHelper = registreHelper;
		}

		@Override
		public void run() {
			ConfigHelper.setEntitat(this.entitatActual);
			registreHelper.processarAnotacioPendentArxiuInThreadExecutor(registreId);
		}
		
	    @Override
	    public String toString(){
			return this.registreId.toString();
	    }
	}
	
	/** Comprova si està informada la propietat de periode innactiu de la tasca 
	 * i si  es troba dins del període d'innactivitat definit per l'expressió
	 * 'cron' de la propietat <code>es.caib.distribucio.tasca.guardar.annexos.innectivitat.cron</code>.
	 * 
	 * @return True si es determina que la següent execucio es troba dins del període
	 * d'innactivitat.
	 * 
	 */
	private boolean isGuardarAnotacionsPendentsEnArxiuInactive() {
		boolean innactiva = false;
    	String expInnactivitat = configHelper.getConfig("es.caib.distribucio.tasca.guardar.annexos.innectivitat.cron");
    	if (expInnactivitat != null) {
    		Date now = new Date();
    		CronSequenceGenerator cronGen;
    		Date nextSecond;
    		try {
    			// Calcula quin seria el segon de la propera exeució
    			// Propera execució per cada segon
    			cronGen = new CronSequenceGenerator("* * * * * *");
    			nextSecond = cronGen.next(now);
    			cronGen = new CronSequenceGenerator(expInnactivitat);
    			Date nextInnactiu = cronGen.next(now);
    			innactiva = (nextInnactiu.getTime() <= nextSecond.getTime());
    		} catch(Exception e) {
    			logger.error("Error comprovant el període d'innactivitat per guardar anotacions pendents d'Arxiu: " + e.getMessage(), e);
    		}
    	}
    	return innactiva;
    }

	@Override
	@Scheduled(fixedDelayString = "60000")
	public void addNewEntryToHistogram() {

		int pendentsArxiu = 0;
		for (EntitatEntity entitat : entitatRepository.findByActiva(true)) {
			int maxReintents = getGuardarAnnexosMaxReintentsProperty(entitat);
			pendentsArxiu += registreHelper.countGuardarAnnexPendents(entitat, maxReintents);			
		}		
		historicsPendentHelper.addNewEntryToHistogram(pendentsArxiu);

	}

	@Override	
	public void enviarIdsAnotacionsPendentsBackoffice() {
		
		long startTime = new Date().getTime();
		
		logger.debug("Execució de tasca programada (" + startTime + "): enviar ids del anotacions pendents al backoffice");
		
		for (EntitatEntity entitat : entitatRepository.findByActiva(true)) {
			
			EntitatDto entitatDto = new EntitatDto();
			entitatDto.setCodi(entitat.getCodi());
			ConfigHelper.setEntitat(entitatDto);

			int maxReintents = getEnviarIdsAnotacionsMaxReintentsProperty(entitat);
		
			// getting annotacions pendents to send to backoffice with active regla and past retry time, grouped by regla
			List<RegistreEntity> pendents = registreHelper.findAmbEstatPendentEnviarBackoffice(entitat, new Date(), maxReintents);
			if (pendents != null && !pendents.isEmpty()) {
				
				Map<ReglaEntity, List<Long>> pendentsByRegla = new HashMap<ReglaEntity, List<Long>>();
				for (RegistreEntity pendent : pendents) {
					if (!pendentsByRegla.containsKey(pendent.getRegla())) {
						pendentsByRegla.put(pendent.getRegla(), new ArrayList<Long>());
					}
					pendentsByRegla.get(pendent.getRegla()).add(pendent.getId());
				}
				
				for (ReglaEntity regla : pendentsByRegla.keySet()) {
					final Timer timer = metricRegistry.timer(MetricRegistry.name(SegonPlaServiceImpl.class, "enviarIdsAnotacionsPendentsBackoffice"));
					Timer.Context context = timer.time();
					List<Long> pendentsIdsGroupedByRegla = pendentsByRegla.get(regla);
					logger.debug("Enviant grup de " + pendentsIdsGroupedByRegla.size() + "anotacions al backoffice " + regla.getBackofficeDesti().getNom() + " per la regla " + regla.getNom());
					Throwable t = registreHelper.enviarIdsAnotacionsBackUpdateDelayTime(pendentsIdsGroupedByRegla);
					if (t != null) {
						logger.warn("Error " + t.getClass() + " enviant grup de " + pendentsIdsGroupedByRegla.size() + "anotacions al backoffice " + regla.getBackofficeDesti().getNom() + " per la regla " + 
									regla.getNom() + ": " + t.getMessage());
					}
					context.stop();
				}
			}
		}
		long stopTime = new Date().getTime();
		logger.debug("Fi de tasca programada (" + startTime + "): enviar ids del anotacions pendents al backoffice " + (stopTime - startTime) + "ms");
	}
	
	
	@Override
	public void aplicarReglesPendentsBackoffice() {
		
		long startTime = new Date().getTime();
		logger.trace("Execució de tasca programada (" + startTime + "): aplicar regles pendents");
		
		for (EntitatEntity entitat : entitatRepository.findByActiva(true)) {
			
			EntitatDto entitatDto = new EntitatDto();
			entitatDto.setCodi(entitat.getCodi());
			ConfigHelper.setEntitat(entitatDto);

			int maxReintents = getAplicarReglesMaxReintentsProperty(entitat);
			List<RegistreEntity> pendents = registreHelper.findAmbReglaPendentAplicar(entitat, maxReintents);
			
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

				EntitatDto entitatDto = new EntitatDto();
				entitatDto.setCodi(registre.getEntitatCodi());
				ConfigHelper.setEntitat(entitatDto);

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
	
	/** 
	 * Consulta i guarda les dades històriques
	 */
	@Override
	@Transactional
	public void calcularDadesHistoriques() {
		historicHelper.calcularDades(new Date());
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

	private int getGuardarAnnexosMaxReintentsProperty(EntitatEntity entitat) {
		EntitatDto entitatDto = conversioTipusHelper.convertir(entitat, EntitatDto.class);
		String maxReintents = configHelper.getConfig(entitatDto, "es.caib.distribucio.tasca.guardar.annexos.max.reintents");
		if (maxReintents != null) {
			return Integer.parseInt(maxReintents);
		} else {
			return 0;
		}
	}
	
	private int getEnviarIdsAnotacionsMaxReintentsProperty(EntitatEntity entitat) {
		EntitatDto entitatDto = conversioTipusHelper.convertir(entitat, EntitatDto.class);
		String maxReintents = configHelper.getConfig(entitatDto, "es.caib.distribucio.tasca.enviar.anotacions.max.reintents");
		if (maxReintents != null) {
			return Integer.parseInt(maxReintents);
		} else {
			return 0;
		}
	}

	private int getAplicarReglesMaxReintentsProperty(EntitatEntity entitat) {
		
		/*String maxReintents = configHelper.getConfig("es.caib.distribucio." + entitat.getCodi() + ".tasca.aplicar.regles.max.reintents");
		if (maxReintents == null) {
			maxReintents = configHelper.getConfig("es.caib.distribucio.tasca.aplicar.regles.max.reintents");
		}*/
		EntitatDto entitatDto = conversioTipusHelper.convertir(entitat, EntitatDto.class);
		String maxReintents = configHelper.getConfig(entitatDto, "es.caib.distribucio.tasca.aplicar.regles.max.reintents");
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

	@Override
	public void esborrarDadesAntigesMonitorIntegracio() {
		String diesAntiguitat = configHelper.getConfig("es.caib.distribucio.tasca.monitor.integracio.esborrar.antics.dies", "30");
		logger.debug("Execució de tasca programada d'esborrar dades del monitor d'integracions mab " + diesAntiguitat + " dies d'antiguitat.");
		try {
			int dies = Integer.parseInt(diesAntiguitat);
			Calendar c = new GregorianCalendar();
			c.setTime(new Date());
			c.add(Calendar.DATE, -dies);
			Date data = c.getTime();
			int n = monitorIntegracioService.esborrarDadesAntigues(data);
			if (n > 0) {
				logger.debug(n + " dades de monitor d'integració antigues esborrades.");
			}
		} catch (Exception e) {
			logger.error("Error en la tasca d'esborrar dades antigues del monitor d'integracions", e);
		}
	}
	
	

	@Override
	public void reintentarProcessamentBackoffice() {
		
		long startTime = new Date().getTime();
		logger.trace("Execució de tasca programada (" + startTime + "): reintentar enviament d'una anotació al backoffice");
		
		String maxReintentsString = configHelper.getConfig("es.caib.distribucio.backoffice.reintentar.processament.max.reintents");
		int maxReintents = 0;
		if (maxReintentsString != null) {
			maxReintents = Integer.parseInt(maxReintentsString);
		}
		
		List<Long> registresBackError = registreHelper.findRegistresBackError(maxReintents);
		for (int i=0; i<registresBackError.size(); i++) {
			try {
				List<Long> pendents = new ArrayList<Long>();
				pendents.add(registresBackError.get(i));
				registreHelper.enviarIdsAnotacionsBackUpdateDelayTime(pendents);
				logger.info("S'ha reenviat al backoffice l'anotació amb id " + registresBackError.get(i));
			}catch(Exception e) {
				logger.error("No s'ha pogut reenviar al backoffice l'anotació amb id " + registresBackError.get(i), e);
			}
		}
		
	}
	
	
	@Override
	public void actualitzarProcediments() throws Exception {
		List<EntitatEntity> llistaEntitats = entitatRepository.findAll();
		List<String> entitatsError = new ArrayList<>();		
		for (EntitatEntity entitat : llistaEntitats) {
			try {
				procedimentService.findAndUpdateProcediments(entitat.getId());
			} catch (Exception e) {
				entitatsError.add(entitat.getCodi());
				logger.error("Error sincronitzant els procediments per l'entitat " + entitat.getCodi() + " - " + entitat.getNom() + ": " + e.getMessage()); 
			}			
		}
		if (!entitatsError.isEmpty()) {
			throw new Exception("No s'han pogut sincronitzar tots els procediments per les següents entitats: " + entitatsError);
		}
	}
	
	
	private static final Logger logger = LoggerFactory.getLogger(SegonPlaServiceImpl.class);

}
