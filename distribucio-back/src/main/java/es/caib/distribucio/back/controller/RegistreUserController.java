/**
 * 
 */
package es.caib.distribucio.back.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import es.caib.distribucio.back.command.ContingutReenviarCommand;
import es.caib.distribucio.back.command.MarcarProcessatCommand;
import es.caib.distribucio.back.command.RegistreClassificarCommand;
import es.caib.distribucio.back.command.RegistreClassificarCommand.Classificar;
import es.caib.distribucio.back.command.RegistreClassificarTipusEnum;
import es.caib.distribucio.back.command.RegistreEnviarIProcessarCommand;
import es.caib.distribucio.back.command.RegistreEnviarViaEmailCommand;
import es.caib.distribucio.back.command.RegistreFiltreCommand;
import es.caib.distribucio.back.helper.AjaxHelper;
import es.caib.distribucio.back.helper.AjaxHelper.AjaxFormResponse;
import es.caib.distribucio.back.helper.BeanGeneratorHelper;
import es.caib.distribucio.back.helper.DatatablesHelper;
import es.caib.distribucio.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.back.helper.ElementsPendentsBustiaHelper;
import es.caib.distribucio.back.helper.EnumHelper;
import es.caib.distribucio.back.helper.ExceptionHelper;
import es.caib.distribucio.back.helper.MissatgesHelper;
import es.caib.distribucio.back.helper.RequestSessionHelper;
import es.caib.distribucio.back.helper.RolHelper;
import es.caib.distribucio.logic.intf.dto.AlertaDto;
import es.caib.distribucio.logic.intf.dto.BustiaDto;
import es.caib.distribucio.logic.intf.dto.ClassificacioResultatDto;
import es.caib.distribucio.logic.intf.dto.ContingutDto;
import es.caib.distribucio.logic.intf.dto.DominiDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.HistogramPendentsEntryDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto.OrdreDireccioDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentDto;
import es.caib.distribucio.logic.intf.dto.RegistreAnnexDto;
import es.caib.distribucio.logic.intf.dto.RegistreDto;
import es.caib.distribucio.logic.intf.dto.RegistreProcesEstatSimpleEnumDto;
import es.caib.distribucio.logic.intf.dto.RegistreTipusDocFisicaEnumDto;
import es.caib.distribucio.logic.intf.dto.ResultatConsultaDto;
import es.caib.distribucio.logic.intf.dto.ResultatDominiDto;
import es.caib.distribucio.logic.intf.dto.ServeiDto;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.dto.UsuariPermisDto;
import es.caib.distribucio.logic.intf.exception.DominiException;
import es.caib.distribucio.logic.intf.exception.EmptyMailException;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.exception.PermissionDeniedException;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.logic.intf.service.AlertaService;
import es.caib.distribucio.logic.intf.service.AplicacioService;
import es.caib.distribucio.logic.intf.service.BustiaService;
import es.caib.distribucio.logic.intf.service.ConfigService;
import es.caib.distribucio.logic.intf.service.ContingutService;
import es.caib.distribucio.logic.intf.service.DominiService;
import es.caib.distribucio.logic.intf.service.MetaDadaService;
import es.caib.distribucio.logic.intf.service.ProcedimentService;
import es.caib.distribucio.logic.intf.service.RegistreService;
import es.caib.distribucio.logic.intf.service.ServeiService;

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

	private static final String COOKIE_SENSE_ASSIGNAR = "mostrar_sense_assignar";
	
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
	@Autowired
	private MetaDadaService metaDadaService;
	@Autowired
	private BeanGeneratorHelper beanGeneratorHelper;
	@Autowired
	private DominiService dominiService;
	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private ServeiService serveiService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String registreUserGet(
			@CookieValue(value = COOKIE_SENSE_ASSIGNAR, defaultValue = "false") boolean mostrarSenseAssignar,
			HttpServletRequest request,
			Model model) {
		RegistreFiltreCommand filtreCommand = getFiltreCommand(request);
		UsuariDto usuari = aplicacioService.getUsuariActual();
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		resetFiltreBustia(request, filtreCommand);
//		###
		BustiaDto bustiaPerDefecte = aplicacioService.getBustiaPerDefecte(usuari, entitatActual.getId());
		model.addAttribute(filtreCommand);
		model.addAttribute(
				"tipusDocumentacio",
				EnumHelper.getOptionsForEnum(
						RegistreTipusDocFisicaEnumDto.class,
						"registre.tipus.doc.fisica.enum."));
		model.addAttribute("isPermesReservarAnotacions", isPermesReservarAnotacions());
		model.addAttribute("isEnviarConeixementActiu", isEnviarConeixementActiu());
		if (bustiaPerDefecte != null)
			model.addAttribute("bustiaPerDefecte", bustiaPerDefecte.getId());
		model.addAttribute("isPermesAssignarAnotacions", isPermesAssignarAnotacions());
		model.addAttribute("nomCookieSenseAssignar", COOKIE_SENSE_ASSIGNAR);
		model.addAttribute("mostrarSenseAssignar", mostrarSenseAssignar);
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
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
			EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
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
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
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
			Model model) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		
		try {
			RegistreDto registre = registreService.findOneAmbDades(
							entitatActual.getId(),
							registreId,
							isVistaMoviments,
							RolHelper.getRolActual(request));
						
			//recupera la ruta del destí lògic del moviment o no l'actual
			if (isVistaMoviments && destiLogic != null) {
				List<ContingutDto> path = registreService.getPathContingut(entitatActual.getId(), destiLogic);
				registre.setPath(path);
			}
			// Nom del procediment

			String codiSia = registre.getProcedimentCodi();
			if (codiSia != null) {
				ProcedimentDto procediment = procedimentService.findByCodiSia(entitatActual.getId(), codiSia);
				if (procediment == null) {
					procediment = new ProcedimentDto();
					procediment.setCodi(codiSia);
					procediment.setCodiSia(codiSia);
					procediment.setNom(getMessage(request, "registre.detalls.camp.procediment.no.trobat", new Object[] {codiSia}));
				}
				model.addAttribute("procedimentDades", procediment);				
			}

			// Nom del servei

			String codiServei = registre.getServeiCodi();
			if (codiServei != null) {
				ServeiDto servei = serveiService.findByCodiSia(entitatActual.getId(), codiServei);
				if (servei == null) {
					servei = new ServeiDto();
					servei.setCodi(codiServei);
					servei.setCodiSia(codiServei);
					servei.setNom(getMessage(request, "registre.detalls.camp.servei.no.trobat", new Object[] {codiServei}));
				}
				model.addAttribute("serveiDades", servei);				
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
			model.addAttribute("numeroAnnexosPendentsArxiu", this.numeroAnnexosPendentsArxiu(registre));
			model.addAttribute("numeroAnnexosFirmaInvalida", this.numeroAnnexosFirmaInvalida(registre));
			model.addAttribute("numeroAnnexosEstatEsborrany", this.numeroAnnexosEstatEsborrany(registre));
			model.addAttribute("metaDades", metaDadaService.findByEntitat(entitatActual.getId()));
			model.addAttribute("dadesCommand",
					beanGeneratorHelper.generarCommandDadesRegistre(
							entitatActual.getId(),
							((RegistreDto)registre).getDades()));
			model.addAttribute("metadadesActives", isMetadadesActives());
			model.addAttribute("isPermesAssignarAnotacions", isPermesAssignarAnotacions());
			model.addAttribute("copies", emplenarModelCopies(request, entitatActual, registre));
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
			EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
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
			EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
			model.addAttribute(
					"annex",
					registreService.getAnnexSenseFirmes(
							entitatActual.getId(),
							registreId,
							annexId,
							isVistaMoviments));
			model.addAttribute("registreId", registreId);
			model.addAttribute("concsvBaseUrl", configService.getConcsvBaseUrl());
			model.addAttribute("gestioDocumentalFirmes", registreService.getDadesAnnexFirmesSenseDetall(annexId)); // CANVIAR NOM
		} catch(Exception ex) {
			String msgError = "Error recuperant informació de l'annex";
			logger.error(msgError, ex);
			model.addAttribute("missatgeError", msgError + ". " +  ex.getMessage());
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
			EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
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
			String msgError = "Error recuperant informació de firma";
			logger.error(msgError, ex);
			model.addAttribute("missatgeError", msgError + ". " +  ex.getMessage());
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
			EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
			annexFirmes = registreService.getAnnexAmbFirmes(
							entitatActual.getId(),
							registreId,
							annexId,
							isVistaMoviments);
		} catch(Exception ex) {
			String msgError = "Error recuperant informació de firma";
			logger.error(msgError, ex);
			model.addAttribute("missatgeError", msgError + ". " +  ex.getMessage());
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
			EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
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
		model.addAttribute("expInactivitat", aplicacioService.propertyFindByNom("es.caib.distribucio.tasca.guardar.annexos.innectivitat.cron"));

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
		getEntitatActualComprovantPermisUsuari(request);
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
				
		// Valida les adreces
		String adreces = this.revisarAdreces(request, command.getAddresses(), bindingResult);

		try {
			// Valida l'estat del registre
			RegistreDto registreDto = registreService.findOne(
					entitatActual.getId(), 
					command.getContingutId(), 
					false,
					RolHelper.getRolActual(request));
			this.revisarEstatPerEnviarViaEmail(request, entitatActual, registreDto, bindingResult);

			if (bindingResult.hasErrors()) {
				return "registreViaEmail";
			}
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
			Throwable thr = ExceptionHelper.getRootCauseOrItself(exception);
			String errMsg = getMessage(
					request, 	
					"bustia.controller.pendent.contingut.enviat.email.ko",
					new Object[] {thr.getMessage()});
			MissatgesHelper.error(
					request,
					errMsg);
			logger.error(errMsg, exception);
			return "registreViaEmail";
		}
		
	}
	
	
	
	@RequestMapping(value = "/enviarViaEmailMultiple", method = RequestMethod.GET)
	public String enviarViaEmailMultipleGet(
			HttpServletRequest request,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			Model model) {
		RegistreEnviarViaEmailCommand command = new RegistreEnviarViaEmailCommand();
		model.addAttribute("registreEnviarViaEmailCommand", command);
		model.addAttribute("isVistaMoviments", isVistaMoviments);
		

		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		List<RegistreDto> registres = obtenirRegistresSeleccioMoviments(request, entitatActual, isVistaMoviments);
		
		model.addAttribute("registres", registres);
		return "registreViaEmail";
	}
	
	@ResponseBody
	@RequestMapping(value = "/enviarViaEmailAjax/{registreId}", method = RequestMethod.POST)
	public AjaxFormResponse enviarViaEmailAjaxPost(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			@Valid RegistreEnviarViaEmailCommand command,
			BindingResult bindingResult) {
		AjaxFormResponse response;
		String adreces = this.revisarAdreces(request, command.getAddresses(), bindingResult);
		if (bindingResult.hasErrors()) {
			response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
			response.setMissatge(getMessage(request, "processamentMultiple.error.validacio"));
			return response;
		}

		try {
			this.entrarSemafor(registreId);
			EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
			bustiaService.registreAnotacioEnviarPerEmail(
					entitatActual.getId(),
					registreId,
					adreces, 
					command.getMotiu(),
					isVistaMoviments,
					RolHelper.getRolActual(request));
			response = AjaxHelper.generarAjaxFormOk();
			response.setMissatge(getMessage(request, "bustia.controller.pendent.contingut.enviat.email.ok"));
		} catch (Exception exception) {
			Throwable thr = ExceptionHelper.getRootCauseOrItself(exception);
			String errMsg = getMessage(
					request, 	
					"bustia.controller.pendent.contingut.enviat.email.ko",
					new Object[] {thr.getMessage()});
			logger.error(errMsg, exception);
			response = AjaxHelper.generarAjaxError(errMsg);
		} finally {
			this.sortirSemafor(registreId);
		}
		return response;
	}
	
	
	/** Mètode per enviar i processar una anotació de registre. Petició Ajax des del les accions 
	 * múltiples.
	 * @return Retorna un objecte amb el resultat de l'operació.
	 */
	@ResponseBody
	@RequestMapping(value = "/enviarIProcessarAjax/{registreId}", method = RequestMethod.POST)
	public AjaxFormResponse enviarIProcessarAjaxPost(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			@Valid RegistreEnviarIProcessarCommand command,
			BindingResult bindingResult) {
		AjaxFormResponse response;
		
		// Valida les adreces
		String adreces = this.revisarAdreces(request, command.getAddresses(), bindingResult);
		// Valida l'estat del registre per enviar i marcar com a processat
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		RegistreDto registreDto = registreService.findOne(
				entitatActual.getId(), 
				registreId, 
				false,
				RolHelper.getRolActual(request));
		this.revisarEstatPerEnviarViaEmail(request, entitatActual, registreDto, bindingResult);
		this.revisarEstatPerMarcarProcessat(request, entitatActual, registreDto, bindingResult);
		
		if (bindingResult.hasErrors()) {
			response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
			response.setMissatge(getMessage(request, "processamentMultiple.error.validacio"));
			return response;
		}

		boolean correcte = true;
		StringBuilder missatge = new StringBuilder();
		// Envia per correu
		try {
			this.entrarSemafor(registreId);
			bustiaService.registreAnotacioEnviarPerEmail(
					entitatActual.getId(),
					registreDto.getId(),
					adreces, 
					null,
					isVistaMoviments,
					RolHelper.getRolActual(request));
			missatge.append(getMessage(request, "bustia.controller.pendent.contingut.enviat.email.ok"));	
		} catch (Exception exception) {
			Throwable thr = ExceptionHelper.getRootCauseOrItself(exception);
			correcte = false;
			String errMsg = getMessage(
					request, 	
					"bustia.controller.pendent.contingut.enviat.email.ko",
					new Object[] {thr.getMessage()});
			logger.error(errMsg, exception);
			missatge.append(errMsg);
		} finally {
			this.sortirSemafor(registreId);
		}
		missatge.append(". ");
		
		// Marca com a processada
		if (correcte) {
			try {
				String rolActual = RolHelper.getRolActual(request);
				contingutService.marcarProcessat(
						entitatActual.getId(), 
						registreId,
						"<span class='label label-default'>" + 
						getMessage(
								request, 
								"bustia.pendent.accio.marcat.processat") + 
						"</span> " + command.getMotiu(), 
						rolActual);
				missatge.append(getMessage(request, "bustia.controller.pendent.contingut.marcat.processat.ok"));	
			} catch (Exception exception) {
				correcte = false;
				Throwable thr = ExceptionHelper.getRootCauseOrItself(exception);
				String errMsg = getMessage(
						request, 	
						"bustia.pendent.accio.marcar.processat.error",
						new Object[] {thr.getMessage()});
				logger.error(errMsg, exception);
				missatge.append(errMsg);
			}
		} else {
			missatge.append(getMessage(request, "registre.user.controller.marcar.processat.cancelat"));
		}
		
		if (correcte) {
			response = AjaxHelper.generarAjaxFormOk();
			response.setMissatge(getMessage(request, missatge.toString()));
		} else {
			response = AjaxHelper.generarAjaxError(missatge.toString());
		}
		return response;
	}
	
	/** Valida que l'anotació no estigui pendent d'arxiu o si ho està que hagi esgotat els reintents. */
	private void revisarEstatPerEnviarViaEmail(HttpServletRequest request, EntitatDto entitatActual,
			RegistreDto registreDto, BindingResult bindingResult) {

		if (registreDto.getProcesEstat() == RegistreProcesEstatEnum.ARXIU_PENDENT && !registreDto.isReintentsEsgotat()) {
			bindingResult.reject(
					"bustia.controller.pendent.contingut.enviar.email.validacio.estat", 
					getMessage(request, 
							"bustia.controller.pendent.contingut.enviar.email.validacio.estat", 
							new Object[] {registreDto.getProcesEstat()}));
		}	
	}

	/** Valida que l'anotació estigui pendent de bústia o que estigui pendent d'Arxiu i hagi esgotat els reintents. */
	private void revisarEstatPerMarcarProcessat(HttpServletRequest request, EntitatDto entitatActual,
			RegistreDto registreDto, BindingResult bindingResult) {

		if (registreDto.getProcesEstat() != RegistreProcesEstatEnum.BUSTIA_PENDENT
				&& !(registreDto.getProcesEstat() == RegistreProcesEstatEnum.ARXIU_PENDENT && registreDto.isReintentsEsgotat())) {
			bindingResult.reject(
					"registre.user.controller.marcar.processat.validacio.estat", 
					getMessage(request, 
							"registre.user.controller.marcar.processat.validacio.estat", 
							new Object[] {registreDto.getProcesEstat()}));
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
			EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
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
			model.addAttribute("isReenviarBustiaDefaultEntitatDisabled", isReenviarBustiaDefaultEntitatDisabled());
			model.addAttribute("isPermesAssignarAnotacions", isPermesAssignarAnotacions());
		
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
			EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
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
				model.addAttribute("isReenviarBustiaDefaultEntitatDisabled", isReenviarBustiaDefaultEntitatDisabled());
				model.addAttribute("isPermesAssignarAnotacions", isPermesAssignarAnotacions());
				return "registreReenviarForm";
			}
			bustiaService.registreReenviar(
					entitatActual.getId(),
					command.getDestins(),
					registreId,
					command.isDeixarCopia(),
					command.getComentariEnviar(),
					command.getPerConeixement(),
					command.getDestinsUsuari(),
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
			EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
			omplirModelPerReenviarMultiple(request, entitatActual, model, isVistaMoviments);
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
	
	private void omplirModelPerReenviarMultiple(
			HttpServletRequest request, 
			EntitatDto entitatActual,
			Model model, 
			boolean isVistaMoviments) {

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
		model.addAttribute("isReenviarBustiaDefaultEntitatDisabled", isReenviarBustiaDefaultEntitatDisabled());
		model.addAttribute("isPermesAssignarAnotacions", isPermesAssignarAnotacions());
		model.addAttribute(
				"arbreUnitatsOrganitzatives",
				bustiaService.findArbreUnitatsOrganitzatives(
						entitatActual.getId(),
						true,
						false,
						true));
		
		List<RegistreDto> registres = obtenirRegistresSeleccioMoviments(request, entitatActual, isVistaMoviments);
		
		model.addAttribute("registres", registres);
		
	}
	
	private List<RegistreDto> obtenirRegistresSeleccioMoviments(
			HttpServletRequest request, 
			EntitatDto entitatActual,
			boolean isVistaMoviments) {
		List<RegistreDto> registres = new ArrayList<>();
		if (isVistaMoviments) {
			Set<Long> seleccio = new HashSet<Long>();
//			## ID = ID_REGISTRE + ID_DESTI (extreure registre)
			@SuppressWarnings("unchecked")
			Set<String> seleccioMoviments = (Set<String>) RequestSessionHelper.obtenirObjecteSessio(
					request,
					SESSION_ATTRIBUTE_SELECCIO_MOVIMENTS);
			if (seleccioMoviments != null && !seleccioMoviments.isEmpty()) {
				for (String idVistaMoviment: seleccioMoviments) {
					seleccio.add(Long.valueOf(idVistaMoviment.split("_")[0]));
				}
			}
			registres = registreService.findMultiple(
					entitatActual.getId(),
					new ArrayList<Long>(seleccio),
					false);

		} else {
			registres = registreService.findMultiple(
							getEntitatActual(request).getId(),
							this.getRegistresSeleccionats(request, SESSION_ATTRIBUTE_SELECCIO),
							false);
		}
		return registres;
	}

	@ResponseBody
	@RequestMapping(value = "/registreReenviarAjax/{registreId}", method = RequestMethod.POST)
	public AjaxFormResponse registreReenviarAjaxPost(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@Valid ContingutReenviarCommand command,
			BindingResult bindingResult) {
		AjaxFormResponse response;
		if (bindingResult.hasErrors()) {
			response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
			response.setMissatge(getMessage(request, "processamentMultiple.error.validacio"));
			return response;
		}
		if (command.getDestins() == null || command.getDestins().length <= 0) {
			response = AjaxHelper.generarAjaxError(getMessage(
					request, 	
					"registre.user.controller.massiva.no.desti"));
			return response;
		}

		try {
			this.entrarSemafor(registreId);
			EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
			bustiaService.registreReenviar(
					entitatActual.getId(),
					command.getDestins(),
					registreId,
					command.isDeixarCopia(),
					command.getComentariEnviar(),
					command.getPerConeixement(),
					command.getDestinsUsuari(),
					null);
			response = AjaxHelper.generarAjaxFormOk();
			response.setMissatge(getMessage(request, "bustia.controller.pendent.contingut.reenviat.ok"));
		} catch (Exception exception) {
			response = AjaxHelper.generarAjaxError(
					getMessage(
						request, 
						"registre.user.controller.reenviar.massiva.error",
						new Object[] {String.valueOf(registreId), exception.getMessage()}));
		} finally {
			this.sortirSemafor(registreId);
		}
		return response;
	}


	@RequestMapping(value = "/registre/{registreId}/reintentar", method = RequestMethod.GET)
	public String reintentar(
			HttpServletRequest request,
			@PathVariable Long registreId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		RegistreDto registreReenviat = registreService.findOne(entitatActual.getId(), registreId, false);
		boolean processatOk = registreService.reintentarProcessamentUser(
				entitatActual.getId(),
				registreId);
		if (processatOk) {
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../",
					"contingut.admin.controller.registre.reintentat.ok", 
					new Object[] {registreReenviat.getBackCodi()});
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
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

		return "redirect:/modal/registreAdmin/" + registreId + "/detall";
	}
	
	@RequestMapping(value = "/{registreId}/marcarPendent", method = RequestMethod.GET)
	public String marcarPendentGet(
			HttpServletRequest request,
			@PathVariable Long registreId,
			Model model) {
		getEntitatActualComprovantPermisUsuari(request);
		MarcarProcessatCommand command = new MarcarProcessatCommand();
		model.addAttribute("marcarPendentCommand", command);
		return "registreUserMarcarPendent";
	}

	@RequestMapping(value = "/{registreId}/marcarPendent", method = RequestMethod.POST)
	public String marcarPendentPost(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@Valid MarcarProcessatCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
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
	
	@ResponseBody
	@RequestMapping(value = "/marcarPendentAjax/{registreId}", method = RequestMethod.POST)
	public AjaxFormResponse marcarPendentAjaxPost(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@Valid MarcarProcessatCommand command,
			BindingResult bindingResult) {
		AjaxFormResponse response;
		if (bindingResult.hasErrors()) {
			response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
			response.setMissatge(getMessage(request, "processamentMultiple.error.validacio"));
			return response;
		}
		
		try {
			this.entrarSemafor(registreId);
			EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
			String rolActual = RolHelper.getRolActual(request);
			registreService.marcarPendent(
					entitatActual.getId(), 
					registreId,
					"<span class='label label-default'>" + 
					getMessage(
							request, 
							"registre.user.controller.accio.marcat.pendent") + 
					"</span> " + command.getMotiu(), 
					rolActual);

			response = AjaxHelper.generarAjaxFormOk();
			response.setMissatge(getMessage(request, "registre.user.controller.accio.marcat.pendent.ok"));
		} catch (Exception exception) {
			response = AjaxHelper.generarAjaxError(
					getMessage(
						request, 
						"registre.user.controller.marcat.pendent.error",
						new Object[] {exception.getMessage()}));
		} finally {
			this.sortirSemafor(registreId);
		}
		return response;
	}

	@RequestMapping(value = "/pendent/{contingutId}/marcarProcessat", method = RequestMethod.GET)
	public String bustiaMarcarProcessatGet(
			HttpServletRequest request,
			@PathVariable Long contingutId,
			Model model) {
		getEntitatActualComprovantPermisUsuari(request);
		MarcarProcessatCommand command = new MarcarProcessatCommand();
		model.addAttribute("marcarProcessatCommand", command);
		return "registreUserMarcarProcessat";
	}

	@RequestMapping(value = "/pendent/{registreId}/marcarProcessat", method = RequestMethod.POST)
	public String bustiaMarcarProcessatPost(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@Valid MarcarProcessatCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
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

	@ResponseBody
	@RequestMapping(value = "/marcarProcessatAjax/{registreId}", method = RequestMethod.POST)
	public AjaxFormResponse marcarProcessatAjaxPost(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@Valid MarcarProcessatCommand command,
			BindingResult bindingResult) {
		AjaxFormResponse response;
		if (bindingResult.hasErrors()) {
			response = AjaxHelper.generarAjaxFormErrors(command, bindingResult);
			response.setMissatge(getMessage(request, "processamentMultiple.error.validacio"));
			return response;
		}

		try {
			this.entrarSemafor(registreId);
			EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
			String rolActual = RolHelper.getRolActual(request);
			contingutService.marcarProcessat(
					entitatActual.getId(), 
					registreId,
					"<span class='label label-default'>" + 
					getMessage(
							request, 
							"bustia.pendent.accio.marcat.processat") + 
					"</span> " + command.getMotiu(), 
					rolActual);
			response = AjaxHelper.generarAjaxFormOk();
			response.setMissatge(getMessage(request, "bustia.controller.pendent.contingut.marcat.processat.ok"));
		} catch (Exception exception) {
			response = AjaxHelper.generarAjaxError(
					getMessage(
						request, 
						"bustia.pendent.accio.marcar.processat.error",
						new Object[] {exception.getMessage()}));
		} finally {
			this.sortirSemafor(registreId);
		}
		return response;
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		return bustiaService.findBustiesPermesesPerUsuari(entitatActual.getId(), mostrarInactives);
	}
	
	/** Retorna el llistat de totes les bústies per filtrar el destí dels moviments. Pot incloure o no les innactives */
	@RequestMapping(value = "/busties", method = RequestMethod.GET)
	@ResponseBody
	public List<BustiaDto> busties(
			HttpServletRequest request,
			@RequestParam(required = false, defaultValue = "false") boolean mostrarInactives,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		return bustiaService.findBustiesPerUsuari(entitatActual.getId(), mostrarInactives);
	}

	@RequestMapping(value = "/classificar/{registreId}", method = RequestMethod.GET)
	public String bustiaClassificarGet(
			HttpServletRequest request,
			@PathVariable Long registreId,
			Model model) {
		try {			
			EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
			RegistreClassificarCommand command = new RegistreClassificarCommand();
			RegistreDto registre = this.emplenarModelClassificar(
					entitatActual, 
					registreId,
					model);
			if (registre.getServeiCodi() != null) {
				command.setTipus(RegistreClassificarTipusEnum.SERVEI.name());
			} else {				
				command.setTipus(RegistreClassificarTipusEnum.PROCEDIMENT.name());
			}
			command.setContingutId(registreId);
			command.setCodiProcediment(registre.getProcedimentCodi());
			command.setCodiServei(registre.getServeiCodi());
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

	/** Mètode comú per emplenar el model per classificar un registre o quan falla la validació.
	 * 
	 * @param entitatActual
	 * @param registreId
	 * @param model
	 */
	private RegistreDto emplenarModelClassificar(EntitatDto entitatActual, Long registreId, Model model) {
		
		RegistreDto registre = registreService.findOne(
				entitatActual.getId(),
				registreId,
				false);
		model.addAttribute("registre", registre);
		model.addAttribute("isPermesModificarTitol", isPermesModificarTitol());
		model.addAttribute(
				"tipus",
				EnumHelper.getOptionsForEnum(
						RegistreClassificarTipusEnum.class,
						"registre.classificar.tipus.enum."));
		this.emplenarModelProcedimentClassificar(
				entitatActual,
				registre,
				model);
		this.emplenarModelServeiClassificar(
				entitatActual,
				registre,
				model);
		
		return registre;
	}

	@RequestMapping(value = "/classificar/{registreId}", method = RequestMethod.POST)
	public String bustiaClassificarPost(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@Validated(Classificar.class) RegistreClassificarCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		if (bindingResult.hasErrors()) {
			this.emplenarModelClassificar(
					entitatActual, 
					registreId, 
					model);
			return "registreClassificar";
		}
		String codiProcediment = null;
		if (command.getTipus() != null && RegistreClassificarTipusEnum.PROCEDIMENT.name().equals(command.getTipus()) ) {
				codiProcediment = command.getCodiProcediment();
		}
		String codiServei = null;
		if (command.getTipus() != null && RegistreClassificarTipusEnum.SERVEI.name().equals(command.getTipus()) ) {
				codiServei = command.getCodiServei();
		}
		ClassificacioResultatDto resultat = registreService.classificar(
				entitatActual.getId(),
				registreId,
				codiProcediment,
				codiServei,
				command.getTitol());
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
		case TITOL_MODIFICAT:
			MissatgesHelper.info(
					request, 
					getMessage(
							request, 
							"bustia.controller.pendent.contingut.classificat.titol",
							null));
			break;
		}
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:/registreUser/registre/" + registreId,
				"bustia.controller.pendent.contingut.classificat.ok");
	}	
	
	//Gestió bústies favorits
	@RequestMapping(value = "/favorits/add/{bustiaId}", method = RequestMethod.GET)
	@ResponseBody
	public void addBustiaToFavorits(
			HttpServletRequest request,
			@PathVariable Long bustiaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		bustiaService.addToFavorits(entitatActual.getId(), bustiaId);
	}
	
	@RequestMapping(value = "/favorits/remove/{id}", method = RequestMethod.GET)
	@ResponseBody
	public void removeBustiaFromFavorits(
			HttpServletRequest request,
			@PathVariable Long id) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		bustiaService.removeFromFavorits(entitatActual.getId(), id);
	}
	
	@RequestMapping(value = "/favorits/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse registreUserFavoritsDatatable(
			HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		
		List<Long> idsBustiesFavorits = bustiaService.getIdsBustiesFavoritsUsuariActual(entitatActual.getId());
		return idsBustiesFavorits;
	}
	
	@RequestMapping(value = "/favorits/check/{bustiaId}", method = RequestMethod.GET)
	@ResponseBody
	public boolean checkIfExists(
			HttpServletRequest request,
			@PathVariable Long bustiaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
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
		return bustiaService.getUsuarisPerBustia(bustiaId);
	}

	@RequestMapping(value = "/domini/{dominiCodi}", method = RequestMethod.GET)
	@ResponseBody
	public ResultatDominiDto getDomini(
			HttpServletRequest request,
			@PathVariable String dominiCodi,
			@RequestParam(value="filter", required = false) String filter,
			@RequestParam(value="pageSize", required = false) int pageSize,
			@RequestParam(value="page", required = false) int page){
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		ResultatDominiDto resultatDomini = null;
		DominiDto domini = dominiService.findByCodiAndEntitat(dominiCodi,entitatActual.getId());
		try {
			resultatDomini = dominiService.getResultDomini(
						entitatActual.getId(),
						domini,
						filter != null ? filter : "",
						page,
						pageSize);
		} catch (DominiException e) {
			e.printStackTrace();
		}
		
		return resultatDomini;
	}
	
	@RequestMapping(value = "/domini/{dominiCodi}/valor", method = RequestMethod.GET)
	@ResponseBody
	public ResultatConsultaDto getDomini(
			HttpServletRequest request,
			@PathVariable String dominiCodi,
			@RequestParam(value="dadaValor", required = false) String dadaValor){
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		ResultatConsultaDto resultatConsulta = null;
		DominiDto domini = dominiService.findByCodiAndEntitat(dominiCodi, entitatActual.getId());
		resultatConsulta = dominiService.getSelectedDomini(
					entitatActual.getId(),
					domini,
					dadaValor);
		return resultatConsulta;
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
		
		boolean duplicarContingutInArxiu = Boolean.valueOf(aplicacioService.propertyFindByNom("es.caib.distribucio.plugins.distribucio.fitxers.duplicar.contingut.arxiu"));
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
		model.addAttribute("isReenviarBustiaDefaultEntitatDisabled", isReenviarBustiaDefaultEntitatDisabled());
		model.addAttribute("isPermesAssignarAnotacions", isPermesAssignarAnotacions());
		return registreDto;
	}
	
	private boolean isPermesReservarAnotacions() {
		return Boolean.valueOf(aplicacioService.propertyFindByNom("es.caib.distribucio.anotacions.permetre.reservar"));
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
	
	private Object isReenviarBustiaDefaultEntitatDisabled() {
		String isReenviarBustiaDefaultEntitatDisabled = aplicacioService.propertyFindByNom("es.caib.distribucio.no.permetre.reenviar.bustia.default.entitat");
		return Boolean.parseBoolean(isReenviarBustiaDefaultEntitatDisabled);
	}

	private boolean isFavoritsPermes() {
		String isFavoritsPermesStr = aplicacioService.propertyFindByNom("es.caib.distribucio.contingut.reenviar.favorits");
		return Boolean.parseBoolean(isFavoritsPermesStr);
	}

	private boolean isMostrarPermisosBustiaPermes() {
		String isMostrarPermisosBustiaPermesStr = aplicacioService.propertyFindByNom("es.caib.distribucio.contingut.reenviar.mostrar.permisos");
		return Boolean.parseBoolean(isMostrarPermisosBustiaPermesStr);
	}

	private boolean isPermesModificarTitol() {
		String isModificarTitolActivat = aplicacioService.propertyFindByNom("es.caib.distribucio.contingut.modificar.titol");
		return Boolean.parseBoolean(isModificarTitolActivat);
	}
	
	private boolean isMetadadesActives() {
		return Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.distribucio.permetre.metadades.registre"));
	}
	
	private boolean isPermesAssignarAnotacions() {
		return Boolean.parseBoolean(aplicacioService.propertyFindByNom("es.caib.distribucio.assignar.anotacions"));
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
		
		if (isPermesAssignarAnotacions()) {
			Cookie cookie = WebUtils.getCookie(request, COOKIE_SENSE_ASSIGNAR);
			filtreCommand.setMostrarSenseAssignar(cookie != null && "true".equals(cookie.getValue()));
			
			if (filtreCommand.isMostrarSenseAssignar()) {
				filtreCommand.setUsuariAssignatCodi(null);
			}
		}
		
		return filtreCommand;
	}

	private String emplenarModelProcedimentClassificar(
			EntitatDto entitatActual,
			RegistreDto registre,		
			Model model) {		
		
		model.addAttribute(
				"procediments",
				registreService.classificarFindProcediments(
						entitatActual.getId(),
						registre.getPareId()));		
		
		return registre.getProcedimentCodi();
	}
	
	private String emplenarModelServeiClassificar(
			EntitatDto entitatActual,
			RegistreDto registre,		
			Model model) {		
		
		model.addAttribute(
				"serveis",
				registreService.classificarFindServeis(
						entitatActual.getId(),
						registre.getPareId()));		
		
		return registre.getServeiCodi();
	}
	
	private List<RegistreDto> emplenarModelCopies(
			HttpServletRequest request,
			EntitatDto entitatActual,
			RegistreDto registre) {			
		List<RegistreDto> copies = registreService.findByEntitatCodiAndNumero(entitatActual.getId(), registre.getNumero());
		for (RegistreDto copia : copies) {
			copia.setEstatDescripcio(getMessage(request, "registre.proces.estat.enum."+copia.getProcesEstat()));
		}		
		return copies;		
	}
	
	

	private static final Logger logger = LoggerFactory.getLogger(RegistreUserController.class);
}
