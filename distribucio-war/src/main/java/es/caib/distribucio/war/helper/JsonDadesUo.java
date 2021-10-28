package es.caib.distribucio.war.helper;

import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class JsonDadesUo implements Comparable<JsonDadesUo>{


	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Madrid")
    private Date fecha;
    private String uoCodi;
    private String uo;
    private Long anotacionsNoves;
    private Long anotacionsTotals;
    private Long numAnotacionsReenviades;
	private Long numAnotacionsEmail;
    private Long numJustificants;
    private Long numAnnexos;
    private Long numBusties;
    private Long numUsuaris;
    
    public JsonDadesUo(Date fecha, String uoCodi, String uo, Long anotacionsNoves, 
    		Long anotacionsTotals, Long numAnotacionsReenviades, Long numAnotacionsEmail, Long numJustificants,
    		Long numAnnexos, Long numBusties, Long numUsuaris){
    	this.fecha = fecha;
    	this.uoCodi = uoCodi;
    	this.uo = uo;
    	this.anotacionsNoves = anotacionsNoves;
    	this.anotacionsTotals = anotacionsTotals;
    	this.numAnotacionsReenviades = numAnotacionsReenviades;
    	this.numAnotacionsEmail = numAnotacionsEmail;
    	this.numJustificants = numJustificants;
    	this.numAnnexos = numAnnexos;
    	this.numBusties = numBusties;
    	this.numUsuaris = numUsuaris;
    }
	@Override
	public int compareTo(JsonDadesUo o) {
		return this.fecha.compareTo(o.getFecha());
	}

	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Long getAnotacionsNoves() {
		return anotacionsNoves;
	}
	public void setAnotacionsNoves(Long anotacionsNoves) {
		this.anotacionsNoves = anotacionsNoves;
	}
	public Long getAnotacionsTotals() {
		return anotacionsTotals;
	}
	public void setAnotacionsTotals(Long anotacionsTotals) {
		this.anotacionsTotals = anotacionsTotals;
	}
		public String getUoCodi() {
		return uoCodi;
	}
	public void setUoCodi(String uoCodi) {
		this.uoCodi = uoCodi;
	}
	public String getUo() {
		return uo;
	}
	public void setUo(String uo) {
		this.uo = uo;
	}
	public Long getNumAnotacionsReenviades() {
		return numAnotacionsReenviades;
	}
	public void setNumAnotacionsReenviades(Long numAnotacionsReenviades) {
		this.numAnotacionsReenviades = numAnotacionsReenviades;
	}
	public Long getNumAnotacionsEmail() {
		return numAnotacionsEmail;
	}
	public void setNumAnotacionsEmail(Long numAnotacionsEmail) {
		this.numAnotacionsEmail = numAnotacionsEmail;
	}
	public Long getNumJustificants() {
		return numJustificants;
	}
	public void setNumJustificants(Long numJustificants) {
		this.numJustificants = numJustificants;
	}
	public Long getNumAnnexos() {
		return numAnnexos;
	}
	public void setNumAnnexos(Long numAnnexos) {
		this.numAnnexos = numAnnexos;
	}
	public Long getNumBusties() {
		return numBusties;
	}
	public void setNumBusties(Long numBusties) {
		this.numBusties = numBusties;
	}
	public Long getNumUsuaris() {
		return numUsuaris;
	}
	public void setNumUsuaris(Long numUsuaris) {
		this.numUsuaris = numUsuaris;
	}
    
	public int getMes() {
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(fecha);
		 return cal.get(Calendar.MONTH) + 1;
	}
	
	public String getMesNom() {

		switch (getMes()) {
		case 1:
			return "Gener";
		case 2:
			return "Febrer";
		case 3:
			return "Març";
		case 4:
			return "Abril";
		case 5:
			return "Maig";
		case 6:
			return "Juny";
		case 7:
			return "Juliol";
		case 8:
			return "Agost";
		case 9:
			return "Setembre";
		case 10:
			return "Octubre";
		case 11:
			return "Novembre";
		case 12:
			return "Desembre";
		default:
			return null;
		}
	}

}
