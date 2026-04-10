/**
 * 
 */
package es.caib.distribucio.back.command;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.intf.dto.BustiaFiltreDto;

/**
 * Command per al filtre del localitzador de continguts
 * dels usuaris administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class BustiaFiltreCommand {

	private String unitatCodi;
	private String nom;
	private String codiUnitatSuperior;
	private Boolean unitatObsoleta;
	private Long unitatId;
	private Boolean perDefecte;
	private Boolean activa;
    private Boolean permis;

	public static BustiaFiltreCommand asCommand(BustiaFiltreDto dto) {
		BustiaFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				BustiaFiltreCommand.class);
		return command;
	}
	public static BustiaFiltreDto asDto(BustiaFiltreCommand command) {
		BustiaFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				BustiaFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	

}
