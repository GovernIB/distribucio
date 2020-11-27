/**
 * 
 */
package es.caib.distribucio.war.command;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import es.caib.distribucio.core.api.dto.BackofficeTipusEnumDto;
import es.caib.distribucio.core.api.dto.ReglaDto;
import es.caib.distribucio.core.api.dto.ReglaTipusEnumDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.war.command.ReglaCommand.CreateUpdate;
import es.caib.distribucio.war.helper.ConversioTipusHelper;
import es.caib.distribucio.war.validation.Regla;

/**
 * Command per al manteniment de regles.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Regla(groups = {CreateUpdate.class})
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
	@Size(max = 64, groups = {CreateUpdate.class})
	private String procedimentCodiFiltre;
	private Long unitatFiltreId;
	private Long bustiaFiltreId;

	// ------------- ACCIO  ----------------------
	private ReglaTipusEnumDto tipus;
	private Long bustiaDestiId;
	private Long backofficeDestiId;
	private Long unitatDestiId;
	
	
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
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public ReglaTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(ReglaTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public Long getBustiaDestiId() {
		return bustiaDestiId;
	}
	public void setBustiaDestiId(Long bustiaDestiId) {
		this.bustiaDestiId = bustiaDestiId;
	}
	public String getAssumpteCodiFiltre() {
		return assumpteCodiFiltre;
	}
	public void setAssumpteCodiFiltre(String assumpteCodiFiltre) {
		this.assumpteCodiFiltre = assumpteCodiFiltre;
	}
	public String getProcedimentCodiFiltre() {
		return procedimentCodiFiltre;
	}
	public void setProcedimentCodiFiltre(String procedimentCodiFiltre) {
		this.procedimentCodiFiltre = procedimentCodiFiltre;
	}
	public Long getUnitatFiltreId() {
		return unitatFiltreId;
	}
	public void setUnitatFiltreId(Long unitatFiltreId) {
		this.unitatFiltreId = unitatFiltreId;
	}
	public Long getBustiaFiltreId() {
		return bustiaFiltreId;
	}
	public void setBustiaFiltreId(Long bustiaFiltreId) {
		this.bustiaFiltreId = bustiaFiltreId;
	}
	public Long getBackofficeDestiId() {
		return backofficeDestiId;
	}
	public void setBackofficeDestiId(Long backofficeDestiId) {
		this.backofficeDestiId = backofficeDestiId;
	}

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
	
	public Long getUnitatDestiId() {
		return unitatDestiId;
	}
	public void setUnitatDestiId(Long unitatDestiId) {
		this.unitatDestiId = unitatDestiId;
	}

	public interface CreateUpdate {}

}