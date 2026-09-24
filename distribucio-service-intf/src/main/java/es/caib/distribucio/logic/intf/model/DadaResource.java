package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseResource;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "valor", "metaDada.nom" },
        descriptionField = "valor",
        accessConstraints = {
                @ResourceAccessConstraint(
                        type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
                        grantedPermissions = { PermissionEnum.READ }
                ),
//                @ResourceAccessConstraint(
//                        type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
//                        roles = { BaseConfig.ROLE_ADMIN },
//                        grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE,  PermissionEnum.DELETE }
//                )
        },
        artifacts = {
        }
)
public class DadaResource extends BaseResource<Long> {

    protected ResourceReference<MetaDadaResource, Long> metaDada;
    protected ResourceReference<RegistreResource, Long> registre;
    protected String valor;
    protected int ordre;

    private long version = 0;

}