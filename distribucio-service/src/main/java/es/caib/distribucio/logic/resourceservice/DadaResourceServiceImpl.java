package es.caib.distribucio.logic.resourceservice;

import es.caib.distribucio.logic.base.service.BaseMutableResourceService;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.model.DadaResource;
import es.caib.distribucio.logic.intf.resourceservice.DadaResourceService;
import es.caib.distribucio.persist.resourceentity.DadaResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DadaResourceServiceImpl extends BaseMutableResourceService<DadaResource, Long,  DadaResourceEntity> implements DadaResourceService {

    @PostConstruct
    public void init() {
    }

    protected void beforeSave(DadaResourceEntity entity, DadaResource resource) {
        entity.setValue( resource.getValue() );
    }

    @Override
    protected void beforeCreateSave(DadaResourceEntity entity, DadaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        this.beforeSave(entity, resource);
        super.beforeCreateSave(entity, resource, answers);
    }

    @Override
    protected void beforeUpdateSave(DadaResourceEntity entity, DadaResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        this.beforeSave(entity, resource);
        super.beforeUpdateSave(entity, resource, answers);
    }

    @Override
    protected void afterConversion(DadaResourceEntity entity, DadaResource resource) {
        resource.setValue( entity.getValue() );
        super.afterConversion(entity, resource);
    }
}