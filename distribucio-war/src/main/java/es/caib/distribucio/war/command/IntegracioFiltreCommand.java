package es.caib.distribucio.war.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.core.api.dto.IntegracioAccioEstatEnumDto;
import es.caib.distribucio.core.api.dto.IntegracioFiltreDto;
import es.caib.distribucio.war.helper.ConversioTipusHelper;

/**
 * Command per al filtre de 
 * integració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class IntegracioFiltreCommand {
	
	private String codi;
	private Date data;
	private String descripcio;
	private String usuari;
	private IntegracioAccioEstatEnumDto estat;
	
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getUsuari() {
		return usuari;
	}
	public void setUsuari(String usuari) {
		this.usuari = usuari;
	}
	public IntegracioAccioEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(IntegracioAccioEstatEnumDto estat) {
		this.estat = estat;
	}
	
	
	public static IntegracioFiltreCommand asCommand(IntegracioFiltreDto dto) {
		IntegracioFiltreCommand command = ConversioTipusHelper.convertir(
				dto, 
				IntegracioFiltreCommand.class);
		return command;
	}
	
	public static IntegracioFiltreDto asDto(IntegracioFiltreCommand command) {
		IntegracioFiltreDto dto = ConversioTipusHelper.convertir(
				command, 
				IntegracioFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
