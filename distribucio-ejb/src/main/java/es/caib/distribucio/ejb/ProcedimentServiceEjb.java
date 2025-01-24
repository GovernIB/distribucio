package es.caib.distribucio.ejb;

import java.util.List;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentFiltreDto;
import es.caib.distribucio.logic.intf.dto.UpdateProgressDto;
import es.caib.distribucio.logic.intf.service.ProcedimentService;
import lombok.experimental.Delegate;

/**
 * Implementació de ProcedimentService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Stateless
public class ProcedimentServiceEjb extends AbstractServiceEjb<ProcedimentService> implements ProcedimentService {

	@Delegate
	private ProcedimentService delegateService = null;

	@Override
	public PaginaDto<ProcedimentDto> findAmbFiltre(Long entitatId, ProcedimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findAmbFiltre(entitatId, filtre, paginacioParams);
	}

	@Override
	public void findAndUpdateProcediments(Long entitatId) throws Exception {
		delegateService.findAndUpdateProcediments(entitatId);
	}

	@Override
	public ProcedimentDto findByCodiSia(Long entitatId, String codiSia) {
		return delegateService.findByCodiSia(entitatId, codiSia);		
	}

	@Override
	public List<ProcedimentDto> findByNomOrCodiSia(Long entitatId, String nom) {
		return delegateService.findByNomOrCodiSia(entitatId, nom);
	}

	@Override
	public boolean isUpdatingProcediments(Long entitatId) {
		return delegateService.isUpdatingProcediments(entitatId);
	}

	@Override
	public UpdateProgressDto getProgresActualitzacio(Long entitatId) {
		return delegateService.getProgresActualitzacio(entitatId);
	}

	protected void setDelegateService(ProcedimentService delegateService) {
		this.delegateService = delegateService;
	}

}
