package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ReglaPresencialEnumDto;
import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;
import es.caib.distribucio.logic.intf.model.ReglaResource;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entitat de base de dades del recurs {@link ReglaResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link es.caib.distribucio.persist.entity.ReglaEntity},
 * dedicada exclusivament al mapeig genèric per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "regla")
@Getter
@Setter
@NoArgsConstructor
public class ReglaResourceEntity extends BaseAuditableEntity<ReglaResource, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    @SequenceGenerator(name = "default_seq", sequenceName = "dis_hibernate_seq", allocationSize = 1)
    private Long id;

    @Column(name = "nom", length = 256, nullable = false)
    private String nom;

    @Column(name = "descripcio", length = 1024)
    private String descripcio;

    // ------------- FILTRE ----------------------
    @Column(name = "assumpte_codi", length = 16)
    private String assumpteCodiFiltre;

    @Column(name = "procediment_codi", length = 1024)
    private String procedimentCodiFiltre;

    @Column(name = "servei_codi", length = 1024)
    private String serveiCodiFiltre;

    @Column(name = "tramit_codi", length = 1024)
    private String tramitCodiFiltre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "unitat_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "regla_uofiltre_fk"))
    private UnitatOrganitzativaResourceEntity unitatOrganitzativaFiltre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "bustia_filtre_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "regla_bustiafiltre_fk"))
    private BustiaResourceEntity bustiaFiltre;

    @Column(name = "presencial")
    @Enumerated(EnumType.ORDINAL)
    private ReglaPresencialEnumDto presencial;

    // ------------- ACCIO  ----------------------
    @Column(name = "tipus", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReglaTipusEnumDto tipus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "backoffice_desti_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "regla_bodesti_fk"))
    private BackofficeResourceEntity backofficeDesti;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "bustia_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "regla_bustia_fk"))
    private BustiaResourceEntity bustiaDesti;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "unitat_desti_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "regla_uodesti_fk"))
    private UnitatOrganitzativaResourceEntity unitatDesti;

    @Column(name = "aturar_avaluacio", nullable = false)
    private boolean aturarAvaluacio;

    @Column(name = "ordre", nullable = false)
    private int ordre;

    @Column(name = "activa", nullable = false)
    private boolean activa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "entitat_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "regla_entitat_fk"))
    private EntitatResourceEntity entitat;

    @Version
    private long version = 0;

}
