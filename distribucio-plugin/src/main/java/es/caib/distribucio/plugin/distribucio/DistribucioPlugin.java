package es.caib.distribucio.plugin.distribucio;

import es.caib.distribucio.plugin.SistemaExternException;

/**
 * Plugin per a la distribució de contingut contra sistemes externs
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DistribucioPlugin {
	
	public String ditribuirAssentament(DistribucioRegistreAnotacio anotacio, String unitatArrelCodi) throws SistemaExternException;
	
	public void eliminarContingutExistent(String idContingut) throws SistemaExternException;
	
	public void marcarProcessat(DistribucioRegistreAnotacio anotacio) throws SistemaExternException;

}
