package es.caib.distribucio.back.command;

import javax.validation.constraints.NotEmpty;

public class RegistreEnviarViaEmailCommand {
	
	Long contingutId;
	@NotEmpty
	String addresses;
	String motiu;
	String entorn;
	
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
	public String getMotiu() {
		return motiu;
	}
	public void setMotiu(String motiu) {
		this.motiu = motiu;
	}
	public String getEntorn() {
		return entorn;
	}
	public void setEntorn(String entorn) {
		this.entorn = entorn;
	}
}

