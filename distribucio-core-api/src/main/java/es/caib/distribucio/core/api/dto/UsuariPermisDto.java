/**
 * 
 */
package es.caib.distribucio.core.api.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class UsuariPermisDto implements Serializable {

	private String codi;
	private String nom;
	private Set<String> rols = new HashSet<>();

	boolean hasUsuariPermission;

	public boolean isHasUsuariPermission() {
		return hasUsuariPermission;
	}
	public void setHasUsuariPermission(boolean hasUsuariPermission) {
		this.hasUsuariPermission = hasUsuariPermission;
	}
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

	public Set<String> getRols() {
		return rols;
	}
	public void setRols(Set<String> rols) {
		this.rols = rols;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		UsuariPermisDto other = (UsuariPermisDto) obj;
		if (codi == null) {
			if (other.codi != null)
				return false;
		} else if (codi != null) {
			if (!codi.equals(other.codi)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return (int) codi.hashCode();
	}

	private static final long serialVersionUID = -139254994389509932L;

}
