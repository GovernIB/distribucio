/**
 * 
 */
package es.caib.distribucio.ejb;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.MonitorIntegracioService;
import lombok.experimental.Delegate;

/**
 * Implementació de MonitorIntegracioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class MonitorIntegracioServiceEjb extends AbstractServiceEjb<MonitorIntegracioService> implements MonitorIntegracioService {

	@Delegate
	private MonitorIntegracioService delegateService = null;

	protected void setDelegateService(MonitorIntegracioService delegateService) {
		this.delegateService = delegateService;
	}

}
