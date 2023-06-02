/**
 * 
 */
package es.caib.distribucio.rest.client.integracio.domini;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe que representa una anotació de registre entrada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class AnotacioRegistreEntrada extends AnotacioRegistreBase {

	private String destiCodi;
	private String destiDescripcio;

}
