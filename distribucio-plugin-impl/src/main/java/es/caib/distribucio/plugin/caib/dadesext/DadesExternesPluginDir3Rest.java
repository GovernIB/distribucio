package es.caib.distribucio.plugin.caib.dadesext;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.dir3caib.ws.api.catalogo.CatPais;
import es.caib.distribucio.logic.intf.config.PropertyConfig;
import es.caib.distribucio.plugin.AbstractSalutPlugin;
import es.caib.distribucio.plugin.DistribucioAbstractPluginProperties;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.dadesext.*;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Implementació de proves del plugin d'unitats organitzatives.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class DadesExternesPluginDir3Rest extends DistribucioAbstractPluginProperties implements DadesExternesPlugin {

    public DadesExternesPluginDir3Rest(Properties properties, boolean configuracioEspecifica) {
        super(properties);
        salutPluginComponent.setConfiguracioEspecifica(configuracioEspecifica);
    }

    @Override
    public List<Pais> paisFindAll() throws SistemaExternException {
        List<Pais> paisos = new ArrayList<>();
        try {
            URL url = new URL(getServiceUrl() + "paises?estado=V");
            log.info("[DADES_EXTERNES] Consulta paisos url " + url);
            HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            List<CatPais> paisosRest = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance().constructCollectionType(List.class, CatPais.class));

            for (CatPais catPais : paisosRest) {
                Pais pais = new Pais(
                        catPais.getCodigoPais(),
                        catPais.getAlfa2Pais(),
                        catPais.getAlfa3Pais(),
                        catPais.getDescripcionPais());
                paisos.add(pais);
            }
            return paisos;
        } catch (Exception ex) {
            log.error("No s'han pogut consultar els paisos", ex);
            throw new SistemaExternException("No s'han pogut consultar els paisos", ex);
        }
    }

    @Override
    public List<ComunitatAutonoma> comunitatFindAll() throws SistemaExternException {
        List<ComunitatAutonoma> comunitats = new ArrayList<>();
        try {
            URL url = new URL(getServiceUrl() + "comunidadesAutonomas");
            log.info("[DADES_EXTERNES] Consulta comunitats autonomes url " + url);
            HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            List<CodiValor> catComunitats = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance().constructCollectionType(List.class, CodiValor.class));
            Collections.sort(catComunitats);

            if (catComunitats != null) {
                for (CodiValor catComunitat: catComunitats) {
                    ComunitatAutonoma comunitat = new ComunitatAutonoma(
                            Long.parseLong(catComunitat.getId()),
                            null,
                            catComunitat.getDescripcio());
                    comunitats.add(comunitat);
                }
            }
            return comunitats;
        } catch (Exception ex) {
            log.error("No s'han pogut consultar les comunitats", ex);
            throw new SistemaExternException("No s'han pogut consultar les comunitats", ex);
        }
    }

    @Override
    public List<Provincia> provinciaFindAll() throws SistemaExternException {
        List<Provincia> provincies = new ArrayList<>();
        try {
            URL url = new URL(getServiceUrl() + "provincias");
            log.info("[DADES_EXTERNES] Consulta provincies url " + url);
            HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            List<CodiValor> catProvincies = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance().constructCollectionType(List.class, CodiValor.class));
            Collections.sort(catProvincies);

            if (catProvincies != null) {
                for (CodiValor catProvincia: catProvincies) {
                    Provincia provincia = new Provincia(
                            Long.parseLong(catProvincia.getId()),
                            null,
                            catProvincia.getDescripcio());
                    provincies.add(provincia);
                }
            }
            return provincies;
        } catch (Exception ex) {
            log.error("No s'han pogut consultar les provincies", ex);
            throw new SistemaExternException("No s'han pogut consultar les provincies", ex);
        }
    }

    @Override
    public List<Provincia> provinciaFindByComunitat(String comunitatCodi) throws SistemaExternException {
        List<Provincia> provinciesComunitat = new ArrayList<>();
        if (comunitatCodi == null || comunitatCodi.isEmpty())
            return provinciesComunitat;

        try {
            URL url = new URL(getServiceUrl() + "provincias/comunidadAutonoma?id=" + comunitatCodi);
            log.info("[DADES_EXTERNES] Consulta provincies url " + url);
            HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            List<CodiValor> catProvincies = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance().constructCollectionType(List.class, CodiValor.class));
            Collections.sort(catProvincies);

            if (catProvincies != null) {
                for (CodiValor catProvincia: catProvincies) {
                    Provincia provincia = new Provincia(
                            Long.parseLong(catProvincia.getId()),
                            Long.parseLong(comunitatCodi),
                            catProvincia.getDescripcio());
                    provinciesComunitat.add(provincia);
                }
            }
            return provinciesComunitat;
        } catch (Exception ex) {
            log.error("No s'han pogut consultar les provincies", ex);
            throw new SistemaExternException("No s'han pogut consultar les provincies", ex);
        }
    }

    @Override
    public List<Municipi> municipiFindByProvincia(String provinciaCodi) throws SistemaExternException {
        List<Municipi> municipisProvincia = new ArrayList<>();
        if (provinciaCodi == null || provinciaCodi.isEmpty())
            return municipisProvincia;

        try {
            URL url = new URL(getServiceUrl()
                    + "localidades/provincia/entidadGeografica?"
                    + "codigoProvincia=" + provinciaCodi
                    + "&codigoEntidadGeografica=01");
            log.info("[DADES_EXTERNES] Consulta localitats de la provincia " + provinciaCodi + " url " + url);
            HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            List<CodiValor> localitats = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance().constructCollectionType(List.class, CodiValor.class));
            Collections.sort(localitats);

            for (CodiValor localitat: localitats) {
                Municipi municipi = new Municipi(
                        Long.parseLong(localitat.getId()),
                        "01",
                        Long.parseLong(provinciaCodi),
                        localitat.getDescripcio());
                municipisProvincia.add(municipi);
            }
            return municipisProvincia;
        } catch (Exception ex) {
            log.error("No s'han pogut consultar els municipis de la província " + provinciaCodi, ex);
            throw new SistemaExternException("No s'han pogut consultar els municipis de la província " + provinciaCodi, ex);
        }
    }

    @Override
    public List<EntitatGeografica> entitatGeograficaFindAll() throws SistemaExternException {
        return null;
    }

    @Override
    public List<NivellAdministracio> nivellAdministracioFindAll() throws SistemaExternException {
        List<NivellAdministracio> nivellsAdministracio = new ArrayList<>();
        try {
            URL url = new URL(getServiceUrl() + "nivelesAdministracion");
            log.info("[DADES_EXTERNES] Consulta nivells administracio url " + url);
            HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            List<CodiValor> catNivellsAdministracio = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance().constructCollectionType(List.class, CodiValor.class));
            Collections.sort(catNivellsAdministracio);

            if (catNivellsAdministracio != null) {
                for (CodiValor catNivellAdministracio: catNivellsAdministracio) {
                    NivellAdministracio nivellAdministracio = new NivellAdministracio(
                            Long.parseLong(catNivellAdministracio.getId()),
                            catNivellAdministracio.getDescripcio());
                    nivellsAdministracio.add(nivellAdministracio);
                }
            }
            return nivellsAdministracio;
        } catch (Exception ex) {
            log.error("No s'han pogut consultar els nivells d'administració", ex);
            throw new SistemaExternException("No s'han pogut consultar els nivells d'administració", ex);
        }
    }

    @Override
    public List<TipusVia> tipusViaFindAll() throws SistemaExternException {
        List<TipusVia> tipusVia = new ArrayList<>();
        try {
            URL url = new URL(getServiceUrl() + "tiposvia");
            log.info("[DADES_EXTERNES] Consulta tipus de via url " + url);
            HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setDoInput(true);
            httpConnection.setDoOutput(true);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            List<CodiValor> catTiposVia = mapper.readValue(httpConnection.getInputStream(), TypeFactory.defaultInstance().constructCollectionType(List.class, CodiValor.class));
            Collections.sort(catTiposVia);

            if (catTiposVia != null) {
                for (CodiValor catTipoVia: catTiposVia) {
                    TipusVia tipoVia = new TipusVia(
                            Long.parseLong(catTipoVia.getId()),
                            catTipoVia.getDescripcio());
                    tipusVia.add(tipoVia);
                }
            }
            return tipusVia;
        } catch (Exception ex) {
            log.error("No s'han pogut consultar els tipus de via", ex);
            throw new SistemaExternException("No s'han pogut consultar els tipus de via", ex);
        }
    }

//    @Override
//    public String getEndpointURL() {
//        String endpoint = getProperty(PropertyConfig.DADESEXT_PLUGIN_DIR3_ENDPOINT);
//        if (endpoint != null && !endpoint.isEmpty()) {
//            endpoint = getServiceUrl();
//        }
//        return endpoint;
//    }

    private String getServiceUrl() {
        return getProperty(PropertyConfig.DADESEXT_PLUGIN_DIR3_REST_URL);
    }

    @Override
    public String getUsuariIntegracio() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
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
        IntegracioPeticions peticions = salutPluginComponent.getPeticionsPlugin();
        peticions.setEndpoint(getServiceUrl());
        return peticions;
    }

}
