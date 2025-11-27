package es.caib.distribucio.rest.client.regla;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

import es.caib.distribucio.rest.client.RestClientBase;
import es.caib.distribucio.rest.client.regla.domini.Regla;
import es.caib.distribucio.rest.client.regla.domini.ReglaResponse;

/** Client API REST per donar d'alta regles, consultar-les i modificar-les. 
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ReglesRestClient extends RestClientBase{

	private static final String CARPETA_SERVICE_PATH = "/regla";

	public ReglesRestClient(
			String baseUrl, 
			String username, 
			String password) {
		super(baseUrl, username, password);
	}

	public ReglesRestClient(
			String baseUrl, 
			String username, 
			String password, 
			boolean autenticacioBasic) {
		super(baseUrl, username, password, autenticacioBasic);
	}

	public ReglesRestClient(
			String baseUrl, 
			String username, 
			String password, 
			int connecTimeout,
			int readTimeout) {
		super(baseUrl, username, password, true, connecTimeout, readTimeout);
	}

	public ReglesRestClient(
			String baseUrl, 
			String username, 
			String password,
			boolean autenticacioBasic, 
			int connecTimeout, 
			int readTimeout) {
		super(baseUrl, username, password, autenticacioBasic, connecTimeout, readTimeout);
	}

	
	/** Mètode per crear una regla per un codi Sia, un backoffice i a l'entitat indicada.
	 * 
	 * @throws Exception Llença excepció en cas d'error de comunicació o no controlat.
	 */
	public ReglaResponse add (
			String entitat, 
			String sia,
			String tipus,
			String backoffice,
			Boolean presencial) throws RuntimeException {
		
		ReglaResponse ret = null;
		try {
			String urlAmbMetode = baseUrl + CARPETA_SERVICE_PATH + "/add?entitat=" + entitat + "&sia=" + sia + "&tipusSia=" + tipus + "&backoffice=" + backoffice + "&presencial=" + (presencial != null ? presencial.toString() : "");
			Client jerseyClient = generarClient(urlAmbMetode);
			ClientResponse response = jerseyClient
					.resource(urlAmbMetode)
					.post(ClientResponse.class);
			
			ret = this.buildResponse(response);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ret;
	}
	
	/** Mètode per activar/desactivar una regla.
	 * 
	 * @throws Exception Llença excepció en cas d'error de comunicació o no controlat.
	 */
	public ReglaResponse canviEstat(
			String sia,
			Boolean activa)  throws RuntimeException {
		ReglaResponse ret = null;
		try {
			String urlAmbMetode = baseUrl + CARPETA_SERVICE_PATH + "/canviEstat?sia=" + sia + "&activa=" + (activa != null? activa.booleanValue() : "");
			Client jerseyClient = generarClient(urlAmbMetode);
			ClientResponse response = jerseyClient
					.resource(urlAmbMetode)
					.post(ClientResponse.class);
			
			ret = this.buildResponse(response);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ret;
	}
	
	/** 
	 * Mètode per canviar els estats dels camps booleans 'activa' i 'presencial'
	 * 
	 * @throws Exception Llença excepció en cas d'error de comunicació o no controlat.
	 */
	public ReglaResponse update(
			String sia,
			Boolean activa,
			Boolean presencial) throws RuntimeException {
		ReglaResponse ret = null;
			
		try {
			String urlAmbMetode = baseUrl + CARPETA_SERVICE_PATH + "/update" + "?sia=" + sia + 
											"&activa=" + (activa != null ? activa.toString() : "") + 
											"&presencial=" + (presencial != null ? presencial.toString() : "");
			Client jerseyClient = generarClient(urlAmbMetode);
			ClientResponse response = jerseyClient
					.resource(urlAmbMetode)
					.post(ClientResponse.class);
			ret = this.buildResponse(response);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ret;
	}

	
	/** Mètode per consultar regles per codi SIA.
	 * 
	 * @param sia Codi sia del procediment per consultar la regla associada.
	 * 
	 * @throws Exception Error en cas de no poder generar el client, de mal format de dades o error en la comunicació.
	 */
	public Regla consultarRegla (
			String sia) throws RuntimeException {
		Regla regla = null;
		try {
			String urlAmbMetode = baseUrl + CARPETA_SERVICE_PATH + "/consultarRegla?sia=" + sia;

			Client jerseyClient = generarClient(urlAmbMetode);
			String json = jerseyClient.
					resource(urlAmbMetode).
					type("application/json").
					get(String.class);
			List<Map<String, Object>> regles = getMapper().readValue(json, new TypeReference<List<Map<String, Object>>>(){});
			if (regles != null && !regles.isEmpty() ) {
				regla = new Regla();
				Map<String, Object> reg = regles.get(0);
				regla.setId(Long.valueOf(reg.get("id").toString()));
				regla.setEntitat(String.valueOf(reg.get("entitat")));
				regla.setData(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse((String) reg.get("data")));
				regla.setNom((String)reg.get("nom"));
				regla.setActiva(Boolean.valueOf(String.valueOf(reg.get("activa"))));
				regla.setPresencial((Boolean) reg.get("presencial"));
				if (reg.containsKey("backofficeDesti")) {
					regla.setBackofficeDesti(String.valueOf(reg.get("backofficeDesti")));					
				}
			}			
		} catch (Exception ex) { 
			if (ex instanceof UniformInterfaceException
					&& ((UniformInterfaceException) ex).getResponse().getStatus() == 404) {
					// es considera com no trobat i es retorna null
			} else {
				throw new RuntimeException(ex);
			}
		}

		return regla;
	}

	/** Mètode provat per retornar una resposta de creació de regles.
	 * 
	 * @param response
	 * @return
	 */
	private ReglaResponse buildResponse(ClientResponse response) {
		int status = response.getStatus();
		String reasonPhrase = response.getStatusInfo().getReasonPhrase();
		String resp = response.getEntity(String.class);
		String msg = status + " " + reasonPhrase + ": " + resp;
		return new ReglaResponse(
				response.getStatus(), 
				msg);
	}

}
