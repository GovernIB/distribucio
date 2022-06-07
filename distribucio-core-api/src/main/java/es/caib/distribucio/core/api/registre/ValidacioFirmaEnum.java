/**
 * 
 */
package es.caib.distribucio.core.api.registre;

/**
 * Enumeració dels estats de la validació de la firma dels annexos. La firma pot haver
 * estat no validada, validada sense error, validada amb error o error validant. Els possibles
 * estats són:
 * 
 * NO_VALIDAT: No s'ha validat la firma.
 * SENSE_FIRMES: S'ha validat i l'annexo no conté firmes.
 * FIRMA_VALIDA: S'ha validat i no s'ha trobat error.
 * FIRMA_INVALIDA: S'ha validat i s'ha trobat error en la validació.
 * ERROR_VALIDANT: S'ha validat però la validació ha provocat un error.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum ValidacioFirmaEnum {

	/** No s'ha validat la firma. */
	NO_VALIDAT,
	/** S'ha validat i l'annexo no conté firmes. */
	SENSE_FIRMES,
	/** S'ha validat i no s'ha trobat error. */
	FIRMA_VALIDA,
	/** S'ha validat i s'ha trobat error en la validació. */
	FIRMA_INVALIDA,
	/** S'ha validat però la validació ha provocat un error. */
	ERROR_VALIDANT;

	/** Es considera que la firma és vàlida si no té firmes o la firma és vàlida o no 
	 * s'ha validat perquè el plugin no està configurat.
	 * 
	 * @param validacioFirma
	 * @return
	 */
	public static boolean isValida(ValidacioFirmaEnum validacioFirma) {
		return validacioFirma == ValidacioFirmaEnum.SENSE_FIRMES
				|| validacioFirma == ValidacioFirmaEnum.FIRMA_VALIDA
				|| validacioFirma == ValidacioFirmaEnum.NO_VALIDAT;
	}
}
