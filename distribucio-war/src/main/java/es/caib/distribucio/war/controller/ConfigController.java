package es.caib.distribucio.war.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.dto.ConfigDto;
import es.caib.distribucio.core.api.dto.ConfigGroupDto;
import es.caib.distribucio.core.api.service.ConfigService;
import es.caib.distribucio.war.command.ConfigCommand;
import es.caib.distribucio.war.helper.ExceptionHelper;
import es.caib.distribucio.war.helper.JsonResponse;

/**
 * Controlador per a la gestió de la configuració de l'aplicació.
 * Només accessible amb el rol de superusuari.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/config")
public class ConfigController extends BaseUserController{
    @Autowired
    private ConfigService configService;

    @RequestMapping(method = RequestMethod.GET)
    public String get(
            HttpServletRequest request,
            Model model) {
        List<ConfigGroupDto> configGroups = configService.findAll();
        model.addAttribute("config_groups", configGroups);
        for (ConfigGroupDto cGroup: configGroups) {
            fillFormsModel(cGroup, model);
        }
        return "config";
    }

    @RequestMapping(value="/update", method = RequestMethod.POST)
    @ResponseBody
    public JsonResponse updateConfig(
            HttpServletRequest request,
            Model model,
			@Valid ConfigCommand configCommand,
			BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
        	return new JsonResponse(true, getMessage(request, "config.controller.edit.error"));
        }

        try {
        	configService.updateProperty(ConfigCommand.asDto(configCommand));
			return new JsonResponse(configCommand.getKey());
            
        } catch (Exception e) {
        	return new JsonResponse(true, getMessage(request, "config.controller.edit.error") + ": " + ExceptionHelper.getRootCauseOrItself(e).getMessage());
        }
    }
    
    @RequestMapping(value="/synchronize", method = RequestMethod.GET)
    public String synchronize(
            HttpServletRequest request,
            Model model) {
    	
    	try {
			configService.synchronize();
			
	        return getModalControllerReturnValueSuccess(
	                request,
	                "redirect:.",
	                "config.controller.synchronize.ok");
			
		} catch (Exception e) {
			logger.error("Error al sincronitzar properties", e);
	        return getModalControllerReturnValueErrorNoKey(
	                request,
	                "redirect:.",
	                getMessage(request,"config.controller.synchronize.error") + ": " + ExceptionHelper.getRootCauseOrItself(e).getMessage());
		}

    }
    

    private void fillFormsModel(ConfigGroupDto cGroup, Model model){
        for (ConfigDto config: cGroup.getConfigs()) {
            model.addAttribute("config_" + config.getKey().replace('.', '_'),
                    ConfigCommand.asCommand(config));
        }
        if (cGroup.getInnerConfigs() == null || cGroup.getInnerConfigs().isEmpty()){
            return;
        }
        for (ConfigGroupDto child : cGroup.getInnerConfigs()){
            fillFormsModel(child, model);
        }
    }
    
	private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);
}
