/**
 * 
 */
package es.caib.distribucio.core.helper;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.distribucio.core.api.dto.BackofficeTipusEnumDto;
import es.caib.distribucio.core.api.dto.LogTipusEnumDto;
import es.caib.distribucio.core.api.dto.ReglaTipusEnumDto;
import es.caib.distribucio.core.api.exception.AplicarReglaException;
import es.caib.distribucio.core.api.exception.ScheduledTaskException;
import es.caib.distribucio.core.api.registre.RegistreAnotacio;
import es.caib.distribucio.core.api.service.bantel.wsClient.v2.BantelFacadeException;
import es.caib.distribucio.core.api.service.bantel.wsClient.v2.BantelFacadeWsClient;
import es.caib.distribucio.core.api.service.bantel.wsClient.v2.model.ReferenciaEntrada;
import es.caib.distribucio.core.api.service.bantel.wsClient.v2.model.ReferenciasEntrada;
import es.caib.distribucio.core.api.service.ws.DistribucioBackofficeResultatProces;
import es.caib.distribucio.core.api.service.ws.DistribucioBackofficeWsService;
import es.caib.distribucio.core.entity.BustiaEntity;
import es.caib.distribucio.core.entity.ContingutMovimentEntity;
import es.caib.distribucio.core.entity.EntitatEntity;
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
		BustiaEntity pendentBustia = null;
		if (pendent.getPare() instanceof BustiaEntity) {
			pendentBustia = (BustiaEntity)pendent.getPare();
		}
		ReglaEntity regla = pendent.getRegla();
		String error = null;
		try {
			switch (regla.getTipus()) {
			case BACKOFFICE:
				if (BackofficeTipusEnumDto.SISTRA.equals(regla.getBackofficeTipus())) {
					// SISTRA
					
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
				} else {
					// DISTRIBUCIO

					//System.out.println(">>> Processant anotacio de registre amb backoffice (id=" + pendent.getId() + ", identificador=" + pendent.getIdentificador() + ")");
					DistribucioBackofficeWsService backofficeClient = new WsClientHelper<DistribucioBackofficeWsService>().generarClientWs(
							getClass().getResource("/es/caib/distribucio/core/service/ws/backoffice/DistribucioBackoffice.wsdl"),
							regla.getBackofficeUrl(),
							new QName(
									"http://www.caib.es/distribucio/ws/backoffice",
									"DistribucioBackofficeService"),
							regla.getBackofficeUsuari(),
							regla.getBackofficeContrasenya(),
							null,
							DistribucioBackofficeWsService.class);
					DistribucioBackofficeResultatProces resultat = backofficeClient.processarAnotacio(
							registreHelper.fromRegistreEntity(pendent));
					if (resultat.isError()) {
						error = "[" + resultat.getErrorCodi() + "] " + resultat.getErrorDescripcio();
					}
				}
				break;
			case BUSTIA:
				//System.out.println(">>> Processant anotacio de registre movent a bústia (id=" + pendent.getId() + ", identificador=" + pendent.getIdentificador() + ")");
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
			if (pendentBustia != null) {
				bustiaHelper.evictElementsPendentsBustia(
						regla.getEntitat(),
						pendentBustia);
			}
			
			// Si la regla és del tipus backoffice marca com a esborrat el contingut una vegada processat
			if (ReglaTipusEnumDto.BACKOFFICE.equals(regla.getTipus())) {
				pendent.updateEsborrat(1);
			}
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
