package es.caib.distribucio.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.AbstractPersistable;

import es.caib.distribucio.core.api.dto.historic.HistoricTipusEnumDto;

/**
 * Classe de model de dades que conté la informació de les dades estadístiques
 * històriques d'anotacions per unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "dis_his_anotacio")
public class HistoricAnotacioEntity extends AbstractPersistable<Long> {
		
	private static final long serialVersionUID = -1732378813457140401L;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "dis_entitat_his_anot_fk")
	private EntitatEntity entitat;

	/** Distinció per unitat organitzativa. Si és null llavors és un registre dels
	 * agregats de l'entitat.
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "unitat_id")
	@ForeignKey(name = "dis_unitat_his_anot_fk")
	private UnitatOrganitzativaEntity unitat;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "tipus", length = 16, nullable = false)
	private HistoricTipusEnumDto tipus;

	@Column(name = "data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;
	
	/** Número d'anotacions creades en la data */
	@Column(name = "anotacions")
	private Long anotacions = 0L;

	/** Número d'anotacions creades totals fins la data */
	@Column(name = "anotacions_total")
	private Long anotacionsTotal = 0L;

	/** Número de reenviaments a altres bústies.*/
	@Column(name = "reenviaments")
	private Long reenviaments = 0L;

	/** Número d'emails enviats.*/
	@Column(name = "emails")
	private Long emails = 0L;
	
	/** Número de justificants.*/
	@Column(name = "justificants")
	private Long justificants = 0L;
	
	/** Número d'annexos.*/
	@Column(name = "annexos")
	private Long annexos = 0L;

	/** Número de bústies.*/
	@Column(name = "busties")
	private Long busties = 0L;

	/** Número d'usuaris.*/
	@Column(name = "usuaris")
	private Long usuaris = 0L;

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

	public Long getAnotacions() {
		return anotacions;
	}

	public void setAnotacions(Long anotacions) {
		this.anotacions = anotacions;
	}

	public Long getAnotacionsTotal() {
		return anotacionsTotal;
	}

	public void setAnotacionsTotal(Long anotacionsTotal) {
		this.anotacionsTotal = anotacionsTotal;
	}

	public Long getReenviaments() {
		return reenviaments;
	}

	public void setReenviaments(Long reenviaments) {
		this.reenviaments = reenviaments;
	}

	public Long getEmails() {
		return emails;
	}

	public void setEmails(Long emails) {
		this.emails = emails;
	}

	public Long getJustificants() {
		return justificants;
	}

	public void setJustificants(Long justificants) {
		this.justificants = justificants;
	}

	public Long getAnnexos() {
		return annexos;
	}

	public void setAnnexos(Long annexos) {
		this.annexos = annexos;
	}

	public Long getBusties() {
		return busties;
	}

	public void setBusties(Long busties) {
		this.busties = busties;
	}

	public Long getUsuaris() {
		return usuaris;
	}

	public void setUsuaris(Long usuaris) {
		this.usuaris = usuaris;
	}
}
