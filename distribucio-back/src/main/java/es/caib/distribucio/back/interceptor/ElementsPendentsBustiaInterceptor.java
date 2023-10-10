/**
 * 
 */
package es.caib.distribucio.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import es.caib.distribucio.back.helper.ElementsPendentsBustiaHelper;
import es.caib.distribucio.logic.intf.service.BustiaService;

/**
 * Interceptor per a comptar els elements pendents de les bústies
 * de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ElementsPendentsBustiaInterceptor implements AsyncHandlerInterceptor {

	@Autowired
	private BustiaService bustiaService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		ElementsPendentsBustiaHelper.countElementsPendentsBusties(
				request,
				bustiaService);
		return true;
	}

}
