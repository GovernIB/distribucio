/**
 * 
 */
package es.caib.distribucio.logic.intf.exception;

/**
 * Excepció que indica la falta de correu
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class EmptyMailException extends RuntimeException {

	public EmptyMailException(
			String message) {
		super(message);
	}

	public EmptyMailException(
			String message,
			Throwable cause) {
		super(message, cause);
	}

}
