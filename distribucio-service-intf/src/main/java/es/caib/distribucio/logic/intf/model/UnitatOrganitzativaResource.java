package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.*;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.UnitatOrganizzativaEstatEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.Date;

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
		descriptionField = UnitatOrganitzativaResource.Fields.denominacio,
		quickFilterFields = { UnitatOrganitzativaResource.Fields.codi, UnitatOrganitzativaResource.Fields.denominacio },
		accessConstraints = {
				@ResourceAccessConstraint(
						type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
						roles = { BaseConfig.ROLE_SUPER, BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA },
						grantedPermissions = { PermissionEnum.READ }
				)
		},
		artifacts = {
				@ResourceArtifact(
						type = ResourceArtifactType.FILTER,
						code = UnitatOrganitzativaResource.FILTER_CODE,
						formClass = UnitatOrganitzativaResource.FormFilter.class),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = UnitatOrganitzativaResource.ACTION_SYNCHRONIZE_INFO_CODE,
                        accessConstraints = {
                                @ResourceAccessConstraint(
                                        type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                                        roles = { BaseConfig.ROLE_ADMIN },
                                        grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
                                )
                        }),
				@ResourceArtifact(
						type = ResourceArtifactType.ACTION,
						code = UnitatOrganitzativaResource.ACTION_SYNCHRONIZE_CODE,
                        formClass = Boolean.class,
                        accessConstraints = {
                                @ResourceAccessConstraint(
                                        type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                                        roles = { BaseConfig.ROLE_ADMIN },
                                        grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
                                )
                        }),
		}
)
public class UnitatOrganitzativaResource implements Resource<Long> {

	public static final String FILTER_CODE = "FILTER";
	public static final String ACTION_SYNCHRONIZE_CODE = "SYNCHRONIZE";
	public static final String ACTION_SYNCHRONIZE_INFO_CODE = "SYNCHRONIZE_INFO";

    private Long id;
    private String codi;
    private String denominacio;
    private String nifCif;
    private String nivellAdministracio;
    private String tipusEntitatPublica;
    private String tipusUnitatOrganica;
    private String poder;
    private String sigles;
    private Long nivellJerarquic;
    private Date dataCreacioOficial;
    private Date dataSupressioOficial;
    private Date dataExtincioFuncional;
    private Date dataAnulacio;
    private UnitatOrganizzativaEstatEnumDto estat;
    private String codiPais;
    private String codiComunitat;
    private String codiProvincia;
    private String codiPostal;
    private String nomLocalitat;
    private String adressa;
    private Long tipusVia;
    private String nomVia;
    private String numVia;

    private ResourceReference<UnitatOrganitzativaResource, Long> unitatSuperior;
    private ResourceReference<UnitatOrganitzativaResource, Long> unitatArrel;
    private ResourceReference<EntitatResource, Long> entitat;

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
		private String denominacio;
		private ResourceReference<UnitatOrganitzativaResource, Long> unitatSuperior;
		private UnitatOrganizzativaEstatEnumDto estat = UnitatOrganizzativaEstatEnumDto.V;

	}

}
