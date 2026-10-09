package es.caib.distribucio.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.DominiDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.ResultatConsultaDto;
import es.caib.distribucio.logic.intf.dto.ResultatDominiDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;

/**
 * Implementació de DominiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class DominiService extends AbstractService<es.caib.distribucio.logic.intf.service.DominiService> implements es.caib.distribucio.logic.intf.service.DominiService {

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public DominiDto create(
			Long entitatId, 
			DominiDto tipusDocumental) throws NotFoundException {
		return getDelegateService().create(
				entitatId, 
				tipusDocumental);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public DominiDto update(
			Long entitatId, 
			DominiDto tipusDocumental) throws NotFoundException {
		return getDelegateService().update(
				entitatId,
				tipusDocumental);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public DominiDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		return getDelegateService().delete(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public DominiDto findById(
			Long entitatId, 
			Long id) throws NotFoundException {
		return getDelegateService().findById(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public PaginaDto<DominiDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		return getDelegateService().findByEntitatPaginat(
				entitatId, 
				paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<DominiDto> findByEntitat(
			Long entitatId) throws NotFoundException {
		return getDelegateService().findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public DominiDto findByCodiAndEntitat(String codi, Long entitatId) throws NotFoundException {
		return getDelegateService().findByCodiAndEntitat(codi, entitatId);
	}

	@Override
	@RolesAllowed("**")
	public ResultatDominiDto getResultDomini(Long entitatId, DominiDto domini, String filter, int page, int resultCount)
			throws NotFoundException {
		return getDelegateService().getResultDomini(entitatId, domini, filter, page, resultCount);
	}

	@Override
	@RolesAllowed("**")
	public ResultatConsultaDto getSelectedDomini(Long entitatId, DominiDto domini, String dadaValor)
			throws NotFoundException {
		return getDelegateService().getSelectedDomini(entitatId, domini, dadaValor);
	}

	@Override
	@RolesAllowed("**")
	public List<DominiDto> findByEntitatPermisLecturaAndTipusDomini(Long entitatId) {
		return getDelegateService().findByEntitatPermisLecturaAndTipusDomini(entitatId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public void evictDominiCache() {
		getDelegateService().evictDominiCache();
	}

}
