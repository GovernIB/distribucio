package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.AvisNivellEnumDto;
import es.caib.distribucio.logic.intf.model.AvisResource;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Entitat de base de dades del recurs {@link AvisResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link es.caib.distribucio.persist.entity.AvisEntity},
 * dedicada exclusivament al mapeig genèric per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "dis_avis")
@Getter
@Setter
@NoArgsConstructor
public class AvisResourceEntity extends BaseAuditableEntity<AvisResource, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    @SequenceGenerator(name = "default_seq", sequenceName = "dis_hibernate_seq", allocationSize = 1)
    private Long id;

    @Column(name = "assumpte", length = 256, nullable = false)
    private String assumpte;

    @Column(name = "missatge", length = 2048, nullable = false)
    private String missatge;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_inici", nullable = false)
    private Date dataInici;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_final")
    private Date dataFinal;

    @Column(name = "actiu", nullable = false)
    private Boolean actiu;

    @Enumerated(EnumType.STRING)
    @Column(name = "avis_nivell", length = 2048, nullable = false)
    private AvisNivellEnumDto avisNivell;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "entitat",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "avis_entitat_fk")
    )
    private EntitatResourceEntity entitat;

}