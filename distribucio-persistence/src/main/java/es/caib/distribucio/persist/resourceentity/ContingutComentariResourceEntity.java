package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.ContingutComentariResource;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import es.caib.distribucio.persist.entity.ContingutComentariEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entitat de base de dades del recurs {@link ContingutComentariResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link ContingutComentariEntity},
 * dedicada exclusivament al mapeig genèric per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "cont_comment")
@Getter
@Setter
@NoArgsConstructor
public class ContingutComentariResourceEntity extends BaseAuditableEntity<ContingutComentariResource, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    @SequenceGenerator(name = "default_seq", sequenceName = "dis_hibernate_seq", allocationSize = 1)
    private Long id;

    @Lob
    @Column(name = "text")
    protected String text;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "contingut_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "cont_comment_cont_fk"))
    protected ContingutResourceEntity<?> contingut;

}
