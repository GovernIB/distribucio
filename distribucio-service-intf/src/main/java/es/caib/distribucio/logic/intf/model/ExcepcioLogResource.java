package es.caib.distribucio.logic.intf.model;

import java.util.Date;

import org.springframework.data.domain.Sort;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.Resource;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

/**
 * Excepció del sistema guardada al log d'excepcions.
 * <p>
 * Recurs de només lectura i només per a DIS_SUPER, com la pantalla JSP d'excepcions
 * ("/excepcio" és a SUPER_PATHS). Les files les crea el log d'excepcions, no l'API.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = ExcepcioLogResource.Fields.message,
		quickFilterFields = {
				ExcepcioLogResource.Fields.tipus,
				ExcepcioLogResource.Fields.uri,
				ExcepcioLogResource.Fields.origen,
				ExcepcioLogResource.Fields.message},
		defaultSortFields = {@ResourceConfig.ResourceSort(field = ExcepcioLogResource.Fields.data, direction = Sort.Direction.DESC)},
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = {BaseConfig.ROLE_SUPER},
						grantedPermissions = {PermissionEnum.READ}
				)
		}
)
public class ExcepcioLogResource implements Resource<Long> {

	private Long id;
	private Date data;
	private String tipus;
	private String objectId;
	private String objectClass;
	private String uri;
	private String origen;
	private String param1;
	private String param2;
	private String message;
	private String stacktrace;
	private String entitatCodi;

	@Override
	public Long getId() {
		return id;
	}

}
