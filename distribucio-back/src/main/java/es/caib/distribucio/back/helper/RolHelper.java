/**
 * 
 */
package es.caib.distribucio.back.helper;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.service.AplicacioService;

/**
 * Utilitat per a gestionar el canvi de rol de l'usuari actual.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RolHelper {

	private static final String REQUEST_PARAMETER_CANVI_ROL = "canviRol";
	private static final String SESSION_ATTRIBUTE_ROL_ACTUAL = "RolHelper.rol.actual";



	public static void processarCanviRols(
			HttpServletRequest request,
			AplicacioService aplicacioService) {
		String canviRol = request.getParameter(REQUEST_PARAMETER_CANVI_ROL);
		if (canviRol != null && canviRol.length() > 0) {
			LOGGER.trace("Processant canvi rol (rol=" + canviRol + ")");
			if (request.isUserInRole(canviRol)) {
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_ROL_ACTUAL,
						canviRol);
				aplicacioService.setRolUsuariActual(canviRol);
			}
		}
	}
	
	public static void setRolActualFromDb(HttpServletRequest request, AplicacioService aplicacioService) {
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		if (rolActual == null) {
			UsuariDto usuari = aplicacioService.getUsuariActual();
			if (usuari != null) {
				rolActual = aplicacioService.getUsuariActual().getRolActual();
			}
			
		}
		if (rolActual != null && !rolActual.isEmpty()) {
			request.getSession().setAttribute(
					SESSION_ATTRIBUTE_ROL_ACTUAL,
					rolActual);
		}
	}

	public static String getRolActual(HttpServletRequest request) {
		String rolActual = (String)request.getSession().getAttribute(
				SESSION_ATTRIBUTE_ROL_ACTUAL);
		List<String> rolsDisponibles = getRolsUsuariActual(request);
		if (rolActual == null || !rolsDisponibles.contains(rolActual)) {
			if (request.isUserInRole(BaseConfig.ROLE_USER) && rolsDisponibles.contains(BaseConfig.ROLE_USER)) {
				rolActual = BaseConfig.ROLE_USER;
			} else if (request.isUserInRole(BaseConfig.ROLE_ADMIN) && rolsDisponibles.contains(BaseConfig.ROLE_ADMIN)) {
				rolActual = BaseConfig.ROLE_ADMIN;
			} else if (request.isUserInRole(BaseConfig.ROLE_ADMIN_LECTURA) && rolsDisponibles.contains(BaseConfig.ROLE_ADMIN_LECTURA)) {
				rolActual = BaseConfig.ROLE_ADMIN_LECTURA;
			} else if (request.isUserInRole(BaseConfig.ROLE_SUPER) && rolsDisponibles.contains(BaseConfig.ROLE_SUPER)) {
				rolActual = BaseConfig.ROLE_SUPER;
			}
			if (rolActual != null) {
				request.getSession().setAttribute(
						SESSION_ATTRIBUTE_ROL_ACTUAL,
						rolActual);
			}
		}
		LOGGER.trace("Obtenint rol actual (rol=" + rolActual + ")");
		return rolActual;
	}

	public static boolean isRolActualSuperusuari(HttpServletRequest request) {
		return BaseConfig.ROLE_SUPER.equals(getRolActual(request));
	}
	public static boolean isRolActualAdministrador(HttpServletRequest request) {
		return BaseConfig.ROLE_ADMIN.equals(getRolActual(request));
	}
	public static boolean isRolActualAdminLectura(HttpServletRequest request) {
		return BaseConfig.ROLE_ADMIN_LECTURA.equals(getRolActual(request));
	}
	public static boolean isRolActualUsuari(HttpServletRequest request) {
		return BaseConfig.ROLE_USER.equals(getRolActual(request));
	}

	public static List<String> getRolsUsuariActual(HttpServletRequest request) {
		LOGGER.trace("Obtenint rols disponibles per a l'usuari actual");
		List<String> rols = new ArrayList<String>();
		if (request.isUserInRole(BaseConfig.ROLE_SUPER)) {
			rols.add(BaseConfig.ROLE_SUPER);
		}
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		if (entitatActual != null) {
			if (entitatActual.isUsuariActualAdministration() && request.isUserInRole(BaseConfig.ROLE_ADMIN)) {
				rols.add(BaseConfig.ROLE_ADMIN);
			}
			if (entitatActual.isUsuariActualAdminLectura () && request.isUserInRole(BaseConfig.ROLE_ADMIN_LECTURA)) {
				rols.add(BaseConfig.ROLE_ADMIN_LECTURA);
			}
			if (entitatActual.isUsuariActualRead() && request.isUserInRole(BaseConfig.ROLE_USER)) {
				rols.add(BaseConfig.ROLE_USER);
			}
		}
		return rols;
	}

	public static void esborrarRolActual(HttpServletRequest request) {
		request.getSession().removeAttribute(SESSION_ATTRIBUTE_ROL_ACTUAL);
	}

	public static String getRequestParameterCanviRol() {
		return REQUEST_PARAMETER_CANVI_ROL;
	}



	private static final Logger LOGGER = LoggerFactory.getLogger(RolHelper.class);

}
