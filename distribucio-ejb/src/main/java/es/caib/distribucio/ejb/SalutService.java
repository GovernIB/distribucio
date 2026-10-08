package es.caib.distribucio.ejb;

import java.util.List;

import javax.ejb.Stateless;

import org.springframework.context.annotation.Primary;

import es.caib.comanda.model.server.monitoring.ContextInfo;
import es.caib.comanda.model.server.monitoring.IntegracioInfo;
import es.caib.comanda.model.server.monitoring.SalutInfo;
import es.caib.comanda.model.server.monitoring.SubsistemaInfo;
import es.caib.distribucio.ejb.base.AbstractServiceEjb;

/**
 * Implementació de ReglaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Primary
@Stateless
public class SalutService extends AbstractService<es.caib.distribucio.logic.intf.service.SalutService> implements es.caib.distribucio.logic.intf.service.SalutService {

	
    @Override
    public List<IntegracioInfo> getIntegracions() {
        return getDelegateService().getIntegracions();
    }

    @Override
    public List<SubsistemaInfo> getSubsistemes() {
        return getDelegateService().getSubsistemes();
    }

    @Override
    public SalutInfo checkSalut(String versio, String performanceUrl) {
        return getDelegateService().checkSalut(versio, performanceUrl);
    }

    @Override
    public List<ContextInfo> getContexts(String baseUrl) {
        return getDelegateService().getContexts(baseUrl);
    }

}
