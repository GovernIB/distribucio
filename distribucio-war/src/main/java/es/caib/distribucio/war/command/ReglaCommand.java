/**
 * 
 */
package es.caib.distribucio.war.command;

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
	private ReglaTipusEnumDto tipus;
	@Size(max = 16, groups = {CreateUpdate.class})
	private String assumpteCodi;
	@Size(max = 64, groups = {CreateUpdate.class})
	private String procedimentCodi;
	@Size(max = 9, groups = {CreateUpdate.class})
	private String unitatCodi;
	private Long unitatId;
	private Long bustiaId;
	private BackofficeTipusEnumDto backofficeTipus;
	@Size(max = 256, groups = {CreateUpdate.class})
	private String backofficeUrl;
	@Size(max = 64, groups = {CreateUpdate.class})
	private String backofficeUsuari;
	@Size(max = 64, groups = {CreateUpdate.class})
	private String backofficeContrasenya;
	private Integer backofficeIntents;
	private Integer backofficeTempsEntreIntents;



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
	public String getUnitatCodi() {
		return unitatCodi;
	}
	public void setUnitatCodi(String unitatCodi) {
		this.unitatCodi = unitatCodi;
	}
	public Long getBustiaId() {
		return bustiaId;
	}
	public void setBustiaId(Long bustiaId) {
		this.bustiaId = bustiaId;
	}
	public BackofficeTipusEnumDto getBackofficeTipus() {
		return backofficeTipus;
	}
	public void setBackofficeTipus(BackofficeTipusEnumDto backofficeTipus) {
		this.backofficeTipus = backofficeTipus;
	}
	public String getBackofficeUrl() {
		return backofficeUrl;
	}
	public void setBackofficeUrl(String backofficeUrl) {
		this.backofficeUrl = backofficeUrl;
	}
	public String getBackofficeUsuari() {
		return backofficeUsuari;
	}
	public void setBackofficeUsuari(String backofficeUsuari) {
		this.backofficeUsuari = backofficeUsuari;
	}
	public String getBackofficeContrasenya() {
		return backofficeContrasenya;
	}
	public void setBackofficeContrasenya(String backofficeContrasenya) {
		this.backofficeContrasenya = backofficeContrasenya;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public Integer getBackofficeIntents() {
		return backofficeIntents;
	}
	public void setBackofficeIntents(Integer backofficeIntents) {
		this.backofficeIntents = backofficeIntents;
	}

	public Integer getBackofficeTempsEntreIntents() {
		return backofficeTempsEntreIntents;
	}
	public void setBackofficeTempsEntreIntents(Integer backofficeTempsEntreIntents) {
		this.backofficeTempsEntreIntents = backofficeTempsEntreIntents;
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
	
	public interface CreateUpdate {}

}
