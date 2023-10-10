package es.caib.distribucio.logic.intf.dto.historic;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonFormat;

import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;

/**
 * Informació de dades d'anotacions per estat i unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@XmlRootElement(name = "dadaEstat")
@XmlAccessorType (XmlAccessType.FIELD)
public class HistoricEstatDto {
	
	private HistoricTipusEnumDto tipus;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone="Europe/Madrid")
	private Date data;

	private RegistreProcesEstatEnum estat;
	private Long correcte;
	private Long correcteTotal;
	private Long error;
	private Long errorTotal;
	private Long total;
	
	public HistoricEstatDto() {
		
	}
	/** Constructor per la consulta d'agregats. */
	public HistoricEstatDto(
			Date data,
			HistoricTipusEnumDto tipus,
			RegistreProcesEstatEnum estat,
			Long correcte,
			Long correcteTotal,
			Long error,
			Long errorTotal,
			Long total) {
		this.setData(data);
		this.setTipus(tipus);
		this.setEstat(estat);
		this.setCorrecte(correcte);
		this.setCorrecteTotal(correcteTotal);
		this.setError(error);
		this.setErrorTotal(errorTotal);
		this.setTotal(total);
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
