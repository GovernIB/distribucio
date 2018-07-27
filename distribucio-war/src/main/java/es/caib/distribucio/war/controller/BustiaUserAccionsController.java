/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.util.ArrayList;

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

import es.caib.ripea.core.api.dto.ArxiuDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.EscriptoriDto;
import es.caib.ripea.core.api.service.ArxiuService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.war.command.ContenidorCommand.Create;
import es.caib.ripea.war.command.ContingutMoureCopiarEnviarCommand;
import es.caib.ripea.war.command.ExpedientCommand;

/**
 * Controlador per a gentionar el contingut pendent a les bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/bustiaUser")
public class BustiaUserAccionsController extends BaseUserController {

	@Autowired
	private ContingutService contenidorService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private MetaExpedientService metaExpedientService;
	@Autowired
	private ArxiuService arxiuService;



	@RequestMapping(value = "/{bustiaId}/pendent/contingut/{contingutId}/nouexp", method = RequestMethod.GET)
	public String registrePendentNouexpGet(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		EscriptoriDto escriptori = contenidorService.getEscriptoriPerUsuariActual(entitatActual.getId());
		ExpedientCommand command = new ExpedientCommand();
		command.setEntitatId(entitatActual.getId());
		command.setPareId(escriptori.getId());
		model.addAttribute(command);
		omplirModelPerNouExpedient(
				entitatActual,
				command.getMetaNodeId(),
				model);
		return "bustiaPendentContingutNouexp";
	}
	@RequestMapping(value = "/{bustiaId}/pendent/contingut/{contingutId}/nouexp", method = RequestMethod.POST)
	public String registrePendentNouexpPost(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long contingutId,
			@Validated({Create.class}) ExpedientCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			omplirModelPerNouExpedient(
					entitatActual,
					command.getMetaNodeId(),
					model);
			return "bustiaPendentContingutNouexp";
		}
		EscriptoriDto escriptori = contenidorService.getEscriptoriPerUsuariActual(entitatActual.getId());
		expedientService.create(
				entitatActual.getId(),
				escriptori.getId(),
				command.getMetaNodeId(),
				command.getArxiuId(),
				null,
				command.getNom(),
				null,
				null);
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../pendent",
				"bustia.controller.pendent.contingut.nouexp.ok");
	}

	@RequestMapping(value = "/{bustiaId}/pendent/contingut/{contingutId}/addexp", method = RequestMethod.GET)
	public String registrePendentAddexpGet(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		omplirModelPerAfegirAExpedient(
				entitatActual,
				model,
				contingutId);
		return "bustiaPendentContingutAddexp";
	}
	@RequestMapping(value = "/{bustiaId}/pendent/contingut/{contingutId}/addexp", method = RequestMethod.POST)
	public String registrePendentAddexpPost(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long contingutId,
			@Valid ContingutMoureCopiarEnviarCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			omplirModelPerAfegirAExpedient(
					entitatActual,
					model,
					contingutId);
			return "bustiaPendentContingutAddexp";
		}
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../pendent",
				"bustia.controller.pendent.contingut.addexp.ok");
	}


	private void omplirModelPerNouExpedient(
			EntitatDto entitatActual,
			Long metaExpedientId,
			Model model) {
		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId()));
		if (metaExpedientId != null) {
			model.addAttribute(
					"arxius",
					arxiuService.findPermesosPerUsuariIMetaExpedient(
							entitatActual.getId(),
							metaExpedientId));
		} else {
			model.addAttribute(
					"arxius",
					new ArrayList<ArxiuDto>());
		}
	}

	private void omplirModelPerAfegirAExpedient(
			EntitatDto entitatActual,
			Model model,
			Long contenidorOrigenId) {
		EscriptoriDto escriptori = contenidorService.getEscriptoriPerUsuariActual(entitatActual.getId());
		model.addAttribute(
				"contenidorDesti",
				escriptori);
		ContingutMoureCopiarEnviarCommand command = new ContingutMoureCopiarEnviarCommand();
		command.setOrigenId(contenidorOrigenId);
		model.addAttribute(command);
	}
}
