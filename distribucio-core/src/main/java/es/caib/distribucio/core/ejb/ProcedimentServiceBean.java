package es.caib.distribucio.core.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.ProcedimentDto;
import es.caib.distribucio.core.api.dto.ProcedimentFiltreDto;
import es.caib.distribucio.core.api.service.ProcedimentService;

/**
 * Implementació de ProcedimentService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ProcedimentServiceBean implements ProcedimentService {
	
	@Autowired
	private ProcedimentService delegate;

	@Override
	public PaginaDto<ProcedimentDto> findAmbFiltre(Long entitatId, ProcedimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltre(entitatId, filtre, paginacioParams);
	}

	@Override
	public void findAndUpdateProcediments(Long entitatId) throws Exception {
		delegate.findAndUpdateProcediments(entitatId);
	}

	@Override
	public ProcedimentDto findByCodiSia(Long entitatId, String codiSia) {
		return delegate.findByCodiSia(entitatId, codiSia);		
	}

	@Override
	public List<ProcedimentDto> findByNomOrCodiSia(Long entitatId, String nom) {
		return delegate.findByNomOrCodiSia(entitatId, nom);
	}

	@Override
	public List<ProcedimentDto> findByNom(Long entitatId, String nom) {
		return delegate.findByNom(entitatId, nom);
	}

}
