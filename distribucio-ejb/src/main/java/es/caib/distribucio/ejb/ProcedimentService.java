package es.caib.distribucio.ejb;

import java.util.List;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentFiltreDto;
import es.caib.distribucio.logic.intf.dto.UpdateProgressDto;

/**
 * Implementació de ProcedimentService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Stateless
public class ProcedimentService extends AbstractService<es.caib.distribucio.logic.intf.service.ProcedimentService> implements es.caib.distribucio.logic.intf.service.ProcedimentService {

	@Override
	public PaginaDto<ProcedimentDto> findAmbFiltre(Long entitatId, ProcedimentFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAmbFiltre(entitatId, filtre, paginacioParams);
	}

	@Override
	public void findAndUpdateProcediments(Long entitatId) throws Exception {
		getDelegateService().findAndUpdateProcediments(entitatId);
	}

	@Override
	public ProcedimentDto findAndUpdateProcediment(Long entitatId, String procedimentCodi) throws Exception {
		return getDelegateService().findAndUpdateProcediment(entitatId, procedimentCodi);
	}

	@Override
	public ProcedimentDto findByCodiSia(Long entitatId, String codiSia) {
		return getDelegateService().findByCodiSia(entitatId, codiSia);		
	}

	@Override
	public List<ProcedimentDto> findByNomOrCodiSia(Long entitatId, String nom) {
		return getDelegateService().findByNomOrCodiSia(entitatId, nom);
	}

	@Override
	public boolean isUpdatingProcediments(Long entitatId) {
		return getDelegateService().isUpdatingProcediments(entitatId);
	}

	@Override
	public UpdateProgressDto getProgresActualitzacio(Long entitatId) {
		return getDelegateService().getProgresActualitzacio(entitatId);
	}

}
