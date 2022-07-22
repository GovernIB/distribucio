package es.caib.distribucio.rest.client;

import es.caib.distribucio.rest.client.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.domini.Estat;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InfoCanviEstat {

    AnotacioRegistreId id;
    Estat estat;
    String observacions;

}
