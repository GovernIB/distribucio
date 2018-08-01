/**
 * 
 */
package es.caib.distribucio.plugin.caib.unitat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.List;

import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.unitat.UnitatOrganitzativa;
import es.caib.distribucio.plugin.unitat.UnitatsOrganitzativesPlugin;

/**
 * Implementació de proves del plugin d'unitats organitzatives que
 * consulta una istantània de les unitats de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatsOrganitzativesPluginCaibMock implements UnitatsOrganitzativesPlugin {

	
	@Override
	public UnitatOrganitzativa findUnidad(String pareCodi, Timestamp fechaActualizacion, Timestamp fechaSincronizacion) throws MalformedURLException{
		return null;
	}
	
//	@Override
//	public List<UnitatOrganitzativa> obtenerArbolUnidades(String pareCodi, Timestamp fechaActualizacion, Timestamp fechaSincronizacion) throws MalformedURLException{
//		return null;
//	}
//	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<UnitatOrganitzativa> findAmbPare(
			String pareCodi, 
			Timestamp fechaActualizacion, 
			Timestamp fechaSincronizacion) throws SistemaExternException{
		try {
			return (List<UnitatOrganitzativa>)deserialize(
					"/es/caib/distribucio/plugin/unitat/ArbreUnitatsCaib.ser");
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar les unitats organitzatives via WS (" +
					"pareCodi=" + pareCodi + ")",
					ex);
		}
	}



	/*@SuppressWarnings("unchecked")
	@Override
	public List<UnitatOrganitzativaD3> cercaUnitatsD3(
			String codiUnitat, 
			String denominacioUnitat,
			Long codiNivellAdministracio, 
			Long codiComunitat, 
			Boolean ambOficines, 
			Boolean esUnitatArrel,
			Long codiProvincia, 
			String codiLocalitat) throws SistemaExternException {
		try {
			return (List<UnitatOrganitzativaD3>)deserialize(
					"/es/caib/distribucio/plugin/unitat/ArbreUnitatsCaib.ser");
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar les unitats organitzatives via REST (" +
					"codiUnitat=" + codiUnitat + ", " +
					"denominacioUnitat=" + denominacioUnitat + ", " +
					"codiNivellAdministracio=" + codiNivellAdministracio + ", " +
					"codiComunitat=" + codiComunitat + ", " +
					"ambOficines=" + ambOficines + ", " +
					"esUnitatArrel=" + esUnitatArrel + ", " +
					"codiProvincia=" + codiProvincia + ", " +
					"codiLocalitat=" + codiLocalitat + ")",
					ex);
		}
	}*/

	@Override
	public List<UnitatOrganitzativa> cercaUnitats(String codiUnitat, String denominacioUnitat,
			Long codiNivellAdministracio, Long codiComunitat, Boolean ambOficines, Boolean esUnitatArrel,
			Long codiProvincia, String codiLocalitat) throws SistemaExternException {
		// TODO Auto-generated method stub
		return null;
	}



	private Object deserialize(String resource) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(
				getClass().getResourceAsStream(resource));
		Object obj = ois.readObject();
		ois.close();
		return obj;
	}

}
