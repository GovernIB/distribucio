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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.io.Serializable;

/**
 * Informació d'una entitat.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = EntitatResource.Fields.nom,
		quickFilterFields = { EntitatResource.Fields.codi, EntitatResource.Fields.nom, EntitatResource.Fields.cif },
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_SUPER },
						grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
				),
				// Nomes lectura per a la resta de rols -- necessari per a poder llistar les
				// entitats accessibles a l'usuari actual (selector d'entitat de DistribucioProvider);
				// la gestió (creació/modificació) de l'entitat continua restringida a DIS_SUPER, igual
				// que a la interfície JSP (veure WebMvcConfig.SUPER_PATHS).
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA, BaseConfig.ROLE_USER },
						grantedPermissions = { PermissionEnum.READ }
				)
		},
		artifacts = {
				@ResourceArtifact(
						type = ResourceArtifactType.FILTER,
						code = EntitatResource.FILTER_CODE,
						formClass = EntitatResource.FormFilter.class)
		}
)
public class EntitatResource extends BaseResource<Long> {

	public static final String FILTER_CODE = "FILTER";

	@NotNull
	@Size(max = 64)
	private String codi;
	@NotNull
	@Size(max = 256)
	private String nom;
	@Size(max = 1024)
	private String descripcio;
	@NotNull
	@Size(max = 9)
	private String cif;
	@NotNull
	@Size(max = 9)
	private String codiDir3;
	@Size(max = 32)
	private String colorFons;
	@Size(max = 32)
	private String colorLletra;
	private boolean activa;

	/**
	 * Camps del filtre del llistat d'entitats.
	 * <p>
	 * {@code activa} es un {@link Boolean} (no un {@code boolean}) perque el motor generic de
	 * recursos el representi amb un desplegable de tres valors -- buit, Si i No -- i aixi es
	 * pugui consultar tant les actives com les inactives. Sense valor no filtra, i el llistat
	 * mostra totes les entitats igual que el llistat JSP.
	 */
	@Getter
	@Setter
	public static class FormFilter implements Serializable {

		private static final long serialVersionUID = 1L;

		private String codi;
		private String nom;
		private String cif;
		private String codiDir3;
		private Boolean activa;

	}

}
