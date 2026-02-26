/**
 * 
 */
package es.caib.distribucio.logic.intf.service.ws.backoffice;

import javax.xml.bind.annotation.XmlTransient;

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

    public void setIdentificador(String identificador) {
    	this.identificador = identificador;
    	// Per compatibilitat amb backoffices anteriors a la versió 1.0.7
    	this.indetificador = identificador;
    }
	@XmlTransient
    public String getIdentificador() {
        return identificador != null ? identificador : indetificador;
    }
}
