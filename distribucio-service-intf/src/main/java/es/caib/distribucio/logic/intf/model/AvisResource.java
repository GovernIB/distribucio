package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.Resource;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.AvisNivellEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Informació d'una avis.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        descriptionField = AvisResource.Fields.assumpte,
        quickFilterFields = { AvisResource.Fields.assumpte, AvisResource.Fields.avisNivell },
        accessConstraints = {
                // CREATE i DELETE es declaren explícitament perquè el motor genèric mostri
                // el botó de crear i l'acció d'esborrar (veure PermissionEvaluatorService.toBasePermissions).
                @ResourceAccessConstraint(
                        type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                        roles = { BaseConfig.ROLE_SUPER },
                        grantedPermissions = {
                                PermissionEnum.READ,
                                PermissionEnum.WRITE,
                                PermissionEnum.CREATE,
                                PermissionEnum.DELETE }
                )
        },
        artifacts = {
                // Activació i desactivació de l'avís, equivalents a avis/{id}/enable i
                // avis/{id}/disable de la interfície JSP. Sense formClass no obren cap
                // formulari: s'executen directament sobre la fila.
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = AvisResource.ACTION_ACTIVAR_CODE,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = AvisResource.ACTION_DESACTIVAR_CODE,
                        requiresId = true),
                // Acció massiva per activar, desactivar o eliminar múltiples avisos
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = AvisResource.ACTION_ACCIO_MASSIVA_CODE,
                        requiresId = false,
                        formClass = AvisResource.FormAccioMassiva.class)
        }
)
public class AvisResource implements Resource<Long> {

    public static final String ACTION_ACTIVAR_CODE = "ACTIVAR";
    public static final String ACTION_DESACTIVAR_CODE = "DESACTIVAR";
    public static final String ACTION_ACCIO_MASSIVA_CODE = "ACCIO_MASSIVA";

    @NotNull
    private Long id;

    @NotNull
    @Size(max = 256)
    private String assumpte;

    @NotNull
    @Size(max = 2048)
    private String missatge;

    @NotNull
    private Date dataInici;

    private Date dataFinal;

    @NotNull
    private Boolean actiu;

    @NotNull
    private AvisNivellEnumDto avisNivell;

    // Referència opcional a l'entitat: si és null l'avís és global.
    private ResourceReference<EntitatResource, Long> entitat;

    @Override
    public Long getId() {
        return id;
    }

    /**
     * Formulari de l'acció massiva per activar, desactivar o eliminar múltiples avisos.
     * Equival a l'accio massiva de la interfície JSP (AvisController.accioMassiva).
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    public static class FormAccioMassiva implements Serializable {

        private static final long serialVersionUID = 1L;

        @NotNull
        private String accio; // "activar", "desactivar", "eliminar"

        @NotNull
        private List<Long> ids;

    }

}