package es.caib.distribucio.ejb;

import java.util.Date;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.historic.HistoricDadesDto;
import es.caib.distribucio.logic.intf.dto.historic.HistoricFiltreDto;
import es.caib.distribucio.logic.intf.service.HistoricService;
import lombok.experimental.Delegate;

/**
 * Implementació de HistoricService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class HistoricServiceEjb extends AbstractServiceEjb<HistoricService> implements HistoricService {

	@Delegate
	private HistoricService delegateService = null;

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public HistoricDadesDto getDadesHistoriques(Long entitatId, HistoricFiltreDto filtre) {
		return delegateService.getDadesHistoriques(entitatId, filtre);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public void calcularDadesHistoriques(Date data) {
		delegateService.calcularDadesHistoriques(data);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public void recalcularTotals(Date data) {
		delegateService.recalcularTotals(data);
	}

	protected void setDelegateService(HistoricService delegateService) {
		this.delegateService = delegateService;
	}

}
