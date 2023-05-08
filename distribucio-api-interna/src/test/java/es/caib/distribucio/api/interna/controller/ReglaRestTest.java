package es.caib.distribucio.api.interna.controller;

import java.util.ArrayList;
import java.util.Date;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

/** Test per provar l'API REST de reglees de l'api interna usant un client jersey.
 * 
 */
public class ReglaRestTest {

	public static String BASE_URL = "https://dev.caib.es/distribucioapi/interna/regla";
	public static String ENTITAT = "A04003003";
	public static String BACKOFFICE = "helium";
	public static String USERNAME = "$helium_distribucio";
	public static String PASSWORD = "****";
	
	/** Prova de crear una regla, descactivar-la i consultar-la. */
	public static void main(String[] args) {
	
		
		// Configura el client jersey.
		ClientResponse response;
		Client jerseyClient = Client.create();
		// Afegeix l'autenticació WS Basic
		jerseyClient.addFilter(
				new HTTPBasicAuthFilter(USERNAME, PASSWORD));
		// Afegeix un filtre per evitar fer login en cada petició
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

		String sia = String.valueOf(new Date().getTime());
		// Crea una nova regla
		response = jerseyClient
				.resource(BASE_URL + "/add?entitat=" + ENTITAT + "&sia=" + sia + "&backoffice=" + BACKOFFICE)
				.post(ClientResponse.class);
		System.out.println("Resposta de la creació de la regla: " + response.getStatus() + " " + response.getEntity(String.class));
		
		// Desactiva la regla
		response = jerseyClient
				.resource(BASE_URL + "/canviEstat?&sia=" + sia + "&activa=false")
				.post(ClientResponse.class);
		System.out.println("Resposta de la desactivació de la regla: " + response.getStatus() + " " + response.getEntity(String.class));
		
		// Consulta la regla
		response = jerseyClient
				.resource(BASE_URL + "/consultarRegla?sia=" + sia)
				.get(ClientResponse.class);		
		System.out.println("Resposta de la consulta de la regla: " + response.getStatus() + " " + response.getEntity(String.class));
	}
}
