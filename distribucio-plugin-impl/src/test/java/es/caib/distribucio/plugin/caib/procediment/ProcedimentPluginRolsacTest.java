/**
 * 
 */
package es.caib.distribucio.plugin.caib.procediment;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.procediment.Procediment;
import es.caib.distribucio.plugin.procediment.ProcedimentPlugin;
import es.caib.distribucio.plugin.utils.PropertiesHelper;

/**
 * Test del plugin de consulta de procediments que accedeix a ROLSAC.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcedimentPluginRolsacTest {

	private static final String ENDPOINT_ADDRESS = "https://proves.caib.es/rolsac/api/rest/v1/procedimientos";
	private static final String USERNAME = "$distribucio_rolsac";
	private static final String PASSWORD = "distribucio_rolsac";
	private static final String CODI_DIR3 = "A04019281";

	private ProcedimentPlugin plugin;

	@Before
	public void setUp() throws Exception {
		PropertiesHelper.getProperties().setLlegirSystem(false);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.distribucio.plugin.procediment.rolsac.service.url",
				ENDPOINT_ADDRESS);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.distribucio.plugin.procediment.rolsac.service.username",
				USERNAME);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.distribucio.plugin.procediment.rolsac.service.password",
				PASSWORD);
		plugin = new ProcedimentPluginRolsac();
	}

	@Test
	public void test() throws SistemaExternException {
		List<Procediment> procediments = plugin.findAmbCodiDir3(CODI_DIR3);
		for (Procediment procediment: procediments) {
			System.out.println(">>> [" + procediment.getCodigo() + ", " + procediment.getCodigoSIA() + "] " + procediment.getNombre());
		}
		System.out.println(">>> Total: " + procediments.size());
	}

}
