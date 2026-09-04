package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
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
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Informació d'una bústia.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = ContingutResource.Fields.nom,
		quickFilterFields = { ContingutResource.Fields.nom },
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
                        code = BustiaResource.FILTER_CODE,
                        formClass = BustiaResource.FormFilter.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = BustiaResource.PERSPECTIVE_PERMISOS_COUNT_CODE),
        }
)
public class BustiaResource extends ContingutResource {

    public static final String FILTER_CODE = "FILTER";
    public static final String PERSPECTIVE_PERMISOS_COUNT_CODE = "PERMISOS_COUNT";

	@NotNull
	private ResourceReference<UnitatOrganitzativaResource, Long> unitatOrganitzativa;
	@Transient private boolean pendent;
	private boolean perDefecte;
	private boolean activa = true;

    @Transient private Integer permisosCount;

    @Getter
    @Setter
    public static class FormFilter implements Serializable {

        private String nom;
        private ResourceReference<UnitatOrganitzativaResource, Long> unitatSuperior;
        private ResourceReference<UnitatOrganitzativaResource, Long> unitatOrganitzativa;
        private boolean pendent;
        private boolean principal;
        private boolean activa;

        private Boolean permisPerUsuari;

    }

}
