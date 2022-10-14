package es.caib.distribucio.core.api.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ProcedimentFiltreDto implements Serializable{
	
	private String codi;
	private String nom;
	private String codiSia;
	private UnitatOrganitzativaDto unitatOrganitzativa;
	private EntitatDto entitat;	

	
	public String getCodi() {
		return codi;
	}
	
	public void setCodi(String codi) {
		this.codi = codi;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getCodiSia() {
		return codiSia;
	}

	public void setCodiSia(String codiSia) {
		this.codiSia = codiSia;
	}

	public UnitatOrganitzativaDto getUnitatOrganitzativa() {
		return unitatOrganitzativa;
	}

	public void setUnitatOrganitzativa(UnitatOrganitzativaDto unitatOrganitzativa) {
		this.unitatOrganitzativa = unitatOrganitzativa;
	}

	public EntitatDto getEntitat() {
		return entitat;
	}

	public void setEntitat(EntitatDto entitat) {
		this.entitat = entitat;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


	private static final long serialVersionUID = -5749404179903245757L;	

}