package es.caib.distribucio.back.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import es.caib.distribucio.back.command.BustiaFiltreCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.back.command.BustiaCommand;
import es.caib.distribucio.back.command.BustiaCommand.CreateUpdate;
import es.caib.distribucio.back.command.BustiaFiltreOrganigramaCommand;
import es.caib.distribucio.back.helper.BustiaHelper;
import es.caib.distribucio.back.helper.RequestSessionHelper;
import es.caib.distribucio.logic.intf.dto.ArbreDto;
import es.caib.distribucio.logic.intf.dto.BustiaDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.PermisDto;
import es.caib.distribucio.logic.intf.dto.PrincipalTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.logic.intf.dto.UsuariPermisDto;
import es.caib.distribucio.logic.intf.service.BustiaService;
import es.caib.distribucio.logic.intf.service.UnitatOrganitzativaService;

/**
 * Controlador per al manteniment de bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/bustiaAdminOrganigrama")
public class BustiaAdminOrganigramaController extends BaseAdminController {
	
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
			Model model,
			Long bustiaId) {
		
		omplirModel(request, model);
		
		return "bustiaAdminOrganigrama";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid BustiaFiltreCommand filtreCommand,
			BindingResult bindingResult,
			Model model,
			@RequestParam(value = "accio", required = false) String accio) {
		getEntitatActualComprovantPermisAdmin(request);
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
		return "redirect:bustiaAdminOrganigrama";
	}
	
	
//	@RequestMapping(value = "/excelUsuarisPerBustiaAntic", method = RequestMethod.GET)
//	public void excelUsuarisPermissionsPerBustia(
//			HttpServletRequest request,
//			HttpServletResponse response) throws IllegalAccessException, NoSuchMethodException  {
//		
//		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
//		BustiaFiltreOrganigramaCommand bustiaFiltreOrganigramaCommand = getFiltreOrganigramaCommand(request);
//		
//		List<BustiaDto> busties = bustiaService.findAmbEntitatAndFiltre(
//				entitatActual.getId(),
//				BustiaFiltreOrganigramaCommand.asDto(bustiaFiltreOrganigramaCommand));
//
//		bustiaHelper.generarExcelUsuarisPermissionsPerBustiaAntic(
//				response,
//				busties);
//	}
	
    @RequestMapping(value = "/excelUsuarisPerBustia", method = RequestMethod.GET)
    @ResponseBody
    public String startExcelGeneration(HttpServletRequest request) {
        EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
        BustiaFiltreCommand bustiaFiltreOrganigramaCommand = getFiltreOrganigramaCommand(request);

        List<BustiaDto> busties = bustiaService.findAmbEntitatAndFiltre(
                entitatActual.getId(),
                BustiaFiltreCommand.asDto(bustiaFiltreOrganigramaCommand));

        // Arranca la generación en segundo plano
        String taskId = bustiaHelper.generateExcelAsync(busties);
        return taskId; // el cliente recibe este ID
    }
    
    @RequestMapping(value = "/excelStatus/{taskId}", method = RequestMethod.GET)
    @ResponseBody
    public boolean checkStatus(@PathVariable String taskId) {
        return bustiaHelper.isReady(taskId);
    }

    @RequestMapping(value = "/excelDownload/{taskId}", method = RequestMethod.GET)
    public void downloadExcel(@PathVariable String taskId, HttpServletResponse response) throws IOException {
        File file = bustiaHelper.getFile(taskId);
        if (file == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setHeader("Content-Disposition", "attachment; filename=UsuarisPerBustia.xls");
        response.setContentType("application/vnd.ms-excel");

        try (InputStream is = new FileInputStream(file)) {
            is.transferTo(response.getOutputStream());
        }
    }
	
	
	@RequestMapping(value = "/findAllAmbEntitat", method = RequestMethod.GET)
	@ResponseBody
	public List<BustiaDto> findAllAmbEntitat(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		return bustiaService.findAmbEntitat(
				entitatActual.getId());
	}
	
	private BustiaFiltreCommand getFiltreOrganigramaCommand(
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
	
	
	@RequestMapping(value = "/{bustiaId}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long bustiaId) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
			bustiaService.updateActiva(
					entitatActual.getId(),
					bustiaId,
					true);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../bustiaAdminOrganigrama",
					"bustia.controller.activat.ok");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return getModalControllerReturnValueErrorNoKey(
					request,
					"redirect:../../bustiaAdminOrganigrama",
					e.getMessage());
		}
	}
	
	
	@RequestMapping(value = "/{bustiaId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long bustiaId) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
			bustiaService.updateActiva(
					entitatActual.getId(),
					bustiaId,
					false);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../bustiaAdminOrganigrama",
					"bustia.controller.desactivat.ok");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return getModalControllerReturnValueErrorNoKey(
					request,
					"redirect:../../bustiaAdminOrganigrama",
					e.getMessage());
		}
	}
	
	
	@RequestMapping(value = "/modify", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Validated(CreateUpdate.class) BustiaCommand command,
			BindingResult bindingResult,
			Model model) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
			if (bindingResult.hasErrors()) {
				omplirModel(request, model);
				List<String> errorMsgs = new ArrayList<>();
				for(ObjectError objectError: bindingResult.getAllErrors()){
					String field = "";
					if (objectError instanceof FieldError) {
						FieldError fieldError = (FieldError) objectError;
						 field = fieldError.getField() + ": ";
					}
					errorMsgs.add(field + objectError.getDefaultMessage());
				}
				return getAjaxControllerReturnValueError(
						request,
						"redirect:/bustiaAdminOrganigrama",
						"bustia.controller.modificat.error.validacio",
						new Object[] {errorMsgs});	
			}
			bustiaService.update(
					entitatActual.getId(),
					BustiaCommand.asDto(command));
			
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:/bustiaAdminOrganigrama",
					"bustia.controller.modificat.ok");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return getAjaxControllerReturnValueError(
					request,
					"redirect:/bustiaAdminOrganigrama",
					"bustia.controller.modificat.error.validacio",
					new Object[] {e.getMessage()});			
		}
	}
	
	
	@RequestMapping(value = "/{bustiaId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long bustiaId) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		try {
			bustiaService.delete(
					entitatActual.getId(),
					bustiaId);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../bustiaAdminOrganigrama",
					"bustia.controller.esborrat.ok");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../bustiaAdminOrganigrama",
					"bustia.controller.esborrat.error.validacio",
					new Object[] {e.getMessage()});				
		}
	}
	
	@RequestMapping(value = "/{bustiaId}/default", method = RequestMethod.GET)
	public String defecte(
			HttpServletRequest request,
			@PathVariable Long bustiaId) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
			bustiaService.marcarPerDefecte(
					entitatActual.getId(),
					bustiaId);
			return getAjaxControllerReturnValueSuccess(
					request,
					"redirect:../../bustiaAdminOrganigrama",
					"bustia.controller.defecte.ok");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return getModalControllerReturnValueErrorNoKey(
					request,
					"redirect:../../bustiaAdminOrganigrama",
					e.getMessage());
		}
	}
	
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newGet(
			HttpServletRequest request,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		
		BustiaCommand command = new BustiaCommand();
		command.setEntitatId(entitatActual.getId());
		model.addAttribute(command);
		model.addAttribute("isOrganigrama", true);

		return "bustiaAdminForm";
	}
	
	
	
	@RequestMapping(value = "/{bustiaId}", method = RequestMethod.GET)
	@ResponseBody
	public BustiaDto bustiaGet(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		BustiaDto bustia = null;

		bustia = bustiaService.findById(
					entitatActual.getId(),
					bustiaId);

		// Completa la informació dels permisos amb el nom complet per usuaris
		if (bustia.getPermisos() != null) {
			for (PermisDto permis : bustia.getPermisos()) {
				Map<String, UsuariPermisDto> usuarisBustia = bustiaService.getUsuarisPerBustia(bustiaId, true, false);		
				if (PrincipalTipusEnumDto.USUARI.equals(permis.getPrincipalTipus())) {
					UsuariPermisDto usuari = usuarisBustia.get(permis.getPrincipalNom());
					if (usuari != null) {
						permis.setPrincipalDescripcio(usuari.getNom());	
					}
				}
			}
		}

		// setting last historicos to the unitat of this bustia
		bustia.setUnitatOrganitzativa(unitatService.getLastHistoricos(bustia.getUnitatOrganitzativa()));

		return bustia;
	}
	
	@RequestMapping(value = "/{bustiaId}/otherBustiesOfUnitatObsoleta", method = RequestMethod.GET)
	@ResponseBody
	public List<BustiaDto> bustiesOfOldUnitat(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		BustiaDto bustia = null;

		bustia = bustiaService.findById(
				entitatActual.getId(),
				bustiaId);

		// getting unitat with the last historicos calculated and assigned
		bustia.setUnitatOrganitzativa(unitatService.getLastHistoricos(bustia.getUnitatOrganitzativa()));
				
		// getting all the busties connected with old unitat excluding the one you are currently in 
		List<BustiaDto> bustiesOfOldUnitat = bustiaService.findAmbUnitatCodiAdmin(entitatActual.getId(), bustia.getUnitatOrganitzativa().getCodi());
		List<BustiaDto> bustiesOfOldUnitatWithoutCurrent = new ArrayList<BustiaDto>();
		for(BustiaDto bustiaI: bustiesOfOldUnitat){
			if(!bustiaI.getId().equals(bustia.getId())){
				bustiesOfOldUnitatWithoutCurrent.add(bustiaI);
			}
		}
		return bustiesOfOldUnitatWithoutCurrent;
	}
	
	
	private void omplirModel(
			HttpServletRequest request,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);

        BustiaFiltreCommand bustiaFiltreOrganigramaCommand = getFiltreOrganigramaCommand(request);
		
		
		List<BustiaDto> busties = bustiaService.findAmbEntitatAndFiltre(
				entitatActual.getId(),
                BustiaFiltreCommand.asDto(bustiaFiltreOrganigramaCommand));
		
		model.addAttribute(
				"busties",
				busties);
		
		ArbreDto<UnitatOrganitzativaDto> arbreUnitatsOrganitzatives = bustiaService.findArbreUnitatsOrganitzativesAmbFiltre(
				entitatActual.getId(),
				busties);
		
		model.addAttribute(
				"arbreUnitatsOrganitzatives",
				arbreUnitatsOrganitzatives);
	
		BustiaCommand command = new BustiaCommand();
		command.setEntitatId(entitatActual.getId());
		model.addAttribute("bustiaCommand", command);
		
		model.addAttribute("bustiaFiltreOrganigramaCommand", bustiaFiltreOrganigramaCommand);		
	}

	private static final Logger logger = LoggerFactory.getLogger(BustiaAdminOrganigramaController.class);
	
}
