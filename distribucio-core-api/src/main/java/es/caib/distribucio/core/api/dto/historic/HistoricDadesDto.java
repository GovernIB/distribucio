package es.caib.distribucio.core.api.dto.historic;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe per encapsular les dades històriques consultades a retornar i exportar
 * com XML o JSON.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class HistoricDadesDto {

	@XmlElement(name = "dadaAnotacio")
	@XmlElementWrapper(name = "dadesAnotacions")
	private List<HistoricAnotacioDto> dadesAnotacions = null;

	@XmlElement(name = "dadaEstat")
	@XmlElementWrapper(name = "dadesEstats")
	private List<HistoricEstatDto> dadesEstats = null;
	
	@XmlElement(name = "dadaBustia")
	@XmlElementWrapper(name = "dadesBusties")
	private List<HistoricBustiaDto> dadesBusties = null;
	
	public boolean hasDadesAnotacions() {
		return dadesAnotacions != null && !dadesAnotacions.isEmpty();
	}

	public boolean hasDadesEstats() {
		return dadesEstats != null && !dadesEstats.isEmpty();
	}

	public boolean hasDadesBusties() {
		return dadesBusties != null && !dadesBusties.isEmpty();
	}

	public List<HistoricAnotacioDto> getDadesAnotacions() {
		return dadesAnotacions;
	}
	public void setDadesAnotacions(List<HistoricAnotacioDto> dadesAnotacions) {
		this.dadesAnotacions = dadesAnotacions;
	}
	
	public List<HistoricEstatDto> getDadesEstats() {
		return dadesEstats;
	}
	public void setDadesEstats(List<HistoricEstatDto> dadesEstats) {
		this.dadesEstats = dadesEstats;
	}
	
	public List<HistoricBustiaDto> getDadesBusties() {
		return dadesBusties;
	}
	public void setDadesBusties(List<HistoricBustiaDto> dadesBusties) {
		this.dadesBusties = dadesBusties;
	}
	
}
