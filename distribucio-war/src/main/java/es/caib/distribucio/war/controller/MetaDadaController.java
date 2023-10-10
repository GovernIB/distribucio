/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.logic.intf.dto.DominiDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.MetaDadaDto;
import es.caib.distribucio.logic.intf.dto.MetaDadaTipusEnumDto;
import es.caib.distribucio.logic.intf.service.DominiService;
import es.caib.distribucio.logic.intf.service.MetaDadaService;
import es.caib.distribucio.war.command.MetaDadaCommand;
import es.caib.distribucio.war.helper.DatatablesHelper;
import es.caib.distribucio.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.war.helper.ExceptionHelper;
/**
 * Controlador per al manteniment de les meta-dades
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/metaDada")
public class MetaDadaController extends BaseAdminController {

	@Autowired
	private MetaDadaService metaDadaService;
	@Autowired
	private DominiService dominiService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		getEntitatActualComprovantPermisAdminLectura(request);
		return "metaDadaList";
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				metaDadaService.findByEntitatPaginat(
						entitatActual.getId(),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
		return dtr;
	}
	
	@RequestMapping(value = "/{metaDadaId}/move/{posicio}", method = RequestMethod.GET)
	public String move(
			HttpServletRequest request,
			@PathVariable Long metaDadaId,
			@PathVariable int posicio) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		metaDadaService.moveTo(
				entitatActual.getId(),
				metaDadaId,
				posicio);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:metaDada",
				null);
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			Model model) {
		return get(request, null, model);
	}
	@RequestMapping(value = "/{metaDadaId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long metaDadaId,
			Model model) {
		Long entitatActualId = getEntitatActualComprovantPermisAdmin(request).getId();
		MetaDadaDto metaDada = null;
		if (metaDadaId != null)
			metaDada = metaDadaService.findById(
					entitatActualId,
					metaDadaId);
		MetaDadaCommand command = null;
		if (metaDada != null) {
			command = MetaDadaCommand.asCommand(metaDada);
			if (metaDada.getTipus().equals(MetaDadaTipusEnumDto.DOMINI))
				model.addAttribute("selectedMetaDada", metaDada.getCodi());
		} else {
			command = new MetaDadaCommand();
		}
		command.setEntitatId(entitatActualId);
		model.addAttribute(command);
		return "metaDadaForm";
	}
	@RequestMapping(method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid MetaDadaCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		if (bindingResult.hasErrors()) {
			return "metaDadaForm";
		}

		if (command.getId() != null) {
			metaDadaService.update(
					entitatActual.getId(),
					MetaDadaCommand.asDto(command));
			
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaDada",
					"metadada.controller.modificat.ok");
		} else {
			metaDadaService.create(
					entitatActual.getId(),
					MetaDadaCommand.asDto(command));
			
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:metaDada",
					"metadada.controller.creat.ok");
		}
	}

	@RequestMapping(value = "/{metaDadaId}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long metaDadaId) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		
		metaDadaService.updateActiva(
				entitatActual.getId(),
				metaDadaId,
				true);
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDada",
				"metadada.controller.activada.ok");
	}
	
	@RequestMapping(value = "/{metaDadaId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long metaDadaId) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		
		metaDadaService.updateActiva(
				entitatActual.getId(),
				metaDadaId,
				false);
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../metaDada",
				"metadada.controller.desactivada.ok");
	}

	@RequestMapping(value = "/{metaDadaId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long metaDadaId) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		
		try {
			metaDadaService.delete(
					entitatActual.getId(),
					metaDadaId);
			
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../metaDada",
					"metadada.controller.esborrat.ok");
		} catch (Exception e) {
			logger.error("Error al esborrar metadada", e);
			if (ExceptionHelper.getRootCauseOrItself(e) instanceof DataIntegrityViolationException) {
				return getAjaxControllerReturnValueError(
						request,
						"redirect:../../esborrat",
						"metadada.controller.esborrar.error.fk");
			} else {
				return getAjaxControllerReturnValueErrorMessage(
						request,
						"redirect:../../metaDada",
						ExceptionHelper.getRootCauseOrItself(e).getMessage());
			}
		}
	}
	
	
	@RequestMapping(value = "/domini", method = RequestMethod.GET)
	@ResponseBody
	public List<DominiDto> getDominis(HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		List<DominiDto> dominis = dominiService.findByEntitat(entitatActual.getId());
		return dominis;
	}
	
	@RequestMapping(value = "/metaDadaPermisLectura/domini", method = RequestMethod.GET)
	@ResponseBody
	public List<DominiDto> getDominisEntitatPermisLectura(HttpServletRequest request){
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		List<DominiDto> dominis = dominiService.findByEntitat(entitatActual.getId());
		return dominis;
	}

	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	    binder.registerCustomEditor(
	    		BigDecimal.class,
	    		new CustomNumberEditor(
	    				BigDecimal.class,
	    				NumberFormat.getInstance(new Locale("es","ES")),
	    				true));
	    binder.registerCustomEditor(
	    		Double.class,
	    		new CustomNumberEditor(
	    				Double.class,
	    				NumberFormat.getInstance(new Locale("es","ES")),
	    				true));
	}
	
	private static final Logger logger = LoggerFactory.getLogger(MetaDadaController.class);
}
