/**
 * 
 */
package es.caib.distribucio.ejb.ws;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.ws.api.annotation.WebContext;

import es.caib.distribucio.logic.intf.service.ws.bustia.BustiaV1WsService;
import lombok.experimental.Delegate;

/**
 * Implementació dels mètodes per al servei de bústies de DISTRIBUCIO.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = BustiaV1WsService.SERVICE_NAME,
		serviceName = BustiaV1WsService.SERVICE_NAME + "Service",
		portName = BustiaV1WsService.SERVICE_NAME + "ServicePort",
		targetNamespace = BustiaV1WsService.NAMESPACE_URI)
@SOAPBinding(style = SOAPBinding.Style.RPC)
@WebContext(
		contextRoot = "/distribucio/ws",
		urlPattern = "/v1/bustia"/*,
		authMethod = "WSBASIC",
		transportGuarantee = "NONE",
		secureWSDLAccess = false*/)
@RolesAllowed({"DIS_BSTWS"})
public class BustiaV1WsServiceBean implements BustiaV1WsService {

	@Delegate
	private BustiaV1WsService delegateService = null;

	protected void setDelegateService(BustiaV1WsService delegateService) {
		this.delegateService = delegateService;
	}

}
