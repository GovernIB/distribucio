/**
 * 
 */
package es.caib.distribucio.ejb.ws;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.logic.intf.service.ws.backoffice.BackofficeIntegracioWsService;
import es.caib.distribucio.logic.intf.service.ws.backoffice.Estat;
import lombok.experimental.Delegate;

/**
 * Implementació de BackofficeIntegracioWsService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * Aquest EJB és utilitzat per l'EJB que defineix el servei web homònim.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@RolesAllowed(BaseConfig.ROLE_BACKOFFICE_WS)
public class BackofficeIntegracioWsServiceEjb extends AbstractServiceEjb<BackofficeIntegracioWsService> implements BackofficeIntegracioWsService {

	@Delegate
	private BackofficeIntegracioWsService delegateService = null;

	@Override
	public AnotacioRegistreEntrada consulta(AnotacioRegistreId id) {
		propagateEjbAuthenticationToSpringSecurity(BaseConfig.ROLE_BACKOFFICE_WS);
		return delegateService.consulta(id);
	}

	@Override
	public void canviEstat(
			AnotacioRegistreId id,
			Estat estat,
			String observacions) {
		propagateEjbAuthenticationToSpringSecurity(BaseConfig.ROLE_BACKOFFICE_WS);
		delegateService.canviEstat(id, estat, observacions);
	}

	protected void setDelegateService(BackofficeIntegracioWsService delegateService) {
		this.delegateService = delegateService;
	}

}
