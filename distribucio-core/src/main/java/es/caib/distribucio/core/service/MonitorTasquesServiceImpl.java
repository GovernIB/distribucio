package es.caib.distribucio.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.caib.distribucio.core.api.monitor.MonitorTascaEstatEnum;
import es.caib.distribucio.core.api.monitor.MonitorTascaInfo;
import es.caib.distribucio.core.api.service.MonitorTasquesService;


/**
 * Implementació dels mètodes per a gestionar el monitor de tasques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Service
public class MonitorTasquesServiceImpl implements MonitorTasquesService {
	
	private static Map<String, MonitorTascaInfo> tasques = new HashMap<>();

	@Override
	public MonitorTascaInfo addTasca(String codiTasca) {
		MonitorTascaInfo monitorTascaInfo = new MonitorTascaInfo();
		monitorTascaInfo.setCodi(codiTasca);
		monitorTascaInfo.setEstat(MonitorTascaEstatEnum.EN_ESPERA);
		
		MonitorTasquesServiceImpl.tasques.put(codiTasca, monitorTascaInfo);
		
		return monitorTascaInfo;
	}

	@Override
	public void updateTasca(String codiTasca, MonitorTascaEstatEnum estat, Date inici, Date fi, Date properaExecucio,
			String observacions) {
		logger.info("Actualitzant la tasca " + codiTasca);

	}

	@Override
	public void updateEstat(String codi, MonitorTascaEstatEnum estat) {
		MonitorTascaInfo monitorTascaInfo = MonitorTasquesServiceImpl.tasques.get(codi);
		monitorTascaInfo.setEstat(estat);
		MonitorTasquesServiceImpl.tasques.put(codi, monitorTascaInfo);

	}

	@Override
	public void updateObservacions(String codi, MonitorTascaEstatEnum estat) {
		MonitorTascaInfo monitorTascaInfo = MonitorTasquesServiceImpl.tasques.get(codi);
		monitorTascaInfo.setObservacions("");

	}

	@Override
	public void updateDataInici(String codi) {
		Date dataInici = updateData(0);
		MonitorTascaInfo monitorTascaInfo = MonitorTasquesServiceImpl.tasques.get(codi);
		monitorTascaInfo.setDataInici(dataInici);
		MonitorTasquesServiceImpl.tasques.put(codi, monitorTascaInfo);
	}

	@Override
	public void updateDataFi(String codi, boolean iniciant) {
		Date dataFi = updateData(0);
		MonitorTascaInfo monitorTascaInfo = MonitorTasquesServiceImpl.tasques.get(codi);
		monitorTascaInfo.setDataFi(iniciant ? null : dataFi);
		MonitorTasquesServiceImpl.tasques.put(codi, monitorTascaInfo);
	}

	@Override
	public void updateProperaExecucio(String codi, long plusValue) {
		Date dataProperaExecucio = updateData(plusValue);
		MonitorTascaInfo monitorTascaInfo = MonitorTasquesServiceImpl.tasques.get(codi);
		monitorTascaInfo.setProperaExecucio(dataProperaExecucio);
		MonitorTasquesServiceImpl.tasques.put(codi, monitorTascaInfo);
	}
	

	private Date updateData(long plusValue) {
		Date data = new Date(System.currentTimeMillis() + plusValue);
		return data;
	}

	@Override
	public List<MonitorTascaInfo> findAll() {
		List<MonitorTascaInfo> monitorTasques = new ArrayList<>();
		for(Map.Entry<String, MonitorTascaInfo> tasca : MonitorTasquesServiceImpl.tasques.entrySet()) {
			monitorTasques.add(tasca.getValue());
		}
		
		return monitorTasques;
	}

	@Override
	public MonitorTascaInfo findByCodi(String codi) {
		return MonitorTasquesServiceImpl.tasques.get(codi);
	}

	@Override
	public void inici(String codiTasca) {
    	this.updateDataInici(codiTasca);
    	this.updateDataFi(codiTasca, true);
    	this.updateEstat(codiTasca, MonitorTascaEstatEnum.EN_EXECUCIO);		
	}

	@Override
	public void fi(String codiTasca) {
		this.updateEstat(codiTasca, MonitorTascaEstatEnum.EN_ESPERA);
		this.updateDataFi(codiTasca, false);		
	}

	@Override
	public void error(String codiTasca) {
		this.updateEstat(codiTasca, MonitorTascaEstatEnum.ERROR);
		this.updateDataFi(codiTasca, false);
	}
	
	
		
	private static final Logger logger = LoggerFactory.getLogger(RegistreServiceImpl.class);

}
