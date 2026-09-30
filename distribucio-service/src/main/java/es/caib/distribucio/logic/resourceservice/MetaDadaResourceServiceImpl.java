package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.dto.MetaDadaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.MultiplicitatEnumDto;
import es.caib.distribucio.logic.intf.model. MetaDadaResource;
import es.caib.distribucio.logic.intf.resourceservice. MetaDadaResourceService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.persist.resourceentity. MetaDadaResourceEntity;
import es.caib.distribucio.persist.resourcerepository.DominiResourceRepository;
import es.caib.distribucio.persist.resourcerepository.MetaDadaResourceRepository;
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
public class MetaDadaResourceServiceImpl extends BaseMutableResourceService<MetaDadaResource, Long,  MetaDadaResourceEntity> implements  MetaDadaResourceService {

    private final MetaDadaResourceRepository metaDadaResourceRepository;
    private final DominiResourceRepository dominiResourceRepository;

    @PostConstruct
    public void init() {
        register(MetaDadaResource.Fields.tipus, new TipusOnchangeLogicProcessor());
        register(MetaDadaResource.ACTION_REORDENAR_CODE, new ReordenarActionExecutor());
    }

    protected void beforeSave(MetaDadaResourceEntity entity, MetaDadaResource resource) {
        entity.setValue( resource.getValue() );
    }

    @Override
    protected void beforeCreateSave(MetaDadaResourceEntity entity, MetaDadaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        this.beforeSave(entity, resource);
        super.beforeCreateSave(entity, resource, answers);
    }

    @Override
    protected void beforeUpdateSave(MetaDadaResourceEntity entity, MetaDadaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        this.beforeSave(entity, resource);
        super.beforeUpdateSave(entity, resource, answers);
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

    @Override
    protected void afterConversion(MetaDadaResourceEntity entity, MetaDadaResource resource) {
        resource.setValue( entity.getValue() );

        if (MetaDadaTipusEnumDto.DOMINI.equals(resource.getTipus()) && entity.getValor() != null) {
            dominiResourceRepository.findByCodi( entity.getValor() )
                    .ifPresent((domini) -> resource.setDomini(
                            ResourceReference.toResourceReference(
                                    domini.getId(), domini.getNom()
                            )));
        }

        super.afterConversion(entity, resource);
    }

    private class TipusOnchangeLogicProcessor implements OnChangeLogicProcessor<MetaDadaResource> {
        @Override
        public void onChange(Serializable id, MetaDadaResource previous, String fieldName, Object fieldValue,
                             Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, MetaDadaResource target) {
            if (MetaDadaTipusEnumDto.DOMINI.equals(fieldValue)) {
                target.setMultiplicitat(MultiplicitatEnumDto.M_0_1);
            }
        }
    }

    private class ReordenarActionExecutor implements ActionExecutor<MetaDadaResourceEntity, Integer, Serializable> {

        @Override
        public Serializable exec(String code, MetaDadaResourceEntity entity, Integer params) throws ActionExecutionException {
            try {
                Long entitatActualId = SessioActualUtil.getEntitatId();

                List<MetaDadaResourceEntity> entitatMetaDades = metaDadaResourceRepository.findByEntitatIdOrderByOrdreAsc(entitatActualId);
                int indexOrigen = -1;
                for (MetaDadaResourceEntity md: entitatMetaDades) {
                    if (md.getId().equals(entity.getId())) {
                        indexOrigen = md.getOrdre();
                        break;
                    }
                }
                entitatMetaDades.add(
                        params,
                        entitatMetaDades.remove(indexOrigen));
                for (int i = 0; i < entitatMetaDades.size(); i++) {
                    entitatMetaDades.get(i).setOrdre(i);
                }

            } catch (Exception e) {
                throw new ActionExecutionException(
                        MetaDadaResource.class,
                        entity.getId(),
                        code,
                        e.getMessage()
                );
            }
            return null;
        }

        @Override
        public void onChange(Serializable id, Integer previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, Integer target) {
        }
    }

}