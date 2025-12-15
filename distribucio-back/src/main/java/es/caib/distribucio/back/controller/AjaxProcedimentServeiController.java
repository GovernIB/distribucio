package es.caib.distribucio.back.controller;

import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentDto;
import es.caib.distribucio.logic.intf.dto.ServeiDto;
import es.caib.distribucio.logic.intf.service.ProcedimentService;
import es.caib.distribucio.logic.intf.service.ServeiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador per a les consultes ajax dels procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/procedimentserveiajax")
public class AjaxProcedimentServeiController extends BaseAdminController{
	
	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private ServeiService serveiService;
	
	@RequestMapping(value = "/procedimentservei/{elementId}", method = RequestMethod.GET)
	@ResponseBody
	public Object getByCodi(
			HttpServletRequest request, 
			@PathVariable String elementId,
			Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		ProcedimentDto procediment = procedimentService.findByCodiSia( entitatActual.getId(), elementId );
        ServeiDto servei = serveiService.findByCodiSia( entitatActual.getId(), elementId );
		if (procediment != null) {
            return procediment;
        }
		if (servei != null) {
            return servei;
        }
        return this.getProcedimentNoTrobat(request, elementId);
	}
	
	@RequestMapping(value = "/procedimentserveis/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<Object> getByName (
			HttpServletRequest request, 
			@PathVariable String text, 
			Model model) {
		String decodedToUTF8 = null;
		try {
			decodedToUTF8 = new String(text.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			String msgError = "No s'ha pogut consultar el text " + text + ": " + e.getMessage();
			logger.error(msgError);
		}
        List<Object> result = new ArrayList<>();
		EntitatDto entitatActual = getEntitatActual(request);
		List<ProcedimentDto> procediments = procedimentService.findByNomOrCodiSia(
				entitatActual.getId(),
				decodedToUTF8);
        result.addAll(procediments);

		List<ServeiDto> serveis = serveiService.findByNomOrCodiSia(
				entitatActual.getId(),
				decodedToUTF8);
        result.addAll(serveis);
		
		if (result.isEmpty()) {
            result.add(this.getProcedimentNoTrobat(request, text));
		}
		return result;
	}
	
	private ProcedimentDto getProcedimentNoTrobat(HttpServletRequest request, String elementId) {
		ProcedimentDto noTrobat = new ProcedimentDto();
		noTrobat.setCodi(elementId);
		noTrobat.setCodiSia(elementId);
		noTrobat.setNom("(" + getMessage(request, "comu.no.trobat") + ")");
		return noTrobat;
	}


	
	private static final Logger logger = LoggerFactory.getLogger(AjaxProcedimentServeiController.class);

}
