/**
 * 
 */
package es.caib.distribucio.ejb;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.SegonPlaService;
import lombok.experimental.Delegate;

/**
 * Implementació de BustiaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class SegonPlaServiceEjb extends AbstractServiceEjb<SegonPlaService> implements SegonPlaService {

	@Delegate
	private SegonPlaService delegateService = null;

	protected void setDelegateService(SegonPlaService delegateService) {
		this.delegateService = delegateService;
	}

}
