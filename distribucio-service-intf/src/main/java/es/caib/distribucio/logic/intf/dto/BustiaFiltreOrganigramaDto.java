package es.caib.distribucio.logic.intf.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Informació del filtre de continguts.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaFiltreOrganigramaDto implements Serializable {

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
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
