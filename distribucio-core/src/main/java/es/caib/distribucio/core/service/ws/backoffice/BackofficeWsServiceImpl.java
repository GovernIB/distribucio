package es.caib.distribucio.core.service.ws.backoffice;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.CreateException;
import javax.jws.WebService;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import javax.naming.NamingException;
import javax.xml.namespace.QName;

import org.jboss.mx.util.MBeanProxyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.caib.distribucio.backoffice.utils.BackofficeUtilsImpl;
import es.caib.distribucio.core.api.dto.ExpedientEstatEnumDto;
import es.caib.distribucio.core.api.exception.ValidationException;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.core.api.service.ws.backoffice.BackofficeIntegracioWsService;
import es.caib.distribucio.core.api.service.ws.backoffice.BackofficeWsService;
import es.caib.distribucio.core.api.service.ws.backoffice.Estat;
import es.caib.distribucio.core.helper.PropertiesHelper;
import es.caib.distribucio.core.helper.RegistreHelper;
import es.caib.distribucio.core.helper.WsClientHelper;
import es.caib.distribucio.plugin.SistemaExternException;
import es.caib.loginModule.client.AuthenticationFailureException;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.ExpedientEstat;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import es.caib.plugins.arxiu.caib.ArxiuPluginCaib;

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
			
			
			BackofficeIntegracioWsService backofficeClient = getBackofficeIntergracioWsClient();

			
			for (AnotacioRegistreId id : ids) {
				
				AnotacioRegistreEntrada anotacioRegistreEntrada = backofficeClient.consulta(id);
				backofficeClient.canviEstat(id,
						Estat.REBUDA,
						"Canviar l'estat a rebuda");
				
				
				
				Expedient expedient = toArxiuExpedient(
						null,
						anotacioRegistreEntrada.getIdentificador(),
						null,
						Arrays.asList("A04019281"),
						new Date(),
						"000000",
						ExpedientEstatEnumDto.OBERT,
						null,
						"S0001");
				
				BackofficeUtilsImpl backofficeUtilsImpl = new BackofficeUtilsImpl(new ArxiuPluginCaib());
				backofficeUtilsImpl.crearExpedientAmbAnotacioRegistre(expedient, anotacioRegistreEntrada);

				

			}
			
			
			
			
		} catch (Throwable ex) {
			logger.error("Error al recibir anotacions al test backoffice");
			throw new RuntimeException(ex);
		}
	}


	
	
	private Expedient toArxiuExpedient(
			String identificador,
			String nom,
			String ntiIdentificador,
			List<String> ntiOrgans,
			Date ntiDataObertura,
			String ntiClassificacio,
			ExpedientEstatEnumDto ntiEstat,
			List<String> ntiInteressats,
			String serieDocumental) {
		Expedient expedient = new Expedient();
		expedient.setNom(nom);
		expedient.setIdentificador(identificador);
		ExpedientMetadades metadades = new ExpedientMetadades();
		metadades.setIdentificador(ntiIdentificador);
		metadades.setDataObertura(ntiDataObertura);
		metadades.setClassificacio(ntiClassificacio);
		if (ntiEstat != null) {
			switch (ntiEstat) {
			case OBERT:
				metadades.setEstat(ExpedientEstat.OBERT);
				break;
			case TANCAT:
				metadades.setEstat(ExpedientEstat.TANCAT);
				break;
			case INDEX_REMISSIO:
				metadades.setEstat(ExpedientEstat.INDEX_REMISSIO);
				break;
			}
		}
		metadades.setOrgans(ntiOrgans);
		metadades.setInteressats(ntiInteressats);
		metadades.setSerieDocumental(serieDocumental);
		expedient.setMetadades(metadades);
		return expedient;
	}
	
	
	private BackofficeIntegracioWsService getBackofficeIntergracioWsClient() throws Exception {

		String url = PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.backoffice.test.backofficeIntegracio.url");
		String usuari = PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.backoffice.test.backofficeIntegracio.usuari");
		String contrasenya = PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.backoffice.test.backofficeIntegracio.contrasenya");

		BackofficeIntegracioWsService backofficeClient = null;

		if (url != null && usuari != null && contrasenya != null) {
			// create ws client
			logger.debug(">>> Abans de crear backofficeIntegracio WS");
			backofficeClient = new WsClientHelper<BackofficeIntegracioWsService>().generarClientWs(
					getClass().getResource("/es/caib/distribucio/core/service/ws/backoffice/backofficeIntegracio.wsdl"),
					url,
					new QName("http://www.caib.es/distribucio/ws/backofficeIntegracio", "BackofficeIntegracioService"),
					usuari,
					contrasenya,
					null,
					BackofficeIntegracioWsService.class);

		}
		return backofficeClient;
	}

	private static final Logger logger = LoggerFactory.getLogger(RegistreHelper.class);
}
