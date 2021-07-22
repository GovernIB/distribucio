package es.caib.distribucio.core.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;

/**
 * Classe de model de dades que conté els moviments de les anotacions amb origen i destí
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Subselect("select 	concat(r.id,concat('_', dst.id)) as id,"
		+ "			r.id as idRegistre,"
		+ "			r.numero as numero, "
		+ "			r.enviat_per_email as enviatPerEmail, "
		+ "			r.proces_estat as procesEstat, "
		+ "			r.proces_error as procesError, "
		+ "			r.extracte as extracte, "
		+ "			r.num_orig as numeroOrigen, "
		+ "  		r.pendent, "	
		+ "			r.data as data, "
		+ " 		r.docfis_codi as documentacioFisicaCodi,"
		+ " 		r.docfis_desc as documentacioFisicaDescripcio,"
		+ " 		r.back_codi as backCodi,"
		+ "			ori.id as origen, "
		+ "			dst.id as desti, "
		+ "         m.createdDate as createdDate, "
		+ "         m.id as movimentId, "
		+ "         contingut.contmov_id as darrerMoviment, "
		+ "         contingut.entitat_id as entitat, "
		+ "         contingut.pare_id as bustia, "
		+ "			usuari.nom as remitent"
		+ " from	dis_registre r"
		+ " inner join dis_cont_mov m on m.contingut_id = r.id"
		+ " inner join dis_contingut ori on ori.id = m.origen_id "
		+ " inner join dis_contingut dst on dst.id = m.desti_id "
		+ " inner join dis_contingut contingut on contingut.id = r.id "
		+ " inner join dis_usuari usuari on usuari.codi = m.remitent_codi ")
@Immutable
public class VistaMovimentEntity {
	
	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "movimentId")
	private String movimentId;
	
	@Column(name = "idRegistre")
	private Long idRegistre;
	
	@Column(name = "numero")
	protected String numero;
	
	@Column(name = "extracte")
	protected String extracte;

	@Column(name = "numeroOrigen")
	protected String numeroOrigen;
	
	@Column(name = "remitent")
	protected String remitent;
	
	@Column(name = "pendent")
	protected boolean pendent;
	
	@Column(name = "enviatPerEmail")
	protected boolean enviatPerEmail;
	
	@Column(name = "procesEstat")
	private RegistreProcesEstatEnum procesEstat;
	
	@Column(name = "procesError")
	private String procesError;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data")
	private Date data;
	
	@Column(name = "documentacioFisicaCodi")
	protected String documentacioFisicaCodi;
	
	@Column(name = "documentacioFisicaDescripcio")
	protected String documentacioFisicaDescripcio;
	
	@Column(name = "backCodi")
	protected String backCodi;
	
	@Column(name = "origen")
	protected Long origen;
	
	@Column(name = "desti")
	protected Long desti;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createdDate")
	private Date createdDate;
	
	@Column(name = "entitat")
	protected Long entitat;
	
	@Column(name = "bustia")
	protected Long bustia;
	
	@Column(name = "darrerMoviment")
	private Long darrerMoviment;
	
	@OneToMany(
			mappedBy = "contingut",
			fetch = FetchType.LAZY,
			orphanRemoval = true)
	@OrderBy("createdDate ASC")
	protected List<AlertaEntity> alertes = new ArrayList<AlertaEntity>();
	
	@OneToMany(
			mappedBy = "registre",
			fetch = FetchType.LAZY,
			cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<RegistreInteressatEntity> interessats = new ArrayList<RegistreInteressatEntity>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMovimentId() {
		return movimentId;
	}

	public void setMovimentId(String movimentId) {
		this.movimentId = movimentId;
	}

	public Long getIdRegistre() {
		return idRegistre;
	}

	public void setIdRegistre(Long idRegistre) {
		this.idRegistre = idRegistre;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public boolean isEnviatPerEmail() {
		return enviatPerEmail;
	}

	public void setEnviatPerEmail(boolean enviatPerEmail) {
		this.enviatPerEmail = enviatPerEmail;
	}

	public RegistreProcesEstatEnum getProcesEstat() {
		return procesEstat;
	}

	public void setProcesEstat(RegistreProcesEstatEnum procesEstat) {
		this.procesEstat = procesEstat;
	}

	public String getProcesError() {
		return procesError;
	}

	public void setProcesError(String procesError) {
		this.procesError = procesError;
	}

	public String getExtracte() {
		return extracte;
	}

	public void setExtracte(String extracte) {
		this.extracte = extracte;
	}

	public String getNumeroOrigen() {
		return numeroOrigen;
	}

	public void setNumeroOrigen(String numeroOrigen) {
		this.numeroOrigen = numeroOrigen;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Long getOrigen() {
		return origen;
	}

	public void setOrigen(Long origen) {
		this.origen = origen;
	}

	public Long getDesti() {
		return desti;
	}

	public void setDesti(Long desti) {
		this.desti = desti;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Long getEntitat() {
		return entitat;
	}

	public void setEntitat(Long entitat) {
		this.entitat = entitat;
	}

	public Long getBustia() {
		return bustia;
	}

	public void setBustia(Long bustia) {
		this.bustia = bustia;
	}

	public Long getDarrerMoviment() {
		return darrerMoviment;
	}

	public void setDarrerMoviment(Long darrerMoviment) {
		this.darrerMoviment = darrerMoviment;
	}

	public List<AlertaEntity> getAlertes() {
		return alertes;
	}

	public void setAlertes(List<AlertaEntity> alertes) {
		this.alertes = alertes;
	}

	public List<RegistreInteressatEntity> getInteressats() {
		return interessats;
	}

	public void setInteressats(List<RegistreInteressatEntity> interessats) {
		this.interessats = interessats;
	}

	public String getRemitent() {
		return remitent;
	}

	public void setRemitent(String remitent) {
		this.remitent = remitent;
	}

	public boolean isPendent() {
		return pendent;
	}

	public void setPendent(boolean pendent) {
		this.pendent = pendent;
	}

	public String getDocumentacioFisicaCodi() {
		return documentacioFisicaCodi;
	}

	public void setDocumentacioFisicaCodi(String documentacioFisicaCodi) {
		this.documentacioFisicaCodi = documentacioFisicaCodi;
	}

	public String getDocumentacioFisicaDescripcio() {
		return documentacioFisicaDescripcio;
	}

	public void setDocumentacioFisicaDescripcio(String documentacioFisicaDescripcio) {
		this.documentacioFisicaDescripcio = documentacioFisicaDescripcio;
	}

	public String getBackCodi() {
		return backCodi;
	}

	public void setBackCodi(String backCodi) {
		this.backCodi = backCodi;
	}

}
