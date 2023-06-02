package es.caib.distribucio.rest.client.integracio;

import java.net.URLEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;

import es.caib.distribucio.rest.client.RestClientBase;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.integracio.domini.Estat;

/** Client de l'API REST del servei per a la integració de backoffices. Permet consultar 
 * i canviar l'estat a una anotació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 *
 */
public class BackofficeIntegracioRestClient extends RestClientBase{

	protected String BACKOFFICE_SERVICE_PATH = "/backoffice";

	public BackofficeIntegracioRestClient(
			String baseUrl, 
			String username, 
			String password) {
		super(baseUrl, username, password);
	}

	public BackofficeIntegracioRestClient(
			String baseUrl, 
			String username, 
			String password, 
			boolean autenticacioBasic) {
		super(baseUrl, username, password, autenticacioBasic);
	}

	public BackofficeIntegracioRestClient(
			String baseUrl, 
			String username, 
			String password, 
			int connecTimeout,
			int readTimeout) {
		super(baseUrl, username, password, true, connecTimeout, readTimeout);
	}

	public BackofficeIntegracioRestClient(
			String baseUrl, 
			String username, 
			String password,
			boolean autenticacioBasic, 
			int connecTimeout, 
			int readTimeout) {
		super(baseUrl, username, password, autenticacioBasic, connecTimeout, readTimeout);
	}
	
    // MÈTODES
    // ////////////////////////////////////////////////


	public AnotacioRegistreEntrada consulta(AnotacioRegistreId id) throws Exception {
        String urlAmbMetode = baseUrl + BACKOFFICE_SERVICE_PATH + "/consulta";
        Client jerseyClient = generarClient(urlAmbMetode);
        String json = jerseyClient.
                resource(urlAmbMetode).
                queryParam("indetificador", URLEncoder.encode(id.getIndetificador(), "UTF-8")).
                queryParam("clauAcces", URLEncoder.encode(id.getClauAcces(), "UTF-8")).
                type("application/json").
                get(String.class);
        return getMapper().readValue(json, AnotacioRegistreEntrada.class);
    }


    public void canviEstat(
            AnotacioRegistreId id,
            Estat estat,
            String observacions) throws Exception {
        InfoCanviEstat infoCanviEstat = InfoCanviEstat.builder()
                .id(id)
                .estat(estat)
                .observacions(observacions).build();
        String urlAmbMetode = baseUrl + BACKOFFICE_SERVICE_PATH + "/canviEstat";
        ObjectMapper mapper  = getMapper();
        String body = mapper.writeValueAsString(infoCanviEstat);
        Client jerseyClient = generarClient(urlAmbMetode);
        jerseyClient.
                resource(urlAmbMetode).
                type("application/json").
                post(body);
    }

}
