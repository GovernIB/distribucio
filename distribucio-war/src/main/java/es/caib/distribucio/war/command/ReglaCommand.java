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
	private String assumpteCodi;
	@Size(max = 64, groups = {CreateUpdate.class})
	private String procedimentCodi;
	private Long unitatId;
	private Long bustiaFiltreId;

	// ------------- ACCIO  ----------------------
	private ReglaTipusEnumDto tipus;
	private Long bustiaId;
	private Long backofficeDestiId;
	private Long unitatDestiId;
	
	
	public Long getUnitatId() {
		return unitatId;
	}
	public void setUnitatId(Long unitatId) {
		this.unitatId = unitatId;
	}
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
	public String getAssumpteCodi() {
		return assumpteCodi;
	}
	public void setAssumpteCodi(String assumpteCodi) {
		this.assumpteCodi = assumpteCodi;
	}
	public Long getBustiaId() {
		return bustiaId;
	}
	public void setBustiaId(Long bustiaId) {
		this.bustiaId = bustiaId;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
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
		 
		if (dto.getUnitatOrganitzativa() != null)
			command.setUnitatId(
					dto.getUnitatOrganitzativa().getId());
		return command;
	}
	public static ReglaDto asDto(ReglaCommand command) {
		ReglaDto reglaDto = ConversioTipusHelper.convertir(
				command,
				ReglaDto.class);
		UnitatOrganitzativaDto unitatOrganitzativaDto = new UnitatOrganitzativaDto();
		unitatOrganitzativaDto.setId(command.getUnitatId());
		reglaDto.setUnitatOrganitzativa(unitatOrganitzativaDto);
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
