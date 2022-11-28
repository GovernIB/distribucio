/**
 * 
 */
package es.caib.distribucio.ws.client;

import static org.junit.Assert.fail;

import java.io.IOException;

import javax.xml.datatype.DatatypeConfigurationException;

import org.junit.Test;

import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreEntrada;
import es.caib.distribucio.ws.backofficeintegracio.AnotacioRegistreId;
import es.caib.distribucio.ws.backofficeintegracio.BackofficeIntegracio;
import es.caib.distribucio.ws.backofficeintegracio.Estat;
/**
 * Client de test per al servei bustia de RIPEA.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BackofficeIntegracioTest {
	
	private static final String IDENTIFICADOR = "GOIBE1669628521530/2022"; 
	private static final String CLAU_ACCESS = "IsCo71eGcWiq14eEP3USG8GD7pt0OYusdLP1DB7EIAM=";
	
	@Test
	public void consulta() throws DatatypeConfigurationException, IOException {

		try {
			
			AnotacioRegistreId anotacioRegistreId = new AnotacioRegistreId();
			anotacioRegistreId.setIndetificador(IDENTIFICADOR);
			anotacioRegistreId.setClauAcces(CLAU_ACCESS);		
			
			
			AnotacioRegistreEntrada response = getBustiaServicePort().consulta(
					anotacioRegistreId);
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
			
			getBustiaServicePort().canviEstat(
					anotacioRegistreId, 
					Estat.ERROR, 
					"Error");
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
	
	
	private BackofficeIntegracio getBustiaServicePort() throws IOException {
		return BackofficeIntegracioWsClientFactory.getWsClient(
				"http://10.35.3.232:8080/distribucio/ws/backofficeIntegracio",
				"danielm",				
				"danielm");				
	}	

}
