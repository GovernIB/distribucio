/**
 * 
 */
package es.caib.distribucio.ejb.ws;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.logic.intf.service.ws.backoffice.BackofficeIntegracioWsService;
import es.caib.distribucio.logic.intf.service.ws.backoffice.Estat;
import lombok.experimental.Delegate;

/**
 * Implementació dels mètodes per al servei de bústies de DISTRIBUCIO. L'usuari que invoca
 * el servei ha de tenir el rol DIS_BACKWS.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
//@RolesAllowed(BaseConfig.ROLE_BACKOFFICE_WS)
public class BackofficeIntegracioWsServiceWs extends AbstractServiceEjb<BackofficeIntegracioWsService> implements BackofficeIntegracioWsService {

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
