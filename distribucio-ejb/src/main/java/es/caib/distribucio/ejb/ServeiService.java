package es.caib.distribucio.ejb;

import java.util.List;

import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.ServeiDto;
import es.caib.distribucio.logic.intf.dto.ServeiFiltreDto;
import es.caib.distribucio.logic.intf.dto.UpdateProgressDto;

/**
 * Implementació de ServeiService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */

@Stateless
public class ServeiService extends AbstractService<es.caib.distribucio.logic.intf.service.ServeiService> implements es.caib.distribucio.logic.intf.service.ServeiService {

	@Override
	public PaginaDto<ServeiDto> findAmbFiltre(Long entitatId, ServeiFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAmbFiltre(entitatId, filtre, paginacioParams);
	}

	@Override
    public ServeiDto findAndUpdateServei(Long entitatId, String serveiCodi) throws Exception {
		return getDelegateService().findAndUpdateServei(entitatId, serveiCodi);
	}

	@Override
	public void findAndUpdateServeis(Long entitatId) throws Exception {
		getDelegateService().findAndUpdateServeis(entitatId);
	}

	@Override
	public ServeiDto findByCodiSia(Long entitatId, String codiSia) {
		return getDelegateService().findByCodiSia(entitatId, codiSia);		
	}

	@Override
	public List<ServeiDto> findByNomOrCodiSia(Long entitatId, String nom) {
		return getDelegateService().findByNomOrCodiSia(entitatId, nom);
	}

	@Override
	public boolean isUpdatingServeis(Long entitatId) {
		return getDelegateService().isUpdatingServeis(entitatId);
	}

	@Override
	public UpdateProgressDto getProgresActualitzacio(Long entitatId) {
		return getDelegateService().getProgresActualitzacio(entitatId);
	}

}
