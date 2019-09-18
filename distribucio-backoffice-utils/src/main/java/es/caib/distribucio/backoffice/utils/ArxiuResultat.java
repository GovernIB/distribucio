package es.caib.distribucio.backoffice.utils;

public class ArxiuResultat {

	int errorCodi;
	String errorMessage;
	Exception exception;
	String identificadorExpedient;
	
	public int getErrorCodi() {
		return errorCodi;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public Exception getException() {
		return exception;
	}
	public String getIdentificadorExpedient() {
		return identificadorExpedient;
	}
	public void setErrorCodi(int errorCodi) {
		this.errorCodi = errorCodi;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	public void setIdentificadorExpedient(String identificadorExpedient) {
		this.identificadorExpedient = identificadorExpedient;
	}
	
	
	
	
	
}
