package es.caib.distribucio.core.ejb;

import java.util.Date;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.distribucio.core.api.dto.historic.HistoricDadesDto;
import es.caib.distribucio.core.api.dto.historic.HistoricFiltreDto;
import es.caib.distribucio.core.api.service.HistoricService;

/**
 * Implementació de HistoricService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class HistoricServiceBean implements HistoricService {

	@Autowired
	HistoricService delegate;

	@Override
	@RolesAllowed("DIS_ADMIN")
	public HistoricDadesDto getDadesHistoriques(Long entitatId, HistoricFiltreDto filtre) {
		return delegate.getDadesHistoriques(entitatId, filtre);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public void calcularDadesHistoriques(Date data) {
		delegate.calcularDadesHistoriques(data);		
	}

	
}
