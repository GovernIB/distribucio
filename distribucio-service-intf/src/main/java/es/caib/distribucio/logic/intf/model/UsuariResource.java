package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseResource;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Perfil de l'usuari autenticat actual (només es pot consultar/modificar el propi -- veure
 * {@code UsuariResourceServiceImpl.additionalSpecification}).
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = UsuariResource.Fields.nom,
		accessConstraints = @ResourceAccessConstraint(
				type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
				grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
		),
		// bustiaPerDefecte no es mapeja mai automàticament contra l'entitat mirall (es guarda a una
		// taula diferent, dis_bustia_default, per parella entitat+usuari) -- es gestiona íntegrament
		// als hooks completeResource/beforeUpdateSave de UsuariResourceServiceImpl.
		mappingIgnoredFields = { UsuariResource.Fields.bustiaPerDefecte }
)
public class UsuariResource extends BaseResource<String> {

	@Size(max = 200)
	private String nom;
	@Size(max = 9)
	private String nif;
	@Size(max = 200)
	private String email;
	@Size(max = 200)
	private String emailAlternatiu;
	@Size(max = 2)
	private String idioma;
	private Boolean rebreEmailsBustia;
	private Boolean rebreEmailsAgrupats;
	private Boolean emailErrorAnotacio;
	/** Entitat que s'utilitza per defecte (i com a context per a {@link #bustiaPerDefecte}). */
	private Long entitatPerDefecteId;
	/** Es guarda per parella (entitat, usuari) -- veure {@link #entitatPerDefecteId}. Nomes es
	 *  mapeja manualment (veure {@code mappingIgnoredFields}); no forma part de l'entitat mirall. */
	private Long bustiaPerDefecte;
	private Long numElementsPagina;
	private TemaAplicacioEnum temaAplicacio;
	@NotNull
	private MenuEstilEnum estilMenu = MenuEstilEnum.TEMA;
	/** Nomes es fixa en llegir el recurs (veure completeResource); no es persisteix. */
	private String[] rols;

}
