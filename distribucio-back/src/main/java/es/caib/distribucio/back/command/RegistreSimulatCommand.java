/**
 * 
 */
package es.caib.distribucio.back.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.intf.dto.RegistreSimulatDto;
import es.caib.distribucio.logic.intf.dto.ReglaPresencialEnumDto;


/**
 * Command que simula anotacio de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class RegistreSimulatCommand {

	@NotNull
	private Long unitatId;
	@Size(max = 16)
	private String assumpteCodi;
	@Size(max = 64)
	private String procedimentCodi;
	private String serveiCodi;
	private String tramitCodi;
	private Long bustiaId;
	private ReglaPresencialEnumDto presencial;

	public static RegistreSimulatCommand asCommand(RegistreSimulatDto dto) {
		RegistreSimulatCommand command = ConversioTipusHelper.convertir(
				dto,
				RegistreSimulatCommand.class);
		return command;
	}
	public static RegistreSimulatDto asDto(RegistreSimulatCommand command) {
		RegistreSimulatDto dto = ConversioTipusHelper.convertir(
				command,
				RegistreSimulatDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


}
