package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseResource;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Informació d'una entitat.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = EntitatResource.Fields.nom,
		quickFilterFields = { EntitatResource.Fields.codi, EntitatResource.Fields.nom, EntitatResource.Fields.cif },
		accessConstraints = @ResourceAccessConstraint(
				type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
				roles = { BaseConfig.ROLE_SUPER },
				grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
		)
)
public class EntitatResource extends BaseResource<Long> {

	@NotNull
	@Size(max = 64)
	private String codi;
	@NotNull
	@Size(max = 256)
	private String nom;
	@Size(max = 1024)
	private String descripcio;
	@NotNull
	@Size(max = 9)
	private String cif;
	@NotNull
	@Size(max = 9)
	private String codiDir3;
	@Size(max = 32)
	private String colorFons;
	@Size(max = 32)
	private String colorLletra;
	private boolean activa;

}
