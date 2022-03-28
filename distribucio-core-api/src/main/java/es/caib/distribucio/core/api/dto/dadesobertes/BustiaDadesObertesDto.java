package es.caib.distribucio.core.api.dto.dadesobertes;

import java.io.Serializable;

import es.caib.distribucio.core.api.dto.BustiaContingutDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;

public class BustiaDadesObertesDto implements Serializable{
	
	private Long id;
	private String nom;
	private String uo;
	private String uoNom;
	private String uoSuperior;
	private String uoSuperiorNom;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long bustiaId) {
		this.id = bustiaId;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String bustiaName) {
		this.nom = bustiaName;
	}
	public String getUO() {
		return uo;
	}
	public void setUO(String codiUO) {
		this.uo = codiUO;
	}
	public String getUoNom() {
		return uoNom;
	}
	public void setUoNom(String uoName) {
		this.uoNom = uoName;
	}
	public String getUOsuperior() {
		return uoSuperior;
	}
	public void setUOsuperior(String codiUOsuperior) {
		this.uoSuperior = codiUOsuperior;
	}
	public String getUOsuperiorNom() {
		return uoSuperiorNom;
	}
	public void setUOsuperiorNom(String nameUOsuperior) {
		this.uoSuperiorNom = nameUOsuperior;
	}

}
