/**
 * 
 */
package es.caib.distribucio.core.api.service.ws.backoffice;

import java.util.Date;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Classe que representa un annex d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Annex {

	private String titol;
	private String nom;
	private byte[] contingut;
	private String uuid;
	private long tamany;
	private String tipusMime;
	private NtiTipoDocumento ntiTipoDocumental;
	private NtiOrigen ntiOrigen;
	private Date ntiFechaCaptura;
	private SicresTipoDocumento sicresTipoDocumento;
	private SicresValidezDocumento sicresValidezDocumento;
	private NtiEstadoElaboracion ntiEstadoElaboracion;
	private String observacions;
	private FirmaTipus firmaTipus;
	private FirmaPerfil firmaPerfil;
	private byte[] firmaContingut;
	private long firmaTamany;
	private String firmaNom;
	private String firmaTipusMime;

	private boolean documentValid;
	private String documentError;

	
	public String getFirmaTipusMime() {
		return firmaTipusMime;
	}
	public void setFirmaTipusMime(String firmaTipusMime) {
		this.firmaTipusMime = firmaTipusMime;
	}
	public String getFirmaNom() {
		return firmaNom;
	}
	public void setFirmaNom(String firmaNom) {
		this.firmaNom = firmaNom;
	}
	public NtiEstadoElaboracion getNtiEstadoElaboracion() {
		return ntiEstadoElaboracion;
	}
	public void setNtiEstadoElaboracion(NtiEstadoElaboracion ntiEstadoElaboracion) {
		this.ntiEstadoElaboracion = ntiEstadoElaboracion;
	}
	public String getTitol() {
		return titol;
	}
	public void setTitol(String titol) {
		this.titol = titol;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public byte[] getContingut() {
		return contingut;
	}
	public void setContingut(byte[] contingut) {
		this.contingut = contingut;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public long getTamany() {
		return tamany;
	}
	public void setTamany(long tamany) {
		this.tamany = tamany;
	}
	public String getTipusMime() {
		return tipusMime;
	}
	public void setTipusMime(String tipusMime) {
		this.tipusMime = tipusMime;
	}
	public NtiTipoDocumento getNtiTipoDocumental() {
		return ntiTipoDocumental;
	}
	public void setNtiTipoDocumental(NtiTipoDocumento ntiTipoDocumental) {
		this.ntiTipoDocumental = ntiTipoDocumental;
	}
	public NtiOrigen getNtiOrigen() {
		return ntiOrigen;
	}
	public void setNtiOrigen(NtiOrigen ntiOrigen) {
		this.ntiOrigen = ntiOrigen;
	}
	public Date getNtiFechaCaptura() {
		return ntiFechaCaptura;
	}
	public void setNtiFechaCaptura(Date ntiFechaCaptura) {
		this.ntiFechaCaptura = ntiFechaCaptura;
	}
	public SicresTipoDocumento getSicresTipoDocumento() {
		return sicresTipoDocumento;
	}
	public void setSicresTipoDocumento(SicresTipoDocumento sicresTipoDocumento) {
		this.sicresTipoDocumento = sicresTipoDocumento;
	}
	public SicresValidezDocumento getSicresValidezDocumento() {
		return sicresValidezDocumento;
	}
	public void setSicresValidezDocumento(SicresValidezDocumento sicresValidezDocumento) {
		this.sicresValidezDocumento = sicresValidezDocumento;
	}
	public String getObservacions() {
		return observacions;
	}
	public void setObservacions(String observacions) {
		this.observacions = observacions;
	}
	public FirmaTipus getFirmaTipus() {
		return firmaTipus;
	}
	public void setFirmaTipus(FirmaTipus firmaTipus) {
		this.firmaTipus = firmaTipus;
	}
	public FirmaPerfil getFirmaPerfil() {
		return firmaPerfil;
	}
	public void setFirmaPerfil(FirmaPerfil firmaPerfil) {
		this.firmaPerfil = firmaPerfil;
	}
	public byte[] getFirmaContingut() {
		return firmaContingut;
	}
	public void setFirmaContingut(byte[] firmaContingut) {
		this.firmaContingut = firmaContingut;
	}
	public long getFirmaTamany() {
		return firmaTamany;
	}
	public void setFirmaTamany(long firmaTamany) {
		this.firmaTamany = firmaTamany;
	}
	@XmlTransient
	public boolean isDocumentValid() {
		return documentValid;
	}
	public void setDocumentValid(boolean documentValid) {
		this.documentValid = documentValid;
	}
	@XmlTransient
	public String getDocumentError() {
		return documentError;
	}
	public void setDocumentError(String documentError) {
		this.documentError = documentError;
	}

}
