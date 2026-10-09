package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.model. DominiResource;
import es.caib.distribucio.logic.intf.resourceservice. DominiResourceService;
import es.caib.distribucio.logic.intf.service.DominiService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.persist.resourceentity. DominiResourceEntity;
import es.caib.distribucio.persist.resourceentity.EntitatResourceEntity;
import es.caib.distribucio.persist.resourcerepository.EntitatResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DominiResourceServiceImpl extends BaseMutableResourceService<DominiResource, Long,  DominiResourceEntity> implements DominiResourceService {

    private final DominiService dominiService;
    private final EntitatResourceRepository entitatResourceRepository;

    @PostConstruct
    public void init() {
        register(DominiResource.ACTION_CLEAN_CACHE_CODE, new CleanCacheActionExecutor());
    }

    @Override
    protected Specification< DominiResourceEntity> additionalSpecification(String[] namedQueries) {
        Long entitatActualId = SessioActualUtil.getEntitatId();

//        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (entitatActualId != null) {
                predicates.add(cb.equal(root.get("entitat").get("id"), entitatActualId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    protected void beforeCreateSave(DominiResourceEntity entity, DominiResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        Long entitatId = SessioActualUtil.getEntitatId();
        if (entitatId != null && entity.getEntitat() == null) {
            EntitatResourceEntity entitat = entitatResourceRepository.getReferenceById(entitatId);
            entity.setEntitat(entitat);
        }
    }

    protected class CleanCacheActionExecutor implements ActionExecutor<DominiResourceEntity, Serializable, Serializable> {

        @Override
        public Serializable exec(String code, DominiResourceEntity entity, Serializable params) throws ActionExecutionException {
            dominiService.evictDominiCache();
            return null;
        }

        @Override
        public void onChange(Serializable id, Serializable previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Serializable target) {
        }
    }

}