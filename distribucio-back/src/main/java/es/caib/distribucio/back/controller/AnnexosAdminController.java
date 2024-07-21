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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.back.command.AnnexosFiltreCommand;
import es.caib.distribucio.back.helper.DatatablesHelper;
import es.caib.distribucio.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.back.helper.RequestSessionHelper;
import es.caib.distribucio.logic.intf.service.AnnexosService;
import es.caib.distribucio.logic.intf.service.ConfigService;

/**
 * Controlador per a la consulta d'arxius pels administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/annexosAdmin")
public class AnnexosAdminController extends BaseAdminController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "AnnexosAdminController.session.filtre";

	@Autowired
	private AnnexosService annexosService;
	@Autowired
	private ConfigService configService;


	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		getEntitatActualComprovantPermisAdminLectura(request);
		AnnexosFiltreCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute("concsvBaseUrl", configService.getConcsvBaseUrl());
		model.addAttribute(
				filtreCommand);
		return "annexosAdminList";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String annexFiltre(
			HttpServletRequest request,
			@Valid AnnexosFiltreCommand annexosFiltreCommand,
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
						annexosFiltreCommand);
			}
		}
		model.addAttribute("concsvBaseUrl", configService.getConcsvBaseUrl());
		return "redirect:annexosAdmin";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {		
		AnnexosFiltreCommand filtreCommand = getFiltreCommand(request);

		return DatatablesHelper.getDatatableResponse(
				request,
				annexosService.findAdmin(						
						AnnexosFiltreCommand.asDto(filtreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
	}

	@RequestMapping(value = "/{id}/guardarDefinitiu", method = RequestMethod.GET)
	public String guardarDefinitiu(
			HttpServletRequest request,
			@PathVariable Long id) {
		annexosService.guardarComADefinitiu(					 
				id);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../annexosAdmin",
				"annexos.accio.guardar.definitiu.ok");
	}
	
//	@RequestMapping(value = "/{id}/guardarDefinitiu", method = RequestMethod.GET)
//	public AjaxFormResponse guardarDefinitiu(
//			HttpServletRequest request,
//			@PathVariable Long id,
//			Model model) {
//		AjaxFormResponse response;		
//		
//		StringBuilder missatge = new StringBuilder();
//		boolean correcte = true;
//		
//		// Marca com a definitiu
//		if (correcte) {
//			try {
//				String rolActual = RolHelper.getRolActual(request);
//				annexosService.guardarComADefinitiu(					 
//						id);
//				missatge.append(getMessage(request, "annexos.accio.guardar.definitiu.ok"));	
//			} catch (Exception exception) {
//				correcte = false;
//				missatge.append(
//						getMessage(
//								request, 
//								"annexos.accio.guardar.definitiu",
//								new Object[] {ExceptionUtils.getRootCauseMessage(exception)}));
//			}
//		} else {
//			missatge.append(getMessage(request, "annexos.accio.guardar.definitiu.ko"));
//		}
//		
//		if (correcte) {
//			response = AjaxHelper.generarAjaxFormOk();
//			response.setMissatge(getMessage(request, missatge.toString()));
//		} else {
//			response = AjaxHelper.generarAjaxError(missatge.toString());
//		}
//		return response;
//		
//		
//		
//	}
	
//	@RequestMapping(value = "/{contingutId}/detall", method = RequestMethod.GET)
//	public String detall(
//			HttpServletRequest request,
//			@PathVariable Long contingutId,
//			Model model) {
//		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
//		
//		ContingutDto contingutDto = contingutService.findAmbIdAdmin(
//				entitatActual.getId(),
//				contingutId,
//				true);
//
//		model.addAttribute(
//				"contingut",
//				contingutDto);
//
//		switch (contingutDto.getTipus()) {
//		case BUSTIA:
//			model.addAttribute(
//					"bustia",
//					contingutDto);
//			return "bustiaAdminDetall";
//
//		case REGISTRE:
//			model.addAttribute(
//					"registre",
//					contingutDto);
//			
//			model.addAttribute(
//					"registreId",
//					contingutDto.getId());
//						
//			model.addAttribute(
//					"isContingutAdmin",
//					true);
//			
//			// Dades del procediment
//			String codiSia = ((RegistreDto)contingutDto).getProcedimentCodi();
//			if (codiSia != null) {
//				ProcedimentDto procediment = procedimentService.findByCodiSia(entitatActual.getId(), codiSia);
//				if (procediment == null) {
//					procediment = new ProcedimentDto();
//					procediment.setCodi(codiSia);
//					procediment.setCodiSia(codiSia);
//					procediment.setNom(getMessage(request, "registre.detalls.camp.procediment.no.trobat", new Object[] {codiSia}));
//				}
//				model.addAttribute("procedimentDades", procediment);				
//			}
//
//			return "registreDetall";
//		}
//
//		return null;
//	}
//
//	@RequestMapping(value = "/{contingutId}/log", method = RequestMethod.GET)
//	public String log(
//			HttpServletRequest request,
//			@PathVariable Long contingutId,
//			Model model) {
//		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
//		model.addAttribute(
//				"isPanelUser",
//				false);
//		model.addAttribute(
//				"logs",
//				contingutService.findLogsPerContingutAdmin(
//						entitatActual.getId(),
//						contingutId));
//		model.addAttribute(
//				"moviments",
//				contingutService.findMovimentsPerContingutAdmin(
//						entitatActual.getId(),
//						contingutId));
//		
//		model.addAttribute(
//				"contingut",
//				contingutService.findAmbIdAdmin(
//						entitatActual.getId(),
//						contingutId,
//						true));
//		return "contingutLog";
//	}
//	
//	
//	@RequestMapping(value = "/{contingutId}/log/{contingutLogId}/detalls", method = RequestMethod.GET)
//	@ResponseBody
//	public ContingutLogDetallsDto logDetalls(
//			HttpServletRequest request,
//			@PathVariable Long contingutId,
//			@PathVariable Long contingutLogId,
//			Model model) {
//		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
//		return contingutService.findLogDetallsPerContingutUser(
//				entitatActual.getId(),
//				contingutId,
//				contingutLogId);
//	}
//
//
//	@InitBinder
//	protected void initBinder(WebDataBinder binder) {
//		binder.registerCustomEditor(
//				Date.class,
//				new CustomDateEditor(
//						new SimpleDateFormat("dd/MM/yyyy"),
//						true));
//	}

	private AnnexosFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		AnnexosFiltreCommand filtreCommand = (AnnexosFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new AnnexosFiltreCommand();			
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

}
