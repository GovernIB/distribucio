package es.caib.distribucio.logic.intf.dto;

public class IntegracioDiagnosticDto {
	
	private boolean correcte;
	private String errMsg;
	private String prova;
	private String excepcioStacktrace;
	
		
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
	public String getExcepcioStacktrace() {
		return excepcioStacktrace;
	}
	public void setExcepcioStacktrace(String excepcioStacktrace) {
		this.excepcioStacktrace = excepcioStacktrace;
	}
	
	

}
