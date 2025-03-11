/**
 * 
 */
package es.caib.distribucio.logic.helper;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.fundaciobit.pluginsib.validatecertificate.InformacioCertificat;
import org.fundaciobit.pluginsib.validatesignature.api.IValidateSignaturePlugin;
import org.fundaciobit.pluginsib.validatesignature.api.SignatureDetailInfo;
import org.fundaciobit.pluginsib.validatesignature.api.SignatureRequestedInformation;
import org.fundaciobit.pluginsib.validatesignature.api.TimeStampInfo;
import org.fundaciobit.pluginsib.validatesignature.api.ValidateSignatureRequest;
import org.fundaciobit.pluginsib.validatesignature.api.ValidateSignatureResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.distribucio.logic.intf.dto.ArxiuFirmaDetallDto;
import es.caib.distribucio.logic.intf.dto.DocumentEniRegistrableDto;
import es.caib.distribucio.logic.intf.dto.FitxerDto;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentDto;
import es.caib.distribucio.logic.intf.dto.TipusViaDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.logic.intf.exception.SistemaExternException;
import es.caib.distribucio.logic.intf.helper.ArxiuConversions;
import es.caib.distribucio.persist.entity.RegistreAnnexEntity;
import es.caib.distribucio.persist.entity.RegistreEntity;
import es.caib.distribucio.persist.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.plugin.dadesext.DadesExternesPlugin;
import es.caib.distribucio.plugin.dadesext.Municipi;
import es.caib.distribucio.plugin.dadesext.Provincia;
import es.caib.distribucio.plugin.distribucio.DistribucioPlugin;
import es.caib.distribucio.plugin.distribucio.DistribucioPlugin.IntegracioManager;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnnex;
import es.caib.distribucio.plugin.gesdoc.GestioDocumentalPlugin;
import es.caib.distribucio.plugin.procediment.Procediment;
import es.caib.distribucio.plugin.procediment.ProcedimentPlugin;
import es.caib.distribucio.plugin.procediment.UnitatAdministrativa;
import es.caib.distribucio.plugin.servei.Servei;
import es.caib.distribucio.plugin.servei.ServeiPlugin;
import es.caib.distribucio.plugin.signatura.SignaturaPlugin;
import es.caib.distribucio.plugin.signatura.SignaturaResposta;
import es.caib.distribucio.plugin.unitat.UnitatOrganitzativa;
import es.caib.distribucio.plugin.unitat.UnitatsOrganitzativesPlugin;
import es.caib.distribucio.plugin.usuari.DadesUsuari;
import es.caib.distribucio.plugin.usuari.DadesUsuariPlugin;
import es.caib.distribucio.plugin.validacio.ValidaSignaturaResposta;
import es.caib.pluginsib.arxiu.api.Document;
import es.caib.pluginsib.arxiu.api.DocumentContingut;
import es.caib.pluginsib.arxiu.api.IArxiuPlugin;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PluginHelper {

	private DadesUsuariPlugin dadesUsuariPlugin = null;
	private Map<String, UnitatsOrganitzativesPlugin> unitatsOrganitzativesPlugin = new HashMap<>();
	private Map<String, DadesExternesPlugin> dadesExternesPlugin = new HashMap<>();
	private Map<String, IArxiuPlugin> arxiuPlugin = new HashMap<>();
	private Map<String, IValidateSignaturePlugin> validaSignaturaPlugin = new HashMap<>();
	private Map<String, ProcedimentPlugin> procedimentPlugin = new HashMap<>();
	private Map<String, ServeiPlugin> serveiPlugin = new HashMap<>();
	private Map<String, GestioDocumentalPlugin> gestioDocumentalPlugin = new HashMap<>();
	private Map<String, DistribucioPlugin> distribucioPlugin = new HashMap<>();
	private Map<String, SignaturaPlugin> signaturaPlugin = new HashMap<>();

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Resource
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private LoadedPropertiesHelper loadedPropertiesHelper;

	/** Mètode per consultar el codi de l'entitat actual */
 	private String getCodiEntitatActual() {
		String codiEntitat = ConfigHelper.getEntitatActualCodi();
		if (StringUtils.isEmpty(codiEntitat)) {
			throw new RuntimeException("El codi de l'entitat no pot ser null");
		}
		return codiEntitat;
	}

	/* Mètode per crear un nou expedient*/
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
			String contenidorUuid = getDistribucioPlugin().expedientCrear(
					expedientNumero,
					unitatOrganitzativaCodi);
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
			
			String documentUuid = getDistribucioPlugin().saveAnnexAsDocumentInArxiu(
					annex,
					unitatOrganitzativaCodi,
					uuidExpedient,
					documentEniRegistrableDto, 
					procedimentCodi);
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

	public DadesUsuari dadesUsuariFindAmbCodi(
			String usuariCodi) {
		String accioDescripcio = "Consulta d'usuari amb codi";
		
		String usuariIntegracio = this.getUsuariAutenticat();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi", usuariCodi);
		long t0 = System.currentTimeMillis();
		try {
			DadesUsuari dadesUsuari = getDadesUsuariPlugin().findAmbCodi(
					usuariCodi);
			// RegistreNumero no cal!!!
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
	public List<DadesUsuari> dadesUsuariFindAmbGrup(
			String grupCodi) {
		String accioDescripcio = "Consulta d'usuaris d'un grup";

		String usuariIntegracio = this.getUsuariAutenticat();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("grup", grupCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<DadesUsuari> dadesUsuari = getDadesUsuariPlugin().findAmbGrup(
					grupCodi);
			// RegistreNumero no cal!!!
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
	
	public boolean isActiuPluginUnitatsOrganitzatives() {
		return getUnitatsOrganitzativesPlugin() != null;
	}

	public UnitatOrganitzativa findUnidad(
			String pareCodi, 
			Timestamp fechaActualizacion, 
			Timestamp fechaSincronizacion) {
		String accioDescripcio = "Consulta unitat donat un pare"; 
		
		UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin = this.getUnitatsOrganitzativesPlugin(); 
		String usuariIntegracio = unitatsOrganitzativesPlugin.getUsuariIntegracio();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("unitatPare", pareCodi);
		accioParams.put("fechaActualizacion", fechaActualizacion == null ? null : fechaActualizacion.toString());
		accioParams.put("fechaSincronizacion", fechaSincronizacion == null ? null : fechaSincronizacion.toString());
		long t0 = System.currentTimeMillis();
		try {
			UnitatOrganitzativa unitat = getUnitatsOrganitzativesPlugin().findUnidad(
					pareCodi, fechaActualizacion, fechaSincronizacion);
			if (unitat != null) {
				// RegistreNumero no cal!!!
				integracioHelper.addAccioOk(
						IntegracioHelper.INTCODI_UNITATS,
						accioDescripcio,
						usuariIntegracio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0);
				return unitat;
			} else {
				String errorMissatge = "No s'ha trobat la unitat organitzativa llistat (codi=" + pareCodi + ")";
				integracioHelper.addAccioError(
						IntegracioHelper.INTCODI_UNITATS,
						accioDescripcio,
						usuariIntegracio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						errorMissatge,
						null);
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_UNITATS,
						errorMissatge);
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'unitats organitzatives";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_UNITATS,
					errorDescripcio,
					ex);
		}
	}
	
	public List<UnitatOrganitzativa> findAmbPare(
			String pareCodi, 
			Timestamp fechaActualizacion, 
			Timestamp fechaSincronizacion) {
		String accioDescripcio = "Consulta llista d'unitats donat un pare";
		
		UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin = this.getUnitatsOrganitzativesPlugin(); 
		String usuariIntegracio = unitatsOrganitzativesPlugin.getUsuariIntegracio();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("unitatPare", pareCodi);
		accioParams.put("fechaActualizacion", fechaActualizacion == null ? null : fechaActualizacion.toString());
		accioParams.put("fechaSincronizacion", fechaSincronizacion == null ? null : fechaSincronizacion.toString());
		long t0 = System.currentTimeMillis();
		try {
			List<UnitatOrganitzativa> arbol = getUnitatsOrganitzativesPlugin().findAmbPare(
					pareCodi, 
					fechaActualizacion,
					fechaSincronizacion);
			// Remove from list unitats that are substituted by itself
			removeUnitatsSubstitutedByItself(arbol);

			if (arbol != null && !arbol.isEmpty()) {
				
				logger.info("Consulta d'unitats a WS [tot camps](" +
						"codiDir3=" + pareCodi + ", " +
						"fechaActualizacion=" + fechaActualizacion + ", " +
						"fechaSincronizacion=" + fechaSincronizacion + ")");
				for (UnitatOrganitzativa un : arbol) {
					logger.info(ToStringBuilder.reflectionToString(un));
				}
				// RegistreNumero no cal!!!
				integracioHelper.addAccioOk(
						IntegracioHelper.INTCODI_UNITATS,
						accioDescripcio,
						usuariIntegracio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0);
				
			} else {
				logger.info("No s'han trobat cap unitats per consulta a WS (" +
						"codiDir3=" + pareCodi + ", " +
						"fechaActualizacion=" + fechaActualizacion + ", " +
						"fechaSincronizacion=" + fechaSincronizacion + ")");
				
				accioDescripcio = "No s'ha trobat la unitat organitzativa llistat (codi=" + pareCodi + ")";
				integracioHelper.addAccioOk(
						IntegracioHelper.INTCODI_UNITATS,
						accioDescripcio,
						usuariIntegracio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0);
			}
			return arbol;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'unitats organitzatives";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_UNITATS,
					errorDescripcio,
					ex);
		}
	}

	public List<UnitatOrganitzativaDto> unitatsOrganitzativesFindByFiltre(
			String codiUnitat, 
			String denominacioUnitat,
			String codiNivellAdministracio, 
			String codiComunitat, 
			String codiProvincia, 
			String codiLocalitat, 
			Boolean esUnitatArrel) {
		String accioDescripcio = "Consulta d'unitats organitzatives donat un filtre";

		UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin = this.getUnitatsOrganitzativesPlugin(); 
		String usuariIntegracio = unitatsOrganitzativesPlugin.getUsuariIntegracio();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codiUnitat", codiUnitat);
		accioParams.put("denominacioUnitat", denominacioUnitat);
		accioParams.put("codiNivellAdministracio", codiNivellAdministracio);
		accioParams.put("codiComunitat", codiComunitat);
		accioParams.put("codiProvincia", codiProvincia);
		accioParams.put("codiLocalitat", codiLocalitat);
		accioParams.put("esUnitatArrel", esUnitatArrel == null ? "null" : esUnitatArrel.toString() );
		long t0 = System.currentTimeMillis();
		try {
			List<UnitatOrganitzativaDto> unitatsOrganitzatives = conversioTipusHelper.convertirList(
					getUnitatsOrganitzativesPlugin().cercaUnitats(
							codiUnitat, 
							denominacioUnitat, 
							toLongValue(codiNivellAdministracio), 
							toLongValue(codiComunitat), 
							false, 
							esUnitatArrel, 
							toLongValue(codiProvincia), 
							codiLocalitat),
					UnitatOrganitzativaDto.class);
			// RegistreNumero no cal!!!
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return unitatsOrganitzatives;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al realitzar la cerca de unitats organitzatives";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_UNITATS,
					errorDescripcio,
					ex);
		}
	}

	public boolean isArxiuPluginActiu() {
		return getArxiuPlugin() != null;
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
			getArxiuPlugin().expedientEsborrar(idContingut);
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
			getArxiuPlugin().expedientReobrir(
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
			getArxiuPlugin().expedientTancar(
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

	public Document arxiuDocumentConsultar(
			String arxiuUuid,
			String versio,
			boolean ambContingut,
			String registreNumero) {
		return arxiuDocumentConsultar(
				arxiuUuid,
				versio,
				ambContingut,
				false,
				registreNumero);
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
			Document documentDetalls = getDistribucioPlugin().documentDescarregar(arxiuUuid, versio, ambContingut, ambVersioImprimible);
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
			DocumentContingut documentImprimible = getDistribucioPlugin().documentImprimible(fitxerArxiuUuid);
			if (documentImprimible != null) {
				fitxerDto.setNom(titol);
				fitxerDto.setContentType(documentImprimible.getTipusMime());
				fitxerDto.setContingut(documentImprimible.getContingut());
				fitxerDto.setTamany(documentImprimible.getContingut().length);
			}
		} catch (Exception e) {
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_ARXIU,
					"Error consultant el contingut imprimible del document " + fitxerArxiuUuid,
					e);
		}
		
		return fitxerDto;
	}

	/** Recupera les dades de l'arxiu i el modifica per fer-lo definitiu */
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
			getDistribucioPlugin().documentSetDefinitiu(annex.getFitxerArxiuUuid());
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


	public boolean isValidaSignaturaPluginActiu() {
		return getValidaSignaturaPlugin() != null;
	}

	public ValidaSignaturaResposta validaSignaturaObtenirDetalls(
			byte[] documentContingut,
			byte[] firmaContingut,
			String registreNumero) {

		ValidaSignaturaResposta resposta = new ValidaSignaturaResposta();
		
		String accioDescripcio = "Obtenir informació de document firmat";
		String usuariIntegracio = this.getPropertyUsuariValidacioSignatura();
		
		// Abans de cridar a la validació de firmes comprova si la grandària supera el màxim en bytes
		Integer maxBytes = getPropertyMaxBytesValidacioFirma();
		int bytes = Math.max(documentContingut != null ? documentContingut.length : 0, 
								firmaContingut != null ? firmaContingut.length : 0);
		if (maxBytes != null && maxBytes < bytes) {
			resposta.setStatus(ValidaSignaturaResposta.FIRMA_ERROR);
			resposta.setErrMsg("Error de validació. La grandària de la firma " + bytes + " és superior a la grandària màxima configurada " + maxBytes + " i no es pot enviar a validar.");
			return resposta;
		}
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("documentContingut", documentContingut != null ? documentContingut.length + " bytes" : "null");
		accioParams.put("firmaContingut", firmaContingut != null ? firmaContingut.length + " bytes" : "null");
		long t0 = System.currentTimeMillis();
		try {
			ValidateSignatureRequest validationRequest = new ValidateSignatureRequest();
			if (documentContingut != null && firmaContingut == null) {
				firmaContingut = documentContingut;
				documentContingut = null;
			}
			if (firmaContingut != null) {
				validationRequest.setSignedDocumentData(documentContingut);
				validationRequest.setSignatureData(firmaContingut);
			} else {
				validationRequest.setSignatureData(documentContingut);
			}
			SignatureRequestedInformation sri = new SignatureRequestedInformation();
			sri.setReturnSignatureTypeFormatProfile(true);
			sri.setReturnCertificateInfo(true);
			sri.setReturnValidationChecks(false);
			sri.setValidateCertificateRevocation(false);
			sri.setReturnCertificates(false);
			sri.setReturnTimeStampInfo(true);
			validationRequest.setSignatureRequestedInformation(sri);
			ValidateSignatureResponse validateSignatureResponse = getValidaSignaturaPlugin().validateSignature(validationRequest);
			
			// Completa la resposta
			resposta.setStatus(validateSignatureResponse.getValidationStatus().getStatus());
			resposta.setErrMsg(validateSignatureResponse.getValidationStatus().getErrorMsg());
			resposta.setErrException(validateSignatureResponse.getValidationStatus().getErrorException());
			
			if (validateSignatureResponse.getSignatureDetailInfo() != null) {
				for (SignatureDetailInfo signatureInfo: validateSignatureResponse.getSignatureDetailInfo()) {
					ArxiuFirmaDetallDto detall = new ArxiuFirmaDetallDto();
					TimeStampInfo timeStampInfo = signatureInfo.getTimeStampInfo();
					if (timeStampInfo != null) {
						detall.setData(timeStampInfo.getCreationTime());
					} else {
						detall.setData(signatureInfo.getSignDate());
					}
					InformacioCertificat certificateInfo = signatureInfo.getCertificateInfo();
					if (certificateInfo != null) {
						detall.setResponsableNif(certificateInfo.getNifResponsable());
						detall.setResponsableNom(certificateInfo.getNomCompletResponsable());
						detall.setEmissorCertificat(certificateInfo.getEmissorOrganitzacio());
					}
					resposta.getFirmaDetalls().add(detall);
				}
				resposta.setPerfil(ArxiuConversions.toPerfilFirmaArxiu(validateSignatureResponse.getSignProfile()));
				resposta.setTipus(ArxiuConversions.toFirmaTipus(
						validateSignatureResponse.getSignType(),
						validateSignatureResponse.getSignFormat()));
			}
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
			String errorDescripcio = "Error al accedir al plugin de validar signatures";
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

	public SignaturaResposta signarDocument(String id,
			String nom,
			String motiu,
			byte[] contingut, 
			String mime,
			String tipusDocumental) {
		
		SignaturaPlugin pluginSignar = this.signaturaPlugin();

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
			SignaturaResposta signatura = pluginSignar.signar(id,
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
			logger.error(msgError, ex);
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

	


	public boolean isDadesExternesPluginActiu() {
		return getDadesExternesPlugin() != null;
	}

	public List<TipusViaDto> dadesExternesTipusViaAll() {
		String accioDescripcio = "Consulta de tipus de via";
		
		DadesExternesPlugin dadesExternesPlugin = this.getDadesExternesPlugin();
		String usuariIntegracio = dadesExternesPlugin.getUsuariIntegracio();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		long t0 = System.currentTimeMillis();
		try {
			List<TipusViaDto> tipusVies = conversioTipusHelper.convertirList(
					getDadesExternesPlugin().tipusViaFindAll(),
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

		DadesExternesPlugin dadesExternesPlugin = this.getDadesExternesPlugin();
		String usuariIntegracio = dadesExternesPlugin.getUsuariIntegracio();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("comunitatCodi", comunitatCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<Provincia> provincies = getDadesExternesPlugin().provinciaFindByComunitat(comunitatCodi);
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

		DadesExternesPlugin dadesExternesPlugin = this.getDadesExternesPlugin();
		String usuariIntegracio = dadesExternesPlugin.getUsuariIntegracio();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("provinciaCodi", provinciaCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<Municipi> municipis = getDadesExternesPlugin().municipiFindByProvincia(provinciaCodi);
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


	public es.caib.pluginsib.arxiu.api.Expedient arxiuExpedientInfo(
			String arxiuUuid,
			String registreNumero) {

		String accioDescripcio = "Consulta d'un expedient";
		String usuariIntegracio = this.getUsuariAutenticat();
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("expedientArxiuUuid", arxiuUuid);
		long t0 = System.currentTimeMillis();
		try {
			es.caib.pluginsib.arxiu.api.Expedient exp = getArxiuPlugin().expedientDetalls(arxiuUuid, null);

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
	
	public boolean isProcedimentPluginActiu() {
		return getProcedimentPlugin() != null;
	}
		
	public UnitatAdministrativa procedimentGetUnitatAdministrativa(String codi) {
		String accioDescripcio = "Consulta de la unitat organitzativa per codi " + codi;
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi", codi);
		
		long t0 = System.currentTimeMillis();
		UnitatAdministrativa unitatAdministrativa = null;
		try {
			unitatAdministrativa = getProcedimentPlugin().findUnitatAdministrativaAmbCodi(codi);

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

	public List<Procediment> procedimentFindByCodiDir3(
			String codiDir3) {
		String accioDescripcio = "Consulta dels procediments pel codi DIR3 " + codiDir3;

		ProcedimentPlugin procedimentPlugin = this.getProcedimentPlugin();
		String usuariIntegracio = procedimentPlugin.getUsuariIntegracio();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codiDir3", codiDir3);
		long t0 = System.currentTimeMillis();
		try {
			//codiDir3 = "A04003003";
			List<Procediment> procediments = getProcedimentPlugin().findAmbCodiDir3(codiDir3);
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
	
	public boolean isServeiPluginActiu() {
		return getServeiPlugin() != null;
	}

	public List<Servei> serveiFindByCodiDir3(
			String codiDir3) {
		String accioDescripcio = "Consulta dels serveis pel codi DIR3 " + codiDir3;

		ServeiPlugin serveiPlugin = this.getServeiPlugin();
		String usuariIntegracio = serveiPlugin.getUsuariIntegracio();
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codiDir3", codiDir3);
		long t0 = System.currentTimeMillis();
		try {
			//codiDir3 = "A04003003";
			List<Servei> serveis = getServeiPlugin().findAmbCodiDir3(codiDir3);
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
	
	/** Consulta de l'unitat organitzativa d'un servei.
	 * 
	 * @param codi
	 * @return
	 */
	public UnitatAdministrativa serveiGetUnitatAdministrativa(String codi) {
		String accioDescripcio = "Consulta de la unitat organitzativa per codi " + codi;
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi", codi);
		
		long t0 = System.currentTimeMillis();
		UnitatAdministrativa unitatAdministrativa = null;
		try {
			unitatAdministrativa = getProcedimentPlugin().findUnitatAdministrativaAmbCodi(codi);

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
		String usuariIntegracio = getProcedimentPlugin().getUsuariIntegracio();
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codiSia", codiSia);
		long t0 = System.currentTimeMillis();
		try {
			// codiDir3="A04003003" 		codiSia="874123"
			ProcedimentDto procediment = getProcedimentPlugin().findAmbCodiSia(codiSia);
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

	public boolean isGestioDocumentalPluginActiu() {
		return getGestioDocumentalPlugin() != null;
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
			if (getGestioDocumentalPlugin() != null) {
				getGestioDocumentalPlugin().get(
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
			if (getGestioDocumentalPlugin() != null) {
				gestioDocumentalId = getGestioDocumentalPlugin().create(
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
			if (getGestioDocumentalPlugin() != null) {
				getGestioDocumentalPlugin().delete(
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
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}

	private Long toLongValue(String text) {
		if (text == null || text.isEmpty())
			return null;
		return Long.parseLong(text);
	}

	public void resetPlugins() {
		 dadesUsuariPlugin = null;
		 unitatsOrganitzativesPlugin.clear();
		 dadesExternesPlugin.clear();
		 arxiuPlugin.clear();
		 validaSignaturaPlugin.clear();
		 procedimentPlugin.clear();
		 gestioDocumentalPlugin.clear();
		 distribucioPlugin.clear();
		 signaturaPlugin.clear();
	}

	private DadesUsuariPlugin getDadesUsuariPlugin() {
		loadPluginProperties("USUARIS");
		if (dadesUsuariPlugin == null) {
			String pluginClass = getPropertyPluginDadesUsuari();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					dadesUsuariPlugin = (DadesUsuariPlugin)clazz.
							getDeclaredConstructor(String.class, Properties.class).
							newInstance("es.caib.distribucio.plugin.dades.usuari.", configHelper.getAllEntityProperties(null));
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_USUARIS,
							"Error al crear la instància del plugin de dades d'usuari amb el nom de la classe " + pluginClass,
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_USUARIS,
						"No està configurada la classe pel plugin de dades d'usuari");
			}
		}
		return dadesUsuariPlugin;
	}
	
	private UnitatsOrganitzativesPlugin getUnitatsOrganitzativesPlugin() {
		loadPluginProperties("UNITATS");
		String codiEntitat = getCodiEntitatActual();
		UnitatsOrganitzativesPlugin plugin = unitatsOrganitzativesPlugin.get(codiEntitat);
		if (plugin == null) {
			String pluginClass = getPropertyPluginUnitatsOrganitzatives();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					plugin = (UnitatsOrganitzativesPlugin)clazz.
							getDeclaredConstructor(Properties.class).
							newInstance(configHelper.getAllEntityProperties(codiEntitat));
					unitatsOrganitzativesPlugin.put(codiEntitat, plugin);
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_UNITATS,
							"Error al crear la instància del plugin d'unitats organitzatives amb el nom de la classe " + pluginClass,
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_UNITATS,
						"No està configurada la classe pel plugin d'unitats organitzatives");
			}
		}
		return plugin;
	}
	
	private IArxiuPlugin getArxiuPlugin() {
		loadPluginProperties("ARXIU");
		String codiEntitat = getCodiEntitatActual();
		IArxiuPlugin plugin = arxiuPlugin.get(codiEntitat);
		if (plugin == null) {
			String pluginClass = getPropertyPluginArxiu();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					// El plugin Arxiu CAIB té un constructor amb la key base i les propietats
					Properties properties = configHelper.getAllProperties(codiEntitat);
					plugin = (IArxiuPlugin)clazz.getDeclaredConstructor(
							String.class,
							Properties.class).newInstance("es.caib.distribucio.", properties);
					arxiuPlugin.put(codiEntitat, plugin);
					if (plugin == null) {
						throw new SistemaExternException(
								"No s'ha trobat la classe " + plugin);
					}
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_ARXIU,
							"Error al crear la instància del plugin d'arxiu digital amb el nom de la classe " + pluginClass,
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_ARXIU,
						"No està configurada la classe pel plugin d'arxiu digital");
			}
		}
		return plugin;
	}
	private DadesExternesPlugin getDadesExternesPlugin() {
		loadPluginProperties("DADES_EXTERNES");
		String codiEntitat = getCodiEntitatActual();
		DadesExternesPlugin plugin = dadesExternesPlugin.get(codiEntitat);
		if (plugin == null) {
			String pluginClass = getPropertyPluginDadesExternes();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					plugin = (DadesExternesPlugin)clazz.
							getDeclaredConstructor(Properties.class).
							newInstance(configHelper.getAllEntityProperties(codiEntitat));
					dadesExternesPlugin.put(codiEntitat, plugin);
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_DADESEXT,
							"Error al crear la instància del plugin de consulta de dades externes amb el nom de la classe " + pluginClass,
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_DADESEXT,
						"No està configurada la classe pel plugin de dades externes");
			}
		}
		return plugin;
	}

	private ProcedimentPlugin getProcedimentPlugin() {
		loadPluginProperties("PROCEDIMENTS");
		String codiEntitat = getCodiEntitatActual();
		ProcedimentPlugin plugin = procedimentPlugin.get(codiEntitat);
		if (plugin == null) {
			String pluginClass = getPropertyPluginProcediment();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					plugin = (ProcedimentPlugin)clazz.
							getDeclaredConstructor(Properties.class).
							newInstance(configHelper.getAllEntityProperties(codiEntitat));
					procedimentPlugin.put(codiEntitat, plugin);
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_PROCEDIMENT,
							"Error al crear la instància del plugin de procediments amb el nom de la classe " + pluginClass,
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_PROCEDIMENT,
						"No està configurada la classe pel plugin de procediments");
			}
		}
		return plugin;
	}
	
	private ServeiPlugin getServeiPlugin() {
		loadPluginProperties("SERVEIS");
		String codiEntitat = getCodiEntitatActual();
		ServeiPlugin plugin = serveiPlugin.get(codiEntitat);
		if (plugin == null) {
			String pluginClass = getPropertyPluginServei();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					plugin = (ServeiPlugin)clazz.
							getDeclaredConstructor(Properties.class).
							newInstance(configHelper.getAllEntityProperties(codiEntitat));
					serveiPlugin.put(codiEntitat, plugin);
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_SERVEI,
							"Error al crear la instància del plugin de serveis amb el nom de la classe " + pluginClass,
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_SERVEI,
						"No està configurada la classe pel plugin de serveis");
			}
		}
		return plugin;
	}
	
	private GestioDocumentalPlugin getGestioDocumentalPlugin() {
		loadPluginProperties("GES_DOC");
		String codiEntitat = getCodiEntitatActual();
		GestioDocumentalPlugin plugin = gestioDocumentalPlugin.get(codiEntitat);
		if (plugin == null) {
			String pluginClass = getPropertyPluginGestioDocumental();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					plugin = (GestioDocumentalPlugin)clazz.
							getDeclaredConstructor(Properties.class).
							newInstance(configHelper.getAllEntityProperties(codiEntitat));
					gestioDocumentalPlugin.put(codiEntitat, plugin);
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_GESDOC,
							"Error al crear la instància del plugin de gestió documental amb el nom de la classe " + pluginClass,
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_GESDOC,
						"No està configurada la classe pel plugin de gestió documental");
			}
		}
		return plugin;
	}
	
	private IValidateSignaturePlugin getValidaSignaturaPlugin() {
		loadPluginProperties("VALID_SIGN");
		String codiEntitat = getCodiEntitatActual();
		IValidateSignaturePlugin plugin = validaSignaturaPlugin.get(codiEntitat);
		if (plugin == null) {
			String pluginClass = getPropertyPluginValidaSignatura();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
//					Properties properties = ConfigHelper.JBossPropertiesHelper.getProperties().findAll();
//					properties.putAll(configHelper.getAllEntityProperties(codiEntitat));
					Properties properties = configHelper.getAllProperties(codiEntitat);
					plugin = (IValidateSignaturePlugin)clazz.
							getDeclaredConstructor(String.class, Properties.class).
							newInstance("es.caib.distribucio.", properties);
					validaSignaturaPlugin.put(codiEntitat, plugin);
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_VALIDASIG,
							"Error al crear la instància del plugin de validació de signatures amb el nom de la classe " + pluginClass,
							ex);
				}
			} else {
				return null;
			}
		}
		return plugin;
	}
	
	private SignaturaPlugin signaturaPlugin() {
		loadPluginProperties("SIGNATURA");
		String codiEntitat = getCodiEntitatActual();
		SignaturaPlugin plugin = signaturaPlugin.get(codiEntitat);
		if (plugin == null) {
			String pluginClass = getPropertyPluginSignatura();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					plugin = (SignaturaPlugin)clazz.
							getDeclaredConstructor(Properties.class).
							newInstance(configHelper.getAllEntityProperties(codiEntitat));
					signaturaPlugin.put(codiEntitat, plugin);
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_GESDOC,
							"Error al crear la instància del plugin de signatura amb el nom de la classe " + pluginClass,
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_GESDOC,
						"No està configurada la classe pel plugin de signatura");
			}
		}
		return plugin;
	}

	private DistribucioPlugin getDistribucioPlugin() {
		loadPluginProperties("ARXIU");
		loadPluginProperties("GES_DOC");
		loadPluginProperties("SIGNATURA");
		String codiEntitat = getCodiEntitatActual();
		DistribucioPlugin plugin = distribucioPlugin.get(codiEntitat);
		if (plugin == null) {
			String pluginClass = getPropertyPluginDistribucio();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					
//					Properties properties = ConfigHelper.JBossPropertiesHelper.getProperties().findAll();
//					properties.putAll(configHelper.getAllEntityProperties(codiEntitat));
					Properties properties = configHelper.getAllProperties(codiEntitat);
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
					distribucioPlugin.put(codiEntitat, plugin);
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_DISTRIBUCIO,
							"Error al crear la instància del plugin de distribucio amb el nom de la classe " + pluginClass,
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_DISTRIBUCIO,
						"No està configurada la classe pel plugin de distribucio");
			}
		}
		return plugin;
	}

	public synchronized void loadPluginProperties(String codeProperties) {
		 Map<String, Boolean> loadedProperties = loadedPropertiesHelper.getLoadedProperties();
		if (!loadedProperties.containsKey(codeProperties) || !loadedProperties.get(codeProperties)) {
			loadedProperties.put(codeProperties, true);
			Map<String, String> pluginProps = configHelper.getGroupProperties(codeProperties);
			for (Map.Entry<String, String> entry : pluginProps.entrySet() ) {
				if (entry.getValue() != null) {
					System.setProperty(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	private String getPropertyUsuariValidacioSignatura() {
		return configHelper.getConfig(
				"es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.username","-");
	}
	private String getPropertyPluginDadesUsuari() {
		return configHelper.getConfig(
				"es.caib.distribucio.plugin.dades.usuari.class");
	}
	private String getPropertyPluginUnitatsOrganitzatives() {
		return configHelper.getConfig(
				"es.caib.distribucio.plugin.unitats.organitzatives.class");
	}
	private String getPropertyPluginArxiu() {
		return configHelper.getConfig(
				"es.caib.distribucio.plugin.arxiu.class");
	}
	private String getPropertyPluginDadesExternes() {
		return configHelper.getConfig(
				"es.caib.distribucio.plugin.dadesext.class");
	}
	private String getPropertyPluginValidaSignatura() {
		return configHelper.getConfig(
				"es.caib.distribucio.plugin.validatesignature.class");
	}
	private String getPropertyPluginProcediment() {
		return configHelper.getConfig(
				"es.caib.distribucio.plugin.procediment.class");
	}
	private String getPropertyPluginServei() {
		return configHelper.getConfig(
				"es.caib.distribucio.plugin.servei.class");
	}
	private String getPropertyPluginGestioDocumental() {
		return configHelper.getConfig(
				"es.caib.distribucio.plugin.gesdoc.class");
	}
	private String getPropertyPluginDistribucio() {
		String pluginClass = configHelper.getConfig(
				"es.caib.distribucio.plugins.distribucio.class");
		if (pluginClass == null) {
			return configHelper.getConfig(
					"es.caib.distribucio.plugins.distribucio.fitxers.class");
		} else {
			return pluginClass;
		}
	}
	private String getPropertyPluginSignatura() {
		return configHelper.getConfig("es.caib.distribucio.plugin.signatura.class");
	}
	private String getUsuariAutenticat() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null ? authentication.getName() : null;
	}

	private Integer getPropertyMaxBytesValidacioFirma() {
		Integer maxBytes = null;
		String configKey = "es.caib.distribucio.pluginsib.validatesignature.maxBytes";
		try {
			maxBytes = Integer.valueOf(configHelper.getConfig(configKey));
		} catch(Exception e) {
			logger.error("Error llegint la propietat entera de màxim de bytes del plugin de validació " + configKey + ": " + e.getMessage());
		}
		return maxBytes;
	}

	/**
	 * Remove from list unitats that are substituted by itself
	 * for example if webservice returns two elements:
	 * 
	 * UnitatOrganitzativa(codi=A00000010, estat=E, historicosUO=[A00000010])
	 * UnitatOrganitzativa(codi=A00000010, estat=V, historicosUO=null)
	 * 
	 * then remove the first one.
	 * That way this transition can be treated by application the same way as transition CANVI EN ATRIBUTS
	 */
	private void removeUnitatsSubstitutedByItself(List<UnitatOrganitzativa> unitatsOrganitzatives) {
		if (!unitatsOrganitzatives.isEmpty()) {
			Iterator<UnitatOrganitzativa> i = unitatsOrganitzatives.iterator();
			while (i.hasNext()) {
				UnitatOrganitzativa unitatOrganitzativa = i.next();
				if (unitatOrganitzativa.getHistoricosUO()!=null 
						&& !unitatOrganitzativa.getHistoricosUO().isEmpty() 
						&& unitatOrganitzativa.getHistoricosUO().size() == 1 
						&& unitatOrganitzativa.getHistoricosUO().get(0).equals(unitatOrganitzativa.getCodi())) {
					i.remove();
				}
			}
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(PluginHelper.class);

}