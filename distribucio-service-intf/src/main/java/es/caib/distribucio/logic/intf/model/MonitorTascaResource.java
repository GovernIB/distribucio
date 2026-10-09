package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseResource;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Tasca en segon pla (pestanya "Tasques en segon pla" del monitor de sistema). L'identificador és el codi de la tasca.
 * <p>
 * Recurs sense base de dades: l'estat de les tasques es manté en memòria (MonitorTasquesService). Només accessible
 * per al rol DIS_SUPER; l'acció porta les seves pròpies restriccions perquè, si no, comprovaria el permís WRITE sobre el recurs.
 *
 * @author Límit Tecnologies
 */
@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		quickFilterFields = { MonitorTascaResource.Fields.nom, MonitorTascaResource.Fields.observacions },
		descriptionField = MonitorTascaResource.Fields.nom,
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_SUPER },
						grantedPermissions = { PermissionEnum.READ }
				)
		},
		artifacts = {
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = MonitorTascaResource.ACTION_REINICIAR_CODE,
						formClass = MonitorTascaResource.ReiniciarForm.class,
						accessConstraints = {
								@ResourceAccessConstraint(
										type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
										roles = { BaseConfig.ROLE_SUPER },
										grantedPermissions = { PermissionEnum.WRITE }
								)
						})
		})
public class MonitorTascaResource extends BaseResource<String> {

	public static final String ACTION_REINICIAR_CODE = "REINICIAR_TASCA";

	private String nom;
	private String estat;
	private String dataInici;
	private String tempsExecucio;
	private String properaExecucio;
	private String observacions;

	/** Codis de les tasques a reiniciar. */
	@Getter
	@Setter
	public static class ReiniciarForm implements Serializable {
		@NotNull
		@NotEmpty
		private List<String> ids;
	}

}
