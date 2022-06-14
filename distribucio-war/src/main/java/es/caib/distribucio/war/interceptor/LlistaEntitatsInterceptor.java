/**
 * 
 */
package es.caib.distribucio.war.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.service.ConfigService;
import es.caib.distribucio.core.api.service.EntitatService;
import es.caib.distribucio.war.helper.ContingutEstaticHelper;
import es.caib.distribucio.war.helper.EntitatHelper;

/**
 * Interceptor per a gestionar la llista d'entitats a cada pàgina.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class LlistaEntitatsInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private ConfigService configService;


	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (!ContingutEstaticHelper.isContingutEstatic(request)) {
			EntitatHelper.findEntitatsAccessibles(
					request,
					entitatService);
			EntitatHelper.processarCanviEntitats(
					request,
					entitatService);
		}
		EntitatDto entitatDto = EntitatHelper.getEntitatActual(request);
		if (entitatDto != null) {
			configService.setEntitatPerPropietat(entitatDto);
		}		
		
		return true;
	}

}
