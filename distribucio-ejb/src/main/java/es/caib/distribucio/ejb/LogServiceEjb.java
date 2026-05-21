package es.caib.distribucio.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.comanda.model.server.monitoring.FitxerContingut;
import es.caib.comanda.model.server.monitoring.FitxerInfo;
import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import lombok.experimental.Delegate;
import java.util.concurrent.BlockingQueue;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.service.LogService;

/**
 * Implementació de HistoricService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class LogServiceEjb extends AbstractServiceEjb<LogService> implements LogService {

	@Delegate
	private LogService delegateService = null;

    @Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
    public List<FitxerInfo> llistarFitxers() {
        return delegateService.llistarFitxers();
    }

    @Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
    public FitxerContingut getFitxerByNom(String nom) {
        return delegateService.getFitxerByNom(nom);
    }

    @Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
    public void tailLogFile(String filePath) {
        delegateService.tailLogFile(filePath);
    }

    @Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
    public BlockingQueue<String> getQueue() {
        return delegateService.getQueue();
    }

    @Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
    public List<String> readLastNLines(String nomFitxer, Long nLinies) {
        return delegateService.readLastNLines(nomFitxer, nLinies);
    }

	protected void setDelegateService(LogService delegateService) {
		this.delegateService = delegateService;
	}

}
