/**
 * 
 */
package es.caib.distribucio.rest.client.integracio.domini;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * Classe que representa la base d'anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class AnotacioRegistreBase {

	private String identificador;
	private String procedimentCodi;
	private String extracte;
	private Date data;
	private String entitatCodi;
	private String entitatDescripcio;
	private String usuariCodi;
	private String usuariNom;
	private String oficinaCodi;
	private String oficinaDescripcio;
	private String llibreCodi;
	private String llibreDescripcio;
	private String docFisicaCodi;
	private String docFisicaDescripcio;
	private String assumpteTipusCodi;
	private String assumpteTipusDescripcio;
	private String assumpteCodiCodi;
	private String assumpteCodiDescripcio;
	private String transportTipusCodi;
	private String transportTipusDescripcio;
	private String transportNumero;
	private String idiomaCodi;
	private String idomaDescripcio;
	private String observacions;
	private String origenRegistreNumero;
	private Date origenData;
	private String aplicacioCodi;
	private String aplicacioVersio;
	private String refExterna;
	private String expedientNumero;
	private String exposa;
	private String solicita;
	private List<Interessat> interessats;
	private List<Annex> annexos;
	private String justificantFitxerArxiuUuid;
		
}
