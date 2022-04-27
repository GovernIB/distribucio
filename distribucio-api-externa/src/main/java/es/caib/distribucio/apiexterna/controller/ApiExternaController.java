package es.caib.distribucio.apiexterna.controller;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import es.caib.distribucio.core.api.dto.dadesobertes.BustiaDadesObertesDto;
import es.caib.distribucio.core.api.dto.dadesobertes.LogsDadesObertesDto;
import es.caib.distribucio.core.api.dto.dadesobertes.UsuariDadesObertesDto;
import es.caib.distribucio.core.api.service.BustiaService;
import es.caib.distribucio.core.api.service.ContingutService;

/**
 * Controlador REST per a les dades obertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/externa/opendata")
@Api(value = "/externa/opendata", description = "API REST de consulta de dades obertes. És necessari tenir el rol DIS_REPORT.")
public class ApiExternaController {

	@Autowired
	private BustiaService bustiaService;
	@Autowired
	private ContingutService contingutService;

	@RequestMapping(value = {"", "/apidoc"}, method = RequestMethod.GET)
	public String documentacio(HttpServletRequest request) {
		
		return "apidoc";
	}

	
	@RequestMapping(value = "/busties", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(
			value = "Petició de dades de bústies", 
			notes = "Retorna informació de bústies"
			)
	public List<BustiaDadesObertesDto> busties(
			HttpServletRequest request, 
			
			@ApiParam(name="id", value="Id de la bústia")
			@RequestParam(required = false) Long id, 
			@ApiParam(name="uo", value="Codi DIR3 de l'unitat organitzativa")
			@RequestParam(required = false) String uo, 
			@ApiParam(name="uoSuperior", value="Codi DIR3 de l'unitat organitzativa superior")
			@RequestParam(required = false) String uoSuperior) {
				
		List<BustiaDadesObertesDto> busties = bustiaService.findBustiesPerDadesObertes(
					id,
					uo,
					uoSuperior
				);		
		
		return busties;
	}
	
	
	@RequestMapping(value = "/usuaris", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(
			value = "Petició de dades de usuaris per bústies", 
			notes = "Retorna informació de usuaris per bústies"
			)
	public List<UsuariDadesObertesDto> usuaris(
			HttpServletRequest request, 
			
			@ApiParam(name="usuari", value="Codi de l'usuari")
			@RequestParam(required = false) String usuari, 
			@ApiParam(name="bustiaId", value="Id de la bústia")
			@RequestParam(required = false) Long bustiaId, 
			@ApiParam(name="uo", value="Codi dir3 de l'unitat organitzativa")
			@RequestParam(required = false) String uo, 
			@ApiParam(name="uoSuperior", value="Codi dir3 de l'unitat organitzativa superior")
			@RequestParam(required = false) String uoSuperior, 
			@ApiParam(name="rol", value="Booleà per indicar si mostrar o no els usuaris amb permís sobre la\r\n"
					+ "bústia per rol, per defecte el valor és sí")
			@RequestParam(required = false, defaultValue = "true") boolean rol,
			@ApiParam(name="permis", value="Indica si mostrar o no els usuaris amb permís directe. Per\r\n"
					+ "defecte el valor és sí") 
			@RequestParam(required = false, defaultValue = "true") boolean permis
			) {
				
		List<UsuariDadesObertesDto> usuariDOdto = bustiaService.findBustiesUsuarisPerDadesObertes(
				usuari, 
				bustiaId,
				uo,
				uoSuperior, 
				rol, 
				permis
			);	
		
		return usuariDOdto;		
	}
	
	
	@RequestMapping(value = "/logs", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(
			value = "Petició de dades de logs", 
			notes = "Retorna informació dels logs"
			)
	public List<LogsDadesObertesDto> logs (
			HttpServletRequest request, 

			@ApiParam(name="dataInici", value="Data inici esdeveniment <br>(dd-mm-yyyy)")
			@RequestParam(required = false) String dataInici, 
			@ApiParam(name="dataFi", value="Data fi esdeveniment <br>(dd-mm-yyyy)")
			@RequestParam(required = false) String dataFi, 
			@ApiParam(name="tipus", value="Id tipus esdeveniment")
			@RequestParam(required = false) String tipus, 
			@ApiParam(name="usuari", value="Codi de l'usuari")
			@RequestParam(required = false) String usuari, 
			@ApiParam(name="anotacioId", value="Id anotació")
			@RequestParam(required = false) Long anotacioId, 
			@ApiParam(name="anotacioEstat", value="Id estat anotació")
			@RequestParam(required = false) String anotacioEstat, 
			@ApiParam(name="errorEstat", value="Id estat error anotació")
			@RequestParam(required = false) Boolean errorEstat, 
			@ApiParam(name="pendent", value="Booleà per indicar si la bústia està pendent")
			@RequestParam(required = false) Boolean pendent, 
			@ApiParam(name="bustiaOrigen", value="Id bústia origen")
			@RequestParam(required = false) Long bustiaOrigen, 
			@ApiParam(name="bustiaDesti", value="Id bústia destí")
			@RequestParam(required = false) Long bustiaDesti, 
			@ApiParam(name="uoOrigen", value="Codi dir3 de l'unitat organitzativa de la bústia origen")
			@RequestParam(required = false) String uoOrigen, 
			@ApiParam(name="uoSuperior", value="Codi dir3 de l'unitat organitzativa superior de la bústia origen")
			@RequestParam(required = false) String uoSuperior, 
			@ApiParam(name="uoDesti", value="Codi dir3 de l'unitat organitzativa de la bústia destí")
			@RequestParam(required = false) String uoDesti, 
			@ApiParam(name="uoDestiSuperior", value="Codi dir3 de l'unitat organitzativa superior de la bústia destí")
			@RequestParam(required = false) String uoDestiSuperior
			) {
		
		if (dataInici == null) {			
			DateTime faUnMes = new DateTime().minusMonths (1); 
			dataInici = faUnMes.getDayOfMonth() + "-" + faUnMes.getMonthOfYear() + "-" + faUnMes.getYear();
			DateTime avui = new DateTime();
			dataFi = avui.getDayOfMonth() + "-" + avui.getMonthOfYear() + "-" + avui.getYear();
		}
		if (dataFi == null) {
			String[] dataIniciSplit = new String[3];
			dataIniciSplit = dataInici.split("-");
			int mes = Integer.parseInt(dataIniciSplit[1]) + 1;
			dataFi = dataIniciSplit[0] + "-" + mes + "-" + dataIniciSplit[2];
			
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Timestamp dataInicit = null;
		Timestamp dataFit = null;
		try {
			Date parseDate = dateFormat.parse(dataInici);
			Timestamp timestamp = new java.sql.Timestamp(parseDate.getTime());
			dataInicit = timestamp;
			Date parseDate2 = dateFormat.parse(dataFi);
			Timestamp timestamp2 = new java.sql.Timestamp(parseDate2.getTime());
			dataFit = timestamp2;
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
		List<LogsDadesObertesDto> listContingutLog = contingutService.findLogsDetallsPerData(
				dataInicit, dataFit, tipus, usuari, anotacioId, anotacioEstat, errorEstat, 
				pendent, bustiaOrigen, bustiaDesti, uoOrigen, uoSuperior, uoDesti, uoDestiSuperior);
		
		return listContingutLog;		
	}
	

	private static final Logger logger = LoggerFactory.getLogger(ApiExternaController.class);
}

