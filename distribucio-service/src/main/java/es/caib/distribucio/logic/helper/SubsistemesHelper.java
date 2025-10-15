package es.caib.distribucio.logic.helper;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import es.caib.comanda.ms.salut.model.EstatByPercent;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.SubsistemaSalut;
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

    private static MeterRegistry registry;
    private static MeterRegistry localRegistry = new SimpleMeterRegistry();

    private static final Map<SubsistemesEnum, Metrics> METRICS = new EnumMap<>(SubsistemesEnum.class);
    private static boolean init = false;

    private static class Metrics {
        Timer timerOkGlobal;
        Counter counterErrorGlobal;
        Timer timerOkLocal;
        Counter counterErrorLocal;
        EstatSalutEnum darrerEstat = EstatSalutEnum.UNKNOWN;
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
            Metrics m = METRICS.computeIfAbsent(s, k -> new Metrics());

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
    }
    
    private static void resetLocalTimers() {
        // Re-crea el localRegistry i totes les mètriques locals per subsistema
        initializeMetrics();
    }
    public static void addSuccessOperation(SubsistemesEnum subsistema, long duracio) {
        Metrics m = METRICS.get(subsistema);
        if (m == null) {
            // Lazy init si cal
            initializeMetrics();
            m = METRICS.get(subsistema);
        }
        if (m.timerOkGlobal != null) {
            m.timerOkGlobal.record(duracio, TimeUnit.MILLISECONDS);
        }
        if (m.timerOkLocal != null) {
            m.timerOkLocal.record(duracio, TimeUnit.MILLISECONDS);
        }
        
        cuaPeticions.add(true);
    }

    public static void addErrorOperation(SubsistemesEnum subsistema) {
        Metrics m = METRICS.get(subsistema);
        if (m == null) {
            // Lazy init si cal
            initializeMetrics();
            m = METRICS.get(subsistema);
        }
        if (m.counterErrorGlobal != null) {
            m.counterErrorGlobal.increment();
        }
        if (m.counterErrorLocal != null) {
            m.counterErrorLocal.increment();
        }
        
        cuaPeticions.add(false);
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

        for (SubsistemesEnum s : SubsistemesEnum.values()) {
            Metrics m = METRICS.get(s);
            if (m == null) {
                // Garantim que hi hagi mètriques
                initializeMetrics();
                m = METRICS.get(s);
            }

            final int tempsMigPeriode = m.timerOkLocal != null ? (int) m.timerOkLocal.mean(TimeUnit.MILLISECONDS) : 0;
            final Long totalOkPeriode = m.timerOkLocal != null ? m.timerOkLocal.count() : 0L;
            final Long totalErrorPeriode = m.counterErrorLocal != null ? (long) m.counterErrorLocal.count() : 0L;

            final int tempsMigGlobal = m.timerOkGlobal != null ? (int) m.timerOkGlobal.mean(TimeUnit.MILLISECONDS) : 0;
            final Long totalOkGlobal = m.timerOkGlobal != null ? m.timerOkGlobal.count() : 0L;
            final Long totalErrorGlobal = m.counterErrorGlobal != null ? (long) m.counterErrorGlobal.count() : 0L;

            final EstatSalutEnum estat = calculaEstat(totalOkPeriode, totalErrorPeriode, s);

            subsistemasSalut.add(SubsistemaSalut.builder()
                    .codi(s.name())
                    .latencia(tempsMigPeriode)
                    .estat(estat)
                    .totalOk(totalOkGlobal)
                    .totalError(totalErrorGlobal)
                    .totalTempsMig(tempsMigGlobal)
                    .peticionsOkUltimPeriode(totalOkPeriode)
                    .peticionsErrorUltimPeriode(totalErrorPeriode)
                    .tempsMigUltimPeriode(tempsMigPeriode)
                    .build());
        }

        resetLocalTimers();
        return subsistemasSalut;
    }

    private static EstatSalutEnum calculaEstat(Long totalPeticionsOk, Long totalPeticionsError, SubsistemesEnum subsistema) {
    	var totalPeticions = totalPeticionsOk + totalPeticionsError;
        if (totalPeticions == 0L) {
            return getDarrerEstat(subsistema);
        }
        long ok;
        long ko;
        if (totalPeticions >= 20) {
            ok = totalPeticionsOk;
            ko = totalPeticionsError;
        } else {
            ok = !cuaPeticions.isEmpty() ? cuaPeticions.getOk() : 0L;
            ko = !cuaPeticions.isEmpty() ? cuaPeticions.getError() : 0L;
        }
        final long total = ok + ko;
        // Percentatge d'errors arrodonit correctament evitant divisió d'enters
        final int errorRatePct = (int) Math.round((ko * 100.0) / Math.max(1L, total));
        EstatSalutEnum estat = EstatByPercent.calculaEstat(errorRatePct);
        setDarrerEstat(subsistema, estat);
        return estat;
    }

    private static EstatSalutEnum getDarrerEstat(SubsistemesEnum subsistema) {
        final Metrics m = METRICS.get(subsistema);
        return m != null && m.darrerEstat != null ? m.darrerEstat : EstatSalutEnum.UNKNOWN;
    }

    private static void setDarrerEstat(SubsistemesEnum subsistema, EstatSalutEnum estat) {
        Metrics m = METRICS.get(subsistema);
        if (m == null) {
            initializeMetrics();
            m = METRICS.get(subsistema);
        }
        m.darrerEstat = estat;
    }

    private static EstatSalutEnum calculateGlobalHealth(List<SubsistemaSalut> subsistemes) {
        // Ordre de severitat: DOWN > ERROR > DEGRADED > WARN > UP > UNKNOWN
        boolean anyDown = false, anyError = false, anyDegraded = false, anyWarn = false, anyUp = false;
        boolean isCritic;
        for (var s : subsistemes) {
            isCritic = SubsistemesEnum.valueOf(s.getCodi()).isSistemaCritic();
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
