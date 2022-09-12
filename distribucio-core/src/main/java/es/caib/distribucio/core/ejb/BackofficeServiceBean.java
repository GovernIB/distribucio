package es.caib.distribucio.core.ejb;




import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.distribucio.core.api.dto.BackofficeDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.service.BackofficeService;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class BackofficeServiceBean implements BackofficeService {

    @Autowired
    private BackofficeService delegate;
    
    
    
	@Override
	@RolesAllowed("DIS_ADMIN")
	public BackofficeDto create(
			Long entitatId, 
			BackofficeDto tipusDocumental) throws NotFoundException {
		return delegate.create(
				entitatId, 
				tipusDocumental);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public BackofficeDto update(
			Long entitatId, 
			BackofficeDto tipusDocumental) throws NotFoundException {
		return delegate.update(
				entitatId,
				tipusDocumental);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public BackofficeDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegate.delete(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed({"DIS_ADMIN", "DIS_ADMIN_LECTURA", "DIS_REGLA"})
	public BackofficeDto findById(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegate.findById(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed({"DIS_ADMIN", "DIS_ADMIN_LECTURA"})
	public PaginaDto<BackofficeDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		return delegate.findByEntitatPaginat(
				entitatId, 
				paginacioParams);
	}

	@Override
	@RolesAllowed({"DIS_ADMIN", "DIS_ADMIN_LECTURA"})
	public List<BackofficeDto> findByEntitat(Long entitatId) throws NotFoundException {
		return delegate.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed({"DIS_ADMIN", "DIS_ADMIN_LECTURA", "DIS_REGLA"})
	public BackofficeDto findByCodi(
			Long entitatId,
			String backofficeCodi) throws NotFoundException {
		return delegate.findByCodi(entitatId, backofficeCodi);
	}


    
    
    
}
