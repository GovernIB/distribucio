package es.caib.distribucio.plugin.distribucio;

public class DistribucioRegistreFirma  {
	
	private String tipus;
	private String perfil;
	private String fitxerNom;
	private String tipusMime;
	private String csvRegulacio;
	private Boolean autofirma = false;
	private String gesdocFirmaId;
	private byte[] contingut;
	private DistribucioRegistreAnnex annex;
	private long tamany;
	
	public long getTamany() {
		return tamany;
	}
	public void setTamany(long tamany) {
		this.tamany = tamany;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public String getPerfil() {
		return perfil;
	}
	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}
	public String getFitxerNom() {
		return fitxerNom;
	}
	public void setFitxerNom(String fitxerNom) {
		this.fitxerNom = fitxerNom;
	}
	public String getTipusMime() {
		return tipusMime;
	}
	public void setTipusMime(String tipusMime) {
		this.tipusMime = tipusMime;
	}
	public String getCsvRegulacio() {
		return csvRegulacio;
	}
	public void setCsvRegulacio(String csvRegulacio) {
		this.csvRegulacio = csvRegulacio;
	}
	public Boolean isAutofirma() {
		return autofirma;
	}
	public void setAutofirma(Boolean autofirma) {
		this.autofirma = autofirma;
	}
	public String getGesdocFirmaId() {
		return gesdocFirmaId;
	}
	public void setGesdocFirmaId(String gesdocFirmaId) {
		this.gesdocFirmaId = gesdocFirmaId;
	}
	public DistribucioRegistreAnnex getAnnex() {
		return annex;
	}
	public void setAnnex(DistribucioRegistreAnnex annex) {
		this.annex = annex;
	}


	public byte[] getContingut() {
		return contingut;
	}


	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
	}
	
}
