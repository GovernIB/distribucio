/**
 * 
 */
package es.caib.distribucio.plugin.caib.validacio;

import static org.junit.Assert.fail;

import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import es.caib.distribucio.plugin.utils.PropertiesHelper;
import es.caib.distribucio.plugin.validacio.ValidaSignaturaResposta;
import es.caib.distribucio.plugin.validacio.ValidacioSignaturaPlugin;

/**
 * Test del plugin de firma simple del portasignatures.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidacioFirmaApiTest {

	private static final String API_ENDPOINT_ADDRESS = "https://se.caib.es/portafibapi/interna";
	private static final String API_USERNAME = "$distribucio_portafib_se";
	private static final String API_PASSWORD = "distribucio_portafib_se";

	private ValidacioSignaturaPlugin plugin;

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
				"es.caib.distribucio.pluginsib.validatesignature.api.portafib.endpoint",
				API_ENDPOINT_ADDRESS);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.distribucio.pluginsib.validatesignature.api.portafib.username",
				API_USERNAME);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.distribucio.pluginsib.validatesignature.api.portafib.password",
				API_PASSWORD);
		plugin = new ValidacioFirmaPluginApiPortafib(PropertiesHelper.getProperties(), false);
	}

	@Test
	public void test() {
		try {
			String documentNom = "annex_firmat.pdf";
			String documentMime = "application/pdf";
			byte[] documentContingut = this.getContingut("/" + documentNom);
			ValidaSignaturaResposta resposta = plugin.validaSignatura(documentNom, documentMime, documentContingut, null);
			
			System.out.println("status: " + resposta.getStatus());
			
			// Per guardar la resposta en un arxiu
			//Path path = Paths.get("/tmp/annex_signat_TF05.csig");
			//Files.write(path, resposta.getContingut());

		} catch(Exception e) {
			System.err.println("Error executant el test: " + e.getClass() + " " + e.getMessage());
			e.printStackTrace(System.err);
			fail("Error capturat: " + e.getClass() + ": " + e.getMessage());
		}
	}

	private byte[] getContingut(String fitxerNom) {
		byte[] contingut;
		try {
			InputStream arxiuContingut = getClass().getResourceAsStream(fitxerNom);
			contingut = IOUtils.toByteArray(arxiuContingut);
		} catch(Exception e) {
			throw new RuntimeException("Error obtenint el contingut de " + fitxerNom + ": " + e.getClass() + ": " + e.getMessage(), e);
		}
		return contingut;
	}
	

}
