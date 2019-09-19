package es.caib.distribucio.core.service.ws.backoffice;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

import es.caib.distribucio.backoffice.utils.ArxiuResultat;
import es.caib.distribucio.backoffice.utils.BackofficeUtilsImpl;
import es.caib.distribucio.backoffice.utils.DistribucioArxiuError;
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
import es.caib.distribucio.plugin.gesdoc.GestioDocumentalArxiu;
import es.caib.loginModule.client.AuthenticationFailureException;
import es.caib.plugins.arxiu.api.ContingutArxiu;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.ExpedientEstat;
import es.caib.plugins.arxiu.api.ExpedientMetadades;
import es.caib.plugins.arxiu.api.IArxiuPlugin;
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

	private IArxiuPlugin arxiuPlugin;
	private String integracioArxiuCodi = "ARXIU";
	
	@Override
	public void comunicarAnotacionsPendents(List<AnotacioRegistreId> ids) {

		BackofficeIntegracioWsService backofficeClient = getBackofficeIntergracioWsClient();
			

		try {
			
			logger.debug("Ids rebuts al backoffice : ");
			for (AnotacioRegistreId id : ids) {
				logger.debug("indetificador: " + id.getIndetificador() + ", clauAcces: " + id.getClauAcces());
			}
			
			
			for (AnotacioRegistreId id : ids) {
				
				AnotacioRegistreEntrada anotacioRegistreEntrada = backofficeClient.consulta(id);
				backofficeClient.canviEstat(id,
						Estat.REBUDA,
						"Canviar l'estat a rebuda");
				
				String backofficeWsServiceImplserieDocuemntal="S0002";
				
				BackofficeUtilsImpl backofficeUtilsImpl = new BackofficeUtilsImpl(getArxiuPlugin());
				ArxiuResultat arxiuResultat = backofficeUtilsImpl.crearExpedientAmbAnotacioRegistre(
						null,
						anotacioRegistreEntrada.getIdentificador(),
						null,
						Arrays.asList("A04019281"),
						new Date(),
						"000000",
						ExpedientEstatEnumDto.OBERT,
						null,
						backofficeWsServiceImplserieDocuemntal,
						anotacioRegistreEntrada);
				
				
				if (arxiuResultat.getErrorCodi() == DistribucioArxiuError.ARXIU_ERROR) {
					backofficeClient.canviEstat(id,
							Estat.ERROR,
							"ArxiuResultat: Error!");
				}
				
				
				ArxiuResultat arxiuResultatSecondCall = backofficeUtilsImpl.crearExpedientAmbAnotacioRegistre(
						arxiuResultat.getIdentificadorExpedient(),
						anotacioRegistreEntrada.getIdentificador(),
						null,
						Arrays.asList("A04019281"),
						new Date(),
						"000000",
						ExpedientEstatEnumDto.OBERT,
						null,
						backofficeWsServiceImplserieDocuemntal,
						anotacioRegistreEntrada);
				
				
				Expedient expedientDetalls = getArxiuPlugin().expedientDetalls(arxiuResultat.getIdentificadorExpedient(), null);
				
				List<ContingutArxiu> continguts = expedientDetalls.getContinguts();
				
				logger.info("Uuid of expedient created in arxiu: "+ arxiuResultat.getIdentificadorExpedient());
				
				backofficeClient.canviEstat(id,
						Estat.PROCESSADA,
						"Canviar l'estat a rebuda");
				
			}
			
			
		} catch (Throwable ex) {
			
			for (AnotacioRegistreId id : ids){
				backofficeClient.canviEstat(id,
						Estat.ERROR,
						"Error!");
			}
			
			logger.error("Error al processar anotacions al test backoffice");
			throw new RuntimeException(ex);
		}
	}


	
	
	private IArxiuPlugin getArxiuPlugin() throws SistemaExternException {
		if (arxiuPlugin == null) {
			String pluginClass = getPropertyPluginArxiu();
			if (pluginClass != null && pluginClass.length() > 0) {
				try {
					Class<?> clazz = Class.forName(pluginClass);
					if (PropertiesHelper.getProperties().isLlegirSystem()) {
						arxiuPlugin = (IArxiuPlugin)clazz.getDeclaredConstructor(
								String.class).newInstance(
								"es.caib.distribucio.");
					} else {
						arxiuPlugin = (IArxiuPlugin)clazz.getDeclaredConstructor(
								String.class,
								Properties.class).newInstance(
								"es.caib.distribucio.",
								PropertiesHelper.getProperties().findAll());
					}
				} catch (Exception ex) {
					throw new SistemaExternException(
							integracioArxiuCodi,
							"Error al crear la instància del plugin d'arxiu digital",
							ex);
				}
			} else {
				throw new SistemaExternException(
						integracioArxiuCodi,
						"No està configurada la classe per al plugin d'arxiu digital");
			}
		}
		return arxiuPlugin;
	}
	private String getPropertyPluginArxiu() {
		return PropertiesHelper.getProperties().getProperty(
				"es.caib.distribucio.plugin.arxiu.class");
	}
	

	
	
	private BackofficeIntegracioWsService getBackofficeIntergracioWsClient(){

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
			try {
				backofficeClient = new WsClientHelper<BackofficeIntegracioWsService>().generarClientWs(
						getClass().getResource("/es/caib/distribucio/core/service/ws/backoffice/backofficeIntegracio.wsdl"),
						url,
						new QName("http://www.caib.es/distribucio/ws/backofficeIntegracio", "BackofficeIntegracioService"),
						usuari,
						contrasenya,
						null,
						BackofficeIntegracioWsService.class);
			} catch (Exception e) {
				throw new RuntimeException();
			}

		}
		return backofficeClient;
	}

	private static final Logger logger = LoggerFactory.getLogger(RegistreHelper.class);
}
