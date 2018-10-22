/**
 * 
 */
package es.caib.distribucio.core.helper;

import java.io.InputStream;
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
import es.caib.distribucio.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.core.api.dto.TipusViaDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.exception.SistemaExternException;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.ContingutEntity;
import es.caib.distribucio.core.entity.RegistreAnnexEntity;
import es.caib.distribucio.core.entity.RegistreAnnexFirmaEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.plugin.dadesext.DadesExternesPlugin;
import es.caib.distribucio.plugin.dadesext.Municipi;
import es.caib.distribucio.plugin.dadesext.Provincia;
import es.caib.distribucio.plugin.distribucio.DistribucioPlugin;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnnex;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnotacio;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreFirma;
import es.caib.distribucio.plugin.gesdoc.GestioDocumentalPlugin;
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
	private DistribucioPlugin distribucioPlugin;

	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Resource
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;



	public String distribuirContingutAnotacioPendent(RegistreEntity anotacio, BustiaEntity bustia, boolean crearAutofirma) {
		String accioDescripcio = "Distribucio de contingut d'anotació de registre";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("anotacioCodi", anotacio.getIdentificador());
		accioParams.put("bustiaNom", bustia.getNom());
		long t0 = System.currentTimeMillis();
		try {
			DistribucioRegistreAnotacio registreAnotacio = conversioTipusHelper.convertir(anotacio, DistribucioRegistreAnotacio.class);
			String unitatArrelCodi = bustia.getEntitat().getCodiDir3();
			
			String identificadorRetorn = getDistribucioPlugin().contenidorCrear(registreAnotacio, unitatArrelCodi);
			
			getDistribucioPlugin().documentCrear(registreAnotacio, unitatArrelCodi, identificadorRetorn);
			
			anotacio.updateExpedientArxiuUuid(registreAnotacio.getExpedientArxiuUuid());
			if (anotacio.getAnnexos() != null && anotacio.getAnnexos().size() > 0) {
				for (int i = 0; i < anotacio.getAnnexos().size();  i++) {
					RegistreAnnexEntity annex = anotacio.getAnnexos().get(i);
					DistribucioRegistreAnnex distribucioAnnex = registreAnotacio.getAnnexos().get(i);
					annex.updateFitxerArxiuUuid(distribucioAnnex.getFitxerArxiuUuid());
					
					actualitzarTamanyContingut(annex);
					
					if (distribucioAnnex.getFirmes() != null && distribucioAnnex.getFirmes().size() > 0) {
						for (DistribucioRegistreFirma distribucioFirma: distribucioAnnex.getFirmes()) {
							if (distribucioFirma.isAutofirma() && crearAutofirma) {
								RegistreAnnexFirmaEntity novaFirma = new RegistreAnnexFirmaEntity();
								novaFirma.updatePerNovaFirma(
										distribucioFirma.getTipus(), 
										distribucioFirma.getPerfil(), 
										distribucioFirma.getFitxerNom(), 
										distribucioFirma.getTipusMime(), 
										distribucioFirma.getCsvRegulacio(), 
										distribucioFirma.isAutofirma(), 
										distribucioFirma.getGesdocFirmaId(), 
										annex);
								annex.getFirmes().add(novaFirma);
							}
						}
					}
				}
			}
			
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_DISTRIBUCIO,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return identificadorRetorn;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de distribucio";
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
//	public boolean arxiuSuportaVersionsExpedients() {
//		return getArxiuPlugin().suportaVersionatExpedient();
//	}
//	public boolean arxiuSuportaVersionsDocuments() {
//		return getArxiuPlugin().suportaVersionatDocument();
//	}
//	public boolean arxiuSuportaMetadades() {
//		return getArxiuPlugin().suportaMetadadesNti();
//	}
//
//	public ContingutArxiu arxiuExpedientPerAnotacioCrear(
//			RegistreEntity anotacio, 
//			BustiaEntity bustia) {
//		String accioDescripcio = "Creant un expedient temporal per anotacio de registre rebuda";
//		String nomExpedient = "EXP_REG_" + anotacio.getExpedientNumero() + "_" + System.currentTimeMillis();
//		Map<String, String> accioParams = new HashMap<String, String>();
//		accioParams.put("nom", nomExpedient);
//		accioParams.put("entitatId", bustia.getEntitat().getId().toString());
//		accioParams.put("entitatCodi", bustia.getEntitat().getCodi());
//		accioParams.put("entitatNom", bustia.getEntitat().getNom());
//		long t0 = System.currentTimeMillis();
//		try {
//			ContingutArxiu expedientCreat = getArxiuPlugin().expedientCrear(
//					toArxiuExpedient(
//							null,
//							nomExpedient,
//							null,
//							Arrays.asList(bustia.getEntitat().getUnitatArrel()),
//							new Date(),
//							getPropertyPluginRegistreExpedientClassificacio(),
//							ExpedientEstatEnumDto.OBERT,
//							null,
//							getPropertyPluginRegistreExpedientSerieDocumental()));
//			integracioHelper.addAccioOk(
//					IntegracioHelper.INTCODI_ARXIU,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0);
//			
//			return expedientCreat;
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_ARXIU,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_ARXIU,
//					errorDescripcio,
//					ex);
//		}
//	}
//
//	public Expedient arxiuExpedientConsultarPerUuid(
//			String uuid) {
//		String accioDescripcio = "Consulta d'un expedient per uuid";
//		Map<String, String> accioParams = new HashMap<String, String>();
//		long t0 = System.currentTimeMillis();
//		try {
//			Expedient arxiuExpedient = getArxiuPlugin().expedientDetalls(
//					uuid, 
//					null);
//			integracioHelper.addAccioOk(
//					IntegracioHelper.INTCODI_ARXIU,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0);
//			return arxiuExpedient;
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_ARXIU,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_ARXIU,
//					errorDescripcio,
//					ex);
//		}
//	}
//	
	public void eliminarContingutExistent(
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

	public void marcarAnotacioComProcessada(
			RegistreEntity registre) {
		String accioDescripcio = "Tancament d'un expedient temporal relacionada amb una anotació de registre";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("expedientArxiuUuid", registre.getExpedientArxiuUuid());
		accioParams.put("expedientNumero", registre.getExpedientNumero());
		accioParams.put("registreNom", registre.getNom());
		accioParams.put("registreNumero", registre.getNumero());
		accioParams.put("registreEntitat", registre.getEntitatCodi());
		accioParams.put("registreUnitatAdmin", registre.getUnitatAdministrativa());
		long t0 = System.currentTimeMillis();
		try {
			
			DistribucioRegistreAnotacio anotacio = conversioTipusHelper.convertir(registre, DistribucioRegistreAnotacio.class);
			getDistribucioPlugin().marcarProcessat(anotacio);
			
			registre.updateArxiuEsborrat();
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
//
//	public void arxiuDocumentActualitzar(
//			DocumentEntity document,
//			FitxerDto fitxer,
//			ContingutEntity contingutPare,
//			String classificacioDocumental) {
//		String accioDescripcio = "Actualització de les dades d'un document";
//		Map<String, String> accioParams = new HashMap<String, String>();
//		accioParams.put("id", document.getId().toString());
//		accioParams.put("títol", document.getNom());
//		accioParams.put("contingutPareId", contingutPare.getId().toString());
//		accioParams.put("contingutPareNom", contingutPare.getNom());
//		accioParams.put("classificacioDocumental", classificacioDocumental);
//		long t0 = System.currentTimeMillis();
//		try {
//			if (document.getArxiuUuid() == null) {
//				ContingutArxiu documentCreat = getArxiuPlugin().documentCrear(
//						toArxiuDocument(
//								null,
//								document.getNom(),
//								fitxer,
//								null,
//								null,
//								null,
//								document.getNtiOrigen(),
//								Arrays.asList(document.getNtiOrgano()),
//								document.getDataCaptura(),
//								document.getNtiEstadoElaboracion(),
//								document.getNtiTipoDocumental(),
//								DocumentEstat.ESBORRANY,
//								DocumentTipusEnumDto.FISIC.equals(document.getDocumentTipus())),
//						contingutPare.getArxiuUuid());
//				if (getArxiuPlugin().suportaMetadadesNti()) {
//					Document documentDetalls = getArxiuPlugin().documentDetalls(
//							documentCreat.getIdentificador(),
//							null,
//							false);
//					propagarMetadadesDocument(
//							documentDetalls,
//							document);
//				}
//				document.updateArxiu(
//						documentCreat.getIdentificador());
//			} else {
//				getArxiuPlugin().documentModificar(
//						toArxiuDocument(
//								document.getArxiuUuid(),
//								document.getNom(),
//								fitxer,
//								null,
//								null,
//								null,
//								document.getNtiOrigen(),
//								Arrays.asList(document.getNtiOrgano()),
//								document.getDataCaptura(),
//								document.getNtiEstadoElaboracion(),
//								document.getNtiTipoDocumental(),
//								DocumentEstat.ESBORRANY,
//								DocumentTipusEnumDto.FISIC.equals(document.getDocumentTipus())));
//				document.updateArxiu(null);
//			}
//			integracioHelper.addAccioOk(
//					IntegracioHelper.INTCODI_ARXIU,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0);
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_ARXIU,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_ARXIU,
//					errorDescripcio,
//					ex);
//		}
//	}
//
//	public String arxiuDocumentAnnexCrear(
//			RegistreAnnexEntity annex,
//			BustiaEntity bustia,
//			FitxerDto fitxer,
//			List<ArxiuFirmaDto> firmes,
//			ContingutArxiu expedient) {
//		String accioDescripcio = "Actualització de les dades d'un document";
//		Map<String, String> accioParams = new HashMap<String, String>();
//		accioParams.put("títol", annex.getTitol());
//		accioParams.put("contingutPareId", expedient.getIdentificador());
//		accioParams.put("contingutPareNom", expedient.getNom());
//		long t0 = System.currentTimeMillis();
//		DocumentEstat estatDocument = DocumentEstat.ESBORRANY;
//		if (annex.getFirmes() != null && !annex.getFirmes().isEmpty()) {
//			estatDocument = DocumentEstat.DEFINITIU;
//		}
//		try {
//			ContingutArxiu contingutFitxer = getArxiuPlugin().documentCrear(
//					toArxiuDocument(
//							null,
//							annex.getTitol(),
//							fitxer,
//							null,
//							firmes,
//							null,
//							(annex.getOrigenCiutadaAdmin() != null ? NtiOrigenEnumDto.values()[Integer.valueOf(annex.getOrigenCiutadaAdmin().getValor())] : null),
//							Arrays.asList(bustia.getEntitat().getUnitatArrel()),
//							annex.getDataCaptura(),
//							(annex.getNtiElaboracioEstat() != null ? DocumentNtiEstadoElaboracionEnumDto.valueOf(annex.getNtiElaboracioEstat().getValor()) : null),
//							(annex.getNtiTipusDocument() != null ? DocumentNtiTipoDocumentalEnumDto.valueOf(annex.getNtiTipusDocument().getValor()) : null),
//							estatDocument,
//							false),
//					expedient.getIdentificador());
//			integracioHelper.addAccioOk(
//					IntegracioHelper.INTCODI_ARXIU,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0);
//			return contingutFitxer.getIdentificador();
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_ARXIU,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_ARXIU,
//					errorDescripcio,
//					ex);
//		}
//	}
//
	public Document arxiuDocumentConsultar(
			ContingutEntity contingut,
			String nodeId,
			String versio,
			boolean ambContingut) {
		return arxiuDocumentConsultar(
				contingut,
				nodeId,
				versio,
				ambContingut,
				false);
	}
	public Document arxiuDocumentConsultar(
			ContingutEntity contingut,
			String nodeId,
			String versio,
			boolean ambContingut,
			boolean ambVersioImprimible) {
		String accioDescripcio = "Consulta d'un document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("contingutId", contingut.getId().toString());
		accioParams.put("contingutNom", contingut.getNom());
		accioParams.put("nodeId", nodeId);
		String arxiuUuid = nodeId;
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
//
//	public void arxiuDocumentEsborrar(
//			DocumentEntity document) {
//		String accioDescripcio = "Eliminació d'un document";
//		Map<String, String> accioParams = new HashMap<String, String>();
//		accioParams.put("id", document.getId().toString());
//		accioParams.put("títol", document.getNom());
//		long t0 = System.currentTimeMillis();
//		try {
//			getArxiuPlugin().documentEsborrar(
//					document.getArxiuUuid());
//			document.updateArxiuEsborrat();
//			integracioHelper.addAccioOk(
//					IntegracioHelper.INTCODI_ARXIU,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0);
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_ARXIU,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_ARXIU,
//					errorDescripcio,
//					ex);
//		}
//	}
//
//	public boolean arxiuDocumentExtensioPermesa(String extensio) {
//		return getArxiuFormatExtensio(extensio) != null;
//	}
//
//	public List<ContingutArxiu> arxiuDocumentObtenirVersions(
//			DocumentEntity document) {
//		String accioDescripcio = "Obtenir versions del document";
//		Map<String, String> accioParams = new HashMap<String, String>();
//		accioParams.put("id", document.getId().toString());
//		accioParams.put("títol", document.getNom());
//		long t0 = System.currentTimeMillis();
//		try {
//			List<ContingutArxiu> versions = getArxiuPlugin().documentVersions(
//					document.getArxiuUuid());
//			integracioHelper.addAccioOk(
//					IntegracioHelper.INTCODI_ARXIU,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0);
//			return versions;
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_ARXIU,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_ARXIU,
//					errorDescripcio,
//					ex);
//		}
//	}
//
//	public String arxiuDocumentGuardarPdfFirmat(
//			DocumentEntity document,
//			FitxerDto fitxerPdfFirmat) {
//		// El paràmetre custodiaTipus es reb sempre com a paràmetre però només te
//		// sentit quan s'empra el plugin d'arxiu que accedeix a valcert.
//		String accioDescripcio = "Guardar PDF firmat com a document definitiu";
//		Map<String, String> accioParams = new HashMap<String, String>();
//		accioParams.put("id", document.getId().toString());
//		accioParams.put("títol", document.getNom());
//		accioParams.put("fitxerPdfFirmatNom", fitxerPdfFirmat.getNom());
//		accioParams.put("fitxerPdfFirmatTamany", new Long(fitxerPdfFirmat.getTamany()).toString());
//		accioParams.put("fitxerPdfFirmatContentType", fitxerPdfFirmat.getContentType());
//		long t0 = System.currentTimeMillis();
//		try {
//			getArxiuPlugin().documentModificar(
//					toArxiuDocument(
//							document.getArxiuUuid(),
//							document.getNom(),
//							null,
//							fitxerPdfFirmat,
//							null,
//							null,
//							document.getNtiOrigen(),
//							Arrays.asList(document.getNtiOrgano()),
//							document.getDataCaptura(),
//							document.getNtiEstadoElaboracion(),
//							document.getNtiTipoDocumental(),
//							DocumentEstat.DEFINITIU,
//							DocumentTipusEnumDto.FISIC.equals(document.getDocumentTipus())));
//			integracioHelper.addAccioOk(
//					IntegracioHelper.INTCODI_ARXIU,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0);
//			document.updateEstat(
//					DocumentEstatEnumDto.CUSTODIAT);
//			return document.getId().toString();
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_ARXIU,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_ARXIU,
//					errorDescripcio,
//					ex);
//		}
//	}
//
//	public FitxerDto arxiuDocumentVersioImprimible(
//			DocumentEntity document) {
//		String accioDescripcio = "Obtenir versió imprimible del document";
//		Map<String, String> accioParams = new HashMap<String, String>();
//		accioParams.put("id", document.getId().toString());
//		accioParams.put("títol", document.getNom());
//		long t0 = System.currentTimeMillis();
//		try {
//			DocumentContingut documentContingut = getArxiuPlugin().documentImprimible(
//					document.getArxiuUuid());
//			FitxerDto fitxer = new FitxerDto();
//			fitxer.setNom(documentContingut.getArxiuNom());
//			fitxer.setContentType(documentContingut.getTipusMime());
//			fitxer.setTamany(documentContingut.getTamany());
//			fitxer.setContingut(documentContingut.getContingut());
//			integracioHelper.addAccioOk(
//					IntegracioHelper.INTCODI_ARXIU,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0);
//			return fitxer;
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_ARXIU,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_ARXIU,
//					errorDescripcio,
//					ex);
//		}
//	}
//
//	public String portafirmesUpload(
//			DocumentEntity document,
//			String motiu,
//			PortafirmesPrioritatEnum prioritat,
//			Date dataCaducitat,
//			String documentTipus,
//			String[] responsables,
//			MetaDocumentFirmaFluxTipusEnumDto fluxTipus,
//			String fluxId,
//			List<DocumentEntity> annexos) {
//		String accioDescripcio = "Enviament de document a firmar";
//		Map<String, String> accioParams = new HashMap<String, String>();
//		accioParams.put(
//				"documentId",
//				document.getId().toString());
//		accioParams.put(
//				"documentTitol",
//				document.getNom());
//		accioParams.put("motiu", motiu);
//		accioParams.put("prioritat", prioritat.toString());
//		accioParams.put(
//				"dataCaducitat",
//				new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dataCaducitat));
//		long t0 = System.currentTimeMillis();
//		if (annexos != null) {
//			StringBuilder annexosIds = new StringBuilder();
//			StringBuilder annexosTitols = new StringBuilder();
//			boolean primer = true;
//			for (DocumentEntity annex: annexos) {
//				if (!primer) {
//					annexosIds.append(", ");
//					annexosTitols.append(", ");
//				}
//				annexosIds.append(annex.getId());
//				annexosTitols.append(annex.getNom());
//				primer = false;
//			}
//			accioParams.put("annexosIds", annexosIds.toString());
//			accioParams.put("annexosTitols", annexosTitols.toString());
//		}
//		PortafirmesDocument portafirmesDocument = new PortafirmesDocument();
//		portafirmesDocument.setTitol(document.getNom());
//		portafirmesDocument.setFirmat(
//				false);
//		/*String urlCustodia = null;
//		if (portafirmesEnviarDocumentEstampat()) {
//			urlCustodia = arxiuDocumentGenerarUrlPerDocument(document);
//		}
//		FitxerDto fitxerOriginal = documentHelper.getFitxerAssociat(document);
//		FitxerDto fitxerConvertit = conversioConvertirPdfIEstamparUrl(
//				fitxerOriginal,
//				urlCustodia);*/
//		FitxerDto fitxerOriginal = documentHelper.getFitxerAssociat(document);
//		FitxerDto fitxerConvertit = this.conversioConvertirPdf(
//				fitxerOriginal,
//				null);
//		portafirmesDocument.setArxiuNom(
//				fitxerConvertit.getNom());
//		portafirmesDocument.setArxiuContingut(
//				fitxerConvertit.getContingut());
//		List<PortafirmesFluxBloc> flux = new ArrayList<PortafirmesFluxBloc>();
//		if (MetaDocumentFirmaFluxTipusEnumDto.SERIE.equals(fluxTipus)) {
//			for (String responsable: responsables) {
//				PortafirmesFluxBloc bloc = new PortafirmesFluxBloc();
//				bloc.setMinSignataris(1);
//				bloc.setDestinataris(new String[] {responsable});
//				bloc.setObligatorietats(new boolean[] {true});
//				flux.add(bloc);
//			}
//		} else if (MetaDocumentFirmaFluxTipusEnumDto.PARALEL.equals(fluxTipus)) {
//			PortafirmesFluxBloc bloc = new PortafirmesFluxBloc();
//			bloc.setMinSignataris(responsables.length);
//			bloc.setDestinataris(responsables);
//			boolean[] obligatorietats = new boolean[responsables.length];
//			Arrays.fill(obligatorietats, true);
//			bloc.setObligatorietats(obligatorietats);
//			flux.add(bloc);
//		}
//		try {
//			Calendar dataCaducitatCal = Calendar.getInstance();
//			dataCaducitatCal.setTime(dataCaducitat);
//			if (	dataCaducitatCal.get(Calendar.HOUR_OF_DAY) == 0 &&
//					dataCaducitatCal.get(Calendar.MINUTE) == 0 &&
//					dataCaducitatCal.get(Calendar.SECOND) == 0 &&
//					dataCaducitatCal.get(Calendar.MILLISECOND) == 0) {
//				dataCaducitatCal.set(Calendar.HOUR_OF_DAY, 23);
//				dataCaducitatCal.set(Calendar.MINUTE, 59);
//				dataCaducitatCal.set(Calendar.SECOND, 59);
//				dataCaducitatCal.set(Calendar.MILLISECOND, 999);
//			}
//			String portafirmesEnviamentId = getPortafirmesPlugin().upload(
//					portafirmesDocument,
//					documentTipus,
//					motiu,
//					"Aplicació DISTRIBUCIO",
//					prioritat,
//					dataCaducitatCal.getTime(),
//					flux,
//					fluxId,
//					null,
//					false);
//			integracioHelper.addAccioOk(
//					IntegracioHelper.INTCODI_PFIRMA,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0);
//			return portafirmesEnviamentId;
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin de portafirmes";
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_PFIRMA,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_PFIRMA,
//					errorDescripcio,
//					ex);
//		}
//	}
//
//	public PortafirmesDocument portafirmesDownload(
//			DocumentPortafirmesEntity documentPortafirmes) {
//		String accioDescripcio = "Descarregar document firmat";
//		Map<String, String> accioParams = new HashMap<String, String>();
//		DocumentEntity document = documentPortafirmes.getDocument();
//		accioParams.put(
//				"documentVersioId",
//				document.getId().toString());
//		accioParams.put(
//				"documentPortafirmesId",
//				documentPortafirmes.getId().toString());
//		accioParams.put(
//				"portafirmesId",
//				new Long(documentPortafirmes.getPortafirmesId()).toString());
//		long t0 = System.currentTimeMillis();
//		PortafirmesDocument portafirmesDocument = null;
//		try {
//			portafirmesDocument = getPortafirmesPlugin().download(
//					documentPortafirmes.getPortafirmesId());
//			integracioHelper.addAccioOk(
//					IntegracioHelper.INTCODI_PFIRMA,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0);
//			return portafirmesDocument;
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al descarregar el document firmat";
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_PFIRMA,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_PFIRMA,
//					errorDescripcio,
//					ex);
//		}
//	}
//
//	public void portafirmesDelete(
//			DocumentPortafirmesEntity documentPortafirmes) {
//		String accioDescripcio = "Esborrar document enviat a firmar";
//		Map<String, String> accioParams = new HashMap<String, String>();
//		DocumentEntity document = documentPortafirmes.getDocument();
//		accioParams.put(
//				"documentId",
//				document.getId().toString());
//		accioParams.put(
//				"documentPortafirmesId",
//				documentPortafirmes.getId().toString());
//		accioParams.put(
//				"portafirmesId",
//				new Long(documentPortafirmes.getPortafirmesId()).toString());
//		long t0 = System.currentTimeMillis();
//		try {
//			getPortafirmesPlugin().delete(
//					documentPortafirmes.getPortafirmesId());
//			integracioHelper.addAccioOk(
//					IntegracioHelper.INTCODI_PFIRMA,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0);
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin de portafirmes";
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_PFIRMA,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_PFIRMA,
//					errorDescripcio,
//					ex);
//		}
//	}
//
//	public List<PortafirmesDocumentTipusDto> portafirmesFindDocumentTipus() {
//		String accioDescripcio = "Consulta de tipus de document";
//		long t0 = System.currentTimeMillis();
//		try {
//			List<PortafirmesDocumentTipus> tipus = getPortafirmesPlugin().findDocumentTipus();
//			integracioHelper.addAccioOk(
//					IntegracioHelper.INTCODI_PFIRMA,
//					accioDescripcio,
//					null,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0);
//			if (tipus != null) {
//				List<PortafirmesDocumentTipusDto> resposta = new ArrayList<PortafirmesDocumentTipusDto>();
//				for (PortafirmesDocumentTipus t: tipus) {
//					PortafirmesDocumentTipusDto dto = new PortafirmesDocumentTipusDto();
//					dto.setId(t.getId());
//					dto.setCodi(t.getCodi());
//					dto.setNom(t.getNom());
//					resposta.add(dto);
//				}
//				return resposta;
//			} else {
//				return null;
//			}
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin de portafirmes";
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_PFIRMA,
//					accioDescripcio,
//					null,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_PFIRMA,
//					errorDescripcio,
//					ex);
//		}
//	}
//
//	public boolean portafirmesEnviarDocumentEstampat() {
//		return !getPortafirmesPlugin().isCustodiaAutomatica();
//	}
//
//	public String conversioConvertirPdfArxiuNom(
//			String nomOriginal) {
//		return getConversioPlugin().getNomArxiuConvertitPdf(nomOriginal);
//	}
//
//	public FitxerDto conversioConvertirPdf(
//			FitxerDto original,
//			String urlPerEstampar) {
//		String accioDescripcio = "Conversió de document a PDF";
//		Map<String, String> accioParams = new HashMap<String, String>();
//		accioParams.put("arxiuOriginalNom", original.getNom());
//		accioParams.put("arxiuOriginalTamany", new Integer(original.getContingut().length).toString());
//		long t0 = System.currentTimeMillis();
//		try {
//			ConversioArxiu convertit = getConversioPlugin().convertirPdfIEstamparUrl(
//					new ConversioArxiu(
//							original.getNom(),
//							original.getContingut()),
//					urlPerEstampar);
//			accioParams.put("arxiuConvertitNom", convertit.getArxiuNom());
//			accioParams.put("arxiuConvertitTamany", new Integer(convertit.getArxiuContingut().length).toString());
//			integracioHelper.addAccioOk(
//					IntegracioHelper.INTCODI_CONVERT,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0);
//			FitxerDto resposta = new FitxerDto();
//			resposta.setNom(
//					convertit.getArxiuNom());
//			resposta.setContingut(
//					convertit.getArxiuContingut());
//			return resposta;
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin de conversió de documents";
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_CONVERT,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_CONVERT,
//					errorDescripcio,
//					ex);
//		}
//	}

//
//	/*public CiutadaExpedientInformacio ciutadaExpedientCrear(
//			ExpedientEntity expedient,
//			InteressatEntity destinatari) {
//		MetaExpedientEntity metaExpedient = expedient.getMetaExpedient();
//		String accioDescripcio = "Creació d'un expedient a la zona personal del ciutadà";
//		Map<String, String> accioParams = new HashMap<String, String>();
//		accioParams.put("expedientId", expedient.getId().toString());
//		accioParams.put("expedientNumero", expedient.getNumero());
//		accioParams.put("expedientTitol", expedient.getNom());
//		accioParams.put("expedientTipusId", expedient.getMetaNode().getId().toString());
//		accioParams.put("expedientTipusNom", expedient.getMetaNode().getNom());
//		accioParams.put("unitatAdministrativa", metaExpedient.getUnitatAdministrativa());
//		String idioma = getIdiomaPerPluginCiutada(destinatari.getPreferenciaIdioma());
//		accioParams.put("idioma", idioma);
//		accioParams.put("destinatari", destinatari.getIdentificador());
//		long t0 = System.currentTimeMillis();
//		try {
//			String descripcio = "[" + expedient.getNumero() + "] " + expedient.getNom();
//			String interessatMobil = null;
//			if (destinatari.getTelefon() != null && isTelefonMobil(destinatari.getTelefon())) {
//				interessatMobil = destinatari.getTelefon();
//			}
//			CiutadaExpedientInformacio expedientInfo = getCiutadaPlugin().expedientCrear(
//					expedient.getNtiIdentificador(),
//					metaExpedient.getUnitatAdministrativa(),
//					metaExpedient.getClassificacioDocumental(),
//					idioma,
//					descripcio,
//					toPluginCiutadaPersona(destinatari),
//					null,
//					expedient.getSistraBantelNum(),
//					destinatari.isNotificacioAutoritzat(),
//					destinatari.getEmail(),
//					interessatMobil);
//			integracioHelper.addAccioOk(
//					IntegracioHelper.INTCODI_CIUTADA,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0);
//			return expedientInfo;
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin de comunicació amb el ciutadà";
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_CIUTADA,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_CIUTADA,
//					errorDescripcio,
//					ex);
//		}
//	}
//
//	public void ciutadaAvisCrear(
//			ExpedientEntity expedient,
//			String titol,
//			String text,
//			String textMobil) {
//		MetaExpedientEntity metaExpedient = expedient.getMetaExpedient();
//		String accioDescripcio = "Creació d'un avis a la zona personal del ciutadà";
//		Map<String, String> accioParams = new HashMap<String, String>();
//		accioParams.put("expedientId", expedient.getId().toString());
//		accioParams.put("expedientNumero", expedient.getNumero());
//		accioParams.put("expedientTitol", expedient.getNom());
//		accioParams.put("expedientTipusId", expedient.getMetaNode().getId().toString());
//		accioParams.put("expedientTipusNom", expedient.getMetaNode().getNom());
//		accioParams.put("titol", titol);
//		accioParams.put("text", text);
//		accioParams.put("textMobil", textMobil);
//		long t0 = System.currentTimeMillis();
//		try {
//			getCiutadaPlugin().avisCrear(
//					expedient.getNtiIdentificador(),
//					metaExpedient.getUnitatAdministrativa(),
//					titol,
//					text,
//					textMobil,
//					null);
//			integracioHelper.addAccioOk(
//					IntegracioHelper.INTCODI_CIUTADA,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0);
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin de comunicació amb el ciutadà";
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_CIUTADA,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_CIUTADA,
//					errorDescripcio,
//					ex);
//		}
//	}
//
//	public CiutadaNotificacioResultat ciutadaNotificacioEnviar(
//			ExpedientEntity expedient,
//			InteressatEntity destinatari,
//			String oficiTitol,
//			String oficiText,
//			String avisTitol,
//			String avisText,
//			String avisTextMobil,
//			InteressatIdiomaEnumDto idioma,
//			boolean confirmarRecepcio,
//			List<DocumentEntity> annexos) {
//		MetaExpedientEntity metaExpedient = expedient.getMetaExpedient();
//		String accioDescripcio = "Enviament d'una notificació electrònica al ciutadà";
//		Map<String, String> accioParams = new HashMap<String, String>();
//		accioParams.put("expedientId", expedient.getId().toString());
//		accioParams.put("expedientNumero", expedient.getNumero());
//		accioParams.put("expedientTitol", expedient.getNom());
//		accioParams.put("expedientTipusId", expedient.getMetaNode().getId().toString());
//		accioParams.put("expedientTipusNom", expedient.getMetaNode().getNom());
//		accioParams.put("unitatAdministrativa", metaExpedient.getUnitatAdministrativa());
//		accioParams.put("llibreCodi", metaExpedient.getNotificacioLlibreCodi());
//		accioParams.put("organCodi", metaExpedient.getNotificacioOrganCodi());
//		accioParams.put("destinatari", (destinatari != null) ? destinatari.getIdentificador() : "<null>");
//		accioParams.put("idioma", idioma.name());
//		accioParams.put("oficiTitol", oficiTitol);
//		accioParams.put("avisTitol", avisTitol);
//		accioParams.put("confirmarRecepcio", new Boolean(confirmarRecepcio).toString());
//		if (annexos != null)
//			accioParams.put("annexos (núm.)", new Integer(annexos.size()).toString());
//		if (annexos != null) {
//			StringBuilder annexosIds = new StringBuilder();
//			StringBuilder annexosTitols = new StringBuilder();
//			boolean primer = true;
//			for (DocumentEntity annex: annexos) {
//				if (!primer) {
//					annexosIds.append(", ");
//					annexosTitols.append(", ");
//				}
//				annexosIds.append(annex.getId());
//				annexosTitols.append(annex.getNom());
//				primer = false;
//			}
//			accioParams.put("annexosIds", annexosIds.toString());
//			accioParams.put("annexosTitols", annexosTitols.toString());
//		}
//		long t0 = System.currentTimeMillis();
//		try {
//			List<CiutadaDocument> ciutadaAnnexos = null;
//			if (annexos != null) {
//				ciutadaAnnexos = new ArrayList<CiutadaDocument>();
//				for (DocumentEntity annex: annexos) {
//					if (DocumentTipusEnumDto.FISIC.equals(annex.getDocumentTipus())) {
//						throw new ValidationException(
//								annex.getId(),
//								DocumentEntity.class,
//								"No espoden emprar documents físics com annexos d'una notificació telemàtica");
//					}
//					CiutadaDocument cdoc = new CiutadaDocument();
//					cdoc.setTitol(annex.getNom());
//					FitxerDto fitxer = documentHelper.getFitxerAssociat(annex);
//					cdoc.setArxiuNom(fitxer.getNom());
//					cdoc.setArxiuContingut(fitxer.getContingut());
//					ciutadaAnnexos.add(cdoc);
//				}
//			}
//			CiutadaNotificacioResultat resultat = getCiutadaPlugin().notificacioCrear(
//					expedient.getNtiIdentificador(),
//					expedient.getSistraUnitatAdministrativa(),
//					metaExpedient.getNotificacioLlibreCodi(),
//					metaExpedient.getNotificacioOrganCodi(),
//					toPluginCiutadaPersona(destinatari),
//					null,
//					getIdiomaPerPluginCiutada(idioma),
//					oficiTitol,
//					oficiText,
//					avisTitol,
//					avisText,
//					avisTextMobil,
//					confirmarRecepcio,
//					ciutadaAnnexos);
//			integracioHelper.addAccioOk(
//					IntegracioHelper.INTCODI_CIUTADA,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0);
//			return resultat;
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin de comunicació amb el ciutadà";
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_CIUTADA,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_CIUTADA,
//					errorDescripcio,
//					ex);
//		}
//	}
//
//	public CiutadaNotificacioEstat ciutadaNotificacioComprovarEstat(
//			ExpedientEntity expedient,
//			String registreNumero) {
//		String accioDescripcio = "Comprovació de l'estat de la notificació";
//		Map<String, String> accioParams = new HashMap<String, String>();
//		accioParams.put("expedientId", expedient.getId().toString());
//		accioParams.put("expedientNumero", expedient.getNumero());
//		accioParams.put("expedientTitol", expedient.getNom());
//		accioParams.put("expedientTipusId", expedient.getMetaNode().getId().toString());
//		accioParams.put("expedientTipusNom", expedient.getMetaNode().getNom());
//		accioParams.put("registreNumero", registreNumero);
//		long t0 = System.currentTimeMillis();
//		try {
//			CiutadaNotificacioEstat justificant = getCiutadaPlugin().notificacioObtenirJustificantRecepcio(
//					registreNumero);
//			integracioHelper.addAccioOk(
//					IntegracioHelper.INTCODI_CIUTADA,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0);
//			return justificant;
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin de comunicació amb el ciutadà";
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_CIUTADA,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_CIUTADA,
//					errorDescripcio,
//					ex);
//		}
//	}*/
//
//	public boolean isRegistreSignarAnnexos() {
//		return this.getPropertyPluginRegistreSignarAnnexos();
//	}
//
//	public byte[] signaturaDistribucioSignar(
//			RegistreAnnexEntity annex,
//			byte[] annexContingut) {
//		String accioDescripcio = "Signatura del document des del servidor";
//		Map<String, String> accioParams = new HashMap<String, String>();
//		accioParams.put(
//				"annexId",
//				annex.getId().toString());
//		accioParams.put(
//				"annexNom",
//				annex.getFitxerNom());
//		long t0 = System.currentTimeMillis();
//		try {
//			String motiu = "Autofirma en servidor de DISTRIBUCIO";
//			String tipusFirma;
//			if ("application/pdf".equalsIgnoreCase(annex.getFitxerTipusMime()))
//				tipusFirma = "PADES";
//			else
//				tipusFirma = "CADES";
//			
//			byte[] firmaContingut = getSignaturaPlugin().signar(
//					annex.getId().toString(),
//					annex.getFitxerNom(),
//					motiu,
//					tipusFirma,
//					annexContingut);
//			integracioHelper.addAccioOk(
//					IntegracioHelper.INTCODI_SIGNATURA,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0);
//			return firmaContingut;
//		} catch (Exception ex) {
//			String errorDescripcio = "Error en accedir al plugin de signatura";
//			integracioHelper.addAccioError(
//					IntegracioHelper.INTCODI_SIGNATURA,
//					accioDescripcio,
//					accioParams,
//					IntegracioAccioTipusEnumDto.ENVIAMENT,
//					System.currentTimeMillis() - t0,
//					errorDescripcio,
//					ex);
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_SIGNATURA,
//					errorDescripcio,
//					ex);
//		}
//	}
//
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
			sri.setReturnTimeStampInfo(false);
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
			InputStream contingut) {
		try {
			String gestioDocumentalId = null;
			if (getGestioDocumentalPlugin() != null) {
				gestioDocumentalId = getGestioDocumentalPlugin().create(
						agrupacio,
							contingut);
			}
			return gestioDocumentalId;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de gestió documental";
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}
//	
//	public void gestioDocumentalUpdate(
//			String id,
//			String agrupacio,
//			InputStream contingut) {
//		try {
//			getGestioDocumentalPlugin().update(
//					id,
//					agrupacio,
//					contingut);
//		} catch (Exception ex) {
//			String errorDescripcio = "Error al accedir al plugin de gestió documental";
//			throw new SistemaExternException(
//					IntegracioHelper.INTCODI_GESDOC,
//					errorDescripcio,
//					ex);
//		}
//	}
	public void gestioDocumentalDelete(
			String id,
			String agrupacio) {
		try {
			if (getGestioDocumentalPlugin() != null) {
				getGestioDocumentalPlugin().delete(
						id,
						agrupacio);
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de gestió documental";
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
		try {
			if (getGestioDocumentalPlugin() != null) {
				getGestioDocumentalPlugin().get(
						id,
						agrupacio,
						contingutOut);
			}
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de gestió documental";
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
	
	private void actualitzarTamanyContingut(RegistreAnnexEntity annex) {
		if (annex.getFitxerTamany() <= 0) {
			Document document = this.arxiuDocumentConsultar(
					annex.getRegistre(), 
					annex.getFitxerArxiuUuid(), 
					null, 
					true);
			if (document.getContingut() != null)
				annex.updateFitxerTamany((int)document.getContingut().getTamany());
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
							IntegracioHelper.INTCODI_CIUTADA,
							"Error al crear la instància del plugin de consulta de dades externes",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_CIUTADA,
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
	private DistribucioPlugin getDistribucioPlugin() {
		if (distribucioPlugin == null) {
			String pluginClass = getPropertyPluginDistribucio();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					distribucioPlugin = (DistribucioPlugin)clazz.newInstance();
				} catch (Exception ex) {
					throw new SistemaExternException(
							IntegracioHelper.INTCODI_NOTIFICACIO,
							"Error al crear la instància del plugin de distribucio",
							ex);
				}
			} else {
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_NOTIFICACIO,
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
	private String getPropertyPluginDistribucio() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.plugins.distribucio.fitxers.class");
	}
	private String getPropertyPluginGestioDocumental() {
		return PropertiesHelper.getProperties().getProperty("es.caib.distribucio.plugin.gesdoc.class");
	}
}