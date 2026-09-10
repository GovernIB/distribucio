package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseAuditableResource;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ElementTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaContingutEstatDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "elementNom", "estat" },
        descriptionField = "elementNom",
        accessConstraints = {
                @ResourceAccessConstraint(
                        type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                        roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_USER },
                        grantedPermissions = { PermissionEnum.READ, PermissionEnum.CREATE }
                )
        }
)
public class ExecucioMassivaContingutResource extends BaseAuditableResource<Long> {

    private ExecucioMassivaContingutEstatDto estat;
    private Date dataCreacio;
    private Date dataInici;
    private Date dataFi;
    private Long elementId;
    private String elementNom;
    private ElementTipusEnumDto elementTipus;
    private String error;
    private String missatge;
    private int ordre;

    private ResourceReference<ExecucioMassivaResource, Long> execucioMassiva;

}