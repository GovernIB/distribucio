package es.caib.distribucio.rest.client.bustia.domini;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe que representa un interessat d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RegistreInteressat {

	private Long id;
	private String tipus;//19 (Long to String)
	private String documentTipus;//1
	private String documentNum;//17
	private String nom;//255
	private String llinatge1;//255
	private String llinatge2;//255
	private String raoSocial;//2000
	private String pais;//100
	private String paisCodi;//19 (Long to String) //3
	private String provincia;//50
	private String provinciaCodi;//19 (Long to String) //2
	private String municipi;//50
	private String municipiCodi;//19 (Long to String) //4
	private String adresa;//160
	private String codiPostal;//5
	private String email;//160
	private String telefon;//20
	private String emailHabilitat;//160
	private String canalPreferent;//2
	private String observacions;//160
	private String codiDire;//15
	private RegistreInteressat representant;
	private RegistreInteressat representat;
    private String organCodi;//is not set
    
}
