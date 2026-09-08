package es.caib.distribucio.logic.intf.model;

import java.util.List;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseAuditableResource;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.LogObjecteTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.LogTipusEnumDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

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
        descriptionField = ContingutLogResource.Fields.tipus,
        accessConstraints = {
                // Recurs de només lectura: encara que el controller és Mutable (motor genèric), no es
                // concedeix cap permís d'escriptura, només READ.
                @ResourceAccessConstraint(
                        type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                        roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA },
                        grantedPermissions = { PermissionEnum.READ }
                )
        },
        mappingIgnoredFields = { ContingutLogResource.Fields.params }
)
public class ContingutLogResource extends BaseAuditableResource<Long> {

	protected LogTipusEnumDto tipus;

	protected ResourceReference<ContingutResource, Long> contingut;

	protected String objecteId;
	protected LogObjecteTipusEnumDto objecteTipus;
	protected LogTipusEnumDto objecteLogTipus;
	/**
	 * Nom llegible de l'objecte modificat (p.ex. el nom del document/expedient/registre), resolt a
	 * {@code ContingutLogResourceServiceImpl.afterConversion} -- mateix algorisme que
	 * {@code ContingutLogHelper.findLogDetalls} del manteniment legacy.
	 */
	protected String objecteNom;

	protected ResourceReference<ContingutMovimentResource, Long> contingutMoviment;
	/** Log "pare" quan aquest és una acció secundària (p.ex. una modificació sobre un subobjecte). */
	protected ResourceReference<ContingutLogResource, Long> pare;

	/**
	 * Paràmetres addicionals del log, ordenats -- equivalent a {@code ContingutLogDto.params} del
	 * manteniment legacy. Calculat a {@code ContingutLogResourceServiceImpl.afterConversion} a
	 * partir de la col·lecció JPA {@code ContingutLogParamEntity} (reutilitzada tal qual).
	 */
	protected List<String> params;

}
