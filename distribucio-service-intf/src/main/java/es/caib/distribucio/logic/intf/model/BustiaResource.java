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
 * Informació d'una bústia.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = BustiaResource.Fields.nom,
		quickFilterFields = { BustiaResource.Fields.nom },
		accessConstraints = @ResourceAccessConstraint(
				type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
				roles = { BaseConfig.ROLE_SUPER, BaseConfig.ROLE_ADMIN },
				grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
		)
)
public class BustiaResource extends BaseResource<Long> {

	@NotNull
	private Long entitatId;
	@NotNull
	@Size(max = 1024)
	private String nom;
	@NotNull
	private Long unitatOrganitzativaId;
	private boolean perDefecte;
	private boolean activa;

}
