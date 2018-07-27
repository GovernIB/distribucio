/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.BustiaDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.UnitatOrganitzativaDto;
import es.caib.ripea.core.api.registre.RegistreProcesEstatEnum;
import es.caib.ripea.core.api.service.BustiaService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.UnitatOrganitzativaService;
import es.caib.ripea.war.command.AnotacioRegistreFiltreCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per a la consulta d'arxius pels administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/anotacionsRegistre")
public class AnotacioRegistreController extends BaseAdminController {

	private static final String SESSION_ATTRIBUTE_ANOTACIO_FILTRE = "ContingutAdminController.session.anotacio.filtre";

	@Autowired
	private ContingutService contingutService;
	@Autowired
	private UnitatOrganitzativaService unitatOrganitzativaService;
	@Autowired
	private BustiaService bustiaService;

	@RequestMapping(method = RequestMethod.GET)
	public String anotacionsRegistre(
			HttpServletRequest request,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		AnotacioRegistreFiltreCommand filtreCommand = getAnotacioRegistreFiltreCommand(request);
		model.addAttribute(
				filtreCommand);
		return "anotacionsRegistreList";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String anotacionsRegistrePost(
			HttpServletRequest request,
			@Valid AnotacioRegistreFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model) {
		if (!bindingResult.hasErrors()) {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_ANOTACIO_FILTRE,
					filtreCommand);
		}
		return "redirect:anotacionsRegistre";
	}
	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse anotacionsDatatable(
			HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		AnotacioRegistreFiltreCommand filtreCommand = getAnotacioRegistreFiltreCommand(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				contingutService.findAnotacionsRegistre(
						entitatActual.getId(),
						AnotacioRegistreFiltreCommand.asDto(filtreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}
	
	@RequestMapping(value = "/ajaxBustia/{bustiaId}", method = RequestMethod.GET)
	@ResponseBody
	public BustiaDto getByCodi(
			HttpServletRequest request,
			@PathVariable String bustiaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return bustiaService.findById(entitatActual.getId(), Long.parseLong(bustiaId));
	}

	@RequestMapping(value = "/ajaxBusties/{unitatCodi}/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<BustiaDto> get(
			HttpServletRequest request,
			@PathVariable String unitatCodi,
			@PathVariable String text,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		UnitatOrganitzativaDto unitatOrganitzativa = null;
		
		if (unitatCodi != null && !"null".equalsIgnoreCase(unitatCodi))
			unitatOrganitzativa = unitatOrganitzativaService.findByCodi(unitatCodi);
		
		if ("undefined".equalsIgnoreCase(text) || "null".equalsIgnoreCase(text))
			text = "";
		
		List<BustiaDto> bustiesFinals = new ArrayList<BustiaDto>();
		
		if (unitatOrganitzativa != null) {
			bustiesFinals = bustiaService.findAmbUnitatCodiAdmin(entitatActual.getId(), unitatCodi);
			
			if (text != null && bustiesFinals != null && !bustiesFinals.isEmpty()) {
				List<BustiaDto> bustiesFiltrades = new ArrayList<BustiaDto>();
				text = text.toUpperCase();
				for (BustiaDto bustia: bustiesFinals) {
					if (bustia.getNom().matches("(?i:.*" + text + ".*)"))
						bustiesFiltrades.add(bustia);
				}
				bustiesFinals = bustiesFiltrades;
			}
		}
		
		return bustiesFinals;
	}

	private AnotacioRegistreFiltreCommand getAnotacioRegistreFiltreCommand(
			HttpServletRequest request) {
		AnotacioRegistreFiltreCommand filtreCommand = (AnotacioRegistreFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ANOTACIO_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new AnotacioRegistreFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_ANOTACIO_FILTRE,
					filtreCommand);
			filtreCommand.setEstat(RegistreProcesEstatEnum.ERROR);
		}
		return filtreCommand;
	}
}
