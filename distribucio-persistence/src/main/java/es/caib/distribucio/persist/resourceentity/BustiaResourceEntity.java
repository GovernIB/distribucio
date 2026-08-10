package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.model.BustiaResource;
import es.caib.distribucio.persist.base.entity.BaseResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.Version;

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
@Table(name = "dis_bustia")
@SecondaryTable(name = "dis_contingut", pkJoinColumns = @PrimaryKeyJoinColumn(name = "id"))
@Getter
@Setter
@NoArgsConstructor
public class BustiaResourceEntity extends BaseResourceEntity<BustiaResource, Long> {

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "nom", table = "dis_contingut", length = 1024, nullable = false)
	private String nom;

	@Column(name = "entitat_id", table = "dis_contingut", nullable = false)
	private Long entitatId;

	@Column(name = "pare_id", table = "dis_contingut")
	private Long pareId;

	@Column(name = "unitat_id", nullable = false)
	private Long unitatOrganitzativaId;

	@Column(name = "per_defecte")
	private boolean perDefecte;

	@Column(name = "activa")
	private boolean activa = true;

	@Version
	@Column(name = "version", table = "dis_contingut")
	private long version = 0;

}
