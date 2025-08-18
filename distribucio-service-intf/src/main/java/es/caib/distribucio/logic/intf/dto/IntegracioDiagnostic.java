package es.caib.distribucio.logic.intf.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegracioDiagnostic {

    private boolean correcte;
    private String errMsg;
    private String prova;
    private Map<String, IntegracioDiagnostic> diagnosticsEntitat;
}
