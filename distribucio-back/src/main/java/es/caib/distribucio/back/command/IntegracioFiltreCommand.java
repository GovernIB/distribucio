package es.caib.distribucio.back.command;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.IntegracioFiltreDto;

/**
 * Command per al filtre de 
 * integració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class IntegracioFiltreCommand {
	
	private String codi;
	private Date data;
	private String descripcio;
	private String usuari;
	private IntegracioAccioEstatEnumDto estat;
    private String entitat;

	public static IntegracioFiltreCommand asCommand(IntegracioFiltreDto dto) {
		IntegracioFiltreCommand command = ConversioTipusHelper.convertir(
				dto, 
				IntegracioFiltreCommand.class);
		return command;
	}
	
	public static IntegracioFiltreDto asDto(IntegracioFiltreCommand command) {
		IntegracioFiltreDto dto = ConversioTipusHelper.convertir(
				command, 
				IntegracioFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
