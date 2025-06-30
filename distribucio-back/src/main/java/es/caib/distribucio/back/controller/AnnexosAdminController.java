/**
 * 
 */
package es.caib.distribucio.back.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import es.caib.distribucio.back.command.AnnexosFiltreCommand;
import es.caib.distribucio.back.helper.AjaxHelper;
import es.caib.distribucio.back.helper.AjaxHelper.AjaxFormResponse;
import es.caib.distribucio.back.helper.DatatablesHelper;
import es.caib.distribucio.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.back.helper.MissatgesHelper;
import es.caib.distribucio.back.helper.RequestSessionHelper;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.RegistreCopiesDto;
import es.caib.distribucio.logic.intf.dto.ResultatAnnexDefinitiuDto;
import es.caib.distribucio.logic.intf.service.AnnexosService;
import es.caib.distribucio.logic.intf.service.ConfigService;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnnexEstat;

/**
 * Controlador per a la consulta d'arxius pels administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/annexosAdmin")
public class AnnexosAdminController extends BaseAdminController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "AnnexosAdminController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "AnnexosAdminController.session.seleccio";

	@Autowired
	private AnnexosService annexosService;
	@Autowired
	private ConfigService configService;


	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		getEntitatActualComprovantPermisAdminLectura(request);
		AnnexosFiltreCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute("concsvBaseUrl", configService.getConcsvBaseUrl());
		model.addAttribute(
				filtreCommand);
		return "annexosAdminList";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String annexFiltre(
			HttpServletRequest request,
			@Valid AnnexosFiltreCommand annexosFiltreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio,
			Model model) {
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE,
						annexosFiltreCommand);
			}
		}
		model.addAttribute("concsvBaseUrl", configService.getConcsvBaseUrl());
		return "redirect:annexosAdmin";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {		
		AnnexosFiltreCommand filtreCommand = getFiltreCommand(request);
		EntitatDto entitatActual = this.getEntitatActualComprovantPermisAdmin(request);
		
		return DatatablesHelper.getDatatableResponse(
				request,
				annexosService.findAdmin(
						entitatActual.getId(),
						AnnexosFiltreCommand.asDto(filtreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id",
				SESSION_ATTRIBUTE_SELECCIO);
	}

	@RequestMapping(value = "/{id}/guardarDefinitiu", method = RequestMethod.GET)
	public String guardarDefinitiu(
			HttpServletRequest request,
			@PathVariable Long id,
			boolean multiple,
			Model model) {		
		
		ResultatAnnexDefinitiuDto resultatAnnexDefinitiu = null;
		
		try {
			resultatAnnexDefinitiu = annexosService.guardarComADefinitiu(id);
		} catch (Exception e) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							resultatAnnexDefinitiu.getKeyMessage(),
							new Object[] {resultatAnnexDefinitiu.getAnnexTitol(), resultatAnnexDefinitiu.getAnotacioNumero(), resultatAnnexDefinitiu.getThrowable()}
					));
		}
		
		if (resultatAnnexDefinitiu.isOk()) {	
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							resultatAnnexDefinitiu.getKeyMessage(),
							new Object[] {resultatAnnexDefinitiu.getAnnexTitol(), resultatAnnexDefinitiu.getAnotacioNumero()}
					));
		} else if (resultatAnnexDefinitiu.getThrowable() != null) {
			MissatgesHelper.error(
					request, 
					getMessage(
							request, 
							resultatAnnexDefinitiu.getKeyMessage(),
							new Object[] {resultatAnnexDefinitiu.getAnnexTitol(), resultatAnnexDefinitiu.getAnotacioNumero(), resultatAnnexDefinitiu.getThrowable()}
					));
		} else {
			MissatgesHelper.warning(
					request, 
					getMessage(
							request, 
							resultatAnnexDefinitiu.getKeyMessage(),
							new Object[] {resultatAnnexDefinitiu.getAnnexTitol(), resultatAnnexDefinitiu.getAnotacioNumero()}
					));
		}			
		
		if (!multiple) {
			return "redirect:../../annexosAdmin";
		}
		
		return "";
	}
	
	@RequestMapping(value = {"/copies/**"}, method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public RegistreCopiesDto copiesRegistre(
			HttpServletRequest request,
			Model model) {
		RegistreCopiesDto resultat = new RegistreCopiesDto();
		List<Integer> copies = new ArrayList<Integer>();
		String numero = extractNumero(request);
		
		if (numero != null)
			copies = annexosService.findCopiesRegistre(numero);
		
		if (! copies.isEmpty())
			resultat.setRegistreTrobat(true);
		
		if (copies.isEmpty() || numero == null)
			copies = Arrays.asList(0,1,2,3,4,5,6,7,8,9,10);
		
		resultat.setCopies(copies);
		
		return resultat;
	}
	
	protected String extractNumero(HttpServletRequest request) {

		var url = request.getRequestURL().toString();
		var urlArr = url.split("/copies");
		return urlArr.length > 1 ? urlArr[1].substring(1) : null;
	}
	
//	@RequestMapping(value = "/guardarDefinitiuMultiple", method = RequestMethod.GET)
//	public String guardarDefinitiuMultiple(
//			HttpServletRequest request,
//			Model model) {		
//		
//		List<Long> ids = this.getRegistresSeleccionats(request, SESSION_ATTRIBUTE_SELECCIO);	
//		
//		for (Long id: ids) {
//			this.guardarDefinitiu(request, id, true, model);
//		}
//		
//		if (ids.isEmpty()) {
//			MissatgesHelper.warning(
//					request, 
//					getMessage(
//							request, 
//							"annexos.admin.missatge.fila.no.seleccionada"
//					));
//			
//		}
//
//		RequestSessionHelper.actualitzarObjecteSessio(
//				request,
//				SESSION_ATTRIBUTE_SELECCIO,
//				null);		
//		
//		return "redirect:../annexosAdmin";
//	}
//	
//	@RequestMapping(value = "/guardarDefinitiuMultiple", method = RequestMethod.GET)
//	public String reintentarProcessamentMultipleGet(
//			HttpServletRequest request,
//			Model model) {
//		Object command = new Object();
//		model.addAttribute("processamentAnnexosMultiple", command);
//		model.addAttribute("annexos",
//				annexosService.findMultiple(
//						getEntitatActualComprovantPermisAdmin(request).getId(),
//						this.getRegistresSeleccionats(request, SESSION_ATTRIBUTE_SELECCIO),
//						true));
//		return "processamentAnnexosMultiple";
//	}
//	
	/** Mèdode per reprocessar una anotacions de registre via ajax des del llistat d'anotacions
	 * de l'administrador.
	 * @param request
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/processamentAjax/{annexId}", method = RequestMethod.POST)
	public AjaxFormResponse reintentarProcessamentAjaxPost(
			HttpServletRequest request,
			@PathVariable Long annexId,
			@Valid Object command,
			BindingResult bindingResult) {
		AjaxFormResponse response;
		if (bindingResult.hasErrors()) {
			response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
			response.setMissatge(getMessage(request, "processamentMultiple.error.validacio"));
			return response;
		}
		try {
			this.entrarSemafor(annexId);
			response = this.processament(request, annexId);
		} catch(InterruptedException e) {
			response = AjaxHelper.generarAjaxError("Error no controlat reintentant el processament: " + e.getMessage());
		} finally {
			this.sortirSemafor(annexId);
		}
		logger.debug("L'annex amb id " + annexId + " s'ha processat " + (response.isEstatOk() ? "correctament." : "amb error.") + response.getMissatge());
		return response;
	}

	private AjaxFormResponse processament(HttpServletRequest request, Long annexId) {
		AjaxFormResponse response = null;
		
		logger.debug("Reprocessar annex amb id " + annexId);
			
		ResultatAnnexDefinitiuDto resultatAnnexDefinitiu = annexosService.guardarComADefinitiu(annexId);
		
		String missatge = getMessage(
				request, 
				resultatAnnexDefinitiu.getKeyMessage(), 
				new Object[] {resultatAnnexDefinitiu.getAnnexTitol(), resultatAnnexDefinitiu.getAnotacioNumero()});
		
		if (resultatAnnexDefinitiu.getThrowable() != null) {
			missatge = getMessage(
					request, 
					resultatAnnexDefinitiu.getKeyMessage(), 
					new Object[] {resultatAnnexDefinitiu.getAnnexTitol(), resultatAnnexDefinitiu.getAnotacioNumero(), resultatAnnexDefinitiu.getThrowable()});
		} else {
			missatge = getMessage(
					request, 
					resultatAnnexDefinitiu.getKeyMessage(), 
					new Object[] {resultatAnnexDefinitiu.getAnnexTitol(), resultatAnnexDefinitiu.getAnotacioNumero()});
		}	
		
		if (resultatAnnexDefinitiu.isOk()) {
			response = AjaxHelper.generarAjaxFormOk();
			response.setMissatge(missatge.toString());
		} else {
			response = AjaxHelper.generarAjaxError(missatge);
		}
		return response;
	}
	
	@RequestMapping(value = "/select", method = RequestMethod.GET)
	@ResponseBody
	public int select(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO,
					seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				seleccio.add(id);
			}
		} else {				
			AnnexosFiltreCommand filtreCommand = getFiltreCommand(request);
			seleccio.addAll(
				annexosService.findAnnexIds(AnnexosFiltreCommand.asDto(filtreCommand))
			);
		}
		return seleccio.size();
	}
	
	@RequestMapping(value = "/deselect", method = RequestMethod.GET)
	@ResponseBody
	public int deselect(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) Long[] ids) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio == null) {
			seleccio = new HashSet<Long>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO,
					seleccio);
		}
		if (ids != null) {
			for (Long id: ids) {
				seleccio.remove(id);
			}
		} else {
			seleccio.clear();
		}
		return seleccio.size();
	}

	
//	@RequestMapping(value = "/{id}/guardarDefinitiu", method = RequestMethod.GET)
//	public AjaxFormResponse guardarDefinitiu(
//			HttpServletRequest request,
//			@PathVariable Long id,
//			Model model) {
//		AjaxFormResponse response;		
//		
//		StringBuilder missatge = new StringBuilder();
//		boolean correcte = true;
//		
//		// Marca com a definitiu
//		if (correcte) {
//			try {
//				String rolActual = RolHelper.getRolActual(request);
//				annexosService.guardarComADefinitiu(					 
//						id);
//				missatge.append(getMessage(request, "annexos.accio.guardar.definitiu.ok"));	
//			} catch (Exception exception) {
//				correcte = false;
//				missatge.append(
//						getMessage(
//								request, 
//								"annexos.accio.guardar.definitiu",
//								new Object[] {ExceptionUtils.getRootCauseMessage(exception)}));
//			}
//		} else {
//			missatge.append(getMessage(request, "annexos.accio.guardar.definitiu.ko"));
//		}
//		
//		if (correcte) {
//			response = AjaxHelper.generarAjaxFormOk();
//			response.setMissatge(getMessage(request, missatge.toString()));
//		} else {
//			response = AjaxHelper.generarAjaxError(missatge.toString());
//		}
//		return response;
//		
//		
//		
//	}
	
//	@RequestMapping(value = "/{contingutId}/detall", method = RequestMethod.GET)
//	public String detall(
//			HttpServletRequest request,
//			@PathVariable Long contingutId,
//			Model model) {
//		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
//		
//		ContingutDto contingutDto = contingutService.findAmbIdAdmin(
//				entitatActual.getId(),
//				contingutId,
//				true);
//
//		model.addAttribute(
//				"contingut",
//				contingutDto);
//
//		switch (contingutDto.getTipus()) {
//		case BUSTIA:
//			model.addAttribute(
//					"bustia",
//					contingutDto);
//			return "bustiaAdminDetall";
//
//		case REGISTRE:
//			model.addAttribute(
//					"registre",
//					contingutDto);
//			
//			model.addAttribute(
//					"registreId",
//					contingutDto.getId());
//						
//			model.addAttribute(
//					"isContingutAdmin",
//					true);
//			
//			// Dades del procediment
//			String codiSia = ((RegistreDto)contingutDto).getProcedimentCodi();
//			if (codiSia != null) {
//				ProcedimentDto procediment = procedimentService.findByCodiSia(entitatActual.getId(), codiSia);
//				if (procediment == null) {
//					procediment = new ProcedimentDto();
//					procediment.setCodi(codiSia);
//					procediment.setCodiSia(codiSia);
//					procediment.setNom(getMessage(request, "registre.detalls.camp.procediment.no.trobat", new Object[] {codiSia}));
//				}
//				model.addAttribute("procedimentDades", procediment);				
//			}
//
//			return "registreDetall";
//		}
//
//		return null;
//	}
//
//	@RequestMapping(value = "/{contingutId}/log", method = RequestMethod.GET)
//	public String log(
//			HttpServletRequest request,
//			@PathVariable Long contingutId,
//			Model model) {
//		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
//		model.addAttribute(
//				"isPanelUser",
//				false);
//		model.addAttribute(
//				"logs",
//				contingutService.findLogsPerContingutAdmin(
//						entitatActual.getId(),
//						contingutId));
//		model.addAttribute(
//				"moviments",
//				contingutService.findMovimentsPerContingutAdmin(
//						entitatActual.getId(),
//						contingutId));
//		
//		model.addAttribute(
//				"contingut",
//				contingutService.findAmbIdAdmin(
//						entitatActual.getId(),
//						contingutId,
//						true));
//		return "contingutLog";
//	}
//	
//	
//	@RequestMapping(value = "/{contingutId}/log/{contingutLogId}/detalls", method = RequestMethod.GET)
//	@ResponseBody
//	public ContingutLogDetallsDto logDetalls(
//			HttpServletRequest request,
//			@PathVariable Long contingutId,
//			@PathVariable Long contingutLogId,
//			Model model) {
//		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
//		return contingutService.findLogDetallsPerContingutUser(
//				entitatActual.getId(),
//				contingutId,
//				contingutLogId);
//	}
//
//
//	@InitBinder
//	protected void initBinder(WebDataBinder binder) {
//		binder.registerCustomEditor(
//				Date.class,
//				new CustomDateEditor(
//						new SimpleDateFormat("dd/MM/yyyy"),
//						true));
//	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
	
	private AnnexosFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		AnnexosFiltreCommand filtreCommand = (AnnexosFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new AnnexosFiltreCommand();		
			filtreCommand.setArxiuEstat(AnnexEstat.ESBORRANY);
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

	private static final Logger logger = LoggerFactory.getLogger(AnnexosAdminController.class);
}
