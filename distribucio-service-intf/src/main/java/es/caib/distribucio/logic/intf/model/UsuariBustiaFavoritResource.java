package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.annotation.ResourceField;
import es.caib.distribucio.logic.intf.base.model.BaseResource;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.InterficieUsuariEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Perfil de l'usuari autenticat actual (només es pot consultar/modificar el propi -- veure
 * {@code UsuariResourceServiceImpl.additionalSpecification}).
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = UsuariBustiaFavoritResource.Fields.bustia,
		accessConstraints = {
                @ResourceAccessConstraint(
                        type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
                        grantedPermissions = {PermissionEnum.READ}
                )
        }
)
public class UsuariBustiaFavoritResource extends BaseResource<Long> {

    protected ResourceReference<BustiaResource, Long> bustia;
    protected ResourceReference<UsuariResource, String> usuari;

}
