package es.caib.distribucio.logic.intf.dto;

import java.util.Date;

public class HistogramPendentsEntryDto {
	
	private Date data;
	private int pendentArxiu;
	private int processats = 0;
	private int errors = 0;
	private float processTimeAverage = 0;
	
	
	
	public HistogramPendentsEntryDto() {
	}
	/**
	 * Use only for tests
	 */
	public HistogramPendentsEntryDto(
			Date data,
			int pendentArxiu,
			int processats,
			int errors,
			float processTimeAverage) {
		super();
		this.data = data;
		this.pendentArxiu = pendentArxiu;
		this.errors = errors;
		this.processats = processats;
		this.processTimeAverage = processTimeAverage;
	}
	
	public Date getData() {
		return data;
	}
	public int getPendentArxiu() {
		return pendentArxiu;
	}
	public int getErrors() {
		return errors;
	}
	public int getProcessats() {
		return processats;
	}
	public float getProcessTimeAverage() {
		return processTimeAverage;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public void setPendentArxiu(int pendentArxiu) {
		this.pendentArxiu = pendentArxiu;
	}
	
	public void increaseErrorCounter() {
		this.errors++;
	}
	public void increaseProcessatsCounter() {
		this.processats++;
	}
	public void setProcessTimeAverage(float processTimeAverage) {
		this.processTimeAverage = processTimeAverage;
	}
	
	
	
//	@Override
//	public String toString() {
//		return "data="+ data+ ", pendentArxiu="+ pendentArxiu + "errors="+ errors + ", processats="+ processats + ", processTimeAverage="+ processTimeAverage;
//	}

	
	
}