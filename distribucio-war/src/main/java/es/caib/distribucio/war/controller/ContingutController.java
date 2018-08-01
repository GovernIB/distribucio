/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.dto.AlertaDto;
import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.ContingutComentariDto;
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.ContingutLogDetallsDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.distribucio.core.api.dto.LogTipusEnumDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDetallDto;
import es.caib.distribucio.core.api.dto.UsuariDto;
import es.caib.distribucio.core.api.registre.RegistreTipusEnum;
import es.caib.distribucio.core.api.service.AlertaService;
import es.caib.distribucio.core.api.service.AplicacioService;
import es.caib.distribucio.core.api.service.BustiaService;
import es.caib.distribucio.core.api.service.ContingutService;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.war.command.ContingutMoureCopiarEnviarCommand;
import es.caib.distribucio.war.helper.DatatablesHelper;
import es.caib.distribucio.war.helper.DatatablesHelper.DatatablesResponse;
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

	private static final String CONTENIDOR_VISTA_ICONES = "icones";
	private static final String CONTENIDOR_VISTA_LLISTAT = "llistat";

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ContingutService contingutService;
	@Autowired
	private BustiaService bustiaService;
	@Autowired
	private RegistreService registreService;
	@Autowired
	private AlertaService alertaService;

	@RequestMapping(value = "/contingut/{contingutId}", method = RequestMethod.GET)
	public String contingutGet(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutId,
				true,
				true);
		omplirModelPerMostrarContingut(
				request,
				entitatActual,
				contingut,
				SessioHelper.desmarcarLlegit(request),
				model);
		return "contingut";
	}

	@RequestMapping(value = "/contingut/{contingutId}/canviVista/icones", method = RequestMethod.GET)
	public String canviVistaLlistat(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		SessioHelper.updateContenidorVista(
				request,
				CONTENIDOR_VISTA_ICONES);
		return "redirect:../../" + contingutId;
	}
	@RequestMapping(value = "/contingut/{contingutId}/canviVista/llistat", method = RequestMethod.GET)
	public String canviVistaIcones(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		SessioHelper.updateContenidorVista(
				request,
				CONTENIDOR_VISTA_LLISTAT);
		return "redirect:../../" + contingutId;
	}

	

	@RequestMapping(value = "/contingut/{contingutOrigenId}/enviar", method = RequestMethod.GET)
	public String enviarForm(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		omplirModelPerEnviar(
				entitatActual,
				contingutOrigenId,
				model);
		ContingutMoureCopiarEnviarCommand command = new ContingutMoureCopiarEnviarCommand();
		command.setOrigenId(contingutOrigenId);
		model.addAttribute(command);
		return "contingutEnviarForm";
	}
	@RequestMapping(value = "/contingut/{contingutOrigenId}/enviar", method = RequestMethod.POST)
	public String enviar(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			@Valid ContingutMoureCopiarEnviarCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			omplirModelPerEnviar(
					entitatActual,
					contingutOrigenId,
					model);
			return "contingutEnviarForm";
		}
		bustiaService.enviarContingut(
				entitatActual.getId(),
				command.getDestiId(),
				contingutOrigenId,
				command.getComentariEnviar());
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../" + contingutOrigenId,
				"contingut.controller.element.enviat.ok");
	}

	@RequestMapping(value = "/contingut/{contingutId}/errors/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse errorsDatatable(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		return DatatablesHelper.getDatatableResponse(
				request,
				alertaService.findPaginatByLlegida(
						false,
						contingutId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
	}
	
	@RequestMapping(value = "/contingut/{contingutId}/errors/{alertaId}/llegir", method = RequestMethod.GET)
	@ResponseBody
	public void llegirAlerta(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long alertaId,
			Model model) {
		AlertaDto alerta = alertaService.find(alertaId);
		alerta.setLlegida(true);
		alertaService.update(alerta);
	}

	@RequestMapping(value = "/contingut/{contingutId}/registre/{registreId}", method = RequestMethod.GET)
	public String registreInfo(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long registreId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"registre",
				registreService.findOne(
						entitatActual.getId(),
						contingutId,
						registreId));

		model.addAttribute("contingutId",
				contingutId);

		return "registreDetall";
	}
	
	
	
	@RequestMapping(value = "/contingut/ajax/{contingutId}/registre/{registreId}/annex/{fitxerArxiuUuid}/registreFirmes", method = RequestMethod.GET)
	public String registreAnnexFirmes(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long registreId,
			@PathVariable String fitxerArxiuUuid,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		model.addAttribute(
				"annex",
				registreService.getAnnexFirmesAmbArxiu(
						entitatActual.getId(),
						contingutId,
						registreId,
						fitxerArxiuUuid));
		model.addAttribute("registreId", registreId);
		model.addAttribute("fitxerArxiuUuid", fitxerArxiuUuid);

		return "registreAnnexFirmes";
	}
	
	
	@RequestMapping(value = "/contingut/ajax/{contingutId}/registre/{registreId}/annex/{fitxerArxiuUuid}", method = RequestMethod.GET)
	public String registreAnnex(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long registreId,
			@PathVariable String fitxerArxiuUuid,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

		model.addAttribute(
				"annex",
				registreService.getAnnexAmbArxiu(
						entitatActual.getId(),
						contingutId,
						registreId,
						fitxerArxiuUuid));
		model.addAttribute("registreId", registreId);


		return "registreAnnex";
	}
	
	@RequestMapping(value = "/contingut/ajax/{contingutId}/registre/{registreId}/registreJustificant", method = RequestMethod.GET)
	public String registreJustific(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long registreId,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		RegistreAnnexDetallDto justificant = registreService.getRegistreJustificant(entitatActual.getId(), contingutId, registreId);
		
		model.addAttribute("justificant",
				justificant);
		
		return "registreJustificant";
	}
	
	@RequestMapping(value = "/contingut/{contingutId}/registre/{registreId}/llegir", method = RequestMethod.GET)
	public String registreMarcarLlegida(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long registreId,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		registreService.marcarLlegida(
				entitatActual.getId(),
				contingutId,
				registreId);
		SessioHelper.marcatLlegit(request);		
		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../",
				"contingut.registre.missatge.anotacio.marcada");
	}
	
	@RequestMapping(value = "/contingut/{contingutId}/registre/{registreId}/annex/{annexId}/arxiu/{tipus}", method = RequestMethod.GET)
	public String descarregar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long contingutId,
			@PathVariable Long registreId,
			@PathVariable Long annexId,
			@PathVariable String tipus) throws IOException {
		FitxerDto fitxer = registreService.getArxiuAnnex(annexId);
		writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);
		return null;
	}
	
	@RequestMapping(value = "/contingut/{contingutId}/registre/{registreId}/justificant", method = RequestMethod.GET)
	public String descarregarJustificant(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long contingutId,
			@PathVariable Long registreId) throws IOException {
		FitxerDto fitxer = registreService.getJustificant(registreId);
		writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);
		return null;
	}
	
	@RequestMapping(value = "/contingut/{contingutId}/registre/{registreId}/annex/{annexId}/firma/{firmaIndex}", method = RequestMethod.GET)
	public String descarregarFirma(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long contingutId,
			@PathVariable Long registreId,
			@PathVariable Long annexId,
			@PathVariable int firmaIndex) throws IOException {
		FitxerDto fitxer = registreService.getAnnexFirmaContingut(annexId,
				firmaIndex);
		
		writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);
		return null;
	}
	
	
	@RequestMapping(value = "/contingut/{contingutId}/registre/{registreId}/reintentar", method = RequestMethod.GET)
	public String reintentar(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long registreId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		boolean processatOk = registreService.reglaReintentarUser(
				entitatActual.getId(),
				contingutId,
				registreId);
		if (processatOk) {
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../" + contingutId,
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
	
	@RequestMapping(value = "/contingut/{contingutId}/log", method = RequestMethod.GET)
	public String log(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"contingut",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						contingutId,
						true,
						false));
		model.addAttribute(
				"logs",
				contingutService.findLogsPerContingutUser(
						entitatActual.getId(),
						contingutId));
		model.addAttribute(
				"moviments",
				contingutService.findMovimentsPerContingutUser(
						entitatActual.getId(),
						contingutId));
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

	@RequestMapping(value = "/contingut/{contingutId}/log/{contingutLogId}/detalls", method = RequestMethod.GET)
	@ResponseBody
	public ContingutLogDetallsDto logDetalls(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long contingutLogId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return contingutService.findLogDetallsPerContingutUser(
				entitatActual.getId(),
				contingutId,
				contingutLogId);
	}

	@RequestMapping(value = "/contingut/{contingutId}/comentaris", method = RequestMethod.GET)
	public String comentaris(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"contingut",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						contingutId,
						true,
						false));
		
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		
		model.addAttribute(
				"usuariActual",
				usuariActual);
		
		return "contingutComentaris";
	}
	
	@RequestMapping(value = "/contingut/{contingutId}/comentaris/publicar", method = RequestMethod.POST)
	@ResponseBody
	public List<ContingutComentariDto> publicarComentari(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@RequestParam String text,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		if (text != null && !text.isEmpty()) {
			contingutService.publicarComentariPerContingut(entitatActual.getId(), contingutId, text);
		}
			
		return contingutService.findComentarisPerContingut(
				entitatActual.getId(), 
				contingutId);
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}



	private void omplirModelPerMostrarContingut(
			HttpServletRequest request,
			EntitatDto entitatActual,
			ContingutDto contingut,
			boolean pipellaAnotacionsRegistre,
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		model.addAttribute("contingut", contingut);
		String contingutVista = SessioHelper.getContenidorVista(request);
		if (contingutVista == null)
			contingutVista = CONTENIDOR_VISTA_ICONES;
		model.addAttribute(
				"vistaIcones",
				new Boolean(CONTENIDOR_VISTA_ICONES.equals(contingutVista)));
		model.addAttribute(
				"vistaLlistat",
				new Boolean(CONTENIDOR_VISTA_LLISTAT.equals(contingutVista)));
		model.addAttribute(
				"registreTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						RegistreTipusEnum.class,
						"registre.anotacio.tipus.enum."));
		model.addAttribute(
				"pluginArxiuActiu",
				aplicacioService.isPluginArxiuActiu());
		model.addAttribute("pipellaAnotacionsRegistre", pipellaAnotacionsRegistre);
	}

	private void omplirModelPerEnviar(
			EntitatDto entitatActual,
			Long contingutOrigenId,
			Model model) {
		ContingutDto contingutOrigen = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutOrigenId,
				true,
				false);
		model.addAttribute(
				"contingutOrigen",
				contingutOrigen);
		List<BustiaDto> busties = bustiaService.findActivesAmbEntitat(
				entitatActual.getId());
		model.addAttribute(
				"busties",
				busties);
		model.addAttribute(
				"arbreUnitatsOrganitzatives",
				bustiaService.findArbreUnitatsOrganitzatives(
						entitatActual.getId(),
						true,
						false,
						true));
	}

}
