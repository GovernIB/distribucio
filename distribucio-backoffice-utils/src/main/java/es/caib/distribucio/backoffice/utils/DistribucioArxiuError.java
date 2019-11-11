package es.caib.distribucio.backoffice.utils;

/** Classe amb les constants dels codis d'errors.
 * 
 *
 */
public class DistribucioArxiuError {
	
	/** 0 Quan no es produeix error */
	public static final int NO_ERROR = 0;
	/** Error general capturat per excepció amb l'Arxiu. */
	public static final int ARXIU_ERROR = 100;
	/** Error movent algun dels annexos */
	public static final int ANNEX_ERROR = 200;
}
