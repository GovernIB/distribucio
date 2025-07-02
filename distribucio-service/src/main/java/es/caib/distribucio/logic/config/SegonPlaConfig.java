package es.caib.distribucio.logic.config;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import es.caib.distribucio.logic.intf.service.ConfigService;
import es.caib.distribucio.logic.intf.service.MonitorTasquesService;
import es.caib.distribucio.logic.intf.service.SegonPlaService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableScheduling
public class SegonPlaConfig implements SchedulingConfigurer {

	@Autowired
	private SegonPlaService segonPlaService;
	@Autowired
	private MonitorTasquesService monitorTasquesService;
	@Autowired
	private ConfigService configService;

	private ScheduledTaskRegistrar taskRegistrar;

	public void reiniciarTasquesSegonPla() {
		if (taskRegistrar != null) {
			taskRegistrar.destroy();
			taskRegistrar.afterPropertiesSet();
		}
	}

	@Bean
	public TaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(10);
		return taskScheduler;
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskScheduler());
		this.taskRegistrar = taskRegistrar;
		final String codiGuardarAnotacionsPendents = "guardarAnotacionsPendents";
		//Guardar anotacions de registre amb estat pendent de guardar a l'arxiu.
		monitorTasquesService.addTasca(codiGuardarAnotacionsPendents);
		taskRegistrar.addTriggerTask(
				new Runnable() {
					@Override
					public void run() {
						monitorTasquesService.inici(codiGuardarAnotacionsPendents);
						try {
							segonPlaService.guardarAnotacionsPendentsEnArxiu();
							monitorTasquesService.fi(codiGuardarAnotacionsPendents);
						} catch(Throwable th) {
							tractarErrorTascaSegonPla(th, codiGuardarAnotacionsPendents);
						}
					}
				},
				new Trigger() {
					@Override
					public Date nextExecutionTime(TriggerContext triggerContext) {
						Long value = null;
						try {
							value = configService.getConfigAsLong("es.caib.distribucio.tasca.guardar.annexos.temps.espera.execucio");
						} catch (Exception e) {
							log.warn("Error consultant la propietat per la propera execució de guardar annexos: " + e.getMessage(), e);
						}
						if (value == null) 
							value = Long.valueOf("60000");
						PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
						trigger.setInitialDelay(value);
						Date nextExecution = trigger.nextExecutionTime(triggerContext);
						Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
						monitorTasquesService.updateProperaExecucio(codiGuardarAnotacionsPendents, longNextExecution);
						return nextExecution;
					}
				});
		monitorTasquesService.addTasca(codiGuardarAnotacionsPendents);
		//Enviar annotacions al backoffice
		final String codiEnviarBackoffice = "enviarAlBackoffice";
		monitorTasquesService.addTasca(codiEnviarBackoffice);
		taskRegistrar.addTriggerTask(
				new Runnable() {
					@Override
					public void run() {
						monitorTasquesService.inici(codiEnviarBackoffice);
						try { 
							segonPlaService.enviarIdsAnotacionsPendentsBackoffice();
							monitorTasquesService.fi(codiEnviarBackoffice);
						} catch(Throwable th) {
							tractarErrorTascaSegonPla(th, codiEnviarBackoffice);
						}
					}
				},
				new Trigger() {
					@Override
					public Date nextExecutionTime(TriggerContext triggerContext) {
						Long value = null;
						try {
							value = configService.getConfigAsLong("es.caib.distribucio.tasca.enviar.anotacions.backoffice.temps.espera.execucio");
						} catch (Exception e) {
							log.warn("Error consultant la propietat per la propera execució d'enviar anotacions pendents als backoffices: " + e.getMessage());
						}
						if (value == null) 
							value = Long.valueOf("60000");
						PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
						trigger.setInitialDelay(value);
						Date nextExecution = trigger.nextExecutionTime(triggerContext);
						Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
						monitorTasquesService.updateProperaExecucio(codiEnviarBackoffice, longNextExecution);
						return nextExecution;
					}
				});
		monitorTasquesService.addTasca(codiEnviarBackoffice);
		//Aplicar regles de tipus backoffice
		final String codiAplicarReglesBackoffice = "aplicarReglesBackoffice";
		monitorTasquesService.addTasca(codiAplicarReglesBackoffice);
		taskRegistrar.addTriggerTask(
				new Runnable() {
					@Override
					public void run() {
						monitorTasquesService.inici(codiAplicarReglesBackoffice);
						try {
							segonPlaService.aplicarReglesPendentsBackoffice();
							monitorTasquesService.fi(codiAplicarReglesBackoffice);
						} catch(Throwable th) {
							tractarErrorTascaSegonPla(th, codiAplicarReglesBackoffice);
						}
					}
				},
				new Trigger() {
					@Override
					public Date nextExecutionTime(TriggerContext triggerContext) {
						Long value = null;
						try {
							value = configService.getConfigAsLong("es.caib.distribucio.tasca.aplicar.regles.temps.espera.execucio");
						} catch (Exception e) {
							log.warn("Error consultant la propietat per la propera execució d'aplicar regles: " + e.getMessage());
						}
						if (value == null) 
							value = Long.valueOf("60000");
						PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
						trigger.setInitialDelay(value);
						Date nextExecution = trigger.nextExecutionTime(triggerContext);
						Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
						monitorTasquesService.updateProperaExecucio(codiAplicarReglesBackoffice, longNextExecution);
						return nextExecution;
					}
				});
		monitorTasquesService.addTasca(codiAplicarReglesBackoffice);
		//Tancar contenidors
		final String codiTancarContenidors = "tancarContenidors";
		monitorTasquesService.addTasca(codiTancarContenidors);
		taskRegistrar.addTriggerTask(
				new Runnable() {
					@Override
					public void run() {
						monitorTasquesService.inici(codiTancarContenidors);
						try { 
							segonPlaService.tancarContenidorsArxiuPendents();
							monitorTasquesService.fi(codiTancarContenidors);
						} catch(Throwable th) {
							tractarErrorTascaSegonPla(th, codiTancarContenidors);
						}
					}
				},
				new Trigger() {
					@Override
					public Date nextExecutionTime(TriggerContext triggerContext) {
						Long value = null;
						try {
							value = configService.getConfigAsLong("es.caib.distribucio.tasca.tancar.contenidors.temps.espera.execucio");
						} catch (Exception e) {
							log.warn("Error consultant la propietat per la propera execució de tancar contenidors a l'Arxiu: " + e.getMessage());
						}
						if (value == null)
							value = Long.valueOf("60000");
						PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
						trigger.setInitialDelay(value);
						Date nextExecution = trigger.nextExecutionTime(triggerContext);
						Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
						monitorTasquesService.updateProperaExecucio(codiTancarContenidors, longNextExecution);
						return nextExecution;
					}
				});
		monitorTasquesService.addTasca(codiTancarContenidors);
		//Enviar emails no agrupats
		final String codiEnviarEmailsNoAgrupats = "enviarEmailsNoAgrupats";
		monitorTasquesService.addTasca(codiEnviarEmailsNoAgrupats);
		taskRegistrar.addTriggerTask(
				new Runnable() {
					@Override
					public void run() {
						monitorTasquesService.inici(codiEnviarEmailsNoAgrupats);
						try {
							segonPlaService.enviarEmailsPendentsNoAgrupats();
							monitorTasquesService.fi(codiEnviarEmailsNoAgrupats);
						} catch(Throwable th) {
							tractarErrorTascaSegonPla(th, codiEnviarEmailsNoAgrupats);
						}
					}
				},
				new Trigger() {
					@Override
					public Date nextExecutionTime(TriggerContext triggerContext) {
						Long value = null;
						try {
							value = configService.getConfigAsLong("es.caib.distribucio.segonpla.email.bustia.periode.enviament.no.agrupat");
						} catch (Exception e) {
							log.warn("Error consultant la propietat per la propera execució d'enviar emails no agrupats: " + e.getMessage());
						}
						if (value == null) 
							value = Long.valueOf("60000");
						PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
						trigger.setInitialDelay(value);
						Date nextExecution = trigger.nextExecutionTime(triggerContext);
						Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
						monitorTasquesService.updateProperaExecucio(codiEnviarEmailsNoAgrupats, longNextExecution);
						return nextExecution;
					}
				});
		monitorTasquesService.addTasca(codiEnviarEmailsNoAgrupats);
		//Enviar emails agrupats
		final String codiEnviarEmailsAgrupats = "enviarEmailsAgrupats";
		monitorTasquesService.addTasca(codiEnviarEmailsAgrupats);
		taskRegistrar.addTriggerTask(
				new Runnable() {
					@Override
					public void run() {
						monitorTasquesService.inici(codiEnviarEmailsAgrupats);
						try {
							segonPlaService.enviarEmailsPendentsAgrupats();
							monitorTasquesService.fi(codiEnviarEmailsAgrupats);
						} catch(Throwable th) {
							tractarErrorTascaSegonPla(th, codiEnviarEmailsAgrupats);
						}
					}
				},
				new Trigger() {
					@Override
					public Date nextExecutionTime(TriggerContext triggerContext) {
						String value = null;
						try {
							value = configService.getConfig("es.caib.distribucio.segonpla.email.bustia.cron.enviament.agrupat");
						} catch (Exception e) {
							log.warn("Error consultant la propietat per la propera execució d'enviar emails agrupats: " + e.getMessage());
						}
						if (value == null) 
							value = "* * * * * *";
						CronTrigger trigger = new CronTrigger(value);
						Date nextExecution = trigger.nextExecutionTime(triggerContext);
						Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
						monitorTasquesService.updateProperaExecucio(codiEnviarEmailsAgrupats, longNextExecution);
						return nextExecution;
					}
				});
		monitorTasquesService.addTasca(codiEnviarEmailsAgrupats);
		// Calcular dades estadístiques històriques
		final String codiCalularDadesHistoriques = "calcularDadesHistoriques";
		monitorTasquesService.addTasca(codiCalularDadesHistoriques);
		taskRegistrar.addTriggerTask(
				new Runnable() {
					@Override
					public void run() {
						monitorTasquesService.inici(codiCalularDadesHistoriques);
						try {
							segonPlaService.calcularDadesHistoriques();
							monitorTasquesService.fi(codiCalularDadesHistoriques);
						} catch(Throwable th) {
							tractarErrorTascaSegonPla(th, codiCalularDadesHistoriques);
						}
					}
				},
				new Trigger() {
					@Override
					public Date nextExecutionTime(TriggerContext triggerContext) {
						// Cada 1h a partir de les 20h:
						//   0 0 20/1 * * *
						// Cada 1h entre les 20h i les 6h
						//    0 0 20-06 * * *
						// Cada 1min entre les 11h i les 12h
						// 0 0/1 11 * * *
						//                    	String value = "0 0 20-06 * * *"; 
						String value = "0 0 20 * * *";
						CronTrigger trigger = new CronTrigger(value);
						Date nextExecution = trigger.nextExecutionTime(triggerContext);
						Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
						monitorTasquesService.updateProperaExecucio(codiCalularDadesHistoriques, longNextExecution);
						// Cada 60s
						//Long value = new Long("60000");
						//PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
						//Date nextExecution = trigger.nextExecutionTime(triggerContext);
						return nextExecution;
					}
				});
		monitorTasquesService.addTasca(codiCalularDadesHistoriques);
		
		// Esborra les dades antigues del monitor d'integracions
		final String codiEsborrarDadesAntigues = "esborrarDadesAntigues";
		monitorTasquesService.addTasca(codiEsborrarDadesAntigues);
		taskRegistrar.addTriggerTask(
				new Runnable() {
					@Override
					public void run() {
						monitorTasquesService.inici(codiEsborrarDadesAntigues);
						try {
							segonPlaService.esborrarDadesAntigesMonitorIntegracio();
							monitorTasquesService.fi(codiEsborrarDadesAntigues);
						} catch(Throwable th) {
							tractarErrorTascaSegonPla(th, codiEsborrarDadesAntigues);
						}
					}
				},
				new Trigger() {
					@Override
					public Date nextExecutionTime(TriggerContext triggerContext) {
						Long value = null;
						try {
							value = configService.getConfigAsLong("es.caib.distribucio.tasca.monitor.integracio.esborrar.antics.periode");
						} catch (Exception e) {
							log.warn("Error consultant la propietat per la propera execució d'esborrar dades monitor integració antigues: " + e.getMessage());
						}
						if (value == null) {
							value = Long.valueOf("3600000"); // Per defecte un cop cada hora per defecte
						}
						PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
						trigger.setInitialDelay(value);
						Date nextExecution = trigger.nextExecutionTime(triggerContext);
						Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
						monitorTasquesService.updateProperaExecucio(codiEsborrarDadesAntigues, longNextExecution);
						return nextExecution;
					}
				});
		monitorTasquesService.addTasca(codiEsborrarDadesAntigues);
		// Reintentar processament al backoffice
		final String codiReintentarProcessament = "reintentarProcessament";
		monitorTasquesService.addTasca(codiReintentarProcessament);
		taskRegistrar.addTriggerTask(
				new Runnable() {
					@Override
					public void run() {
						monitorTasquesService.inici(codiReintentarProcessament);
						try {
							segonPlaService.reintentarProcessamentBackoffice();
							monitorTasquesService.fi(codiReintentarProcessament);
						} catch(Throwable th) {
							tractarErrorTascaSegonPla(th, codiReintentarProcessament);
						}
					}
				}, 
				new Trigger() {
					@Override
					public Date nextExecutionTime(TriggerContext triggerContext) {
						Long value = null;
						try {
							value = configService.getConfigAsLong("es.caib.distribucio.backoffice.interval.temps.reintentar.processament");
						} catch (Exception e) {
							log.warn("Error consultant la propietat per la propera execució per reintentar l'enviament al backoffice");
						}
						if (value == null) {
							value = Long.valueOf("60000");
						}
						PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
						trigger.setInitialDelay(value);
						Date nextExecution = trigger.nextExecutionTime(triggerContext);
						Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
						monitorTasquesService.updateProperaExecucio(codiReintentarProcessament, longNextExecution);
						return nextExecution;
					}
				}
		);
		monitorTasquesService.addTasca(codiReintentarProcessament);
		// Actualitzar els procediments
		final String codiActualitzarProcediments = "actualitzarProcediments";
		monitorTasquesService.addTasca(codiActualitzarProcediments);
		taskRegistrar.addTriggerTask(
				new Runnable() {
					@Override
					public void run() {
						monitorTasquesService.inici(codiActualitzarProcediments);
						try {
							segonPlaService.actualitzarProcediments();
							monitorTasquesService.fi(codiActualitzarProcediments);
						} catch(Throwable th) {
							tractarErrorTascaSegonPla(th, codiActualitzarProcediments);
						}
					}
				}, 
				new Trigger() {
					@Override
					public Date nextExecutionTime(TriggerContext triggerContext) {
						String propertyValue = null;
						try {
							propertyValue = configService.getConfig("es.caib.distribucio.tasca.monitor.integracio.actualitzar.procediments");
						} catch (Exception e) {
							log.warn("Error consultant la propietat per la propera execució per actualitzar la taula de procediments");
						}
						if (propertyValue == null) {
							propertyValue = "0 30 15 * * 5";
						}
						log.info("Actualitzant procediments");
						CronTrigger trigger = new CronTrigger(propertyValue);
						Date nextExecution = trigger.nextExecutionTime(triggerContext);
						Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
						monitorTasquesService.updateProperaExecucio(codiActualitzarProcediments, longNextExecution);
						return nextExecution;
					}
				});
		monitorTasquesService.addTasca(codiActualitzarProcediments);
		// Actualitzar els serveis
		final String codiActualitzarServeis = "actualitzarServeis";
		monitorTasquesService.addTasca(codiActualitzarServeis);
		taskRegistrar.addTriggerTask(
				new Runnable() {
					@Override
					public void run() {
						monitorTasquesService.inici(codiActualitzarServeis);
						try {
							segonPlaService.actualitzarServeis();
							monitorTasquesService.fi(codiActualitzarServeis);
						} catch(Throwable th) {
							tractarErrorTascaSegonPla(th, codiActualitzarServeis);
						}
					}
				}, 
				new Trigger() {
					@Override
					public Date nextExecutionTime(TriggerContext triggerContext) {
						String propertyValue = null;
						try {
							propertyValue = configService.getConfig("es.caib.distribucio.tasca.monitor.integracio.actualitzar.serveis");
						} catch (Exception e) {
							log.warn("Error consultant la propietat per la propera execució per actualitzar la taula de serveis");
						}
						if (propertyValue == null) {
							propertyValue = "0 30 15 * * 5";
						}
						log.info("Actualitzant serveis");
						CronTrigger trigger = new CronTrigger(propertyValue);
						Date nextExecution = trigger.nextExecutionTime(triggerContext);
						Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
						monitorTasquesService.updateProperaExecucio(codiActualitzarServeis, longNextExecution);
						return nextExecution;
					}
				});
		monitorTasquesService.addTasca(codiActualitzarServeis);
		//Execució de les accions massives pendents
		final String codiExecucionMassives = "execucionsMassives";
		monitorTasquesService.addTasca(codiExecucionMassives);
		taskRegistrar.addTriggerTask(
				new Runnable() {
					@Override
					public void run() {
						monitorTasquesService.inici(codiExecucionMassives);
						try {
							segonPlaService.executeNextMassiveScheduledTask();
							monitorTasquesService.fi(codiExecucionMassives);
						} catch(Throwable th) {
							tractarErrorTascaSegonPla(th, codiExecucionMassives);
						}
					}
				},
				new Trigger() {
					@Override
					public Date nextExecutionTime(TriggerContext triggerContext) {
						String value = null;
						try {
							value = configService.getConfig("es.caib.distribucio.segonpla.cron.execucio.massiva");
						} catch (Exception e) {
							log.warn("Error consultant la propietat per la propera execució de les massives: " + e.getMessage());
						}
						if (value == null) 
							value = "0 */15 * * * *"; // Cada 15 min
						CronTrigger trigger = new CronTrigger(value);
						Date nextExecution = trigger.nextExecutionTime(triggerContext);
						Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
						monitorTasquesService.updateProperaExecucio(codiExecucionMassives, longNextExecution);
						return nextExecution;
					}
				});
		monitorTasquesService.addTasca(codiExecucionMassives);
	}

	/** Enregistre l'error als logs i marca la tasca amb error. */
	private void tractarErrorTascaSegonPla(Throwable th, String codiTasca) {
		String errMsg = th.getClass() + ": " + th.getMessage() + " (" + new Date().getTime() + ")";
		log.error("Error no controlat a l'execució de la tasca en segon pla amb codi \"" + codiTasca + "\": " + errMsg, th);
		monitorTasquesService.error(codiTasca, errMsg);
	}

}