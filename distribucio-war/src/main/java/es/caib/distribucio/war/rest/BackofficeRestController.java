/**
 * 
 */
package es.caib.distribucio.war.rest;

import java.util.ArrayList;
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

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.core.api.service.ws.backoffice.BackofficeWsService;
import es.caib.distribucio.war.controller.BaseUserController;

/**
 * Controlador REST per rebre les comunicacions d'anotacions pendents via API REST com
 * a backoffice de Distribucio. És un controlador només de proves, és el mètode API
 * REST que han d'implementar els propis backoffices per rebre anotacions pendents via API REST.
 * 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/api/rest/backoffice")
@Api(value = "/api/rest/backoffice", description = "API REST per comunicar anotacions als backoffices corresponents quan es dispara una regla.")
public class BackofficeRestController extends BaseUserController {
	
	@Autowired
	BackofficeWsService backofficeWs;

	/** Retorna la documentació de l'API. */
	@RequestMapping(value = {"", "/", "/apidoc"}, method = RequestMethod.GET)
	public String documentacio(HttpServletRequest request) {
		
		return "apidoc";
	}

	@RequestMapping(value = "/comunicarAnotacionsPendents", method = RequestMethod.POST)
	@ApiOperation(
			value = "Comunicar anotacions als backoffice", 
			notes = "Comunicar les anotacions que estiguin en estat pendent de comunicar al backoffice " + 
					"amb el backoffice corresponent"	
			)
	@ResponseBody
	public ResponseEntity<Object> comunicarAnotacionsPendents(			
			HttpServletRequest request,

			@ApiParam(name="anotacioRegistreIdClau", value="Identificador i clau d'accés de l'anotació que s'ha de comunicar al backoffice")
			@RequestBody(required = true) String anotacioRegistreIdClau) {
		String[] anotacioRegistreId = anotacioRegistreIdClau.split("&");
		String[] identificador = anotacioRegistreId[1].split("identificador=");
		String[] clauAcces = anotacioRegistreId[0].split("clauAcces=");
		AnotacioRegistreId id = new AnotacioRegistreId();
		id.setIndetificador(identificador[1]);
		id.setClauAcces(clauAcces[1]);
		List<AnotacioRegistreId> ids = new ArrayList<>();
		ids.add(id);
		
		try {
			logger.trace(">>> Abans de cridar backoffice WS");
			backofficeWs.comunicarAnotacionsPendents(ids);
			String msg = "L'anotació amb id " + id.getIndetificador() + " s'ha comunicat correctament al backoffice";
			logger.debug(msg);
			return new ResponseEntity<Object>(msg, HttpStatus.OK);
		} catch(Exception e) {
			String errorDescripcio = "Error " + e.getClass().getSimpleName() + " enviant " + ids.size() + "anotacions als seus backoffices :" + e.getMessage();
			logger.error(errorDescripcio + e.getMessage());
			return new ResponseEntity<Object>(errorDescripcio, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(BackofficeRestController.class);
}
