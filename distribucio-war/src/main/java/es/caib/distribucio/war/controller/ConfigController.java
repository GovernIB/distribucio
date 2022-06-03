package es.caib.distribucio.war.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.dto.ConfigDto;
import es.caib.distribucio.core.api.dto.ConfigGroupDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.service.ConfigService;
import es.caib.distribucio.core.api.service.EntitatService;
import es.caib.distribucio.war.command.ConfigCommand;
import es.caib.distribucio.war.helper.ExceptionHelper;
import es.caib.distribucio.war.helper.JsonResponse;
import es.caib.distribucio.war.helper.RolHelper;



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
    @Autowired
    private EntitatService entitatService;

    @RequestMapping(method = RequestMethod.GET)
    public String get(
            HttpServletRequest request,
            Model model) {
        List<ConfigGroupDto> configGroups = configService.findAll();
        List<EntitatDto> entitats = new ArrayList<>();
        if (RolHelper.isRolActualAdministrador(request)) {
        	entitats = entitatService.findPaginat(PaginacioParamsDto.getPaginacioDtoTotsElsResultats()).getContingut();
        }
        model.addAttribute("config_groups", configGroups);
        //model.addAttribute("entitats", entitats);
        for (ConfigGroupDto cGroup: configGroups) {
            //fillFormsModel(cGroup, model);
            fillFormsModel(cGroup, model, entitats);
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
        	StringBuilder errors = new StringBuilder();
        	for(ObjectError error : bindingResult.getAllErrors()) {
        		if (errors.length() > 0)
        			errors.append(", ");
        		errors.append(error.getDefaultMessage());
        	}
        	return new JsonResponse(true, getMessage(request, "config.controller.edit.error") + ": " + errors.toString());
        }
              
        try {
        	configService.updateProperty(ConfigCommand.asDto(configCommand));
			return new JsonResponse(configCommand.getKey());
            
        } catch (Exception e) {
        	return new JsonResponse(true, getMessage(request, "config.controller.edit.error") + ": " + ExceptionHelper.getRootCauseOrItself(e).getMessage());
        }
    }
    
    
    
    @RequestMapping(value="/propietatsEntitat/{idEntitat}", method = RequestMethod.GET)
    public String propietatsEntitat(
    		HttpServletRequest request, 
    		Model model, 
    		@PathVariable long idEntitat) {
        List<ConfigGroupDto> configGroups = configService.findAll();
        EntitatDto entitatDto = entitatService.findById(idEntitat);
        model.addAttribute("config_groups", configGroups);
        model.addAttribute("entitatDto", entitatDto);
        for (ConfigGroupDto cGroup: configGroups) {
            //fillFormsModelEntitat(cGroup, model, entitatDto);
        	fillFormsModelEntitat(cGroup, model, entitatDto);
        }
        
        List<ConfigDto> llistatPropietats = configService.findAllPerEntitat(entitatDto);
        model.addAttribute("llistatPropietats", llistatPropietats);
        
        Map<String, Object> valorsDefault = new HashMap<>();
        for (ConfigDto configDto : llistatPropietats) {
        	//if (configDto.getValue() != null && !configDto.getValue().contains("////")) {
        		String keyGeneral = configDto.getKey().replace(configDto.getEntitatCodi() + ".", "");
        		ConfigDto propGeneral = configService.findByKey(keyGeneral);
        		valorsDefault.put(propGeneral.getKey(), propGeneral.getValue());
        	//}
        }
        model.addAttribute("valorsDefault", valorsDefault);
    	
    	return "configEntitat";
    }    
    
    
    @ResponseBody
    @RequestMapping(value = "/entitat/{key}", method = RequestMethod.GET)
    public List<ConfigDto> getEntitatConfigByKey(
    		HttpServletRequest request, 
    		@PathVariable String key, 
    		Model model) {
    	
    	try {
    		return configService.findEntitatsConfigByKey(key.replace("-", "."));
    	}catch (Exception ex) {
    		logger.error("Error obtinguent les configuracions d'entitats per la key " + key, ex);
    		return new ArrayList<>();
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
    
    @RequestMapping(value="/reiniciarTasquesSegonPla", method = RequestMethod.GET)
    public String reiniciarTasquesSegonPla(
    		HttpServletRequest request, 
    		Model model, 
    		@RequestParam("currentPage") String paginaActual) {
    	
    	try {
    		configService.reiniciarTasquesEnSegonPla();
    		return getAjaxControllerReturnValueSuccess(
    				request,
    				"redirect:../" + paginaActual,
    				"config.reiniciar.tasques.segon.pla.ok");
    	}catch(Exception e) {
    		return getAjaxControllerReturnValueError(
    				request,
    				"redirect:../" + paginaActual,
    				"config.reiniciar.tasques.segon.pla.error", 
    				new Object[] {e.getMessage()});    		
    	}
    }    


    private void fillFormsModel(ConfigGroupDto cGroup, Model model, List<EntitatDto> entitats){
    	for (ConfigDto config: cGroup.getConfigs()) {
            model.addAttribute("config_" + config.getKey().replace('.', '_'),
                    ConfigCommand.asCommand(config));
            for (EntitatDto entitat : entitats) {
            	String  keyEntitat = config.addEntitatKey(entitat);
            	model.addAttribute("entitat_config_" + keyEntitat.replace('.', '_'));
            }
        }
        if (cGroup.getInnerConfigs() == null || cGroup.getInnerConfigs().isEmpty()){
            return;
        }
        for (ConfigGroupDto child : cGroup.getInnerConfigs()){
            fillFormsModel(child, model, entitats);
        }
    }
    

    private void fillFormsModelEntitat(ConfigGroupDto cGroup, Model model, EntitatDto entitatEntity){
        for (ConfigDto config: cGroup.getConfigs()) {
        	ConfigCommand configCommand = ConfigCommand.asCommand(config);
            model.addAttribute("config_" + config.getKey().replace('.', '_'),
            		configCommand);
        }
        if (cGroup.getInnerConfigs() == null || cGroup.getInnerConfigs().isEmpty()){
            return;
        }
        for (ConfigGroupDto child : cGroup.getInnerConfigs()){
            fillFormsModelEntitat(child, model, entitatEntity);
        }
    }
    
	private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);
}
