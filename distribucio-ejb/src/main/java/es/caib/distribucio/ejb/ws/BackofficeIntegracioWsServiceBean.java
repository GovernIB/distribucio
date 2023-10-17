/**
 * 
 */
package es.caib.distribucio.ejb.ws;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.ws.api.annotation.WebContext;

import es.caib.distribucio.logic.intf.service.ws.backoffice.BackofficeIntegracioWsService;
import lombok.experimental.Delegate;

/**
 * Implementació dels mètodes per al servei de bústies de DISTRIBUCIO. L'usuari que invoca
 * el servei ha de tenir el rol DIS_BACKWS.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = BackofficeIntegracioWsService.SERVICE_NAME,
		serviceName = BackofficeIntegracioWsService.SERVICE_NAME + "Service",
		portName = BackofficeIntegracioWsService.SERVICE_NAME + "ServicePort",
		targetNamespace = BackofficeIntegracioWsService.NAMESPACE_URI)
@SOAPBinding(style = SOAPBinding.Style.RPC)
@WebContext(
		contextRoot = "/distribucio/ws",
		urlPattern = "/backofficeIntegracio"/*,
		authMethod = "WSBASIC",
		transportGuarantee = "NONE",
		secureWSDLAccess = false*/)
public class BackofficeIntegracioWsServiceBean implements BackofficeIntegracioWsService {

	@Delegate
	private BackofficeIntegracioWsService delegateService = null;

	protected void setDelegateService(BackofficeIntegracioWsService delegateService) {
		this.delegateService = delegateService;
	}

}
