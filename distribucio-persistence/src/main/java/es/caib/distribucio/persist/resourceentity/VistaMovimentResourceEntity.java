package es.caib.distribucio.persist.resourceentity;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.model.VistaMovimentResource;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.persist.base.entity.BaseResourceEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Entitat de només lectura del recurs {@link VistaMovimentResource}: una fila per moviment d'una
 * anotació de registre (una anotació movent-se d'una bústia origen a una bústia destí).
 * <p>
 * No hi ha cap taula física que mapejar (una fila combina una anotació amb un dels seus moviments
 * a {@code dis_cont_mov}), per això es mapeja amb un {@code @Subselect} de Hibernate. 
 * L'id és la combinació {@code <idRegistre>_<idContingutDesti>}.
 *
 * @author Límit Tecnologies
 */
@Entity
@Subselect("select concat(r.id, concat('_', dst.id)) as id, "
		+ "    r.id as idRegistre, "
		+ "    m.id as movimentId, "
		+ "    r.numero as numero, "
		+ "    r.extracte as titol, "
		+ "    r.num_orig as numeroOrigen, "
		+ "    m.remitent_codi as remitent, "
		+ "    r.data as data, "
		+ "    m.createdDate as dataMoviment, "
		+ "    r.proces_estat as procesEstat, "
		+ "    r.proces_error as procesError, "
		+ "    r.pendent as pendent, "
		+ "    r.enviat_per_email as enviatPerEmail, "
		+ "    r.docfis_codi as documentacioFisicaCodi, "
		+ "    r.docfis_desc as documentacioFisicaDescripcio, "
		+ "    r.back_codi as backCodi, "
		+ "    ori.id as origen, "
		+ "    dst.id as desti, "
		+ "    dst.id as destiLogic, "
		+ "    contingut.entitat_id as entitatId, "
		+ "    (select count(al.id) from " + BaseConfig.DB_PREFIX + "alerta al "
		+ "        where al.contingut_id = r.id and al.llegida = 0) as alertesPendents, "
		+ "    (select rg.nom from " + BaseConfig.DB_PREFIX + "regla rg "
		+ "        where rg.id = r.regla_id) as reglaNom, "
		+ "    (select count(cm.id) from " + BaseConfig.DB_PREFIX + "cont_comment cm "
		+ "        where cm.contingut_id = r.id) as numComentaris "
		+ "from " + BaseConfig.DB_PREFIX + "registre r "
		+ "inner join " + BaseConfig.DB_PREFIX + "cont_mov m on m.contingut_id = r.id "
		+ "inner join " + BaseConfig.DB_PREFIX + "contingut ori on ori.id = m.origen_id "
		+ "inner join " + BaseConfig.DB_PREFIX + "contingut dst on dst.id = m.desti_id "
		+ "inner join " + BaseConfig.DB_PREFIX + "contingut contingut on contingut.id = r.id")
@Immutable
@Getter
@Setter
@NoArgsConstructor
public class VistaMovimentResourceEntity extends BaseResourceEntity<VistaMovimentResource, String> {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "idRegistre")
	private Long idRegistre;

	@Column(name = "movimentId")
	private Long movimentId;

	@Column(name = "numero")
	private String numero;

	@Column(name = "titol")
	private String titol;

	@Column(name = "numeroOrigen")
	private String numeroOrigen;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "remitent", insertable = false, updatable = false)
	private UsuariResourceEntity remitent;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data")
	private Date data;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "dataMoviment")
	private Date dataMoviment;

	@Enumerated(EnumType.STRING)
	@Column(name = "procesEstat")
	private RegistreProcesEstatEnum procesEstat;

	@Column(name = "procesError")
	private String procesError;

	@Column(name = "pendent")
	private boolean pendent;

	@Column(name = "enviatPerEmail")
	private boolean enviatPerEmail;

	@Column(name = "documentacioFisicaCodi")
	private String documentacioFisicaCodi;

	@Column(name = "documentacioFisicaDescripcio")
	private String documentacioFisicaDescripcio;

	@Column(name = "backCodi")
	private String backCodi;

	/** Id del contingut destí del moviment. Necessari per a les futures accions "Reenviar"/"Detalls". */
	@Column(name = "destiLogic")
	private Long destiLogic;

	@Column(name = "entitatId")
	private Long entitatId;

	/** Nombre d'alertes no llegides de l'anotació. Determina si el botó "Llistat d'alertes" és visible. */
	@Column(name = "alertesPendents")
	private int alertesPendents;

	/** Nom de la regla que ha processat l'anotació (només informat si l'anotació té regla). */
	@Column(name = "reglaNom")
	private String reglaNom;

	/** Nombre de comentaris de l'anotació de registre (compartit entre els seus moviments). */
	@Column(name = "numComentaris")
	private int numComentaris;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "origen", insertable = false, updatable = false)
	private BustiaResourceEntity bustiaOrigen;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "desti", insertable = false, updatable = false)
	private BustiaResourceEntity bustiaDesti;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

}
