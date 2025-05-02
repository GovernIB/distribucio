/**
 * 
 */
package es.caib.distribucio.back.command;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.logic.intf.dto.AnnexosFiltreDto;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaTipusEnumDto;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnnexEstat;

/**
 * Command per al filtre del localitzador d'annexos
 * dels usuaris administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AnnexosFiltreCommand {

	private String numero;
	private AnnexEstat arxiuEstat;
	private ArxiuFirmaTipusEnumDto tipusFirma;
	private String titol;
	private String fitxerNom;
	private String fitxerTipusMime;
	private Date dataRecepcioInici;
	private Date dataRecepcioFi;
	private Integer numeroCopia;
	
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}		
	public AnnexEstat getArxiuEstat() {
		return arxiuEstat;
	}
	public void setArxiuEstat(AnnexEstat arxiuEstat) {
		this.arxiuEstat = arxiuEstat;
	}
	public ArxiuFirmaTipusEnumDto getTipusFirma() {
		return tipusFirma;
	}
	public void setTipusFirma(ArxiuFirmaTipusEnumDto tipusFirma) {
		this.tipusFirma = tipusFirma;
	}	
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getFitxerNom() {
		return fitxerNom;
	}
	public void setFitxerNom(String fitxerNom) {
		this.fitxerNom = fitxerNom;
	}
	public String getFitxerTipusMime() {
		return fitxerTipusMime;
	}
	public void setFitxerTipusMime(String fitxerTipusMime) {
		this.fitxerTipusMime = fitxerTipusMime;
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
	public Integer getNumeroCopia() {
		return numeroCopia;
	}
	public void setNumeroCopia(Integer numeroCopia) {
		this.numeroCopia = numeroCopia;
	}
	public static AnnexosFiltreCommand asCommand(AnnexosFiltreDto dto) {
		AnnexosFiltreCommand command = ConversioTipusHelper.convertir(
				dto,
				AnnexosFiltreCommand.class);
		return command;
	}
	public static AnnexosFiltreDto asDto(AnnexosFiltreCommand command) {
		AnnexosFiltreDto dto = ConversioTipusHelper.convertir(
				command,
				AnnexosFiltreDto.class);		
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
