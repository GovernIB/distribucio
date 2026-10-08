package es.caib.distribucio.logic.intf.resourceservice;

import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.base.service.MutableResourceService;
import es.caib.distribucio.logic.intf.model.AclEntryResource;
import es.caib.distribucio.logic.intf.model.ResourceType;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AclEntryResourceService extends MutableResourceService<AclEntryResource, String> {

	boolean anyPermissionGranted(
			ResourceType resourceType,
			Serializable resourceId,
			List<PermissionEnum> permissions,
			String user,
			List<String> roles);

	Set<Serializable> findIdsWithAnyPermission(
			ResourceType resourceType,
			List<PermissionEnum> permissions,
			String user,
			List<String> roles);

    Integer countSidsWithPermission(
        ResourceType resourceType,
        Serializable resourceId);

    Map<Serializable, Integer> countAllSidsWithPermission(
        ResourceType resourceType,
        List<Serializable> resourcesIds);

}
