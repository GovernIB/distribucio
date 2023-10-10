/**
 * 
 */
package es.caib.distribucio.ejb;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.ContingutService;
import lombok.experimental.Delegate;

/**
 * Implementació de ContenidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ContingutServiceEjb extends AbstractServiceEjb<ContingutService> implements ContingutService {

	@Delegate
	private ContingutService delegateService = null;

	protected void setDelegateService(ContingutService delegateService) {
		this.delegateService = delegateService;
	}

}