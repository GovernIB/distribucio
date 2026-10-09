/**
 * 
 */
package es.caib.distribucio.back.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import es.caib.distribucio.back.helper.AjaxHelper;
import es.caib.distribucio.back.helper.EntitatHelper;
import es.caib.distribucio.back.helper.ModalHelper;
import es.caib.distribucio.back.helper.RolHelper;
import es.caib.distribucio.back.helper.SessioHelper;
import es.caib.distribucio.logic.intf.config.PropertyConfig;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.InterficieUsuariEnumDto;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.service.AplicacioService;

/**
 * Controlador amb utilitats per a l'aplicació DISTRIBUCIO.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class DistribucioController {

	/** Paràmetre de petició per a forçar una interfície concreta (veure {@link #getInterficieEfectiva}). */
	public static final String REQUEST_PARAMETER_INTERFICIE = "interficie";

	@Autowired
	private AplicacioService aplicacioService;

	@RequestMapping(path = { "/", "/index" }, method = RequestMethod.GET)
	public String get(HttpServletRequest request) {
		if (InterficieUsuariEnumDto.REACT.equals(getInterficieEfectiva(request))) {
			return "redirect:/reactapp/";
		}
		if (RolHelper.isRolActualSuperusuari(request)) {
			return "redirect:integracio";
		} else {
			EntitatDto entitat = EntitatHelper.getEntitatActual(request);
			if (entitat == null)
				return "redirect:unauthorized";
			if (RolHelper.isRolActualAdministrador(request) || RolHelper.isRolActualAdminLectura(request)) {
				return "redirect:registreAdmin";
			} else if (RolHelper.isRolActualUsuari(request)) {
				return "redirect:registreUser";
			} else {
				return "index";
			}
		}
	}

	/**
	 * Interfície amb la que s'ha d'entrar a l'aplicació, per ordre de prioritat:
	 * <ol>
	 * <li>el paràmetre {@value #REQUEST_PARAMETER_INTERFICIE} de la petició, que la interfície
	 * actual afegeix als seus enllaços cap a l'arrel (canvi de rol o d'entitat, logotip...) per
	 * no fer saltar l'usuari a l'altra interfície;</li>
	 * <li>la interfície triada per l'usuari al seu perfil;</li>
	 * <li>la propietat {@code es.caib.distribucio.interface.defecte};</li>
	 * <li>{@link InterficieUsuariEnumDto#REACT} si res del anterior no té un valor vàlid.</li>
	 * </ol>
	 */
	private InterficieUsuariEnumDto getInterficieEfectiva(HttpServletRequest request) {
		InterficieUsuariEnumDto interficie = toInterficie(request.getParameter(REQUEST_PARAMETER_INTERFICIE));
		if (interficie == null) {
			interficie = getInterficieUsuariActual(request);
		}
		if (interficie == null) {
			interficie = toInterficie(aplicacioService.propertyFindByNom(PropertyConfig.INTERFACE_DEFECTE));
		}
		return interficie != null ? interficie : InterficieUsuariEnumDto.REACT;
	}

	/**
	 * Interfície triada per l'usuari al seu perfil. Es consulta al servei i no a la sessió perquè
	 * la interfície REACT també pot modificar el perfil, i la còpia de la sessió quedaria
	 * desfasada; si la consulta falla (p. ex. un usuari sense fitxa a dis_usuari) es cau cap a la
	 * còpia de la sessió per no impedir l'entrada a l'aplicació.
	 */
	private InterficieUsuariEnumDto getInterficieUsuariActual(HttpServletRequest request) {
		try {
			UsuariDto usuariActual = aplicacioService.getUsuariActual();
			if (usuariActual != null) {
				return usuariActual.getInterficieUsuari();
			}
		} catch (Exception ex) {
			logger.warn("No s'ha pogut consultar l'usuari actual per a decidir la interfície", ex);
		}
		UsuariDto usuariSessio = SessioHelper.getUsuariActual(request);
		return usuariSessio != null ? usuariSessio.getInterficieUsuari() : null;
	}

	/** Converteix el text a interfície, o null si és buit o no és cap valor conegut. */
	private InterficieUsuariEnumDto toInterficie(String valor) {
		if (valor == null || valor.trim().isEmpty()) {
			return null;
		}
		try {
			return InterficieUsuariEnumDto.valueOf(valor.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			logger.warn("Valor d'interfície d'usuari desconegut: " + valor);
			return null;
		}
	}

	@RequestMapping(value = "/unauthorized", method = RequestMethod.GET)
	public String unauthorized(
			HttpServletRequest request,
			Model model) {
		EntitatHelper.getEntitatActual(request);
		return "unauthorized";
	}

	@RequestMapping(value = ModalHelper.ACCIO_MODAL_TANCAR, method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void modalTancar() {
	}
	@RequestMapping(value = AjaxHelper.ACCIO_AJAX_OK, method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void ajaxOk() {
	}
	@RequestMapping(value = "/missatges", method = RequestMethod.GET)
	public String get() {
		return "util/missatges";
	}

	@RequestMapping(value = "/desenv/usuariActual", method = RequestMethod.GET)
	@ResponseBody
	public UsuariDto desenvUsuariActual() {
		return aplicacioService.getUsuariActual();
	}

	// PER LLEVAR
	@RequestMapping(value = "/util/modalTancar", method = RequestMethod.GET)
	public String utilModalTancar() {
		return "util/modalTancar";
	}
	@RequestMapping(value = "/util/ajaxOk", method = RequestMethod.GET)
	public String utilAjaxOk() {
		return "util/ajaxOk";
	}
	@RequestMapping(value = "/util/alertes", method = RequestMethod.GET)
	public String utilAlertes() {
		return "util/missatges";
	}
	// /PER LLEVAR

	/*@RequestMapping(value = "/error")
	public String error(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(
				"errorObject",
				new ErrorObject(request));
		return "util/error";
	}*/

	private static final Logger logger = LoggerFactory.getLogger(DistribucioController.class);

	public static class ErrorObject {
		Integer statusCode;
		Throwable throwable;
		String exceptionMessage;
		String requestUri;
		String message;
		public ErrorObject(HttpServletRequest request) {
			statusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
			throwable = (Throwable)request.getAttribute("javax.servlet.error.exception");
			exceptionMessage = getExceptionMessage(throwable, statusCode);
			requestUri = (String)request.getAttribute("javax.servlet.error.request_uri");
			if (requestUri == null) 
				requestUri = "Desconeguda";
			message = 
					"Retornat codi d'error " + statusCode + " "
					+ "per al recurs " + requestUri + " "
					+ "amb el missatge: " + exceptionMessage;
		}
		public Integer getStatusCode() {
			return statusCode;
		}
		public Throwable getThrowable() {
			return throwable;
		}
		public String getThrowableClassName() {
			return throwable.getClass().getName();
		}
		public String getExceptionMessage() {
			return exceptionMessage;
		}
		public String getRequestUri() {
			return requestUri;
		}
		public String getMessage() {
			return message;
		}
		public String getStackTrace() {
			return ExceptionUtils.getStackTrace(throwable);
		}
		public String getFullStackTrace() {
			return ExceptionUtils.getFullStackTrace(throwable);
		}
		public String getRootCauseMessage() {
			return ExceptionUtils.getRootCauseMessage(throwable);
		}
		private String getExceptionMessage(Throwable throwable, Integer statusCode) {
			if (throwable != null) {
				Throwable rootCause = ExceptionUtils.getRootCause(throwable);
				if (rootCause != null)
					return rootCause.getMessage();
				else
					return throwable.getMessage();
			} else {
				HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
				return httpStatus.getReasonPhrase();
			}
		}
	}

}
