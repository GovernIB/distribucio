package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.annotation.ResourceField;
import es.caib.distribucio.logic.intf.base.model.BaseResource;
import es.caib.distribucio.logic.intf.base.model.FileReference;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.PrincipalTipusEnumDto;
import es.caib.distribucio.logic.intf.resourcevalidation.ValidImageFile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.io.Serializable;
import java.util.List;

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
				// CREATE i DELETE són permisos independents de WRITE (veure
				// PermissionEvaluatorService.toBasePermissions): sense declarar-los el motor
				// genèric no ofereix ni el botó de crear de la graella ni l'acció d'esborrar de
				// la fila, encara que l'usuari pugui modificar el recurs.
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_SUPER },
						grantedPermissions = {
								PermissionEnum.READ,
								PermissionEnum.WRITE,
								PermissionEnum.CREATE,
								PermissionEnum.DELETE }
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
						formClass = EntitatResource.FormFilter.class),
				// Activació i desactivació de l'entitat, equivalents a entitat/{id}/enable i
				// entitat/{id}/disable de la interfície JSP. Sense formClass no obren cap
				// formulari: s'executen directament sobre la fila. Sense accessConstraints
				// pròpies requereixen el permís WRITE sobre el recurs -- és a dir, DIS_SUPER
				// (veure BasePermissionHelper.checkResourceArtifactPermission).
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = EntitatResource.ACTION_ACTIVAR_CODE,
						requiresId = true),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = EntitatResource.ACTION_DESACTIVAR_CODE,
						requiresId = true),
				// Manteniment dels permisos de l'entitat, equivalent a entitat/{id}/permis de la
				// interficie JSP (EntitatPermisSuperController). Les dues perspectives omplen els
				// camps permisosCount i permisos, i les dues accions deleguen en els mateixos
				// metodes d'EntitatService que fa servir la JSP. Com la resta d'artefactes sense
				// accessConstraints propies: lectura de les perspectives amb el permis READ i
				// execucio de les accions amb el permis WRITE (o sia, DIS_SUPER).
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = EntitatResource.PERSPECTIVE_PERMISOS_COUNT_CODE),
				@ResourceArtifact(
						type = ResourceArtifactType.PERSPECTIVE,
						code = EntitatResource.PERSPECTIVE_PERMISOS_CODE),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = EntitatResource.ACTION_PERMIS_GUARDAR_CODE,
						requiresId = true,
						formClass = EntitatResource.FormPermis.class),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = EntitatResource.ACTION_PERMIS_ESBORRAR_CODE,
						requiresId = true,
						formClass = EntitatResource.FormPermisEsborrar.class)
		}
)
public class EntitatResource extends BaseResource<Long> {

	public static final String FILTER_CODE = "FILTER";
	public static final String ACTION_ACTIVAR_CODE = "ACTIVAR";
	public static final String ACTION_DESACTIVAR_CODE = "DESACTIVAR";
	public static final String PERSPECTIVE_PERMISOS_CODE = "PERMISOS";
	public static final String PERSPECTIVE_PERMISOS_COUNT_CODE = "PERMISOS_COUNT";
	public static final String ACTION_PERMIS_GUARDAR_CODE = "PERMIS_GUARDAR";
	public static final String ACTION_PERMIS_ESBORRAR_CODE = "PERMIS_ESBORRAR";

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
	private byte[] logoImgBytes;
	@Size(max = 32)
	private String colorFonsDark;
	@Size(max = 32)
	private String colorLletraDark;
	private byte[] logoImgBytesDark;

	@Transient
	@ResourceField(onChangeActive = true)
	@ValidImageFile
	private FileReference logoImgFile;

	@Transient
	@ResourceField(onChangeActive = true)
	@ValidImageFile
	private FileReference logoImgFileDark;

	/** Per defecte cert, com fa la interficie JSP: EntitatEntity.getBuilder no permet indicar
	 *  l'estat i les entitats sempre es creen actives. */
	private boolean activa = true;

	/**
	 * Nombre de permisos concedits sobre l'entitat, l'equivalent d'EntitatDto.getPermisosCount()
	 * de la interficie JSP. Nomes te valor si s'ha demanat la perspectiva PERMISOS_COUNT; en cas
	 * contrari es null. No es cap columna de dis_entitat: el mapeig recurs/entitat l'ignora
	 * perque EntitatResourceEntity no te cap camp amb aquest nom.
	 */
	private Integer permisosCount;
	/**
	 * Llistat de permisos concedits sobre l'entitat. Nomes te valor si s'ha demanat la
	 * perspectiva PERMISOS. Igual que permisosCount, no es cap columna de dis_entitat.
	 */
	private List<Permis> permisos;

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

	/**
	 * Un permis concedit sobre l'entitat, tal com el mostra el llistat de la interficie JSP
	 * (entitatPermis.jsp). Es la projeccio d'un {@code PermisDto} amb nomes els tres permisos
	 * que l'entitat admet, amb els noms de la JSP:
	 * <ul>
	 * <li>{@code administracio} -&gt; {@code PermisDto.administration} (ADMINISTRATION),</li>
	 * <li>{@code adminLectura} -&gt; {@code PermisDto.adminLectura} (ADMIN_LECTURA),</li>
	 * <li>{@code usuari} -&gt; {@code PermisDto.read} (READ).</li>
	 * </ul>
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	@FieldNameConstants
	public static class Permis implements Serializable {

		private static final long serialVersionUID = 1L;

		/** Identificador de l'entrada d'ACL (dis_acl_entry), el que necessita l'accio d'esborrar. */
		private Long id;
		private PrincipalTipusEnumDto principalTipus;
		private String principalNom;
		private boolean administracio;
		private boolean adminLectura;
		private boolean usuari;

	}

	/**
	 * Formulari de l'accio PERMIS_GUARDAR: crea el permis si el principal encara no en te, i el
	 * substitueix si ja n'hi ha (es el que fa {@code PermisosHelper.updatePermis}, que sempre
	 * neteja les entrades d'ACL del principal abans d'afegir-hi les noves). Es l'equivalent del
	 * {@code PermisCommand} que envia entitatPermisForm.jsp.
	 * <p>
	 * L'{@code id} no s'utilitza per a desar -- el permis s'identifica pel principal --, pero
	 * viatja en el formulari perque la interficie sapiga si es una alta o una modificacio i
	 * bloquegi els camps del principal, com fa la JSP.
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	@FieldNameConstants
	public static class FormPermis implements Serializable {

		private static final long serialVersionUID = 1L;

		private Long id;
		@NotNull
		private PrincipalTipusEnumDto principalTipus;
		@NotEmpty
		@Size(max = 64)
		private String principalNom;
		private boolean administracio;
		private boolean adminLectura;
		private boolean usuari;

	}

	/**
	 * Formulari de l'accio PERMIS_ESBORRAR. Nomes duu l'identificador de l'entrada d'ACL a
	 * esborrar; l'entitat arriba com a id de l'accio. No es mostra mai: la interficie demana
	 * confirmacio i crida l'accio directament.
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	@FieldNameConstants
	public static class FormPermisEsborrar implements Serializable {

		private static final long serialVersionUID = 1L;

		@NotNull
		private Long permisId;

	}

}
