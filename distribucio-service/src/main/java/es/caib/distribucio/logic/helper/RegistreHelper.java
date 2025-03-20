/**
 * 
 */
package es.caib.distribucio.logic.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.XMLConstants;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.w3c.dom.NodeList;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;

import es.caib.distribucio.logic.intf.dto.ArxiuFirmaDetallDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.BackofficeTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.DocumentEniRegistrableDto;
import es.caib.distribucio.logic.intf.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.FitxerDto;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.LogTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.distribucio.logic.intf.dto.RegistreAnnexDto;
import es.caib.distribucio.logic.intf.dto.RegistreDto;
import es.caib.distribucio.logic.intf.dto.ReglaDto;
import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.exception.SistemaExternException;
import es.caib.distribucio.logic.intf.exception.ValidationException;
import es.caib.distribucio.logic.intf.registre.Firma;
import es.caib.distribucio.logic.intf.registre.RegistreAnnex;
import es.caib.distribucio.logic.intf.registre.RegistreAnnexElaboracioEstatEnum;
import es.caib.distribucio.logic.intf.registre.RegistreAnnexNtiTipusDocumentEnum;
import es.caib.distribucio.logic.intf.registre.RegistreAnnexOrigenEnum;
import es.caib.distribucio.logic.intf.registre.RegistreAnnexSicresTipusDocumentEnum;
import es.caib.distribucio.logic.intf.registre.RegistreAnotacio;
import es.caib.distribucio.logic.intf.registre.RegistreInteressat;
import es.caib.distribucio.logic.intf.registre.RegistreInteressatCanalEnum;
import es.caib.distribucio.logic.intf.registre.RegistreInteressatDocumentTipusEnum;
import es.caib.distribucio.logic.intf.registre.RegistreInteressatTipusEnum;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.logic.intf.registre.RegistreTipusEnum;
import es.caib.distribucio.logic.intf.registre.ValidacioFirmaEnum;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnnexEstat;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.logic.intf.service.ws.backoffice.BackofficeWsService;
import es.caib.distribucio.logic.service.RegistreServiceImpl;
import es.caib.distribucio.logic.service.SegonPlaServiceImpl.GuardarAnotacioPendentThread;
import es.caib.distribucio.persist.entity.BackofficeEntity;
import es.caib.distribucio.persist.entity.ContingutEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.RegistreAnnexEntity;
import es.caib.distribucio.persist.entity.RegistreAnnexFirmaEntity;
import es.caib.distribucio.persist.entity.RegistreEntity;
import es.caib.distribucio.persist.entity.RegistreFirmaDetallEntity;
import es.caib.distribucio.persist.entity.RegistreInteressatEntity;
import es.caib.distribucio.persist.entity.ReglaEntity;
import es.caib.distribucio.persist.entity.UsuariEntity;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.RegistreAnnexFirmaRepository;
import es.caib.distribucio.persist.repository.RegistreAnnexRepository;
import es.caib.distribucio.persist.repository.RegistreFirmaDetallRepository;
import es.caib.distribucio.persist.repository.RegistreInteressatRepository;
import es.caib.distribucio.persist.repository.RegistreRepository;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnnex;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnotacio;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreFirma;
import es.caib.distribucio.plugin.utils.TemporalThreadStorage;
import es.caib.distribucio.plugin.validacio.ValidaSignaturaResposta;
import es.caib.pluginsib.arxiu.api.ContingutArxiu;
import es.caib.pluginsib.arxiu.api.ContingutTipus;
import es.caib.pluginsib.arxiu.api.Document;
import es.caib.pluginsib.arxiu.api.DocumentContingut;
import es.caib.pluginsib.arxiu.api.DocumentEstat;
import es.caib.pluginsib.arxiu.api.DocumentMetadades;
import es.caib.pluginsib.arxiu.api.Expedient;
import es.caib.pluginsib.arxiu.api.FirmaTipus;

/**
 * Mètodes comuns per a aplicar regles.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class RegistreHelper {

//	private static final String URL_API_BACKOFFICE = "/distribucio/api/rest/backoffice";
	private boolean autenticacioBasic = true;

	@Autowired
	private RegistreRepository registreRepository;
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;
	@Autowired
	private RegistreAnnexFirmaRepository registreAnnexFirmaRepository;
	@Autowired
	private RegistreInteressatRepository registreInteressatRepository;
	@Autowired
	private RegistreFirmaDetallRepository registreFirmaDetallRepository;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private GestioDocumentalHelper gestioDocumentalHelper;
	@Autowired
	private ReglaHelper reglaHelper;
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
	@Autowired
	private PaginacioHelper paginacioHelper;

	@PersistenceContext
	private EntityManager entityManager;

	/** Referència pròpia per cridar mètodes de forma transaccional */
	private RegistreHelper self;
	@Autowired
	private ApplicationContext applicationContext;

	@PostConstruct
	public void postContruct() {
		self = applicationContext.getBean(RegistreHelper.class);
	}

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
					registreAnotacio.getServeiCodi(),
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
				// Si ve informat amb uuid no guardar en filesystem
				boolean isGuardarEnFilesystem = registreAnnex.getFitxerArxiuUuid() == null;
				registre.getAnnexos().add(
						crearAnnexEntity(
								isGuardarEnFilesystem,
								registreAnnex,
								registre));
			}
		}
		contextsaveAnnexos.stop();
		return registre;
	}

	public void assignar(UsuariEntity usuari, UsuariEntity usuariActual, RegistreEntity registreEntity) {
		registreEntity.updateAgafatPer(usuari);
		List<String> params = new ArrayList<>();
		params.add(usuariActual.getNom());
		params.add(usuari.getNom());
		contingutLogHelper.log(
				registreEntity,
				LogTipusEnumDto.ASSIGNAR,
				params,
				false);
	}

	public void bloquejar(RegistreEntity registreEntity, String usuariCodi) {
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
		String clauAcces =  new String(Base64.getEncoder().encode(encryptResult));

		return clauAcces;
	}

	public String encriptar(String missatgeAEncriptar) throws Exception {
		SecretKeySpec secretKey = generarClau(this.getClauSecretaProperty());
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		String encriptat = new String(Base64.getEncoder().encode(cipher.doFinal(missatgeAEncriptar.getBytes("UTF-8"))));
		encriptat = encriptat.replace("/", "%252F");
		encriptat = encriptat.replace("==", "");
		return encriptat;
	}

	public String desencriptar(String missatgeADesencriptar) throws Exception {
		SecretKeySpec secretKey = generarClau(this.getClauSecretaProperty());
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return new String(cipher.doFinal(Base64.getDecoder().decode(missatgeADesencriptar)));
	}

	public static SecretKeySpec generarClau(String clauSecreta) throws Exception {
		SecretKeySpec secretKey = null;
		byte[] key;
		MessageDigest sha = null;
	    try {
	      key = clauSecreta.getBytes("UTF-8");
	      sha = MessageDigest.getInstance("SHA-1");
	      key = sha.digest(key);
	      key = Arrays.copyOf(key, 16);
	      secretKey = new SecretKeySpec(key, "AES");
	    } catch (NoSuchAlgorithmException e) {
	      e.printStackTrace();
	    }
	      return secretKey;
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
		RegistreEntity registre = annex.getRegistre();
		List<ArxiuFirmaDto> firmes = null;
		if (annex.getFirmes() != null) {
			firmes = new ArrayList<ArxiuFirmaDto>();
			for (RegistreAnnexFirmaEntity annexFirma: annex.getFirmes()) {
				byte[] firmaContingut = null;
				
				if (annexFirma.getGesdocFirmaId() != null) {
					firmaContingut = this.getFirmaContingut(annexFirma.getGesdocFirmaId(),registre.getNumero());
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
	
	
	public void processarAnotacioPendentArxiuInThreadExecutor(Long registreId) {
		
		Timer.Context context = metricRegistry.timer(MetricRegistry.name(GuardarAnotacioPendentThread.class, "processarAnotacioPendentArxiu")).time();
		logger.debug("Processant anotacio pendent de guardar a l'arxiu (registreId=" + registreId + ")");
		
		long startTime = new Date().getTime();
    	
    	Throwable excepcio = null;
        try {
			excepcio = processarAnotacioPendentArxiu(registreId);
		} catch (NotFoundException e) {
			if (e.getObjectClass() == UnitatOrganitzativaDto.class) {
				excepcio = null;
			}
		} catch (Throwable e) {
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
	}
	
	/** Mètode que s'ha d'executar sense transacció per poder anar guardant anotació i annexos en
	 * transaccions separades per evitar error de timeout en la transacció.
	 * 
	 * @param anotacioId
	 * @return
	 */
	public Throwable processarAnotacioPendentArxiu(Long anotacioId) {

		if (TransactionSynchronizationManager.isActualTransactionActive()) {
			logger.warn("La transacció actual està activa, es desactiva per poder tractar anotació i annexos per separat.");
			TransactionSynchronizationManager.setActualTransactionActive(false);
		}
		
		// PROCESSAR ARXIU
		List<Throwable> exceptionsGuardantAnnexos = createRegistreAndAnnexosInArxiu(anotacioId);
		if (exceptionsGuardantAnnexos == null) {

			DistribucioRegistreAnotacio distribucioRegistreAnotacio = self.getDistribucioRegistreAnotacio(anotacioId);
			boolean allRegistresWithSameNumeroSavedInArxiu = true;
			if (distribucioRegistreAnotacio.getAnnexos() != null ) {
				for (DistribucioRegistreAnnex annex : distribucioRegistreAnotacio.getAnnexos()) {
					if (annex.getFitxerArxiuUuid() == null || annex.getFitxerArxiuUuid().isEmpty()) {
						allRegistresWithSameNumeroSavedInArxiu = false;
					}
				}
			}
			if (allRegistresWithSameNumeroSavedInArxiu) {
				gestioDocumentalHelper.esborrarDocsTemporals(anotacioId);
			}
		}
		self.updateAnotacioEstat(anotacioId, exceptionsGuardantAnnexos);
		return exceptionsGuardantAnnexos != null && !exceptionsGuardantAnnexos.isEmpty() ? 
					exceptionsGuardantAnnexos.get(0) 
					: null;
	}

	@Transactional
	public Exception processarAnotacioPendentRegla(Long anotacioId) {
		RegistreEntity anotacio = registreRepository.getReferenceById(anotacioId);
		Exception exceptionAplicantRegla = null;
		if (anotacio.getRegla() != null) {
			if (anotacio.getRegla().isActiva()) {
				List<ReglaEntity> reglesApplied = new ArrayList<ReglaEntity>();
				exceptionAplicantRegla = reglaHelper.aplicarControlantException(anotacio, reglesApplied);
				if (exceptionAplicantRegla != null) {
					anotacio.updateProces(
							null,
							exceptionAplicantRegla);
				}
			} else {
				// Corregeix l'estat a pendent d'Arxiu per a que segueixi el procés fins a bústia pendent
				anotacio.updateRegla(null);
				Boolean isRegistreArxiuPendent = registreRepository.isRegistreArxiuPendentByUuid(anotacio.getId(), anotacio.getEntitat());
				if (isRegistreArxiuPendent)
					anotacio.setNewProcesEstat(RegistreProcesEstatEnum.ARXIU_PENDENT);
				else
					anotacio.setNewProcesEstat(RegistreProcesEstatEnum.BUSTIA_PENDENT);
			}
		} else {
			// Corregeix l'estat a pendent d'Arxiu per a que segueixi el procés fins a bústia pendent
			Boolean isRegistreArxiuPendent = registreRepository.isRegistreArxiuPendentByUuid(anotacio.getId(), anotacio.getEntitat());
			if (isRegistreArxiuPendent)
				anotacio.setNewProcesEstat(RegistreProcesEstatEnum.ARXIU_PENDENT);
			else
				anotacio.setNewProcesEstat(RegistreProcesEstatEnum.BUSTIA_PENDENT);
		}
		return exceptionAplicantRegla;
	}

	/**
	 *  Mètode no transaccional per guardar l'anotació i els annexos en diferents transaccions.
	 *  
	 * @param registreEntity
	 * @param codiDir3
	 * @param crearAutofirma
	 * @return
	 */
	public List<Throwable> createRegistreAndAnnexosInArxiu(
			long anotacioId) {
		
		DistribucioRegistreAnotacio distribucioRegistreAnotacio = 
				self.getDistribucioRegistreAnotacio(anotacioId);
		
		String unitatOrganitzativaCodi = distribucioRegistreAnotacio.getUnitatOrganitzativaCodi();
		List<Throwable> exceptions = null;
		
		if (distribucioRegistreAnotacio.getAnnexos() != null && distribucioRegistreAnotacio.getAnnexos().size() > 0) {

			String uuidExpedient = null;
			// check if registre is not already created in arxiu
			if (distribucioRegistreAnotacio.getExpedientArxiuUuid() == null) {
				
				exceptions = self.crearExpedientArxiu(
								distribucioRegistreAnotacio, 
								unitatOrganitzativaCodi, 
								uuidExpedient);
				if (exceptions != null && !exceptions.isEmpty()) {
					return exceptions;
				}
				uuidExpedient = distribucioRegistreAnotacio.getArxiuUuid();
				
			// Si el contenidor ja està creat agafam el seu UUID
			} else {
				uuidExpedient = distribucioRegistreAnotacio.getExpedientArxiuUuid();
				logger.trace("L'anotació (" +
						"anotacioId=" + distribucioRegistreAnotacio.getId() + ", " +
						"anotacioNumero=" + distribucioRegistreAnotacio.getNumero() + ", " +
						"unitatOrganitzativaCodi=" + unitatOrganitzativaCodi + ") ja estava a l'Arxiu amb uuid " + uuidExpedient);
			}
			
			if (uuidExpedient != null) {
				logger.debug("Guardant " + distribucioRegistreAnotacio.getAnnexos().size() + " annexos de l'anotació (" +
						"anotacioId=" + distribucioRegistreAnotacio.getId() + ", " +
						"anotacioNumero=" + distribucioRegistreAnotacio.getNumero() + ", " +
						"unitatOrganitzativaCodi=" + unitatOrganitzativaCodi + ") amb uuid " + uuidExpedient + " a l'Arxiu.");				
				exceptions = new ArrayList<>();
				List<String> titolsAnnexes = new ArrayList<>();
				for (DistribucioRegistreAnnex annex : distribucioRegistreAnotacio.getAnnexos()) {
					try {
						boolean titolRepetit = titolsAnnexes.contains(annex.getTitol());
				        if (!titolRepetit) {
				            titolsAnnexes.add(annex.getTitol());
				        }
						self.crearAnnexInArxiu(
								annex.getId(), 
								annex, 
								unitatOrganitzativaCodi,
								uuidExpedient, 
								distribucioRegistreAnotacio.getProcedimentCodi(), 
								titolRepetit);
					} catch (Throwable th) {
						logger.error("Error creant l'annex " + annex.getId() + " " + annex.getFitxerNom() + " de l'anotació " 
										+ distribucioRegistreAnotacio.getNumero() + ": " + th.getMessage(), th );
						exceptions.add(th);
					}
				}				
			}
		}
		// Actualitza el número d'annexos en estat esborrany
		self.comptarAnnexosEstatEsborrany(anotacioId);
		
		if (exceptions != null && !exceptions.isEmpty()) {
			return exceptions;
		} else {
			logger.trace("Creació del contenidor i dels annexos finalitzada correctament (" +
					"anotacioId=" + anotacioId + ", " +
					"anotacioNumero=" + distribucioRegistreAnotacio.getNumero() + ", " +
					"unitatOrganitzativaCodi=" + unitatOrganitzativaCodi + ")");
			
			self.crearLogDistribucio(anotacioId);
			
			return null;
		}
	}	


	/** Compta el número d'annexos de l'anotació en estat esborrany i ho informa a l'anotació. */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void comptarAnnexosEstatEsborrany(long anotacioId) {
		RegistreEntity registre = registreRepository.getReferenceById(anotacioId);
		registre.setAnnexosEstatEsborrany(
				registreAnnexRepository.countByRegistreAndArxiuEstat(registre, AnnexEstat.ESBORRANY).intValue());
		registreRepository.save(registre);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void crearLogDistribucio(long anotacioId) {
		RegistreEntity registreEntity = registreRepository.getReferenceById(anotacioId);
		List<String> params = new ArrayList<>();
		params.add(registreEntity.getNom());
		params.add(null);
		contingutLogHelper.log(
				registreEntity,
				LogTipusEnumDto.DISTRIBUCIO,
				params,
				false);
	}

	@Transactional
	private void tancarContenidorAmbAnnexos(
			DistribucioRegistreAnotacio distribucioRegistreAnotacio,
			String unitatOrganitzativaCodi) {
		RegistreEntity registreEntity = registreRepository.getReferenceById(distribucioRegistreAnotacio.getId());
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
	}

	/** Mètode per validar les firmes de l'annex tingui o no firmes per revisar si 
	 * l'annex té firmes invàlides.
	 * @param firmes 
	 * 
	 * @param distribucioAnnex
	 */
	@Transactional
	public ValidacioFirmaEnum validaFirmes(RegistreAnnexEntity annex, List<DistribucioRegistreFirma> firmes) {
		logger.debug("Validant firmes de l'annex \"" + annex.getTitol() + "\" de l'anotació " + annex.getRegistre().getIdentificador());
		ValidacioFirmaEnum validacioFirmaEstat = ValidacioFirmaEnum.NO_VALIDAT;
		String validacioFirmaError = null;
		FitxerDto fitxer = this.getAnnexFitxer(annex.getId(), false);
		byte[] documentContingut = fitxer.getContingut();
		byte[] firmaContingut = null;
		RegistreEntity registre = annex.getRegistre();
		if (firmes == null) {
			firmes = new ArrayList<>();
		}
		// 0 - Mira si és un PDF sense firmes
		boolean senseFirmes = false;
		if ((annex.getFirmes() == null
				|| annex.getFirmes().isEmpty()) ) {
			
			if ("application/pdf".equals(annex.getFitxerTipusMime()) ) {
				PdfReader reader;
				try {
					reader = new PdfReader(documentContingut);
					AcroFields acroFields = reader.getAcroFields();
					List<String> signatureNames = acroFields.getSignatureNames();
					if (signatureNames == null || signatureNames.isEmpty()) {
						senseFirmes = true;
						validacioFirmaEstat = ValidacioFirmaEnum.SENSE_FIRMES;
					}
				} catch (Exception e) {
					logger.debug("Error validant si l'annex PDF \"" + annex.getTitol() + "\" de l'anotació " + annex.getRegistre().getIdentificador() + " conté informació de firmes amb PdfReader.");
				}
			}  else if ("application/xml".equals(annex.getFitxerTipusMime())) {
				try {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
					dbf.setNamespaceAware(true); 
					DocumentBuilder db = dbf.newDocumentBuilder();
					org.w3c.dom.Document doc = db.parse(new ByteArrayInputStream(documentContingut));
					NodeList signatures = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
					if (signatures == null || signatures.getLength() <= 0) {
						senseFirmes = true;
						validacioFirmaEstat = ValidacioFirmaEnum.SENSE_FIRMES;
					}
				} catch (Exception e) {
					logger.warn("Error validant si l'annex XML \"" + annex.getTitol() + "\" de l'anotació " + annex.getRegistre().getIdentificador() + " conté informació de firmes.");
				}
			}
		}

		if ((!senseFirmes 	&& pluginHelper.isValidaSignaturaPluginActiu())) {
			// Si el document per separat no té firmes o té firmes vàlides llavors comprova les firmes
			boolean annexFirmat = annex.getFirmes() != null && !annex.getFirmes().isEmpty();
			if (annexFirmat) {
				// 1- Valida les firmes de l'annex
				int nFirma = 1;
				for (RegistreAnnexFirmaEntity firma : annex.getFirmes()) {
					
					// Si encara no està a l'Arxiu o està en estat esborrany recupera el fitxer de firma de la gesió documental.
					if (annex.getFitxerArxiuUuid() == null || AnnexEstat.ESBORRANY.equals(annex.getArxiuEstat())) {
						if (!"TF05".equals(firma.getTipus())) { // <> TF05 CAdDES attached
							firmaContingut = this.getFirmaContingut(firma.getGesdocFirmaId(),registre.getNumero());
						}
					} else {
						// Altrament obté la 1a firma de l'Arxiu
						es.caib.pluginsib.arxiu.api.Firma firmaArxiu = this.getFirma(annex, 0);
						if (firmaArxiu != null) {
							firmaContingut = firmaArxiu.getContingut();
						}
					}
					try {
						logger.debug("Validant la firma " + nFirma++ + "/" + annex.getFirmes().size() + " " + firma.getTipus()
									 + " de l'annex \"" + annex.getTitol() + "\" de l'anotació " + annex.getRegistre().getIdentificador());
						
						ValidaSignaturaResposta validacioFirma = pluginHelper.validaSignaturaObtenirDetalls(documentContingut, firmaContingut,registre.getNumero());
						switch(validacioFirma.getStatus()) {
							case ValidaSignaturaResposta.FIRMA_VALIDA:
								validacioFirmaEstat = ValidacioFirmaEnum.FIRMA_VALIDA;
								break;
							case ValidaSignaturaResposta.FIRMA_INVALIDA:
								validacioFirmaEstat = ValidacioFirmaEnum.FIRMA_INVALIDA;
								validacioFirmaError = validacioFirma.getCausaInvalida();
								break;
							case ValidaSignaturaResposta.FIRMA_ERROR:
								validacioFirmaEstat = ValidacioFirmaEnum.ERROR_VALIDANT;
								validacioFirmaError = validacioFirma.getCausaInvalida();
								break;
							default:
								validacioFirmaEstat = ValidacioFirmaEnum.SENSE_FIRMES;
						}
						firmes.add(toRegistreFirmes(validacioFirma, firmaContingut, firma.getTipusMime()));
					} catch(Throwable th) {
						logger.error("Error validant una firma del document", th);
						validacioFirmaEstat = ValidacioFirmaEnum.ERROR_VALIDANT;
						validacioFirmaError = "Error no controlat validant les firmes del document: " + th.getMessage();
					}
					// Si troba un error s'atura de valida
					if (validacioFirmaEstat == ValidacioFirmaEnum.ERROR_VALIDANT 
							||validacioFirmaEstat == ValidacioFirmaEnum.FIRMA_INVALIDA ) 
					{
						break;
					}
				}
			} else {
				// 1- Valida el document com si no tingués firmes
				try {
					ValidaSignaturaResposta validacioFirma = pluginHelper.validaSignaturaObtenirDetalls(null, documentContingut,registre.getNumero());
					switch(validacioFirma.getStatus()) {
						case ValidaSignaturaResposta.FIRMA_VALIDA:
							validacioFirmaEstat = ValidacioFirmaEnum.FIRMA_VALIDA;
							break;
						case ValidaSignaturaResposta.FIRMA_INVALIDA:
							validacioFirmaEstat = ValidacioFirmaEnum.FIRMA_INVALIDA;
							validacioFirmaError = validacioFirma.getCausaInvalida();
							break;
						case ValidaSignaturaResposta.FIRMA_ERROR:
							validacioFirmaEstat = ValidacioFirmaEnum.ERROR_VALIDANT;
							validacioFirmaError = validacioFirma.getCausaInvalida();
							break;
						default:
							validacioFirmaEstat = ValidacioFirmaEnum.SENSE_FIRMES;
					}
					firmes.add(toRegistreFirmes(validacioFirma, documentContingut, annex.getFitxerTipusMime()));
				} catch(Exception e) {
					// Determina si és error perquè no té firmes o validant
					Throwable throwable = ExceptionHelper.getRootCauseOrItself(e);
					String message = throwable.getMessage() != null ? throwable.getMessage() : String.valueOf(throwable);
					if (message.contains("El formato de la firma no es valido(urn:oasis:names:tc:dss:1.0:resultmajor:RequesterError)") 
							|| message.contains("El formato de la firma no es válido(urn:oasis:names:tc:dss:1.0:resultmajor:RequesterError)") 
							|| message.contains("El documento OOXML no está firmado(urn:oasis:names:tc:dss:1.0:resultmajor:ResponderError)")
							|| message.contains("El documento OOXML no está firmado.(urn:oasis:names:tc:dss:1.0:resultmajor:ResponderError)")) {
						validacioFirmaEstat = ValidacioFirmaEnum.SENSE_FIRMES;
					} else {
						logger.error("Error validant les firmes del document", e);
						validacioFirmaEstat = ValidacioFirmaEnum.ERROR_VALIDANT;
						validacioFirmaError = "Error no controlat validant: " + e.getMessage();
					}
				}
			}
			// Valida que no tingui més de 1 firma PADES
			int nPades = 0;
			for (DistribucioRegistreFirma firma : firmes) {
				if ("TF06".equals(firma.getTipus())) {
					nPades++;
				}
			}
			if (nPades > 1) {
				validacioFirmaEstat = ValidacioFirmaEnum.FIRMA_INVALIDA;
				validacioFirmaError = "Els annexos no poden tenir més d'una firma PAdES";
			}
		}
		annex.setValidacioFirmaEstat(validacioFirmaEstat);
		annex.setValidacioFirmaError(validacioFirmaError);
		logger.debug("Validació firmes de l'annex \"" + annex.getTitol() + "\" de l'anotació " + annex.getRegistre().getIdentificador() + " finalitzada: " +
							validacioFirmaEstat + " " + (validacioFirmaError != null ? validacioFirmaError : ""));
		return validacioFirmaEstat;
	}

	/** Serverix per convertir les firmes reconegudes d'un document després de la validació i afegir-les al registre. 
	 * @param validacioFirma
	 * @param contingut 
	 * @param tipusMime 
	 */
	private DistribucioRegistreFirma toRegistreFirmes(ValidaSignaturaResposta validacioFirma, byte[] contingut, String tipusMime) {
		DistribucioRegistreFirma registreFirma = new DistribucioRegistreFirma();
		registreFirma.setTipus(validacioFirma.getTipus());
		registreFirma.setPerfil(validacioFirma.getPerfil());
		registreFirma.setTipusMime(tipusMime);
//			private String fitxerNom;
//			private String csvRegulacio;
		registreFirma.setContingut(contingut);
		if (contingut != null) {
			registreFirma.setTamany(contingut.length);
		}
		return registreFirma;
	}

	public FitxerDto getJustificant(Long registreId) {
		RegistreEntity registre = registreRepository.getReferenceById(registreId);
		FitxerDto arxiu = new FitxerDto();
		Document document = null;
		document = pluginHelper.arxiuDocumentConsultar(registre.getJustificantArxiuUuid(),
				null,
				true,
				true,
				registre.getNumero());
		if (document != null) {
			DocumentContingut documentContingut = document.getContingut();
			if (documentContingut != null) {
				arxiu.setNom(self.obtenirJustificantNom(document));
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
		RegistreAnnexEntity registreAnnexEntity = registreAnnexRepository.getReferenceById(annexId);
		RegistreEntity registre = registreAnnexEntity.getRegistre();
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
								streamAnnex,
								registre.getNumero());
						documentContingut = streamAnnex.toByteArray();
					}
					byte[] firmaContingut = null;
					if (registreAnnexFirmaEntity.getGesdocFirmaId() != null && !registreAnnexFirmaEntity.getGesdocFirmaId().isEmpty()) {
						firmaContingut = this.getFirmaContingut(registreAnnexFirmaEntity.getGesdocFirmaId(),registre.getNumero());
					}
					ValidaSignaturaResposta validacioFirma = pluginHelper.validaSignaturaObtenirDetalls(
							documentContingut,
							firmaContingut,
							registre.getNumero()); 
					arxiuFirmaDto.setDetalls(validacioFirma.getFirmaDetalls());
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
				false,
				false,
				true);

		RegistreEntity registre = registreRepository.findByEntitatAndId(
				entitat,
				registreId);

		if (!usuariHelper.isAdmin() && !usuariHelper.isAdminLectura() && !isVistaMoviments)
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
	

	
	
	
	private void loadSignaturaDetallsToDB(RegistreAnnexEntity annexEntity) {
		
		logger.debug("Guardant els detalls de la firma a la BBDD de l'annex " + annexEntity.getId() + " " + annexEntity.getTitol() + " de l'anotació " + annexEntity.getRegistre().getNumero());
		
		RegistreEntity registre = annexEntity.getRegistre();
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
					true,
					registre.getNumero());
			contexarxiuDocumentConsultar.stop();

			// set fitxer size if unset
			if (annexEntity.getFitxerTamany() <= 0 && document.getContingut() != null) {
				annexEntity.updateFitxerTamany(
							(int)document.getContingut().getTamany());
			}							
			// Guarda l'estat del documetn a l'Arxiu
			AnnexEstat estatAnterior = annexEntity.getArxiuEstat();
			switch(document.getEstat()) {
			case DEFINITIU:
				annexEntity.setArxiuEstat(AnnexEstat.DEFINITIU);
				break;
			case ESBORRANY:
				annexEntity.setArxiuEstat(AnnexEstat.ESBORRANY);
				break;
			}
			// Si passa d'esborrany a definitiu resta un en el recompte d'annexos en estat d'esborrany de l'anotació
			if ((estatAnterior == null || estatAnterior == AnnexEstat.ESBORRANY )
					&& annexEntity.getArxiuEstat() == AnnexEstat.DEFINITIU) {
				annexEntity.getRegistre().setAnnexosEstatEsborrany(
						Math.max(0, annexEntity.getRegistre().getAnnexosEstatEsborrany() - 1));
			}
			DocumentMetadades metadades = document.getMetadades();
			if (metadades != null) {
				annexEntity.updateFirmaCsv(metadades.getCsv());
			}
			
			if (document.getFirmes() != null && document.getFirmes().size() > 0 && annexEntity.getFirmes().size() > 0) {
				List<RegistreAnnexFirmaEntity> firmes = annexEntity.getFirmes();
				Iterator<es.caib.pluginsib.arxiu.api.Firma> it = document.getFirmes().iterator();
				
				int firmaIndex = 0;
				while (it.hasNext()) {
					es.caib.pluginsib.arxiu.api.Firma arxiuFirma = it.next();
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
							ValidaSignaturaResposta validacioFirma = pluginHelper.validaSignaturaObtenirDetalls(
									documentContingut,
									firmaContingut,
									registre.getNumero()) ;
							List<ArxiuFirmaDetallDto> firmaDetalls = validacioFirma.getFirmaDetalls();
							contevalidaSignaturaObtenirDetalls.stop();
							
							for (ArxiuFirmaDetallDto arxiuFirmaDetallDto : firmaDetalls) {
								RegistreFirmaDetallEntity firmaDetallEntity = RegistreFirmaDetallEntity.getBuilder(
										arxiuFirmaDetallDto,
										firma).build();
								firma.getDetalls().add(firmaDetallEntity);
								registreFirmaDetallRepository.save(firmaDetallEntity);
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
			logger.error("Error al carregar singatura detalls a la base de dades de l'annex " + annexEntity.getId() + " " + annexEntity.getTitol() 
						+ " de l'anotació " + annexEntity.getRegistre().getNumero(), e);
		}
	}
	
	@Transactional
	public void tancarExpedientArxiu(Long registreId) {
		RegistreEntity registre = registreRepository.getReferenceById(registreId);
		Exception exception = null;
		try {
			if (registre.getExpedientArxiuUuid() != null) {
				Expedient expedient = pluginHelper.arxiuExpedientInfo(registre.getExpedientArxiuUuid(),registre.getNumero());

				if (expedient.getContinguts() == null || expedient.getContinguts().isEmpty()) {
					// Si no té annexos esborra l'espedient, el tancament fallaria
					pluginHelper.arxiuExpedientEliminar(registre.getExpedientArxiuUuid(),registre.getNumero());
				} else {
					// Primer comprova si hi ha cap document en estat d'esborrany
					for(ContingutArxiu contingut : expedient.getContinguts()) {
						Document document = pluginHelper.arxiuDocumentConsultar(
								contingut.getIdentificador(), null, false,registre.getNumero());
						if (ContingutTipus.DOCUMENT.equals(contingut.getTipus()) 
								&& DocumentEstat.ESBORRANY.equals(document.getEstat())) 
						{
								throw new ValidationException("No es pot tancar perquè hi ha documents en estat esborrany.");
						}
					}
					// Tanca l'expedient
					pluginHelper.arxiuExpedientTancar(registre);
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

	/** Envia els identificadors pendents d'una regla. */
	@Transactional(readOnly = true)
	public Exception enviarIdsAnotacionsBackoffice(List<Long> pendentsIdsGroupedByRegla) {
		Exception exception = null;
		ReglaEntity regla = null;
		try {
			List <RegistreEntity> pendentsByRegla = new ArrayList<>();
			for(Long id: pendentsIdsGroupedByRegla){
				RegistreEntity pendent = registreRepository.getReferenceById(id);
				pendentsByRegla.add(pendent);
			}
			if (pendentsByRegla.isEmpty())
				return new Exception("La llista d'anotacions pendents és buida");

			Map<String, String> accioParams = new HashMap<String, String>();
			regla = pendentsByRegla.get(0).getRegla();
			
			BackofficeEntity backofficeDesti = regla.getBackofficeDesti();
			if (backofficeDesti == null) {
				return new RuntimeException("No existeix cap backoffice destí per aplicar amb la regla " + regla.getNom());
			}
			accioParams.put("Regla", regla.getId() + " - " + regla.getNom());
			accioParams.put("Backoffice", backofficeDesti.getCodi());


			String clauSecreta = configHelper.getConfig(
					"es.caib.distribucio.backoffice.integracio.clau");
			if (clauSecreta == null) {
				return new RuntimeException("Clau secreta no especificada al fitxer de propietats");
			}
			
			List<AnotacioRegistreId> ids = new ArrayList<>();
			for (RegistreEntity pendent : pendentsByRegla) {
				
				AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
				anotacioRegistreId.setIndetificador(pendent.getNumero());			
				
				try {
					anotacioRegistreId.setClauAcces(RegistreHelper.encrypt(pendent.getNumero() + "_" + pendent.getId(),
							clauSecreta));
				} catch (Exception ex) {
					String errMsg = "Error encriptant la clau d'accés \"" + pendent.getNumero() + "\" per comunicar anotacions al backoffice " + backofficeDesti;
					logger.error(errMsg, ex);
					return new RuntimeException(
							errMsg,
							ex);
				}
				ids.add(anotacioRegistreId);
				accioParams.put(anotacioRegistreId.getIndetificador(), anotacioRegistreId.getClauAcces());
			}
			
			exception = comunicarAnotacionsAlBackoffice(backofficeDesti, ids, accioParams);
		} catch(Exception e) {
			String errMsg = "Error no controlat comunicant " + pendentsIdsGroupedByRegla.size() + " annotacions al backoffice";
			if (regla != null) {
				errMsg += " " + regla.getBackofficeDesti().getNom() + " per la regla " + regla.getId() + " " + regla.getNom();
			}
			logger.error(errMsg);
			exception = e;
		}
		return exception;
	}
	
	/** Mètode per provar la connexió amb un backoffice comunicant una llista buida d'identificadors d'anotacions de registre.
	 * 
	 * @param backoffice
	 * @return
	 */
	@Transactional(readOnly = true)
	public Exception provarConnexioBackoffice(BackofficeEntity backoffice) {
		List<AnotacioRegistreId> ids = new ArrayList<>();
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("Backoffice", backoffice.getCodi());
		
		return comunicarAnotacionsAlBackoffice(backoffice, ids, accioParams);
	}

	private Exception comunicarAnotacionsAlBackoffice(
			BackofficeEntity backofficeDesti, 
			List<AnotacioRegistreId> ids, 
			Map<String, String> accioParams) {
		long t0 = System.currentTimeMillis();
		String accioDescripcio = "Comunicar ";
		if (ids.size() == 1) {
			accioDescripcio += " l'anotació " + ids.get(0).getIndetificador();
		} else {
			accioDescripcio +=  ids.size() + " anotacions";
		}
		accioDescripcio += " al backoffice " + backofficeDesti.getCodi();
		String usuari = null;
		try {
			usuari = backofficeDesti.getUsuari();
			String contrasenya = backofficeDesti.getContrasenya();
			String url = backofficeDesti.getUrl();
			if (usuari != null && !usuari.isEmpty() && usuari.startsWith("${") && usuari.endsWith("}")) {
				usuari = configHelper.getConfig(backofficeDesti.getUsuari().replaceAll("\\$\\{", "").replaceAll("\\}", ""));
			}
			if (contrasenya != null && !contrasenya.isEmpty() && contrasenya.startsWith("${") && contrasenya.endsWith("}")) {
				contrasenya = configHelper.getConfig(backofficeDesti.getContrasenya().replaceAll("\\$\\{", "").replaceAll("\\}", ""));
			}
			if (url != null && !url.isEmpty() && url.startsWith("${") && url.endsWith("}")) {
				url = configHelper.getConfig(backofficeDesti.getUrl().replaceAll("\\$\\{", "").replaceAll("\\}", ""));
			}
			logger.trace(">>> Abans de generar backoffice WS " + backofficeDesti.getCodi());
			if (backofficeDesti.getTipus().equals(BackofficeTipusEnumDto.REST)) {
				String urlAmbMetode = url + "/comunicarAnotacionsPendents";
				Client jerseyClient = generarClient();
				if (usuari != null && !usuari.trim().isEmpty()) {
					autenticarClient(
							jerseyClient, 
							url,
							urlAmbMetode, 
							usuari, 
							contrasenya);
				}
				ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
				String json = ow.writeValueAsString(ids);
				jerseyClient
						.resource(urlAmbMetode)
						.type("application/json")
						.post(json);
			} else {
				BackofficeWsService backofficeClient = new WsClientHelper<BackofficeWsService>().generarClientWs(
						getClass().getResource(
								"/es/caib/distribucio/core/service/ws/backoffice/backoffice.wsdl"),
						url,
						new QName(
								"http://www.caib.es/distribucio/ws/backoffice",
								"BackofficeService"),
						usuari,
						contrasenya,
						null,
						BackofficeWsService.class);
				logger.trace(">>> Abans de cridar backoffice WS "+ backofficeDesti.getCodi());
				backofficeClient.comunicarAnotacionsPendents(ids);
				
				// RegistreNumero sí si només n'hi ha 1				
				if (ids.size()==1) {					
					String identificador = ids.get(0).getIndetificador();
					integracioHelper.addAccioOk(
							IntegracioHelper.INTCODI_BACKOFFICE,
							identificador,
							accioDescripcio,
							usuari,
							accioParams,
							IntegracioAccioTipusEnumDto.ENVIAMENT,
							System.currentTimeMillis() - t0);
				} else {
					integracioHelper.addAccioOk(
							IntegracioHelper.INTCODI_BACKOFFICE,
							accioDescripcio,
							usuari,
							accioParams,
							IntegracioAccioTipusEnumDto.ENVIAMENT,
							System.currentTimeMillis() - t0);
				}				
			}
			logger.trace(">>> Despres de cridar backoffice WS "+ backofficeDesti.getCodi());
			return null;
		} catch (Exception ex) {
			String errorDescripcio = "";
			if (ids.size() > 0) {
				errorDescripcio = "Error " + ex.getClass().getSimpleName() + " enviant " + ids.size() + "anotacions al backoffice " + backofficeDesti.getNom() + ":" + ex.getMessage();
			} else {
				errorDescripcio = "No s'ha pogut fer la connexió amb el backoffice: " + ex.getMessage();
			}
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_BACKOFFICE,
					accioDescripcio,
					usuari,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			return new SistemaExternException(
					IntegracioHelper.INTCODI_BACKOFFICE,
					errorDescripcio,
					ex);
		}
	}

	private Client generarClient() {
		
		Client jerseyClient = Client.create();
		jerseyClient.addFilter(
				new ClientFilter() {
					private ArrayList<Object> cookies;
					@Override
					public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
						if (cookies != null) {
							request.getHeaders().put("Cookie", cookies);
						}
						ClientResponse response = getNext().handle(request);
						if (response.getCookies() != null) {
							if (cookies == null) {
								cookies = new ArrayList<Object>();
							}
							cookies.addAll(response.getCookies());
						}
						return response;
					}
				});
		jerseyClient.addFilter(
				new ClientFilter() {
					@Override
					public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
						ClientHandler ch = getNext();
						ClientResponse resp = ch.handle(request);
						if (resp.getStatus() / 100 != 3) {
						//if (resp.getStatusInfo().getFamily() != Response.Status.Family.REDIRECTION) {
							return resp;
						} else {
							String redirectTarget = resp.getHeaders().getFirst("Location");
							request.setURI(URI.create(redirectTarget));
							return ch.handle(request);
						}
					}
				});
		return jerseyClient;
	}

	private void autenticarClient(
			Client jerseyClient,
			String baseUrl,
			String urlAmbMetode,
			String username,
			String password) throws InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException {
		if (!autenticacioBasic) {
			logger.trace(
					"Autenticant client REST per a fer peticions cap a servei desplegat a damunt jBoss (" +
					"urlAmbMetode=" + urlAmbMetode + ", " +
					"username=" + username +
					"password=********)");
			jerseyClient.resource(urlAmbMetode).get(String.class);
			Form form = new Form();
			form.putSingle("j_username", username);
			form.putSingle("j_password", password);
			jerseyClient.
			resource(baseUrl + "/j_security_check").
			type("application/x-www-form-urlencoded").
			post(form);
		} else {
			logger.trace(
					"Autenticant REST amb autenticació de tipus HTTP basic (" +
					"urlAmbMetode=" + urlAmbMetode + ", " +
					"username=" + username +
					"password=********)");
			jerseyClient.addFilter(
					new HTTPBasicAuthFilter(username, password));
		}
	}

	public int getGuardarAnnexosMaxReintentsProperty() {
		String maxReintents = configHelper.getConfig("es.caib.distribucio.tasca.guardar.annexos.max.reintents");
		if (maxReintents != null) {
			return Integer.parseInt(maxReintents);
		} else {
			return 0;
		}
	}

	public int getBackofficeMaxReintentsProperty() {
		String maxReintents = configHelper.getConfig("es.caib.distribucio.backoffice.reintentar.processament.max.reintents");
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

	/** Envia les anotacions pendents agrupades per regla/backoffice i actualita l'estat o reintent
	 * de l'anotació. 
	 * @param pendentsIdsGroupedByRegla
	 * @return
	 */
	public Throwable enviarIdsAnotacionsBackUpdateDelayTime(List<Long> pendentsIdsGroupedByRegla) {
		Date dataComunicacio = new Date();
		Throwable throwable; 
		try {
			throwable = self.enviarIdsAnotacionsBackoffice(pendentsIdsGroupedByRegla);
		} catch(Throwable th) {
			logger.error("Error no controlat enviant ids d'anotacions pendents: " + th.getMessage());
			throwable = th;
		}
		int minutesEspera = 1;
		String tempsEspera = configHelper.getConfig(
				"es.caib.distribucio.tasca.enviar.anotacions.backoffice.temps.espera.execucio");
		// we convert to minutes to not have to deal with too big numbers out of bounds
		minutesEspera = ((Integer.parseInt(tempsEspera) / 1000) / 60);
		if (minutesEspera < 1) {
			minutesEspera = 1;
		}			
		for (Long pendentId : pendentsIdsGroupedByRegla) {
			self.updateBackEnviarDelayData(pendentId, throwable, dataComunicacio, minutesEspera);
		}
		return throwable;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateBackEnviarDelayData(
			Long pendentId, 
			Throwable throwable, 
			Date dataComunicacio,
			int minutesEspera) {

		RegistreEntity pend = registreRepository.findOneAmbBloqueig(pendentId);
		if (throwable == null) {
			// remove exception message and increment procesIntents
			pend.updateProces(null, null);
			// Si no s'ha actualitzat després de la hora de la comunicació actualitza l'estat com a comunicada al backoffice.
			if (pend.getLastModifiedDate().orElseThrow().isBefore(LocalDateTime.ofInstant(dataComunicacio.toInstant(), ZoneId.systemDefault()))) {
				pend.updateBackEstat(RegistreProcesEstatEnum.BACK_COMUNICADA, "Comunicada " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dataComunicacio));
			}
		} else { 
			// if excepion occured during sending anotacions ids to backoffice
			// add exception message and increment procesIntents
			pend.updateProces(null,
					throwable);				
		}
		// set delay for another send retry
		int procesIntents = pend.getProcesIntents();
		// with every proces intent delay between resends will be longer
		int delayMinutes = minutesEspera * procesIntents;
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MINUTE,
				delayMinutes);
		pend.updateBackRetryEnviarData(cal.getTime());
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
			Boolean isRegistreArxiuPendent,
			RegistreAnnex registreAnnex,
			RegistreEntity registre) {
		String gestioDocumentalId = null;
		if (registreAnnex.getFitxerContingut() != null && isRegistreArxiuPendent) {
			gestioDocumentalId = gestioDocumentalHelper.gestioDocumentalCreate(
					GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP,
					registreAnnex.getFitxerContingut(),
					registre.getNumero());
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
		RegistreEntity registre = annex.getRegistre();
		if (firma.getContingut() != null) {
			gestioDocumentalId = gestioDocumentalHelper.gestioDocumentalCreate(
					GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP,
					firma.getContingut(),
					registre.getNumero());
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

	/** Compta els registres pedents de guardar els annexos a l'Arxiu. 
	 * @param maxResultats */
	@Transactional
	public int countGuardarAnnexPendents(EntitatEntity entitat, int maxReintents) {
		return Long.valueOf(registreRepository.countGuardarAnnexPendents(entitat, maxReintents)).intValue();
	}

	/** Consulta els registres pedents de guardar els annexos a l'Arxiu. 
	 * @param maxResultats */
	@Transactional
	public List<RegistreEntity> findGuardarAnnexPendents(EntitatEntity entitat, int maxReintents, int maxResultats) {
		PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
		paginacioParams.setPaginaNum(0);
		paginacioParams.setPaginaTamany(maxResultats);
		paginacioParams.afegirOrdre("data", OrdreDireccioDto.ASCENDENT);
		Pageable pageable = paginacioHelper.toSpringDataPageable(paginacioParams, null);
		Page<RegistreEntity> pagina = registreRepository.findGuardarAnnexPendentsPaged(
				entitat,
				maxReintents,
				pageable);
		return pagina.getContent();
	}

	/** Consulta els registres pendents d'enviar al backoffice ordenats per regla. */
	@Transactional
	public List<RegistreEntity> findAmbEstatPendentEnviarBackoffice(EntitatEntity entitat, Date date, int maxReintents) {
		return registreRepository.findAmbEstatPendentEnviarBackoffice(entitat, date, maxReintents);
	}

	/** Consulta les anotacions pendents d'aplicar regles amb un màxim de reintents. */
	@Transactional
	public List<RegistreEntity> findAmbReglaPendentAplicar(EntitatEntity entitat, int maxReintents) {
		return registreRepository.findAmbReglaPendentAplicar(entitat, maxReintents);
	}

	/** Consulta les anotacions pendents de tancar a l'arxiu. */
	@Transactional
	public List<RegistreEntity> findPendentsTancarArxiuByEntitat(Date date, EntitatEntity entitat) {
		return registreRepository.findPendentsTancarArxiuByEntitat(date, entitat);
	}

	@Transactional
	public FitxerDto getAnnexFitxer(Long annexId, boolean ambVersioImprimible) {
		RegistreAnnexEntity registreAnnexEntity = registreAnnexRepository.getReferenceById(annexId);
		RegistreEntity registre = registreAnnexEntity.getRegistre();
		String titol = registreAnnexEntity.getFitxerNom().replace(".pdf", "_imprimible.pdf");
		FitxerDto fitxerDto = new FitxerDto();
		// if annex is already created in arxiu take content from arxiu
		if (registreAnnexEntity.getFitxerArxiuUuid() != null && !registreAnnexEntity.getFitxerArxiuUuid().isEmpty()) {
			if (ambVersioImprimible && this.potGenerarVersioImprimible(registreAnnexEntity)) {
				try {
					TemporalThreadStorage.set("numeroRegistre", registre.getNumero());
					fitxerDto = pluginHelper.arxiuDocumentImprimible(registreAnnexEntity.getFitxerArxiuUuid(), titol);
				} catch (Exception ex) {
					Document document = pluginHelper.arxiuDocumentConsultar(registreAnnexEntity.getFitxerArxiuUuid(), null, true, false, registre.getNumero());
					if (document != null) {
						DocumentContingut documentContingut = document.getContingut();
						if (documentContingut != null) {
							fitxerDto.setNom(registreAnnexEntity.getFitxerNom());
							fitxerDto.setContentType(documentContingut.getTipusMime());
							fitxerDto.setContingut(documentContingut.getContingut());
							fitxerDto.setTamany(documentContingut.getContingut().length);
						}
						switch(document.getEstat()) {
						case DEFINITIU:
							registreAnnexEntity.setArxiuEstat(AnnexEstat.DEFINITIU);
							break;
						case ESBORRANY:
							registreAnnexEntity.setArxiuEstat(AnnexEstat.ESBORRANY);
							break;
						}
					}
				}
			} else {
				Document document = pluginHelper.arxiuDocumentConsultar(registreAnnexEntity.getFitxerArxiuUuid(), null, true, false,registre.getNumero());
				if (document != null) {
					DocumentContingut documentContingut = document.getContingut();
					if (documentContingut != null) {
						fitxerDto.setNom(registreAnnexEntity.getFitxerNom());
						fitxerDto.setContentType(documentContingut.getTipusMime());
						fitxerDto.setContingut(documentContingut.getContingut());
						fitxerDto.setTamany(documentContingut.getContingut().length);
					}
				}
			}
		// if annex is not yet created in arxiu take content from gestio documental
		} else {
			// if annex is signed with firma attached, contingut is located either in firma or in annex
			if (registreAnnexEntity.getFirmes() != null && !registreAnnexEntity.getFirmes().isEmpty() &&
					!registreAnnexEntity.getFirmes().get(0).getTipus().equals("TF02") && !registreAnnexEntity.getFirmes().get(0).getTipus().equals("TF04")) {
				RegistreAnnexFirmaEntity firmaEntity = registreAnnexEntity.getFirmes().get(0);
				if (firmaEntity.getGesdocFirmaId() != null) {
					byte[] firmaContingut = this.getFirmaContingut(firmaEntity.getGesdocFirmaId(),registre.getNumero());
					fitxerDto.setNom(firmaEntity.getFitxerNom());
					fitxerDto.setContentType(firmaEntity.getTipusMime());
					fitxerDto.setContingut(firmaContingut);
					fitxerDto.setTamany(firmaContingut.length);
				}
				if (registreAnnexEntity.getGesdocDocumentId() != null) {
					ByteArrayOutputStream streamAnnex = new ByteArrayOutputStream();
					gestioDocumentalHelper.gestioDocumentalGet(
							registreAnnexEntity.getGesdocDocumentId(), 
							GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP, 
							streamAnnex,
							registre.getNumero());
					byte[] annexContingut = streamAnnex.toByteArray();
					fitxerDto.setNom(registreAnnexEntity.getFitxerNom());
					fitxerDto.setContentType(registreAnnexEntity.getFitxerTipusMime());
					fitxerDto.setContingut(annexContingut);
					fitxerDto.setTamany(annexContingut.length);
				}
			// if annex not signed or is signed with firma detached contingut is in annex	
			} else {
				if (registreAnnexEntity.getGesdocDocumentId() != null) {
					ByteArrayOutputStream streamAnnex = new ByteArrayOutputStream();
					gestioDocumentalHelper.gestioDocumentalGet(
							registreAnnexEntity.getGesdocDocumentId(), 
							GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP, 
							streamAnnex,
							registre.getNumero());
					byte[] annexContingut = streamAnnex.toByteArray();
					fitxerDto.setNom(registreAnnexEntity.getFitxerNom());
					fitxerDto.setContentType(registreAnnexEntity.getFitxerTipusMime());
					fitxerDto.setContingut(annexContingut);
					fitxerDto.setTamany(annexContingut.length);
				}
			}
		}
		return fitxerDto;
	}

	/**
	 * Mètode per retornar un llistat dels identificadors de les anotacions processades al backoffice
	 * amb errors i la regla aplicada.
	 **/
	@Transactional(readOnly = true)
	public List<Long> findRegistresBackError() {
		
		// Primer consulta la propietat del màxim de reintents.
		String maxReintentsString = configHelper.getConfig("es.caib.distribucio.backoffice.reintentar.processament.max.reintents");
		int maxReintents = 2;
		if (maxReintentsString != null) {
			try {
				maxReintents = Integer.parseInt(maxReintentsString);
			} catch (Exception e) {
				logger.error("Error llegint la propietat es.caib.distribucio.backoffice.reintentar.processament.max.reintents amb valor \"" + maxReintentsString 
						+ "\" com a enter per establir el màxim de reintents per reprocessar anotacions, es deixen per defecte "  + maxReintents + " intents." );
			}
		}

		// Consulta els registres amb error de processament amb un màxim de reintents
		List<Long> registresBackError = new ArrayList<>();
		List<RegistreEntity> llistatRegistresBackError = registreRepository.findRegistresBackError(new Date(), maxReintents);
		for(RegistreEntity registre : llistatRegistresBackError) {
			registresBackError.add(registre.getId());
		}
		
		return registresBackError;
	}

	/** Consulta el contingut de la firma en la gestió documental. */
	private byte[] getFirmaContingut(String gesdocFirmaId, String registreNumero) {
		byte[] firmaContingut = null;
		if (gesdocFirmaId != null) {
			ByteArrayOutputStream streamAnnexFirma = new ByteArrayOutputStream();
			gestioDocumentalHelper.gestioDocumentalGet(
					gesdocFirmaId, 
					GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_FIR_TMP, 
					streamAnnexFirma,
					registreNumero);
			firmaContingut = streamAnnexFirma.toByteArray();
		}
		return firmaContingut;
	}

	/** Llistat d'extensions convertibles. */
	private static String[] extensionsConvertiblesPdf = {
			"pdf", "odt", "sxw", "rtf", "doc", "wpd", "txt", "ods",
			"sxc", "xls", "csv", "tsv", "odp", "sxi", "ppt"};

	/** Determina si generar o no la versió imprimible del document. Es demanarà la versió imprimible si està firmat i el format permet 
	 * la conversió a PDF i si el tipus de firma és TF04, TF05 o TF06.
	 * 
	 * @param annex
	 * @return
	 */
	private boolean potGenerarVersioImprimible(RegistreAnnexEntity annex) {
		// Si no està firmat no cal la versió imprimible
		if (AnnexEstat.ESBORRANY.equals(annex.getArxiuEstat()) && (annex.getFirmes() == null || annex.getFirmes().isEmpty())) {
			return false;
		}
		// Revisa que sigui convertible
		boolean convertible = false;
		String extensio = FilenameUtils.getExtension(annex.getFitxerNom());
		if (extensio != null) {
			for (int i = 0; i < extensionsConvertiblesPdf.length; i++) {
				if (extensio.equalsIgnoreCase(extensionsConvertiblesPdf[i])) {
					convertible = true;
					break;
				}
			}
		}
		if (!convertible) {
			return false;
		}
		// Comprova segons el tipus de firma
		boolean generarVersioImprimible = false;
		if ((annex.getFitxerNom().toLowerCase().endsWith(".pdf") 
				|| "application/pdf".equals(annex.getFitxerTipusMime()))) {
			
			if (annex.getFirmes() != null 
					&& !annex.getFirmes().isEmpty()) {
				// Comprova les fimres
				for (RegistreAnnexFirmaEntity firma : annex.getFirmes()) {
					if (firma.getTipus() != null ) {
						if (	   DocumentNtiTipoFirmaEnumDto.TF06.toString().equals(firma.getTipus())
								|| DocumentNtiTipoFirmaEnumDto.TF05.toString().equals(firma.getTipus())
								|| DocumentNtiTipoFirmaEnumDto.TF04.toString().equals(firma.getTipus()))
						generarVersioImprimible = true;
						break;
					}
				}
			} else {
				// Comprova l'estat definitiu
				generarVersioImprimible = AnnexEstat.DEFINITIU.equals(annex.getArxiuEstat());
			}
		}
		return generarVersioImprimible;
	}

	// Mètodes per cridar de forma transaccional amb self
	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public DistribucioRegistreAnotacio getDistribucioRegistreAnotacio(long registreId) {
		
		RegistreEntity registreEntity = registreRepository.findOneAmbBloqueig(registreId);		
		DistribucioRegistreAnotacio distribucioRegistreAnotacio = conversioTipusHelper.convertir(
				registreEntity, 
				DistribucioRegistreAnotacio.class);
		distribucioRegistreAnotacio.setId(registreId);
		distribucioRegistreAnotacio.setUnitatOrganitzativaCodi(registreEntity.getEntitat() != null ? registreEntity.getEntitat().getCodiDir3() : null);
		distribucioRegistreAnotacio.setProcedimentCodi(registreEntity.getProcedimentCodi());
		return distribucioRegistreAnotacio;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public List<Throwable> crearExpedientArxiu(
			DistribucioRegistreAnotacio distribucioRegistreAnotacio, 
			String unitatOrganitzativaCodi, 
			String uuidExpedient) {
		RegistreEntity registreEntity = registreRepository.findOneAmbBloqueig(distribucioRegistreAnotacio.getId());
		if (registreEntity.getArxiuUuid() != null) {
			// Ja està guardat
			logger.warn("L'anotació ja té uuid d'arxiu i per tant no es tornarà a crear un contenidor (" +
					"anotacioId=" + registreEntity.getId() + ", " +
					"anotacioNumero=" + registreEntity.getNumero() + ", " +
					"unitatOrganitzativaCodi=" + unitatOrganitzativaCodi + ") amb uuid " + uuidExpedient);
			return null;
		}
		try {
			// ============= SAVE REGISTRE AS EXPEDIENT IN ARXIU ============
			uuidExpedient = pluginHelper.saveRegistreAsExpedientInArxiu(
					registreEntity.getNumero(),
					distribucioRegistreAnotacio.getNumero(),
					unitatOrganitzativaCodi);
			registreEntity.updateExpedientArxiuUuid(uuidExpedient);
			distribucioRegistreAnotacio.setArxiuUuid(uuidExpedient);
			
			logger.trace("Creat el contenidor a l'Arxiu per l'anotació (" +
					"anotacioId=" + registreEntity.getId() + ", " +
					"anotacioNumero=" + registreEntity.getNumero() + ", " +
					"unitatOrganitzativaCodi=" + unitatOrganitzativaCodi + ") amb uuid " + uuidExpedient);
			loadJustificantToDB(registreEntity.getId());
			registreRepository.saveAndFlush(registreEntity);
		} catch (Exception ex) {
			return Arrays.asList(ex);
		}
		return null;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void crearAnnexInArxiu(
			Long annexId, 
			DistribucioRegistreAnnex distribucioAnnex, 
			String unitatOrganitzativaCodi, 
			String uuidExpedient, 
			String procedimentCodi, 
			boolean titolRepetit) {
		RegistreAnnexEntity annex = registreAnnexRepository.getReferenceById(annexId);
		String fitxerTitol = "";
		String titolData = ""; 
		if (titolRepetit) {
			do {
				fitxerTitol = String.valueOf(new Date().getTime()) + annex.getTitol();
			}while(fitxerTitol.equals(titolData));
			annex.updateTitol(fitxerTitol);
		}
		if (annex.getTitol().equals("") || 
				annex.getTitol().startsWith(".")) {
			do {
				fitxerTitol = String.valueOf(new Date().getTime()) + annex.getTitol();
			}while(fitxerTitol.equals(titolData));
			annex.updateTitol(fitxerTitol);
		}
		RegistreEntity registre = annex.getRegistre();
		DocumentEniRegistrableDto documentEniRegistrableDto = new DocumentEniRegistrableDto();
		documentEniRegistrableDto.setNumero(registre.getNumero());
		documentEniRegistrableDto.setData(registre.getData());
		documentEniRegistrableDto.setOficinaDescripcio(registre.getOficinaDescripcio());
		documentEniRegistrableDto.setOficinaCodi(registre.getOficinaCodi());
		// Només crea l'annex a dins el contenidor si encara no s'ha creat o està com esborrany per tornar a provar de guardar com a definitiu
		if (annex.getFitxerArxiuUuid() == null || 
				!AnnexEstat.DEFINITIU.equals(annex.getArxiuEstat()) ) {
			
			// Valida si l'annex té o no firmes invàlides, si no pot validar-ho falla
			List<DistribucioRegistreFirma> firmes = new ArrayList<>();
			ValidacioFirmaEnum validacioFirma = this.validaFirmes(annex, firmes);
			if (validacioFirma == ValidacioFirmaEnum.ERROR_VALIDANT) {
				logger.warn("No s'han pogut validar les firmes per l'annex \"" +  annex.getTitol() + "\" (" + annex.getFitxerNom() + ") de l'anotació " + registre.getIdentificador() );
			} else {
				// Si no té firmes i es reconeixen firmes vàlides llavors les afegeix per guardar l'annexo com a definitiu
				if (distribucioAnnex.getFirmes().isEmpty() 
						&& ValidacioFirmaEnum.FIRMA_VALIDA == validacioFirma) {
					distribucioAnnex.getFirmes().addAll(firmes);
				}
			}
			distribucioAnnex.setPocesIntents(registre.getProcesIntents());
			// Es considera que la firma és vàlida si no té firmes o la firma és vàlida o no s'ha validat perquè el plugin no està configurat.
			distribucioAnnex.setValidacioFirmaEstat(validacioFirma);
			// ================= SAVE ANNEX AS DOCUMENT IN ARXIU ============== sign it if unsigned an save it with firma in arxiu
			String uuidDocument = pluginHelper.saveAnnexAsDocumentInArxiu(
					registre.getNumero(),
					distribucioAnnex,
					unitatOrganitzativaCodi,
					uuidExpedient,
					documentEniRegistrableDto, 
					procedimentCodi);
			annex.updateFitxerArxiuUuid(uuidDocument);
			if (annex.getArxiuEstat() == AnnexEstat.ESBORRANY) {
				// Marca l'annex per a que es revalidin les firmes i l'estat
				annex.updateSignaturaDetallsDescarregat(false);
			}
			if (distribucioAnnex.getFirmes() != null) {
				for (DistribucioRegistreFirma distribucioFirma: distribucioAnnex.getFirmes()) {
					// if firma was created with autofirma save info about firma(without content bytes) in db
					if (distribucioFirma.isAutofirma()) {
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
				this.loadSignaturaDetallsToDB(annex);
			}
		}
		if(annex.getArxiuEstat() == AnnexEstat.ESBORRANY) {
				registre.setAnnexosEstatEsborrany(registre.getAnnexosEstatEsborrany() + 1);
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateAnotacioEstat(long anotacioId, List<Throwable> exceptionsGuardantAnnexos) {
		RegistreEntity anotacio = registreRepository.getReferenceById(anotacioId);
		if (exceptionsGuardantAnnexos == null) {
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
		} else {
			anotacio.updateProcesMultipleExcepcions(
				null, 
				exceptionsGuardantAnnexos);
		}
	}

	public RegistreAnnexEntity loadJustificantToDB(Long registreId) {
		RegistreAnnexEntity annex = new RegistreAnnexEntity();
		try {
			RegistreEntity registre = registreRepository.getReferenceById(registreId);
			Document document = pluginHelper.arxiuDocumentConsultar(registre.getJustificantArxiuUuid(), null, true,registre.getNumero());
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

	@Transactional(readOnly = true)
	public RegistreDto findOne(
			Long entitatId,
			Long registreId,
			boolean isVistaMoviments,
			String rolActual) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		RegistreEntity registre = registreRepository.findById(registreId).orElse(null);
		if (registre == null)
			throw new NotFoundException(registreId, RegistreEntity.class);
		if (!usuariHelper.isAdmin() && !usuariHelper.isAdminLectura() && !isVistaMoviments)
			entityComprovarHelper.comprovarBustia(
							entitat,
							registre.getPareId(),
							true);
		RegistreDto registreAnotacio = (RegistreDto)contingutHelper.toContingutDto(
				registre,
				false,
				false,
				false,
				false,
				true,
				false,
				true);
		contingutHelper.tractarInteressats(registreAnotacio.getInteressats());	
		// Traiem el justificant de la llista d'annexos si té el mateix id o uuid
		for (RegistreAnnexDto annexDto : registreAnotacio.getAnnexos()) {
			if ((registre.getJustificant() != null && registreAnotacio.getJustificant().getId().equals(annexDto.getId()))
					|| registre.getJustificantArxiuUuid() != null && registre.getJustificantArxiuUuid().equals(annexDto.getFitxerArxiuUuid()) ) {
				registreAnotacio.getAnnexos().remove(annexDto);
				break;
			}
		}
		if ("tothom".equalsIgnoreCase(rolActual)) {
			List<RegistreAnnexDto> registreAnnexos = new ArrayList<RegistreAnnexDto>();
			for (RegistreAnnexDto annexDto : registreAnotacio.getAnnexos()) {
				if (annexDto.getSicresTipusDocument() == null 
						|| !RegistreAnnexSicresTipusDocumentEnum.INTERN.getValor().equals(annexDto.getSicresTipusDocument())) {
					registreAnnexos.add(annexDto);
				}
			}
			registreAnotacio.setAnnexos(registreAnnexos);
		}
		return registreAnotacio;
	}

	public String getClauSecretaProperty() {
		String clauSecreta = configHelper.getConfig("es.caib.distribucio.backoffice.integracio.clau");
		if (clauSecreta == null)
			throw new RuntimeException("Clau secreta no specificada al fitxer de propietats");
		return clauSecreta;
	}

	public int getEnviarIdsAnotacionsMaxReintentsProperty(EntitatEntity entitat) {
		EntitatDto entitatDto = conversioTipusHelper.convertir(entitat, EntitatDto.class);
		String maxReintents = configHelper.getConfig(entitatDto, "es.caib.distribucio.tasca.enviar.anotacions.max.reintents");
		if (maxReintents != null) {
			return Integer.parseInt(maxReintents);
		} else {
			return 0;
		}
	}

	/** Consulta la firma amb l'índex assenayalat per paràmetre excloent les firmes CSV. */
	public es.caib.pluginsib.arxiu.api.Firma getFirma(RegistreAnnexEntity registreAnnexEntity, int indexFirma) {
		es.caib.pluginsib.arxiu.api.Firma firma = null;
		if (registreAnnexEntity.getFitxerArxiuUuid() == null ) {
			return null;
		}
		RegistreEntity registre = registreAnnexEntity.getRegistre();
		Document document = pluginHelper.arxiuDocumentConsultar(registreAnnexEntity.getFitxerArxiuUuid(), null, true, registre.getNumero());
		if (document != null) {
			List<es.caib.pluginsib.arxiu.api.Firma> firmes = document.getFirmes();
			if (firmes != null && firmes.size() > indexFirma) {
				Iterator<es.caib.pluginsib.arxiu.api.Firma> it = firmes.iterator();
				while (it.hasNext()) {
					es.caib.pluginsib.arxiu.api.Firma f = it.next();
					if (f.getTipus() == FirmaTipus.CSV) {
						it.remove();
					}
				}
				firma = firmes.get(indexFirma);
			}
		}
		return firma;
	}

	private static final Logger logger = LoggerFactory.getLogger(RegistreHelper.class);

	/** Mètode per consultar totes les anotacions pendents d'enviar agrupades per ReglaDto, cal tenir en compte que cada entitat
	 * pot tenir el seu número màxim de reintents diferent.
	 * 
	 * @return Retorna un Map<ReglaDto, List<Long>> agrupant els identificadors dels registres pendents per regla.
	 */
	@Transactional(readOnly = true)
	public Map<ReglaDto, List<Long>> getPendentsEnviarBackoffice() {
		
		Map<Long, ReglaDto> regles = new HashMap<>();
		Map<Long, List<Long>> pendentsIds = new HashMap<>();
		// Per cada entitat
		for (EntitatEntity entitat : entitatRepository.findByActiva(true)) {
			
			EntitatDto entitatDto = new EntitatDto();
			entitatDto.setCodi(entitat.getCodi());
			ConfigHelper.setEntitat(entitatDto);

			int maxReintents = this.getEnviarIdsAnotacionsMaxReintentsProperty(entitat);
		
			// getting annotacions pendents to send to backoffice with active regla and past retry time, grouped by regla
			List<RegistreEntity> pendents = this.findAmbEstatPendentEnviarBackoffice(entitat, new Date(), maxReintents);
			if (pendents != null && !pendents.isEmpty()) {
				for (RegistreEntity pendent : pendents) {
					if (pendent.getRegla() == null) {
						continue;
					}
					if (!pendentsIds.containsKey(pendent.getRegla().getId())) {
						ReglaDto regla = conversioTipusHelper.convertir(pendent.getRegla(), ReglaDto.class);
						pendentsIds.put(regla.getId(), new ArrayList<Long>());
						regles.put(regla.getId(), regla);
					}
					pendentsIds.get(pendent.getRegla().getId()).add(pendent.getId());
				}
			}
		}
		// Construeix el map resultat.
		Map<ReglaDto, List<Long>> pendentsByRegla = new HashMap<ReglaDto, List<Long>>();
		for (Long reglaId : pendentsIds.keySet()) {
			pendentsByRegla.put(regles.get(reglaId), pendentsIds.get(reglaId));
		}
		return pendentsByRegla;
	}

}
