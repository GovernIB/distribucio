package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.annotation.ResourceField;
import es.caib.distribucio.logic.intf.base.model.BaseResource;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.InterficieUsuariEnumDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

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
		mappingIgnoredFields = { UsuariResource.Fields.bustiaPerDefecte },
		artifacts = {
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = UsuariResource.ACTION_CANVI_CODIS_CODE,
						requiresId = false,
						formClass = UsuariResource.CanviCodisForm.class,
						accessConstraints = {
								@ResourceAccessConstraint(
										type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
										roles = { BaseConfig.ROLE_SUPER },
										grantedPermissions = { PermissionEnum.WRITE }
								)
						})
		}
)
public class UsuariResource extends BaseResource<String> {

	public static final String ACTION_CANVI_CODIS_CODE = "CANVI_CODIS";

	@Size(max = 200)
	private String nom;
	@Size(max = 9)
	private String nif;
	@Size(max = 200)
	private String email;
	@Size(max = 200)
	private String emailAlternatiu;
	/**
	 * Idioma de la interfície. Es guarda com a text (columna {@code dis_usuari.idioma}) i no com
	 * a enumerat perquè les dades existents barregen majúscules i minúscules: la interfície JSP
	 * hi desa el nom de la constant d'{@link es.caib.distribucio.logic.intf.dto.IdiomaEnumDto}
	 * ("CA"/"ES") mentre que l'alta automàtica d'usuaris hi posa "ca" (veure
	 * {@code AplicacioServiceImpl}), i un {@code @Enumerated} petaria en llegir aquestes files.
	 * <p>
	 * {@code enumType} fa que el motor genèric el publiqui com a camp d'opcions, que aporta el
	 * {@code FieldOptionsProvider} registrat a {@code UsuariResourceServiceImpl} amb els mateixos
	 * valors que el desplegable de la JSP.
	 */
	@Size(max = 2)
	@ResourceField(enumType = true)
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
	/** Darrer rol amb que ha operat l'usuari (columna dis_usuari.rol_actual, la mateixa que fa
	 *  servir la interficie JSP): la interficie REACT el recupera en iniciar sessio i el desa
	 *  quan es canvia de rol des del selector. */
	@Size(max = 64)
	private String rolActual;
	/**
	 * Interficie amb la que l'usuari entra a l'aplicacio (columna dis_usuari.interficie_usuari,
	 * la mateixa que fa servir la interficie JSP). Es opcional a posta: sense valor mana la
	 * propietat es.caib.distribucio.interface.defecte (veure DistribucioController.get()).
	 */
	private InterficieUsuariEnumDto interficieUsuari;
	/** Nomes es fixa en llegir el recurs (veure completeResource); no es persisteix. */
	private String[] rols;

	/** Paràmetres de l'acció {@link #ACTION_CANVI_CODIS_CODE}. */
	@Getter
	@Setter
	public static class CanviCodisForm implements Serializable {
		private static final long serialVersionUID = 1L;
		/** Una entrada per línia amb el format "codiActual=codiNou". */
		@NotBlank
		@Size(max = 20000)
		private String mapeig;
		/** Si el codi nou ja correspon a un usuari existent, unifica els dos usuaris. Sense marcar, la línia s'omet */
		private Boolean unificaUsuarisExistents;
	}

	/** Resultat de l'acció {@link #ACTION_CANVI_CODIS_CODE}: una entrada per cada línia processada. */
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CanviCodisResultat implements Serializable {
		private static final long serialVersionUID = 1L;
		private List<CanviCodisLinia> linies;
		/** Temps total de l'acció (ms). */
		private long duradaTotal;
	}

	/** Resultat d'una línia "codiActual=codiNou". El text de cada estat el posa la interfície. */
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CanviCodisLinia implements Serializable {
		private static final long serialVersionUID = 1L;
		/** Número de línia (1..n) dins el text de l'entrada. */
		private int numLinia;
		private String codiAntic;
		private String codiNou;
		private CanviCodisEstat estat;
		private Long registresModificats;
		/** Temps del canvi d'aquesta línia (ms). */
		private Long durada;
		/** Detall de l'error (només amb estat ERROR) o text original de la línia (amb FORMAT_INCORRECTE). */
		private String missatge;
	}

	public enum CanviCodisEstat {
		OK,
		ERROR,
		FORMAT_INCORRECTE,
		ANTIC_NO_EXISTEIX,
		NOU_EXISTEIX_SALTAT,
		DUPLICAT
	}

}
