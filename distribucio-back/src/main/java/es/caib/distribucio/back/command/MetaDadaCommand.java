/**
 * 
 */
package es.caib.distribucio.back.command;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.distribucio.back.helper.ConversioTipusHelper;
import es.caib.distribucio.back.validation.CodiMetaDadaNoRepetit;
import es.caib.distribucio.back.validation.CodiMetaDadaNomValid;
import es.caib.distribucio.logic.intf.dto.MetaDadaDto;
import es.caib.distribucio.logic.intf.dto.MetaDadaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.MultiplicitatEnumDto;

/**
 * Command per al manteniment de meta-dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@CodiMetaDadaNoRepetit(
		campId = "id",
		campCodi = "codi",
		campEntitatId = "entitatId")
public class MetaDadaCommand {

	private Long id;

	@NotEmpty @Size(max=64) @CodiMetaDadaNomValid
	private String codi;
	@NotEmpty @Size(max=256)
	private String nom;
	@NotNull
	private MetaDadaTipusEnumDto tipus;
	@NotNull
	private MultiplicitatEnumDto multiplicitat;
	
	@Size(max=1024)
	private String descripcio;
	private Long entitatId;
	private Long metaNodeId;
	private String domini;
		
	private Long valorSencer;
	private Double valorFlotant;
	private BigDecimal valorImport;
	private Date valorData; 
	private Boolean valorBoolea;
	private String valorString;
	
	private boolean noAplica;
	
	public static List<MetaDadaCommand> toMetaDadaCommands(
			List<MetaDadaDto> dtos) {
		List<MetaDadaCommand> commands = new ArrayList<MetaDadaCommand>();
		for (MetaDadaDto dto: dtos) {
			commands.add(
					ConversioTipusHelper.convertir(
							dto,
							MetaDadaCommand.class));
		}
		return commands;
	}

	public static MetaDadaCommand asCommand(MetaDadaDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				MetaDadaCommand.class);
	}
	public static MetaDadaDto asDto(MetaDadaCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				MetaDadaDto.class);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCodi() {
		return codi;
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public MetaDadaTipusEnumDto getTipus() {
		return tipus;
	}

	public void setTipus(MetaDadaTipusEnumDto tipus) {
		this.tipus = tipus;
	}

	public MultiplicitatEnumDto getMultiplicitat() {
		return multiplicitat;
	}

	public void setMultiplicitat(MultiplicitatEnumDto multiplicitat) {
		this.multiplicitat = multiplicitat;
	}

	public String getDescripcio() {
		return descripcio;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	public Long getEntitatId() {
		return entitatId;
	}

	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}

	public Long getMetaNodeId() {
		return metaNodeId;
	}

	public void setMetaNodeId(Long metaNodeId) {
		this.metaNodeId = metaNodeId;
	}

	public String getDomini() {
		return domini;
	}

	public void setDomini(String domini) {
		this.domini = domini;
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

	public Date getValorData() {
		return valorData;
	}

	public void setValorData(Date valorData) {
		this.valorData = valorData;
	}

	public Boolean getValorBoolea() {
		return valorBoolea;
	}

	public void setValorBoolea(Boolean valorBoolea) {
		this.valorBoolea = valorBoolea;
	}

	public String getValorString() {
		return valorString;
	}

	public void setValorString(String valorString) {
		this.valorString = valorString;
	}

	public boolean isNoAplica() {
		return noAplica;
	}

	public void setNoAplica(boolean noAplica) {
		this.noAplica = noAplica;
	}
	
}
