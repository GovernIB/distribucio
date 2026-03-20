package es.caib.distribucio.rest.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import es.caib.distribucio.rest.client.integracio.BackofficeIntegracioRestClient;
import es.caib.distribucio.rest.client.integracio.BackofficeIntegracioRestClientFactory;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.integracio.domini.Estat;

/** Test per provar el client REST de l'API RESt d'integració per backoffices. Es prova 
 * la consulta i canvi d'estat d'una anotació.
 */
public class ApiRestIntegracioTest {

	public static String BASE_URL = "http://10.35.3.232:8080/distribucioapi/interna";
	public static String USERNAME = "dis_backws";
	public static String PASSWORD = "dis_backws";
	
	/** Prova de crear una regla, descactivar-la i consultar-la. 
	 * @throws Exception */
	public static void main(String[] args) throws Exception {
		
		// Configura el client API REST
		BackofficeIntegracioRestClient restClient = 
				BackofficeIntegracioRestClientFactory.getRestClient(BASE_URL, USERNAME, PASSWORD);
		
		// Crea l'objecte id per la consulta
		AnotacioRegistreId id = new AnotacioRegistreId();			
		id.setIdentificador("GOIBE1708001141307/2024");
		id.setClauAcces("BYlMBmFPLGJSytavAH325Yjbwqo9xfXhM+wM0vzHxL4=");		

		// Consulta l'anotació
		AnotacioRegistreEntrada anotacio = restClient.consulta(id);
		System.out.println("Anotació consultada: " + anotacio.getIdentificador() + " " + anotacio.getExtracte());
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();		
		System.out.println("JSON anotació: " + ow.writeValueAsString(anotacio));
		
		// Canvi d'estat
		restClient.canviEstat(id, Estat.REBUDA, "Observacions text");
		System.out.println("Test finalitzat correctament.");
	}
}
