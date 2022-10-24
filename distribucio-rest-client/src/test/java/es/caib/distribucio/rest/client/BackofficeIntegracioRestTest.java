/**
 * 
 */
package es.caib.distribucio.rest.client;

import static org.junit.Assert.fail;

import java.io.IOException;

import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import es.caib.distribucio.rest.client.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.domini.Estat;

/**
 * Client de test per al servei bustia de RIPEA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BackofficeIntegracioRestTest {
	private static final String URL_BASE = "http://10.35.3.232:8080/distribucio/";	
	private static final String USER = "admin";
	private static final String PASS = "admin";

	private static final String IDENTIFICADOR = "GOIBE1657117867882/2022";
	private static final String CLAU_ACCESS = "AVtXSNHCgNEIC8XndUkSExtd7CQ1ecd81v1zBWVhTrE=";

	@Test
	public void consulta() throws DatatypeConfigurationException, IOException {

		try {
			
			AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
			anotacioRegistreId.setIndetificador(IDENTIFICADOR);
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
			anotacioRegistreId.setIndetificador(IDENTIFICADOR);
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

	private BackofficeIntegracioRestClient getClientRest() {
		return BackofficeIntegracioRestClientFactory.getRestClient(URL_BASE, USER, PASS, false);
	}
}
