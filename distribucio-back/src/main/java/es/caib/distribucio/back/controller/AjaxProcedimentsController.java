package es.caib.distribucio.back.controller;

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

import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentDto;
import es.caib.distribucio.logic.intf.service.ProcedimentService;

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
		ProcedimentDto procediment = procedimentService.findByCodiSia( entitatActual.getId(), 
																	   procedimentCodi);
		if (procediment == null) {
			procediment = this.getProcedimentNoTrobat(request, procedimentCodi);
		}
		return procediment;
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
			String msgError = "No s'ha pogut consultar el text " + text + ": " + e.getMessage();
			logger.error(msgError);
		}
		EntitatDto entitatActual = getEntitatActual(request);		
		List<ProcedimentDto> procediments = procedimentService.findByNomOrCodiSia(
				entitatActual.getId(), 
				decodedToUTF8);
		
		if (procediments.isEmpty()) {
			procediments.add(this.getProcedimentNoTrobat(request, text));
		}
		return procediments;
	}
	
	private ProcedimentDto getProcedimentNoTrobat(HttpServletRequest request, String procedimentCodi) {
		ProcedimentDto noTrobat = new ProcedimentDto();
		noTrobat.setCodi(procedimentCodi);
		noTrobat.setCodiSia(procedimentCodi);
		noTrobat.setNom("(" + getMessage(request, "comu.no.trobat") + ")");
		return noTrobat;
	}


	
	private static final Logger logger = LoggerFactory.getLogger(AjaxProcedimentsController.class);

}
