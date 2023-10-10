package es.caib.distribucio.ejb;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.ProcedimentService;
import lombok.experimental.Delegate;

/**
 * Implementació de ProcedimentService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Stateless
public class ProcedimentServiceEjb extends AbstractServiceEjb<ProcedimentService> implements ProcedimentService {

	@Delegate
	private ProcedimentService delegateService = null;

	protected void setDelegateService(ProcedimentService delegateService) {
		this.delegateService = delegateService;
	}

}
