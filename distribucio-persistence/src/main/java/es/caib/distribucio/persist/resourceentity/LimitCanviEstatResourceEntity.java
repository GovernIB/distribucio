package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.model.LimitCanviEstatResource;
import es.caib.distribucio.persist.base.entity.BaseResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entitat de base de dades del recurs {@link LimitCanviEstatResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link es.caib.distribucio.persist.entity.LimitCanviEstatEntity},
 * dedicada exclusivament al mapeig genèric per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "dis_limit_canvi_estat")
@Getter
@Setter
@NoArgsConstructor
public class LimitCanviEstatResourceEntity extends BaseResourceEntity<LimitCanviEstatResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
	@SequenceGenerator(name = "default_seq", sequenceName = "dis_hibernate_seq", allocationSize = 1)
	private Long id;

	@Column(name = "usuari_codi")
	private String usuariCodi;

	@Column(name = "descripcio")
	private String descripcio;

	@Column(name = "lim_min_lab")
	private Integer limitMinutLaboral;

	@Column(name = "lim_min_nolab")
	private Integer limitMinutNoLaboral;

	@Column(name = "lim_dia_lab")
	private Integer limitDiaLaboral;

	@Column(name = "lim_dia_nolab")
	private Integer limitDiaNoLaboral;

}
