package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.LogObjecteTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.LogTipusEnumDto;
import es.caib.distribucio.logic.intf.model.ContingutLogResource;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import es.caib.distribucio.persist.entity.ContingutLogParamEntity;
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
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Entitat de base de dades del recurs {@link ContingutLogResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci
 * {@link es.caib.distribucio.persist.entity.ContingutLogEntity} ({@code dis_cont_log}), dedicada
 * exclusivament al mapeig genèric per reflexió del recurs REST. La col·lecció {@code params}
 * reutilitza directament l'entitat legacy {@link ContingutLogParamEntity} -- mateix patró que
 * {@code ContingutResourceEntity.logs}/{@code .alertes}, que ja reutilitzen entitats legacy per a
 * relacions {@code @OneToMany} en lloc de duplicar-les com a "Resource".
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "cont_log")
@Getter
@Setter
@NoArgsConstructor
public class ContingutLogResourceEntity extends BaseAuditableEntity<ContingutLogResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
	@SequenceGenerator(name = "default_seq", sequenceName = BaseConfig.DB_PREFIX + "hibernate_seq", allocationSize = 1)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipus", length = 30, nullable = false)
	private LogTipusEnumDto tipus;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "contingut_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "cont_log_contingut_fk"))
	private ContingutResourceEntity<?> contingut;

	@Column(name = "objecte_id", length = 64)
	private String objecteId;
	@Enumerated(EnumType.STRING)
	@Column(name = "objecte_tipus", length = 12)
	private LogObjecteTipusEnumDto objecteTipus;
	@Enumerated(EnumType.STRING)
	@Column(name = "objecte_log_tipus", length = 30)
	private LogTipusEnumDto objecteLogTipus;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "contmov_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "cont_log_contmov_fk"))
	private ContingutMovimentResourceEntity contingutMoviment;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "pare_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "cont_log_pare_fk"))
	private ContingutLogResourceEntity pare;

	@OneToMany(mappedBy = "contingutLog", fetch = FetchType.LAZY)
	@OrderBy("numero ASC")
	private List<ContingutLogParamEntity> params = new ArrayList<>();

}
