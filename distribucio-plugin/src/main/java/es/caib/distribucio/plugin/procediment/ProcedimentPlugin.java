/**
 * 
 */
package es.caib.distribucio.plugin.procediment;

import java.util.List;

import es.caib.distribucio.core.api.dto.ProcedimentDto;
import es.caib.distribucio.plugin.IntegracioPlugin;
import es.caib.distribucio.plugin.SistemaExternException;

/**
 * Plugin per a consultar la llista de procediments d'una font externa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ProcedimentPlugin extends IntegracioPlugin {

	/**
	 * Retorna els procediment associats a una unitat organitzativa.
	 * 
	 * @param codiDir3
	 *            Codi DIR3 de l'unitat organitzativa.
	 * @return la llista de procediments.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar els procediments.
	 */
	public List<Procediment> findAmbCodiDir3(String codiDir3) throws SistemaExternException;
	
	
	/**
	 * Retorna el procediment associats a un codi SIA.
	 * @param codiDir3 
				Codi DIR3
	 * @param codiSia
	 *            Codi SIA.
	 * 
	 * @return Procediment.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar el procediment.
	 */
	public ProcedimentDto findAmbCodiSia(String codiDir3, String codiSia) throws SistemaExternException;

}
