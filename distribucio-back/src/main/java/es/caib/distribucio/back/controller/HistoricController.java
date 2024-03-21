package es.caib.distribucio.back.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.velocity.tools.generic.DateTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.format.annotation.DateTimeFormat;
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

import es.caib.distribucio.back.command.HistoricFiltreCommand;
import es.caib.distribucio.back.helper.JsonDades;
import es.caib.distribucio.back.helper.JsonDadesBustia;
import es.caib.distribucio.back.helper.JsonDadesEstat;
import es.caib.distribucio.back.helper.JsonDadesUo;
import es.caib.distribucio.back.helper.MissatgesHelper;
import es.caib.distribucio.back.helper.RequestSessionHelper;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.FitxerDto;
import es.caib.distribucio.logic.intf.dto.historic.HistoricAnotacioDto;
import es.caib.distribucio.logic.intf.dto.historic.HistoricBustiaDto;
import es.caib.distribucio.logic.intf.dto.historic.HistoricDadesDto;
import es.caib.distribucio.logic.intf.dto.historic.HistoricEstatDto;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.logic.intf.service.AplicacioService;
import es.caib.distribucio.logic.intf.service.HistoricService;
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
	@Autowired
	private AplicacioService aplicacioService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, 
			Model model) {	
		
		getEntitatActualComprovantPermisAdminLectura(request);
		HistoricFiltreCommand historicFiltreCommand = getFiltreCommand(request);
		model.addAttribute(historicFiltreCommand);
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
	
	@RequestMapping(value = "/JsonDadesHistoric", method = RequestMethod.POST)
	@ResponseBody
	public JsonDades getDadesHistoric(
			HttpServletRequest request,
			@Valid HistoricFiltreCommand historicFiltreCommand,
			BindingResult bindingResult,
			Model model) {

		EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
		
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
		
		// Transforma les dades a resultats
		JsonDades results = transformarDadesJson(request, dades);

		return results;	
	}	
	
	/** Mètode per forçar el càlcul entre dues dates o des d'una data fins al dia actual
	 */
	@RequestMapping(value = "/calcularDades", method = RequestMethod.GET)
	public String calcularDades(
			HttpServletRequest request,
			@DateTimeFormat(pattern = "dd/MM/yyy")
			@RequestParam(value="dataInici", required = false) Date dataInici,
			@DateTimeFormat(pattern = "dd/MM/yyy")
			@RequestParam(value="dataFi", required = false) Date dataFi,
			Model model) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			if (dataInici == null)
				dataInici = new Date();
			if (dataFi == null)
				dataFi = new Date();
			Calendar c = new GregorianCalendar();
			c.setTime(dataFi);
			int diesCalculats = 0;
			do {
				logger.debug("Calculant dades històriques per la data " + sdf.format(c.getTime()));
				historicService.calcularDadesHistoriques(c.getTime());
				historicService.recalcularTotals(c.getTime());
				c.add(Calendar.DATE, -1);
				diesCalculats++;
			} while (c.getTime().getTime() >= dataInici.getTime());
			MissatgesHelper.success(
					request, 
					getMessage(request, 
							"historic.calcularDates.success", 
							new Object[] {diesCalculats, sdf.format(dataInici), sdf.format(dataFi)}));
		} catch (Exception e) {
			String errMsg = getMessage(request, 
							"historic.calcularDates.error", 
							new Object[] {sdf.format(dataInici), sdf.format(dataFi), e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(
					request, 
					errMsg);
		}
		return "redirect:/historic";
	}
	
	private JsonDades transformarDadesJson(HttpServletRequest request, HistoricDadesDto dades) {
		
		// Dades d'anotacions per UO
		Map<String, List<JsonDadesUo>> resultsUo = new HashMap<>();
		
		if (dades.getDadesAnotacions() != null) {
			for (HistoricAnotacioDto anotacio : dades.getDadesAnotacions()) {
				List<JsonDadesUo> lrespuestaJson = resultsUo.get(anotacio.getUnitatCodi());
				if (lrespuestaJson == null) {
					lrespuestaJson = new ArrayList<JsonDadesUo>();
					resultsUo.put(anotacio.getUnitatCodi(), lrespuestaJson);
				}
				lrespuestaJson.add(new JsonDadesUo(
						anotacio.getData(),
						anotacio.getUnitatCodi(), 
						anotacio.getUnitatNom(),
						anotacio.getAnotacions(), 
						anotacio.getAnotacionsTotal(), 
						anotacio.getReenviaments(), 
						anotacio.getEmails(), 
						anotacio.getJustificants(), 
						anotacio.getAnnexos(), 
						anotacio.getBusties(), 
						anotacio.getUsuaris()));
			}
		}

		// Dades estat
		Map<RegistreProcesEstatEnum, String> estats = getEstatLiterals(request); 
		Map<String, List<JsonDadesEstat>> resultsEstat = new HashMap<>();
		if (dades.getDadesEstats() != null) {
			for (HistoricEstatDto estat : dades.getDadesEstats()) {
				List<JsonDadesEstat> lrespuestaJson = resultsEstat.get(estats.get(estat.getEstat()));
				if (lrespuestaJson == null) {
					lrespuestaJson = new ArrayList<JsonDadesEstat>();
					resultsEstat.put(estats.get(estat.getEstat()), lrespuestaJson);
				}
				lrespuestaJson.add(new JsonDadesEstat(
						estat.getData(),
						estats.get(estat.getEstat()),
						estat.getCorrecte(),
						estat.getCorrecteTotal(),
						estat.getError(),
						estat.getErrorTotal(),
						estat.getTotal()));
			}
		}
				
		// Dades bústies
		Map<String, List<JsonDadesBustia>> resultsBustia = new HashMap<>();
		if (dades.getDadesBusties() != null) {
			for (HistoricBustiaDto bustia : dades.getDadesBusties()) {
				List<JsonDadesBustia> lrespuestaJson = resultsBustia.get(bustia.getNom() + " (" + bustia.getUnitatCodi() + ")");
				if (lrespuestaJson == null) {
					lrespuestaJson = new ArrayList<JsonDadesBustia>();
					resultsBustia.put(bustia.getNom() + " (" + bustia.getUnitatCodi() + ")", lrespuestaJson);
				}
				lrespuestaJson.add(new JsonDadesBustia(
						bustia.getData(),
						bustia.getUnitatCodi(),
						bustia.getUnitatNom(),
						bustia.getBustiaId(),
						bustia.getNom(),
						bustia.getUsuaris(),
						bustia.getUsuarisPermis(),
						bustia.getUsuarisRol()));
			}
		}
		
		return new JsonDades(resultsUo, resultsEstat, resultsBustia);
	}
	
	@RequestMapping(value = "/exportar", method = RequestMethod.GET, produces = {})
	public void exportar(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "format", required = true, defaultValue = "JSON") String format,
			Model model) throws NoSuchAlgorithmException {	

		try {
			EntitatDto entitatActual = getEntitatActualComprovantPermisAdminLectura(request);
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
		HSSFCellStyle dataStyle;
		HSSFCellStyle defaultStyle;
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
		
		dataStyle = wb.createCellStyle();
		dataStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy"));
		dataStyle.setWrapText(true);
		
		cellGreyStyle = wb.createCellStyle();
		cellGreyStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy"));
		cellGreyStyle.setWrapText(true);
		cellGreyStyle.setFont(greyFont);
		
		greyStyle = wb.createCellStyle();
		greyStyle.setFont(greyFont);
	
		DataFormat format = wb.createDataFormat();
		defaultStyle = wb.createCellStyle();
		defaultStyle.setDataFormat(format.getFormat("0"));
	
		dGreyStyle = wb.createCellStyle();
		dGreyStyle.setFont(greyFont);
		dGreyStyle.setDataFormat(format.getFormat("0.00"));
		
		if (dades.hasDadesAnotacions()) {
			HSSFSheet sheet = wb.createSheet(this.getMessage(request, "historic.titol.seccio.dades.uo"));
			this.crearCapcaleraXlsxDadesAnotacions(
					request,
					wb,
					sheet);
			int rowNum = 1;
			for (HistoricAnotacioDto anotacio : dades.getDadesAnotacions()) {
				try {
					HSSFRow xlsRow = sheet.createRow(rowNum++);
					
					HSSFCell cellUnitatData = xlsRow.createCell(0);
					cellUnitatData.setCellValue(anotacio.getData());
					cellUnitatData.setCellStyle(dataStyle);
					
					HSSFCell cellUnitatcodi = xlsRow.createCell(1);
					cellUnitatcodi.setCellValue(anotacio.getUnitatCodi());
					cellUnitatcodi.setCellStyle(defaultStyle);

					HSSFCell cellUnitatNom = xlsRow.createCell(2);
					cellUnitatNom.setCellValue(anotacio.getUnitatNom());
					cellUnitatNom.setCellStyle(defaultStyle);
					
					HSSFCell cellAnotacions = xlsRow.createCell(3);
					cellAnotacions.setCellValue(anotacio.getAnotacions());
					cellAnotacions.setCellStyle(defaultStyle);

					HSSFCell cellAnotacionsTotal = xlsRow.createCell(4);
					cellAnotacionsTotal.setCellValue(anotacio.getAnotacionsTotal());
					cellAnotacionsTotal.setCellStyle(defaultStyle);
					
					HSSFCell cellReenviades = xlsRow.createCell(5);
					cellReenviades.setCellValue(anotacio.getReenviaments());
					cellReenviades.setCellStyle(defaultStyle);
					
					HSSFCell cellPerEmail = xlsRow.createCell(6);
					cellPerEmail.setCellValue(anotacio.getEmails());
					cellPerEmail.setCellStyle(defaultStyle);
					
					HSSFCell cellJustificants = xlsRow.createCell(7);
					cellJustificants.setCellValue(anotacio.getJustificants());
					cellJustificants.setCellStyle(defaultStyle);
					
					HSSFCell cellAnnexos = xlsRow.createCell(8);
					cellAnnexos.setCellValue(anotacio.getAnnexos());
					cellAnnexos.setCellStyle(defaultStyle);
					
					HSSFCell cellBusties = xlsRow.createCell(9);
					cellBusties.setCellValue(anotacio.getBusties());
					cellBusties.setCellStyle(defaultStyle);
					
					HSSFCell cellUsuaris = xlsRow.createCell(10);
					cellUsuaris.setCellValue(anotacio.getUsuaris());
					cellUsuaris.setCellStyle(defaultStyle);
				} catch (Exception e) {
					logger.error("Export Excel: No s'ha pogut crear la línia: " + rowNum, e);
				}
			}
			for(int i=0; i<5; i++)
				sheet.autoSizeColumn(i);
		}
		if (dades.hasDadesEstats()) {
			HSSFSheet sheet = wb.createSheet(this.getMessage(request, "historic.titol.seccio.dades.estat"));
			this.crearCapcaleraXlsxDadesEstats(
					request,
					wb,
					sheet);
			int rowNum = 1;
			Map<RegistreProcesEstatEnum, String> estats = getEstatLiterals(request);
			for (HistoricEstatDto estat : dades.getDadesEstats()) {
				try {
					HSSFRow xlsRow = sheet.createRow(rowNum++);
					
					HSSFCell cellEstatData = xlsRow.createCell(0);
					cellEstatData.setCellValue(estat.getData());
					cellEstatData.setCellStyle(dataStyle);
					
					HSSFCell cellEstat = xlsRow.createCell(1);
					cellEstat.setCellValue(estats.get(estat.getEstat()));
					cellEstat.setCellStyle(defaultStyle);

					HSSFCell cellAnotacionsCorrectes = xlsRow.createCell(2);
					cellAnotacionsCorrectes.setCellValue(estat.getCorrecte());
					cellAnotacionsCorrectes.setCellStyle(defaultStyle);

					HSSFCell cellAnotacionsCorrectesTotals = xlsRow.createCell(3);
					cellAnotacionsCorrectesTotals.setCellValue(estat.getCorrecteTotal());
					cellAnotacionsCorrectesTotals.setCellStyle(defaultStyle);
					
					HSSFCell cellAnotacionsError = xlsRow.createCell(4);
					cellAnotacionsError.setCellValue(estat.getError());
					cellAnotacionsError.setCellStyle(defaultStyle);
					
					HSSFCell cellAnotacionsErrorTotals = xlsRow.createCell(5);
					cellAnotacionsErrorTotals.setCellValue(estat.getErrorTotal());
					cellAnotacionsErrorTotals.setCellStyle(defaultStyle);
					
					HSSFCell cellAnotacionsTotal = xlsRow.createCell(6);
					cellAnotacionsTotal.setCellValue(estat.getTotal());
					cellAnotacionsTotal.setCellStyle(defaultStyle);
					
				} catch (Exception e) {
					logger.error("Export Excel: No s'ha pogut crear la línia: " + rowNum, e);
				}
			}
			for(int i=0; i<5; i++)
				sheet.autoSizeColumn(i);		
		}
		if (dades.hasDadesBusties()) {

			HSSFSheet sheet = wb.createSheet(this.getMessage(request, "historic.titol.seccio.usuaris.busties"));
			this.crearCapcaleraXlsxDadesBusties(
					request,
					wb,
					sheet);
			int rowNum = 1;
			for (HistoricBustiaDto bustia : dades.getDadesBusties()) {
				try {
					HSSFRow xlsRow = sheet.createRow(rowNum++);
					
					HSSFCell cellBustiaData = xlsRow.createCell(0);
					cellBustiaData.setCellValue(bustia.getData());
					cellBustiaData.setCellStyle(dataStyle);
					
					HSSFCell cellBustiaUnitatcodi = xlsRow.createCell(1);
					cellBustiaUnitatcodi.setCellValue(bustia.getUnitatCodi());
					cellBustiaUnitatcodi.setCellStyle(defaultStyle);

					HSSFCell cellBustiaUnitatNom = xlsRow.createCell(2);
					cellBustiaUnitatNom.setCellValue(bustia.getUnitatNom());
					cellBustiaUnitatNom.setCellStyle(defaultStyle);
					
					HSSFCell cellBustiaId = xlsRow.createCell(3);
					cellBustiaId.setCellValue(bustia.getBustiaId());
					cellBustiaId.setCellStyle(defaultStyle);
					
					HSSFCell cellBustiaNom = xlsRow.createCell(4);
					cellBustiaNom.setCellValue(bustia.getNom());
					cellBustiaNom.setCellStyle(defaultStyle);

					HSSFCell cellUsuarisTotals = xlsRow.createCell(5);
					cellUsuarisTotals.setCellValue(bustia.getUsuaris());
					cellUsuarisTotals.setCellStyle(defaultStyle);	
					
					HSSFCell cellUsuarisPermisDirecte = xlsRow.createCell(6);
					cellUsuarisPermisDirecte.setCellValue(bustia.getUsuarisPermis());
					cellUsuarisPermisDirecte.setCellStyle(defaultStyle);	
					
					HSSFCell cellUsuarisRol = xlsRow.createCell(7);
					cellUsuarisRol.setCellValue(bustia.getUsuarisRol());
					cellUsuarisRol.setCellStyle(defaultStyle);	
				} catch (Exception e) {
					logger.error("Export Excel: No s'ha pogut crear la línia: " + rowNum, e);
				}
			}
			for(int i=0; i<5; i++)
				sheet.autoSizeColumn(i);			
		}
		byte[] contingut = wb.getBytes();
		wb.close();
		return contingut;
	}
	
	private void crearCapcaleraXlsxDadesAnotacions(
			HttpServletRequest request, 
			HSSFWorkbook wb,
			HSSFSheet sheet) {
		
		HSSFFont bold;
		bold = wb.createFont();
		bold.setBold(true);
		bold.setColor(HSSFColor.WHITE.index);
		HSSFCellStyle headerStyle;
		headerStyle = wb.createCellStyle();
		headerStyle.setFillPattern(FillPatternType.FINE_DOTS);
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
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.anotacions.totals")));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.anotacions.reenviades")));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.anotacions.perEmail")));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.anotacions.justificants")));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.anotacions.annexos")));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.anotacions.busties")));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.anotacions.usuaris")));
		cell.setCellStyle(headerStyle);
	}

	private void crearCapcaleraXlsxDadesEstats(
			HttpServletRequest request, 
			HSSFWorkbook wb,
			HSSFSheet sheet) {
		
		HSSFFont bold;
		bold = wb.createFont();
		bold.setBold(true);
		bold.setColor(HSSFColor.WHITE.index);
		HSSFCellStyle headerStyle;
		headerStyle = wb.createCellStyle();
		headerStyle.setFillPattern(FillPatternType.FINE_DOTS);
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
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.estat")));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.estats.correcte")));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.estats.correcteTotal")));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.estats.error")));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.estats.errorTotal")));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.estats.total")));
		cell.setCellStyle(headerStyle);		
	}
	
	private void crearCapcaleraXlsxDadesBusties(
			HttpServletRequest request, 
			HSSFWorkbook wb,
			HSSFSheet sheet) {
		
		HSSFFont bold;
		bold = wb.createFont();
		bold.setBold(true);
		bold.setColor(HSSFColor.WHITE.index);
		HSSFCellStyle headerStyle;
		headerStyle = wb.createCellStyle();
		headerStyle.setFillPattern(FillPatternType.FINE_DOTS);
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
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.bustia.id")));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.bustia.nom")));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.busties.usuaris")));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.busties.usuarisPermis")));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString(this.getMessage(request, "historic.taula.header.busties.usuarisRol")));
		cell.setCellStyle(headerStyle);
	}
	
	private byte[] dadesToJson(HttpServletRequest request, HistoricDadesDto dades) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		return mapper.writeValueAsBytes(dades);
	}	

	private byte[] dadesToOdt(HttpServletRequest request, HistoricDadesDto dades) throws Exception {
		Locale locale = new RequestContext(request).getLocale();

		String plantilla = getPlantillaHistoric(locale.getLanguage());
		
		// 1) Load ODT file and set Velocity template engine and cache it to the registry					
    	InputStream in = null;
    	if (plantilla != null)
    		in = new FileInputStream(new File(plantilla));
    	else
    		in = this.getClass().getResourceAsStream("/plantilles/historic_" + locale.getLanguage() + ".odt");
    	IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in,TemplateEngineKind.Velocity);

    	// 2) Create Java model context 
    	IContext context = report.createContext();
		context.put("data", new Date());
		context.put("dades", dades);
		context.put("dateFormatter", new DateTool());
		context.put("estats", getEstatLiterals(request));

    	// 3) Set PDF as format converter
    	//Options options = Options.getTo(ConverterTypeTo.PDF);

    	// 3) Generate report by merging Java model with the ODT and convert it to PDF
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	report.process(context, bos);
    	//report.convert(context, options, bos);
    	
    	return bos.toByteArray();

    }

	private String getPlantillaHistoric(String idioma) {
		return aplicacioService.propertyFindByNom("es.caib.distribucio.plantilla.historic." + idioma);
	}
	
	private Map<RegistreProcesEstatEnum, String> getEstatLiterals(HttpServletRequest request) {
		Map<RegistreProcesEstatEnum, String> estats = new HashMap<>();
		for (RegistreProcesEstatEnum estat : RegistreProcesEstatEnum.values()) {
			estats.put(estat, this.getMessage(request, "registre.proces.estat.enum." + estat.toString()));
		}
		return estats;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(HistoricController.class);
}