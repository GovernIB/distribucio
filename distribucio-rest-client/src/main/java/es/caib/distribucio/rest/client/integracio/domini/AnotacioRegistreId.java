/**
 * 
 */
package es.caib.distribucio.rest.client.integracio.domini;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Classe que representa id del anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnotacioRegistreId {

	private String identificador;
	private String clauAcces;

    public void setIndetificador(String indetificador) {
        this.identificador = indetificador;
    }
}
