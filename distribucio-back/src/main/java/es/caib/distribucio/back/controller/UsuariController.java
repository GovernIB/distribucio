/**
 * 
 */
package es.caib.distribucio.back.controller;

import java.util.ArrayList;
import java.util.List;

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

	@RequestMapping(value = "/configuracio", method = RequestMethod.GET)
	public String getConfiguracio(
			HttpServletRequest request,
			Model model) {
		UsuariDto usuari = aplicacioService.getUsuariActual();
		EntitatDto entitatActual = this.getEntitatActual(request);
		BustiaDto bustiaPerDefecte = aplicacioService.getBustiaPerDefecte(usuari, entitatActual.getId());
		
		model.addAttribute(UsuariCommand.asCommand(usuari));
		model.addAttribute("rolsPerMostrar", this.getFiltraRolsPerMostrar(usuari.getRols()));
		model.addAttribute(
				"idiomaEnumOptions",
				EnumHelper.getOptionsForEnum(
						IdiomaEnumDto.class,
						"usuari.form.camp.idioma.enum."));
		if (bustiaPerDefecte != null)
			model.addAttribute("bustiaPerDefecte", bustiaPerDefecte.getId());
		return "usuariForm";
	}

	/** Filtra els rols per a que es mostrin només els que comencen per 'DIS_'*/
	private String[] getFiltraRolsPerMostrar(String[] rols) {
		List<String> rolsPerMostrar = new ArrayList<>();
		if (rols != null) {
			for (int i = 0; i < rols.length; i++) {
				if (rols[i].startsWith("DIS_")) {
					rolsPerMostrar.add(rols[i]);
				}
			}
		}
		return rolsPerMostrar.toArray(String[]::new);
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
