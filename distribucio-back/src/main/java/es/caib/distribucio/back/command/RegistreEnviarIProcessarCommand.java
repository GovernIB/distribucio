package es.caib.distribucio.back.command;

import javax.validation.constraints.NotEmpty;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command per a enviar i processar anotacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreEnviarIProcessarCommand {

	Long contingutId;
	@NotEmpty
	String addresses;
	@NotEmpty
	String motiu;
	
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
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	

}

