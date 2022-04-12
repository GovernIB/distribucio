/**
 * 
 */
package es.caib.distribucio.war.rest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.dto.ReglaDto;
import es.caib.distribucio.core.api.dto.ReglaTipusEnumDto;
import es.caib.distribucio.core.api.service.ReglaService;
import es.caib.distribucio.core.entity.BackofficeEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.ReglaEntity;
import es.caib.distribucio.core.repository.BackofficeRepository;
import es.caib.distribucio.core.repository.EntitatRepository;
import es.caib.distribucio.core.repository.ReglaRepository;
import es.caib.distribucio.war.controller.BaseUserController;
import lombok.extern.slf4j.Slf4j;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controlador REST per a les dades obertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@Slf4j
@RequestMapping("/api/regles")
public class ReglesController extends BaseUserController {


	@Autowired
	private ReglaService reglaService;
	@Autowired
	private ReglaRepository reglaRepository;
	@Autowired
	private BackofficeRepository backofficeRepository;
	@Autowired
	private EntitatRepository entitatRepository;

	
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(
			value = "Petició de dades de bústies", 
			notes = "Retorna informació de bústies"
			)
	public ResponseEntity<String> add(			
			HttpServletRequest request, 
			
			@ApiParam(name="entitat", value="Entitat que crea la regla")
			@RequestParam(required = false) String entitat, 
			@ApiParam(name="sia", value="Codi SIA")
			@RequestParam(required = false) String sia,
			@ApiParam(name="backoffice", value="Codi Backoffice")
			@RequestParam(required = false) String backoffice) {
		
		// Obtenim el nom de l'usuari que ha fet la petició
		Object usuariContext = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String usuari;
		if (usuariContext instanceof UserDetails ) {
			usuari = ((UserDetails)usuariContext).getUsername();
		}else {
			usuari = usuariContext.toString();
		}
		
		// CREAR Regla AMB TOTES LES VALIDACIONS
		
		// Per posar la data a la descripció
		DateTimeFormatter dateTimeFormater = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		LocalDateTime localDateTime = LocalDateTime.now();
		String dataAra = dateTimeFormater.format(localDateTime);
		
		// Definim els valors que no hi son als paràmetres
		String nom = "Ripea SIA " + sia;
		String descripcio = "Creació de regla per part de " + usuari + " en data de " + dataAra + " pel backoffice amb codi " + backoffice;
		ReglaTipusEnumDto tipus = ReglaTipusEnumDto.BACKOFFICE;		
		
		// Validar que la entitat existeix
		EntitatEntity entitatEntity = entitatRepository.findByCodi(entitat);
		if (entitatEntity == null ) {
			return new ResponseEntity<String>("No s'ha trobat l'entitat " + entitat, HttpStatus.NOT_FOUND);
		}
		
		// Validar qeu es troba el backoffice
		BackofficeEntity backofficeEntity = backofficeRepository.findByEntitatAndCodi(entitatEntity, backoffice);
		if (backofficeEntity == null ) {
			return new ResponseEntity<String>("No s'ha trobat el backoffice amb codi " + backoffice, HttpStatus.NOT_FOUND);
		}
				
		// Validar que no hi ha cap altra regla pel SIA per un backoffice diferent
		List<ReglaEntity> reglesPerSia = reglaRepository.findReglaBackofficeByCodiProcediment(sia);
		for (ReglaEntity regla : reglesPerSia) {
			System.out.println("REGLA: " + regla.getBackofficeDesti().getCodi() + "=>" + regla.getProcedimentCodiFiltre());
			if (regla.getBackofficeDesti().getCodi().equals(backoffice) && regla.getProcedimentCodiFiltre().equals(sia)  ) {
				return new ResponseEntity<String>("Ja existeix una regla per aquest codi SIA " + backoffice, HttpStatus.OK);
			}
		}


		// Cream l'objecte de tipus ReglaDto
		ReglaDto novaReglaDto = new ReglaDto();
		novaReglaDto.setNom(nom);
		novaReglaDto.setDescripcio(descripcio);
		novaReglaDto.setTipus(tipus);
		novaReglaDto.setBackofficeDestiId(backofficeEntity.getId());
		novaReglaDto.setBackofficeDestiNom(backoffice);
		novaReglaDto.setProcedimentCodiFiltre(sia);
		
		try {
			reglaService.create(entitatEntity.getId(), novaReglaDto);
			return new ResponseEntity<String>("Regla creada correctamet " + backoffice, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>("Error inesperat: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}	

	private static final Logger logger = LoggerFactory.getLogger(ReglesController.class);
}
