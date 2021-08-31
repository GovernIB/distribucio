/**
 * 
 */
package es.caib.distribucio.plugin.caib.signatura;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.fundaciobit.apisib.apifirmasimple.v1.ApiFirmaEnServidorSimple;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleAvailableProfile;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleCommonInfo;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleFile;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleFileInfoSignature;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignDocumentRequest;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignatureResult;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleSignedFileInfo;
import org.fundaciobit.apisib.apifirmasimple.v1.beans.FirmaSimpleStatus;
import org.fundaciobit.apisib.apifirmasimple.v1.jersey.ApiFirmaEnServidorSimpleJersey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.distribucio.core.api.exception.SistemaExternException;
import es.caib.distribucio.plugin.signatura.SignaturaPlugin;
import es.caib.distribucio.plugin.utils.PropertiesHelper;

/**
 * Implementació del plugin de signatura emprant el portafirmes
 * de la CAIB desenvolupat per l'IBIT (PortaFIB).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class FirmaSimplePluginPortafib implements SignaturaPlugin {	  
	
	private static final String PROPERTIES_BASE = "es.caib.distribucio.plugin.signatura.portafib.";
	  
	@Override
	public byte[] signar(
			String id,
			String nom,
			String motiu,
			String tipusFirma,
			byte[] contingut, 
			String tipusDocumental) {

		ApiFirmaEnServidorSimple api = new ApiFirmaEnServidorSimpleJersey(
				getPropertyEndpoint(), 
				getPropertyUsername(),
				getPropertyPassword());
		
		FirmaSimpleFile fileToSign = new FirmaSimpleFile(nom, "application/pdf", contingut);

		FirmaSimpleSignatureResult result;
		try {
			
//			getAvailableProfiles(api);
			String perfil = getPropertyPerfil();
			result = internalSignDocument(
					api,
					perfil,
					fileToSign,
					motiu,
					tipusDocumental);
			
			return result.getSignedFile().getData();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}  
	

   
	
	protected FirmaSimpleSignatureResult internalSignDocument(
			ApiFirmaEnServidorSimple api,
			final String perfil,
			FirmaSimpleFile fileToSign,
			String motiu,
			String tipusDocumental) throws Exception, FileNotFoundException, IOException {
		String signID = "999";
		String name = fileToSign.getNom();
		String reason = motiu;
		String location = PropertiesHelper.getProperties().getProperty(PROPERTIES_BASE + "location", "Palma");

		int signNumber = 1;
		String languageSign = "ca";
		Long tipusDocumentalID = tipusDocumental != null ? Long.valueOf(tipusDocumental.substring(2)) : null;

		FirmaSimpleFileInfoSignature fileInfoSignature = new FirmaSimpleFileInfoSignature(
				fileToSign,
				signID,
				name,
				reason,
				location,
				signNumber,
				languageSign,
				tipusDocumentalID);

		String languageUI = "ca";
		String username = PropertiesHelper.getProperties().getProperty(PROPERTIES_BASE + "username", null);
		String administrationID = null;
		String signerEmail = PropertiesHelper.getProperties().getProperty(PROPERTIES_BASE + "signerEmail", "suport@caib.es");

		FirmaSimpleCommonInfo commonInfo;
		commonInfo = new FirmaSimpleCommonInfo(perfil, languageUI, username, administrationID, signerEmail);

		logger.debug("languageUI = |" + languageUI + "|");

		FirmaSimpleSignDocumentRequest signature;
		signature = new FirmaSimpleSignDocumentRequest(commonInfo, fileInfoSignature);

		FirmaSimpleSignatureResult fullResults = api.signDocument(signature);

		FirmaSimpleStatus transactionStatus = fullResults.getStatus();

		int status = transactionStatus.getStatus();

		switch (status) {

		case FirmaSimpleStatus.STATUS_INITIALIZING: // = 0;
			throw new SistemaExternException("API de firma simple ha tornat status erroni: Initializing ...Unknown Error (???)");

		case FirmaSimpleStatus.STATUS_IN_PROGRESS: // = 1;
			throw new SistemaExternException("API de firma simple ha tornat status erroni: In PROGRESS ...Unknown Error (???)");

		case FirmaSimpleStatus.STATUS_FINAL_ERROR: // = -1;
			throw new SistemaExternException("Error durant la realització de les firmes: " + transactionStatus.getErrorMessage() +"\r\n" +transactionStatus.getErrorStackTrace());

		case FirmaSimpleStatus.STATUS_CANCELLED: // = -2;
			throw new SistemaExternException("S'ha cancel·lat el procés de firmat.");

		case FirmaSimpleStatus.STATUS_FINAL_OK: // = 2;
		{
			logger.debug(" ===== RESULTAT  =========");
			logger.debug(" ---- Signature [ " + fullResults.getSignID() + " ]");
			logger.debug(FirmaSimpleSignedFileInfo.toString(fullResults.getSignedFileInfo()));

			return fullResults;
		}
		default:
			throw new SistemaExternException("Status de firma desconegut");
		}
	}

	 public void getAvailableProfiles(ApiFirmaEnServidorSimple api) throws Exception {

		    final String languagesUI[] = new String[] { "ca", "es" };

		    for (String languageUI : languagesUI) {
		      logger.debug(" ==== LanguageUI : " + languageUI + " ===========");

		      List<FirmaSimpleAvailableProfile> listProfiles = api.getAvailableProfiles(languageUI);
		      if (listProfiles.size() == 0) {
		        logger.debug("NO HI HA PERFILS PER AQUEST USUARI APLICACIÓ");
		      } else {
		        for (FirmaSimpleAvailableProfile ap : listProfiles) {
		          logger.debug("  + " + ap.getName() + ":");
		          logger.debug("      * Codi: " + ap.getCode());
		          logger.debug("      * Desc: " + ap.getDescription());
		        }
		      }
		    }
	 }
	
	
	private String getPropertyEndpoint() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.plugin.api.firma.en.servidor.simple.endpoint");
	}
	
	private String getPropertyUsername() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.plugin.api.firma.en.servidor.simple.username");
	}
	
	private String getPropertyPassword() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.plugin.api.firma.en.servidor.simple.password");
	}
	
	private String getPropertyPerfil() {
		return System.getProperty(
				"es.caib.distribucio.plugin.api.firma.en.servidor.simple.perfil");
	}

	private static final Logger logger = LoggerFactory.getLogger(FirmaSimplePluginPortafib.class);
}
