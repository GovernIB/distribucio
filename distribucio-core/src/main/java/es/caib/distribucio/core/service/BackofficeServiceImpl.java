package es.caib.distribucio.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.BackofficeDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.service.BackofficeService;
import es.caib.distribucio.core.entity.BackofficeEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.helper.ConversioTipusHelper;
import es.caib.distribucio.core.helper.EntityComprovarHelper;
import es.caib.distribucio.core.helper.PaginacioHelper;
import es.caib.distribucio.core.repository.BackofficeRepository;
import es.caib.distribucio.core.repository.RegistreRepository;


@Service
public class BackofficeServiceImpl implements BackofficeService {
	
	
	@Autowired
	private BackofficeRepository backofficeRepository;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private ConversioTipusHelper conversioTipusHelper;
	@Autowired
	private PaginacioHelper paginacioHelper;
	@Autowired
	private RegistreRepository registreRepository;

	
	@Transactional
	@Override
	public BackofficeDto create(
			Long entitatId,
			BackofficeDto backofficeDto) throws NotFoundException {
		logger.debug("Creant un nou backoffice per l'entitat (" +
				"entitatId=" + entitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		
		
		BackofficeEntity enitity = BackofficeEntity.getBuilder(
				backofficeDto.getCodi(),
				backofficeDto.getNom(),
				backofficeDto.getUrl(),
				backofficeDto.getTipus(),
				entitat).
				usuari(backofficeDto.getUsuari()).
				contrasenya(backofficeDto.getContrasenya()).
				intents(backofficeDto.getIntents()).
				tempsEntreIntents(backofficeDto.getTempsEntreIntents()).
				build();

		BackofficeDto dto = conversioTipusHelper.convertir(
				backofficeRepository.save(enitity),
				BackofficeDto.class);
		return dto;
	}

	@Transactional
	@Override
	public BackofficeDto update(
			Long entitatId,
			BackofficeDto backofficeDto) throws NotFoundException {
		logger.debug("Actualitzant el backoffice per l'entitat (" +
				"entitatId=" + entitatId +
				"backofficeId=" + backofficeDto.getId() + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		
		BackofficeEntity backofficeEntity = backofficeRepository.findOne(backofficeDto.getId());
		
		List<RegistreEntity> llistatRegistres = registreRepository.findRegistreBackCodi(backofficeEntity.getCodi());
		
		backofficeEntity.update(
				backofficeDto.getCodi(),
				backofficeDto.getNom(),
				backofficeDto.getUrl(),
				backofficeDto.getUsuari(),
				backofficeDto.getContrasenya(),
				backofficeDto.getIntents(),
				backofficeDto.getTempsEntreIntents(), 
				backofficeDto.getTipus());
		
		
		BackofficeDto dto = conversioTipusHelper.convertir(
				backofficeEntity,
				BackofficeDto.class);
		
		for(RegistreEntity registreEntity : llistatRegistres) {
			registreEntity.updateBackCodi(dto.getCodi());
		}
		
		return dto;
	}

	@Transactional
	@Override
	public BackofficeDto delete(
			Long entitatId,
			Long id) throws NotFoundException {
		logger.debug("Esborrant el tipus documental per l'entitat (" +
				"entitatId=" + entitatId +
				"backofficeId=" + id + ")");
		entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);

		BackofficeEntity backofficeEntity = backofficeRepository.findOne(id);
		
		backofficeRepository.delete(backofficeEntity);

		BackofficeDto dto = conversioTipusHelper.convertir(
				backofficeEntity,
				BackofficeDto.class);
		return dto;
	}

	@Transactional
	@Override
	public BackofficeDto findById(Long entitatId, Long id) throws NotFoundException {
		logger.debug("Consultant el tipus documental per l'entitat (" +
				"entitatId=" + entitatId +
				"backofficeId=" + id + ")");
		BackofficeEntity entity = backofficeRepository.findOne(id);

		BackofficeDto dto = conversioTipusHelper.convertir(
				entity,
				BackofficeDto.class);
		return dto;
	}
	
	
	@Transactional
	@Override
	public BackofficeDto findByCodi(Long entitatId, String backofficeCodi) throws NotFoundException {
		logger.debug("Consultant el backoffice per backoffice codi (" +
				"entitatId=" + entitatId +
				"backofficeCodi=" + backofficeCodi + ")");
		
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);
		
		BackofficeEntity entity = backofficeRepository.findByEntitatAndCodi(entitat, backofficeCodi);

		BackofficeDto dto = conversioTipusHelper.convertir(
				entity,
				BackofficeDto.class);
		return dto;
	}

	@Transactional
	@Override
	public PaginaDto<BackofficeDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);

		Page<BackofficeEntity> page = backofficeRepository.findByEntitat(
				entitat,
				paginacioParams.getFiltre() == null,
				paginacioParams.getFiltre(),
				paginacioHelper.toSpringDataPageable(paginacioParams));
		
		
		PaginaDto<BackofficeDto> pageDto = paginacioHelper.toPaginaDto(
				page,
				BackofficeDto.class);

		return pageDto;
	}
	
	
	
	
	@Transactional
	@Override
	public List<BackofficeDto> findByEntitat(
			Long entitatId) throws NotFoundException {
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);

		List<BackofficeEntity> list = backofficeRepository.findByEntitat(
				entitat);
		
		
		List<BackofficeDto> listDto = conversioTipusHelper.convertirList(
				list,
				BackofficeDto.class);

		return listDto;
	}
	

	
	

	

	
	
	private static final Logger logger = LoggerFactory.getLogger(BackofficeServiceImpl.class);
	
	
}
