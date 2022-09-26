package es.caib.distribucio.war.helper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import es.caib.distribucio.war.controller.BaseController;

@Component
public class RegistreHelper extends BaseController{
	
	@Autowired
	private CsvHelper csvHelper;
		
	
	public FitxerDto exportarAnotacions(
			HttpServletRequest request,
			HttpServletResponse response,
			List<RegistreDto> registres, 
			String format) throws IOException {
		
		FitxerDto fitxer = new FitxerDto();
		String[] columnes = {	"numero",
								"titol",
								"data",
								"data.presentacio",
								"oficina",
								"presencial",
								"documentacio.fisica",
								"numero.annexes", 
								"procediment",
								"observacions",
								"origenNum",
								"origenData",
								"origenOficina",
								"interessats",
								"estat",
								"uo",
								"bustia"};
		for (int i = 0; i < columnes.length; i++) {
			columnes[i] = getMessage(request, "registre.user.exportar.columna." + columnes[i]);
		}

		List<String[]> files = new ArrayList<String[]>();
		for (RegistreDto registre : registres) {
			String[] fila = new String[17];
			
			fila[0] = registre.getNumero();
			
			fila[1] = registre.getExtracte();
			
			Date data = registre.getData();
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
			String strData = dateFormat.format(data);
			fila[2] = strData;
			
			if (registre.getJustificant() != null) {
				fila[3] = registre.getJustificant().getDataCaptura().toString(); //TODO: data presentació, revisar amb en Daniel
			}else {
				fila[3] = null;
			}
			
			fila[4] = registre.getOficinaDescripcio();
			
			String presencial = "";
			if (registre.getPresencial()) {
				presencial = "Si";
			}else {
				presencial = "no";
			}
			fila[5] = presencial;
			
			fila[6] = registre.getDocumentacioFisicaDescripcio();
			
			fila[7] = String.valueOf(registre.getAnnexos().size());
			
			fila[8] = registre.getProcedimentCodi();
			
			fila[9] = registre.getObservacions();
			
			fila[10] = registre.getNumeroOrigen();
			
			Date date = registre.getDataOrigen();
			DateFormat dateFormat2 = new SimpleDateFormat("dd-mm-yyyy hh:mm:ss");
			String origenData;
			if (date == null) {
				origenData = "";
			}else {
				origenData = dateFormat2.format(date);
			}
			fila[11] = origenData;
			
			fila[12] = registre.getOficinaOrigenCodi();
			
			List<RegistreInteressat> llistatInteressats = registre.getInteressats();
			String nomComplet = "";
			for (RegistreInteressat interessat : llistatInteressats) {
				if (interessat.getNom() != null) {
					nomComplet += interessat.getNom() + " " + interessat.getLlinatge1() + " " + interessat.getLlinatge2() + "(" + interessat.getDocumentNum() + ") | ";
				} else {
					nomComplet += interessat.getRaoSocial() + "(" + interessat.getDocumentNum() + ") | ";
				}
			}
			fila[13] = nomComplet;
			
			String propietat = "registre.proces.estat.enum." + registre.getProcesEstat().toString();
			String estatDescripcio = getMessage(
					request, 
					propietat,
					null);
			fila[14] = estatDescripcio;
			
			String uo = "";
			if (registre.getPath() != null && registre.getPath().size() > 1) {
				uo = registre.getPath().get(registre.getPath().size()-2).getNom();
			}
			fila[15] = uo; 
			String bustia = "";
			if (registre.getPath() != null && registre.getPath().size() > 0) {
				bustia = registre.getPath().get(registre.getPath().size()-1).getNom();
			}
			fila[16] = bustia; 
			
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
			fitxer.setNom(getMessage(request, "registre.user.exportar.nomFitxer") + ".csv");
			fitxer.setContentType("text/csv");
			StringBuilder sb = new StringBuilder();
			csvHelper.afegirLinia(sb, columnes, ';');
			for (String[] fila : files) {
				csvHelper.afegirLinia(sb, fila, ';');
			}
			fitxer.setContingut(sb.toString().getBytes(StandardCharsets.UTF_8));
		} else {
			throw new ValidationException("Format de fitxer no suportat: " + format);
		}
		return fitxer;
		
	}

}
