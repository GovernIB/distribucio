/**
 * 
 */
package es.caib.distribucio.war.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.ripea.core.api.dto.BustiaDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.PermisDto;
import es.caib.ripea.core.api.service.BustiaService;
import es.caib.ripea.war.command.PermisCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;

/**
 * Controlador per al manteniment de permisos de l'entitat
 * actual per a l'usuari administrador.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/bustiaAdmin")
public class BustiaAdminPermisController extends BaseAdminController {

	@Autowired
	private BustiaService bustiaService;



	@RequestMapping(value = "/{bustiaId}/permis", method = RequestMethod.GET)
	public String permis(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"bustia",
				bustiaService.findById(entitatActual.getId(), bustiaId));
		return "bustiaAdminPermis";
	}
	@RequestMapping(value = "/{bustiaId}/permis/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				bustiaService.findById(entitatActual.getId(), bustiaId).getPermisos(),
				"id");
		return dtr;
	}

	@RequestMapping(value = "/{bustiaId}/permis/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			Model model) {
		return get(request, bustiaId, null, model);
	}
	@RequestMapping(value = "/{bustiaId}/permis/{permisId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long permisId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		BustiaDto bustia = bustiaService.findById(entitatActual.getId(), bustiaId);
		model.addAttribute("bustia", bustia);
		PermisDto permis = null;
		if (permisId != null) {
			for (PermisDto p: bustia.getPermisos()) {
				if (p.getId().equals(permisId)) {
					permis = p;
					break;
				}
			}
		}
		if (permis != null)
			model.addAttribute(PermisCommand.asCommand(permis));
		else
			model.addAttribute(new PermisCommand());
		return "bustiaAdminPermisForm";
	}

	@RequestMapping(value = "/{bustiaId}/permis", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@Valid PermisCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			model.addAttribute(
					"bustia",
					bustiaService.findById(entitatActual.getId(), bustiaId));
			return "bustiaAdminPermisForm";
		}
		bustiaService.updatePermis(
				entitatActual.getId(),
				bustiaId,
				PermisCommand.asDto(command));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../bustiaAdmin/" + bustiaId + "/permis",
				"bustia.controller.permis.modificat.ok");
	}

	@RequestMapping(value = "/{bustiaId}/permis/{permisId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long permisId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		bustiaService.deletePermis(
				entitatActual.getId(),
				bustiaId,
				permisId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../../../bustiaAdmin/" + bustiaId + "/permis",
				"bustia.controller.permis.esborrat.ok");
	}

}
