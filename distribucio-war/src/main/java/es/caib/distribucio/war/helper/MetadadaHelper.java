package es.caib.distribucio.war.helper;

import javax.servlet.http.HttpServletRequest;

import es.caib.distribucio.logic.intf.service.AplicacioService;

/**
 * 
 * Utilitat per controlar si l'ús de les metadades està permés.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
public class MetadadaHelper {

	private static final String SESSION_METADADES = "MetadadaHelper.isMetadadesActives";
	
	public static Boolean isMetadadesActives(HttpServletRequest request) {
		return (Boolean)request.getSession().getAttribute(SESSION_METADADES);
	}
	
	public static void setMetadadesActives(
			HttpServletRequest request,
			AplicacioService aplicacioService) {
		boolean isMetadadesActives = Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.distribucio.permetre.metadades.registre"));
		request.getSession().setAttribute(
				SESSION_METADADES,
				isMetadadesActives);
	}
	
}
