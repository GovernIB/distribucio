package es.caib.distribucio.core.ejb;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.distribucio.core.api.monitor.MonitorTascaEstatEnum;
import es.caib.distribucio.core.api.monitor.MonitorTascaInfo;
import es.caib.distribucio.core.api.service.MonitorTasquesService;


/**
 * Implementació de MonitorTasquesService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class MonitorTasquesServiceBean implements MonitorTasquesService {
	
	@Autowired
	MonitorTasquesService delegate;

	@Override
	public MonitorTascaInfo addTasca(String codiTasca) {
		return delegate.addTasca(codiTasca);
	}

	@Override
	public void updateTasca(String codiTasca, MonitorTascaEstatEnum estat, Date inici, Date fi, Date properaExecucio,
			String observacions) {
		delegate.updateTasca(codiTasca, estat, inici, fi, properaExecucio, observacions);
	}

	@Override
	public void updateEstat(String codi, MonitorTascaEstatEnum estat) {
		delegate.updateEstat(codi, estat);
	}

	@Override
	public void updateObservacions(String codi, String observacions) {
		delegate.updateObservacions(codi, observacions);
	}

	@Override
	public void updateDataInici(String codi) {
		delegate.updateDataInici(codi);
	}

	@Override
	public void updateDataFi(String codi) {
		delegate.updateDataFi(codi);
	}

	@Override
	public List<MonitorTascaInfo> findAll() {
		return delegate.findAll();
	}

	@Override
	public MonitorTascaInfo findByCodi(String codi) {
		return delegate.findByCodi(codi);
	}

	@Override
	public void updateProperaExecucio(String codi, long plusValue) {
		delegate.updateProperaExecucio(codi, plusValue);		
	}

}
