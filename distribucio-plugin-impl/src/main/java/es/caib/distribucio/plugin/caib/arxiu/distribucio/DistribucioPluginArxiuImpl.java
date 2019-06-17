/**
 * 
 */
package es.caib.distribucio.plugin.caib.arxiu.distribucio;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import es.caib.distribucio.core.api.dto.ArxiuFirmaDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.core.api.dto.DocumentEniRegistrableDto;
import es.caib.distribucio.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.distribucio.core.api.dto.DocumentNtiTipoDocumentalEnumDto;
import es.caib.distribucio.core.api.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.distribucio.core.api.dto.ExpedientEstatEnumDto;
import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.NtiOrigenEnumDto;
import es.caib.distribucio.core.api.exception.ValidationException;
import es.caib.distribucio.core.api.registre.RegistreAnnexElaboracioEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreAnnexNtiTipusDocumentEnum;
import es.caib.distribucio.core.api.registre.RegistreAnnexOrigenEnum;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.distribucio.DistribucioPlugin;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnnex;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnotacio;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreFirma;
import es.caib.distribucio.plugin.gesdoc.GestioDocumentalPlugin;
import es.caib.distribucio.plugin.signatura.SignaturaPlugin;
import es.caib.distribucio.plugin.utils.PropertiesHelper;
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

/**
 * Implementació del plugin de distribució que utilitza
 * els següents serveis de la CAIB:
 *   · Arxiu digital
 *   · Firma en servidor
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DistribucioPluginArxiuImpl implements DistribucioPlugin {


	private IntegracioManager integracioManager;
	private String itegracioGesdocCodi = "GESDOC";
	private String integracioArxiuCodi = "ARXIU";
	private String integracioSignaturaCodi = "SIGNATURA";
	private String gesdocAgrupacioAnnexos = "anotacions_registre_doc_tmp";
	private String gesdocAgrupacioFirmes = "anotacions_registre_fir_tmp";

	private IArxiuPlugin arxiuPlugin;
	private SignaturaPlugin signaturaPlugin;
	private GestioDocumentalPlugin gestioDocumentalPlugin;


	@Override
	public String contenidorCrear(
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
	public String documentCrear(
			DistribucioRegistreAnnex distribucioAnnex,
			String unitatArrelCodi,
			String uuidExpedient,
			DocumentEniRegistrableDto documentEniRegistrableDto) throws SistemaExternException {
		
		List<ArxiuFirmaDto> arxiuFirmes = null;
		byte[] annexContingut = null;
		
		// if annex was already created in arxiu 
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
					switch (firma.getTipus()) {
					case CSV:
						arxiuFirma.setTipus(ArxiuFirmaTipusEnumDto.CSV);
						break;
					case XADES_DET:
						arxiuFirma.setTipus(ArxiuFirmaTipusEnumDto.XADES_DET);
						break;
					case XADES_ENV:
						arxiuFirma.setTipus(ArxiuFirmaTipusEnumDto.XADES_ENV);
						break;
					case CADES_DET:
						arxiuFirma.setTipus(ArxiuFirmaTipusEnumDto.CADES_DET);
						break;
					case CADES_ATT:
						arxiuFirma.setTipus(ArxiuFirmaTipusEnumDto.CADES_ATT);
						break;
					case PADES:
						arxiuFirma.setTipus(ArxiuFirmaTipusEnumDto.PADES);
						break;
					case SMIME:
						arxiuFirma.setTipus(ArxiuFirmaTipusEnumDto.SMIME);
						break;
					case ODT:
						arxiuFirma.setTipus(ArxiuFirmaTipusEnumDto.ODT);
						break;
					case OOXML:
						arxiuFirma.setTipus(ArxiuFirmaTipusEnumDto.OOXML);
						break;
					}
					switch (firma.getPerfil()) {
					case BES:
						arxiuFirma.setPerfil(ArxiuFirmaPerfilEnumDto.BES);
						break;
					case EPES:
						arxiuFirma.setPerfil(ArxiuFirmaPerfilEnumDto.EPES);
						break;
					case LTV:
						arxiuFirma.setPerfil(ArxiuFirmaPerfilEnumDto.LTV);
						break;
					case T:
						arxiuFirma.setPerfil(ArxiuFirmaPerfilEnumDto.T);
						break;
					case C:
						arxiuFirma.setPerfil(ArxiuFirmaPerfilEnumDto.C);
						break;
					case X:
						arxiuFirma.setPerfil(ArxiuFirmaPerfilEnumDto.X);
						break;
					case XL:
						arxiuFirma.setPerfil(ArxiuFirmaPerfilEnumDto.XL);
						break;
					case A:
						arxiuFirma.setPerfil(ArxiuFirmaPerfilEnumDto.A);
						break;
					}
					arxiuFirma.setFitxerNom(firma.getFitxerNom());
					arxiuFirma.setContingut(firma.getContingut());
					arxiuFirma.setTipusMime(firma.getTipusMime());
					arxiuFirma.setCsvRegulacio(firma.getCsvRegulacio());
					arxiuFirmes.add(arxiuFirma);
				}
			}
		// if annex is not created in arxiu 	
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
		
		FitxerDto fitxerContingut = new FitxerDto();
		fitxerContingut.setNom(distribucioAnnex.getFitxerNom());
		fitxerContingut.setContentType(distribucioAnnex.getFitxerTipusMime());
		fitxerContingut.setContingut(annexContingut);
		fitxerContingut.setTamany(distribucioAnnex.getFitxerTamany());
		
		if (annexContingut != null) {
			// Si l'annex no està firmat el firma amb el plugin de firma
			// en servidor.
			boolean annexFirmat = arxiuFirmes != null && !arxiuFirmes.isEmpty();
			if (!annexFirmat && isRegistreSignarAnnexos()) {
				String tipusFirmaServidor;
				String tipusFirmaArxiu = null;
				String perfil = null;
				String fitxerNom = null;
				String tipusMime = null;
				String csvRegulacio = null;

				if ("application/pdf".equalsIgnoreCase(distribucioAnnex.getFitxerTipusMime())) {
					tipusFirmaServidor = "CADES";
					tipusFirmaArxiu = DocumentNtiTipoFirmaEnumDto.TF04.toString();
					perfil = FirmaPerfil.BES.toString();
					fitxerNom = distribucioAnnex.getFitxerNom() + "_cades_det.csig";

					tipusMime = "application/pdf";
				} else {
					tipusFirmaServidor = "CADES";
					tipusFirmaArxiu = DocumentNtiTipoFirmaEnumDto.TF04.toString();
					perfil = FirmaPerfil.BES.toString();
					fitxerNom = distribucioAnnex.getFitxerNom() + "_cades_det.csig";
					tipusMime = "application/octet-stream";
				}
				//sign annex and return firma content bytes
				byte[] firmaDistribucioContingut = signaturaDistribucioSignar(
						distribucioAnnex,
						annexContingut,
						"Firma en servidor de document annex de l'anotació de registre",
						tipusFirmaServidor);
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
			}
		}
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
	public void contenidorMarcarProcessat(
			DistribucioRegistreAnotacio anotacio) throws SistemaExternException {
		String accioDescripcio = "Tancant expedient";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("identificador", anotacio.getExpedientArxiuUuid());
		long t0 = System.currentTimeMillis();
		try {
			getArxiuPlugin().expedientTancar(
					anotacio.getExpedientArxiuUuid());
			integracioAddAccioOk(
					integracioArxiuCodi,
					accioDescripcio,
					accioParams,
					System.currentTimeMillis() - t0);
		} catch (Exception ex) {
			String errorDescripcio = "Error al tancar rexpedient";
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



	private String arxiuDocumentAnnexCrear(
			DistribucioRegistreAnnex annex,
			String unitatArrelCodi,
			FitxerDto fitxer,
			List<ArxiuFirmaDto> firmes,
			String identificadorPare,
			DocumentEniRegistrableDto documentEniRegistrableDto) throws SistemaExternException {
		
		DocumentEstat estatDocument = DocumentEstat.ESBORRANY;
		if (annex.getFirmes() != null && !annex.getFirmes().isEmpty()) {
			estatDocument = DocumentEstat.DEFINITIU;
		}
		//creating info for integracio logs
		String accioDescripcio = "Creant document annex";
		Map<String, String> accioParams = new HashMap<String, String>();
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
		
		long t0 = System.currentTimeMillis();
		try {
			
			
			String annexTitol = inArxiu(
					annex.getTitol(),
					identificadorPare);
			
			ContingutArxiu contingutFitxer = getArxiuPlugin().documentCrear(
					toArxiuDocument(
							null,
							annexTitol,
							fitxer,
							firmes,
							null,
							(annex.getOrigenCiutadaAdmin() != null ? NtiOrigenEnumDto.values()[Integer.valueOf(RegistreAnnexOrigenEnum.valueOf(annex.getOrigenCiutadaAdmin()).getValor())] : null),
							Arrays.asList(unitatArrelCodi),
							annex.getDataCaptura(),
							(annex.getNtiElaboracioEstat() != null ? DocumentNtiEstadoElaboracionEnumDto.valueOf(RegistreAnnexElaboracioEstatEnum.valueOf(annex.getNtiElaboracioEstat()).getValor()) : null),
							(annex.getNtiTipusDocument() != null ? DocumentNtiTipoDocumentalEnumDto.valueOf(RegistreAnnexNtiTipusDocumentEnum.valueOf(annex.getNtiTipusDocument()).getValor()) : null),
							estatDocument,
							documentEniRegistrableDto),
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
	
	
	
	private String inArxiu(
			String arxiuNom,
			String identificadorPare) throws ArxiuException, SistemaExternException {

		// geting all docuements of an expedient saved already in arxiu
		List<ContingutArxiu> continguts = getArxiuPlugin().expedientDetalls(
				identificadorPare,
				null).getContinguts();
		int ocurrences = 0;
		if (continguts != null) {
			List<String> noms = new ArrayList<String>();
			// Itreating over the files and saving their names in a List
			for (ContingutArxiu contingut : continguts) {
				noms.add(contingut.getNom());
			}
			// comping the name of the new file we try to store
			String nName = new String(arxiuNom);
			
			// Checking if the file name exist in the expedient
			while (noms.indexOf(nName) >= 0) {
				// if it does 'ocurrences' increments
				ocurrences++;
				// and the number of ocurences is added to the file name
				// and it checks again if the new file name exists
				nName = arxiuNom + " (" + ocurrences + ")";
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
				firma.setPerfil(
						ArxiuFirmaPerfilEnumDto.valueOf(annexFirma.getPerfil()));
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

	private byte[] signaturaDistribucioSignar(
			DistribucioRegistreAnnex annex,
			byte[] annexContingut,
			String motiu,
			String tipusFirma) throws SistemaExternException {
		String accioDescripcio = "Firma en servidor de document annex de l'anotació de registre";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("annexId", annex.getId().toString());
		accioParams.put("annexFitxerNom", annex.getFitxerNom());
		accioParams.put("annexFitxerContingut", (annexContingut != null ? "" + annexContingut.length : "0") + " bytes");
		accioParams.put("motiu", motiu);
		accioParams.put("tipusFirma", tipusFirma);
		long t0 = System.currentTimeMillis();
		try {
			byte[] firmaContingut = getSignaturaPlugin().signar(
					annex.getId().toString(),
					annex.getFitxerNom(),
					motiu,
					tipusFirma,
					annexContingut);
			integracioAddAccioOk(
					integracioSignaturaCodi,
					accioDescripcio,
					accioParams,
					System.currentTimeMillis() - t0);
			return firmaContingut;
		} catch (Exception ex) {
			String errorDescripcio = "Error al firmar document en servidor";
			integracioAddAccioError(
					integracioSignaturaCodi,
					accioDescripcio,
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
		String accioDescripcio = "Obtenint detalls del document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("arxiuUuid", arxiuUuid);
		accioParams.put("versio", versio);
		accioParams.put("ambContingut", new Boolean(ambContingut).toString());
		long t0 = System.currentTimeMillis();
		Document documentDetalls = null;
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
			String errorDescripcio = "Error al obtenir detalls del document";
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
		if (ambVersioImprimible && ambContingut && documentDetalls.getFirmes() != null && !documentDetalls.getFirmes().isEmpty()) {
			boolean generarVersioImprimible = false;
			
			for (Firma firma : documentDetalls.getFirmes()) {
				if (documentDetalls.getContingut().getTipusMime().equals("application/pdf") && (firma.getTipus() == FirmaTipus.PADES || firma.getTipus() == FirmaTipus.CADES_ATT || firma.getTipus() == FirmaTipus.CADES_DET)) {
					generarVersioImprimible = true;
				}
			}
			if (generarVersioImprimible) {
				generarVersioImprimible(documentDetalls);
			}
		}
		return documentDetalls;
	}

	private void generarVersioImprimible(
			Document documentDetalls) throws SistemaExternException {
		String accioDescripcio = "Generant versió imprimible del document";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("identificador", documentDetalls.getIdentificador());
		long t0 = System.currentTimeMillis();
		try {
			documentDetalls.setContingut(
					getArxiuPlugin().documentImprimible(
							documentDetalls.getIdentificador()));
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

	private Document toArxiuDocument(
			String identificador,
			String nom,
			FitxerDto fitxer,
			List<ArxiuFirmaDto> firmes, 
			String ntiIdentificador,
			NtiOrigenEnumDto ntiOrigen,
			List<String> ntiOrgans,
			Date ntiDataCaptura,
			DocumentNtiEstadoElaboracionEnumDto ntiEstatElaboracio,
			DocumentNtiTipoDocumentalEnumDto ntiTipusDocumental,
			DocumentEstat estat,
			DocumentEniRegistrableDto documentEniRegistrableDto) {
		Document document = new Document();
		document.setNom(nom);
		document.setIdentificador(identificador);
		DocumentMetadades metadades = new DocumentMetadades();
		metadades.setIdentificador(ntiIdentificador);
		if (ntiOrigen != null) {
			switch (ntiOrigen) {
			case O0:
				metadades.setOrigen(ContingutOrigen.CIUTADA);
				break;
			case O1:
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
		DocumentExtensio extensio = null;
		DocumentContingut contingut = null;
		if (fitxer != null) {
			String fitxerExtensio = fitxer.getExtensio();
			String extensioAmbPunt = (fitxerExtensio.startsWith(".")) ? fitxerExtensio.toLowerCase() : "." + fitxerExtensio.toLowerCase();
			extensio = DocumentExtensio.toEnum(extensioAmbPunt);
			if (fitxer.getContingut() != null) {
				contingut = new DocumentContingut();
				contingut.setArxiuNom(fitxer.getNom());
				contingut.setContingut(fitxer.getContingut());
				contingut.setTipusMime(fitxer.getContentType());
			}
		}
		if (firmes != null && firmes.size() > 0) {
			if (document.getFirmes() == null) {
				document.setFirmes(new ArrayList<Firma>());
			}
			for (ArxiuFirmaDto firmaDto: firmes) {
				Firma firma = new Firma();
				firma.setContingut(firmaDto.getContingut());
				firma.setCsvRegulacio(firmaDto.getCsvRegulacio());
				firma.setFitxerNom(firmaDto.getFitxerNom());
				if (firmaDto.getContingut() != null)
					firma.setTamany(firmaDto.getContingut().length);

				if (firmaDto.getPerfil() != null) {
					switch(firmaDto.getPerfil()) {
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
					}
				}
				if (firmaDto.getTipus() != null) {
					switch(firmaDto.getTipus()) {
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
				firma.setTipusMime(firmaDto.getTipusMime());
				document.getFirmes().add(firma);
			}
		}
		if (extensio != null) {
			metadades.setExtensio(extensio);
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
			metadades.setFormat(format);
		}
		metadades.setOrgans(ntiOrgans);
		
		

		
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
		
		metadades.setMetadadesAddicionals(metaDadesAddicionals);
	
		document.setMetadades(metadades);
		document.setContingut(contingut);
		document.setEstat(estat);
		return document;
	}

	private void integracioAddAccioOk(
			String integracioCodi,
			String descripcio,
			Map<String, String> parametres,
			long tempsResposta) {
		if (integracioManager != null) {
			integracioManager.addAccioOk(
					integracioCodi,
					descripcio,
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
		if (integracioManager != null) {
			integracioManager.addAccioError(
					integracioCodi,
					descripcio,
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
					signaturaPlugin = (SignaturaPlugin)clazz.newInstance();
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
	private boolean gestioDocumentalPluginConfiguracioProvada = false;
	private GestioDocumentalPlugin getGestioDocumentalPlugin() throws SistemaExternException {
		if (gestioDocumentalPlugin == null && !gestioDocumentalPluginConfiguracioProvada) {
			gestioDocumentalPluginConfiguracioProvada = true;
			String pluginClass = getPropertyPluginGestioDocumental();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					gestioDocumentalPlugin = (GestioDocumentalPlugin)clazz.newInstance();
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
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.plugin.arxiu.class");
	}
	private String getPropertyPluginRegistreExpedientClassificacio() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.anotacions.registre.expedient.classificacio");
	}
	private String getPropertyPluginRegistreExpedientSerieDocumental() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.anotacions.registre.expedient.serie.documental");
	}
	private String getPropertyPluginGestioDocumental() {
		return PropertiesHelper.getProperties().getProperty("es.caib.distribucio.plugin.gesdoc.class");
	}
	private boolean getPropertyPluginRegistreSignarAnnexos() {
		return PropertiesHelper.getProperties().getAsBoolean(
				"es.caib.distribucio.plugin.signatura.signarAnnexos");
	}
	private String getPropertyPluginSignatura() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.plugin.signatura.class");
	}

}
