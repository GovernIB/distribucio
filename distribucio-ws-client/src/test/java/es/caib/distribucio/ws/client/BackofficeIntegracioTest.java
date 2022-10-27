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
	private static final String IDENTIFICADOR = "GOIBE4347/2022"; //"L11E1646737371605/2022"; //DES: "GOIBE4347/2022" 
	private static final String CLAU_ACCESS = "dwWOGZgn0kTPVex5QNdsymYR8MwFv01arTjJIOodgQQ="; //"iqV8SiGk90e9sv2DqQfdCC0E4r//5M/WyG0K0fE19dg="; //DES: "dwWOGZgn0kTPVex5QNdsymYR8MwFv01arTjJIOodgQQ="
	
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
				"http://localhost:8080/distribucio/ws/backofficeIntegracio",
				"admin",				
				"admin");				
	}									
	

	/* DES */
//	"http://10.35.3.232:8080/distribucio/ws/backofficeIntegracio",
//	"admin",				
//	"admin"
		
	/* PROVES AMB USUARI NOMÉS A SEYCON */
//	usuari: provesFernando
//	contrasenya: provesFernando
	

	


	

}
