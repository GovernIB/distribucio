package es.caib.distribucio.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.core.api.dto.BustiaFiltreOrganigramaDto;
import es.caib.distribucio.war.helper.ConversioTipusHelper;

/**
 * Command per al filtre del localitzador de continguts
 * dels usuaris administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaFiltreOrganigramaCommand {

	private String unitatCodiFiltre;
	private String nomFiltre;
	private Long unitatIdFiltre;
	
	
	public Long getUnitatIdFiltre() {
		return unitatIdFiltre;
	}
	public void setUnitatIdFiltre(Long unitatIdFiltre) {
		this.unitatIdFiltre = unitatIdFiltre;
	}
	public String getUnitatCodiFiltre() {
		return unitatCodiFiltre;
	}
	public void setUnitatCodiFiltre(String unitatCodiFiltre) {
		this.unitatCodiFiltre = unitatCodiFiltre;
	}
	public String getNomFiltre() {
		return nomFiltre;
	}
	public void setNomFiltre(String nomFiltre) {
		this.nomFiltre = nomFiltre;
	}
	
	public static BustiaFiltreOrganigramaCommand asCommand(BustiaFiltreOrganigramaDto dto) {
		BustiaFiltreOrganigramaCommand command = ConversioTipusHelper.convertir(
				dto,
				BustiaFiltreOrganigramaCommand.class);
		return command;
	}
	public static BustiaFiltreOrganigramaDto asDto(BustiaFiltreOrganigramaCommand command) {
		BustiaFiltreOrganigramaDto dto = ConversioTipusHelper.convertir(
				command,
				BustiaFiltreOrganigramaDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	

}
