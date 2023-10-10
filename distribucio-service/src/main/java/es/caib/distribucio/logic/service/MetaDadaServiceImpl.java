/**
 * 
 */
package es.caib.distribucio.logic.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.logic.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.helper.EntityComprovarHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper;
import es.caib.distribucio.logic.helper.PermisosHelper;
import es.caib.distribucio.logic.intf.dto.MetaDadaDto;
import es.caib.distribucio.logic.intf.dto.MetaDadaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.service.MetaDadaService;
import es.caib.distribucio.persist.entity.EntitatEntity;
import es.caib.distribucio.persist.entity.MetaDadaEntity;
import es.caib.distribucio.persist.repository.DadaRepository;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.persist.repository.MetaDadaRepository;

/**
 * Implementació del servei de gestió de meta-dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class MetaDadaServiceImpl implements MetaDadaService {

	@Resource
	private MetaDadaRepository metaDadaRepository;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private DadaRepository dadaRepository;

	@Resource
	ConversioTipusHelper conversioTipusHelper;
	@Resource
	PaginacioHelper paginacioHelper;
	@Resource
	private PermisosHelper permisosHelper;
	@Resource
	private EntityComprovarHelper entityComprovarHelper;

	@Transactional
	@Override
	public MetaDadaDto create(
			Long entitatId,
			MetaDadaDto metaDada) {
		logger.debug("Creant una nova meta-dada ("
				+ "entitatId=" + entitatId + ", "
				+ "metadada=" + metaDada + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				false,
				false);
		int ordre = metaDadaRepository.countByEntitat(entitat);
		
		Object valor = null;
		if (metaDada.getTipus()==MetaDadaTipusEnumDto.BOOLEA) {
			valor = metaDada.getValorBoolea();
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.DATA) {
			valor = metaDada.getValorData();
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.FLOTANT) {
			valor = metaDada.getValorFlotant();
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.IMPORT) {
			valor = metaDada.getValorImport();
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.SENCER) {
			valor = metaDada.getValorSencer();
		}  else if (metaDada.getTipus()==MetaDadaTipusEnumDto.TEXT || metaDada.getTipus()==MetaDadaTipusEnumDto.DOMINI) {
			valor = metaDada.getValorString();
		}
		
		MetaDadaEntity entity = MetaDadaEntity.getBuilder(
				metaDada.getCodi(), 
				metaDada.getNom(), 
				metaDada.getTipus(), 
				metaDada.getMultiplicitat(), 
				valor, 
				metaDada.isReadOnly(), 
				ordre, 
				metaDada.isNoAplica(),
				entitat).build();
		return conversioTipusHelper.convertir(
				metaDadaRepository.save(entity),
				MetaDadaDto.class);
	}

	@Transactional
	@Override
	public MetaDadaDto update(
			Long entitatId,
			MetaDadaDto metaDada) {
		logger.debug("Actualitzant meta-dada existent (" +
				"entitatId=" + entitatId + ", " +
				"metaDada=" + metaDada + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaDadaEntity entity = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaDada.getId());
		
		
		Object valor = null;
		if (metaDada.getTipus()==MetaDadaTipusEnumDto.BOOLEA) {
			valor = metaDada.getValorBoolea();
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.DATA) {
			valor = metaDada.getValorData();
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.FLOTANT) {
			valor = metaDada.getValorFlotant();
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.IMPORT) {
			valor = metaDada.getValorImport();
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.SENCER) {
			valor = metaDada.getValorSencer();
		}  else if (metaDada.getTipus()==MetaDadaTipusEnumDto.TEXT || metaDada.getTipus()==MetaDadaTipusEnumDto.DOMINI) {
			valor = metaDada.getValorString();
		}
		entity.update(
				metaDada.getCodi(),
				metaDada.getNom(),
				metaDada.getTipus(),
				metaDada.getMultiplicitat(),
				valor,
				metaDada.getDescripcio(),
				metaDada.isReadOnly(),
				metaDada.isNoAplica());
		
		return conversioTipusHelper.convertir(
				entity,
				MetaDadaDto.class);
	}
	
	@Transactional
	@Override
	public MetaDadaDto delete(
			Long entitatId,
			Long metaDadaId) {
		logger.debug("Esborrant meta-dada (" +
				"entitatId=" + entitatId + ", " +
				"metaDadaId" + metaDadaId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		
		MetaDadaEntity metaDada = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaDadaId);
		
		metaDadaRepository.delete(metaDada);
		return conversioTipusHelper.convertir(
				metaDada,
				MetaDadaDto.class);
	}

	@Transactional
	@Override
	public MetaDadaDto updateActiva(
			Long entitatId,
			Long id,
			boolean activa) {
		logger.debug("Actualitzant propietat activa de la meta-dada (" +
				"entitatId=" + entitatId + ", " +
				"id=" + id + "," +
				"activa=" + activa + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaDadaEntity metaDada = entityComprovarHelper.comprovarMetaDada(
				entitat,
				id);
		metaDada.updateActiva(activa);
		return conversioTipusHelper.convertir(
				metaDada,
				MetaDadaDto.class);
	}

	@Transactional
	@Override
	public void moveUp(
			Long entitatId,
			Long metaDadaId) {
		logger.debug("Movent meta-dada al meta-expedient cap amunt ("
				+ "entitatId=" + entitatId +  ", "
				+ "metaDadaId=" + metaDadaId +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaDadaEntity metaDada = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaDadaId);
		moureMetaNodeMetaDada(
				entitat,
				metaDada,
				metaDada.getOrdre() - 1);
	}

	@Transactional
	@Override
	public void moveDown(
			Long entitatId,
			Long metaDadaId) {
		logger.debug("Movent meta-dada al meta-expedient cap avall ("
				+ "entitatId=" + entitatId +  ", "
				+ "metaDadaId=" + metaDadaId +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaDadaEntity metaDada = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaDadaId);
		moureMetaNodeMetaDada(
				entitat,
				metaDada,
				metaDada.getOrdre() + 1);
	}

	@Transactional
	@Override
	public void moveTo(
			Long entitatId,
			Long metaDadaId,
			int posicio) {
		logger.debug("Movent meta-dada al meta-expedient ("
				+ "entitatId=" + entitatId +  ", "
				+ "metaDadaId=" + metaDadaId +  ", "
				+ "posicio=" + posicio +  ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaDadaEntity metaDada = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaDadaId);
		moureMetaNodeMetaDada(
				entitat,
				metaDada,
				posicio);
	}
	
	@Transactional(readOnly=true)
	@Override
	public MetaDadaDto findById(
			Long entitatId,
			Long metaDadaId) {
		logger.debug("Consulta de la meta-dada (" +
				"entitatId=" + entitatId + ", " +
				"metaDadaId=" + metaDadaId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		MetaDadaEntity metaDada = entityComprovarHelper.comprovarMetaDada(
				entitat,
				metaDadaId);
		MetaDadaDto metaDadaDto = conversioTipusHelper.convertir(
				metaDada,
				MetaDadaDto.class);
		
		return metaDadaDto;
	}

	@Transactional(readOnly=true)
	@Override
	public MetaDadaDto findByCodi(
			Long entitatId,
			String codi) {
		logger.debug("Consulta de la meta-dada per entitat i codi (" +
				"entitatId=" + entitatId + ", " +
				"codi=" + codi + ")");
		return conversioTipusHelper.convertir(
				metaDadaRepository.findByCodi(codi),
				MetaDadaDto.class);
	}

	@Transactional(readOnly=true)
	@Override
	public PaginaDto<MetaDadaDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams) {
		logger.debug("Consulta paginada de les meta-dades de l'entitat (" +
				"entitatId=" + entitatId + ", " +
				"paginacioParams=" + paginacioParams + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		
		PaginaDto<MetaDadaDto> resposta;
		if (paginacioHelper.esPaginacioActivada(paginacioParams)) {
			resposta = paginacioHelper.toPaginaDto(
					metaDadaRepository.findByEntitat(
							entitat,
							paginacioParams.getFiltre() == null,
							paginacioParams.getFiltre(),
							paginacioHelper.toSpringDataPageable(paginacioParams)),
					MetaDadaDto.class);
		} else {
			resposta = paginacioHelper.toPaginaDto(
					metaDadaRepository.findByEntitat(
							entitat,
							paginacioParams.getFiltre() == null,
							paginacioParams.getFiltre(),
							paginacioHelper.toSpringDataSort(paginacioParams)),
					MetaDadaDto.class);
		}
		return resposta;
	}

//	@Transactional(readOnly=true)
//	@Override
//	public List<MetaDadaDto> findActiveByMetaNode(
//			Long entitatId) {
//		logger.debug("Consulta de les meta-dades de l'entitat (" +
//				"entitatId=" + entitatId + ")");
//		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
//				entitatId,
//				false,
//				true,
//				false, false, false);
//		MetaNodeEntity metaNode = entityComprovarHelper.comprovarMetaNode(
//				entitat,
//				metaNodeId);
//		return conversioTipusHelper.convertirList(
//				metaDadaRepository.findByMetaNodeAndActivaTrueOrderByOrdreAsc(metaNode),
//				MetaDadaDto.class);
//	}

	@Transactional(readOnly=true)
	@Override
	public List<MetaDadaDto> findByEntitat(Long entitatId) {
		logger.debug("Consulta de les meta-dades disponibles al node ("
				+ "entitatId=" + entitatId + ")");
		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
				entitatId,
				false,
				true,
				false);
		return conversioTipusHelper.convertirList(
				metaDadaRepository.findByEntitatAndActivaTrueOrderByOrdreAsc(entitat),
				MetaDadaDto.class);
	}
//
//	@Override
//	public Long findMetaNodeIdByNodeId(
//			Long entitatId,
//			Long nodeId) {
//		EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(
//				entitatId,
//				true,
//				false,
//				false, false, false);
//		NodeEntity node = entityComprovarHelper.comprovarNode(entitat, nodeId);
//		return node.getMetaNode().getId();
//	}
	
	private void moureMetaNodeMetaDada(
			EntitatEntity entitat,
			MetaDadaEntity metaDada,
			int posicio) {
		List<MetaDadaEntity> entitatMetaDades = metaDadaRepository.findByEntitatOrderByOrdreAsc(entitat);
		int indexOrigen = -1;
		for (MetaDadaEntity md: entitatMetaDades) {
			if (md.getId().equals(metaDada.getId())) {
				indexOrigen = md.getOrdre();
				break;
			}
		}
		entitatMetaDades.add(
				posicio,
				entitatMetaDades.remove(indexOrigen));
		for (int i = 0; i < entitatMetaDades.size(); i++) {
			entitatMetaDades.get(i).updateOrdre(i);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(MetaDadaServiceImpl.class);

}