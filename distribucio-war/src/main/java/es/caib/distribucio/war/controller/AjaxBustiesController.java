/**
 * Controlador per a les consultes ajax dels unitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
package es.caib.distribucio.war.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.service.BustiaService;

@Controller
@RequestMapping("/bustiaajax")
public class AjaxBustiesController extends BaseAdminController{
	
	@Autowired
	private BustiaService bustiaService;
	
	@RequestMapping(value = "/bustia/{bustiaId}", method = RequestMethod.GET)
	@ResponseBody
	public BustiaDto getById(
			HttpServletRequest request, 
			@PathVariable Long bustiaId, 
			Model model) {

		return bustiaService.findById(bustiaId);	
	}
	
	@RequestMapping(value = "/busties/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<ContingutDto> get(
			HttpServletRequest request, 
			@PathVariable String text, 
			Model model) {
		String decodedToUTF8 = null;
		try {
			decodedToUTF8 = new String(text.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		List<ContingutDto> busties = bustiaService.findPerEntitatIFiltreInput(entitatActual.getId(), decodedToUTF8);
		
		return busties;
	}
	
}
