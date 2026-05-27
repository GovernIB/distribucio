/**
 * 
 */
package es.caib.distribucio.back.command;

import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.intf.dto.BackofficeFiltreDto;
import es.caib.distribucio.logic.intf.dto.BackofficeTipusEnumDto;
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
public class BackofficeFiltreCommand {

    private String codi;
    private String nom;
    private String url;
    private BackofficeTipusEnumDto tipus;

	public static BackofficeFiltreCommand asCommand(BackofficeFiltreDto dto) {
		BackofficeFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				BackofficeFiltreCommand.class);
		return command;
	}
	public static BackofficeFiltreDto asDto(BackofficeFiltreCommand command) {
		BackofficeFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				BackofficeFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	

}
