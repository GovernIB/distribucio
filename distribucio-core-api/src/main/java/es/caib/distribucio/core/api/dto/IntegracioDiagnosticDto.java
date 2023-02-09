package es.caib.distribucio.core.api.dto;

public class IntegracioDiagnosticDto {
	
	private boolean correcte;
	private String errMsg;
	private String prova;
	
		
	public String getProva() {
		return prova;
	}
	public void setProva(String prova) {
		this.prova = prova;
	}
	public IntegracioDiagnosticDto() {
	}
	public boolean isCorrecte() {
		return correcte;
	}
	public void setCorrecte(boolean correcte) {
		this.correcte = correcte;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	
	

}
