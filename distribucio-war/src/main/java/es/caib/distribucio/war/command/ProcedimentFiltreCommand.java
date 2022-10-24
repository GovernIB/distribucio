package es.caib.distribucio.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.ProcedimentFiltreDto;
import es.caib.distribucio.war.helper.ConversioTipusHelper;

public class ProcedimentFiltreCommand {
	
	private String codi;
	private String nom;
	private String codiSia;
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
	
	public static ProcedimentFiltreCommand asCommand(ProcedimentFiltreDto dto) {
		ProcedimentFiltreCommand command = ConversioTipusHelper.convertir(
				dto, 
				ProcedimentFiltreCommand.class);
		return command;
	}
	
	public static ProcedimentFiltreDto asDto(ProcedimentFiltreCommand command) {
		ProcedimentFiltreDto dto = ConversioTipusHelper.convertir(
				command, 
				ProcedimentFiltreDto.class);
		return dto;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	

}
