/**
 * 
 */
package es.caib.distribucio.back.command;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command per a copiar, moure o enviar contenidors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ContingutReenviarCommand {

	@Size(max=3940)
	protected String comentariEnviar;
    @NotNull
	@NotEmpty
	protected Long[] destins;
	@NotNull
	protected boolean deixarCopia;
	protected String[] params;
	
	protected Long[] perConeixement;
	
	protected Map<Long, String> destinsUsuari = new HashMap<Long, String>();

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
