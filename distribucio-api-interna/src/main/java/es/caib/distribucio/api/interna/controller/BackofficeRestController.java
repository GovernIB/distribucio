package es.caib.distribucio.api.interna.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.distribucio.logic.intf.exception.SistemaExternException;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.logic.intf.service.ws.backoffice.BackofficeIntegracioWsService;
import es.caib.distribucio.logic.intf.service.ws.backoffice.Estat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador pel servei REST de backoffice.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/backoffice")
@Tag(
		name = "Backoffice",
		description = "API REST de gestió de backoffices. Per invocar els mètodes és necessari accedir amb un usuari que tengui el rol DIS_BACKWS.")
public class BackofficeRestController {

	private final String ROLE_DIS_BACKWS = "DIS_BACKWS";
	private final String ROLE_DIS_BACKWS_MAPPED = "ROLE_BACKWS";

	@Autowired
	private BackofficeIntegracioWsService backofficeIntegracioWsService;

	@RequestMapping(
			value= "/consulta",
			method = RequestMethod.GET,
			produces = "application/json")
	@Operation(
			summary = "Consulta d'una anotació de registre pendent d'enviar al Backoffice",
			description = "Retorna totes les dades de l'anotació de registre pendents d'enviar al Backoffice consultada")
	public ResponseEntity<Object> consulta(
			HttpServletRequest request,
			@Parameter(name = "indetificador", description = "Identificador de la anotació de registre, sol ser el número de registre", required = true)
			String indetificador,
			@Parameter(name = "clauAcces", description = "Clau de caràcters alfanumèrics proporcionada per Distribucio per poder consultar l'anotació", required = true)
			String clauAcces ) throws SistemaExternException {

		if (!hasRole())
			return responseUnautorized();
		try {
			AnotacioRegistreId id = new AnotacioRegistreId();
			id.setIndetificador(indetificador);
			id.setClauAcces(clauAcces);
			AnotacioRegistreEntrada anotacio = backofficeIntegracioWsService.consulta(id);
			return new ResponseEntity<Object>(anotacio, HttpStatus.OK);
		} catch(Exception e) {
			String errMsg = "Error no controlat consultant l'anotació amb id " + indetificador + " i clau " + clauAcces + ": " + e.getMessage();
			return new ResponseEntity<Object>(errMsg, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(
			value= "/canviEstat",
			method = RequestMethod.POST,
			produces = "application/json")
	@Operation(
			summary = "Canvi d'estat d'una anotació de registre",
			description = "Canvia l'estat d'una anotació de registre enviada a backoffice")
	public ResponseEntity<Object> canviEstat(
			HttpServletRequest request,
			@Parameter(name = "indetificador", description = "Identificador de la anotació de registre, sol ser el número de registre", required = true, in = ParameterIn.QUERY)
			String indetificador,
			@Parameter(name = "clauAcces", description = "Clau de caràcters alfanumèrics proporcionada per Distribucio per poder consultar l'anotació", required = true, in = ParameterIn.QUERY)
			String clauAcces,
			@Parameter(name = "estat", description = "Codi de l'estat que es comunica per a l'anotació. Els possibles valors són 'PENDENT', 'REBUDA', 'PROCESSADA', 'REBUTJADA' i 'ERROR'", required = true, in = ParameterIn.QUERY)
		    Estat estat,
			@Parameter(name = "observacions", description = "Cadena de text opcional per posar observacions en el processament i canvi d'estat per part del backoffice. Se sol usar per indicar un motiu"
					+ "de rebuig o observacions sobre el resultat del processament.", in = ParameterIn.QUERY)
		    String observacions) throws SistemaExternException {
		if (!hasRole())
			return responseUnautorized();
		try {
			AnotacioRegistreId id = new AnotacioRegistreId();
			id.setIndetificador(indetificador);
			id.setClauAcces(clauAcces);
			backofficeIntegracioWsService.canviEstat(
					id,
					estat,
					observacions);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch(Exception e) {
			String errMsg = "Error no controlat canviant l'estat a l'anotació amb id " + indetificador + " i clau " + clauAcces + ": " + e.getMessage();
			return new ResponseEntity<Object>(errMsg, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/** Comprova que que l'usuari autenticat té el rol DIS_BACKWS.
	 * 
	 * @return Retorna fals en cas contrari.
	 */
	private boolean hasRole() {
		boolean hasRole = false;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			for (GrantedAuthority ga : auth.getAuthorities())
				if (ga.getAuthority().equals(ROLE_DIS_BACKWS_MAPPED) 
						|| ga.getAuthority().equals(ROLE_DIS_BACKWS)) {
					hasRole = true;
					break;
				}
		}
		return hasRole;
	}

	/** Construeix una resposta de tipus ResponseEntity amb el missatge d'error en el cos del missatge.
	 * 
	 * @return ResponseEntity amb el missatge del rol y HttpStatus.401
	 */
	private ResponseEntity<Object> responseUnautorized() {
		String errMsg = "";
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			errMsg = "L'usuari autenticat " + auth.getName() + " no té el rol necessari " + ROLE_DIS_BACKWS;
		} else {
			errMsg = "No hi ha cap usuari autenticat amb el rol "  + ROLE_DIS_BACKWS;
		}
		return new ResponseEntity<Object>(errMsg, HttpStatus.UNAUTHORIZED);
	}

}
