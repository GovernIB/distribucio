package es.caib.distribucio.plugin.caib.arxiu.distribucio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.distribucio.core.api.dto.ArxiuFirmaDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.core.api.dto.DocumentNtiEstadoElaboracionEnumDto;
import es.caib.distribucio.core.api.dto.DocumentNtiTipoDocumentalEnumDto;
import es.caib.distribucio.core.api.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.distribucio.core.api.dto.ExpedientEstatEnumDto;
import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.NtiOrigenEnumDto;
import es.caib.distribucio.core.api.exception.BustiaServiceException;
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

public class DistribucioPluginArxiuImpl implements DistribucioPlugin {
	
	private IArxiuPlugin arxiuPlugin;
	private GestioDocumentalPlugin gestioDocumentalPlugin;
	private SignaturaPlugin signaturaPlugin;

	private final String INTCODI_ARXIU = "ARXIU";
	private final String INTCODI_USUARIS = "USUARIS";
	private final String INTCODI_GESDOC = "GESDOC";
	private final String INTCODI_SIGNATURA = "SIGNATURA";
	
	private final String GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP = "anotacions_registre_doc_tmp";
	private final String GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP = "anotacions_registre_fir_tmp";
	
	
	@Override
	public void eliminarContingutExistent(String idContingut) throws SistemaExternException {
		try {
			getArxiuPlugin().expedientEsborrar(idContingut);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			throw new SistemaExternException(
					this.INTCODI_ARXIU,
					errorDescripcio,
					ex);
		}
	}

	@Override
	public String ditribuirAssentament(DistribucioRegistreAnotacio anotacio, String unitatArrelCodi)
			throws SistemaExternException {
		
		ContingutArxiu expedientCreat = null;
		if (anotacio.getAnnexos() != null && anotacio.getAnnexos().size() > 0 && anotacio.getExpedientArxiuUuid() == null) {
			expedientCreat = arxiuExpedientPerAnotacioCrear(anotacio, unitatArrelCodi);
			anotacio.setExpedientArxiuUuid(expedientCreat.getIdentificador());
			processarAnnexosRegistre(anotacio, unitatArrelCodi, expedientCreat);
		}
		
		return expedientCreat != null ? expedientCreat.getIdentificador() : null;
	}
	
	@Override
	public void marcarProcessat(DistribucioRegistreAnotacio anotacio) throws SistemaExternException {
		try {
			getArxiuPlugin().expedientTancar(
					anotacio.getExpedientArxiuUuid());
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			throw new SistemaExternException(
					this.INTCODI_ARXIU,
					errorDescripcio,
					ex);
		}
	}

	private ContingutArxiu arxiuExpedientPerAnotacioCrear(
			DistribucioRegistreAnotacio anotacio,
			String unitatArrelCodi) throws SistemaExternException {
		String nomExpedient = "EXP_REG_" + anotacio.getExpedientNumero() + "_" + System.currentTimeMillis();
		ContingutArxiu expedientCreat = getArxiuPlugin().expedientCrear(
				toArxiuExpedient(
						null,
						nomExpedient,
						null,
						Arrays.asList(unitatArrelCodi),
						new Date(),
						getPropertyPluginRegistreExpedientClassificacio(),
						ExpedientEstatEnumDto.OBERT,
						null,
						getPropertyPluginRegistreExpedientSerieDocumental()));
		
		return expedientCreat;
	}
	
	private void processarAnnexosRegistre(
			DistribucioRegistreAnotacio anotacio, 
			String unitatArrelCodi, 
			ContingutArxiu expedientCreat) {
		
		boolean isExpedientFinal = false;
		try {
			for (DistribucioRegistreAnnex annex: anotacio.getAnnexos()) {
				
				guardarDocumentAnnex (
						annex, 
						unitatArrelCodi, 
						expedientCreat);
				
				isExpedientFinal = true;
			}
		} catch (Exception ex) {
			try {
				if (isExpedientFinal) {
					getArxiuPlugin().expedientTancar(
							anotacio.getExpedientArxiuUuid());
					anotacio.setArxiuUuid(null);
					anotacio.setArxiuDataActualitzacio(null);
				} else {
					eliminarContingutExistent(anotacio.getExpedientArxiuUuid());
				}
			} catch (Exception ex2) {
				logger.error(
						"Error al eliminar o tancar l'expedient creat l'arxiu digital",
						ex2);
			}
			throw new BustiaServiceException(
					"Error al processar els annexos de l'anotació de registre",
					ex);
		}
	}
	
	private void guardarDocumentAnnex(
			DistribucioRegistreAnnex annex,
			String unitatArrelCodi,
			ContingutArxiu expedientCreat) throws IOException, SistemaExternException {
		byte[] contingut = null;
		if (annex.getGesdocDocumentId() != null) {
			ByteArrayOutputStream baos_doc = new ByteArrayOutputStream();
			
			getGestioDocumentalPlugin().get(
					annex.getGesdocDocumentId(),
					this.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP,
					baos_doc);
			
			contingut = baos_doc.toByteArray();
		} else {
			Document arxiuDocument = this.arxiuDocumentConsultar(
					annex.getFitxerArxiuUuid(),
					null,
					true,
					false);
			if (arxiuDocument.getContingut() != null) {
				contingut = arxiuDocument.getContingut().getContingut();
			} else {
				throw new ValidationException(
						"No s'ha trobat cap contingut per l'annex (" +
						"uuid=" + annex.getFitxerArxiuUuid() + ")");
			}
			
			
			if (arxiuDocument.getFirmes() != null && arxiuDocument.getFirmes().size() > 0) {
//				List<ArxiuFirmaDto> firmes = registreHelper.convertirFirmesAnnexToArxiuFirmaDto(annexEntity, null);
				Iterator<Firma> it = arxiuDocument.getFirmes().iterator();
				
				int firmaIndex = 0;
				while (it.hasNext()) {
					Firma arxiuFirma = it.next();
					if (!FirmaTipus.CSV.equals(arxiuFirma.getTipus()) && !FirmaTipus.PADES.equals(arxiuFirma.getTipus())) {
						DistribucioRegistreFirma firma = annex.getFirmes().get(firmaIndex);
						firma.setContingut(arxiuFirma.getContingut());
						firmaIndex++;
					} else {
						it.remove();
					}
				}
			}
		}
		
		FitxerDto fitxer = new FitxerDto();
		fitxer.setNom(annex.getFitxerNom());
		fitxer.setContingut(contingut);
		fitxer.setContentType(annex.getFitxerTipusMime());
		fitxer.setTamany(contingut.length);

		byte[] firmaDistribucioContingut = null;
		if (this.isRegistreSignarAnnexos() && annex.getFirmes().size() == 0) {
			// Distribucio signa amb el plugin de signatures els annexos sense firmes
			firmaDistribucioContingut = this.signaturaDistribucioSignar(
					annex,
					contingut);
			
			String tipus = null;
			String perfil = null;
			String fitxerNom = null;
			String tipusMime = null;
			String csvRegulacio = null;
			
			if ("application/pdf".equalsIgnoreCase(annex.getFitxerTipusMime())) {
				tipus = DocumentNtiTipoFirmaEnumDto.TF06.toString();
				perfil = FirmaPerfil.EPES.toString();
				fitxer.setContingut(firmaDistribucioContingut);
				firmaDistribucioContingut = null;
			} else {
				tipus = DocumentNtiTipoFirmaEnumDto.TF04.toString();
				perfil = FirmaPerfil.BES.toString();
				fitxerNom = annex.getFitxerNom() + "_cades_det.csig";
				tipusMime = "application/octet-stream";
			}
			
			DistribucioRegistreFirma annexFirma = new DistribucioRegistreFirma();
			annexFirma.setTipus(tipus);
			annexFirma.setPerfil(perfil);
			annexFirma.setFitxerNom(fitxerNom);
			annexFirma.setTipusMime(tipusMime);
			annexFirma.setCsvRegulacio(csvRegulacio);
			annexFirma.setAutofirma(true);
			annexFirma.setGesdocFirmaId(null);
			annexFirma.setContingut(contingut);
			annexFirma.setAnnex(annex);
			
//			DistribucioRegistreFirma annexFirma = new DistribucioRegistreFirma(
//					tipus, 
//					perfil, 
//					fitxerNom, 
//					tipusMime, 
//					csvRegulacio, 
//					true, 
//					null, 
//					firmaDistribucioContingut, 
//					annex);
			
			annex.getFirmes().add(annexFirma);
		}

		String uuidDocument = this.arxiuDocumentAnnexCrear(
				annex,
				unitatArrelCodi,
				fitxer,
				convertirFirmesAnnexToArxiuFirmaDto(annex, firmaDistribucioContingut),
				expedientCreat);
		annex.setFitxerArxiuUuid(uuidDocument);
	}
	
	private String arxiuDocumentAnnexCrear(
			DistribucioRegistreAnnex annex,
			String unitatArrelCodi,
			FitxerDto fitxer,
			List<ArxiuFirmaDto> firmes,
			ContingutArxiu expedient) throws SistemaExternException {
		DocumentEstat estatDocument = DocumentEstat.ESBORRANY;
		if (annex.getFirmes() != null && !annex.getFirmes().isEmpty()) {
			estatDocument = DocumentEstat.DEFINITIU;
		}
		try {
			ContingutArxiu contingutFitxer = getArxiuPlugin().documentCrear(
					toArxiuDocument(
							null,
							annex.getTitol(),
							fitxer,
							null,
							firmes,
							null,
							(annex.getOrigenCiutadaAdmin() != null ? NtiOrigenEnumDto.values()[Integer.valueOf(RegistreAnnexOrigenEnum.valueOf(annex.getOrigenCiutadaAdmin()).getValor())] : null),
							Arrays.asList(unitatArrelCodi),
							annex.getDataCaptura(),
							(annex.getNtiElaboracioEstat() != null ? DocumentNtiEstadoElaboracionEnumDto.valueOf(RegistreAnnexElaboracioEstatEnum.valueOf(annex.getNtiElaboracioEstat()).getValor()) : null),
							(annex.getNtiTipusDocument() != null ? DocumentNtiTipoDocumentalEnumDto.valueOf(RegistreAnnexNtiTipusDocumentEnum.valueOf(annex.getNtiTipusDocument()).getValor()) : null),
							estatDocument,
							false),
					expedient.getIdentificador());
			return contingutFitxer.getIdentificador();
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			throw new SistemaExternException(
					this.INTCODI_ARXIU,
					errorDescripcio,
					ex);
		}
	}
	
	private List<ArxiuFirmaDto> convertirFirmesAnnexToArxiuFirmaDto(
			DistribucioRegistreAnnex annex,
			byte[] firmaDistribucioContingut) throws SistemaExternException {
		List<ArxiuFirmaDto> firmes = null;
		if (annex.getFirmes() != null) {
			firmes = new ArrayList<ArxiuFirmaDto>();
			for (DistribucioRegistreFirma annexFirma: annex.getFirmes()) {
				byte[] firmaContingut = null;
				
				if (annexFirma.getGesdocFirmaId() != null) {
					ByteArrayOutputStream baos_fir = new ByteArrayOutputStream();
					this.gestioDocumentalGet(
							annexFirma.getGesdocFirmaId(), 
						this.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP, 
						baos_fir);
					firmaContingut = baos_fir.toByteArray();
				} else if(firmaDistribucioContingut != null) {
					firmaContingut = firmaDistribucioContingut;
				} else if (firmaDistribucioContingut == null && !"TF06".equalsIgnoreCase(annexFirma.getTipus())) {
						firmaContingut = annexFirma.getContingut();
				}
				
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
				firma.setContingut(firmaContingut);
				firmes.add(firma);
			}
		}
		return firmes;
	}
	
	private void gestioDocumentalGet(
			String id,
			String agrupacio,
			OutputStream contingutOut) throws SistemaExternException {
		try {
			getGestioDocumentalPlugin().get(
					id,
					agrupacio,
					contingutOut);
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin de gestió documental";
			throw new SistemaExternException(
					this.INTCODI_GESDOC,
					errorDescripcio,
					ex);
		}
	}
	
	private byte[] signaturaDistribucioSignar(
			DistribucioRegistreAnnex annex,
			byte[] annexContingut) throws SistemaExternException {
		try {
			String motiu = "Autofirma en servidor de DISTRIBUCIO";
			String tipusFirma;
			if ("application/pdf".equalsIgnoreCase(annex.getFitxerTipusMime()))
				tipusFirma = "PADES";
			else
				tipusFirma = "CADES";
			
			byte[] firmaContingut = getSignaturaPlugin().signar(
					annex.getId().toString(),
					annex.getFitxerNom(),
					motiu,
					tipusFirma,
					annexContingut);
			return firmaContingut;
		} catch (Exception ex) {
			String errorDescripcio = "Error en accedir al plugin de signatura";
			throw new SistemaExternException(
					this.INTCODI_SIGNATURA,
					errorDescripcio,
					ex);
		}
	}
	
	private Document arxiuDocumentConsultar(
			String arxiuUuid,
			String versio,
			boolean ambContingut,
			boolean ambVersioImprimible) throws SistemaExternException {
		try {
			Document documentDetalls = getArxiuPlugin().documentDetalls(
					arxiuUuid,
					versio,
					ambContingut);
			
			if (ambVersioImprimible && ambContingut && documentDetalls.getFirmes() != null && !documentDetalls.getFirmes().isEmpty()) {
				boolean isPdf = false;
				for (Firma firma : documentDetalls.getFirmes()) {
					if (firma.getTipus() == FirmaTipus.PADES) {
						isPdf = true;
					}
				}
				if (isPdf) {
					documentDetalls.setContingut(getArxiuPlugin().documentImprimible(documentDetalls.getIdentificador()));
				}
			}
			
			return documentDetalls;
		} catch (Exception ex) {
			String errorDescripcio = "Error al accedir al plugin d'arxiu digital: " + ex.getMessage();
			throw new SistemaExternException(
					this.INTCODI_ARXIU,
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
			FitxerDto firmaPdf,
			List<ArxiuFirmaDto> firmes, 
			String ntiIdentificador,
			NtiOrigenEnumDto ntiOrigen,
			List<String> ntiOrgans,
			Date ntiDataCaptura,
			DocumentNtiEstadoElaboracionEnumDto ntiEstatElaboracio,
			DocumentNtiTipoDocumentalEnumDto ntiTipusDocumental,
			DocumentEstat estat,
			boolean enPaper) {
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
		if (fitxer != null && !enPaper) {
			String fitxerExtensio = fitxer.getExtensio();
			String extensioAmbPunt = (fitxerExtensio.startsWith(".")) ? fitxerExtensio.toLowerCase() : "." + fitxerExtensio.toLowerCase();
			extensio = DocumentExtensio.toEnum(extensioAmbPunt);
			if (!isFirmesPades(firmes) && firmaPdf == null) {
				contingut = new DocumentContingut();
				contingut.setArxiuNom(fitxer.getNom());
				contingut.setContingut(fitxer.getContingut());
				contingut.setTipusMime(fitxer.getContentType());
			}
//			document.setContingut(contingut);
		}
		if (firmaPdf != null) {
			Firma firmaPades = new Firma();
			firmaPades.setTipus(FirmaTipus.PADES);
			firmaPades.setPerfil(FirmaPerfil.EPES);
			firmaPades.setTipusMime("application/pdf");
			firmaPades.setFitxerNom(firmaPdf.getNom());
			firmaPades.setContingut(firmaPdf.getContingut());
			document.setFirmes(Arrays.asList(firmaPades));
			extensio = DocumentExtensio.PDF;
		}
		if (firmes != null && firmes.size() > 0) {
			if (document.getFirmes() == null)
				document.setFirmes(new ArrayList<Firma>());
			for (ArxiuFirmaDto firmaDto: firmes) {
				Firma firma = new Firma();
				firma.setContingut(firmaDto.getContingut());
				firma.setCsvRegulacio(firmaDto.getCsvRegulacio());
				firma.setFitxerNom(firmaDto.getFitxerNom());
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
		document.setMetadades(metadades);
		document.setContingut(contingut);
		document.setEstat(estat);
		return document;
	}
	
	private boolean isFirmesPades(List<ArxiuFirmaDto> firmes) {
		for (ArxiuFirmaDto firma: firmes) {
			if (ArxiuFirmaTipusEnumDto.PADES == firma.getTipus())
				return true;
		}
		return false;
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
							this.INTCODI_ARXIU,
							"Error al crear la instància del plugin d'arxiu digital",
							ex);
				}
			} else {
				throw new SistemaExternException(
						this.INTCODI_ARXIU,
						"No està configurada la classe per al plugin d'arxiu digital");
			}
		}
		return arxiuPlugin;
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
							this.INTCODI_GESDOC,
							"Error al crear la instància del plugin de gestió documental",
							ex);
				}
			} else {
				throw new SistemaExternException(
						this.INTCODI_USUARIS,
						"La classe del plugin de gestió documental no està configurada");
			}
		}
		return gestioDocumentalPlugin;
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
							this.INTCODI_SIGNATURA,
							"Error al crear la instància del plugin de signatura",
							ex);
				}
			} else {
				throw new SistemaExternException(
						this.INTCODI_SIGNATURA,
						"No està configurada la classe per al plugin de signatura");
			}
		}
		return signaturaPlugin;
	}
	
	private boolean isRegistreSignarAnnexos() {
		return this.getPropertyPluginRegistreSignarAnnexos();
	}
	private String getPropertyPluginArxiu() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.plugin.arxiu.class");
	}
	private String getPropertyPluginRegistreExpedientClassificacio() {
		return PropertiesHelper.getProperties().getPropertyAmbComprovacio(
				"es.caib.distribucio.anotacions.registre.expedient.classificacio");
	}
	private String getPropertyPluginRegistreExpedientSerieDocumental() {
		return PropertiesHelper.getProperties().getPropertyAmbComprovacio(
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
	
	private static final Logger logger = LoggerFactory.getLogger(DistribucioPluginArxiuImpl.class);
	
}
