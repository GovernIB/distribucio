package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseAuditableResource;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.LogObjecteTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.LogTipusEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

import java.util.List;

/**
 * Entrada del log d'accions d'un {@link ContingutResource} (pestanya "Accions" de l'historial
 * legacy, {@code contingutLog.jsp}).
 * <p>
 * Recurs de només lectura: el mapeig genèric per reflexió ja aporta {@code createdBy}/
 * {@code createdDate}/{@code createdByFullName} via {@link BaseAuditableResource}. {@code objecteNom}
 * es resol a {@code ContingutLogResourceServiceImpl.afterConversion} amb el mateix algorisme que
 * {@code ContingutLogHelper.findLogDetalls} del manteniment legacy.
 *
 * @author Límit Tecnologies
 */
@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        descriptionField = ContingutLogParamResource.Fields.valor,
        accessConstraints = {
                @ResourceAccessConstraint(
                        type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
                        grantedPermissions = { PermissionEnum.READ }
                )
        }
)
public class ContingutLogParamResource extends BaseAuditableResource<Long> {

    private long numero;

    private String valor;

    private ResourceReference<ContingutLogResource, Long> contingutLog;
}
