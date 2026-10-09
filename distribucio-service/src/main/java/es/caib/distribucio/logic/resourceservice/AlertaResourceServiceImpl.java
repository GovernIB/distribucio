package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.model.AlertaResource;
import es.caib.distribucio.logic.intf.resourceservice.AlertaResourceService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.persist.resourceentity.AlertaResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertaResourceServiceImpl extends BaseMutableResourceService<AlertaResource, Long, AlertaResourceEntity> implements AlertaResourceService {

    @PostConstruct
    public void init() {
    }

    @Override
    protected Specification<AlertaResourceEntity> additionalSpecification(String[] namedQueries) {
        Long entitatActualId = SessioActualUtil.getEntitatId();

//        Map<String, String> mapaNamedQueries =  Utils.namedQueriesToMap(namedQueries);
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isFalse(root.get("llegida")));

            if (entitatActualId != null) {
                predicates.add(cb.equal(root.get("contingut").get("entitat").get("id"), entitatActualId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}