/**
 * 
 */
package es.caib.distribucio.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.service.AplicacioService;
import es.caib.distribucio.war.helper.RolHelper;

/**
 * Interceptor per controlar l'accés a funcionalitat desde el rol de superusuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AccesSuperInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	private AplicacioService aplicacioService;


	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		if (!RolHelper.isRolActualSuperusuari(request))
			throw new SecurityException("Es necessari ser superadministrador per accedir a aquesta página. " +
					"L'usuari actual " + usuariActual.getCodi() + " no té el rol requerit.", null);
		
		return true;
	}
	
}
