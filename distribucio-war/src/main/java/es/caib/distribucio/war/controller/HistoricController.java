package es.caib.distribucio.war.controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.velocity.tools.generic.DateTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.historic.HistoricAnotacioDto;
import es.caib.distribucio.core.api.dto.historic.HistoricDadesDto;
import es.caib.distribucio.core.api.service.HistoricService;
import es.caib.distribucio.war.command.HistoricFiltreCommand;
import es.caib.distribucio.war.helper.JsonDadesUo;
import es.caib.distribucio.war.helper.MissatgesHelper;
import es.caib.distribucio.war.helper.RequestSessionHelper;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;

@Controller
@RequestMapping("/historic")
public class HistoricController extends BaseAdminController {
	private static final String SESSION_ATTRIBUTE_FILTRE = "HistoricController.session.filtre";

	@Autowired
	private HistoricService historicService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, 
			HttpServletResponse response, 
			Model model) {	
		
		getEntitatActualComprovantPermisos(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		model.addAttribute(historicFiltreCommand);
//		model.addAttribute("historicFiltreCommand", historicFiltreCommand);
		model.addAttribute("showDadesUO", false);
		model.addAttribute("showDadesEstat", false);
		model.addAttribute("showDadesBusties", false);

		return "historic";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid HistoricFiltreCommand historicFiltreCommand,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio,
			Model model) {
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE);
		} 
//		else {
//			if (!bindingResult.hasErrors()) {
//				RequestSessionHelper.actualitzarObjecteSessio(
//						request,
//						SESSION_ATTRIBUTE_FILTRE,
//						historicFiltreCommand);
//			}
//		}
		return "redirect:historic";
	}
	
	private HistoricFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		HistoricFiltreCommand filtreCommand = (HistoricFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (filtreCommand == null) {
			filtreCommand = new HistoricFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request, 
					SESSION_ATTRIBUTE_FILTRE, 
					filtreCommand);
		}
		return filtreCommand;
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}
	
	@RequestMapping(value = "/JsonDataUO", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<JsonDadesUo>> getDataHistoricUO(
			HttpServletRequest request,
			@Valid HistoricFiltreCommand historicFiltreCommand,
			BindingResult bindingResult,
			Model model) {
//			Model model) throws NoSuchAlgorithmException {	
//		Map<String, List<JsonDadesUo>> results = new HashMap<>();		
//		results.put("uo_1", cargarTabla("uo_1"));
//		//TODO: comentar las dos líneas siguientes para ver todas las métricas de la UO en un sólo gráfico
//		results.put("uo_2", cargarTabla("uo_2"));
//		results.put("uo_3", cargarTabla("uo_3"));

		EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
		
		if (!bindingResult.hasErrors()) {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					historicFiltreCommand);
		}

		if (historicFiltreCommand.isActualitzar()) {
				historicService.calcularDadesHistoriques(new Date());
		}
			
		HistoricDadesDto dades = historicService.getDadesHistoriques(
				entitatActual.getId(),
				historicFiltreCommand.asDto());
		
		// Transforma les dades a resultlats
		Map<String, List<JsonDadesUo>> results = transformarDadesJson(dades);

		return results;	
	}	
	
	private Map<String, List<JsonDadesUo>> transformarDadesJson(HistoricDadesDto dades) {
		Map<String, List<JsonDadesUo>> results = new HashMap<>();
		
		// Dades d'anotacions per UO
		for (HistoricAnotacioDto anotacio : dades.getDadesAnotacions()) {
			List<JsonDadesUo> lrespuestaJson = results.get(anotacio.getUnitat().getCodi());
			if (lrespuestaJson == null) {
				lrespuestaJson = new ArrayList<JsonDadesUo>();
				results.put(anotacio.getUnitat().getCodi(), lrespuestaJson);
			}
			lrespuestaJson.add(new JsonDadesUo(
					anotacio.getData(),
					anotacio.getUnitat().getCodi(), 
					anotacio.getUnitat().getNom(),
					anotacio.getAnotacions(), 
					anotacio.getAnotacionsTotal(), 
					anotacio.getReenviaments(), 
					anotacio.getEmails(), 
					anotacio.getJustificants(), 
					anotacio.getAnnexos(), 
					anotacio.getBusties(), 
					anotacio.getUsuaris()));
		}

		return results;
	}

	private List<JsonDadesUo> cargarTabla(String uo) throws NoSuchAlgorithmException {
		List<JsonDadesUo> lrespuestaJson = new ArrayList<JsonDadesUo>();

		SecureRandom number = SecureRandom.getInstance("SHA1PRNG");

		for (long i = 0; i < 6; i++) {
			long j = number.nextInt(21);
			Date dt = new Date();
	        Calendar c = Calendar.getInstance();
	        c.setTime(dt);
	        c.add(Calendar.DATE, new Long(i).intValue());
	        dt = c.getTime();
	        JsonDadesUo fila = new JsonDadesUo(dt, "uoCod_"+i, uo, i+j, (i+j)*5,
					i+j+2, i+j+1, i+j+3, i+j+7, i+j+10, i+j+15);
			lrespuestaJson.add(fila);
		}
		return lrespuestaJson;
	}
	
	@RequestMapping(value = "/exportar", method = RequestMethod.GET, produces = {})
	public void exportar(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "format", required = true, defaultValue = "JSON") String format,
			Model model) throws NoSuchAlgorithmException {	

		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
			HistoricDadesDto dades = historicService.getDadesHistoriques(
					entitatActual.getId(),
					this.getFiltreCommand(request).asDto());
			
			FitxerDto fitxer = this.exportarDades(request, dades, format);
			this.writeFileToResponse(fitxer.getNom(), fitxer.getContingut(), response);
		} catch(Exception e) {
			String errMsg = getMessage(
					request, 
					"historic.exportacio.error",
					new Object[] {e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(
					request, 
					errMsg);
		}
	}
	
	private FitxerDto exportarDades(HttpServletRequest request, HistoricDadesDto dades, String format) throws Exception {
		FitxerDto fitxer = new FitxerDto();
		fitxer.setNom(this.getMessage(request, "historic.exportacio.fitxerNom") + "." + format.toLowerCase());
		switch (format.toLowerCase()) 
		{
			case "json":
				fitxer.setContentType("application/json");
				fitxer.setContingut(this.dadesToJson(request, dades));
				break;
			case "xlsx":
				fitxer.setContentType("application/vnd.ms-excel");
				fitxer.setContingut(this.dadesToXlsx(request, dades));
				break;
			case "odt":
				fitxer.setContentType("application/vnd.oasis.opendocument.text");
				fitxer.setContingut(this.dadesToOdt(request, dades));
				break;
			case "xml":
				fitxer.setContentType("application/xml");
				fitxer.setContingut(this.dadesToXml(request, dades));
				break;
			default:
				throw new Exception("Unsuported file format: " + format);
		}
		return fitxer;	
	}

	private byte[] dadesToXml(HttpServletRequest request, HistoricDadesDto dades) throws Exception {
		
		JAXBContext context = JAXBContext.newInstance(HistoricDadesDto.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		StringWriter sw = new StringWriter();
		m.marshal(dades, sw);
		return sw.toString().getBytes();
	}

	private byte[] dadesToXlsx(HttpServletRequest request, HistoricDadesDto dades) throws Exception {
		
		// Crea el llibre de càlcul
		HSSFWorkbook wb;
		HSSFCellStyle cellStyle;
		HSSFCellStyle dStyle;
		HSSFFont bold;
		HSSFCellStyle cellGreyStyle;
		HSSFCellStyle greyStyle;
		HSSFCellStyle dGreyStyle;
		HSSFFont greyFont;
		wb = new HSSFWorkbook();
	
		bold = wb.createFont();
		bold.setBold(true);
		bold.setColor(HSSFColor.WHITE.index);
		
		greyFont = wb.createFont();
		greyFont.setColor(HSSFColor.GREY_25_PERCENT.index);
		greyFont.setCharSet(HSSFFont.ANSI_CHARSET);
		
		cellStyle = wb.createCellStyle();
		cellStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
		cellStyle.setWrapText(true);
		
		cellGreyStyle = wb.createCellStyle();
		cellGreyStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
		cellGreyStyle.setWrapText(true);
		cellGreyStyle.setFont(greyFont);
		
		greyStyle = wb.createCellStyle();
		greyStyle.setFont(greyFont);
	
		DataFormat format = wb.createDataFormat();
		dStyle = wb.createCellStyle();
		dStyle.setDataFormat(format.getFormat("0.00"));
	
		dGreyStyle = wb.createCellStyle();
		dGreyStyle.setFont(greyFont);
		dGreyStyle.setDataFormat(format.getFormat("0.00"));
		
		if (dades.hasDadesAnotacions()) {
			HSSFSheet sheet = wb.createSheet(this.getMessage(request, "historic.titol.seccio.dades.uo"));
			this.crearCapcaleraXlsx(
					request,
					wb,
					sheet);
			int rowNum = 1;
			for (HistoricAnotacioDto anotacio : dades.getDadesAnotacions()) {
				try {
					HSSFRow xlsRow = sheet.createRow(rowNum++);
					
					HSSFCell cellUnitatCodi = xlsRow.createCell(0);
					cellUnitatCodi.setCellValue(anotacio.getData());
					cellUnitatCodi.setCellStyle(dStyle);
					
					HSSFCell cellUnitatcodi = xlsRow.createCell(1);
					cellUnitatcodi.setCellValue(anotacio.getUnitat().getCodi());
					cellUnitatcodi.setCellStyle(dStyle);

					HSSFCell cellUnitatNom = xlsRow.createCell(2);
					cellUnitatNom.setCellValue(anotacio.getUnitat().getNom());
					cellUnitatNom.setCellStyle(dStyle);
					
					HSSFCell cellAnotacions = xlsRow.createCell(3);
					cellAnotacions.setCellValue(anotacio.getAnotacions());
					cellAnotacions.setCellStyle(dStyle);

					HSSFCell cellAnotacionsTotal = xlsRow.createCell(4);
					cellAnotacionsTotal.setCellValue(anotacio.getAnotacionsTotal());
					cellAnotacionsTotal.setCellStyle(dStyle);
					
					//TODO: posar la resta de columnes

				} catch (Exception e) {
					logger.error("Export Excel: No s'ha pogut crear la línia: " + rowNum, e);
				}
			}
			for(int i=0; i<5; i++)
				sheet.autoSizeColumn(i);
		}
		if (dades.hasDadesEstats()) {
			
		}
		if (dades.hasDadesBusties()) {
			
		}
		byte[] contingut = wb.getBytes();
		wb.close();
		return contingut;
	}
	
	private void crearCapcaleraXlsx(
			HttpServletRequest request, 
			HSSFWorkbook wb,
			HSSFSheet sheet) {
		
		HSSFFont bold;
		bold = wb.createFont();
		bold.setBold(true);
		bold.setColor(HSSFColor.WHITE.index);
		HSSFCellStyle headerStyle;
		headerStyle = wb.createCellStyle();
		headerStyle.setFillPattern(HSSFCellStyle.FINE_DOTS);
		headerStyle.setFillBackgroundColor(HSSFColor.GREY_80_PERCENT.index);
		headerStyle.setFont(bold);
		int rowNum = 0;
		int colNum = 0;
		// Capçalera
		HSSFRow xlsRow = sheet.createRow(rowNum++);
		HSSFCell cell;
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.data")));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.codi.uo")));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.uo")));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.anotacions.noves")));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.anotacions.total")));
		cell.setCellStyle(headerStyle);
	}

	private byte[] dadesToJson(HttpServletRequest request, HistoricDadesDto dades) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		return mapper.writeValueAsBytes(dades);
	}	

	private byte[] dadesToOdt(HttpServletRequest request, HistoricDadesDto dades) throws Exception {
		Locale locale = new RequestContext(request).getLocale();


		// 1) Load ODT file and set Velocity template engine and cache it to the registry					
    	InputStream in= this.getClass().getResourceAsStream("/plantilles/historic_" + locale.getLanguage() + ".odt");
    	IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in,TemplateEngineKind.Velocity);

    	// 2) Create Java model context 
//		FieldsMetadata metadata = new FieldsMetadata();
//		metadata.setTemplateEngineKind("Velocity");
//		metadata.addFieldAsList("dades.dadesAnotacions.data");
//		metadata.addFieldAsList("dades.dadesAnotacions.unitat.codi");
//		metadata.addFieldAsList("dades.dadesAnotacions.unitat.nom");
//		metadata.addFieldAsList("dades.dadesAnotacions.anotacions");
//		metadata.addFieldAsList("dades.dadesAnotacions.anotacionsTotal");
//		metadata.addFieldAsList("dades.dadesAnotacions.reenviaments");
//		metadata.addFieldAsList("dades.dadesAnotacions.emails");
//		metadata.addFieldAsList("dades.dadesAnotacions.justificants");
//		metadata.addFieldAsList("dades.dadesAnotacions.annexos");
//		metadata.addFieldAsList("dades.dadesAnotacions.busties");
//		metadata.addFieldAsList("dades.dadesAnotacions.usuaris");
//		report.setFieldsMetadata(metadata);
    	
    	IContext context = report.createContext();
		context.put("data", new Date());
		context.put("dades", dades);
		context.put("dateFormatter", new DateTool());

    	// 3) Set PDF as format converter
    	//Options options = Options.getTo(ConverterTypeTo.PDF);

    	// 3) Generate report by merging Java model with the ODT and convert it to PDF
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	report.process(context, bos);
    	//report.convert(context, options, bos);
    	
    	return bos.toByteArray();

    }

	private static final Logger logger = LoggerFactory.getLogger(HistoricController.class);
}
