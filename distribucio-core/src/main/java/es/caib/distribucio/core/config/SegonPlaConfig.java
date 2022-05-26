package es.caib.distribucio.core.config;

import java.util.Date;
import java.util.concurrent.TimeUnit;

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

   	 	//Guardar anotacions de registre amb estat pendent de guardar a l'arxiu.
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {
                        segonPlaService.guardarAnotacionsPendentsEnArxiu();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                    	
                    	Long value = configHelper.getAsLong("es.caib.distribucio.tasca.guardar.annexos.temps.espera.execucio");
                    	if (value == null) 
                    		value = new Long("60000");
                        PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );
        
        
   	 	//Enviar annotacions al backoffice
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {
                        segonPlaService.enviarIdsAnotacionsPendentsBackoffice();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                    	
                    	Long value = configHelper.getAsLong("es.caib.distribucio.tasca.enviar.anotacions.backoffice.temps.espera.execucio");
                    	if (value == null) 
                    		value = new Long("60000");
                    	PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );
        
   	 	//Aplicar regles de tipus backoffice
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {
                        segonPlaService.aplicarReglesPendentsBackoffice();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                    	Long value = configHelper.getAsLong("es.caib.distribucio.tasca.aplicar.regles.temps.espera.execucio");
                    	if (value == null) 
                    		value = new Long("60000");
                    	PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );
        
        
   	 	//Tancar contenidors
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {
                        segonPlaService.tancarContenidorsArxiuPendents();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                    	Long value = configHelper.getAsLong("es.caib.distribucio.tasca.tancar.contenidors.temps.espera.execucio");
                    	if (value == null) 
                    		value = new Long("60000");
                    	PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);

                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );
        
   	 	//Enviar emails no agrupats
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {
                        segonPlaService.enviarEmailsPendentsNoAgrupats();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                    	Long value = configHelper.getAsLong("es.caib.distribucio.segonpla.email.bustia.periode.enviament.no.agrupat");
                    	if (value == null) 
                    		value = new Long("60000");
                    	PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);

                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );
        
        
   	 	//Enviar emails agrupats
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {
                        segonPlaService.enviarEmailsPendentsAgrupats();
                    }
                },
                new Trigger() {
                    @Override
                    public Date nextExecutionTime(TriggerContext triggerContext) {
                    	String value = configHelper.getConfig("es.caib.distribucio.segonpla.email.bustia.cron.enviament.agrupat");
                    	if (value == null) 
							value = "* * * * * *";
                    	
                    	CronTrigger trigger = new CronTrigger(value);

                        Date nextExecution = trigger.nextExecutionTime(triggerContext);
                        return nextExecution;
                    }
                }
        );
        
        // Calcular dades estadístiques històriques
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {
                        segonPlaService.calcularDadesHistoriques();;
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
                        
                        // Cada 60s
                    	//Long value = new Long("60000");
                    	//PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
                        //Date nextExecution = trigger.nextExecutionTime(triggerContext);

                        return nextExecution;
                    }
                }
        );        

    }
}