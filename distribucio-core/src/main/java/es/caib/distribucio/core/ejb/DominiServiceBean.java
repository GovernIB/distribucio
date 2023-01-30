package es.caib.distribucio.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.distribucio.core.api.dto.DominiDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.ResultatConsultaDto;
import es.caib.distribucio.core.api.dto.ResultatDominiDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.service.DominiService;

/**
 * Implementació de DominiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class DominiServiceBean implements DominiService {

	@Autowired
	private DominiService delegate;
	
	@Override
	@RolesAllowed("DIS_ADMIN")
	public DominiDto create(
			Long entitatId, 
			DominiDto tipusDocumental) throws NotFoundException {
		return delegate.create(
				entitatId, 
				tipusDocumental);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public DominiDto update(
			Long entitatId, 
			DominiDto tipusDocumental) throws NotFoundException {
		return delegate.update(
				entitatId,
				tipusDocumental);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public DominiDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegate.delete(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public DominiDto findById(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegate.findById(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public PaginaDto<DominiDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		return delegate.findByEntitatPaginat(
				entitatId, 
				paginacioParams);
	}

	@Override
	@RolesAllowed({"DIS_ADMIN", "tothom"})
	public List<DominiDto> findByEntitat(
			Long entitatId) throws NotFoundException {
		return delegate.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed("tothom")
	public DominiDto findByCodiAndEntitat(String codi, Long entitatId) throws NotFoundException {
		return delegate.findByCodiAndEntitat(codi, entitatId);

	}

	@Override
	@RolesAllowed("tothom")
	public ResultatDominiDto getResultDomini(Long entitatId, DominiDto domini, String filter, int page, int resultCount)
			throws NotFoundException {
		return delegate.getResultDomini(entitatId, domini, filter, page, resultCount);
	}

	@Override
	@RolesAllowed("tothom")
	public ResultatConsultaDto getSelectedDomini(Long entitatId, DominiDto domini, String dadaValor)
			throws NotFoundException {
		return delegate.getSelectedDomini(entitatId, domini, dadaValor);
	}

	@Override
	@RolesAllowed("tothom")
	public List<DominiDto> findByEntitatPermisLecturaAndTipusDomini(Long entitatId) {
		return delegate.findByEntitatPermisLecturaAndTipusDomini(entitatId);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public void evictDominiCache() {
		delegate.evictDominiCache();
	}
}
