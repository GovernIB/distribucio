package es.caib.distribucio.logic.intf.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseAuditableResource;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ContingutTipusEnumDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        descriptionField = ContingutResource.Fields.nom,
        quickFilterFields = { ContingutResource.Fields.nom },
        accessConstraints = {
                @ResourceAccessConstraint(
                        type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                        roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA },
                        grantedPermissions = { PermissionEnum.READ }
                )
        },
        artifacts = {
                @ResourceArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = ContingutResource.FILTER_CODE,
                        formClass = ContingutResource.FormFilter.class)
        }
)
public class ContingutResource extends BaseAuditableResource<Long> {

	public static final String FILTER_CODE = "FILTER";

	@NotNull
	@Size(max = 1024)
	protected String nom;
	protected ContingutTipusEnumDto tipus;
	protected int esborrat = 0;
	@Size(max = 36)
	protected String arxiuUuid;
	protected Date arxiuDataActualitzacio;

	protected ResourceReference<EntitatResource, Long> entitat;
	protected ResourceReference<ContingutResource, Long> pare;

	/**
	 * Breadcrumb dels avantpassats (columna "Bústia" del llistat legacy): de la unitat organitzativa
	 * arrel fins a la bústia pare directa, sense incloure el propi contingut. Calculat manualment a
	 * {@code ContingutResourceServiceImpl.afterConversion}, seguint el mateix algorisme que
	 * {@code ContingutHelper.getPathContingut} del manteniment legacy.
	 */
	protected List<String> path;

	/**
	 * Mateixes opcions que {@code ContingutFiltreCommand.ContenidorFiltreOpcionsEsborratEnum} del
	 * manteniment legacy: només s'utilitza per al desplegable del filtre.
	 */
	public enum EsborratFiltreEnum {
		NOMES_NO_ESBORRATS,
		NOMES_ESBORRATS,
		ESBORRATS_I_NO_ESBORRATS
	}

	@Getter
	@Setter
	public static class FormFilter implements Serializable {

		private static final long serialVersionUID = 1L;

		private String nom;
		private ContingutTipusEnumDto tipus;
		private Date dataCreacioInici;
		private Date dataCreacioFi;
		private EsborratFiltreEnum opcionsEsborrat = EsborratFiltreEnum.NOMES_NO_ESBORRATS;

	}

}