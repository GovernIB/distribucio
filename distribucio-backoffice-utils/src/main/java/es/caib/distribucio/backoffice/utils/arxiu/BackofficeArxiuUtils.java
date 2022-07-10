
package es.caib.distribucio.backoffice.utils.arxiu;

import java.util.Date;
import java.util.List;

import es.caib.distribucio.core.api.dto.ExpedientEstatEnumDto;
import es.caib.distribucio.rest.client.domini.AnotacioRegistreEntrada;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.IArxiuPlugin;

/** Interfície amb els métodes de les funcions d'utilitats per a backoffices
 * 
 */
public interface BackofficeArxiuUtils {	
	

	/** Crea un expedient i mou el contingut dels annexos i interessats de l'expedient de l'anotació de registre de Distribució.
	 * 
	 * @param expedient
	 * 			Objecte expedient de l'Api de l'Arxiu a crear.
	 * @param anotacioRegistreEntrada
	 * 			Objecte de l'anotació de registre d'entrada consultada.
	 * @return
	 * 		Retorna un objecte amb la informació de com ha anat l'operació.
	 */
	public ArxiuResultat crearExpedientAmbAnotacioRegistre(
			Expedient expedient,
			AnotacioRegistreEntrada anotacioRegistreEntrada);
	
	/** Crea un expedient i mou el contingut dels annexos i interessats de l'expedient de l'anotació de registre de Distribució. Aquest 
	 * mètode té com a paràmetres la informació mínima per a crear l'expedient.
	 * 
	 * @param anotacioRegistreEntrada
	 * 			Objecte de l'anotació de registre d'entrada consultada.
	 * @return
	 * 		Retorna un objecte amb la informació de com ha anat l'operació.
	 */
	public ArxiuResultat crearExpedientAmbAnotacioRegistre(
			String identificador,
			String nom,
			String ntiIdentificador,
			List<String> ntiOrgans,
			Date ntiDataObertura,
			String ntiClassificacio,
			ExpedientEstatEnumDto ntiEstat,
			String serieDocumental,
			AnotacioRegistreEntrada anotacioRegistreEntrada);

	/** Fixa la referència al plugin d'Arxiu per realitzar crides i comprovacions.
	 * 
	 * @param iArxiuPlugin
	 */
	public void setArxiuPlugin(IArxiuPlugin iArxiuPlugin);

	/** Consulta la referència al plugin d'Arxiu amb què es realitzaran les crides i comprovacions
	 */
	public IArxiuPlugin getArxiuPlugin();

	/** Fixa el nom de la carpeta on es mouran els annexos. Si no es fixa la propietat llavors els annexos es mouran a la carpeta arrel de l'expedient.
	 * 
	 * @param carpetaNom
	 */
	public void setCarpeta(String carpetaNom);
	
	/** Consulta el nom de la carpeta on es mouran els annexos. Si no es fixa la propietat llavors els annexos es mouran a la carpeta arrel de l'expedient.
	 * 
	 * @return nom de la carpeta on es mouran els annexos
	 */
	public String getCarpeta();
	
	
	/** Fixa una instància de la interfície ArxiuPluginListener per estar notificat de les crides que es fan a l'Arxiu.
	 * 
	 * @param listener
	 * Instància d'un objecte que implementa la interfície ArxiuPluginListener on es notificaran les crides a l'Arxiu.
	 */
	public void setArxiuPluginListener(ArxiuPluginListener listener);
	
	/** Consulta de la referència a la instància fixada de ArxiuPluginListener per escoltar crides a l'Arxiu */
	public ArxiuPluginListener getArxiuPluginListener();
	
}
