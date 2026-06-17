/**
 * 
 */
package es.caib.distribucio.back.command;

import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.intf.dto.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command per al filtre del localitzador de continguts
 * dels usuaris administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ExecucioMassivaFiltreCommand {

	private String usuariCodi;
    private ExecucioMassivaTipusDto tipus;
	
	public static ExecucioMassivaFiltreCommand asCommand(ExecucioMassivaFiltreDto dto) {
		ExecucioMassivaFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				ExecucioMassivaFiltreCommand.class);
		return command;
	}
	public static ExecucioMassivaFiltreDto asDto(ExecucioMassivaFiltreCommand command) {
		ExecucioMassivaFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				ExecucioMassivaFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
