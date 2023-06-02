/**
 * 
 */
package es.caib.distribucio.rest.client.integracio.domini;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe que representa base de l'interessat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class InteressatBase {

	private InteressatTipus tipus;
	private DocumentTipus documentTipus;
	private String documentNumero;
	private String raoSocial;
	private String nom;
	private String llinatge1;
	private String llinatge2;
	private String paisCodi;
	private String provinciaCodi;
	private String municipiCodi;
	private String pais;
	private String provincia;
	private String municipi;	
	private String adresa;
	private String cp;
	private String email;
	private String telefon;
	private String adresaElectronica;
	private String canal;
	private String observacions;
    private String organCodi;

}
