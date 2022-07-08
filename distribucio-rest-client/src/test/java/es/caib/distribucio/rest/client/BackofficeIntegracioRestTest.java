/**
 * 
 */
package es.caib.distribucio.rest.client;

import es.caib.distribucio.rest.client.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.domini.Estat;
import org.junit.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;

import static org.junit.Assert.fail;

/**
 * Client de test per al servei bustia de RIPEA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BackofficeIntegracioRestTest {
	private static final String URL_BASE = "http://localhost:8280/distribucioapi/interna";
	private static final String USER = "admin";
	private static final String PASS = "admin";

	private static final String IDENTIFICADOR = "GOIBE1657117867882/2022"; //L11E1557929165976/2019
	private static final String CLAU_ACCESS = "AVtXSNHCgNEIC8XndUkSExtd7CQ1ecd81v1zBWVhTrE="; //jqCJ/24gm+j5VGtETYj8itrQmlD6zwHp4BL8g33ulh4=

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
		return BackofficeIntegracioRestClientFactory.getRestClient(URL_BASE, USER, PASS);
	}
	

	


	

}
