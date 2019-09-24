
package es.caib.distribucio.backoffice.utils;

import java.util.Map;

/** Interfície a implementar si es vol estar informat de les peticions que es fan a l'Arxiu
 * 
 */
public interface ArxiuPluginListener {	
	
	/** Mètode per notificar les crides fetes a l'arxiu, els paràmetres, el resultat i el temps de la petició
	 * 
	 * @param metode
	 * 			Mètode invocat
	 * @param parametres
	 * 			Paràmetres més representatius
	 * @param correcte
	 * 			Resultat correcte
	 * @param error
	 * 			Descripció de l'error en cas d'error
	 * @param e
	 * 			Excepció en cas de produir-se una excepció
	 * @param timeMs
	 * 			Temps en ms transcorreguts en la crida
	 */
	public void event(String metode, Map<String, String> parametres, boolean correcte, String error, Exception e, long timeMs);

}
