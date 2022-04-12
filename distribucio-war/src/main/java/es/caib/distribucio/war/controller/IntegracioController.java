/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.dto.IntegracioDto;
import es.caib.distribucio.core.api.dto.IntegracioEnumDto;
import es.caib.distribucio.core.api.dto.MonitorIntegracioDto;
import es.caib.distribucio.core.api.service.MonitorIntegracioService;
import es.caib.distribucio.war.helper.DatatablesHelper;
import es.caib.distribucio.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.war.helper.EnumHelper;
import es.caib.distribucio.war.helper.RequestSessionHelper;

/**
 * Controlador per a la consulta d'accions de les integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/integracio")
public class IntegracioController extends BaseUserController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "IntegracioController.session.filtre";
	
	@Autowired
	private MonitorIntegracioService monitorIntegracioService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		return getAmbCodi(request, null, model);
	}
	
	/** Consulta els diferents integracions i el número d'errors per integració. Si es
	 * passa un codi llavors el fixa en sessió pel filtre.
	 * @param request
	 * @param codi
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{codi}", method = RequestMethod.GET)
	public String getAmbCodi(
			HttpServletRequest request,
			@PathVariable String codi,
			Model model) {
		
		// Fa una llista de les diferents integracions i els errors actuals
		List<IntegracioDto> integracions = monitorIntegracioService.integracioFindAll();
		
		// Consulta el número d'errors per codi d'integracio
		Map<String, Integer> errors = monitorIntegracioService.countErrors();
		
		for (IntegracioDto integracio: integracions) {
			for (IntegracioEnumDto integracioEnum: IntegracioEnumDto.values()) {
				if (integracio.getCodi() == integracioEnum.name()) {
					integracio.setNom(
							EnumHelper.getOneOptionForEnum(
									IntegracioEnumDto.class,
									"integracio.list.pipella." + integracio.getCodi()).getText());
				}
			}
			if (errors.containsKey(integracio.getCodi())) {
				integracio.setNumErrors(errors.get(integracio.getCodi()).intValue());
			}
		}
		
		model.addAttribute(
				"integracions",
				integracions);
		if (codi != null) {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					codi);
		} else if (integracions.size() > 0) {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					integracions.get(0).getCodi());
		}
		model.addAttribute(
				"codiActual",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE));
		
		return "integracioList";
	}

	
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		String codi = (String)RequestSessionHelper.obtenirObjecteSessio(request,SESSION_ATTRIBUTE_FILTRE);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				monitorIntegracioService.findPaginat(
						DatatablesHelper.getPaginacioDtoFromRequest(request),						
						codi
				)				
		);
		return dtr;
	}

	@RequestMapping(value = "/{codi}/{id}", method = RequestMethod.GET)
	public String detall(
			HttpServletRequest request,
			@PathVariable String codi,
			@PathVariable Long id,
			Model model) {
		MonitorIntegracioDto integracio = monitorIntegracioService.findById(id);
		if (integracio != null) {
			model.addAttribute(
					"integracio",
					integracio);
			model.addAttribute(
					"codiActual", 
					codi);
			return "integracioDetall";
			
		} else {
			return getModalControllerReturnValueError(
					request,
					"redirect:../../integracio",
					"integracio.list.no.existeix");
		}
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}

}
