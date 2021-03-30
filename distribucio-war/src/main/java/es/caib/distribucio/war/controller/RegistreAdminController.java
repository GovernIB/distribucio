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

import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.BustiaFiltreDto;
import es.caib.distribucio.core.api.dto.BustiaFiltreOrganigramaDto;
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.distribucio.core.api.dto.RegistreDto;
import es.caib.distribucio.core.api.dto.RegistreProcesEstatSimpleEnumDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.service.BustiaService;
import es.caib.distribucio.core.api.service.ContingutService;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.core.api.service.UnitatOrganitzativaService;
import es.caib.distribucio.war.command.RegistreFiltreCommand;
import es.caib.distribucio.war.helper.DatatablesHelper;
import es.caib.distribucio.war.helper.DatatablesHelper.DatatablesResponse;
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
	

	@RequestMapping(method = RequestMethod.GET)
	public String registreAdminGet(
			HttpServletRequest request,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		RegistreFiltreCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute(filtreCommand);

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
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		RegistreFiltreCommand filtreCommand = getFiltreCommand(request);
				
		return DatatablesHelper.getDatatableResponse(
				request,
				registreService.findRegistre(
						entitatActual.getId(),
						null, // bustiesUsuari
						RegistreFiltreCommand.asDto(filtreCommand),
						false,
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
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		try {
			
			ContingutDto registreDto = contingutService.findAmbIdAdmin(
					entitatActual.getId(),
					registreId,
					true);
			
			model.addAttribute(
					"registre",
					registreDto);
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
			seleccio.addAll(
					registreService.findRegistreIds(
							entitatActual.getId(),
							null, // bustiesUsuari
							RegistreFiltreCommand.asDto(filtreCommand),
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		boolean processatOk = registreService.reintentarProcessamentAdmin(
				entitatActual.getId(),
				registreId);
		if (processatOk) {
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"contingut.admin.controller.registre.reintentat.ok",
							null));
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
	
	
	@RequestMapping(value = "/registre/{registreId}/reintentarEnviamentBackoffice", method = RequestMethod.GET)
	public String reintentarEnviamentBackoffice(HttpServletRequest request,
			@PathVariable Long registreId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			boolean processatOk = registreService.reintentarEnviamentBackofficeAdmin(entitatActual.getId(),
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



		return "redirect:../../" + registreId + "/detall";
	}
	
	
	/** Mèdode per reprocessar una selecció d'anotacions de registre des del llistat d'anotacions
	 * de l'administrador.
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/reintentarProcessamentMultiple", method = RequestMethod.GET)
	public String reintentarProcessamentMultiple(
			HttpServletRequest request,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio != null && !seleccio.isEmpty()) {
			List<Long> seleccioList = new ArrayList<Long>();
			seleccioList.addAll(seleccio);
			
			int errors = 0;
			int correctes = 0;
			int estatErroni = 0;
			boolean processatOk;
			// Reintenta el processament de les anotacions seleccionades
			ContingutDto contingutDto;
			for (Long registreId : seleccioList) {
				contingutDto = null;
				try {
					logger.debug("Reprocessar anotació amb id " + registreId);
					contingutDto = contingutService.findAmbIdAdmin(entitatActual.getId(), registreId, false);
					RegistreDto registreDto = (RegistreDto) contingutDto;
					if (registreDto.getPare() == null) {
						// Restaura la bústia per defecte i la la regla aplicable si s'escau
						processatOk = registreService.reintentarBustiaPerDefecte(entitatActual.getId(),
								registreId);
						contingutDto = contingutService.findAmbIdAdmin(entitatActual.getId(), registreId, false);
					} 
					else if ( ArrayUtils.contains(estatsReprocessables, registreDto.getProcesEstat())) 
					{
						if (registreDto.getProcesEstat() == RegistreProcesEstatEnum.ARXIU_PENDENT 
							|| registreDto.getProcesEstat() == RegistreProcesEstatEnum.REGLA_PENDENT) 
						{
							// Pendent de processament d'arxiu o regla
							processatOk = registreService.reintentarProcessamentAdmin(entitatActual.getId(), 
									registreId);
							
						} else {
							// Pendent d'enviar a backoffice
							processatOk = registreService.reintentarEnviamentBackofficeAdmin(entitatActual.getId(), 
									registreId);
						}
						if (processatOk)
							correctes++;
						else
							errors++;
						logger.debug("L'anotació amb id " + registreId + " " + registreDto.getNom() + " s'ha processat " + (processatOk ? "correctament" : "amb error"));
					} 
					else {
						logger.debug("L'estat de l'anotació amb id " + registreId + " és " + registreDto.getProcesEstat() + " i no es reprocessarà.");
						estatErroni++;
					}
				} catch(Exception e) {
					logger.error("Error incontrolat reprocessant l'anotació amb id " + registreId + ": " + e.getMessage() , e);
					String errMsg = getMessage(request, 
												"contingut.admin.controller.registre.reintentat.massiva.errorNoControlat",
												new Object[] {(contingutDto != null ? contingutDto.getNom() : String.valueOf(registreId)), e.getMessage()});
					MissatgesHelper.error(request, errMsg);
					errors++;
				}
			}
			
			if (correctes > 0){
				MissatgesHelper.success(request,
						getMessage(request, "contingut.admin.controller.registre.reintentat.massiva.correctes", new Object[]{correctes}));
			} 
			if (errors > 0) {
				MissatgesHelper.error(request,
						getMessage(request, "contingut.admin.controller.registre.reintentat.massiva.errors", new Object[]{errors}));
			} 
			if (estatErroni > 0) {
				MissatgesHelper.warning(request,
						getMessage(request,
								"contingut.admin.controller.registre.reintentat.massiva.estatErroni", new Object[]{estatErroni}));
			}
		} else {
			MissatgesHelper.error(request,
					getMessage(request,
							"contingut.admin.controller.registre.reintentat.massiva.cap"));
		}
		return "redirect:/registreAdmin";
	}
	
	

	@RequestMapping(value = "/ajaxBustia/{bustiaId}", method = RequestMethod.GET)
	@ResponseBody
	public BustiaDto getByCodi(
			HttpServletRequest request,
			@PathVariable String bustiaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
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

	private static final Logger logger = LoggerFactory.getLogger(RegistreAdminController.class);
}
