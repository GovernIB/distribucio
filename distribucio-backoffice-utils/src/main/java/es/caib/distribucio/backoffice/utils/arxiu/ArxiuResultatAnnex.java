package es.caib.distribucio.backoffice.utils.arxiu;

import es.caib.distribucio.rest.client.domini.Annex;

/** Classe que conté el resultat de l'operació de moure un annex de l'Arxiu amb el mètode
 * crearExpedientAmbAnotacioRegistre de la funció d'utilitats {@link BackofficeArxiuUtils}.
 *
 */
public class ArxiuResultatAnnex {
	
	/** Enumeració amb les possibles accions resultants. */
	public enum AnnexAccio {
		/** L'annex s'ha mogut. */
		MOGUT,
		/** L'annex ja existia */
		EXISTENT,
		/** Error realitzant l'acció */
		ERROR;
	}

	private String identificadorAnnex = null;
	private AnnexAccio accio = null;

	private int errorCodi = 0;
	private String errorMessage = null;
	private Exception exception = null;

	/** Referència a l'annex del resultat. */
	private Annex annex;

	
	/** Identificador de l'expedient creat o existent. */
	public String getIdentificadorAnnex() {
		return identificadorAnnex;
	}	
	public void setIdentificadorAnnex(String identificadorAnnex) {
		this.identificadorAnnex = identificadorAnnex;
	}

	/** Acció resultant */
	public AnnexAccio getAccio() {
		return accio;
	}
	public void setAccio(AnnexAccio accio) {
		this.accio = accio;
	}	

	
	
	/** Codi de l'error. 0 si no hi ha error. {@link DistribucioArxiuError} */
	public int getErrorCodi() {
		return errorCodi;
	}
	public void setErrorCodi(int errorCodi) {
		this.errorCodi = errorCodi;
	}
	
	/** Missatge descriptiu de l'error. */
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/** Excepció capturada en l'error. */
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	public Annex getAnnex() {
		return annex;
	}
	public void setAnnex(Annex annex) {
		this.annex = annex;
	}	
	
	
	/// 
}
