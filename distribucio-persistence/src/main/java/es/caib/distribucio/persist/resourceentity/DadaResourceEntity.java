package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.DadaResource;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import es.caib.distribucio.persist.entity.DadaEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entitat de base de dades del recurs {@link DadaResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link DadaEntity},
 * dedicada exclusivament al mapeig genèric per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "dada")
@Getter
@Setter
@NoArgsConstructor
public class DadaResourceEntity extends BaseAuditableEntity<DadaResource, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    @SequenceGenerator(name = "default_seq", sequenceName = "dis_hibernate_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(
            name = "metadada_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "dada_metadada_fk"))
    protected MetaDadaResourceEntity metaDada;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(
            name = "registre_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "dada_registre_fk"))
    protected RegistreResourceEntity registre;
    @Column(name = "valor", length = 256, nullable = false)
    protected String valor;
    @Column(name = "ordre")
    protected int ordre;
    @Version
    private long version = 0;

}
