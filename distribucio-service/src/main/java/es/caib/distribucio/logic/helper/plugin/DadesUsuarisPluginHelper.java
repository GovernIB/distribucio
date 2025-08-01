package es.caib.distribucio.logic.helper.plugin;

import static es.caib.distribucio.logic.intf.dto.IntegracioCodi.USUARIS;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import es.caib.comanda.salut.model.EstatSalut;
import es.caib.comanda.salut.model.EstatSalutEnum;
import es.caib.comanda.salut.model.IntegracioApp;
import es.caib.comanda.salut.model.IntegracioSalut;
import es.caib.distribucio.logic.helper.AbstractPluginHelper;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.helper.IntegracioHelper;
import es.caib.distribucio.logic.intf.dto.AccioParam;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.IntegracioCodi;
import es.caib.distribucio.logic.intf.dto.IntegracioDiagnostic;
import es.caib.distribucio.logic.intf.dto.IntegracioInfo;
import es.caib.distribucio.logic.intf.exception.SistemaExternException;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.plugin.usuari.DadesUsuari;
import es.caib.distribucio.plugin.usuari.DadesUsuariPlugin;
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
			EntitatRepository entitatRepository) {
		super(integracioHelper, configHelper, entitatRepository);
	}

	@Override
	public boolean diagnosticar(Map<String, IntegracioDiagnostic> diagnostics) throws Exception {
		var dades = dadesUsuariFindAmbCodi(diagnostics.keySet().stream().iterator().next());
		return dades != null;
	}
	
	public DadesUsuari dadesUsuariFindAmbCodi(String usuariCodi) {
		
		var info = new IntegracioInfo(
				IntegracioCodi.USUARIS,
				null,
				"Consulta d'usuari amb codi", 
				getUsuariAutenticat(),
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi d'usuari", usuariCodi));

		try {
			var dadesUsuari = getPlugin().findAmbCodi(usuariCodi);
			integracioHelper.addAccioOk(
					info, 
					false);
			return dadesUsuari;
		} catch (Exception ex) {
			var errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(
					info, 
					errorDescripcio, 
					ex);
			throw new SistemaExternException(USUARIS.name(), errorDescripcio, ex);
		}
	}

	public List<DadesUsuari> findAmbGrup(String grupCodi) {
		var info = new IntegracioInfo(
				IntegracioCodi.USUARIS,
				null,
				"Consulta d'usuaris d'un grup", 
				getUsuariAutenticat(),
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				new AccioParam("Codi del grup", grupCodi));

		try {
			var dadesUsuari = getPlugin().findAmbGrup(grupCodi);
			integracioHelper.addAccioOk(
					info, 
					false);
			return dadesUsuari;
		} catch (Exception ex) {
			var errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(
					info, 
					errorDescripcio, 
					ex);
			throw new SistemaExternException(USUARIS.name(), errorDescripcio, ex);
		}
	}
	
	@Override
	protected DadesUsuariPlugin getPlugin() {
		var plugin = pluginMap.get(GLOBAL);
		if (plugin != null) {
			return plugin;
		}
		
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
						getDeclaredConstructor(String.class, Properties.class).
						newInstance("es.caib.distribucio.plugin.dades.usuari.", configHelper.getAllEntityProperties(null));
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
	
	// SALUT

	@Override
	public List<es.caib.comanda.salut.model.IntegracioInfo> getIntegracionsInfo() {
		return List.of(es.caib.comanda.salut.model.IntegracioInfo.builder()
				.codi(getCodiApp().name())
				.nom(getCodiApp().getNom())
				.build());
	}

	@Override
	public List<IntegracioSalut> getIntegracionsSalut() {
		var plugin = pluginMap.get(GLOBAL);
		if (plugin == null) {
			return List.of(IntegracioSalut.builder().codi(getCodiApp().name()).estat(EstatSalutEnum.UNKNOWN).build());
		}

		EstatSalut estatSalut = plugin.getEstatPlugin();
		return List.of(IntegracioSalut.builder()
				.codi(getCodiApp().name())
				.estat(estatSalut.getEstat())
				.latencia(estatSalut.getLatencia())
				.peticions(plugin.getPeticionsPlugin())
				.build());
	}
	
	private String getUsuariAutenticat() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null ? authentication.getName() : null;
	}

}
