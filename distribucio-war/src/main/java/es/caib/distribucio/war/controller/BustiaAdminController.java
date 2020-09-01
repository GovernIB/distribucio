/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.BustiaFiltreOrganigramaDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.service.BustiaService;
import es.caib.distribucio.core.api.service.UnitatOrganitzativaService;
import es.caib.distribucio.war.command.BustiaCommand;
import es.caib.distribucio.war.command.BustiaCommand.CreateUpdate;
import es.caib.distribucio.war.command.BustiaFiltreCommand;
import es.caib.distribucio.war.command.MoureAnotacionsCommand;
import es.caib.distribucio.war.helper.BustiaHelper;
import es.caib.distribucio.war.helper.DatatablesHelper;
import es.caib.distribucio.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.war.helper.MissatgesHelper;
import es.caib.distribucio.war.helper.RequestSessionHelper;

/**
 * Controlador per al manteniment de bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/bustiaAdmin")
public class BustiaAdminController extends BaseAdminController {
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "BustiaAdminController.session.filtre";

	@Autowired
	private BustiaService bustiaService;
	@Autowired
	private UnitatOrganitzativaService unitatService;
	@Autowired
	private BustiaHelper bustiaHelper;


	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		BustiaFiltreCommand bustiaFiltreCommand = getFiltreCommand(request);
		model.addAttribute("bustiaFiltreCommand", bustiaFiltreCommand);
		return "bustiaAdminList";
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		BustiaFiltreCommand bustiaFiltreCommand = getFiltreCommand(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				bustiaService.findAmbFiltreAdmin(
						entitatActual.getId(),
						BustiaFiltreCommand.asDto(bustiaFiltreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String bustiaPost(
			HttpServletRequest request,
			@Valid BustiaFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model) {
		if (!bindingResult.hasErrors()) {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return "redirect:bustiaAdmin";
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newGet(
			HttpServletRequest request,
			Model model) {
		String vista = formGet(request, null, model);
		return vista;
	}
	
	
	// save new or modified bústia
	@RequestMapping(value = "/newOrModify", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Validated(CreateUpdate.class) BustiaCommand command,
			BindingResult bindingResult,
			Model model) {
		String isOrganigrama = request.getParameter("isOrganigrama");
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			if (bindingResult.hasErrors()) {
				return "bustiaAdminForm";
			}
			
			// if it is modified
			if (command.getId() != null) {
				bustiaService.update(
						entitatActual.getId(),
						BustiaCommand.asDto(command));
				
				return getModalControllerReturnValueSuccess(
						request,
						"true".equals(isOrganigrama) ? "redirect:bustiaAdminOrganigrama" : "redirect:bustiaAdmin",
						"bustia.controller.modificat.ok");
			//if it is new	
			} else {
				bustiaService.create(
						entitatActual.getId(),
						BustiaCommand.asDto(command));
				return getModalControllerReturnValueSuccess(
						request,
						"true".equals(isOrganigrama) ? "redirect:bustiaAdminOrganigrama" : "redirect:bustiaAdmin",
						"bustia.controller.creat.ok");
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return getModalControllerReturnValueErrorNoKey(
					request,
					"true".equals(isOrganigrama) ? "redirect:bustiaAdminOrganigrama" : "redirect:bustiaAdmin",
					e.getMessage());
		}
	}
	
	

	@RequestMapping(value = "/{bustiaId}/transicioInfo", method = RequestMethod.GET)
	public String bustiaTransicioInfo(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		BustiaDto bustia = null;
		if (bustiaId != null){
			
			bustia = bustiaService.findById(
					entitatActual.getId(),
					bustiaId);
			
		// setting last historicos to the unitat of this bustia
		bustia.setUnitatOrganitzativa(unitatService.getLastHistoricos(bustia.getUnitatOrganitzativa()));
			
		
		// getting all the busties connected with old unitat excluding the one you are currently in 
		List<BustiaDto> bustiesOfOldUnitat = bustiaService.findAmbUnitatCodiAdmin(entitatActual.getId(), bustia.getUnitatOrganitzativa().getCodi());
		List<BustiaDto> bustiesOfOldUnitatWithoutCurrent = new ArrayList<BustiaDto>();
		for(BustiaDto bustiaI: bustiesOfOldUnitat){
			if(!bustiaI.getId().equals(bustia.getId())){
				bustiesOfOldUnitatWithoutCurrent.add(bustiaI);
			}
		}
		model.addAttribute("bustiesOfOldUnitatWithoutCurrent", bustiesOfOldUnitatWithoutCurrent);
		model.addAttribute(bustia);	
		}
		
		return "bustiaAdminTransicioInfo";
	}
	
	
	@RequestMapping(value = "/excelUsuarisPerBustia", method = RequestMethod.GET)
	public void excelUsuarisPermissionsPerBustia(
			HttpServletRequest request,
			HttpServletResponse response) throws IllegalAccessException, NoSuchMethodException  {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		List<BustiaDto> busties = bustiaService.findAmbEntitatAndFiltre(
				entitatActual.getId(),
				new BustiaFiltreOrganigramaDto());

		bustiaHelper.generarExcelUsuarisPermissionsPerBustia(
				response,
				busties);
	}
	



	@RequestMapping(value = "/{bustiaId}", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		BustiaDto bustia = null;
		if (bustiaId != null){
			
			bustia = bustiaService.findById(
					entitatActual.getId(),
					bustiaId);
			
			// setting last historicos to the unitat of this bustia
			bustia.setUnitatOrganitzativa(unitatService.getLastHistoricos(bustia.getUnitatOrganitzativa()));
			
		
			// getting all the busties connected with old unitat excluding the one you are currently in 
			List<BustiaDto> bustiesOfOldUnitat = bustiaService.findAmbUnitatCodiAdmin(entitatActual.getId(), bustia.getUnitatOrganitzativa().getCodi());
			List<BustiaDto> bustiesOfOldUnitatWithoutCurrent = new ArrayList<BustiaDto>();
			for(BustiaDto bustiaI: bustiesOfOldUnitat){
				if(!bustiaI.getId().equals(bustia.getId())){
					bustiesOfOldUnitatWithoutCurrent.add(bustiaI);
				}
			}
			model.addAttribute("bustiesOfOldUnitatWithoutCurrent", bustiesOfOldUnitatWithoutCurrent);
			model.addAttribute(bustia);	
		
			model.addAttribute("usuaris", bustiaService.getUsersPermittedForBustia(bustiaId));
		}
		
		BustiaCommand command = null;
		if (bustia != null)
			command = BustiaCommand.asCommand(bustia);
		else
			command = new BustiaCommand();
		model.addAttribute(command);
		command.setEntitatId(entitatActual.getId());
		return "bustiaAdminForm";
	}

	/** Opció de l'usuari administrador per moure totes les anotacions de registre d'una bústia
	 * cap a una altra.
	 * @param bustiaId
	 * 			Bústia orígen.
	 * @return
	 */
	@RequestMapping(value = "/{bustiaId}/moureAnotacions", method = RequestMethod.GET)
	public String moureAnotacionsGet(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		omplirModelPerMoureAnotacions(
				entitatActual,
				bustiaId,
				model);
		MoureAnotacionsCommand command = new MoureAnotacionsCommand();
		command.setOrigenId(bustiaId);
		model.addAttribute(command);
		return "bustiaAdminMoureAnotacions";
	}
	
	/** Mou les anotacions de registre d'una bústia orígen cap a la bústia seleccionada per l'usuari.
	 * Pot deixar un comentari en el moviment. Es valida que la bústia origen i destí no siguin la mateixa.
	 * @param request
	 * @param bustiaId
	 * 			Bústia origen.
	 * @param command
	 * 			Bústia destí i comentaris.
	 * @return
	 */
	@RequestMapping(value = "/{bustiaId}/moureAnotacions", method = RequestMethod.POST)
	public String moureAnotacionsPost(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@Valid MoureAnotacionsCommand command,
			BindingResult bindingResult,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		// Valida que el destí no sigui igual que l'origen.
		if (bustiaId.equals(command.getDestiId())) {
			bindingResult.rejectValue(
					"destiId", 
					"bustia.moure.anotacions.desti.origen.error");
		}
		if (bindingResult.hasErrors()) {
			omplirModelPerMoureAnotacions(
					entitatActual,
					bustiaId,
					model);
			return "bustiaAdminMoureAnotacions";
		}
		int anotacionsMogudes = 0;
		try {
			anotacionsMogudes = bustiaService.moureAnotacions(
					entitatActual.getId(),
					bustiaId,
					command.getDestiId(),
					command.getComentari()); 
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			MissatgesHelper.error(
					request,
					getMessage(
							request, 
							"bustia.moure.anotacions.error", 
							new Object[] {e.getLocalizedMessage()}));
			omplirModelPerMoureAnotacions(
					entitatActual,
					bustiaId,
					model);
			return "bustiaAdminMoureAnotacions";	
		}
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:/bustiaAdmin",
				"bustia.moure.anotacions.ok",
				new Object[] {anotacionsMogudes});
	}
	
	private void omplirModelPerMoureAnotacions(
			EntitatDto entitatActual,
			Long bustiaId,
			Model model) {
		BustiaDto bustia = bustiaService.findById(entitatActual.getId(), bustiaId);
		if (bustia == null)
			throw new NotFoundException(
					bustiaId,
					BustiaDto.class);
		model.addAttribute(
				"bustia",
				bustia);
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
	
	@RequestMapping(value = "/{bustiaId}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long bustiaId) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			bustiaService.updateActiva(
					entitatActual.getId(),
					bustiaId,
					true);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../bustiaAdmin",
					"bustia.controller.activat.ok");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return getModalControllerReturnValueErrorNoKey(
					request,
					"redirect:../../bustiaAdmin",
					e.getMessage());
		}
	}
	@RequestMapping(value = "/{bustiaId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long bustiaId) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			bustiaService.updateActiva(
					entitatActual.getId(),
					bustiaId,
					false);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../bustiaAdmin",
					"bustia.controller.desactivat.ok");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return getModalControllerReturnValueErrorNoKey(
					request,
					"redirect:../../bustiaAdmin",
					e.getMessage());
		}
	}

	@RequestMapping(value = "/{bustiaId}/default", method = RequestMethod.GET)
	public String defecte(
			HttpServletRequest request,
			@PathVariable Long bustiaId) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			bustiaService.marcarPerDefecte(
					entitatActual.getId(),
					bustiaId);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../bustiaAdmin",
					"bustia.controller.defecte.ok");
		} catch (NotFoundException e) {
			logger.error(e.getMessage(), e);
			return getModalControllerReturnValueErrorNoKey(
					request,
					"redirect:../../bustiaAdmin",
					e.getMessage());
		}
	}

	@RequestMapping(value = "/{bustiaId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long bustiaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		try {
			bustiaService.delete(
					entitatActual.getId(),
					bustiaId);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../bustiaAdmin",
					"bustia.controller.esborrat.ok");
		} catch (RuntimeException ve) {
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../bustiaAdmin",
					"bustia.controller.esborrat.error.validacio",
					new Object[] {ve.getMessage()});			
		}
	}

	private BustiaFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		BustiaFiltreCommand bustiaFiltreCommand = (BustiaFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (bustiaFiltreCommand == null) {
			bustiaFiltreCommand = new BustiaFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					bustiaFiltreCommand);
		}
		return bustiaFiltreCommand;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(BustiaAdminController.class);
	
}
