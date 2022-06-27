package es.caib.distribucio.core.api.service;

import java.util.Date;
import java.util.List;

import es.caib.distribucio.core.api.monitor.MonitorTascaEstatEnum;
import es.caib.distribucio.core.api.monitor.MonitorTascaInfo;

public interface MonitorTasquesService {
	
	public MonitorTascaInfo addTasca(String codiTasca);
	
	public void updateTasca(String codiTasca, 
							MonitorTascaEstatEnum estat, 
							Date inici, 
							Date fi, 
							Date properaExecucio, 
							String observacions);
	
	public void updateEstat(String codi, MonitorTascaEstatEnum estat);
	
	public void updateObservacions(String codi, MonitorTascaEstatEnum estat);
	
	public void updateDataInici(String codi);
	
	public void updateDataFi(String codi);
	
	public void updateProperaExecucio(String codi, long plusValue);
	
	public List<MonitorTascaInfo> findAll();
	
	public MonitorTascaInfo findByCodi(String codi);
	
	

}
