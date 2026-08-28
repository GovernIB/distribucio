package es.caib.distribucio.logic.resourceservice;

import java.util.Map;

import org.springframework.stereotype.Service;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.model.BustiaDefaultResource;
import es.caib.distribucio.logic.intf.resourceservice.BustiaDefaultResourceService;
import es.caib.distribucio.persist.resourceentity.BustiaDefaultResourceEntity;
import es.caib.distribucio.persist.resourcerepository.BustiaResourceRepository;
import es.caib.distribucio.persist.resourcerepository.EntitatResourceRepository;
import es.caib.distribucio.persist.resourcerepository.UsuariResourceRepository;
import lombok.RequiredArgsConstructor;

/**
 * Implementació del servei de consulta i modificació de bústies per defecte via el motor genèric
 * de recursos.
 * <p>
 * Les relacions amb entitat/bústia/usuari es resolen manualment (veure {@code mappingIgnoredFields}
 * a {@link BustiaDefaultResource}), ja que {@link BustiaDefaultResourceEntity} les emmagatzema com
 * a referències i no com a ids.
 *
 * @author Límit Tecnologies
 */
@Service
@RequiredArgsConstructor
public class BustiaDefaultResourceServiceImpl extends BaseMutableResourceService<BustiaDefaultResource, Long, BustiaDefaultResourceEntity> implements BustiaDefaultResourceService {

	private final EntitatResourceRepository entitatResourceRepository;
	private final BustiaResourceRepository bustiaResourceRepository;
	private final UsuariResourceRepository usuariResourceRepository;

	@Override
	protected void beforeCreateSave(
			BustiaDefaultResourceEntity entity,
			BustiaDefaultResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		applyRelations(entity, resource);
	}

	@Override
	protected void beforeUpdateSave(
			BustiaDefaultResourceEntity entity,
			BustiaDefaultResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		applyRelations(entity, resource);
	}

	@Override
	protected void afterConversion(BustiaDefaultResourceEntity entity, BustiaDefaultResource resource) {
		resource.setEntitatId(entity.getEntitat().getId());
		resource.setBustiaId(entity.getBustia().getId());
		resource.setUsuariId(entity.getUsuari().getId());
	}

	private void applyRelations(BustiaDefaultResourceEntity entity, BustiaDefaultResource resource) {
		entity.setEntitat(entitatResourceRepository.getReferenceById(resource.getEntitatId()));
		entity.setBustia(bustiaResourceRepository.getReferenceById(resource.getBustiaId()));
		entity.setUsuari(usuariResourceRepository.getReferenceById(resource.getUsuariId()));
	}

}