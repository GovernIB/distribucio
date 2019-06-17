/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.dto.AlertaDto;
import es.caib.distribucio.core.api.dto.BustiaContingutFiltreEstatEnumDto;
import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.service.AlertaService;
import es.caib.distribucio.core.api.service.BustiaService;
import es.caib.distribucio.core.api.service.ContingutService;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.war.command.BustiaUserFiltreCommand;
import es.caib.distribucio.war.command.ContingutReenviarCommand;
import es.caib.distribucio.war.command.MarcarProcessatCommand;
import es.caib.distribucio.war.command.RegistreClassificarCommand;
import es.caib.distribucio.war.command.RegistreClassificarCommand.Classificar;
import es.caib.distribucio.war.command.RegistreEnviarViaEmailCommand;
import es.caib.distribucio.war.helper.DatatablesHelper;
import es.caib.distribucio.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.war.helper.ElementsPendentsBustiaHelper;
import es.caib.distribucio.war.helper.MissatgesHelper;
import es.caib.distribucio.war.helper.RequestSessionHelper;

/**
 * Controlador per al manteniment de bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/bustiaUser")
public class BustiaUserController extends BaseUserController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "BustiaUserController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "BustiaUserController.session.seleccio";

	@Autowired
	private BustiaService bustiaService;
	@Autowired
	private RegistreService registreService;
	@Autowired
	private ContingutService contingutService;
	@Autowired
	private AlertaService alertaService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		BustiaUserFiltreCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute(
				filtreCommand);
		model.addAttribute("bustiesUsuari", bustiaService.findPermesesPerUsuari(entitatActual.getId(), filtreCommand.isMostrarInactives()));
		
		return "bustiaUserList";
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
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			BustiaUserFiltreCommand filtreCommand = getFiltreCommand(request);
			List<BustiaDto> bustiesUsuari = null;
			if (filtreCommand.getBustia() == null || filtreCommand.getBustia().isEmpty()) {
				bustiesUsuari = bustiaService.findPermesesPerUsuari(entitatActual.getId(), filtreCommand.isMostrarInactives());
			}
			seleccio.addAll(
					bustiaService.findIdsAmbFiltre(
							entitatActual.getId(),
							bustiesUsuari,
							BustiaUserFiltreCommand.asDto(filtreCommand)));
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
	
	
	@RequestMapping(value = "/metriques", method = RequestMethod.GET)
	public String bustiaMetriques2(
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		try{
			byte[] b = bustiaService.getApplictionMetrics().getBytes();
		
			writeFileToResponse(
					"metrics.json",
					b,
					response);
		} catch (Exception ex) {
			return getModalControllerReturnValueError(
					request,
					"redirect:.",
					"contingut.controller.document.descarregar.error");
		}
		return null;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String bustiaPost(
			HttpServletRequest request,
			@Valid BustiaUserFiltreCommand filtreCommand,
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
						filtreCommand);
			}
		}
		return "redirect:bustiaUser";
	}

	@RequestMapping(value = "/netejar", method = RequestMethod.GET)
	public String expedientNetejar(
			HttpServletRequest request,
			@PathVariable Long arxiuId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		RequestSessionHelper.esborrarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		return "redirect:bustiaUser";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		BustiaUserFiltreCommand bustiaUserFiltreCommand = getFiltreCommand(request);
		List<BustiaDto> bustiesUsuari = null;
		if (bustiaUserFiltreCommand.getBustia() == null || bustiaUserFiltreCommand.getBustia().isEmpty()) {
			bustiesUsuari = bustiaService.findPermesesPerUsuari(entitatActual.getId(), bustiaUserFiltreCommand.isMostrarInactives());
		}
		return DatatablesHelper.getDatatableResponse(
				request,
				bustiaService.contingutPendentFindByDatatable(
						entitatActual.getId(),
						bustiesUsuari,
						BustiaUserFiltreCommand.asDto(bustiaUserFiltreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
	}

	@RequestMapping(value = "/{bustiaId}/enviarByEmail/{contingutId}", method = RequestMethod.GET)
	public String bustiaEnviarByEmailGet(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long contingutId,
			Model model) {
		RegistreEnviarViaEmailCommand command = new RegistreEnviarViaEmailCommand();
		command.setBustiaId(bustiaId);
		command.setContingutId(contingutId);
		model.addAttribute(command);
		return "registreViaEmail";
	}

	@RequestMapping(value = "/{bustiaId}/enviarByEmail/{contingutId}", method = RequestMethod.POST)
	public String bustiaEnviarByEmailPost(
			HttpServletRequest request,
			@Valid RegistreEnviarViaEmailCommand command,
			BindingResult bindingResult,
			Model model)  {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "bustiaUserList";
		}
		String adresses = command.getAddresses();
		String adressesParsed = adresses.replaceAll("\\s*,\\s*|\\s+", ",");
//		String serverPortContext = request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		try {
			bustiaService.registreAnotacioEnviarPerEmail(
					entitatActual.getId(),
					command.getBustiaId(),
					command.getContingutId(),
					adressesParsed);
		} catch (MessagingException messagingException) {
			getModalControllerReturnValueError(
					request, 
					"redirect:../../../pendent", 
					ExceptionUtils.getRootCauseMessage(messagingException));
		}
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../pendent",
				"bustia.controller.pendent.contingut.enviat.email.ok");
	}

	@RequestMapping(value = "/{bustiaId}/pendent/{contingutId}/reenviar", method = RequestMethod.GET)
	public String bustiaPendentReenviarGet(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		omplirModelPerReenviar(
				entitatActual,
				bustiaId,
				contingutId,
				model);
		ContingutReenviarCommand command = new ContingutReenviarCommand();
		command.setOrigenId(bustiaId);
		model.addAttribute(command);
		return "bustiaPendentRegistreReenviar";
	}
	@RequestMapping(value = "/{bustiaId}/pendent/{contingutId}/reenviar", method = RequestMethod.POST)
	public String bustiaPendentReenviarPost(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long contingutId,
			@Valid ContingutReenviarCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			omplirModelPerReenviar(
					entitatActual,
					bustiaId,
					contingutId,
					model);
			return "bustiaPendentRegistreReenviar";
		}
		if (command.getDestins() == null || command.getDestins().length <= 0) {
			MissatgesHelper.error(
					request,
					getMessage(
							request, 	
							"bustia.pendent.accio.reenviar.no.desti"));			
			return "bustiaPendentRegistreReenviar";
		}
		bustiaService.contingutPendentReenviar(
				entitatActual.getId(),
				bustiaId,
				command.getDestins(),
				contingutId,
				command.isDeixarCopia(),
				command.getComentariEnviar());
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../pendent",
				"bustia.controller.pendent.contingut.reenviat.ok");
	}

	@RequestMapping(value = "/{bustiaId}/registre/{registreId}/reintentar", method = RequestMethod.GET)
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

	@RequestMapping(value = "/{bustiaId}/pendent/{contingutId}/marcarProcessat", method = RequestMethod.GET)
	public String bustiaMarcarProcessatGet(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long contingutId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		MarcarProcessatCommand command = new MarcarProcessatCommand();
		model.addAttribute(command);
		return "bustiaContingutMarcarProcessat";
	}
	
	@RequestMapping(value = "/{bustiaId}/pendent/{contingutId}/marcarProcessat", method = RequestMethod.POST)
	public String bustiaMarcarProcessatPost(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long contingutId,
			@Valid MarcarProcessatCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "bustiaContingutMarcarProcessat";
		}
		try {
			contingutService.marcarProcessat(
					entitatActual.getId(), 
					contingutId,
					"<span class='label label-default'>" + 
					getMessage(
							request, 
							"bustia.pendent.accio.marcat.processat") + 
					"</span> " + command.getMotiu());
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/bustiaUser",
					"bustia.controller.pendent.contingut.marcat.processat.ok");
		} catch (RuntimeException re) {
			MissatgesHelper.error(
					request,
					getMessage(
							request, 
							"bustia.pendent.accio.marcar.processat.error",
							new Object[] {re.getMessage()}));			
			return "bustiaContingutMarcarProcessat";
		}
	}
	
	@RequestMapping(value = "/{bustiaId}/pendent/{contingutId}/alertes", method = RequestMethod.GET)
	public String bustiaListatAlertes(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long contingutId,
			Model model) {
		
		model.addAttribute("bustiaId", bustiaId);
		model.addAttribute("contingutId", contingutId);
		return "registreErrors";
	}
	
	@RequestMapping(value = "/{bustiaId}/pendent/{contingutId}/alertes/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse bustiaListatAlertesDatatable(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long contingutId,
			Model model) {
		return DatatablesHelper.getDatatableResponse(
				request,
				alertaService.findPaginatByLlegida(
						false,
						contingutId,
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
	}
	
	@RequestMapping(value = "/{bustiaId}/pendent/{contingutId}/alertes/{alertaId}/llegir", method = RequestMethod.GET)
	@ResponseBody
	public void bustiaListatAlertesLlegir(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long contingutId,
			@PathVariable Long alertaId,
			Model model) {
		AlertaDto alerta = alertaService.find(alertaId);
		alerta.setLlegida(true);
		alertaService.update(alerta);
	}
	
	@ResponseBody
	@RequestMapping(value = "/getNumPendents", method = RequestMethod.GET)
	public Long bustaGetNumeroPendents(HttpServletRequest request) {
		Long ret = ElementsPendentsBustiaHelper.countElementsPendentsBusties(request, bustiaService);
		return ret;
	}
	

	/** Retorna el llistat de bústies permeses per a l'usuari. Pot incloure o no les innactives */
	@RequestMapping(value = "/bustiesPermeses", method = RequestMethod.GET)
	@ResponseBody
	public List<BustiaDto> bustiesPermeses(
			HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "false") boolean mostrarInactives,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return bustiaService.findPermesesPerUsuari(entitatActual.getId(), mostrarInactives);
	}


	@RequestMapping(value = "/{bustiaId}/classificar/{contingutId}", method = RequestMethod.GET)
	public String bustiaPendentClassificarGet(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long contingutId,
			Model model) {
		RegistreClassificarCommand command = new RegistreClassificarCommand();
		command.setBustiaId(bustiaId);
		command.setContingutId(contingutId);
		model.addAttribute(command);
		return "registreClassificar";
	}
	
	@RequestMapping(value = "/{bustiaId}/classificar/{contingutId}", method = RequestMethod.POST)
	public String bustiaPendentClassificarPost(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long contingutId,
			@Validated(Classificar.class) RegistreClassificarCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			return "registreClassificar";
		}
		bustiaService.contingutPendentClassificar(
				entitatActual.getId(),
				bustiaId,
				command.getCodiProcediment());
		// TODO: posar quina regla s'ha aplicat i si s'ha mogut de bústia
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../pendent",
				"bustia.controller.pendent.contingut.reenviat.ok");
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}



	private void omplirModelPerReenviar(
			EntitatDto entitatActual,
			Long bustiaId,
			Long contingutId,
			Model model) {
		model.addAttribute(
				"contingutPendent",
				bustiaService.contingutPendentFindOne(
						entitatActual.getId(),
						bustiaId,
						contingutId));
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
	
	private BustiaUserFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		BustiaUserFiltreCommand filtreCommand = (BustiaUserFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new BustiaUserFiltreCommand();
			filtreCommand.setEstatContingut(BustiaContingutFiltreEstatEnumDto.PENDENT);
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
			filtreCommand.setMostrarInactives(false);
		}
		return filtreCommand;
	}

}
