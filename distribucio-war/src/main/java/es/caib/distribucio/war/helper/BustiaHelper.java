/**
 * 
 */
package es.caib.distribucio.war.helper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.distribucio.logic.intf.dto.BustiaDto;
import es.caib.distribucio.logic.intf.dto.UsuariPermisDto;
import es.caib.distribucio.logic.intf.service.BustiaService;

@Component
public class BustiaHelper {
	
	@Autowired
	private BustiaService bustiaService;

	class BustiesUnitatOrganicaComparador implements Comparator<BustiaDto> {
	    @Override
	    public int compare(BustiaDto bustia1, BustiaDto bustia2) {
	        return bustia1.getUnitatOrganitzativa().getCodi().compareTo(bustia2.getUnitatOrganitzativa().getCodi());
	    }
	}
	
	public void generarExcelUsuarisPermissionsPerBustia(
			HttpServletResponse response,
			List<BustiaDto> busties) {
		
		Collections.sort(busties, new BustiesUnitatOrganicaComparador());

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
		for (BustiaDto bustiaDto : busties) {
			try {
				
				List<UsuariPermisDto> usuaris =  bustiaService.getUsuarisPerBustia(bustiaDto.getId());
				
				for (UsuariPermisDto usuariPermisDto : usuaris) {
					HSSFRow xlsRow = sheet.createRow(rowNum++);
					
					HSSFCell cellUnitatCodi = xlsRow.createCell(0);
					cellUnitatCodi.setCellValue(bustiaDto.getUnitatOrganitzativa().getCodi());
					cellUnitatCodi.setCellStyle(dStyle);
					
					HSSFCell cellUnitatNom = xlsRow.createCell(1);
					cellUnitatNom.setCellValue(bustiaDto.getUnitatOrganitzativa().getNom());
					cellUnitatNom.setCellStyle(dStyle);
					
					HSSFCell cellBustia = xlsRow.createCell(2);
					cellBustia.setCellValue(bustiaDto.getNom());
					cellBustia.setCellStyle(dStyle);

					HSSFCell cellCodiUsuari = xlsRow.createCell(3);
					cellCodiUsuari.setCellValue(usuariPermisDto.getCodi());
					cellCodiUsuari.setCellStyle(dStyle);
					
					HSSFCell cellNomUsuari = xlsRow.createCell(4);
					cellNomUsuari.setCellValue(usuariPermisDto.getNom());
					cellNomUsuari.setCellStyle(dStyle);
					
				}


			} catch (Exception e) {
				logger.error("Export Excel: No s'ha pogut crear la línia: " + rowNum + " - amb bustiaId: " + bustiaDto.getId(), e);
			}
		}
		for(int i=0; i<5; i++)
			sheet.autoSizeColumn(i);

		try {
			String fileName = "UsuarisPerBustia.xls";
			response.setHeader("Pragma", "");
			response.setHeader("Expires", "");
			response.setHeader("Cache-Control", "");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			response.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
			wb.write( response.getOutputStream() );
		} catch (Exception e) {
			logger.error("No s'ha pogut realitzar la exportació.", e);
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
		cell.setCellValue(new HSSFRichTextString("Codi unitat organitzativa"));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString("Nom unitat organitzativa"));
		cell.setCellStyle(headerStyle);

		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString("Bustia"));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString("Codi usuari"));
		cell.setCellStyle(headerStyle);
		
		cell = xlsRow.createCell(colNum++);
		cell.setCellValue(new HSSFRichTextString("Nom usuari"));
		cell.setCellStyle(headerStyle);
		
	}


	private static final Logger logger = LoggerFactory.getLogger(BustiaHelper.class);

}
