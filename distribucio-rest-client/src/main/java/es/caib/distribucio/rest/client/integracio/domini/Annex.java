/**
 * 
 */
package es.caib.distribucio.rest.client.integracio.domini;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe que representa un annex d'una anotació de registre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class Annex {

	private String titol;
	private String nom;
	private byte[] contingut;
	private String uuid;
	private long tamany;
	private String tipusMime;
	private NtiTipoDocumento ntiTipoDocumental;
	private NtiOrigen ntiOrigen;
	private Date ntiFechaCaptura;
	private SicresTipoDocumento sicresTipoDocumento;
	private SicresValidezDocumento sicresValidezDocumento;
	private NtiEstadoElaboracion ntiEstadoElaboracion;
	private String observacions;
	private FirmaTipus firmaTipus;
	private FirmaPerfil firmaPerfil;
	private byte[] firmaContingut;
	private long firmaTamany;
	private String firmaNom;
	private String firmaTipusMime;

	private boolean documentValid;
	private String documentError;
	private AnnexEstat estat;

	
	public boolean isDocumentValid() {
		return documentValid;
	}
	public String getDocumentError() {
		return documentError;
	}
	public AnnexEstat getEstat() {
		return estat;
	}

}
