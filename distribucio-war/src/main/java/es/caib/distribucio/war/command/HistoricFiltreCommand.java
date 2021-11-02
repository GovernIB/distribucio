package es.caib.distribucio.war.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import es.caib.distribucio.core.api.dto.historic.HistoricDadesMostrarEnumDto;
import es.caib.distribucio.core.api.dto.historic.HistoricFiltreDto;
import es.caib.distribucio.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.distribucio.war.helper.ConversioTipusHelper;

public class HistoricFiltreCommand {

	private Date dataInici;
	private Date dataFi;

	private String codiUnitatSuperior;
	private List<Long> unitatIdFiltre;
	
	private boolean actualitzar;
	
	@NotNull @NotEmpty
	private List<HistoricDadesMostrarEnumDto> dadesMostrar;
	
	private HistoricTipusEnumDto tipusAgrupament; // DIARI, MENSUAL
	
	public Date getDataInici() {
		return dataInici;
	}

	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}

	public Date getDataFi() {
		return dataFi;
	}

	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}

	public String getCodiUnitatSuperior() {
		return codiUnitatSuperior;
	}

	public void setCodiUnitatSuperior(String codiUnitatSuperior) {
		this.codiUnitatSuperior = codiUnitatSuperior;
	}

	public List<Long> getUnitatIdFiltre() {
		return unitatIdFiltre;
	}

	public void setUnitatIdFiltre(List<Long> unitatIdFiltre) {
		this.unitatIdFiltre = unitatIdFiltre;
	}

	public List<HistoricDadesMostrarEnumDto> getDadesMostrar() {
		return dadesMostrar;
	}

	public void setDadesMostrar(List<HistoricDadesMostrarEnumDto> dadesMostrar) {
		this.dadesMostrar = dadesMostrar;
	}

	public boolean isActualitzar() {
		return actualitzar;
	}

	public void setActualitzar(boolean actualitzar) {
		this.actualitzar = actualitzar;
	}

	public HistoricTipusEnumDto getTipusAgrupament() {
		return tipusAgrupament;
	}

	public void setTipusAgrupament(HistoricTipusEnumDto tipusAgrupament) {
		this.tipusAgrupament = tipusAgrupament;
	}

	public HistoricFiltreCommand() {
		this.dataFi = null;
		this.dataInici = null;
		this.codiUnitatSuperior = null;
		this.unitatIdFiltre = new ArrayList<Long>();
		this.dadesMostrar = new ArrayList<HistoricDadesMostrarEnumDto>();
		dadesMostrar.add(HistoricDadesMostrarEnumDto.UO);
		dadesMostrar.add(HistoricDadesMostrarEnumDto.ESTAT);
		dadesMostrar.add(HistoricDadesMostrarEnumDto.BUSTIES);
		this.tipusAgrupament = HistoricTipusEnumDto.DIARI;
	}
	
	public boolean showingDadesUO() {
		return dadesMostrar != null && dadesMostrar.contains(HistoricDadesMostrarEnumDto.UO);
	}

	public boolean showingDadesEstat() {
		return dadesMostrar != null && dadesMostrar.contains(HistoricDadesMostrarEnumDto.ESTAT);
	}

	public boolean showingDadesBusties() {
		return dadesMostrar != null && dadesMostrar.contains(HistoricDadesMostrarEnumDto.BUSTIES);
	}

	public HistoricFiltreDto asDto() {
		return ConversioTipusHelper.convertir(this, HistoricFiltreDto.class);
	}
	
}
