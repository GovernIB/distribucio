package es.caib.distribucio.backoffice.utils.sistra;

import es.caib.distribucio.backoffice.utils.sistra.formulario.Formulario;
import es.caib.distribucio.backoffice.utils.sistra.pago.Pago;

/** Interfície amb els métodes de les funcions d'utilitats per a backoffices per tractar 
 * informació d'annexos de documentació tècnica de Sistra2 de pagaments o dades de formularis.
 */
public interface BackofficeSistra2Utils {	
	

	/** Extreu la informació del pagament a partir del contingut de l'annex.
	 * 
	 * @param contingut
	 * 			Contingut del document tècnic d'un pagament de Sistra2.
	 * @return
	 * 		Retorna un objecte amb la informació del pagament.
	 * @throws
	 * 		Si es produeix algun error interpretant el contingut de l'arxiu es llença una excepció.
	 */
	public Pago parseXmlPago(byte[] contingut) throws Exception;	
	
	/** Extreu la informació del formulari a partir del contingut de l'annex.
	 * 
	 * @param contingut
	 * 			Contingut del document tècnic d'un formulario de Sistra2.
	 * @return
	 * 		Retorna un objecte amb la informació del formulari.
	 * @throws
	 * 		Si es produeix algun error interpretant el contingut de l'arxiu es llença una excepció.
	 */
	public Formulario parseXmlFormulario(byte[] contingut) throws Exception;

}
