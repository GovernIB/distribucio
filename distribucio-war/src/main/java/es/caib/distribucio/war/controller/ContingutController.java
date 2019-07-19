/**
 * 
 */
package es.caib.distribucio.war.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;

import es.caib.distribucio.core.api.dto.ArxiuDetallDto;
import es.caib.distribucio.core.api.dto.ContingutComentariDto;
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.ContingutLogDetallsDto;
import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.LogObjecteTipusEnumDto;
import es.caib.distribucio.core.api.dto.LogTipusEnumDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDto;
import es.caib.distribucio.core.api.dto.RegistreDto;
import es.caib.distribucio.core.api.dto.UsuariDto;
import es.caib.distribucio.core.api.service.AplicacioService;
import es.caib.distribucio.core.api.service.ContingutService;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.war.helper.EnumHelper;
import es.caib.distribucio.war.helper.MissatgesHelper;
import es.caib.distribucio.war.helper.SessioHelper;
import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;

/**
 * Controlador per a la gestió de contenidors i mètodes compartits entre
 * diferents tipus de contingut.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class ContingutController extends BaseUserController {

	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ContingutService contingutService;
	@Autowired
	private RegistreService registreService;







	@RequestMapping(value = "/contingut/{bustiaId}/registre/{registreId}/registreJustificant", method = RequestMethod.GET)
	public String registreJustific(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			Model model) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			RegistreAnnexDto justificant = registreService.getRegistreJustificant(entitatActual.getId(), bustiaId, registreId);
			model.addAttribute(
					"justificant",
					justificant);
			model.addAttribute("bustiaId",
					bustiaId);			
			
		} catch(Exception ex) {
			logger.error("Error recuperant informació del justificant", ex);
			model.addAttribute("missatgeError", ex.getMessage());
			return "ajaxErrorPage";
		}
		return "registreJustificant";
	}

	@RequestMapping(value = "/contingut/{bustiaId}/registre/{registreId}/arxiuInfo", method = RequestMethod.GET)
	public String arxiuInfo(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			Model model) {
		try {
			getEntitatActualComprovantPermisos(request);
			ArxiuDetallDto arxiuDetall = registreService.getArxiuDetall(registreId);
			model.addAttribute(
					"arxiuDetall",
					arxiuDetall);
		} catch(Exception ex) {
			logger.error("Error recuperant informació de l'arxiu", ex);
			model.addAttribute("missatgeError", ex.getMessage());
			return "ajaxErrorPage";
		}
		return "arxiuInfo";
	}

	@RequestMapping(value = "/contingut/{bustiaId}/registre/{registreId}/llegir", method = RequestMethod.GET)
	public String registreMarcarLlegida(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		registreService.marcarLlegida(
				entitatActual.getId(),
				bustiaId,
				registreId);
		SessioHelper.marcatLlegit(request);		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../",
				"contingut.registre.missatge.anotacio.marcada");
	}

	@RequestMapping(value = "/contingut/{bustiaId}/registre/{registreId}/annex/{annexId}/arxiu/{tipus}", method = RequestMethod.GET)
	public String descarregar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			@PathVariable Long annexId,
			@PathVariable String tipus) throws IOException {
		try {
			FitxerDto fitxer = registreService.getAnnexFitxer(annexId);
			writeFileToResponse(
					fitxer.getNom(),
					fitxer.getContingut(),
					response);
		} catch (Exception ex) {
			logger.error("Error descarregant el document", ex);
			return getModalControllerReturnValueError(
					request,
					"/contingut/" + bustiaId + "/registre/" + registreId,
					"contingut.controller.document.descarregar.error");
		}
		return null;
	}

	@RequestMapping(value = "/contingut/{bustiaId}/registre/{registreId}/justificant", method = RequestMethod.GET)
	public String descarregarJustificant(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId) throws IOException {
		FitxerDto fitxer = registreService.getJustificant(registreId);
		writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);
		return null;
	}

	@RequestMapping(value = "/contingut/{bustiaId}/registre/{registreId}/annex/{annexId}/firma/{firmaIndex}", method = RequestMethod.GET)
	public String descarregarFirma(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			@PathVariable Long annexId,
			@PathVariable int firmaIndex) throws IOException {
		FitxerDto fitxer = registreService.getAnnexFirmaFitxer(annexId,
				firmaIndex);
		writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);
		return null;
	}

	@RequestMapping(value = "/contingut/{bustiaId}/registre/{registreId}/reintentar", method = RequestMethod.GET)
	public String reintentar(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long registreId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		boolean processatOk = registreService.reintentarProcessamentUser(
				entitatActual.getId(),
				bustiaId,
				registreId);
		if (processatOk) {
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../../" + bustiaId,
					"contingut.admin.controller.registre.reintentat.ok");
		} else {
			MissatgesHelper.error(
					request,
					getMessage(
							request, 
							"contingut.admin.controller.registre.reintentat.error",
							null));
			return "redirect:../" + registreId;
		}
	}

	@RequestMapping(value = "/contingut/{bustiaId}/log", method = RequestMethod.GET)
	public String log(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"contingut",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						bustiaId,
						true,
						false));

		List<ContingutLogDetallsDto> logsDetall = contingutService.findLogsDetallsPerContingutUser(
				entitatActual.getId(),
				bustiaId); 
		model.addAttribute(
				"logsDetall",
				logsDetall);
		// Recupera la informació
		ContingutDto contingut = contingutService.findAmbIdUser(
						entitatActual.getId(),
						bustiaId,
						true,
						false);
		RegistreDto registre = registreService.findOne(
				entitatActual.getId(), 
				contingut.getPare().getId(), 
				bustiaId);
		model.addAttribute(
				"logsResum", 
				this.buildLogsResum(request, registre, logsDetall));
		
		model.addAttribute(
				"logs",
				contingutService.findLogsPerContingutUser(
						entitatActual.getId(),
						bustiaId));
		model.addAttribute(
				"moviments",
				contingutService.findMovimentsPerContingutUser(
						entitatActual.getId(),
						bustiaId));
		model.addAttribute(
				"logTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						LogTipusEnumDto.class,
						"log.tipus.enum."));
		model.addAttribute(
				"logObjecteTipusEnumOptions",
				EnumHelper.getOptionsForEnum(
						LogObjecteTipusEnumDto.class,
						"log.objecte.tipus.enum."));
		return "contingutLog";
	}

	/** Construeix una llista on per cada fila:
	 * llista[i][0] És el log amb la informació
	 * llista[i][1] És el text descriptiu
	 * 
	 * @param request
	 * @param registre
	 * @param logsDetall
	 * @return Una llista on en cada posició hi ha un array d'objectes amb el log i el text descriptiu construït a partir del log.
	 */
	private List<Object[]> buildLogsResum(
			HttpServletRequest request, 
			RegistreDto registre,
			List<ContingutLogDetallsDto> logsDetall) {
		List<Object[]> ret = new ArrayList<Object[]>();
		Object[] item;
		for (ContingutLogDetallsDto log : logsDetall) {
			item = new Object[2];
			item[0] = log;
			item[1] = this.getLogText(request, log);
			ret.add(item);
		}
		return ret;
	}

	@RequestMapping(value = "/contingut/{bustiaId}/log/{contingutLogId}/detalls", method = RequestMethod.GET)
	@ResponseBody
	public ContingutLogDetallsDto logDetalls(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			@PathVariable Long contingutLogId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		return contingutService.findLogDetallsPerContingutUser(
				entitatActual.getId(),
				bustiaId,
				contingutLogId);
	}
	
	/** Descarrega un informe del resum de moviments a partir d'una plantilla 
	 * @throws Exception */
	@RequestMapping(value = "/contingut/{contingutId}/log/informe", method = RequestMethod.GET)
	public String informe(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long contingutId,
			Model model) throws Exception {
		
		String ret = null;
		try {
			// Data de l'informe
			Date data = new Date();
			
			// Recupera la informació
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			ContingutDto contingut = contingutService.findAmbIdUser(
							entitatActual.getId(),
							contingutId,
							true,
							false);
			RegistreDto registre = registreService.findOne(
					entitatActual.getId(), 
					contingut.getPare().getId(), 
					contingutId);
			List<ContingutLogDetallsDto> logsResum = contingutService.findLogsDetallsPerContingutUser(
							entitatActual.getId(),
							contingutId);
			// Genera el report
			byte[] informeContingut = this.generaInformeTracabilitat(
					request,
					data,
					contingut,
					registre, 
					logsResum);
			
			String informeNom = this.getMessage(request, "contingut.log.informe.nom.template", new Object[] {
					registre.getNumero(),
					new SimpleDateFormat("yyyyMMddHHmm").format(data)
			});
			
			// Descarrega el report escrivint-lo a la resposta
			writeFileToResponse(
					informeNom,
					informeContingut,
					response);	
		} catch (Exception e) {
			logger.error("Error generant l'informe de tracabilitat pel contingut amb id " + contingutId, e);
			ret =  getAjaxControllerReturnValueError(
					request,
					"redirect:" + request.getHeader("referer"),
					"contingut.log.informe.error",
					new Object[] {e.getMessage()});
		}
		return ret;
	}

	/** Retorna el contingut PDF de la generació del report. Construeix els textos i els passa a la plantilla.
	 * @param request 
	 * @param data 
	 * @param contingut 
	 * @param registre 
	 * @param logsResum 
	 * 
	 * @return
	 */
	private byte[] generaInformeTracabilitat(
			HttpServletRequest request, 
			Date data, 
			ContingutDto contingut, 
			RegistreDto registre, 
			List<ContingutLogDetallsDto> logsResum) throws Exception{
		Locale locale = new RequestContext(request).getLocale();


		// 1) Load ODT file and set Velocity template engine and cache it to the registry					
    	InputStream in= this.getClass().getResourceAsStream("/plantilles/informe_" + locale.getLanguage() + ".odt");
    	IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in,TemplateEngineKind.Velocity);

    	// 2) Create Java model context 
    	IContext context = report.createContext();
    	context.put("contingut", contingut);
    	context.put("registre", registre);
    	List<String> textList = new ArrayList<String>();
    	String text;
    	for (ContingutLogDetallsDto log : logsResum) {
    		text = this.getLogText(request, log);
    		// posa la 1a lletra en minúscula
    		text = decapitalize(text);
    		// Afegeix "A data XXX "
    		text = this.getMessage(
    				request, 
    				"contingut.log.informe.text", 
    				new Object[] {
    						log.getCreatedDateAmbFormat(),
    						text});
    		textList.add(text);
    	}
    	context.put("text_list", textList);
    	context.put("data", new SimpleDateFormat("EEEE dd 'de' MMMM 'de' yyyy", locale).format(data));

    	// 3) Set PDF as format converter
    	Options options = Options.getTo(ConverterTypeTo.PDF);

    	// 3) Generate report by merging Java model with the ODT and convert it to PDF
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	report.convert(context, options, bos);
    	return bos.toByteArray();
    }
	
	public static String decapitalize(String string) {
	    if (string == null || string.length() == 0) {
	        return string;
	    }
	    char c[] = string.toCharArray();
	    c[0] = Character.toLowerCase(c[0]);
	    return new String(c);
	}

	/** Mètode per crear la descripció per a una línia del log
	 * @param request 
	 * 
	 * @param log
	 * @return
	 */
	private String getLogText(HttpServletRequest request, ContingutLogDetallsDto log) {
		
		StringBuilder sb = new StringBuilder();
		String usuari = log.getCreatedBy() != null ? log.getCreatedBy().getCodi() + " - " + log.getCreatedBy().getNom() : "-";
		switch(log.getTipus()) {
		case CREACIO:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.creacio", new Object[] {log.getParam1()}));
			break;
		case MOVIMENT:
		case REENVIAMENT:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.moure"));
			if (log.getContenidorMoviment() != null) {
				if (log.getContingutMoviment().getOrigen() != null) {
					sb.append(" ").append(this.getMessage(request, "contingut.log.resum.msg.deLaBustia")).append(" \"");
					sb.append(log.getContenidorMoviment().getOrigen().getNom()).append("\"");
				}
				if (log.getContingutMoviment().getDesti() != null) {
					sb.append(" ").append(this.getMessage(request, "contingut.log.resum.msg.aLaBustia")).append(" \"");
					sb.append(log.getContenidorMoviment().getDesti().getNom()).append("\"");
				}
			}
			break;
		case ENVIAMENT_EMAIL:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.enviamentEmail", 
								new Object[] {usuari,
											 log.getParam2()}));
			break;
		case MARCAMENT_PROCESSAT:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.marcamentProcessat", new Object[] {usuari}));
			break;
		case DISTRIBUCIO:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.distribucio"));
			break;
		default:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.accio")).append(": \"");
			sb.append(this.getMessage(request, "log.tipus.enum." + log.getTipus().name())).append("\"");
			if (log.getParam1() != null)
				sb.append(" param1: \"").append(log.getParam1()).append("\"");
			if (log.getParam2() != null)
				sb.append(" param2: \"").append(log.getParam2()).append("\"");
			break;
		}
		return sb.toString();
	}

	@RequestMapping(value = "/contingut/{bustiaId}/comentaris", method = RequestMethod.GET)
	public String comentaris(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		model.addAttribute(
				"contingut",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						bustiaId,
						true,
						false));
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		model.addAttribute(
				"usuariActual",
				usuariActual);
		return "contingutComentaris";
	}

	@RequestMapping(value = "/contingut/{registreId}/comentaris/publicar", method = RequestMethod.POST)
	@ResponseBody
	public List<ContingutComentariDto> publicarComentari(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@RequestParam String text,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		if (text != null && !text.isEmpty()) {
			contingutService.publicarComentariPerContingut(entitatActual.getId(), registreId, text);
		}
		return contingutService.findComentarisPerContingut(
				entitatActual.getId(), 
				registreId);
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}

	private static final Logger logger = LoggerFactory.getLogger(ContingutController.class);

}
