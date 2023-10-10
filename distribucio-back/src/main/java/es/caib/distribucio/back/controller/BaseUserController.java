/**
 * 
 */
package es.caib.distribucio.back.controller;

import javax.servlet.http.HttpServletRequest;

import es.caib.distribucio.logic.intf.dto.EntitatDto;


/**
 * Controlador base que implementa funcionalitats comunes per
 * als controladors de les accions de l'administrador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseUserController extends BaseController {

	public EntitatDto getEntitatActualComprovantPermisUsuari(
			HttpServletRequest request) {
		EntitatDto entitat = this.getEntitatActual(request);
		if (entitat.isUsuariActualRead() || entitat.isUsuariActualAdminLectura()) {
			return entitat;
		}else {
			throw new SecurityException(getMessage(request, "entitat.actual.error.permis.usuari"));
		}
	}
}
