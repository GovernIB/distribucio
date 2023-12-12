/**
 * 
 */
package es.caib.distribucio.ws.integracio;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.ws.api.annotation.WebContext;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.logic.intf.service.ws.backoffice.BackofficeIntegracioWsService;
import es.caib.distribucio.logic.intf.service.ws.backoffice.Estat;

/**
 * Implementació dels mètodes per al servei de bústies de DISTRIBUCIO. L'usuari que invoca
 * el servei ha de tenir el rol DIS_BACKWS.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = BackofficeIntegracioWsServiceI.SERVICE_NAME,
		serviceName = BackofficeIntegracioWsServiceI.SERVICE_NAME + "Service",
		portName = BackofficeIntegracioWsServiceI.SERVICE_NAME + "ServicePort",
		targetNamespace = BackofficeIntegracioWsServiceI.NAMESPACE_URI)
@SOAPBinding(style = SOAPBinding.Style.RPC)
@WebContext(
		contextRoot = "/distribucio/ws",
		urlPattern = "/backofficeIntegracio",
		secureWSDLAccess = false)
//@RolesAllowed(BaseConfig.ROLE_BACKOFFICE_WS)
public class BackofficeIntegracioWsServiceWs implements BackofficeIntegracioWsServiceI {

	@Autowired
	private BackofficeIntegracioWsService backofficeIntegracioService = null;

	@Override
	@WebMethod
	public AnotacioRegistreEntrada consulta(AnotacioRegistreId id) {
		return backofficeIntegracioService.consulta(id);
	}

	@Override
	@WebMethod
	public void canviEstat(
			AnotacioRegistreId id,
			Estat estat,
			String observacions) {
		backofficeIntegracioService.canviEstat(id, estat, observacions);
	}

}
