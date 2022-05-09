/**
 * 
 */
package es.caib.distribucio.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.distribucio.core.api.dto.UsuariDto;
import es.caib.distribucio.core.api.service.AplicacioService;
import es.caib.distribucio.war.helper.RolHelper;

/**
 * Interceptor per controlar l'accés a funcionalitat desde el rol d'admininistrador o
 * administració (lectura).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AccesAdminInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	private AplicacioService aplicacioService;


	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (!RolHelper.isRolActualAdministrador(request) 
				//TODO 2- Permetre d'accés a la zona d'administració a DIS_ADMIN_LECTURA, no només a DIS_ADMIN
				 && !RolHelper.isRolActualAdminLectura(request) 
				) {
			UsuariDto usuariActual = aplicacioService.getUsuariActual();
			throw new SecurityException("Es necessari ser administrador per accedir a aquesta página. " +
					"L'usuari actual " + usuariActual.getCodi() + " no té el rol requerit.", null);
		}
		return true;
	}
	
}
