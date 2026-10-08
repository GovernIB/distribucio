package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.AlertaResource;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import es.caib.distribucio.persist.entity.AlertaEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entitat de base de dades del recurs {@link AlertaResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link AlertaEntity},
 * dedicada exclusivament al mapeig genèric per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "alerta")
@Getter
@Setter
@NoArgsConstructor
public class AlertaResourceEntity extends BaseAuditableEntity<AlertaResource, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    @SequenceGenerator(name = "default_seq", sequenceName = "dis_hibernate_seq", allocationSize = 1)
    private Long id;

    @Column(name = "text", length = 256, nullable = false)
    private String text;
    @Column(name = "error", length = 2048)
    private String error;
    @Column(name = "llegida", nullable = false)
    private Boolean llegida;

    @ManyToOne(optional = true, fetch = FetchType.EAGER)
    @JoinColumn(
            name = "contingut_id",
            foreignKey = @ForeignKey(name = "dis_alerta_contingut_fk"))
    protected ContingutResourceEntity contingut;

}
