/**
 * 
 */
package es.caib.distribucio.ejb.ws;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.ws.api.annotation.WebContext;

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
		urlPattern = "/backoffice"/*,
		transportGuarantee = "NONE",
		secureWSDLAccess = false*/)
public class BackofficeWsServiceBean implements BackofficeWsService {

	@Delegate
	private BackofficeWsService delegateService = null;

	protected void setDelegateService(BackofficeWsService delegateService) {
		this.delegateService = delegateService;
	}

}
