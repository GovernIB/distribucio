package es.caib.distribucio.logic.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import es.caib.comanda.model.server.monitoring.*;
import es.caib.comanda.ms.salut.helper.MonitorHelper;
import es.caib.distribucio.logic.helper.BackofficeSalutHelper;
import es.caib.distribucio.logic.helper.BackofficeSalutHelper.Metrics;
import es.caib.distribucio.persist.entity.BackofficeEntity;
import es.caib.distribucio.persist.repository.BackofficeRepository;
import org.apache.commons.lang3.time.DateUtils;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.PluginHelper;
import es.caib.distribucio.logic.helper.SubsistemesHelper;
import es.caib.distribucio.logic.helper.plugin.AbstractPluginHelper;
import es.caib.distribucio.logic.intf.service.SalutService;
import es.caib.distribucio.logic.intf.util.DatesUtils;
import es.caib.distribucio.logic.utils.DistribucioBenchmark;
import es.caib.distribucio.persist.repository.AvisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalutServiceImpl implements SalutService {

	private static final int MAX_CONNECTION_RETRY = 3;
	
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;

    @Autowired
    private BackofficeRepository backofficeRepository;
	
	private final JdbcTemplate jdbcTemplate;
    private final RestTemplate restTemplate;
    private final PluginHelper pluginHelper;
    private final AvisRepository avisRepository;

    @Override
	public List<IntegracioInfo> getIntegracions() {
		List<IntegracioInfo> integracionsInfo = pluginHelper.getPluginHelpers().stream()
	            .flatMap(helper -> helper.getIntegracionsInfo().stream())
	            .collect(Collectors.collectingAndThen(
	                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(IntegracioInfo::getCodi))),
	                    ArrayList::new));

        List<BackofficeEntity> backofficeList = backofficeRepository.findAll();
        for (BackofficeEntity backoffice: backofficeList) {
            integracionsInfo.add(new IntegracioInfo().codi(backoffice.getCodi()).nom(backoffice.getNom()));
        }
		return integracionsInfo;
	}

	@Override
	public List<SubsistemaInfo> getSubsistemes() {
        List<SubsistemaInfo> subsistemes = new ArrayList<>();
        for(SubsistemesHelper.SubsistemesEnum subsistema: SubsistemesHelper.SubsistemesEnum.values()) {
            subsistemes.add(new SubsistemaInfo().codi(subsistema.name()).nom(subsistema.getNom()));
        }
        return subsistemes;
	}

	@Override
	public SalutInfo checkSalut(String versio, String performanceUrl) {
        var estatSalut = checkEstatSalut(performanceUrl);   // Estat
        var salutDatabase = checkDatabase();                // Base de dades
        var integracions = checkIntegracions();             // Integracions
//        var altres = checkAltres();                         // Altres
        var missatges = checkMissatges();                   // Missatges

        SubsistemesHelper.SubsistemesInfo subsistemesInfo = SubsistemesHelper.getSubsistemesInfo();
        List<SubsistemaSalut> subsistemes = subsistemesInfo.getSubsistemesSalut();  // Subsistemes
        var estatGlobalSubsistemes = subsistemesInfo.getEstatGlobal();
        
        if (EstatSalutEnum.UP.equals(estatSalut.getEstat()) && !EstatSalutEnum.UP.equals(estatGlobalSubsistemes) && !EstatSalutEnum.UNKNOWN.equals(estatGlobalSubsistemes)) {
            estatSalut = new EstatSalut()
                    .estat(estatGlobalSubsistemes)
                    .latencia(estatSalut.getLatencia());
        }

        InformacioSistema systemInfo = MonitorHelper.getInfoSistema();
        return new SalutInfo()
                .codi("DIS")
                .versio(versio)
                .data(DatesUtils.toOffsetDateTime(new Date()))
                .estatGlobal(estatSalut)
                .estatBaseDeDades(salutDatabase)
                .integracions(integracions)
                .subsistemes(subsistemes)
//                .altres(altres)
                .missatges(missatges)
                .informacioSistema(systemInfo);
	}
	
    @Override
    public List<ContextInfo> getContexts(String baseUrl) {
        return List.of(
                new ContextInfo()
                        .codi("BACK")
                        .nom("Backoffice")
                        .path(baseUrl + "/distribucioback")
                        .manuals(List.of(
                                new Manual().nom("Manual d'usuari").path("https://github.com/GovernIB/distribucio/blob/dis-1.0/doc/pdf/02_Distribucio_Manual_Usuari.pdf"),
                                new Manual().nom("Manual d'administració").path("https://github.com/GovernIB/distribucio/blob/dis-1.0/doc/pdf/02_Distribucio_Manual_Administrador.pdf")))
                        ,
                new ContextInfo()
                        .codi("INT")
                        .nom("API interna")
                        .path(baseUrl + "/distribucioapi/interna")
                        .manuals(List.of(new Manual().nom("Manual d'integració").path("https://github.com/GovernIB/distribucio/blob/dis-1.0/doc/pdf/03_Distribucio_Manual_Integració.pdf")))
                        .api(baseUrl + "/distribucioapi/interna")
                        ,
                new ContextInfo()
                        .codi("EXT")
                        .nom("API externa")
                        .path(baseUrl + "/distribucioapi/externa")
                        .api(baseUrl + "/distribucioapi/externa")

        );
    }
	
    private EstatSalut checkEstatSalut(String performanceUrl) {

        Instant start = Instant.now();
        EstatSalutEnum estat = EstatSalutEnum.UP;
        
        for (int i = 1; i <= MAX_CONNECTION_RETRY; i++) {
            try {
                restTemplate.getForObject(performanceUrl, String.class);
                break;
            } catch (Exception e) {
//                if (i == MAX_CONNECTION_RETRY) {
//                    estat = EstatSalutEnum.DOWN; // After 3 connection failed attempts
//                }
            }
        }
        Instant end = Instant.now();
        Integer latency = (int) Duration.between(start, end).toMillis();

        return new EstatSalut()
                .estat(estat)
                .latencia(latency);
    }
    
    private EstatSalut executePerformanceTest() {

        Options opt = new OptionsBuilder()
                .include(DistribucioBenchmark.class.getSimpleName())
                .timeUnit(TimeUnit.MILLISECONDS)
                .forks(1)
                .build();

        try {
            var runResults = new Runner(opt).run();

            // Processar els resultats
            DoubleSummaryStatistics stats = runResults.stream()
                    .mapToDouble(result -> result.getPrimaryResult().getScore())
                    .summaryStatistics();

            return new EstatSalut()
                    .estat(EstatSalutEnum.UP)
                    .latencia((int) Math.round(stats.getAverage()));
        } catch (RunnerException e) {
            throw new RuntimeException(e);
        }

    }

    private EstatSalut checkDatabase() {

        try {
            Instant start = Instant.now();
            jdbcTemplate.execute("SELECT ID FROM DIS_ENTITAT WHERE ID = 1");
            Instant end = Instant.now();

            return new EstatSalut()
                    .estat(EstatSalutEnum.UP)
                    .latencia((int) Duration.between(start, end).toMillis());
        } catch (Exception e) {
            return new EstatSalut().estat(EstatSalutEnum.DOWN);
        }
    }
    
    private List<IntegracioSalut> checkIntegracions() {

        List<IntegracioSalut> integracionsSalut = new ArrayList<>();
        try {
            List<AbstractPluginHelper<?>> helpers = pluginHelper.getPluginHelpers();
            for (AbstractPluginHelper<?> helper : helpers) {
                integracionsSalut.add(helper.getIntegracionsSalut());
            }
        } catch (Exception e) {
            log.error("Error checkIntegracions", e);
            return Collections.emptyList();
        }

        // Agrupar per codiapp (sistema extern)
        Map<String, List<IntegracioSalut>> agrupades = integracionsSalut.stream()
                .collect(Collectors.groupingBy(IntegracioSalut::getCodi));

        List<IntegracioSalut> result = new ArrayList<>();

        for (Map.Entry<String, List<IntegracioSalut>> entry : agrupades.entrySet()) {
            String codiApp = entry.getKey();
            List<IntegracioSalut> llista = entry.getValue();

            if (llista.size() == 1) {
                // Si només hi ha un plugin del sistema extern: retornar tal qual
                result.add(llista.get(0));
            } else {
                // Si hi ha múltiples plugins (serveis/procediments): combinar
                var totalOk = 0L;
                var totalError = 0L;
                var peticionsOkUltimPeriode = 0L;
                var peticionsErrorUltimPeriode = 0L;
                var totalTempsMig = 0;
                var tempsMigUltimPeriode = 0;
                EstatSalutEnum estat = null;
                var maxLatencia = 0;

                Map<String, IntegracioPeticions> peticionsPerEntorn = new HashMap<>();

                for (IntegracioSalut pluginIntegracio : llista) {
                    IntegracioPeticions p = pluginIntegracio.getPeticions();

                    totalOk += p.getTotalOk();
                    totalError += p.getTotalError();
                    peticionsOkUltimPeriode += p.getPeticionsOkUltimPeriode();
                    peticionsErrorUltimPeriode += p.getPeticionsErrorUltimPeriode();

                    if (p.getTempsMigUltimPeriode() != null)
                    	tempsMigUltimPeriode = Math.max(tempsMigUltimPeriode, p.getTempsMigUltimPeriode());
                    
                    if (p.getTotalTempsMig() != null)
                    	totalTempsMig = Math.max(totalTempsMig, p.getTotalTempsMig());

                    if (pluginIntegracio.getLatencia() != null)
                    	maxLatencia = Math.max(maxLatencia, pluginIntegracio.getLatencia());

                    if (pluginIntegracio.getEstat() != null) {
	                    //Clau por endpoint: Serveis o Procediments
	                    String key = getKeyFromEndpoint(p.getEndpoint());
	                    peticionsPerEntorn.put(key, p);
                    }
                }
                
                // Combinar estats: si hi ha un UP retornar UP, si hi ha un DOWN retornar DOWN
                if (!peticionsPerEntorn.isEmpty()) {
                    estat = llista.stream()
                                  .map(IntegracioSalut::getEstat)
                                  .filter(e -> e != null && e != EstatSalutEnum.UNKNOWN)
                                  .reduce(this::recuperarEstat)
                                  .orElse(null);
                }
                
                IntegracioPeticions combinada = new IntegracioPeticions()
                        .totalOk(totalOk)
                        .totalError(totalError)
                        .totalTempsMig(totalTempsMig)
                        .peticionsOkUltimPeriode(peticionsOkUltimPeriode)
                        .peticionsErrorUltimPeriode(peticionsErrorUltimPeriode)
                        .tempsMigUltimPeriode(tempsMigUltimPeriode)
                        .peticionsPerEntorn(peticionsPerEntorn);

                IntegracioSalut combinadaSalut = new IntegracioSalut()
                        .codi(codiApp)
                        .estat(estat)
                        .latencia(maxLatencia)
                        .peticions(combinada);

                result.add(combinadaSalut);
            }
        }

        List<Metrics> integracionsInfo = BackofficeSalutHelper.getInfo();
        Map<String, List<Metrics>> map = integracionsInfo.stream()
                .collect(Collectors.groupingBy(Metrics::getCodi));

        for (Map.Entry<String, List<Metrics>> entry : map.entrySet()) {
            String codiApp = entry.getKey();
            List<Metrics> llista = entry.getValue();

            if (llista.size() == 1) {
                // Si només hi ha un plugin del sistema extern: retornar tal qual
                Metrics backoffice = llista.get(0);
                IntegracioSalut backofficeSalut = backoffice.getSalutInfo();
                result.add(backofficeSalut);
            } else {
                // Si hi ha múltiples plugins (serveis/procediments): combinar
                IntegracioPeticions integracioPeticions = new IntegracioPeticions()
                        .totalOk(0L)
                        .totalError(0L)
                        .totalTempsMig(0)
                        .peticionsOkUltimPeriode(0L)
                        .peticionsErrorUltimPeriode(0L)
                        .tempsMigUltimPeriode(0);

                for (Metrics m : llista) {
                    IntegracioPeticions peticio = m.getSalutInfo().getPeticions();
                    integracioPeticions.setTotalOk(integracioPeticions.getTotalOk() + peticio.getTotalOk());
                    integracioPeticions.setTotalError(integracioPeticions.getTotalError() + peticio.getTotalError());
                    integracioPeticions.setPeticionsOkUltimPeriode(integracioPeticions.getPeticionsOkUltimPeriode() + peticio.getPeticionsOkUltimPeriode());
                    integracioPeticions.setPeticionsErrorUltimPeriode(integracioPeticions.getPeticionsErrorUltimPeriode() + peticio.getPeticionsErrorUltimPeriode());

                    if (peticio.getTotalTempsMig() > integracioPeticions.getTotalTempsMig()) {
                        integracioPeticions.setTotalTempsMig(peticio.getTotalTempsMig());
                    }
                    if (peticio.getTempsMigUltimPeriode() > integracioPeticions.getTempsMigUltimPeriode()) {
                        integracioPeticions.setTempsMigUltimPeriode(peticio.getTempsMigUltimPeriode());
                    }

                    integracioPeticions.getPeticionsPerEntorn()
                            .put(m.getEntitat(), peticio);
                }

                IntegracioSalut integracioBackoffice = new IntegracioSalut()
                        .codi(codiApp)
                        .peticions(integracioPeticions)
                        .estat(BackofficeSalutHelper.calculateHealth(llista));
                result.add(integracioBackoffice);
            }
        }

        return result;
    }

    // Helper para identificar la key de cada plugin por endpoint
    private String getKeyFromEndpoint(String endpoint) {
        if (endpoint == null) return "UNKNOWN";
        if (endpoint.contains("/servicios")) return "Serveis";
        if (endpoint.contains("/procedimientos")) return "Procediments";
        if (endpoint.contains("Catalogos")) return "Dades externes";
        if (endpoint.contains("/unidades")) return "Unitats";
        if (endpoint.contains("/evidenciesibapi/externa")) return "Validació firma àgil";
        if (endpoint.contains("/DSSAfirmaVerify")) return "Validació firma @firma";
        if (endpoint.contains("/portafibapi/interna")) return "Validació firma Portafib";
        return "UNKNOWN";
    }


    public List<SubsistemaSalut> checkSubsistemes() {
        try {

            return null;
        } catch (Exception e) {
            return null;
        }
    }

//    public List<DetallSalut> checkAltres() {
//    	String totalSpace = null, freeSpace = null;
//        // Nombre de cores (CPU)
//        var os = MonitorHelper.getName() + " " + MonitorHelper.getVersion() + " (" + MonitorHelper.getArch() + ")";
//
//        try {
//            for (File root : File.listRoots()) {
//    			totalSpace = MonitorHelper.humanReadableByteCount(root.getTotalSpace());
//    			freeSpace = MonitorHelper.humanReadableByteCount(root.getFreeSpace());
//    		}
//
//            return List.of(
//                    DetallSalut.builder().codi("PRC").nom("Processadors").valor(String.valueOf(Runtime.getRuntime().availableProcessors())).build(),
//                    DetallSalut.builder().codi("SCPU").nom("Càrrega del sistema").valor(MonitorHelper.getSystemCpuLoad()).build(),
//                    DetallSalut.builder().codi("PCPU").nom("Càrrega del procés").valor(MonitorHelper.getProcessCPULoad()).build(),
//                    DetallSalut.builder().codi("MED").nom("Memòria disponible").valor(MonitorHelper.humanReadableByteCount(Runtime.getRuntime().freeMemory())).build(),
//                    DetallSalut.builder().codi("MET").nom("Memòria total").valor(MonitorHelper.humanReadableByteCount(Runtime.getRuntime().totalMemory())).build(),
//                    DetallSalut.builder().codi("EDT").nom("Espai de disc total").valor(totalSpace).build(),
//                    DetallSalut.builder().codi("EDL").nom("Espai de disc lliure").valor(freeSpace).build(),
//                    DetallSalut.builder().codi("SO").nom("Sistema operatiu").valor(os).build());
//
//        } catch (Exception e) {
//        	log.error("Salut: No s'ha pogut obtenir informació del sistema amb la implementació de Sun", e);
//            return null;
//        }
//    }

    public List<MissatgeSalut> checkMissatges() {
        List<MissatgeSalut> missatges = new ArrayList<>();
        try {
            var avisos = avisRepository.findActive(DateUtils.truncate(new Date(), Calendar.DATE));
            if (avisos != null && !avisos.isEmpty()) {
            	missatges = conversioTipusHelper.convertirList(avisos, MissatgeSalut.class);
            }

            return missatges;
        } catch (Exception e) {
            return null;
        }
    }

    private EstatSalutEnum recuperarEstat(EstatSalutEnum e1, EstatSalutEnum e2) {
        if (e1 == EstatSalutEnum.UP   || e2 == EstatSalutEnum.UP)   return EstatSalutEnum.UP;
        if (e1 == EstatSalutEnum.DOWN || e2 == EstatSalutEnum.DOWN) return EstatSalutEnum.DOWN;
        return EstatSalutEnum.UNKNOWN;
    }

}
