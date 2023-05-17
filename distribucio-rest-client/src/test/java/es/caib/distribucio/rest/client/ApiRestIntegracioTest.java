package es.caib.distribucio.rest.client;

import es.caib.distribucio.rest.client.integracio.BackofficeIntegracioRestClient;
import es.caib.distribucio.rest.client.integracio.BackofficeIntegracioRestClientFactory;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.integracio.domini.Estat;

/** Test per provar el client REST de l'API RESt d'integració per backoffices. Es prova 
 * la consulta i canvi d'estat d'una anotació.
 */
public class ApiRestIntegracioTest {

	public static String BASE_URL = "https://dev.caib.es/distribucioapi/interna";
	public static String USERNAME = "$helium_distribucio";
	public static String PASSWORD = "****";
	
	/** Prova de crear una regla, descactivar-la i consultar-la. 
	 * @throws Exception */
	public static void main(String[] args) throws Exception {
		
		// Configura el client API REST
		BackofficeIntegracioRestClient restClient = 
				BackofficeIntegracioRestClientFactory.getRestClient(BASE_URL, USERNAME, PASSWORD);
		
		// Crea l'objecte id per la consulta
		AnotacioRegistreId id = new AnotacioRegistreId();			
		id.setIndetificador("GOIBE715-DEV/2023");
		id.setClauAcces("JRwLmCaMES6JKAAnzhd5K9Qi4ppjudiLwd+2b5LNsVo=");		

		// Consulta l'anotació
		AnotacioRegistreEntrada anotacio = restClient.consulta(id);
		System.out.println("Anotació consultada: " + anotacio.getIdentificador() + " " + anotacio.getExtracte());
		
		// Canvi d'estat
		restClient.canviEstat(id, Estat.REBUDA, "Observacions text");
		System.out.println("Test finalitzat correctament.");
	}
}
