package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.model. MetaDadaResource;
import es.caib.distribucio.logic.intf.resourceservice. MetaDadaResourceService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.persist.resourceentity. MetaDadaResourceEntity;
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
public class MetaDadaResourceServiceImpl extends BaseMutableResourceService<MetaDadaResource, Long,  MetaDadaResourceEntity> implements  MetaDadaResourceService {

    @PostConstruct
    public void init() {
    }

    @Override
    protected Specification< MetaDadaResourceEntity> additionalSpecification(String[] namedQueries) {
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

}