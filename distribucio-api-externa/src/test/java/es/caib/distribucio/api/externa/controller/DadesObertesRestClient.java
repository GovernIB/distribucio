package es.caib.distribucio.api.externa.controller;

import java.rmi.RemoteException;
import java.util.List;

import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;

import es.caib.distribucio.logic.intf.dto.dadesobertes.BustiaDadesObertesDto;


/**
 * Client REST de l'API REST de consulta de dades obertes de Distribicio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesObertesRestClient {

	private static final String CARPETA_SERVICE_PATH = "/externa/opendata";

	private String baseUrl;
	private String username;
	private String password;

	private boolean autenticacioBasic = false;

	public DadesObertesRestClient() {}
	public DadesObertesRestClient(
			String baseUrl,
			String username,
			String password) {
		super();
		this.baseUrl = baseUrl;
		this.username = username;
		this.password = password;
	}

	public DadesObertesRestClient(
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

	public List<BustiaDadesObertesDto> busties(
			Long bustiaId,
			String uo,
			String uoSuperior) {
		try {
			String urlAmbMetode = baseUrl + CARPETA_SERVICE_PATH + "/busties?";
			if (bustiaId != null)
					urlAmbMetode += "&bustiaId=" + bustiaId; 
			if (uo != null)
				urlAmbMetode += "&uo=" + uo; 
			if (uoSuperior != null)
				urlAmbMetode += "&uoSuperior=" + uoSuperior; 
			Client jerseyClient = generarClient();
			if (username != null) {
				autenticarClient(
						jerseyClient,
						urlAmbMetode,
						username,
						password);
			}
			String json = jerseyClient.
					resource(urlAmbMetode).
					type("application/json").
					get(String.class);
			ObjectMapper mapper  = new ObjectMapper();
			List<BustiaDadesObertesDto> resultat = mapper.readValue(json, new TypeReference<List<BustiaDadesObertesDto>>(){});
			return resultat;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public boolean isAutenticacioBasic() {
		return autenticacioBasic;
	}

	private Client generarClient() {
		Client jerseyClient = Client.create();
		/*jerseyClient.addFilter(
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
		);*/
		jerseyClient.addFilter(
				new ClientFilter() {
					@Override
					public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
						ClientHandler ch = getNext();
						ClientResponse resp = ch.handle(request);
						if (resp.getStatusInfo().getFamily() != Response.Status.Family.REDIRECTION) {
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
					"username=" + username + ", " +
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
					"username=" + username + ", " +
					"password=********)");
			jerseyClient.addFilter(
					new HTTPBasicAuthFilter(username, password));
		}
	}

}
