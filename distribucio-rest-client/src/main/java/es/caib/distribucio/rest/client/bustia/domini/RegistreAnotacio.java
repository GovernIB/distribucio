package es.caib.distribucio.rest.client.bustia.domini;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe que representa una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class RegistreAnotacio {
	
	//length of fields in registre application
	private String tipusES; //1
	private String numero;//255
	private Date data;
	private Date dataOrigen;
	private String identificador;//19 (Long to String)
	private String entitatCodi;//255
	private String entitatDescripcio;//255
	private String oficinaCodi;//9
	private String oficinaDescripcio;//300
	private String oficinaOrigenCodi;//9
	private String oficinaOrigenDescripcio;//is not filled in registre
	private String llibreCodi;//4
	private String llibreDescripcio;//255
	private String extracte;//240
	private String assumpteTipusCodi;//2
	private String assumpteTipusDescripcio;//Deprecated, will be alliminated
	private String assumpteCodi;//16
	private String procedimentCodi;//19 (Long to String)
	private String serveiCodi;//19 (Long to String)
	private String assumpteDescripcio;//255
	private String referencia;//16
	private String expedientNumero;//80
	private String numeroOrigen;//20
	private String idiomaCodi;//19
	private String idiomaDescripcio;//10 (18n value - i.e. Castellano)
	private String transportTipusCodi;//20
	private String transportTipusDescripcio;// 25 (18n value i.e. Correo postal certificado) 
	private String transportNumero;//20
	private String usuariCodi;//19 (Long to String)
	private String usuariNom;//767
	private String usuariContacte;//255
	private String aplicacioCodi;//255
	private String aplicacioVersio;//255
	private String documentacioFisicaCodi;//19 (Long to String)
	private String documentacioFisicaDescripcio;// 66 (18n value i.e. DocumentaciÃ³n adjunta digitalizada y complementariamente en papel) 
	private String observacions;//50
	private String exposa;//2147483647
	private String solicita;//2147483647
	private boolean presencial;
	private List<RegistreInteressat> interessats = new ArrayList<RegistreInteressat>();
	private List<RegistreAnnex> annexos = new ArrayList<RegistreAnnex>();
	private RegistreAnnex justificant;
	private String tramitCodi;
	private String tramitNom;
	
}