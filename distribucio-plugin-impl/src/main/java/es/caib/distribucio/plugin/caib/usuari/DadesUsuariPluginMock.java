/**
 * 
 */
package es.caib.distribucio.plugin.caib.usuari;

import java.util.List;
import java.util.Properties;

import es.caib.distribucio.plugin.DistribucioAbstractPluginProperties;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.usuari.DadesUsuari;
import es.caib.distribucio.plugin.usuari.DadesUsuariPlugin;

/**
 * Implementació de test del plugin de consulta de dades d'usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesUsuariPluginMock extends DistribucioAbstractPluginProperties implements DadesUsuariPlugin {

	public DadesUsuariPluginMock() {
		super();
	}
	
	public DadesUsuariPluginMock(Properties properties) {
		super(properties);
	}
	
	@Override
	public DadesUsuari findAmbCodi(
			String usuariCodi) throws SistemaExternException {
		DadesUsuari dadesUsuari = new DadesUsuari();
		dadesUsuari.setCodi(usuariCodi);
		dadesUsuari.setNomSencer(usuariCodi + " " + usuariCodi);
		dadesUsuari.setNom(usuariCodi);
		dadesUsuari.setLlinatges(usuariCodi);
		dadesUsuari.setNif("12345678Z");
		dadesUsuari.setEmail(usuariCodi + "@aqui.es");
		return dadesUsuari;
	}

	@Override
	public List<DadesUsuari> findAmbGrup(
			String grupCodi) throws SistemaExternException {
		throw new SistemaExternException("Mètode no implementat");
	}
	
}