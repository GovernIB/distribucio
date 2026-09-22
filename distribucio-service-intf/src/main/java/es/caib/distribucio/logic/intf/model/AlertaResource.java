package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseAuditableResource;
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
        quickFilterFields = { "text", "error" },
        descriptionField = "text",
        accessConstraints = {
                @ResourceAccessConstraint(
                        type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                        roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA, BaseConfig.ROLE_USER },
                        grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
                )
        },
        artifacts = {
        }
)
public class AlertaResource extends BaseAuditableResource<Long> {

    private String text;
    private String error;
    private Boolean llegida;

    protected ResourceReference<ContingutResource, Long> contingut;
}