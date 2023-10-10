/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Informació del filtre de continguts.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaFiltreDto implements Serializable {

	private String unitatCodi;
	private String nom;
	private String codiUnitatSuperior;
	// if the obsolete is true we look for the busties of extinguished or anulated unitats  
	private Boolean unitatObsoleta;
	private Long unitatId;
	private String numeroOrigen;
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
	public String getNumeroOrigen() {
		return numeroOrigen;
	}
	public void setNumeroOrigen(String numeroOrigen) {
		this.numeroOrigen = numeroOrigen;
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

	public String getCodiUnitatSuperior() {
		return codiUnitatSuperior;
	}
	public void setCodiUnitatSuperior(String codiUnitatSuperior) {
		this.codiUnitatSuperior = codiUnitatSuperior;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
