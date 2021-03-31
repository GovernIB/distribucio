package es.caib.distribucio.war.command;

import org.hibernate.validator.constraints.NotEmpty;

public class RegistreClassificarCommand {
	
	Long contingutId;
	@NotEmpty(groups = {Classificar.class})
	String codiProcediment;
	
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

