package es.caib.distribucio.war.helper;

import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class JsonDadesBustia implements Comparable<JsonDadesUo>{


	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone="Europe/Madrid")
    private Date fecha;
    private String uoCodi;
    private String uo;
    private Long bustiaId;
	private String nom;
	private Long usuaris;
	private Long usuarisPermis;
	private Long usuarisRol;
	
	public JsonDadesBustia(Date fecha, String uoCodi, String uo, Long bustiaId, String nom, Long usuaris,
			Long usuarisPermis, Long usuarisRol) {
		super();
		this.fecha = fecha;
		this.uoCodi = uoCodi;
		this.uo = uo;
		this.bustiaId = bustiaId;
		this.nom = nom;
		this.usuaris = usuaris;
		this.usuarisPermis = usuarisPermis;
		this.usuarisRol = usuarisRol;
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

	public Long getBustiaId() {
		return bustiaId;
	}

	public void setBustiaId(Long bustiaId) {
		this.bustiaId = bustiaId;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public Long getUsuaris() {
		return usuaris;
	}

	public void setUsuaris(Long usuaris) {
		this.usuaris = usuaris;
	}

	public Long getUsuarisPermis() {
		return usuarisPermis;
	}

	public void setUsuarisPermis(Long usuarisPermis) {
		this.usuarisPermis = usuarisPermis;
	}

	public Long getUsuarisRol() {
		return usuarisRol;
	}

	public void setUsuarisRol(Long usuarisRol) {
		this.usuarisRol = usuarisRol;
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
