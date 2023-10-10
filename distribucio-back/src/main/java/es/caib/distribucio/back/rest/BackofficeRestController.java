/**
 * 
 */
package es.caib.distribucio.back.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.back.controller.BaseUserController;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.logic.intf.service.ws.backoffice.BackofficeWsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST per rebre les comunicacions d'anotacions pendents via API REST com
 * a backoffice de Distribucio. És un controlador només de proves, és el mètode API
 * REST que han d'implementar els propis backoffices per rebre anotacions pendents via API REST.
 * 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
//@Controller
@RequestMapping("/public/backoffice")
@Tag(name = "/public/backoffice", description = "API REST per comunicar anotacions als backoffices corresponents quan es dispara una regla per provar backoffices de tipus API REST.")
public class BackofficeRestController extends BaseUserController {
	
	@Autowired
	BackofficeWsService backofficeWs;

	/** Retorna la documentació de l'API. */
	@RequestMapping(value = {"", "/", "/apidoc"}, method = RequestMethod.GET)
	public String documentacio(HttpServletRequest request) {
		
		return "apidoc";
	}

	
	@RequestMapping(value = "/comunicarAnotacionsPendents", method = RequestMethod.POST)
	@Operation(
			summary = "Comunicar anotacions als backoffice", 
					description = "Comunicar les anotacions que estiguin en estat pendent de comunicar al backoffice " + 
					"amb el backoffice corresponent"	
			)
	@ResponseBody
	public ResponseEntity<Object> comunicarAnotacionsPendents(			
			HttpServletRequest request,
			@Parameter(name="ids", description="Llista de identificadors d'anotacions comunicades.")
			@RequestBody(required = true) List<AnotacioRegistreId> ids) {
		try {
			logger.debug("S'ha rebut una comunicació de " + ids.size() + " anotacions pendents: " + ids);
			backofficeWs.comunicarAnotacionsPendents(ids);
			String msg = "La comunicació de les anotacions pendents s'ha processat correctament.";
			logger.debug(msg);
			return new ResponseEntity<Object>(msg, HttpStatus.OK);
		} catch(Exception e) {
			String errorDescripcio = "La comunicació de les anotacions pendents s'ha processat amb error: " + e.getMessage();
			logger.error(errorDescripcio, e);
			return new ResponseEntity<Object>(errorDescripcio, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(BackofficeRestController.class);
}