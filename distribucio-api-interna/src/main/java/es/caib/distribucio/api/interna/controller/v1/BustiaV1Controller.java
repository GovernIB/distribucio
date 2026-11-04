package es.caib.distribucio.api.interna.controller.v1;

import es.caib.distribucio.api.interna.model.anotacio.DadesAltaAnotacio;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.RegistreClassificarTipusEnum;
import es.caib.distribucio.logic.intf.model.SiaResource;
import es.caib.distribucio.logic.intf.registre.RegistreAnotacio;
import es.caib.distribucio.logic.intf.registre.RegistreAnotacioBase;
import es.caib.distribucio.logic.intf.registre.v1.RegistreAnotacioV1;
import es.caib.distribucio.logic.intf.resourceservice.SiaResourceService;
import es.caib.distribucio.logic.intf.service.EntitatService;
import es.caib.distribucio.logic.intf.service.ws.bustia.BustiaV1WsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping("/v1/bustia")
@Tag(
		name = "Bústia V1",
		description = "API REST d'alta de registres d'entrada a l'aplicació Distribució.")
public class BustiaV1Controller {
	
	@Autowired
	private BustiaV1WsService bustiaV1WsService;
    @Autowired
    private SiaResourceService siaResourceService;
    @Autowired
    private EntitatService entitatService;

    @RequestMapping(value = "/alta", method = RequestMethod.POST)
	@Operation(
			summary = "Alta d'una anotació de registre dins d'una bústia.", 
			description = "Dona d'alta una anotació de registre a la bústia per defecte "
					+ "o a una altra bústia, aplicant el filtre per regla segons "
					+ "la unitat organitzativa i el codi SIA indicats a la petició.")
	@ResponseBody
    public ResponseEntity<String> altaAnotacio(@RequestBody DadesAltaAnotacio<RegistreAnotacioV1> dadesAlta) {
		String entitatCodi = dadesAlta.getEntitatCodi();
		String unitatAdministrativaCodi = dadesAlta.getUnitatAdministrativaCodi();
        RegistreAnotacioV1 registreAnotacio = dadesAlta.getRegistreAnotacio();

        RegistreAnotacio registre = (RegistreAnotacio)((RegistreAnotacioBase)registreAnotacio);

        EntitatDto entitat = entitatService.findByCodi(entitatCodi);
        SiaResource siaResource = siaResourceService.findByEntitatIdAndCodiSia(entitat.getId(), registreAnotacio.getSiaCodi());

        if ( siaResource == null || RegistreClassificarTipusEnum.PROCEDIMENT.equals(siaResource.getTipus()) ) {
            registre.setProcedimentCodi(registreAnotacio.getSiaCodi());
        } else {
            registre.setServeiCodi(registreAnotacio.getSiaCodi());
        }

		try {
			bustiaV1WsService.enviarAnotacioRegistreEntrada(
					entitatCodi, 
					unitatAdministrativaCodi,
                    registre,
					BustiaV1WsService.Origen.REST);
			
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
