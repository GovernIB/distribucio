/**
 * 
 */
package es.caib.distribucio.war.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.core.api.dto.BustiaContingutFiltreEstatEnumDto;
import es.caib.distribucio.core.api.dto.BustiaUserFiltreDto;
import es.caib.distribucio.war.helper.ConversioTipusHelper;

/**
 * Command per al filtre del localitzador de continguts
 * dels usuaris administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaUserFiltreCommand {

	private String bustia;
	private String contingutDescripcio;
	private String remitent;
	private Date dataRecepcioInici;
	private Date dataRecepcioFi;
	private BustiaContingutFiltreEstatEnumDto estatContingut;
	
	public static BustiaUserFiltreCommand asCommand(BustiaUserFiltreDto dto) {
		BustiaUserFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				BustiaUserFiltreCommand.class);
		return command;
	}
	public static BustiaUserFiltreDto asDto(BustiaUserFiltreCommand command) {
		BustiaUserFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				BustiaUserFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public String getBustia() {
		return bustia;
	}
	public void setBustia(String bustia) {
		this.bustia = bustia;
	}
	public String getContingutDescripcio() {
		return contingutDescripcio;
	}
	public void setContingutDescripcio(String contingutDescripcio) {
		this.contingutDescripcio = contingutDescripcio;
	}
	public String getRemitent() {
		return remitent;
	}
	public void setRemitent(String remitent) {
		this.remitent = remitent;
	}
	public Date getDataRecepcioInici() {
		return dataRecepcioInici;
	}
	public void setDataRecepcioInici(Date dataRecepcioInici) {
		this.dataRecepcioInici = dataRecepcioInici;
	}
	public Date getDataRecepcioFi() {
		return dataRecepcioFi;
	}
	public void setDataRecepcioFi(Date dataRecepcioFi) {
		this.dataRecepcioFi = dataRecepcioFi;
	}
	public BustiaContingutFiltreEstatEnumDto getEstatContingut() {
		return estatContingut;
	}
	public void setEstatContingut(BustiaContingutFiltreEstatEnumDto estatContingut) {
		this.estatContingut = estatContingut;
	}
}
