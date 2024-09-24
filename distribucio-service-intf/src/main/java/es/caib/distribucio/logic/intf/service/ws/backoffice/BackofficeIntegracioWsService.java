/**
 * 
 */
package es.caib.distribucio.logic.intf.service.ws.backoffice;

import java.util.Date;
import java.util.List;

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
	 * Processa una anotació de registre d'entrada des del seu estat inicial comunicada a rebuda pel backoffice
	 * 
	 * @param id
	 *           Id de l'anotació
	 * @param observacions
	 *
	 */
	public void canviEstatComunicadaARebuda(
			AnotacioRegistreId id,            
            String observacions);
	
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
	

	/**
	 * Consulta el llistat d'anotacions per número i data de registre
	 * 
	 * @param numeroRegistre
	 *            Número del registre d'entrada.
	 * @param dataRegistre
	 *            Data del registre d'entrada.
	 * @return llistat de les anotacions trobades.
	 */
	public List<AnotacioRegistreEntrada> llistar(
			String identificador,
			Date dataRegistre);
}
