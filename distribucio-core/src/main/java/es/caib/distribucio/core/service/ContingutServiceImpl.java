/**
 * 
 */
package es.caib.distribucio.core.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import es.caib.distribucio.core.api.dto.RespostaPublicacioComentariDto;
import es.caib.distribucio.core.api.dto.dadesobertes.LogsDadesObertesDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.exception.PermissionDeniedException;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.service.ContingutService;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.ContingutEntity;
import es.caib.distribucio.core.entity.ContingutLogEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.core.helper.BustiaHelper;
import es.caib.distribucio.core.helper.CacheHelper;
import es.caib.distribucio.core.helper.ConfigHelper;
import es.caib.distribucio.core.helper.ContingutHelper;
import es.caib.distribucio.core.helper.ContingutLogHelper;
import es.caib.distribucio.core.helper.ConversioTipusHelper;
import es.caib.distribucio.core.helper.EmailHelper;
import es.caib.distribucio.core.helper.EntityComprovarHelper;
import es.caib.distribucio.core.helper.MessageHelper;
import es.caib.distribucio.core.helper.PaginacioHelper;
import es.caib.distribucio.core.helper.PaginacioHelper.Converter;
import es.caib.distribucio.core.helper.PluginHelper;
import es.caib.distribucio.core.helper.RegistreHelper;
import es.caib.distribucio.core.helper.UsuariHelper;
import es.caib.distribucio.core.repository.AlertaRepository;
import es.caib.distribucio.core.repository.BustiaRepository;
import es.caib.distribucio.core.repository.ContingutComentariRepository;
import es.caib.distribucio.core.repository.ContingutLogRepository;
import es.caib.distribucio.core.repository.ContingutRepository;
import es.caib.distribucio.core.repository.RegistreRepository;
import es.caib.distribucio.core.repository.UnitatOrganitzativaRepository;
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
	@Resource
	private ContingutLogRepository contingutLogRepository;
	@Resource
	private EmailHelper emailHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private RegistreHelper registreHelper;
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private BustiaRepository bustiaRepository;	
	@Autowired
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;


	@Transactional(readOnly = true)
	@Override
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions, 
			String rolActual,
			boolean isVistaMoviments) {
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
		boolean comprovarPermisLectura = !rolActual.equals("DIS_ADMIN") && !isVistaMoviments;
		contingutHelper.comprovarPermisosPathContingut(
				contingut,
				comprovarPermisLectura,
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
				ambVersions,
				true);
		
		
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
				true,
				true);
	}

	

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
	
	
	@Transactional(readOnly = true)
	@Override
	public List<ContingutLogDetallsDto> findLogsDetallsPerContingutUser(
			Long entitatId,
			Long contingutId) {

		return contingutHelper.findLogsDetallsPerContingutUser(
				entitatId,
				contingutId);
	}
	
	
	

	
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
						filtre.getNom() != null ? filtre.getNom() : "",
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
								true);
					}
				});
	}


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
	public RespostaPublicacioComentariDto publicarComentariPerContingut(
			Long entitatId,
			Long contingutId,
			String text) {
		return contingutHelper.publicarComentariPerContingut(
				entitatId,
				contingutId,
				text);
	}

	@Transactional
	@Override
	public boolean marcarProcessat(
			Long entitatId,
			Long contingutId,
			String text, 
			String rolActual) {
		
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
			
			if (isPermesReservarAnotacions())
				registreHelper.comprovarRegistreAlliberat(registre);
			
			registresAmbMateixUuid = registreRepository.countByExpedientArxiuUuidAndEsborrat(registre.getExpedientArxiuUuid(), 0);
		}
		
		if (registre != null) {
			registre.setProces(RegistreProcesEstatEnum.BUSTIA_PROCESSADA);
			registre.updateSobreescriure(false);
		}
		
		if (contingut.getPare() != null) {
			// Marca per evitar la cache de la bustia
			Long bustiaId = contingut.getPareId();
			BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
					entitat,
					bustiaId,
					!rolActual.equals("DIS_ADMIN"));
			bustiaHelper.evictCountElementsPendentsBustiesUsuari(entitat, bustia);
		}
		// Si el contingut és una anotació de registre s'ha de 
		// tancar l'expedient temporal 
		if (registre != null && !registre.getArxiuTancat()) {
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
		List<String> params = new ArrayList<>();
		params.add(registre.getNom());
		params.add(null);
		contingutLogHelper.log(
				registre,
				LogTipusEnumDto.MARCAMENT_PROCESSAT,
				params,
				false);
		return publicarComentariPerContingut(
				entitatId,
				contingutId,
				text).isPublicat();
	}
	


	@Transactional
	@Override
	public boolean hasPermisSobreBustia(Long entitatId, Long contingutId) throws NotFoundException {
		boolean hasPermis = true;
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				true,
				false,
				false);

		RegistreEntity registre = registreRepository.findOne(contingutId);
		try {
			if (!usuariHelper.isAdmin()) {
				entityComprovarHelper.comprovarBustia(
								entitat,
								registre.getPareId(),
								true);
			}
		} catch (PermissionDeniedException e) {
			hasPermis = false;
		}
		return hasPermis;
	}

	private boolean isPermesReservarAnotacions() {
		return configHelper.getAsBoolean("es.caib.distribucio.anotacions.permetre.reservar");
	}

	private int getPropertyExpedientDiesTancament() {
		String numDies = configHelper.getConfig(
				"es.caib.distribucio.tancament.expedient.dies",
				"30");
		return Integer.parseInt(numDies);
	}
	
	
	
	@Override
	@Transactional(readOnly = true)
	public List<LogsDadesObertesDto> findLogsDetallsPerData(Date dataInici, Date dataFi, String tipus, String usuari,
			Long anotacioId, String anotacioEstat, Boolean errorEstat, Boolean pendent, Long bustiaOrigen, Long bustiaDesti,
			String uoOrigen, String uoSuperior, String uoDesti, String uoDestiSuperior) {

		
		List<LogsDadesObertesDto> resultat = new ArrayList<LogsDadesObertesDto>();
		ContingutTipusEnumDto tipusEnum = null;
		RegistreProcesEstatEnum anotacioEstatEnum = null;
		
		if (tipus != null) {
			tipus = tipus.toUpperCase();
			tipusEnum = ContingutTipusEnumDto.valueOf(tipus);
		}
		
		if (anotacioEstat != null) {
			anotacioEstat = anotacioEstat.toUpperCase();
			anotacioEstatEnum = RegistreProcesEstatEnum.valueOf(anotacioEstat);
		}
		
		List<ContingutLogEntity> continguts = contingutLogRepository.findLogsPerDadesObertes(
				dataInici, 
				dataFi, 
				tipusEnum == null, 
				tipusEnum != null ? tipusEnum : tipusEnum,
				usuari == null || usuari.isEmpty(), 
				usuari != null && !usuari.isEmpty() ? usuari : "",
				anotacioId == null, 
				anotacioId != null ? anotacioId : 0L, 
				anotacioEstatEnum == null, 
				anotacioEstatEnum != null ? anotacioEstatEnum : anotacioEstatEnum, 
				errorEstat == null,
				errorEstat != null ? errorEstat : errorEstat, 
				pendent == null, 
				pendent != null ? pendent : pendent, 
				bustiaOrigen == null, 
				bustiaOrigen != null ? bustiaOrigen : 0L, 
				bustiaDesti == null, 
				bustiaDesti != null ? bustiaDesti : 0L,
				uoOrigen == null || uoOrigen.isEmpty(),  
				uoOrigen != null && !uoOrigen.isEmpty() ? uoOrigen : "", 
				uoSuperior == null || uoSuperior.isEmpty(), 
				uoSuperior != null && !uoSuperior.isEmpty() ? uoSuperior : "", 
				uoDesti == null || uoDesti.isEmpty(),  
				uoDesti != null && !uoDesti.isEmpty() ? uoDesti : "", 
				uoDestiSuperior == null || uoDestiSuperior.isEmpty(), 
				uoDestiSuperior != null && !uoDestiSuperior.isEmpty() ? uoDestiSuperior : ""
				);
		
		for(ContingutLogEntity contingut : continguts) {
			LogsDadesObertesDto logDOdto = new LogsDadesObertesDto();
			RegistreEntity registreEntity = registreRepository.findOne(contingut.getContingut().getId());
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
			String strDate = dateFormat.format(contingut.getCreatedDate().toDate());
			try {
				logDOdto.setData(strDate);
			} catch (ParseException e1) {
				
				e1.printStackTrace();
			}
			logDOdto.setTipus(contingut.getContingut().getTipus().name()); 
			logDOdto.setTipusDesc(contingut.getTipus().name());
			
			try {
				logDOdto.setUsuari(contingut.getCreatedBy().getCodi());
			}catch (NullPointerException e) {
				logDOdto.setUsuari("");
			}
			logDOdto.setAnotacioId(contingut.getContingut().getId()); 
			logDOdto.setAnotacioEstat(registreEntity.getProcesEstat().toString());
			logDOdto.setAnotacioEstatDesc(registreEntity.getExtracte()); 
			logDOdto.setAnotacioError(new Boolean(registreEntity.getArxiuTancatError()).toString());
			logDOdto.setAnotacioErrorDesc(registreEntity.getProcesError());
			logDOdto.setPendent(registreEntity.getPendent());			
			logDOdto.setnAnnexos((long)registreEntity.getAnnexos().size()); 
			
			try {
				logDOdto.setBustiaOrigenId(contingut.getContingutMoviment().getOrigenId());
				logDOdto.setBustiaOrigenNom(contingut.getContingutMoviment().getOrigenNom());
			}catch (NullPointerException e) {
				logDOdto.setBustiaOrigenId((long)0);
				logDOdto.setBustiaOrigenNom("");
			}
			try {
				logDOdto.setBustiaDestiId(contingut.getContingutMoviment().getDestiId());
				logDOdto.setBustiaDestiNom(contingut.getContingutMoviment().getDestiNom());
			}catch (NullPointerException e) {
				logDOdto.setBustiaDestiId((long)0);
				logDOdto.setBustiaDestiNom("");
			}
			
			try {
				BustiaEntity bustiaEntity = bustiaRepository.getOne(contingut.getContingutMoviment().getOrigenId());
				UnitatOrganitzativaEntity uoEntityOrigen = unitatOrganitzativaRepository.findByCodi(bustiaEntity.getUnitatOrganitzativa().getCodi());
				logDOdto.setUoOrigenCodi(uoEntityOrigen.getCodi());
				logDOdto.setUoOrigenNom(uoEntityOrigen.getDenominacio());
				logDOdto.setUoSuperirOrigenCodi(uoEntityOrigen.getCodiUnitatSuperior());
				if (uoEntityOrigen.getCodiUnitatSuperior().contains("A99999") && uoSuperior == null) {
					uoSuperior = registreEntity.getEntitatCodi();
				}else if(!uoEntityOrigen.getCodiUnitatSuperior().contains("A99999") && uoSuperior == null) {
					uoSuperior = uoEntityOrigen.getCodiUnitatSuperior();
				}
				UnitatOrganitzativaEntity uoSuperiorEntityOrigen = unitatOrganitzativaRepository.findByCodi(uoSuperior);
				logDOdto.setUoSuperiorOrigenNom(uoSuperiorEntityOrigen.getDenominacio());
			
			} catch (NullPointerException e) {
				e.getMessage();
				
			} catch (EntityNotFoundException e2) {
				e2.getMessage();			
			}
			

			try {
				BustiaEntity bustiaEntity = bustiaRepository.getOne(contingut.getContingutMoviment().getDestiId());
				UnitatOrganitzativaEntity uoEntityDesti = unitatOrganitzativaRepository.findByCodi(bustiaEntity.getUnitatOrganitzativa().getCodi());
				logDOdto.setUoDestiCodi(uoEntityDesti.getCodi());
				logDOdto.setUoDestiNom(uoEntityDesti.getDenominacio());
				logDOdto.setUoSuperiorDestiCodi(uoEntityDesti.getCodiUnitatSuperior());
				if (uoEntityDesti.getCodiUnitatSuperior().contains("A99999") && uoDestiSuperior == null) {
					uoDestiSuperior = registreEntity.getEntitatCodi();;
				}else if (!uoEntityDesti.getCodiUnitatSuperior().contains("A99999") && uoDestiSuperior == null) {
					uoDestiSuperior = uoEntityDesti.getCodiUnitatSuperior();
				}
				UnitatOrganitzativaEntity uoSuperiorEntityDesti = unitatOrganitzativaRepository.findByCodi(uoDestiSuperior);
				logDOdto.setUoSuperiorDestiNom(uoSuperiorEntityDesti.getDenominacio());
			
			} catch (NullPointerException e) {
				e.getMessage();
			
			} catch (EntityNotFoundException e2) {
				e2.getMessage();		
			}
			
			resultat.add(logDOdto);
		}
		
		return resultat;
	}
	
	

	private static final Logger logger = LoggerFactory.getLogger(ContingutServiceImpl.class);



}
