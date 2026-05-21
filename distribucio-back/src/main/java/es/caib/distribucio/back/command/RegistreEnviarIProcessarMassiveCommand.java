package es.caib.distribucio.back.command;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.validation.constraints.NotEmpty;

/**
 * Command per a enviar i processar anotacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RegistreEnviarIProcessarMassiveCommand extends MassiveCommand {

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

