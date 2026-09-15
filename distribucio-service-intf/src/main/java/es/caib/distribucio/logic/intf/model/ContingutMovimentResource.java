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
import org.springframework.data.annotation.Transient;

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

	protected Long origenId;
    protected String origenNom;
	protected Long destiId;
	protected String destiNom;

    @Size(max = 3940)
    protected String comentari;
    @Size(max = 256)
    protected String comentariDestins;
    protected Boolean perConeixement;
    protected Integer numDuplicat;

	protected ResourceReference<UsuariResource, String> remitent;
    protected ResourceReference<ContingutResource, Long> contingut;

    @Transient private ResourceReference<UnitatOrganitzativaResource, Long> bustiaOrigen;
    @Transient private ResourceReference<UnitatOrganitzativaResource, Long> bustiaDesti;
    @Transient private ResourceReference<UnitatOrganitzativaResource, Long> unitatOrganitzativaOrigen;
    @Transient private ResourceReference<UnitatOrganitzativaResource, Long> unitatOrganitzativaDesti;

}
