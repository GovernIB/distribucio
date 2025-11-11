package es.caib.distribucio.rest.client.bustia;

import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import es.caib.distribucio.rest.client.RestClientBase;
import es.caib.distribucio.rest.client.bustia.domini.PeticioAlta;
import es.caib.distribucio.rest.client.bustia.domini.RegistreAnotacio;

public class BustiaRestClient  extends RestClientBase{

	private static final String BUSTIA_SERVICE_PATH = "/bustia";
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	public BustiaRestClient(
			String baseUrl, 
			String username, 
			String password) {
		super(baseUrl, username, password);
	}

	public BustiaRestClient(
			String baseUrl, 
			String username, 
			String password, 
			boolean autenticacioBasic) {
		super(baseUrl, username, password, autenticacioBasic);
	}

	public BustiaRestClient(
			String baseUrl, 
			String username, 
			String password, 
			int connecTimeout,
			int readTimeout) {
		super(baseUrl, username, password, true, connecTimeout, readTimeout);
	}

	public BustiaRestClient(
			String baseUrl, 
			String username, 
			String password,
			boolean autenticacioBasic, 
			int connecTimeout, 
			int readTimeout) {
		super(baseUrl, username, password, autenticacioBasic, connecTimeout, readTimeout);
	}

	
	public void alta (
			String entitat, 
			String unitatAdministrativa,
			RegistreAnotacio registreAnotacio) throws RuntimeException {
		try {	
			String urlAmbMetode = baseUrl + BUSTIA_SERVICE_PATH + "/alta";
			
			PeticioAlta peticio = new PeticioAlta(entitat, unitatAdministrativa, registreAnotacio);
			
			String request = objectMapper.writeValueAsString(peticio);
			
			Client jerseyClient = generarClient(urlAmbMetode);
			
			jerseyClient
				.resource(urlAmbMetode)
				.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, request);
			
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
