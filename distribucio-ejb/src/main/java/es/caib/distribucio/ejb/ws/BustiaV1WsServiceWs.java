/**
 * 
 */
package es.caib.distribucio.ejb.ws;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.ws.api.annotation.WebContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.registre.RegistreAnotacio;
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
		urlPattern = "/v1/bustia",
		secureWSDLAccess = false)
//@RolesAllowed(BaseConfig.ROLE_BUSTIA_WS)
public class BustiaV1WsServiceWs extends AbstractServiceEjb<BustiaV1WsService> implements BustiaV1WsService {

	@Delegate
	private BustiaV1WsService delegateService = null;

	@Override
	public void enviarAnotacioRegistreEntrada(
			String entitat,
			String unitatAdministrativa,
			RegistreAnotacio registreEntrada) {
		checkRole();
		delegateService.enviarAnotacioRegistreEntrada(
				entitat,
				unitatAdministrativa,
				registreEntrada);
	}

	@Override
	public void enviarDocument(
			String entitat,
			String unitatAdministrativa,
			String referenciaDocument) {
		delegateService.enviarDocument(
				entitat,
				unitatAdministrativa,
				referenciaDocument);
	}

	@Override
	public void enviarExpedient(
			String entitat,
			String unitatAdministrativa,
			String referenciaExpedient) {
		delegateService.enviarExpedient(
				entitat,
				unitatAdministrativa,
				referenciaExpedient);
	}

	protected void setDelegateService(BustiaV1WsService delegateService) {
		this.delegateService = delegateService;
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
