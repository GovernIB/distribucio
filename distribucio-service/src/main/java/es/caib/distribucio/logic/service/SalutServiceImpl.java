package es.caib.distribucio.logic.service;

import java.io.File;
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

import org.apache.commons.lang3.time.DateUtils;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import es.caib.comanda.ms.salut.model.AppInfo;
import es.caib.comanda.ms.salut.model.DetallSalut;
import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.IntegracioInfo;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.comanda.ms.salut.model.IntegracioSalut;
import es.caib.comanda.ms.salut.model.MissatgeSalut;
import es.caib.comanda.ms.salut.model.SalutInfo;
import es.caib.comanda.ms.salut.model.SubsistemaSalut;
import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.MonitorHelper;
import es.caib.distribucio.logic.helper.PluginHelper;
import es.caib.distribucio.logic.helper.SubsistemesHelper;
import es.caib.distribucio.logic.helper.plugin.AbstractPluginHelper;
import es.caib.distribucio.logic.intf.service.SalutService;
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
		
		return integracionsInfo;
	}

	@Override
	public List<AppInfo> getSubsistemes() {
		return List.of(
				AppInfo.builder().codi("AWS").nom("Alta Registre WS").build(),
				AppInfo.builder().codi("BKC").nom("Backoffice consulta").build(), 
				AppInfo.builder().codi("BKE").nom("Backoffice canvi estat").build(),
				AppInfo.builder().codi("BKL").nom("Backoffice llistar").build(),
				AppInfo.builder().codi("RGB").nom("Aplicar Regla tipus Backoffice").build(),
				AppInfo.builder().codi("GDO").nom("Gestió documental FileSystem").build()
		);
	}

	@Override
	public SalutInfo checkSalut(String versio, String performanceUrl) {
        var estatSalut = checkEstatSalut(performanceUrl);   // Estat
        var salutDatabase = checkDatabase();                // Base de dades
        var integracions = checkIntegracions();             // Integracions
        var altres = checkAltres();                         // Altres
        var missatges = checkMissatges();                   // Missatges

        SubsistemesHelper.SubsistemesInfo subsistemesInfo = SubsistemesHelper.getSubsistemesInfo();
        var subsistemes = subsistemesInfo.getSubsistemesSalut();  // Subsistemes
        var estatGlobalSubsistemes = subsistemesInfo.getEstatGlobal();
        if (EstatSalutEnum.UP.equals(estatSalut.getEstat()) && !EstatSalutEnum.UP.equals(estatGlobalSubsistemes)) {
            estatSalut = EstatSalut.builder()
                    .estat(estatGlobalSubsistemes)
                    .latencia(estatSalut.getLatencia())
                    .build();
        }
        
        return SalutInfo.builder()
                .codi("DIS")
                .versio(versio)
                .data(new Date())
                .estat(estatSalut)
                .bd(salutDatabase)
                .integracions(integracions)
                .subsistemes(subsistemes)
                .altres(altres)
                .missatges(missatges)
                .build();
	}
	
    private EstatSalut checkEstatSalut(String performanceUrl) {

        Instant start = Instant.now();
        EstatSalutEnum estat = EstatSalutEnum.UP;
        try {
            executePerformanceTest();
        } catch (Exception e) {}
        for (int i = 1; i <= MAX_CONNECTION_RETRY; i++) {
            try {
                restTemplate.getForObject(performanceUrl, String.class);
                break;
            } catch (Exception e) {
                if (i == MAX_CONNECTION_RETRY) {
                    estat = EstatSalutEnum.UNKNOWN; // After 3 connection failed attempts
                }
            }
        }
        Instant end = Instant.now();
        Integer latency = (int) Duration.between(start, end).toMillis();

        return EstatSalut.builder()
                .estat(estat)
                .latencia(latency)
                .build();
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

            return EstatSalut.builder()
                    .estat(EstatSalutEnum.UP)
                    .latencia((int) Math.round(stats.getAverage()))
                    .build();
        } catch (RunnerException e) {
            throw new RuntimeException(e);
        }

    }

    private EstatSalut checkDatabase() {

        try {
            Instant start = Instant.now();
            jdbcTemplate.execute("SELECT ID FROM DIS_ENTITAT WHERE ID = 1");
            Instant end = Instant.now();

            return EstatSalut.builder()
                    .estat(EstatSalutEnum.UP)
                    .latencia((int) Duration.between(start, end).toMillis())
                    .build();
        } catch (Exception e) {
            return EstatSalut.builder().estat(EstatSalutEnum.DOWN).build();
        }
    }

    private List<IntegracioSalut> checkIntegracions() {

        List<IntegracioSalut> integracionsSalut = new ArrayList<>();
        try {
            List<AbstractPluginHelper<?>> helpers = pluginHelper.getPluginHelpers();
            for (AbstractPluginHelper<?> helper : helpers) {
                integracionsSalut.addAll(helper.getIntegracionsSalut());
            }
        } catch (Exception e) {
        	log.error("Error checkIntegracions", e);
            return Collections.emptyList();
        }
        
        // Hi ha diferents pluginHelper que criden al mateix sistma extern
        Map<String, IntegracioSalut> integracionsSalutUnificades = new HashMap<>();
        for (IntegracioSalut integracio : integracionsSalut) {
        	integracionsSalutUnificades.merge(
                integracio.getCodi(),
                integracio,
                (a, b) -> mergeIntegracions(a, b)
            );
        }

        return new ArrayList<>(integracionsSalutUnificades.values());
        
//        return integracionsSalut;
    }

    public List<SubsistemaSalut> checkSubsistemes() {
        try {

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public List<DetallSalut> checkAltres() {
    	String totalSpace = null, freeSpace = null;
        // Nombre de cores (CPU)
        var os = MonitorHelper.getName() + " " + MonitorHelper.getVersion() + " (" + MonitorHelper.getArch() + ")";

        try {
            for (File root : File.listRoots()) {
    			totalSpace = MonitorHelper.humanReadableByteCount(root.getTotalSpace());
    			freeSpace = MonitorHelper.humanReadableByteCount(root.getFreeSpace());
    		}
            
            return List.of(
                    DetallSalut.builder().codi("PRC").nom("Processadors").valor(String.valueOf(Runtime.getRuntime().availableProcessors())).build(),
                    DetallSalut.builder().codi("SCPU").nom("Càrrega del sistema").valor(MonitorHelper.getSystemCpuLoad()).build(),
                    DetallSalut.builder().codi("PCPU").nom("Càrrega del procés").valor(MonitorHelper.getProcessCPULoad()).build(),
                    DetallSalut.builder().codi("MED").nom("Memòria disponible").valor(MonitorHelper.humanReadableByteCount(Runtime.getRuntime().freeMemory())).build(),
                    DetallSalut.builder().codi("MET").nom("Memòria total").valor(MonitorHelper.humanReadableByteCount(Runtime.getRuntime().totalMemory())).build(),
                    DetallSalut.builder().codi("EDT").nom("Espai de disc total").valor(totalSpace).build(),
                    DetallSalut.builder().codi("EDL").nom("Espai de disc lliure").valor(freeSpace).build(),
                    DetallSalut.builder().codi("SO").nom("Sistema operatiu").valor(os).build());

        } catch (Exception e) {
        	log.error("Salut: No s'ha pogut obtenir informació del sistema amb la implementació de Sun", e);
            return null;
        }
    }

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
    
    private IntegracioSalut mergeIntegracions(IntegracioSalut a, IntegracioSalut b) {
        return IntegracioSalut.builder()
                .codi(a.getCodi())
                .estat(recuperarEstat(a.getEstat(), b.getEstat()))
                .latencia(mergeLatencia(a.getLatencia(), b.getLatencia()))
                .peticions(mergePeticions(a.getPeticions(), b.getPeticions()))
                .build();
    }

    private EstatSalutEnum recuperarEstat(EstatSalutEnum e1, EstatSalutEnum e2) {
        if (e1 == EstatSalutEnum.DOWN || e2 == EstatSalutEnum.DOWN) return EstatSalutEnum.DOWN;
        if (e1 == EstatSalutEnum.UP   || e2 == EstatSalutEnum.UP)   return EstatSalutEnum.UP;
        return EstatSalutEnum.UNKNOWN;
    }

    private Integer mergeLatencia(Integer l1, Integer l2) {
        if (l1 == null) return l2;
        if (l2 == null) return l1;
        return Math.max(l1, l2);
    }

    private IntegracioPeticions mergePeticions(IntegracioPeticions p1, IntegracioPeticions p2) {
        if (p1 == null) return p2;
        if (p2 == null) return p1;

        p1.setTotalOk(p1.getTotalOk() + p2.getTotalOk());
        p1.setTotalError(p1.getTotalError() + p2.getTotalError());

        return p1;
    }
    
}
