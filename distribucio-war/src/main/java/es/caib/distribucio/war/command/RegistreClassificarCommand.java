package es.caib.distribucio.war.command;

import org.hibernate.validator.constraints.NotEmpty;

public class RegistreClassificarCommand {
	
	String titol;
	Long contingutId;
	//@NotEmpty(groups = {Classificar.class})
	String codiProcediment;
	
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

	public interface Classificar {}
}

