package es.caib.distribucio.back.resourcecontroller;

import es.caib.distribucio.back.base.controller.BaseMutableResourceController;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.AclEntryResource;
import es.caib.distribucio.logic.intf.model.ResourceType;
import es.caib.distribucio.logic.intf.resourceservice.AclEntryResourceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController("aclEntryController")
@RequestMapping(BaseConfig.API_PATH + "/aclEntries")
@Tag(name = "26. ACL Entries", description = "Gestió de regles ACL")
public class AclEntryResourceController extends BaseMutableResourceController<AclEntryResource, String> {

	private final AclEntryResourceService aclEntryService;

	@GetMapping("/anyPermissionGranted")
	public ResponseEntity<Boolean> anyPermissionGranted(
			@RequestParam ResourceType resourceType,
			@RequestParam Serializable resourceId,
			@RequestParam List<PermissionEnum> permissions,
			@RequestParam String user,
			@RequestParam List<String> roles) {
		boolean granted = aclEntryService.anyPermissionGranted(
				resourceType,
				resourceId,
				permissions,
				user,
				roles);
		return ResponseEntity.ok(granted);
	}

	@GetMapping("/findIdsWithAnyPermission")
	public ResponseEntity<Set<Serializable>> findIdsWithAnyPermission(
			@RequestParam ResourceType resourceType,
			@RequestParam List<PermissionEnum> permissions,
			@RequestParam String user,
			@RequestParam List<String> roles) {
		Set<Serializable> ids = aclEntryService.findIdsWithAnyPermission(
				resourceType,
				permissions,
				user,
				roles);
		return ResponseEntity.ok(ids);
	}

    @GetMapping("/countSidsWithPermission")
    public ResponseEntity<Integer> countSidsWithPermission(
        @RequestParam ResourceType resourceType,
        @RequestParam Serializable resourceId) {
        Integer sidsNum = aclEntryService.countSidsWithPermission(
            resourceType,
            resourceId);
        return ResponseEntity.ok(Optional.ofNullable(sidsNum).orElse(0));
    }

    @GetMapping("/countAllSidsWithPermission")
    public ResponseEntity<Map<Serializable, Integer>> countAllSidsWithPermission(
        @RequestParam ResourceType resourceType,
        @RequestParam String resourcesIds) {
        Map<Serializable, Integer> map = aclEntryService.countAllSidsWithPermission(
            resourceType,
            Arrays.stream(resourcesIds.split(",")).collect(Collectors.toList()));
        return ResponseEntity.ok(map);
    }

}
