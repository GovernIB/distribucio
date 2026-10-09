package es.caib.distribucio.ejb.base;

import es.caib.distribucio.logic.intf.base.model.Resource;
import es.caib.distribucio.logic.intf.base.permission.ResourcePermissions;
import es.caib.distribucio.logic.intf.base.service.ResourceApiService;
import lombok.experimental.Delegate;

import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.List;

@Stateless
@PermitAll
public class ResourceApiServiceEjb extends AbstractServiceEjb<ResourceApiService> implements ResourceApiService {

	@Delegate private ResourceApiService delegateService;

	protected void setDelegateService(ResourceApiService delegateService) {
		this.delegateService = delegateService;
	}

	@Override
	public void resourceRegister(Class<? extends Resource<?>> resourceClass) {
		delegateService.resourceRegister(resourceClass);
	}

	@Override
	public List<Class<? extends Resource<?>>> resourceFindAllowed() {
		return delegateService.resourceFindAllowed();
	}

	@Override
	public ResourcePermissions permissionsCurrentUser(Class<?> resourceClass, Serializable resourceId) {
		return delegateService.permissionsCurrentUser(resourceClass, resourceId);
	}

}
