package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ProcedimentEstatEnumDto;
import es.caib.distribucio.logic.intf.model.ProcedimentResource;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entitat de base de dades del recurs {@link ProcedimentResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link es.caib.distribucio.persist.entity.ProcedimentEntity},
 * dedicada exclusivament al mapeig genèric per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "dis_procediment")
@Getter
@Setter
@NoArgsConstructor
public class ProcedimentResourceEntity extends BaseAuditableEntity<ProcedimentResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
	@SequenceGenerator(name = "default_seq", sequenceName = "dis_hibernate_seq", allocationSize = 1)
	private Long id;

	@Column(name = "codi", length = 64, nullable = false)
	private String codi;

	@Column(name = "nom", length = 256)
	private String nom;

	@Column(name = "codisia", length = 64)
	private String codiSia;

	@Column(name = "estat", length = 20)
	@Enumerated(EnumType.STRING)
	private ProcedimentEstatEnumDto estat = ProcedimentEstatEnumDto.VIGENT;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "id_unitat_organitzativa",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "procediment_unitat_fk"))
	private UnitatOrganitzativaResourceEntity unitatOrganitzativa;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "entitat",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "procediment_entitat_fk"))
	private EntitatResourceEntity entitat;

	@Column(name = "comu")
	private boolean comu;

}
