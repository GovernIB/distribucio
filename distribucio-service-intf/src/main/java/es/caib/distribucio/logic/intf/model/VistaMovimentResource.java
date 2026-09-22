package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.BaseResource;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.RegistreEnviatPerEmailEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreProcesEstatSimpleEnumDto;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.logic.intf.resourcevalidation.EmailValid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * "Vista de moviments": una fila per cada moviment d'una anotació de registre (una anotació
 * movent-se d'una bústia origen a una bústia destí).
 * <p>
 * Recurs de només lectura: només es concedeix el permís READ.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = VistaMovimentResource.Fields.numero,
		quickFilterFields = { VistaMovimentResource.Fields.numero, VistaMovimentResource.Fields.titol },
		defaultSortFields = { @ResourceConfig.ResourceSort(field = VistaMovimentResource.Fields.data, direction = Sort.Direction.DESC) },
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_USER },
						grantedPermissions = { PermissionEnum.READ }
				)
		},
		artifacts = {
				@ResourceArtifact(
						type = ResourceArtifactType.FILTER,
						code = VistaMovimentResource.FILTER_CODE,
						formClass = VistaMovimentResource.FormFilter.class),
				@ResourceArtifact(
						// "Descarregar ZIP" (versió original)
						type = ResourceArtifactType.REPORT,
						code = VistaMovimentResource.REPORT_DESCARREGAR_ZIP_ORIGINAL_CODE,
						requiresId = true),
				@ResourceArtifact(
						// "Descarregar ZIP" (còpia autèntica imprimible)
						type = ResourceArtifactType.REPORT,
						code = VistaMovimentResource.REPORT_DESCARREGAR_ZIP_CAI_CODE,
						requiresId = true),
				@ResourceArtifact(
						// "Enviar via email": mateixa lògica que RegistreResource.ACTION_ENVIAR_EMAIL_CODE,
						// però declarada aquí, perquè aquell recurs només en concedeix a ROLE_ADMIN
						// i la Vista de moviments l'ha de poder executar com a ROLE_USER.
						// requiresId = false perquè l'acció massiva no passa id per la URL, sinó la llista "ids" del formulari.
						type = ResourceArtifactType.ACTION,
						code = VistaMovimentResource.ACTION_ENVIAR_EMAIL_CODE,
						formClass = VistaMovimentResource.EnviarEmailForm.class,
						accessConstraints = {
								@ResourceAccessConstraint(
										type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
										roles = { BaseConfig.ROLE_USER },
										grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
								)
						}),
				@ResourceArtifact(
						// "Reenviar": mateixa lògica que RegistreResource.ACTION_REENVIAR_CODE, però declarada
						// aquí i passant destiLogic. requiresId = false, mateix motiu que ENVIAR_EMAIL.
						type = ResourceArtifactType.ACTION,
						code = VistaMovimentResource.ACTION_REENVIAR_CODE,
						formClass = VistaMovimentResource.ReenviarForm.class,
						accessConstraints = {
								@ResourceAccessConstraint(
										type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
										roles = { BaseConfig.ROLE_USER },
										grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
								)
						}),
		}
)
public class VistaMovimentResource extends BaseResource<String> {

	public static final String FILTER_CODE = "FILTER";
	public static final String REPORT_DESCARREGAR_ZIP_ORIGINAL_CODE = "DESCARREGAR_ZIP_ORIGINAL";
	public static final String REPORT_DESCARREGAR_ZIP_CAI_CODE = "DESCARREGAR_ZIP_CAI";
	public static final String ACTION_ENVIAR_EMAIL_CODE = "ENVIAR_EMAIL";
	public static final String ACTION_REENVIAR_CODE = "REENVIAR";

	private Long idRegistre;
	private Long movimentId;
	private String numero;
	private String titol;
	private String numeroOrigen;
	private ResourceReference<UsuariResource, String> remitent;
	private Date data;
	private Date dataMoviment;
	private RegistreProcesEstatEnum procesEstat;
	private String procesError;
	private boolean pendent;
	private boolean enviatPerEmail;
	private String documentacioFisicaCodi;
	private String documentacioFisicaDescripcio;
	private String backCodi;
	/** Id del contingut destí del moviment. El faran servir les futures accions "Reenviar"/"Detalls". */
	private Long destiLogic;
	/** Nombre d'alertes no llegides de l'anotació. Determina si el botó "Llistat d'alertes" és visible. */
	private int alertesPendents;
	/** Resum dels interessats de l'anotació de registre. */
	private String interessatsString;
	/** Indica si la bústia origen/destí segueix activa, per mostrar l'avís corresponent al llistat. */
	private boolean bustiaOrigenActiva;
	private boolean bustiaDestiActiva;
	/** Nom de la regla que ha processat l'anotació. Es mostra a l'estat REGLA_PENDENT. */
	private String reglaNom;
	/** Nombre de comentaris de l'anotació de registre. */
	private int numComentaris;
	/** Enviaments per email de l'anotació, amb el format "dd/MM/yyyy HH:mm:ss destinataris". */
	@Transient
	private List<String> enviamentsPerEmail;

	private ResourceReference<BustiaResource, Long> bustiaOrigen;
	private ResourceReference<BustiaResource, Long> bustiaDesti;
	/**
	 * Breadcrumb complet de la bústia origen/destí (unitat organitzativa arrel fins a la pròpia bústia,
	 * inclosa), calculat manualment a {@code VistaMovimentResourceServiceImpl.afterConversion} igual que
	 * {@code ContingutResource.path}.
	 */
	@Transient
	private List<String> bustiaOrigenPath;
	@Transient
	private List<String> bustiaDestiPath;

	@Getter
	@Setter
	@FieldNameConstants
	public static class EnviarEmailForm implements Serializable {
		@NotNull
		@EmailValid
		private String destinatari;
		private String motiu;
		// Camps de l'acció massiva (a diferència de RegistreResource.EnviarEmailForm, no s'estén
		// MassiveForm perquè aquí l'id és String -reg_dest-, no Long): quan "massive" és true, l'id
		// no arriba per la URL i cal iterar "ids".
		private List<String> ids;
		private boolean massive;
	}

	@Getter
	@Setter
	@FieldNameConstants
	public static class ReenviarForm implements Serializable {
		/** Indica si la funcionalitat "per coneixement" està activa (propietat de configuració). */
		private boolean coneixementActiva = false;
		private String comentari;
		private List<Long> busties = new ArrayList<>();
		private List<Long> coneixement = new ArrayList<>();
		/** Camps de l'acció massiva, mateix motiu que a {@link EnviarEmailForm}. */
		private List<String> ids;
		private boolean massive;
		// NOTA: a diferència de RegistreResource.ReenviarForm no hi ha camp "ambCopia": des de la Vista
		// de moviments sempre es deixa còpia.
	}

	@Getter
	@Setter
	public static class FormFilter implements Serializable {
		private String numero;
		private String titol;
		private String numeroOrigen;
		private ResourceReference<UsuariResource, String> remitent;
		private String interessat;
		private Date dataRecepcioInici;
		private Date dataRecepcioFi;
		private ResourceReference<BustiaResource, Long> bustiaOrigen;
		private ResourceReference<BustiaResource, Long> bustiaDesti;
		/** Inclou les bústies inactives entre les opcions del desplegable "bustiaOrigen". */
		private boolean mostrarInactivesOrigen;
		/** Inclou les bústies inactives entre les opcions del desplegable "bustiaDesti". */
		private boolean mostrarInactives;
		private RegistreProcesEstatSimpleEnumDto estat = RegistreProcesEstatSimpleEnumDto.PENDENT;
		private RegistreEnviatPerEmailEnumDto enviatPerEmail;
	}

}
