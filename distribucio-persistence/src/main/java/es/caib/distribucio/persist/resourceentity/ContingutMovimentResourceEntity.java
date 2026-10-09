package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.ContingutMovimentResource;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
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
 * Entitat de base de dades del recurs {@link ContingutMovimentResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci
 * {@link es.caib.distribucio.persist.entity.ContingutMovimentEntity} ({@code dis_cont_mov}),
 * dedicada exclusivament al mapeig genèric per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "cont_mov")
@Getter
@Setter
@NoArgsConstructor
public class ContingutMovimentResourceEntity extends BaseAuditableEntity<ContingutMovimentResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
	@SequenceGenerator(name = "default_seq", sequenceName = BaseConfig.DB_PREFIX + "hibernate_seq", allocationSize = 1)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "contingut_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "cont_mov_contingut_fk"))
	private ContingutResourceEntity<?> contingut;

	@Column(name = "origen_id")
	private Long origenId;
	@Column(name = "desti_id")
	private Long destiId;
	@Column(name = "origen_nom")
	private String origenNom;
	@Column(name = "desti_nom")
	private String destiNom;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "remitent_codi",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "remitent_contmov_fk"))
	private UsuariResourceEntity remitent;

	@Column(name = "comentari", length = 3940)
	private String comentari;
	@Column(name = "per_coneixement")
	private Boolean perConeixement;
	@Column(name = "comentari_destins", length = 256)
	private String comentariDestins;
	@Column(name = "num_duplicat")
	private Integer numDuplicat;

}
