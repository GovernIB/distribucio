/**
 * 
 */
package es.caib.distribucio.plugin.caib.validacio;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.EstatSalutEnum;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaDetallDto;
import es.caib.distribucio.logic.intf.helper.ArxiuConversions;
import es.caib.distribucio.plugin.DistribucioAbstractPluginProperties;
import es.caib.distribucio.plugin.validacio.ValidaSignaturaResposta;
import es.caib.distribucio.plugin.validacio.ValidacioSignaturaPlugin;
import es.caib.portafib.apiinterna.client.signature.v1.api.SignatureValidationV1Api;
import es.caib.portafib.apiinterna.client.signature.v1.model.CertificateInformation;
import es.caib.portafib.apiinterna.client.signature.v1.model.Document;
import es.caib.portafib.apiinterna.client.signature.v1.model.SignatureDetailInfo;
import es.caib.portafib.apiinterna.client.signature.v1.model.SignatureRequestedInformation;
import es.caib.portafib.apiinterna.client.signature.v1.model.TimeStampInfo;
import es.caib.portafib.apiinterna.client.signature.v1.model.ValidateSignatureRequest;
import es.caib.portafib.apiinterna.client.signature.v1.model.ValidateSignatureResponse;
import es.caib.portafib.apiinterna.client.signature.v1.services.ApiClient;
import lombok.Synchronized;

/**
 * Implementació del plugin de validació de firmes emprant el client del portafirmes
 * per comunicar-se amb Afirma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidacioFirmaPluginApiPortafib extends DistribucioAbstractPluginProperties implements ValidacioSignaturaPlugin {	  
		
	public ValidacioFirmaPluginApiPortafib() {
		super();
	}
	
	public ValidacioFirmaPluginApiPortafib(Properties properties) {
		super(properties);
	}

	@Override
	public ValidaSignaturaResposta validaSignatura(
			String documentNom,
			String documentMime,
			byte[] documentContingut,
			byte[] firmaContingut
			) throws es.caib.distribucio.plugin.SistemaExternException {
		ValidateSignatureRequest validateRequest = new ValidateSignatureRequest();
		
		ValidaSignaturaResposta resposta = new ValidaSignaturaResposta();
		
		Document document = new Document();
		document.setName(documentNom);
		document.setMime(documentMime);
		document.setData(documentContingut);

		Document firma = null;
		if (firmaContingut != null) {
			firma = new Document();
			firma.setData(firmaContingut);
		}
        validateRequest.setSignatureDocument(document);
		validateRequest.setDetachedDocument(firma);
		
		SignatureRequestedInformation sri = new SignatureRequestedInformation();
		sri.setReturnSignatureTypeFormatProfile(true);
		sri.setReturnCertificateInfo(true);
		sri.setReturnValidationChecks(false);
		sri.setValidateCertificateRevocation(false);
		sri.setReturnCertificates(false);
		sri.setReturnTimeStampInfo(true);
		validateRequest.setSignatureRequestedInformation(sri);
		
		ApiClient apiClient;
		try {
			apiClient = getApiClient();
			SignatureValidationV1Api api = getApi(apiClient);			
	        ValidateSignatureResponse response = api.validateSignature(getLanguageUI(), validateRequest);
	        
	        // Completa la resposta
			resposta.setStatus(response.getValidationStatus().getStatus());
			resposta.setErrMsg(response.getValidationStatus().getErrorMsg());
			resposta.setErrException(new RuntimeException(response.getValidationStatus().getErrorException()));
			
			if (response.getSignatureDetailInfo() != null) {
				for (SignatureDetailInfo signatureInfo: response.getSignatureDetailInfo()) {
					ArxiuFirmaDetallDto detall = new ArxiuFirmaDetallDto();
					TimeStampInfo timeStampInfo = signatureInfo.getTimeStampInfo();
					if (timeStampInfo != null) {					
						detall.setData(Date.from(timeStampInfo.getCreationTime().toInstant()));
					} else if (signatureInfo.getSignDate() != null){					
						detall.setData(Date.from(signatureInfo.getSignDate().toInstant()));
					}
					CertificateInformation certificateInformation = signatureInfo.getCertificateInfo();
					if (certificateInformation != null) {
						detall.setResponsableNif(certificateInformation.getEuropeanOrganizationAdministrationID());
						detall.setResponsableNom(certificateInformation.getFullName());
						detall.setEmissorCertificat(certificateInformation.getIssuerOrganization());
					}
					resposta.getFirmaDetalls().add(detall);
				}
				resposta.setPerfil(ArxiuConversions.toPerfilFirmaArxiu(response.getSignProfile()));
				resposta.setTipus(ArxiuConversions.toFirmaTipusPortafib(
						response.getSignType(),
						response.getSignMode()));
			}
			incrementarOperacioOk();
		} catch (Exception e) {
			incrementarOperacioError();
			e.printStackTrace();
		}		
		return resposta;
	}
	
    public SignatureValidationV1Api getApi(ApiClient client) throws Exception {
        SignatureValidationV1Api api = new SignatureValidationV1Api(client);
        return api;
    }
    
	protected ApiClient getApiClient() throws Exception {

        // Properties prop = getConfigProperties();
        String languageUI = null; // = getLanguageUI(prop);

        String basePath = this.getPropertyEndpoint(); // = getRequiredProperty(prop, "basePath");
        
        String username = this.getPropertyUsername(); // = getRequiredProperty(prop, "username");
        
        String password = this.getPropertyPassword(); // = getRequiredProperty(prop, "password");

        ApiClient apiClient = getApiClient(basePath, username, password, languageUI);

        return apiClient;
    }
	
    protected String getLanguageUI() throws Exception {
        Properties prop; // = getConfigProperties();
        return null; // getRequiredProperty(prop, "languageUI");
    }
    
    protected String getLanguageUI(Properties prop) throws Exception {
        return null; // getRequiredProperty(prop, "languageUI");
    }
	
    protected ApiClient getApiClient(String basePath, String username, String password, String languageUI) {
        ApiClient client = new ApiClient();
        client.setBasePath(basePath);
        client.setUsername(username);
        client.setPassword(password);

        client.setDebugging(true);

        client.addDefaultHeader("Accept-Language", languageUI);
        return client;
    }

	@Override
	public String getUsuariIntegracio() {
		return this.getPropertyUsername();
	}
	
	private String getPropertyEndpoint() {
		return getProperties().getProperty(
				"es.caib.distribucio.pluginsib.validatesignature.api.portafib.endpoint");
	}
	
	private String getPropertyUsername() {
		return getProperties().getProperty(
				"es.caib.distribucio.pluginsib.validatesignature.api.portafib.username");
	}
	
	private String getPropertyPassword() {
		return getProperties().getProperty(
				"es.caib.distribucio.pluginsib.validatesignature.api.portafib.password");
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
            
			String documentNom = "validacio_firma.pdf";
			String documentMime = "application/pdf";
			byte[] documentContingut = IOUtils.toByteArray(this.getClass().getResourceAsStream("/es/caib/distribucio/plugin/validacio/validacio_firma.pdf"));
			
			validaSignatura(documentNom, documentMime, documentContingut, null);
			
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
