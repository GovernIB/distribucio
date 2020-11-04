/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.ReglaDto;
import es.caib.distribucio.core.api.dto.ReglaTipusEnumDto;
import es.caib.distribucio.core.api.service.BackofficeService;
import es.caib.distribucio.core.api.service.BustiaService;
import es.caib.distribucio.core.api.service.ReglaService;
import es.caib.distribucio.core.api.service.UnitatOrganitzativaService;
import es.caib.distribucio.war.command.ReglaCommand;
import es.caib.distribucio.war.command.ReglaCommand.CreateUpdate;
import es.caib.distribucio.war.command.ReglaFiltreCommand;
import es.caib.distribucio.war.helper.DatatablesHelper;
import es.caib.distribucio.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.war.helper.EnumHelper;
import es.caib.distribucio.war.helper.RequestSessionHelper;

/**
 * Controlador per al manteniment de regles.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/regla")
public class ReglaController  extends BaseAdminController {

	@Autowired
	private ReglaService reglaService;
	@Autowired
	private BustiaService bustiaService;
	@Autowired
	private UnitatOrganitzativaService unitatService;
	@Autowired
	private BackofficeService backofficeService;

	private static final String SESSION_ATTRIBUTE_FILTRE = "ReglaController.session.filtre";

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		model.addAttribute(
				"reglaTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						ReglaTipusEnumDto.class,
						"regla.tipus.enum."));
		ReglaFiltreCommand reglaFiltreCommand = getFiltreCommand(request);
		
		model.addAttribute("reglaFiltreCommand", reglaFiltreCommand);
		
		model.addAttribute(
				"backoffices",
				backofficeService.findByEntitat(
						entitatActual.getId()));
		
		return "reglaList";
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ReglaFiltreCommand reglaFiltreCommand = getFiltreCommand(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				reglaService.findAmbFiltrePaginat(
						entitatActual.getId(),
						ReglaFiltreCommand.asDto(reglaFiltreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
		return dtr;
	}
	
	
	@RequestMapping(method = RequestMethod.POST)
	public String reglaPost(
			HttpServletRequest request,
			@Valid ReglaFiltreCommand filtreCommand,
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
		return "redirect:regla";
	}
	
	
	private ReglaFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		ReglaFiltreCommand reglaFiltreCommand = (ReglaFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (reglaFiltreCommand == null) {
			reglaFiltreCommand = new ReglaFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					reglaFiltreCommand);
		}
		return reglaFiltreCommand;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			Model model) {
		return get(
				request,
				null,
				model);
	}
	@RequestMapping(value = "/{reglaId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long reglaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ReglaDto regla = null;
		if (reglaId != null) {
			regla = reglaService.findOne(
					entitatActual.getId(),
					reglaId);
			
			if (regla.getUnitatOrganitzativa().getTipusTransicio() != null) {
				// setting last historicos to the unitat of this regla
				regla.setUnitatOrganitzativa(unitatService.getLastHistoricos(regla.getUnitatOrganitzativa()));
			
				// getting all the regles connected with old unitat excluding the
				// one you are currently in
				List<ReglaDto> reglesOfOldUnitat = reglaService.findByEntitatAndUnitatCodi(
						entitatActual.getId(),
						regla.getUnitatOrganitzativa().getCodi());
				List<ReglaDto> reglesOfOldUnitatWithoutCurrent = new ArrayList<ReglaDto>();
				for (ReglaDto reglaI : reglesOfOldUnitat) {
					if (!reglaI.getId().equals(regla.getId())) {
						reglesOfOldUnitatWithoutCurrent.add(reglaI);
					}
				}
				model.addAttribute("reglesOfOldUnitatWithoutCurrent", reglesOfOldUnitatWithoutCurrent);
			}
			model.addAttribute(regla);	
		}
		
		ReglaCommand command = null;
		if (regla != null)
			command = ReglaCommand.asCommand(regla);
		else
			command = new ReglaCommand();
		
		model.addAttribute(command);
		emplenarModelFormulari(
				request,
				model);
		return "reglaForm";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Validated(CreateUpdate.class) ReglaCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			emplenarModelFormulari(
					request,
					model);
			return "reglaForm";
		}
		if (command.getId() != null) {
			reglaService.update(
					entitatActual.getId(),
					ReglaCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:regla",
					"regla.controller.modificada.ok");
		} else {
			reglaService.create(
					entitatActual.getId(),
					ReglaCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:regla",
					"regla.controller.creada.ok");
		}
	}

	@RequestMapping(value = "/{reglaId}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long reglaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		reglaService.updateActiva(
				entitatActual.getId(),
				reglaId,
				true);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../regla",
				"regla.controller.activada.ok");
	}
	@RequestMapping(value = "/{reglaId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long reglaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		reglaService.updateActiva(
				entitatActual.getId(),
				reglaId,
				false);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../regla",
				"regla.controller.desactivada.ok");
	}

	@RequestMapping(value = "/{reglaId}/up", method = RequestMethod.GET)
	public String up(
			HttpServletRequest request,
			@PathVariable Long reglaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		reglaService.moveUp(
				entitatActual.getId(),
				reglaId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../regla",
				null);
	}
	@RequestMapping(value = "/{reglaId}/down", method = RequestMethod.GET)
	public String down(
			HttpServletRequest request,
			@PathVariable Long reglaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		reglaService.moveDown(
				entitatActual.getId(),
				reglaId);
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../regla",
				null);
	} 
	@RequestMapping(value = "/{reglaId}/move/{posicio}", method = RequestMethod.GET)
	public String move(
			HttpServletRequest request,
			@PathVariable Long reglaId,
			@PathVariable int posicio) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		reglaService.moveTo(
				entitatActual.getId(),
				reglaId,
				posicio);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../regla",
				null);
	}
	@RequestMapping(value = "/{reglaId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long reglaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		try {
			reglaService.delete(
					entitatActual.getId(),
					reglaId);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../regla",
					"regla.controller.esborrada.ok");
		} catch (RuntimeException ve) {
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../bustiaAdmin",
					"regla.controller.esborrada.error.validacio",
					new Object[] {ve.getMessage()});			
		}
	}



	private void emplenarModelFormulari(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(
				"reglaTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						ReglaTipusEnumDto.class,
						"regla.tipus.enum."));
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"busties",
			bustiaService.findActivesAmbEntitat(
						entitatActual.getId()));
		
		model.addAttribute(
				"backoffices",
				backofficeService.findByEntitat(
						entitatActual.getId()));
	}

}
