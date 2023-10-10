/**
 * 
 */
package es.caib.distribucio.plugin.distribucio;

import java.util.Date;
import java.util.List;

import es.caib.distribucio.logic.intf.registre.ValidacioFirmaEnum;

/**
 * Classe que representa un annex d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DistribucioRegistreAnnex {

	private Long id;
	private Date dataCaptura;
	private String gesdocDocumentId;
	private String origenCiutadaAdmin;
	private String ntiTipusDocument;
	private String sicresTipusDocument;
	private String ntiElaboracioEstat;
	private String titol;
	private String fitxerNom;
	private int fitxerTamany;
	private String fitxerTipusMime;
	private String fitxerArxiuUuid;
	private List<DistribucioRegistreFirma> firmes;
	private String metaDades;
	
	private ValidacioFirmaEnum validacioFirmaEstat;
	private String validacioFirmaError;
	
	private Integer procesIntents;
	
	
	
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
	
	public Date getDataCaptura() {
		return dataCaptura;
	}
	public void setDataCaptura(Date dataCaptura) {
		this.dataCaptura = dataCaptura;
	}
	public String getGesdocDocumentId() {
		return gesdocDocumentId;
	}
	public void setGesdocDocumentId(String gesdocDocumentId) {
		this.gesdocDocumentId = gesdocDocumentId;
	}
	public String getSicresTipusDocument() {
		return sicresTipusDocument;
	}
	public void setSicresTipusDocument(String sicresTipusDocument) {
		this.sicresTipusDocument = sicresTipusDocument;
	}
	public List<DistribucioRegistreFirma> getFirmes() {
		return firmes;
	}
	public void setFirmes(List<DistribucioRegistreFirma> firmes) {
		this.firmes = firmes;
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
	public String getNtiElaboracioEstat() {
		return ntiElaboracioEstat;
	}
	public void setNtiElaboracioEstat(String ntiElaboracioEstat) {
		this.ntiElaboracioEstat = ntiElaboracioEstat;
	}
	public String getMetaDades() {
		return metaDades;
	}
	public void setMetaDades(String metaDades) {
		this.metaDades = metaDades;
	}
	public ValidacioFirmaEnum getValidacioFirmaEstat() {
		return validacioFirmaEstat;
	}
	public void setValidacioFirmaEstat(ValidacioFirmaEnum validacioFirmaEstat) {
		this.validacioFirmaEstat = validacioFirmaEstat;
	}
	public String getValidacioFirmaError() {
		return validacioFirmaError;
	}
	public void setFirmaValidacioDesc(String firmaValidacioDesc) {
		this.validacioFirmaError = firmaValidacioDesc;
	}
	/** Guarda el número d'intents del procés per guardar l'annex a l'Arxiu. Si se superen es guardarà
	 * com esborrany.
	 * 
	 * @param procesIntents
	 */
	public void setPocesIntents(Integer procesIntents) {
		this.procesIntents = procesIntents;
	}
	public int getProcesIntents() {
		return this.procesIntents != null ? procesIntents.intValue() : 0;
	}
}
