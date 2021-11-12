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
 * Informació de dades d'anotacions per bústia.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@XmlRootElement(name = "dadaBustia")
@XmlAccessorType (XmlAccessType.PROPERTY)
public class HistoricBustiaDto {

    @JsonIgnore
	@XmlTransient
	private EntitatDto entitat;
    @JsonIgnore
	@XmlTransient
	private UnitatOrganitzativaDto unitat;

	private HistoricTipusEnumDto tipus;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", timezone="Europe/Madrid")
	private Date data;

	private Long bustiaId;
	private String nom;
	private Long usuaris;
	private Long usuarisPermis;
	private Long usuarisRol;
	
	public EntitatDto getEntitat() {
		return entitat;
	}
	public void setEntitat(EntitatDto entitat) {
		this.entitat = entitat;
	}
	public UnitatOrganitzativaDto getUnitat() {
		return unitat;
	}
	@XmlElement(name = "unitatCodi")
	public String getUnitatCodi() {
		return this.unitat != null ? this.unitat.getCodi() : null;
	}
	@XmlElement(name = "unitatNom")
	public String getUnitatNom() {
		return this.unitat != null ? this.unitat.getDenominacio() : null;
	}
	public void setUnitat(UnitatOrganitzativaDto unitat) {
		this.unitat = unitat;
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
}
