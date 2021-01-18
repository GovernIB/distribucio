/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.ClassificacioResultatDto;
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.distribucio.core.api.dto.RegistreDto;
import es.caib.distribucio.core.api.dto.RegistreProcesEstatSimpleEnumDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.service.AlertaService;
import es.caib.distribucio.core.api.service.AplicacioService;
import es.caib.distribucio.core.api.service.BustiaService;
import es.caib.distribucio.core.api.service.ContingutService;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.war.command.ContingutReenviarCommand;
import es.caib.distribucio.war.command.MarcarProcessatCommand;
import es.caib.distribucio.war.command.RegistreClassificarCommand;
import es.caib.distribucio.war.command.RegistreClassificarCommand.Classificar;
import es.caib.distribucio.war.command.RegistreEnviarViaEmailCommand;
import es.caib.distribucio.war.command.RegistreFiltreCommand;
import es.caib.distribucio.war.helper.DatatablesHelper;
import es.caib.distribucio.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.war.helper.ElementsPendentsBustiaHelper;
import es.caib.distribucio.war.helper.ExceptionHelper;
import es.caib.distribucio.war.helper.MissatgesHelper;
import es.caib.distribucio.war.helper.RequestSessionHelper;

/**
 * Controlador per al manteniment de registres.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/registreUser")
public class RegistreUserController extends BaseUserController {

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
	@Autowired
	private AplicacioService aplicacioService;	
	
	
	@RequestMapping(method = RequestMethod.GET)
	public String registreUserGet(
			HttpServletRequest request,
			Model model) {
		RegistreFiltreCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute(filtreCommand);
		return "registreUserList";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String registreUserPost(
			HttpServletRequest request,
			@Valid RegistreFiltreCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio,
			Model model) {
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE);
		} else {
			if (!bindingResult.hasErrors()) {
				// Elimina els espais en blanc del títol i el número
				if (!StringUtils.isEmpty(filtreCommand.getNumero()))
					filtreCommand.setNumero(filtreCommand.getNumero().trim());
				if (!StringUtils.isEmpty(filtreCommand.getTitol()))
					filtreCommand.setTitol(filtreCommand.getTitol().trim());
				// Guarda el filtre en sessió
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE,
						filtreCommand);
			}
		}
		return "redirect:registreUser";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse registreUserDatatable(
			HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		RegistreFiltreCommand registreFiltreCommand = getFiltreCommand(request);
		List<BustiaDto> bustiesPermesesPerUsuari = null;
		if (registreFiltreCommand.getBustia() == null || registreFiltreCommand.getBustia().isEmpty()) {
			bustiesPermesesPerUsuari = bustiaService.findBustiesPermesesPerUsuari(entitatActual.getId(), registreFiltreCommand.isMostrarInactives());
		}
		return DatatablesHelper.getDatatableResponse(
				request,
				registreService.findRegistreUser(
						entitatActual.getId(),
						bustiesPermesesPerUsuari,
						RegistreFiltreCommand.asDto(registreFiltreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id",
				SESSION_ATTRIBUTE_SELECCIO);
	}
	
	
	
	@RequestMapping(value = "/bustia/{bustiaId}/registre/{registreId}", method = RequestMethod.GET)
	public String registreUserDetall(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			@RequestParam(value="registreNumero", required=false) Integer registreNumero,
			@RequestParam(value="registreTotal", required = false) Integer registreTotal,
			@RequestParam(value="ordreColumn", required = false) String ordreColumn,
			@RequestParam(value="ordreDir", required = false) String ordreDir,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		try {
			model.addAttribute(
					"registre",
					registreService.findOne(
							entitatActual.getId(),
							bustiaId,
							registreId));
			model.addAttribute("bustiaId", bustiaId);
			model.addAttribute("registreNumero", registreNumero);
			model.addAttribute("registreTotal", registreTotal);
			model.addAttribute("ordreColumn", ordreColumn);
			model.addAttribute("ordreDir", ordreDir);
		} catch (Exception e) {
			Throwable thr = ExceptionHelper.findThrowableInstance(e, NotFoundException.class, 3);
			if (thr != null) {
				NotFoundException exc = (NotFoundException) thr;
				if (exc.getObjectClass().getName().equals("es.caib.distribucio.core.entity.RegistreEntity")) {
					model.addAttribute("currentContainingBustia", exc.getParam());
					return "errorRegistreNotFound";
				} else {
					throw e;
				}
			} else {
				throw e;
			}
		}
		return "registreDetall";
	}	
	
	/** Mètode per determinar la direcció d'un registre i redireccionar cap al seu detall. S'invoca des
	 * dels botons "Anterior" i "Següent" de la pàgina del detall.
	 * @param request
	 * @param registreNumero
	 * @param ordreColumn
	 * @param ordreDir
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/navega/{registreNumero}", method = RequestMethod.GET)
	public String registreNavegacio(
			HttpServletRequest request,
			@PathVariable int registreNumero,
			@RequestParam(value="ordreColumn", required = false) String ordreColumn,
			@RequestParam(value="ordreDir", required = false) String ordreDir,
			Model model) {
		
		ContingutDto registre = null;
		String ret = null;
		// Recupera el registre a partir del número de regitre
		try {
			// Prepara la pàgina per consultar
			int paginaTamany = 1;
			int paginaNum = registreNumero - 1;
			PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
			paginacioParams.setPaginaTamany(paginaTamany);
			paginacioParams.setPaginaNum(paginaNum);
			paginacioParams.afegirOrdre(
					ordreColumn,
					"asc".equals(ordreDir) ? OrdreDireccioDto.ASCENDENT : OrdreDireccioDto.DESCENDENT);
			// Consulta la pàgina amb el registre anterior, actual i final
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			RegistreFiltreCommand registreFiltreCommand = getFiltreCommand(request);
			List<BustiaDto> bustiesPermesesPerUsuari = null;
			if (registreFiltreCommand.getBustia() == null || registreFiltreCommand.getBustia().isEmpty()) {
				bustiesPermesesPerUsuari = bustiaService.findBustiesPermesesPerUsuari(entitatActual.getId(), registreFiltreCommand.isMostrarInactives());
			}
			PaginaDto<ContingutDto> pagina = 
				registreService.findRegistreUser(
						entitatActual.getId(),
						bustiesPermesesPerUsuari,
						RegistreFiltreCommand.asDto(registreFiltreCommand),
						paginacioParams);
			// Posa les dades dels registres al model segons la consulta
			if (!pagina.getContingut().isEmpty()) {
				registre = pagina.getContingut().get(0);
				ret = "redirect:/modal/registreUser/bustia/" + registre.getPare().getId() + "/registre/" + registre.getId() + "?registreNumero=" + registreNumero + "&registreTotal=" + pagina.getElementsTotal() + "&ordreColumn=" + ordreColumn + "&ordreDir=" + ordreDir;
			}
		} catch (Exception e) {
			String errMsg = getMessage(request, "contingut.navegacio.error") + ": " + e.getMessage();
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
		}
		if (ret == null) {
			ret = "redirect:" + request.getHeader("referer");
		}
		return ret;
	}

	@RequestMapping(value = "/registreAnnex/{bustiaId}/{registreId}/{annexId}", method = RequestMethod.GET)
	public String registreAnnex(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			@PathVariable Long annexId,
			Model model) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			model.addAttribute(
					"annex",
					registreService.getAnnexSenseFirmes(
							entitatActual.getId(),
							bustiaId,
							registreId,
							annexId));
			model.addAttribute("registreId", registreId);
		} catch(Exception ex) {
			logger.error("Error recuperant informació de l'annex", ex);
			model.addAttribute("missatgeError", ex.getMessage());
			return "ajaxErrorPage";
		}
		return "registreAnnex";
	}
	
	@RequestMapping(value = "/registreAnnexFirmes/{bustiaId}/{registreId}/{annexId}/{isResum}", method = RequestMethod.GET)
	public String registreAnnexFirmes(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			@PathVariable Long annexId,
			@PathVariable boolean isResum,
			Model model) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			model.addAttribute(
					"annex",
					registreService.getAnnexAmbFirmes(
							entitatActual.getId(),
							bustiaId,
							registreId,
							annexId));
			model.addAttribute("registreId", registreId);
			
			model.addAttribute("isUsuariActualAdministration", entitatActual.isUsuariActualAdministration());
			
			model.addAttribute("isResum", isResum);
			
		} catch(Exception ex) {
			logger.error("Error recuperant informació de firma", ex);
			model.addAttribute("missatgeError", ex.getMessage());
			return "ajaxErrorPage";
		}

		return "registreAnnexFirmes";
	}
	
	
	/** Mètode Ajax per seleccionar tots els registres a partir del mateix filtre del datatable.
	 * 
	 * @param request
	 * @param ids
	 * @return Retorna el número d'elements seleccionats
	 */
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
			RegistreFiltreCommand filtreCommand = getFiltreCommand(request);
			List<BustiaDto> bustiesUsuari = null;
			if (filtreCommand.getBustia() == null || filtreCommand.getBustia().isEmpty()) {
				bustiesUsuari = bustiaService.findBustiesPermesesPerUsuari(entitatActual.getId(), filtreCommand.isMostrarInactives());
			}
			seleccio.addAll(
					bustiaService.findIdsAmbFiltre(
							entitatActual.getId(),
							bustiesUsuari,
							RegistreFiltreCommand.asDto(filtreCommand)));
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
		try {
			byte[] b = bustiaService.getApplictionMetrics().getBytes();
			writeFileToResponse(
					"metrics.json",
					b,
					response);
		} catch (Exception ex) {
			return getModalControllerReturnValueError(
					request,
					"redirect:.",
					"contingut.controller.document.descarregar.error",
					new Object[] {ex.getMessage()});
		}
		return null;
	}
	
	@RequestMapping(value = "/metriquesView", method = RequestMethod.GET)
	public String metriquesView(
			HttpServletRequest request,
			Model model) throws IOException {
		model.addAttribute("metriques", bustiaService.getApplictionMetrics());

		return "metrics";
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
		return "redirect:registreUser";
	}

	@RequestMapping(value = "/{bustiaId}/enviarViaEmail/{contingutId}", method = RequestMethod.GET)
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

	@RequestMapping(value = "/{bustiaId}/enviarViaEmail/{contingutId}", method = RequestMethod.POST)
	public String bustiaEnviarByEmailPost(
			HttpServletRequest request,
			@Valid RegistreEnviarViaEmailCommand command,
			BindingResult bindingResult,
			Model model)  {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		String adreces = this.revisarAdreces(request, command.getAddresses(), bindingResult);
		if (bindingResult.hasErrors()) {
			return "registreViaEmail";
		}

		try {
			bustiaService.registreAnotacioEnviarPerEmail(
					entitatActual.getId(),
					command.getBustiaId(),
					command.getContingutId(),
					adreces);
			MissatgesHelper.success(
					request,
					getMessage(request, "bustia.controller.pendent.contingut.enviat.email.ok"));
			return modalUrlTancar();
		} catch (Exception exception) {
			String errMsg = getMessage(
					request, 	
					"bustia.controller.pendent.contingut.enviat.email.ko",
					new Object[] {ExceptionUtils.getRootCauseMessage(exception)});
			MissatgesHelper.error(
					request,
					errMsg);
			logger.error(errMsg, exception);
			return "registreViaEmail";
		}
		
	}

	/** Realitza les següents accions:
	 * - Revisa que no es repeteixin les adreces.
	 * - Substitueix els espais en blanc per comes.
	 * - Revisa que les adreces siguin correctes, en cas contrari afegeix un error.
	 * - Revisa que com a mínim hi hagi una adreça.
	 * @param request 
	 * @param adreces
	 * @param bindingResult
	 * @return
	 */
	private String revisarAdreces(
			HttpServletRequest request, 
			String adreces, 
			BindingResult bindingResult) {

		Set<String> adrecesRevisades = new HashSet<>();
		Set<String> adrecesErronies = new HashSet<>();
		if (adreces != null && !adreces.isEmpty() ) {
			// substitueix els espais per comes
			adreces = adreces.replaceAll("\\s*,\\s*|\\s+", ",");
			for(String adr : adreces.split(",")) {
				if (!adrecesRevisades.contains(adr) && !adrecesErronies.contains(adr)) {
					if (adr.matches("\\S+@\\S+[.\\S+]+")) {
						adrecesRevisades.add(adr);
					} else {
						adrecesErronies.add(adr);
					}
				}
			}
			if (adrecesErronies.size() > 0) 
				bindingResult.rejectValue(
						"addresses", 
						"bustia.controller.pendent.contingut.enviar.email.validacio.adreces", 
						new Object[] {StringUtils.join(adrecesErronies.toArray(), ", ")}, 
						getMessage(request, 
								"bustia.controller.pendent.contingut.enviar.email.validacio.adreces", 
								new Object[] {StringUtils.join(adrecesErronies.toArray(), ", ")}));
			}
		return StringUtils.join(adrecesRevisades,",");
	}

	@RequestMapping(value = "/{bustiaId}/pendent/{registreId}/reenviar", method = RequestMethod.GET)
	public String registreReenviarGet(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			@RequestParam(value="registreNumero", required = false) String registreNumero,
			@RequestParam(value="ordreColumn", required = false) String ordreColumn,
			@RequestParam(value="ordreDir", required = false) String ordreDir,
			@RequestParam(value="avanzarPagina", required = false) String avanzarPagina,
			Model model) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			omplirModelPerReenviar(entitatActual, bustiaId, registreId, model);
			ContingutReenviarCommand command = new ContingutReenviarCommand();
			command.setOrigenId(bustiaId);
			if (registreNumero != null)
				command.setParams(new String [] {registreNumero, ordreColumn, ordreDir, avanzarPagina});
			model.addAttribute(command);
			model.addAttribute("maxLevel", getMaxLevelArbre());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (NotFoundException.class.equals((e.getCause() != null ? e.getCause() : e).getClass())) {
				return getModalControllerReturnValueError(
						request,
						"",
						"registre.user.controller.errorReenviant.registreNoTrobat");
			} else {
				return getModalControllerReturnValueErrorNoKey(
						request,
						"",
						e.getMessage());
			}
		}
		return "registreReenviarForm";
	}

	@RequestMapping(value = "/{bustiaId}/pendent/{registreId}/reenviar", method = RequestMethod.POST)
	public String registreReenviarPost(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			@Valid ContingutReenviarCommand command,
			BindingResult bindingResult,
			Model model) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			if (bindingResult.hasErrors()) {
				omplirModelPerReenviar(
						entitatActual,
						bustiaId,
						registreId,
						model);
				return "registreReenviarForm";
			}
			if (command.getDestins() == null || command.getDestins().length <= 0) {
				MissatgesHelper.error(
						request,
						getMessage(
								request, 	
								"bustia.pendent.accio.reenviar.no.desti"));
				model.addAttribute("maxLevel", getMaxLevelArbre());
				return "registreReenviarForm";
			}
			bustiaService.registreReenviar(
					entitatActual.getId(),
					bustiaId,
					command.getDestins(),
					registreId,
					command.isDeixarCopia(),
					command.getComentariEnviar());
			if (command.getParams().length == 0) {
				return getModalControllerReturnValueSuccess(
						request,
						"redirect:../../../pendent",
						"bustia.controller.pendent.contingut.reenviat.ok");
			} else {
				//avançar a la següent pàgina al reenviar
				boolean avanzar = Boolean.parseBoolean(command.getParams()[3]);
				if (avanzar) {
					int numeroPagina = Integer.parseInt(command.getParams()[0]);
					command.getParams()[0] = String.valueOf(++numeroPagina);
				}
				MissatgesHelper.success(
						request, 
						getMessage(
								request,
								"bustia.controller.pendent.contingut.reenviat.ok"));
				return "redirect:/registreUser/navega/" + command.getParams()[0] + "?ordreColumn=" + command.getParams()[1] + "&ordreDir=" + command.getParams()[2];
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (NotFoundException.class.equals((e.getCause() != null ? e.getCause() : e).getClass())) {
				return getModalControllerReturnValueError(
						request,
						"",
						"registre.user.controller.errorReenviant.registreNoTrobat");
			} else {
				return getModalControllerReturnValueErrorNoKey(
						request,
						"",
						e.getMessage());
			}
		}
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
	
	@RequestMapping(value = "/{bustiaId}/registre/{registreId}/reintentarEnviamentBackoffice", method = RequestMethod.GET)
	public String reintentarEnviamentBackoffice(HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			boolean processatOk = registreService.reintentarEnviamentBackofficeAdmin(entitatActual.getId(),
					bustiaId,
					registreId);
			if (processatOk) {
				MissatgesHelper.success(request,
						getMessage(request,
								"contingut.admin.controller.registre.reintentat.ok",
								null));
			} else {
				MissatgesHelper.error(request,
						getMessage(request,
								"contingut.admin.controller.registre.reintentat.error",
								null));
			}

		return "redirect:../../../../modal/registreUser/bustia/" + bustiaId + "/registre/" + registreId;
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
		return "registreUserMarcarProcessat";
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
			return "registreUserMarcarProcessat";
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
					"redirect:/registreUser",
					"bustia.controller.pendent.contingut.marcat.processat.ok");
		} catch (RuntimeException re) {
			MissatgesHelper.error(
					request,
					getMessage(
							request, 
							"bustia.pendent.accio.marcar.processat.error",
							new Object[] {re.getMessage()}));			
			return "registreUserMarcarProcessat";
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
		return bustiaService.findBustiesPermesesPerUsuari(entitatActual.getId(), mostrarInactives);
	}

	@RequestMapping(value = "/{bustiaId}/classificar/{registreId}", method = RequestMethod.GET)
	public String bustiaClassificarGet(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			Model model) {
		try {
			String procedimentCodi = emplenarModelClassificar(
					request,
					bustiaId,
					registreId,
					model);
			RegistreClassificarCommand command = new RegistreClassificarCommand();
			command.setBustiaId(bustiaId);
			command.setContingutId(registreId);
			command.setCodiProcediment(procedimentCodi);
			model.addAttribute(command);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return getModalControllerReturnValueErrorNoKey(
					request,
					"",
					e.getMessage());
		}
		return "registreClassificar";
	}

	@RequestMapping(value = "/{bustiaId}/classificar/{registreId}", method = RequestMethod.POST)
	public String bustiaClassificarPost(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			@Validated(Classificar.class) RegistreClassificarCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			emplenarModelClassificar(
					request,
					bustiaId,
					registreId,
					model);
			return "registreClassificar";
		}
		ClassificacioResultatDto resultat = registreService.classificar(
				entitatActual.getId(),
				bustiaId,
				registreId,
				command.getCodiProcediment());
		switch (resultat.getResultat()) {
		case SENSE_CANVIS:
			break;
		case REGLA_BUSTIA:
		case REGLA_UNITAT:
			MissatgesHelper.info(
					request, 
					getMessage(
							request, 
							"bustia.controller.pendent.contingut.classificat.mogut",
							new Object[] {
									resultat.getBustiaNom(),
									resultat.getBustiaUnitatOrganitzativa().getDenominacio()
							}));
			break;
		case REGLA_BACKOFFICE:
			MissatgesHelper.info(
					request, 
					getMessage(
							request, 
							"bustia.controller.pendent.contingut.classificat.backoffice",
							null));
			break;
		case REGLA_ERROR:
			MissatgesHelper.warning(
					request, 
					getMessage(
							request, 
							"bustia.controller.pendent.contingut.classificat.error",
							null));
			break;
		}
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:../../../pendent",
				"bustia.controller.pendent.contingut.classificat.ok");
	}

	@RequestMapping(value = "/classificarMultiple", method = RequestMethod.GET)
	public String classificarMultipleGet(
			HttpServletRequest request,
			Model model) {
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio != null && !seleccio.isEmpty()) {
			List<Long> seleccioList = new ArrayList<Long>();
			seleccioList.addAll(seleccio);
			boolean mateixPare = emplenarModelClassificarMultiple(
					request,
					seleccioList,
					model);
			if (mateixPare) {
				RegistreClassificarCommand command = new RegistreClassificarCommand();
				model.addAttribute(command);
				return "registreClassificarMultiple";
			} else {
				return getModalControllerReturnValueError(
						request,
						"redirect:../registreUser",
						"bustia.controller.pendent.contingut.classificar.no.mateix.pare.error");
			}
		} else {
			return getModalControllerReturnValueError(
					request,
					"redirect:../registreUser",
					"bustia.controller.pendent.contingut.classificar.seleccio.buida");
		}
	}

	@RequestMapping(value = "/{bustiaId}/classificarMultiple/{registreId}", method = RequestMethod.POST)
	@ResponseBody
	public ClassificacioResultatDto classificarMultiplePost(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			@Validated(Classificar.class) RegistreClassificarCommand command,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ClassificacioResultatDto resultat = registreService.classificar(
				entitatActual.getId(),
				bustiaId,
				registreId,
				command.getCodiProcediment());
		return resultat;
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
			Long registreId,
			Model model) {


		List<BustiaDto> busties = bustiaService.findActivesAmbEntitat(
				entitatActual.getId());
		
		boolean disableDeixarCopia = false;
		RegistreDto registreDto = registreService.findOne(
				entitatActual.getId(),
				bustiaId,
				registreId);
		
		boolean duplicarContingutInArxiu = new Boolean(aplicacioService.propertyFindByNom("es.caib.distribucio.plugins.distribucio.fitxers.duplicar.contingut.arxiu"));
		if (registreDto.getProcesEstat() == RegistreProcesEstatEnum.ARXIU_PENDENT && !duplicarContingutInArxiu) {
			disableDeixarCopia = true;
		}
		model.addAttribute(
				"disableDeixarCopia",
				disableDeixarCopia);
		model.addAttribute("maxLevel", getMaxLevelArbre());
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

	private int getMaxLevelArbre() {
		String maxLevelStr = aplicacioService.propertyFindByNom("es.caib.distribucio.contingut.enviar.arbre.nivell");
		int maxLevel = maxLevelStr != null ? Integer.parseInt(maxLevelStr) : 1;
		return maxLevel;
	}

	private RegistreFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		RegistreFiltreCommand filtreCommand = (RegistreFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new RegistreFiltreCommand();
			filtreCommand.setProcesEstatSimple(RegistreProcesEstatSimpleEnumDto.PENDENT);
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
			filtreCommand.setMostrarInactives(false);
		}
		return filtreCommand;
	}

	private String emplenarModelClassificar(
			HttpServletRequest request,
			Long bustiaId,
			Long registreId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		RegistreDto registre = registreService.findOne(
				entitatActual.getId(),
				bustiaId,
				registreId);
		model.addAttribute("registre", registre);
		model.addAttribute(
				"procediments",
				registreService.classificarFindProcediments(
						entitatActual.getId(),
						bustiaId));
		return registre.getProcedimentCodi();
	}

	private boolean emplenarModelClassificarMultiple(
			HttpServletRequest request,
			List<Long> multipleRegistreIds,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<RegistreDto> registres = registreService.findMultiple(
				entitatActual.getId(),
				multipleRegistreIds);
		model.addAttribute("registres", registres);
		boolean mateixPare = true;
		Long bustiaIdActual = null;
		if (!registres.isEmpty()) {
			for (RegistreDto registre: registres) {
				if (bustiaIdActual == null) {
					bustiaIdActual = registre.getPare().getId();
				}
				if (!bustiaIdActual.equals(registre.getPare().getId())) {
					mateixPare = false;
					break;
				}
			}
		}
		if (mateixPare && bustiaIdActual != null) {
			model.addAttribute(
					"procediments",
					registreService.classificarFindProcediments(
							entitatActual.getId(),
							bustiaIdActual));
		}
		return mateixPare;
	}
	private static final Logger logger = LoggerFactory.getLogger(RegistreUserController.class);
}
