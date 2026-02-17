/**
 * 
 */
package es.caib.distribucio.rest.client.regla.domini;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe per contenir la informació consultada d'una regla.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class Regla {

	public enum TipusSia {
		PROCEDIMENT,
		SERVEI
	}
	private long id;
	private String entitat;
	private Date data;
	private boolean activa;
	private String nom;
	private String backofficeDesti;
	private Boolean presencial;
	private TipusSia tipusSia;

}
