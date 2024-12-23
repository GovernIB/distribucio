package es.caib.distribucio.back.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.back.command.ServeiFiltreCommand;
import es.caib.distribucio.back.helper.DatatablesHelper;
import es.caib.distribucio.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.back.helper.MissatgesHelper;
import es.caib.distribucio.back.helper.RequestSessionHelper;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.service.ServeiService;

/**
 * Controlador per al manteniment dels serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/servei")
public class ServeiController extends BaseAdminController{
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "ServeiController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "ServeiController.session.seleccio";
	
	@Autowired
	private ServeiService serveiService;
	
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request, 
			Model model) {
		
		EntitatDto entitat = getEntitatActualComprovantPermisAdminLectura(request);
		ServeiFiltreCommand serveiFiltreCommand = getFiltreCommand(request);
		
		model.addAttribute("serveiFiltreCommand", serveiFiltreCommand);
		model.addAttribute("entitat", entitat);
		
		return "serveiList";
	}	
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request, 
			@Valid ServeiFiltreCommand filtreCommand, 
			BindingResult bindingResult, 
			@RequestParam(value = "accio", required = false) String accio, 
			Model model) {
		
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE,
						filtreCommand);
			}
		}
		return "redirect:servei";
		
	}
	
	
	@RequestMapping(value = "/actualitzar")
	public String actualitzar(
			HttpServletRequest request, 
			Model model) throws Exception {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);		
		try {
			serveiService.findAndUpdateServeis(entitatActual.getId());
			
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"servei.controller.actualitzar.ok"));
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							"servei.controller.actualitzar.error", 
							new Object[] {e.getMessage()}));
		}
		return "redirect:/servei";
	}
	
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		
		EntitatDto entitat = getEntitatActualComprovantPermisAdminLectura(request);
		ServeiFiltreCommand serveiFiltreCommand = getFiltreCommand(request);
		
		return DatatablesHelper.getDatatableResponse(
				request, 
				serveiService.findAmbFiltre(
						entitat.getId(), 
						ServeiFiltreCommand.asDto(serveiFiltreCommand), 
						DatatablesHelper.getPaginacioDtoFromRequest(request)), 
				"id", 
				SESSION_ATTRIBUTE_SELECCIO);
	}
	
	private ServeiFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		ServeiFiltreCommand serveiFiltreCommand = (ServeiFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(
				request, 
				SESSION_ATTRIBUTE_FILTRE);
		if (serveiFiltreCommand == null) {
			serveiFiltreCommand = new ServeiFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request, 
					SESSION_ATTRIBUTE_FILTRE, 
					serveiFiltreCommand);
		}
		
		return serveiFiltreCommand;
	}
	

}