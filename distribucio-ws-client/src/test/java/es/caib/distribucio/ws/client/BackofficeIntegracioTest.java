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
	
	private static final String IDENTIFICADOR = "GOIBE2283-SE/2023"; 
	private static final String CLAU_ACCESS = "p8EXNvIYMUp7fFfnApvr08OAuriUNID4K8xuAEpTHtA=";
	
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
					Estat.REBUDA, 
					null);
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			fail();
		}
	}
	
	
	private BackofficeIntegracio getBustiaServicePort() throws IOException {
		return BackofficeIntegracioWsClientFactory.getWsClient(
				"https://se.caib.es/distribucio/ws/backofficeIntegracio",
				"$udit_distribucio",				
				"udit_distribucio");				
	}	

}
