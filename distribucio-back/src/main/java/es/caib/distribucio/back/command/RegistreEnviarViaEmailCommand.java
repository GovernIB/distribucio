package es.caib.distribucio.back.command;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class RegistreEnviarViaEmailCommand {

	private Long contingutId;
	@NotEmpty
	private String addresses;
	private String motiu;
	private String entorn;

}

