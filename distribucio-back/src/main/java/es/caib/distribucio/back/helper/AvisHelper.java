/**
 * 
 */
package es.caib.distribucio.back.helper;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import es.caib.distribucio.logic.intf.dto.AvisDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.service.AvisService;

/**
 * Utilitat per obtenir els avisos de sessió..
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AvisHelper {

	private static final String REQUEST_PARAMETER_AVISOS = "AvisHelper.findAvisos";


	@SuppressWarnings("unchecked")
	public static void findAvisos(
			HttpServletRequest request, 
			AvisService avisService) {
		
		List<AvisDto> avisos = (List<AvisDto>) request.getAttribute(REQUEST_PARAMETER_AVISOS);
		if (avisos == null && !RequestHelper.isError(request) && avisService != null) {
            avisos = avisService.findActive();
            if (!RolHelper.isRolActualSuperusuari(request)) {
                EntitatDto entitat = EntitatHelper.getEntitatActual(request);
                avisos = avisos.stream()
                        .filter((avis)->avis.getEntitatId() == null || avis.getEntitatId().equals(entitat.getId()))
                        .collect(Collectors.toList());
            }
			request.setAttribute(REQUEST_PARAMETER_AVISOS, avisos);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<AvisDto> getAvisos(
			HttpServletRequest request) {
		return (List<AvisDto>) request.getAttribute(REQUEST_PARAMETER_AVISOS);
	}
	

}
