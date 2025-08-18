package es.caib.distribucio.logic.helper.plugin;

import static es.caib.distribucio.logic.intf.dto.IntegracioCodi.USUARIS;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
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
import es.caib.distribucio.persist.entity.RegistreEntity;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.RegistreRepository;
import es.caib.distribucio.plugin.arxiu.ArxiuPlugin;
import es.caib.pluginsib.arxiu.api.Expedient;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class ArxiuPluginHelper extends AbstractPluginHelper<ArxiuPlugin> {

	public static final String GRUP = "ARXIU";

	@Autowired
	private RegistreRepository registreRepository;
	
	public ArxiuPluginHelper(
			IntegracioHelper integracioHelper, 
			ConfigHelper configHelper,
			EntitatRepository entitatRepository) {
		super(integracioHelper, configHelper, entitatRepository);
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
				var registre = registreRepository.findTopByEntitatAndExpedientArxiuUuidNotNull(entitat);
				var arxiu = plugin.documentDetalls(registre.getExpedientArxiuUuid(), null, true);
				diagnostic = new IntegracioDiagnostic();
				diagnostic.setCorrecte(arxiu != null);
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
			var registre = registreRepository.findTopByEntitatAndExpedientArxiuUuidNotNull(entitat);
			var expedient = arxiuExpedientInfo(registre.getExpedientArxiuUuid(), registre.getNumero());
			diagnostic = new IntegracioDiagnostic();
			diagnostic.setCorrecte(expedient != null);
			diagnostics.put(entitat.getCodi(), diagnostic);
		}
		return diagnosticOk;
	}
	
	public void arxiuExpedientEliminar(
			String idContingut,
			String registreNumero) {
		String accioDescripcio = "Eliminació d'un expedient";
		String usuariIntegracio = this.getUsuariAutenticat();
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("idContingut", idContingut);
		long t0 = System.currentTimeMillis();
		try {
			getPlugin().expedientEsborrar(idContingut);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					registreNumero,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					registreNumero,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_ARXIU,
					errorDescripcio,
					ex);
		}
	}
	
	public void arxiuExpedientReobrir(
			RegistreEntity registre) {
		String accioDescripcio = "Reobrir l'expedient a l'Arxiu";
		String usuariIntegracio = this.getUsuariAutenticat();
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("expedientArxiuUuid", registre.getExpedientArxiuUuid());
		accioParams.put("expedientNumero", registre.getExpedientNumero());
		accioParams.put("registreNom", registre.getNom());
		accioParams.put("registreNumero", registre.getNumero());
		accioParams.put("registreEntitat", registre.getEntitat().getCodi());
		accioParams.put("registreUnitatAdmin", registre.getUnitatAdministrativa());
		long t0 = System.currentTimeMillis();
		try {			
			getPlugin().expedientReobrir(
					registre.getExpedientArxiuUuid());
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					registre.getNumero(),
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al reobrir expedient";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					registre.getNumero(),
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_ARXIU,
					errorDescripcio,
					ex);
		}
	}
	
	public void arxiuExpedientTancar(
			RegistreEntity registre) {
		String accioDescripcio = "Tancar l'expedient a l'Arxiu";
		String usuariIntegracio = this.getUsuariAutenticat();
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("expedientArxiuUuid", registre.getExpedientArxiuUuid());
		accioParams.put("expedientNumero", registre.getExpedientNumero());
		accioParams.put("registreNom", registre.getNom());
		accioParams.put("registreNumero", registre.getNumero());
		accioParams.put("registreEntitat", registre.getEntitat().getCodi());
		accioParams.put("registreUnitatAdmin", registre.getUnitatAdministrativa());
		long t0 = System.currentTimeMillis();
		try {			
			getPlugin().expedientTancar(
					registre.getExpedientArxiuUuid());
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					registre.getNumero(),
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al marcar el contenidor com a processat";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					registre.getNumero(),
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_ARXIU,
					errorDescripcio,
					ex);
		}
	}
	
	public Expedient arxiuExpedientInfo(
			String arxiuUuid,
			String registreNumero) {

		String accioDescripcio = "Consulta d'un expedient";
		String usuariIntegracio = this.getUsuariAutenticat();
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("expedientArxiuUuid", arxiuUuid);
		long t0 = System.currentTimeMillis();
		try {
			es.caib.pluginsib.arxiu.api.Expedient exp = getPlugin().expedientDetalls(arxiuUuid, null);

			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					registreNumero,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return exp;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					registreNumero,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_ARXIU,
					errorDescripcio,
					ex);
		}
	}
	
	public boolean isActiu() {
		return getPlugin() != null;
	}
	
	@Override
	protected ArxiuPlugin getPlugin() {
		var plugin = pluginMap.get(GLOBAL);
		if (plugin != null) {
			return plugin;
		}

		String codiEntitat = getCodiEntitatActual();
		
		loadPluginProperties(GRUP);
		
		String pluginClass = getPluginClassProperty();
		
		if (Strings.isNullOrEmpty(pluginClass)) {
			var msg = "No està configurada la classe pel plugin d'arxiu digital";
			log.error(msg);
			throw new SistemaExternException(USUARIS.name(), msg);
		}
		
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				Class<?> clazz = Class.forName(pluginClass);
				Properties properties = configHelper.getAllProperties(codiEntitat);
				plugin = (ArxiuPlugin)clazz.
						getDeclaredConstructor(String.class, Properties.class).
						newInstance("es.caib.distribucio.", properties);
			} catch (Exception ex) {
				throw new SistemaExternException(
						USUARIS.name(),
						"Error al crear la instància del plugin d'arxiu digital amb el nom de la classe " + pluginClass,
						ex);
			}
			pluginMap.put(GLOBAL, plugin);
			return plugin;
		} else {
			throw new SistemaExternException(
					USUARIS.name(),
					"No està configurada la classe pel plugin d'arxiu digital");
		}
	}

	@Override
	protected String getPluginClassProperty() {
		return configHelper.getConfig("es.caib.distribucio.plugin.arxiu.class");
	}
	
	@Override
	protected IntegracioApp getCodiApp() {
		return IntegracioApp.ARX;
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
