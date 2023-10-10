/**
 * 
 */
package es.caib.distribucio.logic.intf.registre;

/**
 * Classe que representa un interessat d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreInteressat {

	private Long id;
	private String tipus;//19 (Long to String)
	private String documentTipus;//1
	private String documentNum;//17
	private String nom;//255
	private String llinatge1;//255
	private String llinatge2;//255
	private String raoSocial;//2000
	private String pais;//100
	private String paisCodi;//19 (Long to String) //3
	private String provincia;//50
	private String provinciaCodi;//19 (Long to String) //2
	private String municipi;//50
	private String municipiCodi;//19 (Long to String) //4
	private String adresa;//160
	private String codiPostal;//5
	private String email;//160
	private String telefon;//20
	private String emailHabilitat;//160
	private String canalPreferent;//2
	private String observacions;//160
	private String codiDire;//15
	private RegistreInteressat representant;
	private RegistreInteressat representat;
    private String organCodi;//is not set



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
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public String getMunicipiCodi() {
		return municipiCodi;
	}
	public void setMunicipiCodi(String municipiCodi) {
		this.municipiCodi = municipiCodi;
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodiDire() {
		return codiDire;
	}
	public void setCodiDire(String codiDire) {
		this.codiDire = codiDire;
	}
	public String getOrganCodi() {
		return organCodi;
	}
	public void setOrganCodi(String organCodi) {
		this.organCodi = organCodi;
	}

}
