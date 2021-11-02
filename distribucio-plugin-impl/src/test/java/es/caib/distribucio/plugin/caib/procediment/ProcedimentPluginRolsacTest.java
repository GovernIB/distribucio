/**
 * 
 */
package es.caib.distribucio.plugin.caib.procediment;

import static org.junit.Assert.fail;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
	private static final String CODI_DIR3 = "A04013512";

	private ProcedimentPlugin plugin;

	/** Accepta els certificats i afegeix el protocol TLSv1.2.
	 * @throws Exception */
	@Before
	public void init() throws Exception {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
		    public X509Certificate[] getAcceptedIssuers(){return null;}
		    public void checkClientTrusted(X509Certificate[] certs, String authType){}
		    public void checkServerTrusted(X509Certificate[] certs, String authType){}
		}};

		// Install the all-trusting trust manager
		try {
		    SSLContext sc = SSLContext.getInstance("TLS");
		    sc.init(null, trustAllCerts, new SecureRandom());
		    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
		    System.err.println("Error ingorant certificats: " + e.getMessage());
		    e.printStackTrace();
		}		
		// Afegeix el protocol TLSv1.2
    	SSLContext context = SSLContext.getInstance("TLSv1.2");
    	context.init(null,null,null);
    	SSLContext.setDefault(context); 
	}
	
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
	public void test() {
		try {
			List<Procediment> procediments = plugin.findAmbCodiDir3(CODI_DIR3);
			for (Procediment procediment: procediments) {
				System.out.println(">>> [" + procediment.getCodigo() + ", " + procediment.getCodigoSIA() + "] " + procediment.getNombre());
			}
			System.out.println(">>> Total: " + procediments.size());
		} catch(Exception e) {
			fail("Error capturat: " + e.getClass() + ": " + e.getMessage());
			e.printStackTrace(System.err);
		}
	}

}
