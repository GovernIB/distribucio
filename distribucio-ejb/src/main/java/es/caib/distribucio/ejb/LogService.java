package es.caib.distribucio.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.comanda.model.server.monitoring.FitxerContingut;
import es.caib.comanda.model.server.monitoring.FitxerInfo;
import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import java.util.concurrent.BlockingQueue;

import es.caib.distribucio.logic.intf.config.BaseConfig;

/**
 * Implementació de HistoricService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class LogService extends AbstractService<es.caib.distribucio.logic.intf.service.LogService> implements es.caib.distribucio.logic.intf.service.LogService {

    @Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
    public List<FitxerInfo> llistarFitxers() {
        return getDelegateService().llistarFitxers();
    }

    @Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
    public FitxerContingut getFitxerByNom(String nom) {
        return getDelegateService().getFitxerByNom(nom);
    }

    @Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
    public void tailLogFile(String filePath) {
        getDelegateService().tailLogFile(filePath);
    }

    @Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
    public BlockingQueue<String> getQueue() {
        return getDelegateService().getQueue();
    }

    @Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
    public List<String> readLastNLines(String nomFitxer, Long nLinies) {
        return getDelegateService().readLastNLines(nomFitxer, nLinies);
    }

}
