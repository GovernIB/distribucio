/**
 * 
 */
package es.caib.distribucio.plugin.servei;

import com.fasterxml.jackson.annotation.JsonProperty;

import es.caib.distribucio.plugin.Link;

/**
 * Informació d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Servei {

	private String codigo;
	private String codigoSIA;
	private String codigoSia;
	private String nombre;
	@JsonProperty("link_unidadAdministrativa")
	private Link unidadAdministrativa;
	@JsonProperty("link_organoInstructor")
	private Link organoInstructor;

	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getCodigoSIA() {
		return codigoSIA;
	}
	public void setCodigoSIA(String codigoSIA) {
		this.codigoSIA = codigoSIA;
	}
	public String getCodigoSia() {
		return codigoSia;
	}
	public void setCodigoSia(String codigoSia) {
		this.codigoSia = codigoSia;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Link getUnidadAdministrativa() {
		return unidadAdministrativa;
	}
	public void setUnidadAdministrativa(Link unidadAdministrativa) {
		this.unidadAdministrativa = unidadAdministrativa;
	}
	public Link getOrganoInstructor() {
		return organoInstructor;
	}
	public void setOrganoInstructor(Link organoInstructor) {
		this.organoInstructor = organoInstructor;
	}
}
