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

import es.caib.ripea.core.api.dto.AlertaDto;
import es.caib.ripea.core.api.dto.BustiaDto;
import es.caib.ripea.core.api.dto.ContingutComentariDto;
import es.caib.ripea.core.api.dto.ContingutDto;
import es.caib.ripea.core.api.dto.ContingutLogDetallsDto;
import es.caib.ripea.core.api.dto.DocumentEnviamentEstatEnumDto;
import es.caib.ripea.core.api.dto.EntitatDto;
import es.caib.ripea.core.api.dto.ExpedientDto;
import es.caib.ripea.core.api.dto.FitxerDto;
import es.caib.ripea.core.api.dto.InteressatDto;
import es.caib.ripea.core.api.dto.InteressatTipusEnumDto;
import es.caib.ripea.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.ripea.core.api.dto.LogTipusEnumDto;
import es.caib.ripea.core.api.dto.NodeDto;
import es.caib.ripea.core.api.dto.RegistreAnnexDetallDto;
import es.caib.ripea.core.api.dto.RegistreAnotacioDto;
import es.caib.ripea.core.api.dto.UsuariDto;
import es.caib.ripea.core.api.registre.RegistreTipusEnum;
import es.caib.ripea.core.api.service.AlertaService;
import es.caib.ripea.core.api.service.AplicacioService;
import es.caib.ripea.core.api.service.BustiaService;
import es.caib.ripea.core.api.service.ContingutService;
import es.caib.ripea.core.api.service.ExpedientInteressatService;
import es.caib.ripea.core.api.service.ExpedientService;
import es.caib.ripea.core.api.service.MetaDadaService;
import es.caib.ripea.core.api.service.MetaDocumentService;
import es.caib.ripea.core.api.service.MetaExpedientService;
import es.caib.ripea.core.api.service.RegistreService;
import es.caib.ripea.war.command.ContingutMoureCopiarEnviarCommand;
import es.caib.ripea.war.helper.BeanGeneratorHelper;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.EnumHelper;
import es.caib.ripea.war.helper.MissatgesHelper;
import es.caib.ripea.war.helper.SessioHelper;

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
	private MetaExpedientService metaExpedientService;
	@Autowired
	private MetaDocumentService metaDocumentService;
	@Autowired
	private ExpedientInteressatService interessatService;
	@Autowired
	private ExpedientService expedientService;
	@Autowired
	private MetaDadaService metaDadaService;
	@Autowired
	private BustiaService bustiaService;
	@Autowired
	private RegistreService registreService;
	@Autowired
	private AlertaService alertaService;

	@Autowired
	private BeanGeneratorHelper beanGeneratorHelper;



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

	@RequestMapping(value = "/contingut/{contingutId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutId,
				true,
				false);
		contingutService.deleteReversible(
				entitatActual.getId(),
				contingutId);
		return getAjaxControllerReturnValueSuccess(
				request,
				(contingut.getPare() != null) ? "redirect:../../contingut/" + contingut.getPare().getId() : "redirect:../../escriptori",
				"contingut.controller.element.esborrat.ok");
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

	@RequestMapping(value = "/contingut/{contingutOrigenId}/moure", method = RequestMethod.GET)
	public String moureForm(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		omplirModelPerMoureOCopiar(
				entitatActual,
				contingutOrigenId,
				model);
		ContingutMoureCopiarEnviarCommand command = new ContingutMoureCopiarEnviarCommand();
		command.setOrigenId(contingutOrigenId);
		model.addAttribute(command);
		return "contingutMoureForm";
	}
	@RequestMapping(value = "/contingut/{contingutOrigenId}/moure", method = RequestMethod.POST)
	public String moure(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			@Valid ContingutMoureCopiarEnviarCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			omplirModelPerMoureOCopiar(
					entitatActual,
					contingutOrigenId,
					model);
			return "contingutMoureForm";
		}
		contingutService.move(
				entitatActual.getId(),
				contingutOrigenId,
				command.getDestiId());
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../" + contingutOrigenId,
				"contingut.controller.element.mogut.ok");
	}
	@RequestMapping(value = "/contingut/{contingutOrigenId}/moure/{contingutDestiId}", method = RequestMethod.GET)
	public String moureDragDrop(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			@PathVariable Long contingutDestiId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contingutOrigen = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutOrigenId,
				true,
				false);
		contingutService.move(
				entitatActual.getId(),
				contingutOrigenId,
				contingutDestiId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../" + contingutOrigen.getPare().getId(),
				"contingut.controller.element.mogut.ok");
	}

	@RequestMapping(value = "/contingut/{contingutOrigenId}/copiar", method = RequestMethod.GET)
	public String copiarForm(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		omplirModelPerMoureOCopiar(
				entitatActual,
				contingutOrigenId,
				model);
		ContingutMoureCopiarEnviarCommand command = new ContingutMoureCopiarEnviarCommand();
		command.setOrigenId(contingutOrigenId);
		model.addAttribute(command);
		return "contingutCopiarForm";
	}
	@RequestMapping(value = "/contingut/{contingutOrigenId}/copiar", method = RequestMethod.POST)
	public String copiar(
			HttpServletRequest request,
			@PathVariable Long contingutOrigenId,
			@Valid ContingutMoureCopiarEnviarCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			omplirModelPerMoureOCopiar(
					entitatActual,
					contingutOrigenId,
					model);
			return "contingutCopiarForm";
		}
		ContingutDto contingutCreat = contingutService.copy(
				entitatActual.getId(),
				contingutOrigenId,
				command.getDestiId(),
				true);
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../" + contingutCreat.getId(),
				"contingut.controller.element.copiat.ok");
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

	@RequestMapping(value = "/contingut/{contingutId}/errors", method = RequestMethod.GET)
	public String errors(
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
				"errors",
				contingutService.findErrorsValidacio(
						entitatActual.getId(),
						contingutId));
		return "contingutErrors";
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

	@RequestMapping(value = "/contingut/{contingutId}/registre/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse registreDatatable(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<RegistreAnotacioDto> registres = null;
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutId,
				true,
				false);
		if (contingut instanceof ExpedientDto) {
			ExpedientDto expedient = (ExpedientDto)contingut;
			registres = expedient.getFillsRegistres();
		}
		return DatatablesHelper.getDatatableResponse(
				request,
				registres);
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
	/*@RequestMapping(value = "/contingut/{contingutId}/registre/{registreId}/log", method = RequestMethod.GET)
	public String registreLog(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@PathVariable Long registreId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"contingut",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						contingutId,
						true));
		model.addAttribute(
				"registre",
				registreService.findOne(
						entitatActual.getId(),
						contingutId,
						registreId));
		model.addAttribute(
				"moviments",
				contingutService.findMovimentsPerContingutUser(
						entitatActual.getId(),
						registreId));
		return "registreLog";
	}*/
	
	@RequestMapping(value = "/contingut/{contingutId}/interessat/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse interessatDatatable(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<InteressatDto> interessats = null;
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutId,
				true,
				false);
		if (contingut instanceof ExpedientDto) {
			interessats = interessatService.findByExpedient(
					entitatActual.getId(),
					contingutId);
		}
		return DatatablesHelper.getDatatableResponse(
				request,
				interessats);
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

	@RequestMapping(value = "/contingut/{contingutId}/arxiu")
	public String arxiu(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ContingutDto contingut = contingutService.findAmbIdUser(
				entitatActual.getId(),
				contingutId,
				false,
				false);
		model.addAttribute("contingut", contingut);
		if (contingut.isReplicatDinsArxiu()) {
			model.addAttribute(
					"arxiuDetall",
					contingutService.getArxiuDetall(
							entitatActual.getId(),
							contingutId));
		}
		return "contingutArxiu";
	}

	@RequestMapping(value = "/contingut/{contingutId}/exportar", method = RequestMethod.GET)
	public String exportar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long contingutId) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		FitxerDto fitxer = contingutService.exportacioEni(
				entitatActual.getId(),
				contingutId);
		writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);
		return null;
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
		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId()));
		model.addAttribute(
				"metaDocuments",
				metaDocumentService.findActiveByEntitatAndContenidorPerCreacio(
						entitatActual.getId(),
						contingut.getId()));
		if (contingut instanceof ExpedientDto) {
			model.addAttribute(
					"interessats",
					interessatService.findByExpedient(
							entitatActual.getId(),
							contingut.getId()));
			model.addAttribute("relacionats", expedientService.relacioFindAmbExpedient(
					entitatActual.getId(),
					contingut.getId()));
		}
		/*if (contingut instanceof DocumentDto) {
			model.addAttribute(
					"documentVersions",
					documentService.findVersionsByDocument(
							entitatActual.getId(),
							contingut.getId()));
		}*/
		if (contingut instanceof NodeDto) {
			model.addAttribute(
					"metaDades",
					metaDadaService.findByNode(
							entitatActual.getId(),
							contingut.getId()));
			model.addAttribute(
					"dadesCommand",
					beanGeneratorHelper.generarCommandDadesNode(
							entitatActual.getId(),
							contingut.getId(),
							((NodeDto)contingut).getDades()));
		}
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
				"notificacioEstatEnumOptions",
				EnumHelper.getOptionsForEnum(
						DocumentEnviamentEstatEnumDto.class,
						"notificacio.estat.enum.",
						new Enum<?>[] {DocumentEnviamentEstatEnumDto.PROCESSAT}));
		model.addAttribute(
				"publicacioEstatEnumOptions",
				EnumHelper.getOptionsForEnum(
						DocumentEnviamentEstatEnumDto.class,
						"publicacio.estat.enum.",
						new Enum<?>[] {
							DocumentEnviamentEstatEnumDto.ENVIAT,
							DocumentEnviamentEstatEnumDto.PROCESSAT,
							DocumentEnviamentEstatEnumDto.CANCELAT}));
		model.addAttribute(
				"interessatTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						InteressatTipusEnumDto.class,
						"interessat.tipus.enum."));
		model.addAttribute(
				"pluginArxiuActiu",
				aplicacioService.isPluginArxiuActiu());
		model.addAttribute("pipellaAnotacionsRegistre", pipellaAnotacionsRegistre);
	}

	private void omplirModelPerMoureOCopiar(
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
