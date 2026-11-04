/**
 * 
 */
package es.caib.distribucio.logic.intf.registre;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe que representa una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RegistreAnotacio extends RegistreAnotacioBase {

	private String procedimentCodi;//19 (Long to String)
	private String serveiCodi;//19 (Long to String)
	
}
