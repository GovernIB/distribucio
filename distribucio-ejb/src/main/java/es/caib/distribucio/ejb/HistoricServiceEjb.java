package es.caib.distribucio.ejb;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.HistoricService;
import lombok.experimental.Delegate;

/**
 * Implementació de HistoricService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class HistoricServiceEjb extends AbstractServiceEjb<HistoricService> implements HistoricService {

	@Delegate
	private HistoricService delegateService = null;

	protected void setDelegateService(HistoricService delegateService) {
		this.delegateService = delegateService;
	}

}
