package es.caib.distribucio.logic.helper.plugin;

import static es.caib.distribucio.logic.intf.dto.IntegracioCodi.USUARIS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.helper.IntegracioHelper;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.IntegracioDiagnostic;
import es.caib.distribucio.logic.intf.exception.SistemaExternException;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.plugin.usuari.DadesUsuari;
import es.caib.distribucio.plugin.usuari.DadesUsuariPlugin;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class DadesUsuarisPluginHelper extends AbstractPluginHelper<DadesUsuariPlugin> {

	public static final String GRUP = "USUARIS";
	
	public DadesUsuarisPluginHelper(
			IntegracioHelper integracioHelper, 
			ConfigHelper configHelper,
			EntitatRepository entitatRepository,
			MeterRegistry meterRegistry) {
		super(integracioHelper, configHelper, entitatRepository, meterRegistry);
	}

	@Override
	public boolean diagnosticar(Map<String, IntegracioDiagnostic> diagnostics) throws Exception {
		var dades = dadesUsuariFindAmbCodi(diagnostics.keySet().stream().iterator().next());
		return dades != null;
	}
	
	public DadesUsuari dadesUsuariFindAmbCodi(String usuariCodi) {
		
//		var info = new IntegracioInfo(
//				IntegracioCodi.USUARIS,
//				null,
//				"Consulta d'usuari amb codi", 
//				getUsuariAutenticat(),
//				IntegracioAccioTipusEnumDto.ENVIAMENT,
//				new AccioParam("Codi d'usuari", usuariCodi));
//
//		try {
//			var dadesUsuari = getPlugin().findAmbCodi(usuariCodi);
//			integracioHelper.addAccioOk(
//					info, 
//					false);
//			return dadesUsuari;
//		} catch (Exception ex) {
//			var errorDescripcio = "Error al accedir al plugin de dades d'usuari";
//			integracioHelper.addAccioError(
//					info, 
//					errorDescripcio, 
//					ex);
//			throw new SistemaExternException(USUARIS.name(), errorDescripcio, ex);
//		}
		String accioDescripcio = "Consulta d'usuari amb codi";
		
		String usuariIntegracio = this.getUsuariAutenticat();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi", usuariCodi);
		long t0 = System.currentTimeMillis();
		try {
			DadesUsuari dadesUsuari = getPlugin().findAmbCodi(
					usuariCodi);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return dadesUsuari;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_USUARIS,
					errorDescripcio,
					ex);
		}
	}

	public List<DadesUsuari> findAmbGrup(String grupCodi) {
//		var info = new IntegracioInfo(
//				IntegracioCodi.USUARIS,
//				null,
//				"Consulta d'usuaris d'un grup", 
//				getUsuariAutenticat(),
//				IntegracioAccioTipusEnumDto.ENVIAMENT,
//				new AccioParam("Codi del grup", grupCodi));
//
//		try {
//			var dadesUsuari = getPlugin().findAmbGrup(grupCodi);
//			integracioHelper.addAccioOk(
//					info, 
//					false);
//			return dadesUsuari;
//		} catch (Exception ex) {
//			var errorDescripcio = "Error al accedir al plugin de dades d'usuari";
//			integracioHelper.addAccioError(
//					info, 
//					errorDescripcio, 
//					ex);
//			throw new SistemaExternException(USUARIS.name(), errorDescripcio, ex);
//		}
		String accioDescripcio = "Consulta d'usuaris d'un grup";

		String usuariIntegracio = this.getUsuariAutenticat();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("grup", grupCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<DadesUsuari> dadesUsuari = getPlugin().findAmbGrup(
					grupCodi);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return dadesUsuari;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_USUARIS,
					errorDescripcio,
					ex);
		}
	}

	public List<String> findRolsPerUsuari(String usuariCodi) {
		String accioDescripcio = "Consulta rols d'un usuari amb codi";
		
		String usuariIntegracio = this.getUsuariAutenticat();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi", usuariCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<String> rols = getPlugin().findRolsPerUsuari(usuariCodi);
			// RegistreNumero no cal!!!
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return rols;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_USUARIS,
					errorDescripcio,
					ex);
		}
	}
	
	@Override
	protected DadesUsuariPlugin getPlugin() {
		var plugin = pluginMap.get(GLOBAL);
		if (plugin != null) {
			return plugin;
		}
		
		loadPluginProperties(GRUP);
		
		String pluginClass = getPluginClassProperty();
		
		if (Strings.isNullOrEmpty(pluginClass)) {
			var msg = "No està configurada la classe pel plugin de dades d'usuari";
			log.error(msg);
			throw new SistemaExternException(USUARIS.name(), msg);
		}
		
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				Class<?> clazz = Class.forName(pluginClass);
				plugin = (DadesUsuariPlugin)clazz.
						getDeclaredConstructor(String.class, Properties.class, boolean.class).
						newInstance("es.caib.distribucio.plugin.dades.usuari.", configHelper.getAllEntityProperties(null), false);
				plugin.init(meterRegistry, getCodiApp().name());
			} catch (Exception ex) {
				throw new SistemaExternException(
						USUARIS.name(),
						"Error al crear la instància del plugin de dades d'usuari amb el nom de la classe " + pluginClass,
						ex);
			}
			pluginMap.put(GLOBAL, plugin);
			return plugin;
		} else {
			throw new SistemaExternException(
					USUARIS.name(),
					"No està configurada la classe pel plugin de dades d'usuari");
		}
	}
	
	@Override
	protected String getPluginClassProperty() {
		return configHelper.getConfig("es.caib.distribucio.plugin.dades.usuari.class");
	}

	@Override
	protected IntegracioApp getCodiApp() {
		return IntegracioApp.USR;
	}

	@Override
	protected String getConfigGrup() {
		return GRUP;
	}
	
	private String getUsuariAutenticat() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null ? authentication.getName() : null;
	}

}
