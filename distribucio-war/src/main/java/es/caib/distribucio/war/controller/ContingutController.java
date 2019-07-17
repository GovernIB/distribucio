/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.dto.ArxiuDetallDto;
import es.caib.distribucio.core.api.dto.ContingutComentariDto;
import es.caib.distribucio.core.api.dto.ContingutLogDetallsDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.distribucio.core.api.dto.LogTipusEnumDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDto;
import es.caib.distribucio.core.api.dto.UsuariDto;
import es.caib.distribucio.core.api.service.AplicacioService;
import es.caib.distribucio.core.api.service.ContingutService;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.war.helper.EnumHelper;
import es.caib.distribucio.war.helper.MissatgesHelper;
import es.caib.distribucio.war.helper.SessioHelper;

/**
 * Controlador per a la gestió de contenidors i mètodes compartits entre
 * diferents tipus de contingut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ContingutController extends BaseUserController {

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ContingutService contingutService;
	@Autowired
	private RegistreService registreService;







	@RequestMapping(value = "/contingut/{bustiaId}/registre/{registreId}/registreJustificant", method = RequestMethod.GET)
	public String registreJustific(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			Model model) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			RegistreAnnexDto justificant = registreService.getRegistreJustificant(entitatActual.getId(), bustiaId, registreId);
			model.addAttribute(
					"justificant",
					justificant);
			model.addAttribute("bustiaId",
					bustiaId);			
			
		} catch(Exception ex) {
			logger.error("Error recuperant informació del justificant", ex);
			model.addAttribute("missatgeError", ex.getMessage());
			return "ajaxErrorPage";
		}
		return "registreJustificant";
	}

	@RequestMapping(value = "/contingut/{bustiaId}/registre/{registreId}/arxiuInfo", method = RequestMethod.GET)
	public String arxiuInfo(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			Model model) {
		try {
			getEntitatActualComprovantPermisos(request);
			ArxiuDetallDto arxiuDetall = registreService.getArxiuDetall(registreId);
			model.addAttribute(
					"arxiuDetall",
					arxiuDetall);
		} catch(Exception ex) {
			logger.error("Error recuperant informació de l'arxiu", ex);
			model.addAttribute("missatgeError", ex.getMessage());
			return "ajaxErrorPage";
		}
		return "arxiuInfo";
	}

	@RequestMapping(value = "/contingut/{bustiaId}/registre/{registreId}/llegir", method = RequestMethod.GET)
	public String registreMarcarLlegida(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		registreService.marcarLlegida(
				entitatActual.getId(),
				bustiaId,
				registreId);
		SessioHelper.marcatLlegit(request);		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../",
				"contingut.registre.missatge.anotacio.marcada");
	}

	@RequestMapping(value = "/contingut/{bustiaId}/registre/{registreId}/annex/{annexId}/arxiu/{tipus}", method = RequestMethod.GET)
	public String descarregar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			@PathVariable Long annexId,
			@PathVariable String tipus) throws IOException {
		try {
			FitxerDto fitxer = registreService.getAnnexFitxer(annexId);
			writeFileToResponse(
					fitxer.getNom(),
					fitxer.getContingut(),
					response);
		} catch (Exception ex) {
			logger.error("Error descarregant el document", ex);
			return getModalControllerReturnValueError(
					request,
					"/contingut/" + bustiaId + "/registre/" + registreId,
					"contingut.controller.document.descarregar.error");
		}
		return null;
	}

	@RequestMapping(value = "/contingut/{bustiaId}/registre/{registreId}/justificant", method = RequestMethod.GET)
	public String descarregarJustificant(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId) throws IOException {
		FitxerDto fitxer = registreService.getJustificant(registreId);
		writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);
		return null;
	}

	@RequestMapping(value = "/contingut/{bustiaId}/registre/{registreId}/annex/{annexId}/firma/{firmaIndex}", method = RequestMethod.GET)
	public String descarregarFirma(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			@PathVariable Long annexId,
			@PathVariable int firmaIndex) throws IOException {
		FitxerDto fitxer = registreService.getAnnexFirmaFitxer(annexId,
				firmaIndex);
		writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);
		return null;
	}

	@RequestMapping(value = "/contingut/{bustiaId}/registre/{registreId}/reintentar", method = RequestMethod.GET)
	public String reintentar(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		boolean processatOk = registreService.reintentarProcessamentUser(
				entitatActual.getId(),
				bustiaId,
				registreId);
		if (processatOk) {
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../" + bustiaId,
					"contingut.admin.controller.registre.reintentat.ok");
		} else {
			MissatgesHelper.error(
					request,
					getMessage(
							request, 
							"contingut.admin.controller.registre.reintentat.error",
							null));
			return "redirect:../" + registreId;
		}
	}

	@RequestMapping(value = "/contingut/{bustiaId}/log", method = RequestMethod.GET)
	public String log(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"contingut",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						bustiaId,
						true,
						false));
		model.addAttribute(
				"logs",
				contingutService.findLogsPerContingutUser(
						entitatActual.getId(),
						bustiaId));
		model.addAttribute(
				"moviments",
				contingutService.findMovimentsPerContingutUser(
						entitatActual.getId(),
						bustiaId));
		model.addAttribute(
				"logTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						LogTipusEnumDto.class,
						"log.tipus.enum."));
		model.addAttribute(
				"logObjecteTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						LogObjecteTipusEnumDto.class,
						"log.objecte.tipus.enum."));
		return "contingutLog";
	}

	@RequestMapping(value = "/contingut/{bustiaId}/log/{contingutLogId}/detalls", method = RequestMethod.GET)
	@ResponseBody
	public ContingutLogDetallsDto logDetalls(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long contingutLogId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return contingutService.findLogDetallsPerContingutUser(
				entitatActual.getId(),
				bustiaId,
				contingutLogId);
	}

	@RequestMapping(value = "/contingut/{bustiaId}/comentaris", method = RequestMethod.GET)
	public String comentaris(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"contingut",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						bustiaId,
						true,
						false));
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		model.addAttribute(
				"usuariActual",
				usuariActual);
		return "contingutComentaris";
	}

	@RequestMapping(value = "/contingut/{registreId}/comentaris/publicar", method = RequestMethod.POST)
	@ResponseBody
	public List<ContingutComentariDto> publicarComentari(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@RequestParam String text,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (text != null && !text.isEmpty()) {
			contingutService.publicarComentariPerContingut(entitatActual.getId(), registreId, text);
		}
		return contingutService.findComentarisPerContingut(
				entitatActual.getId(), 
				registreId);
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}

	private static final Logger logger = LoggerFactory.getLogger(ContingutController.class);

}
