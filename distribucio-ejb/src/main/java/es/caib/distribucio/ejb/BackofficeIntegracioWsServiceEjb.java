/**
 * 
 */
package es.caib.distribucio.ejb;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.logic.intf.service.ws.backoffice.BackofficeIntegracioWsService;
import es.caib.distribucio.logic.intf.service.ws.backoffice.Estat;
import lombok.experimental.Delegate;

/**
 * Implementació de BackofficeIntegracioWsService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class BackofficeIntegracioWsServiceEjb extends AbstractServiceEjb<BackofficeIntegracioWsService> implements BackofficeIntegracioWsService {

	@Delegate
	private BackofficeIntegracioWsService delegateService = null;

	@Override
	public AnotacioRegistreEntrada consulta(AnotacioRegistreId id) {
		return delegateService.consulta(id);
	}

	@Override
	public void canviEstat(
			AnotacioRegistreId id,
			Estat estat,
			String observacions) {
		delegateService.canviEstat(id, estat, observacions);
	}

	protected void setDelegateService(BackofficeIntegracioWsService delegateService) {
		this.delegateService = delegateService;
	}

}
