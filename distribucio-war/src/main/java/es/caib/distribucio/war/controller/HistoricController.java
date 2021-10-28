package es.caib.distribucio.war.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.historic.HistoricAnotacioDto;
import es.caib.distribucio.core.api.dto.historic.HistoricDadesDto;
import es.caib.distribucio.core.api.service.HistoricService;
import es.caib.distribucio.war.command.HistoricFiltreCommand;
import es.caib.distribucio.war.helper.JsonDadesUo;
import es.caib.distribucio.war.helper.RequestSessionHelper;

@Controller
@RequestMapping("/historic")
public class HistoricController extends BaseAdminController {
	private static final String SESSION_ATTRIBUTE_FILTRE = "HistoricController.session.filtre";

	@Autowired
	private HistoricService historicService;

//	@Autowired
//	private ExportacioActionHistoric exportacioActionHistoric;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
	
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		model.addAttribute(historicFiltreCommand);
		model.addAttribute("showDadesUO", false);
		model.addAttribute("showDadesEstat", false);
		model.addAttribute("showDadesBusties", false);

		return "historic";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid HistoricFiltreCommand historicFiltreCommand,
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
						historicFiltreCommand);
			}
		}
		return "redirect:historic";
	}
	
	private HistoricFiltreCommand getFiltreCommand(HttpServletRequest request) {
		HistoricFiltreCommand filtreCommand = (HistoricFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new HistoricFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, filtreCommand);
		}
		return filtreCommand;
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}
	
	@RequestMapping(value = "/JsonDataUO", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<JsonDadesUo>> getDataHistoricUO(HttpServletRequest request,
			@Valid HistoricFiltreCommand historicFiltreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio,
			Model model) throws NoSuchAlgorithmException {	
//		Map<String, List<JsonDadesUo>> results = new HashMap<>();		
//		results.put("uo_1", cargarTabla("uo_1"));
//		//TODO: comentar las dos líneas siguientes para ver todas las métricas de la UO en un sólo gráfico
//		results.put("uo_2", cargarTabla("uo_2"));
//		results.put("uo_3", cargarTabla("uo_3"));
		
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		HistoricDadesDto dades = historicService.getDadesHistoriques(
				entitatActual.getId(),
				historicFiltreCommand.asDto());
		
		// Transforma les dades a resultlats
		Map<String, List<JsonDadesUo>> results = transformarDadesJson(dades);

		return results;	
	}	
	
	private Map<String, List<JsonDadesUo>> transformarDadesJson(HistoricDadesDto dades) {
		Map<String, List<JsonDadesUo>> results = new HashMap<>();
		
		// Dades d'anotacions per UO
		for (HistoricAnotacioDto anotacio : dades.getDadesAnotacions()) {
			List<JsonDadesUo> lrespuestaJson = results.get(anotacio.getUnitat().getCodi());
			if (lrespuestaJson == null) {
				lrespuestaJson = new ArrayList<JsonDadesUo>();
				results.put(anotacio.getUnitat().getCodi(), lrespuestaJson);
			}
			lrespuestaJson.add(new JsonDadesUo(
					anotacio.getData(),
					anotacio.getUnitat().getCodi(), 
					anotacio.getUnitat().getNom(),
					anotacio.getAnotacions(), 
					anotacio.getAnotacionsTotal(), 
					anotacio.getReenviaments(), 
					anotacio.getEmails(), 
					anotacio.getJustificants(), 
					anotacio.getAnnexos(), 
					anotacio.getBusties(), 
					anotacio.getUsuaris()));
		}

		return results;
	}

	private List<JsonDadesUo> cargarTabla(String uo) throws NoSuchAlgorithmException {
		List<JsonDadesUo> lrespuestaJson = new ArrayList<JsonDadesUo>();

		SecureRandom number = SecureRandom.getInstance("SHA1PRNG");

		for (long i = 0; i < 6; i++) {
			long j = number.nextInt(21);
			Date dt = new Date();
	        Calendar c = Calendar.getInstance();
	        c.setTime(dt);
	        c.add(Calendar.DATE, new Long(i).intValue());
	        dt = c.getTime();
	        JsonDadesUo fila = new JsonDadesUo(dt, "uoCod_"+i, uo, i+j, (i+j)*5,
					i+j+2, i+j+1, i+j+3, i+j+7, i+j+10, i+j+15);
			lrespuestaJson.add(fila);
		}
		return lrespuestaJson;
	}
}
