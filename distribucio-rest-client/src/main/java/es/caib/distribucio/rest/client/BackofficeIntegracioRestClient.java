package es.caib.distribucio.rest.client;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.UriBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;

import es.caib.distribucio.rest.client.domini.AnotacioRegistreEntrada;
import es.caib.distribucio.rest.client.domini.AnotacioRegistreId;
import es.caib.distribucio.rest.client.domini.Estat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BackofficeIntegracioRestClient {

    private static final String BACKOFFICE_SERVICE_PATH = "/backoffice";

    protected String baseUrl;
    protected String username;
    protected String password;

    protected boolean autenticacioBasic = true;
    protected int connecTimeout = 20000;
    protected int readTimeout = 120000;
    
    private Client jerseyClient;
    
    public BackofficeIntegracioRestClient(String baseUrl, String username, String password) {
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

    protected Client generarClient(String urlAmbMetode) throws Exception {
    	if (this.jerseyClient == null) {
    		this.jerseyClient = generarClient();
    	} else {
    		return this.jerseyClient;
    	}
    	if (username != null) {
            autenticarClient(
            		this.jerseyClient,
                    urlAmbMetode,
                    username,
                    password);
        }
        return this.jerseyClient;
    }

    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    
    protected Client generarClient() {
        this.jerseyClient = Client.create();
        jerseyClient.setConnectTimeout(connecTimeout);
        jerseyClient.setReadTimeout(readTimeout);
        jerseyClient.addFilter(
                new ClientFilter() {
                    private List<Object> cookies  = new ArrayList<Object>();
                    @Override
                    public ClientResponse handle(ClientRequest request) throws ClientHandlerException {

                    	synchronized(cookies) {
                            if (!cookies.isEmpty()) {
                                request.getHeaders().put("Cookie", cookies);
                            }
                    	}
                        
                    	ClientResponse response = getNext().handle(request);
                        
                        if (response.getCookies() != null) {
                        	List<Object> novesCookies = new ArrayList<>();
                        	for (Object cookie : response.getCookies()) {
                        		if (! SESSION_COOKIE_NAME.equals( ((NewCookie) cookie).getName()) ) {
                        			novesCookies.add(cookie);
                        		}
                        	}
                        	if (!novesCookies.isEmpty()) {
                            	synchronized(cookies) {
                            		cookies.clear();
                            		cookies.addAll(novesCookies);
                            	}
                        	}
                        }
                        return response;
                    }
                }
        );
        jerseyClient.addFilter(
                new ClientFilter() {
                    @Override
                    public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
                        ClientHandler ch = getNext();
                        ClientResponse resp = ch.handle(request);

                        if (resp.getStatus()/100 != 3) {
                            return resp;
                        } else {
                            String redirectTarget = resp.getHeaders().getFirst("Location");
                            request.setURI(UriBuilder.fromUri(redirectTarget).build());
                            return ch.handle(request);
                        }
                    }
                }
        );
        return jerseyClient;
    }

    protected void autenticarClient(
            Client jerseyClient,
            String urlAmbMetode,
            String username,
            String password) throws Exception {
        if (!autenticacioBasic) {
            log.debug(
                    "Autenticant client REST per a fer peticions cap a servei desplegat a damunt jBoss (" +
                            "urlAmbMetode=" + urlAmbMetode + ", " +
                            "username=" + username +
                            "password=********)");
            jerseyClient.resource(urlAmbMetode).get(String.class);
            Form form = new Form();
            form.putSingle("j_username", username);
            form.putSingle("j_password", password);
            jerseyClient.
                    resource(baseUrl + "/j_security_check").
                    type("application/x-www-form-urlencoded").
                    post(form);
        } else {
            log.debug(
                    "Autenticant REST amb autenticació de tipus HTTP basic (" +
                            "urlAmbMetode=" + urlAmbMetode + ", " +
                            "username=" + username +
                            "password=********)");
            jerseyClient.addFilter(
                    new HTTPBasicAuthFilter(username, password));
        }
    }

    protected ObjectMapper getMapper() {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
