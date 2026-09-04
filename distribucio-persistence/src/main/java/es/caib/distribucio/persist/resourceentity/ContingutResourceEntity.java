package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.base.model.Resource;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ContingutTipusEnumDto;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import es.caib.distribucio.persist.entity.AlertaEntity;
import es.caib.distribucio.persist.entity.ContingutLogEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Entitat de base de dades que representa un contingut.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "contingut")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipus", discriminatorType = DiscriminatorType.STRING)
public abstract class ContingutResourceEntity<R extends Resource<?>> extends BaseAuditableEntity<R, Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    @SequenceGenerator(name = "default_seq", sequenceName = BaseConfig.DB_PREFIX + "hibernate_seq", allocationSize = 1)
    private Long id;

	@Column(name = "nom", length = 256, nullable = false)
	protected String nom;

    @Enumerated(EnumType.STRING)
	@Column(name = "tipus", nullable = false, insertable = false, updatable = false)
	protected ContingutTipusEnumDto tipus;

	@Column(name = "esborrat")
	protected int esborrat = 0;

	@Column(name = "arxiu_uuid", length = 36)
	protected String arxiuUuid;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "arxiu_data_act")
	protected Date arxiuDataActualitzacio;

    @Version
    private long version = 0;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(
			name = "pare_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "pare_contingut_fk"))
	protected ContingutResourceEntity<?> pare;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "entitat_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "entitat_contingut_fk"))
	protected EntitatResourceEntity entitat;

//	@OneToMany(
//			mappedBy = "pare",
//			fetch = FetchType.LAZY,
//			cascade = CascadeType.ALL,
//			orphanRemoval = true)
//	protected Set<ContingutResourceEntity<?>> fills;

    // TODO: revisar si es necesaria versión Resource
    @OneToMany(
            mappedBy = "contingut",
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @OrderBy("createdDate ASC")
    protected List<AlertaEntity> alertes = new ArrayList<AlertaEntity>();
    @OneToMany(
            mappedBy = "contingut",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    protected List<ContingutLogEntity> logs = new ArrayList<ContingutLogEntity>();
}
