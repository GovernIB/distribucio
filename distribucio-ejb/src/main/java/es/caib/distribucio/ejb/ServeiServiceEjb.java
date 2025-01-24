package es.caib.distribucio.ejb;

import java.util.List;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.ServeiDto;
import es.caib.distribucio.logic.intf.dto.ServeiFiltreDto;
import es.caib.distribucio.logic.intf.dto.UpdateProgressDto;
import es.caib.distribucio.logic.intf.service.ServeiService;
import lombok.experimental.Delegate;

/**
 * Implementació de ServeiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Stateless
public class ServeiServiceEjb extends AbstractServiceEjb<ServeiService> implements ServeiService {

	@Delegate
	private ServeiService delegateService = null;

	@Override
	public PaginaDto<ServeiDto> findAmbFiltre(Long entitatId, ServeiFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findAmbFiltre(entitatId, filtre, paginacioParams);
	}

	@Override
	public void findAndUpdateServeis(Long entitatId) throws Exception {
		delegateService.findAndUpdateServeis(entitatId);
	}

	@Override
	public ServeiDto findByCodiSia(Long entitatId, String codiSia) {
		return delegateService.findByCodiSia(entitatId, codiSia);		
	}

	@Override
	public List<ServeiDto> findByNomOrCodiSia(Long entitatId, String nom) {
		return delegateService.findByNomOrCodiSia(entitatId, nom);
	}

	@Override
	public boolean isUpdatingServeis(Long entitatId) {
		return delegateService.isUpdatingServeis(entitatId);
	}

	@Override
	public UpdateProgressDto getProgresActualitzacio(Long entitatId) {
		return delegateService.getProgresActualitzacio(entitatId);
	}

	protected void setDelegateService(ServeiService delegateService) {
		this.delegateService = delegateService;
	}

}
