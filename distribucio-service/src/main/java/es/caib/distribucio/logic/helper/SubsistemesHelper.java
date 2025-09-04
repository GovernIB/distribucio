package es.caib.distribucio.logic.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.SubsistemaSalut;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

public class SubsistemesHelper {

    @Getter
    public enum SubsistemesEnum {
    	
    	AWS("Alta Registre WS"),
        BKC("Backoffice consulta"),
        BKE("Backoffice canvi estat"),
        BKL("Backoffice llistar"),
        RGB("Aplicar Regla tipus Backoffice"),
        GDO("Gestió documental FileSystem");

        private final String nom;
        SubsistemesEnum(String nom) {
            this.nom = nom;
        }
    }

    private static final Map<SubsistemesEnum, SubsistemaMetrica> metriquesMap = Arrays.stream(SubsistemesEnum.values())
            .collect(Collectors.toMap(
                    subsistema -> subsistema,
                    subsistema -> new SubsistemaMetrica(),
                    (existing, replacement) -> existing,
                    HashMap::new
            ));

    public static void addSuccessOperation(SubsistemesEnum subsistema, long duracio) {
        metriquesMap.get(subsistema).addOperacio(duracio, false);
    }
    public static void addErrorOperation(SubsistemesEnum subsistema, long duracio) {
        metriquesMap.get(subsistema).addOperacio(duracio, true);
    }

    public static void addOperation(SubsistemesEnum subsistema, long duracio, boolean error) {
        metriquesMap.get(subsistema).addOperacio(duracio, error);
    }

    public static SubsistemesInfo getSubsistemesInfo() {
        List<SubsistemaSalut> subsistemasSalut = getSubsistemesSalut();
        EstatSalutEnum estatGlobal = calculateGlobalHealth(subsistemasSalut);
        return SubsistemesInfo.builder()
                .subsistemesSalut(subsistemasSalut)
                .estatGlobal(estatGlobal)
                .build();
    }

    private static List<SubsistemaSalut> getSubsistemesSalut() {
        List<SubsistemaSalut> subsistemasSalut = new ArrayList<>();
        for (SubsistemesEnum subsistema : SubsistemesEnum.values()) {
            SubsistemaMetrica metrica = metriquesMap.get(subsistema);
            subsistemasSalut.add(SubsistemaSalut.builder()
                    .codi(subsistema.name())
                    .latencia((int) metrica.getTempsMigOperacio())
                    .estat(metrica.calculateEstatSalut())
                    .totalOk(metrica.getTotalOperacions())
                    .totalError(metrica.getOperacionsError())
                    .build());
            metrica.reset();
        }
        return subsistemasSalut;
    }

    private static EstatSalutEnum calculateGlobalHealth(List<SubsistemaSalut> subsistemes) {
        long downCount = subsistemes.stream()
                // No tenim en compte els callbacks per indicar si el sistema està caigut
                .filter(s -> !s.getCodi().startsWith("C") && s.getEstat() == EstatSalutEnum.DOWN)
                .count();
        long degradedCount = subsistemes.stream()
                // Si algun callback està caigut considerem que el sistema està degradat
                .filter(s -> s.getEstat() == EstatSalutEnum.DEGRADED
                        || (s.getCodi().startsWith("C") && s.getEstat() == EstatSalutEnum.DOWN))
                .count();
        long warnCount = subsistemes.stream()
                .filter(s -> s.getEstat() == EstatSalutEnum.WARN)
                .count();

        if (downCount > 0) return EstatSalutEnum.DOWN;
        if (degradedCount > 0) return EstatSalutEnum.DEGRADED;
        if (warnCount > 0) return EstatSalutEnum.WARN;
        return EstatSalutEnum.UP;
    }

    @Data
    private static class SubsistemaMetrica {
        private long totalOperacions;
        private long operacionsError;
        private double tempsMigOperacio;
        private double tempsTotalOperacions;
        private final List<Double> tempsOperacions = new ArrayList<>();

        public void addOperacio(long duracioMs, boolean error) {
            totalOperacions++;
            if (error) {
                operacionsError++;
            }
            tempsOperacions.add((double) duracioMs);
            tempsTotalOperacions += duracioMs;
            tempsMigOperacio = tempsTotalOperacions / totalOperacions;
        }

        public EstatSalutEnum calculateEstatSalut() {
            if (totalOperacions == 0) return EstatSalutEnum.UP;

            double errorRate = operacionsError * 100 / totalOperacions;
            if (errorRate > 30) {
                return EstatSalutEnum.DOWN;
            } else if (errorRate > 10) {
                return EstatSalutEnum.DEGRADED;
            } else if (errorRate < 5) {
                return EstatSalutEnum.UP;
            } else {
                return EstatSalutEnum.WARN; // estat "WARNING" per a 5-10%
            }
        }

        public void reset() {
            totalOperacions = 0;
            operacionsError = 0;
            tempsMigOperacio = 0;
            tempsTotalOperacions = 0;
            tempsOperacions.clear();
        }
    }

    @Getter
    @Builder
    public static class SubsistemesInfo {
        private final List<SubsistemaSalut> subsistemesSalut;
        private final EstatSalutEnum estatGlobal;
    }

}
