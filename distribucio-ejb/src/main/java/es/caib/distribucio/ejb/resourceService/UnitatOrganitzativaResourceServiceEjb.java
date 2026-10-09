package es.caib.distribucio.ejb.resourceService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.distribucio.logic.intf.base.exception.ArtifactNotFoundException;
import es.caib.distribucio.logic.intf.base.exception.ReportGenerationException;
import es.caib.distribucio.logic.intf.base.exception.ResourceFieldNotFoundException;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.resourceservice.UnitatOrganitzativaResourceService;
import lombok.experimental.Delegate;

public class UnitatOrganitzativaResourceServiceEjb extends AbstractServiceEjb<UnitatOrganitzativaResourceService> implements UnitatOrganitzativaResourceService {

    @Delegate
    private UnitatOrganitzativaResourceService delegate = null;

    @Override
    protected void setDelegateService(UnitatOrganitzativaResourceService delegate) {
        this.delegate = delegate;
    }

    @Override
    public <P extends Serializable> Serializable artifactActionExec(Long id, String code, P params)
            throws ArtifactNotFoundException, ActionExecutionException {
        return delegate.artifactActionExec(id, code, params);
    }

    @Override
    public <P extends Serializable> Map<String, Object> artifactOnChange(ResourceArtifactType type, String code,
                                                                         Long id, P previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers)
            throws ArtifactNotFoundException, ResourceFieldNotFoundException, AnswerRequiredException {
        return delegate.artifactOnChange(type, code, id, previous, fieldName, fieldValue, answers);
    }

    @Override
    public <P extends Serializable> List<?> artifactReportGenerateData(Long id, String code, P params)
            throws ArtifactNotFoundException, ReportGenerationException {
        return delegate.artifactReportGenerateData(id, code, params);
    }

}