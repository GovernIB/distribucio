package es.caib.distribucio.back.helper;

import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class JsonDadesBustia implements Comparable<JsonDadesBustia>{


	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone="Europe/Madrid")
    private Date fecha;
    private String uoCodi;
    private String uo;
    private Long bustiaId;
	private String nom;
	private Long usuaris;
	private Long usuarisPermis;
	private Long usuarisRol;
	private Long noves;
	private Long errors;
	private Long pendents;

	public JsonDadesBustia(Date fecha, String uoCodi, String uo, Long bustiaId, String nom, Long usuaris,
			Long usuarisPermis, Long usuarisRol, Long noves, Long errors, Long pendents) {
		super();
		this.fecha = fecha;
		this.uoCodi = uoCodi;
		this.uo = uo;
		this.bustiaId = bustiaId;
		this.nom = nom;
		this.usuaris = usuaris;
		this.usuarisPermis = usuarisPermis;
		this.usuarisRol = usuarisRol;
		this.noves = noves;
		this.errors = errors;
		this.pendents = pendents;
	}

	@Override
	public int compareTo(JsonDadesBustia o) {
		return this.fecha.compareTo(o.getFecha());
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
