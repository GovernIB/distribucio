package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.RegistreResource;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatSistraEnum;
import es.caib.distribucio.logic.intf.registre.RegistreTipusEnum;
import es.caib.distribucio.persist.base.entity.ResourceEntity;
import es.caib.distribucio.persist.entity.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Entitat de base de dades del subtipus REGISTRE de {@link RegistreResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link es.caib.distribucio.persist.entity.RegistreEntity}
 * (herència JOINED sobre {@code dis_contingut}/{@code dis_registre}), dedicada exclusivament al
 * mapeig genèric per reflexió del recurs REST. Encara no hi ha cap {@code RegistreResource} propi,
 * així que els continguts de tipus REGISTRE es representen amb el {@link RegistreResource} genèric.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "registre")
@DiscriminatorValue("REGISTRE")
@Getter
@Setter
@NoArgsConstructor
public class RegistreResourceEntity extends ContingutResourceEntity<RegistreResource> implements ResourceEntity<RegistreResource, Long> {

    @Column(name = "tipus", length = 1, nullable = false)
    private RegistreTipusEnum registreTipus;
    @Column(name = "numero", length = 255, nullable = false)
    private String numero;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data", nullable = false)
    private Date data;
    @Column(name = "identificador", length = 100, nullable = false)
    private String identificador;
    @Column(name = "extracte", length = 240)
    private String extracte;
    @Column(name = "procediment_codi", length = 64)
    private String procedimentCodi;
    @Column(name = "servei_codi", length = 64)
    private String serveiCodi;
    @Column(name = "referencia", length = 16)
    private String referencia;
    @Column(name = "expedient_num", length = 80)
    private String expedientNumero;
    @Column(name = "expedient_arxiu_uuid", length = 100)
    private String expedientArxiuUuid;
    @Column(name = "num_orig", length = 80)
    private String numeroOrigen;
    @Column(name = "transport_num", length = 20)
    private String transportNumero;
    @Column(name = "usuari_contacte", length = 255)
    private String usuariContacte;
    @Column(name = "observacions", length = 50)
    private String observacions;
    @Lob
    @Column(name = "exposa", nullable = true)
    private String exposa;
    @Lob
    @Column(name = "solicita", nullable = true)
    private String solicita;
    @Lob
    @Column(name = "motiu_rebuig", nullable = true)
    private String motiuRebuig;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "proces_data")
    private Date procesData;
    @Enumerated(EnumType.STRING)
    @Column(name = "proces_estat", length = 64, nullable = false)
    private RegistreProcesEstatEnum procesEstat;
    /** Indica si l'anotació de registre està pendent de processament (true) o processada (false). */
    @Column(name = "pendent")
    private Boolean pendent;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_orig")
    private Date dataOrigen;
    @Enumerated(EnumType.STRING)
    @Column(name = "proces_estat_sistra", length = 16)
    private RegistreProcesEstatSistraEnum procesEstatSistra;
    @Column(name = "sistra_id_tram", length = 20)
    private String identificadorTramitSistra;
    @Column(name = "sistra_id_proc", length = 100)
    private String identificadorProcedimentSistra;
    @Lob
    @Column(name = "proces_error")
    private String procesError;
    @Column(name = "proces_intents")
    private int procesIntents;
    // Date when regla change state of anotacio to RegistreProcesEstatEnum.BACK_PENDENT
    @Column(name = "back_pendent_data")
    private Date backPendentData;
    @Column(name = "back_rebuda_data")
    // Date when backoffice called BackofficeIntegracioWsService.canviEstat(RegistreProcesEstatEnum.BACK_REBUDA) method
    private Date backRebudaData;
    @Column(name = "back_comunicada_data")
    // Date when change state of anotacio to RegistreProcesEstatEnum.BACK_COMUNICADA
    private Date backComunicadaData;
    @Column(name = "back_proces_rebutj_error_data")
    // Date when backoffice called BackofficeIntegracioWsService.canviEstat(RegistreProcesEstatEnum.BACK_PROCESADA) or (RegistreProcesEstatEnum.BACK_REBUTJADA) or (RegistreProcesEstatEnum.BACK_ERROR) method
    private Date backProcesRebutjErrorData;
    @Column(name = "back_observacions")
    private String backObservacions;
    // Date when distribucio will retry to send anotacio to backoffice
    @Column(name = "back_retry_enviar_data")
    private Date backRetryEnviarData;

    @Column(name = "presencial")
    private Boolean presencial;

    @Column(name = "justificant_descarregat")
    private boolean justificantDescarregat;

    @Column(name = "justificant_arxiu_uuid", length = 256)
    private String justificantArxiuUuid;

    @Column(name = "llegida")
    private Boolean llegida;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_tancament")
    private Date dataTancament;
    @Column(name = "arxiu_tancat")
    private Boolean arxiuTancat;
    @Column(name = "arxiu_tancat_error")
    private Boolean arxiuTancatError;
    /** Com que es pot reenviar un registre a una altra bústia amb el mateix número de registre es posa el número de còpia per distingir-los. */
    @Column(name = "numero_copia")
    private Integer numeroCopia;
    @Column(name = "enviat_per_email")
    private boolean enviatPerEmail;

    @Column(name = "reactivat")
    private boolean reactivat;

    @Column(name = "sobreescriure")
    private boolean sobreescriure;

    /** Conté el recompte del número d'annexos en estat esborrany */
    @Column(name = "annexos_estat_esborrany")
    private int annexosEstatEsborrany;

    @OneToMany(mappedBy = "registre", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    protected Set<DadaEntity> dades;


    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "justificant_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "registre_justificant_fk"))
    private RegistreAnnexResourceEntity justificant;

    @OneToMany(
            mappedBy = "registre",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<RegistreInteressatResourceEntity> interessats = new ArrayList<>();

    @OneToMany(
            mappedBy = "registre",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<RegistreAnnexResourceEntity> annexos = new ArrayList<>();

//    @ManyToOne(optional = true, fetch = FetchType.EAGER)
//    @JoinColumn(
//            name = "regla_id",
//            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "registre_regla_fk"))
//    private ReglaEntity regla;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "agafat_per",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "registre_agafatper_fk"))
    protected UsuariResourceEntity agafatPer;


    @Column(name = "entitat_codi", length = 255, nullable = false)
    private String entitatCodi;
    @Column(name = "entitat_desc", length = 255)
    private String entitatDescripcio;
    public void setEntitat(EntitatResourceEntity entitat) {
        this.entitat = entitat;
        this.entitatCodi = entitat.getCodi();
        this.entitatDescripcio = entitat.getNom();
    }

    @Column(name = "unitat_adm", length = 21, nullable = false)
    private String unitatAdministrativaCodi;
    @Column(name = "unitat_adm_desc", length = 300)
    private String unitatAdministrativaDescripcio;

    @Column(name = "back_codi", length = 20)
    private String backCodi;

    @Column(name = "oficina_orig_codi", length = 21)
    private String oficinaOrigenCodi;
    @Column(name = "oficina_orig_desc", length = 100)
    private String oficinaOrigenDescripcio;

    @Column(name = "oficina_codi", length = 21, nullable = false)
    private String oficinaCodi;
    @Column(name = "oficina_desc", length = 300)
    private String oficinaDescripcio;

    @Column(name = "llibre_codi", length = 4, nullable = false)
    private String llibreCodi;
    @Column(name = "llibre_desc", length = 255)
    private String llibreDescripcio;

    @Column(name = "assumpte_tipus_codi", length = 16)
    private String assumpteTipusCodi;
    @Column(name = "assumpte_tipus_desc", length = 100)
    private String assumpteTipusDescripcio;

    @Column(name = "assumpte_codi", length = 16)
    private String assumpteCodi;
    @Column(name = "assumpte_desc", length = 255)
    private String assumpteDescripcio;

    @Column(name = "transport_tipus_codi", length = 20)
    private String transportTipusCodi;
    @Column(name = "transport_tipus_desc", length = 100)
    private String transportTipusDescripcio;

    @Column(name = "aplicacio_codi", length = 255)
    private String aplicacioCodi;
    @Column(name = "aplicacio_versio", length = 255)
    private String aplicacioVersio;

    @Column(name = "docfis_codi", length = 19)
    private String documentacioFisicaCodi;
    @Column(name = "docfis_desc", length = 100)
    private String documentacioFisicaDescripcio;

    @Column(name = "idioma_codi", length = 2, nullable = false)
    private String idiomaCodi;
    @Column(name = "idioma_desc", length = 100)
    private String idiomaDescripcio;

    @Column(name = "usuari_codi", length = 20)
    private String usuariCodi;
    @Column(name = "usuari_nom", length = 767)
    private String usuariNom;

    @Column(name = "tramit_codi", length = 64)
    private String tramitCodi;
    @Column(name = "tramit_nom", length = 255)
    private String tramitNom;

}
