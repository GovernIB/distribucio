/**
 * 
 */
package es.caib.distribucio.ejb;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.RegistreService;
import lombok.experimental.Delegate;

/**
 * Implementació de RegistreService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class RegistreServiceEjb extends AbstractServiceEjb<RegistreService> implements RegistreService {

	@Delegate
	private RegistreService delegateService = null;

	protected void setDelegateService(RegistreService delegateService) {
		this.delegateService = delegateService;
	}

}
