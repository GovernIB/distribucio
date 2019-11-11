package es.caib.distribucio.backoffice.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.caib.distribucio.ws.backofficeintegracio.Annex;

/** Classe que conté el resultat de l'operació de moure un expedient de l'Arxiu amb el mètode
 * crearExpedientAmbAnotacioRegistre de la funció d'utilitats {@link BackofficeUtils}.
 *
 */
public class ArxiuResultat {

	/** Enumeració amb les possibles accions resultants. */
	public enum ExpedientAccio {
		/** L'expedient s'ha mogut. */
		MOGUT,
		/** L'expedient ja existia */
		EXISTENT,
		/** Error realitzant l'acció */
		ERROR;
	}
	
	/** Identificador dins l'Arxiu de l'expedient */
	String identificadorExpedient = null;
	/** Acció realitzada amb l'expedient */
	private ExpedientAccio accio = null;
	
	/** Codi d'error del resultat. 0 si no hi ha error. */
	private int errorCodi = 0;
	String errorMessage = null;
	Exception exception = null;

	
	/** Conjunt de resultat per annexos mapeajats pel seu UUID original*/
	private Map<String, ArxiuResultatAnnex> resultatAnnexos = new HashMap<String, ArxiuResultatAnnex>();

	
	/** Identificador de l'expedient creat o existent. */
	public String getIdentificadorExpedient() {
		return identificadorExpedient;
	}	
	public void setIdentificadorExpedient(String identificadorExpedient) {
		this.identificadorExpedient = identificadorExpedient;
	}

	/** Codi de l'error. 0 si no hi ha error. {@link DistribucioArxiuError} */
	public int getErrorCodi() {
		return errorCodi;
	}
	public void setErrorCodi(int errorCodi) {
		this.errorCodi = errorCodi;
	}
	
	/** Acció resultant */
	public ExpedientAccio getAccio() {
		return accio;
	}
	public void setAccio(ExpedientAccio accio) {
		this.accio = accio;
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
	
	/// Mètodes per consultar els resultats de moure annexos */
	
	/** Mapeig dels annexos i dels deus resultats. */
	public List<ArxiuResultatAnnex> getResultatAnnexos() {
		return new ArrayList<ArxiuResultatAnnex>(resultatAnnexos.values());
	}
	
	/** Obté un resultat per a un annex específic. */
	public ArxiuResultatAnnex getResultatAnnex(Annex annex) {
		return this.getResultatAnnex(annex.getUuid());
	}
	/** Obté el resultat per a l'identificador d'arxiu abans de moure l'annex. 
	 * 
	 * @param uuid Identificador de l'annex a l'arxiu abans de moure'l.
	 * @return
	 */
	public ArxiuResultatAnnex getResultatAnnex(String uuid) {
		ArxiuResultatAnnex resultatAnnex = null;
		if (this.resultatAnnexos.containsKey(uuid))
			resultatAnnex = this.resultatAnnexos.get(uuid);
		return resultatAnnex;
	}
	/** Afegeix un resultat per a un annex.
	 * 
	 * @param annex Annex original.
	 * @param resultatAnnex Resultat de l'operació.
	 */
	public void addResultatAnnex(Annex annex, ArxiuResultatAnnex resultatAnnex) {
		this.resultatAnnexos.put(annex.getUuid(), resultatAnnex);
	}

}
