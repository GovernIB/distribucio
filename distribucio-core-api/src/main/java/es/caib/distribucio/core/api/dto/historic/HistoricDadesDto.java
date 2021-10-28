package es.caib.distribucio.core.api.dto.historic;

import java.util.List;

/**
 * Classe per encapsular les dades històriques consultades a retornar.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HistoricDadesDto {

	private List<HistoricAnotacioDto> dadesAnotacions = null;
	private List<HistoricEstatDto> dadesEstats = null;
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
