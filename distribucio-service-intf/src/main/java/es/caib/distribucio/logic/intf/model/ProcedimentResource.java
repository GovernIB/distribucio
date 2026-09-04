package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseResource;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ProcedimentEstatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Informació d'un procediment.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = ProcedimentResource.Fields.nom,
		quickFilterFields = { ProcedimentResource.Fields.codi, ProcedimentResource.Fields.nom, ProcedimentResource.Fields.codiSia },
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_ADMIN },
						grantedPermissions = {
								PermissionEnum.READ,
								PermissionEnum.WRITE,
								PermissionEnum.CREATE,
								PermissionEnum.DELETE }
				),
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_ADMIN_LECTURA },
						grantedPermissions = { PermissionEnum.READ }
				)
		},
		artifacts = {
				@ResourceArtifact(
						type = ResourceArtifactType.FILTER,
						code = ProcedimentResource.FILTER_CODE,
						formClass = ProcedimentResource.FormFilter.class),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = ProcedimentResource.ACTION_ACTUALITZAR_CODE,
						formClass = ProcedimentResource.FormAction.class,
						requiresId = false),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = ProcedimentResource.ACTION_ACTUALITZAR_PROCEDIMENT_CODE,
						requiresId = true),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = ProcedimentResource.ACTION_PROGRES_CODE,
						requiresId = false)
		}
)
public class ProcedimentResource extends BaseResource<Long> {

	public static final String FILTER_CODE = "FILTER";
	public static final String ACTION_ACTUALITZAR_CODE = "ACTUALITZAR";
	public static final String ACTION_ACTUALITZAR_PROCEDIMENT_CODE = "ACTUALITZAR_PROCEDIMENT";
	public static final String ACTION_PROGRES_CODE = "PROGRES";

	@NotNull
	@Size(max = 64)
	private String codi;

	@Size(max = 256)
	private String nom;

	@Size(max = 64)
	private String codiSia;

	private ProcedimentEstatEnumDto estat = ProcedimentEstatEnumDto.VIGENT;

	private boolean comu;

	private ResourceReference<EntitatResource, Long> entitat;

	private ResourceReference<UnitatOrganitzativaResource, Long> unitatOrganitzativa;

	/**
	 * Camps del filtre del llistat de procediments.
	 */
	@Getter
	@Setter
	public static class FormFilter implements Serializable {

		private static final long serialVersionUID = 1L;

		private String codi;
		private String nom;
		private String codiSia;
		private ProcedimentEstatEnumDto estat;
		private Boolean nomesComu;
		private ResourceReference<UnitatOrganitzativaResource, Long> unitatOrganitzativa;

	}

	@Getter
	@Setter
	public static class FormAction implements Serializable {
	}

}
