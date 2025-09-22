/**
 * 
 */
package es.caib.distribucio.plugin.caib.servei;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.distribucio.logic.intf.dto.ServeiDto;
import es.caib.distribucio.plugin.AbstractSalutPlugin;
import es.caib.distribucio.plugin.DistribucioAbstractPluginProperties;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.servei.Servei;
import es.caib.distribucio.plugin.servei.ServeiPlugin;
import es.caib.distribucio.plugin.utils.PropertiesHelper;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Implementació del plugin de consulta de serveis emprant ROLSAC.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiPluginRolsac extends DistribucioAbstractPluginProperties implements ServeiPlugin {

	private Client jerseyClient;
	private ObjectMapper mapper;

	public ServeiPluginRolsac() {
		super();
	}
	
	public ServeiPluginRolsac(Properties properties) {
		super(properties);
	}
	
	@Override
	public List<Servei> findAmbCodiDir3(
			String codiDir3) throws SistemaExternException {
		logger.debug("Consulta dels serveis de l'unitat organitzativa (" +
				"codiDir3=" + codiDir3 + ")");
		ProcedimientosResponse response = null;
		long start = System.currentTimeMillis();
		try {
			StringBuilder sb = new StringBuilder(getServiceUrl());
			response = findServeisRolsac(
					sb.toString(),
					"lang=ca&filtro={\"codigoUADir3\":\"" + codiDir3 + "\",\"estadoSia\":\"A\",\"buscarEnDescendientesUA\":\"1\"}&filtroPaginacion={\"page\":\"1\", \"size\":\"9999\"}");
		} catch (Exception ex) {
			logger.error("No s'han pogut consultar els serveis de ROLSAC (" +
					"codiDir3=" + codiDir3 + ")",
					ex);
			throw new SistemaExternException(
					"No s'han pogut consultar els serveis de ROLSAC (" +
					"codiDir3=" + codiDir3 + ")",
					ex);
		}
		
		if (response != null && response.getStatus().equals("200")) {
			salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - start);
			return response.getResultado();
		} else {
			salutPluginComponent.incrementarOperacioError();
			logger.error("No s'han pogut consultar els serveis de ROLSAC (" +
					"codiDir3=" + codiDir3 + "). Resposta rebuda amb el codi " + response.getStatus());
			throw new SistemaExternException(
					"No s'han pogut consultar els serveis de ROLSAC (" +
					"codiDir3=" + codiDir3 + "). Resposta rebuda amb el codi " + response.getStatus());
		}
	}

	private Client getJerseyClient() {
		if (jerseyClient == null) {
			jerseyClient = new Client();
			if (getServiceTimeout() != null) {
				jerseyClient.setConnectTimeout(getServiceTimeout());
				jerseyClient.setReadTimeout(getServiceTimeout());
			}
			if (getServiceUsername() != null) {
				jerseyClient.addFilter(new HTTPBasicAuthFilter(getServiceUsername(), getServicePassword()));
			}
			//jerseyClient.addFilter(new LoggingFilter(System.out));
			mapper = new ObjectMapper();
			// Permet rebre un sol objecte en el lloc a on hi hauria d'haver una llista.
			mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
			// Mecanisme de deserialització dels enums
			mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
			// Per a no serialitzar propietats amb valors NULL
			mapper.setSerializationInclusion(Include.NON_NULL);
			// No falla si hi ha propietats que no estan definides a l'objecte destí
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		}
		return jerseyClient;
	}

	private ProcedimientosResponse findServeisRolsac(
			String url,
			String body) throws UniformInterfaceException, ClientHandlerException, IOException {
		logger.debug("Enviant petició HTTP a l'arxiu (" +
				"url=" + url + ", " +
				"tipus=application/json, " +
				"body=" + body + ")");
		ClientResponse response = getJerseyClient().
				resource(url).
				accept("application/json").
				type("application/json").
				post(ClientResponse.class, body);
		String json = response.getEntity(String.class);
		return mapper.readValue(
				json,
				TypeFactory.defaultInstance().constructType(ProcedimientosResponse.class));
	}

	@Override
	public ServeiDto findAmbCodiSia(
			String codiSia) throws SistemaExternException {
		logger.debug("Consulta del servei pel codi SIA (" +
			"codiSia=" + codiSia + ")");
		ProcedimientosResponse response = null;
		try {
			long start = System.currentTimeMillis();
			StringBuilder sb = new StringBuilder(getServiceUrl());			
			String params = "?lang=ca&filtro={\"codigoSia\":\"" + codiSia + "\",\"estadoSia\":\"A\",\"buscarEnDescendientesUA\":\"1\"}";
			
			response = findServeisRolsac(
					sb.toString(),
					params);
			salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - start);
		} catch (Exception ex) {
			salutPluginComponent.incrementarOperacioError();
			logger.error("Error consultant el servei de ROLSAC (" +
					"codiSia=" + codiSia + "): " + ex.getMessage(),
					ex);
			throw new SistemaExternException(
					"Error consultant el servei de ROLSAC (" +
					"codiSia=" + codiSia + "): " + ex.getMessage(),
					ex);
		}
		
		if (response != null && response.getStatus().equals("200")) {
			if (response.getResultado() != null && !response.getResultado().isEmpty()) {
				for (Servei servei: response.getResultado()) {
					toProcedmientDto(servei);
				}
				
				return toProcedmientDto(response.getResultado().get(0));
			} else { 
				return null;
			}
			
		} else {
			return null;
//			throw new SistemaExternException(
//					"No s'han pogut consultar el servei de ROLSAC (" +
//					"codiSia=" + codiSia + "). Resposta rebuda amb el codi " + response.getStatus());
		}	
	}
	
	public ServeiDto toProcedmientDto (Servei servei) throws  SistemaExternException {
		ServeiDto dto = new ServeiDto();
		if (servei != null) {
			dto.setCodi(servei.getCodigo());
			dto.setCodiSia(servei.getCodigoSIA());
			dto.setNom(servei.getNombre());			
		}
		return dto;
	}

	
	public String getServiceUrl() {
		return getProperty(
				"es.caib.distribucio.plugin.servei.rolsac.service.url");
	}
	private String getServiceUsername() {
		return getProperty(
				"es.caib.distribucio.plugin.servei.rolsac.service.username");
	}
	private String getServicePassword() {
		return getProperty(
				"es.caib.distribucio.plugin.servei.rolsac.service.password");
	}
	private Integer getServiceTimeout() {
		String key = "es.caib.distribucio.plugin.servei.rolsac.service.timeout";
		if (PropertiesHelper.getProperties().getProperty(key) != null) {
			return PropertiesHelper.getProperties().getAsInt(key);
		} else {
			return null;
		}
	}

	public String getUsuariIntegracio() {
		return getProperty(
					"es.caib.distribucio.plugin.servei.rolsac.service.username","-");		
	}
	
	public static class ProcedimientosResponse {
		private String numeroElementos;
		private String status;
		private List<Servei> resultado;
		public String getNumeroElementos() {
			return numeroElementos;
		}
		public void setNumeroElementos(String numeroElementos) {
			this.numeroElementos = numeroElementos;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public List<Servei> getResultado() {
			return resultado;
		}
		public void setResultado(List<Servei> resultado) {
			this.resultado = resultado;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(ServeiPluginRolsac.class);

	// Mètodes de SALUT
	// /////////////////////////////////////////////////////////////////////////////////////////////
    private AbstractSalutPlugin salutPluginComponent = new AbstractSalutPlugin();
    public void init(MeterRegistry registry, String codiPlugin) {
        salutPluginComponent.init(registry, codiPlugin);
    }
    
    @Override
	public boolean teConfiguracioEspecifica() {
		return salutPluginComponent.teConfiguracioEspecifica();
	}

	@Override
	public EstatSalut getEstatPlugin() {
		return salutPluginComponent.getEstatPlugin();
	}

	@Override
	public IntegracioPeticions getPeticionsPlugin() {
		return salutPluginComponent.getPeticionsPlugin();
	}

}
