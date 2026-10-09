package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.UsuariBustiaFavoritResource;
import es.caib.distribucio.logic.intf.model.UsuariResource;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entitat de base de dades del recurs {@link UsuariResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link es.caib.distribucio.persist.entity.UsuariEntity}
 * (clau primària natural {@code codi}, sense generador), dedicada exclusivament al mapeig
 * genèric per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "bustia_favorit")
@Getter
@Setter
@NoArgsConstructor
public class UsuariBustiaFavoritResourceEntity extends BaseAuditableEntity<UsuariBustiaFavoritResource, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    @SequenceGenerator(name = "default_seq", sequenceName = BaseConfig.DB_PREFIX + "hibernate_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "bustia_id",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "bustia_fav_bustia_fk"))
    protected BustiaResourceEntity bustia;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "usuari_codi",
            foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "bustia_fav_usuari_fk"))
    protected UsuariResourceEntity usuari;

}
