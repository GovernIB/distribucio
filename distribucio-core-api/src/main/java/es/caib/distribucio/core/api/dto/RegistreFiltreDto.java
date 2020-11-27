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
public class RegistreFiltreDto implements Serializable {

	private String bustia;
	/** Per mostrar el contingut de les bústies innactives */
	private boolean mostrarInnactives;
	private String numero;
	private String titol;
	private String remitent;
	private Date dataRecepcioInici;
	private Date dataRecepcioFi;
	private RegistreProcesEstatSimpleEnumDto procesEstatSimple;
	private String numeroOrigen;
	private String interessat;
	private RegistreTipusDocFisicaEnumDto tipusDocFisica;
	private RegistreEnviatPerEmailEnumDto registreEnviatPerEmailEnum;


	public RegistreEnviatPerEmailEnumDto getRegistreEnviatPerEmailEnum() {
		return registreEnviatPerEmailEnum;
	}
	public void setRegistreEnviatPerEmailEnum(RegistreEnviatPerEmailEnumDto registreEnviatPerEmailEnum) {
		this.registreEnviatPerEmailEnum = registreEnviatPerEmailEnum;
	}
	public RegistreTipusDocFisicaEnumDto getTipusDocFisica() {
		return tipusDocFisica;
	}
	public void setTipusDocFisica(RegistreTipusDocFisicaEnumDto tipusDocFisica) {
		this.tipusDocFisica = tipusDocFisica;
	}
	public String getInteressat() {
		return interessat;
	}
	public void setInteressat(String interessat) {
		this.interessat = interessat;
	}
	public String getNumeroOrigen() {
		return numeroOrigen;
	}
	public void setNumeroOrigen(String numeroOrigen) {
		this.numeroOrigen = numeroOrigen;
	}
	public String getBustia() {
		return bustia;
	}
	public void setBustia(String bustia) {
		this.bustia = bustia;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
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
	public RegistreProcesEstatSimpleEnumDto getProcesEstatSimple() {
		return procesEstatSimple;
	}
	public void setProcesEstatSimple(RegistreProcesEstatSimpleEnumDto procesEstatSimple) {
		this.procesEstatSimple = procesEstatSimple;
	}
	public boolean isMostrarInactives() {
		return mostrarInnactives;
	}
	public void setMostrarInactives(boolean mostrarInactives) {
		this.mostrarInnactives = mostrarInactives;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	private static final long serialVersionUID = -139254994389509932L;
}