/**
 * 
 */
package es.caib.distribucio.plugin.caib.unitat;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.distribucio.plugin.DistribucioAbstractPluginProperties;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.unitat.UnitatOrganitzativa;
import es.caib.distribucio.plugin.unitat.UnitatsOrganitzativesPlugin;
import es.caib.distribucio.plugin.utils.PropertiesHelper;
import lombok.Synchronized;


/**
 * Implementació de proves del plugin d'unitats organitzatives.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UnitatsOrganitzativesPluginDir3 extends DistribucioAbstractPluginProperties implements UnitatsOrganitzativesPlugin {
	  	
	public UnitatsOrganitzativesPluginDir3() {
		super();
	}
	
	public UnitatsOrganitzativesPluginDir3(Properties properties) {
		super(properties);
	}
	
	@Override
	public UnitatOrganitzativa findUnidad(
			String pareCodi, 
			Timestamp fechaActualizacion, 
			Timestamp fechaSincronizacion) throws MalformedURLException {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
		UnidadRest unidad = getUnitatsOrganitzativesRestClient().obtenerUnidad(
				pareCodi,
				fechaActualizacion != null ? dateFormat.format(fechaActualizacion) : null,
				fechaSincronizacion != null ? dateFormat.format(fechaSincronizacion) : null, false);
		if (unidad != null) {
			return toUnitatOrganitzativa(unidad);
		} else {
			return null;
		}

	}
	
	@Override
	public List<UnitatOrganitzativa> findAmbPare(
			String pareCodi,
			Timestamp fechaActualizacion,
			Timestamp fechaSincronizacion) throws SistemaExternException {
		try {
			List<UnitatOrganitzativa> unitatOrganitzativa = new ArrayList<UnitatOrganitzativa>();
			
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
			List<UnidadRest> unidades = getUnitatsOrganitzativesRestClient().obtenerArbolUnidades(
					pareCodi,
					fechaActualizacion != null ? dateFormat.format(fechaActualizacion) : null,
					fechaSincronizacion != null ? dateFormat.format(fechaSincronizacion) : null, false);

            if (unidades != null) {
                for (UnidadRest unidad : unidades) {
                	unitatOrganitzativa.add(toUnitatOrganitzativa(unidad));
                }
            }
			return unitatOrganitzativa;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar les unitats organitzatives via WS (" +
					"pareCodi=" + pareCodi + ")",
					ex);
		}
	}
	


	public List<UnitatOrganitzativa> cercaUnitats(
			String codi, 
			String denominacio,
			Long nivellAdministracio, 
			Long comunitatAutonoma, 
			Boolean ambOficines, 
			Boolean esUnitatArrel,
			Long provincia, 
			String municipi) throws SistemaExternException {
		try {
			URL url = new URL(getServiceCercaUrl()
					+ "?codigo=" + codi
					+ "&denominacion=" + denominacio
					+ "&codNivelAdministracion=" + (nivellAdministracio != null ? nivellAdministracio : "-1")
					+ "&codComunidadAutonoma=" + (comunitatAutonoma != null ? comunitatAutonoma : "-1")
					+ "&conOficinas=" + (ambOficines != null && ambOficines ? "true" : "false")
					+ "&unidadRaiz=" + (esUnitatArrel != null && esUnitatArrel ? "true" : "false")
					+ "&provincia="+ (provincia != null ? provincia : "-1")
					+ "&localidad=" + (municipi != null ? municipi : "-1")
					+ "&vigentes=true");
			HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setDoInput(true);
			httpConnection.setDoOutput(true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			List<UnitatOrganitzativa> unitats = mapper.readValue(
					httpConnection.getInputStream(), 
					TypeFactory.defaultInstance().constructCollectionType(
							List.class,  
							UnitatOrganitzativa.class));
			Collections.sort(unitats);
			return unitats;
		} catch (Exception ex) {
			throw new SistemaExternException(
					"No s'han pogut consultar les unitats organitzatives via REST (" +
					"codi=" + codi + ", " +
					"denominacio=" + denominacio + ", " +
					"nivellAdministracio=" + nivellAdministracio + ", " +
					"comunitatAutonoma=" + comunitatAutonoma + ", " +
					"ambOficines=" + ambOficines + ", " +
					"esUnitatArrel=" + esUnitatArrel + ", " +
					"provincia=" + provincia + ", " +
					"municipi=" + municipi + ")",
					ex);
		}
	}




	private UnitatOrganitzativa toUnitatOrganitzativa(UnidadRest unidad) {
		UnitatOrganitzativa unitat = new UnitatOrganitzativa(
				unidad.getCodigo(),
				unidad.getDenominacionCooficial() != null ? unidad.getDenominacionCooficial() : unidad.getDenominacion(),
				unidad.getCodigo(), // CifNif
				unidad.getFechaAltaOficial(),
				unidad.getCodigoEstadoEntidad(),
				unidad.getCodUnidadSuperior(),
				unidad.getCodUnidadRaiz(),
				unidad.getCodigoAmbPais(),
				unidad.getCodAmbComunidad(),
				unidad.getCodAmbProvincia(),
				unidad.getCodPostal(),
				unidad.getDescripcionLocalidad(),
				unidad.getCodigoTipoVia(), 
				unidad.getNombreVia(), 
				unidad.getNumVia(),
				unidad.getHistoricosUO());
		return unitat;
	}
	
	private UnitatsOrganitzativesRestClient getUnitatsOrganitzativesRestClient() {
		UnitatsOrganitzativesRestClient unitatsOrganitzativesRestClient = new UnitatsOrganitzativesRestClient(
				getServiceUrl(),
				getServiceUsername(),
				getServicePassword());

		return unitatsOrganitzativesRestClient;
	}

	public String getServiceUrl() {
		return getProperty(
				"es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.url");
	}
	private String getServiceUsername() {
		return getProperty(
				"es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.username");
	}
	private String getServicePassword() {
		return getProperty(
				"es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.password");
	}
	@SuppressWarnings("unused")
	private boolean isLogMissatgesActiu() {
		return PropertiesHelper.getProperties().getAsBoolean(
				"es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.log.actiu");
	}
	@SuppressWarnings("unused")
	private Integer getServiceTimeout() {
		String value = getProperty("es.caib.distribucio.plugin.unitats.organitzatives.dir3.service.timeout");		
		return value != null ? Integer.valueOf(value) : null;
	}
	private String getServiceCercaUrl() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.plugin.unitats.cerca.dir3.service.url");
	}

	@Override
	public String getUsuariIntegracio() {
		return getServiceUsername();
	}
	
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
			String url = getServiceCercaUrl() + "?codigo=fakeUnitat&denominacion=&codNivelAdministracion=-1&codComunidadAutonoma=-1&conOficinas=false&unidadRaiz=false&provincia=-1&localidad=-1&vigentes=true";
			WebResource webResource = Client.create().resource(url);
			ClientResponse response = webResource.get(ClientResponse.class);
//			cercaUnitats("fakeUnitat", null, null, null, null, null, null, null);
			if (response.getStatus() == 204 || response.getStatus() == 200) {
				return EstatSalut.builder()
						.latencia((int) Duration.between(start, Instant.now()).toMillis())
						.estat(EstatSalutEnum.UP)
						.build();
			}
		} catch (Exception ex) {}
		return EstatSalut.builder().estat(EstatSalutEnum.DOWN).build();
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
