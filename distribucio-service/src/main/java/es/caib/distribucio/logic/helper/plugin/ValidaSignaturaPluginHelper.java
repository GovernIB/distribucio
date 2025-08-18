package es.caib.distribucio.logic.helper.plugin;

import static es.caib.distribucio.logic.intf.dto.IntegracioCodi.VALIDASIG;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.fundaciobit.pluginsib.validatecertificate.InformacioCertificat;
import org.fundaciobit.pluginsib.validatesignature.api.SignatureDetailInfo;
import org.fundaciobit.pluginsib.validatesignature.api.SignatureRequestedInformation;
import org.fundaciobit.pluginsib.validatesignature.api.TimeStampInfo;
import org.fundaciobit.pluginsib.validatesignature.api.ValidateSignatureRequest;
import org.fundaciobit.pluginsib.validatesignature.api.ValidateSignatureResponse;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.distribucio.logic.helper.ConfigHelper;
import es.caib.distribucio.logic.helper.IntegracioHelper;
import es.caib.distribucio.logic.intf.dto.ArxiuFirmaDetallDto;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.IntegracioDiagnostic;
import es.caib.distribucio.logic.intf.exception.SistemaExternException;
import es.caib.distribucio.logic.intf.helper.ArxiuConversions;
import es.caib.distribucio.persist.repository.EntitatRepository;
import es.caib.distribucio.plugin.validacio.ValidaSignaturaResposta;
import es.caib.distribucio.plugin.validatesignature.ValidateSignaturePlugin;
import lombok.extern.slf4j.Slf4j;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class ValidaSignaturaPluginHelper extends AbstractPluginHelper<ValidateSignaturePlugin> {
	
	public static final String GRUP = "VALID_SIGNATURE";
	
	public ValidaSignaturaPluginHelper(
			IntegracioHelper integracioHelper, 
			ConfigHelper configHelper,
			EntitatRepository entitatRepository) {
		super(integracioHelper, configHelper, entitatRepository);
	}

	@Override
	public boolean diagnosticar(Map<String, IntegracioDiagnostic> diagnostics) throws Exception {
		try (var arxiuSignat = this.getClass().getResourceAsStream("/diagnostic/test_validacio_firma.pdf")){
			if (arxiuSignat == null) {
				log.error("L'Arxiu de proves per el diagnostic no existeix");
				return false;
			}
			var bytes = arxiuSignat.readAllBytes();
			var signatura = validaSignaturaObtenirDetalls(bytes, null, null);
			return signatura != null && signatura.getErrMsg() == null;
		}
	}


	public ValidaSignaturaResposta validaSignaturaObtenirDetalls(
			byte[] documentContingut,
			byte[] firmaContingut,
			String registreNumero) {

		ValidaSignaturaResposta resposta = new ValidaSignaturaResposta();
		
		String accioDescripcio = "Obtenir informació de document firmat";
		String usuariIntegracio = this.getPropertyUsuariValidacioSignatura();
		
		// Abans de cridar a la validació de firmes comprova si la grandària supera el màxim en bytes
		Integer maxBytes = getPropertyMaxBytesValidacioFirma();
		int bytes = Math.max(documentContingut != null ? documentContingut.length : 0, 
								firmaContingut != null ? firmaContingut.length : 0);
		if (maxBytes != null && maxBytes < bytes) {
			resposta.setStatus(ValidaSignaturaResposta.FIRMA_ERROR);
			resposta.setErrMsg("Error de validació. La grandària de la firma " + bytes + " és superior a la grandària màxima configurada " + maxBytes + " i no es pot enviar a validar.");
			return resposta;
		}
		
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("documentContingut", documentContingut != null ? documentContingut.length + " bytes" : "null");
		accioParams.put("firmaContingut", firmaContingut != null ? firmaContingut.length + " bytes" : "null");
		long t0 = System.currentTimeMillis();
		try {
			ValidateSignatureRequest validationRequest = new ValidateSignatureRequest();
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
			ValidateSignatureResponse validateSignatureResponse = getPlugin().validateSignature(validationRequest);
			
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
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_VALIDASIG,
					registreNumero,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return resposta;
		} catch (Throwable ex) {
			String errorDescripcio = "Error al accedir al plugin de validar signatures";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_VALIDASIG,
					registreNumero,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_VALIDASIG,
					errorDescripcio,
					ex);
		}
	}
	
	public boolean isActiu() {
		return getPlugin() != null;
	}

	@Override
	protected ValidateSignaturePlugin getPlugin() {
		var plugin = pluginMap.get(GLOBAL);
		if (plugin != null) {
			return plugin;
		}

		String codiEntitat = getCodiEntitatActual();
		
		loadPluginProperties(GRUP);
		
		String pluginClass = getPluginClassProperty();
		
		if (Strings.isNullOrEmpty(pluginClass)) {
			var msg = "No està configurada la classe pel plugin de validació de signatures";
			log.error(msg);
			throw new SistemaExternException(VALIDASIG.name(), msg);
		}
		
		if (pluginClass != null && pluginClass.length() > 0) {
			try {
				Class<?> clazz = Class.forName(pluginClass);
				Properties properties = configHelper.getAllProperties(codiEntitat);
				plugin = (ValidateSignaturePlugin)clazz.
						getDeclaredConstructor(String.class, Properties.class).
						newInstance(properties);
			} catch (Exception ex) {
				throw new SistemaExternException(
						VALIDASIG.name(),
						"Error al crear la instància del plugin de validació de signatures amb el nom de la classe " + pluginClass,
						ex);
			}
			pluginMap.put(GLOBAL, plugin);
			return plugin;
		} else {
			throw new SistemaExternException(
					VALIDASIG.name(),
					"No està configurada la classe pel plugin de validació de signatures");
		}
	}

	@Override
	protected String getPluginClassProperty() {
		return configHelper.getConfig("es.caib.distribucio.plugin.validatesignature.class");
	}
	
	@Override
	protected IntegracioApp getCodiApp() {
		return IntegracioApp.VFI;
	}

	@Override
	protected String getConfigGrup() {
		return GRUP;
	}
	
	private String getPropertyUsuariValidacioSignatura() {
		return configHelper.getConfig(
				"es.caib.distribucio.plugins.validatesignature.afirmacxf.authorization.username","-");
	}
	
	private Integer getPropertyMaxBytesValidacioFirma() {
		Integer maxBytes = null;
		String configKey = "es.caib.distribucio.pluginsib.validatesignature.maxBytes";
		try {
			maxBytes = Integer.valueOf(configHelper.getConfig(configKey));
		} catch(Exception e) {
			log.error("Error llegint la propietat entera de màxim de bytes del plugin de validació " + configKey + ": " + e.getMessage());
		}
		return maxBytes;
	}

}
