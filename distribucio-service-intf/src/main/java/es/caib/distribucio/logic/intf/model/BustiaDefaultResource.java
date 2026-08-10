package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseResource;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;

/**
 * Bústia per defecte d'un usuari per a una entitat determinada.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		accessConstraints = @ResourceAccessConstraint(
				type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
				grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
		),
		// Els camps de relació es resolen manualment a BustiaDefaultResourceServiceImpl (no hi ha
		// camps amb el mateix nom a BustiaDefaultResourceEntity, que emmagatzema les relacions com a
		// referències a entitat/bustia/usuari, no com a ids).
		mappingIgnoredFields = { BustiaDefaultResource.Fields.entitatId, BustiaDefaultResource.Fields.bustiaId, BustiaDefaultResource.Fields.usuariId }
)
public class BustiaDefaultResource extends BaseResource<Long> {

	@NotNull
	private Long entitatId;
	@NotNull
	private Long bustiaId;
	@NotNull
	private String usuariId;

}
