/**
 * 
 */
package es.caib.distribucio.logic.intf.registre;

import java.util.Date;
import java.util.List;


/**
 * Classe que representa una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreAnotacio {
	//length of fields in registre application
	private String tipusES; //1
	private String numero;//255
	private Date data;
	private Date dataOrigen;
	private String identificador;//19 (Long to String)
	private String entitatCodi;//255
	private String entitatDescripcio;//255
	private String oficinaCodi;//9
	private String oficinaDescripcio;//300
	private String oficinaOrigenCodi;//9
	private String oficinaOrigenDescripcio;//is not filled in registre
	private String llibreCodi;//4
	private String llibreDescripcio;//255
	private String extracte;//240
	private String assumpteTipusCodi;//2
	private String assumpteTipusDescripcio;//Deprecated, will be alliminated
	private String assumpteCodi;//16
	private String procedimentCodi;//19 (Long to String)
	private String assumpteDescripcio;//255
	private String referencia;//16
	private String expedientNumero;//80
	private String numeroOrigen;//20
	private String idiomaCodi;//19
	private String idiomaDescripcio;//10 (18n value - i.e. Castellano)
	private String transportTipusCodi;//20
	private String transportTipusDescripcio;// 25 (18n value i.e. Correo postal certificado) 
	private String transportNumero;//20
	private String usuariCodi;//19 (Long to String)
	private String usuariNom;//767
	private String usuariContacte;//255
	private String aplicacioCodi;//255
	private String aplicacioVersio;//255
	private String documentacioFisicaCodi;//19 (Long to String)
	private String documentacioFisicaDescripcio;// 66 (18n value i.e. DocumentaciÃ³n adjunta digitalizada y complementariamente en papel) 
	private String observacions;//50
	private String exposa;//2147483647
	private String solicita;//2147483647
	private boolean presencial;
	private List<RegistreInteressat> interessats;
	private List<RegistreAnnex> annexos;
	private RegistreAnnex justificant;
	
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public Date getDataOrigen() {
		return dataOrigen;
	}
	public void setDataOrigen(Date dataOrigen) {
		this.dataOrigen = dataOrigen;
	}
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public String getEntitatCodi() {
		return entitatCodi;
	}
	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}
	public String getEntitatDescripcio() {
		return entitatDescripcio;
	}
	public void setEntitatDescripcio(String entitatDescripcio) {
		this.entitatDescripcio = entitatDescripcio;
	}
	public String getOficinaCodi() {
		return oficinaCodi;
	}
	public void setOficinaCodi(String oficinaCodi) {
		this.oficinaCodi = oficinaCodi;
	}
	public String getOficinaDescripcio() {
		return oficinaDescripcio;
	}
	public void setOficinaDescripcio(String oficinaDescripcio) {
		this.oficinaDescripcio = oficinaDescripcio;
	}
	public String getOficinaOrigenCodi() {
		return oficinaOrigenCodi;
	}
	public void setOficinaOrigenCodi(String oficinaOrigenCodi) {
		this.oficinaOrigenCodi = oficinaOrigenCodi;
	}
	public String getOficinaOrigenDescripcio() {
		return oficinaOrigenDescripcio;
	}
	public void setOficinaOrigenDescripcio(String oficinaOrigenDescripcio) {
		this.oficinaOrigenDescripcio = oficinaOrigenDescripcio;
	}
	public String getLlibreCodi() {
		return llibreCodi;
	}
	public void setLlibreCodi(String llibreCodi) {
		this.llibreCodi = llibreCodi;
	}
	public String getLlibreDescripcio() {
		return llibreDescripcio;
	}
	public void setLlibreDescripcio(String llibreDescripcio) {
		this.llibreDescripcio = llibreDescripcio;
	}
	public String getExtracte() {
		return extracte;
	}
	public void setExtracte(String extracte) {
		this.extracte = extracte;
	}
	public String getAssumpteTipusCodi() {
		return assumpteTipusCodi;
	}
	public void setAssumpteTipusCodi(String assumpteTipusCodi) {
		this.assumpteTipusCodi = assumpteTipusCodi;
	}
	public String getAssumpteTipusDescripcio() {
		return assumpteTipusDescripcio;
	}
	public void setAssumpteTipusDescripcio(String assumpteTipusDescripcio) {
		this.assumpteTipusDescripcio = assumpteTipusDescripcio;
	}
	public String getAssumpteCodi() {
		return assumpteCodi;
	}
	public void setAssumpteCodi(String assumpteCodi) {
		this.assumpteCodi = assumpteCodi;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}	
	public String getAssumpteDescripcio() {
		return assumpteDescripcio;
	}
	public void setAssumpteDescripcio(String assumpteDescripcio) {
		this.assumpteDescripcio = assumpteDescripcio;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getExpedientNumero() {
		return expedientNumero;
	}
	public void setExpedientNumero(String expedientNumero) {
		this.expedientNumero = expedientNumero;
	}
	public String getNumeroOrigen() {
		return numeroOrigen;
	}
	public void setNumeroOrigen(String numeroOrigen) {
		this.numeroOrigen = numeroOrigen;
	}
	public String getIdiomaCodi() {
		return idiomaCodi;
	}
	public void setIdiomaCodi(String idiomaCodi) {
		this.idiomaCodi = idiomaCodi;
	}
	public String getIdiomaDescripcio() {
		return idiomaDescripcio;
	}
	public void setIdiomaDescripcio(String idiomaDescripcio) {
		this.idiomaDescripcio = idiomaDescripcio;
	}
	public String getTransportTipusCodi() {
		return transportTipusCodi;
	}
	public void setTransportTipusCodi(String transportTipusCodi) {
		this.transportTipusCodi = transportTipusCodi;
	}
	public String getTransportTipusDescripcio() {
		return transportTipusDescripcio;
	}
	public void setTransportTipusDescripcio(String transportTipusDescripcio) {
		this.transportTipusDescripcio = transportTipusDescripcio;
	}
	public String getTransportNumero() {
		return transportNumero;
	}
	public void setTransportNumero(String transportNumero) {
		this.transportNumero = transportNumero;
	}
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}
	public String getUsuariNom() {
		return usuariNom;
	}
	public void setUsuariNom(String usuariNom) {
		this.usuariNom = usuariNom;
	}
	public String getUsuariContacte() {
		return usuariContacte;
	}
	public void setUsuariContacte(String usuariContacte) {
		this.usuariContacte = usuariContacte;
	}
	public String getAplicacioCodi() {
		return aplicacioCodi;
	}
	public void setAplicacioCodi(String aplicacioCodi) {
		this.aplicacioCodi = aplicacioCodi;
	}
	public String getAplicacioVersio() {
		return aplicacioVersio;
	}
	public void setAplicacioVersio(String aplicacioVersio) {
		this.aplicacioVersio = aplicacioVersio;
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
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public String getExposa() {
		return exposa;
	}
	public void setExposa(String exposa) {
		this.exposa = exposa;
	}
	public String getSolicita() {
		return solicita;
	}
	public void setSolicita(String solicita) {
		this.solicita = solicita;
	}
	public List<RegistreInteressat> getInteressats() {
		return interessats;
	}
	public void setInteressats(List<RegistreInteressat> interessats) {
		this.interessats = interessats;
	}
	public List<RegistreAnnex> getAnnexos() {
		return annexos;
	}
	public void setAnnexos(List<RegistreAnnex> annexos) {
		this.annexos = annexos;
	}
	public RegistreAnnex getJustificant() {
		return justificant;
	}
	public void setJustificant(RegistreAnnex justificant) {
		this.justificant = justificant;
	}
	public boolean isPresencial() {
		return presencial;
	}
	public void setPresencial(boolean presencial) {
		this.presencial = presencial;
	}
	public String getTipusES() {
		return tipusES;
	}
	public void setTipusES(String tipusES) {
		this.tipusES = tipusES;
	}

	
}
