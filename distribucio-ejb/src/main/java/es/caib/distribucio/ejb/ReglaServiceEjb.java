/**
 * 
 */
package es.caib.distribucio.ejb;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.ReglaService;
import lombok.experimental.Delegate;

/**
 * Implementació de ReglaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ReglaServiceEjb extends AbstractServiceEjb<ReglaService> implements ReglaService {

	@Delegate
	private ReglaService delegateService = null;

	protected void setDelegateService(ReglaService delegateService) {
		this.delegateService = delegateService;
	}

}
