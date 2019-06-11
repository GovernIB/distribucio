/**
 * 
 */
package es.caib.distribucio.war.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command per moure les anotacions de registre d'una bústia a una altra en les opcions de l'administrador.
 * 
 */
public class MoureAnotacionsCommand {

	@NotNull
	protected Long origenId;
	@NotNull
	protected Long destiId;
	@Size(max=256)
	protected String comentari;

	public Long getOrigenId() {
		return origenId;
	}
	public void setOrigenId(Long origenId) {
		this.origenId = origenId;
	}
	public String getComentari() {
		return comentari;
	}
	public void setComentari(String comentari) {
		this.comentari = comentari;
	}
	public Long getDestiId() {
		return destiId;
	}
	public void setDestiId(Long destiId) {
		this.destiId = destiId;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
