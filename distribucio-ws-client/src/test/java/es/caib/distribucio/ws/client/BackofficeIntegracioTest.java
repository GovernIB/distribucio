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
	// autofirma wih PADES
	private static final String IDENTIFICADOR = "L11E1660041737586/2022"; //L11E1557929165976/2019
	private static final String CLAU_ACCESS = "JyYoc/Oq8gCh4AwODQOAqw8ODKkc3f3AapnhGvkFuRQ="; //jqCJ/24gm+j5VGtETYj8itrQmlD6zwHp4BL8g33ulh4=
	
//	// autofirma wih CADES
//	private static final String IDENTIFICADOR = "L11E1557470209883/2019";
//	private static final String CLAU_ACCESS = "oj8hh9dnK7GojsCALPK1UfKibLbmJ64CMnpNfXXPdDo=";

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
				"admin",				// proves amb usuari només a SEYCON
				"admin");				// usuari: provesFernando
	}									// contrasenya: provesFernando
	

	


	

}
