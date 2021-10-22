package es.caib.distribucio.war.helper;

import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class JsonDadesEstat implements Comparable<JsonDadesEstat>{


	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone="Europe/Madrid")
    private Date fecha;
    private String uoCodi;
    private String uo;
    private Integer anotacionsNoves;
    private Integer anotacionsTotals;
    private Integer numAnotacionsReenviades;
	private Integer numAnotacionsEmail;
    private Integer numJustificants;
    private Integer numAnnexos;
    private Integer numBusties;
    private Integer numUsuaris;
    
    public JsonDadesEstat(Date fecha, String uoCodi, String uo, Integer anotacionsNoves, 
    		Integer anotacionsTotals, Integer numAnotacionsReenviades, Integer numAnotacionsEmail, Integer numJustificants,
    		Integer numAnnexos, Integer numBusties, Integer numUsuaris){
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
	public int compareTo(JsonDadesEstat o) {
		return this.fecha.compareTo(o.getFecha());
	}

	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Integer getAnotacionsNoves() {
		return anotacionsNoves;
	}
	public void setAnotacionsNoves(Integer anotacionsNoves) {
		this.anotacionsNoves = anotacionsNoves;
	}
	public Integer getAnotacionsTotals() {
		return anotacionsTotals;
	}
	public void setAnotacionsTotals(Integer anotacionsTotals) {
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
	public Integer getNumAnotacionsReenviades() {
		return numAnotacionsReenviades;
	}
	public void setNumAnotacionsReenviades(Integer numAnotacionsReenviades) {
		this.numAnotacionsReenviades = numAnotacionsReenviades;
	}
	public Integer getNumAnotacionsEmail() {
		return numAnotacionsEmail;
	}
	public void setNumAnotacionsEmail(Integer numAnotacionsEmail) {
		this.numAnotacionsEmail = numAnotacionsEmail;
	}
	public Integer getNumJustificants() {
		return numJustificants;
	}
	public void setNumJustificants(Integer numJustificants) {
		this.numJustificants = numJustificants;
	}
	public Integer getNumAnnexos() {
		return numAnnexos;
	}
	public void setNumAnnexos(Integer numAnnexos) {
		this.numAnnexos = numAnnexos;
	}
	public Integer getNumBusties() {
		return numBusties;
	}
	public void setNumBusties(Integer numBusties) {
		this.numBusties = numBusties;
	}
	public Integer getNumUsuaris() {
		return numUsuaris;
	}
	public void setNumUsuaris(Integer numUsuaris) {
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
