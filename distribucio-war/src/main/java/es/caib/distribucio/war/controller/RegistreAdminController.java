/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
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

import es.caib.distribucio.core.api.dto.BackofficeDto;
import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.BustiaFiltreDto;
import es.caib.distribucio.core.api.dto.BustiaFiltreOrganigramaDto;
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.distribucio.core.api.dto.ProcedimentDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDto;
import es.caib.distribucio.core.api.dto.RegistreDto;
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
import es.caib.distribucio.war.command.RegistreFiltreCommand;
import es.caib.distribucio.war.helper.AjaxHelper;
import es.caib.distribucio.war.helper.AjaxHelper.AjaxFormResponse;
import es.caib.distribucio.war.helper.DatatablesHelper;
import es.caib.distribucio.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.war.helper.EnumHelper;
import es.caib.distribucio.war.helper.ExceptionHelper;
import es.caib.distribucio.war.helper.MissatgesHelper;
import es.caib.distribucio.war.helper.RequestSessionHelper;

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

	@RequestMapping(method = RequestMethod.GET)
	public String registreAdminGet(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		RegistreFiltreCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute(
				"tipusDocumentacio",
				EnumHelper.getOptionsForEnum(
						RegistreTipusDocFisicaEnumDto.class,
						"registre.tipus.doc.fisica.enum."));
		model.addAttribute(filtreCommand);
		List<BackofficeDto> backoffices = backofficeService.findByEntitat(
				entitatActual.getId());
		BackofficeDto backNull = new BackofficeDto();
		backNull.setNom("Sense backoffice");
		backNull.setCodi("senseBackoffice");
		backoffices.add(backNull);
		model.addAttribute("backoffices", backoffices);

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
			int numeroAnnexosFirmaInvalida = 0;
			int numeroAnnexosEstatEsborrany = 0;
			if (registreDto instanceof RegistreDto) {
				numeroAnnexosPendentsArxiu = this.numeroAnnexosPendentsArxiu((RegistreDto)registreDto);
				numeroAnnexosFirmaInvalida = this.numeroAnnexosFirmaInvalida((RegistreDto)registreDto);
				numeroAnnexosEstatEsborrany = this.numeroAnnexosEstatEsborrany((RegistreDto)registreDto);

				String codiSia = ((RegistreDto)registreDto).getProcedimentCodi();
				if (codiSia != null) {
					String procedimentNom = null;
					// Descripció del procediment
					try {
						ProcedimentDto procedimentDto = registreService.procedimentFindByCodiSia(entitatActual.getId(), codiSia);
						if (procedimentDto != null) {
							procedimentNom = procedimentDto.getNom();
						} else {
							String errMsg = getMessage(request, "registre.detalls.camp.procediment.no.trobat", new Object[] {codiSia});
							MissatgesHelper.warning(request, errMsg);
							procedimentNom = "(" + errMsg + ")";
						}
					}catch(NullPointerException e) {
						String errMsg = getMessage(request, "registre.detalls.camp.procediment.error", new Object[] {codiSia, e.getMessage()});
						logger.error(errMsg, e);
						MissatgesHelper.warning(request, errMsg);
						procedimentNom = "(" + errMsg + ")";
					}
					model.addAttribute("procedimentNom", procedimentNom);
				}
			}
			model.addAttribute("registre", registreDto);
			model.addAttribute("registreNumero", registreNumero);
			model.addAttribute("registreTotal", registreTotal);
			model.addAttribute("ordreColumn", ordreColumn);
			model.addAttribute("ordreDir", ordreDir);
			model.addAttribute("isVistaMoviments", false);
			model.addAttribute("numeroAnnexosPendentsArxiu", numeroAnnexosPendentsArxiu);
			model.addAttribute("numeroAnnexosFirmaInvalida", numeroAnnexosFirmaInvalida);
			model.addAttribute("numeroAnnexosEstatEsborrany", numeroAnnexosEstatEsborrany);
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
			List<BustiaDto> bustiesPermesesPerUsuari = new ArrayList<BustiaDto>();
			PaginaDto<ContingutDto> pagina = registreService.findRegistre(
								entitatActual.getId(),
								bustiesPermesesPerUsuari,
								RegistreFiltreCommand.asDto(registreFiltreCommand),
								paginacioParams,
								true);
			
			// Posa les dades dels registres al model segons la consulta
			if (pagina != null && !pagina.getContingut().isEmpty()) {
				registre = pagina.getContingut().get(0);///{registreId}/detall    /registre/{registreId}
				ret = "redirect:/modal/registreAdmin/" + registre.getId() + "/detall?registreNumero=" + registreNumero + "&registreTotal=" + pagina.getElementsTotal() + "&ordreColumn=" + ordreColumn + "&ordreDir=" + ordreDir;
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
			RegistreProcesEstatEnum.BACK_PENDENT,
			RegistreProcesEstatEnum.BACK_COMUNICADA,
			RegistreProcesEstatEnum.BACK_REBUDA,
			RegistreProcesEstatEnum.BACK_ERROR,
			RegistreProcesEstatEnum.BACK_PROCESSADA,
			RegistreProcesEstatEnum.BACK_REBUTJADA,
	};
	
	
	
	@RequestMapping(value = "/registre/{registreId}/reintentar", method = RequestMethod.GET)
	public String reintentar(
			HttpServletRequest request,
			@PathVariable Long registreId,
			Model model) {
		
		AjaxFormResponse response = this.reintentarProcessament(request, registreId);		
		if (response.isEstatOk()) {
			MissatgesHelper.success(
					request, 
					response.getMissatge());
		} else {
			MissatgesHelper.error(
					request,
					response.getMissatge());
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
	public String reintentarEnviamentBackoffice(
			HttpServletRequest request,
			@PathVariable Long registreId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		RegistreDto registreDto = registreService.findOne(entitatActual.getId(), registreId, false);
		boolean processatOk = registreService.reintentarEnviamentBackofficeAdmin(entitatActual.getId(),
				registreId);
		if (processatOk) {
			MissatgesHelper.success(request,
					getMessage(request,
							"contingut.admin.controller.registre.backoffice.reintentat.ok", new Object[] {registreDto.getBackCodi()}));
		} else {
			MissatgesHelper.error(request,
					getMessage(request,
							"contingut.admin.controller.registre.reintentat.error",
							null));
		}
		return "redirect:" + request.getHeader("referer");
	}
	
	
	@RequestMapping(value = "/reintentarEnviamentBackofficeMultiple", method = RequestMethod.GET)
	public String reintentarEnviamentBackofficeMultiple(
			HttpServletRequest request,
			Model model) {
		Object command = new Object();
		model.addAttribute("reintentarEnviamentBackofficeCommand", command);
		model.addAttribute("registres", registreService.findMultiple(
				getEntitatActualComprovantPermisAdmin(request).getId(), 
				this.getRegistresSeleccionats(request, SESSION_ATTRIBUTE_SELECCIO), 
				true));
		return "reintentarEnviamentBackofficeMultiple";
	}
	
	
	/** Mèdode per enviar al backoffice una anotacions de registre via ajax des del llistat d'anotacions
	 * de l'administrador.
	 * @param request
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/reintentarEnviamentBackofficeAjax/{registreId}", method = RequestMethod.POST)
	public AjaxFormResponse reintentarEnviamentBackofficeAjaxPost(
			HttpServletRequest request, 
			@PathVariable Long registreId, 
			@Valid Object command, 
			BindingResult bindingResult) {
		
		AjaxFormResponse response = null;
		
		if (bindingResult.hasErrors()) {
			response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
			response.setMissatge(getMessage(request, "enviamentMultiple.error.validacio"));
			return response;
		}
		
		boolean correcte = false;
		String missatge = null;
		ContingutDto contingutDto = null;
		RegistreDto registreDto = null;
		
		try {
			logger.debug("Reintentar enviament al backoffice l'anotació amb id " + registreId);
			
			EntitatDto entitatActual = this.getEntitatActualComprovantPermisAdmin(request);
			contingutDto = contingutService.findAmbIdAdmin(entitatActual.getId(), registreId, false);
			registreDto = (RegistreDto) contingutDto;
			
			if (registreDto.getPare() == null) {
				correcte = registreService.reintentarBustiaPerDefecte(entitatActual.getId(), registreId);
				contingutDto = contingutService.findAmbIdAdmin(entitatActual.getId(), registreId, false);
				missatge = getMessage(request, "registre.admin.controller.reintentar.processament.pare.restaurat");
			
			}else if (ArrayUtils.contains(estatsReprocessables, registreDto.getProcesEstat())) {
				correcte = registreService.reintentarEnviamentBackofficeAdmin(entitatActual.getId(), registreId);
				missatge = "Anotació reenviada al backoffice " + (correcte ? "correctament" : "amb error");
			
			}else {
				missatge = getMessage(request, "");
				correcte = true;
			}
		
		}catch (Exception e) {
			logger.error("Error incontrolat enviant al backoffice l'anotació amb id " + registreId + ": " + e.getMessage(), e);
			String errMsg = getMessage(
					request, 
					"contingut.admin.controller.registre.reintentat.massiva.errorNoControlat", 
					new Object[] {(contingutDto != null ? contingutDto.getNom() : String.valueOf(registreId)), e.getMessage()});
			response = AjaxHelper.generarAjaxError(errMsg);
		}
		
		if (correcte) {
			response = AjaxHelper.generarAjaxFormOk();
			response.setMissatge(missatge.toString());
			
		}else {
			response = AjaxHelper.generarAjaxError(missatge.toString());
		}
		
		logger.debug("L'anotació amb id " + registreId + " " + (registreDto != null ? registreDto.getNom() : "") + " s'ha enviat al backoffice " + (correcte ? "correctament" : "amb error"));
		
		return response;
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

		response = this.reintentarProcessament(request, registreId);
		
		/*
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
			} else 
			{
				missatge = getMessage(request, "registre.admin.controller.reintentar.processament.reprocessables.no.detectat");
				correcte = true;
			}
		} catch(Exception e) {
			missatge = getMessage(request, "registre.admin.controller.reintentar.processament.error", new Object[] {registreId, e.getMessage()});
			logger.error(missatge, e);
			correcte = false;
		}
		
		if (correcte) {
			response = AjaxHelper.generarAjaxFormOk();
			response.setMissatge(missatge.toString());
		} else {
			response = AjaxHelper.generarAjaxError(missatge);
		}
		
		*/
		logger.debug("L'anotació amb id " + registreId + " s'ha processat " + (response.isEstatOk() ? "correctament." : "amb error.") + response.getMissatge());

		return response;
	}
	
	private AjaxFormResponse reintentarProcessament(HttpServletRequest request, Long registreId) {

		AjaxFormResponse response = null;
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
			} else 
			{
				missatge = getMessage(request, "registre.admin.controller.reintentar.processament.reprocessables.no.detectat");
				correcte = true;
			}
		} catch(Exception e) {
			missatge = getMessage(request, "registre.admin.controller.reintentar.processament.error", new Object[] {registreId, e.getMessage()});
			logger.error(missatge, e);
			correcte = false;
		}
		
		if (correcte) {
			response = AjaxHelper.generarAjaxFormOk();
			response.setMissatge(missatge.toString());
		} else {
			response = AjaxHelper.generarAjaxError(missatge);
		}
		
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
		List<RegistreAnnexDto> llistatAnnexes = registreDto.getAnnexos();
		for (RegistreAnnexDto registreAnnex : llistatAnnexes) {
			if (registreAnnex.getFitxerArxiuUuid() == null) {
				isPendentArxiu = true;
			}
		}
		return isPendentArxiu;
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
