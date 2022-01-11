/**
 * 
 */
package es.caib.distribucio.plugin.caib.signatura;

import static org.junit.Assert.fail;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import es.caib.distribucio.plugin.signatura.SignaturaPlugin;
import es.caib.distribucio.plugin.signatura.SignaturaResposta;
import es.caib.distribucio.plugin.utils.PropertiesHelper;

/**
 * Test del plugin de firma simple del portasignatures.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FirmaSimplePluginPortafibTest {

	private static final String API_ENDPOINT_ADDRESS = "https://dev.caib.es/portafib/common/rest/apifirmaenservidorsimple/v1/";
	//private static final String API_ENDPOINT_ADDRESS = "https://proves.caib.es/portafib/common/rest/apifirmaenservidorsimple/v1/";	
	private static final String API_USERNAME = "$distribucio_portafib";
	private static final String API_PASSWORD = "distribucio_portafib";
	//private static final String PERFIL = "FIRMAAPISIMPLE";
	//private static final String PERFIL = "XADES_DETACHED"; //TF02
	//private static final String PERFIL = "CADES_DETACHED"; //TF04
	private static final String PERFIL = "CADES_ATTACHED"; //TF05
	//private static final String PERFIL = "PADES"; //TF06
	//private static final String PERFIL = "FIRMAAPISIMPLE"; //TF02
	// Nom del certificat emprat per @firma per firmar
	private static final String USERNAME = "afirmades-firma";
	//private static final String USERNAME = "preprod-dgmad";

	private SignaturaPlugin plugin;

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
				"es.caib.distribucio.plugin.api.firma.en.servidor.simple.endpoint",
				API_ENDPOINT_ADDRESS);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.distribucio.plugin.api.firma.en.servidor.simple.username",
				API_USERNAME);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.distribucio.plugin.api.firma.en.servidor.simple.password",
				API_PASSWORD);
		System.setProperty(
				"es.caib.distribucio.plugin.api.firma.en.servidor.simple.perfil",
				PERFIL);
		PropertiesHelper.getProperties().setProperty(
				"es.caib.distribucio.plugin.signatura.portafib.username",
				USERNAME);
		plugin = new FirmaSimplePluginPortafib();
	}

	@Test
	public void test() {
		try {
			String annexId = "annexId";
			String fitxerNom = "annex_sense_firma.pdf";
			String motiu = "Prova firma en servidor";
			String mime = "application/pdf";
			String tipusDocumental = null;
			byte[] contingut = this.getContingut("/" + fitxerNom);
			SignaturaResposta resposta = plugin.signar(
					annexId, 
					fitxerNom, 
					motiu, 
					contingut, 
					mime,
					tipusDocumental);
			
			System.out.println("contingut: " + resposta.getContingut().length + " b");
			System.out.println("tipus: " + resposta.getTipusFirmaEni());
			System.out.println("perfil: " + resposta.getPerfilFirmaEni());
			
			// Per guardar la resposta en un arxiu
			//Path path = Paths.get("/tmp/annex_signat_TF05.csig");
			//Files.write(path, resposta.getContingut());

		} catch(Exception e) {
			fail("Error capturat: " + e.getClass() + ": " + e.getMessage());
			e.printStackTrace(System.err);
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
