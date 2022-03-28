/**
 * 
 */
package es.caib.distribucio.war.rest;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
//import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.Query;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.ContingutLogDetallsDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.dto.UsuariPermisDto;
import es.caib.distribucio.core.api.dto.dadesobertes.BustiaDadesObertesDto;
import es.caib.distribucio.core.api.dto.dadesobertes.LogsDadesObertesDto;
import es.caib.distribucio.core.api.dto.dadesobertes.UsuariDadesObertesDto;
import es.caib.distribucio.core.api.service.AlertaService;
import es.caib.distribucio.core.api.service.AplicacioService;
import es.caib.distribucio.core.api.service.BustiaService;
import es.caib.distribucio.core.api.service.ConfigService;
import es.caib.distribucio.core.api.service.ContingutService;
import es.caib.distribucio.core.api.service.EntitatService;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.core.api.service.UnitatOrganitzativaService;
import es.caib.distribucio.core.entity.BustiaDefaultEntity;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.HistoricBustiaEntity;
import es.caib.distribucio.core.entity.UnitatOrganitzativaEntity;
import es.caib.distribucio.core.helper.ConfigHelper;
import es.caib.distribucio.core.repository.HistoricBustiaRepository;
import es.caib.distribucio.core.repository.UnitatOrganitzativaRepository;
import es.caib.distribucio.core.service.BustiaServiceImpl;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.caib.usuari.DadesUsuariPluginJdbc;
import es.caib.distribucio.plugin.unitat.UnitatOrganitzativa;
import es.caib.distribucio.plugin.usuari.DadesUsuari;
import es.caib.distribucio.war.controller.BaseUserController;
import org.hibernate.Session;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controlador REST per a les dades obertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/proves")
public class DadesObertesController extends BaseUserController {


	@Autowired
	private BustiaService bustiaService;
	@Autowired
	private UnitatOrganitzativaService uoService;
	@Autowired
	private EntitatService entitatService;
	@Autowired
	private RegistreService registreService;
	@Autowired
	private ContingutService contingutService;
	@Autowired
	private AlertaService alertaService;
	@Autowired
	private AplicacioService aplicacioService;	
	@Autowired
	private ConfigService configService;	
	@Autowired
	private ConfigHelper configHelper;
	@Autowired
	private UnitatOrganitzativaRepository uoRepository;
	@Autowired
	private HistoricBustiaRepository histBustiaRepository;

	
	@RequestMapping(value = "/busties", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(
			value = "Petició de dades de bústies", 
			notes = "Retorna informació de bústies"
			)
	public List<BustiaDadesObertesDto> expedientsEntitatChartData(
			HttpServletRequest request, 
			
			@ApiParam(name="id", value="Id de la bústia")
			@RequestParam(required = false) Long id, 
			@ApiParam(name="uo", value="Codi DIR3 de l'unitat organitzativa")
			@RequestParam(required = false) String uo, 
			@ApiParam(name="uoSuperior", value="Codi DIR3 de l'unitat organitzativa")
			@RequestParam(required = false) String uoSuperior) {
		
		System.out.println("Consulta de dades de bústies: ");
		System.out.println(id + "--" + uo + "--" + uoSuperior);		
		
		List<BustiaDadesObertesDto> busties = bustiaService.findBustiesPerDadesObertes(
					id,
					uo,
					uoSuperior
				);		
		
		return busties;
	}
	
	
	@RequestMapping(value = "/usuaris", method = RequestMethod.GET)
	@ResponseBody
	public List<UsuariDadesObertesDto> apiDadesObertesUsuaris(
			HttpServletRequest request, 
			
			@RequestParam(required = false) String usuari, 
			@RequestParam(required = false) Long bustiaId, 
			@RequestParam(required = false) String uo, 
			@RequestParam(required = false) String uoSuperior, 
			@RequestParam(required = false, defaultValue = "true") boolean rol, 
			@RequestParam(required = false, defaultValue = "true") boolean permis
			) {
		
		System.out.println("Consulta de dades de usuaris-bústies: ");
		System.out.println(usuari + "--" + bustiaId + "--" + uo + "--" + uoSuperior
				 + "--" + rol + "--" + permis);	
		
		List<UsuariDadesObertesDto> usuariDOdto = bustiaService.findBustiesUsuarisPerDadesObertes(
				usuari, 
				bustiaId,
				uo,
				uoSuperior, 
				rol, 
				permis
			);	
		
		return usuariDOdto;		
	}
	
	
	@RequestMapping(value = "/logs", method = RequestMethod.GET)
	@ResponseBody
	public List<LogsDadesObertesDto> apiDadesObertesLogs (
			HttpServletRequest request, 
			
			@RequestParam(required = false) String dataInici, 
			@RequestParam(required = false) String dataFi, 
			@RequestParam(required = false) String tipus, 
			@RequestParam(required = false) String usuari, 
			@RequestParam(required = false) Long anotacioId, 
			@RequestParam(required = false) String anotacioEstat, 
			@RequestParam(required = false) Boolean errorEstat, 
			@RequestParam(required = false) Boolean pendent, 
			@RequestParam(required = false) Long bustiaOrigen, 
			@RequestParam(required = false) Long bustiaDesti, 
			@RequestParam(required = false) String uoOrigen, 
			@RequestParam(required = false) String uoSuperior, 
			@RequestParam(required = false) String uoDesti, 
			@RequestParam(required = false) String uoDestiSuperior
			) {

		if (dataInici == null) {			
			DateTime faUnMes = new DateTime().minusMonths (1); 
			dataInici = faUnMes.getYear() + "-" + faUnMes.getMonthOfYear() + "-" + faUnMes.getDayOfMonth();
			DateTime avui = new DateTime();
			dataFi = avui.getYear() + "-" + avui.getMonthOfYear() + "-" + avui.getDayOfMonth();
		}
		if (dataFi == null) {
			String[] dataIniciSplit = new String[3];
			dataIniciSplit = dataInici.split("-");
			int mes = Integer.parseInt(dataIniciSplit[1]) + 1;
			dataFi = dataIniciSplit[0] + "-" + mes + "-" + dataIniciSplit[2];
			
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Timestamp dataInicit = null;
		Timestamp dataFit = null;
		try {
			Date parseDate = dateFormat.parse(dataInici);
			Timestamp timestamp = new java.sql.Timestamp(parseDate.getTime());
			dataInicit = timestamp;
			Date parseDate2 = dateFormat.parse(dataFi);
			Timestamp timestamp2 = new java.sql.Timestamp(parseDate2.getTime());
			dataFit = timestamp2;
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
		System.out.println("Consulta de dades de logs: ");
		System.out.println(dataInicit + "--" + dataFit + "--" + tipus + "--" + usuari
				 + "--" + anotacioId + "--" + anotacioEstat + "--" + errorEstat + "--" 
				 + pendent + "--" + bustiaOrigen + "--" + bustiaDesti + "--" + uoOrigen
				 + "--" + uoSuperior + "--" + uoDesti + "--" + uoDestiSuperior);	
		
		List<LogsDadesObertesDto> listContingutLog = contingutService.findLogsDetallsPerData(
				dataInicit, dataFit, tipus, usuari, anotacioId, anotacioEstat, errorEstat, 
				pendent, bustiaOrigen, bustiaDesti, uoOrigen, uoSuperior, uoDesti, uoDestiSuperior);
		
		
		return listContingutLog;		
	}
	

	private static final Logger logger = LoggerFactory.getLogger(DadesObertesController.class);
}
