/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.logic.intf.dto.BustiaDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.RegistreDto;
import es.caib.distribucio.logic.intf.dto.UsuariPermisDto;
import es.caib.distribucio.logic.intf.service.AplicacioService;
import es.caib.distribucio.logic.intf.service.BustiaService;
import es.caib.distribucio.logic.intf.service.RegistreService;
import es.caib.distribucio.war.command.RegistreAssignarCommand;

/**
 * Controlador per assignar registres a usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/registreUser")
public class RegistreUserAssignarController extends BaseUserController {

	@Autowired
	private BustiaService bustiaService;
	@Autowired
	private RegistreService registreService;
	@Autowired
	private AplicacioService aplicacioService;
	
	@RequestMapping(value = "/assignar/{registreId}", method = RequestMethod.GET)
	public String registreAssignarGet(
			HttpServletRequest request,
			@PathVariable Long registreId,
			Model model) {
		try {
			if (!isPermesAssignarAnotacions()) {
				throw new SecurityException("Es necessari activar la propietat 'es.caib.distribucio.assignar.anotacions' per accedir a aquesta página.", null);
			}
			emplenarModelAssignar(request, registreId, model);
			RegistreAssignarCommand command = new RegistreAssignarCommand();
			model.addAttribute(command);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return getModalControllerReturnValueErrorNoKey(
					request,
					"redirect:/registreUser/registre/" + registreId,
					e.getMessage());
		}
		return "registreUserAssignar";
	}

	@RequestMapping(value = "/assignar/{registreId}", method = RequestMethod.POST)
	public String bustiaClassificarPost(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@Valid RegistreAssignarCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			emplenarModelAssignar(request, registreId, model);
			return "registreUserAssignar";
		}
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
			registreService.assignar(
					entitatActual.getId(), 
					registreId, 
					command.getUsuariCodi(), 
					command.getComentari());
		} catch (Exception e) {
			logger.error("Error assignant l'anotació", e);
			return getModalControllerReturnValueError(
					request,
					"redirect:/registreUser/assignar/" + registreId,
					"registre.user.controller.assignar",
					new Object[] {command.getUsuariCodi()});
		}
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:/registreUser/registre/" + registreId,
				"bustia.controller.pendent.contingut.assignat",
				new Object[] {command.getUsuariCodi()});
	}
	
	/** Retorna el llistat de bústies permeses per a l'usuari. Pot incloure o no les innactives */
	@RequestMapping(value = "/assignar/usuaris", method = RequestMethod.GET)
	@ResponseBody
	public List<UsuariPermisDto> usuarisAssignats(
			HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "false") boolean mostrarInactives,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		Set<UsuariPermisDto> usuarisAssignats = new HashSet<UsuariPermisDto>();
		List<BustiaDto> bustiesPermeses = bustiaService.findBustiesPermesesPerUsuari(entitatActual.getId(), false);
		
		for (BustiaDto bustiaDto : bustiesPermeses) {
			List<UsuariPermisDto> usuarisBustia = bustiaService.getUsuarisPerBustia(bustiaDto.getId());
			usuarisAssignats.addAll(usuarisBustia);
		}
		
		return new ArrayList<UsuariPermisDto>(usuarisAssignats);
	}
	
	
	private void emplenarModelAssignar(
			HttpServletRequest request,
			Long registreId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		RegistreDto registre = registreService.findOne(
				entitatActual.getId(),
				registreId,
				false);
		List<UsuariPermisDto> usuarisPerAssignar = bustiaService.getUsuarisPerBustia(registre.getPareId());
		model.addAttribute("usuarisAmbPermis", usuarisPerAssignar);
	}
	
	private boolean isPermesAssignarAnotacions() {
		return new Boolean(aplicacioService.propertyFindByNom("es.caib.distribucio.assignar.anotacions"));
	}

	private static final Logger logger = LoggerFactory.getLogger(RegistreUserAssignarController.class);
}
