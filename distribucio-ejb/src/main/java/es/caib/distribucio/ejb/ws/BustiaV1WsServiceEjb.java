/**
 * 
 */
package es.caib.distribucio.ejb.ws;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.registre.RegistreAnotacio;
import es.caib.distribucio.logic.intf.service.ws.bustia.BustiaV1WsService;
import lombok.experimental.Delegate;

/**
 * Implementació de BustiaV1WsService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * Aquest EJB és utilitzat per l'EJB que defineix el servei web homònim.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@RolesAllowed({ BaseConfig.ROLE_BUSTIA_WS })
public class BustiaV1WsServiceEjb extends AbstractServiceEjb<BustiaV1WsService> implements BustiaV1WsService {

	@Delegate
	private BustiaV1WsService delegateService = null;

	@Override
	public void enviarAnotacioRegistreEntrada(
			String entitat,
			String unitatAdministrativa,
			RegistreAnotacio registreEntrada) {
		propagateEjbAuthenticationToSpringSecurity(BaseConfig.ROLE_BUSTIA_WS);
		delegateService.enviarAnotacioRegistreEntrada(entitat, unitatAdministrativa, registreEntrada);
	}

	@Override
	public void enviarDocument(
			String entitat,
			String unitatAdministrativa,
			String referenciaDocument) {
		propagateEjbAuthenticationToSpringSecurity(BaseConfig.ROLE_BUSTIA_WS);
		delegateService.enviarDocument(entitat, unitatAdministrativa, referenciaDocument);
	}

	@Override
	public void enviarExpedient(
			String entitat,
			String unitatAdministrativa,
			String referenciaExpedient) {
		propagateEjbAuthenticationToSpringSecurity(BaseConfig.ROLE_BUSTIA_WS);
		delegateService.enviarExpedient(entitat, unitatAdministrativa, referenciaExpedient);
	}

	@Override
	protected void setDelegateService(BustiaV1WsService delegateService) {
		this.delegateService = delegateService;
	}

}
