/**
 * 
 */
package es.caib.distribucio.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaEstatEnumDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaFiltreDto;
import es.caib.distribucio.war.helper.ConversioTipusHelper;

/**
 * Command per al filtre del localitzador de continguts
 * dels usuaris administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatOrganitzativaFiltreCommand {

	private String codi;
	private String denominacio;
	
	private String codiUnitatSuperior;
	private UnitatOrganitzativaEstatEnumDto estat;
	
	

	public String getCodiUnitatSuperior() {
		return codiUnitatSuperior;
	}
	public void setCodiUnitatSuperior(String codiUnitatSuperior) {
		this.codiUnitatSuperior = codiUnitatSuperior;
	}

	public UnitatOrganitzativaEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(UnitatOrganitzativaEstatEnumDto estat) {
		this.estat = estat;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getDenominacio() {
		return denominacio;
	}
	public void setDenominacio(String denominacio) {
		this.denominacio = denominacio;
	}
	public static UnitatOrganitzativaFiltreCommand asCommand(UnitatOrganitzativaFiltreDto dto) {
		UnitatOrganitzativaFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				UnitatOrganitzativaFiltreCommand.class);
		return command;
	}
	public static UnitatOrganitzativaFiltreDto asDto(UnitatOrganitzativaFiltreCommand command) {
		UnitatOrganitzativaFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				UnitatOrganitzativaFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
