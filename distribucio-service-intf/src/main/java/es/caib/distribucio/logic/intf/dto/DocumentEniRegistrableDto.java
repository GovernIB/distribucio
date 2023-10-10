/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;



public class DocumentEniRegistrableDto implements Serializable {



	private static final long serialVersionUID = 1L;
	private String numero;
	private Date data;
	private String oficinaCodi;
	private String oficinaDescripcio;
	

	public String getNumero() {
		return numero;
	}


	public void setNumero(String numero) {
		this.numero = numero;
	}


	public Date getData() {
		return data;
	}


	public void setData(Date data) {
		this.data = data;
	}


	public String getOficinaCodi() {
		return oficinaCodi;
	}


	public void setOficinaCodi(String oficinaCodi) {
		this.oficinaCodi = oficinaCodi;
	}


	public String getOficinaDescripcio() {
		return oficinaDescripcio;
	}


	public void setOficinaDescripcio(String oficinaDescripcio) {
		this.oficinaDescripcio = oficinaDescripcio;
	}


	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}



}
