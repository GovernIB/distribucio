package es.caib.distribucio.rest.client.integracio;

import java.net.URLEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;

import es.caib.distribucio.rest.client.RestClientBase;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.integracio.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.integracio.domini.Estat;

public class BackofficeIntegracioRestClient extends RestClientBase{

	protected String BACKOFFICE_SERVICE_PATH = "/backoffice";

    
    BackofficeIntegracioRestClient(String baseUrl, String username, String password) {
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
    }

    public BackofficeIntegracioRestClient(String baseUrl, String username, String password, boolean autenticacioBasic) {
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
        this.autenticacioBasic = autenticacioBasic;
    }

    public BackofficeIntegracioRestClient(String baseUrl, String username, String password, int connecTimeout, int readTimeout) {
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
        this.connecTimeout = connecTimeout;
        this.readTimeout = readTimeout;
    }

    public BackofficeIntegracioRestClient(String baseUrl, String username, String password, boolean autenticacioBasic, int connecTimeout, int readTimeout) {
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
        this.autenticacioBasic = autenticacioBasic;
        this.connecTimeout = connecTimeout;
        this.readTimeout = readTimeout;
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
