package es.caib.distribucio.core.api.dto.historic;

import java.util.Date;

import es.caib.distribucio.core.api.dto.EntitatDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;

/**
 * Informació de dades d'anotacions per bústia.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HistoricBustiaDto {

	private EntitatDto entitat;
	private UnitatOrganitzativaDto unitat;
	private HistoricTipusEnumDto tipus;
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
