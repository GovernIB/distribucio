package es.caib.distribucio.back.command;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.ServeiEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.ServeiFiltreDto;

@Getter
@Setter
public class ServeiFiltreCommand {
	
	private String codi;
	private String nom;
	private String codiSia;
	private ServeiEstatEnumDto estat;
	private Long unitatOrganitzativa;
	private EntitatDto entitat;
	
	public static ServeiFiltreCommand asCommand(ServeiFiltreDto dto) {
		ServeiFiltreCommand command = ConversioTipusHelper.convertir(
				dto, 
				ServeiFiltreCommand.class);
		return command;
	}
	
	public static ServeiFiltreDto asDto(ServeiFiltreCommand command) {
		ServeiFiltreDto dto = ConversioTipusHelper.convertir(
				command, 
				ServeiFiltreDto.class);
		return dto;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	

}
