/**
 * 
 */
package es.caib.distribucio.logic.intf.registre;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Classe que representa un annex d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreAnnex {

	private Long id;
	private String titol;//200
	private String fitxerNom;//comes from document custody plugin
	private int fitxerTamany;
	private String fitxerTipusMime;//comes from document custody plugin
	private String fitxerArxiuUuid;//256
	private byte[] fitxerContingut;
	private Date eniDataCaptura;
	private String eniOrigen;//10 (Integer to String)
	private String eniEstatElaboracio;//4
	private String eniTipusDocumental;//255
	private String sicresTipusDocument;//2
	private String localitzacio;//is not set
	private String observacions;//50
	private List<Firma> firmes;
	private String timestamp;//is not set
	private String validacioOCSP;//255
	
	private Map<String, String> metaDades;	

	public Long getId() {
		return id;
	}
	public Map<String, String> getMetaDades() {
		return metaDades;
	}
	public void setMetaDades(Map<String, String> metaDades) {
		this.metaDades = metaDades;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getFitxerNom() {
		return fitxerNom;
	}
	public void setFitxerNom(String fitxerNom) {
		this.fitxerNom = fitxerNom;
	}
	public int getFitxerTamany() {
		return fitxerTamany;
	}
	public void setFitxerTamany(int fitxerTamany) {
		this.fitxerTamany = fitxerTamany;
	}
	public String getFitxerTipusMime() {
		return fitxerTipusMime;
	}
	public void setFitxerTipusMime(String fitxerTipusMime) {
		this.fitxerTipusMime = fitxerTipusMime;
	}
	public String getFitxerArxiuUuid() {
		return fitxerArxiuUuid;
	}
	public void setFitxerArxiuUuid(String fitxerArxiuUuid) {
		this.fitxerArxiuUuid = fitxerArxiuUuid;
	}
	public byte[] getFitxerContingut() {
		return fitxerContingut;
	}
	public void setFitxerContingut(byte[] fitxerContingut) {
		this.fitxerContingut = fitxerContingut;
	}
	public Date getEniDataCaptura() {
		return eniDataCaptura;
	}
	public void setEniDataCaptura(Date eniDataCaptura) {
		this.eniDataCaptura = eniDataCaptura;
	}
	public String getEniOrigen() {
		return eniOrigen;
	}
	public void setEniOrigen(String eniOrigen) {
		this.eniOrigen = eniOrigen;
	}
	public String getEniEstatElaboracio() {
		return eniEstatElaboracio;
	}
	public void setEniEstatElaboracio(String eniEstatElaboracio) {
		this.eniEstatElaboracio = eniEstatElaboracio;
	}
	public String getEniTipusDocumental() {
		return eniTipusDocumental;
	}
	public void setEniTipusDocumental(String eniTipusDocumental) {
		this.eniTipusDocumental = eniTipusDocumental;
	}
	public String getSicresTipusDocument() {
		return sicresTipusDocument;
	}
	public void setSicresTipusDocument(String sicresTipusDocument) {
		this.sicresTipusDocument = sicresTipusDocument;
	}
	public String getLocalitzacio() {
		return localitzacio;
	}
	public void setLocalitzacio(String localitzacio) {
		this.localitzacio = localitzacio;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public List<Firma> getFirmes() {
		return firmes;
	}
	public void setFirmes(List<Firma> firmes) {
		this.firmes = firmes;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getValidacioOCSP() {
		return validacioOCSP;
	}
	public void setValidacioOCSP(String validacioOCSP) {
		this.validacioOCSP = validacioOCSP;
	}

}
