package es.caib.distribucio.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.logic.intf.dto.BustiaFiltreOrganigramaDto;
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
	private String codiUnitatSuperior;
	private Long unitatIdFiltre;
	private Boolean unitatObsoleta;
	private Boolean perDefecte;
	private Boolean activa;
	
	public Boolean getPerDefecte() {
		return perDefecte;
	}
	public void setPerDefecte(Boolean perDefecte) {
		this.perDefecte = perDefecte;
	}
	public Boolean getActiva() {
		return activa;
	}
	public void setActiva(Boolean activa) {
		this.activa = activa;
	}
	public Boolean getUnitatObsoleta() {
		return unitatObsoleta;
	}
	public void setUnitatObsoleta(Boolean unitatObsoleta) {
		this.unitatObsoleta = unitatObsoleta;
	}
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
	
	public String getCodiUnitatSuperior() {
		return codiUnitatSuperior;
	}
	public void setCodiUnitatSuperior(String codiUnitatSuperior) {
		this.codiUnitatSuperior = codiUnitatSuperior;
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
