package es.caib.distribucio.plugin.dadesext;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CodiValor implements Serializable, Comparable<CodiValor> {

    @NonNull
    @JsonProperty("id")
    private String id;
    @NonNull
    @JsonProperty("descripcion")
    private String descripcio;

    @Override
    public int compareTo(CodiValor o) {
        return descripcio.compareToIgnoreCase(o.getDescripcio());
    }

    private static final long serialVersionUID = -5602898182576627524L;

}
