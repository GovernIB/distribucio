/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.dto.IntegracioDiagnosticDto;
import es.caib.distribucio.core.api.dto.IntegracioDto;
import es.caib.distribucio.core.api.dto.IntegracioEnumDto;
import es.caib.distribucio.core.api.dto.MonitorIntegracioDto;
import es.caib.distribucio.core.api.dto.UsuariDto;
import es.caib.distribucio.core.api.service.AplicacioService;
import es.caib.distribucio.core.api.service.ConfigService;
import es.caib.distribucio.core.api.service.MonitorIntegracioService;
import es.caib.distribucio.war.command.IntegracioFiltreCommand;
import es.caib.distribucio.war.helper.DatatablesHelper;
import es.caib.distribucio.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.war.helper.EnumHelper;
import es.caib.distribucio.war.helper.MissatgesHelper;
import es.caib.distribucio.war.helper.RequestSessionHelper;

/**
 * Controlador per a la consulta d'accions de les integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/integracio")
public class IntegracioController extends BaseAdminController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "IntegracioController.session.filtre";
	
	@Autowired
	private MonitorIntegracioService monitorIntegracioService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private AplicacioService aplicacioService;
	
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
		
		IntegracioFiltreCommand filtreCommand = getFiltreCommand(request);
				
		String numeroHoresPropietat = configService.getTempsErrorsMonitorIntegracio();
		if (numeroHoresPropietat == null) {
			numeroHoresPropietat = "48";
		}
		model.addAttribute("numeroHoresPropietat", numeroHoresPropietat);
		
		// Fa una llista de les diferents integracions i els errors actuals
		List<IntegracioDto> integracions = this.getIntegracionsIErrors(numeroHoresPropietat);
		
		model.addAttribute(
				"integracions",
				integracions);
		if (codi != null) {
			filtreCommand.setCodi(codi);
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		} else if (integracions.size() > 0) {
			filtreCommand.setCodi(integracions.get(0).getCodi());
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		model.addAttribute(filtreCommand);
		model.addAttribute(
				"codiActual",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE));
		
		return "integracioList";
	}
	
	@RequestMapping(value = "/{codi}", method = RequestMethod.POST)
	public String getAmbCodiPost(
			HttpServletRequest request, 
			@PathVariable String codi,
			@Valid IntegracioFiltreCommand integracioFiltreCommand, 
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
						integracioFiltreCommand);
				
			}
		}
		return "redirect:/integracio/" + codi;
	}

	
	private IntegracioFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		IntegracioFiltreCommand filtreCommand = new IntegracioFiltreCommand();
		try {
			filtreCommand = (IntegracioFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE);
		}catch (Exception e) {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		
		if (filtreCommand == null) {
			filtreCommand = new IntegracioFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

	/** Mètode per consultar les integracions i els errors.*/
	@ResponseBody
	@RequestMapping(value = "integracions", method = RequestMethod.GET)
	public List<IntegracioDto> getIntegracionsIErrors(String numeroHoresPropietat) {
		List<IntegracioDto> integracions = monitorIntegracioService.integracioFindAll();
		if (numeroHoresPropietat == null) {
			numeroHoresPropietat = configService.getTempsErrorsMonitorIntegracio();
		}
		int numeroHores = Integer.parseInt(numeroHoresPropietat != null ? numeroHoresPropietat : "48");
		
		// Consulta el número d'errors per codi d'integracio
		Map<String, Integer> errors = monitorIntegracioService.countErrors(numeroHores);
		
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
		return integracions;
	}
	

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {		
		IntegracioFiltreCommand filtreCommand = getFiltreCommand(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				monitorIntegracioService.findPaginat(
						DatatablesHelper.getPaginacioDtoFromRequest(request),	
						IntegracioFiltreCommand.asDto(filtreCommand)
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
	
	@RequestMapping(value = "/diagnostic", method = RequestMethod.GET)
	public String diagnostic(
			HttpServletRequest request,
			Model model) {		

		List<IntegracioDto> integracions = monitorIntegracioService.findPerDiagnostic();
		model.addAttribute("integracions", integracions);
		
		return "integracioDiagnostic";
	}

	
	@RequestMapping(value = "/diagnosticAjax/{codiIntegracio}", method = RequestMethod.GET)
	public @ResponseBody IntegracioDiagnosticDto diagnosticAjax(
			HttpServletRequest request,
			@PathVariable String codiIntegracio,
			Model model) {
		
		/**
		 * TODO: Daniel, no m'ha donat temps d'acabar aquesta tasca. 
		 * Veuràs que la modal amb la resposta surt quan ja ha fet totes les consultes, 
		 * l'idea era que surti el llistat i vagi fent 'check' a mesura que fa les consultes. 
		 * Tampoc he pogut comprovar que les consultes siguin les bones per aquesta tasca.
		 * 
		 * */
		
		/**
		 * Gràcies per tota la paciència que has tingut amb jo. He après molt i he treballat molt a gust.
		 * Això és mèrit teu
		 * 
		 * */
		UsuariDto usuari = aplicacioService.getUsuariActual();		

		return 		monitorIntegracioService.diagnostic(codiIntegracio, usuari);

	}
	
	@ResponseBody
	@RequestMapping(value = "/{codi}/esborrar", method = RequestMethod.GET)
	public String esborrar(
			HttpServletRequest request,
			@PathVariable String codi,
			Model model) {
		int n = 0;
		try {
			n = monitorIntegracioService.delete(codi);			
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"integracio.esborrar.success",
							new Object[] {n, codi}));
		} catch (Exception e) {
			String errMsg = getMessage(
					request,
					"integracio.esborrar.error",
					new Object[] {codi, e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
		}
		
		return "redirect:/integracio/" + codi;
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}

	private static final Logger logger = LoggerFactory.getLogger(IntegracioController.class);
}
