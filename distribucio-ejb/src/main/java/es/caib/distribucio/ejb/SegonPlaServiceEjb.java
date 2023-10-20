/**
 * 
 */
package es.caib.distribucio.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.SegonPlaService;
import lombok.experimental.Delegate;

/**
 * Implementació de BustiaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class SegonPlaServiceEjb extends AbstractServiceEjb<SegonPlaService> implements SegonPlaService {

	@Delegate
	private SegonPlaService delegateService = null;

	@Override
	@RolesAllowed("**")
	public void enviarEmailsPendentsNoAgrupats() {
		delegateService.enviarEmailsPendentsNoAgrupats();
	}

	@Override
	@RolesAllowed("**")
	public void enviarEmailsPendentsAgrupats() {
		delegateService.enviarEmailsPendentsAgrupats();
	}

	@Override
	@RolesAllowed("**")
	public void aplicarReglesPendentsBackoffice() {
		delegateService.aplicarReglesPendentsBackoffice();
	}
	
	@Override
	@RolesAllowed("**")
	public void tancarContenidorsArxiuPendents() {
		delegateService.tancarContenidorsArxiuPendents();
	}

	@Override
	@RolesAllowed("**")
	public void guardarAnotacionsPendentsEnArxiu() {
		delegateService.guardarAnotacionsPendentsEnArxiu();
	}

	@Override
	@RolesAllowed("**")
	public void enviarIdsAnotacionsPendentsBackoffice() {
		delegateService.enviarIdsAnotacionsPendentsBackoffice();
		
	}

	@Override
	@RolesAllowed("**")
	public void addNewEntryToHistogram() {
		delegateService.addNewEntryToHistogram();
	}

	@Override
	@RolesAllowed("**")
	public void calcularDadesHistoriques() {
		delegateService.calcularDadesHistoriques();
	}

	@Override
	@RolesAllowed("**")
	public void esborrarDadesAntigesMonitorIntegracio() {
		delegateService.esborrarDadesAntigesMonitorIntegracio();
	}

	@Override
	public void reintentarProcessamentBackoffice() {
		delegateService.reintentarProcessamentBackoffice();
		
	}

	@Override
	public void actualitzarProcediments() throws Exception {
		delegateService.actualitzarProcediments();
	}

	protected void setDelegateService(SegonPlaService delegateService) {
		this.delegateService = delegateService;
	}

}
