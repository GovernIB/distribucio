package es.caib.distribucio.back.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.ServeiEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.ServeiFiltreDto;

public class ServeiFiltreCommand {
	
	private String codi;
	private String nom;
	private String codiSia;
	private ServeiEstatEnumDto estat;
	private String unitatOrganitzativa;
	private EntitatDto entitat;
	
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCodiSia() {
		return codiSia;
	}
	public void setCodiSia(String codiSia) {
		this.codiSia = codiSia;
	}
	public ServeiEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(ServeiEstatEnumDto estat) {
		this.estat = estat;
	}
	public String getUnitatOrganitzativa() {
		return unitatOrganitzativa;
	}
	public void setUnitatOrganitzativa(String unitatOrganitzativa) {
		this.unitatOrganitzativa = unitatOrganitzativa;
	}
	public EntitatDto getEntitat() {
		return entitat;
	}
	public void setEntitat(EntitatDto entitat) {
		this.entitat = entitat;	
	}
	
	public static ServeiFiltreCommand asCommand(ServeiFiltreDto dto) {
		ServeiFiltreCommand command = ConversioTipusHelper.convertir(
				dto, 
				ServeiFiltreCommand.class);
		return command;
	}
	
	public static ServeiFiltreDto asDto(ServeiFiltreCommand command) {
		ServeiFiltreDto dto = ConversioTipusHelper.convertir(
				command, 
				ServeiFiltreDto.class);
		return dto;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	

}
