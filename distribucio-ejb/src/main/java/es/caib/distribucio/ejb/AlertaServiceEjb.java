/**
 * 
 */
package es.caib.distribucio.ejb;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.AlertaService;
import lombok.experimental.Delegate;

/**
 * Implementació de AlertaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AlertaServiceEjb extends AbstractServiceEjb<AlertaService> implements AlertaService {

	@Delegate
	private AlertaService delegateService = null;

	protected void setDelegateService(AlertaService delegateService) {
		this.delegateService = delegateService;
	}

}
