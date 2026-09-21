package es.caib.distribucio.ejb.resourceService;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.base.exception.*;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.resourceservice.VistaMovimentResourceService;
import lombok.experimental.Delegate;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class VistaMovimentResourceServiceEjb extends AbstractServiceEjb<VistaMovimentResourceService> implements VistaMovimentResourceService {

    @Delegate
    private VistaMovimentResourceService delegate = null;

    @Override
    protected void setDelegateService(VistaMovimentResourceService delegate) {
        this.delegate = delegate;
    }

	@Override
	public <P extends Serializable> Serializable artifactActionExec(String id, String code, P params)
			throws ArtifactNotFoundException, ActionExecutionException {
		return delegate.artifactActionExec(id, code, params);
	}

	@Override
	public <P extends Serializable> Map<String, Object> artifactOnChange(ResourceArtifactType type, String code,
			String id, P previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers)
			throws ArtifactNotFoundException, ResourceFieldNotFoundException, AnswerRequiredException {
		return delegate.artifactOnChange(type, code, id, previous, fieldName, fieldValue, answers);
	}

	@Override
	public <P extends Serializable> List<?> artifactReportGenerateData(String id, String code, P params)
			throws ArtifactNotFoundException, ReportGenerationException {
		return delegate.artifactReportGenerateData(id, code, params);
	}
}
