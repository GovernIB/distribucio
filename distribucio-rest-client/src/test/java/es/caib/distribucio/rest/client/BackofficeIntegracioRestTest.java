/**
 * 
 */
package es.caib.distribucio.rest.client;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.Test;

import es.caib.distribucio.rest.client.integracio.BackofficeIntegracioRestClient;
import es.caib.distribucio.rest.client.integracio.BackofficeIntegracioRestClientFactory;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.integracio.domini.Estat;

/**
 * Client de test per al servei bustia de RIPEA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BackofficeIntegracioRestTest {
	
	//private static final String URL_BASE = "http://192.168.99.100:8080/distribucioapi/interna";
	private static final String URL_BASE = "http://localhost:8080/distribucioapi/interna";
	private static final String USER = "dis_backws";
	private static final String PASS = "dis_backws";

	private static final String IDENTIFICADOR = "GOIBE1707325316091/2024"; 
	private static final String CLAU_ACCESS = "F1sm0tSDIORwHJEaVJwhZiB72st06qnvokYvCcntbGY=";
	private static final String DATA_REGISTRE = "19/03/2024 08:13:17";

	@Test
	public void consulta() throws DatatypeConfigurationException, IOException {

		try {
			
			AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
			anotacioRegistreId.setIdentificador(IDENTIFICADOR);
			anotacioRegistreId.setClauAcces(CLAU_ACCESS);		
			
			
			AnotacioRegistreEntrada response = getClientRest().consulta(anotacioRegistreId);
			System.out.println("Test: " + response);

		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void canviEstat() throws DatatypeConfigurationException, IOException {

		try {
			AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
			anotacioRegistreId.setIdentificador(IDENTIFICADOR);
			anotacioRegistreId.setClauAcces(CLAU_ACCESS);

			getClientRest().canviEstat(
					anotacioRegistreId, 
					Estat.ERROR,
					"Error");
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	@Test
	public void consulta_i_canviEstat() throws DatatypeConfigurationException, IOException {

		try {
			BackofficeIntegracioRestClient client = getClientRest();
			
			AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
			anotacioRegistreId.setIdentificador(IDENTIFICADOR);
			anotacioRegistreId.setClauAcces(CLAU_ACCESS);		

			// Consulta
			AnotacioRegistreEntrada response = client.consulta(anotacioRegistreId);
			System.out.println("Test: " + response);
			
			// Canvi estat
			client.canviEstat(
					anotacioRegistreId, 
					Estat.ERROR,
					"Error");
			
			client.consulta(anotacioRegistreId);
			client.canviEstat(
					anotacioRegistreId, 
					Estat.ERROR,
					"Error");
			client.consulta(anotacioRegistreId);
			client.canviEstat(
					anotacioRegistreId, 
					Estat.ERROR,
					"Error");
			client.consulta(anotacioRegistreId);
			client.canviEstat(
					anotacioRegistreId, 
					Estat.ERROR,
					"Error");
			client.consulta(anotacioRegistreId);
			client.canviEstat(
					anotacioRegistreId, 
					Estat.ERROR,
					"Error");
			client.consulta(anotacioRegistreId);
			client.canviEstat(
					anotacioRegistreId, 
					Estat.ERROR,
					"Error");

		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void llistar() throws DatatypeConfigurationException, IOException {

		try {
			List<AnotacioRegistreEntrada> response = getClientRest().llistar(IDENTIFICADOR, DATA_REGISTRE);
			
			System.out.println("Test: " + response);

		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}

	
	private BackofficeIntegracioRestClient getClientRest() {
		return BackofficeIntegracioRestClientFactory.getRestClient(URL_BASE, USER, PASS);
	}
}
