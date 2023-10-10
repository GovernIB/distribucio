package es.caib.distribucio.ejb;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
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

	protected void setDelegateService(MonitorTasquesService delegateService) {
		this.delegateService = delegateService;
	}

}
