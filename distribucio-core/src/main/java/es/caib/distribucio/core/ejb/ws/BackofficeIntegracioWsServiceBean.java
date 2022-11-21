/**
 * 
 */
package es.caib.distribucio.core.ejb.ws;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.jboss.annotation.security.SecurityDomain;
import org.jboss.wsf.spi.annotation.WebContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.core.api.service.ws.backoffice.BackofficeIntegracioWsService;
import es.caib.distribucio.core.api.service.ws.backoffice.Estat;
import es.caib.distribucio.core.helper.UsuariHelper;
import es.caib.distribucio.core.service.ws.backoffice.BackofficeIntegracioWsServiceImpl;

/**
 * Implementació dels mètodes per al servei de bústies de DISTRIBUCIO. L'usuari que invoca
 * el servei ha de tenir el rol DIS_BACKWS.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "BackofficeIntegracio",
		serviceName = "BackofficeIntegracioService",
		portName = "BackofficeIntegracioServicePort",
		targetNamespace = "http://www.caib.es/distribucio/ws/backofficeIntegracio")
@WebContext(
		contextRoot = "/distribucio/ws",
		urlPattern = "/backofficeIntegracio",
		authMethod = "WSBASIC",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
@SecurityDomain("seycon")
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class BackofficeIntegracioWsServiceBean implements BackofficeIntegracioWsService {

	@Autowired
	private BackofficeIntegracioWsServiceImpl delegate;

	@Resource
	private SessionContext sessionContext;
	@Autowired
	private UsuariHelper usuariHelper;


	@Override
	public AnotacioRegistreEntrada consulta(
			AnotacioRegistreId id) {
		
		usuariHelper.generarUsuariAutenticatEjb(
				sessionContext,
				true);
		
		this.checkRole();
		
		return delegate.consulta(
				id);
		
	}

	@Override
	public void canviEstat(
			AnotacioRegistreId id,
			Estat estat,
			String observacions) {
		
		usuariHelper.generarUsuariAutenticatEjb(
				sessionContext,
				true);

		this.checkRole();
		
		delegate.canviEstat(id, estat, observacions);
	}

	private void checkRole() {
		if (!usuariHelper.hasRole("DIS_BACKWS")) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			throw new RuntimeException("L'usuari " + (auth != null ? auth.getName() : "(null)") + " no té el rol requerit DIS_BACKWS.");
		}		
	}

}
