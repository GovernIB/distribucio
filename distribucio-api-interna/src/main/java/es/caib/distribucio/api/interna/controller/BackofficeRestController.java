package es.caib.distribucio.api.interna.controller;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.distribucio.api.interna.model.InfoCanviEstat;
import es.caib.distribucio.logic.intf.dto.LimitCanviEstatDto;
import es.caib.distribucio.logic.intf.exception.SistemaExternException;
import es.caib.distribucio.logic.intf.service.ConfigService;
import es.caib.distribucio.logic.intf.service.LimitCanviEstatService;
import es.caib.distribucio.logic.intf.service.MonitorIntegracioService;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.logic.intf.service.ws.backoffice.BackofficeIntegracioWsService;
import es.caib.distribucio.logic.intf.service.ws.backoffice.Estat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador pel servei REST de backoffice.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@SecurityRequirement(name = "basicScheme")
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
	@Autowired
	private LimitCanviEstatService limitCanviEstatService;
	@Autowired
	private MonitorIntegracioService monitorIntegracioService;
	@Autowired
	private ConfigService configService;

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
			String clauAcces) throws SistemaExternException {
		if (!hasRole())
			return responseUnautorized();
		try {
			AnotacioRegistreId id = new AnotacioRegistreId();
			id.setIndetificador(indetificador);
			id.setClauAcces(clauAcces);
			AnotacioRegistreEntrada anotacio = backofficeIntegracioWsService.consulta(id);
			
			// Canvi d'estat de "Comunicada al backoffice" a "Rebuda pel backoffice"
			backofficeIntegracioWsService.canviEstatComunicadaARebuda(
					id,
					"Canvi a nivell de consulta des de l'estat comunicada a rebuda pel backoffice");
				return new ResponseEntity<Object>(anotacio, HttpStatus.OK);
		} catch(Exception e) {
			String errMsg = "Error no controlat consultant l'anotació amb id " + indetificador + " i clau " + clauAcces + ": " + e.getMessage();
			return new ResponseEntity<Object>(errMsg, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

    public static boolean matchesNow(String cron) {
        try {
            CronExpression expression = CronExpression.parse(cron);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nextExecution = expression.next(now.minusSeconds(1));

            if (nextExecution == null) {
                return false;
            }

            // Si la próxima ejecución ocurre dentro del segundo actual
            Duration diff = Duration.between(now, nextExecution).abs();
            return diff.getSeconds() == 0;
        } catch (Exception e) {
            return false; // Si el cron es inválido
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
			@io.swagger.v3.oas.annotations.parameters.RequestBody()
			@RequestBody final InfoCanviEstat infoCanviEstat
			) throws SistemaExternException {
		if (!hasRole())
			return responseUnautorized();
		if (infoCanviEstat == null) {
			return new ResponseEntity<Object>("No s'ha rebut en el cos de la petició la informació pel canvi d'estat", HttpStatus.BAD_REQUEST);
		}
		if (infoCanviEstat.getId() == null 
				|| infoCanviEstat.getId().getIndetificador() == null 
				|| infoCanviEstat.getId().getClauAcces() == null) {
			return new ResponseEntity<Object>("Falta informació de l'identificador de l'anotació.", HttpStatus.BAD_REQUEST);		
		}
		if (infoCanviEstat.getEstat() == null) {
			return new ResponseEntity<Object>("No s'ha informat el nou estat.", HttpStatus.BAD_REQUEST);		
		}
		Estat estat = null;
		try {
			estat = Estat.valueOf(infoCanviEstat.getEstat().toString());
		} catch(Exception e) {
			return new ResponseEntity<Object>("L'estat " + infoCanviEstat.getEstat() + " no és un estat vàlid.", HttpStatus.BAD_REQUEST);		
		}

        String horariLaboral = this.getConfigOrDefault("es.caib.distribucio.horari.laboral", "* * 7-16 * * MON-FRI");

        boolean inHorariLaboral = matchesNow(horariLaboral);
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        LimitCanviEstatDto limitCanviEstatDto = limitCanviEstatService.findByUsuariCodi(user);
        if (limitCanviEstatDto == null) limitCanviEstatDto = new LimitCanviEstatDto();
        if (limitCanviEstatDto.getLimitMinutLaboral() == null) limitCanviEstatDto.setLimitMinutLaboral(Integer.valueOf(this.getConfigOrDefault("es.caib.distribucio.limit.minut.laboral", "4")));
        if (limitCanviEstatDto.getLimitMinutNoLaboral() == null) limitCanviEstatDto.setLimitMinutNoLaboral(Integer.valueOf(this.getConfigOrDefault("es.caib.distribucio.limit.minut.no.laboral", "8")));
        if (limitCanviEstatDto.getLimitDiaLaboral() == null) limitCanviEstatDto.setLimitDiaLaboral(Integer.valueOf(this.getConfigOrDefault("es.caib.distribucio.limit.dia.laboral", "8000")));
        if (limitCanviEstatDto.getLimitDiaNoLaboral() == null) limitCanviEstatDto.setLimitDiaNoLaboral(Integer.valueOf(this.getConfigOrDefault("es.caib.distribucio.limit.dia.no.laboral", "1000")));

        Integer limitMinut = inHorariLaboral ?limitCanviEstatDto.getLimitMinutLaboral() : limitCanviEstatDto.getLimitMinutNoLaboral();
        Integer limitDia = inHorariLaboral ?limitCanviEstatDto.getLimitDiaLaboral() : limitCanviEstatDto.getLimitDiaNoLaboral();
        Map<String, Integer> count = monitorIntegracioService.countCanvisEstatFromUser(user);

        if ( limitMinut < count.get("minut") ) {
            return new ResponseEntity<Object>("S'ha superat el nombre máxim d'enviaments per minut ("+limitMinut+")", HttpStatus.BAD_REQUEST);
        }
        if ( limitDia < count.get("dia") ) {
            return new ResponseEntity<Object>("S'ha superat el nombre máxim d'enviaments per dia ("+limitDia+")", HttpStatus.BAD_REQUEST);
        }

		try {
			AnotacioRegistreId id = new AnotacioRegistreId();
			id.setIndetificador(infoCanviEstat.getId().getIndetificador());
			id.setClauAcces(infoCanviEstat.getId().getClauAcces());
			backofficeIntegracioWsService.canviEstat(
					id,
					estat,
					infoCanviEstat.getObservacions());
			return new ResponseEntity<>(HttpStatus.OK);
		} catch(Exception e) {
			String errMsg = "Error no controlat canviant l'estat a l'anotació amb informació d'entrada infoCanviEstat =  " + infoCanviEstat + ": " + e.getMessage();
			return new ResponseEntity<Object>(errMsg, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(
	        value= "/llistar",
	        method = RequestMethod.GET,
	        produces = "application/json")
	@Operation(
			summary = "Consulta el llistat d'anotacions per número i data de registre",
					description = "Retorna en un llistat les dades de les anotacions del número i data de registre especificats")
	public ResponseEntity<Object> llistarAnotacions(
	        HttpServletRequest request,
	        @Parameter(name = "indetificador", description = "Identificador de la anotació de registre, sol ser el número de registre", required = true)
			String indetificador,
			@Parameter(name = "dataRegistre", description = "La data de registre en format dd/MM/yyyy HH:mm:ss", required = true)
			String dataRegistreStr) throws SistemaExternException {

	    if (!hasRole())
	        return responseUnautorized();
	    try {
	    	
	    	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date dataRegistre = formatter.parse(dataRegistreStr);
            
	        // Lógica para consultar la anotación usando los parámetros proporcionados en el objeto consultaParams
	        List<AnotacioRegistreEntrada> anotacions = backofficeIntegracioWsService.llistar(
	        		indetificador, 
	        		dataRegistre);
	        return new ResponseEntity<Object>(anotacions, HttpStatus.OK);
	    } catch(Exception e) {
	        String errMsg = "Error no controlat al consultar les anotacions del registre " + indetificador + " i la data " + dataRegistreStr + ": " + e.getMessage();
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

    private String getConfigOrDefault(String key, String defaultValue) {
    	
    	String value = configService.getConfig(key);
    	if (value == null) {
    		value = defaultValue;
    	}
		return value;
	}
}
