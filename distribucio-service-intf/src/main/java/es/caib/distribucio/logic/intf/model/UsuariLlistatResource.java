package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseResource;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.domain.Sort;

/**
 * Llistat de només lectura de tots els usuaris de l'aplicació, pensat per a emplenar
 * desplegables d'usuaris. L'id és el codi de l'usuari.
 * <p>
 * És un recurs diferent de {@link UsuariResource}, que és el perfil de l'usuari autenticat i
 * només retorna el propi usuari. Només s'exposen el codi i el nom; el NIF no es publica però
 * també es cerca amb el filtre ràpid (veure {@code UsuariLlistatResourceServiceImpl}). S'exclouen
 * els usuaris de sistema amb el mateix criteri que RIPEA.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = UsuariLlistatResource.Fields.nom,
		// "id" és el codi de l'usuari i ve de BaseResource, per això no té constant a Fields
		quickFilterFields = { "id", UsuariLlistatResource.Fields.nom },
		defaultSortFields = { @ResourceConfig.ResourceSort(field = UsuariLlistatResource.Fields.nom, direction = Sort.Direction.ASC) },
		accessConstraints = @ResourceAccessConstraint(
				type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
				grantedPermissions = { PermissionEnum.READ }
		)
)
public class UsuariLlistatResource extends BaseResource<String> {

	private String nom;

}
