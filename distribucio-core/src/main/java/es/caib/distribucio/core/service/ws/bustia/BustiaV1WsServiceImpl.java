/**
 * 
 */
package es.caib.distribucio.core.service.ws.bustia;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.caib.distribucio.core.api.dto.DocumentNtiTipoFirmaEnumDto;
import es.caib.distribucio.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.core.api.exception.ValidationException;
import es.caib.distribucio.core.api.registre.Firma;
import es.caib.distribucio.core.api.registre.RegistreAnnex;
import es.caib.distribucio.core.api.registre.RegistreAnnexSicresTipusDocumentEnum;
import es.caib.distribucio.core.api.registre.RegistreAnotacio;
import es.caib.distribucio.core.api.registre.RegistreTipusEnum;
import es.caib.distribucio.core.api.service.BustiaService;
import es.caib.distribucio.core.api.service.ReglaService;
import es.caib.distribucio.core.api.service.ws.BustiaV1WsService;
import es.caib.distribucio.core.helper.IntegracioHelper;
import es.caib.distribucio.core.helper.RegistreHelper;
import es.caib.plugins.arxiu.api.ContingutOrigen;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import es.caib.plugins.arxiu.api.DocumentTipus;
import es.caib.plugins.arxiu.api.FirmaPerfil;
import es.caib.plugins.arxiu.api.FirmaTipus;
/**
 * Implementació dels mètodes per al servei d'enviament de
 * continguts a bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@WebService(
		name = "BustiaV1",
		serviceName = "BustiaV1Service",
		portName = "BustiaV1ServicePort",
		endpointInterface = "es.caib.distribucio.core.api.service.ws.BustiaV1WsService",
		targetNamespace = "http://www.caib.es/distribucio/ws/v1/bustia")
public class BustiaV1WsServiceImpl implements BustiaV1WsService {

	@Resource
	private ReglaService reglaService;
	@Resource
	private BustiaService bustiaService;
	@Resource
	private IntegracioHelper integracioHelper;
	@Resource
	private RegistreHelper registreHelper;



	@Override
	public void enviarAnotacioRegistreEntrada(
			String entitat,
			String unitatAdministrativa,
			RegistreAnotacio registreEntrada) {
		
		String registreNumero = (registreEntrada != null) ? registreEntrada.getIdentificador() : null;

		//Dades pel monitor d'integracions
		String accioDescripcio = "Crida del WS d'Enviament d'anotació de registre d'entrada";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("entitat", entitat);
		accioParams.put("unitatAdministrativa", unitatAdministrativa);
		accioParams.put("numero", registreNumero);
		accioParams.put("tipusRegistre", RegistreTipusEnum.ENTRADA.toString());
		long t0 = System.currentTimeMillis();
		///
		
		try {
			logger.debug(
					"Processant enviament d'anotació de registre d'entrada al servei web de bústia (" +
					"entitat:" + entitat + ", " +
					"unitatAdministrativa:" + unitatAdministrativa + ", " +
					"numero:" + registreNumero + ")");
			
			validarAnotacioRegistre(registreEntrada);
			
			Long idRetornada = bustiaService.registreAnotacioCrear(
					entitat,
					RegistreTipusEnum.ENTRADA,
					unitatAdministrativa,
					registreEntrada);
			
			if (idRetornada != null)
				registreHelper.distribuirAnotacioPendent(idRetornada);
			
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_REGISTRE,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0);
		} catch (RuntimeException ex) {
			String errorDescripcio = "Error al cridar el WS d'Enviament d'anotació de registre d'entrada";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_REGISTRE,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw ex;
		}
	}

	@Override
	public void enviarDocument(
			@WebParam(name="entitat") String entitat,
			@WebParam(name="unitatAdministrativa") String unitatAdministrativa,
			@WebParam(name="referenciaDocument") String referenciaDocument) {
		logger.debug(
				"Processant enviament de document al servei web de bústia (" +
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
				"Processant enviament d'expedient al servei web de bústia (" +
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
		
		// Validació de format de camps
		validarFormatCampsRegistre(registreEntrada);
		
		// Validació d'annexos
		if (registreEntrada.getAnnexos() != null && registreEntrada.getAnnexos().size() > 0)
			for (RegistreAnnex annex : registreEntrada.getAnnexos())
				validarAnnex(annex);
		
		// Validació de precedència de justificant
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
		if (registreEntrada.getAssumpteTipusCodi() == null) {
			throw new ValidationException(
					"Es obligatori especificar un valor pel camp 'assumpteTipusCodi'");
		}
		if (registreEntrada.getIdiomaCodi() == null) {
			throw new ValidationException(
					"Es obligatori especificar un valor pel camp 'idiomaCodi'");
		}
	}
	
	private void validarFormatCampsRegistre(RegistreAnotacio registreEntrada) {
		
		if (registreEntrada.getJustificant() != null)
			validarFormatAnnex(registreEntrada.getJustificant());
		
		if (registreEntrada.getAnnexos() != null) {
			for (RegistreAnnex annex: registreEntrada.getAnnexos()) {
				validarFormatAnnex(annex);
			}
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
	    for (Enum<E> c : enumerat.getEnumConstants()) {
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

	/** Valida que l'annex:
	 * Tingui el nom informat.
	 * Valida les seves firmes.
	 * @param annex
	 * @throws ValidationException
	 */
	private void validarAnnex(RegistreAnnex annex) throws ValidationException{
		
		if (annex.getFitxerArxiuUuid() == null && annex.getFitxerContingut() == null)
			throw new ValidationException(
					"S'ha d'especificar o bé la referència del document o el contingut del document"
					+ " per l'annex [" + annex.getTitol() + "]");
		
		if (annex.getFitxerContingut() != null && annex.getFitxerNom() == null) {
			throw new ValidationException(
					"Es obligatori especificar un valor pel camp 'fitxerNom' per l'annex");
		}
		
		if (annex.getFirmes() != null && annex.getFirmes().size() > 0)
			for (Firma firma : annex.getFirmes())
				validaFirma(annex, firma);
	}

	/** Valida la firma. Valida:
	 * El tipus de firma ha d'estar reconegut.
	 * Si el tipus és TF04 l'annex ha de tenir el contingut informat.
	 * Si el tipus és TF05 la firma ha de tenir el contingut informat
	 * @param annex
	 * @param firma
	 */
	private void validaFirma(RegistreAnnex annex, Firma firma) {
		if (firma.getTipus() == null)
			throw new ValidationException(
					"Es obligatori especificar un valor pel camp 'tipus' de la firma");
		DocumentNtiTipoFirmaEnumDto firmaTipus = null;
		try {
			firmaTipus = DocumentNtiTipoFirmaEnumDto.valueOf(firma.getTipus());
		} catch(Exception e) {
			throw new ValidationException(
					"El tipus de firma '" + firma.getTipus() + "' no es reconeix com a vàlid.");
		}
		// Validacions segons el tipus de firma
		if (DocumentNtiTipoFirmaEnumDto.TF04.equals(firmaTipus)) {
			if (annex.getFitxerContingut() == null)
				throw new ValidationException(
						"El contingut de l'annex ha d'estar informat quan conté una firma del tipus TF04");
			if (firma.getContingut() == null)
				throw new ValidationException(
						"El contingut de la firma ha d'estar informat pel tipus TF04");
		} else if (DocumentNtiTipoFirmaEnumDto.TF05.equals(firmaTipus)) {
			if (firma.getContingut() != null)
				throw new ValidationException(
						"El tipus de firma TF05 (CADES ATTACHED) no permet contingut en la firma");
		}		
	}

	private static final Logger logger = LoggerFactory.getLogger(BustiaV1WsServiceImpl.class);

}
