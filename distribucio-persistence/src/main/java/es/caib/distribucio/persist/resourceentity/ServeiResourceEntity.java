package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ServeiEstatEnumDto;
import es.caib.distribucio.logic.intf.model.ServeiResource;
import es.caib.distribucio.persist.base.entity.BaseResourceEntity;
import es.caib.distribucio.persist.entity.UnitatOrganitzativaEntity;
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
import javax.persistence.Version;

/**
 * Entitat de base de dades del recurs {@link ServeiResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link es.caib.distribucio.persist.entity.ServeiEntity},
 * dedicada exclusivament al mapeig genèric per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = "dis_servei")
@Getter
@Setter
@NoArgsConstructor
public class ServeiResourceEntity extends BaseResourceEntity<ServeiResource, Long> {

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
	private ServeiEstatEnumDto estat = ServeiEstatEnumDto.VIGENT;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(
			name = "id_unitat_organitzativa",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "servei_unitat_fk"))
	private UnitatOrganitzativaResourceEntity unitatOrganitzativa;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "entitat",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "servei_entitat_fk"))
	private EntitatResourceEntity entitat;

	@Column(name = "comu")
	private boolean comu;

}
