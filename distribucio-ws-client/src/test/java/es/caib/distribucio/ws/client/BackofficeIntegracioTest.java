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
	
	// DES
	private static final String IDENTIFICADOR = "GOIBE1707325316091/2024"; 
	private static final String CLAU_ACCESS = "F1sm0tSDIORwHJEaVJwhZiB72st06qnvokYvCcntbGY=";
	private static final String URL = "http://localhost:8080/distribucio/ws/backofficeIntegracio";
	private static final String USUARI = "$helium_dis";
	private static final String PASSWORD = "helium_dis";
	
	// SE
//	private static final String IDENTIFICADOR = "GOIBE2283-SE/2023"; 
//	private static final String CLAU_ACCESS = "p8EXNvIYMUp7fFfnApvr08OAuriUNID4K8xuAEpTHtA=";
//	private static final String URL = "https://se.caib.es/distribucio/ws/backofficeIntegracio";
//	private static final String USUARI = "dis_backws";
//	private static final String PASSWORD = "dis_backws";
	
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
				URL,
				USUARI,				
				PASSWORD);				
	}	

}
