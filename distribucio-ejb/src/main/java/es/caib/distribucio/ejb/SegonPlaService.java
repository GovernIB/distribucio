/**
 * 
 */
package es.caib.distribucio.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;

/**
 * Implementació de BustiaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class SegonPlaService extends AbstractService<es.caib.distribucio.logic.intf.service.SegonPlaService> implements es.caib.distribucio.logic.intf.service.SegonPlaService {

	@Override
	@RolesAllowed("**")
	public void enviarEmailsPendentsNoAgrupats() {
		getDelegateService().enviarEmailsPendentsNoAgrupats();
	}

	@Override
	@RolesAllowed("**")
	public void enviarEmailsPendentsAgrupats() {
		getDelegateService().enviarEmailsPendentsAgrupats();
	}

	@Override
	@RolesAllowed("**")
	public void aplicarReglesPendentsBackoffice() {
		getDelegateService().aplicarReglesPendentsBackoffice();
	}

	@Override
	@RolesAllowed("**")
	public void tancarContenidorsArxiuPendents() {
		getDelegateService().tancarContenidorsArxiuPendents();
	}

	@Override
	@RolesAllowed("**")
	public void guardarAnotacionsPendentsEnArxiu() {
		getDelegateService().guardarAnotacionsPendentsEnArxiu();
	}

	@Override
	@RolesAllowed("**")
	public void enviarIdsAnotacionsPendentsBackoffice() {
		getDelegateService().enviarIdsAnotacionsPendentsBackoffice();
	}

	@Override
	@RolesAllowed("**")
	public void addNewEntryToHistogram() {
		getDelegateService().addNewEntryToHistogram();
	}

	@Override
	@RolesAllowed("**")
	public void calcularDadesHistoriques() {
		getDelegateService().calcularDadesHistoriques();
	}

	@Override
	@RolesAllowed("**")
	public void esborrarDadesAntigesMonitorIntegracio() {
		getDelegateService().esborrarDadesAntigesMonitorIntegracio();
	}

	@Override
	public void reintentarProcessamentBackoffice() {
		getDelegateService().reintentarProcessamentBackoffice();
	}

    @Override
	public void enviarEmailsAnotacionsErrorProcessament() {
		getDelegateService().enviarEmailsAnotacionsErrorProcessament();
	}

    @Override
	public void esborrarZipAccionsMassives() {
		getDelegateService().esborrarZipAccionsMassives();
	}

    @Override
	public void canviEstatComunicatAPendent() {
		getDelegateService().canviEstatComunicatAPendent();
	}

	@Override
	public void actualitzarProcediments() throws Exception {
		getDelegateService().actualitzarProcediments();
	}
	
	@Override
	public void actualitzarServeis() throws Exception {
		getDelegateService().actualitzarServeis();
	}

	@Override
	@RolesAllowed("**")
	public void restartSchedulledTasks(String taskCodi) {
		getDelegateService().restartSchedulledTasks(taskCodi);
	}

	@Override
	public void executeNextMassiveScheduledTask() throws Exception {
		getDelegateService().executeNextMassiveScheduledTask();
	}

}
