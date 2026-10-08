package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.Resource;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Informació d'un límit de canvi d'estat per usuari.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = LimitCanviEstatResource.Fields.usuariCodi,
		quickFilterFields = {LimitCanviEstatResource.Fields.usuariCodi, LimitCanviEstatResource.Fields.descripcio},
		defaultSortFields = {@ResourceConfig.ResourceSort(field = LimitCanviEstatResource.Fields.id, direction = Sort.Direction.DESC)},
		accessConstraints = {
				// CREATE i DELETE es declaren explícitament perquè el motor genèric mostri
				// el botó de crear i l'acció d'esborrar (veure PermissionEvaluatorService.toBasePermissions).
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = {BaseConfig.ROLE_SUPER},
						grantedPermissions = {
								PermissionEnum.READ,
								PermissionEnum.WRITE,
								PermissionEnum.CREATE,
								PermissionEnum.DELETE}
				)
		}
)
public class LimitCanviEstatResource implements Resource<Long> {

	private Long id;

	@NotNull
	@Size(max = 64)
	private String usuariCodi;

	@Size(max = 256)
	private String descripcio;

	private Integer limitMinutLaboral;

	private Integer limitMinutNoLaboral;

	private Integer limitDiaLaboral;

	private Integer limitDiaNoLaboral;

	@Override
	public Long getId() {
		return id;
	}

}
