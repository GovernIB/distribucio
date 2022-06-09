package es.caib.distribucio.war.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.RegistreDto;
import es.caib.distribucio.core.api.exception.ValidationException;
import es.caib.distribucio.core.api.registre.RegistreInteressat;
import es.caib.distribucio.core.api.service.ContingutService;
import es.caib.distribucio.core.helper.CsvHelper;
import es.caib.distribucio.war.controller.BaseController;

@Component
public class RegistreHelper extends BaseController{
	
	@Autowired
	private ContingutService contingutService;
	@Autowired
	private CsvHelper csvHelper;
		
	
	public FitxerDto exportarAnotacions(
			HttpServletRequest request,
			HttpServletResponse response,
			List<RegistreDto> registres, 
			String format) throws IOException {
		
		FitxerDto fitxer = new FitxerDto();
		String[] columnes = {
				"Número", 
				"Data", 
				"Oficina", 
				"Presencial", 
				"Extracte", 
				"Documentació física", 
				"Codi procediment", 
				"Observacions", 
				"Origen num", 
				"Origen data", 
				"Origen oficina", 
				"Interessats", 
				"Estat", 
				"Bústia"};

		List<String[]> files = new ArrayList<String[]>();
		for (RegistreDto registre : registres) {
			String[] fila = new String[14];
			
			fila[0] = registre.getNumero();
			
			Date data = registre.getData();
			DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
			String strData = dateFormat.format(data);
			fila[1] = strData;
			
			fila[2] = registre.getOficinaDescripcio();
			
			String presencial = "";
			if (registre.getPresencial()) {
				presencial = "Si";
			}else {
				presencial = "no";
			}
			fila[3] = presencial;
			
			fila[4] = registre.getExtracte();
			
			fila[5] = registre.getDocumentacioFisicaCodi();
			
			fila[6] = registre.getProcedimentCodi();
			
			fila[7] = registre.getObservacions();
			
			fila[8] = registre.getNumeroOrigen();
			
			Date date = registre.getDataOrigen();
			DateFormat dateFormat2 = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
			String origenData;
			if (date == null) {
				origenData = "";
			}else {
				origenData = dateFormat2.format(date);
			}
			fila[9] = origenData;
			
			fila[10] = registre.getOficinaOrigenCodi();
			
			List<RegistreInteressat> llistatInteressats = registre.getInteressats();
			String nomComplet = "";
			for (RegistreInteressat interessat : llistatInteressats) {
				nomComplet += interessat.getNom() + " " + interessat.getLlinatge1() + " " + interessat.getLlinatge2() + "(" + interessat.getDocumentNum() + ") | ";
				System.out.println(nomComplet);
			}
			fila[11] = nomComplet;
			
			String propietat = "registre.proces.estat.enum." + registre.getProcesEstat().toString();
			String estatDescripcio = getMessage(
					request, 
					propietat,
					null);
			fila[12] = estatDescripcio;
			
			String bustiaOrigen = contingutService.cercarBustia(registre.getId());
			fila[13] = bustiaOrigen; 
			
			files.add(fila);
		}
		

		if ("ODS".equalsIgnoreCase(format)) {
			Object[][] filesArray = files.toArray(new Object[files.size()][14]);
			TableModel model = new DefaultTableModel(filesArray, columnes);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			SpreadSheet.createEmpty(model).getPackage().save(baos);
			fitxer.setNom("exportacio.ods");
			fitxer.setContentType("application/vnd.oasis.opendocument.spreadsheet");
			fitxer.setContingut(baos.toByteArray());
		} else if ("CSV".equalsIgnoreCase(format)) {
			fitxer.setNom("exportacio.csv");
			fitxer.setContentType("text/csv");
			StringBuilder sb = new StringBuilder();
			csvHelper.afegirLinia(sb, columnes, ';');
			for (String[] fila : files) {
				csvHelper.afegirLinia(sb, fila, ';');
			}
			fitxer.setContingut(sb.toString().getBytes());
		} else {
			throw new ValidationException("Format de fitxer no suportat: " + format);
		}
		return fitxer;
		
	}

}
