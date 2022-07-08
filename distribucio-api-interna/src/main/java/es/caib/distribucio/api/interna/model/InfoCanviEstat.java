package es.caib.distribucio.api.interna.model;

import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.core.api.service.ws.backoffice.Estat;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InfoCanviEstat {

    AnotacioRegistreId id;
    Estat estat;
    String observacions;

}
