/**
 * 
 */
package es.caib.distribucio.core.api.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;


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
	private RegistreEnviatPerEmailEnumDto enviatPerEmail;

	private String backCodi;
	
	private ReglaDto regla;

	private RegistreMarcatPerSobreescriureEnumDto sobreescriure;
	
	private RegistreFiltreReintentsEnumDto reintents;
	
	// Filtre per administradors
	/** Estat específic. */
	private RegistreProcesEstatEnum estat;
	/** Per filtrar només les que tinguin error informat. */
	private boolean nomesAmbErrors;
	/** Unitat organitzativa superior. */
	private Long unitatId;
	
	private String bustiaOrigen;
	
	/** Per mostrar el contingut de les bústies innactives */
	private boolean mostrarInnactivesOrigen;
	
	/** Per filtrar només les que tinguin annexos en estat esborrany. */
	private boolean nomesAmbEsborranys;
	
	private String procedimentCodi;
	
	private RegistreNombreAnnexesEnumDto nombreAnnexes;

	
	public String getBackCodi() {
		return backCodi;
	}
	public void setBackCodi(String backCodi) {
		this.backCodi = backCodi;
	}
	public ReglaDto getRegla() {
		return regla;
	}
	public void setRegla(ReglaDto regla) {
		this.regla = regla;
	}
	public RegistreEnviatPerEmailEnumDto getEnviatPerEmail() {
		return enviatPerEmail;
	}
	public void setEnviatPerEmail(RegistreEnviatPerEmailEnumDto enviatPerEmail) {
		this.enviatPerEmail = enviatPerEmail;
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
		return mostrarInnactivesOrigen;
	}
	public void setMostrarInactivesOrigen(boolean mostrarInactivesOrigen) {
		this.mostrarInnactivesOrigen = mostrarInactivesOrigen;
	}
	public RegistreMarcatPerSobreescriureEnumDto getSobreescriure() {
		return sobreescriure;
	}
	public void setSobreescriure(RegistreMarcatPerSobreescriureEnumDto sobreescriure) {
		this.sobreescriure = sobreescriure;
	}	
	public RegistreFiltreReintentsEnumDto getReintents() {
		return reintents;
	}
	public void setReintents(RegistreFiltreReintentsEnumDto reintents) {
		this.reintents = reintents;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	public boolean isNomesAmbEsborranys() {
		return nomesAmbEsborranys;
	}
	public void setNomesAmbEsborranys(boolean nomesAmbEsborranys) {
		this.nomesAmbEsborranys = nomesAmbEsborranys;
	}
	
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}

	public RegistreNombreAnnexesEnumDto getNombreAnnexes() {
		return nombreAnnexes;
	}
	public void setNombreAnnexes(RegistreNombreAnnexesEnumDto nombreAnnexes) {
		this.nombreAnnexes = nombreAnnexes;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
