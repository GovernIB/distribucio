package es.caib.distribucio.core.config;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import es.caib.distribucio.core.api.service.MonitorTasquesService;
import es.caib.distribucio.core.api.service.SegonPlaService;
import es.caib.distribucio.core.helper.ConfigHelper;


@Configuration
@EnableScheduling
public class SegonPlaConfig implements SchedulingConfigurer {

	
    @Autowired
    TaskScheduler taskScheduler;
    @Autowired
    SegonPlaService segonPlaService;
    @Autowired
	private ConfigHelper configHelper;
    @Autowired
    private MonitorTasquesService monitorTasquesService;
    
    
    private ScheduledTaskRegistrar taskRegistrar;
    
    
    public void reiniciarTasquesSegonPla() {
    	if (taskRegistrar != null) {
    		taskRegistrar.destroy();
    		taskRegistrar.afterPropertiesSet();
    	}
    }


    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    	taskRegistrar.setScheduler(taskScheduler);
    	this.taskRegistrar = taskRegistrar;

		final String codiGuardarAnotacionsPendents = "guardarAnotacionsPendents";
   	 	//Guardar anotacions de registre amb estat pendent de guardar a l'arxiu.
		monitorTasquesService.addTasca(codiGuardarAnotacionsPendents);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {
                    	monitorTasquesService.inici(codiGuardarAnotacionsPendents);
                        try{ 
                        	segonPlaService.guardarAnotacionsPendentsEnArxiu();
                        	monitorTasquesService.fi(codiGuardarAnotacionsPendents);
                        } catch(Exception e) {
                        	monitorTasquesService.error(codiGuardarAnotacionsPendents);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                    	
                    	Long value = null;
						try {
							value = configHelper.getAsLong("es.caib.distribucio.tasca.guardar.annexos.temps.espera.execucio");
						} catch (Exception e) {
							logger.warn("Error consultant la propietat per la propera execució de guardar annexos: " + e.getMessage());
						}
                    	if (value == null) 
                    		value = new Long("60000");
                        PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
                        trigger.setInitialDelay(value);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);

                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
        				monitorTasquesService.updateProperaExecucio(codiGuardarAnotacionsPendents, longNextExecution);

                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(codiGuardarAnotacionsPendents);
        
        
        
   	 	//Enviar annotacions al backoffice
        final String codiEnviarBackoffice = "enviarAlBackoffice";
		monitorTasquesService.addTasca(codiEnviarBackoffice);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {
                    	monitorTasquesService.inici(codiEnviarBackoffice);
                        try{ 
                        	segonPlaService.enviarIdsAnotacionsPendentsBackoffice();
                        	monitorTasquesService.fi(codiEnviarBackoffice);
                        } catch(Exception e) {                        	
                        	monitorTasquesService.error(codiEnviarBackoffice);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                    	
                    	Long value = null;
						try {
							value = configHelper.getAsLong("es.caib.distribucio.tasca.enviar.anotacions.backoffice.temps.espera.execucio");
						} catch (Exception e) {
							logger.warn("Error consultant la propietat per la propera execució d'enviar anotacions pendents als backoffices: " + e.getMessage());
						}
                    	if (value == null) 
                    		value = new Long("60000");
                    	PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
                    	trigger.setInitialDelay(value);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        
                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
        				monitorTasquesService.updateProperaExecucio(codiEnviarBackoffice, longNextExecution);
                        
                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(codiEnviarBackoffice);
        
   	 	//Aplicar regles de tipus backoffice
        final String codiAplicarReglesBackoffice = "aplicarReglesBackoffice";
		monitorTasquesService.addTasca(codiAplicarReglesBackoffice);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {
                    	monitorTasquesService.inici(codiAplicarReglesBackoffice);
                        try{ 
                        	segonPlaService.aplicarReglesPendentsBackoffice();
                        	monitorTasquesService.fi(codiAplicarReglesBackoffice);
                        } catch(Exception e) {                        	
                        	monitorTasquesService.error(codiAplicarReglesBackoffice);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                    	Long value = null;
						try {
							value = configHelper.getAsLong("es.caib.distribucio.tasca.aplicar.regles.temps.espera.execucio");
						} catch (Exception e) {
							logger.warn("Error consultant la propietat per la propera execució d'aplicar regles: " + e.getMessage());
						}
                    	if (value == null) 
                    		value = new Long("60000");
                    	PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
                    	trigger.setInitialDelay(value);                        
                    	Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        
                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
        				monitorTasquesService.updateProperaExecucio(codiAplicarReglesBackoffice, longNextExecution);
                        
                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(codiAplicarReglesBackoffice);
        
        
   	 	//Tancar contenidors
        final String codiTancarContenidors = "tancarContenidors";
		monitorTasquesService.addTasca(codiTancarContenidors);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {
                    	monitorTasquesService.inici(codiTancarContenidors);
                        try{ 
                        	segonPlaService.tancarContenidorsArxiuPendents();
                        	monitorTasquesService.fi(codiTancarContenidors);
                        } catch(Exception e) {                        	
                        	monitorTasquesService.error(codiTancarContenidors);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                    	Long value = null;
						try {
							value = configHelper.getAsLong("es.caib.distribucio.tasca.tancar.contenidors.temps.espera.execucio");
						} catch (Exception e) {
							logger.warn("Error consultant la propietat per la propera execució de tancar contenidors a l'Arxiu: " + e.getMessage());
						}
                    	if (value == null) 
                    		value = new Long("60000");
                    	
                    	PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
                    	trigger.setInitialDelay(value);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        
                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
        				monitorTasquesService.updateProperaExecucio(codiTancarContenidors, longNextExecution);
                        
                        
                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(codiTancarContenidors);
        
   	 	//Enviar emails no agrupats
        final String codiEnviarEmailsNoAgrupats = "enviarEmailsNoAgrupats";
		monitorTasquesService.addTasca(codiEnviarEmailsNoAgrupats);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {
                    	monitorTasquesService.inici(codiEnviarEmailsNoAgrupats);
                        try{ 
                        	segonPlaService.enviarEmailsPendentsNoAgrupats();
                        	monitorTasquesService.fi(codiEnviarEmailsNoAgrupats);
                        } catch(Exception e) {                        	
                        	monitorTasquesService.error(codiEnviarEmailsNoAgrupats);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                    	Long value = null;
						try {
							value = configHelper.getAsLong("es.caib.distribucio.segonpla.email.bustia.periode.enviament.no.agrupat");
						} catch (Exception e) {
							logger.warn("Error consultant la propietat per la propera execució d'enviar emails no agrupats: " + e.getMessage());
						}
                    	if (value == null) 
                    		value = new Long("60000");
                    	
                    	PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
                    	trigger.setInitialDelay(value);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        
                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
        				monitorTasquesService.updateProperaExecucio(codiEnviarEmailsNoAgrupats, longNextExecution);
        				
        				
                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(codiEnviarEmailsNoAgrupats);
        
        
   	 	//Enviar emails agrupats
        final String codiEnviarEmailsAgrupats = "enviarEmailsAgrupats";
		monitorTasquesService.addTasca(codiEnviarEmailsAgrupats);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {
                    	monitorTasquesService.inici(codiEnviarEmailsAgrupats);
                        try{ 
                        	segonPlaService.enviarEmailsPendentsAgrupats();
                        	monitorTasquesService.fi(codiEnviarEmailsAgrupats);
                        } catch(Exception e) {                        	
                        	monitorTasquesService.error(codiEnviarEmailsAgrupats);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                    	String value = null;
						try {
							value = configHelper.getConfig("es.caib.distribucio.segonpla.email.bustia.cron.enviament.agrupat");
						} catch (Exception e) {
							logger.warn("Error consultant la propietat per la propera execució d'enviar emails agrupats: " + e.getMessage());
						}
                    	if (value == null) 
							value = "* * * * * *";
                    	
                    	CronTrigger trigger = new CronTrigger(value);
                    	
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
                        
        				monitorTasquesService.updateProperaExecucio(codiEnviarEmailsAgrupats, longNextExecution);

        				
                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(codiEnviarEmailsAgrupats);
        
        // Calcular dades estadístiques històriques
        final String codiCalularDadesHistoriques = "calcularDadesHistoriques";
		monitorTasquesService.addTasca(codiCalularDadesHistoriques);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {
                    	monitorTasquesService.inici(codiCalularDadesHistoriques);
                        try{ 
                        	segonPlaService.calcularDadesHistoriques();
                        	monitorTasquesService.fi(codiCalularDadesHistoriques);
                        } catch(Exception e) {                        	
                        	monitorTasquesService.error(codiCalularDadesHistoriques);
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
                }
        );
        monitorTasquesService.addTasca(codiCalularDadesHistoriques);
        
   	 	// Esborra les dades antigues del monitor d'integracions
        final String codiEsborrarDadesAntigues = "esborrarDadesAntigues";
		monitorTasquesService.addTasca(codiEsborrarDadesAntigues);
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {
                    	monitorTasquesService.inici(codiEsborrarDadesAntigues);
                        try{ 
                        	segonPlaService.esborrarDadesAntigesMonitorIntegracio();
                        	monitorTasquesService.fi(codiEsborrarDadesAntigues);
                        } catch(Exception e) {                        	
                        	monitorTasquesService.error(codiEsborrarDadesAntigues);
                        }
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                    	
                    	Long value = null;
						try {
							value = configHelper.getAsLong("es.caib.distribucio.tasca.monitor.integracio.esborrar.antics.periode");
						} catch (Exception e) {
							logger.warn("Error consultant la propietat per la propera execució d'esborrar dades monitor integració antigues: " + e.getMessage());
						}
                    	if (value == null) {
                    		value = new Long("3600000"); // Per defecte un cop cada hora per defecte
                    	}
                    	
                        PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
                        trigger.setInitialDelay(value);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        
                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
        				monitorTasquesService.updateProperaExecucio(codiEsborrarDadesAntigues, longNextExecution);
        				
        				
                        return nextExecution;
                    }
                }
        );
        monitorTasquesService.addTasca(codiEsborrarDadesAntigues);
        
        
        // Reintentar processament al backoffice
        final String codiReintentarProcessament = "reintentarProcessament";
		monitorTasquesService.addTasca(codiReintentarProcessament);
        taskRegistrar.addTriggerTask(
        		new Runnable() {
					@Override
					public void run() {
                    	monitorTasquesService.inici(codiReintentarProcessament);
                        try{ 
                        	segonPlaService.reintentarProcessamentBackoffice();
                        	monitorTasquesService.fi(codiReintentarProcessament);
                        } catch(Exception e) {                        	
                        	monitorTasquesService.error(codiReintentarProcessament);
                        }
					}        			
        		}, 
        		new Trigger() {
					@Override
					public Date nextExecutionTime(TriggerContext triggerContext) {
						Long value = null;
						try {
							value = configHelper.getAsLong("es.caib.distribucio.backoffice.interval.temps.reintentar.processament");
						}catch (Exception e) {
							logger.warn("Error consultant la propietat per la propera execució per reintentar l'enviament al backoffice");
						}
						if (value == null) {
							value = new Long("60000");
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
                        try{ 
                        	segonPlaService.actualitzarProcediments();
                        	monitorTasquesService.fi(codiActualitzarProcediments);
                        } catch(Exception e) {                        	
                        	monitorTasquesService.error(codiActualitzarProcediments);
                        }
					}        			
        		}, 
        		new Trigger() {
					@Override
					public Date nextExecutionTime(TriggerContext triggerContext) {
						
						String propertyValue = null;
						try {
							propertyValue = configHelper.getConfig("es.caib.distribucio.tasca.monitor.integracio.actualitzar.procediments");
						}catch (Exception e) {
							logger.warn("Error consultant la propietat per la propera execució per actualitzar la taula de procediments");
						}
						if (propertyValue == null) {
							propertyValue = "0 30 15 * * 5";
						}
                    	CronTrigger trigger = new CronTrigger(propertyValue);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
        				monitorTasquesService.updateProperaExecucio(codiActualitzarProcediments, longNextExecution);

                        return nextExecution;
					}        			
        		}
        );
        monitorTasquesService.addTasca(codiActualitzarProcediments);
    }
    
	private static final Logger logger = LoggerFactory.getLogger(SegonPlaConfig.class);

}