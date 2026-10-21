package es.caib.distribucio.logic.intf.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseResource;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.domain.Sort;

/**
 * Entrada del monitor d'integracions (log d'accions contra sistemes externs).
 * <p>
 * Recurs de només lectura: només es concedeix el permís READ (veure {@code accessConstraints}), tot i
 * que el controller extén la classe mutable genèrica, seguint el mateix patró que {@code ContingutResource}.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = MonitorIntegracioParamResource.Fields.key,
		quickFilterFields = {
				MonitorIntegracioParamResource.Fields.key,
				MonitorIntegracioParamResource.Fields.value},
		defaultSortFields = {@ResourceConfig.ResourceSort(field = MonitorIntegracioParamResource.Fields.key, direction = Sort.Direction.DESC)},
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = {BaseConfig.ROLE_SUPER},
						grantedPermissions = {PermissionEnum.READ}
				)
		}
)
public class MonitorIntegracioParamResource extends BaseResource<Long> {

    private String key;
    private String value;

    @JsonIgnore
    private ResourceReference<MonitorIntegracioResource, Long> monitorIntegracio;

}
