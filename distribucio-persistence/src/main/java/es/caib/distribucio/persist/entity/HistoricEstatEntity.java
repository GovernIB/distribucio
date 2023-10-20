package es.caib.distribucio.persist.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.historic.HistoricTipusEnumDto;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;

/**
 * Classe de model de dades que conté la informació de les dades estadístiques
 * històriques d'estats de les anotacions per unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = BaseConfig.DB_PREFIX + "his_estat")
public class HistoricEstatEntity extends DistribucioPersistable<Long> {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "entitat_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "his_estat_entitat_fk"))
	private EntitatEntity entitat;

	/** Distinció per unitat organitzativa. Si és null llavors és un registre dels
	 * agregats de l'entitat.
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "unitat_id",
			foreignKey = @ForeignKey(name = BaseConfig.DB_PREFIX + "his_estat_unitat_fk"))
	private UnitatOrganitzativaEntity unitat;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "tipus", length = 16, nullable = false)
	private HistoricTipusEnumDto tipus;

	@Column(name = "data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "estat", length = 64, nullable = false)
	private RegistreProcesEstatEnum estat;
	
	/** Número d'anotacions correctes */
	@Column(name = "correcte")
	private Long correcte = 0L;

	/** Número d'anotacions correctes totals fins la data */
	@Column(name = "correcte_total")
	private Long correcteTotal = 0L;

	/** Número d'anotacions amb error.*/
	@Column(name = "error")
	private Long error = 0L;

	/** Número d'anotacions total per amb error.*/
	@Column(name = "error_total")
	protected Long errorTotal = 0L;
	
	/** Número d'anotacions total amb i sense error.*/
	@Column(name = "total")
	private Long total = 0L;

	public EntitatEntity getEntitat() {
		return entitat;
	}

	public void setEntitat(EntitatEntity entitat) {
		this.entitat = entitat;
	}

	public UnitatOrganitzativaEntity getUnitat() {
		return unitat;
	}

	public void setUnitat(UnitatOrganitzativaEntity unitat) {
		this.unitat = unitat;
	}

	public HistoricTipusEnumDto getTipus() {
		return tipus;
	}

	public void setTipus(HistoricTipusEnumDto tipus) {
		this.tipus = tipus;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public RegistreProcesEstatEnum getEstat() {
		return estat;
	}

	public void setEstat(RegistreProcesEstatEnum estat) {
		this.estat = estat;
	}

	public Long getCorrecte() {
		return correcte;
	}

	public void setCorrecte(Long correcte) {
		this.correcte = correcte;
	}

	public Long getCorrecteTotal() {
		return correcteTotal;
	}

	public void setCorrecteTotal(Long correcteTotal) {
		this.correcteTotal = correcteTotal;
	}

	public Long getError() {
		return error;
	}

	public void setError(Long error) {
		this.error = error;
	}

	public Long getErrorTotal() {
		return errorTotal;
	}

	public void setErrorTotal(Long errorTotal) {
		this.errorTotal = errorTotal;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}
}
