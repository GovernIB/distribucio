/**
 * 
 */
package es.caib.distribucio.core.api.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Informació sobre contingut pendent d'una bústia.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BustiaContingutDto implements Serializable {

	private Long id;
	private ContingutTipusEnumDto tipus;
	private String nom;
	private String remitent;
	private Date recepcioData;
	private String comentari;
	private long pareId;
	private List<ContingutDto> path;
	private RegistreProcesEstatSimpleEnumDto procesEstatSimple;
	private boolean procesAutomatic;
	private boolean error;
	private long numComentaris;
	private boolean alerta;
	private String numeroOrigen;
	private boolean isBustiaActiva;

	public String getNumeroOrigen() {
		return numeroOrigen;
	}
	public void setNumeroOrigen(String numeroOrigen) {
		this.numeroOrigen = numeroOrigen;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public ContingutTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(ContingutTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getRemitent() {
		return remitent;
	}
	public void setRemitent(String remitent) {
		this.remitent = remitent;
	}
	public Date getRecepcioData() {
		return recepcioData;
	}
	public void setRecepcioData(Date recepcioData) {
		this.recepcioData = recepcioData;
	}
	public String getComentari() {
		return comentari;
	}
	public void setComentari(String comentari) {
		this.comentari = comentari;
	}
	public long getPareId() {
		return pareId;
	}
	public void setPareId(long pareId) {
		this.pareId = pareId;
	}
	public List<ContingutDto> getPath() {
		return path;
	}
	public void setPath(List<ContingutDto> path) {
		this.path = path;
	}
	public RegistreProcesEstatSimpleEnumDto getProcesEstatSimple() {
		return procesEstatSimple;
	}
	public void setProcesEstatSimple(RegistreProcesEstatSimpleEnumDto procesEstatSimple) {
		this.procesEstatSimple = procesEstatSimple;
	}
	public boolean isProcesAutomatic() {
		return procesAutomatic;
	}
	public void setProcesAutomatic(boolean procesAutomatic) {
		this.procesAutomatic = procesAutomatic;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public long getNumComentaris() {
		return numComentaris;
	}
	public void setNumComentaris(long numComentaris) {
		this.numComentaris = numComentaris;
	}
	public boolean isAlerta() {
		return alerta;
	}
	public void setAlerta(boolean alerta) {
		this.alerta = alerta;
	}

	public boolean isBustiaActiva() {
		return isBustiaActiva;
	}
	public void setBustiaActiva(boolean isBustiaActiva) {
		this.isBustiaActiva = isBustiaActiva;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
