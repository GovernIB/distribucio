/**
 * 
 */
package es.caib.distribucio.war.controller;

import javax.servlet.http.HttpServletRequest;

import es.caib.distribucio.logic.intf.dto.EntitatDto;


/**
 * Controlador base que implementa funcionalitats comunes per
 * als controladors de les accions de l'administrador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseAdminController extends BaseController {

	public EntitatDto getEntitatActualComprovantPermisAdmin(
			HttpServletRequest request) {
		EntitatDto entitat = this.getEntitatActual(request);
		if (!entitat.isUsuariActualAdministration())
			throw new SecurityException(getMessage(request, "entitat.actual.error.permis.admin"));
		return entitat;
	}
	
	public EntitatDto getEntitatActualComprovantPermisAdminLectura(
			HttpServletRequest request) {
		EntitatDto entitat = this.getEntitatActual(request);
		if (!entitat.isUsuariActualAdministration() && !entitat.isUsuariActualAdminLectura())
			throw new SecurityException(getMessage(request, "entitat.actual.error.permis.admin"));
		return entitat;
	}


}
