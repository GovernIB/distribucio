package es.caib.distribucio.logic.helper.plugin;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Strings;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.comanda.ms.salut.model.IntegracioInfo;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.comanda.ms.salut.model.IntegracioSalut;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.helper.IntegracioHelper;
import es.caib.distribucio.logic.helper.LoadedPropertiesHelper;
import es.caib.distribucio.logic.intf.dto.IntegracioDiagnostic;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.plugin.SalutPlugin;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
public abstract class AbstractPluginHelper<T extends SalutPlugin> {

	protected final static String GLOBAL = "GLOBAL";
	
	protected final IntegracioHelper integracioHelper;
	protected final ConfigHelper configHelper;
	protected final EntitatRepository entitatRepository;
	protected final MeterRegistry meterRegistry;
	
	@Autowired
	protected LoadedPropertiesHelper loadedPropertiesHelper;
	
	protected Map<String, T> pluginMap = new HashMap<>();
	
	protected String getCodiEntitatActual() {

		var codiEntitat = ConfigHelper.getEntitatActualCodi();
		if (Strings.isNullOrEmpty(codiEntitat)) {
			throw new RuntimeException("El codi de l'entitat no pot ser null");
		}
		return codiEntitat;
	}

	protected synchronized void loadPluginProperties(String codeProperties) {
		Map<String, Boolean> loadedProperties = loadedPropertiesHelper.getLoadedProperties();
		if (!loadedProperties.containsKey(codeProperties) || !loadedProperties.get(codeProperties)) {
			loadedProperties.put(codeProperties, true);
			Map<String, String> pluginProps = configHelper.getGroupProperties(codeProperties);
			for (Map.Entry<String, String> entry : pluginProps.entrySet()) {
				if (entry.getValue() != null) {
					System.setProperty(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	public void resetPlugin() {
		pluginMap = new HashMap<>();
	}
	
	public IntegracioSalut getIntegracionsSalut() {

		Map<String, IntegracioSalut> integracionsMap = createIntegracionsFromPlugins();
        if (integracionsMap.size() == 1) {
            var entry = integracionsMap.entrySet().iterator().next();
            return entry.getValue();
        }

        var keys = integracionsMap.keySet();
        Map<String, IntegracioPeticions> peticionsMap = new HashMap<>();
        var totalOk = 0L;
        var totalError = 0L;
        var peticionsOkUltimPeriode = 0L;
        var peticionsErrorUltimPeriode = 0L;
        var totalTempsMig = 0;
        var tempsMigUltimPeriode = 0;
        for (var entitatCodi : keys) {
            var integracionsEntitat = integracionsMap.get(entitatCodi);
            var peticionsEntitat = integracionsEntitat.getPeticions();
            peticionsMap.put(entitatCodi, peticionsEntitat);
            totalOk += peticionsEntitat.getTotalOk();
            totalError += peticionsEntitat.getTotalError();
            peticionsOkUltimPeriode += peticionsEntitat.getPeticionsOkUltimPeriode();
            peticionsErrorUltimPeriode += peticionsEntitat.getPeticionsErrorUltimPeriode();
            totalTempsMig += peticionsEntitat.getTotalTempsMig();
            tempsMigUltimPeriode += peticionsEntitat.getTempsMigUltimPeriode();
        }
        var peticions = IntegracioPeticions.builder()
                .totalOk(totalOk)
                .totalError(totalError)
                .peticionsOkUltimPeriode(peticionsOkUltimPeriode)
                .totalTempsMig(totalTempsMig)
                .peticionsErrorUltimPeriode(peticionsErrorUltimPeriode)
                .peticionsPerEntorn(peticionsMap)
                .tempsMigUltimPeriode(tempsMigUltimPeriode)
                .build();

        var codi = getCodiApp().name();
        return IntegracioSalut.builder().codi(codi).peticions(peticions).build();
	}
	
	private Map<String, IntegracioSalut> createIntegracionsFromPlugins() {

		Map<String, IntegracioSalut> integracioResult = new HashMap<>();
		String codiIntegracio = getCodiApp().name();

		pluginMap.forEach((codiEntitat, plugin) -> {
			EstatSalut estatSalut = plugin.getEstatPlugin();

			if (plugin.teConfiguracioEspecifica()) {
				integracioResult.put(codiEntitat, createIntegracioForPlugin(codiIntegracio, codiEntitat, estatSalut, plugin));
			} else {
				mergeGlobalIntegracio(integracioResult, plugin, estatSalut, codiIntegracio);
			}
		});

		return integracioResult;
	}

	private static <T extends SalutPlugin> void mergeGlobalIntegracio(Map<String, IntegracioSalut> integracioResult, T plugin, EstatSalut estatSalut, String codiIntegracio) {
		integracioResult.merge(GLOBAL,
				IntegracioSalut.builder()
						.codi(codiIntegracio)
						.estat(estatSalut.getEstat())
						.latencia(estatSalut.getLatencia())
						.peticions(plugin.getPeticionsPlugin())
						.build(),
				(existing, nou) -> {
					existing.getPeticions().setTotalOk(existing.getPeticions().getTotalOk() + nou.getPeticions().getTotalOk());
					existing.getPeticions().setTotalError(existing.getPeticions().getTotalError() + nou.getPeticions().getTotalError());
					return existing;
				});
	}

	private IntegracioSalut createIntegracioForPlugin(String codiIntegracio, String codiEntitat, EstatSalut estatSalut, T plugin) {
		return IntegracioSalut.builder()
				.codi(setFormatIntegracio(codiIntegracio, codiEntitat, 16))
				.estat(estatSalut.getEstat())
				.latencia(estatSalut.getLatencia())
				.peticions(plugin.getPeticionsPlugin())
				.build();
	}

	public List<IntegracioInfo> getIntegracionsInfo() {
		List<CodiBool> entitatsFiltrades = getEntitatsFiltrades();
		return entitatsFiltrades.stream()
				.map(entitat -> obtenirIntegracioInfo(entitat))
				.collect(Collectors.toList());
	}

	private IntegracioInfo obtenirIntegracioInfo(CodiBool entitat) {
		boolean showInfoEspecifica = entitat.isConfiguracioEspecifica();
		String codiIntegracio = getCodiApp().name();
		String nomIntegracio = getCodiApp().getNom();

		return IntegracioInfo.builder()
				.codi(showInfoEspecifica ? setFormatIntegracio(codiIntegracio, entitat.getCodi(), 16) : codiIntegracio)
				.nom(showInfoEspecifica ? setFormatIntegracio(nomIntegracio, entitat.getCodi(), 255) : nomIntegracio)
				.build();
	}


	private List<CodiBool> getEntitatsFiltrades() {
		AtomicBoolean foundFirstWithConfig = new AtomicBoolean(false);
		AtomicBoolean hasConfiguracioEspecifica = new AtomicBoolean(false);

		List<CodiBool> codisFiltrats = entitatRepository.findCodiActives().stream()
				.filter(codiEntitat -> shouldInclude(codiEntitat, foundFirstWithConfig, hasConfiguracioEspecifica))
				.map(codiEntitat -> CodiBool.builder().codi(codiEntitat).configuracioEspecifica(hasConfiguracioEspecifica.get()).build())
				.collect(Collectors.toList());
		return codisFiltrats;
	}

	private boolean shouldInclude(String codiEntitat, AtomicBoolean foundFirstWithConfig, AtomicBoolean hasConfiguracioEspecifica) {
		boolean hasConfigEspecifica = pluginMap.get(codiEntitat) != null ?
				pluginMap.get(codiEntitat).teConfiguracioEspecifica() :
				configHelper.hasEntityGroupPropertiesModified(codiEntitat, getConfigGrup());
		hasConfiguracioEspecifica.set(hasConfigEspecifica);

		if (!hasConfigEspecifica && !foundFirstWithConfig.get()) {
			foundFirstWithConfig.set(true);
			return true; // Inclou el primer que compleix !teConfiguracioEspecifica
		}

		return hasConfigEspecifica; // Inclou només els elements que no compleixen teConfiguracioEspecifica o el primer que sí
	}

	private String setFormatIntegracio(String text, String codiEntitat, int maxLength) {
		String codi = GLOBAL.equals(codiEntitat) ? text : text + "-" + codiEntitat;
		return codi.length() > maxLength ? codi.substring(0, maxLength) : codi;
	}

	abstract protected T getPlugin();
	abstract protected String getPluginClassProperty();
	abstract protected IntegracioApp getCodiApp();
	abstract protected String getConfigGrup();

	public abstract boolean diagnosticar(Map<String, IntegracioDiagnostic> diagnostics) throws Exception;


	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter @Setter
	public static class CodiBool implements Serializable {
		private static final long serialVersionUID = 4560706730350110246L;
		private String codi;
		private boolean configuracioEspecifica;
	}
	
}
