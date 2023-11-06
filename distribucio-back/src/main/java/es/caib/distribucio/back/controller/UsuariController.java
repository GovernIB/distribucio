/**
 * 
 */
package es.caib.distribucio.back.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.distribucio.back.command.UsuariCommand;
import es.caib.distribucio.back.helper.EnumHelper;
import es.caib.distribucio.back.helper.SessioHelper;
import es.caib.distribucio.logic.intf.dto.BustiaDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.IdiomaEnumDto;
import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.service.AplicacioService;

/**
 * Controlador per al manteniment de regles.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/usuari")
public class UsuariController  extends BaseAdminController {

	@Autowired
	private AplicacioService aplicacioService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			Model model) {
		model.addAttribute(
				"reglaTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						ReglaTipusEnumDto.class,
						"regla.tipus.enum."));
		return "reglaList";
	}

	/*@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		SecurityContextHolder.clearContext();
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		for (Cookie c: request.getCookies()) {
			// Es sobreescriu el valor de cada cookie a NULL
			Cookie ck = new Cookie(c.getName(), null);
			ck.setPath(request.getContextPath());
			response.addCookie(ck);
		}
		return "redirect:/";
	}*/

	@RequestMapping(value = "/configuracio", method = RequestMethod.GET)
	public String getConfiguracio(
			HttpServletRequest request,
			Model model) {
		UsuariDto usuari = aplicacioService.getUsuariActual();
		EntitatDto entitatActual = this.getEntitatActual(request);
		BustiaDto bustiaPerDefecte = aplicacioService.getBustiaPerDefecte(usuari, entitatActual.getId());
		
		model.addAttribute(UsuariCommand.asCommand(usuari));
		model.addAttribute(
				"idiomaEnumOptions",
				EnumHelper.getOptionsForEnum(
						IdiomaEnumDto.class,
						"usuari.form.camp.idioma.enum."));
		if (bustiaPerDefecte != null)
			model.addAttribute("bustiaPerDefecte", bustiaPerDefecte.getId());
		return "usuariForm";
	}

	@RequestMapping(value = "/configuracio", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid UsuariCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			return "usuariForm";
		}
		EntitatDto entitatActual = getEntitatActual(request);
		UsuariDto usuari = aplicacioService.updateUsuariActual(UsuariCommand.asDto(command), entitatActual.getId());
		SessioHelper.setUsuariActual(request, usuari);
		
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/",
					"usuari.controller.modificat.ok");
	}

}
