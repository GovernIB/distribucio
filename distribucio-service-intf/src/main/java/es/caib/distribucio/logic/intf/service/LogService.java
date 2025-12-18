package es.caib.distribucio.logic.intf.service;

import es.caib.comanda.model.v1.log.FitxerContingut;
import es.caib.comanda.model.v1.log.FitxerInfo;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public interface LogService {

    @PreAuthorize("hasRole('DIS_COM')")
    List<FitxerInfo> llistarFitxers();

    @PreAuthorize("hasRole('DIS_COM')")
    FitxerContingut getFitxerByNom(String nom);

    @PreAuthorize("hasRole('DIS_COM')")
    void tailLogFile(String filePath);

    @PreAuthorize("hasRole('DIS_COM')")
    BlockingQueue<String> getQueue();

    @PreAuthorize("hasRole('DIS_COM')")
    List<String> readLastNLines(String nomFitxer, Long nLinies);
}
