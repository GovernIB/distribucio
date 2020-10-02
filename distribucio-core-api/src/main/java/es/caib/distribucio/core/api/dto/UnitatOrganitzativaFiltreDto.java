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
public class UnitatOrganitzativaFiltreDto implements Serializable {

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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
