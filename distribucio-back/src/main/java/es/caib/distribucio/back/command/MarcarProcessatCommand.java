/**
 * 
 */
package es.caib.distribucio.back.command;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang.builder.ToStringBuilder;


public class MarcarProcessatCommand {

	@NotBlank
	private String motiu;

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
