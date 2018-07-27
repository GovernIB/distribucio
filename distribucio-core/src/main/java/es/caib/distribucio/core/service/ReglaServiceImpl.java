/**
 * 
 */
package es.caib.distribucio.core.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.helper.ConversioTipusHelper;
import es.caib.distribucio.core.helper.EntityComprovarHelper;
import es.caib.distribucio.core.helper.PaginacioHelper;
import es.caib.distribucio.core.helper.UnitatOrganitzativaHelper;
import es.caib.ripea.core.api.dto.PaginaDto;
import es.caib.ripea.core.api.dto.PaginacioParamsDto;
import es.caib.ripea.core.api.dto.ReglaDto;
import es.caib.ripea.core.api.dto.UnitatOrganitzativaDto;
import es.caib.ripea.core.api.exception.NotFoundException;
import es.caib.ripea.core.api.service.ReglaService;
import es.caib.ripea.core.entity.ArxiuEntity;
import es.caib.ripea.core.entity.BustiaEntity;
import es.caib.ripea.core.entity.EntitatEntity;
import es.caib.ripea.core.entity.MetaExpedientEntity;
import es.caib.ripea.core.entity.ReglaEntity;
import es.caib.ripea.core.entity.UnitatOrganitzativaEntity;
import es.caib.ripea.core.repository.EntitatRepository;
import es.caib.ripea.core.repository.ReglaRepository;
import es.caib.ripea.core.repository.UnitatOrganitzativaRepository;

/**
 * Implementació dels mètodes per a gestionar regles.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ReglaServiceImpl implements ReglaService {

	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private ReglaRepository reglaRepository;

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	@Resource
	private PaginacioHelper paginacioHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
	private UnitatOrganitzativaRepository unitatOrganitzativaRepository;

	@Override
	@Transactional
	public ReglaDto create(
			Long entitatId,
			ReglaDto regla) {
		logger.debug("Creant una nova regla ("
				+ "entitatId=" + entitatId + ", "
				+ "regla=" + regla + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		int ordre = reglaRepository.countByEntitat(entitat);
		ReglaEntity entity = ReglaEntity.getBuilder(
				entitat,
				regla.getNom(),
				regla.getTipus(),
				regla.getAssumpteCodi(),
				unitatOrganitzativaRepository.findOne(regla.getUnitatOrganitzativa().getId()),
				ordre).build();
		return toReglaDto(reglaRepository.save(entity));
	}

	@Override
	@Transactional
	public ReglaDto update(
			Long entitatId,
			ReglaDto regla) throws NotFoundException {
		logger.debug("Modificant la regla ("
				+ "entitatId=" + entitatId + ", "
				+ "regla=" + regla + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		ReglaEntity entity = entityComprovarHelper.comprovarRegla(
				entitat,
				regla.getId());
		entity.update(
				regla.getNom(),
				regla.getDescripcio(),
				regla.getTipus(),
				regla.getAssumpteCodi(),
				unitatOrganitzativaRepository.findOne(regla.getUnitatOrganitzativa().getId())); 
		switch(regla.getTipus()) {
		case BACKOFFICE:
			entity.updatePerTipusBackoffice(
					regla.getBackofficeTipus(),
					regla.getBackofficeUrl(),
					regla.getBackofficeUsuari(),
					regla.getBackofficeContrasenya(),
					regla.getBackofficeIntents(),
					regla.getBackofficeTempsEntreIntents());
			break;
		case BUSTIA:
			BustiaEntity bustia = entityComprovarHelper.comprovarBustia(
					entitat,
					regla.getBustiaId(),
					false);
			entity.updatePerTipusBustia(
					bustia);
			break;
		case EXP_AFEGIR:
		case EXP_CREAR:
			MetaExpedientEntity metaExpedient = entityComprovarHelper.comprovarMetaExpedient(
					entitat,
					regla.getMetaExpedientId(),
					false,
					false);
			ArxiuEntity arxiu = entityComprovarHelper.comprovarArxiu(
					entitat,
					regla.getArxiuId(),
					false);
			entity.updatePerTipusExpedient(
					metaExpedient,
					arxiu);
		}
		return toReglaDto(reglaRepository.save(entity));
	}

	@Override
	@Transactional
	public ReglaDto updateActiva(
			Long entitatId,
			Long reglaId,
			boolean activa) throws NotFoundException {
		logger.debug("Modificant propietat activa de la regla ("
				+ "entitatId=" + entitatId + ", "
				+ "reglaId=" + reglaId + ", "
				+ "activa=" + activa + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		ReglaEntity regla = entityComprovarHelper.comprovarRegla(
				entitat,
				reglaId);
		regla.updateActiva(activa);
		return toReglaDto(reglaRepository.save(regla));
	}

	@Override
	@Transactional
	public ReglaDto delete(
			Long entitatId,
			Long reglaId) throws NotFoundException {
		logger.debug("Esborrant la regla ("
				+ "entitatId=" + entitatId + ", "
				+ "reglaId=" + reglaId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		ReglaEntity regla = entityComprovarHelper.comprovarRegla(
				entitat,
				reglaId);
		reglaRepository.delete(regla);
		return toReglaDto(regla);
	}

	@Override
	@Transactional
	public ReglaDto moveUp(
			Long entitatId,
			Long reglaId) throws NotFoundException {
		logger.debug("Movent la regla per amunt ("
				+ "entitatId=" + entitatId + ", "
				+ "reglaId=" + reglaId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		ReglaEntity regla = entityComprovarHelper.comprovarRegla(
				entitat,
				reglaId);
		canviPosicio(
				regla,
				regla.getOrdre() - 1);
		return toReglaDto(regla);
	}

	@Override
	@Transactional
	public ReglaDto moveDown(
			Long entitatId,
			Long reglaId) throws NotFoundException {
		logger.debug("Movent la regla per avall ("
				+ "entitatId=" + entitatId + ", "
				+ "reglaId=" + reglaId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		ReglaEntity regla = entityComprovarHelper.comprovarRegla(
				entitat,
				reglaId);
		canviPosicio(
				regla,
				regla.getOrdre() + 1);
		return toReglaDto(regla);
	}

	@Override
	@Transactional
	public ReglaDto moveTo(
			Long entitatId,
			Long reglaId,
			int posicio) throws NotFoundException {
		logger.debug("Movent la regla a la posició especificada ("
				+ "entitatId=" + entitatId + ", "
				+ "reglaId=" + reglaId + ", "
				+ "posicio=" + posicio + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		ReglaEntity regla = entityComprovarHelper.comprovarRegla(
				entitat,
				reglaId);
		canviPosicio(
				regla,
				posicio);
		return toReglaDto(regla);
	}

	@Override
	@Transactional(readOnly = true)
	public ReglaDto findOne(
			Long entitatId,
			Long reglaId) {
		logger.debug("Cercant la regla ("
				+ "entitatId=" + entitatId + ", "
				+ "reglaId=" + reglaId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		ReglaEntity regla = entityComprovarHelper.comprovarRegla(
				entitat,
				reglaId);
		return toReglaDto(regla);
	}

	@Override
	@Transactional(readOnly = true)
	public PaginaDto<ReglaDto> findAmbEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta de regles amb paginació ("
				+ "entitatId=" + entitatId + ", "
				+ "paginacioParams=" + paginacioParams + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		return paginacioHelper.toPaginaDto(
				reglaRepository.findByEntitatAndFiltrePaginat(
						entitat,
						paginacioParams.getFiltre() == null,
						paginacioParams.getFiltre(),
						paginacioHelper.toSpringDataPageable(paginacioParams)),
				ReglaDto.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ReglaDto> findByEntitatAndUnitatCodi(
			Long entitatId, 
			String unitatCodi) {
		logger.debug("Cercant las regles de la unitat (" 
				+ "entitatId=" + entitatId 
				+ ", " + "unitatCodi=" + unitatCodi
				+ ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId, false, true, false);

		List<ReglaEntity> regles = reglaRepository.findByEntitatAndUnitatCodi(entitat, unitatCodi);
		List<ReglaDto> resposta = new ArrayList<ReglaDto>();
		for (ReglaEntity regla : regles) {
			resposta.add(toReglaDto(regla));
		}
		return resposta;
	}



	private void canviPosicio(
			ReglaEntity regla,
			int posicio) {
		List<ReglaEntity> regles = reglaRepository.findByEntitatOrderByOrdreAsc(
				regla.getEntitat());
		if (posicio >= 0 && posicio < regles.size()) {
			if (posicio < regla.getOrdre()) {
				for (ReglaEntity reg: regles) {
					if (reg.getOrdre() >= posicio && reg.getOrdre() < regla.getOrdre()) {
						reg.updateOrdre(reg.getOrdre() + 1);
					}
				}
			} else if (posicio > regla.getOrdre()) {
				for (ReglaEntity reg: regles) {
					if (reg.getOrdre() > regla.getOrdre() && reg.getOrdre() <= posicio) {
						reg.updateOrdre(reg.getOrdre() - 1);
					}
				}
			}
			regla.updateOrdre(posicio);
		}
	}

	private ReglaDto toReglaDto(ReglaEntity regla) {
		ReglaDto dto = conversioTipusHelper.convertir(
				regla,
				ReglaDto.class);
		if (regla.getArxiu() != null)
			dto.setArxiuId(regla.getArxiu().getId());
		if (regla.getBustia() != null)
			dto.setBustiaId(regla.getBustia().getId());
		if (regla.getMetaExpedient() != null)
			dto.setMetaExpedientId(regla.getMetaExpedient().getId());
		
		UnitatOrganitzativaEntity unitatEntity = regla.getUnitatOrganitzativa();
		UnitatOrganitzativaDto unitatDto = conversioTipusHelper.convertir(
				unitatEntity,
				UnitatOrganitzativaDto.class);
		unitatDto = UnitatOrganitzativaHelper.assignAltresUnitatsFusionades(unitatEntity, unitatDto);
		dto.setUnitatOrganitzativa(unitatDto);
		
		return dto;
	}

	private static final Logger logger = LoggerFactory.getLogger(ReglaServiceImpl.class);

}
