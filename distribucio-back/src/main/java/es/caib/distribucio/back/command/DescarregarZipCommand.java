/**
 * 
 */
package es.caib.distribucio.back.command;

import es.caib.distribucio.logic.intf.registre.FileNameOption;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DescarregarZipCommand {

    private boolean estructuraCarpetes;
    private boolean versioImprimible;
    @NotNull
    private FileNameOption nomDocument;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
