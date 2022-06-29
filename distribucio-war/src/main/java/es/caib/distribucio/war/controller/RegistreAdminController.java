/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.ArrayUtils;
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

import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.BustiaFiltreDto;
import es.caib.distribucio.core.api.dto.BustiaFiltreOrganigramaDto;
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.distribucio.core.api.dto.ProcedimentDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDto;
import es.caib.distribucio.core.api.dto.RegistreDto;
import es.caib.distribucio.core.api.dto.RegistreEnviatPerEmailEnumDto;
import es.caib.distribucio.core.api.dto.RegistreFiltreDto;
import es.caib.distribucio.core.api.dto.RegistreMarcatPerSobreescriureEnumDto;
import es.caib.distribucio.core.api.dto.RegistreProcesEstatSimpleEnumDto;
import es.caib.distribucio.core.api.dto.RegistreTipusDocFisicaEnumDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.registre.ValidacioFirmaEnum;
import es.caib.distribucio.core.api.service.BackofficeService;
import es.caib.distribucio.core.api.service.BustiaService;
import es.caib.distribucio.core.api.service.ContingutService;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.core.api.service.UnitatOrganitzativaService;
import es.caib.distribucio.war.command.MarcarProcessatCommand;
import es.caib.distribucio.war.command.RegistreFiltreCommand;
import es.caib.distribucio.war.helper.AjaxHelper;
import es.caib.distribucio.war.helper.AjaxHelper.AjaxFormResponse;
import es.caib.distribucio.war.helper.DatatablesHelper;
import es.caib.distribucio.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.war.helper.EntitatHelper;
import es.caib.distribucio.war.helper.ExceptionHelper;
import es.caib.distribucio.war.helper.MissatgesHelper;
import es.caib.distribucio.war.helper.RegistreHelper;
import es.caib.distribucio.war.helper.RequestSessionHelper;
import es.caib.distribucio.war.helper.RolHelper;

/**
 * Controlador per a la consulta d'arxius pels administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/registreAdmin")
public class RegistreAdminController extends BaseAdminController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "RegistreAdminController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "RegistreAdminController.session.seleccio";

	@Autowired
	private RegistreService registreService;	
	@Autowired
	private UnitatOrganitzativaService unitatOrganitzativaService;
	@Autowired
	private BustiaService bustiaService;
	@Autowired
	private ContingutService contingutService;
	@Autowired
	private BackofficeService backofficeService;
	@Autowired
	private RegistreHelper registreHelper;
	

	@RequestMapping(method = RequestMethod.GET)
	public String registreAdminGet(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		
		RegistreFiltreCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute(filtreCommand);
		model.addAttribute(
				"backoffices",
				backofficeService.findByEntitat(
						entitatActual.getId()));

		return "registreAdminList";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String registreAdminPost(
			HttpServletRequest request,
			@Valid RegistreFiltreCommand registreFiltreCommand,
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
						registreFiltreCommand);
			}
		}
		return "redirect:registreAdmin";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse registreAdminDatatable(
			HttpServletRequest request) {
//		request.getUserPrincipal();
//		boolean isValidRequest = request.isRequestedSessionIdValid();
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		RegistreFiltreCommand filtreCommand = getFiltreCommand(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				registreService.findRegistre(
						entitatActual.getId(),
						null, // bustiesUsuari
						RegistreFiltreCommand.asDto(filtreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request), 
						true),
				"id",
				SESSION_ATTRIBUTE_SELECCIO);
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}
	
	@RequestMapping(value = "/{registreId}/detall", method = RequestMethod.GET)
	public String registreUserDetall(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@RequestParam(value="registreNumero", required=false) Integer registreNumero,
			@RequestParam(value="registreTotal", required = false) Integer registreTotal,
			@RequestParam(value="ordreColumn", required = false) String ordreColumn,
			@RequestParam(value="ordreDir", required = false) String ordreDir,
			Model model) throws Exception {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		
		try {
			
			ContingutDto registreDto = contingutService.findAmbIdAdmin(
					entitatActual.getId(),
					registreId,
					true);
			
			int numeroAnnexosPendentsArxiu = 0;
			String codiSia = null;
			int numeroAnnexosFirmaInvalida = 0;
			if (registreDto instanceof RegistreDto) {
				RegistreDto registreDtoAmbAnnexos = (RegistreDto)registreDto;		
				codiSia = registreDtoAmbAnnexos.getProcedimentCodi();
				for (RegistreAnnexDto registreAnnexDto:registreDtoAmbAnnexos.getAnnexos()) {
					if (registreAnnexDto.getFitxerArxiuUuid()==null) {
						numeroAnnexosPendentsArxiu++;
					}
				}
			}			
			
			String codiDir3;
			if (RolHelper.isRolActualAdministrador(request)) {
				codiDir3 = EntitatHelper.getEntitatActual(request).getCodiDir3();
			}else {
				codiDir3 = entitatActual.getCodiDir3();
			}
			
			try {
				ProcedimentDto procedimentDto = registreService.procedimentFindByCodiSia(codiDir3, codiSia);
				String procedimentNom = procedimentDto.getNom();
				model.addAttribute("procedimentNom", procedimentDto.getNom());
			}catch(NullPointerException e) {
				model.addAttribute("procedimentNom", null);
				String errMsg = "No s'ha pogut concretar el nom del procediment";
				logger.info(errMsg);
				numeroAnnexosPendentsArxiu = this.numeroAnnexosPendentsArxiu((RegistreDto)registreDto);
				numeroAnnexosFirmaInvalida = this.numeroAnnexosFirmaInvalida((RegistreDto)registreDto);
			}
			model.addAttribute("registre", registreDto);
			model.addAttribute("registreNumero", registreNumero);
			model.addAttribute("registreTotal", registreTotal);
			model.addAttribute("ordreColumn", ordreColumn);
			model.addAttribute("ordreDir", ordreDir);
			model.addAttribute("isVistaMoviments", false);
			model.addAttribute("numeroAnnexosPendentsArxiu", numeroAnnexosPendentsArxiu);
			model.addAttribute("numeroAnnexosFirmaInvalida", numeroAnnexosFirmaInvalida);
		} catch (Exception e) {
			
			Throwable thr = ExceptionHelper.getRootCauseOrItself(e);
			if (thr.getClass() == NotFoundException.class) {
				NotFoundException exc = (NotFoundException) thr;
				if (exc.getObjectClass().getName().equals("es.caib.distribucio.core.entity.ContingutEntity")) {
					model.addAttribute("errorTitol", getMessage(request, "error.titol.not.found"));
					model.addAttribute("missatgeError", getMessage(request, "registre.detalls.notFound"));
					return "ajaxErrorPage";
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
		// Recupera el registre a partir del número de registre
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
			EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
			RegistreFiltreCommand registreFiltreCommand = getFiltreCommand(request);
			List<BustiaDto> bustiesPermesesPerUsuari = null;
			PaginaDto<ContingutDto> pagina = 
				registreService.findRegistre(
						entitatActual.getId(),
						bustiesPermesesPerUsuari,
						RegistreFiltreCommand.asDto(registreFiltreCommand),
						paginacioParams, false);
			// Posa les dades dels registres al model segons la consulta
			if (!pagina.getContingut().isEmpty()) {
				registre = pagina.getContingut().get(0);
				ret = "redirect:/modal/registreUser/registre/" + registre.getId() + "?registreNumero=" + registreNumero + "&registreTotal=" + pagina.getElementsTotal() + "&ordreColumn=" + ordreColumn + "&ordreDir=" + ordreDir;
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
			EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
			RegistreFiltreCommand filtreCommand = getFiltreCommand(request);			
			seleccio.addAll(
					registreService.findRegistreIds(
							entitatActual.getId(),
							null, // bustiesUsuari
							RegistreFiltreCommand.asDto(filtreCommand),
							false, 
							true));
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
	
	
	@RequestMapping(value = "/busties", method = RequestMethod.GET)
	@ResponseBody
	public List<BustiaDto> busties(
			HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "false") boolean mostrarInactives,
			@RequestParam(required = false) Long unitatId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		BustiaFiltreOrganigramaDto filtre = new BustiaFiltreOrganigramaDto();
		filtre.setActiva(!mostrarInactives);
		filtre.setUnitatIdFiltre(unitatId);
		return bustiaService.findAmbEntitatAndFiltre(entitatActual.getId(), filtre);
	}
	
	
	
	/** Estats que permeten el reprocessament */
	private static RegistreProcesEstatEnum[] estatsReprocessables = {
			RegistreProcesEstatEnum.ARXIU_PENDENT,
			RegistreProcesEstatEnum.REGLA_PENDENT,
			RegistreProcesEstatEnum.BACK_PENDENT
	};
	
	
	
	@RequestMapping(value = "/registre/{registreId}/reintentar", method = RequestMethod.GET)
	public String reintentar(
			HttpServletRequest request,
			@PathVariable Long registreId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		RegistreDto registreReenviat = registreService.findOne(entitatActual.getId(), registreId, false);
		boolean processatOk = registreService.reintentarProcessamentAdmin(
				entitatActual.getId(),
				registreId);
		if (processatOk) {
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"contingut.admin.controller.registre.reintentat.ok", 
							new Object[] {registreReenviat.getBackCodi()}));
		} else {
			MissatgesHelper.error(
					request,
					getMessage(
							request, 
							"contingut.admin.controller.registre.reintentat.error",
							null));
		}
		return "redirect:../../" + registreId + "/detall";
	}
	
	@RequestMapping(value = "/registre/{registreId}/processarAnnexos", method = RequestMethod.GET)
	public String processarAnnexos(
			HttpServletRequest request,
			@PathVariable Long registreId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		boolean processatOk = registreService.processarAnnexosAdmin(
				entitatActual.getId(),
				registreId);
		if (processatOk) {
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"contingut.admin.controller.registre.desat.arxiu.ok",
							null));
		} else {
			MissatgesHelper.error(
					request,
					getMessage(
							request, 
							"contingut.admin.controller.registre.desat.arxiu.error",
							null));
		}
		return "redirect:../../" + registreId + "/detall";
	}
	
	
	@RequestMapping(value = "/registre/{registreId}/reintentarEnviamentBackoffice", method = RequestMethod.GET)
	public String reintentarEnviamentBackoffice(HttpServletRequest request,
			@PathVariable Long registreId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		RegistreDto registreReenviat = registreService.findOne(entitatActual.getId(), registreId, false);
			boolean processatOk = registreService.reintentarEnviamentBackofficeAdmin(entitatActual.getId(),
					registreId);
			if (processatOk) {
				MissatgesHelper.success(request,
						getMessage(request,
								"contingut.admin.controller.registre.reintentat.ok", 
								new Object[] {registreReenviat.getBackCodi()}));
			} else {
				MissatgesHelper.error(request,
						getMessage(request,
								"contingut.admin.controller.registre.reintentat.error",
								null));
			}



//		return "redirect:../../" + registreId + "/detall";
			return "redirect:" + request.getHeader("referer");
	}
	
	
	@RequestMapping(value = "/{registreId}/marcarSobreescriure", method = RequestMethod.GET)
	public String marcarSobreescriure(
			HttpServletRequest request,
			@PathVariable Long registreId) {
		
		try {
			registreService.marcarSobreescriure(getEntitatActualComprovantPermisAdmin(request).getId(), registreId);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../registreAdmin",
					"registre.admin.controller.marcar.sobreescriure.ok",
					new Object[] {registreId});
		} catch (Exception e) {
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../registreAdmin",
					"registre.admin.controller.marcar.sobreescriure.error",
					new Object[] {ExceptionHelper.getRootCauseOrItself(e).getMessage()});
		}

	}
	

	@RequestMapping(value = "/marcarSobreescriureMultiple", method = RequestMethod.GET)
	public String marcarSobreescriureMultipleGet(
			HttpServletRequest request,
			Model model) {
		Object command = new Object();
		model.addAttribute("marcarSobreescriureCommand", command);
		model.addAttribute("registres",
				registreService.findMultiple(
						getEntitatActualComprovantPermisAdmin(request).getId(),
						this.getRegistresSeleccionats(request, SESSION_ATTRIBUTE_SELECCIO),
						true));
		return "marcarSobreescriure";
	}
	
	
	/** Mèdode per marcar per sobreescriure una anotació de registre via ajax des del llistat d'anotacions
	 * de l'administrador.
	 * @param request
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/marcarSobreescriureAjax/{registreId}", method = RequestMethod.POST)
	public AjaxFormResponse marcarSobreescriureAjaxPost(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@Valid Object command,
			BindingResult bindingResult) {
		
		AjaxFormResponse response;
		if (bindingResult.hasErrors()) {
			response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
			response.setMissatge(getMessage(request, "processamentMultiple.error.validacio"));
			return response;
		}
		
		boolean correcte = false;
		String missatge = null;
		RegistreDto registreDto = null;
		try {
			logger.debug("Marcar per sobreescriure l'anotació amb id " + registreId);			
			EntitatDto entitatActual = this.getEntitatActualComprovantPermisAdmin(request);
			
			registreDto = registreService.findOne(entitatActual.getId(), registreId, false);

			if (RegistreProcesEstatEnum.isPendent(registreDto.getProcesEstat()) && !registreDto.isArxiuTancat()) {
				registreService.marcarSobreescriure(entitatActual.getId(), registreId);
				missatge = getMessage(request, "registre.admin.controller.marcar.sobreescriure.ok", new Object[] {registreId});
				correcte = true;
			} else {
				missatge = getMessage(request, "registre.admin.controller.marcar.sobreescriure.estat.error", new Object[] {registreId, registreDto.getProcesEstat()});
				correcte = false;
			}
			if (correcte) {
				response = AjaxHelper.generarAjaxFormOk();
				response.setMissatge(missatge.toString());
			} else {
				response = AjaxHelper.generarAjaxError(missatge.toString());
			}
			logger.debug(missatge);
		} catch(Exception e) {
			logger.error("Error incontrolat marcant per sobreescriure l'anotació amb id " + registreId + ": " + e.getMessage() , e);
			String errMsg = getMessage(request, 
										"contingut.admin.controller.registre.reintentat.massiva.errorNoControlat",
										new Object[] {(registreDto != null ? registreDto.getIdentificador() : String.valueOf(registreId)), e.getMessage()});
			response = AjaxHelper.generarAjaxError(errMsg);
		}
		return response;
	}
	
	
	@RequestMapping(value = "/reintentarProcessamentMultiple", method = RequestMethod.GET)
	public String reintentarProcessamentMultipleGet(
			HttpServletRequest request,
			Model model) {
		Object command = new Object();
		model.addAttribute("reintentarProcessamentCommand", command);
		model.addAttribute("registres",
				registreService.findMultiple(
						getEntitatActualComprovantPermisAdmin(request).getId(),
						this.getRegistresSeleccionats(request, SESSION_ATTRIBUTE_SELECCIO),
						true));
		return "reintentarProcessamentMultiple";
	}
	
	
	/** Mèdode per reprocessar una anotacions de registre via ajax des del llistat d'anotacions
	 * de l'administrador.
	 * @param request
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/reintentarProcessamentAjax/{registreId}", method = RequestMethod.POST)
	public AjaxFormResponse reintentarProcessamentAjaxPost(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@Valid Object command,
			BindingResult bindingResult) {
		
		AjaxFormResponse response;
		if (bindingResult.hasErrors()) {
			response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
			response.setMissatge(getMessage(request, "processamentMultiple.error.validacio"));
			return response;
		}
		
		boolean correcte = false;
		String missatge = null;
		ContingutDto contingutDto = null;
		RegistreDto registreDto = null;;
		try {
			logger.debug("Reprocessar anotació amb id " + registreId);
			
			EntitatDto entitatActual = this.getEntitatActualComprovantPermisAdmin(request);
			contingutDto = contingutService.findAmbIdAdmin(entitatActual.getId(), registreId, false);
			registreDto = (RegistreDto) contingutDto;
			
			if (registreDto.getPare() == null) {
				// Restaura la bústia per defecte i la la regla aplicable si s'escau
				correcte = registreService.reintentarBustiaPerDefecte(entitatActual.getId(),
						registreId);
				contingutDto = contingutService.findAmbIdAdmin(entitatActual.getId(), registreId, false);
				missatge = getMessage(request, "registre.admin.controller.reintentar.processament.pare.restaurat");
			} 
			else if ( ArrayUtils.contains(estatsReprocessables, registreDto.getProcesEstat())) 
			{
				if (registreDto.getProcesEstat() == RegistreProcesEstatEnum.ARXIU_PENDENT 
					|| registreDto.getProcesEstat() == RegistreProcesEstatEnum.REGLA_PENDENT) 
				{
					// Pendent de processament d'arxiu o regla
					correcte = registreService.reintentarProcessamentAdmin(entitatActual.getId(), 
							registreId);
					missatge = "Anotació reprocessada " + (correcte ? "correctament" : "amb error");
				} else {
					// Pendent d'enviar a backoffice
					correcte = registreService.reintentarEnviamentBackofficeAdmin(entitatActual.getId(), 
							registreId);
					missatge = "Anotació reenviada al backoffice " + (correcte ? "correctament" : "amb error");
				}
			} 
			else if (this.isPendentArxiu(registreDto)) {
				correcte = registreService.processarAnnexosAdmin(
						entitatActual.getId(),
						registreId);
				missatge = getMessage(
						request, 
						"contingut.admin.controller.registre.desat.arxiu." + (correcte ? "ok" : "error"),
						null);		
				boolean processatOk = registreService.processarAnnexosAdmin(
						entitatActual.getId(),
						registreId);
				if (processatOk) {
					MissatgesHelper.success(
							request, 
							getMessage(
									request, 
									"contingut.admin.controller.registre.desat.arxiu.ok",
									null));
				} else {
					MissatgesHelper.error(
							request,
							getMessage(
									request, 
									"contingut.admin.controller.registre.desat.arxiu.error",
									null));
				}
			} else 
			{
				missatge = getMessage(request, "registre.admin.controller.reintentar.processament.reprocessables.no.detectat");
				correcte = true;
			}
		} catch(Exception e) {
			logger.error("Error incontrolat reprocessant l'anotació amb id " + registreId + ": " + e.getMessage() , e);
			String errMsg = getMessage(request, 
										"contingut.admin.controller.registre.reintentat.massiva.errorNoControlat",
										new Object[] {(contingutDto != null ? contingutDto.getNom() : String.valueOf(registreId)), e.getMessage()});
			response = AjaxHelper.generarAjaxError(errMsg);
		}
		
		if (correcte) {
			response = AjaxHelper.generarAjaxFormOk();
			response.setMissatge(getMessage(request, missatge.toString()));
		} else {
			response = AjaxHelper.generarAjaxError(missatge.toString());
		}
		
		logger.debug("L'anotació amb id " + registreId + " " + (registreDto != null ? registreDto.getNom() : "") + " s'ha processat " + (correcte ? "correctament" : "amb error"));

		return response;
	}
	
	private boolean isPendentArxiu(RegistreDto registreDto) {
		boolean isPendentArxiu = false;
		if (registreDto.getExpedientArxiuUuid() == null) {
			isPendentArxiu = true;
		} else {
			for (RegistreAnnexDto registreAnnex : registreDto.getAnnexos()) {
				if (registreAnnex.getFitxerArxiuUuid() == null) {
					isPendentArxiu = true;
				}
			}
		}
		boolean annexosPendents = false;
		// Mirar si té uuid
		List<RegistreAnnexDto> llistatAnnexes = registreDto.getAnnexos();
		for (RegistreAnnexDto registreAnnex : llistatAnnexes) {
			if (registreAnnex.getFitxerArxiuUuid() == null) {
				annexosPendents = true;
			}
		}
		isPendentArxiu = registreDto.getArxiuUuid() == null || annexosPendents;
//		List<RegistreAnnexDto> llistatAnnexes = registreDto.getAnnexos();
//		for (RegistreAnnexDto registreAnnex : llistatAnnexes) {
//			if (registreAnnex.getFitxerArxiuUuid() == null) {
//				isPendentArxiu = true;
//			}
//		}
		return isPendentArxiu;
	}

	@RequestMapping(value = "/marcarPendentMultiple", method = RequestMethod.GET)
	public String marcarPendentMultipleGet(
			HttpServletRequest request,
			Model model) {
		MarcarProcessatCommand command = new MarcarProcessatCommand();
		model.addAttribute("marcarPendentCommand", command);
		model.addAttribute("registres", 
				registreService.findMultiple(
						getEntitatActualComprovantPermisAdmin(request).getId(),
						this.getRegistresSeleccionats(request, SESSION_ATTRIBUTE_SELECCIO),
						true));
	return "registreUserMarcarPendent";
	}


	@RequestMapping(value = "/ajaxBustia/{bustiaId}", method = RequestMethod.GET)
	@ResponseBody
	public BustiaDto getByCodi(
			HttpServletRequest request,
			@PathVariable String bustiaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		return bustiaService.findById(
				entitatActual.getId(),
				Long.parseLong(bustiaId));
	}

	@RequestMapping(value = "/ajaxBusties/{unitatCodi}/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<BustiaDto> get(
			HttpServletRequest request,
			@PathVariable String unitatCodi,
			@PathVariable String text,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		UnitatOrganitzativaDto unitatOrganitzativa = null;
		if (unitatCodi != null && !"null".equalsIgnoreCase(unitatCodi)) {
			unitatOrganitzativa = unitatOrganitzativaService.findByCodi(unitatCodi);
		}
		if ("undefined".equalsIgnoreCase(text) || "null".equalsIgnoreCase(text)) {
			text = "";
		}
		List<BustiaDto> bustiesFinals = new ArrayList<BustiaDto>();
		if (unitatOrganitzativa != null) {
			bustiesFinals = bustiaService.findAmbUnitatCodiAdmin(entitatActual.getId(), unitatCodi);
			if (text != null && bustiesFinals != null && !bustiesFinals.isEmpty()) {
				List<BustiaDto> bustiesFiltrades = new ArrayList<BustiaDto>();
				text = text.toUpperCase();
				for (BustiaDto bustia: bustiesFinals) {
					if (bustia.getNom().matches("(?i:.*" + text + ".*)")) {
						bustiesFiltrades.add(bustia);
					}
				}
				bustiesFinals = bustiesFiltrades;
			}
		} else {
			PaginacioParamsDto paginacioParams = new PaginacioParamsDto();
			paginacioParams.setPaginaTamany(Integer.MAX_VALUE);
			paginacioParams.afegirOrdre("nom", OrdreDireccioDto.ASCENDENT);
			BustiaFiltreDto filtre = new BustiaFiltreDto();
			filtre.setNom(text);
			bustiesFinals = bustiaService.findAmbFiltreAdmin(entitatActual.getId(), filtre, paginacioParams).getContingut();
		}
		return bustiesFinals;
	}
	
	
	@RequestMapping(value="/exportar", method = RequestMethod.GET)
	public String exportar(
			HttpServletRequest request,
			HttpServletResponse response, 
			Model model, 
			@RequestParam String llistat, 
			@RequestParam String[] filtresForm, 
			@RequestParam String format) throws IllegalAccessException, NoSuchMethodException  {
		
		List<RegistreDto> llistatRegistres = new ArrayList<RegistreDto>();
		if (llistat.equals("seleccio")) {
			llistatRegistres = registreService.findMultiple(
				getEntitatActualComprovantPermisAdmin(request).getId(), 
				this.getRegistresSeleccionats(request, SESSION_ATTRIBUTE_SELECCIO), 
				true);
		
		} else {
			EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
			List<BustiaDto> llistatBusties = bustiaService.findAmbEntitat(entitatActual.getId());
			RegistreFiltreDto registreFiltreDto = new RegistreFiltreDto();
			registreFiltreDto.setNumero(filtresForm[0]);
			registreFiltreDto.setTitol(filtresForm[1]);
			registreFiltreDto.setNumeroOrigen(filtresForm[2]);
			registreFiltreDto.setRemitent(filtresForm[3]);
			registreFiltreDto.setInteressat(filtresForm[4]);
			String stringDateInici = filtresForm[5];
			Date dataInici = null;
			try {
				dataInici = new SimpleDateFormat("dd/MM/yyyy").parse(stringDateInici);
			} catch (ParseException e) {
				dataInici = null;
			}
			registreFiltreDto.setDataRecepcioInici(dataInici);
			String stringDateFi = filtresForm[6];
			Date dataFi = null;
			try {
				dataFi = new SimpleDateFormat("dd/MM/yyyy").parse(stringDateFi);
			} catch (ParseException e) {
				dataFi = null;
			}
			registreFiltreDto.setDataRecepcioFi(dataFi);
			Long unitatId;
			if (!filtresForm[7].equals("")) {
				unitatId = Long.parseLong(filtresForm[7]);
			}else {
				unitatId = null;
			}
			registreFiltreDto.setUnitatId(unitatId);
			registreFiltreDto.setBustia(filtresForm[8]);
			RegistreEnviatPerEmailEnumDto enviatPerEmail;
			if (!filtresForm[9].equals("")) {
				enviatPerEmail = RegistreEnviatPerEmailEnumDto.valueOf(filtresForm[9]);
			}else {
				enviatPerEmail = null;
			}			
			registreFiltreDto.setEnviatPerEmail(enviatPerEmail);
			RegistreTipusDocFisicaEnumDto tipusDocumentacio;
			if (!filtresForm[10].equals("")) { 
				tipusDocumentacio = RegistreTipusDocFisicaEnumDto.valueOf(filtresForm[10]);
			}else {
				tipusDocumentacio = null;
			}
			registreFiltreDto.setTipusDocFisica(tipusDocumentacio);
			registreFiltreDto.setBackCodi(filtresForm[11]);
			RegistreProcesEstatSimpleEnumDto procesEstatSimple;
			if (!filtresForm[12].equals("")) {
				procesEstatSimple = RegistreProcesEstatSimpleEnumDto.valueOf(filtresForm[12]);
			}else {
				procesEstatSimple = null;
			}			
			registreFiltreDto.setProcesEstatSimple(procesEstatSimple);
			RegistreProcesEstatEnum estat;
			if (!filtresForm[13].equals("")) {
				estat = RegistreProcesEstatEnum.valueOf(filtresForm[13]);
			}else {
				estat = null;
			}
			registreFiltreDto.setEstat(estat);
			RegistreMarcatPerSobreescriureEnumDto sobreescriure;
			if (!filtresForm[14].equals("")) {
				sobreescriure = RegistreMarcatPerSobreescriureEnumDto.valueOf(filtresForm[14]);
			}else {
				sobreescriure = null;
			}
			registreFiltreDto.setSobreescriure(sobreescriure);
			List<Long> llistatFiltrat = registreService.findRegistreIds(entitatActual.getId(), llistatBusties, registreFiltreDto, false, true);
			
			llistatRegistres = registreService.findMultiple(
					getEntitatActualComprovantPermisAdmin(request).getId(), 
					llistatFiltrat, 
					true);			
		}
		
		FitxerDto fitxer;
		try {
			fitxer = registreHelper.exportarAnotacions(request, response, llistatRegistres, format);
			writeFileToResponse(
					fitxer.getNom(),
					fitxer.getContingut(),
					response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
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

	
	/** Mètode per validar les firmes d'un annex per mostrar informació de les firmes en el detall de l'annex.
	 * 
	 * @param request
	 * @param registreId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/registre/{registreId}/annex/{annexId}/validarFirmes", method = RequestMethod.GET)
	public String validarFirmesAnnex(HttpServletRequest request,
			@PathVariable Long registreId,
			@PathVariable Long annexId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		ValidacioFirmaEnum validacioFirma = registreService.validarFirmes(entitatActual.getId(), registreId, annexId);
		if (ValidacioFirmaEnum.isValida(validacioFirma)) {
			MissatgesHelper.success(request,
					getMessage(request,
							"contingut.admin.controller.validar.firmes.valides"));
		} else {
			MissatgesHelper.warning(request,
					getMessage(request,
							"contingut.admin.controller.validar.firmes.no.valides"));
		}
		return "redirect:" + request.getHeader("referer");
	}
	
	private static final Logger logger = LoggerFactory.getLogger(RegistreAdminController.class);
}
