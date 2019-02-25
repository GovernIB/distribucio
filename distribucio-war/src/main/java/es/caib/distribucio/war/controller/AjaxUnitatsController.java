/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.service.UnitatOrganitzativaService;

/**
 * Controlador per a les consultes ajax dels usuaris normals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/unitatajax") // No podem posar "/ajaxuser" per mor del AjaxInterceptor
public class AjaxUnitatsController extends BaseAdminController {
	
	@Autowired
	private UnitatOrganitzativaService unitatOrganitzativaService;
	
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
			e.printStackTrace();
		}
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<UnitatOrganitzativaDto> unitatsEntitat = unitatOrganitzativaService
				.findByEntitatAndFiltre(entitatActual.getCodi(), decodedToUTF8);
		
		return unitatsEntitat;
	}
}