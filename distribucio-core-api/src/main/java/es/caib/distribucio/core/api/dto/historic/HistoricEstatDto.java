package es.caib.distribucio.core.api.dto.historic;

import java.util.Date;

import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;

/**
 * Informació de dades d'anotacions per estat i unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HistoricEstatDto {

	private EntitatDto entitat;
	private UnitatOrganitzativaDto unitat;
	private HistoricTipusEnumDto tipus;
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
