package es.caib.distribucio.war.command;

import org.hibernate.validator.constraints.NotEmpty;

public class RegistreClassificarCommand {
	
	Long bustiaId;
	Long contingutId;
	@NotEmpty(groups = {Classificar.class})
	String codiProcediment;
	
	public Long getBustiaId() {
		return bustiaId;
	}
	public void setBustiaId(Long bustiaId) {
		this.bustiaId = bustiaId;
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

