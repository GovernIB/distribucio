/**
 * 
 */
package es.caib.distribucio.ws.backoffice;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.ws.api.annotation.WebContext;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.logic.intf.service.ws.backoffice.BackofficeWsService;

/**
 * Implementació dels mètodes per al servei de bústies de DISTRIBUCIO.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = BackofficeWsServiceI.SERVICE_NAME,
		serviceName = BackofficeWsServiceI.SERVICE_NAME + "Service",
		portName = BackofficeWsServiceI.SERVICE_NAME + "ServicePort",
		targetNamespace = BackofficeWsServiceI.NAMESPACE_URI)
@SOAPBinding(style = SOAPBinding.Style.RPC)
@WebContext(
		contextRoot = "/distribucio/ws",
		urlPattern = "/backoffice",
		secureWSDLAccess = false)
public class BackofficeWsServiceWs implements BackofficeWsServiceI {

	@Autowired
	private BackofficeWsService backofficeWsService = null;

	@Override
	public void comunicarAnotacionsPendents(List<AnotacioRegistreId> ids) {
		backofficeWsService.comunicarAnotacionsPendents(ids);
	}
}
