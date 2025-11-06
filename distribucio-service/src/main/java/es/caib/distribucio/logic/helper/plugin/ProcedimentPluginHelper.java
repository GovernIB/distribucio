package es.caib.distribucio.logic.helper.plugin;

import static es.caib.distribucio.logic.intf.dto.IntegracioCodi.PROCEDIMENT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.helper.IntegracioHelper;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.IntegracioDiagnostic;
import es.caib.distribucio.logic.intf.dto.ProcedimentDto;
import es.caib.distribucio.logic.intf.exception.SistemaExternException;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.plugin.procediment.Procediment;
import es.caib.distribucio.plugin.procediment.ProcedimentPlugin;
import es.caib.distribucio.plugin.procediment.UnitatAdministrativa;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class ProcedimentPluginHelper extends AbstractPluginHelper<ProcedimentPlugin> {

	public static final String GRUP = "PROCEDIMENTS";
	
	public ProcedimentPluginHelper(
			IntegracioHelper integracioHelper, 
			ConfigHelper configHelper,
			EntitatRepository entitatRepository,
			MeterRegistry meterRegistry) {
		super(integracioHelper, configHelper, entitatRepository, meterRegistry);
	}

	@Override
	public boolean diagnosticar(Map<String, IntegracioDiagnostic> diagnostics) throws Exception {
		var entitats = entitatRepository.findAll();
		IntegracioDiagnostic diagnostic;
		var diagnosticOk = true;
		String codi;
		for (var entitat : entitats) {
			codi = entitat.getCodi();
			try {
				var plugin = pluginMap.get(codi);
				if (plugin == null)  {
					continue;
				}
				var procediments = plugin.findAmbCodiDir3(codi);
				diagnostic = new IntegracioDiagnostic();
				diagnostic.setCorrecte(procediments != null && !procediments.isEmpty());
				diagnostics.put(codi, diagnostic);
			} catch(Exception ex) {
				diagnostic = new IntegracioDiagnostic();
				diagnostic.setErrMsg(ex.getMessage());
				diagnostics.put(codi, diagnostic);
				diagnosticOk = false;
			}
		}
		if (diagnostics.isEmpty() && !entitats.isEmpty()) {
			var entitat = entitatRepository.findByCodi(getCodiEntitatActual());
			var procediments = procedimentFindByCodiDir3(entitat.getCodiDir3());
			diagnostic = new IntegracioDiagnostic();
			diagnostic.setCorrecte(procediments != null && !procediments.isEmpty());
			diagnostics.put(entitat.getCodi(), diagnostic);
		}
		return diagnosticOk;
	}

	public List<Procediment> procedimentFindByCodiDir3(
			String codiDir3) {
		String accioDescripcio = "Consulta dels procediments pel codi DIR3 " + codiDir3;

		ProcedimentPlugin procedimentPlugin = this.getPlugin();
		String usuariIntegracio = procedimentPlugin.getUsuariIntegracio();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codiDir3", codiDir3);
		long t0 = System.currentTimeMillis();
		try {
			//codiDir3 = "A04003003";
			List<Procediment> procediments = getPlugin().findAmbCodiDir3(codiDir3);
			// RegistreNumero no cal!!!
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_PROCEDIMENT,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return procediments;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de procediments: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PROCEDIMENT,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_PROCEDIMENT,
					errorDescripcio,
					ex);
		}
	}
	
	public UnitatAdministrativa procedimentGetUnitatAdministrativa(String codi) {
		String accioDescripcio = "Consulta de la unitat organitzativa per codi " + codi;
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi", codi);
		
		long t0 = System.currentTimeMillis();
		UnitatAdministrativa unitatAdministrativa = null;
		try {
			unitatAdministrativa = getPlugin().findUnitatAdministrativaAmbCodi(codi);

			accioParams.put("resultat", unitatAdministrativa != null ? 
					unitatAdministrativa.getCodiDir3() + " " + unitatAdministrativa.getNom() 
					: "(no trobada)");
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_PROCEDIMENT,
					accioDescripcio,
					"USUARI_INTEGRACIO",
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);							
		} catch (Exception ex) {
			String errorDescripcio = "Error consultant la unitat organitzativa amb codi " + codi+ ": " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PROCEDIMENT,
					"NUMERO_REGISTRE",
					accioDescripcio,
					"USUARI_INTEGRACIO",
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
//			throw tractarExcepcioEnSistemaExtern(
//					IntegracioHelper.INTCODI_PROCEDIMENT,
//					errorDescripcio, 
//					ex);
		}
		return unitatAdministrativa;
	}

	public UnitatAdministrativa serveiGetUnitatAdministrativa(String codi) {
		String accioDescripcio = "Consulta de la unitat organitzativa per codi " + codi;
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi", codi);
		
		long t0 = System.currentTimeMillis();
		UnitatAdministrativa unitatAdministrativa = null;
        String usuariIntegracio = getPlugin().getUsuariIntegracio();
		try {
			unitatAdministrativa = getPlugin().findUnitatAdministrativaAmbCodi(codi);

			accioParams.put("resultat", unitatAdministrativa != null ? 
					unitatAdministrativa.getCodiDir3() + " " + unitatAdministrativa.getNom() 
					: "(no trobada)");
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_PROCEDIMENT,
					accioDescripcio,
					"USUARI_INTEGRACIO",
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);							
		} catch (Exception ex) {
			String errorDescripcio = "Error consultant la unitat organitzativa amb codi " + codi+ ": " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PROCEDIMENT,
					"NUMERO_REGISTRE",
					accioDescripcio,
					"USUARI_INTEGRACIO",
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
//			throw tractarExcepcioEnSistemaExtern(
//					IntegracioHelper.INTCODI_PROCEDIMENT,
//					errorDescripcio, 
//					ex);
		}
		return unitatAdministrativa;
	}
	
	public ProcedimentDto procedimentFindByCodiSia(String codiSia) {
		String accioDescripcio = "Consulta dels procediments pel codi SIA";
		String usuariIntegracio = getPlugin().getUsuariIntegracio();
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codiSia", codiSia);
		long t0 = System.currentTimeMillis();
		try {
			// codiDir3="A04003003" 		codiSia="874123"
			ProcedimentDto procediment = getPlugin().findAmbCodiSia(codiSia);
			// RegistreNumero no cal!!!
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_PROCEDIMENT,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return procediment;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de procediments: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PROCEDIMENT,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
							IntegracioHelper.INTCODI_PROCEDIMENT,
							errorDescripcio,
							ex);
		}
	}
	
	public boolean isActiu() {
		return getPlugin() != null;
	}

	@Override
	protected ProcedimentPlugin getPlugin() {
		var plugin = pluginMap.get(GLOBAL);
		if (plugin != null) {
			return plugin;
		}

		String codiEntitat = getCodiEntitatActual();
		
		loadPluginProperties(GRUP);
		
		String pluginClass = getPluginClassProperty();
		
		if (Strings.isNullOrEmpty(pluginClass)) {
			var msg = "No està configurada la classe pel plugin de procediments";
			log.error(msg);
			throw new SistemaExternException(PROCEDIMENT.name(), msg);
		}
		
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				Class<?> clazz = Class.forName(pluginClass);
				var configuracioEspecifica = configHelper.hasEntityGroupPropertiesModified(codiEntitat, getConfigGrup());
				Properties properties = configHelper.getAllEntityProperties(codiEntitat);
				plugin = (ProcedimentPlugin)clazz.
						getDeclaredConstructor(Properties.class, boolean.class).
						newInstance(properties, configuracioEspecifica);
				plugin.init(meterRegistry, getCodiApp().name() + "_PROCEDIMENTSS");
			} catch (Exception ex) {
				throw new SistemaExternException(
						PROCEDIMENT.name(),
						"Error al crear la instància del plugin de procediments amb el nom de la classe " + pluginClass,
						ex);
			}
			pluginMap.put(GLOBAL, plugin);
			return plugin;
		} else {
			throw new SistemaExternException(
					PROCEDIMENT.name(),
					"No està configurada la classe pel plugin de procediments");
		}
	}

	@Override
	protected String getPluginClassProperty() {
		return configHelper.getConfig("es.caib.distribucio.plugin.procediment.class");
	}
	
	@Override
	protected IntegracioApp getCodiApp() {
		return IntegracioApp.RSC;
	}

	@Override
	protected String getConfigGrup() {
		return GRUP;
	}

}
