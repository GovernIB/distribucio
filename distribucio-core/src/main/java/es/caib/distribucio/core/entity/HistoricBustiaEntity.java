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

import es.caib.distribucio.logic.intf.dto.historic.HistoricTipusEnumDto;

/**
 * Classe de model de dades que conté la informació de les dades estadístiques
 * històriques per bústies i per unitat organitzativa. Guarda informació del
 * número d'usuaris per permís directe o per rol.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "dis_his_bustia")
public class HistoricBustiaEntity extends AbstractPersistable<Long> {
		
	private static final long serialVersionUID = -1732378813457140401L;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "entitat_id")
	@ForeignKey(name = "dis_entitat_his_bustia_fk")
	private EntitatEntity entitat;

	/** Distinció per unitat organitzativa. Si és null llavors és un registre dels
	 * agregats de l'entitat.
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "unitat_id")
	@ForeignKey(name = "dis_unitat_his_bustia_fk")
	private UnitatOrganitzativaEntity unitat;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "tipus", length = 16, nullable = false)
	private HistoricTipusEnumDto tipus;

	@Column(name = "data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;
	
	@Column(name = "bustia_id", nullable = false)
	private Long bustiaId;

	@Column(name = "nom", length = 1024, nullable = false)
	private String nom;
	
	/** Número d'usuaris total*/
	@Column(name = "usuaris")
	private Long usuaris = 0L;

	/** Número d'usuaris per permís directe.*/
	@Column(name = "usuaris_permis")
	private Long usuarisPermis = 0L;

	/** Número d'usuaris per permís per rol.*/
	@Column(name = "usuaris_rol")
	private Long usuarisRol= 0L;

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

	public Long getBustiaId() {
		return bustiaId;
	}

	public void setBustiaId(Long bustiaId) {
		this.bustiaId = bustiaId;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public Long getUsuaris() {
		return usuaris;
	}

	public void setUsuaris(Long usuaris) {
		this.usuaris = usuaris;
	}

	public Long getUsuarisPermis() {
		return usuarisPermis;
	}

	public void setUsuarisPermis(Long usuarisPermis) {
		this.usuarisPermis = usuarisPermis;
	}

	public Long getUsuarisRol() {
		return usuarisRol;
	}

	public void setUsuarisRol(Long usuarisRol) {
		this.usuarisRol = usuarisRol;
	}	
}
