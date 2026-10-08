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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Monitor d'anotacions pendents d'Arxiu.
 * <p>
 * Recurs sense base de dades: les dades es llegeixen en temps real de l'històric en memòria de la tasca que guarda
 * els annexos a Arxiu.
 *
 * @author Límit Tecnologies
 */
@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
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
						code = PendentsArxiuResource.ACTION_OBTENIR_HISTOGRAMA_CODE,
						accessConstraints = {
								@ResourceAccessConstraint(
										type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
										roles = { BaseConfig.ROLE_SUPER },
										grantedPermissions = { PermissionEnum.READ }
								)
						})
		})
public class PendentsArxiuResource extends BaseResource<String> {

	public static final String ACTION_OBTENIR_HISTOGRAMA_CODE = "OBTENIR_HISTOGRAMA";

	/** Dades de configuració de la tasca i històric de les darreres execucions (de més antiga a més recent). */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class Histograma implements Serializable {
		/** Número de threads configurats per guardar annexos a Arxiu. */
		private Integer numeroThreads;
		/** Expressió 'cron' d'inactivitat de la tasca (pot ser null o buida). */
		private String expressioInactivitat;
		private List<Entrada> entrades;
	}

	/** Estat de la tasca en el moment d'una execució. */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class Entrada implements Serializable {
		private Date data;
		private int pendentArxiu;
		private int processats;
		private int errors;
		/** Temps mig de processament d'una anotació, en mil·lisegons. */
		private float tempsMitjaMs;
	}

}
