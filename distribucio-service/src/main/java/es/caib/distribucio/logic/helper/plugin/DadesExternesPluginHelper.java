package es.caib.distribucio.logic.helper.plugin;

import static es.caib.distribucio.logic.intf.dto.IntegracioCodi.UNITATS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.IntegracioHelper;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.IntegracioDiagnostic;
import es.caib.distribucio.logic.intf.dto.TipusViaDto;
import es.caib.distribucio.logic.intf.exception.SistemaExternException;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.plugin.dadesext.DadesExternesPlugin;
import es.caib.distribucio.plugin.dadesext.Municipi;
import es.caib.distribucio.plugin.dadesext.Provincia;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class DadesExternesPluginHelper extends AbstractPluginHelper<DadesExternesPlugin> {

	public static final String GRUP = "DADES_EXTERNES";
	
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	
	public DadesExternesPluginHelper(
			IntegracioHelper integracioHelper, 
			ConfigHelper configHelper,
			EntitatRepository entitatRepository,
			MeterRegistry meterRegistry) {
		super(integracioHelper, configHelper, entitatRepository, meterRegistry);
	}

	@Override
	public boolean diagnosticar(Map<String, IntegracioDiagnostic> diagnostics) throws Exception {
		var diagnosticOk = true;
		if (diagnostics.isEmpty()) {
			var entitat = entitatRepository.findByCodi(getCodiEntitatActual());
			var tipusVies = getPlugin().tipusViaFindAll();
			IntegracioDiagnostic diagnostic = new IntegracioDiagnostic();
			diagnostic.setCorrecte(tipusVies != null);
			diagnostics.put(entitat.getCodi(), diagnostic);
		}
		return diagnosticOk;
	}
	
	public boolean isActiu() {
		return getPlugin() != null;
	}

	public List<TipusViaDto> dadesExternesTipusViaAll() {
		String accioDescripcio = "Consulta de tipus de via";
		
		DadesExternesPlugin dadesExternesPlugin = this.getPlugin();
		String usuariIntegracio = dadesExternesPlugin.getUsuariIntegracio();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		long t0 = System.currentTimeMillis();
		try {
			List<TipusViaDto> tipusVies = conversioTipusHelper.convertirList(
					getPlugin().tipusViaFindAll(),
					TipusViaDto.class);
			// RegistreNumero no cal!!!
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0);
			return tipusVies;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_DADESEXT,
					errorDescripcio,
					ex);
		}
	}

	public List<Provincia> dadesExternesProvinciesFindAmbComunitat(
			String comunitatCodi) {
		String accioDescripcio = "Consulta de les províncies d'una comunitat";

		DadesExternesPlugin dadesExternesPlugin = this.getPlugin();
		String usuariIntegracio = dadesExternesPlugin.getUsuariIntegracio();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("comunitatCodi", comunitatCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<Provincia> provincies = getPlugin().provinciaFindByComunitat(comunitatCodi);
			// RegistreNumero no cal!!!
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return provincies;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_DADESEXT,
					errorDescripcio,
					ex);
		}
	}

	public List<Municipi> dadesExternesMunicipisFindAmbProvincia(
			String provinciaCodi) {
		String accioDescripcio = "Consulta dels municipis d'una província";

		DadesExternesPlugin dadesExternesPlugin = this.getPlugin();
		String usuariIntegracio = dadesExternesPlugin.getUsuariIntegracio();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("provinciaCodi", provinciaCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<Municipi> municipis = getPlugin().municipiFindByProvincia(provinciaCodi);
			// RegistreNumero no cal!!!
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return municipis;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_DADESEXT,
					errorDescripcio,
					ex);
		}
	}
	
	@Override
	protected DadesExternesPlugin getPlugin() {
		var plugin = pluginMap.get(GLOBAL);
		if (plugin != null) {
			return plugin;
		}

		String codiEntitat = getCodiEntitatActual();
		
		loadPluginProperties(GRUP);
		
		String pluginClass = getPluginClassProperty();
		
		if (Strings.isNullOrEmpty(pluginClass)) {
			var msg = "No està configurada la classe pel plugin de dades externes";
			log.error(msg);
			throw new SistemaExternException(UNITATS.name(), msg);
		}
		
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				Class<?> clazz = Class.forName(pluginClass);
				var configuracioEspecifica = configHelper.hasEntityGroupPropertiesModified(codiEntitat, getConfigGrup());
				Properties properties = configHelper.getAllEntityProperties(codiEntitat);
				plugin = (DadesExternesPlugin)clazz.
						getDeclaredConstructor(Properties.class, boolean.class).
						newInstance(properties, configuracioEspecifica);
				plugin.init(meterRegistry, getCodiApp().name());
			} catch (Exception ex) {
				throw new SistemaExternException(
						UNITATS.name(),
						"Error al crear la instància del plugin de consulta de dades externes amb el nom de la classe " + pluginClass,
						ex);
			}
			pluginMap.put(GLOBAL, plugin);
			return plugin;
		} else {
			throw new SistemaExternException(
					UNITATS.name(),
					"No està configurada la classe pel plugin de dades externes");
		}
	}

	@Override
	protected String getPluginClassProperty() {
		return configHelper.getConfig("es.caib.distribucio.plugin.dadesext.class");
	}
	
	@Override
	protected IntegracioApp getCodiApp() {
		return IntegracioApp.DIR;
	}

	@Override
	protected String getConfigGrup() {
		return GRUP;
	}

}
