package es.caib.distribucio.war.rest;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;


/**
 * Client REST de l'API REST de creació de regles automàtiques de Distribucio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ReglesRestClient {

	private static final String CARPETA_SERVICE_PATH = "/api/rest/regles";
	//	private static final String CARPETA_SERVICE_PATH = "/regla";

	private String baseUrl;
	private String username;
	private String password;

	private boolean autenticacioBasic = true;

	public ReglesRestClient() {}
	public ReglesRestClient(
			String baseUrl,
			String username,
			String password) {
		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
	}
	
	public ReglesRestClient(
			String baseUrl,
			String username,
			String password,
			boolean autenticacioBasic) {
		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
		this.autenticacioBasic = autenticacioBasic;
	}

	/** Mètode per crear una regla per un codi Sia, un backoffice i a l'entitat indicada.
	 * 
	 */
	public boolean add(
			String entitat, 
			String sia,
			String backoffice) {
		boolean ret = false;
		try {
			String urlAmbMetode = baseUrl + CARPETA_SERVICE_PATH + "/add?entitat=" + entitat + "&sia=" + sia + "&backoffice=" + backoffice;
			Client jerseyClient = generarClient();
			if (username != null) {
				autenticarClient(
						jerseyClient,
						urlAmbMetode,
						username,
						password);
			}
			ClientResponse response = jerseyClient
					.resource(urlAmbMetode)
					.post(ClientResponse.class);
			
			System.out.println("Resposta de la creació de la regla " + response.getStatus() + ": " 
								+ response.getEntity(String.class));
            if (response.getStatus()/100 != 2) {
			//if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
				ret = true;
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ret;
	}
	
	/** Mètode per activar/desactivar una regla.
	 * 
	 */
	public boolean canviEstat(
			String sia,
			Boolean activa) {
		boolean ret = false;
		try {
			String urlAmbMetode = baseUrl + CARPETA_SERVICE_PATH + "/canviEstat?sia=" + sia + "&activa=" + (activa != null? activa.booleanValue() : "");
			Client jerseyClient = generarClient();
			if (username != null) {
				autenticarClient(
						jerseyClient,
						urlAmbMetode,
						username,
						password);
			}
			ClientResponse response = jerseyClient
					.resource(urlAmbMetode)
					.post(ClientResponse.class);
			
			System.out.println("Resposta de l'actualització de la regla " + response.getStatus() 
								+ ": " + response.getEntity(String.class));

            if (response.getStatus()/100 != 2) {
			//if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
				ret = true;
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ret;
	}
	
	/** Mètode per consultar regles per codi SIA.
	 * 
	 */
	public boolean consultarRegla(
			String sia) {
		boolean ret = false;
		try {
			String urlAmbMetode = baseUrl + CARPETA_SERVICE_PATH + "/consultarRegla?sia=" + sia;
			Client jerseyClient = generarClient();
			if (username != null) {
				autenticarClient(
						jerseyClient,
						urlAmbMetode,
						username,
						password);
			}
			ClientResponse response = jerseyClient
					.resource(urlAmbMetode)
					.get(ClientResponse.class);
			
			System.out.println("Resposta de la consulta de la regla " + response.getStatus() 
								+ ": " + response.getEntity(String.class));

            if (response.getStatus()/100 != 2) {
			//if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
				ret = true;
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ret;
	}

	
	public boolean isAutenticacioBasic() {
		return autenticacioBasic;
	}

	private Client generarClient() {
		Client jerseyClient = Client.create();
		jerseyClient.addFilter(
				new ClientFilter() {
					private ArrayList<Object> cookies;
					@Override
					public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
						if (cookies != null) {
							request.getHeaders().put("Cookie", cookies);
						}
						ClientResponse response = getNext().handle(request);
						if (response.getCookies() != null) {
							if (cookies == null) {
								cookies = new ArrayList<Object>();
							}
							cookies.addAll(response.getCookies());
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
			    			//if (resp.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
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

	private void autenticarClient(
			Client jerseyClient,
			String urlAmbMetode,
			String username,
			String password) throws InstanceNotFoundException, MalformedObjectNameException, RemoteException, NamingException {
		if (!autenticacioBasic) {
			System.out.println(
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
			System.out.println(
					"Autenticant REST amb autenticació de tipus HTTP basic (" +
					"urlAmbMetode=" + urlAmbMetode + ", " +
					"username=" + username +
					"password=********)");
			jerseyClient.addFilter(
					new HTTPBasicAuthFilter(username, password));
		}
	}
}
