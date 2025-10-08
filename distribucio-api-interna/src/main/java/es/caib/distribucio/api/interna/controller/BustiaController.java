package es.caib.distribucio.api.interna.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.api.interna.model.anotacio.DadesAltaAnotacio;
import es.caib.distribucio.logic.intf.registre.RegistreAnotacio;
import es.caib.distribucio.logic.intf.service.ws.bustia.BustiaV1WsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@SecurityRequirement(name = "basicScheme")
@Slf4j
@Controller
@RequestMapping("/bustia")
@Tag(
		name = "Bústia",
		description = "API REST d'alta de registres d'entrada a l'aplicació Distribució.")
public class BustiaController {
	
	@Autowired
	private BustiaV1WsService bustiaV1WsService;
	
	@RequestMapping(value = "/alta", method = RequestMethod.POST)
	@Operation(
			summary = "Alta d'una anotació de registre dins d'una bústia.", 
			description = "Dona d'alta una anotació de registre a la bústia per defecte "
					+ "o a una altra bústia, aplicant el filtre per regla segons "
					+ "la unitat organitzativa i el codi SIA indicats a la petició.")
	@ResponseBody
    public ResponseEntity<String> altaAnotacio(@RequestBody DadesAltaAnotacio dadesAlta) {
		String entitatCodi = dadesAlta.getEntitatCodi();
		String unitatAdministrativaCodi = dadesAlta.getUnitatAdministrativaCodi();
		RegistreAnotacio registreAnotacio = dadesAlta.getRegistreAnotacio();
		
		try {			
			bustiaV1WsService.enviarAnotacioRegistreEntrada(
					entitatCodi, 
					unitatAdministrativaCodi, 
					registreAnotacio);
			
			String msg = "Anotació creada correctament per l'entitat " + entitatCodi + " i unitat " + unitatAdministrativaCodi;
			
			log.debug(msg);
            return ResponseEntity.status(HttpStatus.OK).body(msg);
		} catch (IllegalArgumentException e) {
            log.warn("Error de validació: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Dades d'entrada no vàlides: " + e.getMessage());
        } catch (Exception e) {
        	String errMsg = "Error en l'alta d'anotació: " + e.getMessage(); 
            log.error(errMsg, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errMsg);
        }
	}	
	
}
