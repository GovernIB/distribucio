package es.caib.distribucio.logic.intf.service.ws.backoffice;

import java.util.List;

/** Interfície del servei per simular la comunicació d'anotacions pendents
 * 
 */
public interface BackofficeWsService {

	/** Mètode de la interfície per simular com a backoffice el mètode per rebre
	 * anotacions pendents.
	 * 
	 * @param ids
	 */
	public void comunicarAnotacionsPendents(
			List<AnotacioRegistreId> ids);

}
