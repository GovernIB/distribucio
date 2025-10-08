package es.caib.distribucio.back.command;

import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.intf.dto.LimitCanviEstatDto;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class LimitCanviEstatCommand {

    private Long id;
    @NotNull
    private String usuariCodi;
    @NotNull
    private String descripcio;
    private Integer limitMinutLaboral;
    private Integer limitMinutNoLaboral;
    private Integer limitDiaLaboral;
    private Integer limitDiaNoLaboral;

    public static LimitCanviEstatDto asDto(LimitCanviEstatCommand command) {
        return ConversioTipusHelper.convertir(command, LimitCanviEstatDto.class);
    }

    public static LimitCanviEstatCommand asCommand(LimitCanviEstatDto dto) {
        return ConversioTipusHelper.convertir(dto, LimitCanviEstatCommand.class);
    }
}
