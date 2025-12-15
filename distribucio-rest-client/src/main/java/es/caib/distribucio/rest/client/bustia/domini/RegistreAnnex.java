package es.caib.distribucio.rest.client.bustia.domini;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe que representa un annex d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RegistreAnnex {

	private Long id;
	private String titol;//200
	private String fitxerNom;//comes from document custody plugin
	private int fitxerTamany;
	private String fitxerTipusMime;//comes from document custody plugin
	private String fitxerArxiuUuid;//256
	private byte[] fitxerContingut;
	private Date eniDataCaptura;
	private String eniOrigen;//10 (Integer to String)
	private String eniEstatElaboracio;//4
	private String eniTipusDocumental;//255
	private String sicresTipusDocument;//2
	private String localitzacio;//is not set
	private String observacions;//50
	private Map<String, String> metaDades;	
	private List<Firma> firmes = new ArrayList<>();
	private String timestamp;//is not set
	private String validacioOCSP;//255
	
}
