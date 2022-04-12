/**
 * 
 */
package es.caib.distribucio.core.service;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.IntegracioAccioDto;
import es.caib.distribucio.core.api.dto.MonitorIntegracioDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.service.MonitorIntegracioService;
import es.caib.distribucio.core.entity.MonitorIntegracioEntity;
import es.caib.distribucio.core.helper.CacheHelper;
import es.caib.distribucio.core.helper.ConversioTipusHelper;
import es.caib.distribucio.core.helper.EntityComprovarHelper;
import es.caib.distribucio.core.helper.PaginacioHelper;
import es.caib.distribucio.core.helper.PermisosHelper;
import es.caib.distribucio.core.repository.BustiaRepository;
import es.caib.distribucio.core.repository.MonitorIntegracioRepository;

/**
 * Implementació del servei de gestió d'items monitorIntegracio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class MonitorIntegracioServiceImpl implements MonitorIntegracioService {

	@Resource
	private MonitorIntegracioRepository monitorIntegracioRepository;
	@Resource
	private BustiaRepository bustiaRepository;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private CacheHelper cacheHelper;

	@Resource
	private EntityComprovarHelper entityComprovarHelper;


	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public MonitorIntegracioDto create(MonitorIntegracioDto monitorIntegracio) {
		logger.debug("Creant una nova monitorIntegracio (" +
				"monitorIntegracio=" + monitorIntegracio + ")");		
		MonitorIntegracioEntity entity = MonitorIntegracioEntity.getBuilder(
				monitorIntegracio.getCodi(),
				monitorIntegracio.getDataEntrada(),
				monitorIntegracio.getDescripcio(),
				monitorIntegracio.getTipus(),
				monitorIntegracio.getTempsResposta(),
				monitorIntegracio.getEstat(),
				monitorIntegracio.getCodiUsuari()).build();
		return conversioTipusHelper.convertir(
				monitorIntegracioRepository.save(entity),
				MonitorIntegracioDto.class);
	}

	@Transactional
	@Override
	public MonitorIntegracioDto update(
			MonitorIntegracioDto monitorIntegracio) {
		logger.debug("Actualitzant monitorIntegracio existent (" +
				"monitorIntegracio=" + monitorIntegracio + ")");

		MonitorIntegracioEntity monitorIntegracioEntity = entityComprovarHelper.comprovarMonitorIntegracio(
				monitorIntegracio.getId(),
				false,
				false,
				false);		
		
		monitorIntegracioEntity.update(
				monitorIntegracio.getCodi(),
				monitorIntegracio.getDataEntrada(),
				monitorIntegracio.getDescripcio(),
				monitorIntegracio.getTipus(),
				monitorIntegracio.getTempsResposta(),
				monitorIntegracio.getEstat(),
				monitorIntegracio.getCodiUsuari());
		return conversioTipusHelper.convertir(
				monitorIntegracioEntity,
				MonitorIntegracioDto.class);
	}

	@Transactional
	@Override
	@CacheEvict(value = "entitatsUsuari", allEntries = true)
	public MonitorIntegracioDto delete(
			Long id) {
		logger.debug("Esborrant monitorIntegracio (" +
				"id=" + id +  ")");

		MonitorIntegracioEntity monitorIntegracio = entityComprovarHelper.comprovarMonitorIntegracio(
				id,
				false,
				false,
				false);
		monitorIntegracioRepository.delete(monitorIntegracio);
		permisosHelper.deleteAcl(
				monitorIntegracio.getId(),
				MonitorIntegracioEntity.class);
		return conversioTipusHelper.convertir(
				monitorIntegracio,
				MonitorIntegracioDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public MonitorIntegracioDto findById(Long id) {
		logger.debug("Consulta de l'monitorIntegracio (" +
				"id=" + id + ")");

		MonitorIntegracioEntity monitorIntegracio = entityComprovarHelper.comprovarMonitorIntegracio(
				id,
				false,
				false,
				false);
		MonitorIntegracioDto dto = conversioTipusHelper.convertir(
				monitorIntegracio,
				MonitorIntegracioDto.class);
		
		return dto;
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public MonitorIntegracioDto findByCodi(String codi) {
		logger.debug("Consulta de l'monitorIntegracio amb codi (" +
				"codi=" + codi + ")");
		MonitorIntegracioDto monitorIntegracio = conversioTipusHelper.convertir(
				monitorIntegracioRepository.findByCodi(codi),
				MonitorIntegracioDto.class);

		return monitorIntegracio;
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<MonitorIntegracioDto> findPaginat(PaginacioParamsDto paginacioParams, String codiMonitor) {
		logger.debug("Consulta de totes les monitorIntegracios paginades (" +
				"paginacioParams=" + paginacioParams + ")");
		PaginaDto<MonitorIntegracioDto> resposta;
		resposta = paginacioHelper.toPaginaDto(
				monitorIntegracioRepository.findByFiltrePaginat(
						paginacioParams.getFiltre() == null || paginacioParams.getFiltre().isEmpty(),
						paginacioParams.getFiltre() != null ? paginacioParams.getFiltre() : "",						
						codiMonitor,
						paginacioHelper.toSpringDataPageable(paginacioParams)),
				MonitorIntegracioDto.class);	
		return resposta;
	}
	
	@Transactional
	@Override
	public void addAccio(String integracioCodi, IntegracioAccioDto accio) {		
		MonitorIntegracioEntity entity = MonitorIntegracioEntity.getBuilder(
				integracioCodi,
				accio.getData(),
				accio.getDescripcio(),		
				accio.getTipus(),
				accio.getTempsResposta(),
				accio.getEstat(),
				accio.getUsuariIntegracio()
				).build();		
		monitorIntegracioRepository.save(entity);
	}

	private static final Logger logger = LoggerFactory.getLogger(MonitorIntegracioServiceImpl.class);

}
