package es.caib.distribucio.ejb.resourceService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.base.exception.ActionExecutionException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException;
import es.caib.distribucio.logic.intf.base.exception.ArtifactNotFoundException;
import es.caib.distribucio.logic.intf.base.exception.ReportGenerationException;
import es.caib.distribucio.logic.intf.base.exception.ResourceFieldNotFoundException;
import es.caib.distribucio.logic.intf.base.exception.AnswerRequiredException.AnswerValue;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.resourceservice.UsuariResourceService;
import lombok.experimental.Delegate;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

@Stateless
@RolesAllowed("**")
public class UsuariResourceServiceEjb  extends AbstractServiceEjb<UsuariResourceService> implements UsuariResourceService {

    @Delegate
    private UsuariResourceService delegate = null;

    @Override
    protected void setDelegateService(UsuariResourceService delegate) {
        this.delegate = delegate;
    }
    
	// Sense transacció de l'EJB: el canvi de codi obre la seva (timeout 1200 s) i la per defecte del contenidor
	// (uns 300 s) podria caducar amb una línia lenta (veure UsuariResourceServiceImpl#artifactActionExec).
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
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