/**
 * 
 */
package es.caib.distribucio.plugin.caib.validacio;

import java.util.Properties;

import org.fundaciobit.pluginsib.validatecertificate.InformacioCertificat;
import org.fundaciobit.pluginsib.validatesignature.api.SignatureDetailInfo;
import org.fundaciobit.pluginsib.validatesignature.api.SignatureRequestedInformation;
import org.fundaciobit.pluginsib.validatesignature.api.TimeStampInfo;
import org.fundaciobit.pluginsib.validatesignature.api.ValidateSignatureRequest;
import org.fundaciobit.pluginsib.validatesignature.api.ValidateSignatureResponse;

import es.caib.comanda.ms.salut.model.EstatSalut;
import es.caib.comanda.ms.salut.model.IntegracioPeticions;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaDetallDto;
import es.caib.distribucio.logic.intf.helper.ArxiuConversions;
import es.caib.distribucio.plugin.AbstractSalutPlugin;
import es.caib.distribucio.plugin.DistribucioAbstractPluginProperties;
import es.caib.distribucio.plugin.validacio.ValidaSignaturaResposta;
import es.caib.distribucio.plugin.validacio.ValidacioSignaturaPlugin;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Implementació del plugin de validació de firmes emprant el client del portafirmes
 * per comunicar-se amb Afirma.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ValidacioFirmaPluginAfirma extends DistribucioAbstractPluginProperties implements ValidacioSignaturaPlugin {	  
		
	public ValidacioFirmaPluginAfirma() {
		super();
	}
	
	public ValidacioFirmaPluginAfirma(Properties properties, boolean configuracioEspecifica) {
		super(properties);
		salutPluginComponent.setConfiguracioEspecifica(configuracioEspecifica);
	}

	@Override
	public String getUsuariIntegracio() {
		return this.getProperty("es.caib.distribucio.pluginsib.validatesignature.afirmacxf.authorization.username");
	}

	@Override
	public ValidaSignaturaResposta validaSignatura(
			String documentNom,
			String documentMime,
			byte[] documentContingut,
			byte[] firmaContingut
			) throws es.caib.distribucio.plugin.SistemaExternException {
		long start = System.currentTimeMillis();
		ValidateSignatureRequest validationRequest = new ValidateSignatureRequest();
		ValidaSignaturaResposta resposta = new ValidaSignaturaResposta();
		
		if (documentContingut != null && firmaContingut == null) {
			firmaContingut = documentContingut;
			documentContingut = null;
		}
		if (firmaContingut != null) {
			validationRequest.setSignedDocumentData(documentContingut);
			validationRequest.setSignatureData(firmaContingut);
		} else {
			validationRequest.setSignatureData(documentContingut);
		}
		SignatureRequestedInformation sri = new SignatureRequestedInformation();
		sri.setReturnSignatureTypeFormatProfile(true);
		sri.setReturnCertificateInfo(true);
		sri.setReturnValidationChecks(false);
		sri.setValidateCertificateRevocation(false);
		sri.setReturnCertificates(false);
		sri.setReturnTimeStampInfo(true);
		validationRequest.setSignatureRequestedInformation(sri);
		ValidateSignatureResponse validateSignatureResponse;
		try {
			validateSignatureResponse = new org.fundaciobit.pluginsib.validatesignature.afirmacxf.AfirmaCxfValidateSignaturePlugin(
					"es.caib.distribucio.",
					this.getProperties()).
			validateSignature(validationRequest);
			salutPluginComponent.incrementarOperacioOk(System.currentTimeMillis() - start);
		} catch (Exception e) {
			salutPluginComponent.incrementarOperacioError();
			throw new es.caib.distribucio.plugin.SistemaExternException(e);
		}
		
		// Completa la resposta
		resposta.setStatus(validateSignatureResponse.getValidationStatus().getStatus());
		resposta.setErrMsg(validateSignatureResponse.getValidationStatus().getErrorMsg());
		resposta.setErrException(validateSignatureResponse.getValidationStatus().getErrorException());
		
		if (validateSignatureResponse.getSignatureDetailInfo() != null) {
			for (SignatureDetailInfo signatureInfo: validateSignatureResponse.getSignatureDetailInfo()) {
				ArxiuFirmaDetallDto detall = new ArxiuFirmaDetallDto();
				TimeStampInfo timeStampInfo = signatureInfo.getTimeStampInfo();
				if (timeStampInfo != null) {
					detall.setData(timeStampInfo.getCreationTime());
				} else {
					detall.setData(signatureInfo.getSignDate());
				}
				InformacioCertificat certificateInfo = signatureInfo.getCertificateInfo();
				if (certificateInfo != null) {
					detall.setResponsableNif(certificateInfo.getNifResponsable());
					detall.setResponsableNom(certificateInfo.getNomCompletResponsable());
					detall.setEmissorCertificat(certificateInfo.getEmissorOrganitzacio());
				}
				resposta.getFirmaDetalls().add(detall);
			}
			resposta.setPerfil(ArxiuConversions.toPerfilFirmaArxiu(validateSignatureResponse.getSignProfile()));
			resposta.setTipus(ArxiuConversions.toFirmaTipus(
					validateSignatureResponse.getSignType(),
					validateSignatureResponse.getSignFormat()));
		}
		return resposta;
	}
	
	private String getPropertyEndpoint() {
		return getProperties().getProperty(
				"es.caib.distribucio.pluginsib.validatesignature.afirmacxf.endpoint");
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
		peticions.setEndpoint(getPropertyEndpoint());
		return peticions;
	}
	
}
