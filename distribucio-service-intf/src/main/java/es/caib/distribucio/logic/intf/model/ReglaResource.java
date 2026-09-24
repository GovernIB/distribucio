package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.annotation.ResourceField;
import es.caib.distribucio.logic.intf.base.model.BaseAuditableResource;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.RegistreClassificarTipusEnum;
import es.caib.distribucio.logic.intf.dto.ReglaFiltreActivaEnumDto;
import es.caib.distribucio.logic.intf.dto.ReglaPresencialEnumDto;
import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganizzativaEstatEnumDto;
import es.caib.distribucio.logic.intf.resourcevalidation.ReglaResourceValid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * Informació d'una regla per a la distribució automàtica d'anotacions de registre.
 * <p>
 * CRUD bàsic, filtres i llistat. Activar/Desactivar, l'acció massiva i Amunt/Avall/Moure (reordenació)
 * tenen {@code ActionExecutor} registrat a {@code ReglaResourceServiceImpl}. Aplicar manualment es
 * declara aquí perquè el frontend en pugui pintar el component, però encara no té cap
 * {@code ActionExecutor} registrat.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ReglaResourceValid
@ResourceConfig(
        descriptionField = ReglaResource.Fields.nom,
        quickFilterFields = { ReglaResource.Fields.nom, ReglaResource.Fields.assumpteCodiFiltre },
        accessConstraints = {
                @ResourceAccessConstraint(
                        type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                        roles = { BaseConfig.ROLE_ADMIN },
                        grantedPermissions = {
                                PermissionEnum.READ,
                                PermissionEnum.WRITE,
                                PermissionEnum.CREATE,
                                PermissionEnum.DELETE }
                ),
                @ResourceAccessConstraint(
                        type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                        roles = { BaseConfig.ROLE_ADMIN_LECTURA },
                        grantedPermissions = { PermissionEnum.READ }
                )
        },
        artifacts = {
                @ResourceArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = ReglaResource.FILTER_CODE,
                        formClass = ReglaResource.FormFilter.class),
                // Accions de fila equivalents a regla/{id}/enable, disable, aplicarPreview, up i down de
                // la interfície JSP (ReglaController). Encara sense ActionExecutor registrat: en aquesta
                // fase el frontend les intercepta abans de cridar l'API (mostra un avís de "pendent
                // d'implementar" en lloc de trucar-les).
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ReglaResource.ACTION_ACTIVAR_CODE,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ReglaResource.ACTION_DESACTIVAR_CODE,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ReglaResource.ACTION_APLICAR_MANUALMENT_CODE,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ReglaResource.ACTION_AMUNT_CODE,
                        requiresId = true),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ReglaResource.ACTION_AVALL_CODE,
                        requiresId = true),
                // Reordenació per drag&drop del llistat: mou la regla a una posició absoluta concreta.
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ReglaResource.ACTION_MOURE_CODE,
                        requiresId = true,
                        formClass = ReglaResource.FormMoure.class),
                // Acció massiva equivalent a enableMultiple/disableMultiple/deleteMultiple.
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = ReglaResource.ACTION_ACCIO_MASSIVA_CODE,
                        requiresId = false,
                        formClass = ReglaResource.FormAccioMassiva.class)
        }
)
public class ReglaResource extends BaseAuditableResource<Long> {

    public static final String ACTION_ACTIVAR_CODE = "ACTIVAR";
    public static final String ACTION_DESACTIVAR_CODE = "DESACTIVAR";
    public static final String ACTION_APLICAR_MANUALMENT_CODE = "APLICAR_MANUALMENT";
    public static final String ACTION_AMUNT_CODE = "AMUNT";
    public static final String ACTION_AVALL_CODE = "AVALL";
    public static final String ACTION_MOURE_CODE = "MOURE";
    public static final String ACTION_ACCIO_MASSIVA_CODE = "ACCIO_MASSIVA";
    public static final String FILTER_CODE = "FILTER";

    @NotNull
    @Size(max = 256)
    private String nom;

    @Size(max = 1024)
    private String descripcio;

    // ------------- FILTRE ----------------------
    @Size(max = 16)
    private String assumpteCodiFiltre;
    @Size(max = 1024)
    private String procedimentCodiFiltre;
    @Size(max = 1024)
    private String serveiCodiFiltre;
    @Size(max = 1024)
    private String tramitCodiFiltre;
    private ResourceReference<UnitatOrganitzativaResource, Long> unitatOrganitzativaFiltre;
    // Només lectura: estat de la unitat de filtre, perquè el llistat hi pugui pintar l'avís d'obsoleta
    // (equivalent a ReglaDto.estatUnitatOrganitzativa) sense haver de consultar la unitat a part.
    private UnitatOrganizzativaEstatEnumDto unitatOrganitzativaFiltreEstat;
    private ResourceReference<BustiaResource, Long> bustiaFiltre;
    private ReglaPresencialEnumDto presencial;

    // Decideix si el filtre per codi SIA es fa per procedimentCodiFiltre o serveiCodiFiltre.
    @Transient
    @ResourceField(onChangeActive = true)
    private RegistreClassificarTipusEnum tipusSia = RegistreClassificarTipusEnum.PROCEDIMENT;

    // ------------- ACCIO  ----------------------
    @ResourceField(onChangeActive = true)
    @NotNull
    private ReglaTipusEnumDto tipus = ReglaTipusEnumDto.BUSTIA;
    private ResourceReference<BustiaResource, Long> bustiaDesti;
    private ResourceReference<BackofficeResource, Long> backofficeDesti;
    private ResourceReference<UnitatOrganitzativaResource, Long> unitatDesti;
    private boolean aturarAvaluacio;

    // Activa per defecte, igual que a la UI antiga.
    private boolean activa = true;

    // Només lectura: no forma part del formulari, es calcula a la creació (veure ReglaResourceServiceImpl).
    private int ordre;

    // Només lectura: nombre total de regles de l'entitat, perquè el frontend pugui amagar "Amunt" a la
    // primera i "Avall" a l'última sense haver de conèixer l'ordre de la resta de files.
    private int totalRegles;

    // No visible al formulari: s'assigna sola a partir de l'entitat actual de la sessió.
    private ResourceReference<EntitatResource, Long> entitat;

    /**
     * Formulari de l'acció massiva per activar, desactivar o eliminar múltiples regles. Equival a
     * l'accioMassiva de la interfície JSP (enableMultiple/disableMultiple/deleteMultiple de
     * ReglaController).
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

    /**
     * Formulari de l'acció de reordenació per drag&drop del llistat, equivalent al paràmetre
     * <code>posicio</code> de {@code ReglaController.move}/{@code ReglaService.moveTo}.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    public static class FormMoure implements Serializable {
        private static final long serialVersionUID = 1L;
        @NotNull
        private Integer posicio;
    }

    /**
     * Camps del filtre del llistat de regles, equivalent a {@code ReglaFiltreCommand} de la interfície
     * JSP (reglaList.jsp).
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @FieldNameConstants
    public static class FormFilter implements Serializable {
        private static final long serialVersionUID = 1L;
        private String nom;
        private String codiAssumpte;
        private String codiSIA;
        private String codiServei;
        private String codiTramit;
        private ResourceReference<UnitatOrganitzativaResource, Long> unitat;
        private ResourceReference<BustiaResource, Long> bustia;
        private ReglaFiltreActivaEnumDto activa;
        private ReglaPresencialEnumDto presencial;
        private ReglaTipusEnumDto tipus;
        private ResourceReference<UnitatOrganitzativaResource, Long> unitatDesti;
        private ResourceReference<BustiaResource, Long> bustiaDesti;
        private ResourceReference<BackofficeResource, Long> backoffice;
    }

}
