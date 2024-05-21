package es.caib.distribucio.rest.client.integracio;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.integracio.domini.Estat;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InfoCanviEstat {

    AnotacioRegistreId id;
    Estat estat;
    String observacions;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}