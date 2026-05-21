package es.caib.distribucio.back.command;

import javax.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command per a enviar i processar anotacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RegistreEnviarIProcessarCommand {

	private Long contingutId;
	@NotEmpty
    private String addresses;
	@NotEmpty
    private String motiu;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	

}

