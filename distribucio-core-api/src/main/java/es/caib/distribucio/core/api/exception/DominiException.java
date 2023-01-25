/**
 * 
 */
package es.caib.distribucio.core.api.exception;

/**
 * Excepció que es produeix gestionant un domini.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class DominiException extends RuntimeException {

	public DominiException(
			String message) {
		super(message);
	}

	public DominiException(
			String message,
			Throwable cause) {
		super(message, cause);
	}

}
