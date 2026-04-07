package es.caib.distribucio.ejb;

import java.time.LocalDate;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.comanda.model.server.monitoring.DimensioDesc;
import es.caib.comanda.model.server.monitoring.IndicadorDesc;
import es.caib.comanda.model.server.monitoring.RegistresEstadistics;
import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.service.EstadisticaService;
import lombok.experimental.Delegate;
import es.caib.distribucio.logic.intf.config.BaseConfig;

/**
 * Implementació de ReglaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class EstadisticaServiceEjb  extends AbstractServiceEjb<EstadisticaService> implements EstadisticaService {

	@Delegate
	private EstadisticaService delegateService = null;
	
	@Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
	public List<DimensioDesc> getDimensions() {
		return delegateService.getDimensions();
	}

	@Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
	public List<IndicadorDesc> getIndicadors() {
		return delegateService.getIndicadors();
	}

	@Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
	public RegistresEstadistics consultaUltimesEstadistiques() {
		return delegateService.consultaUltimesEstadistiques();
	}

	@Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
	public RegistresEstadistics consultaEstadistiques(LocalDate data) {
		return delegateService.consultaEstadistiques(data);
	}

	@Override
    @RolesAllowed(BaseConfig.ROLE_COMANDA)
	public List<RegistresEstadistics> consultaEstadistiques(LocalDate startDate, LocalDate endDate) {
		return delegateService.consultaEstadistiques(startDate, endDate);
	}

	@Override
	protected void setDelegateService(EstadisticaService delegateService) {
		this.delegateService = delegateService;
	}
}
