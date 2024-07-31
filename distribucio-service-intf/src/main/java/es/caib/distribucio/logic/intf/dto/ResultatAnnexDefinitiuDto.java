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

	private Long anotacioNumero;
	private Long annexId;
	private boolean ok;
	private String keyMessage;	

	public Long getAnotacioNumero() {
		return anotacioNumero;
	}
	public void setAnotacioNumero(Long anotacioNumero) {
		this.anotacioNumero = anotacioNumero;
	}
	public Long getAnnexId() {
		return annexId;
	}
	public void setAnnexId(Long annexId) {
		this.annexId = annexId;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
