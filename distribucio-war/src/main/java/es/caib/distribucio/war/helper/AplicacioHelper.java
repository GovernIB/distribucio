/**
 * 
 */
package es.caib.distribucio.war.helper;

import javax.servlet.http.HttpServletRequest;

import es.caib.distribucio.core.api.service.AplicacioService;

/**
 * Utilitat per a gestionar accions de context d'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AplicacioHelper {

	public static final String APPLICATION_ATTRIBUTE_VERSIO_ACTUAL = "AplicacioHelper.versioActual";
	public static final String APPLICATION_ATTRIBUTE_VERSIO_DATA = "AplicacioHelper.versioData";

	public static void comprovarVersioActual(
			HttpServletRequest request,
			AplicacioService aplicacioService) {
		String versioActual = (String)request.getSession().getServletContext().getAttribute(APPLICATION_ATTRIBUTE_VERSIO_ACTUAL);
		if (versioActual == null) {
			versioActual = aplicacioService.getVersioActual();
			request.getSession().getServletContext().setAttribute(
					APPLICATION_ATTRIBUTE_VERSIO_ACTUAL,
					versioActual);
		}
	}

	public static void comprovarVersioData(
			HttpServletRequest request,
			AplicacioService aplicacioService) {
		String versioData = (String)request.getSession().getServletContext().getAttribute(APPLICATION_ATTRIBUTE_VERSIO_DATA);
		if (versioData == null) {
			versioData = aplicacioService.getVersioData();
			request.getSession().getServletContext().setAttribute(
					APPLICATION_ATTRIBUTE_VERSIO_DATA,
					versioData);
		}
	}

	public static String getVersioActual(HttpServletRequest request) {
		String versioActual = (String)request.getSession().getServletContext().getAttribute(
				APPLICATION_ATTRIBUTE_VERSIO_ACTUAL);
		return versioActual;
	}
	
	public static String getVersioData(HttpServletRequest request) {
		String versioData = (String)request.getSession().getServletContext().getAttribute(
				APPLICATION_ATTRIBUTE_VERSIO_DATA);
		return versioData;
	}

	
}
