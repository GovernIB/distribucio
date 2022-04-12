/**
 * 
 */
package es.caib.distribucio.core.service.ws.backoffice;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.distribucio.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.core.api.dto.UsuariDto;
import es.caib.distribucio.core.api.exception.SistemaExternException;
import es.caib.distribucio.core.api.service.AplicacioService;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.core.api.service.ws.backoffice.BackofficeIntegracioWsService;
import es.caib.distribucio.core.api.service.ws.backoffice.Estat;
import es.caib.distribucio.core.helper.IntegracioHelper;

/**
 * Implementació dels mètodes per al servei backoffice integracio
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

	@Autowired
	private IntegracioHelper integracioHelper;
	
	@Resource
	private RegistreService registreService;
	
	@Autowired
	private AplicacioService aplicacioService;
	
	@Override
	public AnotacioRegistreEntrada consulta(
		AnotacioRegistreId id) {

		AnotacioRegistreEntrada anotacioRegistreEntrada;
		String accioDescripcio = "Consulta d'anotació pendent";
//		String usuariIntegracio = "Obtenir l'usuari integracio";
		String usuariIntegracio = this.getUsuariIntegracio();
		Map<String, String> accioParams = new HashMap<String, String>();
		
		accioParams.put("Anotació identificador", id.getIndetificador());
		accioParams.put("Anotació clau accés", id.getClauAcces());
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth!=null) {
			String usuariCodi = auth.getName();
			accioParams.put("Usuari", usuariCodi);
		}
		
		long t0 = System.currentTimeMillis();
		
		try {
			
			logger.trace(">>> Abans de cridar el servei de registre");					
			
			anotacioRegistreEntrada =  registreService.findOneForBackoffice(id);
			
			integracioHelper.addAccioOk (
					IntegracioHelper.INTCODI_BACKOFFICE,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0
			);			
			logger.trace(">>> Despres de cridar el servei de registre");	
			
			return anotacioRegistreEntrada;

		} catch (Exception ex) {
			logger.error(
					"Error al processar nou registre d'entrada en el servei web de backoffice integració (" + "id="
					+ id + ex);			
			
			String errorDescripcio = "Error  al processar nou registre d'entrada en el servei web de backoffice integració";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_BACKOFFICE,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_BACKOFFICE,
					errorDescripcio,
					ex);
		}
	}
	
	
	@Override
	public void canviEstat(
			AnotacioRegistreId id,
			Estat estat,
			String observacions) {
		
		String accioDescripcio = "Canvi d'estat";
//		String usuariIntegracio = "Obtenir l'usuari integracio";
		String usuariIntegracio = this.getUsuariIntegracio();
		Map<String, String> accioParams = new HashMap<String, String>();
		
		accioParams.put("Anotació identificador", id.getIndetificador());
		accioParams.put("Anotació clau accés", id.getClauAcces());
		accioParams.put("Estat", estat.name());
		accioParams.put("Observacions", observacions);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth!=null) {
			String usuariCodi = auth.getName();
			accioParams.put("Usuari", usuariCodi);
		}
		
		long t0 = System.currentTimeMillis();
		
		try {
			
			logger.trace(">>> Abans de cridar el servei de canvi d'estat");			
			
			registreService.canviEstat(id, estat, observacions);
			
			integracioHelper.addAccioOk (
					IntegracioHelper.INTCODI_BACKOFFICE,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0
			);		
			
			logger.trace(">>> Despres de cridar el servei de canvi d'estat");	
			
			
		} catch (Exception ex) {
			logger.error("Error al canviar estat de registre d'entrada en el servei web de backoffice integració (" + "id="
					+ id + ex);
			
			String errorDescripcio = "Error  al canviar estat de registre d'entrada en el servei web de backoffice integració";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_BACKOFFICE,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_BACKOFFICE,
					errorDescripcio,
					ex);	
			
		}

		logger.debug("");
	}

	private String getUsuariIntegracio() {
		UsuariDto usuariDto =  aplicacioService.getUsuariActual();
		return usuariDto.getCodi();
	}

	private static final Logger logger = LoggerFactory.getLogger(BackofficeIntegracioWsServiceImpl.class);


}
