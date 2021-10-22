/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.dto.UsuariDto;
import es.caib.distribucio.core.api.service.AplicacioService;
import es.caib.distribucio.war.helper.EnumHelper.HtmlOption;

/**
 * Controlador per a les consultes ajax dels usuaris normals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/userajax") // No podem posar "/ajaxuser" per mor del AjaxInterceptor
public class AjaxUserController extends BaseUserController {

	@Autowired
	private AplicacioService aplicacioService;

	@RequestMapping(value = "/usuari/{codi}", method = RequestMethod.GET)
	@ResponseBody
	public UsuariDto getByCodi(
			HttpServletRequest request,
			@PathVariable String codi,
			Model model) {
		return aplicacioService.findUsuariAmbCodi(codi);
	}

	@RequestMapping(value = "/usuaris/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<UsuariDto> get(
			HttpServletRequest request,
			@PathVariable String text,
			Model model) {
		return aplicacioService.findUsuariAmbText(text);
	}

	@RequestMapping(value = "/enum/{enumClass}", method = RequestMethod.GET)
	@ResponseBody
	public List<HtmlOption> enumValorsAmbText(
			HttpServletRequest request,
			@PathVariable String enumClass) throws ClassNotFoundException {
		Class<?> enumeracio = findEnumDtoClass(enumClass);
		StringBuilder textKeyPrefix = new StringBuilder();
		String[] textKeys = StringUtils.splitByCharacterTypeCamelCase(enumClass);
		for (String textKey: textKeys) {
			if (!"dto".equalsIgnoreCase(textKey)) {
				textKeyPrefix.append(textKey.toLowerCase());
				textKeyPrefix.append(".");
			}
		}
		List<HtmlOption> resposta = new ArrayList<HtmlOption>();
		if (enumeracio.isEnum()) {
			for (Object e: enumeracio.getEnumConstants()) {
				resposta.add(new HtmlOption(
						((Enum<?>)e).name(),
						getMessage(
								request,
								textKeyPrefix.toString() + ((Enum<?>)e).name(),
								null)));
			}
		}
		return resposta;
	}
	
	@RequestMapping(value = "/remitent", method = RequestMethod.GET)
	@ResponseBody
	public List<UsuariDto> getRemitent(HttpServletRequest request, Model model) {
		return get(request, null, model);
	}
	
	@RequestMapping(value = "/remitent/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<UsuariDto> getRemitent(HttpServletRequest request, @PathVariable String text, Model model) {
		return getRemitentWithParam(request, text, model);
	}
	
	@RequestMapping(value = "/remitent/item/{id}", method = RequestMethod.GET)
	@ResponseBody
	public UsuariDto getItem(HttpServletRequest request, @PathVariable String codi, Model model) {
		return aplicacioService.findUsuariAmbCodi(codi);
	}
	
	private List<UsuariDto> getRemitentWithParam(HttpServletRequest request, String text, Model model)  {
		
		try {
			text = URLDecoder.decode(request.getRequestURI().split("/")[4], StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) { }
		
		List<UsuariDto> remitentsList = aplicacioService.findUsuariAmbCodiAndNom(text);
		
		if (text == null) {
			return remitentsList.subList(0, 5);
		}

		return remitentsList;
	}
	
	private Class<?> findEnumDtoClass(String className) throws ClassNotFoundException{
		try {
			return Class.forName("es.caib.distribucio.core.api.dto." + className);
		} catch(ClassNotFoundException e) {
			// TODO: això hauria de cercar per tots els subpackages de dto
			return Class.forName("es.caib.distribucio.core.api.dto.historic." + className);
		}		
	}

}
