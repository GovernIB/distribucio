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
import java.util.List;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import es.caib.distribucio.core.api.dto.ArxiuFirmaDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaPerfilEnumDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.core.api.dto.DocumentEniRegistrableDto;
import es.caib.distribucio.core.api.dto.LogTipusEnumDto;
import es.caib.distribucio.core.api.dto.ReglaTipusEnumDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
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
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.RegistreAnnexEntity;
import es.caib.distribucio.core.entity.RegistreAnnexFirmaEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.RegistreInteressatEntity;
import es.caib.distribucio.core.entity.ReglaEntity;
import es.caib.distribucio.core.repository.RegistreAnnexFirmaRepository;
import es.caib.distribucio.core.repository.RegistreAnnexRepository;
import es.caib.distribucio.core.repository.RegistreInteressatRepository;
import es.caib.distribucio.core.repository.RegistreRepository;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnnex;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreAnotacio;
import es.caib.distribucio.plugin.distribucio.DistribucioRegistreFirma;
import es.caib.plugins.arxiu.api.Document;

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
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;
	@Autowired
	private BustiaHelper bustiaHelper;
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
	private MetricRegistry metricRegistry;
	

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
			String unitatAdministrativa,
			RegistreAnotacio registreAnotacio,
			ReglaEntity regla,
			RegistreProcesEstatEnum estat) {
		
		final Timer timerfindPerEntitatAndCodi = metricRegistry.timer(MetricRegistry.name(RegistreHelper.class, "crearRegistreEntity.findPerEntitatAndCodi"));
		Timer.Context contextfindPerEntitatAndCodi = timerfindPerEntitatAndCodi.time();
		UnitatOrganitzativaDto unitat = unitatOrganitzativaHelper.findPerEntitatAndCodi(
				entitat.getCodi(),
				unitatAdministrativa);
		contextfindPerEntitatAndCodi.stop();
		
		final Timer timersaveRegistre = metricRegistry.timer(MetricRegistry.name(RegistreHelper.class, "crearRegistreEntity.saveRegistre"));
		Timer.Context contextsaveRegistre = timersaveRegistre.time();
		String justificantArxiuUuid = null;
		if (registreAnotacio.getJustificant() != null) {
			justificantArxiuUuid = registreAnotacio.getJustificant().getFitxerArxiuUuid();
		}
		// save annotacio in db
		RegistreEntity registreEntity = RegistreEntity.getBuilder(
				entitat,
				tipus,
				unitatAdministrativa,
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
		
		
		final Timer timersaveInteressats = metricRegistry.timer(MetricRegistry.name(RegistreHelper.class, "crearRegistreEntity.saveInteressats"));
		Timer.Context contextsaveInteressats = timersaveInteressats.time();
		// save interessats in db
		if (registreAnotacio.getInteressats() != null) { 
			for (RegistreInteressat registreInteressat: registreAnotacio.getInteressats()) {
				registreEntity.getInteressats().add(
						crearInteressatEntity(
								registreInteressat,
								registreEntity));
			}
		}
		contextsaveInteressats.stop();
		
		
		final Timer timersaveAnnexos = metricRegistry.timer(MetricRegistry.name(RegistreHelper.class, "crearRegistreEntity.saveAnnexos"));
		Timer.Context contextsaveAnnexos = timersaveAnnexos.time();
		
		// save annexos and firmes in db and their byte content in the folder in local filesystem
		if (registreAnotacio.getAnnexos() != null) { 
			for (RegistreAnnex registreAnnex: registreAnotacio.getAnnexos()) {
				registreEntity.getAnnexos().add(
						crearAnnexEntity(
								registreAnnex,
								registreEntity));
			}
		}
		contingutLogHelper.log(
				registreEntity,
				LogTipusEnumDto.CREACIO,
				registreEntity.getNom(),
				null,
				false,
				false);
		contextsaveAnnexos.stop();
		
		
		return registreEntity;
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
		String pathName = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.bustia.contingut.documents.dir");
		
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
				firmes.add(firma);
			}
		}
		return firmes;
	}


	@Transactional
	public Exception processarAnotacioPendentArxiu(Long anotacioId) {
		RegistreEntity anotacio = registreRepository.findOne(anotacioId);
		BustiaEntity bustia = bustiaHelper.findBustiaDesti(
				anotacio.getEntitat(),
				anotacio.getUnitatAdministrativa());
		
		// PROCESSAR ARXIU
		Exception exceptionGuardantAnnexos = createRegistreAndAnnexosInArxiu(
				anotacio,
				bustia.getEntitat().getCodiDir3(),
				true);
		if (exceptionGuardantAnnexos == null) {
			
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
			
			RegistreProcesEstatEnum nouEstat;
			if (anotacio.getRegla() != null && anotacio.getRegla().getTipus() == ReglaTipusEnumDto.BACKOFFICE) {
				nouEstat = RegistreProcesEstatEnum.REGLA_PENDENT;
			} else {
				nouEstat = RegistreProcesEstatEnum.BUSTIA_PENDENT;
				emailHelper.createEmailsPendingToSend(
						bustia, 
						anotacio, 
						anotacio.getDarrerMoviment());	
			}
			anotacio.updateProces(
					nouEstat, 
					null);
			return null;
		} else {
			anotacio.updateProces(
					null, 
					exceptionGuardantAnnexos);
			return exceptionGuardantAnnexos;
		}
	}

	@Transactional
	public Exception processarAnotacioPendentRegla(Long anotacioId) {
		RegistreEntity anotacio = registreRepository.findOne(anotacioId);
		Exception exceptionAplicantRegla = null;
		
//		if (anotacio.getProcesEstat() == RegistreProcesEstatEnum.REGLA_PENDENT && anotacio.getRegla() != null) {
			exceptionAplicantRegla = reglaHelper.aplicarControlantException(anotacio);
//		}
		return exceptionAplicantRegla;

	}

	/**
	 *  It saves anotacio with annexes in arxiu.
	 * @param registreEntity
	 * @param codiDir3
	 * @param crearAutofirma
	 * @return
	 */
	public Exception createRegistreAndAnnexosInArxiu(
			RegistreEntity registreEntity,
			String unitatOrganitzativaCodi,
			boolean crearAutofirma) {
		
		Exception exception = null;
		
		if (registreEntity.getAnnexos() != null && registreEntity.getAnnexos().size() > 0) {
			DistribucioRegistreAnotacio distribucioRegistreAnotacio = conversioTipusHelper.convertir(
					registreEntity,
					DistribucioRegistreAnotacio.class);
			String uuidExpedient = null;
			
			// check if registre is not already created in arxiu
			if (registreEntity.getExpedientArxiuUuid() == null) {
				logger.debug("Creant contenidor pels annexos de l'anotació (" +
						"anotacioNumero=" + registreEntity.getNumero() + ", " +
						"unitatOrganitzativaCodi=" + unitatOrganitzativaCodi + ")");
				try {
					// ============= SAVE REGISTRE AS EXPEDIENT IN ARXIU ============
					uuidExpedient = pluginHelper.saveRegistreAsExpedientInArxiu(
							registreEntity.getNumero(),
							distribucioRegistreAnotacio.getNumero(),
							unitatOrganitzativaCodi);
					registreEntity.updateExpedientArxiuUuid(uuidExpedient);
					logger.debug("Creat el contenidor a l'Arxiu per l'anotació (" +
							"anotacioNumero=" + registreEntity.getNumero() + ", " +
							"unitatOrganitzativaCodi=" + unitatOrganitzativaCodi + ") amb uuid " + uuidExpedient);
				} catch (Exception ex) {
					return ex;
				}
			// Si el contenidor ja està creat agafam el seu UUID
			} else {
				uuidExpedient = registreEntity.getExpedientArxiuUuid();
				logger.debug("L'anotació (" +
						"anotacioNumero=" + registreEntity.getNumero() + ", " +
						"unitatOrganitzativaCodi=" + unitatOrganitzativaCodi + ") ja estava a l'Arxiu amb uuid " + uuidExpedient);
			}
			
			if (uuidExpedient != null) {
				logger.debug("Guardant " + registreEntity.getAnnexos().size() + " annexos de l'anotació (" +
						"anotacioNumero=" + registreEntity.getNumero() + ", " +
						"unitatOrganitzativaCodi=" + unitatOrganitzativaCodi + ") amb uuid " + uuidExpedient + " a l'Arxiu.");
				for (int i = 0; i < registreEntity.getAnnexos().size(); i++) {
					try {
						
						RegistreAnnexEntity annex = registreEntity.getAnnexos().get(i);
						// Només crea l'annex a dins el contenidor si encara no s'ha creat
						if (annex.getFitxerArxiuUuid() == null) {
							logger.debug("Creant annex a dins el contenidor de l'anotació (" +
									"anotacioNumero=" + registreEntity.getNumero() + ", " +
									"annexTitol=" + annex.getTitol() + ", " +
									"unitatOrganitzativaCodi=" + unitatOrganitzativaCodi + ")");
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
						}
					
					} catch (Exception ex) {
						exception = ex;
					}
				}
			}
		}
		if (exception != null) {
			return exception;
		} else {
			logger.debug("Creació del contenidor i dels annexos finalitzada correctament (" +
					"anotacioNumero=" + registreEntity.getNumero() + ", " +
					"unitatOrganitzativaCodi=" + unitatOrganitzativaCodi + ")");
			contingutLogHelper.log(
					registreEntity,
					LogTipusEnumDto.DISTRIBUCIO,
					registreEntity.getNom(),
					null,
					false,
					false);
			return null;
		}
			
	}

	@Transactional
	public void tancarExpedientArxiu(Long registreId) {
		RegistreEntity registre = registreRepository.findOne(registreId);
		Exception exception = null;
		try {
			pluginHelper.distribucioContenidorMarcarProcessat(registre);
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
	public Throwable enviarIdsAnotacionsBackoffice(List<Long> pendentsIdsGroupedByRegla) {

		Throwable throwable = null;
		List <RegistreEntity> pendentsByRegla = new ArrayList<>();
		try {

			for(Long id: pendentsIdsGroupedByRegla){
				RegistreEntity pendent = registreRepository.findOne(id);
				pendentsByRegla.add(pendent);
			}

			String clauSecreta = PropertiesHelper.getProperties().getProperty(
					"es.caib.distribucio.backoffice.integracio.clau");

			List<AnotacioRegistreId> ids = new ArrayList<>();
			for (RegistreEntity pendent : pendentsByRegla) {

				AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
				anotacioRegistreId.setIndetificador(pendent.getNumero());
				anotacioRegistreId.setClauAcces(RegistreHelper.encrypt(pendent.getNumero(),
						clauSecreta));
				ids.add(anotacioRegistreId);

			}
			ReglaEntity regla = pendentsByRegla.get(0).getRegla();
			logger.debug(">>> Abans de crear backoffice WS");
			BackofficeWsService backofficeClient = new WsClientHelper<BackofficeWsService>().generarClientWs(
					getClass().getResource(
							"/es/caib/distribucio/core/service/ws/backoffice/backoffice.wsdl"),
					regla.getBackofficeUrl(),
					new QName(
							"http://www.caib.es/distribucio/ws/backoffice",
							"BackofficeService"),
					regla.getBackofficeUsuari(),
					regla.getBackofficeContrasenya(),
					null,
					BackofficeWsService.class);
			
			logger.debug(">>> Abans de cridar backoffice WS");
			backofficeClient.comunicarAnotacionsPendents(ids);
			logger.debug(">>> Despres de cridar backoffice WS");			
			return null;
		} catch (Throwable ex) {
			logger.error("Error enviant anotacions al backoffice", ex);
			throwable = ex;
			return throwable;
		}
	}
	
	

	public Throwable enviarIdsAnotacionsBackUpdateDelayTime(List<Long> pendentsIdsGroupedByRegla) {

		Throwable throwable = enviarIdsAnotacionsBackoffice(pendentsIdsGroupedByRegla);
		updateBackEnviarDelayData(pendentsIdsGroupedByRegla, throwable);
		return throwable;
	}
	
	
	@Transactional()
	public void updateBackEnviarDelayData(List<Long> pendentsIdsGroupedByRegla, Throwable throwable) {
	
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
			String tempsEspera = PropertiesHelper.getProperties().getProperty(
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
				fitxerTipusMime(registreAnnex.getFitxerTipusMime()).
				localitzacio(registreAnnex.getLocalitzacio()).
				ntiElaboracioEstat(RegistreAnnexElaboracioEstatEnum.valorAsEnum(registreAnnex.getEniEstatElaboracio())).
				observacions(registreAnnex.getObservacions()).
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
}
