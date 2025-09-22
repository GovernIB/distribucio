package es.caib.distribucio.logic.helper.plugin;

import static es.caib.distribucio.logic.intf.dto.IntegracioCodi.DISTRIBUCIO;

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
import es.caib.distribucio.logic.helper.GestioDocumentalHelper;
import es.caib.distribucio.logic.helper.IntegracioHelper;
import es.caib.distribucio.logic.intf.dto.DocumentEniRegistrableDto;
import es.caib.distribucio.logic.intf.dto.FitxerDto;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.IntegracioDiagnostic;
import es.caib.distribucio.logic.intf.exception.SistemaExternException;
import es.caib.distribucio.persist.entity.RegistreAnnexEntity;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.RegistreAnnexRepository;
import es.caib.distribucio.plugin.distribucio.DistribucioPlugin;
import es.caib.distribucio.plugin.distribucio.DistribucioPlugin.IntegracioManager;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnnex;
import es.caib.distribucio.plugin.utils.TemporalThreadStorage;
import es.caib.pluginsib.arxiu.api.Document;
import es.caib.pluginsib.arxiu.api.DocumentContingut;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class DistribucioPluginHelper extends AbstractPluginHelper<DistribucioPlugin> {

	public static final String GRUP = "DISTRIBUCIO";
	
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;
	
	public DistribucioPluginHelper(
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
				var document = registreAnnexRepository.findTopByRegistre_EntitatAndFitxerArxiuUuidNotNull(entitat);
				var documentDetalls = getPlugin().documentDescarregar(document.getFitxerArxiuUuid(), null, true, true);
				diagnostic = new IntegracioDiagnostic();
				diagnostic.setCorrecte(documentDetalls != null);
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
			var document = registreAnnexRepository.findTopByRegistre_EntitatAndFitxerArxiuUuidNotNull(entitat);
			var documentDetalls = getPlugin().documentDescarregar(document.getFitxerArxiuUuid(), null, true, true);
			diagnostic = new IntegracioDiagnostic();
			diagnostic.setCorrecte(documentDetalls != null);
			diagnostics.put(entitat.getCodi(), diagnostic);
		}
		return diagnosticOk;
	}
	
	public String saveRegistreAsExpedientInArxiu(
			String registreNumero,
			String expedientNumero,
			String unitatOrganitzativaCodi) {
		String accioDescripcio = "Creant contenidor pels documents annexos";
		String usuariIntegracio = this.getUsuariAutenticat();
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("registreNumero", registreNumero);
		accioParams.put("unitatOrganitzativaCodi", unitatOrganitzativaCodi);
		long t0 = System.currentTimeMillis();
		try {
			TemporalThreadStorage.set("numeroRegistre", registreNumero);
			String contenidorUuid = getPlugin().expedientCrear(
					expedientNumero,
					unitatOrganitzativaCodi);
			TemporalThreadStorage.clear();
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
					registreNumero,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return contenidorUuid;
		} catch (Exception ex) {
			TemporalThreadStorage.clear();
			String errorDescripcio = "Error al crear contenidor pels documents annexos";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
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

	public String saveAnnexAsDocumentInArxiu(
			String registreNumero,
			DistribucioRegistreAnnex annex,
			String unitatOrganitzativaCodi,
			String uuidExpedient,
			DocumentEniRegistrableDto documentEniRegistrableDto, 
			String procedimentCodi) {
		String accioDescripcio = "Guardant document annex a dins el contenidor";
		String usuariIntegracio = this.getUsuariAutenticat();		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("registreNumero", registreNumero);
		accioParams.put("unitatOrganitzativaCodi", unitatOrganitzativaCodi);
		accioParams.put("annexTitol", annex.getTitol());
		accioParams.put("fitxerNom", annex.getFitxerNom());
		accioParams.put("uuidExpedient", uuidExpedient);
		accioParams.put("validacioFirma", annex.getValidacioFirmaEstat() != null ? annex.getValidacioFirmaEstat().toString() : "-");
		accioParams.put("validacioFirmaError", annex.getValidacioFirmaError()!= null ? annex.getValidacioFirmaError().toString() : "-");
		boolean annexFirmat = annex.getFirmes() != null && !annex.getFirmes().isEmpty();
		accioParams.put("annexFirmat", Boolean.valueOf(annexFirmat).toString());
		long t0 = System.currentTimeMillis();
		try {
			
			boolean throwException = false; // throwException = true
			if(throwException){
				throw new RuntimeException("Mock exception when saving annex in arxiu");
			}
			TemporalThreadStorage.set("numeroRegistre", registreNumero);
			String documentUuid = getPlugin().saveAnnexAsDocumentInArxiu(
					annex,
					unitatOrganitzativaCodi,
					uuidExpedient,
					documentEniRegistrableDto, 
					procedimentCodi);
			TemporalThreadStorage.clear();
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
					registreNumero,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return documentUuid;
		} catch (Exception ex) {
			TemporalThreadStorage.clear();
			String errorDescripcio = "Error al crear el document annex (Titol=" + annex.getTitol() + ") a dins el contenidor";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
					registreNumero,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
					errorDescripcio,
					ex);
		}
	}
	
	public Document arxiuDocumentConsultar(
			String arxiuUuid,
			String versio,
			boolean ambContingut,
			boolean ambVersioImprimible,
			String registreNumero) {
		String accioDescripcio = "Consulta d'un document";
		String usuariIntegracio = this.getUsuariAutenticat();
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("nodeId", arxiuUuid);
		accioParams.put("arxiuUuidCalculat", arxiuUuid);
		accioParams.put("versio", versio);
		accioParams.put("ambContingut", Boolean.valueOf(ambContingut).toString());
		long t0 = System.currentTimeMillis();
		try {
			TemporalThreadStorage.set("numeroRegistre", registreNumero);
			Document documentDetalls = getPlugin().documentDescarregar(arxiuUuid, versio, ambContingut, ambVersioImprimible);
			TemporalThreadStorage.clear();
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
					registreNumero,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return documentDetalls;
		} catch (Exception ex) {
			TemporalThreadStorage.clear();
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
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
	
	public FitxerDto arxiuDocumentImprimible(String fitxerArxiuUuid, String titol) {
		
		FitxerDto fitxerDto = new FitxerDto();
		
		try {
			DocumentContingut documentImprimible = getPlugin().documentImprimible(fitxerArxiuUuid);
			TemporalThreadStorage.clear();
			if (documentImprimible != null) {
				fitxerDto.setNom(titol);
				fitxerDto.setContentType(documentImprimible.getTipusMime());
				fitxerDto.setContingut(documentImprimible.getContingut());
				fitxerDto.setTamany(documentImprimible.getContingut().length);
			}
		} catch (Exception e) {
			TemporalThreadStorage.clear();
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_ARXIU,
					"Error consultant el contingut imprimible del document " + fitxerArxiuUuid,
					e);
		}
		
		return fitxerDto;
	}

	public void arxiuDocumentSetDefinitiu(RegistreAnnexEntity annex) {
		String accioDescripcio = "Canviant el document \"" + annex.getTitol() + "\" de l'anotació " + annex.getRegistre().getNumero() + " a definitiu";
		String usuariIntegracio = this.getUsuariAutenticat();
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("annexUuid", annex.getFitxerArxiuUuid());
		accioParams.put("annexTitol", annex.getTitol());
		accioParams.put("annexFirmesSize", String.valueOf(annex.getFirmes() != null ? annex.getFirmes().size() : 0));
		accioParams.put("registreNumero", annex.getRegistre().getNumero());
		accioParams.put("entitat", annex.getRegistre().getEntitat().getCodi());
		long t0 = System.currentTimeMillis();
		try {
			getPlugin().documentSetDefinitiu(annex.getFitxerArxiuUuid());
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
					annex.getRegistre().getNumero(),
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error posant com a definitiu un annex per l'anotació " + annex.getRegistre().getNumero();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
					annex.getRegistre().getNumero(),
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
					errorDescripcio,
					ex);
		}
	}

	@Override
	protected DistribucioPlugin getPlugin() {
		var plugin = pluginMap.get(GLOBAL);
		if (plugin != null) {
			return plugin;
		}
		
		String codiEntitat = getCodiEntitatActual();
		
		loadPluginProperties("ARXIU");
		loadPluginProperties("GES_DOC");
		loadPluginProperties("SIGNATURA");
		
		String pluginClass = getPluginClassProperty();
		
		if (Strings.isNullOrEmpty(pluginClass)) {
			var msg = "No està configurada la classe pel plugin de distribucio";
			log.error(msg);
			throw new SistemaExternException(DISTRIBUCIO.name(), msg);
		}
		
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				Class<?> clazz = Class.forName(pluginClass);
				Properties properties = configHelper.getAllEntityProperties(codiEntitat);
				plugin = (DistribucioPlugin)clazz.
						getDeclaredConstructor(Properties.class).
						newInstance(properties);
				
				plugin.configurar(
						new IntegracioManager() {
							public void addAccioOk(
									String integracioCodi,
									String descripcio,
									String usuariIntegracio,
									Map<String, String> parametres,
									long tempsResposta) {
								integracioHelper.addAccioOk(
										integracioCodi,
										descripcio,
										usuariIntegracio,
										parametres,
										IntegracioAccioTipusEnumDto.ENVIAMENT,
										tempsResposta);
							}
							public void addAccioError(
									String integracioCodi,
									String descripcio,
									String usuariIntegracio,
									Map<String, String> parametres,
									long tempsResposta,
									String errorDescripcio,
									Throwable throwable) {
								integracioHelper.addAccioError(
										integracioCodi,
										descripcio,
										usuariIntegracio,
										parametres,
										IntegracioAccioTipusEnumDto.ENVIAMENT,
										tempsResposta,
										errorDescripcio,
										throwable);
							}
						},
						IntegracioHelper.INTCODI_GESDOC,
						IntegracioHelper.INTCODI_ARXIU,
						IntegracioHelper.INTCODI_SIGNATURA,
						GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP,
						GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP);
				plugin.init(meterRegistry, getCodiApp().name());
			} catch (Exception ex) {
				throw new SistemaExternException(
						DISTRIBUCIO.name(),
						"Error al crear la instància del plugin de distribucio amb el nom de la classe " + pluginClass,
						ex);
			}
			pluginMap.put(GLOBAL, plugin);
			return plugin;
		} else {
			throw new SistemaExternException(
					DISTRIBUCIO.name(),
					"No està configurada la classe pel plugin de distribucio");
		}
	}

	@Override
	protected String getPluginClassProperty() {
		String pluginClass = configHelper.getConfig(
				"es.caib.distribucio.plugins.distribucio.class");
		if (pluginClass == null) {
			return configHelper.getConfig(
					"es.caib.distribucio.plugins.distribucio.fitxers.class");
		} else {
			return pluginClass;
		}
	}
	
	@Override
	protected IntegracioApp getCodiApp() {
		return IntegracioApp.DIS;
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
