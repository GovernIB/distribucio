/**
 * 
 */
package es.caib.distribucio.ejb.ws;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.ws.api.annotation.WebContext;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.logic.intf.service.ws.backoffice.BackofficeWsService;
import lombok.experimental.Delegate;

/**
 * Implementació dels mètodes per al servei de bústies de DISTRIBUCIO.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = BackofficeWsService.SERVICE_NAME,
		serviceName = BackofficeWsService.SERVICE_NAME + "Service",
		portName = BackofficeWsService.SERVICE_NAME + "ServicePort",
		targetNamespace = BackofficeWsService.NAMESPACE_URI)
@SOAPBinding(style = SOAPBinding.Style.RPC)
@WebContext(
		contextRoot = "/distribucio/ws",
		urlPattern = "/backoffice",
		secureWSDLAccess = false)
public class BackofficeWsServiceWs extends AbstractServiceEjb<BackofficeWsService> implements BackofficeWsService {

	@Delegate
	private BackofficeWsService delegateService = null;

	@Override
	public void comunicarAnotacionsPendents(List<AnotacioRegistreId> ids) {
		delegateService.comunicarAnotacionsPendents(ids);
	}

	protected void setDelegateService(BackofficeWsService delegateService) {
		this.delegateService = delegateService;
	}

}