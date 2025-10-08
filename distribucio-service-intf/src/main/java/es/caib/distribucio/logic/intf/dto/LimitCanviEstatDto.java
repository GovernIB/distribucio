package es.caib.distribucio.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LimitCanviEstatDto {

    protected Long id;
    private String usuariCodi;
    private String descripcio;
    private Integer limitMinutLaboral;
    private Integer limitMinutNoLaboral;
    private Integer limitDiaLaboral;
    private Integer limitDiaNoLaboral;

}
