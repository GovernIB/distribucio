/**
 * 
 */
package es.caib.distribucio.ws.bustia;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.ws.api.annotation.WebContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.registre.RegistreAnotacio;
import es.caib.distribucio.logic.intf.service.ws.bustia.BustiaV1WsService;

/**
 * Implementació dels mètodes per al servei de bústies de DISTRIBUCIO.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = BustiaV1WsServiceI.SERVICE_NAME,
		serviceName = BustiaV1WsServiceI.SERVICE_NAME + "Service",
		portName = BustiaV1WsServiceI.SERVICE_NAME + "ServicePort",
		targetNamespace = BustiaV1WsServiceI.NAMESPACE_URI)
@SOAPBinding(style = SOAPBinding.Style.RPC)
@WebContext(
		contextRoot = "/distribucio/ws",
		urlPattern = "/v1/bustia",
		secureWSDLAccess = false)
public class BustiaV1WsServiceWs implements BustiaV1WsService {

	@EJB(name="BustiaV1WsServiceEjb")
	private BustiaV1WsService bustiaService = null;

	@Override
	@WebMethod
	public void enviarAnotacioRegistreEntrada(
			String entitat,
			String unitatAdministrativa,
			RegistreAnotacio registreEntrada) {
		checkRole();
		bustiaService.enviarAnotacioRegistreEntrada(
				entitat,
				unitatAdministrativa,
				registreEntrada);
	}

	@Override
	@WebMethod
	public void enviarDocument(
			String entitat,
			String unitatAdministrativa,
			String referenciaDocument) {
		bustiaService.enviarDocument(
				entitat,
				unitatAdministrativa,
				referenciaDocument);
	}

	@Override
	@WebMethod
	public void enviarExpedient(
			String entitat,
			String unitatAdministrativa,
			String referenciaExpedient) {
		bustiaService.enviarExpedient(
				entitat,
				unitatAdministrativa,
				referenciaExpedient);
	}

	/** Comprova que que l'usuari autenticat té el rol DIS_BSTWS.
	 * 
	 */
	private void checkRole() {
		boolean hasRole = false;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			for (GrantedAuthority ga : auth.getAuthorities())
				if (ga.getAuthority().equals(BaseConfig.ROLE_BUSTIA_WS) 
						|| ga.getAuthority().equals(BaseConfig.ROLE_BUSTIA_WS)) {
					hasRole = true;
					break;
				}
		}
		if (!hasRole) {
			System.err.println("L'usuari " + (auth != null ? auth.getName() : " no està autenticat i ") + " no té el rol " + BaseConfig.ROLE_BUSTIA_WS);
		}
	}
}
