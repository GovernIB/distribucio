package es.caib.distribucio.war.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
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

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.FitxerDto;
//import es.caib.distribucio.core.api.dto.OrganGestorDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
//import es.caib.distribucio.core.api.dto.historic.HistoricExpedientDto;
//import es.caib.distribucio.core.api.dto.historic.HistoricInteressatDto;
//import es.caib.distribucio.core.api.dto.historic.HistoricMetriquesEnumDto;
import es.caib.distribucio.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.distribucio.war.command.ConfigCommand;
//import es.caib.distribucio.core.api.dto.historic.HistoricUsuariDto;
//import es.caib.distribucio.core.api.exception.PermissionDeniedStatisticsException;
//import es.caib.distribucio.core.api.service.HistoricService;
import es.caib.distribucio.war.command.HistoricFiltreCommand;
import es.caib.distribucio.war.command.RegistreFiltreCommand;
import es.caib.distribucio.war.helper.DatatablesHelper;
import es.caib.distribucio.war.helper.DatatablesHelper.DatatablesResponse;
import es.caib.distribucio.war.helper.JsonResponse;
import es.caib.distribucio.war.helper.JsonDadesUo;
import es.caib.distribucio.war.helper.RequestSessionHelper;
//import es.caib.distribucio.war.historic.ExportacioActionHistoric;

@Controller
@RequestMapping("/historic")
public class HistoricController extends BaseAdminController {
	private static final String SESSION_ATTRIBUTE_FILTRE = "HistoricController.session.filtre";

//	@Autowired
//	private HistoricService historicService;

//	@Autowired
//	private ExportacioActionHistoric exportacioActionHistoric;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
	
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
		model.addAttribute("showDadesUO", historicFiltreCommand.showingDadesUO());
		model.addAttribute("showDadesEstat", historicFiltreCommand.showingDadesEstat());
		model.addAttribute("showDadesBusties", historicFiltreCommand.showingDadesBusties());
		Map<String, List<JsonDadesUo>> results = new HashMap<>();
		results.put("uo_1", cargarTabla("uo_1"));
		//TODO: comentar las dos líneas siguientes para ver todas las métricas de la UO en un sólo gráfico
		results.put("uo_2", cargarTabla("uo_2"));
		results.put("uo_3", cargarTabla("uo_3"));
		return results;	
	}	
	
	private List<JsonDadesUo> cargarTabla(String uo) throws NoSuchAlgorithmException {
		List<JsonDadesUo> lrespuestaJson = new ArrayList<JsonDadesUo>();

		SecureRandom number = SecureRandom.getInstance("SHA1PRNG");

		for (int i = 0; i < 6; i++) {
			int j = number.nextInt(21);
			Date dt = new Date();
	        Calendar c = Calendar.getInstance();
	        c.setTime(dt);
	        c.add(Calendar.DATE, i);
	        dt = c.getTime();
	        JsonDadesUo fila = new JsonDadesUo(dt, "uoCod_"+i, uo, i+j, (i+j)*5,
					i+j+2, i+j+1, i+j+3, i+j+7, i+j+10, i+j+15);
			lrespuestaJson.add(fila);
		}
		return lrespuestaJson;
	}
}
