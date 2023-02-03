/**
 * 
 */
package es.caib.distribucio.war.controller;

import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.MetaDadaDto;
import es.caib.distribucio.core.api.dto.MetaDadaTipusEnumDto;
import es.caib.distribucio.core.api.dto.RegistreDto;
import es.caib.distribucio.core.api.service.MetaDadaService;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.war.helper.AjaxHelper;
import es.caib.distribucio.war.helper.AjaxHelper.AjaxFormResponse;
import es.caib.distribucio.war.helper.BeanGeneratorHelper;
import es.caib.distribucio.war.helper.MissatgesHelper;
import es.caib.distribucio.war.helper.RolHelper;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Controlador per a la gestió de les dades d'un registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/registreDada")
public class RegistreDadaController extends BaseAdminController {

	@Autowired
	private RegistreService registreService;
	@Autowired
	private MetaDadaService metaDadaService;
	@Autowired
	private BeanGeneratorHelper beanGeneratorHelper;

	@ModelAttribute("dadesCommand")
	public Object addDadesCommand(HttpServletRequest request) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		return beanGeneratorHelper.generarCommandDadesRegistre(
				entitatActual.getId(),
				null);
	}

	@RequestMapping(value = "/{registreId}/save", method = RequestMethod.POST)
	@ResponseBody
	public AjaxFormResponse dadaSavePost(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@ModelAttribute("dadesCommand") Object dadesCommand,
			BindingResult bindingResult,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (bindingResult.hasErrors()) {
			MissatgesHelper.error(request, getMessage(request, "registre.controller.dades.modificades.error"));
			return AjaxHelper.generarAjaxFormErrors(
					null,
					bindingResult);
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
			List<MetaDadaDto> contingutMetaDades = metaDadaService.findByEntitat(entitatActual.getId());
			Map<String, Object> valors = new HashMap<String, Object>();
			for (int i = 0; i < contingutMetaDades.size(); i++) {
				MetaDadaDto metaDada = contingutMetaDades.get(i);
				Object valor = PropertyUtils.getSimpleProperty(
						dadesCommand,
						metaDada.getCodi());
				if (valor != null && (!(valor instanceof String) || !((String)valor).isEmpty())) {
					valors.put(
							metaDada.getCodi(),
							valor);
				}
			}
			registreService.dadaSave(
					entitatActual.getId(),
					registreId,
					valors);
			MissatgesHelper.success(request, getMessage(request, "registre.controller.dades.modificades.ok"));
			return AjaxHelper.generarAjaxFormOk();
		}
	}

	@RequestMapping(value = "/{registreId}/count")
	@ResponseBody
	public int dadaCount(
			HttpServletRequest request,
			@PathVariable Long registreId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		RegistreDto registre = registreService.findOne(
				entitatActual.getId(),
				registreId,
				false,
				RolHelper.getRolActual(request));
		if (registre instanceof RegistreDto) {
			return ((RegistreDto)registre).getDadesCount();
		} else {
			return 0;
		}
	}
	
	@RequestMapping(value = "/{registreId}/{metaDadaCodi}")
	@ResponseBody
	public Object getDefaultValueForMetaDada(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@PathVariable String metaDadaCodi) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		MetaDadaDto metaDada = metaDadaService.findByCodi(
				entitatActual.getId(),
				metaDadaCodi);	
		Object valor = null;
		if (metaDada.getTipus()==MetaDadaTipusEnumDto.BOOLEA) {
			valor = metaDada.getValorBoolea();
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.DATA) {
			valor = metaDada.getValorData();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			valor = simpleDateFormat.format(valor);
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.FLOTANT) {
			valor = metaDada.getValorFlotant();
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.IMPORT) {
			valor = metaDada.getValorImport();
		} else if (metaDada.getTipus()==MetaDadaTipusEnumDto.SENCER) {
			valor = metaDada.getValorSencer();
		}  else if (metaDada.getTipus()==MetaDadaTipusEnumDto.TEXT) {
			valor = metaDada.getValorString();
		}
		return valor;
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	    binder.registerCustomEditor(
	    		BigDecimal.class,
	    		new CustomNumberEditor(
	    				BigDecimal.class,
	    				NumberFormat.getInstance(new Locale("es","ES")),
	    				true));
	    binder.registerCustomEditor(
	    		Double.class,
	    		new CustomNumberEditor(
	    				Double.class,
	    				NumberFormat.getInstance(new Locale("es","ES")),
	    				true));
	}

}
