/**
 * 
 */
package es.caib.distribucio.back.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.back.command.BackofficeCommand;
import es.caib.distribucio.back.helper.DatatablesHelper;
import es.caib.distribucio.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.back.helper.MissatgesHelper;
import es.caib.distribucio.logic.intf.dto.BackofficeDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.service.BackofficeService;



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
			@RequestParam(value = "accio", required = false) String accio,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("nou", true);
			if (command.getCodi() != null &&
					!command.getCodi().isEmpty() &&
					command.getCodi().contains(" ")) {
				model.addAttribute("codiEmpySpace", true);
			}
			return "backofficeForm";
		}
		
		try {
			model.addAttribute("codiEmpySpace", false);
			if (command.getCodi().contains(" ")) {
				model.addAttribute("codiEmpySpace", true);
				return getAjaxControllerReturnValueErrorMessage(
						request,
						"backofficeForm", 
						"backoffice.form.camp.codi.espai.blanc");
			}
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		BackofficeDto backoffice = backofficeService.findById(entitatActual.getId(), backofficeId);
		try {
			
			Exception exception = backofficeService.provar(
					entitatActual.getId(), 
					backofficeId);
			
			if (exception == null) {
				MissatgesHelper.success(
						request, 
						getMessage(
								request, 
								"backoffice.controller.provar.ok",
								new Object[] {
										backoffice.getCodi()}));
			} else {
				MissatgesHelper.error(
				request,
				getMessage(
						request, 
						"backoffice.controller.provar.error",
						new Object[] {
								backoffice.getCodi(),
								exception.getMessage()}));				
			}
			
		} catch (Exception e) {
			MissatgesHelper.error(
			request,
			getMessage(
					request, 
					"backoffice.controller.provar.error",
					new Object[] {
							backoffice.getCodi(),
							e.getMessage()}));

		}
		return "redirect:../../backoffice";
	}
	
	@RequestMapping(value = "/{backofficeId}/provarajax", method = RequestMethod.GET)
	@ResponseBody
	public boolean provarajax(
			HttpServletRequest request, 
			@PathVariable Long backofficeId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		BackofficeDto backoffice = backofficeService.findById(entitatActual.getId(), backofficeId);
		boolean response = false;
		try {
			
			Exception exception = backofficeService.provar(
					entitatActual.getId(), 
					backofficeId);
			
			if (exception == null) {
				response = true;
			}
		} catch (Exception e) {
			MissatgesHelper.error(
					request,
					getMessage(
							request, 
							"backoffice.controller.provar.error",
							new Object[] {
									backoffice.getCodi(),
									e.getMessage()}));
		}
		
		
		return response;
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
			String errorMessage;
			Throwable t = e.getCause();
			if (t instanceof ConstraintViolationException) {
				errorMessage = getMessage(request, "backoffice.controller.esborrat.ko.constraintviolation");				
			} else {
				errorMessage = ExceptionUtils.getRootCause(e).getMessage();
			}
			return getAjaxControllerReturnValueErrorMessage(
					request,
					"redirect:../../backoffice",
					errorMessage);
		}
	}
	


}
