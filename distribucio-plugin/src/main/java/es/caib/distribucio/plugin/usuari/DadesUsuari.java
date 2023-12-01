/**
 * 
 */
package es.caib.distribucio.plugin.usuari;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dades d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadesUsuari implements Serializable {

	private String codi;
	private String nomSencer;
	private String nom;
	private String llinatges;
	private String nif;
	private String email;
	private boolean actiu = true;
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNomSencer() {
		if (nomSencer != null) {
			return nomSencer;
		} else if (nom != null) {
			if (llinatges != null) {
				return nom + " " + llinatges;
			} else {
				return nom;
			}
		} else {
			return null;
		}
	}

	private static final long serialVersionUID = -139254994389509932L;
}
