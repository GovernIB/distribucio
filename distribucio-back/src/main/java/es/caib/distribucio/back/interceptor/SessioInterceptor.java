/**
 * 
 */
package es.caib.distribucio.back.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.caib.distribucio.logic.intf.service.ConfigService;
import es.caib.distribucio.logic.intf.service.EntitatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import es.caib.distribucio.back.helper.SessioHelper;
import es.caib.distribucio.logic.intf.service.AplicacioService;

/**
 * Interceptor per a les accions de context de sessió.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class SessioInterceptor implements AsyncHandlerInterceptor {

	@Autowired
	private AplicacioService aplicacioService;
    @Autowired private EntitatService entitatService;
    @Autowired private ConfigService configService;

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
        SessioHelper.processarAutenticacio(request, response, aplicacioService, entitatService, configService);
		return true;
	}

}
