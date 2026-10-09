package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseAuditableResource;
import es.caib.distribucio.logic.intf.base.model.BaseResource;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.BackofficeTipusEnumDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = ContingutComentariResource.Fields.text,
        descriptionField = ContingutComentariResource.Fields.text,
        accessConstraints = {
                @ResourceAccessConstraint(
                        type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
                        grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE }
                ),
        }
)
public class ContingutComentariResource extends BaseAuditableResource<Long> {

    protected String text;

    protected ResourceReference<ContingutResource, Long> contingut;
}