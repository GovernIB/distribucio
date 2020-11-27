/**
 * 
 */
package es.caib.distribucio.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.core.api.dto.ReglaFiltreDto;
import es.caib.distribucio.core.api.dto.ReglaTipusEnumDto;
import es.caib.distribucio.war.helper.ConversioTipusHelper;

/**
 * Command per al filtre del localitzador de continguts
 * dels usuaris administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ReglaFiltreCommand {

	private String unitatCodi;
	private String nom;
	private Long unitatId;
	private ReglaTipusEnumDto tipus;
	private Long backofficeId;
	
	
	public ReglaTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(ReglaTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public Long getUnitatId() {
		return unitatId;
	}
	public void setUnitatId(Long unitatId) {
		this.unitatId = unitatId;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getUnitatCodi() {
		return unitatCodi;
	}
	public void setUnitatCodi(String unitatCodi) {
		this.unitatCodi = unitatCodi;
	}
	public Long getBackofficeId() {
		return backofficeId;
	}
	public void setBackofficeId(Long backofficeId) {
		this.backofficeId = backofficeId;
	}
	public static ReglaFiltreCommand asCommand(ReglaFiltreDto dto) {
		ReglaFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				ReglaFiltreCommand.class);
		return command;
	}
	public static ReglaFiltreDto asDto(ReglaFiltreCommand command) {
		ReglaFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				ReglaFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	

}