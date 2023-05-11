/**
 * 
 */
package es.caib.distribucio.war.rest;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import es.caib.distribucio.core.api.dto.ReglaDto;

/**
 * Test per al client REST de l'API REST de creació de regles automàtiques de Distribucio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ReglesRestTest {

	private static final String URL = "http://localhost:8080/distribucioapi/interna";
	// Usuari amb només el rol de DIS_REGLA
	private static final String USERNAME = "disregla";
	private static final String PASSWORD = "disregla";

	
	/** Mètode de prova de creació d'una regla. 
	 * @throws Exception */
	public static void main(String[] args) throws Exception {
		
		ReglesRestTest.trustAllCertificates();
		
		// Creació del client
		ReglesRestClient client = new ReglesRestClient(
				URL,
				USERNAME,
				PASSWORD,
				true);
		
		// Dades del test
		Long entitatId = 401L;
		String entitat = "A04019281";
		String sia = "868187";
		String backoffice = "HELIUM";
		boolean activa = true;
		boolean presencial = true;
		

//		// Creació de la regla
//		try {
//			boolean ret = client.add(entitat, sia, backoffice);
//			System.out.println("Creació finalitzada correctament amb resultat " + ret);
//		} catch (Exception e) {
//			System.err.println("Error creant la regla: " + e.getMessage());
//			e.printStackTrace();
//		}
//		
//		// Modificació de l'activació de la regla
//		try {
//			boolean ret = client.canviEstat(sia, null);
//			System.out.println("Canvi d'estat finalitzat correctament amb resultat " + ret);
//		} catch (Exception e) {
//			System.err.println("Error canviant estat a la regla: " + e.getMessage());
//			e.printStackTrace();
//		}
//		
//		// Consulta de la regla
//		try {
//			boolean ret = client.consultarRegla(sia);
//			System.out.println("Consulta finalitzada correctament amb resultat " + ret);
//		} catch (Exception e) {
//			System.err.println("Error consultant la regla: " + e.getMessage());
//			e.printStackTrace();
//		}
		
		// nova utilitat per a api de regles
		try {
			boolean ret = client.update(sia, activa, presencial);
			System.out.println("Update finalitzado correctament amb resultat " + ret);
		} catch (Exception e) {
			System.err.println("Error consultant la regla: " + e.getMessage());
			e.printStackTrace();
		}
	}


	private static void trustAllCertificates() throws Exception {
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
}