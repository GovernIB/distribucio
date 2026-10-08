package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.BustiaResource;
import es.caib.distribucio.persist.base.entity.ResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Entitat de base de dades del recurs {@link BustiaResource}.
 * <p>
 * Mapeja les mateixes taules que l'entitat de negoci {@link es.caib.distribucio.persist.entity.BustiaEntity}
 * (herència JOINED sobre {@code dis_contingut}/{@code dis_bustia}), dedicada exclusivament al mapeig
 * genèric per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "bustia")
@DiscriminatorValue("BUSTIA")
@Getter
@Setter
@NoArgsConstructor
public class BustiaResourceEntity extends ContingutResourceEntity<BustiaResource> implements ResourceEntity<BustiaResource, Long> {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "unitat_id")
	private UnitatOrganitzativaResourceEntity unitatOrganitzativa;

	@Column(name = "per_defecte")
	private boolean perDefecte;

	@Column(name = "activa")
	private boolean activa = true;

}
