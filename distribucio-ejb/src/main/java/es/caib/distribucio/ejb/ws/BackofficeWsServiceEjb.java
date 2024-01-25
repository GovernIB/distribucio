/**
 * 
 */
package es.caib.distribucio.ejb.ws;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.logic.intf.service.ws.backoffice.BackofficeWsService;
import lombok.experimental.Delegate;

/**
 * Implementació de BackofficeWsService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * Aquest EJB és utilitzat per l'EJB que defineix el servei web homònim.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@RolesAllowed(BaseConfig.ROLE_BACKOFFICE_WS)
public class BackofficeWsServiceEjb extends AbstractServiceEjb<BackofficeWsService> implements BackofficeWsService {

	@Delegate
	private BackofficeWsService delegateService = null;

	@Override
	public void comunicarAnotacionsPendents(
			List<AnotacioRegistreId> ids) {
		propagateEjbAuthenticationToSpringSecurity(BaseConfig.ROLE_BACKOFFICE_WS);
		delegateService.comunicarAnotacionsPendents(ids);
	}

	protected void setDelegateService(BackofficeWsService delegateService) {
		propagateEjbAuthenticationToSpringSecurity(BaseConfig.ROLE_BACKOFFICE_WS);
		this.delegateService = delegateService;
	}

}
