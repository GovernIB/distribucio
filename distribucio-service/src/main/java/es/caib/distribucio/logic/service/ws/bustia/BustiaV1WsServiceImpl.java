/**
 * 
 */
package es.caib.distribucio.logic.service.ws.bustia;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jws.WebParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import es.caib.distribucio.logic.helper.IntegracioHelper;
import es.caib.distribucio.logic.helper.SubsistemesHelper;
import es.caib.distribucio.logic.helper.SubsistemesHelper.SubsistemesEnum;
import es.caib.distribucio.logic.intf.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.SemaphoreDto;
import es.caib.distribucio.logic.intf.exception.ValidationException;
import es.caib.distribucio.logic.intf.registre.Firma;
import es.caib.distribucio.logic.intf.registre.RegistreAnnex;
import es.caib.distribucio.logic.intf.registre.RegistreAnnexSicresTipusDocumentEnum;
import es.caib.distribucio.logic.intf.registre.RegistreAnotacio;
import es.caib.distribucio.logic.intf.registre.RegistreTipusEnum;
import es.caib.distribucio.logic.intf.service.BustiaService;
import es.caib.distribucio.logic.intf.service.ConfigService;
import es.caib.distribucio.logic.intf.service.EntitatService;
import es.caib.distribucio.logic.intf.service.ws.bustia.BustiaV1WsService;
import es.caib.pluginsib.arxiu.api.ContingutOrigen;
import es.caib.pluginsib.arxiu.api.DocumentEstatElaboracio;
import es.caib.pluginsib.arxiu.api.DocumentTipus;
import es.caib.pluginsib.arxiu.api.FirmaPerfil;
import es.caib.pluginsib.arxiu.api.FirmaTipus;

/**
 * Implementació dels mètodes per al servei d'enviament de
 * continguts a bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class BustiaV1WsServiceImpl implements BustiaV1WsService {

	@Autowired
	private BustiaService bustiaService;
	@Autowired
	private IntegracioHelper integracioHelper;
	@Autowired
	private MetricRegistry metricRegistry;
	@Autowired
	private ConfigService configService;
	@Autowired
	private EntitatService entitatService;

	@Override
	@Transactional
	public void enviarAnotacioRegistreEntrada(
			String entitat,
			String unitatAdministrativa,
			RegistreAnotacio registreEntrada) {
		final Timer timer = metricRegistry.timer(MetricRegistry.name(BustiaV1WsServiceImpl.class, "enviarAnotacioRegistreEntrada"));
		Timer.Context context = timer.time();
		long start = System.currentTimeMillis();
		String entitatOArrel;
		if (entitat == null || entitat.isEmpty()) {
//			UnitatOrganitzativaDto unitatOrganitzativaDto = unitatOrganitzativaHelper.findAmbCodi(unitatAdministrativa);
//			entitatOArrel = unitatOrganitzativaDto.getCodiUnitatArrel();
//			registreEntrada.setEntitatCodi(entitatOArrel);
            throw new ValidationException("Entitat no pot ser buida");
		} else {
			entitatOArrel = entitat;
		}
		EntitatDto entitatDto = entitatService.findByCodiDir3(entitatOArrel);
		if (entitatDto == null) {
			entitatDto = new EntitatDto();
			entitatDto.setCodi(entitatOArrel);
		}
		configService.setEntitatPerPropietat(entitatDto);
		String registreEntradaNumero = (registreEntrada != null) ? registreEntrada.getNumero() : null;
		String registreEntradaExtracte = (registreEntrada != null) ? registreEntrada.getExtracte() : null;
		int numAnnexos = (registreEntrada != null && registreEntrada.getAnnexos() != null) ? registreEntrada.getAnnexos().size() : 0;
		StringBuilder ambFirma = new StringBuilder();
		if (registreEntrada != null && registreEntrada.getAnnexos() != null) {
			boolean first = true;
			String fitxerTitol = "";
			String titolData = "";
			for (RegistreAnnex annex: registreEntrada.getAnnexos()) {
				if (!first) {
					ambFirma.append(", ");
				}
				if (annex.getTitol().equals("") ||
						annex.getTitol().startsWith(".")) {
					do {
						fitxerTitol = String.valueOf(new Date().getTime()) + annex.getTitol();
					}while (fitxerTitol.equals(titolData));
					
					titolData = fitxerTitol;
					annex.setTitol(fitxerTitol);
				}
				ambFirma.append(Boolean.toString(annex.getFirmes() != null));
				first = false;
			}
		}
		String accioDescripcio = "Nou registre d'entrada ";
		String usuariIntegracio = this.getUsuariIntegracio();
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("entitat", entitatOArrel);
		accioParams.put("unitatAdministrativa", unitatAdministrativa);
		accioParams.put("numero", registreEntradaNumero);
		accioParams.put("extracte", registreEntradaExtracte);
		accioParams.put("annexosNum", Integer.toString(numAnnexos));
		accioParams.put("annexosFirmats", ambFirma.toString());
		long t0 = System.currentTimeMillis();
		try {
			logger.debug(
					"Nou registre d'entrada rebut en el servei web de bústia (" +
					"entitat=" + entitatOArrel + ", " +
					"unitatAdministrativa=" + unitatAdministrativa + ", " +
					"numero=" + registreEntradaNumero + ", " +
					"extracte=" + registreEntradaExtracte + ", " +
					"annexosNum=" + Integer.toString(numAnnexos) + ", " +
					"annexosFirmats=" + ambFirma.toString() + ")");
			final Timer timerTotalvalidarAnotacioRegistre = metricRegistry.timer(MetricRegistry.name(BustiaV1WsServiceImpl.class, "enviarAnotacioRegistreEntrada.validarAnotacioRegistre"));
			Timer.Context contextTotalvalidarAnotacioRegistre = timerTotalvalidarAnotacioRegistre.time();
			validarAnotacioRegistre(registreEntrada);
			contextTotalvalidarAnotacioRegistre.stop();
			RegistreTipusEnum registreTipus = RegistreTipusEnum.ENTRADA;
			if (registreEntrada.getTipusES() != null && registreEntrada.getTipusES().equals("S")) {
				registreTipus = RegistreTipusEnum.SORTIDA;
			}
			final Timer timerregistreAnotacioCrearIProcessar = metricRegistry.timer(MetricRegistry.name(BustiaV1WsServiceImpl.class, "enviarAnotacioRegistreEntrada.registreAnotacioCrearIProcessar"));
			Timer.Context contextregistreAnotacioCrearIProcessar = timerregistreAnotacioCrearIProcessar.time();
			long registreId;
			synchronized(SemaphoreDto.getSemaphore()) {
				// Crea l'anotació
				registreId = bustiaService.registreAnotacioCrear(
						entitatOArrel,
						registreTipus,
						unitatAdministrativa,
						registreEntrada);
				// Processa l'anotació
				bustiaService.registreAnotacioProcessar(
						registreId);
			}
			contextregistreAnotacioCrearIProcessar.stop();
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_BUSTIAWS,
					registreEntrada.getNumero(),
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0);
			SubsistemesHelper.addSuccessOperation(SubsistemesEnum.AWS, System.currentTimeMillis() - start);
			context.stop();	
		} catch (Exception ex) {
			logger.error(
					"Error al processar nou registre d'entrada en el servei web de bústia (" +
					"entitat=" + entitatOArrel + ", " +
					"unitatAdministrativa=" + unitatAdministrativa + ", " +
					"numero=" + registreEntradaNumero + ", " +
					"extracte=" + registreEntradaExtracte + ", " +
					"annexosNum=" + Integer.toString(numAnnexos) + ", " +
					"annexosFirmats=" + ambFirma.toString() + ")",
					ex);
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_BUSTIAWS,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					"Error al processar registre d'entrada al servei web de bústia",
					ex);
			SubsistemesHelper.addErrorOperation(SubsistemesEnum.AWS);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void enviarDocument(
			@WebParam(name="entitat") String entitat,
			@WebParam(name="unitatAdministrativa") String unitatAdministrativa,
			@WebParam(name="referenciaDocument") String referenciaDocument) {
		logger.debug(
				"Nou document rebut al servei web de bústia (" +
				"unitatCodi:" + entitat + ", " +
				"unitatAdministrativa:" + unitatAdministrativa + ", " +
				"referenciaDocument:" + referenciaDocument + ")");
		throw new ValidationException(
				"Els enviaments de tipus DOCUMENT encara no estan suportats");
	}

	@Override
	public void enviarExpedient(
			@WebParam(name="entitat") String entitat,
			@WebParam(name="unitatAdministrativa") String unitatAdministrativa,
			@WebParam(name="referenciaExpedient") String referenciaExpedient) {
		logger.debug(
				"Nou expedient rebut al servei web de bústia (" +
				"unitatCodi:" + entitat + ", " +
				"unitatAdministrativa:" + unitatAdministrativa + ", " +
				"referenciaExpedient:" + referenciaExpedient + ")");
		throw new ValidationException(
				"Els enviaments de tipus EXPEDIENT encara no estan suportats");
	}

	private void validarAnotacioRegistre(
			RegistreAnotacio registreEntrada) {
		// Validació d'obligatorietat de camps
		validarObligatorietatRegistre(registreEntrada);
        if (registreEntrada.getProcedimentCodi() != null && registreEntrada.getServeiCodi() != null) {
            throw new ValidationException(
                    "No es pot especificar un valor per als camps 'procedimentCodi' y 'serveiCodi' a la vegada");
        }
		// Validació de format de camps
		validarFormatCampsRegistre(registreEntrada);
		// Validació d'annexos
		if (registreEntrada.getAnnexos() != null && registreEntrada.getAnnexos().size() > 0) {
			for (RegistreAnnex annex : registreEntrada.getAnnexos()) {
				validarAnnex(annex);
			}
		}
		// Validació de procedència de justificant
		if (registreEntrada.getJustificant() != null && registreEntrada.getJustificant().getFitxerArxiuUuid() == null) {
			throw new ValidationException(
					"El justificant adjuntat no conté un uuid (" +
					"entitatCodi=" + registreEntrada.getEntitatCodi() + ", " +
					"llibreCodi=" + registreEntrada.getLlibreCodi() + ", " +
					"tipus=" + RegistreTipusEnum.ENTRADA.getValor() + ", " +
					"numero=" + registreEntrada.getNumero() + ", " +
					"data=" + registreEntrada.getData() + ")");
		}
	}

	private void validarObligatorietatRegistre(RegistreAnotacio registreEntrada) {
		if (registreEntrada.getNumero() == null) {
			throw new ValidationException(
					"Es obligatori especificar un valor pel camp 'numero'");
		}
		if (registreEntrada.getData() == null) {
			throw new ValidationException(
					"Es obligatori especificar un valor pel camp 'data'");
		}
		if (registreEntrada.getIdentificador() == null) {
			throw new ValidationException(
					"Es obligatori especificar un valor pel camp 'identificador'");
		}
		if (registreEntrada.getExtracte() == null) {
			throw new ValidationException(
					"Es obligatori especificar un valor pel camp 'extracte'");
		}
		if (registreEntrada.getOficinaCodi() == null) {
			throw new ValidationException(
					"Es obligatori especificar un valor pel camp 'oficinaCodi'");
		}
		if (registreEntrada.getLlibreCodi() == null) {
			throw new ValidationException(
					"Es obligatori especificar un valor pel camp 'llibreCodi'");
		}
		if (registreEntrada.getIdiomaCodi() == null) {
			throw new ValidationException(
					"Es obligatori especificar un valor pel camp 'idiomaCodi'");
		}
	}

	private void validarFormatCampsRegistre(RegistreAnotacio registreEntrada) {
		if (registreEntrada.getJustificant() != null) {
			validarFormatAnnex(registreEntrada.getJustificant());
		}
		if (registreEntrada.getAnnexos() != null) {
			for (RegistreAnnex annex: registreEntrada.getAnnexos()) {
				validarFormatAnnex(annex);
			}
		}
		if (registreEntrada.getExtracte().length() > 240) {
			throw new ValidationException(
					"La llargada màxima de l'extracte no pot superar els 240 caràcters");
		}
	}

	/** Valida que l'annex:
	 * Tingui el nom informat.
	 * Valida les seves firmes.
	 * @param annex
	 * @throws ValidationException
	 */
	private void validarAnnex(RegistreAnnex annex) throws ValidationException{
		if (annex.getFirmes() != null && annex.getFirmes().size() > 0) {
			for (Firma firma: annex.getFirmes()) {
				validaFirma(annex, firma);
			}
		} else {
			validaContingutAnnex(annex);
		}
	}

	/** Valida la firma. Valida:
	 * El tipus de firma ha d'estar reconegut.
	 * Si el tipus és TF04 l'annex ha de tenir el contingut informat.
	 * Si el tipus és TF05 la firma ha de tenir el contingut informat
	 * @param annex
	 * @param firma
	 */
	private void validaFirma(
			RegistreAnnex annex,
			Firma firma) {
		if (firma.getTipus() == null) {
			throw new ValidationException(
					"Es obligatori especificar un valor pel camp 'tipus' de la firma");
		}
		DocumentNtiTipoFirmaEnumDto firmaTipus = null;
		try {
			firmaTipus = DocumentNtiTipoFirmaEnumDto.valueOf(firma.getTipus());
		} catch(Exception e) {
			throw new ValidationException(
					"El tipus de firma '" + firma.getTipus() + "' no es reconeix com a vàlid.");
		}
		boolean detached = DocumentNtiTipoFirmaEnumDto.TF04.equals(firmaTipus);
		if (detached) {
			if (annex.getFitxerContingut() == null) {
				throw new ValidationException(
						"El contingut de l'annex ha d'estar informat quan aquest conté una firma de tipus detached");
			}
			if (firma.getContingut() == null) {
				throw new ValidationException(
						"El contingut de la firma ha d'estar informat per a les firmes de tipus detached");
			}
			validaContingutAnnex(annex);
		}
	}

	private void validaContingutAnnex(RegistreAnnex annex) {
		if (annex.getFitxerArxiuUuid() == null && annex.getFitxerContingut() == null)
			throw new ValidationException(
					"S'ha d'especificar el contingut del document o l'UUID del document dins l'arxiu (" +
					"annex=" + annex.getTitol() + ")");
		if (annex.getFitxerContingut() != null && annex.getFitxerNom() == null) {
			throw new ValidationException(
					"Si s'envia el contingut de l'annex és obligatori especificar un valor pel camp 'fitxerNom'");
		}
	}

	private void validarFormatAnnex(RegistreAnnex annex) {
		if (annex.getEniOrigen() != null && !enumContains(ContingutOrigen.class, annex.getEniOrigen(), true)) {
			throw new ValidationException(
					"El valor de l'annex o justificant 'EniOrigen' no és vàlid");
		}
		if (annex.getEniEstatElaboracio() != null && !enumContains(DocumentEstatElaboracio.class, annex.getEniEstatElaboracio(), true)) {
			throw new ValidationException(
					"El valor de l'annex o justificant 'EniEstatElaboracio' no és vàlid");
		}
		if (annex.getEniTipusDocumental() != null && !enumContains(DocumentTipus.class, annex.getEniTipusDocumental(), true)) {
			throw new ValidationException(
					"El valor de l'annex o justificant 'EniTipusDocumental' no és vàlid");
		}
		if (annex.getSicresTipusDocument() != null && !enumContains(RegistreAnnexSicresTipusDocumentEnum.class, annex.getSicresTipusDocument(), true)) {
			throw new ValidationException(
					"El valor de l'annex o justificant 'SicresTipusDocument' no és vàlid");
		}
		if (annex.getFirmes() != null) {
			for (Firma firma: annex.getFirmes()) {
				validarFormatFirma(firma);
			}
		}
	}

	private void validarFormatFirma(Firma firma) {
		if (firma.getTipus() != null && !enumContains(FirmaTipus.class, firma.getTipus(), true)) {
			throw new ValidationException(
					"El valor de la firma 'Tipus' no és vàlid");
		}
		if (firma.getPerfil() != null && !enumContains(FirmaPerfil.class, firma.getPerfil(), true)) {
			throw new ValidationException(
					"El valor de la firma 'Perfil' no és vàlid");
		}
	}

	private <E extends Enum<E>> boolean enumContains(Class<E> enumerat, String test, boolean modeText) {
		for (Enum<E> c: enumerat.getEnumConstants()) {
			if (modeText) {
				if (c.toString().equalsIgnoreCase(test)) {
					return true;
				}
			} else {
				if (c.name().equalsIgnoreCase(test)) {
					return true;
				}
			}
		}
		return false;
	}

	private String getUsuariIntegracio() {
		String usuari = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			usuari = auth.getName();
		}
		return usuari;
	}

	private static final Logger logger = LoggerFactory.getLogger(BustiaV1WsServiceImpl.class);

}
