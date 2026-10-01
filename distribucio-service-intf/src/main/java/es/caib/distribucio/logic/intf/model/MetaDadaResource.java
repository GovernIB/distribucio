package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.annotation.ResourceField;
import es.caib.distribucio.logic.intf.base.model.BaseResource;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.MetaDadaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.MultiplicitatEnumDto;
import es.caib.distribucio.logic.intf.resourcevalidation.CodiMetaDadaNomValid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "nom" },
        descriptionField = "nom",
        accessConstraints = {
                @ResourceAccessConstraint(
                        type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
                        grantedPermissions = { PermissionEnum.READ }
                ),
                @ResourceAccessConstraint(
                        type = ResourceAccessConstraint.ResourceAccessConstraintType.CUSTOM,
                        roles = { BaseConfig.ROLE_ADMIN },
                        grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE,  PermissionEnum.DELETE }
                )
        },
        artifacts = {
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = MetaDadaResource.ACTION_REORDENAR_CODE,
                        formClass = Integer.class,
                        requiresId = true),
        }
)
public class MetaDadaResource extends BaseResource<Long> {

    public static final String ACTION_REORDENAR_CODE = "REORDENAR";

    @NotNull @CodiMetaDadaNomValid
    private String codi;
    @NotNull private String nom;
    @NotNull @ResourceField(onChangeActive = true)
    private MetaDadaTipusEnumDto tipus = MetaDadaTipusEnumDto.TEXT;
    @NotNull private MultiplicitatEnumDto multiplicitat = MultiplicitatEnumDto.M_1;
    private Object value;
    private String descripcio;
    private boolean activa = true;
    private boolean readOnly;
    private int ordre;
    private boolean noAplica;

    protected ResourceReference<EntitatResource, Long> entitat;

    @Transient private ResourceReference<DominiResource, Long> domini;

    private long version = 0;

}