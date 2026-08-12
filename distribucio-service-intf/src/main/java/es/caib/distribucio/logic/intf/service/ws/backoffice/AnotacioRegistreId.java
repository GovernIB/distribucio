/**
 * 
 */
package es.caib.distribucio.logic.intf.service.ws.backoffice;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	private String clauAcces;

    public void setIdentificador(String identificador) {
    	// Per compatibilitat amb backoffices anteriors a la versió 1.0.7
    	this.indetificador = identificador;
    }
    /** Mètode per permetre obtenir "indetificador" com identificador però que no ha
     * de crear un JSON.
     * 
     * @return
     */
    @JsonIgnore
	@XmlTransient
    public String getIdentificador() {
        return indetificador;
    }
}
