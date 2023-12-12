/**
 * 
 */
package es.caib.distribucio.logic.intf.service.ws.backoffice;


/**
 * Declaració dels mètodes per al servei per a processar anotacions
 * de registre mitjançant una aplicació externa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface BackofficeIntegracioWsService {

	/**
	 * Processa una anotació de registre d'entrada.
	 * 
	 * @param registreEntrada
	 *            Dades de l'anotació al registre d'entrada.
	 * @return el resultat de processar l'anotació.
	 */
	public AnotacioRegistreEntrada consulta(
			AnotacioRegistreId id);

	/**
	 * Processa una anotació de registre d'entrada.
	 * 
	 * @param registreEntrada
	 *            Dades de l'anotació al registre d'entrada.
	 * @return el resultat de processar l'anotació.
	 */
	public void canviEstat(
			AnotacioRegistreId id,
			Estat estat,
			String observacions);
	

}
