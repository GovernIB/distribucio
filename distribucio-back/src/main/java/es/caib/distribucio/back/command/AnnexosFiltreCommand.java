/**
 * 
 */
package es.caib.distribucio.back.command;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.intf.dto.AnnexosFiltreDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnnexEstat;

/**
 * Command per al filtre del localitzador d'annexos
 * dels usuaris administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class AnnexosFiltreCommand {

	private String numero;
	private AnnexEstat arxiuEstat;
	private ArxiuFirmaTipusEnumDto tipusFirma;
	private String titol;
	private String fitxerNom;
	private String fitxerTipusMime;
	private Date dataRecepcioInici;
	private Date dataRecepcioFi;
	private Integer numeroCopia;

    private Float midaMinima;
    private Float midaMaxima;

	public static AnnexosFiltreCommand asCommand(AnnexosFiltreDto dto) {
		AnnexosFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				AnnexosFiltreCommand.class);
		return command;
	}
	public static AnnexosFiltreDto asDto(AnnexosFiltreCommand command) {
		AnnexosFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				AnnexosFiltreDto.class);		
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
