/**
 * 
 */
package es.caib.distribucio.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.ArxiuContingutDto;
import es.caib.distribucio.core.api.dto.ArxiuContingutTipusEnumDto;
import es.caib.distribucio.core.api.dto.ArxiuDetallDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaDto;
import es.caib.distribucio.core.api.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.core.api.dto.ClassificacioResultatDto;
import es.caib.distribucio.core.api.dto.ClassificacioResultatDto.ClassificacioResultatEnumDto;
import es.caib.distribucio.core.api.dto.ExpedientEstatEnumDto;
import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.ProcedimentDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDetallDto;
import es.caib.distribucio.core.api.dto.RegistreAnotacioDto;
import es.caib.distribucio.core.api.dto.ReglaTipusEnumDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.exception.ValidationException;
import es.caib.distribucio.core.api.registre.RegistreAnnexNtiTipusDocumentEnum;
import es.caib.distribucio.core.api.registre.RegistreAnnexOrigenEnum;
import es.caib.distribucio.core.api.registre.RegistreAnnexSicresTipusDocumentEnum;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatSistraEnum;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.core.api.service.ws.backoffice.Annex;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.core.api.service.ws.backoffice.DocumentTipus;
import es.caib.distribucio.core.api.service.ws.backoffice.Estat;
import es.caib.distribucio.core.api.service.ws.backoffice.Interessat;
import es.caib.distribucio.core.api.service.ws.backoffice.InteressatTipus;
import es.caib.distribucio.core.api.service.ws.backoffice.NtiEstadoElaboracio;
import es.caib.distribucio.core.api.service.ws.backoffice.NtiOrigen;
import es.caib.distribucio.core.api.service.ws.backoffice.NtiTipoDocumento;
import es.caib.distribucio.core.api.service.ws.backoffice.Representant;
import es.caib.distribucio.core.api.service.ws.backoffice.SicresTipoDocumento;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.ContingutEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.RegistreAnnexEntity;
import es.caib.distribucio.core.entity.RegistreAnnexFirmaEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.RegistreInteressatEntity;
import es.caib.distribucio.core.entity.ReglaEntity;
import es.caib.distribucio.core.helper.BustiaHelper;
import es.caib.distribucio.core.helper.ContingutHelper;
import es.caib.distribucio.core.helper.ConversioTipusHelper;
import es.caib.distribucio.core.helper.EntityComprovarHelper;
import es.caib.distribucio.core.helper.PermisosHelper;
import es.caib.distribucio.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.distribucio.core.helper.PluginHelper;
import es.caib.distribucio.core.helper.PropertiesHelper;
import es.caib.distribucio.core.helper.RegistreHelper;
import es.caib.distribucio.core.helper.ReglaHelper;
import es.caib.distribucio.core.helper.UnitatOrganitzativaHelper;
import es.caib.distribucio.core.repository.BustiaRepository;
import es.caib.distribucio.core.repository.RegistreAnnexRepository;
import es.caib.distribucio.core.repository.RegistreRepository;
import es.caib.distribucio.core.security.ExtendedPermission;
import es.caib.distribucio.plugin.procediment.Procediment;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import es.caib.plugins.arxiu.api.Firma;
import es.caib.plugins.arxiu.api.FirmaTipus;

/**
 * Implementació dels mètodes per a gestionar anotacions
 * de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class RegistreServiceImpl implements RegistreService {

	@Autowired
	private RegistreRepository registreRepository;
	@Autowired
	private RegistreAnnexRepository registreAnnexRepository;
	@Autowired
	private BustiaRepository bustiaRepository;
	@Autowired
	private ContingutHelper contingutHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private BustiaHelper bustiaHelper;
	@Autowired
	private RegistreHelper registreHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private ReglaHelper reglaHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private UnitatOrganitzativaHelper unitatOrganitzativaHelper;

	@Transactional(readOnly = true)
	@Override
	public RegistreAnotacioDto findOne(
			Long entitatId,
			Long bustiaId,
			Long registreId) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaId=" + bustiaId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity bustia = entityComprovarHelper.comprovarContingut(
				entitat,
				bustiaId,
				null);
		if (bustia instanceof BustiaEntity) {
			entityComprovarHelper.comprovarBustia(
					entitat,
					bustiaId,
					true);
		} else {
			// Comprova l'accés al path del contenidor pare
			contingutHelper.comprovarPermisosPathContingut(
					bustia,
					true,
					false,
					false,
					true);
		}
		RegistreEntity registre = registreRepository.findByPareAndId(
				bustia,
				registreId);
		RegistreAnotacioDto registreAnotacio = (RegistreAnotacioDto)contingutHelper.toContingutDto(registre);
		contingutHelper.tractarInteressats(registreAnotacio.getInteressats());		
		return registreAnotacio;
	}


	@Override
	@Transactional(readOnly = true)
	public List<RegistreAnotacioDto> findMultiple(
			Long entitatId,
			List<Long> multipleRegistreIds) {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "multipleRegistreIds=" + multipleRegistreIds + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		List<BustiaEntity> bustiesPermeses = bustiaRepository.findByEntitatAndPareNotNull(entitat);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		permisosHelper.filterGrantedAll(
				bustiesPermeses,
				new ObjectIdentifierExtractor<BustiaEntity>() {
					@Override
					public Long getObjectIdentifier(BustiaEntity bustia) {
						return bustia.getId();
					}
				},
				BustiaEntity.class,
				new Permission[] {ExtendedPermission.READ},
				auth);
		List<RegistreEntity> registres = registreRepository.findByPareInAndIdIn(
				bustiesPermeses,
				multipleRegistreIds);
		List<RegistreAnotacioDto> resposta = new ArrayList<RegistreAnotacioDto>();
		for (RegistreEntity registre: registres) {
			resposta.add((RegistreAnotacioDto)contingutHelper.toContingutDto(
					registre,
					false,
					false,
					false,
					false,
					true,
					false,
					false));
		}
		return resposta;
	}






	@Transactional(readOnly = true)
	@Override
	public AnotacioRegistreEntrada findOneForBackoffice(
			AnotacioRegistreId id)  {
		logger.debug("Obtenint anotació de registre per backoffice("
				+ "id=" + id + ")");
		AnotacioRegistreEntrada anotacioPerBackoffice = new AnotacioRegistreEntrada();
		try {
			// check if anotacio was sent with correct key
			String clauSecreta = PropertiesHelper.getProperties().getProperty("es.caib.distribucio.backoffice.integracio.clau");
			if (clauSecreta == null) {
				throw new RuntimeException("Clau secreta no specificada al fitxer de propietats");
			}
			String encryptedIdentificator = RegistreHelper.encrypt(
					id.getIndetificador(),
					clauSecreta);
			if (!encryptedIdentificator.equals(id.getClauAcces())) {
				throw new RuntimeException("La clau o identificador és incorrecte");
			}
			RegistreEntity registreEntity = registreRepository.findByNumero(id.getIndetificador());
			anotacioPerBackoffice.setIdentificador(registreEntity.getNumero());
			anotacioPerBackoffice.setData(registreEntity.getData());
			anotacioPerBackoffice.setExtracte(registreEntity.getExtracte());
			anotacioPerBackoffice.setEntitatCodi(registreEntity.getEntitatCodi());
			anotacioPerBackoffice.setEntitatDescripcio(registreEntity.getEntitatDescripcio());
			anotacioPerBackoffice.setUsuariCodi(registreEntity.getUsuariCodi());
			anotacioPerBackoffice.setUsuariNom(registreEntity.getUsuariNom());
			anotacioPerBackoffice.setOficinaCodi(registreEntity.getOficinaCodi());
			anotacioPerBackoffice.setOficinaDescripcio(registreEntity.getOficinaDescripcio());
			anotacioPerBackoffice.setLlibreCodi(registreEntity.getLlibreCodi());
			anotacioPerBackoffice.setLlibreDescripcio(registreEntity.getLlibreDescripcio());
			anotacioPerBackoffice.setDocFisicaCodi(registreEntity.getDocumentacioFisicaCodi());
			anotacioPerBackoffice.setDocFisicaDescripcio(registreEntity.getDocumentacioFisicaDescripcio());
			anotacioPerBackoffice.setAssumpteTipusCodi(registreEntity.getAssumpteTipusCodi());
			anotacioPerBackoffice.setAssumpteTipusDescripcio(registreEntity.getAssumpteTipusDescripcio());
			anotacioPerBackoffice.setAssumpteCodiCodi(registreEntity.getAssumpteCodi());
			anotacioPerBackoffice.setProcedimentCodi(registreEntity.getProcedimentCodi());
			anotacioPerBackoffice.setAssumpteCodiDescripcio(registreEntity.getAssumpteDescripcio());
			anotacioPerBackoffice.setTransportTipusCodi(registreEntity.getTransportTipusCodi());
			anotacioPerBackoffice.setTransportTipusDescripcio(registreEntity.getTransportTipusDescripcio());
			anotacioPerBackoffice.setTransportNumero(registreEntity.getTransportNumero());
			anotacioPerBackoffice.setIdiomaCodi(registreEntity.getIdiomaCodi());
			anotacioPerBackoffice.setIdomaDescripcio(registreEntity.getIdiomaDescripcio());
			anotacioPerBackoffice.setObservacions(registreEntity.getObservacions());
			anotacioPerBackoffice.setOrigenRegistreNumero(registreEntity.getNumeroOrigen());
			anotacioPerBackoffice.setOrigenData(registreEntity.getDataOrigen());
			anotacioPerBackoffice.setAplicacioCodi(registreEntity.getAplicacioCodi());
			anotacioPerBackoffice.setAplicacioVersio(registreEntity.getAplicacioVersio());
			anotacioPerBackoffice.setRefExterna(registreEntity.getReferencia());
			anotacioPerBackoffice.setExpedientNumero(registreEntity.getExpedientNumero());
			anotacioPerBackoffice.setExposa(registreEntity.getExposa());
			anotacioPerBackoffice.setSolicita(registreEntity.getSolicita());
			anotacioPerBackoffice.setDestiCodi(registreEntity.getUnitatAdministrativa());
			anotacioPerBackoffice.setDestiDescripcio(registreEntity.getUnitatAdministrativaDescripcio());
			anotacioPerBackoffice.setInteressats(toInteressats(registreEntity.getInteressats()));
			anotacioPerBackoffice.setAnnexos(getAnnexosPerBackoffice(registreEntity.getId()));
		} catch (Exception ex){
			throw new RuntimeException(ex);
		}
		return anotacioPerBackoffice;
	}

	@SuppressWarnings("incomplete-switch")
	@Transactional
	@Override
	public void canviEstat(
			AnotacioRegistreId id,
			Estat estat,
			String observacions) {
		try {
			// check if anotacio was sent with correct key
			String clauSecreta = PropertiesHelper.getProperties().getProperty(
					"es.caib.distribucio.backoffice.integracio.clau");
			if (clauSecreta == null)
				throw new RuntimeException("Clau secreta no specificada al fitxer de propietats");
			String encryptedIdentificator = RegistreHelper.encrypt(id.getIndetificador(),
					clauSecreta);
			if (!encryptedIdentificator.equals(id.getClauAcces()))
				throw new RuntimeException("La clau o identificador és incorrecte");
			RegistreEntity registre = registreRepository.findByNumero(id.getIndetificador());
			switch (estat) {
			case REBUDA:
				registre.updateBackEstat(RegistreProcesEstatEnum.BACK_REBUDA,
						observacions);
				registre.updateBackRebudaData(new Date());

				break;
			case PROCESSADA:
				registre.updateBackEstat(RegistreProcesEstatEnum.BACK_PROCESSADA,
						observacions);
				registre.updateBackProcesRebutjErrorData(new Date());
				registreHelper.tancarExpedientArxiu(registre.getId());
				break;
			case REBUTJADA:
				registre.updateBackEstat(RegistreProcesEstatEnum.BACK_REBUTJADA,
						observacions);
				registre.updateBackProcesRebutjErrorData(new Date());
				break;
			case ERROR:
				registre.updateBackEstat(RegistreProcesEstatEnum.BACK_ERROR,
						observacions);
				registre.updateBackProcesRebutjErrorData(new Date());
				break;
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	@Transactional
	@Override
	public RegistreAnnexDetallDto getRegistreJustificant(
			Long entitatId,
			Long bustiaId,
			Long registreId) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaId=" + bustiaId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity bustia = entityComprovarHelper.comprovarContingut(
				entitat,
				bustiaId,
				null);
		if (bustia instanceof BustiaEntity) {
			entityComprovarHelper.comprovarBustia(
					entitat,
					bustiaId,
					true);
		} else {
			// Comprova l'accés al path del contenidor pare
			contingutHelper.comprovarPermisosPathContingut(
					bustia,
					true,
					false,
					false,
					true);
		}
		RegistreEntity registre = registreRepository.findByPareAndId(
				bustia,
				registreId);
		RegistreAnnexDetallDto justificant = getJustificantPerRegistre(
					entitat, 
					bustia, 
					registre.getJustificantArxiuUuid(), 
					true);
		justificant.setRegistreId(registreId);
		return justificant;
	}

	@Transactional(readOnly = true)
	@Override
	public List<RegistreAnnexDetallDto> getAnnexos(
			Long entitatId,
			Long bustiaId,
			Long registreId	
			) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaId=" + bustiaId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity bustia = entityComprovarHelper.comprovarContingut(
				entitat,
				bustiaId,
				null);
		if (bustia instanceof BustiaEntity) {
			entityComprovarHelper.comprovarBustia(
					entitat,
					bustiaId,
					true);
		} else {
			// Comprova l'accés al path del contenidor pare
			contingutHelper.comprovarPermisosPathContingut(
					bustia,
					true,
					false,
					false,
					true);
		}
		RegistreEntity registre = registreRepository.findByPareAndId(
				bustia,
				registreId);
		List<RegistreAnnexDetallDto> anexosDto = new ArrayList<>();
		for (RegistreAnnexEntity annexEntity: registre.getAnnexos()) {
			
			RegistreAnnexDetallDto annex = conversioTipusHelper.convertir(
					annexEntity,
					RegistreAnnexDetallDto.class);
			annex.setAmbDocument(true);
			anexosDto.add(annex);
		}
		return anexosDto;
	}

	@Transactional
	@Override
	public void rebutjar(
			Long entitatId,
			Long bustiaId,
			Long registreId,
			String motiu) {
		logger.debug("Rebutjar anotació de registre a la bústia ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaId=" + bustiaId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaId,
				true);
		RegistreEntity registre = entityComprovarHelper.comprovarRegistre(
				registreId,
				null);
		if (!registre.getPare().equals(bustia)) {
			logger.error("No s'ha trobat el registre a dins la bústia (" +
					"bustiaId=" + bustiaId + "," +
					"registreId=" + registreId + ")");
			throw new ValidationException(
					registreId,
					RegistreEntity.class,
					"La bústia especificada (id=" + bustiaId + ") no coincideix amb la bústia de l'anotació de registre");
		}
		registre.updateMotiuRebuig(motiu);
		bustiaHelper.evictCountElementsPendentsBustiesUsuari(
				entitat,
				bustia);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean reintentarEnviamentBackofficeAdmin(
			Long entitatId,
			Long bustiaId,
			Long registreId) {
		logger.debug("Reintentant processament d'anotació pendent per admins (" +
				"entitatId=" + entitatId + ", " +
				"bustiaId=" + bustiaId + ", " +
				"registreId=" + registreId + ")");

		RegistreEntity anotacio = registreRepository.findOne(registreId);

		List<Long> pendentsIds = new ArrayList<>();
		pendentsIds.add(anotacio.getId());
		Throwable exceptionProcessant = registreHelper.enviarIdsAnotacionsBackUpdateDelayTime(pendentsIds);
		return exceptionProcessant == null;

	}

	@Override
	@Transactional
	public boolean reintentarProcessamentAdmin(
			Long entitatId,
			Long bustiaId,
			Long registreId) {
		logger.debug("Reintentant processament d'anotació pendent per admins (" +
				"entitatId=" + entitatId + ", " +
				"bustiaId=" + bustiaId + ", " +
				"registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaId,
				false);
		RegistreEntity anotacio = entityComprovarHelper.comprovarRegistre(
				registreId,
				bustia);
		Exception exceptionProcessant = processarAnotacioPendent(anotacio);
		return exceptionProcessant == null;
	}

	@Override
	@Transactional
	public boolean reintentarProcessamentUser(
			Long entitatId,
			Long bustiaId,
			Long registreId) {
		logger.debug("Reintentant processament d'anotació pendent per usuaris (" +
				"entitatId=" + entitatId + ", " +
				"bustiaId=" + bustiaId + ", " +
				"registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaId,
				true);
		RegistreEntity anotacio = entityComprovarHelper.comprovarRegistre(
				registreId,
				bustia);
		Exception exceptionProcessant = processarAnotacioPendent(anotacio);
		return exceptionProcessant == null;
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto getArxiuAnnex(
			Long annexId) {
		RegistreAnnexEntity annex = registreAnnexRepository.findOne(annexId);
		FitxerDto arxiu = new FitxerDto();
		Document document = null;
		document = pluginHelper.arxiuDocumentConsultar(annex.getFitxerArxiuUuid(), null, true, true);
		if (document != null) {
			DocumentContingut documentContingut = document.getContingut();
			if (documentContingut != null) {
				arxiu.setNom(annex.getFitxerNom());
				arxiu.setContentType(documentContingut.getTipusMime());
				arxiu.setContingut(documentContingut.getContingut());
				arxiu.setTamany(documentContingut.getContingut().length);
			}
		}
		return arxiu;
	}

	@Transactional(readOnly = true)
	@Override
	public FitxerDto getJustificant(
			Long registreId) {
		RegistreEntity registre = registreRepository.findOne(registreId);
		FitxerDto arxiu = new FitxerDto();
		Document document = null;
		document = pluginHelper.arxiuDocumentConsultar(registre.getJustificantArxiuUuid(), null, true, true);
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

	@Transactional(readOnly = true)
	@Override
	public FitxerDto getAnnexFirmaContingut(
			Long annexId,
			int indexFirma) {
		RegistreAnnexEntity annex = registreAnnexRepository.findOne(annexId);
		FitxerDto arxiu = new FitxerDto();
		Document document = null;
		document = pluginHelper.arxiuDocumentConsultar(annex.getFitxerArxiuUuid(), null, true);
		if (document != null) {
			List<Firma> firmes = document.getFirmes();
			if (firmes != null && firmes.size() > 0) {
				
				Iterator<Firma> it = firmes.iterator();
				while (it.hasNext()) {
					Firma firma = it.next();
					if (firma.getTipus() == FirmaTipus.CSV) {
						it.remove();
					}
				}
				
				Firma firma = firmes.get(indexFirma);
				RegistreAnnexFirmaEntity firmaEntity = annex.getFirmes().get(indexFirma);
				if (firma != null && firmaEntity != null) {
					arxiu.setNom(firmaEntity.getFitxerNom());
					arxiu.setContentType(firmaEntity.getTipusMime());
					arxiu.setContingut(firma.getContingut());
					arxiu.setTamany(firma.getContingut().length);
				}
			}
		}
		return arxiu;
	}

	@Transactional(readOnly = true)
	@Override
	public List<RegistreAnnexDetallDto> getAnnexosAmbArxiu(
			Long entitatId,
			Long bustiaId,
			Long registreId) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaId=" + bustiaId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity bustia = entityComprovarHelper.comprovarContingut(
				entitat,
				bustiaId,
				null);
		if (bustia instanceof BustiaEntity) {
			entityComprovarHelper.comprovarBustia(
					entitat,
					bustiaId,
					true);
		} else {
			// Comprova l'accés al path del contenidor pare
			contingutHelper.comprovarPermisosPathContingut(
					bustia,
					true,
					false,
					false,
					true);
		}
		RegistreEntity registre = registreRepository.findByPareAndId(
				bustia,
				registreId);
		List<RegistreAnnexDetallDto> annexos = new ArrayList<RegistreAnnexDetallDto>();
		for (RegistreAnnexEntity annexEntity: registre.getAnnexos()) {
			RegistreAnnexDetallDto annex = conversioTipusHelper.convertir(
					annexEntity,
					RegistreAnnexDetallDto.class);
			if (annex.getFitxerArxiuUuid() != null && !annex.getFitxerArxiuUuid().isEmpty()) {
				Document document = pluginHelper.arxiuDocumentConsultar(
						annex.getFitxerArxiuUuid(),
						null,
						true);
				DocumentMetadades metadades = document.getMetadades();
				if (metadades != null) {
					annex.setFirmaCsv(metadades.getMetadadaAddicional("eni:csv") != null ? String.valueOf(metadades.getMetadadaAddicional("eni:csv")) : null);
					
				}
				annex.setAmbDocument(true);
				
				if (document.getFirmes() != null && document.getFirmes().size() > 0) {
					List<ArxiuFirmaDto> firmes = registreHelper.convertirFirmesAnnexToArxiuFirmaDto(annexEntity, null);
					Iterator<Firma> it = document.getFirmes().iterator();
					
					int firmaIndex = 0;
					while (it.hasNext()) {
						Firma arxiuFirma = it.next();
						if (!FirmaTipus.CSV.equals(arxiuFirma.getTipus())) {
							ArxiuFirmaDto firma = firmes.get(firmaIndex);
							if (pluginHelper.isValidaSignaturaPluginActiu()) {
								byte[] documentContingut = document.getContingut().getContingut();
								byte[] firmaContingut = arxiuFirma.getContingut();
								if (	ArxiuFirmaTipusEnumDto.XADES_DET.equals(firma.getTipus()) ||
										ArxiuFirmaTipusEnumDto.CADES_DET.equals(firma.getTipus())) {
									firmaContingut = arxiuFirma.getContingut();
								}
								firma.setDetalls(
										pluginHelper.validaSignaturaObtenirDetalls(
												documentContingut,
												firmaContingut));
							}
							firmaIndex++;
						} else {
							it.remove();
						}
					}
					annex.setFirmes(firmes);
					annex.setAmbFirma(true);
 				}
				
			}
			annexos.add(annex);
		}
		return annexos;
	}

	@Transactional(readOnly = true)
	@Override
	public RegistreAnnexDetallDto getAnnexAmbArxiu(
			Long entitatId,
			Long bustiaId,
			Long registreId,
			String fitxerArxiuUuid
			) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaId=" + bustiaId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity bustia = entityComprovarHelper.comprovarContingut(
				entitat,
				bustiaId,
				null);
		if (bustia instanceof BustiaEntity) {
			entityComprovarHelper.comprovarBustia(
					entitat,
					bustiaId,
					true);
		} else {
			// Comprova l'accés al path del contenidor pare
			contingutHelper.comprovarPermisosPathContingut(
					bustia,
					true,
					false,
					false,
					true);
		}
		RegistreEntity registre = registreRepository.findByPareAndId(
				bustia,
				registreId);
		RegistreAnnexEntity chosenAnnexEntity=null;
		for (RegistreAnnexEntity annexEntity: registre.getAnnexos()) {
			if (annexEntity.getFitxerArxiuUuid().equals(fitxerArxiuUuid)) {
				chosenAnnexEntity = annexEntity;
			}
		}
		RegistreAnnexDetallDto annex = conversioTipusHelper.convertir(
				chosenAnnexEntity,
				RegistreAnnexDetallDto.class);
		annex.setAmbDocument(true);
		return annex;
	}

	@Transactional(readOnly = true)
	@Override
	public RegistreAnnexDetallDto getAnnexFirmesAmbArxiu(
			Long entitatId,
			Long bustiaId,
			Long registreId,
			String fitxerArxiuUuid
			) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaId=" + bustiaId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity bustia = entityComprovarHelper.comprovarContingut(
				entitat,
				bustiaId,
				null);
		if (bustia instanceof BustiaEntity) {
			entityComprovarHelper.comprovarBustia(
					entitat,
					bustiaId,
					true);
		} else {
			// Comprova l'accés al path del contenidor pare
			contingutHelper.comprovarPermisosPathContingut(
					bustia,
					true,
					false,
					false,
					true);
		}
		RegistreEntity registre = registreRepository.findByPareAndId(
				bustia,
				registreId);
		RegistreAnnexEntity chosenAnnexEntity=null;
		for (RegistreAnnexEntity annexEntity: registre.getAnnexos()) {
			if (annexEntity.getFitxerArxiuUuid().equals(fitxerArxiuUuid)) {
				chosenAnnexEntity = annexEntity;
			}
		}
		RegistreAnnexDetallDto annex = conversioTipusHelper.convertir(
				chosenAnnexEntity,
				RegistreAnnexDetallDto.class);
		if (annex.getFitxerArxiuUuid() != null && !annex.getFitxerArxiuUuid().isEmpty()) {
			Document document = pluginHelper.arxiuDocumentConsultar(
					annex.getFitxerArxiuUuid(),
					null,
					true);
			annex.setAmbDocument(true);
			if (document.getFirmes() != null && document.getFirmes().size() > 0) {
				List<ArxiuFirmaDto> firmes = registreHelper.convertirFirmesAnnexToArxiuFirmaDto(chosenAnnexEntity, null);
				Iterator<Firma> it = document.getFirmes().iterator();
				int firmaIndex = 0;
				while (it.hasNext()) {
					Firma arxiuFirma = it.next();
					if (!FirmaTipus.CSV.equals(arxiuFirma.getTipus())) {
						ArxiuFirmaDto firma = firmes.get(firmaIndex);
						if (pluginHelper.isValidaSignaturaPluginActiu()) {
							byte[] documentContingut = document.getContingut().getContingut();
							byte[] firmaContingut = arxiuFirma.getContingut();
							if (	ArxiuFirmaTipusEnumDto.XADES_DET.equals(firma.getTipus()) ||
									ArxiuFirmaTipusEnumDto.CADES_DET.equals(firma.getTipus())) {
								firmaContingut = arxiuFirma.getContingut();
							}
							firma.setDetalls(
									pluginHelper.validaSignaturaObtenirDetalls(
											documentContingut,
											firmaContingut));
						}
						firmaIndex++;
					} else {
						it.remove();
					}
				}
				annex.setFirmes(firmes);
				annex.setAmbFirma(true);
			}
		}
		return annex;
	}

	@Transactional(readOnly = true)
	@Override
	public RegistreAnotacioDto findAmbIdentificador(String identificador) {
		RegistreAnotacioDto registreAnotacioDto;
		RegistreEntity registre = registreRepository.findByIdentificador(identificador);
		if (registre != null)
			registreAnotacioDto = (RegistreAnotacioDto) contingutHelper.toContingutDto(registre);
		else
			registreAnotacioDto = null;
		return registreAnotacioDto;
	}

	@Transactional
	@Override
	public void updateProces(
			Long registreId, 
			RegistreProcesEstatEnum procesEstat, 
			RegistreProcesEstatSistraEnum procesEstatSistra,
			String resultadoProcesamiento) {
		logger.debug("Actualitzar estat procés anotació de registre ("
				+ "registreId=" + registreId + ", "
				+ "procesEstat=" + procesEstat + ", "
				+ "procesEstatSistra=" + procesEstatSistra + ", "
				+ "resultadoProcesamiento=" + resultadoProcesamiento + ")");
		RegistreEntity registre = registreRepository.findOne(registreId);
		registre.updateProces(
				procesEstat,
				resultadoProcesamiento != null ? new Exception(resultadoProcesamiento) : null);
		registre.updateProcesSistra(procesEstatSistra);
	}

	@Transactional (readOnly = true)
	@Override
	public List<String> findPerBackofficeSistra(
			String identificadorProcediment, 
			String identificadorTramit,
			RegistreProcesEstatSistraEnum procesEstatSistra, 
			Date desdeDate, 
			Date finsDate) {
		logger.debug("Consultant els numeros d'entrada del registre pel backoffice Sistra ("
				+ "identificadorProcediment=" + identificadorProcediment + ", "
				+ "identificadorTramit=" + identificadorTramit + ", "
				+ "procesEstatSistra=" + procesEstatSistra + ", "
				+ "desdeDate=" + desdeDate + ", "
				+ "finsDate=" + finsDate + ")");
		return registreRepository.findPerBackofficeSistra(
				identificadorProcediment,
				identificadorTramit,
				procesEstatSistra == null,
				procesEstatSistra,
				desdeDate == null,
				desdeDate,
				finsDate == null,
				finsDate);
	}

	@Transactional
	@Override
	public RegistreAnotacioDto marcarLlegida(
			Long entitatId,
			Long bustiaId,
			Long registreId) {
		logger.debug("Marcan com a llegida l'anotació de registre ("
				+ "entitatId=" + entitatId + ", "
				+ "bustiaId=" + bustiaId + ", "
				+ "registreId=" + registreId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity bustia = entityComprovarHelper.comprovarContingut(
				entitat,
				bustiaId,
				null);
		if (bustia instanceof BustiaEntity) {
			entityComprovarHelper.comprovarBustia(
					entitat,
					bustiaId,
					true);
		} else {
			// Comprova l'accés al path del contenidor pare
			contingutHelper.comprovarPermisosPathContingut(
					bustia,
					true,
					false,
					false,
					true);
		}
		RegistreEntity registre = registreRepository.findByPareAndId(
					bustia,
					registreId);
		registre.updateLlegida(true);
		
		return (RegistreAnotacioDto) contingutHelper.toContingutDto(
				registre);		
	}

	@Transactional(readOnly = true)
	@Override
	public ArxiuDetallDto getArxiuDetall(Long registreAnotacioId) {
		logger.debug("Obtenint informació de l'arxiu per l'anotacio ("
				+ "registreAnotacioId=" + registreAnotacioId + ")");
		RegistreEntity registre = registreRepository.findOne(registreAnotacioId);
		ArxiuDetallDto arxiuDetall = null;
		if (registre.getExpedientArxiuUuid() != null) {
			arxiuDetall = new ArxiuDetallDto();
			es.caib.plugins.arxiu.api.Expedient arxiuExpedient = pluginHelper.arxiuExpedientInfo(registre.getExpedientArxiuUuid());
			List<ContingutArxiu> continguts = arxiuExpedient.getContinguts();
			arxiuDetall.setIdentificador(arxiuExpedient.getIdentificador());
			arxiuDetall.setNom(arxiuExpedient.getNom());
			ExpedientMetadades metadades = arxiuExpedient.getMetadades();
			if (metadades != null) {
				arxiuDetall.setEniVersio(metadades.getVersioNti());
				arxiuDetall.setEniIdentificador(metadades.getIdentificador());
				arxiuDetall.setSerieDocumental(metadades.getSerieDocumental());
				arxiuDetall.setEniDataObertura(metadades.getDataObertura());
				arxiuDetall.setEniClassificacio(metadades.getClassificacio());
				if (metadades.getEstat() != null) {
					switch (metadades.getEstat()) {
					case OBERT:
						arxiuDetall.setEniEstat(ExpedientEstatEnumDto.OBERT);
						break;
					case TANCAT:
						arxiuDetall.setEniEstat(ExpedientEstatEnumDto.TANCAT);
						break;
					case INDEX_REMISSIO:
						arxiuDetall.setEniEstat(ExpedientEstatEnumDto.INDEX_REMISSIO);
						break;
					}
				}
				arxiuDetall.setEniInteressats(metadades.getInteressats());
				arxiuDetall.setEniOrgans(metadades.getOrgans());
				arxiuDetall.setMetadadesAddicionals(metadades.getMetadadesAddicionals());
			}
			if (continguts != null) {
				List<ArxiuContingutDto> detallFills = new ArrayList<ArxiuContingutDto>();
				for (ContingutArxiu cont: continguts) {
					ArxiuContingutDto detallFill = new ArxiuContingutDto();
					detallFill.setIdentificador(
							cont.getIdentificador());
					detallFill.setNom(
							cont.getNom());
					if (cont.getTipus() != null) {
						switch (cont.getTipus()) {
						case EXPEDIENT:
							detallFill.setTipus(ArxiuContingutTipusEnumDto.EXPEDIENT);
							break;
						case DOCUMENT:
							detallFill.setTipus(ArxiuContingutTipusEnumDto.DOCUMENT);
							break;
						case CARPETA:
							detallFill.setTipus(ArxiuContingutTipusEnumDto.CARPETA);
							break;
						}
					}
					detallFills.add(detallFill);
				}
				arxiuDetall.setFills(detallFills);
			}
		}
		return arxiuDetall;
	}

	@Override
	@Transactional
	public ClassificacioResultatDto classificar(
			Long entitatId,
			Long contingutId,
			Long registreId,
			String procedimentCodi)
			throws NotFoundException {
		logger.debug("classificant l'anotació de registre (" +
				"entitatId=" + entitatId + ", " +
				"contingutId=" + contingutId + ", " +
				"registreId=" + registreId + ", " +
				"procedimentCodi=" + procedimentCodi + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		if (contingut instanceof BustiaEntity) {
			entityComprovarHelper.comprovarBustia(
					entitat,
					contingutId,
					true);
		} else {
			throw new ValidationException(
					contingutId,
					ContingutEntity.class,
					"El contingut especificat no és de tipus bústia");
		}
		RegistreEntity registre = registreRepository.findByPareAndId(
				contingut,
				registreId);
		registre.updateProcedimentCodi(procedimentCodi);
		BustiaEntity bustia = (BustiaEntity)contingut;
		ReglaEntity reglaAplicable = reglaHelper.findAplicable(
				entitat,
				bustia.getUnitatOrganitzativa().getCodi(),
				registre.getProcedimentCodi(),
				registre.getAssumpteCodi());
		ClassificacioResultatDto classificacioResultat = new ClassificacioResultatDto();
		if (reglaAplicable != null) {
			registre.updateRegla(reglaAplicable);
			bustiaHelper.evictCountElementsPendentsBustiesUsuari(
					entitat,
					bustia);
			Exception ex = reglaHelper.aplicarControlantException(registre);
			if (ex == null) {
				if (ReglaTipusEnumDto.BUSTIA.equals(reglaAplicable.getTipus())) {
					classificacioResultat.setResultat(ClassificacioResultatEnumDto.REGLA_BUSTIA);
					BustiaEntity novaBustia = (BustiaEntity)(registreRepository.getOne(registreId).getPare());
					classificacioResultat.setBustiaNom(novaBustia.getNom());
					classificacioResultat.setBustiaUnitatOrganitzativa(
							unitatOrganitzativaHelper.toDto(novaBustia.getUnitatOrganitzativa()));
				} else if (ReglaTipusEnumDto.BACKOFFICE.equals(reglaAplicable.getTipus())) {
					classificacioResultat.setResultat(ClassificacioResultatEnumDto.REGLA_BACKOFFICE);
				} else {
					classificacioResultat.setResultat(ClassificacioResultatEnumDto.SENSE_CANVIS);
				}
			} else {
				classificacioResultat.setResultat(ClassificacioResultatEnumDto.REGLA_ERROR);
			}
		} else {
			classificacioResultat.setResultat(ClassificacioResultatEnumDto.SENSE_CANVIS);
		}
		return classificacioResultat;
	}

	@Override
	@Transactional(readOnly = true)
	public List<ProcedimentDto> classificarFindProcediments(
			Long entitatId,
			Long bustiaId) {
		logger.debug("classificant l'anotació de registre (" +
				"entitatId=" + entitatId + ", " +
				"bustiaId=" + bustiaId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				bustiaId,
				null);
		if (contingut instanceof BustiaEntity) {
			entityComprovarHelper.comprovarBustia(
					entitat,
					bustiaId,
					true);
		} else {
			throw new ValidationException(
					bustiaId,
					ContingutEntity.class,
					"El contingut especificat no és de tipus bústia");
		}
		BustiaEntity bustia = (BustiaEntity)contingut;
		List<Procediment> procediments = pluginHelper.procedimentFindByCodiDir3(bustia.getUnitatCodi());
		List<ProcedimentDto> dtos = new ArrayList<ProcedimentDto>();
		if (procediments != null) {
			for (Procediment procediment: procediments) {
				if (procediment.getCodigoSIA() != null && !procediment.getCodigoSIA().isEmpty()) {
					ProcedimentDto dto = new ProcedimentDto();
					dto.setCodi(procediment.getCodigo());
					dto.setCodiSia(procediment.getCodigoSIA());
					dto.setNom(procediment.getNombre());
					dtos.add(dto);
				}
			}
		}
		return dtos;
	}

	private List<Annex> getAnnexosPerBackoffice(Long registreId) throws NotFoundException {
		logger.debug("Obtenint annexos per enviar al backoffice (" + "registreId=" + registreId + ")");
		RegistreEntity registre = registreRepository.findOne(registreId);
		List<Annex> annexosPerBackoffice = new ArrayList<Annex>(); 
		for (RegistreAnnexEntity annexEntity : registre.getAnnexos()) {
			Annex annexPerBackoffice = new Annex();
			annexPerBackoffice.setTitol(annexEntity.getTitol());
			annexPerBackoffice.setNom(annexEntity.getFitxerNom());
			annexPerBackoffice.setUuid(annexEntity.getFitxerArxiuUuid());
			annexPerBackoffice.setTamany(annexEntity.getFitxerTamany());
			annexPerBackoffice.setTipusMime(annexEntity.getFitxerTipusMime());
			annexPerBackoffice.setNtiTipoDocumental(toNtiTipoDocumento(annexEntity.getNtiTipusDocument()));
			annexPerBackoffice.setNtiOrigen(toNtiOrigen(annexEntity.getOrigenCiutadaAdmin()));
			annexPerBackoffice.setNtiFechaCaptura(annexEntity.getDataCaptura());
			annexPerBackoffice.setSicresTipoDocumento(toSicresTipoDocumento(annexEntity.getSicresTipusDocument()));
			annexPerBackoffice.setObservacions(annexEntity.getObservacions());
			annexPerBackoffice.setNtiEstadoElaboracio(NtiEstadoElaboracio.valueOf((annexEntity.getNtiElaboracioEstat().toString())));
			boolean retornarAnnexIFirmaContingut = PropertiesHelper.getProperties().getAsBoolean(
					"es.caib.distribucio.backoffice.integracio.retornarAnnexIFirmaContingut");
			// annex should be stored in arxiu
			if (annexEntity.getFitxerArxiuUuid() != null && !annexEntity.getFitxerArxiuUuid().isEmpty()) {
				Document document = pluginHelper.arxiuDocumentConsultar(
						annexEntity.getFitxerArxiuUuid(),
						null,
						retornarAnnexIFirmaContingut);

				if(retornarAnnexIFirmaContingut)
					annexPerBackoffice.setContingut(document.getContingut().getContingut());
				
				// if document is signed
				if (document.getFirmes() != null) {
					for (Firma firma : document.getFirmes()) {
						// we want to use first firma that is not CSV type
						if (!FirmaTipus.CSV.equals(firma.getTipus())) {

							boolean detached = FirmaTipus.XADES_DET.equals(firma.getTipus())
									|| FirmaTipus.CADES_DET.equals(firma.getTipus());
							if (detached && retornarAnnexIFirmaContingut) {
									annexPerBackoffice.setFirmaContingut(firma.getContingut());
									annexPerBackoffice.setFirmaTamany(firma.getContingut().length);
									annexPerBackoffice.setFirmaNom(firma.getFitxerNom());
									annexPerBackoffice.setFirmaTipusMime(firma.getTipusMime());
							}
							annexPerBackoffice.setFirmaTipus(
									firma.getTipus() != null ? es.caib.distribucio.core.api.service.ws.backoffice.FirmaTipus.valueOf(firma.getTipus().name()) : null);
							annexPerBackoffice.setFirmaPerfil(
									firma.getPerfil() != null ? es.caib.distribucio.core.api.service.ws.backoffice.FirmaPerfil.valueOf(firma.getPerfil().name()) : null);
							break;
						}
					}
				}
			} else {
				throw new RuntimeException("Error en la consulta de annexos per backofice. Annex " + annexEntity.getTitol() + "de registre " + registre.getIdentificador() + " no te uuid de arxiu");
			}
			annexosPerBackoffice.add(annexPerBackoffice);
		}
		return annexosPerBackoffice;
	}
	
	
	
						
	
	
	
	

	private NtiTipoDocumento toNtiTipoDocumento(RegistreAnnexNtiTipusDocumentEnum registreAnnexNtiTipusDocument){
		NtiTipoDocumento ntiTipoDocumento = null;
		
		if (registreAnnexNtiTipusDocument != null) {
			switch (registreAnnexNtiTipusDocument) {
			
			case RESOLUCIO:
				ntiTipoDocumento = NtiTipoDocumento.RESOLUCIO;
				break;
			case ACORD:
				ntiTipoDocumento = NtiTipoDocumento.ACORD;
				break;	
			case CONTRACTE:
				ntiTipoDocumento = NtiTipoDocumento.CONTRACTE;
				break;	
			case CONVENI:
				ntiTipoDocumento = NtiTipoDocumento.CONVENI;
				break;
			case DECLARACIO:
				ntiTipoDocumento = NtiTipoDocumento.DECLARACIO;
				break;
			case COMUNICACIO:
				ntiTipoDocumento = NtiTipoDocumento.COMUNICACIO;
				break;	
			case NOTIFICACIO:
				ntiTipoDocumento = NtiTipoDocumento.NOTIFICACIO;
				break;	
			case PUBLICACIO:
				ntiTipoDocumento = NtiTipoDocumento.PUBLICACIO;
				break;	
			case ACUS_REBUT:
				ntiTipoDocumento = NtiTipoDocumento.JUSTIFICANT_RECEPCIO;
				break;	
			case ACTE:
				ntiTipoDocumento = NtiTipoDocumento.ACTA;
				break;	
			case CERTIFICAT:
				ntiTipoDocumento = NtiTipoDocumento.CERTIFICAT;
				break;	
			case DILIGENCIA:
				ntiTipoDocumento = NtiTipoDocumento.DILIGENCIA;
				break;	
			case INFORME:
				ntiTipoDocumento = NtiTipoDocumento.INFORME;
				break;	
			case SOLICITUD:
				ntiTipoDocumento = NtiTipoDocumento.SOLICITUD;
				break;	
			case DENUNCIA:
				ntiTipoDocumento = NtiTipoDocumento.DENUNCIA;
				break;	
			case ALEGACIONS:
				ntiTipoDocumento = NtiTipoDocumento.ALEGACIO;
				break;	
			case RECURSOS:
				ntiTipoDocumento = NtiTipoDocumento.RECURS;
				break;	
			case COMUNICACIO_CIUTADA:
				ntiTipoDocumento = NtiTipoDocumento.COMUNICACIO_CIUTADA;
				break;	
			case FACTURA:
				ntiTipoDocumento = NtiTipoDocumento.FACTURA;
				break;							
			case ALTRES_INCAUTATS:
				ntiTipoDocumento = NtiTipoDocumento.ALTRES_INCAUTATS;
				break;	
			case ALTRES:
				ntiTipoDocumento = NtiTipoDocumento.ALTRES;
				break;	
			}
		}	
		return ntiTipoDocumento;
	}
	
	private NtiOrigen toNtiOrigen(RegistreAnnexOrigenEnum registreAnnexOrigenEnum){
		NtiOrigen ntiOrigen = null;
		
		if (registreAnnexOrigenEnum != null) {
			switch (registreAnnexOrigenEnum) {
			case CIUTADA:
				ntiOrigen = NtiOrigen.CIUTADA;
				break;
			case ADMINISTRACIO:
				ntiOrigen = NtiOrigen.ADMINISTRACIO;
				break;	
			}
		}	
		return ntiOrigen;
	}
	
	
	private SicresTipoDocumento toSicresTipoDocumento(RegistreAnnexSicresTipusDocumentEnum registreAnnexSicresTipusDocumentEnum) {
		SicresTipoDocumento sicresTipoDocumento = null;

		if (registreAnnexSicresTipusDocumentEnum != null) {
			switch (registreAnnexSicresTipusDocumentEnum) {
			case FORM:
				sicresTipoDocumento = SicresTipoDocumento.FORMULARI;
				break;
			case FORM_ADJUNT:
				sicresTipoDocumento = SicresTipoDocumento.ADJUNT;
				break;
			case INTERN:
				sicresTipoDocumento = SicresTipoDocumento.TECNIC_INTERN;
				break;
			}
		}
		return sicresTipoDocumento;
	}

	private List<Interessat> toInteressats(List<RegistreInteressatEntity> registreInteressats) {
		List<Interessat> interessatsPerBackoffice = new ArrayList<>();
		for (RegistreInteressatEntity registreInteressatEntity : registreInteressats) {
			Interessat interessatPerBackoffice = toInteressat(registreInteressatEntity);
			if (registreInteressatEntity.getRepresentant() != null) {
				Representant representant = toRepresentant(registreInteressatEntity.getRepresentant());
				interessatPerBackoffice.setRepresentant(representant);
			}
			interessatsPerBackoffice.add(interessatPerBackoffice);
		}
		return interessatsPerBackoffice;
	}

	private Interessat toInteressat(RegistreInteressatEntity registreInteressatEntity) {
		Interessat interessat = new Interessat();
		switch (registreInteressatEntity.getTipus()) {
		case PERSONA_FIS:
			interessat.setTipus(InteressatTipus.PERSONA_FISICA);
			break;
		case PERSONA_JUR:
			interessat.setTipus(InteressatTipus.PERSONA_JURIDICA);
			break;
		case ADMINISTRACIO:
			interessat.setTipus(InteressatTipus.ADMINISTRACIO);
			break;
		}
		switch (registreInteressatEntity.getDocumentTipus()) {
		case NIF:
			interessat.setDocumentTipus(DocumentTipus.NIF);
			break;
		case CIF:
			interessat.setDocumentTipus(DocumentTipus.CIF);
			break;
		case PASSAPORT:
			interessat.setDocumentTipus(DocumentTipus.PASSAPORT);
			break;
		case ESTRANGER:
			interessat.setDocumentTipus(DocumentTipus.NIE);
			break;
		case ALTRES:
			interessat.setDocumentTipus(DocumentTipus.ALTRES);
			break;
		case CODI_ORIGEN:
			interessat.setDocumentTipus(DocumentTipus.ALTRES);
			break;
		}
		interessat.setDocumentNumero(registreInteressatEntity.getDocumentNum());
		interessat.setRaoSocial(registreInteressatEntity.getRaoSocial());
		interessat.setNom(registreInteressatEntity.getNom());
		interessat.setLlinatge1(registreInteressatEntity.getLlinatge1());
		interessat.setLlinatge2(registreInteressatEntity.getLlinatge2());
		
		interessat.setPaisCodi(registreInteressatEntity.getPaisCodi());
		interessat.setProvinciaCodi(registreInteressatEntity.getProvinciaCodi());
		interessat.setMunicipiCodi(registreInteressatEntity.getMunicipiCodi());
		
		interessat.setPais(registreInteressatEntity.getPais());
		interessat.setProvincia(registreInteressatEntity.getProvincia());
		interessat.setMunicipi(registreInteressatEntity.getMunicipi());
		
		interessat.setAdresa(registreInteressatEntity.getAdresa());
		interessat.setCp(registreInteressatEntity.getCodiPostal());
		interessat.setEmail(registreInteressatEntity.getEmail());
		interessat.setTelefon(registreInteressatEntity.getTelefon());
		interessat.setAdresaElectronica(registreInteressatEntity.getEmail());
		interessat.setCanal(registreInteressatEntity.getCanalPreferent() != null ? registreInteressatEntity.getCanalPreferent().toString() : null);
		interessat.setObservacions(registreInteressatEntity.getObservacions());
		return interessat;
	}

	private Representant toRepresentant(RegistreInteressatEntity registreInteressatEntity) {
		Representant representant = new Representant();
		switch (registreInteressatEntity.getTipus()) {
		case PERSONA_FIS:
			representant.setTipus(InteressatTipus.PERSONA_FISICA);
			break;
		case PERSONA_JUR:
			representant.setTipus(InteressatTipus.PERSONA_JURIDICA);
			break;
		case ADMINISTRACIO:
			representant.setTipus(InteressatTipus.ADMINISTRACIO);
			break;
		}
		switch (registreInteressatEntity.getDocumentTipus()) {
		case NIF:
			representant.setDocumentTipus(DocumentTipus.NIF);
			break;
		case CIF:
			representant.setDocumentTipus(DocumentTipus.CIF);
			break;
		case PASSAPORT:
			representant.setDocumentTipus(DocumentTipus.PASSAPORT);
			break;
		case ESTRANGER:
			representant.setDocumentTipus(DocumentTipus.NIE);
			break;
		case ALTRES:
			representant.setDocumentTipus(DocumentTipus.ALTRES);
			break;
		case CODI_ORIGEN:
			representant.setDocumentTipus(DocumentTipus.ALTRES);
			break;
		}
		representant.setDocumentNumero(registreInteressatEntity.getDocumentNum());
		representant.setRaoSocial(registreInteressatEntity.getRaoSocial());
		representant.setNom(registreInteressatEntity.getNom());
		representant.setLlinatge1(registreInteressatEntity.getLlinatge1());
		representant.setLlinatge2(registreInteressatEntity.getLlinatge2());
		
		representant.setPaisCodi(registreInteressatEntity.getPaisCodi());
		representant.setProvinciaCodi(registreInteressatEntity.getProvinciaCodi());
		representant.setMunicipiCodi(registreInteressatEntity.getMunicipiCodi());
		
		representant.setPais(registreInteressatEntity.getPais());
		representant.setProvincia(registreInteressatEntity.getProvincia());
		representant.setMunicipi(registreInteressatEntity.getMunicipi());
		
		representant.setAdresa(registreInteressatEntity.getAdresa());
		representant.setCp(registreInteressatEntity.getCodiPostal());
		representant.setEmail(registreInteressatEntity.getEmail());
		representant.setTelefon(registreInteressatEntity.getTelefon());
		representant.setAdresaElectronica(registreInteressatEntity.getEmail());
		representant.setCanal(registreInteressatEntity.getCanalPreferent().toString());
		representant.setObservacions(registreInteressatEntity.getObservacions());
		return representant;
	}

	private Exception processarAnotacioPendent(RegistreEntity anotacio) {
		boolean pendentArxiu = RegistreProcesEstatEnum.ARXIU_PENDENT.equals(
				anotacio.getProcesEstat());
		boolean pendentRegla = RegistreProcesEstatEnum.REGLA_PENDENT.equals(
				anotacio.getProcesEstat());
		Exception exceptionProcessant = null;
		if (pendentArxiu || pendentRegla) {
			if (pendentArxiu) {
				exceptionProcessant = registreHelper.processarAnotacioPendentArxiu(
						anotacio.getId());
			}
			if (exceptionProcessant == null && pendentRegla) {
				exceptionProcessant = registreHelper.processarAnotacioPendentRegla(
						anotacio.getId());
			}
		} else {
			throw new ValidationException(
					anotacio.getId(),
					RegistreEntity.class,
					"L'anotació de registre no es troba en estat pendent");
		}
		return exceptionProcessant;
	}

	private RegistreAnnexDetallDto getJustificantPerRegistre(
			EntitatEntity entitat,
			ContingutEntity registre,
			String justificantUuid,
			boolean ambContingut) throws NotFoundException {
		logger.debug("Obtenint anotació de registre ("
				+ "entitatId=" + entitat.getId() + ", "
				+ "registreId=" + registre.getId() + ", "
				+ "justificantUuid=" + justificantUuid + ")");
		RegistreAnnexDetallDto annex = new RegistreAnnexDetallDto();
		Document document = pluginHelper.arxiuDocumentConsultar(justificantUuid, null, ambContingut);
		annex.setFitxerNom(obtenirJustificantNom(document));
		annex.setFitxerTamany(document.getContingut().getContingut().length);
		annex.setFitxerTipusMime(document.getContingut().getTipusMime());
		annex.setTitol(document.getNom());
		DocumentMetadades metadades = document.getMetadades();
		if (metadades != null) {
			annex.setDataCaptura(metadades.getDataCaptura());
			annex.setOrigenCiutadaAdmin(metadades.getOrigen().name());
			annex.setNtiElaboracioEstat(metadades.getEstatElaboracio().name());
			annex.setNtiTipusDocument(metadades.getTipusDocumental().name());
			annex.setFirmaCsv(metadades.getMetadadaAddicional("eni:csv") != null ? String.valueOf(metadades.getMetadadaAddicional("eni:csv")) : null);
		}
		annex.setAmbDocument(true);
		return annex;
	}

	private String obtenirJustificantNom(Document document) {
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

	private static final Logger logger = LoggerFactory.getLogger(RegistreServiceImpl.class);

}
