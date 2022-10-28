package es.caib.distribucio.api.interna.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//import com.sun.jersey.api.client.Client;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import es.caib.distribucio.api.interna.model.InfoCanviEstat;
import es.caib.distribucio.core.api.exception.SistemaExternException;
import es.caib.distribucio.core.api.service.EntitatService;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.core.api.service.ws.backoffice.BackofficeIntegracioWsService;

/**
 * Controlador pel servei REST de backoffice.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/backoffice")
public class BackofficeRestController {

	@Autowired
	EntitatService entitatService;
	
    @Autowired
    BackofficeIntegracioWsService backofficeIntegracioWsService;

    @RequestMapping(
            value= "/consulta",
            method = RequestMethod.GET,
            produces = "application/json")
    @ApiOperation(
            value = "Consulta d'una anotació de registre pendent d'enviar al Backoffice",
            notes = "Retorna totes les dades de l'anotació de registre pendents d'enviar al Backoffice consultada")
    public ResponseEntity<AnotacioRegistreEntrada> consulta(
            HttpServletRequest request,
            @ApiParam(name="id", value="Identificador de la anotació de registre")
            final AnotacioRegistreId id) throws SistemaExternException {
        AnotacioRegistreEntrada respuesta = backofficeIntegracioWsService.consulta(id);
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }
    
    
    @RequestMapping(
            value= "/consultaXXX",
            method = RequestMethod.GET,
            produces = "application/json")
    @ApiOperation(
            value = "Consulta d'una anotació de registre pendent d'enviar al Backoffice",
            notes = "Retorna totes les dades de l'anotació de registre pendents d'enviar al Backoffice consultada")
    public ResponseEntity<AnotacioRegistreEntrada> consultaXXX(
            HttpServletRequest request,
            @ApiParam(name="id", value="Identificador de la anotació de registre")
            final AnotacioRegistreId id) throws SistemaExternException {
    	ResponseEntity<AnotacioRegistreEntrada> response = null;
    	try {
    		response = new ResponseEntity<>(new AnotacioRegistreEntrada(), HttpStatus.OK);
    	}catch (Exception ex){
    		throw new SistemaExternException("No s'ha pogut fer la consulta de l'anotació amb id " + id, ex);
    	}
    	
//        return new ResponseEntity<>(new AnotacioRegistreEntrada(), HttpStatus.OK);
    	return response;
    }

    @RequestMapping(
            value= "/canviEstat",
            method = RequestMethod.POST,
            produces = "application/json")
    @ApiOperation(
            value = "Canvi d'estat d'una anotació de registre",
            notes = "Canvia l'estat d'una anotació de registre enviada a backoffice")
    public ResponseEntity<Void> canviEstat(
            HttpServletRequest request,
            @ApiParam(name="infoCanviEstat", value="Informació del canvi d'estat. Inclou l'identificador de l'anotació de registrem, l'estat que es vol assignar a l'anotació, i observacions.")
            @RequestBody final InfoCanviEstat infoCanviEstat) throws SistemaExternException {
    	backofficeIntegracioWsService.canviEstat(
                infoCanviEstat.getId(),
                infoCanviEstat.getEstat(),
                infoCanviEstat.getObservacions());
        return new ResponseEntity<>(HttpStatus.OK);
    }    
}
