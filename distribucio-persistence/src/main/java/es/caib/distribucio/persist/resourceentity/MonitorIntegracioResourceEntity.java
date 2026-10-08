package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.logic.intf.model.MonitorIntegracioResource;
import es.caib.distribucio.persist.base.entity.BaseResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Entitat de base de dades del recurs {@link MonitorIntegracioResource}.
 * <p>
 * Mapeja la mateixa taula que l'entitat de negoci {@link es.caib.distribucio.persist.entity.MonitorIntegracioEntity},
 * dedicada exclusivament al mapeig genèric per reflexió del recurs REST.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "mon_int")
@Getter
@Setter
@NoArgsConstructor
public class MonitorIntegracioResourceEntity extends BaseResourceEntity<MonitorIntegracioResource, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
	@SequenceGenerator(name = "default_seq", sequenceName = "dis_hibernate_seq", allocationSize = 1)
	private Long id;

	@Column(name = "codi")
	private String codi;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data")
	private Date data;

	@Column(name = "descripcio")
	private String descripcio;

	@Column(name = "tipus")
	@Enumerated(EnumType.STRING)
	private IntegracioAccioTipusEnumDto tipus;

	@Column(name = "temps_resposta")
	private Long tempsResposta;

	@Column(name = "estat")
	@Enumerated(EnumType.STRING)
	private IntegracioAccioEstatEnumDto estat;

	@Column(name = "codi_usuari")
	private String codiUsuari;

	/**
	 * Relació lògica (no hi ha FK física a base de dades) amb l'entitat via el seu codi -- la
	 * columna codi_entitat només emmagatzema el codi, no l'id, així que el join es fa per la
	 * columna codi (unique=true a dis_entitat), no per la clau primària.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "codi_entitat", referencedColumnName = "codi")
	private EntitatResourceEntity entitat;

	@Column(name = "error_descripcio")
	private String errorDescripcio;

	@Column(name = "excepcio_msg")
	private String excepcioMessage;

	@Column(name = "excepcio_stacktrace")
	private String excepcioStacktrace;

	@Column(name = "numero_registre")
	private String numeroRegistre;

}
