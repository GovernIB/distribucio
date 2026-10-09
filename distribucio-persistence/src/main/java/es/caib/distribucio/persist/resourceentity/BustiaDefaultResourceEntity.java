package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.model.BustiaDefaultResource;
import es.caib.distribucio.persist.base.entity.BaseResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entitat de base de dades del recurs {@link BustiaDefaultResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link es.caib.distribucio.persist.entity.BustiaDefaultEntity}
 * (parella entitat+usuari amb la seva bústia per defecte), dedicada exclusivament al mapeig genèric
 * per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "dis_bustia_default")
@Getter
@Setter
@NoArgsConstructor
public class BustiaDefaultResourceEntity extends BaseResourceEntity<BustiaDefaultResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
	@SequenceGenerator(name = "default_seq", sequenceName = "dis_hibernate_seq", allocationSize = 1)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat")
	private EntitatResourceEntity entitat;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "bustia")
	private BustiaResourceEntity bustia;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "usuari")
	private UsuariResourceEntity usuari;

	public void updateBustiaDefault(BustiaResourceEntity bustia) {
		this.bustia = bustia;
	}

	public static Builder getBuilder(
			EntitatResourceEntity entitat,
			BustiaResourceEntity bustia,
			UsuariResourceEntity usuari) {
		return new Builder(entitat, bustia, usuari);
	}

	public static class Builder {
		private final BustiaDefaultResourceEntity built;
		private Builder(
				EntitatResourceEntity entitat,
				BustiaResourceEntity bustia,
				UsuariResourceEntity usuari) {
			built = new BustiaDefaultResourceEntity();
			built.entitat = entitat;
			built.bustia = bustia;
			built.usuari = usuari;
		}
		public BustiaDefaultResourceEntity build() {
			return built;
		}
	}

}
