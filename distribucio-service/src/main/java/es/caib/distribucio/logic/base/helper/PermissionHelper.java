package es.caib.distribucio.logic.base.helper;

import es.caib.distribucio.logic.helper.AclResourceHelper;
import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ContingutTipusEnumDto;
import es.caib.distribucio.logic.intf.model.RegistreResource;
import es.caib.distribucio.logic.intf.model.ResourceType;
import es.caib.distribucio.persist.resourceentity.ContingutResourceEntity;
import es.caib.distribucio.persist.resourceentity.RegistreResourceEntity;
import es.caib.distribucio.persist.resourcerepository.RegistreResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mètodes per a la comprovació de permisos.
 *
 * @author Límit Tecnologies
 */
@Component
@RequiredArgsConstructor
public class PermissionHelper extends BasePermissionHelper {

    private final RegistreResourceRepository registreResourceRepository;
    private final AclResourceHelper aclResourceHelper;

    @Override
	protected boolean checkCustomResourceAccessConstraint(
			Authentication auth,
			Serializable resourceId,
			Class<?> resourceClass,
			ResourceAccessConstraint resourceAccessConstraint,
			BasePermission[] permissions) {

        if (RegistreResource.class.isAssignableFrom(resourceClass)) {
            if (resourceId != null) {
                List<String> roles = auth.getAuthorities().stream()
                        .map(a -> a.getAuthority())
                        .collect(Collectors.toList());

                if ( roles.contains(BaseConfig.ROLE_USER) ) {
                    RegistreResourceEntity registre = registreResourceRepository.findById((Long) resourceId).get();
                    ContingutResourceEntity<?> pare = registre.getPare();
                    if (pare.getTipus() == ContingutTipusEnumDto.BUSTIA) {
                        return aclResourceHelper.anyPermissionGranted(
                                ResourceType.BUSTIA,
                                pare.getId(),
                                List.of(PermissionEnum.WRITE),
                                auth.getName(),
                                roles
                        );
                    }
                }
            }
        }

		return false;
	}

	@Override
	protected boolean checkCustomResourceArtifactAccessConstraint(
			Authentication auth,
			Class<?> resourceClass,
			ResourceArtifactType type,
			String code,
			ResourceAccessConstraint resourceAccessConstraint) {
		return false;
	}

}
