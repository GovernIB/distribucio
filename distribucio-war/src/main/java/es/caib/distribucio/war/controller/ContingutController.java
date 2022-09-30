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
import es.caib.distribucio.core.api.dto.ReglaTipusEnumDto;
import es.caib.distribucio.core.api.dto.RespostaPublicacioComentariDto;
import es.caib.distribucio.core.api.dto.UsuariDto;
import es.caib.distribucio.core.api.service.AplicacioService;
import es.caib.distribucio.core.api.service.ContingutService;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.war.helper.EnumHelper;
import es.caib.distribucio.war.helper.MissatgesHelper;
import es.caib.distribucio.war.helper.RolHelper;
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

	@RequestMapping(value = "/contingut/registre/{registreId}/registreJustificant", method = RequestMethod.GET)
	public String registreJustific(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			Model model) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
			RegistreAnnexDto justificant = registreService.getRegistreJustificant(entitatActual.getId(), registreId, isVistaMoviments);
			model.addAttribute(
					"justificant",
					justificant);
			
		} catch(Exception ex) {
			logger.error("Error recuperant informació del justificant", ex);
			model.addAttribute("missatgeError", ex.getMessage());
			return "ajaxErrorPage";
		}
		return "registreJustificant";
	}

	@RequestMapping(value = "/contingut/registre/{registreId}/arxiuInfo", method = RequestMethod.GET)
	public String arxiuInfo(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			Model model) {
		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
			RegistreDto registre = registreService.findOne(entitatActual.getId(), registreId, isVistaMoviments);
			model.addAttribute(
					"registre", 
					registre);
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

	@RequestMapping(value = "/contingut/registre/{registreId}/llegir", method = RequestMethod.GET)
	public String registreMarcarLlegida(
			HttpServletRequest request,
			@PathVariable Long registreId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		registreService.marcarLlegida(
				entitatActual.getId(),
				registreId);
		SessioHelper.marcatLlegit(request);		
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../",
				"contingut.registre.missatge.anotacio.marcada");
	}
	
	@RequestMapping(value = "/contingut/registre/{registreId}/annex/{annexId}/arxiu/content/{tipus}", method = RequestMethod.GET)
	@ResponseBody
	public FitxerDto descarregarBase64(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long registreId,
			@PathVariable Long annexId,
			@PathVariable String tipus) throws Exception {
		FitxerDto fitxer = new FitxerDto();
		try {
			fitxer = registreService.getAnnexFitxer(annexId, true);
		} catch (Exception ex) {
			fitxer.setError(true);
			fitxer.setErrorMsg(ex.getMessage());
			fitxer.setNom("");
		}
		return fitxer;
	}

	@RequestMapping(value = {	"/contingut/{contingutId}/registre/{registreId}/annex/{annexId}/arxiu/{tipus}", // URL antiga
								"/contingut/registre/{registreId}/annex/{annexId}/arxiu/{tipus}"},
								method = RequestMethod.GET)
	public String descarregar(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long registreId,
			@PathVariable Long annexId,
			@PathVariable String tipus) throws IOException {
		try {
			
			boolean ambVersioImprimible;
			switch (tipus) {
				case "DOCUMENT_ORIGINAL":
					ambVersioImprimible = false;
					break;
				case "DOCUMENT": 
					ambVersioImprimible = true;
					break;				
				default:
					ambVersioImprimible = true;
					break;				
			}
			
			FitxerDto fitxer = registreService.getAnnexFitxer(annexId, ambVersioImprimible);
			writeFileToResponse(
					fitxer.getNom(),
					fitxer.getContingut(),
					response);
		} catch (Exception ex) {
			String errMsg = getMessage(
					request, 
					"contingut.controller.document.descarregar.error",
					new Object[] {ex.getMessage()});
			logger.error(errMsg, ex);
			MissatgesHelper.error(
					request, 
					errMsg);
			return "redirect:" + request.getHeader("referer");
		}
		return null;
	}
	
	@RequestMapping(value = {	"/contingut/{contingutId}/registre/{registreId}/justificant", // URL antiga
								"/contingut/registre/{registreId}/justificant"}, 
					method = RequestMethod.GET)
	public String descarregarJustificant(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long registreId) throws IOException {
		
		try {
			FitxerDto fitxer = registreService.getJustificant(registreId);
			writeFileToResponse(
					fitxer.getNom(),
					fitxer.getContingut(),
					response);
		} catch (Exception ex) {
			String errMsg = getMessage(
					request, 
					"contingut.controller.document.descarregar.error",
					new Object[] {ex.getMessage()});
			logger.error(errMsg, ex);
			MissatgesHelper.error(
					request, 
					errMsg);
			return "redirect:" + request.getHeader("referer");
		}
		return null;
	}

	@RequestMapping(value = "/contingut/registre/{registreId}/annex/{annexId}/firma/{firmaIndex}", method = RequestMethod.GET)
	public String descarregarFirma(
			HttpServletRequest request,
			HttpServletResponse response,
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
	
	/** Recupera el contingut de tots els annexos i crea un ZIP per descarregar */
	@RequestMapping(value = "/contingut/registre/{registreId}/descarregarZip", method = RequestMethod.GET)
	public String descarregarZipDocumentacio(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long registreId) throws IOException {
		String rolActual = RolHelper.getRolActual(request);
		try {
			getEntitatActualComprovantPermisUsuari(request);
			FitxerDto fitxer = registreService.getZipDocumentacio(registreId, rolActual);
			writeFileToResponse(
					fitxer.getNom(),
					fitxer.getContingut(),
					response);
		} catch (Exception ex) {
			String errMsg = getMessage(
					request, 
					"contingut.controller.document.descarregar.error",
					new Object[] {ex.getMessage()});
			logger.error(errMsg, ex);
			MissatgesHelper.error(
					request, 
					errMsg);
			return "redirect:" + request.getHeader("referer");
		}
		return null;
	}
	
	/** Mètode públic per recuperar el contingut de tots els annexos i crea un ZIP per descarregar */
	@RequestMapping(value = "/public/{key}/descarregarZipPublic", method = RequestMethod.GET)
	public String descarregarZipDocumentacioPublic(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable String key) throws IOException {
		try {
			key = key.replace("%2F", "/");
			key = key + "==";
			String clau = registreService.obtenirRegistreIdDesencriptat(key);
			Long registreId = Long.valueOf(clau);
			FitxerDto fitxer = registreService.getZipDocumentacio(registreId, null);
			writeFileToResponse(
					fitxer.getNom(),
					fitxer.getContingut(),
					response);
		} catch (Exception ex) {
			String errMsg = getMessage(
					request, 
					"contingut.controller.document.descarregar.error",
					new Object[] {ex.getMessage()});
			logger.error(errMsg, ex);
		}
		return null;
	}

	@RequestMapping(value = "/contingut/{registreId}/log/moviments", method = RequestMethod.GET)
	public String logMoviments(
			HttpServletRequest request,
			@PathVariable Long registreId,
			Model model) {
		return getLog(request, registreId, model, true);
	}
	
	@RequestMapping(value = "/contingut/{registreId}/log", method = RequestMethod.GET)
	public String log(
			HttpServletRequest request,
			@PathVariable Long registreId,
			Model model) {
		return getLog(request, registreId, model, false);
	}
	
	private String getLog(
			HttpServletRequest request,
			Long registreId,
			Model model,
			boolean isVistaMoviments) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		String rolActual = RolHelper.getRolActual(request);
		
		model.addAttribute(
				"isPanelUser",
				true);
		
		model.addAttribute(
				"contingut",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						registreId,
						true,
						false, 
						rolActual,
						isVistaMoviments));

		List<ContingutLogDetallsDto> logsDetall = contingutService.findLogsDetallsPerContingutUser(
				entitatActual.getId(),
				registreId); 
		model.addAttribute(
				"logsDetall",
				logsDetall);
		// Recupera la informació
		RegistreDto registre = registreService.findOne(
				entitatActual.getId(), 
				registreId,
				isVistaMoviments);
		model.addAttribute(
				"logsResum", 
				this.buildLogsResum(request, registre, logsDetall));
		
		model.addAttribute(
				"logs",
				contingutService.findLogsPerContingutUser(
						entitatActual.getId(),
						registreId));
		model.addAttribute(
				"moviments",
				contingutService.findMovimentsPerContingutUser(
						entitatActual.getId(),
						registreId));
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
			item[1] = this.getLogText(request, log, registre);
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
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
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
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			Model model) throws Exception {
		String rolActual = RolHelper.getRolActual(request);
		
		String ret = null;
		try {
			// Data de l'informe
			Date data = new Date();
			
			// Recupera la informació
			EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
			ContingutDto contingut = contingutService.findAmbIdUser(
							entitatActual.getId(),
							contingutId,
							true,
							false, 
							rolActual,
							isVistaMoviments);
			RegistreDto registre = registreService.findOne(
					entitatActual.getId(), 
					contingutId,
					isVistaMoviments);
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
	
	@RequestMapping(value = "/contingut/hasPermisBustia/{bustiaId}", method = RequestMethod.GET)
	@ResponseBody
	public boolean hasPermisBustia(
			HttpServletRequest request,
			@PathVariable Long bustiaId,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		return contingutService.hasPermisSobreBustia(
				entitatActual.getId(), 
				bustiaId);
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
    		text = this.getLogText(request, log, registre);
    		// posa la 1a lletra en minúscula
    		text = decapitalize(text);
    		// Afegeix "A data"
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
	private String getLogText(HttpServletRequest request, ContingutLogDetallsDto log, RegistreDto registre) {
		
		StringBuilder sb = new StringBuilder();
		String usuari = log.getCreatedBy() != null ? log.getCreatedBy().getCodi() + " - " + log.getCreatedBy().getNom() : "-";
		switch(log.getTipus()) {
		case CREACIO:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.creacio", new Object[] {log.getParams().get(0)}));
			
			if (log.getContingutMoviment() != null) {
				if (log.getContingutMoviment().getDestiId() != null) {
					sb.append(" " + this.getMessage(request, "contingut.log.resum.msg.iEsPosaALaBustia", new Object[] {log.getContenidorMoviment().getDestiNom()}));
				}
			}
			
			break;
		case MOVIMENT:
		case REENVIAMENT:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.reenviar"));
			if (log.getContenidorMoviment() != null) {
				if (log.getContingutMoviment().getOrigenId() != null) {
					sb.append(" ").append(this.getMessage(request, "contingut.log.resum.msg.deLaBustia")).append(" \"");
					sb.append(log.getContenidorMoviment().getOrigenNom()).append("\"");
				}
				if (log.getContingutMoviment().getDestiId() != null) {
					sb.append(" ").append(this.getMessage(request, "contingut.log.resum.msg.aLaBustia")).append(" \"");
					sb.append(log.getContenidorMoviment().getDestiNom()).append("\"");
				}
			}
			break;
		case ENVIAMENT_EMAIL:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.enviamentEmail", 
								new Object[] {usuari,
										log.getParams().get(1)}));
			break;
		case MARCAMENT_PROCESSAT:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.marcamentProcessat", new Object[] {usuari}));
			break;
		case MARCAMENT_PENDENT:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.marcamentPendent", new Object[] {usuari}));
			break;			
		case DISTRIBUCIO:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.distribucio"));
			break;
		case REGLA_APLICAR:
			
			String reglaTipus = null;
			if (log.getParams().get(1).equals(ReglaTipusEnumDto.BUSTIA.toString())) {
				reglaTipus = getMessage(request, "regla.tipus.enum.BUSTIA");
			} else if (log.getParams().get(1).equals(ReglaTipusEnumDto.UNITAT.toString())) {
				reglaTipus = getMessage(request, "regla.tipus.enum.UNITAT");
			} else if (log.getParams().get(1).equals(ReglaTipusEnumDto.BACKOFFICE.toString())) {
				reglaTipus = getMessage(request, "regla.tipus.enum.BACKOFFICE");
			}
			
			sb.append(getMessage(request, "contingut.log.resum.msg.reglaAplicar", new Object[] {log.getParams().get(0), reglaTipus}));

			if (log.getContingutMoviment() != null) {
				
				String msg = getMessage(request, "contingut.log.resum.msg.reenviar");
				msg = msg.substring(0, 1).toLowerCase() + msg.substring(1);
				sb.append(": " + msg);
				if (log.getContingutMoviment().getDestiId() != null) {
					sb.append(" ").append(this.getMessage(request, "contingut.log.resum.msg.aLaBustia")).append(" \"");
					sb.append(log.getContenidorMoviment().getDestiNom()).append("\"");
				}
			}
			
			
			break;
		case BACK_COMUNICADA:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.BACK_COMUNICADA"));
			break;
		case BACK_REBUDA:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.BACK_REBUDA"));
			break;
		case BACK_PROCESSADA:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.BACK_PROCESSADA"));
			break;
		case BACK_REBUTJADA:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.BACK_REBUTJADA"));
			break;
		case BACK_ERROR:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.BACK_ERROR"));
			break;
		case AGAFAR:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.AGAFAR", new Object[] {log.getParams().get(0)}));
			break;
		case ALLIBERAR:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.ALLIBERAR"));
			break;
		case DUPLICITAT:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.DUPLICITAT", new Object[] {log.getParams().get(0), log.getParams().get(1)}));
			break;
		case SOBREESCRIURE:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.SOBREESCRIURE", new Object[] {log.getParams().get(0)}));
			break;
		case CLASSIFICAR:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.CLASSIFICAR", new Object[] {usuari, registre.getProcedimentCodi()}));
			break;
		default:
			sb.append(this.getMessage(request, "contingut.log.resum.msg.accio")).append(": \"");
			sb.append(this.getMessage(request, "log.tipus.enum." + log.getTipus().name())).append("\"");
			if (log.getParams().get(0) != null)
				sb.append(" param1: \"").append(log.getParams().get(0)).append("\"");
			if (log.getParams().get(1) != null)
				sb.append(" param2: \"").append(log.getParams().get(1)).append("\"");
			break;
		}
		return sb.toString();
	}

	@RequestMapping(value = "/contingut/{registreId}/comentaris", method = RequestMethod.GET)
	public String comentaris(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@RequestParam(required=false, defaultValue="false") boolean isVistaMoviments,
			Model model) {
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		String rolActual = RolHelper.getRolActual(request);
		model.addAttribute(
				"contingut",
				contingutService.findAmbIdUser(
						entitatActual.getId(),
						registreId,
						true,
						false, 
						rolActual,
						isVistaMoviments));
		UsuariDto usuariActual = aplicacioService.getUsuariActual();
		model.addAttribute(
				"usuariActual",
				usuariActual);
		boolean hasPermisBustia = contingutService.hasPermisSobreBustia(
				entitatActual.getId(), 
				registreId);
		model.addAttribute("hasPermisBustia", 
				hasPermisBustia);
		return "contingutComentaris";
	}

	@RequestMapping(value = "/contingut/{registreId}/comentaris/publicar", method = RequestMethod.POST)
	@ResponseBody
	public RespostaPublicacioComentariDto publicarComentari(
			HttpServletRequest request,
			@PathVariable Long registreId,
			@RequestParam String text,
			Model model) {
		RespostaPublicacioComentariDto resposta = new RespostaPublicacioComentariDto();
		EntitatDto entitatActual = getEntitatActualComprovantPermisUsuari(request);
		if (text != null && !text.isEmpty()) {
			resposta = contingutService.publicarComentariPerContingut(entitatActual.getId(), registreId, text);
		}
		List<ContingutComentariDto> comentaris = contingutService.findComentarisPerContingut(
				entitatActual.getId(), 
				registreId);
		resposta.setComentaris(comentaris);
		return resposta;
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
