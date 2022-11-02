/**
 * 
 */
package es.caib.distribucio.war.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.dto.BackofficeDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.service.BackofficeService;
import es.caib.distribucio.war.command.BackofficeCommand;
import es.caib.distribucio.war.helper.DatatablesHelper;
import es.caib.distribucio.war.helper.MissatgesHelper;
import es.caib.distribucio.war.helper.DatatablesHelper.DatatablesResponse;



@Controller
@RequestMapping("/backoffice")
public class BackofficeController extends BaseAdminController {

	@Autowired
	private BackofficeService backofficeService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		getEntitatActualComprovantPermisAdminLectura(request);
		return "backofficeList";
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				backofficeService.findByEntitatPaginat(
						entitatActual.getId(),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
		return dtr;
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(
			HttpServletRequest request,
			Model model) {
		model.addAttribute("nou", true);
		return get(request, null, model);
	}
	
	@RequestMapping(value = "/{backofficeId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long backofficeId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		BackofficeDto backoffice = null;
		if (backofficeId != null)
			backoffice = backofficeService.findById(
					entitatActual.getId(),
					backofficeId);
		BackofficeCommand command = null;
		if (backoffice != null) {
			command = BackofficeCommand.asCommand(backoffice);
			command.setEntitatId(entitatActual.getId());
		} else {
			command = new BackofficeCommand();
			command.setEntitatId(entitatActual.getId());
		}
		model.addAttribute(command);
		return "backofficeForm";
	}
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid BackofficeCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		if (bindingResult.hasErrors()) {
			return "backofficeForm";
		}
		
		try {
			if (command.getId() != null) {
				backofficeService.update(
						entitatActual.getId(), 
						BackofficeCommand.asDto(command));
				return getModalControllerReturnValueSuccess(
						request,
						"redirect:../../backoffice",
						"backoffice.controller.modificat.ok");
			} else {
				backofficeService.create(
						entitatActual.getId(), 
						BackofficeCommand.asDto(command));
				return getModalControllerReturnValueSuccess(
						request,
						"redirect:../../backoffice",
						"backoffice.controller.creat.ok");
			}
		} catch (Exception e) {
			return getAjaxControllerReturnValueErrorMessage(
					request,
					"redirect:../../backoffice",
					ExceptionUtils.getRootCause(e).getMessage());
		}
		
	}
	
	@RequestMapping(value = "/{backofficeId}/provar", method = RequestMethod.GET)
	public String provar(
			HttpServletRequest request, 
			@PathVariable Long backofficeId) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
			
			Exception exception = backofficeService.provar(
					entitatActual.getId(), 
					backofficeId);
			
			if (exception == null) {
				MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"backoffice.controller.provar.ok",
								null));
			} else {
				MissatgesHelper.error(
				request,
				getMessage(
						request, 
						"backoffice.controller.provar.error",
						new Object[] {exception.getMessage()}));				
			}
			
		} catch (Exception e) {
			MissatgesHelper.error(
			request,
			getMessage(
					request, 
					"backoffice.controller.provar.error",
					new Object[] {e.getMessage()}));

		}
		return "/";
	}
	
	@RequestMapping(value = "/{backofficeId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long backofficeId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		try {
			backofficeService.delete(
					entitatActual.getId(),
					backofficeId);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../backoffice",
					"backoffice.controller.esborrat.ok");
		} catch (Exception e) {
			return getAjaxControllerReturnValueErrorMessage(
					request,
					"redirect:../../backoffice",
					ExceptionUtils.getRootCause(e).getMessage());
		}
	}
	


}
