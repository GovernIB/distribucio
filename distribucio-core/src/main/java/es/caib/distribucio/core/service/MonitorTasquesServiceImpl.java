package es.caib.distribucio.core.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private Map<String, MonitorTascaInfo> tasques = new HashMap<>();

	@Override
	public MonitorTascaInfo addTasca(String codiTasca) {
		MonitorTascaInfo monitorTascaInfo = new MonitorTascaInfo();
		monitorTascaInfo.setCodi(codiTasca);
		monitorTascaInfo.setEstat(MonitorTascaEstatEnum.EN_ESPERA);
		
		this.tasques.put(codiTasca, monitorTascaInfo);
		
		return monitorTascaInfo;
	}

	@Override
	public void updateTasca(String codiTasca, MonitorTascaEstatEnum estat, Date inici, Date fi, Date properaExecucio,
			String observacions) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateEstat(String codi, MonitorTascaEstatEnum estat) {
		MonitorTascaInfo monitorTascaInfo = this.tasques.get(codi);
		monitorTascaInfo.setEstat(estat);
		this.tasques.put(codi, monitorTascaInfo);

	}

	@Override
	public void updateObservacions(String codi, MonitorTascaEstatEnum estat) {
		MonitorTascaInfo monitorTascaInfo = this.tasques.get(codi);
		monitorTascaInfo.setObservacions("");

	}

	@Override
	public void updateDataInici(String codi) {
		Date dataInici = updateData(0);
		MonitorTascaInfo monitorTascaInfo = this.tasques.get(codi);
		monitorTascaInfo.setDataInici(dataInici);
		this.tasques.put(codi, monitorTascaInfo);
	}

	@Override
	public void updateDataFi(String codi) {
		Date dataFi = updateData(0);
		MonitorTascaInfo monitorTascaInfo = this.tasques.get(codi);
		monitorTascaInfo.setDataFi(dataFi);
		this.tasques.put(codi, monitorTascaInfo);
	}

	@Override
	public void updateProperaExecucio(String codi, long plusValue) {
		Date dataProperaExecucio = updateData(plusValue);
		MonitorTascaInfo monitorTascaInfo = this.tasques.get(codi);
		monitorTascaInfo.setProperaExecucio(dataProperaExecucio);
		this.tasques.put(codi, monitorTascaInfo);
	}
	

	private Date updateData(long plusValue) {
		Date data = new Date(System.currentTimeMillis() + plusValue);
		return data;
	}

	@Override
	public List<MonitorTascaInfo> findAll() {
		List<MonitorTascaInfo> monitorTasques = new ArrayList<>();
		for(Map.Entry<String, MonitorTascaInfo> tasca : this.tasques.entrySet()) {
			monitorTasques.add(tasca.getValue());
		}
		
		return monitorTasques;
	}

	@Override
	public MonitorTascaInfo findByCodi(String codi) {
		return this.tasques.get(codi);
	}

}
