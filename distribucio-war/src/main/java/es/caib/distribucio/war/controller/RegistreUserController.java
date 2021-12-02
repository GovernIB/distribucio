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
import org.springframework.web.servlet.ModelAndView;

import es.caib.distribucio.core.api.dto.AlertaDto;
import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.ClassificacioResultatDto;
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.HistogramPendentsEntryDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDto;
import es.caib.distribucio.core.api.dto.RegistreDto;
import es.caib.distribucio.core.api.dto.RegistreProcesEstatSimpleEnumDto;
import es.caib.distribucio.core.api.dto.UsuariDto;
import es.caib.distribucio.core.api.dto.UsuariPermisDto;
import es.caib.distribucio.core.api.exception.EmptyMailException;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.exception.PermissionDeniedException;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.service.AlertaService;
import es.caib.distribucio.core.api.service.AplicacioService;
import es.caib.distribucio.core.api.service.BustiaService;
import es.caib.distribucio.core.api.service.ConfigService;
import es.caib.distribucio.core.api.service.ContingutService;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.war.command.ContingutReenviarCommand;
import es.caib.distribucio.war.command.MarcarProcessatCommand;
import es.caib.distribucio.war.command.RegistreClassificarCommand;
import es.caib.distribucio.war.command.RegistreClassificarCommand.Classificar;
import es.caib.distribucio.war.command.RegistreEnviarIProcessarCommand;
import es.caib.distribucio.war.command.RegistreEnviarViaEmailCommand;
import es.caib.distribucio.war.command.RegistreFiltreCommand;
import es.caib.distribucio.war.helper.DatatablesHelper;
import es.caib.distribucio.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.war.helper.ElementsPendentsBustiaHelper;
import es.caib.distribucio.war.helper.ExceptionHelper;
import es.caib.distribucio.war.helper.MissatgesHelper;
import es.caib.distribucio.war.helper.RequestSessionHelper;
import es.caib.distribucio.war.helper.RolHelper;

/**
 * Controlador per al manteniment de registres.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/registreUser")
public class RegistreUserController extends BaseUserController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "RegistreUserController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SELECCIO = "RegistreUserController.session.seleccio";
	private static final String SESSION_ATTRIBUTE_SELECCIO_MOVIMENTS = "RegistreUserController.session.seleccio.moviments";

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
	@Autowired
	private ConfigService configService;	
	
	
	@RequestMapping(method = RequestMethod.GET)
	public String registreUserGet(
			HttpServletRequest request,
			Model model) {
		RegistreFiltreCommand filtreCommand = getFiltreCommand(request);
		UsuariDto usuari = aplicacioService.getUsuariActual();
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		resetFiltreBustia(request, filtreCommand);
//		###
		BustiaDto bustiaPerDefecte = aplicacioService.getBustiaPerDefecte(usuari, entitatActual.getId());
		model.addAttribute(filtreCommand);		
		model.addAttribute("isPermesReservarAnotacions", isPermesReservarAnotacions());
		model.addAttribute("isEnviarConeixementActiu", isEnviarConeixementActiu());
		if (bustiaPerDefecte != null)
			model.addAttribute("bustiaPerDefecte", bustiaPerDefecte.getId());
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
				registreService.findRegistre(
						entitatActual.getId(),
						bustiesPermesesPerUsuari,
						RegistreFiltreCommand.asDto(registreFiltreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request), 
						false),
				"id",
				SESSION_ATTRIBUTE_SELECCIO);
	}
	
	
	
	@RequestMapping(value = "/moviments", method = RequestMethod.GET)
	public String registreUserMovimentsGet(
			HttpServletRequest request,
			Model model) {
		RegistreFiltreCommand filtreCommand = getFiltreCommand(request);
		model.addAttribute("bustiaPerDefecte", null);
		model.addAttribute(filtreCommand);
		return "registreUserMovimentsList";
	}
	
	@RequestMapping(value = "/moviments", method = RequestMethod.POST)
	public String registreUserMoviementsPost(
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
		return "redirect:moviments";
	}
	
	@RequestMapping(value = "/moviments/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse registreUserMovimentsDatatable(
			HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		RegistreFiltreCommand registreFiltreCommand = getFiltreCommand(request);
		List<BustiaDto> bustiesPermesesPerUsuari = bustiaService.findBustiesPermesesPerUsuari(
				entitatActual.getId(), 
				registreFiltreCommand.isMostrarInactives());
		return DatatablesHelper.getDatatableResponse(
				request,
				registreService.findMovimentsRegistre(
						entitatActual.getId(),
						bustiesPermesesPerUsuari,
						RegistreFiltreCommand.asDto(registreFiltreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"movimentId",
				SESSION_ATTRIBUTE_SELECCIO_MOVIMENTS);
	}
	
	
	/** Mètode Ajax per seleccionar tots els registres a partir del mateix filtre del datatable.
	 * 
	 * @param request
	 * @param ids
	 * @return Retorna el número d'elements seleccionats
	 */
	@RequestMapping(value = "/select/moviments", method = RequestMethod.GET)
	@ResponseBody
	public int selectMovimentsByRegistreBustia(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) String[] ids) {
		@SuppressWarnings("unchecked")
		Set<String> seleccio = (Set<String>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO_MOVIMENTS);
		if (seleccio == null) {
			seleccio = new HashSet<String>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO_MOVIMENTS,
					seleccio);
		}
		if (ids != null) {
			//per cada registre-desti diferent
			for (String id: ids) {
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
					registreService.findRegistreMovimentsIds(
							entitatActual.getId(),
							bustiesUsuari,
							RegistreFiltreCommand.asDto(filtreCommand),
							false));
		}
		return seleccio.size();
	}

	@RequestMapping(value = "/deselect/moviments", method = RequestMethod.GET)
	@ResponseBody
	public int deselectMoviments(
			HttpServletRequest request,
			@RequestParam(value="ids[]", required = false) String[] ids) {
		@SuppressWarnings("unchecked")
		Set<String> seleccio = (Set<String>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO_MOVIMENTS);
		if (seleccio == null) {
			seleccio = new HashSet<String>();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO_MOVIMENTS,
					seleccio);
		}
		if (ids != null) {
			for (String id: ids) {
				seleccio.remove(id);
			}
		} else {
			seleccio.clear();
		}
		return seleccio.size();
	}
	
	
	
	/** Retorna el llistat de bústies permeses per a l'usuari. Pot incloure o no les innactives */
	@RequestMapping(value = "/bustiesOrigen", method = RequestMethod.GET)
	@ResponseBody
	public List<BustiaDto> bustiesOrigen(
			HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "false") boolean mostrarInactivesOrigen,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		List<BustiaDto> bustiesPermesesPerUsuari = bustiaService.findBustiesPermesesPerUsuari(entitatActual.getId(), true);
		List<BustiaDto> bustiesOrigen = bustiaService.consultaBustiesOrigen(
				entitatActual.getId(), 
				bustiesPermesesPerUsuari, 
				mostrarInactivesOrigen);
		return bustiesOrigen;
	}
	
	/** Redirecciona les urls dels emails amb notificació de nova anotació que encara contenen /bustia/bustiaId
	 * a la URL.
	 * @return Retorna una redirecció cap a la URL normal.
	 */
	@RequestMapping(value = "/bustia/{bustiaId}/registre/{registreId}", method = RequestMethod.GET)
	public ModelAndView registreUserDetallRedirect(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			@RequestParam(value="registreNumero", required=false) Integer registreNumero,
			@RequestParam(value="registreTotal", required = false) Integer registreTotal,
			@RequestParam(value="ordreColumn", required = false) String ordreColumn,
			@RequestParam(value="ordreDir", required = false) String ordreDir,
			Model model) {
        model.addAttribute("registreNumero", registreNumero);
        model.addAttribute("registreTotal", registreTotal);
        model.addAttribute("ordreColumn", ordreColumn);
        model.addAttribute("ordreDir", ordreDir);
        return new ModelAndView("redirect:/registreUser/registre/" + registreId, model.asMap());
	}
		
	@RequestMapping(value = "/registre/{registreId}", method = RequestMethod.GET)
	public String registreUserDetall(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@RequestParam(value="registreNumero", required=false) Integer registreNumero,
			@RequestParam(value="registreTotal", required = false) Integer registreTotal,
			@RequestParam(value="ordreColumn", required = false) String ordreColumn,
			@RequestParam(value="ordreDir", required = false) String ordreDir,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			@RequestParam(required=false) Long destiLogic,
			Model model) {
		return getRegistreDetall(
				request, 
				registreId, 
				isVistaMoviments, 
				destiLogic,
				registreNumero, 
				registreTotal, 
				ordreColumn, 
				ordreDir, 
				model);
	}	
	
	private String getRegistreDetall(
			HttpServletRequest request,
			Long registreId,
			boolean isVistaMoviments,
			Long destiLogic,
			Integer registreNumero,
			Integer registreTotal,
			String ordreColumn,
			String ordreDir,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		try {
			RegistreDto registre = registreService.findOne(
							entitatActual.getId(),
							registreId,
							isVistaMoviments,
							RolHelper.getRolActual(request));
			//recupera la ruta del destí lògic del moviment o no l'actual
			if (isVistaMoviments && destiLogic != null) {
				List<ContingutDto> path = registreService.getPathContingut(entitatActual.getId(), destiLogic);
				registre.setPath(path);
			}
			model.addAttribute("registre", registre);
			model.addAttribute("registreNumero", registreNumero);
			model.addAttribute("registreTotal", registreTotal);
			model.addAttribute("ordreColumn", ordreColumn);
			model.addAttribute("ordreDir", ordreDir);
			model.addAttribute("isPermesReservarAnotacions", isPermesReservarAnotacions());
			model.addAttribute("isEnviarConeixementActiu", isEnviarConeixementActiu());
			model.addAttribute("isVistaMoviments", isVistaMoviments);
			model.addAttribute("destiLogic", destiLogic);
		} catch (Exception e) {
			Throwable thr = ExceptionHelper.getRootCauseOrItself(e);
			if (thr.getClass() == NotFoundException.class) {
				NotFoundException exc = (NotFoundException) thr;
				if (exc.getObjectClass().getName().equals("es.caib.distribucio.core.entity.RegistreEntity")) {
					model.addAttribute("errorTitol", getMessage(request, "error.titol.not.found"));
					model.addAttribute("missatgeError", getMessage(request, "registre.detalls.notFound"));
					return "ajaxErrorPage";
				} else {
					throw e;
				}
			} else if (thr.getClass() == PermissionDeniedException.class) {
				PermissionDeniedException exc = (PermissionDeniedException) thr;
				if (exc.getObjectClass().getName().equals("es.caib.distribucio.core.entity.BustiaEntity") ) {
					model.addAttribute("missatgeError", getMessage(request, "registre.detalls.noPermisos", new Object[] { exc.getParam() }));
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
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
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
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			RegistreFiltreCommand registreFiltreCommand = getFiltreCommand(request);
			List<BustiaDto> bustiesPermesesPerUsuari = null;
			if (registreFiltreCommand.getBustia() == null || registreFiltreCommand.getBustia().isEmpty()) {
				bustiesPermesesPerUsuari = bustiaService.findBustiesPermesesPerUsuari(entitatActual.getId(), registreFiltreCommand.isMostrarInactives());
			}
			PaginaDto<ContingutDto> pagina = null;
			if (isVistaMoviments) {
				pagina = registreService.findMovimentRegistre(
								entitatActual.getId(),
								bustiesPermesesPerUsuari,
								RegistreFiltreCommand.asDto(registreFiltreCommand),
								paginacioParams,
								false);
			} else {
				pagina = registreService.findRegistre(
								entitatActual.getId(),
								bustiesPermesesPerUsuari,
								RegistreFiltreCommand.asDto(registreFiltreCommand),
								paginacioParams,
								false);
			}
			// Posa les dades dels registres al model segons la consulta
			if (pagina != null && !pagina.getContingut().isEmpty()) {
				registre = pagina.getContingut().get(0);
				ret = "redirect:/modal/registreUser/registre/" + registre.getId() + "?registreNumero=" + registreNumero + "&registreTotal=" + pagina.getElementsTotal() + "&ordreColumn=" + ordreColumn + "&ordreDir=" + ordreDir + "&isVistaMoviments=" + isVistaMoviments + (isVistaMoviments ? "&destiLogic=" + registre.getDestiLogic() : "");
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

	@RequestMapping(value = "/registreAnnex/{registreId}/{annexId}", method = RequestMethod.GET)
	public String registreAnnex(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@PathVariable Long annexId,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			Model model) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			model.addAttribute(
					"annex",
					registreService.getAnnexSenseFirmes(
							entitatActual.getId(),
							registreId,
							annexId,
							isVistaMoviments));
			model.addAttribute("registreId", registreId);
			model.addAttribute("concsvBaseUrl", configService.getConcsvBaseUrl());
		} catch(Exception ex) {
			logger.error("Error recuperant informació de l'annex", ex);
			model.addAttribute("missatgeError", ex.getMessage());
			return "ajaxErrorPage";
		}
		return "registreAnnex";
	}
	
	@RequestMapping(value = "/registreAnnexFirmes/{registreId}/{annexId}/{isResum}", method = RequestMethod.GET)
	public String registreAnnexFirmes(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@PathVariable Long annexId,
			@PathVariable boolean isResum,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			Model model) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			model.addAttribute(
					"annex",
					registreService.getAnnexAmbFirmes(
							entitatActual.getId(),
							registreId,
							annexId,
							isVistaMoviments));
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
	
	@RequestMapping(value = "/registreAnnexFirmes/{registreId}/{annexId}", method = RequestMethod.GET)
	@ResponseBody
	public RegistreAnnexDto registreAnnexFirmes(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@PathVariable Long annexId,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			Model model) {
		RegistreAnnexDto annexFirmes = new RegistreAnnexDto();
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			annexFirmes = registreService.getAnnexAmbFirmes(
							entitatActual.getId(),
							registreId,
							annexId,
							isVistaMoviments);
		} catch(Exception ex) {
			logger.error("Error recuperant informació de firma", ex);
			model.addAttribute("missatgeError", ex.getMessage());
		}

		return annexFirmes;
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
					registreService.findRegistreIds(
							entitatActual.getId(),
							bustiesUsuari,
							RegistreFiltreCommand.asDto(filtreCommand),
							false, 
							false));
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
	
	
	
	@RequestMapping(value = "/anotacionsPendentArxiu", method = RequestMethod.GET)
	public String anotacionsPendentArxiu(
			HttpServletRequest request,
			Model model) throws IOException {
		
		model.addAttribute("numberThreads", registreService.getNumberThreads());

		return "histogramPendents";
	}
	
	
	@RequestMapping(value = "/getHistogramPendents", method = RequestMethod.GET)
	@ResponseBody
	public List<HistogramPendentsEntryDto> expedientsEntitatChartData(HttpServletRequest request) {
		
		List<HistogramPendentsEntryDto> histogram = registreService.getHistogram();

		return histogram;
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

	@RequestMapping(value = "/enviarViaEmail/{contingutId}", method = RequestMethod.GET)
	public String enviarViaEmailGet(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			Model model) {
		RegistreEnviarViaEmailCommand command = new RegistreEnviarViaEmailCommand();
		command.setContingutId(contingutId);
		model.addAttribute(command);
		return "registreViaEmail";
	}

	@RequestMapping(value = "/enviarViaEmail/{contingutId}", method = RequestMethod.POST)
	public String enviarViaEmailPost(
			HttpServletRequest request,
			@Valid RegistreEnviarViaEmailCommand command,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
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
					command.getContingutId(),
					adreces, 
					command.getMotiu(),
					isVistaMoviments,
					RolHelper.getRolActual(request));
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
			logger.error(errMsg);
			return "registreViaEmail";
		}
		
	}
	
	
	
	@RequestMapping(value = "/enviarViaEmailMultiple", method = RequestMethod.GET)
	public String enviarViaEmailMultipleGet(
			HttpServletRequest request,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			Model model) {
		RegistreEnviarViaEmailCommand command = new RegistreEnviarViaEmailCommand();
		model.addAttribute(command);
		model.addAttribute("isVistaMoviments", isVistaMoviments);
		return "registreViaEmail";
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/enviarViaEmailMultiple", method = RequestMethod.POST)
	public String enviarViaEmailMultiplePost(
			HttpServletRequest request,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			@Valid RegistreEnviarViaEmailCommand command,
			BindingResult bindingResult,
			Model model)  {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		Set<Long> seleccio = new HashSet<Long>();
		
		String adreces = this.revisarAdreces(request, command.getAddresses(), bindingResult);
		if (bindingResult.hasErrors()) {
			return "registreViaEmail";
		}
		if (isVistaMoviments) {
//			## ID = ID_REGISTRE + ID_DESTI (extreure registre)
			List<String> seleccioMoviments = (List<String>)RequestSessionHelper.obtenirObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO_MOVIMENTS);
			if (seleccioMoviments != null && !seleccioMoviments.isEmpty()) {
				for (String idVistaMoviment: seleccioMoviments) {
					seleccio.add(Long.valueOf(idVistaMoviment.split("_")[0]));
				}
			}
		} else {
			seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO);
		}
		
		if (seleccio != null && !seleccio.isEmpty()) {
			enviarViaEmailMultiple(seleccio, request, entitatActual, adreces, command.getMotiu(), isVistaMoviments);
		} else {
			MissatgesHelper.error(request,
					getMessage(request,
							isVistaMoviments ? "registre.user.controller.massiva.enviament.cap" : "registre.user.controller.massiva.cap"));
		}
		return modalUrlTancar();
		
	}
	
	private void enviarViaEmailMultiple(Set<Long> seleccio, HttpServletRequest request, 
			EntitatDto entitatActual, String adreces, String motiu, boolean isVistaMoviments) {
		enviarViaEmailMultiple(seleccio, request, entitatActual, adreces, motiu, null, isVistaMoviments);
	}
	private void enviarViaEmailMultiple(Set<Long> seleccio, HttpServletRequest request, 
			EntitatDto entitatActual, String adreces, String motiu, List<Long> registreIdErronis, boolean isVistaMoviments) {

		List<Long> seleccioList = new ArrayList<Long>();
		seleccioList.addAll(seleccio);
		
		int errors = 0;
		int correctes = 0;
		int estatErroni = 0;
		
		for (Long registreId : seleccioList) {
			RegistreDto registreDto = registreService.findOne(
					entitatActual.getId(), 
					registreId, 
					false,
					RolHelper.getRolActual(request));
			if (registreDto.getProcesEstat() != RegistreProcesEstatEnum.ARXIU_PENDENT || registreDto.isReintentsEsgotat()) {
				
				boolean processatOk = true;
				try {
					bustiaService.registreAnotacioEnviarPerEmail(
							entitatActual.getId(),
							registreDto.getId(),
							adreces, 
							motiu,
							isVistaMoviments,
							RolHelper.getRolActual(request));
					
				} catch (Exception e) {
					MissatgesHelper.error(
							request,
							getMessage(
									request, 
									"registre.user.controller.enviar.email.massiva.error",
									new Object[] {(registreDto != null ? registreDto.getNom() : String.valueOf(registreId)), e.getMessage()}));
					processatOk = false;
				}
				
				if (processatOk)
					correctes++;
				else {
					errors++;
					if (registreIdErronis != null)
						registreIdErronis.add(registreId);
				}
			} 
			else {
				logger.debug("L'estat de l'anotació amb id " + registreId + " és " + registreDto.getProcesEstat() + " i no es envia via email.");
				estatErroni++;
			}
		}
		if (correctes > 0){
			MissatgesHelper.success(request, getMessage(request, "registre.user.controller.enviar.email.massiva.correctes", new Object[]{correctes}));
		} 
		if (errors > 0) {
			MissatgesHelper.error(request, getMessage(request, "registre.user.controller.enviar.email.massiva.errors", new Object[]{errors}));
		} 
		if (estatErroni > 0) {
			MissatgesHelper.warning(request, getMessage(request, "registre.user.controller.enviar.email.massiva.estatErroni", new Object[]{estatErroni}));
		}
	
	}
	
	@RequestMapping(value = "/enviarIProcessarMultiple", method = RequestMethod.GET)
	public String enviarIProcessarMultipleGet(
			HttpServletRequest request,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		RegistreEnviarIProcessarCommand command = new RegistreEnviarIProcessarCommand();
		model.addAttribute(command);
		return "registreUserEnviarIProcessar";
	}

	@RequestMapping(value = "/enviarIProcessarMultiple", method = RequestMethod.POST)
	public String enviarIProcessarMultiplePost(
			HttpServletRequest request,
			@Valid RegistreEnviarIProcessarCommand command,
			BindingResult bindingResult,
			Model model)  {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		String rolActual = RolHelper.getRolActual(request);
		List<Long> registreIdErronis = new ArrayList<Long>();
		
		String adreces = this.revisarAdreces(request, command.getAddresses(), bindingResult);
		if (bindingResult.hasErrors()) {
			return "registreUserEnviarIProcessar";
		}
		
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio != null && !seleccio.isEmpty()) {
			enviarViaEmailMultiple(seleccio, request, entitatActual, adreces, command.getMotiu(), registreIdErronis, false);
			marcarProcessatMultiple(seleccio, request, entitatActual, command.getMotiu(), rolActual, registreIdErronis);
		} else {
			MissatgesHelper.error(request, getMessage(request, "registre.user.controller.massiva.cap"));
		}
		
		return modalUrlTancar();
		
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

	@RequestMapping(value = "/pendent/{registreId}/reenviar", method = RequestMethod.GET)
	public String registreReenviarGet(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@RequestParam(value="registreNumero", required = false) Integer registreNumero,
			@RequestParam(value="registreTotal", required = false) Integer registreTotal,
			@RequestParam(value="ordreColumn", required = false) String ordreColumn,
			@RequestParam(value="ordreDir", required = false) String ordreDir,
			@RequestParam(value="avanzarPagina", required = false) String avanzarPagina,
			Model model) {
		return registreReenviar(
				request, 
				registreId,
				registreNumero, 
				registreTotal, 
				ordreColumn, 
				ordreDir, 
				avanzarPagina,
				null,
				model);
	}
	
	@RequestMapping(value = "/pendent/{registreId}/{destiLogic}/reenviar", method = RequestMethod.GET)
	public String registreReenviarDestiLogicGet(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@PathVariable Long destiLogic,
			@RequestParam(value="registreNumero", required = false) Integer registreNumero,
			@RequestParam(value="registreTotal", required = false) Integer registreTotal,
			@RequestParam(value="ordreColumn", required = false) String ordreColumn,
			@RequestParam(value="ordreDir", required = false) String ordreDir,
			@RequestParam(value="avanzarPagina", required = false) String avanzarPagina,
			Model model) {
		return registreReenviar(
				request, 
				registreId,
				registreNumero, 
				registreTotal, 
				ordreColumn, 
				ordreDir, 
				avanzarPagina,
				destiLogic,
				model);
	}
	
	private String registreReenviar(
			HttpServletRequest request,
			Long registreId,
			Integer registreNumero,
			Integer registreTotal,
			String ordreColumn,
			String ordreDir,
			String avanzarPagina,
			Long destiLogic,
			Model model) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			omplirModelPerReenviar(entitatActual, registreId, model);
			ContingutReenviarCommand command = new ContingutReenviarCommand();
			RegistreFiltreCommand filtre = getFiltreCommand(request);
			//boolean isAvanzarNullAndLastRegistre = avanzarPagina == null && (registreNumero == registreTotal);
			boolean isNotAvanzarAndNotLastRegistre = !Boolean.parseBoolean(avanzarPagina) && (registreTotal != null && registreNumero < registreTotal);
			boolean isNotAvanzarAndLastRegistre = Boolean.parseBoolean(avanzarPagina) && (registreTotal != null && registreNumero == registreTotal);
			boolean isAvanzarAndWithoutFilter = filtre.getBustia() == null && Boolean.parseBoolean(avanzarPagina);
			boolean isAvanzarWithFilterAndNotLastRegistre = filtre.getBustia() != null && (registreTotal != null && registreNumero != (registreTotal-1)) && Boolean.parseBoolean(avanzarPagina); // si està filtrat i és la penúltima pàgina
			String isVistaMoviments = destiLogic != null ? "true" : "false";
			
 			if (registreNumero != null && (isAvanzarAndWithoutFilter || isAvanzarWithFilterAndNotLastRegistre) || isNotAvanzarAndLastRegistre || isNotAvanzarAndNotLastRegistre) {
				// si params is empty tanca la modal
				command.setParams(new String [] {String.valueOf(registreNumero), ordreColumn, ordreDir, avanzarPagina, isVistaMoviments});
			}
			model.addAttribute(command);
			model.addAttribute("maxLevel", getMaxLevelArbre());
			model.addAttribute("isEnviarConeixementActiu", isEnviarConeixementActiu());
			model.addAttribute("isFavoritsPermes", isFavoritsPermes());
			model.addAttribute("isMostrarPermisosBustiaPermes", isMostrarPermisosBustiaPermes());
			model.addAttribute("destiLogic", destiLogic);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (NotFoundException.class.equals((e.getCause() != null ? e.getCause() : e).getClass())) {
				return getModalControllerReturnValueError(
						request,
						"redirect:/registreUser/registre/" + registreId,
						"registre.user.controller.reenviar.error.registreNoTrobat");
			} else {
				return getModalControllerReturnValueErrorNoKey(
						request,
						"redirect:/registreUser/registre/" + registreId,
						e.getMessage());
			}
		}
		return "registreReenviarForm";
	}
	
	@RequestMapping(value = "/pendent/{registreId}/{destiLogic}/reenviar", method = RequestMethod.POST)
	public String registreReenviar(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@PathVariable Long destiLogic,
			@Valid ContingutReenviarCommand command,
			BindingResult bindingResult,
			Model model) {
		return registreReenviarPost(
				request, 
				registreId, 
				destiLogic,
				command, 
				bindingResult, 
				model);
	}

	@RequestMapping(value = "/pendent/{registreId}/reenviar", method = RequestMethod.POST)
	public String registreReenviar(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@Valid ContingutReenviarCommand command,
			BindingResult bindingResult,
			Model model) {
		return registreReenviarPost(
				request, 
				registreId, 
				null,
				command, 
				bindingResult, 
				model);
	}
	
	private String registreReenviarPost(
			HttpServletRequest request,
			Long registreId,
			Long destiLogic,
			ContingutReenviarCommand command,
			BindingResult bindingResult,
			Model model) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			if (bindingResult.hasErrors()) {
				omplirModelPerReenviar(
						entitatActual,
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
					command.getDestins(),
					registreId,
					command.isDeixarCopia(),
					command.getComentariEnviar(),
					command.getPerConeixement(),
					destiLogic);
			if (command.getParams().length == 0) {
				
				if (command.isDeixarCopia() == false && bustiaService.isBustiaReadPermitted(command.getDestins()[0])) {
					return getModalControllerReturnValueSuccess(
							request,
							"redirect:/registreUser/registre/" + registreId,
							"bustia.controller.pendent.contingut.reenviat.ok");
				} else {
					return getModalControllerReturnValueSuccess(
							request,
							"redirect:/registreUser",
							"bustia.controller.pendent.contingut.reenviat.ok");
				}

			} else {
				//avançar a la següent pàgina al reenviar
				boolean avanzar = Boolean.parseBoolean(command.getParams()[3]);
				if (avanzar) {
					int numeroPagina = Integer.parseInt(command.getParams()[0]);
					numeroPagina += 1;
					command.getParams()[0] = String.valueOf(numeroPagina);
				}
				MissatgesHelper.success(
						request, 
						getMessage(
								request,
								"bustia.controller.pendent.contingut.reenviat.ok"));
				return "redirect:/registreUser/navega/" + command.getParams()[0] + "?ordreColumn=" + command.getParams()[1] + "&ordreDir=" + command.getParams()[2] + "&isVistaMoviments=" + command.getParams()[4];
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (NotFoundException.class.equals((e.getCause() != null ? e.getCause() : e).getClass())) {
				return getModalControllerReturnValueError(
						request,
						"redirect:/registreUser/registre/" + registreId,
						"registre.user.controller.reenviar.error.registreNoTrobat");
			} else {
				return getModalControllerReturnValueErrorNoKey(
						request,
						"redirect:/registreUser/registre/" + registreId,
						e.getMessage());
			}
		}
	}
	
	@RequestMapping(value = "/registreReenviarMultiple", method = RequestMethod.GET)
	public String registreReenviarMultipleGet(
			HttpServletRequest request,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			Model model) {
		
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			omplirModelPerReenviarMultiple(entitatActual, model);
			ContingutReenviarCommand command = new ContingutReenviarCommand();

			model.addAttribute(command);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (NotFoundException.class.equals((e.getCause() != null ? e.getCause() : e).getClass())) {
				return getModalControllerReturnValueError(
						request,
						"",
						"registre.user.controller.reenviar.error.registreNoTrobat");
			} else {
				return getModalControllerReturnValueErrorNoKey(
						request,
						"",
						e.getMessage());
			}
		}
		return "registreReenviarForm";
		

	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/registreReenviarMultiple", method = RequestMethod.POST)
	public String registreReenviarMultiplePost(
			HttpServletRequest request,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			@Valid ContingutReenviarCommand command,
			BindingResult bindingResult,
			Model model) {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			if (bindingResult.hasErrors()) {
				omplirModelPerReenviarMultiple(entitatActual, model);
				return "registreReenviarForm";
			}
			if (command.getDestins() == null || command.getDestins().length <= 0) {
				MissatgesHelper.error(
						request,
						getMessage(
								request, 	
								"registre.user.controller.massiva.no.desti"));			
				return "registreReenviarForm";
			}
			int errors = 0;
			int correctes = 0;
			Set<Long> seleccio = null;
			List<String> seleccioMoviments = null;
			if (isVistaMoviments) {
//				## ID = ID_REGISTRE + ID_DESTI (extreure registre)
				seleccioMoviments = (List<String>)RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_SELECCIO_MOVIMENTS);
				if (seleccioMoviments != null && !seleccioMoviments.isEmpty()) {
					Set<String> seleccioMovimentsSet = new HashSet<String>(seleccioMoviments);
					for (String idVistaMoviment: seleccioMovimentsSet) {
						String[] idVistaMovimentArr = idVistaMoviment.split("_");
						Long registreId = Long.valueOf(idVistaMovimentArr[0]);
						Long destiLogicId = Long.valueOf(idVistaMovimentArr[1]);
						
						boolean processatOk = realitzarReenviamentIndividual(
								request, 
								command, 
								entitatActual, 
								registreId, 
								destiLogicId);
						
						if (processatOk)
							correctes++;
						else
							errors++;
					}
				}
			} else {
				seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_SELECCIO);
			
				if (seleccio != null && !seleccio.isEmpty()) {
					List<Long> seleccioList = new ArrayList<Long>();
					seleccioList.addAll(seleccio);
					for (Long registreId : seleccioList) {
						
						boolean processatOk = realitzarReenviamentIndividual(
								request, 
								command, 
								entitatActual, 
								registreId, 
								null);
						
						if (processatOk)
							correctes++;
						else
							errors++;
					}
	
				} else {
					
				}
			}
			
			if ((seleccio == null || seleccio.isEmpty()) && (seleccioMoviments == null || seleccioMoviments.isEmpty())) {
				MissatgesHelper.error(request,
						getMessage(request,
								isVistaMoviments ? "registre.user.controller.massiva.enviament.cap" : "registre.user.controller.massiva.cap"));
				
			}
			if (correctes > 0){
				MissatgesHelper.success(request,
						getMessage(request, "registre.user.controller.reenviar.massiva.correctes", new Object[]{correctes}));
			} 
			if (errors > 0) {
				MissatgesHelper.error(request,
						getMessage(request, "registre.user.controller.reenviar.massiva.errors", new Object[]{errors}));
			} 
			
			return modalUrlTancar();
	}
	
	private boolean realitzarReenviamentIndividual(
			HttpServletRequest request,
			ContingutReenviarCommand command, 
			EntitatDto entitatActual, 
			Long registreId, 
			Long destiLogicId) {
		RegistreDto registreDto = registreService.findOne(
				entitatActual.getId(), 
				registreId, 
				false,
				RolHelper.getRolActual(request));
		boolean processatOk = true;
		try {
			bustiaService.registreReenviar(
					entitatActual.getId(),
					command.getDestins(),
					registreDto.getId(),
					command.isDeixarCopia(),
					command.getComentariEnviar(),
					command.getPerConeixement(),
					destiLogicId);
			logger.debug("L'anotació amb id " + registreId + " " + registreDto.getNom() + " s'ha reenviat correctament");

		} catch (Exception e) {
			processatOk = false;
			String errMsg;
			if (NotFoundException.class.equals((e.getCause() != null ? e.getCause() : e).getClass())) {
				errMsg = getMessage(
						request, 
						"registre.user.controller.reenviar.error.registreNoTrobat");
			} else {
				errMsg = getMessage(
						request, 
						"registre.user.controller.reenviar.massiva.error",
						new Object[] {(registreDto != null ? registreDto.getNom() : String.valueOf(registreId)), e.getMessage()});
			}
			MissatgesHelper.error(request, errMsg);
			logger.error("L'anotació amb id " + registreId + " " + registreDto.getNom() + " s'ha reenviat amb error", e);
		}
		return processatOk;
	}

	@RequestMapping(value = "/registre/{registreId}/reintentar", method = RequestMethod.GET)
	public String reintentar(
			HttpServletRequest request,
			@PathVariable Long registreId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		boolean processatOk = registreService.reintentarProcessamentUser(
				entitatActual.getId(),
				registreId);
		if (processatOk) {
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../",
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

		return "redirect:../../modal/registreUser/registre/" + registreId;
	}
	
	@RequestMapping(value = "/{registreId}/marcarPendent", method = RequestMethod.GET)
	public String marcarPendentGet(
			HttpServletRequest request,
			@PathVariable Long registreId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		MarcarProcessatCommand command = new MarcarProcessatCommand();
		model.addAttribute(command);
		return "registreUserMarcarPendent";
	}

	@RequestMapping(value = "/{registreId}/marcarPendent", method = RequestMethod.POST)
	public String marcarPendentPost(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@Valid MarcarProcessatCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		String rolActual = RolHelper.getRolActual(request);
		if (bindingResult.hasErrors()) {
			return "registreUserMarcarPendent";
		}
		try {
			registreService.marcarPendent(
					entitatActual.getId(), 
					registreId,
					"<span class='label label-default'>" + 
					getMessage(
							request, 
							"registre.user.controller.accio.marcat.pendent") + 
					"</span> " + command.getMotiu(), 
					rolActual);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/registreUser/registre/" + registreId,
					"registre.user.controller.accio.marcat.pendent.ok");
		} catch (RuntimeException re) {
			MissatgesHelper.error(
					request,
					getMessage(
							request, 
							"registre.user.controller.marcat.pendent.error",
							new Object[] {re.getMessage()}));			
			return "registreUserMarcarPendent";
		}
	}
	
	@RequestMapping(value = "/marcarPendentMultiple", method = RequestMethod.GET)
	public String marcarPendentMultipleGet(
			HttpServletRequest request,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		MarcarProcessatCommand command = new MarcarProcessatCommand();
		model.addAttribute(command);
		return "registreUserMarcarPendent";
	}

	@RequestMapping(value = "/marcarPendentMultiple", method = RequestMethod.POST)
	public String marcarPendentMultiplePost(
			HttpServletRequest request,
			@Valid MarcarProcessatCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		String rolActual = RolHelper.getRolActual(request);
		if (bindingResult.hasErrors()) {
			return "registreUserMarcarPendent";
		}
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
			
			for (Long registreId : seleccioList) {
					RegistreDto registreDto = registreService.findOne(
							entitatActual.getId(), 
							registreId, 
							false,
							RolHelper.getRolActual(request));
					if (registreDto.getProcesEstat() == RegistreProcesEstatEnum.BUSTIA_PROCESSADA) {
						
						boolean processatOk = true;
						try {
							registreService.marcarPendent(
									entitatActual.getId(), 
									registreId,
									"<span class='label label-default'>" + 
									getMessage(
											request, 
											"registre.user.controller.accio.marcat.pendent") + 
									"</span> " + command.getMotiu(), 
									rolActual);
							
		
						} catch (Exception e) {
							MissatgesHelper.error(
									request,
									getMessage(
											request, 
											"registre.user.controller.marcar.pendent.massiva.error",
											new Object[] {(registreDto != null ? registreDto.getNom() : String.valueOf(registreId)), e.getMessage()}));
							
							processatOk = false;
							logger.error("L'anotació amb id " + registreId + " " + registreDto.getNom() + " s'ha marcat com a processat amb error", e);
						}
						
						if (processatOk)
							correctes++;
						else
							errors++;
					} 
					else {
						logger.debug("L'estat de l'anotació amb id " + registreId + " és " + registreDto.getProcesEstat() + " i no es marcarà com processat.");
						estatErroni++;
					}
			}
			
			if (correctes > 0){
				MissatgesHelper.success(request,
						getMessage(request, "registre.user.controller.marcar.pendent.massiva.correctes", new Object[]{correctes}));
			} 
			if (errors > 0) {
				MissatgesHelper.error(request,
						getMessage(request, "registre.user.controller.marcar.pendent.massiva.errors", new Object[]{errors}));
			} 
			if (estatErroni > 0) {
				MissatgesHelper.warning(request,
						getMessage(request,
								"registre.user.controller.marcar.pendent.massiva.estatErroni", new Object[]{estatErroni}));
			}

			
		} else {
			MissatgesHelper.error(request,
					getMessage(request,
							"registre.user.controller.massiva.cap"));
		}
		
		return modalUrlTancar();
	}	
	

	@RequestMapping(value = "/pendent/{contingutId}/marcarProcessat", method = RequestMethod.GET)
	public String bustiaMarcarProcessatGet(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		MarcarProcessatCommand command = new MarcarProcessatCommand();
		model.addAttribute(command);
		return "registreUserMarcarProcessat";
	}

	@RequestMapping(value = "/pendent/{registreId}/marcarProcessat", method = RequestMethod.POST)
	public String bustiaMarcarProcessatPost(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@Valid MarcarProcessatCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		String rolActual = RolHelper.getRolActual(request);
		if (bindingResult.hasErrors()) {
			return "registreUserMarcarProcessat";
		}
		try {
			contingutService.marcarProcessat(
					entitatActual.getId(), 
					registreId,
					"<span class='label label-default'>" + 
					getMessage(
							request, 
							"bustia.pendent.accio.marcat.processat") + 
					"</span> " + command.getMotiu(), 
					rolActual);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:/registreUser/registre/" + registreId,
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
	
	
	@RequestMapping(value = "/marcarProcessatMultiple", method = RequestMethod.GET)
	public String marcarProcessatMultipleGet(
			HttpServletRequest request,
			Model model) {
		getEntitatActualComprovantPermisos(request);
		MarcarProcessatCommand command = new MarcarProcessatCommand();
		model.addAttribute(command);
		return "registreUserMarcarProcessat";
	}

	@RequestMapping(value = "/marcarProcessatMultiple", method = RequestMethod.POST)
	public String marcarProcessatMultiplePost(
			HttpServletRequest request,
			@Valid MarcarProcessatCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		String rolActual = RolHelper.getRolActual(request);
		if (bindingResult.hasErrors()) {
			return "registreUserMarcarProcessat";
		}
		
		
		@SuppressWarnings("unchecked")
		Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_SELECCIO);
		if (seleccio != null && !seleccio.isEmpty()) {
			marcarProcessatMultiple(seleccio, request, entitatActual, command.getMotiu(), rolActual);
		} else {
			MissatgesHelper.error(request,
					getMessage(request,
							"registre.user.controller.massiva.cap"));
		}
		
		return modalUrlTancar();
	}
	
	private void marcarProcessatMultiple(Set<Long> seleccio, HttpServletRequest request, 
			EntitatDto entitatActual, String motiu, String rolActual) {
		marcarProcessatMultiple(seleccio, request, entitatActual, motiu, rolActual, null);
	}
	private void marcarProcessatMultiple(Set<Long> seleccio, HttpServletRequest request, 
			EntitatDto entitatActual, String motiu, String rolActual, List<Long> registreIdErronis) {

		List<Long> seleccioList = new ArrayList<Long>();
		seleccioList.addAll(seleccio);
		
		int errors = 0;
		int correctes = 0;
		int estatErroni = 0;
		

		for (Long registreId : seleccioList) {
			if (registreIdErronis == null || (registreIdErronis != null && !registreIdErronis.contains(registreId))) {
				RegistreDto registreDto = registreService.findOne(
						entitatActual.getId(), 
						registreId, 
						false,
						RolHelper.getRolActual(request));
				if (registreDto.getProcesEstat() == RegistreProcesEstatEnum.BUSTIA_PENDENT
						|| (registreDto.getProcesEstat() == RegistreProcesEstatEnum.ARXIU_PENDENT && registreDto.isReintentsEsgotat())) {
					
					boolean processatOk = true;
					
					try {
						contingutService.marcarProcessat(
								entitatActual.getId(), 
								registreId,
								"<span class='label label-default'>" + 
								getMessage(
										request, 
										"bustia.pendent.accio.marcat.processat") + 
								"</span> " + motiu, 
								rolActual);
						
	
					} catch (Exception e) {
						MissatgesHelper.error(
								request,
								getMessage(
										request, 
										"registre.user.controller.marcar.processat.massiva.error",
										new Object[] {(registreDto != null ? registreDto.getNom() : String.valueOf(registreId)), e.getMessage()}));
						
						processatOk = false;
						logger.error("L'anotació amb id " + registreId + " " + registreDto.getNom() + " s'ha marcat com a processat amb error", e);
					}
					
					if (processatOk)
						correctes++;
					else
						errors++;
				} 
				else {
					logger.debug("L'estat de l'anotació amb id " + registreId + " és " + registreDto.getProcesEstat() + " i no es marcarà com processat.");
					estatErroni++;
				}
			}
		}
		
		if (correctes > 0){
			MissatgesHelper.success(request,
					getMessage(request, "registre.user.controller.marcar.processat.massiva.correctes", new Object[]{correctes}));
		} 
		if (errors > 0) {
			MissatgesHelper.error(request,
					getMessage(request, "registre.user.controller.marcar.processat.massiva.errors", new Object[]{errors}));
		} 
		if (estatErroni > 0) {
			MissatgesHelper.warning(request,
					getMessage(request,
							"registre.user.controller.marcar.processat.massiva.estatErroni", new Object[]{estatErroni}));
		}
	
	}

	@RequestMapping(value = "/pendent/{contingutId}/alertes", method = RequestMethod.GET)
	public String bustiaListatAlertes(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		model.addAttribute("contingutId", contingutId);
		return "registreErrors";
	}

	@RequestMapping(value = "/pendent/{contingutId}/alertes/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse bustiaListatAlertesDatatable(
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

	@RequestMapping(value = "/pendent/{contingutId}/alertes/{alertaId}/llegir", method = RequestMethod.GET)
	@ResponseBody
	public void bustiaListatAlertesLlegir(
			HttpServletRequest request,
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
	
	/** Retorna el llistat de totes les bústies per filtrar el destí dels moviments. Pot incloure o no les innactives */
	@RequestMapping(value = "/busties", method = RequestMethod.GET)
	@ResponseBody
	public List<BustiaDto> busties(
			HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "false") boolean mostrarInactives,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return bustiaService.findBustiesPerUsuari(entitatActual.getId(), mostrarInactives);
	}

	@RequestMapping(value = "/classificar/{registreId}", method = RequestMethod.GET)
	public String bustiaClassificarGet(
			HttpServletRequest request,
			@PathVariable Long registreId,
			Model model) {
		try {
			String procedimentCodi = emplenarModelClassificar(
					request,
					registreId,
					model);
			RegistreClassificarCommand command = new RegistreClassificarCommand();
			command.setContingutId(registreId);
			command.setCodiProcediment(procedimentCodi);
			model.addAttribute(command);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return getModalControllerReturnValueErrorNoKey(
					request,
					"redirect:/registreUser/registre/" + registreId,
					e.getMessage());
		}
		return "registreClassificar";
	}

	@RequestMapping(value = "/classificar/{registreId}", method = RequestMethod.POST)
	public String bustiaClassificarPost(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@Validated(Classificar.class) RegistreClassificarCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (bindingResult.hasErrors()) {
			emplenarModelClassificar(
					request,
					registreId,
					model);
			return "registreClassificar";
		}
		ClassificacioResultatDto resultat = registreService.classificar(
				entitatActual.getId(),
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
				"redirect:/registreUser/registre/" + registreId,
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

	@RequestMapping(value = "/classificarMultiple/{registreId}/{codiProcediment}", method = RequestMethod.GET)
	@ResponseBody
	public ClassificacioResultatDto classificarMultiplePost(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@PathVariable String codiProcediment,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		ClassificacioResultatDto resultat = registreService.classificar(
				entitatActual.getId(),
				registreId,
				codiProcediment);
		return resultat;
	}
	
	
	//Gestió bústies favorits
	@RequestMapping(value = "/favorits/add/{bustiaId}", method = RequestMethod.GET)
	@ResponseBody
	public void addBustiaToFavorits(
			HttpServletRequest request,
			@PathVariable Long bustiaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		bustiaService.addToFavorits(entitatActual.getId(), bustiaId);
	}
	
	@RequestMapping(value = "/favorits/remove/{id}", method = RequestMethod.GET)
	@ResponseBody
	public void removeBustiaFromFavorits(
			HttpServletRequest request,
			@PathVariable Long id) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		bustiaService.removeFromFavorits(entitatActual.getId(), id);
	}
	
	@RequestMapping(value = "/favorits/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse registreUserFavoritsDatatable(
			HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				bustiaService.getBustiesFavoritsUsuariActual(
						entitatActual.getId(), 
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id",
				SESSION_ATTRIBUTE_SELECCIO);
	}
	
	@RequestMapping(value = "/favorits/list", method = RequestMethod.GET)
	@ResponseBody
	public List<Long> registreUserFavoritsList(
			HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		List<Long> idsBustiesFavorits = bustiaService.getIdsBustiesFavoritsUsuariActual(entitatActual.getId());
		return idsBustiesFavorits;
	}
	
	@RequestMapping(value = "/favorits/check/{bustiaId}", method = RequestMethod.GET)
	@ResponseBody
	public boolean checkIfExists(
			HttpServletRequest request,
			@PathVariable Long bustiaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return bustiaService.checkIfFavoritExists(entitatActual.getId(), bustiaId);
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}
	
	@RequestMapping(value = "/{registreId}/bloquejar", method = RequestMethod.GET)
	@ResponseBody
	public boolean agafar(
			HttpServletRequest request,
			@PathVariable Long registreId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		try {
			registreService.bloquejar(
					entitatActual.getId(),
					registreId);
			return true;
		} catch (Exception e) {
			logger.error("Error agafant l'anotació", e);
			boolean permisExcepcion = ExceptionHelper.isExceptionOrCauseInstanceOf(e, PermissionDeniedException.class);
			if (permisExcepcion) {
				logger.error(getMessage(
						request, 
						"bustia.pendent.controller.agafat.ko"), e);
				return false;
			} else {
				throw e;
			}
		}
	}
	
	@RequestMapping(value = "/{registreId}/alliberar", method = RequestMethod.GET)
	@ResponseBody
	public boolean alliberar(
			HttpServletRequest request,
			@PathVariable Long registreId,
			Model model) {
		model.addAttribute("mantenirPaginacio", true);
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		try {
			registreService.alliberar(
					entitatActual.getId(),
					registreId);
			return true;
		} catch (Exception e) {
			logger.error("Error alliberant l'anotació", e);
			boolean permisExcepcion = ExceptionHelper.isExceptionOrCauseInstanceOf(e, PermissionDeniedException.class);
			boolean emailExcepcion = ExceptionHelper.isExceptionOrCauseInstanceOf(e, EmptyMailException.class);
			if (permisExcepcion) {
				logger.error(getMessage(
								request, 
								"bustia.pendent.controller.alliberat.ko"), e);
				return false;
			} else if (emailExcepcion) {
				logger.error(getMessage(
						request, 
						"bustia.pendent.controller.alliberat.email.ko"), e);
				return false;
			} else {
				throw e;
			}
		}
	}

	@RequestMapping(value = "/{bustiaId}/usersPermitted", method = RequestMethod.GET)
	@ResponseBody
	public List<UsuariPermisDto> getUsersPermitted(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			Model model) {
		return bustiaService.getUsersPermittedForBustia(bustiaId);
	}
	
	private void resetFiltreBustia(
			HttpServletRequest request, 
			RegistreFiltreCommand filtreCommand) {
//		###
//		# resol conflicte filtre bustia pantalla moviments (es mostren totes les bústies)
		String paginaAnterior = request.getHeader("Referer"); 
		if (paginaAnterior != null && paginaAnterior.contains("moviments"))
			filtreCommand.setBustia("");
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE,
				filtreCommand);
	}
	
	private RegistreDto omplirModelPerReenviar(
			EntitatDto entitatActual,
			Long registreId,
			Model model) {


		List<BustiaDto> busties = bustiaService.findActivesAmbEntitat(
				entitatActual.getId());
		
		boolean disableDeixarCopia = false;
		RegistreDto registreDto = registreService.findOne(
				entitatActual.getId(),
				registreId,
				false);
		
		boolean duplicarContingutInArxiu = new Boolean(aplicacioService.propertyFindByNom("es.caib.distribucio.plugins.distribucio.fitxers.duplicar.contingut.arxiu"));
		if (registreDto.getProcesEstat() == RegistreProcesEstatEnum.ARXIU_PENDENT && !duplicarContingutInArxiu) {
			disableDeixarCopia = true;
		}
		model.addAttribute(
				"disableDeixarCopia",
				disableDeixarCopia);

		model.addAttribute("maxLevel", getMaxLevelArbre());
		
		model.addAttribute(
				"selectMultiple",
				true);
		
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
		model.addAttribute("isEnviarConeixementActiu", isEnviarConeixementActiu());
		model.addAttribute("isFavoritsPermes", isFavoritsPermes());
		model.addAttribute("isMostrarPermisosBustiaPermes", isMostrarPermisosBustiaPermes());
		return registreDto;
	}
	
	private boolean isPermesReservarAnotacions() {
		return new Boolean(aplicacioService.propertyFindByNom("es.caib.distribucio.anotacions.permetre.reservar"));
	}
	
	private void omplirModelPerReenviarMultiple(
			EntitatDto entitatActual,
			Model model) {

		List<BustiaDto> busties = bustiaService.findActivesAmbEntitat(
				entitatActual.getId());
		
		boolean disableDeixarCopia = true;

		
		model.addAttribute(
				"selectMultiple",
				false);
		
		model.addAttribute(
				"disableDeixarCopia",
				disableDeixarCopia);
		
		model.addAttribute("maxLevel", getMaxLevelArbre());
		
		model.addAttribute(
				"busties",
				busties);
		model.addAttribute("isEnviarConeixementActiu", isEnviarConeixementActiu());
		model.addAttribute("isFavoritsPermes", isFavoritsPermes());
		model.addAttribute("isMostrarPermisosBustiaPermes", isMostrarPermisosBustiaPermes());
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
	
	private boolean isEnviarConeixementActiu() {
		String isEnviarConeixementStr = aplicacioService.propertyFindByNom("es.caib.distribucio.contingut.enviar.coneixement");
		return Boolean.parseBoolean(isEnviarConeixementStr);
	}
	
	private boolean isFavoritsPermes() {
		String isFavoritsPermesStr = aplicacioService.propertyFindByNom("es.caib.distribucio.contingut.reenviar.favorits");
		return Boolean.parseBoolean(isFavoritsPermesStr);
	}

	private boolean isMostrarPermisosBustiaPermes() {
		String isMostrarPermisosBustiaPermesStr = aplicacioService.propertyFindByNom("es.caib.distribucio.contingut.reenviar.mostrar.permisos");
		return Boolean.parseBoolean(isMostrarPermisosBustiaPermesStr);
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
			Long registreId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		RegistreDto registre = registreService.findOne(
				entitatActual.getId(),
				registreId,
				false);
		model.addAttribute("registre", registre);
		model.addAttribute(
				"procediments",
				registreService.classificarFindProcediments(
						entitatActual.getId(),
						registre.getPareId()));
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
					bustiaIdActual = registre.getPareId();
				}
				if (!bustiaIdActual.equals(registre.getPareId())) {
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
