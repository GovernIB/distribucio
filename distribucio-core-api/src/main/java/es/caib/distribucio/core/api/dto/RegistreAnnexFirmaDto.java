package es.caib.distribucio.core.api.dto;

import java.io.Serializable;

/**
 * Classe per fer el mapeig de la classe RegistreAnnexFirmaEntity.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreAnnexFirmaDto  implements Serializable {
	
	private String tipus;
	private String perfil;
	private String fitxerNom;
	private String tipusMime;
	private String csvRegulacio;
	private Boolean autofirma = false;
	private String gesdocFirmaId;
	
	
	
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
	public Boolean getAutofirma() {
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
	
	

}
