package es.caib.distribucio.logic.intf.model;

import javax.validation.constraints.Size;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseAuditableResource;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

/**
 * Moviment (trasllat entre bústies) d'un {@link ContingutResource} (pestanya "Moviments" de
 * l'historial legacy, {@code contingutLog.jsp}).
 *
 * @author Límit Tecnologies
 */
@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        descriptionField = ContingutMovimentResource.Fields.destiNom,
        accessConstraints = {
                @ResourceAccessConstraint(
                        type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                        roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA },
                        grantedPermissions = { PermissionEnum.READ }
                )
        }
)
public class ContingutMovimentResource extends BaseAuditableResource<Long> {

	protected ResourceReference<ContingutResource, Long> contingut;

	protected Long origenId;
	protected Long destiId;
	protected String origenNom;
	protected String destiNom;

	protected ResourceReference<UsuariResource, String> remitent;

	@Size(max = 3940)
	protected String comentari;
	protected Boolean perConeixement;
	@Size(max = 256)
	protected String comentariDestins;
	protected Integer numDuplicat;

}
