/**
 * 
 */
package es.caib.distribucio.war.controller;

import javax.servlet.http.HttpServletRequest;

import es.caib.distribucio.core.api.dto.EntitatDto;


/**
 * Controlador base que implementa funcionalitats comunes per
 * als controladors de les accions de l'administrador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseUserController extends BaseController {

	public EntitatDto getEntitatActualComprovantPermisos(
			HttpServletRequest request) {
		EntitatDto entitat = this.getEntitatActual(request);
		if (!entitat.isUsuariActualRead())
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a usuari");
		return entitat;
	}

}
