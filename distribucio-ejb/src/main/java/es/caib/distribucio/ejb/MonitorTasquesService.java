package es.caib.distribucio.ejb;

import java.util.List;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.monitor.MonitorTascaInfo;

/**
 * Implementació de MonitorTasquesService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class MonitorTasquesService extends AbstractService<es.caib.distribucio.logic.intf.service.MonitorTasquesService> implements es.caib.distribucio.logic.intf.service.MonitorTasquesService {

	@Override
	public MonitorTascaInfo addTasca(String codiTasca) {
		return getDelegateService().addTasca(codiTasca);
	}

	@Override
	public List<MonitorTascaInfo> findAll() {
		return getDelegateService().findAll();
	}

	@Override
	public MonitorTascaInfo findByCodi(String codi) {
		return getDelegateService().findByCodi(codi);
	}

	@Override
	public void updateProperaExecucio(String codi, Long plusValue) {
		getDelegateService().updateProperaExecucio(codi, plusValue);
	}

	@Override
	public void inici(String codiTasca) {
		getDelegateService().inici(codiTasca);
	}

	@Override
	public void fi(String codiTasca) {
		getDelegateService().fi(codiTasca);
	}

	@Override
	public void error(String codiTasca, String error) {
		getDelegateService().error(codiTasca, error);
	}

	@Override
	public void reiniciarTasquesEnSegonPla() {
		getDelegateService().reiniciarTasquesEnSegonPla();
	}
	
	@Override
	public void reiniciarTasquesEnSegonPla(String codiTasca) {
		getDelegateService().reiniciarTasquesEnSegonPla(codiTasca);
	}

}
