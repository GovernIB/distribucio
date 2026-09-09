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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotNull;
import java.util.Date;

@SuppressWarnings("serial")
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
                        roles = { BaseConfig.ROLE_SUPER },
                        grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE,  PermissionEnum.DELETE }
                )
        },
        artifacts = {
            @ResourceArtifact(
                    type = ResourceArtifactType.PERSPECTIVE,
                    code = ConfigResource.PERSPECTIVE_ALTERNATIVE_VALUE_CODE),
            @ResourceArtifact(
                    type = ResourceArtifactType.ACTION,
                    code = ConfigResource.ACTION_SYNC_JBOSS_CODE),
            @ResourceArtifact(
                    type = ResourceArtifactType.ACTION,
                    code = ConfigResource.ACTION_RESTART_TASKS_CODE),
        })
public class ConfigResource extends BaseResource<String> {

	public static final String PERSPECTIVE_ALTERNATIVE_VALUE_CODE = "ALTERNATIVE_VALUE";
	public static final String ACTION_SYNC_JBOSS_CODE = "SYNC_JBOSS";
	public static final String ACTION_RESTART_TASKS_CODE = "RESTART_TASKS";

    @NotNull
    private String key;
    private String value;
    private String description;
    private boolean jbossProperty;

    @NotNull
    private ResourceReference<ConfigGroupResource, String> group;
    @NotNull
    private ResourceReference<ConfigTypeResource, String> type;
    
    @ResourceField(onChangeActive = true)
    private ResourceReference<EntitatResource, Long> entitat;
    private String entitatCodi;
    
    private boolean configurable;
    private int position;
    
    private ResourceReference<UsuariResource, String> lastModifiedBy;
    private Date lastModifiedDate;

    @Transient private String alternativeValue;

    @Transient
    public String getId() {
        return key;
    }
}