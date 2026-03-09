package es.caib.distribucio.back.command;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.back.validation.Backoffice;
import es.caib.distribucio.logic.intf.dto.BackofficeDto;
import es.caib.distribucio.logic.intf.dto.BackofficeTipusEnumDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Backoffice()
public class BackofficeCommand {

    private Long id;
    @NotEmpty @Size(max = 20)
    private String codi;
    @NotEmpty @Size(max = 64)
    private String nom;
    @NotEmpty @Size(max = 256)
    private String url;
    @Size(max = 255)
    private String usuari;
    @Size(max = 255)
    private String contrasenya;
    private Integer intents;
    // Deixem aquest camp que era usat per Bantel perquè potser el reutilitzem en el futur
    private Integer tempsEntreIntents;
    
    private Long entitatId;
    
    private BackofficeTipusEnumDto tipus;

    private Boolean enviamentEmail;
    private String emailResponsable;

	public static BackofficeCommand asCommand(BackofficeDto dto) {
        return ConversioTipusHelper.convertir(dto, BackofficeCommand.class);
    }
    public static BackofficeDto asDto(BackofficeCommand command) {
        return ConversioTipusHelper.convertir(command, BackofficeDto.class);
    }
}
