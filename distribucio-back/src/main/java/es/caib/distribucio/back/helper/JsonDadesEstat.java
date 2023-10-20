package es.caib.distribucio.back.helper;

import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class JsonDadesEstat implements Comparable<JsonDadesEstat>{


	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone="Europe/Madrid")
    private Date fecha;
    private String estat;
    private Long correcte;
	private Long correcteTotal;
	private Long error;
	private Long errorTotal;
	private Long total;
        
	public JsonDadesEstat(Date fecha, String estat, Long correcte, Long correcteTotal, Long error,
			Long errorTotal, Long total) {
		this.fecha = fecha;
		this.estat = estat;
		this.correcte = correcte;
		this.correcteTotal = correcteTotal;
		this.error = error;
		this.errorTotal = errorTotal;
		this.total = total;
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

	public String getEstat() {
		return estat;
	}

	public void setEstat(String estat) {
		this.estat = estat;
	}

	public Long getCorrecte() {
		return correcte;
	}

	public void setCorrecte(Long correcte) {
		this.correcte = correcte;
	}

	public Long getCorrecteTotal() {
		return correcteTotal;
	}

	public void setCorrecteTotal(Long correcteTotal) {
		this.correcteTotal = correcteTotal;
	}

	public Long getError() {
		return error;
	}

	public void setError(Long error) {
		this.error = error;
	}

	public Long getErrorTotal() {
		return errorTotal;
	}

	public void setErrorTotal(Long errorTotal) {
		this.errorTotal = errorTotal;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
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
