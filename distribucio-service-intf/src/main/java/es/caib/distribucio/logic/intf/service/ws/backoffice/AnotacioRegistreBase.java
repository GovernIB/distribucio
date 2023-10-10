/**
 * 
 */
package es.caib.distribucio.logic.intf.service.ws.backoffice;

import java.util.Date;
import java.util.List;

/**
 * Classe que representa la base d'anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AnotacioRegistreBase {

	private String identificador;
	private String procedimentCodi;
	private String extracte;
	private Date data;
	private String entitatCodi;
	private String entitatDescripcio;
	private String usuariCodi;
	private String usuariNom;
	private String oficinaCodi;
	private String oficinaDescripcio;
	private String llibreCodi;
	private String llibreDescripcio;
	private String docFisicaCodi;
	private String docFisicaDescripcio;
	private String assumpteTipusCodi;
	private String assumpteTipusDescripcio;
	private String assumpteCodiCodi;
	private String assumpteCodiDescripcio;
	private String transportTipusCodi;
	private String transportTipusDescripcio;
	private String transportNumero;
	private String idiomaCodi;
	private String idomaDescripcio;
	private String observacions;
	private String origenRegistreNumero;
	private Date origenData;
	private String aplicacioCodi;
	private String aplicacioVersio;
	private String refExterna;
	private String expedientNumero;
	private String exposa;
	private String solicita;
	private List<Interessat> interessats;
	private List<Annex> annexos;
	private String justificantFitxerArxiuUuid;
		
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public String getExtracte() {
		return extracte;
	}
	public void setExtracte(String extracte) {
		this.extracte = extracte;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getEntitatCodi() {
		return entitatCodi;
	}
	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}
	public String getEntitatDescripcio() {
		return entitatDescripcio;
	}
	public void setEntitatDescripcio(String entitatDescripcio) {
		this.entitatDescripcio = entitatDescripcio;
	}
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}
	public String getUsuariNom() {
		return usuariNom;
	}
	public void setUsuariNom(String usuariNom) {
		this.usuariNom = usuariNom;
	}
	public String getOficinaCodi() {
		return oficinaCodi;
	}
	public void setOficinaCodi(String oficinaCodi) {
		this.oficinaCodi = oficinaCodi;
	}
	public String getOficinaDescripcio() {
		return oficinaDescripcio;
	}
	public void setOficinaDescripcio(String oficinaDescripcio) {
		this.oficinaDescripcio = oficinaDescripcio;
	}
	public String getLlibreCodi() {
		return llibreCodi;
	}
	public void setLlibreCodi(String llibreCodi) {
		this.llibreCodi = llibreCodi;
	}
	public String getLlibreDescripcio() {
		return llibreDescripcio;
	}
	public void setLlibreDescripcio(String llibreDescripcio) {
		this.llibreDescripcio = llibreDescripcio;
	}
	public String getDocFisicaCodi() {
		return docFisicaCodi;
	}
	public void setDocFisicaCodi(String docFisicaCodi) {
		this.docFisicaCodi = docFisicaCodi;
	}
	public String getDocFisicaDescripcio() {
		return docFisicaDescripcio;
	}
	public void setDocFisicaDescripcio(String docFisicaDescripcio) {
		this.docFisicaDescripcio = docFisicaDescripcio;
	}
	public String getAssumpteTipusCodi() {
		return assumpteTipusCodi;
	}
	public void setAssumpteTipusCodi(String assumpteTipusCodi) {
		this.assumpteTipusCodi = assumpteTipusCodi;
	}
	public String getAssumpteTipusDescripcio() {
		return assumpteTipusDescripcio;
	}
	public void setAssumpteTipusDescripcio(String assumpteTipusDescripcio) {
		this.assumpteTipusDescripcio = assumpteTipusDescripcio;
	}
	public String getAssumpteCodiCodi() {
		return assumpteCodiCodi;
	}
	public void setAssumpteCodiCodi(String assumpteCodiCodi) {
		this.assumpteCodiCodi = assumpteCodiCodi;
	}
	public String getAssumpteCodiDescripcio() {
		return assumpteCodiDescripcio;
	}
	public void setAssumpteCodiDescripcio(String assumpteCodiDescripcio) {
		this.assumpteCodiDescripcio = assumpteCodiDescripcio;
	}
	public String getTransportTipusCodi() {
		return transportTipusCodi;
	}
	public void setTransportTipusCodi(String transportTipusCodi) {
		this.transportTipusCodi = transportTipusCodi;
	}
	public String getTransportTipusDescripcio() {
		return transportTipusDescripcio;
	}
	public void setTransportTipusDescripcio(String transportTipusDescripcio) {
		this.transportTipusDescripcio = transportTipusDescripcio;
	}
	public String getTransportNumero() {
		return transportNumero;
	}
	public void setTransportNumero(String transportNumero) {
		this.transportNumero = transportNumero;
	}
	public String getIdiomaCodi() {
		return idiomaCodi;
	}
	public void setIdiomaCodi(String idiomaCodi) {
		this.idiomaCodi = idiomaCodi;
	}
	public String getIdomaDescripcio() {
		return idomaDescripcio;
	}
	public void setIdomaDescripcio(String idomaDescripcio) {
		this.idomaDescripcio = idomaDescripcio;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public String getOrigenRegistreNumero() {
		return origenRegistreNumero;
	}
	public void setOrigenRegistreNumero(String origenRegistreNumero) {
		this.origenRegistreNumero = origenRegistreNumero;
	}
	public Date getOrigenData() {
		return origenData;
	}
	public void setOrigenData(Date origenData) {
		this.origenData = origenData;
	}
	public String getAplicacioCodi() {
		return aplicacioCodi;
	}
	public void setAplicacioCodi(String aplicacioCodi) {
		this.aplicacioCodi = aplicacioCodi;
	}
	public String getAplicacioVersio() {
		return aplicacioVersio;
	}
	public void setAplicacioVersio(String aplicacioVersio) {
		this.aplicacioVersio = aplicacioVersio;
	}
	public String getRefExterna() {
		return refExterna;
	}
	public void setRefExterna(String refExterna) {
		this.refExterna = refExterna;
	}
	public String getExpedientNumero() {
		return expedientNumero;
	}
	public void setExpedientNumero(String expedientNumero) {
		this.expedientNumero = expedientNumero;
	}
	public String getExposa() {
		return exposa;
	}
	public void setExposa(String exposa) {
		this.exposa = exposa;
	}
	public String getSolicita() {
		return solicita;
	}
	public void setSolicita(String solicita) {
		this.solicita = solicita;
	}
	public List<Interessat> getInteressats() {
		return interessats;
	}
	public void setInteressats(List<Interessat> interessats) {
		this.interessats = interessats;
	}
	public List<Annex> getAnnexos() {
		return annexos;
	}
	public void setAnnexos(List<Annex> annexos) {
		this.annexos = annexos;
	}
	public String getJustificantFitxerArxiuUuid() {
		return justificantFitxerArxiuUuid;
	}
	public void setJustificantFitxerArxiuUuid(String justificantFitxerArxiuUuid) {
		this.justificantFitxerArxiuUuid = justificantFitxerArxiuUuid;
	}
}
