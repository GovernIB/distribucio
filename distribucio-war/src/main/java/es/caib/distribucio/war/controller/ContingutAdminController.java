/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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

import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.ContingutLogDetallsDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.ProcedimentDto;
import es.caib.distribucio.core.api.dto.ProcedimentEstatEnumDto;
import es.caib.distribucio.core.api.dto.RegistreDto;
import es.caib.distribucio.core.api.service.ContingutService;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.war.command.ContingutFiltreCommand;
import es.caib.distribucio.war.command.ContingutFiltreCommand.ContenidorFiltreOpcionsEsborratEnum;
import es.caib.distribucio.war.helper.DatatablesHelper;
import es.caib.distribucio.war.helper.MissatgesHelper;
import es.caib.distribucio.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.war.helper.RequestSessionHelper;

/**
 * Controlador per a la consulta d'arxius pels administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/contingutAdmin")
public class ContingutAdminController extends BaseAdminController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "ContingutAdminController.session.filtre";

	@Autowired
	private ContingutService contingutService;
	@Autowired
	private RegistreService registreService;


	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		getEntitatActualComprovantPermisAdminLectura(request);
		ContingutFiltreCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute(
				filtreCommand);
		return "contingutAdminList";
	}
	@RequestMapping(method = RequestMethod.POST)
	public String expedientPost(
			HttpServletRequest request,
			@Valid ContingutFiltreCommand filtreCommand,
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

		return "redirect:contingutAdmin";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		ContingutFiltreCommand filtreCommand = getFiltreCommand(request);

		return DatatablesHelper.getDatatableResponse(
				request,
				contingutService.findAdmin(
						entitatActual.getId(),
						ContingutFiltreCommand.asDto(filtreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
	}

	@RequestMapping(value = "/{contingutId}/detall", method = RequestMethod.GET)
	public String detall(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		
		ContingutDto contingutDto = contingutService.findAmbIdAdmin(
				entitatActual.getId(),
				contingutId,
				true);

		model.addAttribute(
				"contingut",
				contingutDto);

		switch (contingutDto.getTipus()) {
		case BUSTIA:
			model.addAttribute(
					"bustia",
					contingutDto);			
			return "bustiaAdminDetall";

		case REGISTRE:
			model.addAttribute(
					"registre",
					contingutDto);
			
			model.addAttribute(
					"registreId",
					contingutDto.getId());
						
			model.addAttribute(
					"isContingutAdmin",
					true);
			
			// Dades del procediment
			String codiSia = ((RegistreDto)contingutDto).getProcedimentCodi();
			if (codiSia != null) {
				Map<String, ProcedimentEstatEnumDto> procedimentDades = new HashMap<String, ProcedimentEstatEnumDto>();
				// Descripció del procediment
				try {
					List<ProcedimentDto> procedimentDto = registreService.procedimentFindByCodiSia(entitatActual.getId(), codiSia);
					if (!procedimentDto.isEmpty()) {
						for(ProcedimentDto procediment : procedimentDto) {
							procedimentDades.put(procediment.getNom(), procediment.getEstat());
						}
					} else {
						String errMsg = getMessage(request, "registre.detalls.camp.procediment.no.trobat", new Object[] {codiSia});
						MissatgesHelper.warning(request, errMsg);
						procedimentDades.put("(" + errMsg + ")", null);
					}
				}catch(NullPointerException e) {
					String errMsg = getMessage(request, "registre.detalls.camp.procediment.error", new Object[] {codiSia, e.getMessage()});
					logger.error(errMsg, e);
					MissatgesHelper.warning(request, errMsg);
					procedimentDades.put("(" + errMsg + ")", null);
				}
				model.addAttribute("procedimentDades", procedimentDades);
			}

			return "registreDetall";
		}

		return null;
	}

	@RequestMapping(value = "/{contingutId}/log", method = RequestMethod.GET)
	public String log(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		model.addAttribute(
				"isPanelUser",
				false);
		model.addAttribute(
				"logs",
				contingutService.findLogsPerContingutAdmin(
						entitatActual.getId(),
						contingutId));
		model.addAttribute(
				"moviments",
				contingutService.findMovimentsPerContingutAdmin(
						entitatActual.getId(),
						contingutId));
		
		model.addAttribute(
				"contingut",
				contingutService.findAmbIdAdmin(
						entitatActual.getId(),
						contingutId,
						true));
		return "contingutLog";
	}
	
	
	@RequestMapping(value = "/{contingutId}/log/{contingutLogId}/detalls", method = RequestMethod.GET)
	@ResponseBody
	public ContingutLogDetallsDto logDetalls(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long contingutLogId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		return contingutService.findLogDetallsPerContingutUser(
				entitatActual.getId(),
				contingutId,
				contingutLogId);
	}


	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}

	private ContingutFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		ContingutFiltreCommand filtreCommand = (ContingutFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new ContingutFiltreCommand();
			filtreCommand.setOpcionsEsborrat(ContenidorFiltreOpcionsEsborratEnum.NOMES_NO_ESBORRATS);
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

	private static final Logger logger = LoggerFactory.getLogger(ContingutAdminController.class);
}
