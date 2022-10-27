package es.caib.distribucio.war.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.ProcedimentDto;
import es.caib.distribucio.core.api.exception.SistemaExternException;
import es.caib.distribucio.core.api.service.ProcedimentService;

/**
 * Controlador per a les consultes ajax dels procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/procedimentajax")
public class AjaxProcedimentsController extends BaseAdminController{
	
	@Autowired
	private ProcedimentService procedimentService;
	
	@RequestMapping(value = "/procediment/{procedimentCodi}", method = RequestMethod.GET)
	@ResponseBody
	public ProcedimentDto getByCodi(
			HttpServletRequest request, 
			@PathVariable String procedimentCodi, 
			Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		return procedimentService.findByCodiSia(
				entitatActual.getId(), 
				procedimentCodi);		
	}
	
	@RequestMapping(value = "/procediments/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<ProcedimentDto> getByName (
			HttpServletRequest request, 
			@PathVariable String text, 
			Model model) {
		String decodedToUTF8 = null;
		try {
			decodedToUTF8 = new String(text.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
			String msgError = "No s'ha pogut consultar el text " + text;
			logger.error(msgError);
			throw new SistemaExternException(msgError);
		}
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);		
		List<ProcedimentDto> procediments = procedimentService.findByNom(
				entitatActual.getId(), 
				decodedToUTF8);
		
		return procediments;
	}

	
	private static final Logger logger = LoggerFactory.getLogger(AjaxProcedimentsController.class);

}
