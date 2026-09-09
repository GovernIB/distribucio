package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
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
        quickFilterFields = { "key", "value", "description" },
        descriptionField = "description",
        accessConstraints = {
                @ResourceAccessConstraint(
                        type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
                        grantedPermissions = { PermissionEnum.READ }
                ),
                @ResourceAccessConstraint(
                        type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                        roles = { BaseConfig.ROLE_ADMIN },
                        grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE,  PermissionEnum.DELETE }
                )
        },
        artifacts = {
                @ResourceArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = BackofficeResource.FILTER_CODE,
                        formClass = BackofficeResource.FormFilter.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = BackofficeResource.ACTION_PROVAR_CODE,
                        formClass = BackofficeResource.MassiveActionForm.class),
        }
)
public class BackofficeResource extends BaseResource<Long> {

    public static final String FILTER_CODE = "FILTER";
    public static final String ACTION_PROVAR_CODE = "PROVAR";

    @NotNull private String codi;
    @NotNull private String nom;
    @NotNull private String url;
    private String usuari;
    private String contrasenya;
    private Integer intents;
    private Integer tempsEntreIntents;

    private BackofficeTipusEnumDto tipus;

    private Boolean enviamentEmail;
    private String emailResponsable;
    private LocalDateTime darrerEmailResponsable;

    private ResourceReference<EntitatResource, Long> entitat;

    @Getter
    @Setter
    public static class FormFilter implements Serializable {
        private String codi;
        private String nom;
        private String url;
        private BackofficeTipusEnumDto tipus;
    }

    @Getter
    @Setter
    public static class MassiveActionForm implements Serializable {
        @NotEmpty
        private List<String> ids;
        private boolean massive;
    }

    @Getter
    @AllArgsConstructor
    public static class ReturnedMessage implements Serializable {
        private String codi;
        private String severity;
        private String message;
    }
}