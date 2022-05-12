package es.caib.distribucio.apiexterna.controller;


import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
			@RequestParam(required = false) String uoSuperior) throws Exception {
				
		long start = new Date().getTime();
		StringBuilder logMsg = new StringBuilder("Consulda de dades obertes de bústies (");
		Exception exception = null;
		logMsg.append("id: " + id); 
		logMsg.append(", uo: " + uo); 
		logMsg.append(", uoSuperior: " + uoSuperior + ")"); 
		List<BustiaDadesObertesDto> busties = null;
		try {
			busties = bustiaService.findBustiesPerDadesObertes(
					id,
					uo,
					uoSuperior
				);	
		} catch(Exception e) {
			exception = e;
		} finally {
			logMsg.append(" finalitada en " + (new Date().getTime()-start) + "ms");
		}
		if (exception != null) {
			logMsg.append(" amb error " + exception.getClass() + ": "  + exception.getMessage());
			logger.error(logMsg.toString(), exception);
			throw new Exception("Error no controlat en la consulta de bústies: " + exception.getMessage(), exception);
		} else {
			logger.debug(logMsg.toString());
		}
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
			@ApiParam(name="rol", value="Booleà opcional per filtrar els usuaris que tinguin o no permís per rol. r\n"
					+ "Per defecte el valor és nul i es mostren tots els usuaris.")
			@RequestParam(required = false) Boolean rol,
			@ApiParam(name="permis", value="Booleà opcional per filtrar els usuaris que tinguin o no permís directe a la bústia. r\n"
					+ "Per defecte el valor és nul i es mostren tots els usuaris.")
			@RequestParam(required = false) Boolean permis
			) throws Exception {
				
		long start = new Date().getTime();
		StringBuilder logMsg = new StringBuilder("Consulda de dades obertes d'usuaris (");
		Exception exception = null;
		logMsg.append("usuari: " + usuari); 
		logMsg.append(", bustiaId: " + bustiaId); 
		logMsg.append(", uo: " + uo); 
		logMsg.append(", uoSuperior: " + uoSuperior); 
		logMsg.append(", rol: " + rol); 
		logMsg.append(", permis: " + permis + ")"); 
		List<UsuariDadesObertesDto> usuariDOdto = null;
		try {
			usuariDOdto = bustiaService.findBustiesUsuarisPerDadesObertes(
					usuari, 
					bustiaId,
					uo,
					uoSuperior, 
					rol, 
					permis
				);	
		} catch(Exception e) {
			exception = e;
		} finally {
			logMsg.append(" finalitada en " + (new Date().getTime()-start) + "ms");
		}
		if (exception != null) {
			logMsg.append(" amb error " + exception.getClass() + ": "  + exception.getMessage());
			logger.error(logMsg.toString(), exception);
			throw new Exception("Error no controlat en la consulta d'usuaris: " + exception.getMessage(), exception);
		} else {
			logger.debug(logMsg.toString());
		}
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
			@ApiParam(name="anotacioNumero", value="Número d'anotació")
			@RequestParam(required = false) String anotacioNumero, 
			@ApiParam(name="anotacioEstat", value="Codi de l'estat de l'anotació")
			@RequestParam(required = false) RegistreProcesEstatEnum anotacioEstat, 
			@ApiParam(name="error", value="Indica si mostrar anotacions amb estat, sense estat o totes.")
			@RequestParam(required = false) Boolean error, 
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
			) throws Exception {

		long start = new Date().getTime();
		StringBuilder logMsg = new StringBuilder("Consulda de dades obertes de logs d'esdeveniments (");
		Exception exception = null;
		logMsg.append("dataInici: " + dataInici); 
		logMsg.append(", bustiaId: " + dataFi); 
		logMsg.append(", tipus: " + tipus); 
		logMsg.append(", usuari: " + usuari); 
		logMsg.append(", anotacioId: " + anotacioId); 
		logMsg.append(", anotacioNumero: " + anotacioNumero); 
		logMsg.append(", anotacioEstat: " + anotacioEstat); 
		logMsg.append(", error: " + error); 
		logMsg.append(", pendent: " + pendent); 
		logMsg.append(", bustiaOrigen: " + bustiaOrigen); 
		logMsg.append(", bustiaDesti: " + bustiaDesti); 
		logMsg.append(", uoOrigen: " + uoOrigen); 
		logMsg.append(", uoSuperior: " + uoSuperior); 
		logMsg.append(", uoDesti: " + uoDesti); 
		logMsg.append(", uoDestiSuperior: " + uoDestiSuperior + ")"); 
		List<LogsDadesObertesDto> listContingutLog = null;
		try {
			listContingutLog = contingutService.findLogsPerDadesObertes(
					dataInici, 
					dataFi, 
					tipus, 
					usuari, 
					anotacioId, 
					anotacioNumero,
					anotacioEstat, 
					error, 
					pendent, 
					bustiaOrigen, 
					bustiaDesti, 
					uoOrigen, 
					uoSuperior, 
					uoDesti, 
					uoDestiSuperior);
		} catch(Exception e) {
			exception = e;
		} finally {
			logMsg.append(" finalitada en " + (new Date().getTime()-start) + "ms");
		}
		if (exception != null) {
			logMsg.append(" amb error " + exception.getClass() + ": "  + exception.getMessage());
			logger.error(logMsg.toString(), exception);
			throw new Exception("Error no controlat en la consulta de logs d'esdeveniments: " + exception.getMessage(), exception);
		} else {
			logger.debug(logMsg.toString());
		}
		return listContingutLog;		
	}
	

	private static final Logger logger = LoggerFactory.getLogger(ApiExternaController.class);
}

