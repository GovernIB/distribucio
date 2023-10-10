/**
 * 
 */
package es.caib.distribucio.back.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.back.command.UnitatOrganitzativaFiltreCommand;
import es.caib.distribucio.back.helper.DatatablesHelper;
import es.caib.distribucio.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.back.helper.RequestSessionHelper;
import es.caib.distribucio.logic.intf.dto.BustiaDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.ReglaDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaEstatEnumDto;
import es.caib.distribucio.logic.intf.service.BustiaService;
import es.caib.distribucio.logic.intf.service.ReglaService;
import es.caib.distribucio.logic.intf.service.UnitatOrganitzativaService;

/**
 * Controlador per al manteniment de unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/unitatOrganitzativa")
@SuppressWarnings("unchecked")
public class UnitatOrganitzativaController extends BaseAdminController{
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "UnitatOrganitzativaController.session.filtre";

	@Autowired
	private UnitatOrganitzativaService unitatOrganitzativaService;
	
	@Autowired
	private BustiaService bustiaService;

	@Autowired
	private ReglaService reglaService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		UnitatOrganitzativaFiltreCommand unitatOrganitzativaFiltreCommand = getFiltreCommand(request);
		model.addAttribute("unitatOrganitzativaFiltreCommand", unitatOrganitzativaFiltreCommand);
		return "unitatOrganitzativaList";
	}
	
	@RequestMapping(value = "/synchronizeGet", method = RequestMethod.GET)
	public String synchronizeGet(
			HttpServletRequest request,
			Model model) {
		
	
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		
		
		MultiMap splitMap = new MultiHashMap();
		MultiMap mergeOrSubstMap = new MultiHashMap();
		MultiMap mergeMap = new MultiHashMap();
		MultiMap substMap = new MultiHashMap();
		List<UnitatOrganitzativaDto> unitatsVigents = new ArrayList<>();
		List<UnitatOrganitzativaDto> unitatsVigentsFirstSincro = new ArrayList<>();
		List<UnitatOrganitzativaDto> unitatsNew = new ArrayList<>();
		List<ReglaDto> rulesFiltre =  new ArrayList<ReglaDto>();
		List<ReglaDto> rulesDesti =  new ArrayList<ReglaDto>();

		
		boolean isFirstSincronization = unitatOrganitzativaService.isFirstSincronization(entitatActual.getId());
		
		if(isFirstSincronization){
			unitatsVigentsFirstSincro = unitatOrganitzativaService.predictFirstSynchronization(entitatActual.getId());
		} else {
			
			
			try {
				
	            //Getting list of unitats that are now vigent in db but syncronization is marking them as obsolete
				List<UnitatOrganitzativaDto> unitatsVigentObsoleteDto = unitatOrganitzativaService
						.getObsoletesFromWS(entitatActual.getId());
	
				// 1, differentiate between split and (subst or merge)
				// 2, check if the map already contains key with this codi
				// 3, if it contains already key with the same codi, assign found key
				diferentiateBetweenSplitAndSubstOrMerge(splitMap, mergeOrSubstMap, unitatsVigentObsoleteDto);	
	
				// differantiate between substitution and merge
				diffSubsAndMerge(mergeOrSubstMap, mergeMap, substMap);
	
				// Getting list of unitats that are now vigent in db and in syncronization are also vigent but with properties changed
				unitatsVigents = unitatOrganitzativaService
						.getVigentsFromWebService(entitatActual.getId());
				
				
				// Getting list of unitats that are totally new (doesnt exist in database)
				unitatsNew = unitatOrganitzativaService
						.getNewFromWS(entitatActual.getId());
				
				
				// For all merges find related rules to old UO
				Set<UnitatOrganitzativaDto> valuesMerge = new HashSet<UnitatOrganitzativaDto>(mergeOrSubstMap.values());
				List<String> codisUosFusionadesSubstituides = new ArrayList<String>();
				for (UnitatOrganitzativaDto uo : valuesMerge) {
					codisUosFusionadesSubstituides.add(uo.getCodi());
				}
				
				for  ( String codiUo : codisUosFusionadesSubstituides) {
					// Regles per filtre
					for(ReglaDto r: reglaService.findByEntitatAndUnitatFiltreCodi(entitatActual.getId(),codiUo)) {
						rulesFiltre.add(r);	
					}
					// Regles per destí
					for(ReglaDto r: reglaService.findByEntitatAndUnitatDestiCodi(entitatActual.getId(),codiUo)) {
						rulesDesti.add(r);	
					}
				}
		
			} catch (Exception exception){
				logger.error("Error synchronizacion", exception);
				return getModalControllerReturnValueErrorNoKey(
						request,
						"redirect:../../unitatOrganitzativa",
						exception.getMessage());
	
			}
		}
		

		
		
		model.addAttribute("isFirstSincronization", isFirstSincronization);
		model.addAttribute("unitatsVigentsFirstSincro", unitatsVigentsFirstSincro);
		
		model.addAttribute("splitMap", splitMap);
		model.addAttribute("mergeMap", mergeMap);
		model.addAttribute("substMap", substMap);
		model.addAttribute("unitatsVigents", unitatsVigents);
		model.addAttribute("unitatsNew", unitatsNew);
		model.addAttribute("rulesFiltre", rulesFiltre);
		model.addAttribute("rulesDesti", rulesDesti);

		
		
		return "synchronizationPrediction";
	}
	
	@RequestMapping(value = "/saveSynchronize", method = RequestMethod.POST)
	public String synchronizePost(
			HttpServletRequest request) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdmin(request);
		unitatOrganitzativaService.synchronize(entitatActual.getId());

		return getModalControllerReturnValueSuccess(
				request,
				"redirect:unitatOrganitzativa",
				"unitat.controller.synchronize.ok");
	}
	
	
	
	
	@RequestMapping(value = "/mostrarArbre", method = RequestMethod.GET)
	public String mostrarArbre(
			HttpServletRequest request,
			Model model) {
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		
		model.addAttribute(
				"arbreUnitatsOrganitzatives",
				unitatOrganitzativaService.findTree(entitatActual.getId()));
		
		return "unitatArbre";
	}
	
	
	
	@RequestMapping(method = RequestMethod.POST)
	public String bustiaPost(
			HttpServletRequest request,
			@Valid UnitatOrganitzativaFiltreCommand filtreCommand,
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

		return "redirect:unitatOrganitzativa";
	}
	
	
	
	@RequestMapping(value = "/{unitatId}/unitatTransicioInfo", method = RequestMethod.GET)
	public String formGet(
			HttpServletRequest request,
			@PathVariable Long unitatId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		UnitatOrganitzativaDto unitat = null;
		if (unitatId != null)
			unitat = unitatOrganitzativaService.findById(
					unitatId);
		model.addAttribute(unitat);
		
		
		// getting all the busties connected with old unitat excluding the one you are currently in 
		List<BustiaDto> bustiesOfOldUnitat = bustiaService.findAmbUnitatCodiAdmin(entitatActual.getId(), unitat.getCodi());
		model.addAttribute("bustiesOfOldUnitat", bustiesOfOldUnitat);	

	
		return "unitatTransicioInfo";
	}
	
	

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		UnitatOrganitzativaFiltreCommand unitatOrganitzativaFiltreCommand = getFiltreCommand(request);
		return DatatablesHelper.getDatatableResponse(
				request,
				unitatOrganitzativaService.findAmbFiltre(
						entitatActual.getId(),
						UnitatOrganitzativaFiltreCommand.asDto(unitatOrganitzativaFiltreCommand),
						DatatablesHelper.getPaginacioDtoFromRequest(request)),
				"id");
	}
	
	private UnitatOrganitzativaFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		UnitatOrganitzativaFiltreCommand unitatOrganitzativaFiltreCommand = (UnitatOrganitzativaFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (unitatOrganitzativaFiltreCommand == null) {
			unitatOrganitzativaFiltreCommand = new UnitatOrganitzativaFiltreCommand();
			unitatOrganitzativaFiltreCommand.setEstat(UnitatOrganitzativaEstatEnumDto.VIGENTE);
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					unitatOrganitzativaFiltreCommand);
		}
		return unitatOrganitzativaFiltreCommand;
	}
	
	private void diferentiateBetweenSplitAndSubstOrMerge(MultiMap splitMap, MultiMap mergeOrSubstMap,
			List<UnitatOrganitzativaDto> unitatsVigentObsoleteDto) {
		// differentiate between split and (subst or merge)
		for (UnitatOrganitzativaDto vigentObsolete : unitatsVigentObsoleteDto) {
			if (vigentObsolete.getLastHistoricosUnitats().size() > 1) {
				for (UnitatOrganitzativaDto hist : vigentObsolete.getLastHistoricosUnitats()) {
					splitMap.put(vigentObsolete, hist);
				}
			} else if (vigentObsolete.getLastHistoricosUnitats().size() == 1) {
				// check if the map already contains key with this codi
				UnitatOrganitzativaDto mergeOrSubstKeyWS = vigentObsolete.getLastHistoricosUnitats().get(0);
				UnitatOrganitzativaDto keyWithTheSameCodi = null;
				Set<UnitatOrganitzativaDto> keysMergeOrSubst = mergeOrSubstMap.keySet();
				for (UnitatOrganitzativaDto mergeOrSubstKeyMap : keysMergeOrSubst) {
					if (mergeOrSubstKeyMap.getCodi().equals(mergeOrSubstKeyWS.getCodi())) {
						keyWithTheSameCodi = mergeOrSubstKeyMap;
					}
				}
				// if it contains already key with the same codi, assign
				// found key
				if (keyWithTheSameCodi != null) {
					mergeOrSubstMap.put(keyWithTheSameCodi, vigentObsolete);
				} else {
					mergeOrSubstMap.put(mergeOrSubstKeyWS, vigentObsolete);
				}
			}
		}
	}
	
	
	private void diffSubsAndMerge(MultiMap mergeOrSubstMap, MultiMap mergeMap, MultiMap substMap) {
		Set<UnitatOrganitzativaDto> keysMergeOrSubst = mergeOrSubstMap.keySet();
		for (UnitatOrganitzativaDto mergeOrSubstKey : keysMergeOrSubst) {
			List<UnitatOrganitzativaDto> values = (List<UnitatOrganitzativaDto>) mergeOrSubstMap.get(mergeOrSubstKey);
			
			if (values.size() > 1) {
				for (UnitatOrganitzativaDto value : values) {
					mergeMap.put(mergeOrSubstKey, value);
				}
			} else {
				substMap.put(mergeOrSubstKey, values.get(0));
			}
		}
	}
	private static final Logger logger = LoggerFactory.getLogger(UnitatOrganitzativaController.class);



}
