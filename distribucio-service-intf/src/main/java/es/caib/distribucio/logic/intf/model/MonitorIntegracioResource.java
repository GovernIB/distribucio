package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.Resource;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.IntegracioCodi;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.Date;

/**
 * Entrada del monitor d'integracions (log d'accions contra sistemes externs).
 * <p>
 * Recurs de només lectura: només es concedeix el permís READ (veure {@code accessConstraints}), tot i
 * que el controller extén la classe mutable genèrica, seguint el mateix patró que {@code ContingutResource}.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = MonitorIntegracioResource.Fields.descripcio,
		quickFilterFields = {
				MonitorIntegracioResource.Fields.descripcio,
				MonitorIntegracioResource.Fields.codiUsuari,
				MonitorIntegracioResource.Fields.numeroRegistre},
		defaultSortFields = {@ResourceConfig.ResourceSort(field = MonitorIntegracioResource.Fields.data, direction = Sort.Direction.DESC)},
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = {BaseConfig.ROLE_SUPER},
						grantedPermissions = {PermissionEnum.READ}
				)
		},
		artifacts = {
				@ResourceArtifact(
						type = ResourceArtifactType.FILTER,
						code = MonitorIntegracioResource.FILTER_CODE,
						formClass = MonitorIntegracioResource.FormFilter.class),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = MonitorIntegracioResource.ACTION_COUNT_ERRORS_CODE,
						formClass = MonitorIntegracioResource.FormFilter.class,
						requiresId = false,
						accessConstraints = {
								@ResourceAccessConstraint(
										type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
										roles = {BaseConfig.ROLE_SUPER},
										grantedPermissions = {PermissionEnum.READ}
								)
						})
		}
)
public class MonitorIntegracioResource implements Resource<Long> {

	public static final String FILTER_CODE = "FILTER";
	public static final String ACTION_COUNT_ERRORS_CODE = "COUNT_ERRORS";

	private Long id;
	private String codi;
	private Date data;
	private String descripcio;
	private IntegracioAccioTipusEnumDto tipus;
	private Long tempsResposta;
	private IntegracioAccioEstatEnumDto estat;
	private String codiUsuari;
	private ResourceReference<EntitatResource, Long> entitat;
	private String errorDescripcio;
	private String excepcioMessage;
	private String excepcioStacktrace;
	private String numeroRegistre;

	@Override
	public Long getId() {
		return id;
	}

	/**
	 * Camps del filtre del llistat, reutilitzats també com a paràmetres de l'acció
	 * {@link #ACTION_COUNT_ERRORS_CODE}. El camp {@code codi} és un {@link IntegracioCodi} tancat amb
	 * els codis d'integració actualment actius; dades històriques poden contenir altres codis ja
	 * retirats, que per tant no es poden seleccionar des d'aquest desplegable.
	 */
	@Getter
	@Setter
	public static class FormFilter implements Serializable {

		private static final long serialVersionUID = 1L;

		private IntegracioCodi codi;
		private ResourceReference<EntitatResource, Long> entitat;
		private IntegracioAccioEstatEnumDto estat;
		private IntegracioAccioTipusEnumDto tipus;
		private String descripcio;
		private Date dataInici;
		private Date dataFi;
		private String usuari;
		private String numeroRegistre;

	}

}
