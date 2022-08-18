/**
 * 
 */
package es.caib.distribucio.war.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

import es.caib.distribucio.core.api.dto.BackofficeDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.ReglaDto;
import es.caib.distribucio.core.api.dto.ReglaTipusEnumDto;
import es.caib.distribucio.core.api.service.BackofficeService;
import es.caib.distribucio.core.api.service.EntitatService;
import es.caib.distribucio.core.api.service.ReglaService;
import es.caib.distribucio.war.controller.BaseUserController;

/**
 * Controlador REST per a les dades obertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/api/rest/regles")
@Api(value = "/api/rest/regles", description = "API REST de creació de regles per backoffices i codi SIA.")
public class ReglesController extends BaseUserController {


	@Autowired
	private ReglaService reglaService;
	@Autowired
	private BackofficeService backofficeService;
	@Autowired
	private EntitatService entitatService;

	/** Retorna la documentació de l'API. */
	@RequestMapping(value = {"", "/", "/apidoc"}, method = RequestMethod.GET)
	public String documentacio(HttpServletRequest request) {
		
		return "apidoc";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(
			value = "Alta de regla per codi SIA", 
			notes = "Dona d'alta una regla pel backoffice i codi SIA indicat per a l'entitat indicada. Per poder invocar aquest mètode "
					+ "és necessari una autenticació bàsica amb el rol DIS_REGLA."
			)
	@ResponseBody
	public ResponseEntity<Object> add(			
			HttpServletRequest request, 
			
			@ApiParam(name="entitat", value="Entitat en la qual crear la regla")
			@RequestParam(required = false) String entitat, 
			@ApiParam(name="sia", value="Codi SIA de la regla")
			@RequestParam(required = false) String sia,
			@ApiParam(name="backoffice", value="Codi Backoffice per la regla")
			@RequestParam(required = false) String backoffice) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if ( auth == null || !this.comprovarRol(auth, "DIS_REGLA") ) {
			return new ResponseEntity<Object>("És necessari estar autenticat i tenir el rol DIS_REGLA per crear regles.", HttpStatus.UNAUTHORIZED);
		}
		// Obtenim el nom de l'usuari que ha fet la petició
		Object usuariContext = auth.getPrincipal();
		String usuari;
		if (usuariContext instanceof UserDetails ) {
			usuari = ((UserDetails)usuariContext).getUsername();
		}else {
			usuari = usuariContext.toString();
		}
		
		// CREAR Regla AMB TOTES LES VALIDACIONS
		
		// Per posar la data a la descripció
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String dataAra = sdf.format(new Date());
		
		// Definim els valors que no hi son als paràmetres
		String nom = backoffice + " " + sia;
		String descripcio = "Creació de regla per part de " + usuari + " en data de " + dataAra + " pel backoffice amb codi " + backoffice + " i codi de procediment " + sia;
		ReglaTipusEnumDto tipus = ReglaTipusEnumDto.BACKOFFICE;		
		
		// Validar que la entitat existeix
		EntitatDto entitatDto = entitatService.findByCodiDir3(entitat);
		if (entitatDto == null ) {
			return new ResponseEntity<Object>("No s'ha trobat l'entitat " + entitat, HttpStatus.NOT_FOUND);
		}
		
		// Validar qeu es troba el backoffice
		BackofficeDto backofficeDto = backofficeService.findByCodi(entitatDto.getId(), backoffice);
		if (backofficeDto == null ) {
			return new ResponseEntity<Object>("No s'ha trobat el backoffice amb codi " + backoffice, HttpStatus.NOT_FOUND);
		}
				
		// Validar que no hi ha cap altra regla pel SIA per un backoffice diferent
		List<ReglaDto> reglesPerSia = reglaService.findReglaBackofficeByProcediment(sia);
		for (ReglaDto regla : reglesPerSia) {			
			if (backofficeDto.getId().compareTo(regla.getBackofficeDestiId()) != 0) {
				// KO Existeix una regla amb mateix codi SIA per un altre backoffice
				return new ResponseEntity<Object>("Ja existeix la regla amb id " + regla.getId() + " i nom \"" + regla.getNom() + "\" pel backoffice \"" + regla.getBackofficeDestiNom() + "\" a l'entitat \"" + regla.getEntitatNom() + "\"", HttpStatus.NOT_ACCEPTABLE);
			} else {
				// OK Regla existent pel mateix backoffie 
				return new ResponseEntity<Object>("Ja existeix la regla amb id " + regla.getId() + " i nom \"" + regla.getNom() + "\" per aquest backoffice i codi SIA", HttpStatus.OK);
			}
		}

		// Cream l'objecte de tipus ReglaDto
		ReglaDto novaReglaDto = new ReglaDto();
		novaReglaDto.setNom(nom);
		novaReglaDto.setDescripcio(descripcio);
		novaReglaDto.setTipus(tipus);
		novaReglaDto.setBackofficeDestiId(backofficeDto.getId());
		novaReglaDto.setBackofficeDestiNom(backoffice);
		novaReglaDto.setProcedimentCodiFiltre(sia);
		
		try {
			novaReglaDto = reglaService.create(entitatDto.getId(), novaReglaDto);
			
			Map<String, Object> regla = new HashMap<>();
			regla.put("nom", nom);
			regla.put("descripcio", descripcio);
			regla.put("tipus", tipus);
			BackofficeDto backofficeDesti = backofficeService.findById(novaReglaDto.getEntitatId(), novaReglaDto.getBackofficeDestiId());
			regla.put("backofficeDesti", backofficeDesti.getNom());
			regla.put("codiSia", sia);
			Date data = novaReglaDto.getCreatedDate();
			DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss");
			regla.put("data", dateFormat.format(data));
			
			
			String msg = "Regla amb id " + novaReglaDto.getId() + " \"" + novaReglaDto.getNom() + "\" creada correctament pel backoffice " + 
					backoffice + " pel codi SIA " + sia + " a l'entitat " + entitat;
			logger.debug(msg);
			return new ResponseEntity<Object>(regla, HttpStatus.OK);
		} catch (Exception e) {
			String errMsg = "Error creant la regla pel backoffice " + backoffice + " pel codi SIA " + sia + " a l'entitat " + entitat + ": " + e.getMessage(); 
			logger.error(errMsg, e);
			return new ResponseEntity<Object>(errMsg, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}	
	
	@RequestMapping(value = "/canviEstat", method = RequestMethod.POST)
	@ApiOperation(
			value = "Activar/Desactivar regla", 
			notes = "Depenent del seu estat, activa o desactiva una regla en concret."
			)
	@ResponseBody
	public ResponseEntity<String> canviEstat(
			HttpServletRequest request, 
			
			@ApiParam(name="codiSia", value="Codi SIA de la regla")
			@RequestParam(required = true) String codiSia, 
			@ApiParam(name="entitatCodi", value="Codi de l'entitat")
			@RequestParam(required = true) String entitatCodi/*,
			@ApiParam(name="backoffice", value="Codi Backoffice per la regla")
			@RequestParam(required = true) String backoffice*/){
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if ( auth == null || !this.comprovarRol(auth, "DIS_REGLA") ) {
			return new ResponseEntity<String>("És necessari estar autenticat i tenir el rol DIS_REGLA per canviar l'estat d'una regla.", HttpStatus.UNAUTHORIZED);
		}

		List<ReglaDto> reglaDto = reglaService.findReglaBackofficeByProcediment(codiSia);
		EntitatDto entitatDto = entitatService.findByCodiDir3(entitatCodi);
		boolean activa = false;
		String response = "";
		if (reglaDto == null) {
			return new ResponseEntity<String>("La regla amb el codi " + codiSia + " no existeix", HttpStatus.CONFLICT);
		}else if (reglaDto.get(0).isActiva()) {
			response = "La regla amb codi " + codiSia + " s'ha desactivat correctament.";
	
		}else {
			activa = true;
			response = "La regla amb codi " + codiSia + " s'ha activat correctament.";	
		}
		try {
			reglaService.updateActiva(entitatDto.getId(), reglaDto.get(0).getId(), activa);
		} catch(Exception e) {
			logger.error("error fent l'update de la regla. ", e);
			return new ResponseEntity<String>("Error intern en el servidor", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<String>(response, HttpStatus.OK);	
	}
	
	
	
	@RequestMapping(value = "/consultarRegla", method = RequestMethod.GET)
	@ApiOperation(
			value = "Consultar una regla", 
			notes = "Consulta si una regla existeix i si està activa"
			)
	@ResponseBody
	public ResponseEntity<Object> consultarRegla(
			HttpServletRequest request, 
			
			@ApiParam(name="codiSia", value="Codi SIA de la regla")
			@RequestParam(required = true) String codiSia) {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if ( auth == null || !this.comprovarRol(auth, "DIS_REGLA") ) {
			return new ResponseEntity<Object>("És necessari estar autenticat i tenir el rol DIS_REGLA per consultar regles", HttpStatus.UNAUTHORIZED);
		}
		List<ReglaDto> reglesDto = reglaService.findReglaBackofficeByProcediment(codiSia);
		Map<String,Map<String, Object>> regles = new HashMap<>();
		
		if (reglesDto == null || reglesDto.isEmpty()) {
			return new ResponseEntity<Object>("No s'ha trobat cap regla amb el codi SIA: " + codiSia, HttpStatus.NOT_FOUND);
		}
		
		for (ReglaDto regla : reglesDto) {
			Map<String, Object> r = new HashMap<>();
			r.put("id", regla.getId());
			r.put("nom", regla.getNom());
			Date data = regla.getCreatedDate();
			DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss");
			r.put("data", dateFormat.format(data));
			r.put("entitat", regla.getEntitatNom());
			r.put("activa", regla.isActiva());
			BackofficeDto backofficeDto = backofficeService.findById(regla.getEntitatId(), regla.getBackofficeDestiId());
			r.put("backofficeDesti", backofficeDto.getNom());			
			
			regles.put(codiSia, r);
		}
		return new ResponseEntity<Object>(regles, HttpStatus.OK);
//		ReglaDto reglaDto = reglaService.findReglaByCodiSia(codiSia);
//		if (reglaDto == null) {
//			return new ResponseEntity<Object>("La regla amb codi SIA: " + codiSia + " no existeix", HttpStatus.NOT_FOUND);
//			
//		}
//
//		Map<String, Object> regla = new HashMap<>();
//		regla.put("id", reglaDto.getId());
//		regla.put("nom", reglaDto.getNom());
//		Date data = reglaDto.getCreatedDate();
//		DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss");
//		regla.put("data", dateFormat.format(data));
//		regla.put("entitat", reglaDto.getEntitatNom());
//		regla.put("activa", reglaDto.isActiva());
//		BackofficeDto backofficeDto = backofficeService.findById(reglaDto.getEntitatId(), reglaDto.getBackofficeDestiId());
//		regla.put("backofficeDesti", backofficeDto.getNom());
//		
//		
//		return new ResponseEntity<Object>(regla, HttpStatus.OK);	
	}
	
	

	/** Comprova que l'usuari autenticat tingui el rol.
	 * 
	 * @param auth
	 * @param rol
	 * @return
	 */
	private boolean comprovarRol(Authentication auth, String rol) {
		boolean ret = false;
		if (auth != null) {
			for (GrantedAuthority a : auth.getAuthorities()) {
				if (a.getAuthority().equals(rol)) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}

	private static final Logger logger = LoggerFactory.getLogger(ReglesController.class);
}
