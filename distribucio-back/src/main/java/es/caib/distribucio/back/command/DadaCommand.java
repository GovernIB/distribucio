/**
 * 
 */
package es.caib.distribucio.back.command;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Command per al manteniment de nodes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadaCommand {

	private Long id;
	@NotNull
	private Long entitatId;
	@NotNull
	private Long registreId;
	@NotNull
	private Long metaDadaId;
	@NotEmpty(groups = {DadaTipusText.class})
	@Size(max=256)
	private String valorText;
	@NotNull(groups = {DadaTipusData.class})
	private Date valorData;
	@NotNull(groups = {DadaTipusSencer.class})
	private Long valorSencer;
	@NotNull(groups = {DadaTipusFlotant.class})
	private Double valorFlotant;
	@NotNull(groups = {DadaTipusImport.class})
	private BigDecimal valorImport;
	private Boolean valorBoolea = false;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public Long getRegistreId() {
		return registreId;
	}
	public void setRegistreId(Long registreId) {
		this.registreId = registreId;
	}
	public Long getMetaDadaId() {
		return metaDadaId;
	}
	public void setMetaDadaId(Long metaDadaId) {
		this.metaDadaId = metaDadaId;
	}
	public String getValorText() {
		return valorText;
	}
	public void setValorText(String valorText) {
		this.valorText = valorText != null ? valorText.trim() : null;
	}
	public Date getValorData() {
		return valorData;
	}
	public void setValorData(Date valorData) {
		this.valorData = valorData;
	}
	public Long getValorSencer() {
		return valorSencer;
	}
	public void setValorSencer(Long valorSencer) {
		this.valorSencer = valorSencer;
	}
	public Double getValorFlotant() {
		return valorFlotant;
	}
	public void setValorFlotant(Double valorFlotant) {
		this.valorFlotant = valorFlotant;
	}
	public BigDecimal getValorImport() {
		return valorImport;
	}
	public void setValorImport(BigDecimal valorImport) {
		this.valorImport = valorImport;
	}
	public Boolean getValorBoolea() {
		return valorBoolea;
	}
	public void setValorBoolea(Boolean valorBoolea) {
		this.valorBoolea = valorBoolea;
	}

	public Object getValor() {
		if (valorText != null && !valorText.isEmpty())
			return valorText;
		else if (valorData != null)
			return valorData;
		else if (valorSencer != null)
			return valorSencer;
		else if (valorFlotant != null)
			return valorFlotant;
		else if (valorImport != null)
			return valorImport;
		else if (valorBoolea != null)
			return valorBoolea;
		return null;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public interface DadaTipusText {}
	public interface DadaTipusData {}
	public interface DadaTipusSencer {}
	public interface DadaTipusFlotant {}
	public interface DadaTipusImport {}
	public interface DadaTipusBoolea {}

}