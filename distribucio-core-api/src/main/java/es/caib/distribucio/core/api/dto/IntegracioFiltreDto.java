package es.caib.distribucio.core.api.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació del filtre del 
 * monitor d'integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class IntegracioFiltreDto implements Serializable {
	
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
	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
	private static final long serialVersionUID = -248365773192710830L;

}
