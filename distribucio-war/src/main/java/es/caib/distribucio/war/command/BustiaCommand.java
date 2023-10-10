/**
 * 
 */
package es.caib.distribucio.war.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.distribucio.logic.intf.dto.BustiaDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.war.command.BustiaCommand.CreateUpdate;
import es.caib.distribucio.war.helper.ConversioTipusHelper;
import es.caib.distribucio.war.validation.Bustia;

/**
 * Command per al manteniment de bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Bustia(groups = {CreateUpdate.class})
public class BustiaCommand {

	private Long id;
	@NotEmpty(groups =  {CreateUpdate.class})
	@Size(max=256, groups =  {CreateUpdate.class})
	private String nom;
	private String unitatCodi;
	@NotNull(groups =  {CreateUpdate.class}) 
	private Long unitatId;
	private Long pareId;
	private Long entitatId;


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getUnitatCodi() {
		return unitatCodi;
	}
	public void setUnitatCodi(String unitatCodi) {
		this.unitatCodi = unitatCodi;
	}
	public Long getPareId() {
		return pareId;
	}
	public void setPareId(Long pareId) {
		this.pareId = pareId;
	}
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}

	public Long getUnitatId() {
		return unitatId;
	}
	public void setUnitatId(Long unitatId) {
		this.unitatId = unitatId;
	}
	
	public static BustiaCommand asCommand(BustiaDto dto) {
		BustiaCommand command = ConversioTipusHelper.convertir(
				dto, 
				BustiaCommand.class);
		if (dto.getUnitatOrganitzativa() != null)
			command.setUnitatId(
					dto.getUnitatOrganitzativa().getId());
		return command;
	}
	
	public static BustiaDto asDto(BustiaCommand command) {
		BustiaDto bustiaDto = ConversioTipusHelper.convertir(
				command,
				BustiaDto.class);
		UnitatOrganitzativaDto unitatOrganitzativaDto = new UnitatOrganitzativaDto();
		unitatOrganitzativaDto.setId(command.getUnitatId());
		bustiaDto.setUnitatOrganitzativa(unitatOrganitzativaDto);
		return bustiaDto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public interface CreateUpdate{}	
}
