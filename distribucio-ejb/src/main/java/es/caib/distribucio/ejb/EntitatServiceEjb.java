/**
 * 
 */
package es.caib.distribucio.ejb;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.EntitatService;
import lombok.experimental.Delegate;

/**
 * Implementació de EntitatService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class EntitatServiceEjb extends AbstractServiceEjb<EntitatService> implements EntitatService {

	@Delegate
	private EntitatService delegateService = null;

	protected void setDelegateService(EntitatService delegateService) {
		this.delegateService = delegateService;
	}

}
