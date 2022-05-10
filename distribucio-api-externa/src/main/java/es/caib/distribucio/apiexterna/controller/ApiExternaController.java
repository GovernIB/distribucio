package es.caib.distribucio.apiexterna.controller;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import es.caib.distribucio.core.api.dto.LogTipusEnumDto;
import es.caib.distribucio.core.api.dto.dadesobertes.BustiaDadesObertesDto;
import es.caib.distribucio.core.api.dto.dadesobertes.LogsDadesObertesDto;
import es.caib.distribucio.core.api.dto.dadesobertes.UsuariDadesObertesDto;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.service.BustiaService;
import es.caib.distribucio.core.api.service.ContingutService;

/**
 * Controlador API REST per a les Dades Obertes per a la consulta de bústies, usuaris i esdevenidments (logs).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/externa/opendata")
@Api(value = "/externa/opendata", description = "API REST de consulta de Dades Obertes. És necessari tenir el rol DIS_REPORT.")
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

			@ApiParam(name="dataInici", value="Data filtre inici. Si no s'especifica serà un mes abans de la data fi o de la data de la consulta.<br>(dd-mm-yyyy)")
			@RequestParam(value = "dataInici", required = false) 
			@DateTimeFormat(pattern = "dd-MM-yyyy") Date dataInici, 
			@ApiParam(name="dataFi", value="Data filtre fi. Si no s'especifica serà un mes posterior a la data d'inici o el mateix dia de la consulta en cas de no especificar la data d'inici La data de fi està inclosa en el rang de consulta.<br>(dd-mm-yyyy)")
			@RequestParam(required = false) 
			@DateTimeFormat(pattern = "dd-MM-yyyy") Date dataFi, 
			@ApiParam(name="tipus", value="Codi del tipus d'esdeveniment")
			@RequestParam(required = false) LogTipusEnumDto tipus, 
			@ApiParam(name="usuari", value="Codi de l'usuari")
			@RequestParam(required = false) String usuari, 
			@ApiParam(name="anotacioId", value="Id anotació")
			@RequestParam(required = false) Long anotacioId, 
			@ApiParam(name="anotacioEstat", value="Codi de l'estat de l'anotació")
			@RequestParam(required = false) RegistreProcesEstatEnum anotacioEstat, 
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
		
		// Adequa les dates d'inici i fi
		Calendar c = new GregorianCalendar();
		if (dataInici != null && dataFi != null) {
			if (dataInici.after(dataFi)) {
				dataFi = dataInici; 
			} else {
				c.setTime(dataInici);
				c.add(Calendar.MONTH, 1);
				if (dataFi.after(c.getTime())) {
					dataFi = c.getTime();
				}
			}
		} else if (dataInici == null && dataFi != null) {
			c.setTime(dataFi);
			c.add(Calendar.MONTH, -1);
			dataInici = c.getTime();
		} else if (dataInici != null && dataFi == null) {
			c.setTime(dataInici);
			c.add(Calendar.MONTH, 1);
			dataFi = c.getTime();
		} else {
			dataInici = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
			c.setTime(dataInici);
			c.add(Calendar.MONTH, 1);
			dataFi = c.getTime();
		}
		// Afegeix un dia a la data de fi per incloure'l en els resultats
		c.setTime(dataFi);
		c.add(Calendar.DATE, 1);
		dataFi = c.getTime();
		
		List<LogsDadesObertesDto> listContingutLog = contingutService.findLogsPerDadesObertes(
				dataInici, 
				dataFi, 
				tipus, 
				usuari, 
				anotacioId, 
				anotacioEstat, 
				errorEstat, 
				pendent, 
				bustiaOrigen, 
				bustiaDesti, 
				uoOrigen, 
				uoSuperior, 
				uoDesti, 
				uoDestiSuperior);
		
		return listContingutLog;		
	}
	

	private static final Logger logger = LoggerFactory.getLogger(ApiExternaController.class);
}

