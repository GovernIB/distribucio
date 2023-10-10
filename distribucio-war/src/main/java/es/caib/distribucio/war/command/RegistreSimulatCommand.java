/**
 * 
 */
package es.caib.distribucio.war.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.logic.intf.dto.RegistreSimulatDto;
import es.caib.distribucio.logic.intf.dto.ReglaPresencialEnumDto;
import es.caib.distribucio.war.helper.ConversioTipusHelper;


/**
 * Command que simula anotacio de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

public class RegistreSimulatCommand {


	@NotNull
	private Long unitatId;
	@Size(max = 16)
	private String assumpteCodi;
	@Size(max = 64)
	private String procedimentCodi;
	private Long bustiaId;
	private ReglaPresencialEnumDto presencial;


	public Long getUnitatId() {
		return unitatId;
	}
	public void setUnitatId(Long unitatId) {
		this.unitatId = unitatId;
	}
	public String getAssumpteCodi() {
		return assumpteCodi;
	}
	public void setAssumpteCodi(String assumpteCodi) {
		this.assumpteCodi = assumpteCodi;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public Long getBustiaId() {
		return bustiaId;
	}
	public void setBustiaId(Long bustiaId) {
		this.bustiaId = bustiaId;
	}
	
	
	public ReglaPresencialEnumDto getPresencial() {
		return presencial;
	}
	public void setPresencial(ReglaPresencialEnumDto presencial) {
		this.presencial = presencial;
	}
	public static RegistreSimulatCommand asCommand(RegistreSimulatDto dto) {
		RegistreSimulatCommand command = ConversioTipusHelper.convertir(
				dto,
				RegistreSimulatCommand.class);
		return command;
	}
	public static RegistreSimulatDto asDto(RegistreSimulatCommand command) {
		RegistreSimulatDto dto = ConversioTipusHelper.convertir(
				command,
				RegistreSimulatDto.class);
		return dto;
	}
	
	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


}
