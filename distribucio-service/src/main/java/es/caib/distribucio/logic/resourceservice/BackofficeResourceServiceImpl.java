package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.helper.RegistreHelper;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.model.BackofficeResource;
import es.caib.distribucio.logic.intf.model.BackofficeResource.ReturnedMessage;
import es.caib.distribucio.logic.intf.resourceservice.BackofficeResourceService;
import es.caib.distribucio.logic.intf.util.SessioActualUtil;
import es.caib.distribucio.persist.entity.BackofficeEntity;
import es.caib.distribucio.persist.repository.BackofficeRepository;
import es.caib.distribucio.persist.resourceentity.BackofficeResourceEntity;
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
public class BackofficeResourceServiceImpl extends BaseMutableResourceService<BackofficeResource, Long, BackofficeResourceEntity> implements BackofficeResourceService {

    private final BackofficeRepository backofficeRepository;
    private final RegistreHelper registreHelper;

    @PostConstruct
    public void init() {
        register(BackofficeResource.ACTION_PROVAR_CODE, new ProvaActionExecutor());
    }

    @Override
    protected Specification<BackofficeResourceEntity> additionalSpecification(String[] namedQueries) {
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

    private class ProvaActionExecutor implements ActionExecutor<BackofficeResourceEntity, BackofficeResource.MassiveActionForm, ArrayList<?>> {

        private ReturnedMessage toReturnedMessage(String codi, Exception exception) {
            if (exception == null) {
                return new ReturnedMessage(codi, "success", "La conexió amb el backoffice " + codi + " ha sigut òptima");
            } else {
                if ("SOAPFaultException".equals(exception.getClass().getSimpleName())) {
                    return new ReturnedMessage(codi, "warning", exception.getMessage());
                } else {
                    return new ReturnedMessage(codi, "error", exception.getMessage());
                }
            }
        }

        @Override
        public ArrayList<?> exec(String code, BackofficeResourceEntity entity, BackofficeResource.MassiveActionForm params) throws ActionExecutionException {
            List<ReturnedMessage> result = new ArrayList<>();

            for (String id : params.getIds()) {
                BackofficeEntity backoffice = backofficeRepository.getReferenceById(Long.valueOf(id));
                Exception exception = registreHelper.provarConnexioBackoffice(backoffice);
                result.add( this.toReturnedMessage(backoffice.getCodi(), exception) );
            }

            return new ArrayList<>(result);
        }

        @Override
        public void onChange(Serializable id, BackofficeResource.MassiveActionForm previous, String fieldName, Object fieldValue, Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, BackofficeResource.MassiveActionForm target) {
        }
    }

}