/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació d'un resultat posar definitiu Annex
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ResultatAnnexDefinitiuDto extends AuditoriaDto {

	private String anotacioNumero;
	private Long annexId;
	private String annexTitol;
	private boolean ok;
	private String keyMessage;
	private Throwable throwable;

	public String getAnotacioNumero() {
		return anotacioNumero;
	}
	public void setAnotacioNumero(String anotacioNumero) {
		this.anotacioNumero = anotacioNumero;
	}
	public Long getAnnexId() {
		return annexId;
	}
	public void setAnnexId(Long annexId) {
		this.annexId = annexId;
	}
	public String getAnnexTitol() {
		return annexTitol;
	}
	public void setAnnexTitol(String annexTitol) {
		this.annexTitol = annexTitol;
	}
	public boolean isOk() {
		return ok;
	}
	public void setOk(boolean ok) {
		this.ok = ok;
	}
	public String getKeyMessage() {
		return keyMessage;
	}
	public void setKeyMessage(String keyMessage) {
		this.keyMessage = keyMessage;
	}

	public Throwable getThrowable() {
		return throwable;
	}
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
