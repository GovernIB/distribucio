/**
 * 
 */
package es.caib.distribucio.ws.client;

import java.net.MalformedURLException;

import javax.xml.namespace.QName;

import es.caib.distribucio.ws.backofficeintegracio.BackofficeIntegracio;
import es.caib.distribucio.ws.v1.bustia.BustiaV1;

/**
 * Utilitat per a instanciar clients per al servei d'enviament
 * de contingut a bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BackofficeIntegracioWsClientFactory {



	public static BackofficeIntegracio getWsClient(
			String endpoint,
			String userName,
			String password) throws MalformedURLException {
		return new WsClientHelper<BackofficeIntegracio>().generarClientWs(
				endpoint,
				new QName(
						"http://www.caib.es/distribucio/ws/backofficeIntegracio",
						"BackofficeIntegracioService"),
				userName,
				password,
				BackofficeIntegracio.class);
	}

}
