/**
 * 
 */
package es.caib.distribucio.back.helper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import es.caib.distribucio.logic.intf.dto.UsuariDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.service.EntitatService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Utilitat per a gestionar les entitats de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatHelper {

	private static final String REQUEST_PARAMETER_CANVI_ENTITAT = "canviEntitat";
	private static final String REQUEST_ATTRIBUTE_ENTITATS = "EntitatHelper.entitats";
	private static final String SESSION_ATTRIBUTE_ENTITAT_ACTUAL = "EntitatHelper.entitatActual";



	public static List<EntitatDto> findEntitatsAccessibles(
			HttpServletRequest request) {
		return findEntitatsAccessibles(request, null);
	}
	@SuppressWarnings("unchecked")
	public static List<EntitatDto> findEntitatsAccessibles(
			HttpServletRequest request,
			EntitatService entitatService) {
		List<EntitatDto> entitats = (List<EntitatDto>)request.getAttribute(
				REQUEST_ATTRIBUTE_ENTITATS);
		if (entitats == null && entitatService != null) {
			entitats = entitatService.findAccessiblesUsuariActual();
			request.setAttribute(REQUEST_ATTRIBUTE_ENTITATS, entitats);
		}
		if (entitats == null) {
			LOGGER.warn("L'usuari " + request.getUserPrincipal().getName() + " no te accés a cap entitat");
		}
		return entitats;
	}
	public static void processarCanviEntitats(
			HttpServletRequest request,
			EntitatService entitatService) {
		String canviEntitat = request.getParameter(REQUEST_PARAMETER_CANVI_ENTITAT);
		if (canviEntitat != null && canviEntitat.length() > 0) {
			LOGGER.debug("Processant canvi entitat (id=" + canviEntitat + ")");
			try {
				Long canviEntitatId = Long.valueOf(canviEntitat);
				List<EntitatDto> entitats = findEntitatsAccessibles(request, entitatService);
				for (EntitatDto entitat: entitats) {
					if (canviEntitatId.equals(entitat.getId())) {
						canviEntitatActual(request, entitat);
					}
				}
			} catch (NumberFormatException ignored) {
			}
		}
	}

	public static EntitatDto getEntitatActual(
			HttpServletRequest request) {
		return getEntitatActual(request, null);
	}
	public static EntitatDto getEntitatActual(
			HttpServletRequest request,
			EntitatService entitatService) {
		EntitatDto entitatActual = (EntitatDto)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ENTITAT_ACTUAL);
		if (entitatActual == null) {
			List<EntitatDto> entitats = findEntitatsAccessibles(request, entitatService);
            if (entitats != null && entitats.size() > 0) {
                UsuariDto usuariActual = (UsuariDto)request.getSession().getAttribute(SessioHelper.SESSION_ATTRIBUTE_USUARI_ACTUAL);
                if (usuariActual != null && usuariActual.getEntitatPerDefecteId() != null && entitatService != null) {
                    entitatActual = entitatService.findById(usuariActual.getEntitatPerDefecteId());
                    // en cas que s'hagin eliminat els permisos sobre la entitat per defecte, l'esborram
                    EntitatDto finalEntitatActual = entitatActual;
                    if (entitats.stream().noneMatch(e -> Objects.equals(e.getId(), finalEntitatActual.getId()) )) {
                        usuariActual.setEntitatPerDefecteId(null);
//                        entitatService.removeEntitatPerDefecteUsuari(usuariActual.getCodi());
                        entitatActual = entitats.get(0);
                    }
                } else {
                    entitatActual = entitats.get(0);
                }
                canviEntitatActual(request, entitatActual);
            }
		}
		return entitatActual;
	}

	public static String getRequestParameterCanviEntitat() {
		return REQUEST_PARAMETER_CANVI_ENTITAT;
	}



	private static void canviEntitatActual(
			HttpServletRequest request,
			EntitatDto entitatActual) {
		request.getSession().setAttribute(
				SESSION_ATTRIBUTE_ENTITAT_ACTUAL,
				entitatActual);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(EntitatHelper.class);

}
