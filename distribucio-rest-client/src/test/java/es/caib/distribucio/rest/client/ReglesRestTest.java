/**
 * 
 */
package es.caib.distribucio.rest.client;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import es.caib.distribucio.rest.client.regla.ReglesRestClient;
import es.caib.distribucio.rest.client.regla.domini.Regla;
import es.caib.distribucio.rest.client.regla.domini.ReglaResponse;

/**
 * Test per al client REST de l'API REST de creació de regles automàtiques de Distribucio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ReglesRestTest {

	private static final String URL = "http://localhost:8080/distribucioapi/interna";
	// Usuari amb només el rol de DIS_REGLA
	private static final String USERNAME = "dis_reglaws";
	private static final String PASSWORD = "dis_reglaws";

	
	/** Mètode de prova de creació d'una regla. 
	 * @throws Exception */
	public static void main(String[] args) throws Exception {
		
		ReglesRestTest.trustAllCertificates();
		
		// Dades del test
		String entitat = "A04003003";
		String sia = String.valueOf(new Date().getTime());
		String backoffice = "HELIUM";
		Boolean activa = false;
		Boolean presencial = null;

		// Creació del client
		ReglesRestClient client = new ReglesRestClient(
				URL,
				USERNAME,
				PASSWORD,
				true);
		
		//ReglesRestTest.altaCanviEstatConsultaUpdate(client, entitat, sia, backoffice, activa, presencial);
		ReglesRestTest.consulta(client, "20220429");
	}

	/** Test general per crear una nova regla, canviar-li l'estat, consultar-la i modificar-la.
	 * 
	 * @param client
	 * @param entitat
	 * @param sia
	 * @param backoffice
	 * @param activa
	 * @param presencial
	 */
	private static void altaCanviEstatConsultaUpdate(
			ReglesRestClient client, 
			String entitat, 
			String sia,
			String backoffice, 
			Boolean activa, 
			Boolean presencial) {

		System.out.println("Inici test API REST de regles ( " + 
				"entitat= " + entitat +
				", sia= " + sia +
				", backoffice= " + backoffice +
				", activa = " + activa +
				", presencial= " + presencial
		);
		ReglaResponse ret;
		// Creació de la regla
		try {
			ret = client.add(entitat, sia, backoffice, presencial);
			System.out.println("Creació finalitzada correctament amb resultat " + (ret.isCorrecte() ? "correcte" : "incorrecte") + " " +
									ret.getStatus() + " " + ret.getMsg());
			ret = client.add(entitat, sia, backoffice, presencial);
			System.out.println("Segona crida creació finalitzada correctament amb resultat " + (ret.isCorrecte() ? "correcte" : "incorrecte") + " " +
									ret.getStatus() + " " + ret.getMsg());
		} catch (Exception e) {
			System.err.println("Error creant la regla: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
		
		// Canvi d'estat de l'activació de la regla
		try {
			ret = client.canviEstat(sia, null);
			System.out.println("Canvi d'estat finalitzat correctament amb resultat " + (ret.isCorrecte() ? "correcte" : "incorrecte") + " " +
								ret.getStatus() + " " + ret.getMsg());
		} catch (Exception e) {
			System.err.println("Error canviant estat a la regla: " + e.getMessage());
			e.printStackTrace();
		}
		
		// Consulta de la regla
		try {
			Regla regla = client.consultarRegla(sia);
			System.out.println("Consulta de la regla realitzada correctament " + regla);
		} catch (Exception e) {
			System.err.println("Error consultant la regla: " + e.getMessage());
			e.printStackTrace();
		}
		
		// Actualització de la regla
		try {
			ret = client.update(sia, activa, presencial);
			System.out.println("Update finalitzado correctament amb resultat " + (ret.isCorrecte() ? "correcte" : "incorrecte") + " " +
					ret.getStatus() + " " + ret.getMsg());
		} catch (Exception e) {
			System.err.println("Error consultant la regla: " + e.getMessage());
			e.printStackTrace();
		}		
	}


	/** Test de consulta de la regla codi SIA.
	 * @param client 
	 * 
	 * @param sia
	 * @throws Exception 
	 */
	private static void consulta(ReglesRestClient client, String sia) throws Exception {
		
		client.update(sia, true, true);
		Regla r  = client.consultarRegla(sia);
		
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		
		System.out.println("Regla consultada: " + ow.writeValueAsString(r));
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