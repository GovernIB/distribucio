/**
 * 
 */
package es.caib.distribucio.core.api.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Informació del filtre de continguts.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaUserFiltreDto implements Serializable {

	private String bustia;
	private String contingutDescripcio;
	private String remitent;
	private Date dataRecepcioInici;
	private Date dataRecepcioFi;
	private BustiaContingutFiltreEstatEnumDto estatContingut;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

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
