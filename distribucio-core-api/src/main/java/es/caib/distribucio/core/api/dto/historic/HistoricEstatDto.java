package es.caib.distribucio.core.api.dto.historic;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;

/**
 * Informació de dades d'anotacions per estat i unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@XmlRootElement(name = "dadaEstat")
@XmlAccessorType (XmlAccessType.FIELD)
public class HistoricEstatDto {

    @JsonIgnore
	@XmlTransient
	private EntitatDto entitat;
    @JsonIgnore
	@XmlTransient
	private UnitatOrganitzativaDto unitat;

	private HistoricTipusEnumDto tipus;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone="Europe/Madrid")
	private Date data;

	private RegistreProcesEstatEnum estat;
	private Long correcte;
	private Long correcteTotal;
	private Long error;
	private Long errorTotal;
	private Long total;
	
	public EntitatDto getEntitat() {
		return entitat;
	}
	public void setEntitat(EntitatDto entitat) {
		this.entitat = entitat;
	}
	public UnitatOrganitzativaDto getUnitat() {
		return unitat;
	}
	@XmlElement(name = "unitatCodi")
	public String getUnitatCodi() {
		return this.unitat != null ? this.unitat.getCodi() : null;
	}
	@XmlElement(name = "unitatNom")
	public String getUnitatNom() {
		return this.unitat != null ? this.unitat.getNom() : null;
	}
	public void setUnitat(UnitatOrganitzativaDto unitat) {
		this.unitat = unitat;
	}
	public HistoricTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(HistoricTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public RegistreProcesEstatEnum getEstat() {
		return estat;
	}
	public void setEstat(RegistreProcesEstatEnum estat) {
		this.estat = estat;
	}
	public Long getCorrecte() {
		return correcte;
	}
	public void setCorrecte(Long correcte) {
		this.correcte = correcte;
	}
	public Long getCorrecteTotal() {
		return correcteTotal;
	}
	public void setCorrecteTotal(Long correcteTotal) {
		this.correcteTotal = correcteTotal;
	}
	public Long getError() {
		return error;
	}
	public void setError(Long error) {
		this.error = error;
	}
	public Long getErrorTotal() {
		return errorTotal;
	}
	public void setErrorTotal(Long errorTotal) {
		this.errorTotal = errorTotal;
	}
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
}
