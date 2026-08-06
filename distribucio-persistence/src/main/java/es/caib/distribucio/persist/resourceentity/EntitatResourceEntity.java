package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.model.EntitatResource;
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
import javax.persistence.Version;

/**
 * Entitat de base de dades del recurs {@link EntitatResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link es.caib.distribucio.persist.entity.EntitatEntity},
 * dedicada exclusivament al mapeig genèric per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "dis_entitat")
@Getter
@Setter
@NoArgsConstructor
public class EntitatResourceEntity extends BaseResourceEntity<EntitatResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
	@SequenceGenerator(name = "default_seq", sequenceName = "dis_hibernate_seq", allocationSize = 1)
	private Long id;

	@Column(name = "codi", length = 64, nullable = false, unique = true)
	private String codi;

	@Column(name = "nom", length = 256, nullable = false)
	private String nom;

	@Column(name = "descripcio", length = 1024)
	private String descripcio;

	@Column(name = "cif", length = 9, nullable = false)
	private String cif;

	@Column(name = "codi_dir3", length = 9, nullable = false)
	private String codiDir3;

	@Column(name = "color_fons", length = 32)
	private String colorFons;

	@Column(name = "color_lletra", length = 32)
	private String colorLletra;

	@Column(name = "activa")
	private boolean activa = true;

	@Version
	private long version = 0;

}
