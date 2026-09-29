package es.caib.distribucio.persist.resourceentity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.ExcepcioLogResource;
import es.caib.distribucio.persist.base.entity.BaseAuditableEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Excepció del sistema guardada per a la consulta del superadministrador.
 * <p>
 * L'escriu l'{@code ExcepcioLogHelper} (cridat des dels aspectes de serveis i controllers), la
 * llegeixen la interfície JSP i el recurs REST {@link ExcepcioLogResource}, i la tasca periòdica
 * d'esborrat n'elimina les antigues.
 * L'entitat es desa amb el codi i no amb una clau forana perquè el log no pot fallar
 * ni dependre de l'existència de l'entitat.
 *
 * @author Límit Tecnologies
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "excepcio_log")
@Getter
@Setter
@NoArgsConstructor
public class ExcepcioLogResourceEntity extends BaseAuditableEntity<ExcepcioLogResource, Long> {

	public static final int TIPUS_LENGTH = 512;
	public static final int OBJECT_LENGTH = 512;
	public static final int URI_LENGTH = 1024;
	public static final int ORIGEN_LENGTH = 512;
	public static final int PARAM_LENGTH = 512;
	public static final int MESSAGE_LENGTH = 4000;
	/**
	 * Caràcters que es guarden del missatge. La columna és de 4000, però a Oracle un VARCHAR2
	 * no passa de 4000 bytes encara que es declari en CHAR, i els missatges porten accents
	 * (2 bytes en UTF-8): amb 4000 caràcters l'insert podria fallar i l'excepció es perdria.
	 */
	public static final int MESSAGE_MAX_CARACTERS = 2000;
	public static final int ENTITAT_CODI_LENGTH = 64;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
	@SequenceGenerator(name = "default_seq", sequenceName = "dis_hibernate_seq", allocationSize = 1)
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data", nullable = false)
	private Date data;

	@Column(name = "tipus", length = TIPUS_LENGTH)
	private String tipus;

	@Column(name = "object_id", length = OBJECT_LENGTH)
	private String objectId;

	@Column(name = "object_class", length = OBJECT_LENGTH)
	private String objectClass;

	@Column(name = "uri", length = URI_LENGTH)
	private String uri;

	@Column(name = "origen", length = ORIGEN_LENGTH)
	private String origen;

	@Column(name = "param1", length = PARAM_LENGTH)
	private String param1;

	@Column(name = "param2", length = PARAM_LENGTH)
	private String param2;

	@Column(name = "message", length = MESSAGE_LENGTH)
	private String message;

	@Lob
	@Column(name = "stacktrace")
	private String stacktrace;

	@Column(name = "entitat_codi", length = ENTITAT_CODI_LENGTH)
	private String entitatCodi;

}
