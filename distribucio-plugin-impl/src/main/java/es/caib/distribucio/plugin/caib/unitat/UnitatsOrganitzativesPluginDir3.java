/**
 * 
 */
package es.caib.distribucio.plugin.caib.unitat;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.distribucio.plugin.AbstractSalutPlugin;
import es.caib.distribucio.plugin.DistribucioAbstractPluginProperties;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.unitat.UnitatOrganitzativa;
import es.caib.distribucio.plugin.unitat.UnitatsOrganitzativesPlugin;
import es.caib.distribucio.plugin.utils.PropertiesHelper;
import io.micrometer.core.instrument.MeterRegistry;


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
			Timestamp fechaSincronizacion) throws SistemaExternException {
		try {
			long start = System.currentTimeMillis();
	        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
			UnidadRest unidad = getUnitatsOrganitzativesRestClient().obtenerUnidad(
					pareCodi,
					fechaActualizacion != null ? dateFormat.format(fechaActualizacion) : null,
					fechaSincronizacion != null ? dateFormat.format(fechaSincronizacion) : null, false);
			if (unidad != null) {
				salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - start);
				return toUnitatOrganitzativa(unidad);
			} else {
				salutPluginComponent.incrementarOperacioError();
				return null;
			}
		} catch (Exception ex) {
			salutPluginComponent.incrementarOperacioError();
			throw new SistemaExternException(
					"No s'han pogut consultar la unitat organitzativa via WS (" +
					"pareCodi=" + pareCodi + ")",
					ex);
		}

	}
	
	@Override
	public List<UnitatOrganitzativa> findAmbPare(
			String pareCodi,
			Timestamp fechaActualizacion,
			Timestamp fechaSincronizacion) throws SistemaExternException {
		try {
			long start = System.currentTimeMillis();
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
            salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - start);
			return unitatOrganitzativa;
		} catch (Exception ex) {
			salutPluginComponent.incrementarOperacioError();
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
			long start = System.currentTimeMillis();
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
			salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - start);
			return unitats;
		} catch (Exception ex) {
			salutPluginComponent.incrementarOperacioError();
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
