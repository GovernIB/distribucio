package es.caib.distribucio.back.command;

public class RegistreClassificarCommand {
	
	String titol;
	Long contingutId;
	String codiProcediment;
	String codiServei;
	// RegistreClassificarTipusEnum PROCEDIMENT / SERVEI 
	String tipus;
	
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public Long getContingutId() {
		return contingutId;
	}
	public void setContingutId(Long contingutId) {
		this.contingutId = contingutId;
	}	
	public String getCodiProcediment() {
		return codiProcediment;
	}
	public void setCodiProcediment(String codiProcediment) {
		this.codiProcediment = codiProcediment;
	}
	public String getCodiServei() {
		return codiServei;
	}
	public void setCodiServei(String codiServei) {
		this.codiServei = codiServei;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}

	public interface Classificar {}
}

