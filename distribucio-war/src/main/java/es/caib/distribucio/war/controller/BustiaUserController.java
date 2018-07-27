/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import es.caib.distribucio.core.api.dto.BustiaContingutPendentTipusEnumDto;
import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.service.AlertaService;
import es.caib.distribucio.core.api.service.BustiaService;
import es.caib.distribucio.core.api.service.ContingutService;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.war.command.BustiaUserFiltreCommand;
import es.caib.distribucio.war.helper.MissatgesHelper;
import es.caib.ripea.core.api.dto.ArxiuDto;
import es.caib.ripea.core.api.dto.EscriptoriDto;
import es.caib.ripea.war.command.ContenidorCommand.Create;
import es.caib.ripea.war.command.ContingutMoureCopiarEnviarCommand;
import es.caib.ripea.war.command.ContingutReenviarCommand;
import es.caib.ripea.war.command.ExpedientCommand;
import es.caib.ripea.war.command.ExpedientFiltreCommand;
import es.caib.ripea.war.command.MarcarProcessatCommand;
import es.caib.ripea.war.helper.DatatablesHelper;
import es.caib.ripea.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.ripea.war.helper.ElementsPendentsBustiaHelper;
import es.caib.ripea.war.helper.RequestSessionHelper;

/**
 * Controlador per al manteniment de bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/bustiaUser")
public class BustiaUserController extends BaseUserController {
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "BustiaUserController.session.filtre";
	private static final String SESSION_ATTRIBUTE_FILTRE_ADDEXP = "ExpedientFilterCommand.session.filtre.";
	
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
		
		model.addAttribute("bustiesUsuari", bustiaService.findPermesesPerUsuari(entitatActual.getId()));
		
		return "bustiaUserList";
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
		if (bustiaUserFiltreCommand.getBustia() == null || bustiaUserFiltreCommand.getBustia().isEmpty())
			bustiesUsuari = bustiaService.findPermesesPerUsuari(entitatActual.getId());
		
		return DatatablesHelper.getDatatableResponse(
				request,
				bustiaService.contingutPendentFindByDatatable(
						entitatActual.getId(),
						bustiesUsuari,
						BustiaUserFiltreCommand.asDto(bustiaUserFiltreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
		
	}

	@RequestMapping(value = "/{bustiaId}/pendent/{contingutTipus}/{contingutId}/nouexp", method = RequestMethod.GET)
	public String bustiaPendentNouexpGet(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable BustiaContingutPendentTipusEnumDto contingutTipus,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		EscriptoriDto escriptori = contingutService.getEscriptoriPerUsuariActual(entitatActual.getId());
		ExpedientCommand command = new ExpedientCommand();
		command.setEntitatId(entitatActual.getId());
		command.setPareId(escriptori.getId());
		model.addAttribute(command);
		omplirModelPerNouExpedient(
				entitatActual,
				model);
		return "bustiaPendentContingutNouexp";
	}
	@RequestMapping(value = "/{bustiaId}/pendent/{contingutTipus}/{contingutId}/nouexp", method = RequestMethod.POST)
	public String bustiaPendentNouexpPost(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable BustiaContingutPendentTipusEnumDto contingutTipus,
			@PathVariable Long contingutId,
			@Validated({Create.class}) ExpedientCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			omplirModelPerNouExpedient(
					entitatActual,
					model);
			return "bustiaPendentContingutNouexp";
		}
		EscriptoriDto escriptori = contingutService.getEscriptoriPerUsuariActual(entitatActual.getId());
		expedientService.create(
				entitatActual.getId(),
				escriptori.getId(),
				command.getMetaNodeId(),
				command.getArxiuId(),
				null,
				command.getNom(),
				contingutTipus,
				contingutId);
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../pendent",
				"bustia.controller.pendent.contingut.nouexp.ok");
	}

	@RequestMapping(value = "/{bustiaId}/pendent/{contingutTipus}/{contingutId}/addexp", method = RequestMethod.GET)
	public String bustiaPendentAddexpGet(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable BustiaContingutPendentTipusEnumDto contingutTipus,
			@PathVariable Long contingutId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		omplirModelPerAfegirAExpedient(
				bustiaId,
				contingutTipus,
				contingutId,
				entitatActual,
				getFiltreExpedientCommand(
						request,
						SESSION_ATTRIBUTE_FILTRE_ADDEXP + bustiaId + "." + contingutTipus + "." + contingutId),
				model,
				contingutId);
		return "bustiaPendentContingutAddexp";
	}
	@RequestMapping(value = "/{bustiaId}/pendent/{contingutTipus}/{contingutId}/addexp/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse bustiaPendentAddexpDatatable(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable BustiaContingutPendentTipusEnumDto contingutTipus,
			@PathVariable Long contingutId,
			Model model) {
		ExpedientFiltreCommand filtre = getFiltreExpedientCommand(
				request,
				SESSION_ATTRIBUTE_FILTRE_ADDEXP + bustiaId + "." + contingutTipus + "." + contingutId);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				expedientService.findAmbFiltreUser(
						entitatActual.getId(), 
						ExpedientFiltreCommand.asDto(filtre), 
						DatatablesHelper.getPaginacioDtoFromRequest(request)));
	}
	@RequestMapping(value = "/{bustiaId}/pendent/{contingutTipus}/{contingutId}/addexp", method = RequestMethod.POST)
	public String bustiaPendentAddexpFiltrar(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable BustiaContingutPendentTipusEnumDto contingutTipus,
			@PathVariable Long contingutId,
			@Valid ExpedientFiltreCommand expedientFiltreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio,
			Model model) {
		if("filtrar".equals(accio)) {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE_ADDEXP + bustiaId + "." + contingutTipus + "." + contingutId,
					expedientFiltreCommand);
			return "redirect:./addexp";
		} else {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE_ADDEXP + bustiaId + "." + contingutTipus + "." + contingutId);
			return "redirect:./addexp";
		}
	}
	@RequestMapping(value = "/{bustiaId}/pendent/{contingutTipus}/{contingutId}/addexp/{expedientId}", method = RequestMethod.POST)
	public String bustiaPendentAddexpPost(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable BustiaContingutPendentTipusEnumDto contingutTipus,
			@PathVariable Long contingutId,
			@PathVariable Long expedientId,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		expedientService.afegirContingutBustia(
				entitatActual.getId(),
				expedientId,
				bustiaId,
				contingutTipus,
				contingutId);
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:/modal/tancar",
				"bustia.controller.pendent.contingut.addexp.ok");
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
		boolean processatOk = registreService.reglaReintentarUser(
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

	private void omplirModelPerNouExpedient(
			EntitatDto entitatActual,
			Model model) {
		model.addAttribute(
				"metaExpedients",
				metaExpedientService.findActiusAmbEntitatPerCreacio(entitatActual.getId()));
		model.addAttribute(
				"arxius",
				new ArrayList<ArxiuDto>());
	}

	private void omplirModelPerAfegirAExpedient(
			Long bustiaId,
			BustiaContingutPendentTipusEnumDto contingutTipus,
			Long contingutId,
			EntitatDto entitatActual,
			ExpedientFiltreCommand expedientFiltreCommand,
			Model model,
			Long contenidorOrigenId) {
		model.addAttribute("bustiaId", bustiaId);
		model.addAttribute("contingutTipus", contingutTipus);
		model.addAttribute("contingutId", contingutId);
		EscriptoriDto escriptori = contingutService.getEscriptoriPerUsuariActual(entitatActual.getId());
		model.addAttribute(
				"contenidorDesti",
				escriptori);
		ContingutMoureCopiarEnviarCommand command = new ContingutMoureCopiarEnviarCommand();
		command.setOrigenId(contenidorOrigenId);
		model.addAttribute(command);
		model.addAttribute("expedientFiltreCommand", expedientFiltreCommand);
		model.addAttribute(
				"arxius",
				arxiuService.findPermesosPerUsuari(
						entitatActual.getId()));
		model.addAttribute(
				"expedientTipus",
				metaExpedientService.findByEntitat(entitatActual.getId()));
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
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
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
		}
		return filtreCommand;
	}
	
	private ExpedientFiltreCommand getFiltreExpedientCommand(
			HttpServletRequest request,
			String KEY) {
		
		ExpedientFiltreCommand expedientFiltreCommand = (ExpedientFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(
				request,
				KEY);
		if(expedientFiltreCommand == null) {
			expedientFiltreCommand = new ExpedientFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					KEY,
					expedientFiltreCommand);
		}
		return expedientFiltreCommand;
	}
	
	

}
