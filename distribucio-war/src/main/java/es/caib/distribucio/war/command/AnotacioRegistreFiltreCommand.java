/**
 * 
 */
package es.caib.distribucio.war.command;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.core.api.dto.AnotacioRegistreFiltreDto;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.war.helper.ConversioTipusHelper;

/**
 * Command per al filtre del localitzador d'anotacions de registre
 * dels usuaris administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AnotacioRegistreFiltreCommand {

	private Long unitatId;
	private String bustia;
	private Date dataCreacioInici;
	private Date dataCreacioFi;
	private RegistreProcesEstatEnum estat;
	private String nom;
	private String numeroOrigen;
	private boolean nomesAmbErrors;
	private String backCodi;

	
	

	public String getNumeroOrigen() {
		return numeroOrigen;
	}
	public void setNumeroOrigen(String numeroOrigen) {
		this.numeroOrigen = numeroOrigen;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public Long getUnitatId() {
		return unitatId;
	}
	public void setUnitatId(Long unitatId) {
		this.unitatId = unitatId;
	}
	public String getBustia() {
		return bustia;
	}
	public void setBustia(String bustia) {
		this.bustia = bustia;
	}
	public Date getDataCreacioInici() {
		return dataCreacioInici;
	}
	public void setDataCreacioInici(Date dataCreacioInici) {
		this.dataCreacioInici = dataCreacioInici;
	}
	public Date getDataCreacioFi() {
		return dataCreacioFi;
	}
	public void setDataCreacioFi(Date dataCreacioFi) {
		this.dataCreacioFi = dataCreacioFi;
	}
	public RegistreProcesEstatEnum getEstat() {
		return estat;
	}
	public void setEstat(RegistreProcesEstatEnum estat) {
		this.estat = estat;
	}
	
	public String getBackCodi() {
		return backCodi;
	}
	public void setBackCodi(String backCodi) {
		this.backCodi = backCodi;
	}
	public static AnotacioRegistreFiltreCommand asCommand(AnotacioRegistreFiltreDto dto) {
		AnotacioRegistreFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				AnotacioRegistreFiltreCommand.class);
		return command;
	}
	public static AnotacioRegistreFiltreDto asDto(AnotacioRegistreFiltreCommand command) {
		AnotacioRegistreFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				AnotacioRegistreFiltreDto.class);
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	public boolean isNomesAmbErrors() {
		return nomesAmbErrors;
	}
	public void setNomesAmbErrors(boolean nomesAmbErrors) {
		this.nomesAmbErrors = nomesAmbErrors;
	}

}