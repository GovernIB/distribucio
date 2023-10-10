package es.caib.distribucio.ejb;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.DominiService;
import lombok.experimental.Delegate;

/**
 * Implementació de DominiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class DominiServiceEjb extends AbstractServiceEjb<DominiService> implements DominiService {

	@Delegate
	private DominiService delegateService = null;

	protected void setDelegateService(DominiService delegateService) {
		this.delegateService = delegateService;
	}

}
