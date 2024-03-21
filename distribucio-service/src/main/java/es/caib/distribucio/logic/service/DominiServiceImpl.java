/**
 *
 */
package es.caib.distribucio.logic.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.helper.CacheHelper;
import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.DominiHelper;
import es.caib.distribucio.logic.helper.EntityComprovarHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper;
import es.caib.distribucio.logic.intf.dto.DominiDto;
import es.caib.distribucio.logic.intf.dto.MetaDadaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.ResultatConsultaDto;
import es.caib.distribucio.logic.intf.dto.ResultatDominiDto;
import es.caib.distribucio.logic.intf.exception.DominiException;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.service.DominiService;
import es.caib.distribucio.persist.entity.DominiEntity;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.MetaDadaEntity;
import es.caib.distribucio.persist.repository.DominiRepository;
import es.caib.distribucio.persist.repository.MetaDadaRepository;

/**
 * Implementació del servei de gestió de dominis.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class DominiServiceImpl implements DominiService {

	@Autowired
	private DominiRepository dominiRepository;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private CacheHelper cacheHelper;
	@Autowired
	private DominiHelper dominiHelper;
	@Autowired
	private MetaDadaRepository metaDadaRepository;
	
	@Transactional
	@Override
	public DominiDto create(
			Long entitatId,
			DominiDto domini) throws NotFoundException {
		
		return dominiHelper.create(
				entitatId,
				domini, true);
	}

	@Transactional
	@Override
	public DominiDto update(
			Long entitatId, 
			DominiDto domini) throws NotFoundException {
		logger.debug("Actualitzant el domini per l'entitat (" +
				"entitatId=" + entitatId +
				"dominiId=" + domini.getId() + ")");
		DominiEntity entity = dominiRepository.findById(domini.getId()).orElse(null);
		if (entity == null || !entity.getId().equals(domini.getId())) {
			throw new NotFoundException(
					domini.getId(),
					DominiEntity.class);
		}
		entity.update(
				domini.getCodi(),
				domini.getNom(),
				domini.getDescripcio(),
				domini.getConsulta(),
				domini.getCadena(),
				dominiHelper.xifrarContrasenya(domini.getContrasenya()));
		DominiDto dominiDto = conversioTipusHelper.convertir(
				entity,
				DominiDto.class);
		return dominiDto;
	}

	@Transactional
	@Override
	public DominiDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		logger.debug("Esborrant el tipus documental per l'entitat (" +
				"entitatId=" + entitatId +
				"dominiId=" + id + ")");

		DominiEntity entity = dominiRepository.getReferenceById(id);

		dominiRepository.delete(entity);

		DominiDto dominiDto = conversioTipusHelper.convertir(
				entity,
				DominiDto.class);
		return dominiDto;
	}

	@Transactional(readOnly = true)
	@Override
	public DominiDto findById(
			Long entitatId, 
			Long id) throws NotFoundException {
		logger.debug("Consultant el domini per l'entitat (" +
				"entitatId=" + entitatId +
				"dominiId=" + id + ")");
		DominiEntity entity = dominiRepository.getReferenceById(id);
		entity.setContrasenya(dominiHelper.desxifrarContrasenya(entity.getContrasenya()));
		
		DominiDto dominiDto = conversioTipusHelper.convertir(
				entity,
				DominiDto.class);
		return dominiDto;	
	}

	@Transactional(readOnly = true)
	@Override
	public PaginaDto<DominiDto> findByEntitatPaginat(Long entitatId, PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);

		Page<DominiEntity> page = dominiRepository.findByEntitat(
				entitat,
				paginacioParams.getFiltre() == null,
				paginacioParams.getFiltre(),
				paginacioHelper.toSpringDataPageable(paginacioParams));

		PaginaDto<DominiDto> dominiDto = paginacioHelper.toPaginaDto(
				page,
				DominiDto.class);
		return dominiDto;
	}

	@Transactional(readOnly = true)
	@Override
	public List<DominiDto> findByEntitat(Long entitatId) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);

		List<DominiEntity> dominis = dominiRepository.findByEntitatOrderByNomAsc(entitat);

		List<DominiDto> dominisDto = conversioTipusHelper.convertirList(
				dominis,
				DominiDto.class);
		return dominisDto;
	}

	@Transactional(readOnly = true)
	@Override
	public DominiDto findByCodiAndEntitat(String codi, Long entitatId) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);

		DominiEntity tipusDocumental = dominiRepository.findByCodiAndEntitat(
				codi, 
				entitat);
		DominiDto dominiDto = conversioTipusHelper.convertir(
				tipusDocumental, 
				DominiDto.class);
		return dominiDto;
	}
	
	@Transactional
	@Override
	public ResultatDominiDto getResultDomini(
			Long entitatId,
			DominiDto domini,
			String filter,
			int page,
			int resultCount) throws NotFoundException, DominiException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		
		if (domini == null) {
			return new ResultatDominiDto();
		}
		JdbcTemplate jdbcTemplate = null;
		Properties conProps = dominiHelper.getProperties(domini);
		
		if (conProps != null && !conProps.isEmpty()) {
			DataSource dataSource = dominiHelper.createDominiConnexio(
					entitat.getCodi(),
					conProps);
			jdbcTemplate = dominiHelper.setDataSource(dataSource);
		}
		int start = (((page - 1) * resultCount != 0 && filter.isEmpty()) ? ((page - 1) * resultCount + 1) : (page - 1) * resultCount);
		int addToEnd = (page - 1) * resultCount;
		int end = resultCount + addToEnd;
		
		return cacheHelper.findDominisByConsutla(
				jdbcTemplate,
				domini.getConsulta(),
				filter,
				start,
				end);
	}
	
	public ResultatConsultaDto getSelectedDomini(
			Long entitatId,
			DominiDto domini,
			String dadaValor) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		
		if (domini == null) {
			return new ResultatConsultaDto();
		}
		JdbcTemplate jdbcTemplate = null;
		Properties conProps = dominiHelper.getProperties(domini);
		
		if (conProps != null && !conProps.isEmpty()) {
			DataSource dataSource = dominiHelper.createDominiConnexio(
					entitat.getCodi(),
					conProps);
			jdbcTemplate = dominiHelper.setDataSource(dataSource);
		}
		
		return cacheHelper.getValueSelectedDomini(
				jdbcTemplate,
				domini.getConsulta(),
				dadaValor);
	}
	
	@Override
	public List<DominiDto> findByEntitatPermisLecturaAndTipusDomini(Long entitatId) {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		
		List<String> dominisCodis = new ArrayList<String>();
		List<DominiEntity> dominis = new ArrayList<DominiEntity>();
		//1. trobar metadades de tipus domini
		List<MetaDadaEntity> metaDades = metaDadaRepository.findByEntitatAndTipusAndActivaTrueOrderByOrdreAsc(entitat, MetaDadaTipusEnumDto.DOMINI);
		
		//2. recuperar els tipus de domini d'aquestes metadades
		for (MetaDadaEntity metaDadaEntity : metaDades) {
			dominisCodis.add(metaDadaEntity.getCodi() != null ? metaDadaEntity.getCodi(): "");
		}
		if (!dominisCodis.isEmpty()) {
			dominis = dominiRepository.findByEntitatAndCodiInOrderByIdAsc(
					entitat, 
					dominisCodis);
		}
		return conversioTipusHelper.convertirList(
				dominis, 
				DominiDto.class);
	}

	public void evictDominiCache() {
		cacheHelper.evictFindDominisByConsutla();
	}
	private static final Logger logger = LoggerFactory.getLogger(DominiServiceImpl.class);

}