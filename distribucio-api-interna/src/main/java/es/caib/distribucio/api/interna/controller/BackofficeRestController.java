package es.caib.distribucio.api.interna.controller;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import es.caib.distribucio.api.interna.model.InfoCanviEstat;
import es.caib.distribucio.core.api.exception.SistemaExternException;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.core.api.service.ws.backoffice.BackofficeIntegracioWsService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Controlador pel servei REST de backoffice.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/backoffice")
@Slf4j
public class BackofficeRestController {

    @Autowired
    BackofficeIntegracioWsService backofficeIntegracioWsService;

    @RequestMapping(
            value= "/consulta",
            method = RequestMethod.GET,
            produces = "application/json;charset=utf-8")
    @ApiOperation(
            value = "Consulta d'una anotació de registre pendent d'enviar al Backoffice",
            notes = "Retorna totes les dades de l'anotació de registre pendents d'enviar al Backoffice consultada")
    @ResponseBody
    public ResponseEntity<AnotacioRegistreEntrada> consulta(
            HttpServletRequest request,
            @ApiParam(name="allParams", value="Mapa que conté tots els paràmetres. Ha d'incloure 2 paràmetres: 'indetificador' (identificador de l'anotació) i 'clauAcces' (clau proporcionada per a poder accedit a l'anotació)")
            @RequestParam Map<String,String> allParams) {
//            @ApiParam(name="id", value="Identificador de la anotació de registre")
//            final AnotacioRegistreId id) throws SistemaExternException {
        log.info("[REST CONSULTA] Paràmetres rebuts (" + allParams.size() + "):");
        for (Map.Entry<String, String> entry: allParams.entrySet()) {
            log.info("[REST CONSULTA]   - " + entry.getKey() + ": " + entry.getValue());
        }

        String identificador = allParams.get("indetificador");
        String clauAcces = allParams.get("clauAcces");

        AnotacioRegistreId id = new AnotacioRegistreId();
        id.setIndetificador(identificador);
        id.setClauAcces(clauAcces);

        AnotacioRegistreEntrada respuesta = backofficeIntegracioWsService.consulta(id);
        return new ResponseEntity<>(respuesta, HttpStatus.OK);
    }

    @RequestMapping(
            value= "/canviEstat",
            method = RequestMethod.POST,
            produces = "application/json;charset=utf-8")
    @ApiOperation(
            value = "Canvi d'estat d'una anotació de registre",
            notes = "Canvia l'estat d'una anotació de registre enviada a backoffice")
    @ResponseBody
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
