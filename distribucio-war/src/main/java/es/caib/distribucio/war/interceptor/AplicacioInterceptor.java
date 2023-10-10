/**
 * 
 */
package es.caib.distribucio.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import es.caib.distribucio.logic.intf.service.AplicacioService;
import es.caib.distribucio.war.helper.AplicacioHelper;

/**
 * Interceptor per a les accions de context d'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AplicacioInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private AplicacioService aplicacioService;



	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		AplicacioHelper.comprovarVersioActual(request, aplicacioService);
		AplicacioHelper.comprovarVersioData(request, aplicacioService);
		request.setAttribute(
				"requestLocale",
				RequestContextUtils.getLocale(request).getLanguage());
		return true;
	}

}
