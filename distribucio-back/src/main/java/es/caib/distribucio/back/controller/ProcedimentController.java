package es.caib.distribucio.back.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.back.command.ProcedimentFiltreCommand;
import es.caib.distribucio.back.helper.DatatablesHelper;
import es.caib.distribucio.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.back.helper.MissatgesHelper;
import es.caib.distribucio.back.helper.RequestSessionHelper;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentUpdateProgressDto;
import es.caib.distribucio.logic.intf.service.ProcedimentService;

/**
 * Controlador per al manteniment dels procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/procediment")
public class ProcedimentController extends BaseAdminController{
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "ProcedimentController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "ProcedimentController.session.seleccio";
	
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
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		
		EntitatDto entitat = getEntitatActualComprovantPermisAdminLectura(request);
		ProcedimentFiltreCommand procedimentFiltreCommand = getFiltreCommand(request);
		
		return DatatablesHelper.getDatatableResponse(
				request, 
				procedimentService.findAmbFiltre(
						entitat.getId(), 
						ProcedimentFiltreCommand.asDto(procedimentFiltreCommand), 
						DatatablesHelper.getPaginacioDtoFromRequest(request)), 
				"id", 
				SESSION_ATTRIBUTE_SELECCIO);
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
	
	/** Mètode per obrir la modal i iniciar o veure el progrés de l'actualització de procediments.
	 * 
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/actualitzar")
	public String actualitzar(
			HttpServletRequest request, 
			Model model) throws Exception {
		
		EntitatDto entitat = getEntitatActualComprovantPermisAdminLectura(request);
		
		model.addAttribute(
				"isUpdatingProcediments", 
				procedimentService.isUpdatingProcediments(entitat.getId()));
		
		return "procedimentUpdateForm";
	}
	
	@RequestMapping(value = "/actualitzar", method = RequestMethod.POST)
	public String actualitzarPost(
			HttpServletRequest request, 
			Model model) throws Exception {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);		
		try {
			procedimentService.findAndUpdateProcediments(entitatActual.getId());
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"procediment.controller.actualitzar.ok"));
		} catch (Exception e) {
			String errMsg = getMessage(
					request, 
					"procediment.controller.actualitzar.error", 
					new Object[] {e.getMessage()});
			logger.error(errMsg);
			MissatgesHelper.error(
					request, 
					errMsg);
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"procedimentUpdateForm",
				"procediment.controller.actualitzar.ok");
	}
	
	
	@RequestMapping(value = "/actualitzar/progres", method = RequestMethod.GET)
	@ResponseBody
	public ProcedimentUpdateProgressDto getProgresActualitzacio(
			HttpServletRequest request) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		
		return procedimentService.getProgresActualitzacio(entitatActual.getId());
	}
	
	
	private static final Logger logger = LoggerFactory.getLogger(ProcedimentController.class);
}
