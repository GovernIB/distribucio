/**
 * 
 */
package es.caib.distribucio.logic.helper;

import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.distribucio.logic.helper.plugin.AbstractPluginHelper;
import es.caib.distribucio.logic.helper.plugin.ArxiuPluginHelper;
import es.caib.distribucio.logic.helper.plugin.DadesExternesPluginHelper;
import es.caib.distribucio.logic.helper.plugin.DadesUsuarisPluginHelper;
import es.caib.distribucio.logic.helper.plugin.DistribucioPluginHelper;
import es.caib.distribucio.logic.helper.plugin.GestioDocumentalPluginHelper;
import es.caib.distribucio.logic.helper.plugin.ProcedimentPluginHelper;
import es.caib.distribucio.logic.helper.plugin.ServeiPluginHelper;
import es.caib.distribucio.logic.helper.plugin.SignaturaPluginHelper;
import es.caib.distribucio.logic.helper.plugin.UnitatsOrganitzativesPluginHelper;
import es.caib.distribucio.logic.helper.plugin.ValidaSignaturaAgilPluginHelper;
import es.caib.distribucio.logic.helper.plugin.ValidaSignaturaPluginHelper;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaDetallDto;
import es.caib.distribucio.logic.intf.dto.DocumentEniRegistrableDto;
import es.caib.distribucio.logic.intf.dto.FitxerDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentDto;
import es.caib.distribucio.logic.intf.dto.TipusViaDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.persist.entity.RegistreAnnexEntity;
import es.caib.distribucio.persist.entity.RegistreEntity;
import es.caib.distribucio.plugin.dadesext.Municipi;
import es.caib.distribucio.plugin.dadesext.Provincia;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnnex;
import es.caib.distribucio.plugin.procediment.Procediment;
import es.caib.distribucio.plugin.procediment.UnitatAdministrativa;
import es.caib.distribucio.plugin.servei.Servei;
import es.caib.distribucio.plugin.signatura.SignaturaResposta;
import es.caib.distribucio.plugin.unitat.UnitatOrganitzativa;
import es.caib.distribucio.plugin.usuari.DadesUsuari;
import es.caib.distribucio.plugin.validacio.ValidaSignaturaResposta;
import es.caib.pluginsib.arxiu.api.Document;
import lombok.RequiredArgsConstructor;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@RequiredArgsConstructor
public class PluginHelper {

	private final DadesUsuarisPluginHelper dadesUsuarisPluginHelper;
	private final ArxiuPluginHelper arxiuPluginHelper;
	private final DistribucioPluginHelper distribucioPluginHelper;
	private final UnitatsOrganitzativesPluginHelper unitatsOrganitzativesPluginHelper;
	private final DadesExternesPluginHelper dadesExternesPluginHelper;
	private final ValidaSignaturaPluginHelper validaSignaturaPluginHelper;
	private final ProcedimentPluginHelper procedimentPluginHelper;
	private final GestioDocumentalPluginHelper gestioDocumentalPluginHelper;
	private final ServeiPluginHelper serveiPluginHelper;
	private final SignaturaPluginHelper signaturaPluginHelper;
	private final ValidaSignaturaAgilPluginHelper validaSignaturaAgilPluginHelper;
	
	@Autowired
	private ConfigHelper configHelper;
	
	/* Mètode per crear un nou expedient*/
	public String saveRegistreAsExpedientInArxiu(
			String registreNumero,
			String expedientNumero,
			String unitatOrganitzativaCodi) {
		return distribucioPluginHelper.saveRegistreAsExpedientInArxiu(
				registreNumero, 
				expedientNumero, 
				unitatOrganitzativaCodi);
	}

	public String saveAnnexAsDocumentInArxiu(
			String registreNumero,
			DistribucioRegistreAnnex annex,
			String unitatOrganitzativaCodi,
			String uuidExpedient,
			DocumentEniRegistrableDto documentEniRegistrableDto, 
			String procedimentCodi) {
		return distribucioPluginHelper.saveAnnexAsDocumentInArxiu(
				registreNumero, 
				annex, 
				unitatOrganitzativaCodi, 
				uuidExpedient, 
				documentEniRegistrableDto, 
				procedimentCodi);
	}

	public DadesUsuari dadesUsuariFindAmbCodi(String usuariCodi) {
		return dadesUsuarisPluginHelper.dadesUsuariFindAmbCodi(usuariCodi);
	}
	
	public List<DadesUsuari> dadesUsuariFindAmbGrup(String grupCodi) {
		return dadesUsuarisPluginHelper.findAmbGrup(grupCodi);
	}

	public List<String> findRolsPerUsuari(String usuariCodi) {
		return dadesUsuarisPluginHelper.findRolsPerUsuari(usuariCodi);
	}
	
	public boolean isActiuPluginUnitatsOrganitzatives() {
		return unitatsOrganitzativesPluginHelper.isActiu();
	}

	public UnitatOrganitzativa findUnidad(
			String pareCodi, 
			Timestamp fechaActualizacion, 
			Timestamp fechaSincronizacion) {
		return unitatsOrganitzativesPluginHelper.findUnidad(pareCodi, fechaActualizacion, fechaSincronizacion);
	}
	
	public List<UnitatOrganitzativa> findAmbPare(
			String pareCodi, 
			Timestamp fechaActualizacion, 
			Timestamp fechaSincronizacion) {
		return unitatsOrganitzativesPluginHelper.findAmbPare(pareCodi, fechaActualizacion, fechaSincronizacion);
	}

	public List<UnitatOrganitzativaDto> unitatsOrganitzativesFindByFiltre(
			String codiUnitat, 
			String denominacioUnitat,
			String codiNivellAdministracio, 
			String codiComunitat, 
			String codiProvincia, 
			String codiLocalitat, 
			Boolean esUnitatArrel) {
		return unitatsOrganitzativesPluginHelper.unitatsOrganitzativesFindByFiltre(
				codiUnitat, 
				denominacioUnitat, 
				codiNivellAdministracio, 
				codiComunitat, 
				codiProvincia, 
				codiLocalitat, 
				esUnitatArrel);
	}

	public boolean isArxiuPluginActiu() {
		return arxiuPluginHelper.isActiu();
	}

	public void arxiuExpedientEliminar(String idContingut, String registreNumero) {
		arxiuPluginHelper.arxiuExpedientEliminar(idContingut, registreNumero);
	}
	
	public void arxiuExpedientReobrir(RegistreEntity registre) {
		arxiuPluginHelper.arxiuExpedientReobrir(registre);
	}
	
	public void arxiuExpedientTancar(RegistreEntity registre) {
		arxiuPluginHelper.arxiuExpedientTancar(registre);
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
		return distribucioPluginHelper.arxiuDocumentConsultar(
				arxiuUuid, 
				versio, 
				ambContingut, 
				ambVersioImprimible, 
				registreNumero);
	}
	
	public FitxerDto arxiuDocumentImprimible(String fitxerArxiuUuid, String titol) {
		return distribucioPluginHelper.arxiuDocumentImprimible(fitxerArxiuUuid, titol);
	}

	/** Recupera les dades de l'arxiu i el modifica per fer-lo definitiu */
	public void arxiuDocumentSetDefinitiu(RegistreAnnexEntity annex) {
		distribucioPluginHelper.arxiuDocumentSetDefinitiu(annex);
	}


	public boolean isValidaSignaturaPluginActiu() {
		return validaSignaturaPluginHelper.isActiu();
	}

	public ValidaSignaturaResposta validaSignaturaObtenirDetalls(
			String documentNom,
			String documentMime,
			byte[] documentContingut,
			byte[] firmaContingut,
			String registreNumero) {
		
		ValidaSignaturaResposta resposta = validaSignaturaPluginHelper.validaSignaturaObtenirDetalls(
				documentNom,
				documentMime,
				documentContingut, 
				firmaContingut, 
				registreNumero);
		
		if (isValidacioFirmaAgilActiva()) {
			ValidaSignaturaResposta respostaAgil = validaSignaturaAgilPluginHelper.validaSignaturaObtenirDetalls(
					documentNom, 
					documentMime, 
					documentContingut, 
					firmaContingut, 
					registreNumero);
			
			// Combinar validació firma certificat i firma àgil 
			if (resposta != null && respostaAgil != null) {
			    List<ArxiuFirmaDetallDto> detalls = resposta.getFirmaDetalls();
			    List<ArxiuFirmaDetallDto> detallsAgil = respostaAgil.getFirmaDetalls();
	
			    if (detalls != null && detallsAgil != null && !detallsAgil.isEmpty()) {
			        detalls.addAll(detallsAgil);
			    }
			}
		}
		
		return resposta;
	}

	public SignaturaResposta signarDocument(String id,
			String nom,
			String motiu,
			byte[] contingut, 
			String mime,
			String tipusDocumental) {
		return signaturaPluginHelper.signarDocument(
				id, 
				nom, 
				motiu, 
				contingut, 
				mime, 
				tipusDocumental);
	}

	public boolean isDadesExternesPluginActiu() {
		return dadesExternesPluginHelper.isActiu();
	}

	public List<TipusViaDto> dadesExternesTipusViaAll() {
		return dadesExternesPluginHelper.dadesExternesTipusViaAll();
	}

	public List<Provincia> dadesExternesProvinciesFindAmbComunitat(String comunitatCodi) {
		return dadesExternesPluginHelper.dadesExternesProvinciesFindAmbComunitat(comunitatCodi);
	}

	public List<Municipi> dadesExternesMunicipisFindAmbProvincia(String provinciaCodi) {
		return dadesExternesPluginHelper.dadesExternesMunicipisFindAmbProvincia(provinciaCodi);
	}


	public es.caib.pluginsib.arxiu.api.Expedient arxiuExpedientInfo(String arxiuUuid, String registreNumero) {
		return arxiuPluginHelper.arxiuExpedientInfo(arxiuUuid, registreNumero);
	}
	
	public boolean isProcedimentPluginActiu() {
		return procedimentPluginHelper.isActiu();
	}
		
	public UnitatAdministrativa procedimentGetUnitatAdministrativa(String codi) {
		return procedimentPluginHelper.procedimentGetUnitatAdministrativa(codi);
	}

	public List<Procediment> procedimentFindByCodiDir3(String codiDir3) {
		return procedimentPluginHelper.procedimentFindByCodiDir3(codiDir3);
	}
	
	public boolean isServeiPluginActiu() {
		return serveiPluginHelper.isActiu();
	}

	public List<Servei> serveiFindByCodiDir3(String codiDir3) {
		return serveiPluginHelper.serveiFindByCodiDir3(codiDir3);
	}
	
	/** Consulta de l'unitat organitzativa d'un servei.
	 * 
	 * @param codi
	 * @return
	 */
	public UnitatAdministrativa serveiGetUnitatAdministrativa(String codi) {
		return procedimentPluginHelper.serveiGetUnitatAdministrativa(codi);
	}
	
	public ProcedimentDto procedimentFindByCodiSia(String codiSia) {
		return procedimentPluginHelper.procedimentFindByCodiSia(codiSia);
	}

	public boolean isGestioDocumentalPluginActiu() {
		return gestioDocumentalPluginHelper.isActiu();
	}

	public void gestioDocumentalGet(
			String id,
			String agrupacio,
			OutputStream contingutOut,
			String registreNumero) {
		gestioDocumentalPluginHelper.gestioDocumentalGet(
				id, 
				agrupacio, 
				contingutOut, 
				registreNumero);
	}

	public String gestioDocumentalCreate(
			String agrupacio,
			byte[] contingut,
			String registreNumero) {
		return gestioDocumentalPluginHelper.gestioDocumentalCreate(
				agrupacio, 
				contingut, 
				registreNumero);
	}

	public void gestioDocumentalDelete(String id, String agrupacio) {
		gestioDocumentalPluginHelper.gestioDocumentalDelete(id, agrupacio);
	}

	public void resetPlugins() {
		dadesUsuarisPluginHelper.resetPlugin();
		arxiuPluginHelper.resetPlugin();
		distribucioPluginHelper.resetPlugin();
		unitatsOrganitzativesPluginHelper.resetPlugin();
		dadesExternesPluginHelper.resetPlugin();
		validaSignaturaPluginHelper.resetPlugin();
		procedimentPluginHelper.resetPlugin();
		gestioDocumentalPluginHelper.resetPlugin();
		serveiPluginHelper.resetPlugin();
		signaturaPluginHelper.resetPlugin();
		validaSignaturaAgilPluginHelper.resetPlugin();
	}
	
	public List<AbstractPluginHelper<?>> getPluginHelpers() {
		return List.of(
				dadesUsuarisPluginHelper,
				arxiuPluginHelper,
				distribucioPluginHelper,
				unitatsOrganitzativesPluginHelper,
				dadesExternesPluginHelper,
				validaSignaturaPluginHelper,
				procedimentPluginHelper,
				gestioDocumentalPluginHelper,
				serveiPluginHelper,
				signaturaPluginHelper,
				validaSignaturaAgilPluginHelper
		);
	}
	
	private Boolean isValidacioFirmaAgilActiva() {
		return configHelper.getAsBoolean(
				"es.caib.distribucio.plugins.validatesignature.api.evidenciesib.activa", false);
	}

}