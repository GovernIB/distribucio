/**
 * 
 */
package es.caib.distribucio.plugin.servei;

import java.util.List;

import es.caib.distribucio.logic.intf.dto.ServeiDto;
import es.caib.distribucio.plugin.IntegracioPlugin;
import es.caib.distribucio.plugin.SistemaExternException;

/**
 * Plugin per a consultar la llista de serveis d'una font externa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiPlugin extends IntegracioPlugin {

	/**
	 * Retorna els servei associats a una unitat organitzativa.
	 * 
	 * @param codiDir3
	 *            Codi DIR3 de l'unitat organitzativa.
	 * @return la llista de serveis.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar els serveis.
	 */
	public List<Servei> findAmbCodiDir3(String codiDir3) throws SistemaExternException;
	
	
	/**
	 * Retorna el servei associats a un codi SIA.
	 * @param codiSia
	 *            Codi SIA.
	 * 
	 * @return Servei.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar el servei.
	 */
	public ServeiDto findAmbCodiSia(String codiSia) throws SistemaExternException;

}
