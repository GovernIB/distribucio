package es.caib.distribucio.logic.intf.service;

import java.time.LocalDate;
import java.util.List;

import es.caib.comanda.ms.estadistica.model.DimensioDesc;
import es.caib.comanda.ms.estadistica.model.IndicadorDesc;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;

public interface EstadisticaService {

	public List<DimensioDesc> getDimensions();

	public List<IndicadorDesc> getIndicadors();

	public RegistresEstadistics consultaUltimesEstadistiques();

	public RegistresEstadistics consultaEstadistiques(LocalDate localData);

	public List<RegistresEstadistics> consultaEstadistiques(LocalDate startLocalData, LocalDate endLocalData);

}
