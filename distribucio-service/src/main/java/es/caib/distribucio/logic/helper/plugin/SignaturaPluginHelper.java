package es.caib.distribucio.logic.helper.plugin;

import static es.caib.distribucio.logic.intf.dto.IntegracioCodi.SIGNATURA;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
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
import es.caib.distribucio.plugin.signatura.SignaturaPlugin;
import es.caib.distribucio.plugin.signatura.SignaturaResposta;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class SignaturaPluginHelper extends AbstractPluginHelper<SignaturaPlugin> {

	public static final String GRUP = "SIGNATURA";
	
	public SignaturaPluginHelper(
			IntegracioHelper integracioHelper, 
			ConfigHelper configHelper,
			EntitatRepository entitatRepository,
			MeterRegistry meterRegistry) {
		super(integracioHelper, configHelper, entitatRepository, meterRegistry);
	}

	@Override
	public boolean diagnosticar(Map<String, IntegracioDiagnostic> diagnostics) throws Exception {
		SignaturaResposta resposta = signarDocument(
				String.valueOf( new Date().getTime()), 
				"test_firmat.pdf", 
				"Prova firma en servidor diagnòstic Distribucio", 
				imputAByte(this.getClass().getResourceAsStream("/diagnostic/test_firma.pdf")), 
				"application/pdf", 
				"TD99");
		
		return resposta != null && resposta.getTipusFirma() != null;
	}

	public SignaturaResposta signarDocument(String id,
			String nom,
			String motiu,
			byte[] contingut, 
			String mime,
			String tipusDocumental) {
		
		SignaturaPlugin pluginSignar = this.getPlugin();

		String accioDescripcio = "Firma en servidor de document annex de l'anotació de registre";
		String usuariIntegracio = "";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("nom", nom);
		accioParams.put("motiu", motiu);
		accioParams.put("contingut", (contingut != null ? "" + contingut.length : "0") + " bytes");
		accioParams.put("mime", mime);
		accioParams.put("tipusDocumental", tipusDocumental);
		long t0 = System.currentTimeMillis();

		try {
			SignaturaResposta signatura = pluginSignar.signar(
					id,
					nom, 
					motiu, 
					contingut, 
					mime, 
					tipusDocumental);
			
			accioParams.put("resposta", "tipus: " + signatura.getTipusFirmaEni() + 
					", perfil: " + signatura.getPerfilFirmaEni() + 
					", nom: " + signatura.getNom() + 
					", mime: " + signatura.getMime() + 
					", grandaria: " + (signatura.getContingut() != null ? 
							signatura.getContingut().length : "-"));
			
			// PENDENT DE REVISAR COM OBTENIR EL NUMERO DE REGISTRE
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_SIGNATURA,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);

			return signatura;
		} catch (Throwable ex){
			String msgError = "No s'ha pogut signar el document: " + ex.getMessage();
			log.error(msgError, ex);
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_SIGNATURA,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					msgError,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_SIGNATURA,
					msgError,
					ex);
		}
	}
	
	public boolean isActiu() {
		return getPlugin() != null;
	}

	@Override
	protected SignaturaPlugin getPlugin() {
		var plugin = pluginMap.get(GLOBAL);
		if (plugin != null) {
			return plugin;
		}

		String codiEntitat = getCodiEntitatActual();
		
		loadPluginProperties(GRUP);
		
		String pluginClass = getPluginClassProperty();
		
		if (Strings.isNullOrEmpty(pluginClass)) {
			var msg = "No està configurada la classe pel plugin de signatura";
			log.error(msg);
			throw new SistemaExternException(SIGNATURA.name(), msg);
		}
		
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				Class<?> clazz = Class.forName(pluginClass);
				Properties properties = configHelper.getAllEntityProperties(codiEntitat);
				plugin = (SignaturaPlugin)clazz.
						getDeclaredConstructor(Properties.class).
						newInstance(properties);
				plugin.init(meterRegistry, getCodiApp().name());
			} catch (Exception ex) {
				throw new SistemaExternException(
						SIGNATURA.name(),
						"Error al crear la instància del plugin de signatura amb el nom de la classe " + pluginClass,
						ex);
			}
			pluginMap.put(GLOBAL, plugin);
			return plugin;
		} else {
			throw new SistemaExternException(
					SIGNATURA.name(),
					"No està configurada la classe pel plugin de signatura");
		}
	}

	@Override
	protected String getPluginClassProperty() {
		return configHelper.getConfig("es.caib.distribucio.plugin.signatura.class");
	}
	
	@Override
	protected IntegracioApp getCodiApp() {
		return IntegracioApp.PFI;
	}

	@Override
	protected String getConfigGrup() {
		return GRUP;
	}
	
	private byte[] imputAByte(InputStream input) throws IOException {
		byte[] buffer = new byte[8192];
		int bytesRead;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
		return output.toByteArray();
	}
}
