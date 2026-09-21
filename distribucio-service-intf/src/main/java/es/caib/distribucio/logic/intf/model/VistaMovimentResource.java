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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
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
		}
)
public class VistaMovimentResource extends BaseResource<String> {

	public static final String FILTER_CODE = "FILTER";

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
