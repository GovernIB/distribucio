/**
 * 
 */
package es.caib.distribucio.core.api.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Informació del filtre de continguts.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ReglaFiltreDto implements Serializable {

	private String unitatCodi;
	private String nom;
	private Long unitatId;
	private ReglaTipusEnumDto tipus;
	private Long backofficeId;
	private String codiSIA;
	private String codiAssumpte;
	private Long bustiaId;
//	private boolean activa = true;
	private ReglaFiltreActivaEnumDto activa;

	
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
	public String getUnitatCodi() {
		return unitatCodi;
	}
	public void setUnitatCodi(String unitatCodi) {
		this.unitatCodi = unitatCodi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCodiSIA() {
		return codiSIA;
	}
	public void setCodiSIA(String codiSIA) {
		this.codiSIA = codiSIA;
	}
	public Long getBackofficeId() {
		return backofficeId;
	}
	public void setBackofficeId(Long backofficeId) {
		this.backofficeId = backofficeId;
	}
	public String getCodiAssumpte() {
		return codiAssumpte;
	}
	public void setCodiAssumpte(String codiAssumpte) {
		this.codiAssumpte = codiAssumpte;
	}
	public Long getBustiaId() {
		return bustiaId;
	}
	public void setBustiaId(Long bustiaId) {
		this.bustiaId = bustiaId;
	}
	public ReglaFiltreActivaEnumDto getActiva() {
		return activa;
	}
	public void setActiva(ReglaFiltreActivaEnumDto activa) {
		this.activa = activa;
	}
	//	public boolean isActiva() {
//		return activa;
//	}
//	public void setActiva(boolean activa) {
//		this.activa = activa;
//	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
