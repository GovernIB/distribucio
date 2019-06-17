package es.caib.distribucio.core.service.ws.backoffice;

import java.util.List;

import javax.jws.WebService;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.core.api.service.ws.backoffice.BackofficeIntegracioWsService;
import es.caib.distribucio.core.api.service.ws.backoffice.BackofficeWsService;
import es.caib.distribucio.core.api.service.ws.backoffice.Estat;
import es.caib.distribucio.core.helper.PropertiesHelper;
import es.caib.distribucio.core.helper.RegistreHelper;
import es.caib.distribucio.core.helper.WsClientHelper;

/**
 * Implementació dels mètodes per al servei de backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@WebService(
		name = "Backoffice",
		serviceName = "BackofficeService",
		portName = "BackofficeServicePort",
		endpointInterface = "es.caib.ripea.core.api.service.ws.BackofficeWsServiceBean",
		targetNamespace = "http://www.caib.es/distribucio/ws/backoffice")
public class BackofficeWsServiceImpl implements BackofficeWsService {

	@Override
	public void comunicarAnotacionsPendents(List<AnotacioRegistreId> ids) {

		try {
			logger.debug("Ids rebuts al backoffice : ");
			for (AnotacioRegistreId id : ids) {
				logger.debug("indetificador: " + id.getIndetificador() + ", clauAcces: " + id.getClauAcces());
			}

			String url = PropertiesHelper.getProperties().getProperty(
					"es.caib.distribucio.backoffice.test.backofficeIntegracio.url");
			String usuari = PropertiesHelper.getProperties().getProperty(
					"es.caib.distribucio.backoffice.test.backofficeIntegracio.usuari");
			String contrasenya = PropertiesHelper.getProperties().getProperty(
					"es.caib.distribucio.backoffice.test.backofficeIntegracio.contrasenya");

			if (url != null && usuari != null && contrasenya != null) {

				for (AnotacioRegistreId id : ids) {

					logger.debug(">>> Abans de crear backofficeIntegracio WS");
					BackofficeIntegracioWsService backofficeClient = new WsClientHelper<BackofficeIntegracioWsService>().generarClientWs(
							getClass().getResource(
									"/es/caib/distribucio/core/service/ws/backoffice/backofficeIntegracio.wsdl"),
							url,
							new QName(
									"http://www.caib.es/distribucio/ws/backofficeIntegracio",
									"BackofficeIntegracioService"),
							usuari,
							contrasenya,
							null,
							BackofficeIntegracioWsService.class);

					logger.debug(">>> Abans de cridar backofficeIntegracio WS");
					backofficeClient.canviEstat(id,
							Estat.REBUDA,
							"Canviar l'estat a rebuda");
					logger.debug(">>> Despres de cridar backofficeIntegracio WS");
				}
			}
		} catch (Throwable ex) {
			logger.error("Error al recibir anotacions al test backoffice");
			throw new RuntimeException(ex);
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(RegistreHelper.class);
}
