package es.caib.distribucio.ejb;

import java.util.List;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.monitor.MonitorTascaInfo;
import es.caib.distribucio.logic.intf.service.MonitorTasquesService;
import lombok.experimental.Delegate;


/**
 * Implementació de MonitorTasquesService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class MonitorTasquesServiceEjb extends AbstractServiceEjb<MonitorTasquesService> implements MonitorTasquesService {

	@Delegate
	private MonitorTasquesService delegateService = null;

	@Override
	public MonitorTascaInfo addTasca(String codiTasca) {
		return delegateService.addTasca(codiTasca);
	}

	@Override
	public List<MonitorTascaInfo> findAll() {
		return delegateService.findAll();
	}

	@Override
	public MonitorTascaInfo findByCodi(String codi) {
		return delegateService.findByCodi(codi);
	}

	@Override
	public void updateProperaExecucio(String codi, Long plusValue) {
		delegateService.updateProperaExecucio(codi, plusValue);
	}

	@Override
	public void inici(String codiTasca) {
		delegateService.inici(codiTasca);
	}

	@Override
	public void fi(String codiTasca) {
		delegateService.fi(codiTasca);
	}

	@Override
	public void error(String codiTasca, String error) {
		delegateService.error(codiTasca, error);
	}

	@Override
	public void reiniciarTasquesEnSegonPla() {
		delegateService.reiniciarTasquesEnSegonPla();
	}
	
	@Override
	public void reiniciarTasquesEnSegonPla(String codiTasca) {
		delegateService.reiniciarTasquesEnSegonPla(codiTasca);
	}

	protected void setDelegateService(MonitorTasquesService delegateService) {
		this.delegateService = delegateService;
	}

}
