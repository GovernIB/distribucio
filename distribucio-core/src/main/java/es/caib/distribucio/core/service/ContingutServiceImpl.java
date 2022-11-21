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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
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
import es.caib.distribucio.core.entity.ContingutMovimentEntity;
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
import es.caib.distribucio.core.repository.ContingutComentariRepository;
import es.caib.distribucio.core.repository.ContingutLogRepository;
import es.caib.distribucio.core.repository.ContingutRepository;
import es.caib.distribucio.core.repository.EntitatRepository;
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

	@Autowired
	private EntitatRepository entitatRepository;
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
		boolean comprovarPermisLectura = !rolActual.equals("DIS_ADMIN") && !rolActual.equals("DIS_ADMIN_LECTURA") && !isVistaMoviments;
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
		
		boolean findRol = false;
		if (!rolActual.equals("DIS_ADMIN") && !rolActual.equals("DIS_ADMIN_LECTURA")) {
			findRol = true;
		}
		
		if (contingut.getPare() != null) {
			// Marca per evitar la cache de la bustia
			Long bustiaId = contingut.getPareId();
			entityComprovarHelper.comprovarBustia(
					entitat,
					bustiaId,
					findRol);
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
					if(registre.getAnnexosEstatEsborrany() == 0) {
						registre.updateDataTancament(dataTancament);
					}
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
			if (!usuariHelper.isAdmin() && !usuariHelper.isAdminLectura()) {
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
	public List<LogsDadesObertesDto> findLogsPerDadesObertes(
			Date dataInici, 
			Date dataFi, 
			LogTipusEnumDto tipus, 
			String usuari,
			Long anotacioId, 
			String anotacioNumero,
			RegistreProcesEstatEnum anotacioEstat, 
			Boolean error, 
			Boolean pendent, 
			Long bustiaOrigen, 
			Long bustiaDesti,
			String uoOrigen, 
			String uoSuperior, 
			String uoDesti, 
			String uoDestiSuperior) {

		
		List<LogsDadesObertesDto> resultat = new ArrayList<LogsDadesObertesDto>();
		
		// Adequa les dates d'inici i fi
		boolean isDatesNules = dataInici == null && dataFi == null;
		Calendar c = new GregorianCalendar();
		if (dataInici != null && dataFi != null) {
			if (dataInici.after(dataFi)) {
				dataFi = dataInici; 
			} else {
				c.setTime(dataInici);
				c.add(Calendar.MONTH, 1);
				if (dataFi.after(c.getTime())) {
					dataFi = c.getTime();
				}
			}
		} else if (dataInici == null && dataFi != null) {
			c.setTime(dataFi);
			c.add(Calendar.MONTH, -1);
			dataInici = c.getTime();
		} else if (dataInici != null && dataFi == null) {
			c.setTime(dataInici);
			c.add(Calendar.MONTH, 1);
			dataFi = c.getTime();
		} else {
			// Dates per defecte
			dataFi = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
			c.setTime(dataFi);
			c.add(Calendar.MONTH, -1);
			dataInici = c.getTime();
		}
		if (dataFi != null) {
			// Afegeix un dia a la data de fi per incloure'l en els resultats
			c.setTime(dataFi);
			c.add(Calendar.DATE, 1);
			dataFi = c.getTime();
		}
		
		// Llistats de UO's superiors
		List<String> codisUoSuperiorsOrigen = new ArrayList<String>();
		boolean isCodisUoSuperiorsOrigenEmpty = this.findCodisUosSuperiors(uoOrigen, uoSuperior, codisUoSuperiorsOrigen);
		List<String> codisUoSuperiorsDesti = new ArrayList<String>();
		boolean isCodisUoSuperiorsDestiEmpty = this.findCodisUosSuperiors(uoDesti, uoDestiSuperior, codisUoSuperiorsDesti);

		// Consulta les dades
		// Ojbect[] {ContingutLogEntity log, RegistreEntity registre, BustiaEntity origen, BustiaEntity destí }
		List<Object[]> logs = contingutLogRepository.findLogsPerDadesObertes(
				isDatesNules,
				dataInici, 
				dataFi, 
				tipus == null, 
				tipus != null ? tipus : LogTipusEnumDto.REENVIAMENT,
				usuari == null || usuari.isEmpty(), 
				usuari != null && !usuari.isEmpty() ? usuari : "",
				anotacioId == null, 
				anotacioId != null ? anotacioId.longValue() : 0L, 
				anotacioNumero == null || anotacioNumero.isEmpty(), 
				anotacioNumero != null && !anotacioNumero.isEmpty() ? anotacioNumero : "",
				anotacioEstat == null, 
				anotacioEstat != null ? anotacioEstat : RegistreProcesEstatEnum.ARXIU_PENDENT,
				error == null,
				error != null ? error.booleanValue() : false, 
				pendent == null, 
				pendent != null ? pendent.booleanValue() : false,
				bustiaOrigen == null, 
				bustiaOrigen != null ? bustiaOrigen.longValue() : 0L, 
				bustiaDesti == null, 
				bustiaDesti != null ? bustiaDesti.longValue() : 0L,
				uoOrigen == null || uoOrigen.isEmpty(),  
				uoOrigen != null && !uoOrigen.isEmpty() ? uoOrigen : "", 
				isCodisUoSuperiorsOrigenEmpty, 
				codisUoSuperiorsOrigen, 
				uoDesti == null || uoDesti.isEmpty(),  
				uoDesti != null && !uoDesti.isEmpty() ? uoDesti : "", 
				isCodisUoSuperiorsDestiEmpty, 
				codisUoSuperiorsDesti
				);
		
		// Processa el resultat
		DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Map<String, UnitatOrganitzativaEntity> uosSuperiors = new HashMap<String, UnitatOrganitzativaEntity>();
		
		for (int i=0; i<logs.size(); i++) {

			LogsDadesObertesDto logDto = new LogsDadesObertesDto();

			ContingutLogEntity log = (ContingutLogEntity) logs.get(i)[0];
			RegistreEntity registre = (RegistreEntity) logs.get(i)[1];
			BustiaEntity bOrigen = (BustiaEntity) logs.get(i)[2];
			BustiaEntity bDesti = (BustiaEntity) logs.get(i)[3];
		
			
			try {
				logDto.setData(sdf.format(log.getCreatedDate().toDate()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			logDto.setTipus(log.getTipus());
			logDto.setTipusDesc(messageHelper.getMessage("log.tipus.enum." + log.getTipus()));
			logDto.setUsuari(log.getCreatedBy() != null ? log.getCreatedBy().getCodi() : null);
			logDto.setAnotacioId(log.getContingut().getId()); 
			logDto.setAnotacioNumero(registre.getNumero());
			logDto.setAnotacioEstat(registre.getProcesEstat());
			logDto.setAnotacioEstatDesc(messageHelper.getMessage("registre.proces.estat.enum." + registre.getProcesEstat())); 
			logDto.setAnotacioError(registre.getProcesError() != null);
			logDto.setAnotacioErrorDesc(registre.getProcesError());
			logDto.setPendent(registre.getPendent() != null? registre.getPendent().booleanValue() : false);
			logDto.setnAnnexos((long)registre.getAnnexos().size()); 
			
			if (log.getContingutMoviment() != null) {
				ContingutMovimentEntity moviment = log.getContingutMoviment();
				if (moviment.getOrigenId() != null) {
					logDto.setBustiaOrigenId(moviment.getOrigenId());
					logDto.setBustiaOrigenNom(moviment.getOrigenNom());
					if (bOrigen != null && moviment.getOrigenId().equals(bOrigen.getId()) && bOrigen.getUnitatOrganitzativa() != null) {
						logDto.setUoOrigenCodi(bOrigen.getUnitatOrganitzativa().getCodi());
						logDto.setUoOrigenNom(bOrigen.getUnitatOrganitzativa().getDenominacio());
						UnitatOrganitzativaEntity uoSuperiorOrigen = this.getUoSuperior(uosSuperiors, bOrigen.getUnitatOrganitzativa());
						logDto.setUoSuperiorOrigenCodi(uoSuperiorOrigen.getCodi());
						logDto.setUoSuperiorOrigenNom(uoSuperiorOrigen.getDenominacio());
					}
				}
				if (moviment.getDestiId() != null) {
					logDto.setBustiaDestiId(moviment.getDestiId());
					logDto.setBustiaDestiNom(moviment.getDestiNom());
					if (bDesti != null && moviment.getDestiId().equals(bDesti.getId()) && bDesti.getUnitatOrganitzativa() != null) {				
						logDto.setUoDestiCodi(bDesti.getUnitatOrganitzativa().getCodi());
						logDto.setUoDestiNom(bDesti.getUnitatOrganitzativa().getDenominacio());
						UnitatOrganitzativaEntity uoSuperiorDesti = this.getUoSuperior(uosSuperiors, bDesti.getUnitatOrganitzativa());
						logDto.setUoSuperiorDestiCodi(uoSuperiorDesti.getCodi());
						logDto.setUoSuperiorDestiNom(uoSuperiorDesti.getDenominacio());
					}
				}
			}
			
			resultat.add(logDto);
		}
		
		return resultat;
	}
	
	/** Cerca i retorna la UO superior en segon nivell per codi. Usa um Map a mode de cache per la consulta
	 * 
	 * @param uosSuperiors Map<codiUo, UnitatOrganitzativaEntity> a mode de cache.
	 * @param uo Unitat organitzativa actual.
	 * @return UO superior.
	 */
	private UnitatOrganitzativaEntity getUoSuperior(
			Map<String, UnitatOrganitzativaEntity> uosSuperiors, 
			UnitatOrganitzativaEntity uo) {
		
		UnitatOrganitzativaEntity uoSuperior = uosSuperiors.get(uo.getCodi());
		if (uoSuperior == null) {
			// Si és la unitat arrel o el codi de la superior és el codi de l'arrel llavors retorna la mateixa UO, si no crida la funció recursiva
			if (uo.getCodi().equals(uo.getCodiUnitatArrel()) 
					|| uo.getCodiUnitatSuperior().equals(uo.getCodiUnitatArrel()))
			{
				// Arrel o superior
				uosSuperiors.put(uo.getCodi(), uo);
				uoSuperior = uo;
			}
			else 
			{
				// Cerca recursiva
				String codiUoSuperior = uo.getCodiUnitatSuperior();
				UnitatOrganitzativaEntity uoPare = unitatOrganitzativaRepository.findByCodi(codiUoSuperior);
				if (uoPare != null) {					
					// Crida recursiva per trobar el pare fins l'arrel que retorna null
					uoSuperior = getUoSuperior(uosSuperiors, uoPare);
					if (uoSuperior == null) {
						uoSuperior = uoPare;
					}
					// Afegeix la UO superior al map
				} else {
					uoSuperior = uo;
				}
				uosSuperiors.put(uo.getCodi(), uoSuperior);
			}
		}
		return uoSuperior;
	}

	/** Crea un llistat de codis de UOs a partir de l'UO d'origen i de l'arbre d'unitats que pengen
	 * de la UO superior. Com a mínim posa un element "-" a la llista per evitar errors de consultes.
	 * 
	 * @param uoOrigen Si s'informa no cal buscar més, retorna la llista.
	 * @param uoSuperior Si no s'informa la UO es busca l'arbre a partir de la UO superior.
	 * @param codisUosSuperiors 
	 * @return Retorna true si no es troba cap i la llista hauria de ser buida a false i es troben UOs.
	 */
	private boolean findCodisUosSuperiors(String uo, String uoSuperior, List<String> codisUosSuperiors) {
		// Crea la llista d'unitats orgàniques superiors
		boolean isCodisUoSuperiorsEmpty = false;
		UnitatOrganitzativaEntity uoEntity = null;
		if (uo != null && !uo.isEmpty()) {
			uoEntity = unitatOrganitzativaRepository.findByCodi(uo);
			if (uoEntity != null) {
				codisUosSuperiors.add(uoEntity.getCodi());
			}
		} else if (uoSuperior != null && !uoSuperior.isEmpty()) {
			// Arbre d'unitats superiors
			UnitatOrganitzativaEntity uoSuperiorEntity = unitatOrganitzativaRepository.findByCodi(uoSuperior);
			if (uoSuperiorEntity != null) {
				EntitatEntity entitat = entitatRepository.findByCodiDir3(uoSuperiorEntity.getCodiUnitatArrel());
				codisUosSuperiors.addAll(bustiaHelper.getCodisUnitatsSuperiors(entitat, uoSuperior)); 
			}
		} else {
			// no es filtra per UO
			isCodisUoSuperiorsEmpty = true;
		}
		if (codisUosSuperiors.isEmpty()) {
			// Per a que la consulta no falli
			codisUosSuperiors.add("-");
		}
		return isCodisUoSuperiorsEmpty;
	}
	

	private static final Logger logger = LoggerFactory.getLogger(ContingutServiceImpl.class);
}
