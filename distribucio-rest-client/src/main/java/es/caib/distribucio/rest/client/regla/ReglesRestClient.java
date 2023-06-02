package es.caib.distribucio.rest.client.regla;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import es.caib.distribucio.rest.client.RestClientBase;

/** Client API REST per donar d'alta regles, consultar-les i modificar-les. */

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
	 */
	public boolean add (
			String entitat, 
			String sia,
			String backoffice) {
		boolean ret = false;
		try {
			String urlAmbMetode = baseUrl + CARPETA_SERVICE_PATH + "/add?entitat=" + entitat + "&sia=" + sia + "&backoffice=" + backoffice;
			Client jerseyClient = generarClient(urlAmbMetode);
			ClientResponse response = jerseyClient
					.resource(urlAmbMetode)
					.post(ClientResponse.class);
			
			System.out.println("Resposta de la creació de la regla " + response.getStatus() + ": " 
								+ response.getEntity(String.class));
            if (response.getStatus()==200){
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
			Client jerseyClient = generarClient(urlAmbMetode);
			ClientResponse response = jerseyClient
					.resource(urlAmbMetode)
					.post(ClientResponse.class);
			
			System.out.println("Resposta de l'actualització de la regla " + response.getStatus() 
								+ ": " + response.getEntity(String.class));

            if (response.getStatus()==200){
				ret = true;
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ret;
	}
	
	/** 
	 * 
	 * Mètode per canviar els estats dels camps booleans 'activa' i 'presencial'
	 *
	 */
	public boolean update(String sia,
			boolean activa,
			boolean presencial) {
		boolean ret = false;
		try {
			
			String urlAmbMetode = baseUrl + CARPETA_SERVICE_PATH + "/update" + "?sia=" + sia + "&activa=" + activa + "&presencial=" + presencial;
			Client jerseyClient = generarClient(urlAmbMetode);
			ClientResponse response = jerseyClient
					.resource(urlAmbMetode)
					.post(ClientResponse.class);
			
			System.out.println("Resposta de l'actualització de la regla " + response.getStatus() 
								+ ": " + response.getEntity(String.class));

			if (response.getStatus() == 200) {
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
			Client jerseyClient = generarClient(urlAmbMetode);
			ClientResponse response = jerseyClient
					.resource(urlAmbMetode)
					.get(ClientResponse.class);
			
			System.out.println("Resposta de la consulta de la regla " + response.getStatus() 
								+ ": " + response.getEntity(String.class));

            if (response.getStatus()==200){
				ret = true;
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return ret;
	}


}
