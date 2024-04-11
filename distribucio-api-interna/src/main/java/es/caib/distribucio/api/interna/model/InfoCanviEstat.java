package es.caib.distribucio.api.interna.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class InfoCanviEstat {

    AnotacioRegistreId id;
    Estat estat;
    String observacions;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
