/**
 * 
 */
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.back.helper.RolHelper;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.logic.intf.service.BustiaService;
import es.caib.distribucio.logic.intf.service.UnitatOrganitzativaService;

/**
 * Controlador per a les consultes ajax dels unitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/unitatajax") 
public class AjaxUnitatsController extends BaseAdminController {
	
	@Autowired
	private UnitatOrganitzativaService unitatOrganitzativaService;
	@Autowired
	private BustiaService bustiaService;
	
	@RequestMapping(value = "/unitat/{unitatId}", method = RequestMethod.GET)
	@ResponseBody
	public UnitatOrganitzativaDto getById(
			HttpServletRequest request,
			@PathVariable Long unitatId,
			Model model) {

		return unitatOrganitzativaService.findById(unitatId);
	}

	@RequestMapping(value = "/unitats/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<UnitatOrganitzativaDto> get(
			HttpServletRequest request,
			@PathVariable String text,
			Model model) {
		String decodedToUTF8 = null;
		try {
			decodedToUTF8 = new String(text.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("No s'ha pogut consultar el text " + text + ": " + e.getMessage());
		}
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		List<UnitatOrganitzativaDto> unitatsEntitat = unitatOrganitzativaService
				.findByEntitatAndFiltre(entitatActual.getCodi(), decodedToUTF8, true, false, RolHelper.isRolActualUsuari(request));
		
		return unitatsEntitat;
	}

	@RequestMapping(value = "/senseEntitat/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<UnitatOrganitzativaDto> getSenseEntitat(
			HttpServletRequest request,
			@PathVariable String text,
			Model model) {
		String decodedToUTF8 = null;
		try {
			decodedToUTF8 = new String(text.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("No s'ha pogut consultar el text " + text + e.getMessage());
		}

		List<UnitatOrganitzativaDto> unitatsEntitat = unitatOrganitzativaService
				.findByCodiAndDenominacioFiltre(decodedToUTF8);
		
		return unitatsEntitat;
	}
	
	@RequestMapping(value = "/unitats/{text}/{codiUnitatSuperior}", method = RequestMethod.GET)
	@ResponseBody
	public List<UnitatOrganitzativaDto> get(
			HttpServletRequest request,
			@PathVariable String text,
			@PathVariable String codiUnitatSuperior,
			Model model) {
		String decodedToUTF8 = null;
		try {
			decodedToUTF8 = new String(text.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("No s'ha pogut consultar el text " + text + ": " + e.getMessage());
		}
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		List<UnitatOrganitzativaDto> unitatsEntitat = unitatOrganitzativaService
				.findByEntitatAndCodiUnitatSuperiorAndFiltre(entitatActual.getCodi(), 
				codiUnitatSuperior, decodedToUTF8, true, false);
		
		return unitatsEntitat;
	}
	
	@RequestMapping(value = "/nomesUnitatsAmbBusties/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<UnitatOrganitzativaDto> getOnlyUnitatsWithBustiesDefined(
			HttpServletRequest request,
			@PathVariable String text,
			Model model) {
		String decodedToUTF8 = null;
		try {
			decodedToUTF8 = new String(text.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("No s'ha pogut consultar el text " + text + ": " + e.getMessage());
		}
		
		EntitatDto entitatActual;
		if (RolHelper.isRolActualUsuari(request)) {
			entitatActual = getEntitatActualComprovantPermisUsuari(request);
		} else {
			entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		}		
		List<UnitatOrganitzativaDto> unitatsEntitat = unitatOrganitzativaService
				.findByEntitatAndFiltre(entitatActual.getCodi(), decodedToUTF8, true, true, RolHelper.isRolActualUsuari(request));
		
		return unitatsEntitat;
	}
	
	private EntitatDto getEntitatActualComprovantPermisUsuari(
			HttpServletRequest request) {
		EntitatDto entitat = this.getEntitatActual(request);
		if (entitat.isUsuariActualRead() || entitat.isUsuariActualAdminLectura()) {
			return entitat;
		}else {
			throw new SecurityException(getMessage(request, "entitat.actual.error.permis.usuari"));
		}
	}
	
	
	@RequestMapping(value = "/unitatSuperior/{unitatSuperiorCodi}", method = RequestMethod.GET)
	@ResponseBody
	public UnitatOrganitzativaDto getUnitatSuperior(
			HttpServletRequest request,
			@PathVariable String unitatSuperiorCodi,
			Model model) {
        EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		return unitatOrganitzativaService.findByCodiDir3EntitatAndCodi(entitatActual.getCodiDir3(), unitatSuperiorCodi);
	}

	/** Obté una llista de les unitats orgàniques que són unitats superiros d'alguna bústia. 
	 * 
	 * @return Retorna la llista filtrada de les unitats orgàniques que són superiors de les bústies.
	 */
	@RequestMapping(value = "/unitatsSuperiors/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<UnitatOrganitzativaDto> getUnitatsSuperiors(
			HttpServletRequest request,
			@PathVariable String text,
			@RequestParam(value = "q", required = false) String query,
			Model model) {
		String decodedToUTF8 = null;
		try {
			if (query != null)
				decodedToUTF8 = new String(text.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("No s'ha pogut consultar el text " + text + ": " + e.getMessage());
		}
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		
		List<UnitatOrganitzativaDto> unitatsEntitat = bustiaService.findUnitatsSuperiors(entitatActual.getId(), decodedToUTF8);
				
		return unitatsEntitat;
	}
	
	@RequestMapping(value = "/unitatsWithoutArrel/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<UnitatOrganitzativaDto> getUnitatsWithoutArrel(
			HttpServletRequest request,
			@PathVariable String text,
			Model model) {
		String decodedToUTF8 = null;
		try {
			decodedToUTF8 = new String(text.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("No s'ha pogut consultar el text " + text + ": " + e.getMessage());
		}
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		List<UnitatOrganitzativaDto> unitatsEntitat = unitatOrganitzativaService
				.findByEntitatAndFiltre(entitatActual.getCodi(), decodedToUTF8, false, false, RolHelper.isRolActualUsuari(request));
		
		return unitatsEntitat;
	}

	
	private static final Logger logger = LoggerFactory.getLogger(AjaxUnitatsController.class);
}