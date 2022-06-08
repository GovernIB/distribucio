package es.caib.distribucio.war.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.DataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.distribucio.core.api.dto.RegistreDto;
import es.caib.distribucio.core.api.registre.RegistreInteressat;
import es.caib.distribucio.core.api.service.ContingutService;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.war.controller.BaseController;

@Component
public class RegistreHelper extends BaseController{
	
	@Autowired
	private RegistreService registreService;
	@Autowired
	private ContingutService contingutService;
	
	public void generarExcelAnotacions(
			HttpServletRequest request,
			HttpServletResponse response,
			List<RegistreDto> registres, 
			String extensio) {

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
		HSSFSheet sheet = wb.createSheet("Hoja 1");
			createHeader(
					wb,
					sheet);
		int rowNum = 1;
		for (RegistreDto registre : registres) {
			try {
				HSSFRow xlsRow = sheet.createRow(rowNum++);
				
				HSSFCell cellRegistreNumero = xlsRow.createCell(0);
				cellRegistreNumero.setCellValue(registre.getNumero());
				cellRegistreNumero.setCellStyle(dStyle);
				
				HSSFCell cellRegistreData = xlsRow.createCell(1);
				Date data = registre.getData();
				DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
				String strData = dateFormat.format(data);
				cellRegistreData.setCellValue(strData);
				cellRegistreData.setCellStyle(dStyle);
				
				HSSFCell cellOficinaCodi = xlsRow.createCell(2);
				cellOficinaCodi.setCellValue(registre.getOficinaDescripcio());
				cellOficinaCodi.setCellStyle(dStyle);

				HSSFCell cellRegistrePresencial = xlsRow.createCell(3);
				String presencial = "";
				if (registre.getPresencial()) {
					presencial = "Si";
				}else {
					presencial = "no";
				}
				cellRegistrePresencial.setCellValue(presencial);
				cellRegistrePresencial.setCellStyle(dStyle);
				
				HSSFCell cellRegistreExtracte = xlsRow.createCell(4);
				cellRegistreExtracte.setCellValue(registre.getExtracte());
				cellRegistreExtracte.setCellStyle(dStyle);
				
				HSSFCell cellRegistreDocumentacio = xlsRow.createCell(5);
				cellRegistreDocumentacio.setCellValue(registre.getDocumentacioFisicaCodi());
				cellRegistreDocumentacio.setCellStyle(dStyle);
				
				HSSFCell cellRegistreProcediment = xlsRow.createCell(6);
				cellRegistreProcediment.setCellValue(registre.getProcedimentCodi());
				cellRegistreProcediment.setCellStyle(dStyle);
				
				HSSFCell cellRegistreObservacions = xlsRow.createCell(7);
				cellRegistreObservacions.setCellValue(registre.getObservacions());
				cellRegistreObservacions.setCellStyle(dStyle);
				
				HSSFCell cellRegistreNumeroOrigen = xlsRow.createCell(8);
				cellRegistreNumeroOrigen.setCellValue(registre.getNumeroOrigen());
				cellRegistreNumeroOrigen.setCellStyle(dStyle);
				
				Date date = registre.getDataOrigen();
				DateFormat dateFormat2 = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
				String origenData;
				if (date == null) {
					origenData = "";
				}else {
					origenData = dateFormat2.format(date);
				}
				HSSFCell cellRegistreDataOrigen = xlsRow.createCell(9);
				cellRegistreDataOrigen.setCellValue(origenData);
				cellRegistreDataOrigen.setCellStyle(dStyle);					
				
				HSSFCell cellRegistreOficinaOrigen = xlsRow.createCell(10);
				cellRegistreOficinaOrigen.setCellValue(registre.getOficinaOrigenCodi());
				cellRegistreOficinaOrigen.setCellStyle(dStyle);
				
				List<RegistreInteressat> llistatInteressats = registre.getInteressats();
				String nomComplet = "";
				for (RegistreInteressat interessat : llistatInteressats) {
					nomComplet += interessat.getNom() + " " + interessat.getLlinatge1() + " " + interessat.getLlinatge2() + "(" + interessat.getDocumentNum() + ") | ";
					System.out.println(nomComplet);
				}

				
				HSSFCell cellRegistreInteressats = xlsRow.createCell(11);
				cellRegistreInteressats.setCellValue(nomComplet);
				cellRegistreInteressats.setCellStyle(dStyle);
				
				HSSFCell cellRegistreEstat = xlsRow.createCell(12);
				String propietat = "registre.proces.estat.enum." + registre.getProcesEstat().toString();
				String estatDescripcio = getMessage(
						request, 
						propietat,
						null);
				cellRegistreEstat.setCellValue(estatDescripcio);
				cellRegistreEstat.setCellStyle(dStyle);
				
				HSSFCell cellRegistreDarrerMoviment = xlsRow.createCell(13);
				String bustiaOrigen = contingutService.cercarBustia(registre.getId());
				cellRegistreDarrerMoviment.setCellValue(bustiaOrigen);
				cellRegistreDarrerMoviment.setCellStyle(dStyle);

			} catch (Exception e) {
				//logger.error("Export Excel: No s'ha pogut crear la línia: " + rowNum + " - amb bustiaId: " + bustiaDto.getId(), e);
				e.printStackTrace();
			}
		}
		for(int i=0; i<5; i++)
			sheet.autoSizeColumn(i);

		try {
			String fileName = "Anotacions." + extensio;
			response.setHeader("Pragma", "");
			response.setHeader("Expires", "");
			response.setHeader("Cache-Control", "");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			response.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
			wb.write( response.getOutputStream() );
		} catch (Exception e) {
			//logger.error("No s'ha pogut realitzar la exportació.");
			e.printStackTrace();
		}
	}
	
	
	private void createHeader(
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
		cell.setCellValue(new HSSFRichTextString("Número"));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString("Data"));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString("Oficina"));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString("Presencial"));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString("Extracte"));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString("Documentació física"));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString("Codi procediment"));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString("Observacions"));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString("Origen num"));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString("Origen data"));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString("Origen oficina"));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString("Interessats"));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString("Estat"));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString("Bústia"));
		cell.setCellStyle(headerStyle);
		
	}

}
