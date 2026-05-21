package es.caib.distribucio.back.command;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentFiltreDto;

@Getter
@Setter
public class ProcedimentFiltreCommand {
	
	private String codi;
	private String nom;
	private String codiSia;
	private ProcedimentEstatEnumDto estat;
	private Long unitatOrganitzativa;
	private EntitatDto entitat;
	
	public static ProcedimentFiltreCommand asCommand(ProcedimentFiltreDto dto) {
		ProcedimentFiltreCommand command = ConversioTipusHelper.convertir(
				dto, 
				ProcedimentFiltreCommand.class);
		return command;
	}
	
	public static ProcedimentFiltreDto asDto(ProcedimentFiltreCommand command) {
		ProcedimentFiltreDto dto = ConversioTipusHelper.convertir(
				command, 
				ProcedimentFiltreDto.class);
		return dto;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	

}
