/**
 * 
 */
package es.caib.distribucio.war.helper;

import javax.servlet.http.HttpServletRequest;

import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.service.BustiaService;

/**
 * Utilitat per a contar els elements pendents de les bústies
 * de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ElementsPendentsBustiaHelper {

	private static final String REQUEST_PARAMETER_PENDENTS_BUSTIA_COUNT = "ElementsPendentsBustiatHelper.countElementsPendentsBustia";



	public static Long countElementsPendentsBusties(
			HttpServletRequest request,
			BustiaService bustiaService) {
		Long count = (Long)request.getAttribute(REQUEST_PARAMETER_PENDENTS_BUSTIA_COUNT);
		if (count == null && !RequestHelper.isError(request) && bustiaService != null && RolHelper.isRolActualUsuari(request)) {
			EntitatDto entitat = EntitatHelper.getEntitatActual(request);
			count = new Long(bustiaService.contingutPendentBustiesAllCount(entitat.getId()));
			request.setAttribute(REQUEST_PARAMETER_PENDENTS_BUSTIA_COUNT, count);
		}
		return count;
	}

	public static Long countElementsPendentsBusties(HttpServletRequest request) {
		return (Long)request.getAttribute(REQUEST_PARAMETER_PENDENTS_BUSTIA_COUNT);
	}

}
