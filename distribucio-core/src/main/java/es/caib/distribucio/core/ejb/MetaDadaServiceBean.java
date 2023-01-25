package es.caib.distribucio.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.distribucio.core.api.dto.MetaDadaDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.service.MetaDadaService;

/**
 * Implementació de MetaDadaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class MetaDadaServiceBean implements MetaDadaService {

	@Autowired
	private MetaDadaService delegate;

	@Override
	@RolesAllowed("DIS_ADMIN")
	public MetaDadaDto create(Long entitatId, MetaDadaDto metaDada) throws NotFoundException {
		return delegate.create(entitatId, metaDada);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public MetaDadaDto update(Long entitatId, MetaDadaDto metaDada) throws NotFoundException {
		return delegate.update(entitatId, metaDada);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public MetaDadaDto delete(Long entitatId, Long metaDadaId) throws NotFoundException {
		return delegate.delete(entitatId, metaDadaId);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public MetaDadaDto updateActiva(Long entitatId, Long metaDadaId, boolean activa) throws NotFoundException {
		return delegate.updateActiva(entitatId, metaDadaId, activa);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public void moveUp(Long entitatId, Long metaDadaId) throws NotFoundException {
		delegate.moveUp(entitatId, metaDadaId);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public void moveDown(Long entitatId, Long metaDadaId) throws NotFoundException {
		delegate.moveDown(entitatId, metaDadaId);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public void moveTo(Long entitatId, Long metaDadaId, int posicio) throws NotFoundException {
		delegate.moveTo(entitatId, metaDadaId, posicio); 
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public MetaDadaDto findById(Long entitatId, Long metaDadaId) throws NotFoundException {
		return delegate.findById(entitatId, metaDadaId);
	}

	@Override
	@RolesAllowed("tothom")
	public MetaDadaDto findByCodi(Long entitatId, String codi) throws NotFoundException {
		return delegate.findByCodi(entitatId, codi);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public PaginaDto<MetaDadaDto> findByEntitatPaginat(Long entitatId, PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		return delegate.findByEntitatPaginat(entitatId, paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public List<MetaDadaDto> findByEntitat(Long entitatId) {
		return delegate.findByEntitat(entitatId);
	}
	

}
