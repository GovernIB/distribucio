package es.caib.distribucio.ejb.resourceService;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.base.exception.*;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.resourceservice.ConfigTypeResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Stateless
@RolesAllowed("**")
public class ConfigTypeResourceServiceEjb extends AbstractServiceEjb<ConfigTypeResourceService> implements ConfigTypeResourceService {

	@Delegate private ConfigTypeResourceService delegateService;
	
	protected void setDelegateService(ConfigTypeResourceService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	public <P extends Serializable> Serializable artifactActionExec(String id, String code, P params)
			throws ArtifactNotFoundException, ActionExecutionException {
		return delegateService.artifactActionExec(id, code, params);
	}

	@Override
	public <P extends Serializable> Map<String, Object> artifactOnChange(ResourceArtifactType type, String code,
			String id, P previous, String fieldName, Object fieldValue, Map<String, AnswerValue> answers)
			throws ArtifactNotFoundException, ResourceFieldNotFoundException, AnswerRequiredException {
		return delegateService.artifactOnChange(type, code, id, previous, fieldName, fieldValue, answers);	
	}

	@Override
	public <P extends Serializable> List<?> artifactReportGenerateData(String id, String code, P params)
			throws ArtifactNotFoundException, ReportGenerationException {
		return delegateService.artifactReportGenerateData(id, code, params);
	}
}
