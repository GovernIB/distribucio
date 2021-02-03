/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.service.EntitatService;
import es.caib.distribucio.war.command.EntitatCommand;
import es.caib.distribucio.war.helper.DatatablesHelper;
import es.caib.distribucio.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.war.helper.EntitatHelper;

/**
 * Controlador per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/entitat")
public class EntitatController extends BaseController {

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private ServletContext servletContext; 

	@RequestMapping(method = RequestMethod.GET)
	public String get() {
		return "entitatList";
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				entitatService.findPaginat(
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(Model model) {
		return get(null, model);
	}
	@RequestMapping(value = "/{entitatId}", method = RequestMethod.GET)
	public String get(
			@PathVariable Long entitatId,
			Model model) {
		EntitatDto entitat = null;
		if (entitatId != null)
			entitat = entitatService.findById(entitatId);
		if (entitat != null)
			model.addAttribute(EntitatCommand.asCommand(entitat));
		else
			model.addAttribute(new EntitatCommand());
		return "entitatForm";
	}
	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid EntitatCommand command,
			BindingResult bindingResult) throws IOException {
		if (bindingResult.hasErrors()) {
			return "entitatForm";
		}
		if (command.getId() != null) {
			entitatService.update(EntitatCommand.asDto(command));
 			return getModalControllerReturnValueSuccess(
					request,
					"redirect:entitat",
					"entitat.controller.modificada.ok");
		} else {
			entitatService.create(EntitatCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:entitat",
					"entitat.controller.creada.ok");
		}
	}

	@RequestMapping(value = "/{entitatId}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long entitatId) {
		entitatService.updateActiva(entitatId, true);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../entitat",
				"entitat.controller.activada.ok");
	}
	@RequestMapping(value = "/{entitatId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long entitatId) {
		entitatService.updateActiva(entitatId, false);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../entitat",
				"entitat.controller.desactivada.ok");
	}

	@RequestMapping(value = "/{entitatId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long entitatId) {
		entitatService.delete(entitatId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../entitat",
				"entitat.controller.esborrada.ok");
	}
	
	@RequestMapping(value = "/logo", method = RequestMethod.GET)
	public String getEntitatLogoCap(
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		
		if (entitatActual != null) {
			if (entitatActual.getLogoCapBytes() != null) {
				writeFileToResponse(
						"Logo_cap.png",
						entitatActual.getLogoCapBytes(),
						response);
			} else {
				try {
					File path = new File(servletContext.getRealPath("/") + "/img/govern-logo.png");
					writeFileToResponse(
							"Logo_cap.png", 
							Files.readAllBytes(path.toPath()), 
							response);
				} catch (Exception ex) {
					logger.debug("Error al obtenir el logo de la capçalera", ex);
				}
			}
		}
		return null;
	}

	private static final Logger logger = LoggerFactory.getLogger(EntitatController.class);
}
