/**
 *
 */
package es.caib.distribucio.api.interna.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Classe que representa id del anotació de registre.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnotacioRegistreId {

    @Hidden
    private String indetificador;
    private String identificador;
    private String clauAcces;

    public String getIdentificador() {
        return identificador != null ? identificador : indetificador;
    }
}
