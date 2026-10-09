package es.caib.distribucio.ejb;

import es.caib.comanda.model.server.monitoring.DimensioDesc;
import es.caib.comanda.model.server.monitoring.IndicadorDesc;
import es.caib.comanda.model.server.monitoring.RegistresEstadistics;
import es.caib.distribucio.logic.intf.config.BaseConfig;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementació de ReglaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class EstadisticaService extends AbstractService<es.caib.distribucio.logic.intf.service.EstadisticaService> implements es.caib.distribucio.logic.intf.service.EstadisticaService {

	
	@Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
	public List<DimensioDesc> getDimensions() {
		return getDelegateService().getDimensions();
	}

	@Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
	public List<IndicadorDesc> getIndicadors() {
		return getDelegateService().getIndicadors();
	}

	@Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
	public RegistresEstadistics consultaUltimesEstadistiques() {
		return getDelegateService().consultaUltimesEstadistiques();
	}

	@Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
	public RegistresEstadistics consultaEstadistiques(LocalDate data) {
		return getDelegateService().consultaEstadistiques(data);
	}

	@Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
	public List<RegistresEstadistics> consultaEstadistiques(LocalDate startDate, LocalDate endDate) {
		return getDelegateService().consultaEstadistiques(startDate, endDate);
	}

}
