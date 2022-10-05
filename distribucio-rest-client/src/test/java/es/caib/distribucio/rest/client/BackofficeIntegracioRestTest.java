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
	public void loginsTest() throws DatatypeConfigurationException, IOException {


			
			do {
				try {
					// Fer login de 100 usuaris diferents
					for (int i = 1; i <= 100; i++) {
						String disuser = "disuser" + i;
						System.out.println("login " + disuser);
						// login
						Client client = BackofficeIntegracioRestClientFactory.getRestClient(URL_BASE, disuser, disuser, false)
								.generarClient(URL_BASE);
//						 Client client = BackofficeIntegracioRestClientFactory.getRestClient(URL_BASE,
//						 "disregla", "disregla", false).generarClient(URL_BASE);
						// Pàgina principal
						String urlString = URL_BASE;
						WebResource webResource = client.resource(urlString);
						ClientResponse response = webResource.get(ClientResponse.class);
						String output = response.getEntity(String.class);
						// datatable
						urlString = URL_BASE + "registreUser/datatable?draw=1&columns%5B0%5D%5Bdata%5D=%3Cnull%3E&columns%5B0%5D%5Bname%5D=&columns%5B0%5D%5Borderable%5D=false&columns%5B1%5D%5Bdata%5D=id&columns%5B2%5D%5Bdata%5D=error&columns%5B3%5D%5Bdata%5D=alerta&columns%5B4%5D%5Bdata%5D=enviatPerEmail&columns%5B5%5D%5Bdata%5D=enviamentsPerEmail&columns%5B6%5D%5Bdata%5D=procesEstatSimple&columns%5B7%5D%5Bdata%5D=perConeixement&columns%5B8%5D%5Bdata%5D=reactivat&columns%5B9%5D%5Bdata%5D=agafat&columns%5B10%5D%5Bdata%5D=agafatPer.codi&columns%5B11%5D%5Bdata%5D=procesError&columns%5B12%5D%5Bdata%5D=numero&columns%5B13%5D%5Bdata%5D=extracte&columns%5B14%5D%5Bdata%5D=documentacioFisicaCodi&columns%5B15%5D%5Bdata%5D=numeroOrigen&columns%5B16%5D%5Bdata%5D=darrerMovimentUsuari&columns%5B16%5D%5Borderable%5D=false&columns%5B17%5D%5Bdata%5D=data&columns%5B18%5D%5Bdata%5D=procesEstat&columns%5B19%5D%5Bdata%5D=procesError&columns%5B19%5D%5Borderable%5D=false&columns%5B20%5D%5Bdata%5D=path&columns%5B20%5D%5Borderable%5D=false&columns%5B21%5D%5Bdata%5D=interessatsResum&columns%5B21%5D%5Borderable%5D=false&columns%5B22%5D%5Bdata%5D=numComentaris&columns%5B22%5D%5Borderable%5D=false&columns%5B23%5D%5Bdata%5D=id&columns%5B23%5D%5Borderable%5D=false&columns%5B24%5D%5Bdata%5D=bustiaActiva&columns%5B25%5D%5Bdata%5D=reintentsEsgotat&columns%5B26%5D%5Bdata%5D=procesIntents&columns%5B27%5D%5Bdata%5D=maxReintents&columns%5B28%5D%5Bdata%5D=darrerMovimentOrigenUoAndBustia&columns%5B28%5D%5Borderable%5D=false&columns%5B29%5D%5Bdata%5D=oficinaDescripcio&columns%5B29%5D%5Borderable%5D=false&order%5B0%5D%5Bcolumn%5D=17&order%5B0%5D%5Bdir%5D=desc&start=0&length=10&search%5Bvalue%5D=&_=1664797069956";
						webResource = client.resource(urlString);
						response = webResource.accept("application/json").get(ClientResponse.class);
						output = response.getEntity(String.class);
					}
				} catch (Exception ex) {
					System.err.println("Error login: " + ex.getMessage());
				}
			} while (1 != 2);

	}

	//@Test
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
	
	//@Test
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
