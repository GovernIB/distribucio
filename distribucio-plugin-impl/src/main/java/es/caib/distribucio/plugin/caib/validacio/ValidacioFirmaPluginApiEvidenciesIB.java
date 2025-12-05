package es.caib.distribucio.plugin.caib.validacio;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaDetallDto;
import es.caib.distribucio.plugin.AbstractSalutPlugin;
import es.caib.distribucio.plugin.DistribucioAbstractPluginProperties;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.distribucio.plugin.utils.EvidenciaIdExtractor;
import es.caib.distribucio.plugin.validacio.ValidaSignaturaResposta;
import es.caib.distribucio.plugin.validacio.ValidacioSignaturaPlugin;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.Setter;

/**
 * Implementació del plugin de validació de firmes emprat per validar firmes no
 * criptogràfiques (firma àgil)
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidacioFirmaPluginApiEvidenciesIB extends DistribucioAbstractPluginProperties
		implements ValidacioSignaturaPlugin {

	private static final ObjectMapper mapper = new ObjectMapper();
	
	public ValidacioFirmaPluginApiEvidenciesIB() {
		super();
	}

	public ValidacioFirmaPluginApiEvidenciesIB(Properties properties, boolean configuracioEspecifica) {
		super(properties);
		salutPluginComponent.setConfiguracioEspecifica(configuracioEspecifica);
	}

	@Override
	public ValidaSignaturaResposta validaSignatura(
	        String documentNom,
	        String documentMime,
	        byte[] documentContingut,
	        byte[] firmaContingut) throws SistemaExternException {
		long start = System.currentTimeMillis();
		try {
			// 1) Extrerue EvidenciaID del PDF
	        Long evidenciaId = null;
	        if (documentContingut != null)
	        	evidenciaId = EvidenciaIdExtractor.extractEvidenciaId(documentContingut);
	        else if (firmaContingut != null)
	        	evidenciaId = EvidenciaIdExtractor.extractEvidenciaId(firmaContingut);
	        
	        if (evidenciaId == null) {
	            return null;
	        }

	        // 2) Obtenir informació de la evidencia desde la API EXTERNA d'EvidenciesIB
	        EvidenciaWs evidencia = getApi().getEvidencia(evidenciaId);

	        if (evidencia == null || evidencia.getFitxerSignat() == null) {
	            ValidaSignaturaResposta resposta = new ValidaSignaturaResposta();
	            resposta.setStatus(ValidaSignaturaResposta.FIRMA_INVALIDA);
	            resposta.setErrMsg("No s'ha pogut obtenir informació de la evidència " + evidenciaId);
	            return resposta;
	        }

	        // 3) Descarregar el document firmat amb l'API EXTERNA d'EvidenciesIB
	        String encryptedFileID = evidencia.getFitxerSignat().getEncryptedFileID();
	        EvidenciaFile file = getApi().getFile(evidenciaId, encryptedFileID);

	        if (file == null || file.getDocument() == null) {
	            ValidaSignaturaResposta resposta = new ValidaSignaturaResposta();
	            resposta.setStatus(ValidaSignaturaResposta.FIRMA_INVALIDA);
	            resposta.setErrMsg("No s'ha trobat el fitxer signat per la evidència " + evidenciaId);
	            return resposta;
	        }

	        byte[] originalFirmat = file.getDocument();

	        // 4) Comparar si l'annex és idèntic a l'original
	        if ((documentContingut != null && !MessageDigest.isEqual(originalFirmat, documentContingut)) || 
	        		(firmaContingut != null && !MessageDigest.isEqual(originalFirmat, firmaContingut))) {
	            ValidaSignaturaResposta resposta = new ValidaSignaturaResposta();
	            resposta.setStatus(ValidaSignaturaResposta.FIRMA_INVALIDA);
	            resposta.setErrMsg("La firma del document no és vàlida (els fitxers no coincideixen)");
	            return resposta;
	        }

	        ValidaSignaturaResposta resposta = new ValidaSignaturaResposta();
	        resposta.setStatus(ValidaSignaturaResposta.FIRMA_VALIDA);
	        ArxiuFirmaDetallDto detall = new ArxiuFirmaDetallDto();
	        detall.setResponsableNom(
	        		Optional.ofNullable(evidencia.getPersonaNom()).orElse("") + " " +
	                Optional.ofNullable(evidencia.getPersonaLlinatge1()).orElse("") + " " +
	                Optional.ofNullable(evidencia.getPersonaLlinatge2()).orElse("")
	        );
	        detall.setResponsableNif(evidencia.getPersonaNif());
	        detall.setData(evidencia.getDataFi() != null ? Date.from(evidencia.getDataFi().toInstant()) : null);
	        resposta.getFirmaDetalls().add(detall);

	        salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - start);
	        return resposta;
	    } catch (Exception e) {
	    	salutPluginComponent.incrementarOperacioError();
			e.printStackTrace();
			throw new es.caib.distribucio.plugin.SistemaExternException(e);
	    }
	}
	
    private ApiRestClient getApi() {
        return new ApiRestClient(
                getPropertyEndpoint(),
                getPropertyUsername(),
                getPropertyPassword()
        );
    }

    private static class ApiRestClient {

        private final HttpClient httpClient;
        private final String baseUrl;
        private final String authHeader;

        public ApiRestClient(String baseUrl, String username, String password) {
            this.baseUrl = baseUrl;
            this.httpClient = HttpClient.newHttpClient();
            String basicAuth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
            this.authHeader = "Basic " + basicAuth;
            
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        }

        public EvidenciaWs getEvidencia(Long evidenciaID) throws Exception {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/secure/evidencies/get/" + evidenciaID))
                    .header("Authorization", authHeader)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Error obteniendo evidencia: HTTP " + response.statusCode());
            }

            return mapper.readValue(
            		response.body(), 
            		EvidenciaWs.class);
        }

        public EvidenciaFile getFile(Long evidenciaID, String encryptedFileID) throws Exception {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/secure/evidencies/getfile/" + evidenciaID + "/" + encryptedFileID))
                    .header("Authorization", authHeader)
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Error obteniendo fichero: HTTP " + response.statusCode());
            }

            return mapper.readValue(
            		response.body(), 
            		EvidenciaFile.class);
        }
    }
    
    @Getter @Setter
    private static class EvidenciaFile {
        private String name;
        private String mime;
        private long size;
        private String encryptedFileID;
        private byte[] document;
    }
    
    @Getter @Setter
    private static class EvidenciaWs {
        private Long evidenciaID;
        private String nom;
        private String personaNom;
        private String personaLlinatge1;
        private String personaLlinatge2;
        private String personaNif;
        private Date dataFi;
        private EvidenciaFile fitxerOriginal;
        private EvidenciaFile fitxerSignat;
    }
    
	@Override
	public String getUsuariIntegracio() {
		return this.getPropertyUsername();
	}

	private String getPropertyEndpoint() {
		return getProperties().getProperty("es.caib.distribucio.plugins.validatesignature.api.evidenciesib.endpoint");
	}

	private String getPropertyUsername() {
		return getProperties().getProperty("es.caib.distribucio.plugins.validatesignature.api.evidenciesib.username");
	}

	private String getPropertyPassword() {
		return getProperties().getProperty("es.caib.distribucio.plugins.validatesignature.api.evidenciesib.password");
	}

	// Mètodes de SALUT
	// /////////////////////////////////////////////////////////////////////////////////////////////
	private AbstractSalutPlugin salutPluginComponent = new AbstractSalutPlugin();

	public void init(MeterRegistry registry, String codiPlugin) {
		String uniqueId = codiPlugin + "-" + System.identityHashCode(this);
		salutPluginComponent.init(registry, uniqueId);
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
		peticions.setEndpoint(getPropertyEndpoint());
		return peticions;
	}

}
