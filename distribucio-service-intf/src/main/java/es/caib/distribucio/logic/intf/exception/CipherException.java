/**
 * 
 */
package es.caib.distribucio.logic.intf.exception;

/**
 * Excepció que es produeix al xifrar una cadena.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class CipherException extends RuntimeException {

	public CipherException(
			String message) {
		super(message);
	}

	public CipherException(
			String message,
			Throwable cause) {
		super(message, cause);
	}

}
