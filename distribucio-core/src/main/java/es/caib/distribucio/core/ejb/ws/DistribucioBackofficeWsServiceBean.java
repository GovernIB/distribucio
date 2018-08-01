/**
 * 
 */
package es.caib.distribucio.core.ejb.ws;

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

import es.caib.distribucio.core.api.registre.RegistreAnotacio;
import es.caib.distribucio.core.api.service.ws.DistribucioBackofficeResultatProces;
import es.caib.distribucio.core.api.service.ws.DistribucioBackofficeWsService;
import es.caib.distribucio.core.helper.UsuariHelper;

/**
 * Implementació dels mètodes per al servei de bústies de DISTRIBUCIO.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "DistribucioBackoffice",
		serviceName = "DistribucioBackofficeService",
		portName = "DistribucioBackofficeServicePort",
		targetNamespace = "http://www.caib.es/distribucio/ws/backoffice")
@WebContext(
		contextRoot = "/distribucio/ws",
		urlPattern = "/backoffice",
		authMethod = "WSBASIC",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
@RolesAllowed({"DIS_BACKWS"})
@SecurityDomain("seycon")
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class DistribucioBackofficeWsServiceBean implements DistribucioBackofficeWsService {

	@Resource
	private SessionContext sessionContext;
	@Autowired
	private UsuariHelper usuariHelper;



	@Override
	public DistribucioBackofficeResultatProces processarAnotacio(
			RegistreAnotacio registreEntrada) {
		usuariHelper.generarUsuariAutenticatEjb(
				sessionContext,
				true);
		return new DistribucioBackofficeResultatProces();
	}

}
