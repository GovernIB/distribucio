package es.caib.distribucio.war.controller;


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

import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.service.ProcedimentService;
import es.caib.distribucio.war.command.ProcedimentFiltreCommand;
import es.caib.distribucio.war.helper.DatatablesHelper;
import es.caib.distribucio.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.war.helper.RequestSessionHelper;

/**
 * Controlador per al manteniment dels procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/procediment")
public class ProcedimentController extends BaseAdminController{
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "ProcedimentController.session.filtre";
	
	@Autowired
	private ProcedimentService procedimentService;
	
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request, 
			Model model) {
		
		EntitatDto entitat = getEntitatActualComprovantPermisAdminLectura(request);
		ProcedimentFiltreCommand procedimentFiltreCommand = getFiltreCommand(request);
		
		model.addAttribute("procedimentFiltreCommand", procedimentFiltreCommand);
		model.addAttribute("entitat", entitat);
		
		return "procedimentList";
	}	
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request, 
			@Valid ProcedimentFiltreCommand filtreCommand, 
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
		return "redirect:procediment";
		
	}
	
	
	@RequestMapping(value = "/actualitzar", method = RequestMethod.GET)
	public String actualitzacioPost(
			HttpServletRequest request, 
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		
		procedimentService.findAndUpdateProcediments(entitatActual.getId());
		
		return getModalControllerReturnValueSuccess(
                request,
                "redirect:.",
                "procediment.controller.actualitzar.ok");
		
	}
	
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		
		EntitatDto entitat = getEntitatActualComprovantPermisAdminLectura(request);
		ProcedimentFiltreCommand procedimentFiltreCommand = getFiltreCommand(request);
//		if (procedimentFiltreCommand.getUnitatOrganitzativa() != null && 
//				procedimentFiltreCommand.getUnitatOrganitzativa().getId() == null) {
//			procedimentFiltreCommand.setUnitatOrganitzativa(null);
//		}
		return DatatablesHelper.getDatatableResponse(
				request, 
				procedimentService.findAmbFiltre(
						entitat.getId(), 
						ProcedimentFiltreCommand.asDto(procedimentFiltreCommand), 
						DatatablesHelper.getPaginacioDtoFromRequest(request)), 
				"id");
	}
	
	private ProcedimentFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		ProcedimentFiltreCommand procedimentFiltreCommand = (ProcedimentFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(
				request, 
				SESSION_ATTRIBUTE_FILTRE);
		if (procedimentFiltreCommand == null) {
			procedimentFiltreCommand = new ProcedimentFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request, 
					SESSION_ATTRIBUTE_FILTRE, 
					procedimentFiltreCommand);
		}
		
		return procedimentFiltreCommand;
	}
	

}
