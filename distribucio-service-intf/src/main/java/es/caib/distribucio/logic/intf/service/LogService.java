package es.caib.distribucio.logic.intf.service;

import es.caib.comanda.model.v1.log.FitxerContingut;
import es.caib.comanda.model.v1.log.FitxerInfo;
import es.caib.distribucio.logic.intf.config.BaseConfig;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public interface LogService {

    @PreAuthorize("hasRole('" + BaseConfig.ROLE_COMANDA + "')")
    public List<FitxerInfo> llistarFitxers();

    @PreAuthorize("hasRole('" + BaseConfig.ROLE_COMANDA + "')")
    public FitxerContingut getFitxerByNom(String nom);

    @PreAuthorize("hasRole('" + BaseConfig.ROLE_COMANDA + "')")
    public void tailLogFile(String filePath);

    @PreAuthorize("hasRole('" + BaseConfig.ROLE_COMANDA + "')")
    public BlockingQueue<String> getQueue();

    @PreAuthorize("hasRole('" + BaseConfig.ROLE_COMANDA + "')")
    public List<String> readLastNLines(String nomFitxer, Long nLinies);
}
