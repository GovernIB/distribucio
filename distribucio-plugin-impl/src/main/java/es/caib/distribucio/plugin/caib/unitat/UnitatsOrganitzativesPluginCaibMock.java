/**
 * 
 */
package es.caib.distribucio.plugin.caib.unitat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import es.caib.distribucio.plugin.DistribucioAbstractPluginProperties;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.unitat.UnitatOrganitzativa;
import es.caib.distribucio.plugin.unitat.UnitatsOrganitzativesPlugin;

/**
 * Implementació de proves del plugin d'unitats organitzatives que
 * consulta una istantània de les unitats de la CAIB.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatsOrganitzativesPluginCaibMock extends DistribucioAbstractPluginProperties implements UnitatsOrganitzativesPlugin {

	public UnitatsOrganitzativesPluginCaibMock() {
		super();
	}
	
	public UnitatsOrganitzativesPluginCaibMock(Properties properties) {
		super(properties);
	}
	
	@Override
	public UnitatOrganitzativa findUnidad(String pareCodi, Timestamp fechaActualizacion, Timestamp fechaSincronizacion) throws MalformedURLException{
		return null;
	}
	
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

	@Override
	public List<UnitatOrganitzativa> cercaUnitats(String codiUnitat, String denominacioUnitat,
			Long codiNivellAdministracio, Long codiComunitat, Boolean ambOficines, Boolean esUnitatArrel,
			Long codiProvincia, String codiLocalitat) throws SistemaExternException {
		return null;
	}



	private Object deserialize(String resource) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(
				getClass().getResourceAsStream(resource));
		Object obj = ois.readObject();
		ois.close();
		return obj;
	}

	@Override
	public String getUsuariIntegracio() {
		return "CaibMock";
	}

}
