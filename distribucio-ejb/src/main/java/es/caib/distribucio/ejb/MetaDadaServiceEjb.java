package es.caib.distribucio.ejb;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.MetaDadaService;
import lombok.experimental.Delegate;

/**
 * Implementació de MetaDadaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class MetaDadaServiceEjb extends AbstractServiceEjb<MetaDadaService> implements MetaDadaService {

	@Delegate
	private MetaDadaService delegateService = null;

	protected void setDelegateService(MetaDadaService delegateService) {
		this.delegateService = delegateService;
	}

}
