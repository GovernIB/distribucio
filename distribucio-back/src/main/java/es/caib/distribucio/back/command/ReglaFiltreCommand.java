/**
 * 
 */
package es.caib.distribucio.back.command;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.intf.dto.ReglaFiltreActivaEnumDto;
import es.caib.distribucio.logic.intf.dto.ReglaFiltreDto;
import es.caib.distribucio.logic.intf.dto.ReglaPresencialEnumDto;
import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;

/**
 * Command per al filtre del localitzador de continguts
 * dels usuaris administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ReglaFiltreCommand {

	private String unitatCodi;
	private String nom;
	private Long unitatId;
	private ReglaTipusEnumDto tipus;
	private String codiSIA;
	private String codiServei;
	private Long backofficeId;
	private String codiAssumpte;
	private Long bustiaId;
//	private boolean activa = true;
	private ReglaFiltreActivaEnumDto activa;
	private ReglaPresencialEnumDto presencial;

    private Long unitatDestiId;
    private Long bustiaDestiId;

	public static ReglaFiltreCommand asCommand(ReglaFiltreDto dto) {
		ReglaFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				ReglaFiltreCommand.class);
		return command;
	}
	public static ReglaFiltreDto asDto(ReglaFiltreCommand command) {
		ReglaFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				ReglaFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	

}
