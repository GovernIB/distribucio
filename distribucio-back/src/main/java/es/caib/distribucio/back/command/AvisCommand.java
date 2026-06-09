/**
 * 
 */
package es.caib.distribucio.back.command;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.intf.dto.AvisDto;
import es.caib.distribucio.logic.intf.dto.AvisNivellEnumDto;

/**
 * Command per al manteniment d'avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class AvisCommand {

	private Long id;
	@NotEmpty
	private String assumpte;
	@NotEmpty
	private String missatge;
	@NotNull
	private Date dataInici;
//	@NotNull
	private Date dataFinal;
	private Boolean actiu;
	@NotNull
	private AvisNivellEnumDto avisNivell;
    private Long entitatId;

	public static AvisCommand asCommand(AvisDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				AvisCommand.class);
	}
	public static AvisDto asDto(AvisCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				AvisDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
