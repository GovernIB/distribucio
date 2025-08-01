package es.caib.distribucio.logic.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.google.common.base.Strings;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.comanda.ms.salut.model.IntegracioInfo;
import es.caib.comanda.ms.salut.model.IntegracioSalut;
import es.caib.distribucio.logic.intf.dto.IntegracioDiagnostic;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.plugin.SalutPlugin;
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
	
	protected Map<String, T> pluginMap = new HashMap<>();
	
	protected String getCodiEntitatActual() {

		var codiEntitat = ConfigHelper.getEntitatActualCodi();
		if (Strings.isNullOrEmpty(codiEntitat)) {
			throw new RuntimeException("El codi de l'entitat no pot ser null");
		}
		return codiEntitat;
	}

	public void resetPlugin() {
		pluginMap = new HashMap<>();
	}
	
	public List<IntegracioSalut> getIntegracionsSalut() {
		Map<String, IntegracioSalut> integracionsMap = createIntegracionsFromPlugins();
		addFilteredEntitiesToIntegracions(integracionsMap);
		return new ArrayList<>(integracionsMap.values());
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

	private void addFilteredEntitiesToIntegracions(Map<String, IntegracioSalut> integracionsMap) {
		String codiIntegracio = getCodiApp().name();

		getEntitatsFiltrades().stream()
				.filter(entitat -> (entitat.isConfiguracioEspecifica() && !integracionsMap.containsKey(entitat.getCodi())) || (!entitat.isConfiguracioEspecifica() && !integracionsMap.containsKey(GLOBAL)))
				.forEach(entitat -> integracionsMap.put(entitat.getCodi(),
						IntegracioSalut.builder()
								.codi(entitat.isConfiguracioEspecifica() ? setFormatIntegracio(codiIntegracio, entitat.getCodi(), 16) : codiIntegracio)
								.estat(EstatSalutEnum.UNKNOWN)
								.build()));
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
		private String codi;
		private boolean configuracioEspecifica;
	}
	
}
