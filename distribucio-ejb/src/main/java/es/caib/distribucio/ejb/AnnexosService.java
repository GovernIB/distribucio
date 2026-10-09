package es.caib.distribucio.ejb;

import es.caib.distribucio.logic.intf.dto.*;
import es.caib.distribucio.logic.intf.exception.NotFoundException;

import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de AnnexosService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Stateless
public class AnnexosService extends AbstractService<es.caib.distribucio.logic.intf.service.AnnexosService> implements es.caib.distribucio.logic.intf.service.AnnexosService {

	@Override
	public PaginaDto<RegistreAnnexDto> findAdmin(
			Long entitatId,
			AnnexosFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		return getDelegateService().findAdmin(entitatId, filtre, paginacioParams);
	}
	
	@Override
	public List<Long> findAnnexIds(Long entitatId, AnnexosFiltreDto filtre) throws NotFoundException {
		return getDelegateService().findAnnexIds(entitatId, filtre);
	}
	
	@Override
	public ResultatAnnexDefinitiuDto guardarComADefinitiu(Long id) {
		return getDelegateService().guardarComADefinitiu(id);
	}

	@Override
	public List<Integer> findCopiesRegistre(String numero) {
		return getDelegateService().findCopiesRegistre(numero);
	}

	@Override
	public List<RegistreAnnexDto> findMultiple(
			Long entitatId,
			List<Long> multipleAnnexosIds,
			boolean isAdmin) throws NotFoundException {
		return getDelegateService().findMultiple(entitatId, multipleAnnexosIds, isAdmin);
	}

}
