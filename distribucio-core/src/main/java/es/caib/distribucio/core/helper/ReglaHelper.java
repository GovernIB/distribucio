/**
 * 
 */
package es.caib.distribucio.core.helper;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.namespace.QName;

import org.apache.axis.encoding.Base64;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.BackofficeTipusEnumDto;
import es.caib.distribucio.core.api.dto.LogTipusEnumDto;
import es.caib.distribucio.core.api.exception.AplicarReglaException;
import es.caib.distribucio.core.api.exception.ScheduledTaskException;
import es.caib.distribucio.core.api.registre.RegistreAnotacio;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.service.bantel.wsClient.v2.BantelFacadeException;
import es.caib.distribucio.core.api.service.bantel.wsClient.v2.BantelFacadeWsClient;
import es.caib.distribucio.core.api.service.bantel.wsClient.v2.model.ReferenciaEntrada;
import es.caib.distribucio.core.api.service.bantel.wsClient.v2.model.ReferenciasEntrada;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.ContingutMovimentEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
import es.caib.distribucio.core.entity.RegistreAnnexEntity;
import es.caib.distribucio.core.entity.RegistreEntity;
import es.caib.distribucio.core.entity.ReglaEntity;
import es.caib.distribucio.core.repository.RegistreRepository;
import es.caib.distribucio.core.repository.ReglaRepository;

/**
 * Mètodes comuns per a aplicar regles.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ReglaHelper {

	@Resource
	private ReglaRepository reglaRepository;
	@Resource
	private RegistreRepository registreRepository;

	@Resource
	private ContingutHelper contingutHelper;
	@Resource
	private ContingutLogHelper contingutLogHelper;
	@Resource
	private RegistreHelper registreHelper;
	@Autowired
	private PluginHelper pluginHelper;
	@Resource
	private BustiaHelper bustiaHelper;
	@Resource
	private EmailHelper emailHelper;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private AlertaHelper alertaHelper;

	private final static String CLAU_XIFRAT = "3çS)ZX!3a94_*?S2";



	public ReglaEntity findAplicable(
			EntitatEntity entitat,
			String unitatAdministrativa,
			RegistreAnotacio anotacio) {
		List<ReglaEntity> regles = reglaRepository.findByEntitatAndActivaTrueOrderByOrdreAsc(entitat);
		ReglaEntity reglaAplicable = null;
		for (ReglaEntity regla: regles) {
			if (regla.getUnitatCodi() == null || regla.getUnitatCodi().equals(unitatAdministrativa)) {
				if (anotacio.getAssumpteCodi() != null && anotacio.getAssumpteCodi().equals(regla.getAssumpteCodi())) {
					reglaAplicable = regla;
					break;
				}
			}
		}
		return reglaAplicable;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void aplicar(
			Long pendentId) {
		RegistreEntity pendent = registreRepository.findOne(pendentId);
		ReglaEntity regla = pendent.getRegla();
		String error = null;
		try {
			switch (regla.getTipus()) {
			case BACKOFFICE: // ############################### BACKOFFICE ###############################
				
				if (BackofficeTipusEnumDto.SISTRA.equals(regla.getBackofficeTipus())) { // ############################### BACKOFFICE SISTRA ###############################
					
					for (RegistreAnnexEntity annex: pendent.getAnnexos()) {
							if (annex.getFitxerNom().equals("DatosPropios.xml") || annex.getFitxerNom().equals("Asiento.xml"))
								processarAnnexSistra(pendent, annex);
					}
					
					BantelFacadeWsClient backofficeSistraClient = new WsClientHelper<BantelFacadeWsClient>().generarClientWs(
							getClass().getResource("/es/caib/distribucio/core/service/ws/backofficeSistra/BantelFacade.wsdl"),
							regla.getBackofficeUrl(),
							new QName(
									"urn:es:caib:bantel:ws:v2:services",
									"BantelFacadeService"),
							regla.getBackofficeUsuari(),
							regla.getBackofficeContrasenya(),
							null,
							BantelFacadeWsClient.class);
					// Crea la llista de referències d'entrada
					ReferenciasEntrada referenciesEntrades = new ReferenciasEntrada();
					ReferenciaEntrada referenciaEntrada = new ReferenciaEntrada();
					referenciaEntrada.setNumeroEntrada(pendent.getNumero());
					referenciaEntrada.setClaveAcceso(ReglaHelper.encrypt(pendent.getNumero()));	
					referenciesEntrades.getReferenciaEntrada().add(referenciaEntrada);
					// Invoca el backoffice sistra
					try {
						backofficeSistraClient.avisoEntradas(referenciesEntrades);
					} catch (BantelFacadeException bfe) {
						error = "[" + bfe.getFaultInfo() + "] " + bfe.getLocalizedMessage();
					}
				
			
				} else if (BackofficeTipusEnumDto.DISTRIBUCIO.equals(regla.getBackofficeTipus())){ // ############################### BACKOFFICE DISTRIBUCIO ###############################
					
					pendent.updateProcesBackPendent();
					pendent.updateBackPendentData(new Date());
					// there is @Scheduled method that sends periodically anotacios with state: BACK_PENDENT to backoffice 
				}
				break;
				
			case BUSTIA: // ############################### BUSTIA ###############################
				ContingutMovimentEntity contingutMoviment = contingutHelper.ferIEnregistrarMoviment(
						pendent,
						regla.getBustia(),
						null);
				contingutLogHelper.log(
						pendent,
						LogTipusEnumDto.MOVIMENT,
						contingutMoviment,
						true,
						true);
				emailHelper.emailBustiaPendentContingut(
						regla.getBustia(),
						pendent,
						contingutMoviment);
				pendent.updateProces(
						RegistreProcesEstatEnum.BUSTIA_PENDENT, 
						null);
				break;
				
			default:
				error = "Tipus de regla desconegut (" + regla.getTipus() + ")";
				break;
			}
		} catch (Exception ex) {
			Throwable t = ExceptionUtils.getRootCause(ex);
			if (t == null)
				t = ex.getCause();
			if (t == null)
				t = ex;
			error = ExceptionUtils.getStackTrace(t);
		}
		
		
		if (error != null) {
			throw new AplicarReglaException(error);
		} else {
			BustiaEntity pendentBustia = null;
			if (pendent.getPare() instanceof BustiaEntity) {
				pendentBustia = (BustiaEntity)pendent.getPare();
			}
			if (pendentBustia != null) {
				bustiaHelper.evictCountElementsPendentsBustiesUsuari(
						regla.getEntitat(),
						pendentBustia);
			}
			
		}
	}
	
	
	
	/*
	 * Mètode privat per obrir el document annex de tipus sistra i extreure'n
	 * informació per a l'anotació de registre. La informació que es pot extreure
	 * depén del document:
	 * - Asiento.xml: ASIENTO_REGISTRAL.DATOS_ASUNTO.IDENTIFICADOR_TRAMITE (VARCHAR2(20))
	 * - DatosPropios.xml: DATOS_PROPIOS.INSTRUCCIONES.IDENTIFICADOR_PROCEDIMIENTO (VARCHAR2(100))
	 * 
	 * @param anotacio 
	 * 			Anotació del registre
	 * @param annex
	 * 			Document annex amb el contingut per a llegir.
	 */
	private void processarAnnexSistra(
			RegistreEntity anotacio,
			RegistreAnnexEntity annex) {
		try {
			byte[] annexContingut = null;
			if (annex.getGesdocDocumentId() != null) {
				ByteArrayOutputStream baos_doc = new ByteArrayOutputStream();
				pluginHelper.gestioDocumentalGet(
					annex.getGesdocDocumentId(), 
					PluginHelper.GESDOC_AGRUPACIO_ANOTACIONS_REGISTRE_DOC_TMP, 
					baos_doc);
				annexContingut = baos_doc.toByteArray();
				annex.updateGesdocDocumentId(null);
			}
			org.w3c.dom.Document doc = XmlHelper.getDocumentFromContent(annexContingut);
			if (annex.getFitxerNom().equals("DatosPropios.xml")) {
				String identificadorProcediment = XmlHelper.getNodeValue(
						doc.getDocumentElement(), "INSTRUCCIONES.IDENTIFICADOR_PROCEDIMIENTO");
				anotacio.updateIdentificadorProcedimentSistra(identificadorProcediment);
			} else if (annex.getFitxerNom().equals("Asiento.xml")) {
				String identificadorTramit = XmlHelper.getNodeValue(
						doc.getDocumentElement(), "DATOS_ASUNTO.IDENTIFICADOR_TRAMITE");
				anotacio.updateIdentificadorTramitSistra(identificadorTramit);
			}		
		} catch (Exception e) {
			logger.error(
					"Error processant l'annex per l'anotació amb regla backoffice SISTRA " + annex.getFitxerNom(),
					e);
		}
	}
	

	public static String encrypt(String input) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] crypted = null;
		SecretKeySpec skey = new SecretKeySpec(CLAU_XIFRAT.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, skey);
		crypted = cipher.doFinal(input.getBytes());
		return new String(Base64.encode(crypted));
	}

	public Exception reglaAplicar(
			RegistreEntity anotacio) {
		contingutLogHelper.log(
				anotacio,
				LogTipusEnumDto.PROCESSAMENT,
				null,
				null,
				false,
				false);
		logger.debug("Aplicant regla a anotació de registre (" +
				"anotacioId=" + anotacio.getId() + ", " +
				"anotacioNumero=" + anotacio.getNumero() + ", " +
				"reglaId=" + anotacio.getRegla().getId() + ", " +
				"reglaTipus=" + anotacio.getRegla().getTipus().name() + ", " +
				(anotacio.getRegla().getBackofficeTipus() != null ?
						"reglaBackofficeTipus=" + anotacio.getRegla().getBackofficeTipus().name() + ", " 
						: "") +
				"bustia=" + anotacio.getRegla().getBustia() + ")");
		try {
			
			//aplicar regla
			aplicar(anotacio.getId());
			
			logger.debug("Processament anotació OK (id=" + anotacio.getId() + ", núm.=" + anotacio.getNumero() + ")");
			alertaHelper.crearAlerta(
					messageHelper.getMessage(
							"alertes.segon.pla.aplicar.regles",
							new Object[] {anotacio.getId()}),
					null,
					anotacio.getId());
			return null;
		} catch (Exception ex) {
			String procesError;
			if (ex instanceof ScheduledTaskException) {
				procesError = ex.getMessage();
			} else {
				procesError = ExceptionUtils.getStackTrace(ExceptionUtils.getRootCause(ex));
			}
			logger.debug("Processament anotació ERROR (" +
					"id=" + anotacio.getId() + ", " +
					"núm.=" + anotacio.getIdentificador() + "): " +
					procesError);
			alertaHelper.crearAlerta(
					messageHelper.getMessage(
							"alertes.segon.pla.aplicar.regles.error",
							new Object[] {anotacio.getId()}),
					ex,
					anotacio.getId());
			return ex;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(ReglaHelper.class);

}
