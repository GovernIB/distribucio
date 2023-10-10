/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import org.apache.commons.lang.builder.ToStringBuilder;




/**
 * Simulat accion.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreSimulatAccionDto {

	private RegistreSimulatAccionEnumDto accion;
	private String param;
	private String reglaNom;
	private Boolean presencial;
	

	public RegistreSimulatAccionDto(
			RegistreSimulatAccionEnumDto accion,
			String param,
			String reglaNom) {
		this.accion = accion;
		this.param = param;
		this.reglaNom = reglaNom;
	}
	public RegistreSimulatAccionEnumDto getAccion() {
		return accion;
	}
	public void setAccion(RegistreSimulatAccionEnumDto accion) {
		this.accion = accion;
	}
	public String getReglaNom() {
		return reglaNom;
	}
	public void setReglaNom(String reglaNom) {
		this.reglaNom = reglaNom;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public Boolean getPresencial() {
		return presencial;
	}
	public void setPresencial(Boolean presencial) {
		this.presencial = presencial;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
