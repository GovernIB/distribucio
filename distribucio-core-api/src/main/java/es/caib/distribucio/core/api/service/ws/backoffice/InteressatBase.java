/**
 * 
 */
package es.caib.distribucio.core.api.service.ws.backoffice;

/**
 * Classe que representa base de l'interessat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InteressatBase {

	private InteressatTipus tipus;
	private DocumentTipus documentTipus;
	private String documentNumero;
	private String raoSocial;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String paisCodi;
	private String provinciaCodi;
	private String municipiCodi;
	private String pais;
	private String provincia;
	private String municipi;	
	private String adresa;
	private String cp;
	private String email;
	private String telefon;
	private String adresaElectronica;
	private String canal;
	private String observacions;

	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public String getMunicipi() {
		return municipi;
	}
	public void setMunicipi(String municipi) {
		this.municipi = municipi;
	}
	public InteressatTipus getTipus() {
		return tipus;
	}
	public void setTipus(InteressatTipus tipus) {
		this.tipus = tipus;
	}
	public DocumentTipus getDocumentTipus() {
		return documentTipus;
	}
	public void setDocumentTipus(DocumentTipus documentTipus) {
		this.documentTipus = documentTipus;
	}
	public String getDocumentNumero() {
		return documentNumero;
	}
	public void setDocumentNumero(String documentNumero) {
		this.documentNumero = documentNumero;
	}
	public String getRaoSocial() {
		return raoSocial;
	}
	public void setRaoSocial(String raoSocial) {
		this.raoSocial = raoSocial;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getLlinatge1() {
		return llinatge1;
	}
	public void setLlinatge1(String llinatge1) {
		this.llinatge1 = llinatge1;
	}
	public String getLlinatge2() {
		return llinatge2;
	}
	public void setLlinatge2(String llinatge2) {
		this.llinatge2 = llinatge2;
	}
	public String getPaisCodi() {
		return paisCodi;
	}
	public void setPaisCodi(String paisCodi) {
		this.paisCodi = paisCodi;
	}
	public String getProvinciaCodi() {
		return provinciaCodi;
	}
	public void setProvinciaCodi(String provinciaCodi) {
		this.provinciaCodi = provinciaCodi;
	}
	public String getMunicipiCodi() {
		return municipiCodi;
	}
	public void setMunicipiCodi(String municipiCodi) {
		this.municipiCodi = municipiCodi;
	}
	public String getAdresa() {
		return adresa;
	}
	public void setAdresa(String adresa) {
		this.adresa = adresa;
	}
	public String getCp() {
		return cp;
	}
	public void setCp(String cp) {
		this.cp = cp;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelefon() {
		return telefon;
	}
	public void setTelefon(String telefon) {
		this.telefon = telefon;
	}
	public String getAdresaElectronica() {
		return adresaElectronica;
	}
	public void setAdresaElectronica(String adresaElectronica) {
		this.adresaElectronica = adresaElectronica;
	}
	public String getCanal() {
		return canal;
	}
	public void setCanal(String canal) {
		this.canal = canal;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}

}
