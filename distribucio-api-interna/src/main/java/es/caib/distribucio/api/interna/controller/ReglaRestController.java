/**
 * 
 */
package es.caib.distribucio.api.interna.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.logic.intf.dto.BackofficeDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentDto;
import es.caib.distribucio.logic.intf.dto.ReglaDto;
import es.caib.distribucio.logic.intf.dto.ReglaPresencialEnumDto;
import es.caib.distribucio.logic.intf.dto.ReglaTipusEnumDto;
import es.caib.distribucio.logic.intf.service.BackofficeService;
import es.caib.distribucio.logic.intf.service.EntitatService;
import es.caib.distribucio.logic.intf.service.ProcedimentService;
import es.caib.distribucio.logic.intf.service.ReglaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST per a l'API REST de creació, canvi d'estat, consulta i actualtizació de regles de Distribucio per a 
 * que els backoffices puguin crear regles sense haver d'entrar a Distribucio. Per invocar
 * aquesta API és necessari el rol DIS_REGLA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/regla")
@Tag(
		name = "Regla",
		description = "API REST de creació de regles per backoffices i codi SIA. Per invocar els mètodes és necessari accedir amb un usuari que tengui el rol DIS_REGLA.")
public class ReglaRestController {

	@Autowired
	private ReglaService reglaService;
	@Autowired
	private BackofficeService backofficeService;
	@Autowired
	private EntitatService entitatService;
	@Autowired
	private ProcedimentService procedimentService;

	@RequestMapping(value = "/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON)
	@Operation(
			summary = "Alta de regla per codi SIA", 
			description = "Dona d'alta una regla pel backoffice i codi SIA indicat per a l'entitat indicada. Per poder invocar aquest mètode "
					+ "és necessari una autenticació bàsica amb el rol DIS_REGLA."
					+ " S'informarà del camp presencial per, si és valor és true, no desar la regla al backoffice.")
	@ResponseBody
	public ResponseEntity<Object> add(HttpServletRequest request,
			@Parameter(name = "entitat", description = "Entitat en la qual crear la regla.") 
			@RequestParam(required = true) String entitat,
			@Parameter(name = "sia", description = "Codi SIA de la regla pel filtre per procediment.")
			@RequestParam(required = true) String sia,
			@Parameter(name = "backoffice", description = "Codi Backoffice per la regla al qual s'enviaran les anotacions.") 
			@RequestParam(required = true) String backoffice,
			@Parameter(name = "presencial", description = "Booleà per informar el filtre presencial de la regla. Paràmetre opcional.") 
			@RequestParam(required = false) Boolean presencial) {
		// Per posar la data a la descripció
		String msg;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String dataAra = sdf.format(new Date());
		// Definim els valors que no hi son als paràmetres
		String nom = backoffice + " " + sia;
		String descripcio = "Creació de regla en data de " + dataAra + " pel backoffice amb codi " + backoffice + " i codi de procediment " + sia;
		ReglaTipusEnumDto tipus = ReglaTipusEnumDto.BACKOFFICE;		
		// Validar que la entitat existeix
		EntitatDto entitatDto = entitatService.findByCodiDir3(entitat);
		if (entitatDto == null ) {
			return new ResponseEntity<Object>("No s'ha trobat l'entitat " + entitat, HttpStatus.NOT_FOUND);
		}
		// Validar que es troba el backoffice
		BackofficeDto backofficeDto = backofficeService.findByCodi(entitatDto.getId(), backoffice);
		if (backofficeDto == null) {
			return new ResponseEntity<Object>("No s'ha trobat el backoffice amb codi " + backoffice,
					HttpStatus.NOT_FOUND);
		}
		// Cream l'objecte de tipus ReglaDto
		ReglaDto novaReglaDto = new ReglaDto();
		novaReglaDto.setNom(nom);
		novaReglaDto.setDescripcio(descripcio);
		novaReglaDto.setTipus(tipus);
		novaReglaDto.setBackofficeDestiId(backofficeDto.getId());
		novaReglaDto.setBackofficeDestiNom(backoffice);
		novaReglaDto.setProcedimentCodiFiltre(sia);
		if (presencial != null) {
			novaReglaDto.setPresencial(presencial.booleanValue() ? ReglaPresencialEnumDto.SI : ReglaPresencialEnumDto.NO );
		}
		// Validar que no hi ha cap altra regla pel SIA per un backoffice diferent
		List<ReglaDto> reglesPerSia = reglaService.findReglaBackofficeByProcediment(sia);
		for (ReglaDto regla : reglesPerSia) {
			if (backofficeDto.getId().compareTo(regla.getBackofficeDestiId()) != 0) {
				// KO Existeix una regla amb mateix codi SIA per un altre backoffice
				return new ResponseEntity<Object>("Ja existeix la regla amb id " + regla.getId() + " i nom \""
						+ regla.getNom() + "\" pel backoffice \"" + regla.getBackofficeDestiNom() + "\" a l'entitat \""
						+ regla.getEntitatNom() + "\"", HttpStatus.NOT_ACCEPTABLE);
			} else {
				//En comprovar-se que ja existeix una regla amb el codi sia (procediment) idèntic 
				//al mateix backoffice es llança un update amb les noves dades i es finalitza el servei.
				novaReglaDto.setId(regla.getId());
				novaReglaDto = reglaService.update(entitatDto.getId(), novaReglaDto);
				msg = "Regla amb id " + novaReglaDto.getId() + " \"" + novaReglaDto.getNom()
				+ "\" actualitzada correctament pel backoffice " + backoffice + " pel codi SIA " + sia
				 + " a l'entitat " + entitat;
				// OK Regla existent pel mateix backoffie
				return new ResponseEntity<Object>(msg, HttpStatus.OK);
			}
		}
		try {
			novaReglaDto = reglaService.create(entitatDto.getId(), novaReglaDto);
			ProcedimentDto procediment = procedimentService.findByCodiSia(entitatDto.getId(), sia);
			if (procediment != null) {
				msg = "Regla amb id " + novaReglaDto.getId() + " \"" + novaReglaDto.getNom()
						+ "\" creada correctament pel backoffice " + backoffice + " pel codi SIA " + sia + " ("
						+ procediment.getNom() + ")" + " de la unitat " + " \""
						+ procediment.getUnitatOrganitzativa().getCodiAndNom() + "\"" + " a l'entitat " + entitat;
			} else {
				msg = "Regla amb id " + novaReglaDto.getId() + " \"" + novaReglaDto.getNom()
						+ "\" creada correctament pel backoffice " + backoffice + " pel codi SIA " + sia
						+ "(Procediment no trobat)" + " a l'entitat " + entitat;
			}
			logger.debug(msg);
			return new ResponseEntity<Object>(msg, HttpStatus.OK);
		} catch (Exception e) {
			String errMsg = "Error creant la regla pel backoffice " + backoffice + " pel codi SIA " + sia + " a l'entitat " + entitat + ": " + e.getMessage(); 
			logger.error(errMsg, e);
			return new ResponseEntity<Object>(errMsg, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/canviEstat", method = RequestMethod.POST)
	@Operation(
			summary = "Activar/Desactivar regla", 
			description = "Depenent del seu estat, activa o desactiva una regla en concret.")
	@ResponseBody
	public ResponseEntity<String> canviEstat(
			HttpServletRequest request, 
			@Parameter(name = "sia", description = "Codi SIA de la regla")
			@RequestParam(required = true) String sia,
			@Parameter(name = "activa", description = "Paràmetre opcional per activar o desactivar la regla. Si on s'especifica es canvia segons el valor que tingui actualment.")
			@RequestParam(required = false) Boolean activa){
		List<ReglaDto> regles = reglaService.findReglaBackofficeByProcediment(sia);
		ReglaDto regla;
		String response = "";
		if (regles == null || regles.isEmpty()) {
			return new ResponseEntity<String>("La regla amb el codi " + sia + " no existeix", HttpStatus.CONFLICT);
		} else if (regles.size() > 1) {
			logger.warn("S'han trobat " + regles.size() + " regles pel codi de procediment " + sia + ", es consultarà només la primera regla.");
		}
		regla = regles.get(0);
		try {
			if (activa == null) {
				activa = !regla.isActiva();
			}
			reglaService.updateActiva(
					regla.getEntitatId(), 
					regla.getId(),
					sia,
					activa);
			if (activa) {
				response = "La regla amb codi " + sia + " s'ha activat correctament.";	
			}else {
				response = "La regla amb codi " + sia + " s'ha desactivat correctament.";
			}
		} catch(Exception e) {
			String errMsg = "error fent l'update de la regla " + regla.getNom() + " pel procediment " + sia + ":" + e.getMessage();
			logger.error(errMsg, e);
			return new ResponseEntity<String>(errMsg, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(response, HttpStatus.OK);	
	}

	@RequestMapping(value = "/consultarRegla", method = RequestMethod.GET)
	@Operation(
			summary = "Consultar regles per codi SIA",
			description = "Consulta les regles per codi SIA que existeixin i esigui actives. En principi només hi pot haver una regla de tipus backoffice per codi SIA.")
	@ResponseBody
	public ResponseEntity<Object> consultarRegla(
			HttpServletRequest request, 
			@Parameter(name = "sia", description = "Codi SIA de la regla que identifica la regla de tipus backoffice.")
			@RequestParam(required = true) String sia) {
		List<ReglaDto> reglesDto = reglaService.findReglaBackofficeByProcediment(sia);
		if (reglesDto.size() > 1) {
			logger.warn("S'han trobat " + reglesDto.size() + " regles pel codi de procediment " + sia + ".");
		}
		List<Map<String, Object>> regles = new ArrayList<>();
		if (reglesDto == null || reglesDto.isEmpty()) {
			return new ResponseEntity<Object>("No s'ha trobat cap regla amb el codi SIA: " + sia, HttpStatus.NOT_FOUND);
		}
		for (ReglaDto regla : reglesDto) {
			regles.add(this.reglaToMap(regla));
		}
		return new ResponseEntity<Object>(regles, HttpStatus.OK);
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json")	
	@Operation(
			method = "POST",
			summary = "Actualitzar qualsevol camp de la regla",
			description = "Servei Update per canviar els estats dels camps booleans 'activa' i 'presencial'.")
	public ResponseEntity<Object> update(HttpServletRequest request,
			@Parameter(name = "sia", description = "Codi SIA de la regla")
			@RequestParam(required = true) String sia,
			@Parameter(name = "activa", description = "Paràmetre opcional per activar o desactivar la regla. Si no s'especifica es canvia segons el valor que tingui actualment.") 
			@RequestParam(required = false) Boolean activa,
			@Parameter(name = "presencial", description = "Paràmetre per fixar el valor del filtre segons si l'anotació és prensencial, no presencial o no té valor.")
			@RequestParam(required = false) Boolean presencial){
		List<ReglaDto> regles = reglaService.findReglaByProcediment(sia);
		ReglaDto regla;
		ReglaPresencialEnumDto presencialEnum;
		if (regles == null || regles.isEmpty()) {
			return new ResponseEntity<Object>("La regla amb el codi " + sia + " no existeix", HttpStatus.CONFLICT);
		} else if (regles.size() > 1) {
			logger.warn("S'han trobat " + regles.size() + " regles pel codi de procediment " + sia
					+ ", es consultarà només la primera regla.");
		}
		regla = regles.get(0);
		if (activa == null) {
			activa = !regla.isActiva();
		}
		if(presencial == null) {
			 presencialEnum = null;
		}else {
			presencialEnum = presencial.booleanValue() ? ReglaPresencialEnumDto.SI : ReglaPresencialEnumDto.NO;
		}
		String response = "";
		try {
			// Actualiza valors de regla
			regla = reglaService.updateActivaPresencial(
					regla.getEntitatId(), 
					regla.getId(), 
					activa, 
					presencialEnum,
					sia);
			response = "Regla amb id " + regla.getId() + " actualitzada correctament.";
		} catch (Exception e) {
			String errMsg = "Error actualitzant la regla " + regla.getNom() + " amb id " + regla.getId() + " pel procediment " + regla.getProcedimentCodiFiltre() + ": " + e.getMessage();
			logger.error(errMsg, e);
			return new ResponseEntity<Object>(errMsg, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	/** Transforma una regla en un Map<String, Object> per facilitar la conversió a JSON.
	 * 
	 * @param regla
	 * @return
	 */
	private Map<String, Object> reglaToMap(ReglaDto regla) {
		Map<String, Object> r = new HashMap<>();
		if (regla != null) {
			r.put("id", regla.getId());
			r.put("nom", regla.getNom());
			Date data = regla.getCreatedDate();
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			r.put("data", dateFormat.format(data));
			r.put("entitat", regla.getEntitatNom());
			r.put("activa", regla.isActiva());
			if (regla.getPresencial() != null) {
				switch(regla.getPresencial()) {
				case NO:
					r.put("presencial", false);
					break;
				default:
					r.put("presencial", true);
					break;
				}
			}
			BackofficeDto backofficeDto = backofficeService.findById(regla.getEntitatId(), regla.getBackofficeDestiId());
			if (backofficeDto != null) {
				r.put("backofficeDesti", backofficeDto.getNom());
			}
		}
		return r;
	}

	private static final Logger logger = LoggerFactory.getLogger(ReglaRestController.class);

}
