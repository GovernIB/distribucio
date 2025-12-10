/**
 * 
 */
package es.caib.distribucio.logic.intf.service.ws.backoffice;

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
public class AnotacioRegistreId {

    private String indetificador;
	private String identificador;
	private String clauAcces;

    public String getIdentificador() {
        return identificador != null ? identificador : indetificador;
    }
}
