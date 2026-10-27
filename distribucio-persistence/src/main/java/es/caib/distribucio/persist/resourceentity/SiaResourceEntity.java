package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ProcedimentEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreClassificarTipusEnum;
import es.caib.distribucio.logic.intf.model.SiaResource;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import lombok.*;

import javax.persistence.*;

/**
 * Entitat de base de dades del recurs {@link SiaResource}.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "sia")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiaResourceEntity extends BaseAuditableEntity<SiaResource, Long> {

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
	private ProcedimentEstatEnumDto estat;

	@Column(name = "tipus", length = 20)
	@Enumerated(EnumType.STRING)
	private RegistreClassificarTipusEnum tipus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "id_unitat_organitzativa",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "sia_unitat_fk"))
	private UnitatOrganitzativaResourceEntity unitatOrganitzativa;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "entitat",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "sia_entitat_fk"))
	private EntitatResourceEntity entitat;

	@Column(name = "comu")
	private boolean comu;

}
