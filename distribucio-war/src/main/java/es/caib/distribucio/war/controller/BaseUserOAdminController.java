/**
 * 
 */
package es.caib.distribucio.war.controller;

import javax.servlet.http.HttpServletRequest;

import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.war.helper.EntitatHelper;


/**
 * Controlador base que implementa funcionalitats comunes per
 * als controladors de les accions que son tant d'usuari com
 * d'administrador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseUserOAdminController extends BaseController {

	public EntitatDto getEntitatActualComprovantPermisos(
			HttpServletRequest request) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null)
			throw new SecurityException("No te cap entitat assignada");
		if (!entitat.isUsuariActualAdministration() && !entitat.isUsuariActualRead())
			throw new SecurityException("No te permisos per accedir a aquesta entitat com a administrador o usuari");
		return entitat;
	}

}
