/**
 * 
 */
package es.caib.distribucio.core.api.registre;

/**
 * Classe que representa un interessat d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreInteressat {

	private String tipus;
	private String documentTipus;
	private String documentNum;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String raoSocial;
	private String pais;
	private String provincia;
	private String municipi;
	private String adresa;
	private String codiPostal;
	private String email;
	private String telefon;
	private String emailHabilitat;
	private String canalPreferent;
	private String observacions;
	private RegistreInteressat representant;
	private RegistreInteressat representat;



	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public String getDocumentTipus() {
		return documentTipus;
	}
	public void setDocumentTipus(String documentTipus) {
		this.documentTipus = documentTipus;
	}
	public String getDocumentNum() {
		return documentNum;
	}
	public void setDocumentNum(String documentNum) {
		this.documentNum = documentNum;
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
	public String getRaoSocial() {
		return raoSocial;
	}
	public void setRaoSocial(String raoSocial) {
		this.raoSocial = raoSocial;
	}
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
	public String getAdresa() {
		return adresa;
	}
	public void setAdresa(String adresa) {
		this.adresa = adresa;
	}
	public String getCodiPostal() {
		return codiPostal;
	}
	public void setCodiPostal(String codiPostal) {
		this.codiPostal = codiPostal;
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
	public String getEmailHabilitat() {
		return emailHabilitat;
	}
	public void setEmailHabilitat(String emailHabilitat) {
		this.emailHabilitat = emailHabilitat;
	}
	public String getCanalPreferent() {
		return canalPreferent;
	}
	public void setCanalPreferent(String canalPreferent) {
		this.canalPreferent = canalPreferent;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public RegistreInteressat getRepresentant() {
		return representant;
	}
	public void setRepresentant(RegistreInteressat representant) {
		this.representant = representant;
	}
	public RegistreInteressat getRepresentat() {
		return representat;
	}
	public void setRepresentat(RegistreInteressat representat) {
		this.representat = representat;
	}

}
