package es.caib.distribucio.war.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import es.caib.distribucio.core.api.dto.historic.HistoricDadesMostrarEnumDto;
import es.caib.distribucio.core.api.dto.historic.HistoricFiltreDto;
import es.caib.distribucio.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.distribucio.war.helper.ConversioTipusHelper;

public class HistoricFiltreCommand {

	private Date dataInici;
	private Date dataFi;

	private List<Long> codiUnitatSuperior;
	private List<Long> unitatIdFiltre;
	
	
	@NotNull @NotEmpty
	private List<HistoricDadesMostrarEnumDto> dadesMostrar;
	
	private HistoricTipusEnumDto tipusAgrupament; // DIARI, MENSUAL

//	private boolean showingTables;
	
	
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

	public List<Long> getCodiUnitatSuperior() {
		return codiUnitatSuperior;
	}

	public void setCodiUnitatSuperior(List<Long> codiUnitatSuperior) {
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

	public HistoricTipusEnumDto getTipusAgrupament() {
		return tipusAgrupament;
	}

	public void setTipusAgrupament(HistoricTipusEnumDto tipusAgrupament) {
		this.tipusAgrupament = tipusAgrupament;
	}

//	public boolean isShowingTables() {
//		return showingTables;
//	}
//
//	public void setShowingTables(boolean showingTables) {
//		this.showingTables = showingTables;
//	}

	public HistoricFiltreCommand() {
//		DateTime dateStartToday = (new LocalDate()).toDateTimeAtStartOfDay();
//		this.dataFi = dateStartToday.minusDays(1).toDate();
//		this.dataInici = dateStartToday.minusDays(30).toDate();
		this.dataFi = null;
		this.dataInici = null;
		this.codiUnitatSuperior = new ArrayList<Long>();
		this.unitatIdFiltre = new ArrayList<Long>();
		this.dadesMostrar = new ArrayList<HistoricDadesMostrarEnumDto>();
		dadesMostrar.add(HistoricDadesMostrarEnumDto.UO);
		dadesMostrar.add(HistoricDadesMostrarEnumDto.ESTAT);
		dadesMostrar.add(HistoricDadesMostrarEnumDto.BUSTIES);
		this.tipusAgrupament = HistoricTipusEnumDto.DIARI;
//		this.showingTables = true;
	}

	public void updateConditional(
			Date dataInici, 
			Date dataFi, 
			List<Long> codiUnitatSuperior, 
			List<Long> unitatIdFiltre,
			HistoricTipusEnumDto tipusAgrupament) {
		if (dataInici != null) {
			this.dataInici = dataInici;
		}
		
		if (dataFi != null) {
			this.dataFi = dataFi;
		}
		
		if (codiUnitatSuperior != null) {
			this.codiUnitatSuperior = codiUnitatSuperior;
		}
		
		if (unitatIdFiltre != null) {
			this.unitatIdFiltre = unitatIdFiltre;
		}

		if (tipusAgrupament != null) {
			this.tipusAgrupament = tipusAgrupament;
		}
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
