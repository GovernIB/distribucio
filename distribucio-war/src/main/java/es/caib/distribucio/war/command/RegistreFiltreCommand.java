/**
 * 
 */
package es.caib.distribucio.war.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.core.api.dto.RegistreEnviatPerEmailEnumDto;
import es.caib.distribucio.core.api.dto.RegistreFiltreDto;
import es.caib.distribucio.core.api.dto.RegistreProcesEstatSimpleEnumDto;
import es.caib.distribucio.core.api.dto.RegistreTipusDocFisicaEnumDto;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.war.helper.ConversioTipusHelper;

/**
 * Command per al filtre del localitzador de continguts
 * dels usuaris administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreFiltreCommand {

	private String bustia;
	/** Per mostrar el contingut de les bústies innactives */
	private boolean mostrarInactives;
	private String numero;
	private String titol;
	private String remitent;
	private Date dataRecepcioInici;
	private Date dataRecepcioFi;
	private RegistreProcesEstatSimpleEnumDto procesEstatSimple;
	private String numeroOrigen;
	private String interessat;
	private RegistreTipusDocFisicaEnumDto tipusDocFisica;
	
	private RegistreEnviatPerEmailEnumDto enviatPerEmail;
	private String backCodi;

	// Filtre per administradors
	/** Estat específic. */
	private RegistreProcesEstatEnum estat;
	/** Per filtrar només les que tinguin error informat. */
	private boolean nomesAmbErrors;
	/** Unitat organitzativa superior. */
	private Long unitatId;
	
	private String bustiaOrigen;
	
	/** Per mostrar el contingut de les bústies origen innactives */
	public boolean mostrarInactivesOrigen;

	public String getBackCodi() {
		return backCodi;
	}
	public void setBackCodi(String backCodi) {
		this.backCodi = backCodi;
	}
	public RegistreEnviatPerEmailEnumDto getEnviatPerEmail() {
		return this.enviatPerEmail;
	}
	public void setEnviatPerEmail(RegistreEnviatPerEmailEnumDto enviatPerEmail) {
		this.enviatPerEmail = enviatPerEmail;
	}
	public String getInteressat() {
		return interessat;
	}
	public void setInteressat(String interessat) {
		this.interessat = interessat;
	}
	
	
	public static RegistreFiltreCommand asCommand(RegistreFiltreDto dto) {
		RegistreFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				RegistreFiltreCommand.class);
		return command;
	}
	public static RegistreFiltreDto asDto(RegistreFiltreCommand command) {
		RegistreFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				RegistreFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public RegistreTipusDocFisicaEnumDto getTipusDocFisica() {
		return tipusDocFisica;
	}
	public void setTipusDocFisica(RegistreTipusDocFisicaEnumDto tipusDocFisica) {
		this.tipusDocFisica = tipusDocFisica;
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
		return mostrarInactives;
	}
	public void setMostrarInactives(boolean mostrarInactives) {
		this.mostrarInactives = mostrarInactives;
	}
	public String getNumeroOrigen() {
		return numeroOrigen;
	}
	public void setNumeroOrigen(String numeroOrigen) {
		this.numeroOrigen = numeroOrigen;
	}
	public RegistreProcesEstatEnum getEstat() {
		return estat;
	}
	public void setEstat(RegistreProcesEstatEnum estat) {
		this.estat = estat;
	}
	public boolean isNomesAmbErrors() {
		return nomesAmbErrors;
	}
	public void setNomesAmbErrors(boolean nomesAmbErrors) {
		this.nomesAmbErrors = nomesAmbErrors;
	}
	public Long getUnitatId() {
		return unitatId;
	}
	public void setUnitatId(Long unitatId) {
		this.unitatId = unitatId;
	}
	public String getBustiaOrigen() {
		return bustiaOrigen;
	}
	public void setBustiaOrigen(String bustiaOrigen) {
		this.bustiaOrigen = bustiaOrigen;
	}
	public boolean isMostrarInactivesOrigen() {
		return mostrarInactivesOrigen;
	}
	public void setMostrarInactivesOrigen(boolean mostrarInactivesOrigen) {
		this.mostrarInactivesOrigen = mostrarInactivesOrigen;
	}
	
}
