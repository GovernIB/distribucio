package es.caib.distribucio.ejb;

import java.util.List;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.dto.AnnexosFiltreDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.RegistreAnnexDto;
import es.caib.distribucio.logic.intf.dto.ResultatAnnexDefinitiuDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.service.AnnexosService;
import lombok.experimental.Delegate;

/**
 * Implementació de AnnexosService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Stateless
public class AnnexosServiceEjb extends AbstractServiceEjb<AnnexosService> implements AnnexosService {

	@Delegate
	private AnnexosService delegateService = null;

	@Override
	public PaginaDto<RegistreAnnexDto> findAdmin(
			Long entitatId,
			AnnexosFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		return delegateService.findAdmin(entitatId, filtre, paginacioParams);
	}
	
	@Override
	public List<Long> findAnnexIds(Long entitatId, AnnexosFiltreDto filtre) throws NotFoundException {
		return delegateService.findAnnexIds(entitatId, filtre);
	}
	
	@Override
	public ResultatAnnexDefinitiuDto guardarComADefinitiu(Long id) {
		return delegateService.guardarComADefinitiu(id);
	}
	
	protected void setDelegateService(AnnexosService delegateService) {
		this.delegateService = delegateService;
	}

}
