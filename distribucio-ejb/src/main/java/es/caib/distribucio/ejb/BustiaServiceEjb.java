/**
 * 
 */
package es.caib.distribucio.ejb;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.BustiaService;
import lombok.experimental.Delegate;

/**
 * Implementació de BustiaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class BustiaServiceEjb extends AbstractServiceEjb<BustiaService> implements BustiaService {

	@Delegate
	private BustiaService delegateService = null;

	protected void setDelegateService(BustiaService delegateService) {
		this.delegateService = delegateService;
	}

}