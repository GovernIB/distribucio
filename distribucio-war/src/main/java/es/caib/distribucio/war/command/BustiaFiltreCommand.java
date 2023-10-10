/**
 * 
 */
package es.caib.distribucio.war.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.logic.intf.dto.BustiaFiltreDto;
import es.caib.distribucio.war.helper.ConversioTipusHelper;

/**
 * Command per al filtre del localitzador de continguts
 * dels usuaris administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaFiltreCommand {

	private String unitatCodi;
	private String nom;
	private String codiUnitatSuperior;
	private Boolean unitatObsoleta;
	private Long unitatId;
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
	public Long getUnitatId() {
		return unitatId;
	}
	public void setUnitatId(Long unitatId) {
		this.unitatId = unitatId;
	}
	public Boolean getUnitatObsoleta() {
		return unitatObsoleta;
	}
	public void setUnitatObsoleta(Boolean unitatObsoleta) {
		this.unitatObsoleta = unitatObsoleta;
	}

	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCodiUnitatSuperior() {
		return codiUnitatSuperior;
	}
	public void setCodiUnitatSuperior(String codiUnitatSuperior) {
		this.codiUnitatSuperior = codiUnitatSuperior;
	}
	public String getUnitatCodi() {
		return unitatCodi;
	}
	public void setUnitatCodi(String unitatCodi) {
		this.unitatCodi = unitatCodi;
	}

	public static BustiaFiltreCommand asCommand(BustiaFiltreDto dto) {
		BustiaFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				BustiaFiltreCommand.class);
		return command;
	}
	public static BustiaFiltreDto asDto(BustiaFiltreCommand command) {
		BustiaFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				BustiaFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	

}
