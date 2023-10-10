package es.caib.distribucio.ejb;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.BackofficeService;
import lombok.experimental.Delegate;

/**
 * Implementació de BackofficeService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class BackofficeServiceEjb extends AbstractServiceEjb<BackofficeService> implements BackofficeService {

	@Delegate
	private BackofficeService delegateService = null;

	protected void setDelegateService(BackofficeService delegateService) {
		this.delegateService = delegateService;
	}

}
