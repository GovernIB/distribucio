/**
 * 
 */
package es.caib.distribucio.ejb;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.UnitatOrganitzativaService;
import lombok.experimental.Delegate;

/**
 * Implementació de UnitatsOrganitzativesService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class UnitatOrganitzativaServiceEjb extends AbstractServiceEjb<UnitatOrganitzativaService> implements UnitatOrganitzativaService {

	@Delegate
	private UnitatOrganitzativaService delegateService = null;

	protected void setDelegateService(UnitatOrganitzativaService delegateService) {
		this.delegateService = delegateService;
	}

}
