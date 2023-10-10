/**
 * 
 */
package es.caib.distribucio.ejb;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.AvisService;
import lombok.experimental.Delegate;

/**
 * Implementació de AvisService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AvisServiceEjb extends AbstractServiceEjb<AvisService> implements AvisService {

	@Delegate
	private AvisService delegateService = null;

	protected void setDelegateService(AvisService delegateService) {
		this.delegateService = delegateService;
	}

}
