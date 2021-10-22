package es.caib.distribucio.core.api.dto.historic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HistoricFiltreDto {

	private Date dataInici;
	private Date dataFi;

	private List<Long> codiUnitatSuperior;
	private List<Long> unitatIdFiltre;

	private HistoricDadesMostrarEnumDto dadesMostrar;
		
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


	public HistoricDadesMostrarEnumDto getDadesMostrar() {
		return dadesMostrar;
	}


	public void setDadesMostrar(HistoricDadesMostrarEnumDto dadesMostrar) {
		this.dadesMostrar = dadesMostrar;
	}


	public HistoricTipusEnumDto getTipusAgrupament() {
		return tipusAgrupament;
	}


	public void setTipusAgrupament(HistoricTipusEnumDto tipusAgrupament) {
		this.tipusAgrupament = tipusAgrupament;
	}


	public List<Date> getQueriedDates() {		
		if (tipusAgrupament == HistoricTipusEnumDto.DIARI) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataFi);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
//			cal.add(Calendar.DAY_OF_MONTH, -1);
			Date dataFinal = cal.getTime();
			
			cal = Calendar.getInstance();
			cal.setTime(dataInici);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date data = cal.getTime();
			
	
			List<Date> dates = new ArrayList<Date> ();
			dates.add(data);
			while(data.compareTo(dataFinal) < 0) {
				cal.add(Calendar.DAY_OF_MONTH, 1);
				data = cal.getTime();
				dates.add(data);
			}
			return dates;
			
		} else if (tipusAgrupament == HistoricTipusEnumDto.MENSUAL) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dataFi);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.MONTH, -1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			Date dataFinal = cal.getTime();
			
			cal = Calendar.getInstance();
			cal.setTime(dataInici);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			Date data = cal.getTime();
			
	
			List<Date> dates = new ArrayList<Date> ();
			dates.add(data);
			while(data.compareTo(dataFinal) < 0) {
				cal.add(Calendar.MONTH, 1);
				data = cal.getTime();
				dates.add(data);
			}
			return dates;
			
		} else {
			return null;
		}
	}
}
