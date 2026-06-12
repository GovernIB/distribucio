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
import java.time.Duration;
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
import java.util.stream.Collectors;

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
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.io.FilenameUtils;
import org.fundaciobit.pluginsib.utils.signature.SignatureCommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.convert.DurationStyle;
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
import com.itextpdf.text.pdf.security.PdfPKCS7;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;

import es.caib.distribucio.logic.helper.SubsistemesHelper.SubsistemesEnum;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaDetallDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.BackofficeTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.DocumentEniRegistrableDto;
import es.caib.distribucio.logic.intf.dto.DocumentNtiTipoFirmaEnumDto;
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
import es.caib.distribucio.logic.intf.helper.AnnexUtil;
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
import es.caib.distribucio.persist.entity.ContingutComentariEntity;
import es.caib.distribucio.persist.entity.ContingutEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.RegistreAnnexEntity;
import es.caib.distribucio.persist.entity.RegistreAnnexFirmaEntity;
import es.caib.distribucio.persist.entity.RegistreEntity;
import es.caib.distribucio.persist.entity.RegistreFirmaDetallEntity;
import es.caib.distribucio.persist.entity.RegistreInteressatEntity;
import es.caib.distribucio.persist.entity.ReglaEntity;
import es.caib.distribucio.persist.entity.UsuariEntity;
import es.caib.distribucio.persist.repository.BackofficeRepository;
import es.caib.distribucio.persist.repository.ContingutComentariRepository;
import es.caib.distribucio.persist.repository.ContingutLogRepository;
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

    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    
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
	private ContingutComentariRepository contingutComentariRepository;
	@Autowired
	private ContingutLogRepository contingutLogRepository;
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
    @Autowired
    private EmailHelper emailHelper;
    @Autowired
    private BackofficeRepository backofficeRepository;

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
					null,
					registreAnotacio.getTramitCodi(),
					registreAnotacio.getTramitNom());
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
			String observacions = AnnexUtil.truncar(registreAnotacio.getObservacions(), AnnexUtil.MAX_OBSERVACIONS);
			
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
			serveiCodi(registreAnotacio.getServeiCodi()).
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
			observacions(observacions).
			exposa(registreAnotacio.getExposa()).
			solicita(registreAnotacio.getSolicita()).
			regla(regla).
			oficinaOrigen(
					registreAnotacio.getDataOrigen(),
					registreAnotacio.getOficinaOrigenCodi(),
					registreAnotacio.getOficinaOrigenDescripcio()).
			justificantArxiuUuid(justificantArxiuUuid).
			presencial(registreAnotacio.isPresencial()).
			tramitCodi(registreAnotacio.getTramitCodi()).
			tramitNom(registreAnotacio.getTramitNom()).
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
			Map<String, Integer> titolsComprovats = new HashMap<String, Integer>();
			for (RegistreAnnex registreAnnex: registreAnotacio.getAnnexos()) {
				// Si ve informat amb uuid no guardar en filesystem
				boolean isGuardarEnFilesystem = registreAnnex.getFitxerArxiuUuid() == null;
				registre.getAnnexos().add(
						crearAnnexEntity(
								isGuardarEnFilesystem,
								registreAnnex,
								registre,
								titolsComprovats));
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

	/** Encriptacio usat en la comunicació d'anotacions a backoffices. */
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

	/** Després de la versió 1.0.5 s'ha canviat l'encriptació per evitar caràcters estranys. */
	public String encriptar(String missatgeAEncriptar) throws Exception {
		SecretKeySpec secretKey = generarClau(this.getClauSecretaProperty());
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		String encriptat = Base64.getUrlEncoder().withoutPadding().encodeToString(cipher.doFinal(missatgeAEncriptar.getBytes()));
		return encriptat;
	}

	/** Desencriptació de l'identificador antic per compatibilitat amb els codis de descàrrega de documentació enviats per correu abans de la versió 1.0.5. */
	private String desencriptarAntic(String missatgeADesencriptar) throws Exception {
		missatgeADesencriptar = missatgeADesencriptar.replaceAll("%", "/");
		missatgeADesencriptar = missatgeADesencriptar.replaceAll("/2F", "/");
		missatgeADesencriptar = missatgeADesencriptar.replace("%252F", "/");
		missatgeADesencriptar = missatgeADesencriptar + "==";
		SecretKeySpec secretKey = generarClau(this.getClauSecretaProperty());
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return new String(cipher.doFinal(Base64.getDecoder().decode(missatgeADesencriptar)));
	}


	public String desencriptar(String missatgeADesencriptar) throws Exception {
		try {
			SecretKeySpec secretKey = generarClau(this.getClauSecretaProperty());
			Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return new String(cipher.doFinal(Base64.getUrlDecoder().decode(missatgeADesencriptar)));
        } catch (IllegalArgumentException e) {
            // En cas d'error es prova a desencriptar amb l'algoritme antic anterior a la versió 1.0.5
            return desencriptarAntic(missatgeADesencriptar);
        }
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
					firmaContingut = this.getFirmaContingut(annexFirma.getGesdocFirmaId(),annexFirma.getFitxerNom(),registre.getNumero());
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
				for (DistribucioRegistreAnnex annex : distribucioRegistreAnotacio.getAnnexos()) {
					try {
						self.crearAnnexInArxiu(
								annex.getId(), 
								annex, 
								unitatOrganitzativaCodi,
								uuidExpedient, 
								distribucioRegistreAnotacio.getProcedimentCodi());
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

    private boolean conteFirmesPdf(byte[] contingut) {
        PdfReader reader;
        try {
            reader = new PdfReader(contingut);
            AcroFields acroFields = reader.getAcroFields();
            List<String> names = acroFields.getSignatureNames();
            if (names == null || names.isEmpty()) {
                return false;
            }
            for (int i = names.size() - 1; i >= 0; i--) {
                String name = names.get(i);
                PdfPKCS7 pk = acroFields.verifySignature(name);
                if (!pk.isTsp()) {
                    return true;
                }
            }
        } catch (Exception e) {
//            logger.debug("Error validant si l'annex PDF \"" + annex.getTitol() + "\" de l'anotació " + annex.getRegistre().getIdentificador() + " conté informació de firmes amb PdfReader.");
        }
        return false;
    }
    private boolean conteFirmesXml(byte[] contingut) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new ByteArrayInputStream(contingut));
            NodeList signatures = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
            return !(signatures == null || signatures.getLength() <= 0);
        } catch (Exception e) {
//            logger.warn("Error validant si l'annex XML \"" + annex.getTitol() + "\" de l'anotació " + annex.getRegistre().getIdentificador() + " conté informació de firmes.");
        }
        return false;
    }
    private boolean conteFirmesDocx(byte[] contingut) {
        try {
//            ByteArrayInputStream is = new ByteArrayInputStream(contingut);
//            OPCPackage pkg = OPCPackage.open(is);
//            List<PackagePart> signatures = pkg.getPartsByContentType(
//                    "application/vnd.openxmlformats-package.digital-signature-xmlsignature+xml"
//            );
//            return !(signatures.isEmpty());
        }catch (Exception e) {
//            logger.warn("Error validant si l'annex DOCX \"" + annex.getTitol() + "\" de l'anotació " + annex.getRegistre().getIdentificador() + " conté informació de firmes.");
        }
        return false;
    }

    private boolean conteFirmesXAdES(byte[] contingut, boolean inputXML) {
        try {
            SignatureCommonUtils.getXAdESMode(contingut, inputXML);
            return true;
        } catch (Exception e) {
            if (!e.getMessage().contains("No s'ha trobat cap node de firma dins de l'XML")) {
                logger.error(e.getMessage());
            }
            return false;
        }
    }
    private boolean conteFirmesCAdES(byte[] contingut) {
        try {
            SignatureCommonUtils.getCAdESMode(contingut);
            return true;
        } catch (Exception e) {
        	// No es fa res amb l'error, només es considera que no té firma
            return false;
        }
    }
    private boolean conteFirmes(String arxiuNom, String mimetype, byte[] contingut) {
        // comprovar pdf's
        if (arxiuNom.toLowerCase().endsWith(".pdf") || mimetype.equals("application/pdf")) {
            if (conteFirmesPdf(contingut))
                return true;
        }

        // Comprovar firmes XADES per XML's i XSIG
        if (arxiuNom.toLowerCase().endsWith(".xml") || arxiuNom.toLowerCase().endsWith(".xsig") || mimetype.equals("application/xml")) {
            if (conteFirmesXAdES(contingut, true) && conteFirmesXml(contingut))
                return true;
        }

//        // Comprovar DOCX
//        if (arxiuNom.toLowerCase().endsWith(".docx") || mimetype.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
//            if (conteFirmesDocx(contingut))
//                return true;
//        }

        // Para el resto de binarios
        return (conteFirmesCAdES(contingut));
    }
    private boolean teFirmesSeparades(List<RegistreAnnexFirmaEntity> firmes) {
        return !(firmes == null || firmes.isEmpty()) || firmesAttached(firmes);
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
		// 0 - Mira si és un PDF sense firmes per evitar enviar a validar documents PDF o XML que haurien de tenir la firma inclosa
        boolean ambFirmes = true;
        if ( !teFirmesSeparades(annex.getFirmes()) ) {
            ambFirmes = this.conteFirmes(fitxer.getNom(), annex.getFitxerTipusMime(), documentContingut);
        }
        if (!ambFirmes) { validacioFirmaEstat = ValidacioFirmaEnum.SENSE_FIRMES; }

		if (ambFirmes && pluginHelper.isValidaSignaturaPluginActiu()) {
			// Si el document per separat no té firmes o té firmes vàlides llavors comprova les firmes
			boolean annexFirmat = annex.getFirmes() != null && !annex.getFirmes().isEmpty();
			if (annexFirmat) {
				// 1- Valida les firmes de l'annex
				int nFirma = 1;
				for (RegistreAnnexFirmaEntity firma : annex.getFirmes()) {
					
					// Si encara no està a l'Arxiu o està en estat esborrany recupera el fitxer de firma de la gesió documental.
					if (annex.getFitxerArxiuUuid() == null || AnnexEstat.ESBORRANY.equals(annex.getArxiuEstat())) {
						if (documentContingut != null && "TF04".equals(firma.getTipus())) { // <> TF04 CAdDES dettached (unica firma realment dettached)
							firmaContingut = this.getFirmaContingut(firma.getGesdocFirmaId(),firma.getFitxerNom(),registre.getNumero());
						}
						
						if (documentContingut == null) {
							firmaContingut = this.getFirmaContingut(firma.getGesdocFirmaId(),firma.getFitxerNom(),registre.getNumero());
						}
					} else {
						// Altrament obté la 1a firma de l'Arxiu
						es.caib.pluginsib.arxiu.api.Firma firmaArxiu = this.getFirma(annex, 0);
						if (firmaArxiu != null && firmaArxiu.getTipus().equals(FirmaTipus.CADES_DET)) { // És la unica firma realment detached (2 fitxers separats)
							firmaContingut = firmaArxiu.getContingut();
						}
					}
					try {
						logger.debug("Validant la firma " + nFirma++ + "/" + annex.getFirmes().size() + " " + firma.getTipus()
									 + " de l'annex \"" + annex.getTitol() + "\" de l'anotació " + annex.getRegistre().getIdentificador());
						
						ValidaSignaturaResposta validacioFirma = pluginHelper.validaSignaturaObtenirDetalls(fitxer.getNom(), annex.getFitxerTipusMime(), documentContingut, firmaContingut, registre.getNumero());
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
					ValidaSignaturaResposta validacioFirma = pluginHelper.validaSignaturaObtenirDetalls(fitxer.getNom(), annex.getFitxerTipusMime(), documentContingut, firmaContingut, registre.getNumero());
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

	/** Comprova si totes les firmes de l'annex són de tipus attached i per tant el propi contingut del document és el que hauria d'incloure la informació de firma. */
	private boolean firmesAttached(List<RegistreAnnexFirmaEntity> firmes) {
		boolean firmesAttached = false;
		if (firmes != null && !firmes.isEmpty()) {
			firmesAttached = true;
			for (RegistreAnnexFirmaEntity firma : firmes) {
				firmesAttached = firmesAttached &&
						("TF02".equals(firma.getTipus()) 				//TF02 - XAdES internally detached signature
								|| "TF03".equals(firma.getTipus())  	//TF03 - XAdES enveloped signature
								|| "TF06".equals(firma.getTipus())); 	//TF06 - PAdES
			}
		}
		return firmesAttached;
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
                                registreAnnexEntity.getFitxerNom(),
								GestioDocumentalHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP,
								streamAnnex,
								registre.getNumero());
						documentContingut = streamAnnex.toByteArray();
					}
					byte[] firmaContingut = null;
					if (registreAnnexFirmaEntity.getGesdocFirmaId() != null && !registreAnnexFirmaEntity.getGesdocFirmaId().isEmpty()) {
						firmaContingut = this.getFirmaContingut(registreAnnexFirmaEntity.getGesdocFirmaId(),registreAnnexFirmaEntity.getFitxerNom(),registre.getNumero());
					}
					ValidaSignaturaResposta validacioFirma = pluginHelper.validaSignaturaObtenirDetalls(
							registreAnnexEntity.getFitxerNom(),
							registreAnnexEntity.getFitxerTipusMime(),
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
	

	
	
	
	public void loadSignaturaDetallsToDB(RegistreAnnexEntity annexEntity) {
		
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
			if (metadades != null && annexEntity.getFirmaCsv() == null) {
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
							byte[] firmaContingut = null;
							if ("TF04".equals(firma.getTipus())) { // TF04 CAdDES dettached ((unica firma en dos fitxers separats)
								firmaContingut = arxiuFirma.getContingut();
							}
							final Timer timevalidaSignaturaObtenirDetalls = metricRegistry.timer(MetricRegistry.name(RegistreServiceImpl.class, "getAnnexosAmbArxiu.validaSignaturaObtenirDetalls"));
							Timer.Context contevalidaSignaturaObtenirDetalls = timevalidaSignaturaObtenirDetalls.time();
							ValidaSignaturaResposta validacioFirma = pluginHelper.validaSignaturaObtenirDetalls(
									annexEntity.getFitxerNom(),
									annexEntity.getFitxerTipusMime(),									
									documentContingut,
									firmaContingut,
									registre.getNumero()) ;
							List<ArxiuFirmaDetallDto> firmaDetalls = validacioFirma.getFirmaDetalls();
							contevalidaSignaturaObtenirDetalls.stop();
							registreFirmaDetallRepository.deleteAll(firma.getDetalls());
							firma.getDetalls().clear();
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
		    	// Per compatibilitat amb backoffices anteriors a la versió 1.0.7 es fixa també indetificador
				anotacioRegistreId.setIdentificador(pendent.getNumero());
				
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
				accioParams.put(anotacioRegistreId.getIdentificador(), anotacioRegistreId.getClauAcces());
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
			accioDescripcio += "l'anotació";
		} else {
			accioDescripcio += ids.size() + " anotacions";
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
					String identificador = ids.get(0).getIdentificador();
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
            BackofficeSalutHelper.addSuccessOperation(backofficeDesti, System.currentTimeMillis() - t0);
			return null;
        } catch (Exception ex) {
            BackofficeSalutHelper.addErrorOperation(backofficeDesti);
			String errorDescripcio = "";
			if (!ids.isEmpty()) {
				errorDescripcio = "Error " + ex.getClass().getSimpleName() + " enviant " + ids.size() + "anotacions al backoffice " + backofficeDesti.getNom();
            } else {
                if (ExceptionHelper.isExceptionOrCauseInstanceOf(ex, SOAPFaultException.class)) {
                    errorDescripcio = "S'ha pogut establir connexió però s'ha produït un error intern al backoffice " + backofficeDesti.getCodi();
                } else {
                    errorDescripcio = "No s'ha pogut probar la connexió amb el backoffice " + backofficeDesti.getCodi();
                }
			}

            if (ids.size()==1) {
                String identificador = ids.get(0).getIdentificador();
                integracioHelper.addAccioError(
                        IntegracioHelper.INTCODI_BACKOFFICE,
                        identificador,
                        accioDescripcio,
                        usuari,
                        accioParams,
                        IntegracioAccioTipusEnumDto.ENVIAMENT,
                        System.currentTimeMillis() - t0,
                        errorDescripcio,
                        ex);
            } else {
                integracioHelper.addAccioError(
                        IntegracioHelper.INTCODI_BACKOFFICE,
                        accioDescripcio,
                        usuari,
                        accioParams,
                        IntegracioAccioTipusEnumDto.ENVIAMENT,
                        System.currentTimeMillis() - t0,
                        errorDescripcio,
                        ex);
            }
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
		long start = System.currentTimeMillis();
		Date dataComunicacio = new Date();
		Throwable throwable; 
		try {
			throwable = self.enviarIdsAnotacionsBackoffice(pendentsIdsGroupedByRegla);
			if (throwable == null)
				SubsistemesHelper.addSuccessOperation(SubsistemesEnum.RGB, System.currentTimeMillis() - start);
			else
				SubsistemesHelper.addErrorOperation(SubsistemesEnum.RGB);
		} catch(Throwable th) {
			logger.error("Error no controlat enviant ids d'anotacions pendents: " + th.getMessage());
			SubsistemesHelper.addErrorOperation(SubsistemesEnum.RGB);
			throwable = th;
		}
		for (Long pendentId : pendentsIdsGroupedByRegla) {
			self.updateBackEnviarDelayData(pendentId, throwable, dataComunicacio);
		}
		return throwable;
	}

	/** Actualitza les dades dels intents de comunicació i error i també comprova si s'ha de comunicar per email al responsable del 
	 * backoffice en cas d'error.
	 * 
	 * @param pendentId
	 * @param throwable
	 * @param dataComunicacio
	 * @param tempsEspera
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateBackEnviarDelayData(
			Long pendentId, 
			Throwable throwable, 
			Date dataComunicacio) {
        
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
			
			// Comproba si s'ha de comunicar l'error al responsable del backoffice
			BackofficeEntity backofficeDesti = backofficeRepository.findByEntitatAndCodi(pend.getEntitat(), pend.getBackCodi());
			if (backofficeDesti != null 
					&& backofficeDesti.getEnviamentEmail() != null 
					&& backofficeDesti.getEnviamentEmail()
					&& backofficeDesti.getEmailResponsable() != null) 
			{
				// Consulta si s'ha superat el màxim de reintents per alguna de les anotacions a fi de comunicar per email l'error al responsable.
				int maxReintents = getEnviarIdsAnotacionsMaxReintentsProperty(backofficeDesti.getEntitat());
            	boolean reintentsEsgotat = pend.getProcesIntents() >= maxReintents;
                if (reintentsEsgotat) {
                    if (backofficeDesti.getEnviamentEmail() && backofficeDesti.getEmailResponsable() != null) {
                        int minuts = Integer.parseInt(configHelper.getConfig("es.caib.distribucio.email.backoffice.responsable.temps", "1440"));
                        // Si ha passat un temps configurat entre emails torna a avisar al responsable.
                        if (backofficeDesti.getDarrerEmailResponsable() == null
                                || Duration.between(backofficeDesti.getDarrerEmailResponsable(), LocalDateTime.now()).toMinutes() >= minuts) {
                            try {
                                emailHelper.sendEmailRepresentantBackoffice(backofficeDesti);
                                backofficeDesti.setDarrerEmailResponsable(LocalDateTime.now());
                                backofficeRepository.save(backofficeDesti);
                            } catch (Exception e) {
                                logger.error("Error no controlat enviament de correu a representant de backoffice: " + e.getMessage());
                            }
                        }
                    }
                }
			}
		}
		// Calcula el temps entre reintents a partir del número de reintents, configuració i valor per defecte #826
		int procesIntents = pend.getProcesIntents();
		Integer tempsEntreIntentsMs = null;
		// Configuració amb expressions ISO-8601 per interpretar amb lava.lang.Duration
		String configTempsEspera = configHelper.getConfig(
				"es.caib.distribucio.tasca.enviar.anotacions.backoffice.temps.entre.intents");
		if (configTempsEspera != null && !configTempsEspera.trim().isEmpty()) {
	        try {
		        ArrayList<Integer> tempsConfigMs = Arrays.stream(configTempsEspera.split(","))
	                        .map((i) -> i.trim())
	                        .map((i) -> (int) DurationStyle.detectAndParse(i).toMillis())
	                        .collect(Collectors.toCollection(ArrayList::new));
		        // Per cada intent li toca una expressió però si hi ha més intents que expressions llavors s'hagafa la darrera t = tempsConfigMs[procesIntent-1]
		        if (!tempsConfigMs.isEmpty()) {
		        	tempsEntreIntentsMs = tempsConfigMs.get(Math.max(0, Math.min(procesIntents-1, tempsConfigMs.size()-1)));
		        }
	        } catch (Exception e) {
	            logger.error("Error no controlat obtenint el temps configurat entre reintents d'enviar a backoffice \"" 
	            				+ configTempsEspera + "\": " + e.getMessage());
	        }
		}
		// Si no s'especifica cap propietat llavors el temps són 10 minuts per reintent
		if (tempsEntreIntentsMs == null) {
			tempsEntreIntentsMs = procesIntents * 10 * 60000;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
        cal.add(Calendar.MILLISECOND, tempsEntreIntentsMs);
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
			RegistreEntity registre,
			Map<String, Integer> titolsComprovats) {
		String gestioDocumentalId = null;
		if (registreAnnex.getFitxerContingut() != null && isRegistreArxiuPendent) {
			gestioDocumentalId = gestioDocumentalHelper.gestioDocumentalCreate(
                    registreAnnex.getFitxerNom(),
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
		
		// Soluciona problema llargària camps registre CHAR
		String titol = AnnexUtil.prepararTitol(registreAnnex.getTitol(), AnnexUtil.MAX_FITXER_TITOL, titolsComprovats);
		String nomFitxer = AnnexUtil.truncarNomFitxer(registreAnnex.getFitxerNom(), AnnexUtil.MAX_FITXER_NOM);
		String observacions = AnnexUtil.truncar(registreAnnex.getObservacions(), AnnexUtil.MAX_OBSERVACIONS);
		
		RegistreAnnexEntity annexEntity = RegistreAnnexEntity.getBuilder(
				titol,
				nomFitxer,
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
				observacions(observacions).
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

	public RegistreAnnexFirmaEntity crearFirmaEntity(
			Firma firma,
			RegistreAnnexEntity annex) {
		String gestioDocumentalId = null;
		RegistreEntity registre = annex.getRegistre();
		if (firma.getContingut() != null) {
			gestioDocumentalId = gestioDocumentalHelper.gestioDocumentalCreate(
                    annex.getFitxerNom(),
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

    public List<RegistreEntity> findEstatErrorProcesament(EntitatEntity entitat, int maxReintents) {
        return registreRepository.findEstatErrorProcesament(entitat, maxReintents);
    }

	/** Consulta els registres pendents d'enviar al backoffice ordenats per regla. */
	@Transactional
	public List<RegistreEntity> findAmbEstatPendentEnviarBackoffice(EntitatEntity entitat, Date date, int maxReintents) {
		return registreRepository.findAmbEstatPendentEnviarBackoffice(entitat, date, maxReintents);
	}

    /** Consulta de les anotacions comunicades que han sobrepassat el limit. */
	@Transactional
	public List<RegistreEntity> findAmbLimitDiesEstatComunicadaBackoffice(int dies) {
        Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
        cal.add(Calendar.DATE, -dies);
        Date dataLimit = cal.getTime();
		return registreRepository.findAmbLimitEstatComunicadaBackoffice(dataLimit);
	}

	/** Consulta les anotacions pendents d'aplicar regles amb un màxim de reintents. */
	@Transactional(readOnly = true)
	public List<RegistreEntity> findAmbReglaPendentAplicar(EntitatEntity entitat, int maxReintents) {
		return registreRepository.findAmbReglaPendentAplicar(entitat, maxReintents);
	}

	/** Consulta les anotacions pendents de tancar a l'arxiu. */
	@Transactional(readOnly = true)
	public List<RegistreEntity> findPendentsTancarArxiuByEntitat(Date date, EntitatEntity entitat) {
		return registreRepository.findPendentsTancarArxiuByEntitat(date, entitat);
	}
	
	@Transactional
	public FitxerDto getAnnexFitxerImprimible(Long annexId) throws Exception {
		RegistreAnnexEntity registreAnnexEntity = registreAnnexRepository.getReferenceById(annexId);
		return this.getAnnexFitxerImprimible(registreAnnexEntity);
	}

	public FitxerDto getAnnexFitxerImprimible(RegistreAnnexEntity registreAnnexEntity) throws Exception {
	    RegistreEntity registre = registreAnnexEntity.getRegistre();
	    FitxerDto fitxerDto = new FitxerDto();
	    String titol = registreAnnexEntity.getFitxerNom().replace(".pdf", "_imprimible.pdf");

        String imprimibleUrl = configHelper.getConfig("es.caib.distribucio.pluginsib.arxiu.caib.conversio.imprimible.url");
        boolean endsWithUuid = imprimibleUrl.endsWith("/uuid") || imprimibleUrl.endsWith("/uuid/");
        if (registreAnnexEntity.getFitxerArxiuUuid() != null && !registreAnnexEntity.getFitxerArxiuUuid().isEmpty()) {
	    	if (this.potGenerarVersioImprimible(registreAnnexEntity)) {
	    		try {
	    			TemporalThreadStorage.set("numeroRegistre", registre.getNumero());
	    			fitxerDto = pluginHelper.arxiuDocumentImprimible(
                            (endsWithUuid ?"":"/uuid/") +
                            registreAnnexEntity.getFitxerArxiuUuid(), titol);
	    		} catch (Exception ex) {
                    if (!endsWithUuid) {
                        try {
                            return pluginHelper.arxiuDocumentImprimible(registreAnnexEntity.getFirmaCsv(), titol);
                        } catch (Exception e) {
                            throw new Exception("Error no controlat consultant la versió imprimible: " + e.getMessage());
                        }
                    }
	    			throw new Exception("Error no controlat consultant la versió imprimible: " + ex.getMessage());
	    		}
	    	} else {
	    		throw new Exception("No és un document pel qual es pugui generar una versió imprimible.");
	    	}
	    } else {
	    	throw new Exception("No es pot obtenir una versió imprimible d'un document que no està a l'Arxiu.");
	    }
	    return fitxerDto;
	}
	
	public FitxerDto getAnnexFitxer(Long annexId, boolean ambVersioImprimible) {
		RegistreAnnexEntity registreAnnexEntity = registreAnnexRepository.findById(annexId).get();
	    RegistreEntity registre = registreAnnexEntity.getRegistre();
	    FitxerDto fitxerDto = new FitxerDto();
	    // if annex is already created in arxiu take content from arxiu
	    if (registreAnnexEntity.getFitxerArxiuUuid() != null && !registreAnnexEntity.getFitxerArxiuUuid().isEmpty()) {
	    	if (ambVersioImprimible && this.potGenerarVersioImprimible(registreAnnexEntity)) {
	    		try {
	    			fitxerDto = this.getAnnexFitxerImprimible(annexId);
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
	    			byte[] firmaContingut = this.getFirmaContingut(firmaEntity.getGesdocFirmaId(),firmaEntity.getFitxerNom(),registre.getNumero());
	    			fitxerDto.setNom(firmaEntity.getFitxerNom());
	    			fitxerDto.setContentType(firmaEntity.getTipusMime());
	    			fitxerDto.setContingut(firmaContingut);
	    			fitxerDto.setTamany(firmaContingut.length);
	    		}
	    		if (registreAnnexEntity.getGesdocDocumentId() != null) {
	    			ByteArrayOutputStream streamAnnex = new ByteArrayOutputStream();
	    			gestioDocumentalHelper.gestioDocumentalGet(
	    					registreAnnexEntity.getGesdocDocumentId(),
                            registreAnnexEntity.getFitxerNom(),
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
                            registreAnnexEntity.getFitxerNom(),
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
	private byte[] getFirmaContingut(String gesdocFirmaId, String nom, String registreNumero) {
		byte[] firmaContingut = null;
		if (gesdocFirmaId != null) {
			ByteArrayOutputStream streamAnnexFirma = new ByteArrayOutputStream();
			gestioDocumentalHelper.gestioDocumentalGet(
					gesdocFirmaId,
                    nom,
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
	public boolean potGenerarVersioImprimible(RegistreAnnexEntity annex) {
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
			String procedimentCodi) {
		RegistreAnnexEntity annex = registreAnnexRepository.getReferenceById(annexId);
		boolean isAnnexDefinitiuInArxiu = false;
		RegistreEntity registre = annex.getRegistre();
		DocumentEniRegistrableDto documentEniRegistrableDto = new DocumentEniRegistrableDto();
		documentEniRegistrableDto.setNumero(registre.getNumero());
		documentEniRegistrableDto.setData(registre.getData());
		documentEniRegistrableDto.setOficinaDescripcio(registre.getOficinaDescripcio());
		documentEniRegistrableDto.setOficinaCodi(registre.getOficinaCodi());
		
		// Comprovar si l'annex està custodiat i si està com a definitiu
		if (annex.getFitxerArxiuUuid() != null) {
			Document annexArxiu = pluginHelper.arxiuDocumentConsultar(
					annex.getFitxerArxiuUuid(), 
					null, 
					false, 
					registre.getNumero());
			
			isAnnexDefinitiuInArxiu = DocumentEstat.DEFINITIU.equals(annexArxiu.getEstat());
			if (isAnnexDefinitiuInArxiu && annex.getFirmaCsv() == null) {
				annex.updateFirmaCsv(annexArxiu.getDocumentMetadades().getCsv());
			}
		}
		// Només crea l'annex a dins el contenidor si encara no s'ha creat o està com esborrany per tornar a provar de guardar com a definitiu
		if ((annex.getFitxerArxiuUuid() == null || 
				!AnnexEstat.DEFINITIU.equals(annex.getArxiuEstat())) && ! isAnnexDefinitiuInArxiu) {
			
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
		
		if (isAnnexDefinitiuInArxiu) {
			annex.setArxiuEstat(AnnexEstat.DEFINITIU);
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

	@Transactional
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
        registreAnotacio.setAnnexos(
                registreAnotacio.getAnnexos().stream().filter(p ->
                        (registre.getJustificant() == null || !registre.getJustificant().getId().equals(p.getId()))
                        && (registre.getJustificantArxiuUuid() == null || !registre.getJustificantArxiuUuid().equals(p.getFitxerArxiuUuid()))
                        && (!"tothom".equalsIgnoreCase(rolActual) || p.getSicresTipusDocument() == null || !RegistreAnnexSicresTipusDocumentEnum.INTERN.getValor().equals(p.getSicresTipusDocument())) )
                        .collect(Collectors.toList())
        );
		return registreAnotacio;
	}
	
	@Transactional(readOnly = true)
	public List<Integer> findCopiesRegistre(String numero) {
		return registreRepository.findCopiesByNumero(numero);
	}
	
	public String getClauSecretaProperty() {
		String clauSecreta = configHelper.getConfig("es.caib.distribucio.backoffice.integracio.clau");
		if (clauSecreta == null)
			throw new RuntimeException("Clau secreta no specificada al fitxer de propietats");
		return clauSecreta;
	}

	public int getEnviarIdsAnotacionsMaxReintentsProperty(EntitatEntity entitat) {
		String maxReintents = configHelper.getConfigForEntitat(entitat != null ? entitat.getCodi() : null, "es.caib.distribucio.tasca.enviar.anotacions.max.reintents");
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
			if (firmes != null) {
				Iterator<es.caib.pluginsib.arxiu.api.Firma> it = firmes.iterator();
				while (it.hasNext()) {
					es.caib.pluginsib.arxiu.api.Firma f = it.next();
					if (f.getTipus() == FirmaTipus.CSV) {
						it.remove();
					}
				}
				if (firmes.size() > indexFirma) {
					firma = firmes.get(indexFirma);
				}
			}
		}
		return firma;
	}

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
			
			ConfigHelper.setEntitatActualCodi(entitat.getCodi());

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

	/** Mètode per modificar l'estat d'una anotació a pendent i afegir un comentari i una entrada als logs. */
	@Transactional
	public void canviEstatComunicatAPendent(Long registreId, Integer dies) {

		RegistreEntity registre = registreRepository.getReferenceById(registreId);
        String observacions = "S'ha canviat automàticament l'estat a \"Bústia pendent\" després d'estar "+
                dies +" dies en estat \"Comunicada a "+ registre.getBackCodi() +"\" sense confirmació de recepció";
        registre.setNewProcesEstat(RegistreProcesEstatEnum.BUSTIA_PENDENT);
        ContingutComentariEntity comentari = ContingutComentariEntity.getBuilder(registre, observacions).build();
        contingutComentariRepository.save(comentari);
        List<String> params = new ArrayList<>();
        params.add(String.valueOf(dies));
        params.add(registre.getBackCodi());
        contingutLogHelper.log(
                registre,
                LogTipusEnumDto.CANVI_PENDENT,
                params,
                false);

	}



	/**
	 * Obté la llista de registres que compleixen amb l'identificador.
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = true)
	public List<RegistreEntity> findRegistresPerIdentificador(AnotacioRegistreId id) throws Exception {
		List<RegistreEntity> resultat = new ArrayList<RegistreEntity>();
		String clauSecreta = this.getClauSecretaProperty();
		// Cerca el registre per clau i identificador encriptades tenint en compte que pot haver anotacions reenviades
		List<RegistreEntity> registres = registreRepository.findByNumero(id.getIdentificador());
		if (registres.isEmpty()) {
			throw new NotFoundException(
					id,
					RegistreEntity.class);
		}
		String encryptedIdentificator = "";
		for(RegistreEntity r : registres) {
			encryptedIdentificator = RegistreHelper.encrypt(
					id.getIdentificador() + "_" + Long.valueOf(r.getId()),
					clauSecreta);
			if (encryptedIdentificator.equals(id.getClauAcces())) {
				resultat.add(r);
			}
		}
		if (resultat.isEmpty() && registres.size() > 0) {
			// Codifica només l'identificador com es feia fins la versió 0.9.43.1 
			encryptedIdentificator = RegistreHelper.encrypt(id.getIdentificador(), clauSecreta);
			if (encryptedIdentificator.equals(id.getClauAcces())) {
				logger.warn("S'han trobat " + registres.size() + " registres per l'identficiador " + id.getIdentificador() + " en la consulta pel backoffice");
				registres = contingutLogRepository.findByNumeroAndComunidaBackoffice(id.getIdentificador());
				if (registres != null && !registres.isEmpty()) {
					for (RegistreEntity r : registres) {
						resultat.add(r);
					}
				}
			}
		}
		return resultat;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(RegistreHelper.class);
}
