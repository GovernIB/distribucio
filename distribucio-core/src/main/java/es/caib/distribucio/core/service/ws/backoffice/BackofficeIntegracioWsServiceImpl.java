/**
 * 
 */
package es.caib.distribucio.core.service.ws.backoffice;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.core.api.service.ws.backoffice.BackofficeIntegracioWsService;
import es.caib.distribucio.core.api.service.ws.backoffice.Estat;

/**
 * Implementació dels mètodes per al servei d'enviament de
 * continguts a bústies.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
@WebService(
		name = "BackofficeIntegracio",
		serviceName = "BackofficeIntegracioService",
		portName = "BackofficeIntegracioServicePort",
		endpointInterface = "es.caib.distribucio.core.api.service.ws.BackofficeIntegracioWsServiceBean",
		targetNamespace = "http://www.caib.es/distribucio/ws/backofficeIntegracio")
public class BackofficeIntegracioWsServiceImpl implements BackofficeIntegracioWsService {


	@Resource
	private RegistreService registreService;


	@Override
	public AnotacioRegistreEntrada consulta(
		AnotacioRegistreId id) {

		try {
			return registreService.findOneForBackoffice(id);

		} catch (Exception ex) {
			logger.error(
					"Error al processar nou registre d'entrada en el servei web de backoffice integració (" + "id="
					+ id + ex);
			throw new RuntimeException(ex);
		}
	}
	
	
	@Override
	public void canviEstat(
			AnotacioRegistreId id,
			Estat estat,
			String observacions) {
		try {
			registreService.canviEstat(
					id,
					estat,
					observacions);
		} catch (Exception ex) {
			logger.error("Error al canviar estat de registre d'entrada en el servei web de backoffice integració (" + "id="
					+ id + ex);
			throw new RuntimeException(ex);
		}

		logger.debug("");
	}



	private static final Logger logger = LoggerFactory.getLogger(BackofficeIntegracioWsServiceImpl.class);


}
