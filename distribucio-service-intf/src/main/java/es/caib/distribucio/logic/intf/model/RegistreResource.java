package es.caib.distribucio.logic.intf.model;

import es.caib.distribucio.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.distribucio.logic.intf.base.annotation.ResourceArtifact;
import es.caib.distribucio.logic.intf.base.annotation.ResourceConfig;
import es.caib.distribucio.logic.intf.base.model.ResourceArtifactType;
import es.caib.distribucio.logic.intf.base.model.ResourceReference;
import es.caib.distribucio.logic.intf.base.permission.PermissionEnum;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.RegistreNombreAnnexesEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreProcesEstatSimpleEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreTipusDocFisicaEnumDto;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatSistraEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.Date;

/**
 * Informació d'una bústia.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
		descriptionField = ContingutResource.Fields.nom,
		quickFilterFields = { ContingutResource.Fields.nom },
		accessConstraints = {
                @ResourceAccessConstraint(
                        type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                        roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA, BaseConfig.ROLE_USER },
                        grantedPermissions = { PermissionEnum.READ }
                )
        },
        artifacts = {
                @ResourceArtifact(
                        type = ResourceArtifactType.FILTER,
                        code = RegistreResource.FILTER_CODE,
                        formClass = RegistreResource.FormFilter.class),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = RegistreResource.PERSPECTIVE_DARRER_MOVIMENT_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.PERSPECTIVE,
                        code = ContingutResource.PERSPECTIVE_COMMENT_NUM_CODE),
                @ResourceArtifact(
                        type = ResourceArtifactType.REPORT,
                        code = RegistreResource.REPORT_INFORME_LOGS_CODE,
                        requiresId = true),
        }
)
public class RegistreResource extends ContingutResource {

    public static final String FILTER_CODE = "FILTER";
    public static final String PERSPECTIVE_DARRER_MOVIMENT_CODE = "DARRER_MOVIMENT";
    public static final String REPORT_INFORME_LOGS_CODE = "INFORME_LOGS";

    private String registreTipus;
    private String numero;
    private Date data;
    private String identificador;
    private String extracte;
    private String procedimentCodi;
    private String serveiCodi;
    private String referencia;
    private String expedientNumero;
    private String expedientArxiuUuid;
    private String numeroOrigen;
    private String transportNumero;
    private String usuariContacte;
    private String observacions;
    private String exposa;
    private String solicita;
    private String motiuRebuig;
    private Date procesData;
    private RegistreProcesEstatEnum procesEstat;
    /** Indica si l'anotació de registre està pendent de processament (true) o processada (false). */
    private Boolean pendent;
    private Date dataOrigen;
    private RegistreProcesEstatSistraEnum procesEstatSistra;
    private String identificadorTramitSistra;
    private String identificadorProcedimentSistra;
    private String procesError;
    private int procesIntents;
    // Date when regla change state of anotacio to RegistreProcesEstatEnum.BACK_PENDENT
    private Date backPendentData;
    // Date when backoffice called BackofficeIntegracioWsService.canviEstat(RegistreProcesEstatEnum.BACK_REBUDA) method
    private Date backRebudaData;
    // Date when change state of anotacio to RegistreProcesEstatEnum.BACK_COMUNICADA
    private Date backComunicadaData;
    // Date when backoffice called BackofficeIntegracioWsService.canviEstat(RegistreProcesEstatEnum.BACK_PROCESADA) or (RegistreProcesEstatEnum.BACK_REBUTJADA) or (RegistreProcesEstatEnum.BACK_ERROR) method
    private Date backProcesRebutjErrorData;
    private String backObservacions;
    // Date when distribucio will retry to send anotacio to backoffice
    private Date backRetryEnviarData;

    private Boolean presencial;

    private boolean justificantDescarregat;

    private String justificantArxiuUuid;
    private Boolean llegida;
    private Date dataTancament;
    private Boolean arxiuTancat;
    private Boolean arxiuTancatError;
    /** Com que es pot reenviar un registre a una altra bústia amb el mateix número de registre es posa el número de còpia per distingir-los. */
    private Integer numeroCopia;
    private boolean enviatPerEmail;

    private boolean reactivat;

    private boolean sobreescriure;

    /** Conté el recompte del número d'annexos en estat esborrany */
    private int annexosEstatEsborrany;

    @Transient private boolean reintentsEsgotat;
    @Transient private int maxReintents;

//    private ResourceReference<RegistreAnnexEntity, Long>  justificant;
//    private List<ResourceReference<RegistreInteressatResource, Long>> interessats = new ArrayList<>();
    private String interessatsString;
//    private List<ResourceReference<RegistreAnnexEntity>> annexos = new ArrayList<>();
//    private ResourceReference<ReglaEntity> regla;
    protected ResourceReference<UsuariResource, String> agafatPer;
//    protected Set<ResourceReference<DadaEntity>> dades;

    private String unitatAdministrativaCodi;
    private String unitatAdministrativaDescripcio;


    /** Codi del backoffice que ha processat l'anotació, s'informa a partir de la Regla.codiBackoffice */
    private String backCodi;

//    private String entitatCodi;
//    private String entitatDescripcio;

    private String oficinaCodi;
    private String oficinaDescripcio;

    private String llibreCodi;
    private String llibreDescripcio;

    private String assumpteTipusCodi;
    private String assumpteTipusDescripcio;

    private String assumpteCodi;
    private String assumpteDescripcio;

    private String idiomaCodi;
    private String idiomaDescripcio;

    private String transportTipusCodi;
    private String transportTipusDescripcio;

    private String documentacioFisicaCodi;
    private String documentacioFisicaDescripcio;

    private String oficinaOrigenCodi;
    private String oficinaOrigenDescripcio;

    private String tramitCodi;
    private String tramitNom;

    private String usuariCodi;
    private String usuariNom;

    private String aplicacioCodi;
    private String aplicacioVersio;

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
        private ResourceReference<UnitatOrganitzativaResource, Long> unitatOrganitzativa;
        private ResourceReference<BustiaResource, Long> bustia;
        private boolean inactives = true;
        private boolean enviatPerEmail;
        private RegistreTipusDocFisicaEnumDto documentacio;
        private boolean ambEsborranys;
        private boolean annexosInterns;
        private ResourceReference<BackofficeResource, Long> backoffice;
        private RegistreProcesEstatSimpleEnumDto estat = RegistreProcesEstatSimpleEnumDto.PENDENT;
        private RegistreProcesEstatEnum procesEstat;
        private boolean ambErrors;
        private boolean sobreescriure;
        private RegistreNombreAnnexesEnumDto nombreAnnexes;
        private ResourceReference<ProcedimentResource, Long> procediment;
        private ResourceReference<ServeiResource, Long> servei;
        private boolean reintents;
    }

}
