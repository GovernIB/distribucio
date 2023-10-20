package es.caib.distribucio.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.dto.DominiDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.ResultatConsultaDto;
import es.caib.distribucio.logic.intf.dto.ResultatDominiDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.service.DominiService;
import lombok.experimental.Delegate;

/**
 * Implementació de DominiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class DominiServiceEjb extends AbstractServiceEjb<DominiService> implements DominiService {

	@Delegate
	private DominiService delegateService = null;

	@Override
	@RolesAllowed("DIS_ADMIN")
	public DominiDto create(
			Long entitatId, 
			DominiDto tipusDocumental) throws NotFoundException {
		return delegateService.create(
				entitatId, 
				tipusDocumental);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public DominiDto update(
			Long entitatId, 
			DominiDto tipusDocumental) throws NotFoundException {
		return delegateService.update(
				entitatId,
				tipusDocumental);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public DominiDto delete(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegateService.delete(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public DominiDto findById(
			Long entitatId, 
			Long id) throws NotFoundException {
		return delegateService.findById(
				entitatId, 
				id);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public PaginaDto<DominiDto> findByEntitatPaginat(
			Long entitatId,
			PaginacioParamsDto paginacioParams)
			throws NotFoundException {
		return delegateService.findByEntitatPaginat(
				entitatId, 
				paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<DominiDto> findByEntitat(
			Long entitatId) throws NotFoundException {
		return delegateService.findByEntitat(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public DominiDto findByCodiAndEntitat(String codi, Long entitatId) throws NotFoundException {
		return delegateService.findByCodiAndEntitat(codi, entitatId);
	}

	@Override
	@RolesAllowed("**")
	public ResultatDominiDto getResultDomini(Long entitatId, DominiDto domini, String filter, int page, int resultCount)
			throws NotFoundException {
		return delegateService.getResultDomini(entitatId, domini, filter, page, resultCount);
	}

	@Override
	@RolesAllowed("**")
	public ResultatConsultaDto getSelectedDomini(Long entitatId, DominiDto domini, String dadaValor)
			throws NotFoundException {
		return delegateService.getSelectedDomini(entitatId, domini, dadaValor);
	}

	@Override
	@RolesAllowed("**")
	public List<DominiDto> findByEntitatPermisLecturaAndTipusDomini(Long entitatId) {
		return delegateService.findByEntitatPermisLecturaAndTipusDomini(entitatId);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public void evictDominiCache() {
		delegateService.evictDominiCache();
	}

	protected void setDelegateService(DominiService delegateService) {
		this.delegateService = delegateService;
	}

}
