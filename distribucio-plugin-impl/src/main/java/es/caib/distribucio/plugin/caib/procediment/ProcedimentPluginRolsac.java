/**
 * 
 */
package es.caib.distribucio.plugin.caib.procediment;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
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
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.distribucio.logic.intf.dto.ProcedimentDto;
import es.caib.distribucio.plugin.DistribucioAbstractPluginProperties;
import es.caib.distribucio.plugin.RespostaUnitatAdministrativa;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.UnitatAdministrativaRolsac;
import es.caib.distribucio.plugin.procediment.Procediment;
import es.caib.distribucio.plugin.procediment.ProcedimentPlugin;
import es.caib.distribucio.plugin.procediment.UnitatAdministrativa;
import es.caib.distribucio.plugin.utils.PropertiesHelper;
import lombok.Synchronized;

/**
 * Implementació del plugin de consulta de procediments emprant ROLSAC.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ProcedimentPluginRolsac extends DistribucioAbstractPluginProperties implements ProcedimentPlugin {

	private Client jerseyClient;
	private ObjectMapper mapper;

	public ProcedimentPluginRolsac() {
		super();
	}
	
	public ProcedimentPluginRolsac(Properties properties) {
		super(properties);
	}
	
	@Override
	public List<Procediment> findAmbCodiDir3(
			String codiDir3) throws SistemaExternException {
		logger.debug("Consulta dels procediments de l'unitat organitzativa (" +
				"codiDir3=" + codiDir3 + ")");
		ProcedimientosResponse response = null;
		try {
			StringBuilder sb = new StringBuilder(getServiceUrl());
			response = findProcedimentsRolsac(
					sb.toString(),
					"lang=ca&filtro={\"codigoUADir3\":\"" + codiDir3 + "\",\"estadoSia\":\"A\",\"buscarEnDescendientesUA\":\"1\"}&filtroPaginacion={\"page\":\"1\", \"size\":\"9999\"}");
		} catch (Exception ex) {
			incrementarOperacioError();
			logger.error("No s'han pogut consultar els procediments de ROLSAC (" +
					"codiDir3=" + codiDir3 + ")",
					ex);
			throw new SistemaExternException(
					"No s'han pogut consultar els procediments de ROLSAC (" +
					"codiDir3=" + codiDir3 + ")",
					ex);
		}
		
		if (response != null && response.getStatus().equals("200")) {
			incrementarOperacioOk();
			return response.getResultado();
		} else {
			incrementarOperacioError();
			logger.error("No s'han pogut consultar els procediments de ROLSAC (" +
					"codiDir3=" + codiDir3 + "). Resposta rebuda amb el codi " + response.getStatus());
			throw new SistemaExternException(
					"No s'han pogut consultar els procediments de ROLSAC (" +
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

	private ProcedimientosResponse findProcedimentsRolsac(
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
	public ProcedimentDto findAmbCodiSia(
			String codiSia) throws SistemaExternException {
		logger.debug("Consulta del procediment pel codi SIA (" +
			"codiSia=" + codiSia + ")");
		ProcedimientosResponse response = null;
		try {
			StringBuilder sb = new StringBuilder(getServiceUrl());			
			String params = "?lang=ca&filtro={\"codigoSia\":\"" + codiSia + "\",\"estadoSia\":\"A\",\"buscarEnDescendientesUA\":\"1\"}";
			
			response = findProcedimentsRolsac(
					sb.toString(),
					params);
			incrementarOperacioOk();
		} catch (Exception ex) {
			incrementarOperacioError();
			logger.error("Error consultant el procediment de ROLSAC (" +
					"codiSia=" + codiSia + "): " + ex.getMessage(),
					ex);
			throw new SistemaExternException(
					"Error consultant el procediment de ROLSAC (" +
					"codiSia=" + codiSia + "): " + ex.getMessage(),
					ex);
		}
		
		if (response != null && response.getStatus().equals("200")) {
			if (response.getResultado() != null && !response.getResultado().isEmpty()) {
				for (Procediment procediment: response.getResultado()) {
					toProcedmientDto(procediment);
				}
				
				return toProcedmientDto(response.getResultado().get(0));
			} else { 
				return null;
			}
			
		} else {
			return null;
//			throw new SistemaExternException(
//					"No s'han pogut consultar el procediment de ROLSAC (" +
//					"codiSia=" + codiSia + "). Resposta rebuda amb el codi " + response.getStatus());
		}	
	}
	
	public ProcedimentDto toProcedmientDto (Procediment procediment) throws  SistemaExternException {
		ProcedimentDto dto = new ProcedimentDto();
		if (procediment != null) {
			dto.setCodi(procediment.getCodigo());
			dto.setCodiSia(procediment.getCodigoSIA());
			dto.setNom(procediment.getNombre());			
		}
		return dto;
	}

	@Override
	public UnitatAdministrativa findUnitatAdministrativaAmbCodi(String codi) throws SistemaExternException {

		logger.debug("Consulta de la unitat administrativa amb codi (" +
				"codi=" + codi + ")");

		UnitatAdministrativa unitatAdministrativa = null;
		try {
			String urlAmbMetode = getRolsacServiceUrl() + "/unidades_administrativas/" + codi;
			
			Client jerseyClient = getJerseyClient();
			
			String json = jerseyClient.
					resource(urlAmbMetode).
					post(String.class);
			
			RespostaUnitatAdministrativa resposta = mapper.readValue(json, RespostaUnitatAdministrativa.class);
			if (resposta.getResultado() != null && !resposta.getResultado().isEmpty()) {
				UnitatAdministrativaRolsac unitatAdministrativaRolsac = resposta.getResultado().get(0);
				unitatAdministrativa = new UnitatAdministrativa();
				unitatAdministrativa.setCodi(String.valueOf(unitatAdministrativaRolsac.getCodigo()));
				unitatAdministrativa.setCodiDir3(unitatAdministrativaRolsac.getCodigoDIR3());
				unitatAdministrativa.setNom(unitatAdministrativaRolsac.getNombre());
				if (unitatAdministrativaRolsac.getPadre() != null) {
					unitatAdministrativa.setPareCodi(unitatAdministrativaRolsac.getPadre().getCodigo());					
				}
			}
			incrementarOperacioOk();
		} catch (Exception ex) {
			incrementarOperacioError();
			throw new SistemaExternException(
					"No s'ha pogut consultar la unitat administrativa amb codi " + codi + " via REST: " + ex.toString(),
					ex);
		}
		return unitatAdministrativa;
	}
	
	public String getRolsacServiceUrl() {
		String rolsacUrl = getProperty(
				"es.caib.distribucio.plugin.rolsac.service.url");
		if (rolsacUrl == null || rolsacUrl.isBlank()) {
			// si no troba la propietat llavors obté la url base a partir de la url
			rolsacUrl = getServiceUrl();
			if (rolsacUrl.endsWith("/procedimientos")) {
				rolsacUrl = rolsacUrl.replace("/procedimientos", "");
			} else if (rolsacUrl.endsWith("/procedimientos")) {
				rolsacUrl = rolsacUrl.replace("/procedimientos", "");
			}
		}
		return rolsacUrl;
	}
	
	public String getServiceUrl() {
		return getProperty(
				"es.caib.distribucio.plugin.procediment.rolsac.service.url");
	}
	private String getServiceUsername() {
		return getProperty(
				"es.caib.distribucio.plugin.procediment.rolsac.service.username");
	}
	private String getServicePassword() {
		return getProperty(
				"es.caib.distribucio.plugin.procediment.rolsac.service.password");
	}
	private Integer getServiceTimeout() {
		String key = "es.caib.distribucio.plugin.procediment.rolsac.service.timeout";
		if (PropertiesHelper.getProperties().getProperty(key) != null) {
			return PropertiesHelper.getProperties().getAsInt(key);
		} else {
			return null;
		}
	}

	public String getUsuariIntegracio() {
		return getProperty(
					"es.caib.distribucio.plugin.procediment.rolsac.service.username","-");		
	}
	
	public static class ProcedimientosResponse {
		private String numeroElementos;
		private String status;
		private List<Procediment> resultado;
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
		public List<Procediment> getResultado() {
			return resultado;
		}
		public void setResultado(List<Procediment> resultado) {
			this.resultado = resultado;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(ProcedimentPluginRolsac.class);

	// Mètodes de SALUT
	// /////////////////////////////////////////////////////////////////////////////////////////////

	private boolean configuracioEspecifica = false;
	private int operacionsOk = 0;
	private int operacionsError = 0;

	@Synchronized
	private void incrementarOperacioOk() {
		operacionsOk++;
	}

	@Synchronized
	private void incrementarOperacioError() {
		operacionsError++;
	}

	@Synchronized
	private void resetComptadors() {
		operacionsOk = 0;
		operacionsError = 0;
	}

	@Override
	public boolean teConfiguracioEspecifica() {
		return this.configuracioEspecifica;
	}

	@Override
	public EstatSalut getEstatPlugin() {
		try {
			Instant start = Instant.now();
			findAmbCodiDir3("000000000");
			return EstatSalut.builder()
					.latencia((int) Duration.between(start, Instant.now()).toMillis())
					.estat(EstatSalutEnum.UP)
					.build();
		} catch (Exception ex) {
			return EstatSalut.builder().estat(EstatSalutEnum.DOWN).build();
		}
	}

	@Override
	public IntegracioPeticions getPeticionsPlugin() {
		IntegracioPeticions integracioPeticions = IntegracioPeticions.builder()
				.totalOk(operacionsOk)
				.totalError(operacionsError)
				.build();
		resetComptadors();
		return integracioPeticions;
	}
}
