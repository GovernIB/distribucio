package es.caib.distribucio.logic.base.helper;

import es.caib.distribucio.logic.helper.AclResourceHelper;
import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ContingutTipusEnumDto;
import es.caib.distribucio.logic.intf.model.DominiResource;
import es.caib.distribucio.logic.intf.model.MetaDadaResource;
import es.caib.distribucio.logic.intf.model.RegistreResource;
import es.caib.distribucio.logic.intf.model.ResourceType;
import es.caib.distribucio.logic.intf.service.AplicacioService;
import es.caib.distribucio.persist.resourceentity.ContingutResourceEntity;
import es.caib.distribucio.persist.resourceentity.RegistreResourceEntity;
import es.caib.distribucio.persist.resourcerepository.RegistreResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
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
    private final AplicacioService aplicacioService;

    @Override
	protected boolean checkCustomResourceAccessConstraint(
			Authentication auth,
			Serializable resourceId,
			Class<?> resourceClass,
			ResourceAccessConstraint resourceAccessConstraint,
			BasePermission[] permissions) {

        if (MetaDadaResource.class.isAssignableFrom(resourceClass) || DominiResource.class.isAssignableFrom(resourceClass)) {
            return Boolean.parseBoolean( aplicacioService.propertyFindByNom("es.caib.distribucio.permetre.metadades.registre") );
        }

        if (RegistreResource.class.isAssignableFrom(resourceClass)) {
            if (resourceId != null) {
                List<String> roles = auth.getAuthorities().stream()
                        .map(a -> a.getAuthority())
                        .collect(Collectors.toList());

                if ( roles.contains(BaseConfig.ROLE_USER) ) {
                    RegistreResourceEntity registre = registreResourceRepository.findById((Long) resourceId).get();

                    if (registre.getAgafatPer() != null) {
                        return Objects.equals(registre.getAgafatPer().getId(), auth.getName());
                    } else {
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
        List<String> roles = auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());
        if (!anyRol(roles.toArray(), resourceAccessConstraint.roles())) {
            return false;
        }
        if (RegistreResource.class.isAssignableFrom(resourceClass)) {
            if (RegistreResource.ACTION_RESERVAR_CODE.equals(code) || RegistreResource.ACTION_ALLIBERAR_CODE.equals(code)) {
                return Boolean.parseBoolean( aplicacioService.propertyFindByNom("es.caib.distribucio.anotacions.permetre.reservar") );
            }
            if (RegistreResource.ACTION_ASSIGNAR_CODE.equals(code)) {
                return Boolean.parseBoolean( aplicacioService.propertyFindByNom("es.caib.distribucio.anotacions.permetre.reservar") )
                        && Boolean.parseBoolean( aplicacioService.propertyFindByNom("es.caib.distribucio.assignar.anotacions") );
            }
        }

		return false;
	}

    public static <T> boolean anyRol(T[] array1, T[] array2) {
        if (array1 == null || array2 == null || array1.length == 0 || array2.length == 0) {
            return false;
        }

        // Ponemos el array más pequeño en el Set para optimizar memoria
        T[] menor = array1.length <= array2.length ? array1 : array2;
        T[] mayor = array1.length <= array2.length ? array2 : array1;

        Set<T> set = new HashSet<>(Arrays.asList(menor));

        for (T elemento : mayor) {
            if (set.contains(elemento)) {
                return true; // Encontramos coincidencia, salimos rápido
            }
        }
        return false;
    }

}
