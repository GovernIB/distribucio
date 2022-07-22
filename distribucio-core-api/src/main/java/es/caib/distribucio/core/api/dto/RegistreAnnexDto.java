/**
 * 
 */
package es.caib.distribucio.core.api.dto;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import es.caib.distribucio.core.api.registre.ValidacioFirmaEnum;
import es.caib.distribucio.core.api.service.ws.backoffice.AnnexEstat;

/**
 * Classe que representa una anotació de registre amb id.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RegistreAnnexDto implements Serializable {

	private Long id;
	private String titol;
	private String fitxerNom;
	private byte[] fitxerContingut;
	private int fitxerTamany;
	private String fitxerTipusMime;
	private Date dataCaptura;
	private String localitzacio;
	private String origenCiutadaAdmin;
	private String ntiTipusDocument;
	private String sicresTipusDocument;
	private String ntiElaboracioEstat;
	private String observacions;
	private Integer firmaMode;
	private String timestamp;
	private String validacioOCSP;
	private String fitxerArxiuUuid;
	private List<ArxiuFirmaDto> firmes;
	private boolean ambFirma;
	private String firmaCsv;
	private Long registreId;
	private boolean signaturaDetallsDescarregat;
	
	private Map<String, String> metaDadesMap;
	
	private ValidacioFirmaEnum validacioFirmaEstat;
	private String validacioFirmaError;

	private AnnexEstat arxiuEstat;

	private static final long serialVersionUID = -8656873728034274066L;

	
	public Map<String, String> getMetaDadesMap() {
		return metaDadesMap;
	}
	public void setMetaDadesMap(Map<String, String> metaDadesMap) {
		this.metaDadesMap = metaDadesMap;
	}
	public boolean isSignaturaDetallsDescarregat() {
		return signaturaDetallsDescarregat;
	}
	public void setSignaturaDetallsDescarregat(boolean signaturaDetallsDescarregat) {
		this.signaturaDetallsDescarregat = signaturaDetallsDescarregat;
	}
	public Long getRegistreId() {
		return registreId;
	}
	public void setRegistreId(Long registreId) {
		this.registreId = registreId;
	}
	public Long getId() {
		return id;
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
	public String getFitxerTipusMime() {
		return fitxerTipusMime;
	}
	public void setFitxerTipusMime(String fitxerTipusMime) {
		this.fitxerTipusMime = fitxerTipusMime;
	}
	public Date getDataCaptura() {
		return dataCaptura;
	}
	public void setDataCaptura(Date dataCaptura) {
		this.dataCaptura = dataCaptura;
	}
	public String getLocalitzacio() {
		return localitzacio;
	}
	public void setLocalitzacio(String localitzacio) {
		this.localitzacio = localitzacio;
	}
	public String getOrigenCiutadaAdmin() {
		return origenCiutadaAdmin;
	}
	public void setOrigenCiutadaAdmin(String origenCiutadaAdmin) {
		this.origenCiutadaAdmin = origenCiutadaAdmin;
	}
	public String getNtiTipusDocument() {
		return ntiTipusDocument;
	}
	public void setNtiTipusDocument(String ntiTipusDocument) {
		this.ntiTipusDocument = ntiTipusDocument;
	}
	public String getSicresTipusDocument() {
		return sicresTipusDocument;
	}
	public void setSicresTipusDocument(String sicresTipusDocument) {
		this.sicresTipusDocument = sicresTipusDocument;
	}
	public String getNtiElaboracioEstat() {
		return ntiElaboracioEstat;
	}
	public void setNtiElaboracioEstat(String ntiElaboracioEstat) {
		this.ntiElaboracioEstat = ntiElaboracioEstat;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public Integer getFirmaMode() {
		return firmaMode;
	}
	public void setFirmaMode(Integer firmaMode) {
		this.firmaMode = firmaMode;
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
	public String getFitxerArxiuUuid() {
		return fitxerArxiuUuid;
	}
	public void setFitxerArxiuUuid(String fitxerArxiuUuid) {
		this.fitxerArxiuUuid = fitxerArxiuUuid;
	}
	public List<ArxiuFirmaDto> getFirmes() {
		return firmes;
	}
	public void setFirmes(List<ArxiuFirmaDto> firmes) {
		this.firmes = firmes;
	}

	public boolean isAmbFirma() {
		return ambFirma;
	}
	public void setAmbFirma(boolean ambFirma) {
		this.ambFirma = ambFirma;
	}
	public int getFitxerTamany() {
		return fitxerTamany;
	}
	public void setFitxerTamany(int fitxerTamany) {
		this.fitxerTamany = fitxerTamany;
	}
	public void setFirmaCsv(String csv) {
		this.firmaCsv = csv;
	}
	public String getFirmaCsv() {
		return this.firmaCsv;
	}
	public byte[] getFitxerContingut() {
		return fitxerContingut;
	}
	public void setFitxerContingut(byte[] fitxerContingut) {
		this.fitxerContingut = fitxerContingut;
	}
	
	public String getFitxerExtension() {
		return fitxerNom != null && fitxerNom.contains(".") ?
				fitxerNom.substring(fitxerNom.lastIndexOf('.') + 1, fitxerNom.length())
				: null;
	}
	
	private static String[] tamanyUnitats = {"b", "Kb", "Mb", "Gb", "Tb", "Pb"};
	
	public String getFitxerTamanyStr() {
		double valor = this.fitxerTamany;
		int i = 0;
		while (this.fitxerTamany > Math.pow(1024, i + 1) 
				&& i < tamanyUnitats.length - 1) {
			valor = valor / 1024;
			i++;
		}
		DecimalFormat df = new DecimalFormat("#,###.##");
		return df.format(valor) + " " + tamanyUnitats[i];
	}
	public ValidacioFirmaEnum getValidacioFirmaEstat() {
		return validacioFirmaEstat;
	}
	public void setValidacioFirmaEstat(ValidacioFirmaEnum validacoFirmaEstat) {
		this.validacioFirmaEstat = validacoFirmaEstat;
	}
	public String getValidacioFirmaError() {
		return validacioFirmaError;
	}
	public void setValidacioFirmaError(String validacioFirmaError) {
		this.validacioFirmaError = validacioFirmaError;
	}
	public AnnexEstat getArxiuEstat() {
		return arxiuEstat;
	}
	public void setArxiuEstat(AnnexEstat arxiuEstat) {
		this.arxiuEstat = arxiuEstat;
	}


}
