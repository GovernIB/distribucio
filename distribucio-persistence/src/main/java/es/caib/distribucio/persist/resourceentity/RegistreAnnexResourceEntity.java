package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.RegistreAnnexResource;
import es.caib.distribucio.logic.intf.registre.ValidacioFirmaEnum;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnnexEstat;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import es.caib.distribucio.persist.entity.RegistreAnnexEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Entitat de base de dades del subtipus REGISTRE de {@link RegistreAnnexResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link RegistreAnnexEntity}
 * (herència JOINED sobre {@code dis_contingut}/{@code dis_registre}), dedicada exclusivament al
 * mapeig genèric per reflexió del recurs REST. Encara no hi ha cap {@code RegistreAnnexResource} propi,
 * així que els continguts de tipus REGISTRE es representen amb el {@link RegistreAnnexResource} genèric.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "registre_annex")
@Getter
@Setter
@NoArgsConstructor
public class RegistreAnnexResourceEntity extends BaseAuditableEntity<RegistreAnnexResource, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    @SequenceGenerator(name = "default_seq", sequenceName = BaseConfig.DB_PREFIX + "hibernate_seq", allocationSize = 1)
    private Long id;

    @Column(name = "titol", length = 200, nullable = false)
    private String titol;
    @Column(name = "fitxer_nom", length = 256, nullable = false)
    private String fitxerNom;
    @Column(name = "fitxer_tamany", nullable = false)
    private int fitxerTamany;
    @Column(name = "fitxer_mime", length = 30)
    private String fitxerTipusMime;
    @Column(name = "fitxer_arxiu_uuid", length = 256)
    private String fitxerArxiuUuid;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_captura", nullable = false)
    private Date dataCaptura;
    @Column(name = "localitzacio", length = 80)
    private String localitzacio;
    @Column(name = "origen_ciuadm", length = 1, nullable = false)
    private String origenCiutadaAdmin;
    @Column(name = "nti_tipus_doc", length = 4, nullable = false)
    private String ntiTipusDocument;
    @Column(name = "sicres_tipus_doc", length = 2)
    private String sicresTipusDocument;
    @Column(name = "nti_elaboracio_estat", length = 4)
    private String ntiElaboracioEstat;
    @Column(name = "observacions", length = 50)
    private String observacions;
    @Column(name = "firma_mode")
    private Integer firmaMode;
    @Column(name = "firma_csv", length = 256)
    private String firmaCsv;
    @Column(name = "timestamp", length = 100)
    private String timestamp;
    @Column(name = "validacio_ocsp", length = 100)
    private String validacioOCSP;
    @Column(name = "gesdoc_doc_id")
    private String gesdocDocumentId;
    @Column(name = "sign_detalls_descarregat")
    private boolean signaturaDetallsDescarregat;
    @Column(name = "meta_dades", length = 4000)
    private String metaDades;
    // Camps per a mostrar informació de la validacó de firmes
    @Enumerated(EnumType.STRING)
    @Column(name = "val_firma_estat")
    private ValidacioFirmaEnum validacioFirmaEstat;
    @Column(name = "val_firma_error", length= 1000)
    private String validacioFirmaError;
    // Camp per mostrar si el document està en estat ESBORRANY o DEFINITIU
    @Enumerated(EnumType.STRING)
    @Column(name = "arxiu_estat")
    private AnnexEstat arxiuEstat;
    @Version
    private long version = 0;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(
            name = "registre_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "registre_annex_reg_fk"))
    private RegistreResourceEntity registre;

//    @OneToMany(
//            mappedBy = "annex",
//            fetch = FetchType.LAZY,
//            cascade = CascadeType.ALL,
//            orphanRemoval = true)
//    private List<RegistreAnnexFirmaResourceEntity> firmes;
}
