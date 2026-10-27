package es.caib.distribucio.back.controller;

import es.caib.distribucio.back.command.ServeiFiltreCommand;
import es.caib.distribucio.back.helper.DatatablesHelper;
import es.caib.distribucio.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.back.helper.RequestSessionHelper;
import es.caib.distribucio.logic.helper.PaginacioHelper;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.model.SiaResource;
import es.caib.distribucio.logic.intf.resourceservice.SiaResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador per al manteniment dels serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/sia")
public class SiaController extends BaseAdminController{
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "SiaController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "SiaController.session.seleccio";

	@Autowired private SiaResourceService siaResourceService;
    @Autowired private PaginacioHelper paginacioHelper;


    @RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request, 
			Model model) {
		
		EntitatDto entitat = getEntitatActualComprovantPermisAdminLectura(request);
		ServeiFiltreCommand serveiFiltreCommand = getFiltreCommand(request);
		
		model.addAttribute("serveiFiltreCommand", serveiFiltreCommand);
		model.addAttribute("entitat", entitat);
		
		return "siaList";
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
		return "redirect:sia";
		
	}
		
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		
//		EntitatDto entitat = getEntitatActualComprovantPermisAdminLectura(request);
		ServeiFiltreCommand serveiFiltreCommand = getFiltreCommand(request);

        List<String> filter = new ArrayList<>();

        if (serveiFiltreCommand.getCodiSia() != null) {
            filter.add("codiSia~'%" + serveiFiltreCommand.getCodiSia() +"%'");
        }
        if (serveiFiltreCommand.getNom() != null) {
            filter.add("nom~'%" + serveiFiltreCommand.getNom() +"%'");
        }
        if (serveiFiltreCommand.getUnitatOrganitzativa() != null) {
            filter.add("unitatOrganitzativa.id:" + serveiFiltreCommand.getUnitatOrganitzativa());
        }
        if (serveiFiltreCommand.getEstat() != null) {
            filter.add("estat:'" + serveiFiltreCommand.getEstat() +"'");
        }
        if (serveiFiltreCommand.getTipus() != null) {
            filter.add("tipus:'" + serveiFiltreCommand.getTipus() +"'");
        }
        if (serveiFiltreCommand.isNomesComu()) {
            filter.add("comu:true");
        }

        Page<SiaResource> page = siaResourceService.findPage(
                null,
                String.join(" AND ", filter),
                null,
                null,
                paginacioHelper.toSpringDataPageable(
                        DatatablesHelper.getPaginacioDtoFromRequest(request))
        );
		
		return DatatablesHelper.getDatatableResponse(
				request,
                paginacioHelper.toPaginaDto(page, SiaResource.class),
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

	private static final Logger logger = LoggerFactory.getLogger(SiaController.class);
}
