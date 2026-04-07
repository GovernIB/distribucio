package es.caib.distribucio.logic.helper;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import es.caib.comanda.ms.salut.helper.EstatHelper;
import es.caib.comanda.model.server.monitoring.EstatSalutEnum;
import es.caib.comanda.model.server.monitoring.SubsistemaSalut;
import es.caib.distribucio.logic.utils.CuaFifoBool;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubsistemesHelper {
    
    private final MeterRegistry meterRegistry;

    private static final CuaFifoBool cuaPeticions = new CuaFifoBool(20);

    // Llindars d'avís en percentatge (0-100)
    private static final int DOWN_PCT = 100;     // 100% errors
    private static final int ERROR_GT_PCT = 30;  // >30% errors
    private static final int DEGRADED_GT_PCT = 10; // >10% errors
    private static final int UP_LT_PCT = 5;      // <5% errors

    private static MeterRegistry registry = new SimpleMeterRegistry();
    private static MeterRegistry localRegistry = new SimpleMeterRegistry();

    private static final Map<String, Metrics> METRICS = new HashMap<>();
    static {
        for (SubsistemesEnum s : SubsistemesEnum.values()) {
            getMetrica(s.name());
        }
    }
    private static boolean init = false;

    private static class Metrics {
        public Timer timerOkGlobal;
        public Counter counterErrorGlobal;
        public Timer timerOkLocal;
        public Counter counterErrorLocal;
        public EstatSalutEnum darrerEstat = EstatSalutEnum.UNKNOWN;

        public Metrics(String nom) {
            // Globals al registry principal (si disponible)
            this.timerOkGlobal = registry.timer("subsistema." + nom);
            this.counterErrorGlobal = registry.counter("subsistema." + nom + ".errors");

            // Locals per a salut
            this.timerOkLocal = localRegistry.timer("subsistema." + nom + ".local");
            this.counterErrorLocal = localRegistry.counter("subsistema." + nom + ".local.errors");
        }

        public void addSuccess(Long duracio) {
            this.timerOkGlobal.record(duracio, TimeUnit.MILLISECONDS);
            this.timerOkLocal.record(duracio, TimeUnit.MILLISECONDS);
        }

        public void addError() {
            this.counterErrorGlobal.increment();
            this.counterErrorLocal.increment();
        }

        public EstatSalutEnum getEstatPeriode() {
            var totalPeticions = this.timerOkLocal.count() + this.counterErrorLocal.count();
            if (totalPeticions == 0L) {
                return this.darrerEstat;
            }
            long ok;
            long ko;
            if (totalPeticions >= 20) {
                ok = this.timerOkLocal.count();
                ko = (long) this.counterErrorLocal.count();
            } else {
                ok = !cuaPeticions.isEmpty() ? cuaPeticions.getOk() : 0L;
                ko = !cuaPeticions.isEmpty() ? cuaPeticions.getError() : 0L;
            }
            final long total = ok + ko;
            // Percentatge d'errors arrodonit correctament evitant divisió d'enters
            final int errorRatePct = (int) Math.round((ko * 100.0) / Math.max(1L, total));
            EstatSalutEnum estat = EstatHelper.calculaEstat(errorRatePct);
            this.darrerEstat = estat;
            return estat;
        }
    }


    @PostConstruct
    public void init() {
        if (registry == null) {
            registry = meterRegistry;

            if (registry == null) {
                log.warn("MeterRegistry no inicialitzat. No es registraran mètriques globals fins que s'estableixi el registry.");
                return;
            }

            initializeMetrics();
        }
    }

    private static void initializeMetrics() {
        // Inicialitza registre local i mètriques per cada subsistema
        if (localRegistry != null) {
            try {
                localRegistry.close();
            } catch (Exception ignore) {
                // Intencionadament ignorat
            }
        }
        localRegistry = new SimpleMeterRegistry();

        for (SubsistemesEnum s : SubsistemesEnum.values()) {
            Metrics m = METRICS.computeIfAbsent(String.valueOf(s), k -> new Metrics(String.valueOf(s)));

            // Globals al registry principal (si disponible)
            if (registry != null && !init) {
                m.timerOkGlobal = Timer.builder("subsistema." + s.name().toLowerCase())
                        .tags("result", "success")
                        .publishPercentiles(0.5, 0.75, 0.95, 0.99)
                        .publishPercentileHistogram()
                        .register(registry);
                m.counterErrorGlobal = Counter.builder("subsistema." + s.name().toLowerCase() + ".errors")
                        .register(registry);
            }

            // Locals per a salut
            m.timerOkLocal = Timer.builder("subsistema." + s.name().toLowerCase() + ".local")
                    .tags("result", "success")
                    .publishPercentiles(0.5, 0.75, 0.95, 0.99)
                    .publishPercentileHistogram()
                    .register(localRegistry);
            m.counterErrorLocal = Counter.builder("subsistema." + s.name().toLowerCase() + ".local.errors")
                    .register(localRegistry);
        }

        if (registry != null) {
            init = true;
        }
    }

    @Getter
    public enum SubsistemesEnum {
    	
    	AWS("Alta Registre WS", true),
        BKC("Backoffice consulta", true),
        BKE("Backoffice canvi estat", true),
        BKL("Backoffice llistar", true),
        RGB("Aplicar Regla tipus Backoffice", true),
        GDO("Gestió documental FileSystem", true);

    	private final String nom;
        private final boolean sistemaCritic;
        SubsistemesEnum(String nom, boolean sistemaCritic) {
            this.nom = nom;
            this.sistemaCritic = sistemaCritic;
        }

        public static SubsistemesEnum valueOfCodi(String codi) {
            for (SubsistemesEnum subsistema : SubsistemesEnum.values()) {
                if (subsistema.name().equals(codi)) {
                    return subsistema;
                }
            }
            return null;
        }
    }

    public static Metrics getMetrica(String subsistema) {
        Metrics metrica = METRICS.get(subsistema);
        if (metrica == null) {
            metrica = new Metrics(subsistema);
            METRICS.put(subsistema, metrica);
        }

        return metrica;
    }
    
    private static void resetLocalTimers() {
        // Re-crea el localRegistry i totes les mètriques locals per subsistema
        initializeMetrics();
    }
    public static void addSuccessOperation(SubsistemesEnum subsistema, long duracio) {
        addSuccessOperation(String.valueOf(subsistema), duracio);
    }
    public static void addSuccessOperation(String subsistema, long duracio) {
        getMetrica(subsistema).addSuccess(duracio);
    }

    public static void addErrorOperation(SubsistemesEnum subsistema) {
        addErrorOperation(String.valueOf(subsistema));
    }
    public static void addErrorOperation(String subsistema) {
        getMetrica(subsistema).addError();
    }

    public static SubsistemesInfo getSubsistemesInfo() {
        final List<SubsistemaSalut> subsistemasSalut = getSubsistemesSalut();
        final EstatSalutEnum estatGlobal = calculateGlobalHealth(subsistemasSalut);
        return SubsistemesInfo.builder()
                .subsistemesSalut(subsistemasSalut)
                .estatGlobal(estatGlobal)
                .build();
    }

    private static List<SubsistemaSalut> getSubsistemesSalut() {
        List<SubsistemaSalut> subsistemasSalut = new ArrayList<>();

        for (Map.Entry<String, Metrics> metricaEntry : METRICS.entrySet()) {
            String subsistema = metricaEntry.getKey();
            Metrics metrica = metricaEntry.getValue();

            subsistemasSalut.add(new SubsistemaSalut()
                    .codi(subsistema)
                    .latencia((int) metrica.timerOkLocal.mean(TimeUnit.MILLISECONDS))
                    .estat(metrica.getEstatPeriode())
                    .totalOk(metrica.timerOkGlobal.count())
                    .totalError((long) metrica.counterErrorGlobal.count())
                    .totalTempsMig((int) metrica.timerOkGlobal.mean(TimeUnit.MILLISECONDS))
                    .peticionsOkUltimPeriode(metrica.timerOkLocal.count())
                    .peticionsErrorUltimPeriode((long) metrica.counterErrorLocal.count())
                    .tempsMigUltimPeriode((int) metrica.timerOkLocal.mean(TimeUnit.MILLISECONDS))
            );
        }

        resetLocalTimers();
        return subsistemasSalut;
    }

    private static EstatSalutEnum calculateGlobalHealth(List<SubsistemaSalut> subsistemes) {
        // Ordre de severitat: DOWN > ERROR > DEGRADED > WARN > UP > UNKNOWN
        boolean anyDown = false, anyError = false, anyDegraded = false, anyWarn = false, anyUp = false;
        for (var s : subsistemes) {
            SubsistemesEnum subsistemaEnum = SubsistemesEnum.valueOfCodi(s.getCodi());
            boolean isCritic = subsistemaEnum != null && subsistemaEnum.isSistemaCritic();
            switch (s.getEstat()) {
                case UP:
                    anyUp = true;
                    break;
                case WARN:
                    anyWarn = true;
                    break;
                case DEGRADED:
                    if (isCritic) {
                        anyDegraded = true;
                    } else {
                        anyWarn = true;
                    }
                    break;
                case ERROR:
                    if (isCritic) {
                        anyError = true;
                    } else {
                        anyWarn = true;
                    }
                    break;
                case DOWN:
                    if (isCritic) {
                        anyDown = true;
                    } else {
                        anyWarn = true;
                    }
                    break;
                default:
                    // UNKNOWN o altres
            }
        }
        
        if (anyError || anyDown)  {
            return EstatSalutEnum.ERROR;
        }
        if (anyDegraded) {
            return EstatSalutEnum.DEGRADED;
        }
        if (anyWarn) {
            return EstatSalutEnum.WARN;
        }
        if (anyUp) {
            return EstatSalutEnum.UP;
        }
        return EstatSalutEnum.UNKNOWN;
    }


    @Getter
    @Builder
    public static class SubsistemesInfo {
        private final List<SubsistemaSalut> subsistemesSalut;
        private final EstatSalutEnum estatGlobal;
    }

}
