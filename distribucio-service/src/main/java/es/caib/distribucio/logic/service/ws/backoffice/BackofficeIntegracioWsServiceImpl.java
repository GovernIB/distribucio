/**
 *
 */
package es.caib.distribucio.logic.service.ws.backoffice;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.distribucio.logic.helper.IntegracioHelper;
import es.caib.distribucio.logic.helper.SubsistemesHelper;
import es.caib.distribucio.logic.helper.SubsistemesHelper.SubsistemesEnum;
import es.caib.distribucio.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.distribucio.logic.intf.exception.SistemaExternException;
import es.caib.distribucio.logic.intf.service.RegistreService;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.logic.intf.service.ws.backoffice.BackofficeIntegracioWsService;
import es.caib.distribucio.logic.intf.service.ws.backoffice.Estat;

/**
 * Implementació dels mètodes per al servei backoffice integracio
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class BackofficeIntegracioWsServiceImpl implements BackofficeIntegracioWsService {

    @Autowired
    private RegistreService registreService;
    @Autowired
    private IntegracioHelper integracioHelper;

    @Override
    public AnotacioRegistreEntrada consulta(
            AnotacioRegistreId id) {
        AnotacioRegistreEntrada anotacioRegistreEntrada;
        String accioDescripcio = "Consulta d'anotació pendent "+ id.getIndetificador();
        String usuariIntegracio = this.getUsuariIntegracio();
        Map<String, String> accioParams = new HashMap<String, String>();
        accioParams.put("Anotació identificador", id.getIndetificador());
        accioParams.put("Anotació clau accés", id.getClauAcces());
        long start = System.currentTimeMillis();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth!=null) {
            String usuariCodi = auth.getName();
            accioParams.put("Usuari", usuariCodi);
        }
        long t0 = System.currentTimeMillis();
        try {
            logger.trace(">>> Abans de cridar el servei de registre");
            anotacioRegistreEntrada =  registreService.findOneForBackoffice(id);
			// RegistreNumero no cal!!!
            integracioHelper.addAccioOk (
                    IntegracioHelper.INTCODI_BACKOFFICE,
                    accioDescripcio,
                    usuariIntegracio,
                    accioParams,
                    IntegracioAccioTipusEnumDto.RECEPCIO,
                    System.currentTimeMillis() - t0
            );
            logger.trace(">>> Despres de cridar el servei de registre");
            SubsistemesHelper.addSuccessOperation(SubsistemesEnum.BKC, System.currentTimeMillis() - start);
            return anotacioRegistreEntrada;
        } catch (Exception ex) {
            logger.error(
                    "Error al processar nou registre d'entrada en el servei web de backoffice integració (" + "id=" + id + ")",
                    ex);
            SubsistemesHelper.addErrorOperation(SubsistemesEnum.BKC);
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
	public List<AnotacioRegistreEntrada> llistar(String identificador, Date dataRegistre) {
		long start = System.currentTimeMillis();
		List<AnotacioRegistreEntrada> anotacionsRegistreEntrada;
		String accioDescripcio = "Consulta anotacions registre "+ identificador;
		String usuariIntegracio = this.getUsuariIntegracio();
		Map<String, String> accioParams = new HashMap<String, String>();
		
		accioParams.put("Número de registre", identificador);
		accioParams.put("Data de registre", dataRegistre.toString());
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth!=null) {
			String usuariCodi = auth.getName();
			accioParams.put("Usuari", usuariCodi);
		}
		
		long t0 = System.currentTimeMillis();
		
		try {
			
			logger.trace(">>> Abans de cridar el servei de consulta de registre");					
			
			anotacionsRegistreEntrada =  registreService.findForBackoffice(identificador, dataRegistre);
						
			integracioHelper.addAccioOk (
					IntegracioHelper.INTCODI_BACKOFFICE,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0
			);			
			logger.trace(">>> Despres de cridar el servei de consulta de registre");	

	        SubsistemesHelper.addSuccessOperation(SubsistemesEnum.BKL, System.currentTimeMillis() - start);
			return anotacionsRegistreEntrada;

		} catch (Exception ex) {
			logger.error(
					"Error al consultar registres d'entrada en el servei web de backoffice integració (" + "numeroRegistre="
					+ identificador + ex);			
			
			String errorDescripcio = "Error al consultar registres d'entrada en el servei web de backoffice integració";
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_BACKOFFICE,
					accioDescripcio,
					usuariIntegracio,
					accioParams,
					IntegracioAccioTipusEnumDto.RECEPCIO,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
	        SubsistemesHelper.addErrorOperation(SubsistemesEnum.BKL);
			throw new SistemaExternException(
					IntegracioHelper.INTCODI_BACKOFFICE,
					errorDescripcio,
					ex);
		}
	}
    
    @Override
    public void canviEstatComunicadaARebuda(
    		 AnotacioRegistreId id,             
             String observacions) {
		long start = System.currentTimeMillis();
    	try {
    		List<Long> registresId = registreService.findRegistresPerIdentificador(id);
			
			for (Long registreId : registresId) {
				registreService.canviEstatComunicadaARebuda(registreId, observacions);
			}
			SubsistemesHelper.addSuccessOperation(SubsistemesEnum.BKC, System.currentTimeMillis() - start);
    	} catch (Exception ex) {
    		logger.error("Error al canviar estat de registre d'entrada en el servei web de backoffice integració (" + "id="+ id + ex);
			SubsistemesHelper.addErrorOperation(SubsistemesEnum.BKC);
    	}
    }
    
    @Override
    public void canviEstat(
            AnotacioRegistreId id,
            Estat estat,
            String observacions) {
		long start = System.currentTimeMillis();
        String accioDescripcio = "Canvi d'estat de l'anotació " + (id != null ? id.getIndetificador() : "-") + " a " + estat;
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
            
			List<Long> registresId = registreService.findRegistresPerIdentificador(id);
			
			for (Long registreId : registresId) {
				registreService.canviEstat(registreId, estat, observacions);
			}

			// RegistreNumero no cal!!!
            integracioHelper.addAccioOk (
                    IntegracioHelper.INTCODI_BACKOFFICE,
                    accioDescripcio,
                    usuariIntegracio,
                    accioParams,
                    IntegracioAccioTipusEnumDto.RECEPCIO,
                    System.currentTimeMillis() - t0
            );
			SubsistemesHelper.addSuccessOperation(SubsistemesEnum.BKE, System.currentTimeMillis() - start);
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
			SubsistemesHelper.addErrorOperation(SubsistemesEnum.BKE);
            throw new SistemaExternException(
                    IntegracioHelper.INTCODI_BACKOFFICE,
                    errorDescripcio,
                    ex);
        }
        logger.debug("");
    }

    private String getUsuariIntegracio() {
        String usuari = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            usuari = auth.getName();
        }
        return usuari;
    }

    private static final Logger logger = LoggerFactory.getLogger(BackofficeIntegracioWsServiceImpl.class);

}