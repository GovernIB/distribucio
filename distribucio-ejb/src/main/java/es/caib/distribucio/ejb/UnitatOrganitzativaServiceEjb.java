/**
 * 
 */
package es.caib.distribucio.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.dto.ArbreDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaFiltreDto;
import es.caib.distribucio.logic.intf.exception.SistemaExternException;
import es.caib.distribucio.logic.intf.service.UnitatOrganitzativaService;
import lombok.experimental.Delegate;

/**
 * Implementació de UnitatsOrganitzativesService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class UnitatOrganitzativaServiceEjb extends AbstractServiceEjb<UnitatOrganitzativaService> implements UnitatOrganitzativaService {

	@Delegate
	private UnitatOrganitzativaService delegateService = null;

	@Override
	@RolesAllowed("**")
	public List<UnitatOrganitzativaDto> findByEntitat(
			String entitatCodi) {
		return delegateService.findByEntitat(entitatCodi);
	}

	@Override
	@RolesAllowed("**")
	public UnitatOrganitzativaDto findByCodi(
			String codi) {
		return delegateService.findByCodi(codi);
	}

	@Override
	@RolesAllowed("**")
	public List<UnitatOrganitzativaDto> findByFiltre(
			String codiDir3, 
			String denominacio, 
			String nivellAdm,
			String comunitat, 
			String provincia, 
			String municipi, 
			Boolean arrel) {
		return delegateService.findByFiltre(
				codiDir3,
				denominacio,
				nivellAdm,
				comunitat,
				provincia,
				municipi,
				arrel);
	}

	@Override
	@RolesAllowed("**")
	public void synchronize (
			Long entitatId
			) throws SistemaExternException {
		delegateService.synchronize(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<UnitatOrganitzativaDto> findAmbFiltre(Long entitatId, UnitatOrganitzativaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findAmbFiltre(entitatId,  filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public UnitatOrganitzativaDto findById(Long id) {
		return delegateService.findById(id);
	}

	@Override
	@RolesAllowed("**")
	public ArbreDto<UnitatOrganitzativaDto> findTree(Long id) {
		return delegateService.findTree(id);
	}

	@Override
	@RolesAllowed("**")
	public List<UnitatOrganitzativaDto> getObsoletesFromWS(Long entitatId) {
		return delegateService.getObsoletesFromWS(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<UnitatOrganitzativaDto> getVigentsFromWebService(Long entidadId) {
		return delegateService.getVigentsFromWebService(entidadId);
	}

	@Override
	@RolesAllowed("**")
	public boolean isFirstSincronization(Long entidadId) {
		return delegateService.isFirstSincronization(entidadId);
	}
	
	@Override
	@RolesAllowed("**")
	public List<UnitatOrganitzativaDto> predictFirstSynchronization(Long entidadId) {
		return delegateService.predictFirstSynchronization(entidadId);
	}
	
	@Override
	@RolesAllowed("**")
	public List<UnitatOrganitzativaDto> findByEntitatAndFiltre(String entitatCodi, String filtre, boolean ambArrel, boolean nomesAmbBusties) {
		return delegateService.findByEntitatAndFiltre(entitatCodi, filtre, ambArrel, nomesAmbBusties);
	}
	
	@Override
	@RolesAllowed("**")
	public List<UnitatOrganitzativaDto> findByEntitatAndCodiUnitatSuperiorAndFiltre(String entitatCodi, String codiUnitatSuperior, String filtre, boolean ambArrel, boolean nomesAmbBusties) {
		return delegateService.findByEntitatAndFiltre(entitatCodi, filtre, ambArrel, nomesAmbBusties);
	}
	
	@Override
	@RolesAllowed("**")
	public UnitatOrganitzativaDto getLastHistoricos(UnitatOrganitzativaDto uo) {
		return delegateService.getLastHistoricos(uo);
	}

	@Override
	@RolesAllowed("**")
	public List<UnitatOrganitzativaDto> getNewFromWS(Long entitatId) {
		return delegateService.getNewFromWS(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<UnitatOrganitzativaDto> findByCodiAndDenominacioFiltre(String filtre) {
		return delegateService.findByCodiAndDenominacioFiltre(filtre);
	}

	protected void setDelegateService(UnitatOrganitzativaService delegateService) {
		this.delegateService = delegateService;
	}

}
