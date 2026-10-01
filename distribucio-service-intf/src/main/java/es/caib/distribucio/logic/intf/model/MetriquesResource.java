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
import java.util.Map;

/**
 * Mètriques de l'aplicació (pantalla "Mètriques" del menú Monitoritzar) i acció per obtenir-les.
 * <p>
 * Recurs sense base de dades: les dades es llegeixen en temps real del registre de mètriques de Dropwizard.
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
						code = MetriquesResource.ACTION_OBTENIR_METRIQUES_CODE,
						accessConstraints = {
								@ResourceAccessConstraint(
										type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
										roles = { BaseConfig.ROLE_SUPER },
										grantedPermissions = { PermissionEnum.READ }
								)
						})
		})
public class MetriquesResource extends BaseResource<String> {

	public static final String ACTION_OBTENIR_METRIQUES_CODE = "OBTENIR_METRIQUES";

	/**
	 * Resultat de l'acció {@link #ACTION_OBTENIR_METRIQUES_CODE}. Conserva l'estructura i els noms de camp del JSON de
	 * Dropwizard (p. ex. m1_rate, duration_units) perquè el fitxer exportat des de la pantalla sigui el mateix que
	 * generava la interfície JSP i els fitxers antics es puguin importar.
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	public static class Metriques implements Serializable {
		private String version;
		private Map<String, Object> gauges;
		private Map<String, Object> counters;
		private Map<String, Object> histograms;
		private Map<String, Object> meters;
		private Map<String, Object> timers;
	}

}
