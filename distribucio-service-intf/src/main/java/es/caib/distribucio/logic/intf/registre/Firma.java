package es.caib.distribucio.logic.intf.registre;

public class Firma {

	private String tipus;//4
	private String perfil;//4
	private String fitxerNom;//comes from document custody plugin
	private byte[] contingut;//comes from document custody plugin
	private String tipusMime;//comes from document custody plugin
	private String csv;//255
	private String csvRegulacio;//13

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
	public byte[] getContingut() {
		return contingut;
	}
	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
	}
	public String getTipusMime() {
		return tipusMime;
	}
	public void setTipusMime(String tipusMime) {
		this.tipusMime = tipusMime;
	}
	public String getCsv() {
		return csv;
	}
	public void setCsv(String csv) {
		this.csv = csv;
	}
	public String getCsvRegulacio() {
		return csvRegulacio;
	}
	public void setCsvRegulacio(String csvRegulacio) {
		this.csvRegulacio = csvRegulacio;
	}
//	public Firma(
//			String tipus, 
//			String perfil, 
//			String fitxerNom, 
//			byte[] contingut, 
//			String tipusMime, 
//			String csv,
//			String csvRegulacio) {
//		super();
//		this.tipus = tipus;
//		this.perfil = perfil;
//		this.fitxerNom = fitxerNom;
//		this.contingut = contingut;
//		this.tipusMime = tipusMime;
//		this.csv = csv;
//		this.csvRegulacio = csvRegulacio;
//	}
//	public Firma() {
//		super();
//		// TODO Auto-generated constructor stub
//	}

}
