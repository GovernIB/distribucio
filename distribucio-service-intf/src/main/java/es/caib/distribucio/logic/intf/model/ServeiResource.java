package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseResource;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ServeiEstatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Informació d'un servei.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = ServeiResource.Fields.nom,
		quickFilterFields = { ServeiResource.Fields.codi, ServeiResource.Fields.nom, ServeiResource.Fields.codiSia },
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_SUPER, BaseConfig.ROLE_ADMIN },
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
						code = ServeiResource.FILTER_CODE,
						formClass = ServeiResource.FormFilter.class),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = ServeiResource.ACTION_ACTUALITZAR_CODE,
						requiresId = false),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = ServeiResource.ACTION_ACTUALITZAR_SERVEI_CODE,
						requiresId = true),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = ServeiResource.ACTION_PROGRES_CODE,
						requiresId = false)
		}
)
public class ServeiResource extends BaseResource<Long> {

	public static final String FILTER_CODE = "FILTER";
	public static final String ACTION_ACTUALITZAR_CODE = "ACTUALITZAR";
	public static final String ACTION_ACTUALITZAR_SERVEI_CODE = "ACTUALITZAR_SERVEI";
	public static final String ACTION_PROGRES_CODE = "PROGRES";

	@NotNull
	@Size(max = 64)
	private String codi;

	@Size(max = 256)
	private String nom;

	@Size(max = 64)
	private String codiSia;

	private ServeiEstatEnumDto estat = ServeiEstatEnumDto.VIGENT;

	private boolean comu;

	private ResourceReference<EntitatResource, Long> entitat;

	// TODO: private ResourceReference<UnitatOrganitzativaResource, Long> unitatOrganitzativa;

	/**
	 * Camps del filtre del llistat de serveis.
	 */
	@Getter
	@Setter
	public static class FormFilter implements Serializable {

		private static final long serialVersionUID = 1L;

		private String codi;
		private String nom;
		private String codiSia;
		private ServeiEstatEnumDto estat;
		private Boolean nomesComu;
		private Long unitatOrganitzativa;

	}

}
