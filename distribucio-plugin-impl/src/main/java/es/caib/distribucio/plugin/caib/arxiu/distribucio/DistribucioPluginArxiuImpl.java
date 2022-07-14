/**
 * 
 */
package es.caib.distribucio.plugin.caib.arxiu.distribucio;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.distribucio.core.api.dto.ArxiuFirmaDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.core.api.dto.DocumentEniRegistrableDto;
import es.caib.distribucio.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.distribucio.core.api.dto.DocumentNtiTipoDocumentalEnumDto;
import es.caib.distribucio.core.api.dto.ExpedientEstatEnumDto;
import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.NtiOrigenEnumDto;
import es.caib.distribucio.core.api.exception.ValidationException;
import es.caib.distribucio.core.api.registre.RegistreAnnexElaboracioEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreAnnexNtiTipusDocumentEnum;
import es.caib.distribucio.core.api.registre.RegistreAnnexOrigenEnum;
import es.caib.distribucio.core.api.registre.ValidacioFirmaEnum;
import es.caib.distribucio.plugin.DistribucioAbstractPluginProperties;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.distribucio.DistribucioPlugin;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnnex;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreFirma;
import es.caib.distribucio.plugin.gesdoc.GestioDocumentalPlugin;
import es.caib.distribucio.plugin.signatura.SignaturaPlugin;
import es.caib.distribucio.plugin.signatura.SignaturaResposta;
import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import es.caib.plugins.arxiu.api.DocumentExtensio;
import es.caib.plugins.arxiu.api.DocumentFormat;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.DocumentTipus;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.ExpedientEstat;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaPerfil;
import es.caib.plugins.arxiu.api.FirmaTipus;
import es.caib.plugins.arxiu.api.IArxiuPlugin;
import es.caib.plugins.arxiu.filesystem.ArxiuPluginFilesystem;

/**
 * Implementació del plugin de distribució que utilitza
 * els següents serveis de la CAIB:
 *   · Arxiu digital
 *   · Firma en servidor
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DistribucioPluginArxiuImpl extends DistribucioAbstractPluginProperties implements DistribucioPlugin {


	private IntegracioManager integracioManager;
	private String itegracioGesdocCodi = "GESDOC";
	private String integracioArxiuCodi = "ARXIU";
	private String integracioSignaturaCodi = "SIGNATURA";
	private String gesdocAgrupacioAnnexos = "anotacions_registre_doc_tmp";
	private String gesdocAgrupacioFirmes = "anotacions_registre_fir_tmp";

	private IArxiuPlugin arxiuPlugin;
	private SignaturaPlugin signaturaPlugin;
	private GestioDocumentalPlugin gestioDocumentalPlugin;

	public DistribucioPluginArxiuImpl() {
		super();
	}
	
	public DistribucioPluginArxiuImpl(Properties properties) {
		super(properties);
	}

	
	@Override
	public String expedientCrear(
			String expedientNumero,
			String unitatArrelCodi) throws SistemaExternException {

		String nomExpedient = "EXP_REG_" + expedientNumero + "_" + System.currentTimeMillis();

		String classificacio = getPropertyPluginRegistreExpedientClassificacio();
		if (classificacio == null || classificacio.isEmpty()) {
			throw new ValidationException(
					"No s'ha configurat la propietat amb la classificació de l'expedient");
		}
		String serieDocumental = getPropertyPluginRegistreExpedientSerieDocumental();
		if (serieDocumental == null || serieDocumental.isEmpty()) {
			throw new ValidationException(
					"No s'ha configurat la propietat amb la sèrie documental de l'expedient");
		}
		String accioDescripcio = "Creant expedient per l'anotació de registre";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("titol", nomExpedient);
		accioParams.put("organ", unitatArrelCodi);
		accioParams.put("classificacio", classificacio);
		accioParams.put("estat", ExpedientEstatEnumDto.OBERT.name());
		accioParams.put("serieDocumental", serieDocumental);
		long t0 = System.currentTimeMillis();
		try {
			ContingutArxiu expedientCreat = getArxiuPlugin().expedientCrear(
					toArxiuExpedient(
							null,
							nomExpedient,
							null,
							Arrays.asList(unitatArrelCodi),
							new Date(),
							classificacio,
							ExpedientEstatEnumDto.OBERT,
							null,
							serieDocumental));
			integracioAddAccioOk(
					integracioArxiuCodi,
					accioDescripcio,
					accioParams,
					System.currentTimeMillis() - t0);
			return expedientCreat.getIdentificador();
		} catch (Exception ex) {
			String errorDescripcio = "Error al crear expedient per l'anotació de registre";
			integracioAddAccioError(
					integracioArxiuCodi,
					accioDescripcio,
					accioParams,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					integracioArxiuCodi,
					errorDescripcio,
					ex);
		}
	}
	


	@Override
	public String saveAnnexAsDocumentInArxiu(
			DistribucioRegistreAnnex distribucioAnnex,
			String unitatArrelCodi,
			String uuidExpedient,
			DocumentEniRegistrableDto documentEniRegistrableDto) throws SistemaExternException {
		
		List<ArxiuFirmaDto> arxiuFirmes = null;
		byte[] annexContingut = null;
		
		// if annex is already created in arxiu 
		if (distribucioAnnex.getFitxerArxiuUuid() != null) {
			// Obtenim contingut bytes i les firmes de l'arxiu
			Document arxiuDocument = this.arxiuDocumentConsultar(
					distribucioAnnex.getFitxerArxiuUuid(),
					null,
					true,
					false);
			if (arxiuDocument.getContingut() == null) {
				throw new ValidationException(
						"No s'ha trobat cap contingut per l'annex (" +
						"uuid=" + distribucioAnnex.getFitxerArxiuUuid() + ")");
			}
			annexContingut = arxiuDocument.getContingut().getContingut();
			
			if (arxiuDocument.getFirmes() != null) {
				arxiuFirmes = new ArrayList<ArxiuFirmaDto>();
				for (Firma firma: arxiuDocument.getFirmes()) {
					ArxiuFirmaDto arxiuFirma = new ArxiuFirmaDto();
					
					arxiuFirma.setTipus(firma.getTipus() != null ? ArxiuFirmaTipusEnumDto.valueOf(firma.getTipus().toString()) : null);
					arxiuFirma.setPerfil(firma.getPerfil() != null ? ArxiuFirmaPerfilEnumDto.valueOf(firma.getPerfil().toString()) : null);
					arxiuFirma.setFitxerNom(firma.getFitxerNom());
					arxiuFirma.setContingut(firma.getContingut());
					arxiuFirma.setTipusMime(firma.getTipusMime());
					arxiuFirma.setCsvRegulacio(firma.getCsvRegulacio());
					arxiuFirmes.add(arxiuFirma);
				}
			}
		// if annex is not yet created in arxiu 	
		} else { 
			if (distribucioAnnex.getGesdocDocumentId() != null) {
			    // get contingut bytes from local file system
				annexContingut = gestioDocumentalGet(
						distribucioAnnex.getGesdocDocumentId(),
						gesdocAgrupacioAnnexos);
			} 
			if (distribucioAnnex.getFirmes() != null) {
				// get firmes info from db and firmes content bytes from local file system
				arxiuFirmes = convertirFirmesAnnexToArxiuFirmaDto(
						distribucioAnnex.getFirmes());
			}
		}
		
		if (annexContingut != null) {
			// Si l'annex no està firmat el firma amb el plugin de firma en servidor si és vàlid i té un format reconegut ler l'Arxiu
			boolean annexFirmat = arxiuFirmes != null && !arxiuFirmes.isEmpty();
			DocumentFormat format = this.getDocumentFormat(this.getDocumentExtensio(distribucioAnnex.getFitxerNom()));
			boolean documentValid = (distribucioAnnex.getValidacioFirma() != ValidacioFirmaEnum.FIRMA_INVALIDA)
									&& (distribucioAnnex.getValidacioFirma() != ValidacioFirmaEnum.ERROR_VALIDANT)
									&& format != null;

			// Mira si firmar en servidor
			if (!annexFirmat 
					&& documentValid
					&& isRegistreSignarAnnexos()) {
				
				try {
					SignaturaResposta signatura = signaturaDistribucioSignar(
							distribucioAnnex,
							annexContingut,
							"Firma en servidor de document annex de l'anotació de registre");
					
					if (StringUtils.isEmpty(signatura.getTipusFirmaEni()) 
							|| StringUtils.isEmpty(signatura.getTipusFirmaEni())) {
						logger.warn("El tipus o perfil de firma s'ha retornat buit i això pot provocar error guardant a l'Arxiu [tipus: " + 
								signatura.getTipusFirmaEni() + ", perfil: " + signatura.getPerfilFirmaEni() + "]");
						if ("cades".equals(StringUtils.lowerCase(signatura.getTipusFirma()))) {
							logger.warn("Fixant el tipus de firma a TF04 i perfil BES");
							if (StringUtils.isEmpty(signatura.getTipusFirmaEni()))
								signatura.setTipusFirmaEni("TF04");
							if (StringUtils.isEmpty(signatura.getPerfilFirmaEni()))
								signatura.setPerfilFirmaEni("BES");
						}
					}
					byte [] firmaDistribucioContingut = signatura.getContingut();
					String tipusFirmaArxiu = signatura.getTipusFirmaEni();
					String perfil = mapPerfilFirma(signatura.getPerfilFirmaEni());
					String fitxerNom = signatura.getNom();
					String tipusMime = signatura.getMime();
					String csvRegulacio = null;
					
					
					DistribucioRegistreFirma annexFirma = new DistribucioRegistreFirma();
					annexFirma.setTipus(tipusFirmaArxiu);
					annexFirma.setPerfil(perfil);
					annexFirma.setFitxerNom(fitxerNom);
					annexFirma.setTipusMime(tipusMime);
					annexFirma.setCsvRegulacio(csvRegulacio);
					annexFirma.setAutofirma(true);
					annexFirma.setGesdocFirmaId(null);
					annexFirma.setContingut(firmaDistribucioContingut);
					annexFirma.setAnnex(distribucioAnnex);
					annexFirma.setTamany(firmaDistribucioContingut.length);
					distribucioAnnex.getFirmes().add(annexFirma);
					
					arxiuFirmes = convertirFirmesAnnexToArxiuFirmaDto(
							distribucioAnnex.getFirmes());					
					
				} catch(SistemaExternException se) {
					if (getPropertyGuardarAnnexosFirmesInvalidesComEsborrany()) {
						logger.error("Error firmant en servidor l'annex \"" + distribucioAnnex.getFitxerNom() + "\" (" + distribucioAnnex.getFitxerNom() + "):"  + se.getMessage() 
						+ ". Per la propietat es.caib.distribucio.tasca.guardar.annexos.firmes.invalides.com.esborrany=true s'ignora l'excepció per guardar l'annex com esborrany.");
					} else {
						throw se;
					}
				}
			}
		}
		
		FitxerDto fitxerContingut = new FitxerDto();
		fitxerContingut.setNom(distribucioAnnex.getFitxerNom());
		fitxerContingut.setContentType(distribucioAnnex.getFitxerTipusMime());
		fitxerContingut.setContingut(annexContingut);
		fitxerContingut.setTamany(distribucioAnnex.getFitxerTamany());
		
		// SAVE IN ARXIU
		String uuidDocumentCreat = arxiuDocumentAnnexCrear(
				distribucioAnnex,
				unitatArrelCodi,
				fitxerContingut,
				arxiuFirmes,
				uuidExpedient,
				documentEniRegistrableDto);
		distribucioAnnex.setFitxerArxiuUuid(uuidDocumentCreat);
		return uuidDocumentCreat;
	}

	/** Map amb el mapeig dels perfils de firma cap als perfils admesos per l'Arxiu. */
	private static Map<String, String> mapPerfilsFirma = new HashMap<String, String>();
	static {
		mapPerfilsFirma.put("AdES-BES", "BES");
		mapPerfilsFirma.put("AdES-EPES", "EPES");
		mapPerfilsFirma.put("AdES-T", "T");
		mapPerfilsFirma.put("AdES-C", "C");
		mapPerfilsFirma.put("AdES-X", "X");
		mapPerfilsFirma.put("AdES-X1", "X");
		mapPerfilsFirma.put("AdES-X2", "X");
		mapPerfilsFirma.put("AdES-XL", "XL");
		mapPerfilsFirma.put("AdES-XL1", "XL");
		mapPerfilsFirma.put("AdES-XL2", "XL");
		mapPerfilsFirma.put("AdES-A", "A");
		mapPerfilsFirma.put("PAdES-LTV", "LTV");
		mapPerfilsFirma.put("PAdES-Basic", "BES");
	}
	
	/** Mapeja els diferents perfils de firma que pot retornar el plugin de firma simple cap
	 * als perfils admesos per l'Arxiu.
	 * 
	 * @param perfil
	 * @return
	 */
	public static String mapPerfilFirma(String perfil) {
		if (mapPerfilsFirma.containsKey(perfil))
			perfil = mapPerfilsFirma.get(perfil);
		return perfil;
	}



	@Override
	public Document documentDescarregar(
			String arxiuUuid,
			String versio, 
			boolean ambContingut,
			boolean ambVersioImprimible) throws SistemaExternException {
		return arxiuDocumentConsultar(
				arxiuUuid,
				versio, 
				ambContingut,
				ambVersioImprimible);
	}
	
	@Override
	public DocumentContingut documentImprimible(
			String arxiuUuid) throws SistemaExternException {
		DocumentContingut documentContingut;
		if (!(this.getArxiuPlugin() instanceof ArxiuPluginFilesystem)) {
			documentContingut = this.generarVersioImprimible(arxiuUuid);
		} else {
			// Plugin Filesystem
			// El plugin ArxiuPluginFilesystem no té el mètode implementat de versió imp
			Document document = this.getDocumentDetalls(arxiuUuid, null, true);
			documentContingut = document.getContingut();
			if (documentContingut.getArxiuNom() == null) {
				documentContingut.setArxiuNom(document.getNom());
			}
		}
		return documentContingut;
	}
	
	@Override
	public void contenidorEliminar(
			String idContingut) throws SistemaExternException {
		String accioDescripcio = "Esborrant expedient";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("identificador", idContingut);
		long t0 = System.currentTimeMillis();
		try {
			getArxiuPlugin().expedientEsborrar(idContingut);
			integracioAddAccioOk(
					integracioArxiuCodi,
					accioDescripcio,
					accioParams,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al esborrar rexpedient";
			integracioAddAccioError(
					integracioArxiuCodi,
					accioDescripcio,
					accioParams,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					integracioArxiuCodi,
					errorDescripcio,
					ex);
		}
	}
	
	@Override
	public void documentSetDefinitiu (
			String arxiuUuid) throws SistemaExternException {

		// Recupera les dades
		Document document = this.getDocumentDetalls(arxiuUuid, null, false);
		if (!document.getEstat().equals(DocumentEstat.DEFINITIU)) {
			// Modifica el document a definitiu
			String accioDescripcio = "Modificar el document a definitiu";
			Map<String, String> accioParams = new HashMap<String, String>();
			accioParams.put("identificador", arxiuUuid);
			accioParams.put("documentDescripcio", document.getDescripcio());
			accioParams.put("documentNom", document.getNom());
			accioParams.put("documentEstat", document.getEstat().toString());
			long t0 = System.currentTimeMillis();
			try {
				document.setEstat(DocumentEstat.DEFINITIU);
				getArxiuPlugin().documentModificar(document);
				integracioAddAccioOk(
						integracioArxiuCodi,
						accioDescripcio,
						accioParams,
						System.currentTimeMillis() - t0);
			} catch (Exception ex) {
				String errorDescripcio = "Error modificant el document a definitiu";
				integracioAddAccioError(
						integracioArxiuCodi,
						accioDescripcio,
						accioParams,
						System.currentTimeMillis() - t0,
						errorDescripcio,
						ex);
				throw new SistemaExternException(
						integracioArxiuCodi,
						errorDescripcio,
						ex);
			}
		}
	}

	@Override
	public void configurar(
			IntegracioManager integracioManager,
			String itegracioGesdocCodi,
			String integracioArxiuCodi,
			String integracioSignaturaCodi,
			String gesdocAgrupacioAnnexos,
			String gesdocAgrupacioFirmes) {
		this.integracioManager = integracioManager;
		this.itegracioGesdocCodi = itegracioGesdocCodi;
		this.integracioArxiuCodi = integracioArxiuCodi;
		this.integracioSignaturaCodi = integracioSignaturaCodi;
		this.gesdocAgrupacioAnnexos = gesdocAgrupacioAnnexos;
		this.gesdocAgrupacioFirmes = gesdocAgrupacioFirmes;
	}

	private String revisarContingutNom(String nom) {
		if (nom != null) {
			String nomNormalitzat = Normalizer.normalize(nom, Normalizer.Form.NFD);   
			String nomSenseAccents = nomNormalitzat.replaceAll("[^\\p{ASCII}]", "");
			return nomSenseAccents.replaceAll("[\n\t]", "").replaceAll("[^a-zA-Z0-9_ -.()]", "").trim();
		} else {
			return null;
		}
	}

	private String arxiuDocumentAnnexCrear(
			DistribucioRegistreAnnex annex,
			String unitatArrelCodi,
			FitxerDto fitxer,
			List<ArxiuFirmaDto> firmes,
			String identificadorPare,
			DocumentEniRegistrableDto documentEniRegistrableDto) throws SistemaExternException {
		
		DocumentEstat estatDocument = DocumentEstat.ESBORRANY;
		// Es guarden definitius si:
		// 1) El documetn té firmes
		boolean guardarDefinitiu = annex.getFirmes() != null && !annex.getFirmes().isEmpty();
		// 2) No té firmes invàlides o la propietat de guardar annexos amb firmes invàlides com a esborrany està desactivada
		guardarDefinitiu = guardarDefinitiu && ValidacioFirmaEnum.isValida(annex.getValidacioFirma()) 
				|| ! getPropertyGuardarAnnexosFirmesInvalidesComEsborrany();
		// 3) Format no reconegut
		DocumentFormat format = this.getDocumentFormat(this.getDocumentExtensio(fitxer));
		guardarDefinitiu = guardarDefinitiu && format != null;
				
		if (guardarDefinitiu) {
				estatDocument = DocumentEstat.DEFINITIU;
		} else {
			// Per guardar-lo com a esborrany treu la informació de les firmes i corregeix el contingut
			if (fitxer.getContingut() == null) {
				fitxer.setContingut(firmes.get(0).getContingut());
			}
			firmes = null;
		}
		//creating info for integracio logs
		String accioDescripcio = "Creant document annex";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", annex.getId());
		accioParams.put("titol", annex.getTitol());
		if (fitxer != null) {
			accioParams.put("fitxerNom", fitxer.getNom());
			accioParams.put("fitxerContentType", fitxer.getContentType());
			accioParams.put("fitxerContingut", (fitxer.getContingut() != null ? "" + fitxer.getContingut().length : "0") + " bytes");
		} else {
			accioParams.put("fitxer", "[buit]");
		}
		accioParams.put("firmesCount", (firmes != null ? "" + firmes.size() : "0"));
		if (firmes != null) {
			StringBuilder firmesTipus = new StringBuilder();
			StringBuilder firmesPerfil = new StringBuilder();
			StringBuilder firmesContingut = new StringBuilder();
			boolean primera = true; 
			for (ArxiuFirmaDto firma: firmes) {
				if (!primera) {
					firmesTipus.append(", ");
					firmesPerfil.append(", ");
					firmesContingut.append(", ");
				}
				firmesTipus.append(firma.getTipus());
				firmesPerfil.append(firma.getPerfil());
				firmesContingut.append((firma.getContingut() != null ? "" + firma.getContingut().length : "0") + " bytes");
				primera = false;
			}
			accioParams.put("firmesTipus", firmesTipus.toString());
			accioParams.put("firmesPerfil", firmesPerfil.toString());
			accioParams.put("firmesContingut", firmesContingut.toString());
		}
		accioParams.put("validacioFirma", annex.getValidacioFirma() != null ? annex.getValidacioFirma().toString() : "-");
		accioParams.put("validacioFirmaError", annex.getValidacioFirmaError());
		
		long t0 = System.currentTimeMillis();
		try {
			
			String nom = this.uniqueNameArxiu(
					annex.getFitxerNom() != null ? annex.getFitxerNom() : annex.getTitol(), 
					identificadorPare);
						
			ContingutArxiu contingutFitxer = getArxiuPlugin().documentCrear(
					toArxiuDocument(
							null,
							nom,
							annex.getTitol(),
							fitxer,
							firmes,
							null,
							(annex.getOrigenCiutadaAdmin() != null ? NtiOrigenEnumDto.values()[Integer.valueOf(RegistreAnnexOrigenEnum.valueOf(annex.getOrigenCiutadaAdmin()).getValor())] : null),
							Arrays.asList(unitatArrelCodi),
							annex.getDataCaptura(),
							(annex.getNtiElaboracioEstat() != null ? DocumentNtiEstadoElaboracionEnumDto.valueOf(RegistreAnnexElaboracioEstatEnum.valueOf(annex.getNtiElaboracioEstat()).getValor()) : null),
							(annex.getNtiTipusDocument() != null ? DocumentNtiTipoDocumentalEnumDto.valueOf(RegistreAnnexNtiTipusDocumentEnum.valueOf(annex.getNtiTipusDocument()).getValor()) : null),
							estatDocument,
							documentEniRegistrableDto,
							annex.getMetaDades()),
					identificadorPare);
			integracioAddAccioOk(
					integracioArxiuCodi,
					accioDescripcio,
					accioParams,
					System.currentTimeMillis() - t0);
			return contingutFitxer.getIdentificador();
		} catch (Exception ex) {
			String errorDescripcio = "Error al crear document annex";
			integracioAddAccioError(
					integracioArxiuCodi,
					accioDescripcio,
					accioParams,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					integracioArxiuCodi,
					errorDescripcio,
					ex);
		}
	}
	
	
	/** Determina l'extensió a partir  del fitxer */
	private DocumentExtensio getDocumentExtensio(FitxerDto fitxer) {
		DocumentExtensio extensio = null;
		if (fitxer != null && fitxer.getExtensio() != null) {
			String extensioAmbPunt = (fitxer.getExtensio().startsWith(".") ? "" : ".") + fitxer.getExtensio().toLowerCase();
			extensio = DocumentExtensio.toEnum(extensioAmbPunt);
		}
		return extensio;
	}
	
	/** Determina l'extensió a partir del nom del fitxer */
	private DocumentExtensio getDocumentExtensio(String fitxerNom) {
		FitxerDto fitxer = new FitxerDto();
		fitxer.setNom(fitxerNom);
		DocumentExtensio extensio = null;
		if (fitxer != null && fitxer.getExtensio() != null) {
			String extensioAmbPunt = (fitxer.getExtensio().startsWith(".") ? "" : ".") + fitxer.getExtensio().toLowerCase();
			extensio = DocumentExtensio.toEnum(extensioAmbPunt);
		}
		return extensio;
	}


	/** Consulta el format a partir de l'extensió
	 * 
	 * @param extensio
	 * @return
	 */
	private DocumentFormat getDocumentFormat(DocumentExtensio extensio) {

		if (extensio == null ) {
			return null;
		}
		DocumentFormat format = null;
		switch (extensio) {
		case AVI:
			format = DocumentFormat.AVI;
			break;
		case CSS:
			format = DocumentFormat.CSS;
			break;
		case CSV:
			format = DocumentFormat.CSV;
			break;
		case DOCX:
			format = DocumentFormat.SOXML;
			break;
		case GML:
			format = DocumentFormat.GML;
			break;
		case GZ:
			format = DocumentFormat.GZIP;
			break;
		case HTM:
			format = DocumentFormat.XHTML; // HTML o XHTML!!!
			break;
		case HTML:
			format = DocumentFormat.XHTML; // HTML o XHTML!!!
			break;
		case JPEG:
			format = DocumentFormat.JPEG;
			break;
		case JPG:
			format = DocumentFormat.JPEG;
			break;
		case MHT:
			format = DocumentFormat.MHTML;
			break;
		case MHTML:
			format = DocumentFormat.MHTML;
			break;
		case MP3:
			format = DocumentFormat.MP3;
			break;
		case MP4:
			format = DocumentFormat.MP4V; // MP4A o MP4V!!!
			break;
		case MPEG:
			format = DocumentFormat.MP4V; // MP4A o MP4V!!!
			break;
		case ODG:
			format = DocumentFormat.OASIS12;
			break;
		case ODP:
			format = DocumentFormat.OASIS12;
			break;
		case ODS:
			format = DocumentFormat.OASIS12;
			break;
		case ODT:
			format = DocumentFormat.OASIS12;
			break;
		case OGA:
			format = DocumentFormat.OGG;
			break;
		case OGG:
			format = DocumentFormat.OGG;
			break;
		case PDF:
			format = DocumentFormat.PDF; // PDF o PDFA!!!
			break;
		case PNG:
			format = DocumentFormat.PNG;
			break;
		case PPTX:
			format = DocumentFormat.SOXML;
			break;
		case RTF:
			format = DocumentFormat.RTF;
			break;
		case SVG:
			format = DocumentFormat.SVG;
			break;
		case TIFF:
			format = DocumentFormat.TIFF;
			break;
		case TXT:
			format = DocumentFormat.TXT;
			break;
		case WEBM:
			format = DocumentFormat.WEBM;
			break;
		case XLSX:
			format = DocumentFormat.SOXML;
			break;
		case ZIP:
			format = DocumentFormat.ZIP;
			break;
		case CSIG:
			format = DocumentFormat.CSIG;
			break;
		case XSIG:
			format = DocumentFormat.XSIG;
			break;
		case XML:
			format = DocumentFormat.XML;
			break;
		}
		return format;
	}

	private String uniqueNameArxiu(
			String arxiuNom,
			String identificadorPare) throws ArxiuException, SistemaExternException {
		
		// Revisa els caràcters estranys com ho fa el plugin abans comprobar si ja existeix el nom
		arxiuNom = revisarContingutNom(arxiuNom);
		

		// geting all docuements of an expedient saved already in arxiu
		List<ContingutArxiu> continguts = getArxiuPlugin().expedientDetalls(
				identificadorPare,
				null).getContinguts();
		if (continguts != null) {
			List<String> nomsExistingInArxiu = new ArrayList<String>();
			// Itreating over the files and saving their names in a List
			for (ContingutArxiu contingut : continguts) {
				nomsExistingInArxiu.add(contingut.getNom().toLowerCase());
			}
			
			String nName = arxiuNom;
			int ocurrences = 0;
			if (arxiuNom.contains(".")) {
				// Nom amb extensió
				String name = arxiuNom.substring(0, arxiuNom.lastIndexOf('.'));
				String extension = arxiuNom.substring(arxiuNom.lastIndexOf('.'));
				nName = name;
				while(nomsExistingInArxiu.indexOf((nName + extension).toLowerCase()) >= 0) {
					ocurrences ++;
					nName = name + " (" + ocurrences + ")";
				}
				nName += extension;

			} else {
				// Nom sense extensió
				while (nomsExistingInArxiu.indexOf(nName.toLowerCase()) >= 0) {
					// if it does 'ocurrences' increments
					ocurrences++;
					// and the number of ocurences is added to the file name
					// and it checks again if the new file name exists
					nName = arxiuNom + " (" + ocurrences + ")";
				}
			}
						
			return nName;
		}
		return arxiuNom;
	}


	private List<ArxiuFirmaDto> convertirFirmesAnnexToArxiuFirmaDto(
			List<DistribucioRegistreFirma> annexFirmes) throws SistemaExternException {
		List<ArxiuFirmaDto> firmes = null;
		if (annexFirmes != null) {
			firmes = new ArrayList<ArxiuFirmaDto>();
			for (DistribucioRegistreFirma annexFirma: annexFirmes) {
				/*if (annexFirma.getGesdocFirmaId() != null) {
					
				} else if(firmaDistribucioContingut != null) {
					firmaContingut = firmaDistribucioContingut;
				} else if (firmaDistribucioContingut == null && !"TF06".equalsIgnoreCase(annexFirma.getTipus())) {
					firmaContingut = annexFirma.getContingut();
				}*/
				ArxiuFirmaDto firma = new ArxiuFirmaDto();
				if ("TF01".equalsIgnoreCase(annexFirma.getTipus())) {
					firma.setTipus(ArxiuFirmaTipusEnumDto.CSV);
				} else if ("TF02".equalsIgnoreCase(annexFirma.getTipus())) {
					firma.setTipus(ArxiuFirmaTipusEnumDto.XADES_DET);
				} else if ("TF03".equalsIgnoreCase(annexFirma.getTipus())) {
					firma.setTipus(ArxiuFirmaTipusEnumDto.XADES_ENV);
				} else if ("TF04".equalsIgnoreCase(annexFirma.getTipus())) {
					firma.setTipus(ArxiuFirmaTipusEnumDto.CADES_DET);
				} else if ("TF05".equalsIgnoreCase(annexFirma.getTipus())) {
					firma.setTipus(ArxiuFirmaTipusEnumDto.CADES_ATT);
				} else if ("TF06".equalsIgnoreCase(annexFirma.getTipus())) {
					firma.setTipus(ArxiuFirmaTipusEnumDto.PADES);
				} else if ("TF07".equalsIgnoreCase(annexFirma.getTipus())) {
					firma.setTipus(ArxiuFirmaTipusEnumDto.SMIME);
				} else if ("TF08".equalsIgnoreCase(annexFirma.getTipus())) {
					firma.setTipus(ArxiuFirmaTipusEnumDto.ODT);
				} else if ("TF09".equalsIgnoreCase(annexFirma.getTipus())) {
					firma.setTipus(ArxiuFirmaTipusEnumDto.OOXML);
				}
				if (StringUtils.isNotEmpty(annexFirma.getPerfil()))
					firma.setPerfil(ArxiuFirmaPerfilEnumDto.valueOf(annexFirma.getPerfil()));
				firma.setFitxerNom(annexFirma.getFitxerNom());
				firma.setTipusMime(annexFirma.getTipusMime());
				firma.setCsvRegulacio(annexFirma.getCsvRegulacio());
				firma.setAutofirma(annexFirma.isAutofirma());
				byte[] firmaContingut = annexFirma.getContingut();
				if (firmaContingut == null && annexFirma.getGesdocFirmaId() != null) {
					firmaContingut = gestioDocumentalGet(
							annexFirma.getGesdocFirmaId(),
							gesdocAgrupacioFirmes);
				}
				firma.setContingut(firmaContingut);
				firmes.add(firma);
			}
		}
		return firmes;
	}

	private SignaturaResposta signaturaDistribucioSignar(
			DistribucioRegistreAnnex annex,
			byte[] annexContingut,
			String motiu) throws SistemaExternException {
		String accioDescripcio = "Firma en servidor de document annex de l'anotació de registre";
		String usuariIntegracio = "";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("annexId", annex.getId().toString());
		accioParams.put("annexFitxerNom", annex.getFitxerNom());
		accioParams.put("annexFitxerContingut", (annexContingut != null ? "" + annexContingut.length : "0") + " bytes");
		accioParams.put("motiu", motiu);
		long t0 = System.currentTimeMillis();
		try {
			usuariIntegracio = getSignaturaPlugin().getUsuariIntegracio();
			// Assegura que l'annex tingui tipus MIME i si és un .pdf sigui application/pdf
			String mime = annex.getFitxerTipusMime();
			if ("pdf".equals(FilenameUtils.getExtension(annex.getFitxerNom().toLowerCase()))) {
				mime = "application/pdf";
			} else if (mime == null || mime.trim().isEmpty()) {
				mime = new MimetypesFileTypeMap().getContentType(annex.getFitxerNom());
			}
			accioParams.put("tipusMime", mime);
			
			String tipusDocumental = annex.getNtiTipusDocument() != null ? RegistreAnnexNtiTipusDocumentEnum.valueOf(annex.getNtiTipusDocument()).getValor() : null;
			
			SignaturaResposta signatura = getSignaturaPlugin().signar(
					annex.getId().toString(),
					annex.getFitxerNom(),
					motiu,
					annexContingut, 
					mime,
					tipusDocumental);
			
			accioParams.put("resposta", "tipus: " + signatura.getTipusFirmaEni() + 
										", perfil: " + signatura.getPerfilFirmaEni() + 
										", nom: " + signatura.getNom() + 
										", mime: " + signatura.getMime() + 
										", grandaria: " + (signatura.getContingut() != null ? 
												signatura.getContingut().length : "-"));
			integracioAddAccioOk(
					integracioSignaturaCodi,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					System.currentTimeMillis() - t0);
			return signatura;
		} catch (Exception ex) {
			String errorDescripcio = "Error al firmar document en servidor";
			integracioAddAccioError(
					integracioSignaturaCodi,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					integracioSignaturaCodi,
					errorDescripcio,
					ex);
		}
	}

	private Document arxiuDocumentConsultar(
			String arxiuUuid,
			String versio,
			boolean ambContingut,
			boolean ambVersioImprimible) throws SistemaExternException {
		Document documentDetalls = null;
		if (ambContingut && ambVersioImprimible) {
			// Consulta els detalls i si està firmat i és pades demana la versió imprimible
			documentDetalls = this.getDocumentDetalls(arxiuUuid, versio, false);
			boolean generarVersioImprimible = false;
			if (documentDetalls.getFirmes() != null && !documentDetalls.getFirmes().isEmpty()) {
				for (Firma firma : documentDetalls.getFirmes()) {
					if ((firma.getTipus() == FirmaTipus.PADES || firma.getTipus() == FirmaTipus.CADES_ATT || firma.getTipus() == FirmaTipus.CADES_DET)
							&& (!(getArxiuPlugin() instanceof ArxiuPluginFilesystem))) {
						generarVersioImprimible = true;
					}
				}
				if (generarVersioImprimible) {
					documentDetalls.setContingut(
							generarVersioImprimible(documentDetalls.getIdentificador()));
				} else {
					documentDetalls = this.getDocumentDetalls(arxiuUuid, versio, true);
				}
			}
		} else {
			// Consulta dels detalls amb o sense contingut
			documentDetalls = this.getDocumentDetalls(arxiuUuid, versio, ambContingut);
		}
		return documentDetalls;
	}

	/** Obté els detalls del document amb o sense contingut depenent dels paràmetres de a funció. */
	private Document getDocumentDetalls(
			String arxiuUuid, 
			String versio, 
			boolean ambContingut) throws SistemaExternException {
		Document documentDetalls = null;
		
		String accioDescripcio = "Obtenint detalls del document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("arxiuUuid", arxiuUuid);
		accioParams.put("versio", versio);
		accioParams.put("ambContingut", new Boolean(ambContingut).toString());
		long t0 = System.currentTimeMillis();
		try {
			documentDetalls = getArxiuPlugin().documentDetalls(
					arxiuUuid,
					versio,
					ambContingut);
			integracioAddAccioOk(
					integracioArxiuCodi,
					accioDescripcio,
					accioParams,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String excMsg = ex.getMessage();
			if (ex.getCause() != null && !ex.getCause().getClass().equals(ex.getClass())) {
				excMsg += ": " + ex.getCause().getMessage();
			}
			String errorDescripcio = "Error al obtenir detalls del document: " + excMsg;
			integracioAddAccioError(
					integracioArxiuCodi,
					accioDescripcio,
					accioParams,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					integracioArxiuCodi,
					errorDescripcio,
					ex);
		}	
		return documentDetalls;
	}
	
	/** Crida al plugin d'Arxiu per obtenir el contingut de la versió imprimible del document. */
	private DocumentContingut generarVersioImprimible(
			String identificadorArxiu) throws SistemaExternException {
		DocumentContingut documentImprimible = null;
		String accioDescripcio = "Obtenint la versió imprimible del document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("identificador", identificadorArxiu);
		long t0 = System.currentTimeMillis();
		try {
			documentImprimible = getArxiuPlugin().documentImprimible(identificadorArxiu);
			integracioAddAccioOk(
					integracioArxiuCodi,
					accioDescripcio,
					accioParams,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al generar la versió imprimible del document";
			integracioAddAccioError(
					integracioArxiuCodi,
					accioDescripcio,
					accioParams,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					integracioArxiuCodi,
					errorDescripcio,
					ex);
		}
		return documentImprimible;
	}

	private byte[] gestioDocumentalGet(
			String id,
			String agrupacio) throws SistemaExternException {
		String accioDescripcio = "Obtenint arxiu de la gestió documental";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("id", id);
		accioParams.put("agrupacio", agrupacio);
		long t0 = System.currentTimeMillis();
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			getGestioDocumentalPlugin().get(
					id,
					agrupacio,
					baos);
			integracioAddAccioOk(
					itegracioGesdocCodi,
					accioDescripcio,
					accioParams,
					System.currentTimeMillis() - t0);
			return baos.toByteArray();
		} catch (Exception ex) {
			String errorDescripcio = "Error al obtenir arxiu de la gestió documental";
			integracioAddAccioError(
					itegracioGesdocCodi,
					accioDescripcio,
					accioParams,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					itegracioGesdocCodi,
					errorDescripcio,
					ex);
		}
	}

	private Expedient toArxiuExpedient(
			String identificador,
			String nom,
			String ntiIdentificador,
			List<String> ntiOrgans,
			Date ntiDataObertura,
			String ntiClassificacio,
			ExpedientEstatEnumDto ntiEstat,
			List<String> ntiInteressats,
			String serieDocumental) {
		Expedient expedient = new Expedient();
		expedient.setNom(nom);
		expedient.setIdentificador(identificador);
		ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setIdentificador(ntiIdentificador);
		metadades.setDataObertura(ntiDataObertura);
		metadades.setClassificacio(ntiClassificacio);
		if (ntiEstat != null) {
			switch (ntiEstat) {
			case OBERT:
				metadades.setEstat(ExpedientEstat.OBERT);
				break;
			case TANCAT:
				metadades.setEstat(ExpedientEstat.TANCAT);
				break;
			case INDEX_REMISSIO:
				metadades.setEstat(ExpedientEstat.INDEX_REMISSIO);
				break;
			}
		}
		metadades.setOrgans(ntiOrgans);
		metadades.setInteressats(ntiInteressats);
		metadades.setSerieDocumental(serieDocumental);
		expedient.setMetadades(metadades);
		return expedient;
	}

	/** Llistat de firmes attached */
	private static List<FirmaTipus> TIPUS_FIRMES_ATTACHED = Arrays.asList(FirmaTipus.CADES_ATT, FirmaTipus.PADES, FirmaTipus.XADES_ENV);

	private Document toArxiuDocument(
			String identificador,
			String nom,
			String descripcio,
			FitxerDto fitxer,
			List<ArxiuFirmaDto> firmes, 
			String ntiIdentificador,
			NtiOrigenEnumDto ntiOrigen,
			List<String> ntiOrgans,
			Date ntiDataCaptura,
			DocumentNtiEstadoElaboracionEnumDto ntiEstatElaboracio,
			DocumentNtiTipoDocumentalEnumDto ntiTipusDocumental,
			DocumentEstat estat,
			DocumentEniRegistrableDto documentEniRegistrableDto,
			String metaDades) {
		Document document = new Document();
		document.setNom(nom);
		document.setDescripcio(descripcio);
		document.setIdentificador(identificador);
		DocumentMetadades metadades = new DocumentMetadades();
		metadades.setIdentificador(ntiIdentificador);
		if (ntiOrigen != null) {
			switch (ntiOrigen) {
			case O0:
				metadades.setOrigen(ContingutOrigen.CIUTADA);
				break;
			case O1:
			case O5:
				metadades.setOrigen(ContingutOrigen.ADMINISTRACIO);
				break;
			}
		}
		metadades.setDataCaptura(ntiDataCaptura);
		DocumentEstatElaboracio estatElaboracio = null;
		switch (ntiEstatElaboracio) {
		case EE01:
			estatElaboracio = DocumentEstatElaboracio.ORIGINAL;
			break;
		case EE02:
			estatElaboracio = DocumentEstatElaboracio.COPIA_CF;
			break;
		case EE03:
			estatElaboracio = DocumentEstatElaboracio.COPIA_DP;
			break;
		case EE04:
			estatElaboracio = DocumentEstatElaboracio.COPIA_PR;
			break;
		case EE99:
			estatElaboracio = DocumentEstatElaboracio.ALTRES;
			break;
		}
		metadades.setEstatElaboracio(estatElaboracio);
		DocumentTipus tipusDocumental = null;
		switch (ntiTipusDocumental) {
		case TD01:
			tipusDocumental = DocumentTipus.RESOLUCIO;
			break;
		case TD02:
			tipusDocumental = DocumentTipus.ACORD;
			break;
		case TD03:
			tipusDocumental = DocumentTipus.CONTRACTE;
			break;
		case TD04:
			tipusDocumental = DocumentTipus.CONVENI;
			break;
		case TD05:
			tipusDocumental = DocumentTipus.DECLARACIO;
			break;
		case TD06:
			tipusDocumental = DocumentTipus.COMUNICACIO;
			break;
		case TD07:
			tipusDocumental = DocumentTipus.NOTIFICACIO;
			break;
		case TD08:
			tipusDocumental = DocumentTipus.PUBLICACIO;
			break;
		case TD09:
			tipusDocumental = DocumentTipus.JUSTIFICANT_RECEPCIO;
			break;
		case TD10:
			tipusDocumental = DocumentTipus.ACTA;
			break;
		case TD11:
			tipusDocumental = DocumentTipus.CERTIFICAT;
			break;
		case TD12:
			tipusDocumental = DocumentTipus.DILIGENCIA;
			break;
		case TD13:
			tipusDocumental = DocumentTipus.INFORME;
			break;
		case TD14:
			tipusDocumental = DocumentTipus.SOLICITUD;
			break;
		case TD15:
			tipusDocumental = DocumentTipus.DENUNCIA;
			break;
		case TD16:
			tipusDocumental = DocumentTipus.ALEGACIO;
			break;
		case TD17:
			tipusDocumental = DocumentTipus.RECURS;
			break;
		case TD18:
			tipusDocumental = DocumentTipus.COMUNICACIO_CIUTADA;
			break;
		case TD19:
			tipusDocumental = DocumentTipus.FACTURA;
			break;
		case TD20:
			tipusDocumental = DocumentTipus.ALTRES_INCAUTATS;
			break;
		default:
			tipusDocumental = DocumentTipus.ALTRES;
			break;
		}
		metadades.setTipusDocumental(tipusDocumental);
		
		DocumentExtensio extensio = this.getDocumentExtensio(fitxer);

		// Firmes
		Firma primeraFirma = null;
		if (firmes != null && firmes.size() > 0) {
			document.setFirmes(new ArrayList<Firma>());
			// Informa les firmes
			for (ArxiuFirmaDto firmaDto: firmes) {
				Firma firma = new Firma();
				firma.setFitxerNom(firmaDto.getFitxerNom());
				firma.setContingut(firmaDto.getContingut());
				if (firmaDto.getContingut() != null)
					firma.setTamany(-1);
				firma.setTipusMime(firmaDto.getTipusMime());
				setFirmaTipusPerfil(firma, firmaDto);					
				firma.setCsvRegulacio(firmaDto.getCsvRegulacio());
				document.getFirmes().add(firma);
			}
			primeraFirma = document.getFirmes().get(0);
		}
		// Contingut
		DocumentContingut contingut = new DocumentContingut();
		if (fitxer != null) {
			contingut.setArxiuNom(fitxer.getNom());
			contingut.setContingut(fitxer.getContingut());
			contingut.setTipusMime(fitxer.getContentType());
		}
		
		if (primeraFirma != null )
		{
			// Document firmat
			if (primeraFirma.getTipus() != null && TIPUS_FIRMES_ATTACHED.contains(FirmaTipus.valueOf(primeraFirma.getTipus().name()))) {

				// Firma attached
				if (es.caib.plugins.arxiu.api.FirmaTipus.PADES.equals(primeraFirma.getTipus())) {
					// Pot ser que el contingut hagi vingut informat com a contingut i no com a firma
					if (primeraFirma.getContingut() == null) {
						primeraFirma.setContingut(contingut.getContingut());
					}
					// el contingut és null pel cas dels PADES
					contingut = null;
					if (fitxer != null) {
						primeraFirma.setFitxerNom(fitxer.getNom());
						primeraFirma.setTipusMime(fitxer.getContentType());
					}
				} else { 
					// CADES i XADES
					if (primeraFirma.getContingut() != null)
						contingut.setContingut(primeraFirma.getContingut());

					if (es.caib.plugins.arxiu.api.FirmaTipus.XADES_ENV.equals(primeraFirma.getTipus())) {
						if (contingut.getArxiuNom() == null) {
							contingut.setArxiuNom(primeraFirma.getFitxerNom() != null ? primeraFirma.getFitxerNom() : "firma.xsig");
						} else if (!contingut.getArxiuNom().toLowerCase().endsWith(".xsig")) {
							contingut.setArxiuNom(contingut.getArxiuNom() + ".xsig");
						}
						extensio = DocumentExtensio.toEnum(".xsig");
						contingut.setTipusMime("application/xml");
					}
				}
			}
		}
		
		if (extensio != null) {
			metadades.setExtensio(extensio);
			DocumentFormat format = this.getDocumentFormat(extensio);
			metadades.setFormat(format);
		}
		metadades.setOrgans(ntiOrgans);
		
		String serieDocumental = getPropertyPluginRegistreExpedientSerieDocumental();
		if (serieDocumental != null && !serieDocumental.isEmpty()) {
			metadades.setSerieDocumental(serieDocumental);
		}
		
		Map<String, Object> metaDadesAddicionals = new HashMap<String, Object>();
		metaDadesAddicionals.put("eni:numero_asiento_registral", documentEniRegistrableDto.getNumero());
		if (documentEniRegistrableDto.getData() != null) {
			TimeZone tz = TimeZone.getTimeZone("UTC");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			df.setTimeZone(tz);
			metaDadesAddicionals.put("eni:fecha_asiento_registral", df.format(documentEniRegistrableDto.getData()));
		}
		metaDadesAddicionals.put("eni:codigo_oficina_registro", documentEniRegistrableDto.getOficinaCodi());
		metaDadesAddicionals.put("eni:tipo_asiento_registral", new Integer("0"));
		
		if (metaDades != null && !metaDades.isEmpty()) {
			Map<String, String> metaDadesMap;
			try {
				metaDadesMap = new ObjectMapper().readValue(metaDades, new TypeReference<Map<String, String>>(){});

				for (String key : metaDadesMap.keySet()) {
					metaDadesAddicionals.put(key,
							metaDadesMap.get(key));
				}

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		metadades.setMetadadesAddicionals(metaDadesAddicionals);
	
		document.setMetadades(metadades);
		document.setEstat(estat);
		document.setContingut(contingut);

		return document;
	}

	private void setFirmaTipusPerfil(
			Firma firma,
			ArxiuFirmaDto arxiuFirmaDto) {
		if (arxiuFirmaDto.getTipus() != null) {
			switch(arxiuFirmaDto.getTipus()) {
			case CSV:
				firma.setTipus(FirmaTipus.CSV);
				break;
			case XADES_DET:
				firma.setTipus(FirmaTipus.XADES_DET);
				break;
			case XADES_ENV:
				firma.setTipus(FirmaTipus.XADES_ENV);
				break;
			case CADES_DET:
				firma.setTipus(FirmaTipus.CADES_DET);
				break;
			case CADES_ATT:
				firma.setTipus(FirmaTipus.CADES_ATT);
				break;
			case PADES:
				firma.setTipus(FirmaTipus.PADES);
				break;
			case SMIME:
				firma.setTipus(FirmaTipus.SMIME);
				break;
			case ODT:
				firma.setTipus(FirmaTipus.ODT);
				break;
			case OOXML:
				firma.setTipus(FirmaTipus.OOXML);
				break;
			}
		}
		if (arxiuFirmaDto.getPerfil() != null) {
			switch(arxiuFirmaDto.getPerfil()) {
			case BES:
				firma.setPerfil(FirmaPerfil.BES);
				break;
			case EPES:
				firma.setPerfil(FirmaPerfil.EPES);
				break;
			case LTV:
				firma.setPerfil(FirmaPerfil.LTV);
				break;
			case T:
				firma.setPerfil(FirmaPerfil.T);
				break;
			case C:
				firma.setPerfil(FirmaPerfil.C);
				break;
			case X:
				firma.setPerfil(FirmaPerfil.X);
				break;
			case XL:
				firma.setPerfil(FirmaPerfil.XL);
				break;
			case A:
				firma.setPerfil(FirmaPerfil.A);
				break;
			case BASELINE_B_LEVEL:
				firma.setPerfil(FirmaPerfil.BASELINE_B_LEVEL);
				break;
			case BASELINE_LTA_LEVEL:
				firma.setPerfil(FirmaPerfil.BASELINE_LTA_LEVEL);
				break;
			case BASELINE_LT_LEVEL:
				firma.setPerfil(FirmaPerfil.BASELINE_LT_LEVEL);
				break;
			case BASELINE_T:
				firma.setPerfil(FirmaPerfil.BASELINE_T);
				break;
			case BASELINE_T_LEVEL:
				firma.setPerfil(FirmaPerfil.BASELINE_T_LEVEL);
				break;
			case BASIC:
				firma.setPerfil(FirmaPerfil.BASIC);
				break;
			case LTA:
				firma.setPerfil(FirmaPerfil.LTA);
				break;
			}
		}
	}



	private void integracioAddAccioOk(
			String integracioCodi,
			String descripcio,
			Map<String, String> parametres,
			long tempsResposta) {
		this.integracioAddAccioOk(
				integracioCodi, 
				descripcio, 
				this.getUsuariIntegracio(),
				parametres, 
				tempsResposta);		
	}
	
	private void integracioAddAccioOk(
			String integracioCodi,
			String descripcio,
			String usuariIntegracio,
			Map<String, String> parametres,
			long tempsResposta) {
		if (integracioManager != null) {
			integracioManager.addAccioOk(
					integracioCodi,
					descripcio,
					usuariIntegracio,
					parametres,
					tempsResposta);
		}
	}

	private void integracioAddAccioError(
			String integracioCodi,
			String descripcio,
			Map<String, String> parametres,
			long tempsResposta,
			String errorDescripcio,
			Throwable throwable) {
		this.integracioAddAccioError(
				integracioCodi, 
				descripcio, 
				this.getUsuariIntegracio(), 
				parametres, 
				tempsResposta, 
				errorDescripcio, 
				throwable);
	}

	private void integracioAddAccioError(
			String integracioCodi,
			String descripcio,
			String usuariIntegracio,
			Map<String, String> parametres,
			long tempsResposta,
			String errorDescripcio,
			Throwable throwable) {
		if (integracioManager != null) {
			integracioManager.addAccioError(
					integracioCodi,
					descripcio,
					usuariIntegracio,
					parametres,
					tempsResposta,
					errorDescripcio,
					throwable);
		}
	}
	private IArxiuPlugin getArxiuPlugin() throws SistemaExternException {
		if (arxiuPlugin == null) {
			String pluginClass = getPropertyPluginArxiu();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					arxiuPlugin = (IArxiuPlugin)clazz.getDeclaredConstructor(
													String.class, 
													Properties.class)
									.newInstance("es.caib.distribucio.", this.getProperties());		
				} catch (Exception ex) {
					throw new SistemaExternException(
							integracioArxiuCodi,
							"Error al crear la instància del plugin d'arxiu digital",
							ex);
				}
			} else {
				throw new SistemaExternException(
						integracioArxiuCodi,
						"No està configurada la classe per al plugin d'arxiu digital");
			}
		}
		return arxiuPlugin;
	}
	private SignaturaPlugin getSignaturaPlugin() throws SistemaExternException {
		if (signaturaPlugin == null) {
			String pluginClass = getPropertyPluginSignatura();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					signaturaPlugin = (SignaturaPlugin)clazz.getDeclaredConstructor(Properties.class)
							.newInstance(this.getProperties());
				} catch (Exception ex) {
					throw new SistemaExternException(
							integracioSignaturaCodi,
							"Error al crear la instància del plugin de signatura",
							ex);
				}
			} else {
				throw new SistemaExternException(
						integracioSignaturaCodi,
						"No està configurada la classe per al plugin de signatura");
			}
		}
		return signaturaPlugin;
	}
	private GestioDocumentalPlugin getGestioDocumentalPlugin() throws SistemaExternException {
		if (gestioDocumentalPlugin == null) {
			String pluginClass = getPropertyPluginGestioDocumental();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					gestioDocumentalPlugin = (GestioDocumentalPlugin)clazz.getDeclaredConstructor(Properties.class)
							.newInstance(this.getProperties());
				} catch (Exception ex) {
					throw new SistemaExternException(
							itegracioGesdocCodi,
							"Error al crear la instància del plugin de gestió documental",
							ex);
				}
			} else {
				throw new SistemaExternException(
						itegracioGesdocCodi,
						"La classe del plugin de gestió documental no està configurada");
			}
		}
		return gestioDocumentalPlugin;
	}

	private boolean isRegistreSignarAnnexos() {
		return this.getPropertyPluginRegistreSignarAnnexos();
	}
	private String getPropertyPluginArxiu() {
		return getProperty(
				"es.caib.distribucio.plugin.arxiu.class");
	}
	private String getPropertyPluginRegistreExpedientClassificacio() {
		return getProperty(
				"es.caib.distribucio.anotacions.registre.expedient.classificacio");
	}
	private String getPropertyPluginRegistreExpedientSerieDocumental() {
		return getProperty(
				"es.caib.distribucio.anotacions.registre.expedient.serie.documental");
	}
	private String getPropertyPluginGestioDocumental() {
		return getProperty("es.caib.distribucio.plugin.gesdoc.class");
	}
	private boolean getPropertyPluginRegistreSignarAnnexos() {
		return new Boolean(getProperty(
				"es.caib.distribucio.plugin.signatura.signarAnnexos")).booleanValue();
	}
	private String getPropertyPluginSignatura() {
		return getProperty(
				"es.caib.distribucio.plugin.signatura.class");
	}
	/** Determina si guardar com a esborrany annexos sense firma vàlida. Per defecte fals. */
	private boolean getPropertyGuardarAnnexosFirmesInvalidesComEsborrany() {
		return new Boolean(this.getProperties().getProperty(
				"es.caib.distribucio.tasca.guardar.annexos.firmes.invalides.com.esborrany")).booleanValue();
	}
	
	@Override
	public String getUsuariIntegracio() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null ? authentication.getName() : null;
	}


	private static final Logger logger = LoggerFactory.getLogger(DistribucioPlugin.class);

}
