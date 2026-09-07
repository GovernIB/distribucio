package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = BustiaResource.ACTION_ACTIVAR_CODE,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = BustiaResource.ACTION_DESACTIVAR_CODE,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = BustiaResource.ACTION_PRINCIPAL_CODE,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = BustiaResource.ACTION_MOURE_ANOTACIO_CODE,
                        requiresId = true,
                        formClass = BustiaResource.MoureAnotacioForm.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.REPORT,
                        code = BustiaResource.REPORT_USUARIS_BUSTIA_CODE,
                        formClass = BustiaResource.UsuariBustiaForm.class),
        }
)
public class BustiaResource extends ContingutResource {

    public static final String FILTER_CODE = "FILTER";
    public static final String PERSPECTIVE_PERMISOS_COUNT_CODE = "PERMISOS_COUNT";
    public static final String ACTION_ACTIVAR_CODE = "ACTIVAR";
    public static final String ACTION_DESACTIVAR_CODE = "DESACTIVAR";
    public static final String ACTION_PRINCIPAL_CODE = "PRINCIPAL";
    public static final String ACTION_MOURE_ANOTACIO_CODE = "MOURE_ANOTACIO";
    public static final String REPORT_USUARIS_BUSTIA_CODE = "USUARIS_BUSTIA";

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

    @Getter
    @Setter
    public static class MoureAnotacioForm implements Serializable {

        @NotNull private ResourceReference<BustiaResource, Long> bustia;
        private String comment;

    }

    @Getter
    @Setter
    public static class UsuariBustiaForm implements Serializable {

        private String filter;
        private String[] namedQueries;

    }

}
