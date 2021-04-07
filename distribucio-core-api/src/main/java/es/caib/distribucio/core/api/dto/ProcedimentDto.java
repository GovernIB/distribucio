/**
 * 
 */
package es.caib.distribucio.core.api.dto;

import java.util.Comparator;

/**
 * Informació d'un procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcedimentDto implements Comparable<ProcedimentDto>{

	private String codi;
	private String nom;
	private String codiSia;

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
	
	@Override
	public int compareTo(ProcedimentDto o) {
		return nom.compareTo(o.getNom());
	}

}
