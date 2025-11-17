package es.caib.distribucio.logic.config;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.security.core.context.SecurityContextHolder;

import es.caib.distribucio.logic.intf.service.ConfigService;
import es.caib.distribucio.logic.intf.service.ExecucioMassivaService;
import es.caib.distribucio.logic.intf.service.MonitorTasquesService;
import es.caib.distribucio.logic.intf.service.SegonPlaService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableAsync
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {

    @Autowired
    private ExecucioMassivaService execucioMassivaService;
    @Autowired
    private SegonPlaService segonPlaService;
    //    @Autowired private AplicacioService aplicacioService;
    @Autowired
    private MonitorTasquesService monitorTasquesService;
    //    @Autowired private ConfigHelper configHelper;
    @Autowired
    private ConfigService configService;
    //
//    private Boolean[] primeraVez = {
//            Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE
//    };
//
//    private static final long DEFAULT_INITIAL_DELAY_MS = 30000L;
    private ScheduledTaskRegistrar taskRegistrar;
    //    //Mantenir un registre de les tasques que s'han enregistrat
    private final Map<String, Runnable> tasks = new HashMap<>();
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<>();
//

    // CONFIGURAR LES DE DISTRIBUCIO    
    private final String codiGuardarAnotacionsPendents = "guardarAnotacionsPendents";
    private final String codiEnviarBackoffice = "enviarAlBackoffice";
    private final String codiAplicarReglesBackoffice = "aplicarReglesBackoffice";
    private final String codiTancarContenidors = "tancarContenidors";
    private final String codiEnviarEmailsNoAgrupats = "enviarEmailsNoAgrupats";
    private final String codiEnviarEmailsAgrupats = "enviarEmailsAgrupats";
    private final String codiCalularDadesHistoriques = "calcularDadesHistoriques";
    private final String codiEsborrarDadesAntigues = "esborrarDadesAntigues";
    private final String codiReintentarProcessament = "reintentarProcessament";
    private final String codiActualitzarProcediments = "actualitzarProcediments";
    private final String codiActualitzarServeis = "actualitzarServeis";
    private final String codiEnviarEmailsAnotacionsErrorProcessament = "enviarEmailsAnotacionsErrorProcessament";
    private final String codiExecucionMassives = "execucionsMassives";


    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        return taskScheduler;
    }

    public void restartSchedulledTasks(String taskCodi) {
        if (taskRegistrar != null) {
            //taskRegistrar.destroy();
            //taskRegistrar.afterPropertiesSet();

            if ("totes".equals(taskCodi)) {
                for (String task : tasks.keySet()) {
                    rescheduleTask(task, getTrigger(task));
                }
            } else if (tasks.containsKey(taskCodi)) {
                rescheduleTask(taskCodi, getTrigger(taskCodi));
            }
        }
    }

    public void addTask(String taskId, Runnable task, Trigger trigger) {
        monitorTasquesService.addTasca(taskId);
        tasks.put(taskId, task);
        ScheduledFuture<?> scheduledTask = taskRegistrar.getScheduler().schedule(task, trigger);
        scheduledTasks.put(taskId, scheduledTask);
    }

    public void rescheduleTask(String taskId, Trigger newTrigger) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.get(taskId);
        if (scheduledTask != null) {
            // Cancelar la tarea existente
            scheduledTask.cancel(true);
            // Añadir la tarea con el nuevo trigger
            Runnable task = tasks.get(taskId);
            if (task != null) {
                ScheduledFuture<?> newScheduledTask = taskRegistrar.getScheduler().schedule(task, newTrigger);
                scheduledTasks.put(taskId, newScheduledTask);
            }
        }
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskScheduler());
        this.taskRegistrar = taskRegistrar;

        addTask(
                codiGuardarAnotacionsPendents,
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        monitorTasquesService.inici(codiGuardarAnotacionsPendents);
                        try {
                            segonPlaService.guardarAnotacionsPendentsEnArxiu();
                            monitorTasquesService.fi(codiGuardarAnotacionsPendents);
                        } catch (Throwable th) {
                            tractarErrorTascaSegonPla(th, codiGuardarAnotacionsPendents);
                        } finally {
                            SecurityContextHolder.clearContext();
                        }
                    }
                },
                getTrigger(codiGuardarAnotacionsPendents)
        );

        addTask(
                codiEnviarBackoffice,
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        monitorTasquesService.inici(codiEnviarBackoffice);
                        try {
                            segonPlaService.enviarIdsAnotacionsPendentsBackoffice();
                            monitorTasquesService.fi(codiEnviarBackoffice);
                        } catch (Throwable th) {
                            tractarErrorTascaSegonPla(th, codiEnviarBackoffice);
                        } finally {
                            SecurityContextHolder.clearContext();
                        }
                    }
                },
                getTrigger(codiEnviarBackoffice)
        );

        addTask(
                codiAplicarReglesBackoffice,
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        monitorTasquesService.inici(codiAplicarReglesBackoffice);
                        try {
                            segonPlaService.aplicarReglesPendentsBackoffice();
                            monitorTasquesService.fi(codiAplicarReglesBackoffice);
                        } catch (Throwable th) {
                            tractarErrorTascaSegonPla(th, codiAplicarReglesBackoffice);
                        } finally {
                            SecurityContextHolder.clearContext();
                        }
                    }
                },
                getTrigger(codiAplicarReglesBackoffice)
        );

        addTask(
                codiTancarContenidors,
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        monitorTasquesService.inici(codiTancarContenidors);
                        try {
                            segonPlaService.tancarContenidorsArxiuPendents();
                            monitorTasquesService.fi(codiTancarContenidors);
                        } catch (Throwable th) {
                            tractarErrorTascaSegonPla(th, codiTancarContenidors);
                        } finally {
                            SecurityContextHolder.clearContext();
                        }
                    }
                },
                getTrigger(codiTancarContenidors)
        );

        addTask(
                codiEnviarEmailsNoAgrupats,
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        monitorTasquesService.inici(codiEnviarEmailsNoAgrupats);
                        try {
                            segonPlaService.enviarEmailsPendentsNoAgrupats();
                            monitorTasquesService.fi(codiEnviarEmailsNoAgrupats);
                        } catch (Throwable th) {
                            tractarErrorTascaSegonPla(th, codiEnviarEmailsNoAgrupats);
                        } finally {
                            SecurityContextHolder.clearContext();
                        }
                    }
                },
                getTrigger(codiEnviarEmailsNoAgrupats)
        );

        addTask(
                codiEnviarEmailsAgrupats,
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        monitorTasquesService.inici(codiEnviarEmailsAgrupats);
                        try {
                            segonPlaService.enviarEmailsPendentsAgrupats();
                            monitorTasquesService.fi(codiEnviarEmailsAgrupats);
                        } catch (Throwable th) {
                            tractarErrorTascaSegonPla(th, codiEnviarEmailsAgrupats);
                        } finally {
                            SecurityContextHolder.clearContext();
                        }
                    }
                },
                getTrigger(codiEnviarEmailsAgrupats)
        );

        addTask(
                codiCalularDadesHistoriques,
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        monitorTasquesService.inici(codiCalularDadesHistoriques);
                        try {
                            segonPlaService.calcularDadesHistoriques();
                            monitorTasquesService.fi(codiCalularDadesHistoriques);
                        } catch (Throwable th) {
                            tractarErrorTascaSegonPla(th, codiCalularDadesHistoriques);
                        } finally {
                            SecurityContextHolder.clearContext();
                        }
                    }
                },
                getTrigger(codiCalularDadesHistoriques)
        );

        addTask(
                codiEsborrarDadesAntigues,
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        monitorTasquesService.inici(codiEsborrarDadesAntigues);
                        try {
                            segonPlaService.esborrarDadesAntigesMonitorIntegracio();
                            monitorTasquesService.fi(codiEsborrarDadesAntigues);
                        } catch (Throwable th) {
                            tractarErrorTascaSegonPla(th, codiEsborrarDadesAntigues);
                        } finally {
                            SecurityContextHolder.clearContext();
                        }
                    }
                },
                getTrigger(codiEsborrarDadesAntigues)
        );

        addTask(
                codiReintentarProcessament,
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        monitorTasquesService.inici(codiReintentarProcessament);
                        try {
                            segonPlaService.reintentarProcessamentBackoffice();
                            monitorTasquesService.fi(codiReintentarProcessament);
                        } catch (Throwable th) {
                            tractarErrorTascaSegonPla(th, codiReintentarProcessament);
                        } finally {
                            SecurityContextHolder.clearContext();
                        }
                    }
                },
                getTrigger(codiReintentarProcessament)
        );

        addTask(
                codiActualitzarProcediments,
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        monitorTasquesService.inici(codiActualitzarProcediments);
                        try {
                            segonPlaService.actualitzarProcediments();
                            monitorTasquesService.fi(codiActualitzarProcediments);
                        } catch (Throwable th) {
                            tractarErrorTascaSegonPla(th, codiActualitzarProcediments);
                        } finally {
                            SecurityContextHolder.clearContext();
                        }
                    }
                },
                getTrigger(codiActualitzarProcediments)
        );

        addTask(
                codiActualitzarServeis,
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        monitorTasquesService.inici(codiActualitzarServeis);
                        try {
                            segonPlaService.actualitzarServeis();
                            monitorTasquesService.fi(codiActualitzarServeis);
                        } catch (Throwable th) {
                            tractarErrorTascaSegonPla(th, codiActualitzarServeis);
                        } finally {
                            SecurityContextHolder.clearContext();
                        }
                    }
                },
                getTrigger(codiActualitzarServeis)
        );

        addTask(
                codiExecucionMassives,
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        monitorTasquesService.inici(codiExecucionMassives);
                        try {
                            segonPlaService.executeNextMassiveScheduledTask();
                            monitorTasquesService.fi(codiExecucionMassives);
                        } catch (Throwable th) {
                            tractarErrorTascaSegonPla(th, codiExecucionMassives);
                        } finally {
                            SecurityContextHolder.clearContext();
                        }
                    }
                },
                getTrigger(codiExecucionMassives)
        );

        addTask(
                codiEnviarEmailsAnotacionsErrorProcessament,
                new Runnable() {
                    @SneakyThrows
                    @Override
                    public void run() {
                        monitorTasquesService.inici(codiEnviarEmailsAnotacionsErrorProcessament);
                        try {
                            segonPlaService.enviarEmailsAnotacionsErrorProcessament();
                            monitorTasquesService.fi(codiEnviarEmailsAnotacionsErrorProcessament);
                        } catch (Throwable th) {
                            tractarErrorTascaSegonPla(th, codiEnviarEmailsAnotacionsErrorProcessament);
                        } finally {
                            SecurityContextHolder.clearContext();
                        }
                    }
                },
                getTrigger(codiEnviarEmailsAnotacionsErrorProcessament)
        );

    } //Fi de configureTasks

    private Date getPeriodicTriggerNextExecutionTime(TriggerContext triggerContext, String taskCodi, Long value) {
        PeriodicTrigger trigger = new PeriodicTrigger(value, TimeUnit.MILLISECONDS);
        trigger.setInitialDelay(value);
        Date nextExecution = trigger.nextExecutionTime(triggerContext);
        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
        monitorTasquesService.updateProperaExecucio(taskCodi, longNextExecution);
        return nextExecution;
    }

    private Date getCronTriggerNextExecutionTime(TriggerContext triggerContext, String taskCodi, String value) {
        CronTrigger trigger = new CronTrigger(value);
        Date nextExecution = trigger.nextExecutionTime(triggerContext);
        Long longNextExecution = nextExecution.getTime() - System.currentTimeMillis();
        monitorTasquesService.updateProperaExecucio(taskCodi, longNextExecution);
        return nextExecution;
    }

    private Trigger getTrigger(String taskCodi) {
        if (taskCodi.equals(codiGuardarAnotacionsPendents)) {
            return new Trigger() {
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
                    return getPeriodicTriggerNextExecutionTime(triggerContext, taskCodi, value);
                }
            };
        } else if (taskCodi.equals(codiEnviarBackoffice)) {
            return new Trigger() {
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
                    return getPeriodicTriggerNextExecutionTime(triggerContext, taskCodi, value);
                }
            };
        } else if (taskCodi.equals(codiAplicarReglesBackoffice)) {
            return new Trigger() {
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
                    return getPeriodicTriggerNextExecutionTime(triggerContext, taskCodi, value);
                }
            };
        } else if (taskCodi.equals(codiTancarContenidors)) {
            return new Trigger() {
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
                    return getPeriodicTriggerNextExecutionTime(triggerContext, taskCodi, value);
                }
            };
        } else if (taskCodi.equals(codiEnviarEmailsNoAgrupats)) {
            return new Trigger() {
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
                    return getPeriodicTriggerNextExecutionTime(triggerContext, taskCodi, value);
                }
            };
        } else if (taskCodi.equals(codiEnviarEmailsAgrupats)) {
            return new Trigger() {
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
                    return getCronTriggerNextExecutionTime(triggerContext, taskCodi, value);
                }
            };
        } else if (taskCodi.equals(codiCalularDadesHistoriques)) {
            return new Trigger() {
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
                    return getCronTriggerNextExecutionTime(triggerContext, taskCodi, value);
                }
            };
        } else if (taskCodi.equals(codiEsborrarDadesAntigues)) {
            return new Trigger() {
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
                    return getPeriodicTriggerNextExecutionTime(triggerContext, taskCodi, value);
                }
            };
        } else if (taskCodi.equals(codiReintentarProcessament)) {
            return new Trigger() {
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
                    return getPeriodicTriggerNextExecutionTime(triggerContext, taskCodi, value);
                }
            };
        } else if (taskCodi.equals(codiActualitzarProcediments)) {
            return new Trigger() {
                @Override
                public Date nextExecutionTime(TriggerContext triggerContext) {
                    String value = null;
                    try {
                        value = configService.getConfig("es.caib.distribucio.tasca.monitor.integracio.actualitzar.procediments");
                    } catch (Exception e) {
                        log.warn("Error consultant la propietat per la propera execució per actualitzar la taula de procediments");
                    }
                    if (value == null) {
                        value = "0 30 15 * * 5";
                    }
                    log.info("Actualitzant procediments");
                    return getCronTriggerNextExecutionTime(triggerContext, taskCodi, value);
                }
            };
        } else if (taskCodi.equals(codiActualitzarServeis)) {
            return new Trigger() {
                @Override
                public Date nextExecutionTime(TriggerContext triggerContext) {
                    String value = null;
                    try {
                        value = configService.getConfig("es.caib.distribucio.tasca.monitor.integracio.actualitzar.serveis");
                    } catch (Exception e) {
                        log.warn("Error consultant la propietat per la propera execució per actualitzar la taula de serveis");
                    }
                    if (value == null) {
                        value = "0 30 15 * * 5";
                    }
                    log.info("Actualitzant serveis");
                    return getCronTriggerNextExecutionTime(triggerContext, taskCodi, value);
                }
            };
        } else if (taskCodi.equals(codiEnviarEmailsAnotacionsErrorProcessament)) {
            return new Trigger() {
                @Override
                public Date nextExecutionTime(TriggerContext triggerContext) {
                    String value = null;
                    try {
                        value = configService.getConfig("es.caib.distribucio.enviar.anotacio.error.cron");
                    } catch (Exception e) {
                        log.warn("Error consultant la propietat per la propera execució per enviar per email les anotacions amb error al processament");
                    }
                    if (value == null) {
                        value = "0 0 0 * * *";
                    }
                    return getCronTriggerNextExecutionTime(triggerContext, taskCodi, value);
                }
            };
        } else if (taskCodi.equals(codiExecucionMassives)) {
            return new Trigger() {
                @Override
                public Date nextExecutionTime(TriggerContext triggerContext) {
                    String value = null;
                    try {
                        value = configService.getConfig("es.caib.distribucio.segonpla.cron.execucio.massiva");
                    } catch (Exception e) {
                        log.warn("Error consultant la propietat per la propera execució de les massives: " + e.getMessage());
                    }
                    if (value == null)
                        value = "0 */15 * * * *"; // 15 min
                    return getCronTriggerNextExecutionTime(triggerContext, taskCodi, value);
                }
            };
        }
        return null;
    }

    /** Enregistre l'error als logs i marca la tasca amb error. */
    private void tractarErrorTascaSegonPla(Throwable th, String codiTasca) {
        String errMsg = th.getClass() + ": " + th.getMessage() + " (" + new Date().getTime() + ")";
        log.error("Error no controlat a l'execució de la tasca en segon pla amb codi \"" + codiTasca + "\": " + errMsg, th);
        monitorTasquesService.error(codiTasca, errMsg);
    }

    private static final Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);
}