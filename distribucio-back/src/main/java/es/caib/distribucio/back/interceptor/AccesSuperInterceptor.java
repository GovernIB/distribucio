/**
 * 
 */
package es.caib.distribucio.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import es.caib.distribucio.back.helper.RolHelper;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.service.AplicacioService;

/**
 * Interceptor per controlar l'accés a funcionalitat desde el rol de superusuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AccesSuperInterceptor implements AsyncHandlerInterceptor {

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
