/**
 * 
 */
package es.caib.distribucio.core.helper;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.fundaciobit.plugins.validatesignature.api.CertificateInfo;
import org.fundaciobit.plugins.validatesignature.api.IValidateSignaturePlugin;
import org.fundaciobit.plugins.validatesignature.api.SignatureDetailInfo;
import org.fundaciobit.plugins.validatesignature.api.SignatureRequestedInformation;
import org.fundaciobit.plugins.validatesignature.api.TimeStampInfo;
import org.fundaciobit.plugins.validatesignature.api.ValidateSignatureRequest;
import org.fundaciobit.plugins.validatesignature.api.ValidateSignatureResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.distribucio.core.api.dto.ArxiuFirmaDetallDto;
import es.caib.distribucio.core.api.dto.DocumentEniRegistrableDto;
import es.caib.distribucio.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.core.api.dto.TipusViaDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.exception.SistemaExternException;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.plugin.dadesext.DadesExternesPlugin;
import es.caib.distribucio.plugin.dadesext.Municipi;
import es.caib.distribucio.plugin.dadesext.Provincia;
import es.caib.distribucio.plugin.distribucio.DistribucioPlugin;
import es.caib.distribucio.plugin.distribucio.DistribucioPlugin.IntegracioManager;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnnex;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnotacio;
import es.caib.distribucio.plugin.gesdoc.GestioDocumentalPlugin;
import es.caib.distribucio.plugin.procediment.Procediment;
import es.caib.distribucio.plugin.procediment.ProcedimentPlugin;
import es.caib.distribucio.plugin.unitat.UnitatOrganitzativa;
import es.caib.distribucio.plugin.unitat.UnitatsOrganitzativesPlugin;
import es.caib.distribucio.plugin.usuari.DadesUsuari;
import es.caib.distribucio.plugin.usuari.DadesUsuariPlugin;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.IArxiuPlugin;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PluginHelper {

	public static final String GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP = "anotacions_registre_doc_tmp";
	public static final String GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP = "anotacions_registre_fir_tmp";
	public static final String GESDOC_AGRUPACIO_CERTIFICACIONS = "certificacions";
	public static final String GESDOC_AGRUPACIO_NOTIFICACIONS = "notificacions";

	private DadesUsuariPlugin dadesUsuariPlugin;
	private UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin;
	private DadesExternesPlugin dadesExternesPlugin;
	private IArxiuPlugin arxiuPlugin;
	private IValidateSignaturePlugin validaSignaturaPlugin;
	private GestioDocumentalPlugin gestioDocumentalPlugin;
	private ProcedimentPlugin procedimentPlugin;
	private DistribucioPlugin distribucioPlugin;

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Resource
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;

	public String distribucioContenidorCrear(
			String registreNumero,
			String expedientNumero,
			String unitatOrganitzativaCodi) {
		String accioDescripcio = "Creant contenidor per als documents annexos";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("registreNumero", registreNumero);
		accioParams.put("unitatOrganitzativaCodi", unitatOrganitzativaCodi);
		long t0 = System.currentTimeMillis();
		try {
			String contenidorUuid = getDistribucioPlugin().contenidorCrear(
					expedientNumero,
					unitatOrganitzativaCodi);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return contenidorUuid;
		} catch (Exception ex) {
			String errorDescripcio = "Error al crear contenidor per als documents annexos";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
					accioDescripcio,
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

	public String distribucioDocumentCrear(
			String registreNumero,
			DistribucioRegistreAnnex annex,
			String unitatOrganitzativaCodi,
			String uuidExpedient,
			DocumentEniRegistrableDto documentEniRegistrableDto) {
		String accioDescripcio = "Creant document annex a dins el contenidor";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("registreNumero", registreNumero);
		accioParams.put("annexTitol", annex.getTitol());
		accioParams.put("unitatOrganitzativaCodi", unitatOrganitzativaCodi);
		accioParams.put("uuidExpedient", uuidExpedient);
		boolean annexFirmat = annex.getFirmes() != null && !annex.getFirmes().isEmpty();
		accioParams.put("annexFirmat", new Boolean(annexFirmat).toString());
		long t0 = System.currentTimeMillis();
		try {
			String documentUuid = getDistribucioPlugin().documentCrear(
					annex,
					unitatOrganitzativaCodi,
					uuidExpedient,
					documentEniRegistrableDto);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return documentUuid;
		} catch (Exception ex) {
			String errorDescripcio = "Error al crear el document annex a dins el contenidor";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
					accioDescripcio,
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

	public void distribucioContenidorMarcarProcessat(
			RegistreEntity registre) {
		String accioDescripcio = "Marcant com a processat el contenidor relacionat amb anotació de registre";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("expedientArxiuUuid", registre.getExpedientArxiuUuid());
		accioParams.put("expedientNumero", registre.getExpedientNumero());
		accioParams.put("registreNom", registre.getNom());
		accioParams.put("registreNumero", registre.getNumero());
		accioParams.put("registreEntitat", registre.getEntitatCodi());
		accioParams.put("registreUnitatAdmin", registre.getUnitatAdministrativa());
		long t0 = System.currentTimeMillis();
		try {
			DistribucioRegistreAnotacio anotacio = conversioTipusHelper.convertir(
					registre,
					DistribucioRegistreAnotacio.class);
			getDistribucioPlugin().contenidorMarcarProcessat(anotacio);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al marcar el contenidor com a processat";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
					accioDescripcio,
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
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi", usuariCodi);
		long t0 = System.currentTimeMillis();
		try {
			DadesUsuari dadesUsuari = getDadesUsuariPlugin().findAmbCodi(
					usuariCodi);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return dadesUsuari;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
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
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("grup", grupCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<DadesUsuari> dadesUsuari = getDadesUsuariPlugin().findAmbGrup(
					grupCodi);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return dadesUsuari;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades d'usuari";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_USUARIS,
					accioDescripcio,
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

	public UnitatOrganitzativa findUnidad(
			String pareCodi, 
			Timestamp fechaActualizacion, 
			Timestamp fechaSincronizacion) {
		String accioDescripcio = "Consulta unitat donat un pare"; 
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("unitatPare", pareCodi);
		accioParams.put("fechaActualizacion", fechaActualizacion == null ? null : fechaActualizacion.toString());
		accioParams.put("fechaSincronizacion", fechaSincronizacion == null ? null : fechaSincronizacion.toString());
		long t0 = System.currentTimeMillis();
		try {
			UnitatOrganitzativa unitat = getUnitatsOrganitzativesPlugin().findUnidad(
					pareCodi, fechaActualizacion, fechaSincronizacion);
			if (unitat != null) {
				integracioHelper.addAccioOk(
						IntegracioHelper.INTCODI_UNITATS,
						accioDescripcio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0);
				return unitat;
			} else {
				String errorMissatge = "No s'ha trobat la unitat organitzativa llistat (codi=" + pareCodi + ")";
				integracioHelper.addAccioError(
						IntegracioHelper.INTCODI_UNITATS,
						accioDescripcio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						errorMissatge);
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_UNITATS,
						errorMissatge);
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'unitats organitzatives";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
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
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("unitatPare", pareCodi);
		accioParams.put("fechaActualizacion", fechaActualizacion == null ? null : fechaActualizacion.toString());
		accioParams.put("fechaSincronizacion", fechaSincronizacion == null ? null : fechaSincronizacion.toString());
		long t0 = System.currentTimeMillis();
		try {
			List<UnitatOrganitzativa> arbol = getUnitatsOrganitzativesPlugin().findAmbPare(
					pareCodi, fechaActualizacion, fechaSincronizacion);
			if (arbol != null) {
				integracioHelper.addAccioOk(
						IntegracioHelper.INTCODI_UNITATS,
						accioDescripcio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0);
				return arbol;
			} else {
				String errorMissatge = "No s'ha trobat la unitat organitzativa llistat (codi=" + pareCodi + ")";
				integracioHelper.addAccioError(
						IntegracioHelper.INTCODI_UNITATS,
						accioDescripcio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						errorMissatge);
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_UNITATS,
						errorMissatge);
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'unitats organitzatives";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
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
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return unitatsOrganitzatives;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al realitzar la cerca de unitats organitzatives";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_UNITATS,
					accioDescripcio,
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
			String idContingut) {
		String accioDescripcio = "Eliminació d'un expedient";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("idContingut", idContingut);
		long t0 = System.currentTimeMillis();
		try {
			getArxiuPlugin().expedientEsborrar(idContingut);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
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
			boolean ambContingut) {
		return arxiuDocumentConsultar(
				arxiuUuid,
				versio,
				ambContingut,
				false);
	}

	public Document arxiuDocumentConsultar(
			String arxiuUuid,
			String versio,
			boolean ambContingut,
			boolean ambVersioImprimible) {
		String accioDescripcio = "Consulta d'un document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("nodeId", arxiuUuid);
		accioParams.put("arxiuUuidCalculat", arxiuUuid);
		accioParams.put("versio", versio);
		accioParams.put("ambContingut", new Boolean(ambContingut).toString());
		long t0 = System.currentTimeMillis();
		try {
			Document documentDetalls = getDistribucioPlugin().documentDescarregar(arxiuUuid, versio, ambContingut, ambVersioImprimible);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return documentDetalls;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
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

	public boolean isValidaSignaturaPluginActiu() {
		return getValidaSignaturaPlugin() != null;
	}

	public List<ArxiuFirmaDetallDto> validaSignaturaObtenirDetalls(
			byte[] documentContingut,
			byte[] firmaContingut) {
		String accioDescripcio = "Obtenir informació de document firmat";
		Map<String, String> accioParams = new HashMap<String, String>();
		long t0 = System.currentTimeMillis();
		try {
			ValidateSignatureRequest validationRequest = new ValidateSignatureRequest();
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
			List<ArxiuFirmaDetallDto> detalls = new ArrayList<ArxiuFirmaDetallDto>();
			if (validateSignatureResponse.getSignatureDetailInfo() != null) {
				for (SignatureDetailInfo signatureInfo: validateSignatureResponse.getSignatureDetailInfo()) {
					ArxiuFirmaDetallDto detall = new ArxiuFirmaDetallDto();
					signatureInfo.getSignDate();
					TimeStampInfo timeStampInfo = signatureInfo.getTimeStampInfo();
					if (timeStampInfo != null) {
						detall.setData(timeStampInfo.getCreationTime());
					} else {
						detall.setData(signatureInfo.getSignDate());
					}
					CertificateInfo certificateInfo = signatureInfo.getCertificateInfo();
					if (certificateInfo != null) {
						detall.setResponsableNif(certificateInfo.getNifResponsable());
						detall.setResponsableNom(certificateInfo.getNombreApellidosResponsable());
						detall.setEmissorCertificat(certificateInfo.getOrganizacionEmisora());
					}
					detalls.add(detall);
				}
			}
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_VALIDASIG,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return detalls;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de validar signatures";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_VALIDASIG,
					accioDescripcio,
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

	public String gestioDocumentalCreate(
			String agrupacio,
			byte[] contingut) {
		String accioDescripcio = "Creant nou document a dins la gestió documental";
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
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return gestioDocumentalId;
		} catch (Exception ex) {
			String errorDescripcio = "Error al crear document a dins la gestió documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
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
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al esborrar document a dins la gestió documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
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

	public void gestioDocumentalGet(
			String id,
			String agrupacio,
			OutputStream contingutOut) {
		String accioDescripcio = "Consultant document a dins la gestió documental";
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
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al consultar document a dins la gestió documental";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_GESDOC,
					accioDescripcio,
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

	public List<TipusViaDto> dadesExternesTipusViaAll() {
		String accioDescripcio = "Consulta de tipus de via";
		Map<String, String> accioParams = new HashMap<String, String>();
		long t0 = System.currentTimeMillis();
		try {
			List<TipusViaDto> tipusVies = conversioTipusHelper.convertirList(
					getDadesExternesPlugin().tipusViaFindAll(),
					TipusViaDto.class);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0);
			return tipusVies;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
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
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("comunitatCodi", comunitatCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<Provincia> provincies = getDadesExternesPlugin().provinciaFindByComunitat(comunitatCodi);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return provincies;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
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
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("provinciaCodi", provinciaCodi);
		long t0 = System.currentTimeMillis();
		try {
			List<Municipi> municipis = getDadesExternesPlugin().municipiFindByProvincia(provinciaCodi);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
					null,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return municipis;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de dades externes";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_DADESEXT,
					accioDescripcio,
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

	public es.caib.plugins.arxiu.api.Expedient arxiuExpedientInfo(
			String arxiuUuid) {
		String accioDescripcio = "Consulta d'un expedient";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("expedientArxiuUuid", arxiuUuid);
		long t0 = System.currentTimeMillis();
		try {
			es.caib.plugins.arxiu.api.Expedient exp = getArxiuPlugin().expedientDetalls(arxiuUuid, null);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return exp;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_ARXIU,
					accioDescripcio,
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

	public List<Procediment> procedimentFindByCodiDir3(
			String codiDir3) {
		String accioDescripcio = "Consulta dels procediments pel codi DIR3";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codiDir3", codiDir3);
		long t0 = System.currentTimeMillis();
		try {
			List<Procediment> procediments = getProcedimentPlugin().findAmbCodiDir3(codiDir3);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_PROCEDIMENT,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return procediments;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de procediments: " + ex.getMessage();
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_PROCEDIMENT,
					accioDescripcio,
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

	private boolean gestioDocumentalPluginConfiguracioProvada = false;
	private GestioDocumentalPlugin getGestioDocumentalPlugin() {
		if (gestioDocumentalPlugin == null && !gestioDocumentalPluginConfiguracioProvada) {
			gestioDocumentalPluginConfiguracioProvada = true;
			String pluginClass = getPropertyPluginGestioDocumental();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					gestioDocumentalPlugin = (GestioDocumentalPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_GESDOC,
							"Error al crear la instància del plugin de gestió documental",
							ex);
				}
			}
			/*else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_USUARIS,
						"La classe del plugin de gestió documental no està configurada");
			}*/
		}
		return gestioDocumentalPlugin;
	}
	private Long toLongValue(String text) {
		if (text == null || text.isEmpty())
			return null;
		return Long.parseLong(text);
	}

	private DadesUsuariPlugin getDadesUsuariPlugin() {
		if (dadesUsuariPlugin == null) {
			String pluginClass = getPropertyPluginDadesUsuari();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					dadesUsuariPlugin = (DadesUsuariPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_USUARIS,
							"Error al crear la instància del plugin de dades d'usuari",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_USUARIS,
						"No està configurada la classe per al plugin de dades d'usuari");
			}
		}
		return dadesUsuariPlugin;
	}
	private UnitatsOrganitzativesPlugin getUnitatsOrganitzativesPlugin() {
		if (unitatsOrganitzativesPlugin == null) {
			String pluginClass = getPropertyPluginUnitatsOrganitzatives();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					unitatsOrganitzativesPlugin = (UnitatsOrganitzativesPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_UNITATS,
							"Error al crear la instància del plugin d'unitats organitzatives",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_UNITATS,
						"No està configurada la classe per al plugin d'unitats organitzatives");
			}
		}
		return unitatsOrganitzativesPlugin;
	}
	private IArxiuPlugin getArxiuPlugin() {
		if (arxiuPlugin == null) {
			String pluginClass = getPropertyPluginArxiu();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					if (PropertiesHelper.getProperties().isLlegirSystem()) {
						arxiuPlugin = (IArxiuPlugin)clazz.getDeclaredConstructor(
								String.class).newInstance(
								"es.caib.distribucio.");
					} else {
						arxiuPlugin = (IArxiuPlugin)clazz.getDeclaredConstructor(
								String.class,
								Properties.class).newInstance(
								"es.caib.distribucio.",
								PropertiesHelper.getProperties().findAll());
					}
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_ARXIU,
							"Error al crear la instància del plugin d'arxiu digital",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_ARXIU,
						"No està configurada la classe per al plugin d'arxiu digital");
			}
		}
		return arxiuPlugin;
	}
	private DadesExternesPlugin getDadesExternesPlugin() {
		if (dadesExternesPlugin == null) {
			String pluginClass = getPropertyPluginDadesExternes();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					dadesExternesPlugin = (DadesExternesPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_DADESEXT,
							"Error al crear la instància del plugin de consulta de dades externes",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_DADESEXT,
						"No està configurada la classe per al plugin de dades externes");
			}
		}
		return dadesExternesPlugin;
	}
	private IValidateSignaturePlugin getValidaSignaturaPlugin() {
		if (validaSignaturaPlugin == null) {
			String pluginClass = getPropertyPluginValidaSignatura();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					if (PropertiesHelper.getProperties().isLlegirSystem()) {
						validaSignaturaPlugin = (IValidateSignaturePlugin)clazz.getDeclaredConstructor(
								String.class).newInstance(
								"es.caib.distribucio.");
					} else {
						validaSignaturaPlugin = (IValidateSignaturePlugin)clazz.getDeclaredConstructor(
								String.class,
								Properties.class).newInstance(
								"es.caib.distribucio.",
								PropertiesHelper.getProperties().findAll());
					}
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_VALIDASIG,
							"Error al crear la instància del plugin de validació de signatures",
							ex);
				}
			} else {
				return null;
			}
		}
		return validaSignaturaPlugin;
	}
	private ProcedimentPlugin getProcedimentPlugin() {
		if (procedimentPlugin == null) {
			String pluginClass = getPropertyPluginProcediment();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					procedimentPlugin = (ProcedimentPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_PROCEDIMENT,
							"Error al crear la instància del plugin de procediments",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_PROCEDIMENT,
						"No està configurada la classe per al plugin de procediments");
			}
		}
		return procedimentPlugin;
	}
	private DistribucioPlugin getDistribucioPlugin() {
		if (distribucioPlugin == null) {
			String pluginClass = getPropertyPluginDistribucio();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					distribucioPlugin = (DistribucioPlugin)clazz.newInstance();
					distribucioPlugin.configurar(
							new IntegracioManager() {
								public void addAccioOk(
										String integracioCodi,
										String descripcio,
										Map<String, String> parametres,
										long tempsResposta) {
									integracioHelper.addAccioOk(
											integracioCodi,
											descripcio,
											parametres,
											IntegracioAccioTipusEnumDto.ENVIAMENT,
											tempsResposta);
								}
								public void addAccioError(
										String integracioCodi,
										String descripcio,
										Map<String, String> parametres,
										long tempsResposta,
										String errorDescripcio,
										Throwable throwable) {
									integracioHelper.addAccioError(
											integracioCodi,
											descripcio,
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
							GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP,
							GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP);
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_DISTRIBUCIO,
							"Error al crear la instància del plugin de distribucio",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_DISTRIBUCIO,
						"No està configurada la classe per al plugin de distribucio");
			}
		}
		return distribucioPlugin;
	}

	private String getPropertyPluginDadesUsuari() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.plugin.dades.usuari.class");
	}
	private String getPropertyPluginUnitatsOrganitzatives() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.plugin.unitats.organitzatives.class");
	}
	private String getPropertyPluginArxiu() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.plugin.arxiu.class");
	}
	private String getPropertyPluginDadesExternes() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.plugin.dadesext.class");
	}
	private String getPropertyPluginValidaSignatura() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.plugin.validatesignature.class");
	}
	private String getPropertyPluginProcediment() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.plugin.procediment.class");
	}
	private String getPropertyPluginDistribucio() {
		String pluginClass = PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.plugins.distribucio.class");
		if (pluginClass == null) {
			return PropertiesHelper.getProperties().getProperty(
					"es.caib.distribucio.plugins.distribucio.fitxers.class");
		} else {
			return pluginClass;
		}
	}
	private String getPropertyPluginGestioDocumental() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.plugin.gesdoc.class");
	}

}