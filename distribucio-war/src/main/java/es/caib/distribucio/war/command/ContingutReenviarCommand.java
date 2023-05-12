/**
 * 
 */
package es.caib.distribucio.war.command;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Command per a copiar, moure o enviar contenidors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ContingutReenviarCommand {

	@Size(max=3940)
	protected String comentariEnviar;
	@NotEmpty
	protected Long[] destins;
	@NotNull
	protected boolean deixarCopia;
	protected String[] params;
	
	protected Long[] perConeixement;
	
	protected Map<Long, String> destinsUsuari = new HashMap<Long, String>();
	
	public String getComentariEnviar() {
		return comentariEnviar;
	}
	public void setComentariEnviar(String comentariEnviar) {
		this.comentariEnviar = comentariEnviar;
	}

	public Long[] getDestins() {
		return destins;
	}
	public void setDestins(Long[] destins) {
		this.destins = destins;
	}
	public boolean isDeixarCopia() {
		return deixarCopia;
	}
	public void setDeixarCopia(boolean deixarCopia) {
		this.deixarCopia = deixarCopia;
	}
	public String[] getParams() {
		return params;
	}
	public void setParams(String[] params) {
		this.params = params;
	}
	public Long[] getPerConeixement() {
		return perConeixement;
	}
	public void setPerConeixement(Long[] perConeixement) {
		this.perConeixement = perConeixement;
	}
	public Map<Long, String> getDestinsUsuari() {
		return destinsUsuari;
	}
	public void setDestinsUsuari(Map<Long, String> destiUsuari) {
		this.destinsUsuari = destiUsuari;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
