/**
 * 
 */
package es.caib.distribucio.core.ejb.ws;



import java.util.List;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.jboss.annotation.security.SecurityDomain;
import org.jboss.wsf.spi.annotation.WebContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.core.api.service.ws.backoffice.BackofficeWsService;
import es.caib.distribucio.core.helper.UsuariHelper;
import es.caib.distribucio.core.service.ws.backoffice.BackofficeWsServiceImpl;





/**
 * Implementació dels mètodes per al servei de bústies de DISTRIBUCIO.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "Backoffice",
		serviceName = "BackofficeService",
		portName = "BackofficeServicePort",
		targetNamespace = "http://www.caib.es/distribucio/ws/backoffice")
@WebContext(
		contextRoot = "/distribucio/ws",
		urlPattern = "/backoffice",
		authMethod = "WSBASIC",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
@RolesAllowed({"DIS_BSTWS"})
@SecurityDomain("seycon")
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class BackofficeWsServiceBean implements BackofficeWsService {

	@Autowired
	private BackofficeWsServiceImpl delegate;

	@Resource
	private SessionContext sessionContext;
	@Autowired
	private UsuariHelper usuariHelper;








	@Override
	public void comunicarAnotacionsPendents(List<AnotacioRegistreId> ids) {
		usuariHelper.generarUsuariAutenticatEjb(
				sessionContext,
				true);
		delegate.comunicarAnotacionsPendents(ids);
		
	}


}
