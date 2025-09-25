/**
 * 
 */
package es.caib.distribucio.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * Informació d'un usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class UsuariDto implements Serializable {

	private String codi;
	private String nom;
	private String nif;
	private String email;
	private String emailAlternatiu;	
	private String idioma;
	private String[] rols;
	private Boolean rebreEmailsBustia;
	private Boolean rebreEmailsAgrupats;
	private Long bustiaPerDefecte;
	private String rolActual;
    private Long entitatPerDefecteId;

	public String getCodiAndNom() {
		return nom + " (" + codi + ")";
	}
	private static final long serialVersionUID = -139254994389509932L;

}
