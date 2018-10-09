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
import es.caib.distribucio.core.api.service.RegistreService;
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
	@Resource
	private RegistreService registreService;



	@Override
	public void enviarAnotacioRegistreEntrada(
			String entitat,
			String unitatAdministrativa,
			RegistreAnotacio registreEntrada) {
		
		registreService.enviarAnotacioRegistreEntrada(entitat, unitatAdministrativa, registreEntrada);
		
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




	

	

	

	




	private static final Logger logger = LoggerFactory.getLogger(BustiaV1WsServiceImpl.class);

}
