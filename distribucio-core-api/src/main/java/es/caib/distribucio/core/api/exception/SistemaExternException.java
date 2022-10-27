/**
 * 
 */
package es.caib.distribucio.core.api.exception;

/**
 * Excepció que es produeix al accedir a un sistema extern.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class SistemaExternException extends RuntimeException {

	private String sistemaExternCodi;

	public SistemaExternException(
			String sistemaExternCodi,
			String message) {
		super(message);
		this.sistemaExternCodi = sistemaExternCodi;
	}

	public SistemaExternException(
			String sistemaExternCodi,
			String message,
			Throwable cause) {
		super(message, cause);
		this.sistemaExternCodi = sistemaExternCodi;
	}

	public String getSistemaExternCodi() {
		return sistemaExternCodi;
	}
	
	public SistemaExternException(Throwable cause) {
		super(cause);
	}

	public SistemaExternException(String message) {
		super(message);
	}

	public SistemaExternException(String message, Throwable cause) {
		super(message, cause);
	}
	
	@Override
	public String getMessage() {
		return "Error amb el sistema extern " + this.getSistemaExternCodi() + ": " + super.getMessage() 
				 + "Torni a intentar-ho o reporteu l'error al vostre administrador. ";
	}

}
