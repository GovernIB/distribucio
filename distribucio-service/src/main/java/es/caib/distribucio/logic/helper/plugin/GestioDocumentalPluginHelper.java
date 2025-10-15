package es.caib.distribucio.logic.helper.plugin;

import static es.caib.distribucio.logic.helper.SubsistemesHelper.SubsistemesEnum.GDO;
import static es.caib.distribucio.logic.intf.dto.IntegracioCodi.GESDOC;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.helper.GestioDocumentalHelper;
import es.caib.distribucio.logic.helper.IntegracioHelper;
import es.caib.distribucio.logic.helper.SubsistemesHelper;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.IntegracioDiagnostic;
import es.caib.distribucio.logic.intf.exception.SistemaExternException;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.plugin.gesdoc.GestioDocumentalPlugin;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class GestioDocumentalPluginHelper extends AbstractPluginHelper<GestioDocumentalPlugin> {

	public static final String GRUP = "GES_DOC";
	
	public GestioDocumentalPluginHelper(
			IntegracioHelper integracioHelper, 
			ConfigHelper configHelper,
			EntitatRepository entitatRepository,
			MeterRegistry meterRegistry) {
		super(integracioHelper, configHelper, entitatRepository, meterRegistry);
	}

	@Override
	public boolean diagnosticar(Map<String, IntegracioDiagnostic> diagnostics) throws Exception {
		var id = gestioDocumentalCreate(GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP, new byte[0], null);
		gestioDocumentalDelete(id, GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP);
		return true;
	}
	
	public void gestioDocumentalGet(
			String id,
			String agrupacio,
			OutputStream contingutOut,
			String registreNumero) {
		String accioDescripcio = "Consultant document a dins la gestió documental";
		String usuariIntegracio = this.getUsuariAutenticat();
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", id);
		accioParams.put("agrupacio", agrupacio);
		long t0 = System.currentTimeMillis();
		try {
			if (getPlugin() != null) {
				getPlugin().get(
						id,
						agrupacio,
						contingutOut);
			}
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_GESDOC,
					registreNumero,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			SubsistemesHelper.addSuccessOperation(GDO, System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al consultar document a dins la gestió documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					registreNumero,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			SubsistemesHelper.addErrorOperation(GDO);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}

	public String gestioDocumentalCreate(
			String agrupacio,
			byte[] contingut,
			String registreNumero) {
		String accioDescripcio = "Creant nou document a dins la gestió documental";
		String usuariIntegracio = this.getUsuariAutenticat();		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("agrupacio", agrupacio);
		int contingutLength = contingut != null ? contingut.length : 0;
		accioParams.put("numBytes", Integer.toString(contingutLength));
		long t0 = System.currentTimeMillis();
		try {
			String gestioDocumentalId = null;
			if (getPlugin() != null) {
				gestioDocumentalId = getPlugin().create(
						agrupacio,
						new ByteArrayInputStream(contingut));
			}
			accioParams.put("idRetornat", gestioDocumentalId);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_GESDOC,
					registreNumero,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			SubsistemesHelper.addSuccessOperation(GDO, System.currentTimeMillis() - t0);
			return gestioDocumentalId;
		} catch (Exception ex) {
			String errorDescripcio = "Error al crear document a dins la gestió documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					registreNumero,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			SubsistemesHelper.addErrorOperation(GDO);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}

	public void gestioDocumentalDelete(
			String id,
			String agrupacio) {
		String accioDescripcio = "Esborrant document a dins la gestió documental";
		String usuariIntegracio = this.getUsuariAutenticat();
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", id);
		accioParams.put("agrupacio", agrupacio);
		long t0 = System.currentTimeMillis();
		try {
			if (getPlugin() != null) {
				getPlugin().delete(
						id,
						agrupacio);
			}
			// PENDENT DE REVISAR COM OBTENIR EL NUMERO DE REGISTRE
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			SubsistemesHelper.addSuccessOperation(GDO, System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al esborrar document a dins la gestió documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			SubsistemesHelper.addErrorOperation(GDO);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}
	
	public boolean isActiu() {
		return getPlugin() != null;
	}

	@Override
	protected GestioDocumentalPlugin getPlugin() {
		var plugin = pluginMap.get(GLOBAL);
		if (plugin != null) {
			return plugin;
		}

		String codiEntitat = getCodiEntitatActual();
		
		loadPluginProperties(GRUP);
		
		String pluginClass = getPluginClassProperty();
		
		if (Strings.isNullOrEmpty(pluginClass)) {
			var msg = "No està configurada la classe pel plugin de gestió documental";
			log.error(msg);
			throw new SistemaExternException(GESDOC.name(), msg);
		}
		
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				Class<?> clazz = Class.forName(pluginClass);
				var configuracioEspecifica = configHelper.hasEntityGroupPropertiesModified(codiEntitat, getConfigGrup());
				Properties properties = configHelper.getAllEntityProperties(codiEntitat);
				plugin = (GestioDocumentalPlugin)clazz.
						getDeclaredConstructor(Properties.class, boolean.class).
						newInstance(properties, configuracioEspecifica);
				plugin.init(meterRegistry, getCodiApp().name());
			} catch (Exception ex) {
				throw new SistemaExternException(
						GESDOC.name(),
						"Error al crear la instància del plugin de gestió documental amb el nom de la classe " + pluginClass,
						ex);
			}
			pluginMap.put(GLOBAL, plugin);
			return plugin;
		} else {
			throw new SistemaExternException(
					GESDOC.name(),
					"No està configurada la classe pel plugin de gestió documental");
		}
	}

	@Override
	protected String getPluginClassProperty() {
		return configHelper.getConfig("es.caib.distribucio.plugin.gesdoc.class");
	}
	
	@Override
	protected IntegracioApp getCodiApp() {
		return IntegracioApp.GDC;
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
