/**
 * 
 */
package es.caib.distribucio.core.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.namespace.QName;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.distribucio.core.api.dto.ArxiuFirmaDetallDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.core.api.dto.DocumentEniRegistrableDto;
import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.core.api.dto.LogTipusEnumDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDto;
import es.caib.distribucio.core.api.dto.ReglaTipusEnumDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.exception.SistemaExternException;
import es.caib.distribucio.core.api.exception.ValidationException;
import es.caib.distribucio.core.api.registre.Firma;
import es.caib.distribucio.core.api.registre.RegistreAnnex;
import es.caib.distribucio.core.api.registre.RegistreAnnexElaboracioEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreAnnexNtiTipusDocumentEnum;
import es.caib.distribucio.core.api.registre.RegistreAnnexOrigenEnum;
import es.caib.distribucio.core.api.registre.RegistreAnnexSicresTipusDocumentEnum;
import es.caib.distribucio.core.api.registre.RegistreAnotacio;
import es.caib.distribucio.core.api.registre.RegistreInteressat;
import es.caib.distribucio.core.api.registre.RegistreInteressatCanalEnum;
import es.caib.distribucio.core.api.registre.RegistreInteressatDocumentTipusEnum;
import es.caib.distribucio.core.api.registre.RegistreInteressatTipusEnum;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreTipusEnum;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.core.api.service.ws.backoffice.BackofficeWsService;
import es.caib.distribucio.core.entity.BackofficeEntity;
import es.caib.distribucio.core.entity.ContingutEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.RegistreAnnexEntity;
import es.caib.distribucio.core.entity.RegistreAnnexFirmaEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.RegistreFirmaDetallEntity;
import es.caib.distribucio.core.entity.RegistreInteressatEntity;
import es.caib.distribucio.core.entity.ReglaEntity;
import es.caib.distribucio.core.entity.UsuariEntity;
import es.caib.distribucio.core.repository.RegistreAnnexFirmaRepository;
import es.caib.distribucio.core.repository.RegistreAnnexRepository;
import es.caib.distribucio.core.repository.RegistreFirmaDetallRepository;
import es.caib.distribucio.core.repository.RegistreInteressatRepository;
import es.caib.distribucio.core.repository.RegistreRepository;
import es.caib.distribucio.core.service.RegistreServiceImpl;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnnex;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnotacio;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreFirma;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.FirmaTipus;

/**
 * Mètodes comuns per a aplicar regles.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class RegistreHelper {

	@Autowired
	private RegistreRepository registreRepository;
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;
	@Autowired
	private RegistreAnnexFirmaRepository registreAnnexFirmaRepository;
	@Autowired
	private RegistreInteressatRepository registreInteressatRepository;
	@Autowired
	RegistreFirmaDetallRepository registreFirmaDetallRepository;
	@Autowired
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private GestioDocumentalHelper gestioDocumentalHelper;	
	@Autowired
	private ReglaHelper reglaHelper;
	@Resource
	private EmailHelper emailHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private ContingutLogHelper contingutLogHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private MetricRegistry metricRegistry;
	@Autowired
	private HistogramPendentsHelper historicsPendentHelper;
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private IntegracioHelper integracioHelper;

	public RegistreAnotacio fromRegistreEntity(
			RegistreEntity entity) {
		RegistreAnotacio anotacio = new RegistreAnotacio();
		anotacio.setNumero(entity.getNumero());
		anotacio.setData(entity.getData());
		anotacio.setIdentificador(entity.getIdentificador());
		anotacio.setEntitatCodi(entity.getEntitatCodi());
		anotacio.setEntitatDescripcio(entity.getEntitatDescripcio());
		anotacio.setOficinaCodi(entity.getOficinaCodi());
		anotacio.setOficinaDescripcio(entity.getOficinaDescripcio());
		anotacio.setDataOrigen(entity.getDataOrigen());
		anotacio.setOficinaOrigenCodi(entity.getOficinaOrigenCodi());
		anotacio.setOficinaOrigenDescripcio(entity.getOficinaOrigenDescripcio());
		anotacio.setLlibreCodi(entity.getLlibreCodi());
		anotacio.setLlibreDescripcio(entity.getLlibreDescripcio());
		anotacio.setExtracte(entity.getExtracte());
		anotacio.setAssumpteTipusCodi(entity.getAssumpteTipusCodi());
		anotacio.setAssumpteTipusDescripcio(entity.getAssumpteTipusDescripcio());
		anotacio.setAssumpteCodi(entity.getAssumpteCodi());
		anotacio.setAssumpteDescripcio(entity.getAssumpteDescripcio());
		anotacio.setProcedimentCodi(entity.getProcedimentCodi());
		anotacio.setReferencia(entity.getReferencia());
		anotacio.setExpedientNumero(entity.getExpedientNumero());
		anotacio.setIdiomaCodi(entity.getIdiomaCodi());
		anotacio.setIdiomaDescripcio(entity.getIdiomaDescripcio());
		anotacio.setTransportTipusCodi(entity.getTransportTipusCodi());
		anotacio.setTransportTipusDescripcio(entity.getTransportTipusDescripcio());
		anotacio.setTransportNumero(entity.getTransportNumero());
		anotacio.setUsuariCodi(entity.getUsuariCodi());
		anotacio.setUsuariNom(entity.getUsuariNom());
		anotacio.setUsuariContacte(entity.getUsuariContacte());
		anotacio.setAplicacioCodi(entity.getAplicacioCodi());
		anotacio.setAplicacioVersio(entity.getAplicacioVersio());
		anotacio.setDocumentacioFisicaCodi(entity.getDocumentacioFisicaCodi());
		anotacio.setDocumentacioFisicaDescripcio(entity.getDocumentacioFisicaDescripcio());
		anotacio.setObservacions(entity.getObservacions());
		anotacio.setExposa(entity.getExposa());
		anotacio.setSolicita(entity.getSolicita());
		if (!entity.getInteressats().isEmpty()) {
			List<RegistreInteressat> interessats = new ArrayList<RegistreInteressat>();
			for (RegistreInteressatEntity interessat: entity.getInteressats()) {
				interessats.add(
						fromInteressatEntity(
								interessat));
			}
			anotacio.setInteressats(interessats);
		}
		if (!entity.getAnnexos().isEmpty()) {
			List<RegistreAnnex> annexos = new ArrayList<RegistreAnnex>();
			for (RegistreAnnexEntity annex: entity.getAnnexos()) {
				annexos.add(
						fromAnnexEntity(
								annex,
								anotacio));
			}
			anotacio.setAnnexos(annexos);
		}
		return anotacio;
	}
	
	public RegistreEntity crearRegistreEntity(
			EntitatEntity entitat,
			RegistreTipusEnum tipus,
			String unitatOrganitzativaCodi,
			RegistreAnotacio registreAnotacio,
			ReglaEntity regla,
			RegistreProcesEstatEnum estat) {
		
		
		
		
		final Timer timerfindPerEntitatAndCodi = metricRegistry.timer(MetricRegistry.name(RegistreHelper.class, "crearRegistreEntity.findPerEntitatAndCodi"));
		Timer.Context contextfindPerEntitatAndCodi = timerfindPerEntitatAndCodi.time();
		UnitatOrganitzativaDto unitat = unitatOrganitzativaHelper.findPerEntitatAndCodi(
				entitat.getCodi(),
				unitatOrganitzativaCodi);
		contextfindPerEntitatAndCodi.stop();
		
		final Timer timersaveRegistre = metricRegistry.timer(MetricRegistry.name(RegistreHelper.class, "crearRegistreEntity.saveRegistre"));
		Timer.Context contextsaveRegistre = timersaveRegistre.time();
		String justificantArxiuUuid = null;
		if (registreAnotacio.getJustificant() != null) {
			justificantArxiuUuid = registreAnotacio.getJustificant().getFitxerArxiuUuid();
		}
		
		
		RegistreEntity registre = null;
		RegistreEntity registreRepetit = registreRepository.findByEntitatCodiAndLlibreCodiAndRegistreTipusAndNumeroAndData(
				registreAnotacio.getEntitatCodi(),
				registreAnotacio.getLlibreCodi(),
				RegistreTipusEnum.ENTRADA.getValor(),
				registreAnotacio.getNumero(),
				registreAnotacio.getData());
		
		if (registreRepetit != null) {
			
			registreRepetit.override(
					entitat,
					tipus,
					unitatOrganitzativaCodi,
					unitat != null ? unitat.getDenominacio() : null,
					registreAnotacio.getNumero(),
					registreAnotacio.getData(),
					0,
					registreAnotacio.getIdentificador(),
					registreAnotacio.getExtracte(),
					registreAnotacio.getOficinaCodi(),
					registreAnotacio.getLlibreCodi(),
					registreAnotacio.getAssumpteTipusCodi(),
					registreAnotacio.getIdiomaCodi(),
					estat,
					null,
					registreAnotacio.getEntitatCodi(),
					registreAnotacio.getEntitatDescripcio(),
					registreAnotacio.getOficinaDescripcio(),
					registreAnotacio.getLlibreDescripcio(),
					registreAnotacio.getAssumpteTipusDescripcio(),
					registreAnotacio.getAssumpteCodi(),
					registreAnotacio.getAssumpteDescripcio(),
					registreAnotacio.getProcedimentCodi(),
					registreAnotacio.getReferencia(),
					registreAnotacio.getExpedientNumero(),
					registreAnotacio.getNumeroOrigen(),
					registreAnotacio.getIdiomaDescripcio(),
					registreAnotacio.getTransportTipusCodi(),
					registreAnotacio.getTransportTipusDescripcio(),
					registreAnotacio.getTransportNumero(),
					registreAnotacio.getUsuariCodi(),
					registreAnotacio.getUsuariNom(),
					registreAnotacio.getUsuariContacte(),
					registreAnotacio.getAplicacioCodi(),
					registreAnotacio.getAplicacioVersio(),
					registreAnotacio.getDocumentacioFisicaCodi(),
					registreAnotacio.getDocumentacioFisicaDescripcio(),
					registreAnotacio.getObservacions(),
					registreAnotacio.getExposa(),
					registreAnotacio.getDataOrigen(),
					registreAnotacio.getOficinaOrigenCodi(),
					registreAnotacio.getOficinaOrigenDescripcio(),
					null);
			
			registreRepetit.updateJustificant(null);
			
			for (Iterator<RegistreAnnexEntity> iterator = registreRepetit.getAnnexos().iterator(); iterator.hasNext();) {
				RegistreAnnexEntity annex = iterator.next();
			    iterator.remove();
				registreAnnexRepository.delete(annex);
			}
			registreRepository.save(registreRepetit);
			
			
			for (Iterator<RegistreInteressatEntity> iterator = registreRepetit.getInteressats().iterator(); iterator.hasNext();) {
				RegistreInteressatEntity interessat = iterator.next();
			    iterator.remove();
			    registreInteressatRepository.delete(interessat);
			}
			registreRepository.save(registreRepetit);
			registre = registreRepetit;
			
			
			List<String> params = new ArrayList<>();
			params.add(registre.getNom());
			params.add(null);
			
			contingutLogHelper.log(
					registre,
					LogTipusEnumDto.SOBREESCRIURE,
					params,
					false);
			
		} else {


			// save annotacio in db
			RegistreEntity registreEntity = RegistreEntity.getBuilder(
					entitat,
					tipus,
					unitatOrganitzativaCodi,
					unitat != null ? unitat.getDenominacio() : null,
					registreAnotacio.getNumero(),
					registreAnotacio.getData(),
					0, // número de còpia
					registreAnotacio.getIdentificador(),
					registreAnotacio.getExtracte(),
					registreAnotacio.getOficinaCodi(),
					registreAnotacio.getLlibreCodi(),
					registreAnotacio.getAssumpteTipusCodi(),
					registreAnotacio.getIdiomaCodi(),
					estat,
					null).
			entitatCodi(registreAnotacio.getEntitatCodi()).
			entitatDescripcio(registreAnotacio.getEntitatDescripcio()).
			oficinaDescripcio(registreAnotacio.getOficinaDescripcio()).
			llibreDescripcio(registreAnotacio.getLlibreDescripcio()).
			assumpteTipusDescripcio(registreAnotacio.getAssumpteTipusDescripcio()).
			assumpteCodi(registreAnotacio.getAssumpteCodi()).
			assumpteDescripcio(registreAnotacio.getAssumpteDescripcio()).
			procedimentCodi(registreAnotacio.getProcedimentCodi()).
			referencia(registreAnotacio.getReferencia()).
			expedientNumero(registreAnotacio.getExpedientNumero()).
			numeroOrigen(registreAnotacio.getNumeroOrigen()).
			idiomaDescripcio(registreAnotacio.getIdiomaDescripcio()).
			transportTipusCodi(registreAnotacio.getTransportTipusCodi()).
			transportTipusDescripcio(registreAnotacio.getTransportTipusDescripcio()).
			transportNumero(registreAnotacio.getTransportNumero()).
			usuariCodi(registreAnotacio.getUsuariCodi()).
			usuariNom(registreAnotacio.getUsuariNom()).
			usuariContacte(registreAnotacio.getUsuariContacte()).
			aplicacioCodi(registreAnotacio.getAplicacioCodi()).
			aplicacioVersio(registreAnotacio.getAplicacioVersio()).
			documentacioFisicaCodi(registreAnotacio.getDocumentacioFisicaCodi()).
			documentacioFisicaDescripcio(registreAnotacio.getDocumentacioFisicaDescripcio()).
			observacions(registreAnotacio.getObservacions()).
			exposa(registreAnotacio.getExposa()).
			solicita(registreAnotacio.getSolicita()).
			regla(regla).
			oficinaOrigen(
					registreAnotacio.getDataOrigen(),
					registreAnotacio.getOficinaOrigenCodi(),
					registreAnotacio.getOficinaOrigenDescripcio()).
			justificantArxiuUuid(justificantArxiuUuid).
			presencial(registreAnotacio.isPresencial()).
			build();
			registreRepository.saveAndFlush(registreEntity);
			
			contextsaveRegistre.stop();
			
			registre = registreEntity;
		}
		
		final Timer timersaveInteressats = metricRegistry.timer(MetricRegistry.name(RegistreHelper.class, "crearRegistreEntity.saveInteressats"));
		Timer.Context contextsaveInteressats = timersaveInteressats.time();
		// save interessats in db
		if (registreAnotacio.getInteressats() != null) { 
			for (RegistreInteressat registreInteressat: registreAnotacio.getInteressats()) {
				registre.getInteressats().add(
						crearInteressatEntity(
								registreInteressat,
								registre));
			}
		}
		contextsaveInteressats.stop();
		
		
		final Timer timersaveAnnexos = metricRegistry.timer(MetricRegistry.name(RegistreHelper.class, "crearRegistreEntity.saveAnnexos"));
		Timer.Context contextsaveAnnexos = timersaveAnnexos.time();
		// save annexos and firmes in db and their byte content in the folder in local filesystem
		if (registreAnotacio.getAnnexos() != null) { 
			for (RegistreAnnex registreAnnex: registreAnotacio.getAnnexos()) {
				registre.getAnnexos().add(
						crearAnnexEntity(
								registreAnnex,
								registre));
			}
		}
		contextsaveAnnexos.stop();
		return registre;

	}
	
	
	
	
	
	
	
	
	public void bloquejar(RegistreEntity registreEntity, String usuariCodi) {
		// Agafa l'expedient. Si l'expedient pertany a un altre usuari li pren
		UsuariEntity usuariNou = usuariHelper.getUsuariByCodi(usuariCodi);
		registreEntity.updateAgafatPer(usuariNou);
		List<String> params = new ArrayList<>();
		params.add(usuariCodi);
		params.add(null);
		contingutLogHelper.log(
				registreEntity,
				LogTipusEnumDto.AGAFAR,
				params,
				false);
	}
	
	public void alliberar(RegistreEntity registreEntity, String usuariCodi) {
		UsuariEntity prevUserAgafat = registreEntity.getAgafatPer();
		UsuariEntity usuariResponsableBloqueig = usuariHelper.getUsuariByCodi(usuariCodi);
		registreEntity.updateAgafatPer(null);
		
		if (prevUserAgafat != usuariResponsableBloqueig) {
			emailHelper.contingutAlliberatPerAltreUsusari(
					registreEntity, 
					prevUserAgafat, 
					usuariResponsableBloqueig);
		}
		
		List<String> params = new ArrayList<>();
		params.add(prevUserAgafat.getCodi());
		params.add(null);
		contingutLogHelper.log(registreEntity, LogTipusEnumDto.ALLIBERAR, params, false);
	}
	
	public static String encrypt
	(String messageToEncrypt,
			String clauSecreta) throws Exception {

		MessageDigest messageDigest;

		messageDigest = MessageDigest.getInstance("SHA-1");
		byte[] digestResult = messageDigest.digest(messageToEncrypt.getBytes());
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE,
				buildKey(clauSecreta));
		byte[] encryptResult = cipher.doFinal(digestResult);
		String clauAcces =  new String(Base64.encode(encryptResult));

		return clauAcces;
	}
	
	
	public static SecretKeySpec buildKey(String message) throws Exception {
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		byte[] key = sha.digest(message.getBytes());
		key = Arrays.copyOf(key,
				16);
		return new SecretKeySpec(key, "AES");
	}
	
	
	
	
	
	public byte[] getAnnexArxiuContingut(String nomArxiu) {
		String pathName = configHelper.getConfig("es.caib.distribucio.bustia.contingut.documents.dir");
		
		Path path = Paths.get(pathName + "/" + nomArxiu);
		try {
			byte[] data = Files.readAllBytes(path);
			return data;
		} catch (IOException e) {
			return null;
		}
	}

	public List<ArxiuFirmaDto> convertirFirmesAnnexToArxiuFirmaDto(
			RegistreAnnexEntity annex,
			byte[] firmaDistribucioContingut) {
		List<ArxiuFirmaDto> firmes = null;
		if (annex.getFirmes() != null) {
			firmes = new ArrayList<ArxiuFirmaDto>();
			for (RegistreAnnexFirmaEntity annexFirma: annex.getFirmes()) {
				byte[] firmaContingut = null;
				
				if (annexFirma.getGesdocFirmaId() != null) {
					ByteArrayOutputStream baos_fir = new ByteArrayOutputStream();
					gestioDocumentalHelper.gestioDocumentalGet(
							annexFirma.getGesdocFirmaId(), 
							GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP, 
							baos_fir);
					firmaContingut = baos_fir.toByteArray();
				} else if(firmaDistribucioContingut != null) {
					firmaContingut = firmaDistribucioContingut;
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
				
				firma.setDetalls(conversioTipusHelper.convertirList(
						annexFirma.getDetalls(),
						ArxiuFirmaDetallDto.class));
				
				firmes.add(firma);
			}
		}
		return firmes;
	}
	
	
	@Transactional
	public void processarAnotacioPendentArxiuInThreadExecuto(Long registreId) {
		
//    	logger.info(Thread.currentThread().getName() + " START = " + registreId);
    	
		Timer.Context context = metricRegistry.timer(MetricRegistry.name(WorkerThread.class, "processarAnotacioPendentArxiu")).time();
		logger.debug("Processant anotacio pendent de guardar a l'arxiu (registreId=" + registreId + ")");
		
		long startTime = new Date().getTime();
    	
    	Exception excepcio = null;
        try {
			excepcio = processarAnotacioPendentArxiu(registreId);
//        	Thread.sleep(10000);	
		} catch (NotFoundException e) {
			if (e.getObjectClass() == UnitatOrganitzativaDto.class) {
				excepcio = null;
			}
		} catch (Exception e) {
			excepcio = e;
		} finally {
			
			long stopTime = new Date().getTime();
			
			if (excepcio == null){
				historicsPendentHelper.addHistogramProcessat(stopTime - startTime);
				
			} else {
				historicsPendentHelper.addHistogramError();
				logger.error("Error processant l'anotacio pendent de l'arxiu (pendentId=" + registreId +  "): " + excepcio.getMessage(), excepcio);
			}
				
		}
		context.stop();
    	
//        logger.info(Thread.currentThread().getName() + " END = " + registreId);
		

	}
	
	


	@Transactional
	public Exception processarAnotacioPendentArxiu(Long anotacioId) {
		RegistreEntity anotacio = registreRepository.findOne(anotacioId);
		
		// PROCESSAR ARXIU
		List<Exception> exceptionsGuardantAnnexos = createRegistreAndAnnexosInArxiu(
				anotacio,
				anotacio.getEntitat().getCodiDir3(),
				true);
		if (exceptionsGuardantAnnexos == null) {
			
			boolean allRegistresWithSameNumeroSavedInArxiu = true;
			List<RegistreEntity> registres = registreRepository.findRegistresByNumero(anotacio.getNumero());
			if (registres != null && !registres.isEmpty()) {
				for (RegistreEntity registreEntity : registres) {
					if (registreEntity.getAnnexos() != null && !registreEntity.getAnnexos().isEmpty()) {
						for (RegistreAnnexEntity registreAnnexEntity : registreEntity.getAnnexos()) {
							if (registreAnnexEntity.getFitxerArxiuUuid() == null || registreAnnexEntity.getFitxerArxiuUuid().isEmpty()) {
								allRegistresWithSameNumeroSavedInArxiu = false;
							}
						}
					}
				}
			}
			if (allRegistresWithSameNumeroSavedInArxiu)
				gestioDocumentalHelper.esborrarDocsTemporals(anotacio);
			
			if (anotacio.getProcesEstat() != RegistreProcesEstatEnum.BUSTIA_PROCESSADA) {
				RegistreProcesEstatEnum nouEstat;
				if (anotacio.getRegla() != null && anotacio.getRegla().getTipus() == ReglaTipusEnumDto.BACKOFFICE) {
					nouEstat = RegistreProcesEstatEnum.REGLA_PENDENT;
				} else {
					nouEstat = RegistreProcesEstatEnum.BUSTIA_PENDENT;	
				}
				anotacio.updateProcesMultipleExcepcions(
						nouEstat, 
						null);
			} else {
				anotacio.updateProcesMultipleExcepcions(
						null, 
						null);
			}

			return null;
		} else {
			anotacio.updateProcesMultipleExcepcions(
					null, 
					exceptionsGuardantAnnexos);
			
			return exceptionsGuardantAnnexos != null && !exceptionsGuardantAnnexos.isEmpty() ? exceptionsGuardantAnnexos.get(0) : null;
		}
	}

	@Transactional
	public Exception processarAnotacioPendentRegla(Long anotacioId) {
		RegistreEntity anotacio = registreRepository.findOne(anotacioId);
		Exception exceptionAplicantRegla = null;
		
		if (anotacio.getRegla() != null) {
			exceptionAplicantRegla = reglaHelper.aplicarControlantException(anotacio, new ArrayList<ReglaEntity>());
			
			if (exceptionAplicantRegla != null) {
				anotacio.updateProces(
						null,
						exceptionAplicantRegla);
			}
		} else {
			// Corregeix l'estat a pendent d'Arxiu per a que segueixi el procés fins a bústia pendent
			anotacio.setNewProcesEstat(RegistreProcesEstatEnum.ARXIU_PENDENT);
		}
		return exceptionAplicantRegla;

	}

	/**
	 *  It saves anotacio with annexes in arxiu.
	 * @param registreEntity
	 * @param codiDir3
	 * @param crearAutofirma
	 * @return
	 */
	public List<Exception> createRegistreAndAnnexosInArxiu(
			RegistreEntity registreEntity,
			String unitatOrganitzativaCodi,
			boolean crearAutofirma) {
		
		List<Exception> exceptions = new ArrayList<>();
		
		if (registreEntity.getAnnexos() != null && registreEntity.getAnnexos().size() > 0) {
			DistribucioRegistreAnotacio distribucioRegistreAnotacio = conversioTipusHelper.convertir(
					registreEntity,
					DistribucioRegistreAnotacio.class);
			String uuidExpedient = null;
			
			// check if registre is not already created in arxiu
			if (registreEntity.getExpedientArxiuUuid() == null) {

				try {
					// ============= SAVE REGISTRE AS EXPEDIENT IN ARXIU ============
					uuidExpedient = pluginHelper.saveRegistreAsExpedientInArxiu(
							registreEntity.getNumero(),
							distribucioRegistreAnotacio.getNumero(),
							unitatOrganitzativaCodi);
					registreEntity.updateExpedientArxiuUuid(uuidExpedient);
					logger.trace("Creat el contenidor a l'Arxiu per l'anotació (" +
							"anotacioId=" + registreEntity.getId() + ", " +
							"anotacioNumero=" + registreEntity.getNumero() + ", " +
							"unitatOrganitzativaCodi=" + unitatOrganitzativaCodi + ") amb uuid " + uuidExpedient);
					
					loadJustificantToDB(registreEntity.getId());
					
				} catch (Exception ex) {
					return Arrays.asList(ex);
				}
			// Si el contenidor ja està creat agafam el seu UUID
			} else {
				uuidExpedient = registreEntity.getExpedientArxiuUuid();
				logger.trace("L'anotació (" +
						"anotacioId=" + registreEntity.getId() + ", " +
						"anotacioNumero=" + registreEntity.getNumero() + ", " +
						"unitatOrganitzativaCodi=" + unitatOrganitzativaCodi + ") ja estava a l'Arxiu amb uuid " + uuidExpedient);
			}
			
			if (uuidExpedient != null) {
				logger.debug("Guardant " + registreEntity.getAnnexos().size() + " annexos de l'anotació (" +
						"anotacioId=" + registreEntity.getId() + ", " +
						"anotacioNumero=" + registreEntity.getNumero() + ", " +
						"unitatOrganitzativaCodi=" + unitatOrganitzativaCodi + ") amb uuid " + uuidExpedient + " a l'Arxiu.");
				for (int i = 0; i < registreEntity.getAnnexos().size(); i++) {
					try {
						
						RegistreAnnexEntity annex = registreEntity.getAnnexos().get(i);
						// Només crea l'annex a dins el contenidor si encara no s'ha creat
						if (annex.getFitxerArxiuUuid() == null) {

							DistribucioRegistreAnnex distribucioAnnex = distribucioRegistreAnotacio.getAnnexos().get(i);
													
							DocumentEniRegistrableDto documentEniRegistrableDto = new DocumentEniRegistrableDto();
							documentEniRegistrableDto.setNumero(registreEntity.getNumero());
							documentEniRegistrableDto.setData(registreEntity.getData());
							documentEniRegistrableDto.setOficinaDescripcio(registreEntity.getOficinaDescripcio());
							documentEniRegistrableDto.setOficinaCodi(registreEntity.getOficinaCodi());
							
							// ================= SAVE ANNEX AS DOCUMENT IN ARXIU ============== sign it if unsigned an save it with firma in arxiu
							String uuidDocument = pluginHelper.saveAnnexAsDocumentInArxiu(
									registreEntity.getNumero(),
									distribucioAnnex,
									unitatOrganitzativaCodi,
									uuidExpedient,
									documentEniRegistrableDto);
							annex.updateFitxerArxiuUuid(uuidDocument);
							
							// set fitxer size if unset
							if (annex.getFitxerTamany() <= 0) { 
								Document document = pluginHelper.arxiuDocumentConsultar(
										annex.getFitxerArxiuUuid(), 
										null, 
										true);
								if (document.getContingut() != null) {
									annex.updateFitxerTamany(
											(int)document.getContingut().getTamany());
								}
							}
							
							if (distribucioAnnex.getFirmes() != null) {
								for (DistribucioRegistreFirma distribucioFirma: distribucioAnnex.getFirmes()) {
									// if firma was created with autofirma save info about firma(without content bytes) in db
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
							
							if (!annex.isSignaturaDetallsDescarregat()) {
								loadSignaturaDetallsToDB(annex);
							}
							
							
						}
					
					} catch (Exception ex) {
						exceptions.add(ex);
					}
				}
			}
		}
		if (exceptions != null && !exceptions.isEmpty()) {
			return exceptions;
		} else {
			logger.trace("Creació del contenidor i dels annexos finalitzada correctament (" +
					"anotacioId=" + registreEntity.getId() + ", " +
					"anotacioNumero=" + registreEntity.getNumero() + ", " +
					"unitatOrganitzativaCodi=" + unitatOrganitzativaCodi + ")");
			
			List<String> params = new ArrayList<>();
			params.add(registreEntity.getNom());
			params.add(null);
			
			contingutLogHelper.log(
					registreEntity,
					LogTipusEnumDto.DISTRIBUCIO,
					params,
					false);
			return null;
		}
			
	}
	

	public FitxerDto getJustificant(Long registreId) {
		RegistreEntity registre = registreRepository.findOne(registreId);
		FitxerDto arxiu = new FitxerDto();
		Document document = null;
		document = pluginHelper.arxiuDocumentConsultar(registre.getJustificantArxiuUuid(),
				null,
				true,
				true);
		if (document != null) {
			DocumentContingut documentContingut = document.getContingut();
			if (documentContingut != null) {
				arxiu.setNom(obtenirJustificantNom(document));
				arxiu.setContentType(documentContingut.getTipusMime());
				arxiu.setContingut(documentContingut.getContingut());
				arxiu.setTamany(documentContingut.getContingut().length);
			}
		}
		return arxiu;
	}

	public RegistreAnnexDto getAnnexAmbFirmes(
			Long annexId) throws NotFoundException {
		
		final Timer timegetgetAnnexosAmbArxiu = metricRegistry.timer(MetricRegistry.name(RegistreServiceImpl.class, "getAnnexosAmbArxiu"));
		Timer.Context contexgetgetAnnexosAmbArxiu = timegetgetAnnexosAmbArxiu.time();
		
		RegistreAnnexEntity registreAnnexEntity = registreAnnexRepository.findOne(annexId);
		
		RegistreAnnexDto registreAnnexDto;
		
		// if annex is already created in arxiu use document and firma content from arxiu
		if (registreAnnexEntity.getFitxerArxiuUuid() != null && !registreAnnexEntity.getFitxerArxiuUuid().isEmpty()) {
			
			if (!registreAnnexEntity.isSignaturaDetallsDescarregat()) {
				loadSignaturaDetallsToDB(registreAnnexEntity);
			}
				
			List<ArxiuFirmaDto> firmes = convertirFirmesAnnexToArxiuFirmaDto(
					registreAnnexEntity,
					null);

			registreAnnexDto = conversioTipusHelper.convertir(
					registreAnnexEntity,
					RegistreAnnexDto.class);
			registreAnnexDto.setFirmes(firmes);
			registreAnnexDto.setAmbFirma(true);
			

		// if annex is not yet created in arxiu use document and firma content from gestio documental
		} else {
			registreAnnexDto = conversioTipusHelper.convertir(
					registreAnnexEntity,
					RegistreAnnexDto.class);

			List<ArxiuFirmaDto> firmes = convertirFirmesAnnexToArxiuFirmaDto(
					registreAnnexEntity,
					null);

			for (int i = 0; i < firmes.size(); i++) {

				ArxiuFirmaDto arxiuFirmaDto = firmes.get(i);
				RegistreAnnexFirmaEntity registreAnnexFirmaEntity = registreAnnexEntity.getFirmes().get(i);

				if (pluginHelper.isValidaSignaturaPluginActiu()) {
					
					byte[] documentContingut = null;
					if (registreAnnexEntity.getGesdocDocumentId() != null && !registreAnnexEntity.getGesdocDocumentId().isEmpty()) {
						
						ByteArrayOutputStream streamAnnex = new ByteArrayOutputStream();
						gestioDocumentalHelper.gestioDocumentalGet(
								registreAnnexEntity.getGesdocDocumentId(),
								GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP,
								streamAnnex);
						documentContingut = streamAnnex.toByteArray();
					}
					byte[] firmaContingut = null;
					if (registreAnnexFirmaEntity.getGesdocFirmaId() != null && !registreAnnexFirmaEntity.getGesdocFirmaId().isEmpty()) {
						ByteArrayOutputStream streamAnnexFirma = new ByteArrayOutputStream();
						gestioDocumentalHelper.gestioDocumentalGet(
								registreAnnexFirmaEntity.getGesdocFirmaId(),
								GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP,
								streamAnnexFirma);
						firmaContingut = streamAnnexFirma.toByteArray();
					}
					arxiuFirmaDto.setDetalls(pluginHelper.validaSignaturaObtenirDetalls(
							documentContingut,
							firmaContingut));
				} else {
					logger.warn("ValidaSignaturaPlugin is not configured");
				}
			}

			registreAnnexDto.setFirmes(firmes);
			registreAnnexDto.setAmbFirma(true);

		}
		
		contexgetgetAnnexosAmbArxiu.stop();
		return registreAnnexDto;
	}
		
	
	public List<RegistreAnnexDto> getAnnexosAmbFirmes(
			Long entitatId,
			Long registreId,
			boolean isVistaMoviments, 
			String rolActual) throws NotFoundException {

		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);

		RegistreEntity registre = registreRepository.findByEntitatAndId(
				entitat,
				registreId);

		if (!usuariHelper.isAdmin() && !isVistaMoviments)
			entityComprovarHelper.comprovarBustia(
					entitat,
					registre.getPareId(),
					true);

		List<RegistreAnnexDto> annexos = new ArrayList<RegistreAnnexDto>();
		
		for (RegistreAnnexEntity registeAnnexEntity: registre.getAnnexos()) {
			
			if ((registre.getJustificant() != null && registre.getJustificant().getId().equals(registeAnnexEntity.getId()))
					|| (registre.getJustificantArxiuUuid() != null && registre.getJustificantArxiuUuid().equals(registeAnnexEntity.getFitxerArxiuUuid()))){
				// El justificant no es retorna com un annex
				
			} else {
				RegistreAnnexDto registreAnnexDto = getAnnexAmbFirmes(
						registeAnnexEntity.getId());
				
				annexos.add(registreAnnexDto);
				
			}
		}
				
		if ("tothom".equalsIgnoreCase(rolActual)) {
			List<RegistreAnnexDto> registreAnnexos = new ArrayList<RegistreAnnexDto>();
			for (RegistreAnnexDto annexo: annexos) {
				if (annexo.getSicresTipusDocument() == null 
						|| !RegistreAnnexSicresTipusDocumentEnum.INTERN.getValor().equals(annexo.getSicresTipusDocument())) {
					registreAnnexos.add(annexo);
				}
			}
			annexos = registreAnnexos;
		}
		
		return annexos;
	}
	

	
	
	
	public void loadSignaturaDetallsToDB(RegistreAnnexEntity annexEntity) {
		
		logger.trace("Loading Signatura detalls to DB");
		
		if (annexEntity.getFitxerArxiuUuid() == null || annexEntity.getFitxerArxiuUuid().isEmpty()) {
			logger.warn("Intent de carregar dades de firmes per l'annex " + annexEntity.getTimestamp() + " de l'anotació " + annexEntity.getRegistre().getIdentificador() + " amb UUID d'Arxiu buit.");
			return;
		}
		
		try {
			final Timer timearxiuDocumentConsultar = metricRegistry.timer(MetricRegistry.name(RegistreServiceImpl.class, "getAnnexosAmbArxiu.arxiuDocumentConsultar"));
			Timer.Context contexarxiuDocumentConsultar = timearxiuDocumentConsultar.time();
			Document document = pluginHelper.arxiuDocumentConsultar(
					annexEntity.getFitxerArxiuUuid(),
					null,
					true);
			contexarxiuDocumentConsultar.stop();
			
			DocumentMetadades metadades = document.getMetadades();
			if (metadades != null) {
				annexEntity.updateFirmaCsv(metadades.getCsv());
			}
			
			if (document.getFirmes() != null && document.getFirmes().size() > 0) {
				List<RegistreAnnexFirmaEntity> firmes = annexEntity.getFirmes();
				Iterator<es.caib.plugins.arxiu.api.Firma> it = document.getFirmes().iterator();
				
				int firmaIndex = 0;
				while (it.hasNext()) {
					es.caib.plugins.arxiu.api.Firma arxiuFirma = it.next();
					if (!FirmaTipus.CSV.equals(arxiuFirma.getTipus())) {
						RegistreAnnexFirmaEntity firma = firmes.get(firmaIndex);
						if (pluginHelper.isValidaSignaturaPluginActiu()) {
							byte[] documentContingut = document.getContingut() != null? document.getContingut().getContingut() : null;
							byte[] firmaContingut = arxiuFirma.getContingut();
							if ("TF05".equals(firma.getTipus())) { // TF05 CAdDES attached
								firmaContingut = null;
							}
							final Timer timevalidaSignaturaObtenirDetalls = metricRegistry.timer(MetricRegistry.name(RegistreServiceImpl.class, "getAnnexosAmbArxiu.validaSignaturaObtenirDetalls"));
							Timer.Context contevalidaSignaturaObtenirDetalls = timevalidaSignaturaObtenirDetalls.time();
							List<ArxiuFirmaDetallDto> firmaDetalls = pluginHelper.validaSignaturaObtenirDetalls(
									documentContingut,
									firmaContingut);
							contevalidaSignaturaObtenirDetalls.stop();
							
							for (ArxiuFirmaDetallDto arxiuFirmaDetallDto : firmaDetalls) {
								RegistreFirmaDetallEntity firmaDetallEntity = RegistreFirmaDetallEntity.getBuilder(
										arxiuFirmaDetallDto,
										firma).build();
								registreFirmaDetallRepository.saveAndFlush(firmaDetallEntity);
								firma.getDetalls().add(firmaDetallEntity);
							}
							
						} else {
							logger.warn("ValidaSignaturaPlugin is not configured");
						}
						firmaIndex++;
					} else {
						it.remove();
					}
				}
				}
			annexEntity.updateSignaturaDetallsDescarregat(true);
		} catch (Exception e) {
			logger.error("Error al carregar singatura detalls a la base de dades", e);
			throw new RuntimeException("Error al carregar singatura detalls a la base de dades", e);
		}
	}
	
	public RegistreAnnexEntity loadJustificantToDB(Long registreId) {
		
		RegistreAnnexEntity annex = new RegistreAnnexEntity();
		try {
			RegistreEntity registre = registreRepository.getOne(registreId);
			Document document = pluginHelper.arxiuDocumentConsultar(registre.getJustificantArxiuUuid(), null, true);
			annex.updateFitxerArxiuUuid(registre.getJustificantArxiuUuid());
			annex.updateFitxerNom(obtenirJustificantNom(document));
			annex.updateFitxerTamany(document.getContingut().getContingut().length);
			annex.updateFitxerTipusMime(document.getContingut().getTipusMime());
			annex.updateTitol(document.getNom());
			DocumentMetadades metadades = document.getMetadades();
			if (metadades != null) {
				annex.updateDataCaptura(metadades.getDataCaptura());
				annex.updateOrigenCiutadaAdmin(metadades.getOrigen().toString());
				annex.updateNtiElaboracioEstat(metadades.getEstatElaboracio().toString());
				annex.updateNtiTipusDocument(metadades.getTipusDocumental().toString());
				annex.updateFirmaCsv(metadades.getCsv());
			}
			annex.updateRegistre(registre);
			
			registreAnnexRepository.saveAndFlush(annex);
			registre.updateJustificantDescarregat(true);
			registre.updateJustificant(annex);
		} catch (Exception e) {
			logger.error("Error descarregant justificant", e);
		}
		return annex;
	}
	
	

	public String obtenirJustificantNom(Document document) {
		String fileName = "";
		String fileExtension = "";
		if (document.getContingut() != null) { 
			if (document.getContingut().getTipusMime() != null) {
				fileExtension = document.getContingut().getTipusMime();
			}
			if (document.getContingut().getArxiuNom() != null && !document.getContingut().getArxiuNom().isEmpty()) {
				fileName = document.getContingut().getArxiuNom();
				fileExtension = document.getContingut().getTipusMime();
			} else {
				fileName = document.getNom();
			}
		} else {
			fileName = document.getNom();
		}
		String fragment = "";
    	if (fileName.length() > 4) {
    		fragment = fileName.substring(fileName.length() -5);
    	}
    	if (fragment.contains(".")) {
    		return fileName;
    	}
    	if (!fileExtension.isEmpty()) {
    		if (fileExtension.contains("/")) {
    			fileName += ("." + fileExtension.split("/")[1]);
    		} else if (fileExtension.contains(".")) {
    			fileName += fileExtension;
    		} else {
    			fileName += "." + fileExtension;
    		}
    	}
		return fileName;
	}

	@Transactional
	public void tancarExpedientArxiu(Long registreId) {
		RegistreEntity registre = registreRepository.findOne(registreId);
		Exception exception = null;
		try {
			if (registre.getExpedientArxiuUuid() != null) {
				Expedient expedient = pluginHelper.arxiuExpedientInfo(registre.getExpedientArxiuUuid());
				if (expedient.getContinguts() != null && !expedient.getContinguts().isEmpty()) {
					pluginHelper.arxiuExpedientTancar(registre);
				} else {
					// Si no té annexos esborra l'espedient, el tancament fallaria
					pluginHelper.arxiuExpedientEliminar(registre.getExpedientArxiuUuid());
				}
			}
		} catch (Exception ex) {
			exception = ex;
		}
		if (exception != null) {
			registre.updateArxiuTancatError(true);
		} else {
			registre.updateArxiuTancat(true);
		}
	}
	
	// sends ids of anotacions to backoffice
	@Transactional(readOnly = true)
	public Exception enviarIdsAnotacionsBackoffice(List<Long> pendentsIdsGroupedByRegla) {
		
		List <RegistreEntity> pendentsByRegla = new ArrayList<>();		
		Map<String, String> accioParams = new HashMap<String, String>();		

		for(Long id: pendentsIdsGroupedByRegla){
			RegistreEntity pendent = registreRepository.findOne(id);
			pendentsByRegla.add(pendent);
		}

		String clauSecreta = configHelper.getConfig(
				"es.caib.distribucio.backoffice.integracio.clau");
		if (clauSecreta == null) {
			throw new RuntimeException("Clau secreta no especificada al fitxer de propietats");
		}
		
		long t0 = System.currentTimeMillis();
		BackofficeEntity backofficeDesti = pendentsByRegla.get(0).getRegla().getBackofficeDesti();
		String accioDescripcio = "Comunicar anotacions pendents " + backofficeDesti.getCodi();
		List<AnotacioRegistreId> ids = new ArrayList<>();
		for (RegistreEntity pendent : pendentsByRegla) {
			
			AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
			anotacioRegistreId.setIndetificador(pendent.getNumero());			
			
			try {
				anotacioRegistreId.setClauAcces(RegistreHelper.encrypt(pendent.getNumero(),
						clauSecreta));
			} catch (Exception ex) {
				String errorDescripcio = "Error enviant anotacions al backoffice";
				accioParams = identificadorsToHashMap(ids);
				accioParams.put("Backoffice", backofficeDesti.getCodi());
				afegirAccioErrorBackOffice(accioDescripcio, errorDescripcio, accioParams, t0, ex);				
				throw new SistemaExternException(
						IntegracioHelper.INTCODI_BACKOFFICE,
						errorDescripcio,
						ex);
			}
			ids.add(anotacioRegistreId);
		}		
		
		try {			
			String usuari = backofficeDesti.getUsuari();
			String contrasenya = backofficeDesti.getContrasenya();
			if (usuari != null && !usuari.isEmpty() && usuari.startsWith("${") && usuari.endsWith("}")) {
				usuari = configHelper.getConfig(backofficeDesti.getUsuari().replaceAll("\\$\\{", "").replaceAll("\\}", ""));
			}
			if (contrasenya != null && !contrasenya.isEmpty() && contrasenya.startsWith("${") && contrasenya.endsWith("}")) {
				contrasenya = configHelper.getConfig(backofficeDesti.getContrasenya().replaceAll("\\$\\{", "").replaceAll("\\}", ""));
			}
			
			logger.trace(">>> Abans de crear backoffice WS");
			BackofficeWsService backofficeClient = new WsClientHelper<BackofficeWsService>().generarClientWs(
					getClass().getResource(
							"/es/caib/distribucio/core/service/ws/backoffice/backoffice.wsdl"),
					backofficeDesti.getUrl(),
					new QName(
							"http://www.caib.es/distribucio/ws/backoffice",
							"BackofficeService"),
					usuari,
					contrasenya,
					null,
					BackofficeWsService.class);			
			
			logger.trace(">>> Abans de cridar backoffice WS");			
			backofficeClient.comunicarAnotacionsPendents(ids);		
			
//			String usuariIntegracio = this.getUsuariIntegracio();
			String usuariIntegracio = "Implementar getUsuariIntegracio()";
			
			integracioHelper.addAccioOk (
					IntegracioHelper.INTCODI_BACKOFFICE,
					accioDescripcio,
					usuariIntegracio,
					identificadorsToHashMap(ids),
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0
			);			
			logger.trace(">>> Despres de cridar backoffice WS");			
			return null;
			
		} catch (Exception ex) {
			String errorDescripcio = "Error enviant anotacions al backoffice";			
			accioParams = identificadorsToHashMap(ids);
			accioParams.put("Backoffice", backofficeDesti.getCodi());
			afegirAccioErrorBackOffice(accioDescripcio, errorDescripcio, accioParams, t0, ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_BACKOFFICE,
					errorDescripcio,
					ex);
		}
	}
	
	private void afegirAccioErrorBackOffice(String accioDescripcio, String errorDescripcio, Map<String, String> accioParams, long tInit, Exception ex) {
		
//		String usuariIntegracio = this.getUsuariIntegracio();
		String usuariIntegracio = "Implementar getUsuariIntegracio()";
		integracioHelper.addAccioError(
				IntegracioHelper.INTCODI_BACKOFFICE,
				accioDescripcio,
				usuariIntegracio,
				accioParams,
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				System.currentTimeMillis() - tInit,
				errorDescripcio,
				ex);
	}
	
	private Map<String, String> identificadorsToHashMap(List<AnotacioRegistreId> ids) {
		
		Map<String, String> accioParams = new HashMap<String, String>();
		for (AnotacioRegistreId anotacioRegistreId:ids) {
			accioParams.put(anotacioRegistreId.getIndetificador(), anotacioRegistreId.getClauAcces());
		}	
		return accioParams;
	}
	
	public int getGuardarAnnexosMaxReintentsProperty() {
		String maxReintents = configHelper.getConfig("es.caib.distribucio.tasca.guardar.annexos.max.reintents");
		if (maxReintents != null) {
			return Integer.parseInt(maxReintents);
		} else {
			return 0;
		}
	}
	
	public int getMaxThreadsParallelProperty() {
		String maxThreadsParallel = configHelper.getConfig("es.caib.distribucio.tasca.guardar.annexos.max.threads.parallel");
		if (maxThreadsParallel != null) {
			return Integer.parseInt(maxThreadsParallel);
		} else {
			return 5;
		}
	}
	

	public Exception enviarIdsAnotacionsBackUpdateDelayTime(List<Long> pendentsIdsGroupedByRegla) {

		Exception throwable = enviarIdsAnotacionsBackoffice(pendentsIdsGroupedByRegla);
		updateBackEnviarDelayData(pendentsIdsGroupedByRegla, throwable);
		return throwable;
	}
	
	
	@Transactional()
	public void updateBackEnviarDelayData(List<Long> pendentsIdsGroupedByRegla, Exception throwable) {
	
		List<RegistreEntity> pendentsByRegla = new ArrayList<>();
		for (Long id : pendentsIdsGroupedByRegla) {
			RegistreEntity pendent = registreRepository.findOne(id);
			pendentsByRegla.add(pendent);
		}
		
		for (RegistreEntity pend : pendentsByRegla) {

			
			if (throwable == null) {
				// remove exception message and increment procesIntents
				pend.updateProces(null,
						null);
			} else { // if excepion occured during sending anotacions ids to backoffice
				// add exception message and increment procesIntents
				pend.updateProces(null,
						throwable);
			}

			// set delay for another send retry
			int procesIntents = pend.getProcesIntents();
			String tempsEspera = configHelper.getConfig(
					"es.caib.distribucio.tasca.enviar.anotacions.backoffice.temps.espera.execucio");
			// we convert to minutes to not have to deal with too big numbers out of bounds
			int minutesEspera = ((Integer.parseInt(tempsEspera) / 1000) / 60);
			if (minutesEspera < 1) {
				minutesEspera = 1;
			} // with every proces intent delay between resends will be longer
			int delayMinutes = minutesEspera * procesIntents;
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.MINUTE,
					delayMinutes);
			pend.updateBackRetryEnviarData(cal.getTime());
			registreRepository.saveAndFlush(pend);
		}
	}

	public void comprovarRegistreAlliberat(RegistreEntity registre) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UsuariEntity agafatPer = registre.getAgafatPer();
		if (agafatPer != null && HibernateHelper.isProxy(agafatPer))
			agafatPer = HibernateHelper.deproxy(agafatPer);
		
		if (agafatPer != null && !agafatPer.getCodi().equals(auth.getName())) {
			throw new ValidationException(
					registre.getId(),
					RegistreEntity.class,
					"L'anotació està bloquejada per un usuari (codiUsuari=" + agafatPer.getCodi() + "). Prova d'alliberar-la abans de realitzar una acció.");
		}
	}
	
	public void esborrarRegistre(RegistreEntity registre) {
		ContingutEntity contingut = (ContingutEntity)registre;
		contingutHelper.esborrarComentarisRegistre(contingut);
		contingutLogHelper.esborrarConstraintsLogsMovimentsRegistre(contingut);
		contingutHelper.esborrarEmailsPendentsRegistre(contingut);
		contingutHelper.esborrarMovimentsRegistre(contingut);
		registreRepository.delete(registre);
	}
	
	private RegistreInteressat fromInteressatEntity(
			RegistreInteressatEntity interessatEntity) {
		RegistreInteressat interessat = new RegistreInteressat();
		if (interessatEntity.getTipus() != null)
			interessat.setTipus(interessatEntity.getTipus().getValor());
		if (interessatEntity.getDocumentTipus() != null)
			interessat.setDocumentTipus(interessatEntity.getDocumentTipus().getValor());
		interessat.setDocumentNum(interessatEntity.getDocumentNum());
		interessat.setNom(interessatEntity.getNom());
		interessat.setLlinatge1(interessatEntity.getLlinatge1());
		interessat.setLlinatge2(interessatEntity.getLlinatge2());
		interessat.setRaoSocial(interessatEntity.getRaoSocial());
		interessat.setPais(interessatEntity.getPais());
		interessat.setPaisCodi(interessatEntity.getPaisCodi());
		interessat.setProvincia(interessatEntity.getProvincia());
		interessat.setProvinciaCodi(interessatEntity.getProvinciaCodi());
		interessat.setMunicipi(interessatEntity.getMunicipi());
		interessat.setMunicipiCodi(interessatEntity.getMunicipiCodi());
		interessat.setAdresa(interessatEntity.getAdresa());
		interessat.setCodiPostal(interessatEntity.getCodiPostal());
		interessat.setEmail(interessatEntity.getEmail());
		interessat.setTelefon(interessatEntity.getTelefon());
		interessat.setEmailHabilitat(interessatEntity.getEmailHabilitat());
		if (interessatEntity.getCanalPreferent() != null)
			interessat.setCanalPreferent(interessatEntity.getCanalPreferent().getValor());
		interessat.setObservacions(interessatEntity.getObservacions());
		if (interessatEntity.getRepresentant() != null) {
			interessat.setRepresentant(
					fromInteressatEntity(interessatEntity.getRepresentant()));
		}
		return interessat;
	}

	private RegistreAnnex fromAnnexEntity(
			RegistreAnnexEntity annexEntity,
			RegistreAnotacio registre) {
		RegistreAnnex annex = new RegistreAnnex();
		annex.setId(annexEntity.getId());
		annex.setTitol(annexEntity.getTitol());
		annex.setFitxerNom(annexEntity.getFitxerNom());
		annex.setFitxerTamany(annexEntity.getFitxerTamany());
		annex.setFitxerTipusMime(annexEntity.getFitxerTipusMime());
		annex.setFitxerArxiuUuid(annexEntity.getFitxerArxiuUuid());
		annex.setEniDataCaptura(annexEntity.getDataCaptura());
		annex.setLocalitzacio(annexEntity.getLocalitzacio());
		if (annexEntity.getOrigenCiutadaAdmin() != null)
			annex.setEniOrigen(annexEntity.getOrigenCiutadaAdmin().getValor());
		if (annexEntity.getNtiTipusDocument() != null)
			annex.setEniTipusDocumental(annexEntity.getNtiTipusDocument().getValor());
		if (annexEntity.getSicresTipusDocument() != null)
			annex.setSicresTipusDocument(annexEntity.getSicresTipusDocument().getValor());
		if (annexEntity.getNtiElaboracioEstat() != null)
			annex.setEniEstatElaboracio(annexEntity.getNtiElaboracioEstat().getValor());
		annex.setObservacions(annexEntity.getObservacions());
		annex.setTimestamp(annexEntity.getTimestamp());
		annex.setValidacioOCSP(annexEntity.getValidacioOCSP());
		return annex;
	}

	private RegistreInteressatEntity crearInteressatEntity(
			RegistreInteressat registreInteressat,
			RegistreEntity registre) {
		RegistreInteressatTipusEnum interessatTipus = RegistreInteressatTipusEnum.valorAsEnum(registreInteressat.getTipus());
		RegistreInteressatEntity.Builder interessatBuilder;
		switch (interessatTipus) {
		case PERSONA_FIS:
			interessatBuilder = RegistreInteressatEntity.getBuilder(
					interessatTipus,
					RegistreInteressatDocumentTipusEnum.valorAsEnum(registreInteressat.getDocumentTipus()),
					registreInteressat.getDocumentNum(),
					registreInteressat.getNom(),
					registreInteressat.getLlinatge1(),
					registreInteressat.getLlinatge2(),
					registre);
			break;
		default: // PERSONA_JUR o ADMINISTRACIO
			interessatBuilder = RegistreInteressatEntity.getBuilder(
					interessatTipus,
					RegistreInteressatDocumentTipusEnum.valorAsEnum(registreInteressat.getDocumentTipus()),
					registreInteressat.getDocumentNum(),
					registreInteressat.getRaoSocial(),
					registre);
			break;
		}
		interessatBuilder.
			pais(registreInteressat.getPais()).
			paisCodi(registreInteressat.getPaisCodi()).
			provincia(registreInteressat.getProvincia()).
			provinciaCodi(registreInteressat.getProvinciaCodi()).
			municipi(registreInteressat.getMunicipi()).
			municipiCodi(registreInteressat.getMunicipiCodi()).
			adresa(registreInteressat.getAdresa()).
			codiPostal(registreInteressat.getCodiPostal()).
			email(registreInteressat.getEmail()).
			telefon(registreInteressat.getTelefon()).
			emailHabilitat(registreInteressat.getEmailHabilitat()).
			canalPreferent(
					RegistreInteressatCanalEnum.valorAsEnum(
							registreInteressat.getCanalPreferent())).
			observacions(registreInteressat.getObservacions()).	
			codiDire(registreInteressat.getCodiDire());
		RegistreInteressatEntity interessatEntity = interessatBuilder.build();
		
		if (registreInteressat.getRepresentant() != null) {
			RegistreInteressat representant = registreInteressat.getRepresentant();
			interessatEntity.updateRepresentant(
					RegistreInteressatTipusEnum.valorAsEnum(representant.getTipus()),
					RegistreInteressatDocumentTipusEnum.valorAsEnum(representant.getDocumentTipus()),
					representant.getDocumentNum(),
					representant.getNom(),
					representant.getLlinatge1(),
					representant.getLlinatge2(),
					representant.getRaoSocial(),
					representant.getPais(),
					representant.getPaisCodi(),
					representant.getProvincia(),
					representant.getProvinciaCodi(),
					representant.getMunicipi(),
					representant.getMunicipiCodi(),
					representant.getAdresa(),
					representant.getCodiPostal(),
					representant.getEmail(),
					representant.getTelefon(),
					representant.getEmailHabilitat(),
					RegistreInteressatCanalEnum.valorAsEnum(representant.getCanalPreferent()),
					representant.getCodiDire());
		}
		registreInteressatRepository.save(interessatEntity);
		return interessatEntity;
	}

	private RegistreAnnexEntity crearAnnexEntity(
			RegistreAnnex registreAnnex,
			RegistreEntity registre) {
		String gestioDocumentalId = null;
		if (registreAnnex.getFitxerContingut() != null) {
			gestioDocumentalId = gestioDocumentalHelper.gestioDocumentalCreate(
					GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP,
					registreAnnex.getFitxerContingut());
		}
		
		String metaDades = null;
		if (registreAnnex.getMetaDades() != null) {
			try {
				metaDades = new ObjectMapper().writeValueAsString(registreAnnex.getMetaDades());
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}

		// Assegura que l'annex tingui tipus MIME i si és un .pdf sigui application/pdf
		String tipusMime = registreAnnex.getFitxerTipusMime();
		if ("pdf".equals(FilenameUtils.getExtension(registreAnnex.getFitxerNom().toLowerCase()))) {
			tipusMime = "application/pdf";
		} else if (tipusMime == null || tipusMime.trim().isEmpty()) {
			tipusMime = new MimetypesFileTypeMap().getContentType(registreAnnex.getFitxerNom());
		}
		
		RegistreAnnexEntity annexEntity = RegistreAnnexEntity.getBuilder(
				registreAnnex.getTitol(),
				registreAnnex.getFitxerNom(),
				registreAnnex.getFitxerTamany(),
				registreAnnex.getFitxerArxiuUuid(),
				registreAnnex.getEniDataCaptura(),
				RegistreAnnexOrigenEnum.valorAsEnum(registreAnnex.getEniOrigen()),
				RegistreAnnexNtiTipusDocumentEnum.valorAsEnum(registreAnnex.getEniTipusDocumental()),
				RegistreAnnexSicresTipusDocumentEnum.valorAsEnum(registreAnnex.getSicresTipusDocument()),
				registre).
				fitxerTipusMime(tipusMime).
				localitzacio(registreAnnex.getLocalitzacio()).
				ntiElaboracioEstat(RegistreAnnexElaboracioEstatEnum.valorAsEnum(registreAnnex.getEniEstatElaboracio())).
				observacions(registreAnnex.getObservacions()).
				metaDades(metaDades).
				build();
		annexEntity.updateGesdocDocumentId(gestioDocumentalId);
		registreAnnexRepository.saveAndFlush(annexEntity);
		if (registreAnnex.getFirmes() != null && registreAnnex.getFirmes().size() > 0) {
			for (Firma firma: registreAnnex.getFirmes()) {
				annexEntity.getFirmes().add(
						crearFirmaEntity(
								firma,
								annexEntity));
			}
		}
		return annexEntity;
	}

	private RegistreAnnexFirmaEntity crearFirmaEntity(
			Firma firma,
			RegistreAnnexEntity annex) {
		String gestioDocumentalId = null;
		if (firma.getContingut() != null) {
			gestioDocumentalId = gestioDocumentalHelper.gestioDocumentalCreate(
					GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP,
					firma.getContingut());
		}
		RegistreAnnexFirmaEntity firmaEntity = RegistreAnnexFirmaEntity.getBuilder(
				firma.getTipus(),
				firma.getPerfil(),
				firma.getFitxerNom(),
				firma.getTipusMime(),
				firma.getCsvRegulacio(),
				false,
				annex).build();
		firmaEntity.updateGesdocFirmaId(gestioDocumentalId);
		registreAnnexFirmaRepository.save(firmaEntity);
		return firmaEntity;
	}


	private static final Logger logger = LoggerFactory.getLogger(RegistreHelper.class);

	/** Consulta el número de còpia màxim pel registre passat com a paràmetre. Quan es copoia una anotació de registre
	 * a una altra bústia s'ha d'informar del número de còpia per poder distingir-lo de les altres anotacions que tenen
	 * el mateix llibre, data, numero i entitat.
	 * @param registre
	 * @return Retorna el número màxim de registre.
	 */
	@Transactional
	public Integer getMaxNumeroCopia(RegistreEntity registre) {
		Integer numeroCopia = 
					registreRepository.findMaxNumeroCopia(
										registre.getEntitatCodi(),
										registre.getLlibreCodi(),
										registre.getData());
		return numeroCopia != null ? numeroCopia : 0;
	}

	/** Consulta els registres pedents de guardar els annexos a l'Arxiu. */
	@Transactional
	public List<RegistreEntity> findGuardarAnnexPendents(int maxReintents) {
		return 	registreRepository.findGuardarAnnexPendents(maxReintents);
	}

	/** Consulta els registres pendents d'enviar al backoffice. */
	@Transactional
	public List<RegistreEntity> findAmbEstatPendentEnviarBackoffice(Date date, int maxReintents) {
		return registreRepository.findAmbEstatPendentEnviarBackoffice(date, maxReintents);
	}

	/** Consulta les anotacions pendents d'aplicar regles amb un màxim de reintents. */
	@Transactional
	public List<RegistreEntity> findAmbReglaPendentAplicar(int maxReintents) {
		return registreRepository.findAmbReglaPendentAplicar(maxReintents);
	}

	/** Consulta les anotacions pendents de tancar a l'arxiu. */
	@Transactional
	public List<RegistreEntity> findPendentsTancarArxiu(Date date) {
		return registreRepository.findPendentsTancarArxiu(date);
	}
	
	
}
