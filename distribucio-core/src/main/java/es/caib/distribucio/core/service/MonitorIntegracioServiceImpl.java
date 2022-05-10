/**
 * 
 */
package es.caib.distribucio.core.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.IntegracioDto;
import es.caib.distribucio.core.api.dto.MonitorIntegracioDto;
import es.caib.distribucio.core.api.dto.MonitorIntegracioParamDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.service.MonitorIntegracioService;
import es.caib.distribucio.core.entity.MonitorIntegracioEntity;
import es.caib.distribucio.core.entity.MonitorIntegracioParamEntity;
import es.caib.distribucio.core.helper.ConversioTipusHelper;
import es.caib.distribucio.core.helper.EntityComprovarHelper;
import es.caib.distribucio.core.helper.IntegracioHelper;
import es.caib.distribucio.core.helper.PaginacioHelper;
import es.caib.distribucio.core.repository.BustiaRepository;
import es.caib.distribucio.core.repository.MonitorIntegracioParamRepository;
import es.caib.distribucio.core.repository.MonitorIntegracioRepository;

/**
 * Implementació del servei de gestió d'items monitorIntegracio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class MonitorIntegracioServiceImpl implements MonitorIntegracioService {

	@Resource
	private IntegracioHelper integracioHelper;

	@Resource
	private MonitorIntegracioRepository monitorIntegracioRepository;
	@Resource
	private MonitorIntegracioParamRepository monitorIntegracioParamRepository;
	@Resource
	private BustiaRepository bustiaRepository;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;

	@Resource
	private EntityComprovarHelper entityComprovarHelper;

	@Override
	public List<IntegracioDto> integracioFindAll() {
		logger.trace("Consultant les integracions");
		return integracioHelper.findAll();
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public MonitorIntegracioDto create(MonitorIntegracioDto monitorIntegracio) {
		logger.trace("Creant una nova monitorIntegracio (" +
				"monitorIntegracio=" + monitorIntegracio + ")");		
		MonitorIntegracioEntity entity = MonitorIntegracioEntity.getBuilder(
				monitorIntegracio.getCodi(),
				monitorIntegracio.getData(),
				monitorIntegracio.getDescripcio(),
				monitorIntegracio.getTipus(),
				monitorIntegracio.getTempsResposta(),
				monitorIntegracio.getEstat(),
				monitorIntegracio.getCodiUsuari(),
				monitorIntegracio.getErrorDescripcio(),
				monitorIntegracio.getExcepcioMessage(),
				monitorIntegracio.getExcepcioStacktrace()).build();
		entity = monitorIntegracioRepository.save(entity);
		for (MonitorIntegracioParamDto paramDto : monitorIntegracio.getParametres()) {
			MonitorIntegracioParamEntity paramEntity = MonitorIntegracioParamEntity.getBuilder(
							entity, 
							paramDto.getNom(), 
							paramDto.getDescripcio()).build();
			paramEntity = monitorIntegracioParamRepository.save(paramEntity); 
			entity.getParametres().add(paramEntity);
		}
		return conversioTipusHelper.convertir(
				entity,
				MonitorIntegracioDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public MonitorIntegracioDto findById(Long id) {
		logger.debug("Consulta de l'monitorIntegracio (" +
				"id=" + id + ")");

		MonitorIntegracioDto dto = conversioTipusHelper.convertir(
				monitorIntegracioRepository.findOne(id),
				MonitorIntegracioDto.class);
		return dto;
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
	
	private static final Logger logger = LoggerFactory.getLogger(MonitorIntegracioServiceImpl.class);

	@Transactional(readOnly = true)
	@Override
	public Map<String, Integer> countErrors() {
		Map<String, Integer> errors = new HashMap<String, Integer>();
		List<Object[]> resultats = monitorIntegracioRepository.countErrorsGroupByCodi();
		for (Object[] resultat : resultats) {
			errors.put(
					(String) resultat[0], 
					((Long) resultat[1]).intValue());
		}
		return errors;
	}
	
	@Transactional
	@Override
	public int esborrarDadesAntigues(Date data) {
		logger.trace("Esborrant dades del monitor d'integració anteriors a : " + data);
		int n = 0;
		if (data != null) {
			for (MonitorIntegracioEntity monitorIntegracio : monitorIntegracioRepository.getDadesAntigues(data)) {
				monitorIntegracioRepository.delete(monitorIntegracio);
				n++;
			}
		}
		return n;
	}

	@Transactional
	@Override
	public int delete(String codi) {
		logger.debug("Esborrant dades del monitor d'integració per la integració amb codi : " + codi);
		int n = 0;
		if (codi != null) {
			for (MonitorIntegracioEntity monitorIntegracio : monitorIntegracioRepository.findByCodi(codi)) {
				monitorIntegracioRepository.delete(monitorIntegracio);
				n++;
			}
		}
		return n;
	}
}
