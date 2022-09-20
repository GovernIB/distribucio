/**
 * 
 */
package es.caib.distribucio.core.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.distribucio.core.api.service.SegonPlaService;

/**
 * Implementació de BustiaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class SegonPlaServiceBean implements SegonPlaService {

	@Autowired
	SegonPlaService delegate;

	@Override
	@RolesAllowed("tothom")
	public void enviarEmailsPendentsNoAgrupats() {
		delegate.enviarEmailsPendentsNoAgrupats();
	}

	@Override
	@RolesAllowed("tothom")
	public void enviarEmailsPendentsAgrupats() {
		delegate.enviarEmailsPendentsAgrupats();
	}

	@Override
	@RolesAllowed("tothom")
	public void aplicarReglesPendentsBackoffice() {
		delegate.aplicarReglesPendentsBackoffice();
	}
	
	@Override
	@RolesAllowed("tothom")
	public void tancarContenidorsArxiuPendents() {
		delegate.tancarContenidorsArxiuPendents();
	}

	@Override
	@RolesAllowed("tothom")
	public void guardarAnotacionsPendentsEnArxiu() {
		delegate.guardarAnotacionsPendentsEnArxiu();
	}

	@Override
	@RolesAllowed("tothom")
	public void enviarIdsAnotacionsPendentsBackoffice() {
		delegate.enviarIdsAnotacionsPendentsBackoffice();
		
	}

	@Override
	@RolesAllowed("tothom")
	public void addNewEntryToHistogram() {
		delegate.addNewEntryToHistogram();
	}

	@Override
	@RolesAllowed("tothom")
	public void calcularDadesHistoriques() {
		delegate.calcularDadesHistoriques();
	}
	
	@Override
	@RolesAllowed("tothom")
	public void esborrarDadesAntigesMonitorIntegracio() {
		delegate.esborrarDadesAntigesMonitorIntegracio();
	}

	@Override
	public void reintentarProcessamentBackoffice() {
		delegate.reintentarProcessamentBackoffice();
		
	}

	@Override
	public void actualitzarProcediments() {
		delegate.actualitzarProcediments();
	}

}
