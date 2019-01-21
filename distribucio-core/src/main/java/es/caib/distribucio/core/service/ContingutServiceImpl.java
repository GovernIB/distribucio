/**
 * 
 */
package es.caib.distribucio.core.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.AnotacioRegistreFiltreDto;
import es.caib.distribucio.core.api.dto.ContingutComentariDto;
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.ContingutFiltreDto;
import es.caib.distribucio.core.api.dto.ContingutLogDetallsDto;
import es.caib.distribucio.core.api.dto.ContingutLogDto;
import es.caib.distribucio.core.api.dto.ContingutMovimentDto;
import es.caib.distribucio.core.api.dto.ContingutTipusEnumDto;
import es.caib.distribucio.core.api.dto.LogTipusEnumDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.RegistreAnotacioDto;
import es.caib.distribucio.core.api.service.ContingutService;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.ContingutComentariEntity;
import es.caib.distribucio.core.entity.ContingutEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.helper.BustiaHelper;
import es.caib.distribucio.core.helper.CacheHelper;
import es.caib.distribucio.core.helper.ContingutHelper;
import es.caib.distribucio.core.helper.ContingutLogHelper;
import es.caib.distribucio.core.helper.ConversioTipusHelper;
import es.caib.distribucio.core.helper.EntityComprovarHelper;
import es.caib.distribucio.core.helper.PaginacioHelper;
import es.caib.distribucio.core.helper.PaginacioHelper.Converter;
import es.caib.distribucio.core.helper.PluginHelper;
import es.caib.distribucio.core.helper.PropertiesHelper;
import es.caib.distribucio.core.helper.UsuariHelper;
import es.caib.distribucio.core.repository.AlertaRepository;
import es.caib.distribucio.core.repository.ContingutComentariRepository;
import es.caib.distribucio.core.repository.ContingutRepository;
import es.caib.distribucio.core.repository.RegistreRepository;
import es.caib.distribucio.core.repository.UsuariRepository;

/**
 * Implementació dels mètodes per a gestionar continguts.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ContingutServiceImpl implements ContingutService {

	@Resource
	private UsuariRepository usuariRepository;
	@Resource
	private ContingutRepository contingutRepository;
	@Resource
	private ContingutComentariRepository contingutComentariRepository;
	@Resource
	private AlertaRepository alertaRepository;
	@Resource
	private RegistreRepository registreRepository;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	PaginacioHelper paginacioHelper;
	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private ContingutHelper contingutHelper;
	@Resource
	private ContingutLogHelper contingutLogHelper;
	@Resource
	private UsuariHelper usuariHelper;
	@Resource
	private PluginHelper pluginHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private BustiaHelper bustiaHelper;



//	@Transactional
//	@Override
//	public ContingutDto rename(
//			Long entitatId,
//			Long contingutId,
//			String nom) {
//		logger.debug("Canviant el nom del contingut ("
//				+ "entitatId=" + entitatId + ", "
//				+ "contingutId=" + contingutId + ", "
//				+ "nom=" + nom + ")");
//		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
//				entitatId,
//				true,
//				false,
//				false);
//		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
//				entitat,
//				contingutId,
//				null);
//		// Comprova que el contingut arrel és l'escriptori de l'usuari actual
//		contingutHelper.comprovarContingutArrelEsEscriptoriUsuariActual(
//				entitat,
//				contingut);
//		// Comprova l'accés al path del contingut
//		contingutHelper.comprovarPermisosPathContingut(
//				contingut,
//				false,
//				false,
//				true,
//				true);
//		if (!contingutHelper.isNomValid(nom)) {
//			throw new ValidationException(
//					contingutId,
//					ContingutEntity.class,
//					"El nom del contingut no és vàlid (no pot començar amb \".\")");
//		}
//		// Canvia el nom del contingut
//		contingut.update(nom);
//		// Registra al log la modificació del contingut
//		contingutLogHelper.log(
//				contingut,
//				LogTipusEnumDto.MODIFICACIO,
//				nom,
//				null,
//				true,
//				true);
//		return contingutHelper.toContingutDto(
//				contingut,
//				true,
//				true,
//				true,
//				false,
//				false,
//				false,
//				false);
//	}
//
	
//
//	@Transactional
//	@Override
//	@CacheEvict(value = "errorsValidacioNode", key = "#contingutId")
//	public ContingutDto deleteDefinitiu(
//			Long entitatId,
//			Long contingutId) {
//		logger.debug("Esborrant el contingut ("
//				+ "entitatId=" + entitatId + ", "
//				+ "contingutId=" + contingutId + ")");
//		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
//				entitatId,
//				false,
//				true,
//				false);
//		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
//				entitat,
//				contingutId,
//				null);
//		// Esborra definitivament el contingut
//		ContingutDto dto = contingutHelper.toContingutDto(
//				contingut,
//				true,
//				false,
//				false,
//				false,
//				false,
//				false,
//				false);
//		if (contingut.getPare() != null) {
//			contingut.getPare().getFills().remove(contingut);
//		}
//		contingutRepository.delete(contingut);
//		// Propaga l'acció a l'arxiu
//		ExpedientEntity expedientSuperior = contingutHelper.getExpedientSuperior(
//				contingut,
//				false,
//				false,
//				false);
//		contingutHelper.arxiuPropagarEliminacio(
//				contingut,
//				expedientSuperior);
//		// Registra al log l'eliminació definitiva del contingut
//		contingutLogHelper.log(
//				contingut,
//				LogTipusEnumDto.ELIMINACIODEF,
//				null,
//				null,
//				true,
//				true);
//		return dto;
//	}
//
//	@Transactional
//	@Override
//	public ContingutDto undelete(
//			Long entitatId,
//			Long contingutId) throws IOException {
//		logger.debug("Recuperant el contingut ("
//				+ "entitatId=" + entitatId + ", "
//				+ "contingutId=" + contingutId + ")");
//		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
//				entitatId,
//				false,
//				true,
//				false);
//		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
//				entitat,
//				contingutId,
//				null);
//		// No es comproven permisos perquè això només ho pot fer l'administrador
//		if (contingut.getEsborrat() == 0) {
//			logger.error("Aquest contingut no està esborrat (contingutId=" + contingutId + ")");
//			throw new ValidationException(
//					contingutId,
//					ContingutEntity.class,
//					"Aquest contingut no està esborrat");
//		}
//		if (contingut.getPare() == null) {
//			logger.error("Aquest contingut no te pare (contingutId=" + contingutId + ")");
//			throw new ValidationException(
//					contingutId,
//					ContingutEntity.class,
//					"Aquest contingut no te pare");
//		}
//		boolean nomDuplicat = contingutRepository.findByPareAndNomAndEsborrat(
//				contingut.getPare(),
//				contingut.getNom(),
//				0) != null;
//		if (nomDuplicat) {
//			throw new ValidationException(
//					contingutId,
//					ContingutEntity.class,
//					"Ja existeix un altre contingut amb el mateix nom dins el mateix pare");
//		}
//		// Recupera el contingut esborrat
//		contingut.updateEsborrat(0);
//		ContingutDto dto = contingutHelper.toContingutDto(
//				contingut,
//				true,
//				false,
//				false,
//				false,
//				false,
//				false,
//				false);
//		// Registra al log la recuperació del contingut
//		contingutLogHelper.log(
//				contingut,
//				LogTipusEnumDto.RECUPERACIO,
//				null,
//				null,
//				true,
//				true);
//		// Propaga l'acció a l'arxiu
//		ExpedientEntity expedientSuperior = contingutHelper.getExpedientSuperior(
//				contingut,
//				false,
//				false,
//				false);
//		FitxerDto fitxer = null;
//		if (contingut instanceof DocumentEntity) {
//			DocumentEntity document = (DocumentEntity)contingut;
//			if (DocumentTipusEnumDto.DIGITAL.equals(document.getDocumentTipus())) {
//				fitxer = fitxerDocumentEsborratLlegir((DocumentEntity)contingut);
//			}
//		}
//		contingutHelper.arxiuPropagarModificacio(
//				contingut,
//				expedientSuperior,
//				fitxer);
//		if (fitxer != null) {
//			fitxerDocumentEsborratEsborrar((DocumentEntity)contingut);
//		}
//		return dto;
//	}
//
//	@Transactional
//	@Override
//	public ContingutDto move(
//			Long entitatId,
//			Long contingutOrigenId,
//			Long contingutDestiId) {
//		logger.debug("Movent el contingut ("
//				+ "entitatId=" + entitatId + ", "
//				+ "contingutOrigenId=" + contingutOrigenId + ", "
//				+ "contingutDestiId=" + contingutDestiId + ")");
//		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
//				entitatId,
//				true,
//				false,
//				false);
//		ContingutEntity contingutOrigen = entityComprovarHelper.comprovarContingut(
//				entitat,
//				contingutOrigenId,
//				null);
//		if (!(contingutOrigen instanceof DocumentEntity) && !(contingutOrigen instanceof CarpetaEntity)) {
//			throw new ValidationException(
//					contingutOrigenId,
//					contingutOrigen.getClass(),
//					"Només es poden moure documents i carpetes");
//		}
//		// Comprova que el contingutOrigen arrel és l'escriptori de l'usuari actual
//		contingutHelper.comprovarContingutArrelEsEscriptoriUsuariActual(
//				entitat,
//				contingutOrigen);
//		// Comprova l'accés al path del contingutOrigen
//		contingutHelper.comprovarPermisosPathContingut(
//				contingutOrigen,
//				true,
//				false,
//				false,
//				true);
//		// Comprova que el contingutDesti arrel és l'escriptori de l'usuari actual
//		ContingutEntity contingutDesti = entityComprovarHelper.comprovarContingut(
//				entitat,
//				contingutDestiId,
//				null);
//		contingutHelper.comprovarContingutArrelEsEscriptoriUsuariActual(
//				entitat,
//				contingutDesti);
//		// Comprova l'accés al path del contingutDesti
//		contingutHelper.comprovarPermisosPathContingut(
//				contingutDesti,
//				false,
//				true,
//				false,
//				true);
//		// Comprova que el nom no sigui duplicat
//		boolean nomDuplicat = contingutRepository.findByPareAndNomAndEsborrat(
//				contingutDesti,
//				contingutOrigen.getNom(),
//				0) != null;
//		if (nomDuplicat) {
//			throw new ValidationException(
//					contingutOrigenId,
//					ContingutEntity.class,
//					"Ja existeix un altre contingut amb el mateix nom dins el contingut destí ("
//							+ "contingutDestiId=" + contingutDestiId + ")");
//		}
//		// Comprova que la sèrie documental sigui la mateixa
//		// TODO comprovació sèrie documental
//		// Realitza el moviment del contingut
//		ContingutMovimentEntity contingutMoviment = contingutHelper.ferIEnregistrarMoviment(
//				contingutOrigen,
//				contingutDesti,
//				null);
//		contingutLogHelper.log(
//				contingutOrigen,
//				LogTipusEnumDto.MOVIMENT,
//				contingutMoviment,
//				true,
//				true);
//		ContingutDto dto = contingutHelper.toContingutDto(
//				contingutOrigen,
//				true,
//				false,
//				false,
//				false,
//				false,
//				false,
//				false);
//		contingutHelper.arxiuPropagarMoviment(
//				contingutOrigen,
//				contingutDesti);
//		return dto;
//	}
//
//	@Transactional
//	@Override
//	public ContingutDto copy(
//			Long entitatId,
//			Long contingutOrigenId,
//			Long contingutDestiId,
//			boolean recursiu) {
//		logger.debug("Copiant el contingut ("
//				+ "entitatId=" + entitatId + ", "
//				+ "contingutOrigenId=" + contingutOrigenId + ", "
//				+ "contingutDestiId=" + contingutDestiId + ", "
//				+ "recursiu=" + recursiu + ")");
//		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
//				entitatId,
//				true,
//				false,
//				false);
//		ContingutEntity contingutOrigen = entityComprovarHelper.comprovarContingut(
//				entitat,
//				contingutOrigenId,
//				null);
//		if (!(contingutOrigen instanceof DocumentEntity) && !(contingutOrigen instanceof CarpetaEntity)) {
//			throw new ValidationException(
//					contingutOrigenId,
//					contingutOrigen.getClass(),
//					"Només es poden copiar documents i carpetes");
//		}
//		// Comprova que el contingutOrigen arrel és l'escriptori de l'usuari actual
//		contingutHelper.comprovarContingutArrelEsEscriptoriUsuariActual(
//				entitat,
//				contingutOrigen);
//		// Comprova l'accés al path del contingutOrigen
//		contingutHelper.comprovarPermisosPathContingut(
//				contingutOrigen,
//				true,
//				false,
//				false,
//				true);
//		ContingutEntity contingutDesti = entityComprovarHelper.comprovarContingut(
//				entitat,
//				contingutDestiId,
//				null);
//		// Comprova que el contingutDesti arrel és l'escriptori de l'usuari actual
//		contingutHelper.comprovarContingutArrelEsEscriptoriUsuariActual(
//				entitat,
//				contingutDesti);
//		// Comprova l'accés al path del contingutDesti
//		contingutHelper.comprovarPermisosPathContingut(
//				contingutDesti,
//				false,
//				true,
//				false,
//				true);
//		// Comprova que el nom no sigui duplicat
//		boolean nomDuplicat = contingutRepository.findByPareAndNomAndEsborrat(
//				contingutDesti,
//				contingutOrigen.getNom(),
//				0) != null;
//		if (nomDuplicat) {
//			throw new ValidationException(
//					contingutOrigenId,
//					ContingutEntity.class,
//					"Ja existeix un altre contingut amb el mateix nom dins el contingut destí ("
//							+ "contingutDestiId=" + contingutDestiId + ")");
//		}
//		// Comprova que la sèrie documental sigui la mateixa
//		// TODO comprovació sèrie documental
//		// Realitza la còpia del contingut
//		ContingutEntity contingutCopia = copiarContingut(
//				entitat,
//				contingutOrigen,
//				contingutDesti,
//				recursiu);
//		contingutLogHelper.log(
//				contingutCopia,
//				LogTipusEnumDto.COPIA,
//				null,
//				null,
//				true,
//				true);
//		ContingutDto dto = contingutHelper.toContingutDto(
//				contingutOrigen,
//				true,
//				false,
//				false,
//				false,
//				false,
//				false,
//				false);
//		contingutHelper.arxiuPropagarCopia(
//				contingutOrigen,
//				contingutDesti);
//		return dto;
//	}

	@Transactional(readOnly = true)
	@Override
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions) {
		logger.debug("Obtenint contingut amb id per usuari ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ", "
				+ "ambFills=" + ambFills + ", "
				+ "ambVersions=" + ambVersions + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		contingutHelper.comprovarPermisosPathContingut(
				contingut,
				true,
				false,
				false,
				true);
		ContingutDto result = contingutHelper.toContingutDto(
				contingut,
				true,
				ambFills,
				ambFills,
				true,
				true,
				false,
				ambVersions);
		
		
		result.setAlerta(alertaRepository.countByLlegidaAndContingutId(
				false,
				result.getId()) > 0);
				
		List<ContingutEntity> continguts = contingutRepository.findRegistresByPareId(result.getId());
		if(!continguts.isEmpty() && alertaRepository.countByLlegidaAndContinguts(
				false,
				continguts
				) > 0) result.setAlerta(true);
		
		return result;
	}

	@Transactional(readOnly = true)
	@Override
	public ContingutDto findAmbIdAdmin(
			Long entitatId,
			Long contingutId,
			boolean ambFills) {
		logger.debug("Obtenint contingut amb id per admin ("
				+ "entitatId=" + entitatId + ", "
				+ "contingutId=" + contingutId + ", "
				+ "ambFills=" + ambFills + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		return contingutHelper.toContingutDto(
				contingut,
				true,
				ambFills,
				ambFills,
				true,
				true,
				false,
				true);
	}

//	@Transactional(readOnly = true)
//	@Override
//	public ContingutDto getContingutAmbFillsPerPath(
//			Long entitatId,
//			String path) {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		logger.debug("Obtenint contingut amb fills donat el seu path ("
//				+ "entitatId=" + entitatId + ", "
//				+ "path=" + path + ", "
//				+ "usuariCodi=" + auth.getName() + ")");
//		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
//				entitatId,
//				true,
//				false,
//				false);
//		EscriptoriEntity escriptori = escriptoriRepository.findByEntitatAndUsuari(
//				entitat,
//				usuariHelper.getUsuariAutenticat());
//		ContingutEntity contingutActual = escriptori;
//		if (!path.isEmpty() && !path.equals("/")) {
//			String[] pathParts;
//			if (path.startsWith("/")) {
//				pathParts = path.substring(1).split("/");
//			} else {
//				pathParts = path.split("/");
//			}
//			for (String pathPart: pathParts) {
//				Long idActual = contingutActual.getId();
//				contingutActual = contingutRepository.findByPareAndNomAndEsborrat(
//						contingutActual,
//						pathPart,
//						0);
//				if (contingutActual == null) {
//					logger.error("No s'ha trobat el contingut (pareId=" + idActual + ", nom=" + pathPart + ")");
//					throw new NotFoundException(
//							"(pareId=" + idActual + ", nom=" + pathPart + ")",
//							ContingutEntity.class);
//				}
//				// Si el contingut actual és un document ens aturam
//				// perquè el següent element del path serà la darrera
//				// versió i no la trobaría com a contingut.
//				if (contingutActual instanceof DocumentEntity)
//					break;
//			}
//		}
//		// Comprova que el contingut arrel és l'escriptori de l'usuari actual
//		contingutHelper.comprovarContingutArrelEsEscriptoriUsuariActual(
//				entitat,
//				contingutActual);
//		// Comprova l'accés al path del contingut
//		contingutHelper.comprovarPermisosPathContingut(
//				contingutActual,
//				true,
//				false,
//				false,
//				true);
//		return contingutHelper.toContingutDto(
//				contingutActual,
//				true,
//				true,
//				true,
//				true,
//				true,
//				false,
//				true);
//	}
//
//	@Transactional(readOnly = true)
//	@Override
//	public List<ValidacioErrorDto> findErrorsValidacio(
//			Long entitatId,
//			Long contingutId) {
//		logger.debug("Obtenint errors de validació del contingut ("
//				+ "entitatId=" + entitatId + ", "
//				+ "contingutId=" + contingutId + ")");
//		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
//				entitatId,
//				true,
//				false,
//				false);
//		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
//				entitat,
//				contingutId,
//				null);
//		// Comprova l'accés al path del contingut
//		contingutHelper.comprovarPermisosPathContingut(
//				contingut,
//				true,
//				false,
//				false,
//				true);
//		if (contingut instanceof NodeEntity) {
//			NodeEntity node = (NodeEntity)contingut;
//			return cacheHelper.findErrorsValidacioPerNode(node);
//		} else {
//			logger.error("El contingut no és cap node (contingutId=" + contingutId + ")");
//			throw new ValidationException(
//					contingutId,
//					ContingutEntity.class,
//					"El contingut no és un node");
//		}
//	}

	@Transactional(readOnly = true)
	@Override
	public List<ContingutLogDto> findLogsPerContingutAdmin(
			Long entitatId,
			Long contingutId) {
		logger.debug("Obtenint registre d'accions pel contingut usuari admin ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + contingutId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		return contingutLogHelper.findLogsContingut(contingut);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ContingutLogDto> findLogsPerContingutUser(
			Long entitatId,
			Long contingutId) {
		logger.debug("Obtenint registre d'accions pel contingut usuari normal ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + contingutId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		// Comprova que l'usuari tengui accés al contingut
		contingutHelper.comprovarPermisosPathContingut(
				contingut,
				false,
				false,
				false,
				true);
		return contingutLogHelper.findLogsContingut(contingut);
	}

//	@Transactional(readOnly = true)
//	@Override
//	public ContingutLogDetallsDto findLogDetallsPerContingutAdmin(
//			Long entitatId,
//			Long contingutId,
//			Long contingutLogId) {
//		logger.debug("Obtenint registre d'accions pel contingut usuari normal ("
//				+ "entitatId=" + entitatId + ", "
//				+ "nodeId=" + contingutId + ")");
//		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
//				entitatId,
//				false,
//				false,
//				true);
//		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
//				entitat,
//				contingutId,
//				null);
//		return contingutLogHelper.findLogDetalls(
//				contingut,
//				contingutLogId);
//	}

	@Transactional(readOnly = true)
	@Override
	public ContingutLogDetallsDto findLogDetallsPerContingutUser(
			Long entitatId,
			Long contingutId,
			Long contingutLogId) {
		logger.debug("Obtenint registre d'accions pel contingut usuari normal ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + contingutId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		// Comprova que l'usuari tengui accés al contingut
		contingutHelper.comprovarPermisosPathContingut(
				contingut,
				false,
				false,
				false,
				true);
		return contingutLogHelper.findLogDetalls(
				contingut,
				contingutLogId);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ContingutMovimentDto> findMovimentsPerContingutAdmin(
			Long entitatId,
			Long contingutId) {
		logger.debug("Obtenint registre de moviments pel contingut usuari admin ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + contingutId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		return contingutLogHelper.findMovimentsContingut(contingut);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ContingutMovimentDto> findMovimentsPerContingutUser(
			Long entitatId,
			Long contingutId) {
		logger.debug("Obtenint registre de moviments pel contingut usuari normal ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + contingutId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		// Comprova que l'usuari tengui accés al contingut
		contingutHelper.comprovarPermisosPathContingut(
				contingut,
				false,
				false,
				false,
				true);
		return contingutLogHelper.findMovimentsContingut(contingut);
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<ContingutDto> findAdmin(
			Long entitatId,
			ContingutFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta de continguts per usuari admin ("
				+ "entitatId=" + entitatId + ", "
				+ "filtre=" + filtre + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		boolean tipusBustia = true;
		boolean tipusRegistre = true;
		if (filtre.getTipus() != null) {
			tipusBustia = false;
			tipusRegistre = false;
			switch (filtre.getTipus()) {
			case BUSTIA:
				tipusBustia = true;
				break;
			case REGISTRE:
				tipusRegistre = true;
				break;
			}
		}
		Date dataInici = filtre.getDataCreacioInici();
		if (dataInici != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataInici);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			dataInici = cal.getTime();
		}
		Date dataFi = filtre.getDataCreacioFi();
		if (dataFi != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataFi);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			dataFi = cal.getTime();
		}
		return paginacioHelper.toPaginaDto(
				contingutRepository.findByFiltrePaginat(
						entitat,
						tipusBustia,
						tipusRegistre,
						(filtre.getNom() == null),
						filtre.getNom(),
						(dataInici == null),
						dataInici,
						(dataFi == null),
						dataFi,
						filtre.isMostrarEsborrats(),
						filtre.isMostrarNoEsborrats(),
						paginacioHelper.toSpringDataPageable(paginacioParams)),
				ContingutDto.class,
				new Converter<ContingutEntity, ContingutDto>() {
					@Override
					public ContingutDto convert(ContingutEntity source) {
						return contingutHelper.toContingutDto(
								source,
								false,
								false,
								false,
								false,
								true,
								false,
								false);
					}
				});
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<RegistreAnotacioDto> findAnotacionsRegistre(
			Long entitatId,
			AnotacioRegistreFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta d'anotacions de registre per usuari admin (" +
				"entitatId=" + entitatId + ", " +
				"filtre=" + filtre + ", " +
				"paginacioParams=" + paginacioParams + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		Date dataInici = filtre.getDataCreacioInici();
		if (dataInici != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataInici);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			dataInici = cal.getTime();
		}
		Date dataFi = filtre.getDataCreacioFi();
		if (dataFi != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataFi);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			dataFi = cal.getTime();
		}
		return paginacioHelper.toPaginaDto(
				registreRepository.findByFiltrePaginat(
						entitat, 
						(filtre.getNom() == null || filtre.getNom().isEmpty()),
						filtre.getNom(),
						(filtre.getNumeroOrigen() == null) || filtre.getNumeroOrigen().isEmpty(),
						filtre.getNumeroOrigen(),
						(filtre.getUnitatOrganitzativa() == null),
						filtre.getUnitatOrganitzativa(),
						(filtre.getBustia() == null),
						(filtre.getBustia() != null ? Long.parseLong(filtre.getBustia()) : null),
						(dataInici == null),
						dataInici,
						(dataFi == null),
						dataFi,
						(filtre.getEstat() == null),
						filtre.getEstat(),
						paginacioHelper.toSpringDataPageable(paginacioParams)),
				RegistreAnotacioDto.class,
				new Converter<RegistreEntity, RegistreAnotacioDto>() {
					@Override
					public RegistreAnotacioDto convert(RegistreEntity source) {
						return (RegistreAnotacioDto)contingutHelper.toContingutDto(
								source,
								false,
								false,
								false,
								false,
								true,
								false,
								false);
					}
				});
	}

//	@Transactional(readOnly = true)
//	@Override
//	public PaginaDto<ContingutDto> findEsborrats(
//			Long entitatId,
//			String nom,
//			String usuariCodi,
//			Date dataInici,
//			Date dataFi,
//			PaginacioParamsDto paginacioParams) {
//		logger.debug("Obtenint elements esborrats ("
//				+ "entitatId=" + entitatId + ", "
//				+ "nom=" + nom + ", "
//				+ "usuariCodi=" + usuariCodi + ", "
//				+ "dataInici=" + dataInici + ", "
//				+ "dataFi=" + dataFi + ")");
//		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
//				entitatId,
//				false,
//				true,
//				false);
//		UsuariEntity usuari = null;
//		if (usuariCodi != null && !usuariCodi.isEmpty()) {
//			usuari = usuariRepository.findOne(usuariCodi);
//			if (usuari == null) {
//				logger.error("No s'ha trobat l'usuari (codi=" + usuariCodi + ")");
//				throw new NotFoundException(
//						usuariCodi,
//						UsuariEntity.class);
//			}
//		}
//		if (dataInici != null) {
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(dataInici);
//			cal.set(Calendar.HOUR_OF_DAY, 0);
//			cal.set(Calendar.MINUTE, 0);
//			cal.set(Calendar.SECOND, 0);
//			cal.set(Calendar.MILLISECOND, 0);
//			dataInici = cal.getTime();
//		}
//		if (dataFi != null) {
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(dataFi);
//			cal.set(Calendar.HOUR_OF_DAY, 23);
//			cal.set(Calendar.MINUTE, 59);
//			cal.set(Calendar.SECOND, 59);
//			cal.set(Calendar.MILLISECOND, 999);
//			dataFi = cal.getTime();
//		}
//		return paginacioHelper.toPaginaDto(
//				contingutRepository.findEsborratsByFiltrePaginat(
//						entitat,
//						(nom == null),
//						(nom != null) ? '%' + nom + '%' : nom,
//						(usuari == null),
//						usuari,
//						(dataInici == null),
//						dataInici,
//						(dataFi == null),
//						dataFi,
//						paginacioHelper.toSpringDataPageable(paginacioParams)),
//				ContingutDto.class,
//				new Converter<ContingutEntity, ContingutDto>() {
//					@Override
//					public ContingutDto convert(ContingutEntity source) {
//						return contingutHelper.toContingutDto(
//								source,
//								false,
//								false,
//								false,
//								false,
//								false,
//								false,
//								false);
//					}
//				});
//	}
//

	@Transactional(readOnly = true)
	@Override
	public List<ContingutComentariDto> findComentarisPerContingut(
			Long entitatId,
			Long contingutId) {
		logger.debug("Obtenint els comentaris pel contingut de bustia ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + contingutId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		// Comprova que l'usuari tengui accés al contingut
		contingutHelper.comprovarPermisosPathContingut(
				contingut,
				false,
				false,
				false,
				true);
		return conversioTipusHelper.convertirList(
				contingutComentariRepository.findByContingutOrderByCreatedDateAsc(contingut), 
				ContingutComentariDto.class);
	}

	@Transactional
	@Override
	public boolean publicarComentariPerContingut(
			Long entitatId,
			Long contingutId,
			String text) {
		logger.debug("Obtenint els comentaris pel contingut de bustia ("
				+ "entitatId=" + entitatId + ", "
				+ "nodeId=" + contingutId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		// Comprova que l'usuari tengui accés al contingut
		contingutHelper.comprovarPermisosPathContingut(
				contingut,
				false,
				false,
				false,
				true);
		//truncam a 1024 caracters
		if (text.length() > 1024)
			text = text.substring(0, 1024);
		ContingutComentariEntity comentari = ContingutComentariEntity.getBuilder(
				contingut, 
				text).build();
		contingutComentariRepository.save(comentari);
		return true;
	}

	@Transactional
	@Override
	public boolean marcarProcessat(
			Long entitatId,
			Long contingutId,
			String text) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				true);
		ContingutEntity contingut = entityComprovarHelper.comprovarContingut(
				entitat,
				contingutId,
				null);
		RegistreEntity registre = null;
		long registresAmbMateixUuid = 0;
		if (ContingutTipusEnumDto.REGISTRE == contingut.getTipus()) {
			registre = (RegistreEntity)contingut;
			registresAmbMateixUuid = registreRepository.countByExpedientArxiuUuidAndEsborrat(registre.getExpedientArxiuUuid(), 0);
		}
		contingut.updateEsborrat(1);
		// Marca per evitar la cache de la bustia
		Long bustiaId = contingut.getPare().getId();
		BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
				entitat,
				bustiaId,
				true);
		bustiaHelper.evictElementsPendentsBustia(entitat, bustia);
		// Si el contingut és una anotació de registre s'ha de 
		// tancar l'expedient temporal 
		if (registre != null) {
			if (registre.getExpedientArxiuUuid() == null || registresAmbMateixUuid <= 1) {
				if (registre.getAnnexos() != null && registre.getAnnexos().size() > 0) {
					int dies = getPropertyExpedientDiesTancament();
					Date ara = new Date();
					Calendar c = Calendar.getInstance();
					c.setTime(ara);
					c.add(Calendar.DATE, dies);
					Date dataTancament = c.getTime();
					registre.updateDataTancament(dataTancament);
					registre.updateArxiuEsborrat();
				}
			}
		}
		contingutLogHelper.log(
				registre,
				LogTipusEnumDto.MARCAMENT_PROCESSAT,
				registre.getNom(),
				null,
				false,
				false);
		return publicarComentariPerContingut(
				entitatId,
				contingutId,
				text);
	}



	private int getPropertyExpedientDiesTancament() {
		String numDies = PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.tancament.expedient.dies",
				"30");
		return Integer.parseInt(numDies);
	}

	private static final Logger logger = LoggerFactory.getLogger(ContingutServiceImpl.class);

}
