package es.caib.distribucio.back.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.back.command.LimitCanviEstatCommand;
import es.caib.distribucio.back.helper.DatatablesHelper;
import es.caib.distribucio.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.back.helper.MissatgesHelper;
import es.caib.distribucio.logic.intf.dto.LimitCanviEstatDto;
import es.caib.distribucio.logic.intf.service.ConfigService;
import es.caib.distribucio.logic.intf.service.LimitCanviEstatService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/limitCanviEstat")
public class LimitCanviEstatController extends BaseAdminController {

    private static final Logger logger = LoggerFactory.getLogger(LimitCanviEstatController.class);

    private final LimitCanviEstatService limitCanviEstatService;
    private final ConfigService configService;

    @RequestMapping(method = RequestMethod.GET)
    public String get(HttpServletRequest request, Model model) {
        return "limitCanviEstat";
    }

    @RequestMapping(value = "/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse datatable(HttpServletRequest request) {
        return DatatablesHelper.getDatatableResponse(
                request,
                limitCanviEstatService.findAllPaged(
                        DatatablesHelper.getPaginacioDtoFromRequest(request)),
                "id");
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newGet(HttpServletRequest request, Model model) {
        return formGet(request, null, model);
    }

    @RequestMapping(value = "/{limitId}", method = RequestMethod.GET)
    public String formGet(
            HttpServletRequest request,
            @PathVariable Long limitId,
            Model model) {
        LimitCanviEstatDto limitDto = null;
        if (limitId != null){
            limitDto = limitCanviEstatService.findById(limitId);
        }

        LimitCanviEstatCommand command = null;
        if (limitDto != null)
            command = LimitCanviEstatCommand.asCommand(limitDto);
        else
            command = new LimitCanviEstatCommand();
        model.addAttribute(command);
        model.addAttribute("placeholderLimitMinutLaboral", this.getConfigOrDefault("es.caib.distribucio.limit.minut.laboral", "4"));
        model.addAttribute("placeholderLimitMinutNoLaboral", this.getConfigOrDefault("es.caib.distribucio.limit.minut.no.laboral", "8"));
        model.addAttribute("placeholderLimitDiaLaboral", this.getConfigOrDefault("es.caib.distribucio.limit.dia.laboral", "8000"));
        model.addAttribute("placeholderLimitDiaNoLaboral", this.getConfigOrDefault("es.caib.distribucio.limit.dia.no.laboral", "1000"));
        return "limitCanviEstatForm";
    }

    private String getConfigOrDefault(String key, String defaultValue) {
    	
    	String value = configService.getConfig(key);
    	if (value == null) {
    		value = defaultValue;
    	}
		return value;
	}

	@RequestMapping(value = "/newOrModify", method = RequestMethod.POST)
    public String save(
            HttpServletRequest request,
            LimitCanviEstatCommand command,
            BindingResult bindingResult,
            Model model) {
        try {
            if (bindingResult.hasErrors()) {
                return "limitCanviEstatForm";
            }

            // if it is modified
            if (command.getId() != null) {
                limitCanviEstatService.update(LimitCanviEstatCommand.asDto(command));
                return getModalControllerReturnValueSuccess(
                        request,
                        "redirect:limitCanviEstat",
                        "limit.canvi.estat.controller.modificat.ok");
                //if it is new
            } else {
                limitCanviEstatService.create(LimitCanviEstatCommand.asDto(command));
                return getModalControllerReturnValueSuccess(
                        request,
                        "redirect:limitCanviEstat",
                        "limit.canvi.estat.controller.creat.ok");
            }
        } catch (Exception e) {
            logger.error("Error no controlat creant o actualitzant un limit de canvi d'estat: " + e.getMessage(), e);
            MissatgesHelper.error(
                    request,
                    "Error : " + e.getClass() + " " + e.getMessage());
            return "limitCanviEstatForm";
        }
    }

    @RequestMapping(value = "/{limitId}/delete", method = RequestMethod.GET)
    public String delete(
            HttpServletRequest request,
            @PathVariable Long limitId) {
        try {
            logger.debug("Esborrant limit de canvi d'estat (id=" + limitId +  ")");
            limitCanviEstatService.delete(limitId);
            return getAjaxControllerReturnValueSuccess(
                    request,
                    "redirect:limitCanviEstat",
                    "limit.canvi.estat.controller.esborrat.ok");
        } catch (RuntimeException ve) {
            return getAjaxControllerReturnValueError(
                    request,
                    "redirect:limitCanviEstat",
                    "limit.canvi.estat.controller.esborrat.error",
                    new Object[] {ve.getMessage()});
        }
    }
}
