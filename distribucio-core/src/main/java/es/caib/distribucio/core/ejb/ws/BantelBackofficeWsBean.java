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
import javax.xml.datatype.XMLGregorianCalendar;

import org.jboss.annotation.security.SecurityDomain;
import org.jboss.wsf.spi.annotation.WebContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.distribucio.core.api.service.bantel.ws.v2.BantelBackofficeWs;
import es.caib.distribucio.core.api.service.bantel.ws.v2.BantelBackofficeWsException;
import es.caib.distribucio.core.api.service.bantel.ws.v2.model.ReferenciaEntrada;
import es.caib.distribucio.core.api.service.bantel.ws.v2.model.ReferenciasEntrada;
import es.caib.distribucio.core.api.service.bantel.ws.v2.model.TramiteBTE;

/**
 * Implementació dels mètodes per al servei de backoffices Sistra de RIPEA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@WebService(
		name = "BackofficeFacade",
		serviceName = "BackofficeFacadeService",
		portName = "BackofficeFacade",
		targetNamespace = "urn:es:caib:bantel:ws:v2:services")
@WebContext(
		contextRoot = "/ripea/ws",
		urlPattern = "/backofficeFacade",
		authMethod = "WSBASIC",
		transportGuarantee = "NONE",
		secureWSDLAccess = false)
@RolesAllowed({"IPA_BSTWS"})
@SecurityDomain("seycon")
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class BantelBackofficeWsBean implements BantelBackofficeWs {

	@Resource
	private SessionContext sessionContext;
	@Autowired
	private BantelBackofficeWsImpl delegate;	
	
	@Override
	public TramiteBTE obtenerEntrada(ReferenciaEntrada numeroEntrada) throws BantelBackofficeWsException {
		return delegate.obtenerEntrada(numeroEntrada);
	}
	@Override
	public void establecerResultadoProceso(
			ReferenciaEntrada numeroEntrada, 
			String resultado,
			String resultadoProcesamiento) throws BantelBackofficeWsException {
		delegate.establecerResultadoProceso(numeroEntrada, resultado, resultadoProcesamiento);
	}
	@Override
	public ReferenciasEntrada obtenerNumerosEntradas(
			String identificadorProcedimiento, 
			String identificadorTramite,
			String procesada, 
			XMLGregorianCalendar desde, 
			XMLGregorianCalendar hasta) throws BantelBackofficeWsException {
		return delegate.obtenerNumerosEntradas(
				identificadorProcedimiento, 
				identificadorTramite, 
				procesada, 
				desde, 
				hasta);
	}
}
