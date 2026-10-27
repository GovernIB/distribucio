package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.model.SiaResource;
import es.caib.distribucio.logic.intf.resourceservice.SiaResourceService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.logic.intf.util.Utils;
import es.caib.distribucio.persist.resourceentity.EntitatResourceEntity;
import es.caib.distribucio.persist.resourceentity.SiaResourceEntity;
import es.caib.distribucio.persist.resourcerepository.EntitatResourceRepository;
import es.caib.distribucio.persist.resourcerepository.SiaResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SiaResourceServiceImpl extends BaseMutableResourceService<SiaResource, Long, SiaResourceEntity> implements SiaResourceService {

    private final EntitatResourceRepository entitatResourceRepository;
    private final SiaResourceRepository siaResourceRepository;

    @Override
	protected Specification<SiaResourceEntity> additionalSpecification(String[] namedQueries) {
        Long entitatActualId = SessioActualUtil.getEntitatId();

        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            /// Entitat
            if (entitatActualId != null) {
                predicates.add(cb.equal(root.get("entitat").get("id"), entitatActualId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
	}

	@Override
	protected void afterConversion(SiaResourceEntity entity, SiaResource resource) {
	}

	@Override
	protected void beforeCreateSave(
			SiaResourceEntity entity,
			SiaResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		if (entity.getEntitat() == null) {
			Long entitatId = SessioActualUtil.getEntitatId();
			if (entitatId != null) {
				EntitatResourceEntity entitat = entitatResourceRepository.getReferenceById(entitatId);
				entity.setEntitat(entitat);
			}
		}
	}

	@Override
	protected void beforeUpdateSave(
			SiaResourceEntity entity,
			SiaResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
	}

    @Override
    public SiaResource findByEntitatIdAndCodiSia(Long entitatId, String cosiSia) {
        Optional<SiaResourceEntity> op = siaResourceRepository.findByEntitatIdAndCodiSia(entitatId, cosiSia);
        return op.map(siaResourceEntity -> objectMappingHelper.newInstanceMap(siaResourceEntity, SiaResource.class)).orElse(null);
    }
}
