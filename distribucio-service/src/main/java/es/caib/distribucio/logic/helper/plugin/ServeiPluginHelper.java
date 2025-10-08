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
import es.caib.distribucio.logic.intf.exception.SistemaExternException;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.plugin.servei.Servei;
import es.caib.distribucio.plugin.servei.ServeiPlugin;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class ServeiPluginHelper extends AbstractPluginHelper<ServeiPlugin> {

	public static final String GRUP = "SERVEIS";
	
	public ServeiPluginHelper(
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
			var procediments = serveiFindByCodiDir3(entitat.getCodiDir3());
			diagnostic = new IntegracioDiagnostic();
			diagnostic.setCorrecte(procediments != null && !procediments.isEmpty());
			diagnostics.put(entitat.getCodi(), diagnostic);
		}
		return diagnosticOk;
	}
	
	public List<Servei> serveiFindByCodiDir3(
			String codiDir3) {
		String accioDescripcio = "Consulta dels serveis pel codi DIR3 " + codiDir3;

		ServeiPlugin serveiPlugin = this.getPlugin();
		String usuariIntegracio = serveiPlugin.getUsuariIntegracio();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codiDir3", codiDir3);
		long t0 = System.currentTimeMillis();
		try {
			//codiDir3 = "A04003003";
			List<Servei> serveis = getPlugin().findAmbCodiDir3(codiDir3);
			// RegistreNumero no cal!!!
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_SERVEI,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,	
					System.currentTimeMillis() - t0);
			return serveis;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de serveis: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_SERVEI,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_SERVEI,
					errorDescripcio,
					ex);
		}
	}
	
	public boolean isActiu() {
		return getPlugin() != null;
	}

	@Override
	protected ServeiPlugin getPlugin() {
		var plugin = pluginMap.get(GLOBAL);
		if (plugin != null) {
			return plugin;
		}

		String codiEntitat = getCodiEntitatActual();
		
		loadPluginProperties(GRUP);
		
		String pluginClass = getPluginClassProperty();
		
		if (Strings.isNullOrEmpty(pluginClass)) {
			var msg = "No està configurada la classe pel plugin de serveis";
			log.error(msg);
			throw new SistemaExternException(PROCEDIMENT.name(), msg);
		}
		
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				Class<?> clazz = Class.forName(pluginClass);
				var configuracioEspecifica = configHelper.hasEntityGroupPropertiesModified(codiEntitat, getConfigGrup());
				Properties properties = configHelper.getAllEntityProperties(codiEntitat);
				plugin = (ServeiPlugin)clazz.
						getDeclaredConstructor(Properties.class, boolean.class).
						newInstance(properties, configuracioEspecifica);
				plugin.init(meterRegistry, getCodiApp().name() + "_SERVEIS");
			} catch (Exception ex) {
				throw new SistemaExternException(
						PROCEDIMENT.name(),
						"Error al crear la instància del plugin de serveis amb el nom de la classe " + pluginClass,
						ex);
			}
			pluginMap.put(GLOBAL, plugin);
			return plugin;
		} else {
			throw new SistemaExternException(
					PROCEDIMENT.name(),
					"No està configurada la classe pel plugin de serveis");
		}
	}

	@Override
	protected String getPluginClassProperty() {
		return configHelper.getConfig("es.caib.distribucio.plugin.servei.class");
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
