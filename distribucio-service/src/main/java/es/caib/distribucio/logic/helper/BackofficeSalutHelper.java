package es.caib.distribucio.logic.helper;

import es.caib.comanda.model.server.monitoring.EstatSalutEnum;
import es.caib.comanda.model.server.monitoring.IntegracioPeticions;
import es.caib.comanda.model.server.monitoring.IntegracioSalut;
import es.caib.comanda.ms.salut.helper.EstatHelper;
import es.caib.distribucio.logic.utils.CuaFifoBool;
import es.caib.distribucio.persist.entity.BackofficeEntity;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class BackofficeSalutHelper {
    
    private final MeterRegistry meterRegistry;

    private static final CuaFifoBool cuaPeticions = new CuaFifoBool(20);

    private static MeterRegistry registry = new SimpleMeterRegistry();
    private static MeterRegistry localRegistry = new SimpleMeterRegistry();

    private static final Map<String, Metrics> METRICS = new HashMap<>();

    @Getter
    public static class Metrics {
        private final String codi;
        private final String entitat;
        private final String endpoint;

        public Timer timerOkGlobal;
        public Counter counterErrorGlobal;
        public Timer timerOkLocal;
        public Counter counterErrorLocal;
        public EstatSalutEnum darrerEstat = EstatSalutEnum.UNKNOWN;

        public Metrics(String codi, String entitat, String endpoint) {
            this.codi = codi;
            this.entitat = entitat;
            this.endpoint = endpoint;

            // Globals al registry principal (si disponible)
            this.timerOkGlobal = registry.timer("backoffice." + codi);
            this.counterErrorGlobal = registry.counter("backoffice." + codi + ".errors");

            // Locals per a salut
            this.timerOkLocal = localRegistry.timer("backoffice." + codi + ".local");
            this.counterErrorLocal = localRegistry.counter("backoffice." + codi + ".local.errors");
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

        public IntegracioSalut getSalutInfo() {
            IntegracioPeticions peticion = new IntegracioPeticions()
                    .endpoint(endpoint)
                    .totalOk(this.timerOkGlobal.count())
                    .totalError((long) this.counterErrorGlobal.count())
                    .totalTempsMig((int) this.timerOkGlobal.mean(TimeUnit.MILLISECONDS))
                    .peticionsOkUltimPeriode(this.timerOkLocal.count())
                    .peticionsErrorUltimPeriode((long) this.counterErrorLocal.count())
                    .tempsMigUltimPeriode((int) this.timerOkLocal.mean(TimeUnit.MILLISECONDS));
            return new IntegracioSalut()
                    .codi(this.codi)
                    .latencia((int) this.timerOkLocal.mean(TimeUnit.MILLISECONDS))
                    .estat(this.getEstatPeriode())
                    .peticions(peticion);
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
    }

    public static Metrics getMetrica(Long id, String codi, String entitatCodi, String endpoint) {
        Metrics metrica = METRICS.get(String.valueOf(id));
        if (metrica == null) {
            metrica = new Metrics(
                    codi,
                    entitatCodi,
                    endpoint
            );
            METRICS.put(String.valueOf(id), metrica);
        }

        return metrica;
    }

    public static Metrics getMetrica(BackofficeEntity backoffice) {
        Metrics metrica = METRICS.get(String.valueOf(backoffice.getId()));
        if (metrica == null) {
            metrica = new Metrics(
                    backoffice.getCodi(),
                    backoffice.getEntitat().getCodi(),
                    backoffice.getUrl()
            );
            METRICS.put(String.valueOf(backoffice.getId()), metrica);
        }

        return metrica;
    }

    private static void resetLocalTimers() {
        // Re-crea el localRegistry i totes les mètriques locals per subsistema
        initializeMetrics();
    }
    public static void addSuccessOperation(BackofficeEntity backoffice, long duracio) {
        getMetrica(backoffice).addSuccess(duracio);
    }

    public static void addErrorOperation(BackofficeEntity backoffice) {
        getMetrica(backoffice).addError();
    }

    public static List<Metrics> getInfo() {
        resetLocalTimers();
        return new ArrayList<>(METRICS.values());
    }

    public static EstatSalutEnum calculateHealth(List<Metrics> backoffices) {
        return calculateGlobalHealth(
                backoffices.stream().map(Metrics::getSalutInfo)
                        .collect(Collectors.toList())
        );
    }
    private static EstatSalutEnum calculateGlobalHealth(List<IntegracioSalut> backoffices) {
        // Ordre de severitat: DOWN > ERROR > DEGRADED > WARN > UP > UNKNOWN
        boolean anyDown = false, anyError = false, anyDegraded = false, anyWarn = false, anyUp = false;
        for (var s : backoffices) {
            switch (s.getEstat()) {
                case UP:
                    anyUp = true;
                    break;
                case WARN:
                    anyWarn = true;
                    break;
                case DEGRADED:
                    anyWarn = true;
                    break;
                case ERROR:
                    anyWarn = true;
                    break;
                case DOWN:
                    anyWarn = true;
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
    public static class IntegracionsInfo {
        private final List<IntegracioSalut> integracionsSalut;
        private final EstatSalutEnum estatGlobal;
    }

}
