package es.caib.distribucio.core.api.dto.historic;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;

/**
 * Informació de dades d'anotacions per unitat organitzativa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@XmlRootElement(name = "dadaAnotacio")
@XmlAccessorType (XmlAccessType.FIELD)
public class HistoricAnotacioDto {

    @JsonIgnore
    @XmlTransient
	private EntitatDto entitat;
    @JsonIgnore
    @XmlTransient
	private UnitatOrganitzativaDto unitat;

	private HistoricTipusEnumDto tipus;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone="Europe/Madrid")
	private Date data;

	private Long anotacions;
	private Long anotacionsTotal;
	private Long reenviaments;
	private Long emails;
	private Long justificants;
	private Long annexos;
	private Long busties;
	private Long usuaris;
	
	public EntitatDto getEntitat() {
		return entitat;
	}
	public void setEntitat(EntitatDto entitat) {
		this.entitat = entitat;
	}
	public UnitatOrganitzativaDto getUnitat() {
		return unitat;
	}
	public void setUnitat(UnitatOrganitzativaDto unitat) {
		this.unitat = unitat;
	}
	@XmlElement(name = "unitatCodi")
	public String getUnitatCodi() {
		return this.unitat != null ? this.unitat.getCodi() : null;
	}
	@XmlElement(name = "unitatNom")
	public String getUnitatNom() {
		return this.unitat != null ? this.unitat.getNom() : null;
	}
	public HistoricTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(HistoricTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public Long getAnotacions() {
		return anotacions;
	}
	public void setAnotacions(Long anotacions) {
		this.anotacions = anotacions;
	}
	public Long getAnotacionsTotal() {
		return anotacionsTotal;
	}
	public void setAnotacionsTotal(Long anotacionsTotal) {
		this.anotacionsTotal = anotacionsTotal;
	}
	public Long getReenviaments() {
		return reenviaments;
	}
	public void setReenviaments(Long reenviaments) {
		this.reenviaments = reenviaments;
	}
	public Long getEmails() {
		return emails;
	}
	public void setEmails(Long emails) {
		this.emails = emails;
	}
	public Long getJustificants() {
		return justificants;
	}
	public void setJustificants(Long justificants) {
		this.justificants = justificants;
	}
	public Long getAnnexos() {
		return annexos;
	}
	public void setAnnexos(Long annexos) {
		this.annexos = annexos;
	}
	public Long getBusties() {
		return busties;
	}
	public void setBusties(Long busties) {
		this.busties = busties;
	}
	public Long getUsuaris() {
		return usuaris;
	}
	public void setUsuaris(Long usuaris) {
		this.usuaris = usuaris;
	}	
}
