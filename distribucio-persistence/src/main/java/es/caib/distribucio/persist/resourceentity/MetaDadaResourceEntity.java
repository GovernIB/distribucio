package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.MetaDadaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.MultiplicitatEnumDto;
import es.caib.distribucio.logic.intf.model.MetaDadaResource;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import es.caib.distribucio.persist.entity.MetaDadaEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entitat de base de dades del recurs {@link MetaDadaResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link MetaDadaEntity},
 * dedicada exclusivament al mapeig genèric per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "metadada")
@Getter
@Setter
@NoArgsConstructor
public class MetaDadaResourceEntity extends BaseAuditableEntity<MetaDadaResource, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    @SequenceGenerator(name = "default_seq", sequenceName = "dis_hibernate_seq", allocationSize = 1)
    private Long id;

    @Column(name = "codi", length = 64, nullable = false)
    private String codi;
    @Column(name = "nom", length = 256, nullable = false)
    private String nom;
    @Column(name = "tipus", nullable = false)
    private MetaDadaTipusEnumDto tipus;
    @Column(name = "multiplicitat", nullable = false)
    private MultiplicitatEnumDto multiplicitat;
    @Column(name = "valor")
    private String valor;
    @Column(name = "descripcio", length = 1024)
    private String descripcio;
    @Column(name = "activa")
    private boolean activa;
    @Column(name = "read_only")
    private boolean readOnly;
    @Column(name = "ordre")
    private int ordre;
    @Column(name = "no_aplica")
    private boolean noAplica;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "entitat_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "metadada_entitat_fk"))
    protected EntitatResourceEntity entitat;

    @Version
    private long version = 0;

}
