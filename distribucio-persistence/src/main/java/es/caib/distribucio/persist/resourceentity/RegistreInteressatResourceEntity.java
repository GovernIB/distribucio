package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.RegistreInteressatResource;
import es.caib.distribucio.logic.intf.registre.RegistreInteressatCanalEnum;
import es.caib.distribucio.logic.intf.registre.RegistreInteressatDocumentTipusEnum;
import es.caib.distribucio.logic.intf.registre.RegistreInteressatTipusEnum;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import es.caib.distribucio.persist.converter.RegistreInteressatCanalConverter;
import es.caib.distribucio.persist.converter.RegistreInteressatDocumentTipusConverter;
import es.caib.distribucio.persist.converter.RegistreInteressatTipusConverter;
import es.caib.distribucio.persist.entity.RegistreInteressatEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entitat de base de dades del subtipus REGISTRE de {@link RegistreInteressatResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link RegistreInteressatEntity}
 * (herència JOINED sobre {@code dis_contingut}/{@code dis_registre}), dedicada exclusivament al
 * mapeig genèric per reflexió del recurs REST. Encara no hi ha cap {@code RegistreInteressatResource} propi,
 * així que els continguts de tipus REGISTRE es representen amb el {@link RegistreInteressatResource} genèric.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "registre_inter")
@Getter
@Setter
@NoArgsConstructor
public class RegistreInteressatResourceEntity extends BaseAuditableEntity<RegistreInteressatResource, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    @SequenceGenerator(name = "default_seq", sequenceName = BaseConfig.DB_PREFIX + "hibernate_seq", allocationSize = 1)
    private Long id;

    @Column(name = "tipus", length = 19, nullable = false)
    @Convert(converter = RegistreInteressatTipusConverter.class)
    private RegistreInteressatTipusEnum tipus;
    @Column(name = "doc_tipus", length = 1)
    @Convert(converter = RegistreInteressatDocumentTipusConverter.class)
    private RegistreInteressatDocumentTipusEnum documentTipus;
    @Column(name = "doc_num", length = 17)
    private String documentNum;
    @Column(name = "nom", length = 255)
    private String nom;
    @Column(name = "llinatge1", length = 255)
    private String llinatge1;
    @Column(name = "llinatge2", length = 255)
    private String llinatge2;
    @Column(name = "rao_social", length = 2000)
    private String raoSocial;
    @Column(name = "pais", length = 100)
    private String pais;
    @Column(name = "pais_codi", length = 4)
    private String paisCodi;
    @Column(name = "provincia", length = 100)
    private String provincia;
    @Column(name = "provincia_codi", length = 4)
    private String provinciaCodi;
    @Column(name = "municipi", length = 100)
    private String municipi;
    @Column(name = "municipi_codi", length = 4)
    private String municipiCodi;
    @Column(name = "adresa", length = 160)
    private String adresa;
    @Column(name = "codi_postal", length = 5)
    private String codiPostal;
    @Column(name = "email", length = 160)
    private String email;
    @Column(name = "telefon", length = 20)
    private String telefon;
    @Column(name = "email_hab", length = 160)
    private String emailHabilitat;
    @Column(name = "canal_pref", length = 2)
    @Convert(converter = RegistreInteressatCanalConverter.class)
    private RegistreInteressatCanalEnum canalPreferent;
    @Column(name = "observacions", length = 160)
    private String observacions;
    @Column(name = "codi_dire", length = 20)
    private String codiDire;
    @Version
    private long version = 0;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "registre_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "reginter_registre_fk"))
    protected RegistreResourceEntity registre;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "representant_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "reginter_representant_fk"))
    protected RegistreInteressatResourceEntity representant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "representat_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "reginter_representat_fk"))
    protected RegistreInteressatResourceEntity representat;

    @Transient private String nomComplet;
    public String getNomComplet() {
        String resum = "";
        switch (this.getTipus()) {
            case PERSONA_FIS:
                resum += nom == null ? "" : nom + " ";
                resum += llinatge1 == null ? "" : llinatge1 + " ";
                resum += llinatge2 == null ? "" : llinatge2 + " ";
                resum += "(" + documentNum + ")" + "\n";
                break;
            case PERSONA_JUR:
            case ADMINISTRACIO:
                resum += raoSocial + " ";
                resum += "(" + documentNum + ")" + "\n";
                break;
        }
        return resum;
    }
}
