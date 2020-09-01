package es.caib.distribucio.war.command;

import org.hibernate.validator.constraints.NotEmpty;

public class RegistreEnviarViaEmailCommand {
	
	Long bustiaId;
	Long contingutId;
	@NotEmpty
	String addresses;
	
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
	public String getAddresses() {
		return addresses;
	}
	public void setAddresses(String addresses) {
		this.addresses = addresses;
	}
	
	

}

