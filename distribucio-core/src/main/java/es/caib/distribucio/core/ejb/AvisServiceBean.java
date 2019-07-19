/**
 * 
 */
package es.caib.distribucio.core.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;
import org.springframework.security.access.prepost.PreAuthorize;

import es.caib.distribucio.core.api.dto.AvisDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.service.AvisService;

/**
 * Implementació de AvisService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class AvisServiceBean implements AvisService {

	@Autowired
	AvisService delegate;

	@Override
	@PreAuthorize("hasRole('tothom')")
	public AvisDto create(AvisDto avis) {
		return delegate.create(avis);
	}

	@Override
	@PreAuthorize("hasRole('tothom')")
	public AvisDto update(AvisDto avis) {
		return delegate.update(avis);
	}

	@Override
	@PreAuthorize("hasRole('tothom')")
	public AvisDto updateActiva(Long id, boolean activa) {
		return delegate.updateActiva(id, activa);
	}

	@Override
	@PreAuthorize("hasRole('tothom')")
	public AvisDto delete(Long id) {
		return delegate.delete(id);
	}

	@Override
	@PreAuthorize("hasRole('tothom')")
	public AvisDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@PreAuthorize("hasRole('tothom')")
	public PaginaDto<AvisDto> findPaginat(PaginacioParamsDto paginacioParams) {
		return delegate.findPaginat(paginacioParams);
	}

	@Override
	@PreAuthorize("hasRole('tothom')")
	public List<AvisDto> findActive() {
		return delegate.findActive();
	}

}
