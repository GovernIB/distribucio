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
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.RegistreDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.service.BustiaService;
import es.caib.distribucio.core.api.service.ContingutService;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.core.api.service.UnitatOrganitzativaService;
import es.caib.distribucio.war.command.AnotacioRegistreFiltreCommand;
import es.caib.distribucio.war.helper.DatatablesHelper;
import es.caib.distribucio.war.helper.MissatgesHelper;
import es.caib.distribucio.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.war.helper.RequestSessionHelper;

/**
 * Controlador per a la consulta d'arxius pels administradors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/registreAdmin")
public class RegistreAdminController extends BaseAdminController {

	private static final String SESSION_ATTRIBUTE_ANOTACIO_FILTRE = "ContingutAdminController.session.anotacio.filtre";
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
		AnotacioRegistreFiltreCommand filtreCommand = getAnotacioRegistreFiltreCommand(request);
		model.addAttribute(
				filtreCommand);
		model.addAttribute(
				"nomesAmbErrors",
				filtreCommand.isNomesAmbErrors());
		return "registreAdminList";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String registreAdminPost(
			HttpServletRequest request,
			@Valid AnotacioRegistreFiltreCommand filtreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio,
			Model model) {
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_ANOTACIO_FILTRE);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_ANOTACIO_FILTRE,
						filtreCommand);
			}
		}
		return "redirect:registreAdmin";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse registreAdminDatatable(
			HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		AnotacioRegistreFiltreCommand filtreCommand = getAnotacioRegistreFiltreCommand(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				registreService.findRegistreAdmin(
						entitatActual.getId(),
						AnotacioRegistreFiltreCommand.asDto(filtreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
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
			AnotacioRegistreFiltreCommand filtreCommand = getAnotacioRegistreFiltreCommand(request);
			List<BustiaDto> bustiesUsuari = null;
			if (filtreCommand.getBustia() == null || filtreCommand.getBustia().isEmpty()) {
				bustiesUsuari = bustiaService.findBustiesPermesesPerUsuari(entitatActual.getId(), true);
			}
			seleccio.addAll(
					registreService.findRegistreAdminIdsAmbFiltre(
							entitatActual.getId(),
							AnotacioRegistreFiltreCommand.asDto(filtreCommand)));
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
			
			int countOk = 0;
			int countNotOk = 0;
			
			for (Long registreId : seleccioList) {
				ContingutDto contingutDto = contingutService.findAmbIdAdmin(entitatActual.getId(), registreId, false);

				RegistreDto registreDto = (RegistreDto) contingutDto;

				if (registreDto.getProcesEstat() == RegistreProcesEstatEnum.ARXIU_PENDENT || registreDto.getProcesEstat() == RegistreProcesEstatEnum.REGLA_PENDENT) {
					boolean processatOk = registreService.reintentarProcessamentAdmin(entitatActual.getId(),
							registreDto.getPareId(), registreId);
					if (processatOk) {
						countOk++;
					} else {
						countNotOk++;
					}

				} else if (registreDto.getProcesEstat() == RegistreProcesEstatEnum.BACK_PENDENT) {
					boolean processatOk = registreService.reintentarEnviamentBackofficeAdmin(entitatActual.getId(),
							registreDto.getPareId(), registreId);
					if (processatOk) {
						countOk++;
					} else {
						countNotOk++;
					}
				}
			}
			
			if (countNotOk == 0) {
				MissatgesHelper.success(request,
						getMessage(request,
								"contingut.admin.controller.registre.reintentat.massiva",
								new Object[]{countOk, countNotOk}));
			} else if (countOk == 0){
				MissatgesHelper.error(request,
						getMessage(request,
								"contingut.admin.controller.registre.reintentat.massiva",
								new Object[]{countOk, countNotOk}));
			} else {
				MissatgesHelper.warning(request,
						getMessage(request,
								"contingut.admin.controller.registre.reintentat.massiva",
								new Object[]{countOk, countNotOk}));
			}
			
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
			BustiaFiltreDto filtre = new BustiaFiltreDto();
			filtre.setNom(text);
			bustiesFinals = bustiaService.findAmbFiltreAdmin(entitatActual.getId(), filtre, paginacioParams).getContingut();
		}
		return bustiesFinals;
	}

	private AnotacioRegistreFiltreCommand getAnotacioRegistreFiltreCommand(
			HttpServletRequest request) {
		AnotacioRegistreFiltreCommand filtreCommand = (AnotacioRegistreFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ANOTACIO_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new AnotacioRegistreFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_ANOTACIO_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

}
