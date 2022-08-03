package es.caib.distribucio.core.api.monitor;

import java.util.Date;

public class MonitorTascaInfo {
	
	private String codi;
	private MonitorTascaEstatEnum estat;
	private Date dataInici;
	private Date dataFi;
	private Date properaExecucio;
	private String observacions;
	
	
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public MonitorTascaEstatEnum getEstat() {
		return estat;
	}
	public void setEstat(MonitorTascaEstatEnum estat) {
		this.estat = estat;
	}
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
	public Date getProperaExecucio() {
		return properaExecucio;
	}
	public void setProperaExecucio(Date properaExecucio) {
		this.properaExecucio = properaExecucio;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	
	
	
	

}
