/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import java.util.Date;

/**
 * Informació d'un error
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ErrorDto {

	private Date data;
	private String titol;
	private String tipus;
	private String descripcio;
	private String stacktrace;
	
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getStacktrace() {
		return stacktrace;
	}
	public void setStacktrace(String stacktrace) {
		this.stacktrace = stacktrace;
	}	
}
