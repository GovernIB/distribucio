/**
 * 
 */
package es.caib.distribucio.ejb;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.AplicacioService;
import lombok.experimental.Delegate;

/**
 * Implementació de AplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AplicacioServiceEjb extends AbstractServiceEjb<AplicacioService> implements AplicacioService {

	@Delegate
	private AplicacioService delegateService = null;

	protected void setDelegateService(AplicacioService delegateService) {
		this.delegateService = delegateService;
	}

}
