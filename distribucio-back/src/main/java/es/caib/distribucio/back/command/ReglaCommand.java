/**
 * 
 */
package es.caib.distribucio.back.command;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import es.caib.distribucio.logic.intf.dto.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.back.command.ReglaCommand.CreateUpdate;
import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.back.validation.Regla;

import java.util.Date;

/**
 * Command per al manteniment de regles.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Regla(groups = {CreateUpdate.class})
@Getter
@Setter
public class ReglaCommand {

	private Long id;
	
	@NotEmpty(groups = {CreateUpdate.class})
	@Size(max = 256, groups = {CreateUpdate.class})
	private String nom;
	@Size(max = 1024, groups = {CreateUpdate.class})
	private String descripcio;
	
	// ------------- FILRE ----------------------
	@Size(max = 16, groups = {CreateUpdate.class})
	private String assumpteCodiFiltre;
	@Size(max = 1024, groups = {CreateUpdate.class})
	private String procedimentCodiFiltre;
	private String serveiCodiFiltre;
	private Long unitatFiltreId;
	private Long bustiaFiltreId;
	private ReglaPresencialEnumDto presencial;

	// ------------- ACCIO  ----------------------
	private ReglaTipusEnumDto tipus;
	private Long bustiaDestiId;
	private Long backofficeDestiId;
	private Long unitatDestiId;
	private boolean aturarAvaluacio;

    private UsuariDto createdBy;
    private Date createdDate;

	public static ReglaCommand asCommand(ReglaDto dto) {
		ReglaCommand command = ConversioTipusHelper.convertir(
				dto,
				ReglaCommand.class);
		 
		if (dto.getUnitatOrganitzativaFiltre() != null)
			command.setUnitatFiltreId(
					dto.getUnitatOrganitzativaFiltre().getId());
		return command;
	}
	public static ReglaDto asDto(ReglaCommand command) {
		ReglaDto reglaDto = ConversioTipusHelper.convertir(
				command,
				ReglaDto.class);
		
		if (command.getUnitatFiltreId() != null) {
			UnitatOrganitzativaDto unitatOrganitzativaDto = new UnitatOrganitzativaDto();
			unitatOrganitzativaDto.setId(command.getUnitatFiltreId());
			reglaDto.setUnitatOrganitzativaFiltre(unitatOrganitzativaDto);
		}

		return reglaDto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public interface CreateUpdate {}

}
