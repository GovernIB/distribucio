package es.caib.distribucio.logic.helper.plugin;

import static es.caib.distribucio.logic.intf.dto.IntegracioCodi.VALIDASIG;

import java.util.HashMap;
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
import es.caib.distribucio.plugin.validacio.ValidaSignaturaResposta;
import es.caib.distribucio.plugin.validacio.ValidacioSignaturaPlugin;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class ValidaSignaturaAgilPluginHelper extends AbstractPluginHelper<ValidacioSignaturaPlugin> {
	
	public static final String GRUP = "VALID_SIGN_AGIL";
	
	public ValidaSignaturaAgilPluginHelper(
			IntegracioHelper integracioHelper, 
			ConfigHelper configHelper,
			EntitatRepository entitatRepository,
			MeterRegistry meterRegistry) {
		super(integracioHelper, configHelper, entitatRepository, meterRegistry);
	}

	@Override
	public boolean diagnosticar(Map<String, IntegracioDiagnostic> diagnostics) throws Exception {
		String arxiuNom = "test_validacio_firma.pdf";
		try (var arxiuSignat = this.getClass().getResourceAsStream("/diagnostic/" + arxiuNom)){
			if (arxiuSignat == null) {
				log.error("L'Arxiu de proves per el diagnostic no existeix");
				return false;
			}
			var bytes = arxiuSignat.readAllBytes();
			var signatura = validaSignaturaObtenirDetalls(arxiuNom, "application/pdf", bytes, null, null);
			return signatura != null && signatura.getErrMsg() == null;
		}
	}

	public ValidaSignaturaResposta validaSignaturaObtenirDetalls(
			String documentNom,
			String documentMime,
			byte[] documentContingut,
			byte[] firmaContingut,
			String registreNumero) {

		ValidaSignaturaResposta resposta = new ValidaSignaturaResposta();
		
		String accioDescripcio = "Obtenir informació de document firmat amb firma àgil";
		String usuariIntegracio = this.getPlugin().getUsuariIntegracio();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("documentContingut", documentContingut != null ? documentContingut.length + " bytes" : "null");
		long t0 = System.currentTimeMillis();
		try {
			resposta = 	this.getPlugin().validaSignatura(
					documentNom, 
					documentMime, 
					documentContingut, 
					firmaContingut);

			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_VALIDASIG,
					registreNumero,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return resposta;
		} catch (Throwable ex) {
			String errorDescripcio = "Error al accedir al plugin de validar signatures àgils";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_VALIDASIG,
					registreNumero,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_VALIDASIG,
					errorDescripcio,
					ex);
		}
	}
	
	
	
	public boolean isActiu() {
		return getPlugin() != null;
	}

	@Override
	protected ValidacioSignaturaPlugin getPlugin() {
		var plugin = pluginMap.get(GLOBAL);
		if (plugin != null) {
			return plugin;
		}

		String codiEntitat = getCodiEntitatActual();
		
		loadPluginProperties(GRUP);
		
		String pluginClass = getPluginClassProperty();
		
		if (Strings.isNullOrEmpty(pluginClass)) {
			var msg = "No està configurada la classe pel plugin de validació de signatures àgils";
			log.error(msg);
			throw new SistemaExternException(VALIDASIG.name(), msg);
		}
		
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				Class<?> clazz = Class.forName(pluginClass);
				var configuracioEspecifica = configHelper.hasEntityGroupPropertiesModified(codiEntitat, getConfigGrup());
				Properties properties = configHelper.getAllProperties(codiEntitat);
				plugin = (ValidacioSignaturaPlugin)clazz.
						getDeclaredConstructor(Properties.class, boolean.class).
						newInstance(properties, configuracioEspecifica);
				plugin.init(meterRegistry, getCodiApp().name());
			} catch (Exception ex) {
				throw new SistemaExternException(
						VALIDASIG.name(),
						"Error al crear la instància del plugin de validació de signatures àgils amb el nom de la classe " + pluginClass,
						ex);
			}
			pluginMap.put(GLOBAL, plugin);
			return plugin;
		} else {
			throw new SistemaExternException(
					VALIDASIG.name(),
					"No està configurada la classe pel plugin de validació de signatures àgils");
		}
	}

	@Override
	protected String getPluginClassProperty() {
		return configHelper.getConfig("es.caib.distribucio.plugins.validatesignature.api.evidenciesib.class");
	}
	
	@Override
	protected IntegracioApp getCodiApp() {
		return IntegracioApp.VFI;
	}

	@Override
	protected String getConfigGrup() {
		return GRUP;
	}

}
