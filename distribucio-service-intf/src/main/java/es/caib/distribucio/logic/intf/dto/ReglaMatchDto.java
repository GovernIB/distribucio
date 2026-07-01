package es.caib.distribucio.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReglaMatchDto {
    private ReglaDto regla;
    private String sia;
    private String tramit;
}
