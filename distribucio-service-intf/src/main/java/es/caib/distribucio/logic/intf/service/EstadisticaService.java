package es.caib.distribucio.logic.intf.service;

import java.time.LocalDate;
import java.util.List;

import es.caib.comanda.model.v1.estadistica.DimensioDesc;
import es.caib.comanda.model.v1.estadistica.IndicadorDesc;
import es.caib.comanda.model.v1.estadistica.RegistresEstadistics;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import org.springframework.security.access.prepost.PreAuthorize;

public interface EstadisticaService {

	@PreAuthorize("hasRole('DIS_COM')")
    public List<DimensioDesc> getDimensions();

	@PreAuthorize("hasRole('DIS_COM')")
    public List<IndicadorDesc> getIndicadors();

	@PreAuthorize("hasRole('DIS_COM')")
    public RegistresEstadistics consultaUltimesEstadistiques();

	@PreAuthorize("hasRole('DIS_COM')")
    public RegistresEstadistics consultaEstadistiques(LocalDate localData);

	@PreAuthorize("hasRole('DIS_COM')")
    public List<RegistresEstadistics> consultaEstadistiques(LocalDate startLocalData, LocalDate endLocalData);

}
