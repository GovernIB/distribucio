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

/**
 * Implementació de UnitatsOrganitzativesService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class UnitatOrganitzativaService extends AbstractService<es.caib.distribucio.logic.intf.service.UnitatOrganitzativaService> implements es.caib.distribucio.logic.intf.service.UnitatOrganitzativaService {

	@Override
	@RolesAllowed("**")
	public List<UnitatOrganitzativaDto> findByEntitat(
			String entitatCodi) {
		return getDelegateService().findByEntitat(entitatCodi);
	}

	@Override
	@RolesAllowed("**")
	public UnitatOrganitzativaDto findByCodiDir3EntitatAndCodi(
            String codiDir3Entitat, String codi) {
		return getDelegateService().findByCodiDir3EntitatAndCodi(codiDir3Entitat, codi);
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
		return getDelegateService().findByFiltre(
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
	public void synchronize (Long entitatId) throws SistemaExternException {
		getDelegateService().synchronize(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public void forcedSynchronize (Long entitatId) throws SistemaExternException {
		getDelegateService().forcedSynchronize(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<UnitatOrganitzativaDto> findAmbFiltre(Long entitatId, UnitatOrganitzativaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAmbFiltre(entitatId,  filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public UnitatOrganitzativaDto findById(Long id) {
		return getDelegateService().findById(id);
	}

	@Override
	@RolesAllowed("**")
	public ArbreDto<UnitatOrganitzativaDto> findTree(Long id) {
		return getDelegateService().findTree(id);
	}

	@Override
	@RolesAllowed("**")
	public List<UnitatOrganitzativaDto> getObsoletesFromWS(Long entitatId) {
		return getDelegateService().getObsoletesFromWS(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<UnitatOrganitzativaDto> getVigentsFromWebService(Long entidadId) {
		return getDelegateService().getVigentsFromWebService(entidadId);
	}

	@Override
	@RolesAllowed("**")
	public boolean isFirstSincronization(Long entidadId) {
		return getDelegateService().isFirstSincronization(entidadId);
	}
	
	@Override
	@RolesAllowed("**")
	public List<UnitatOrganitzativaDto> predictFirstSynchronization(Long entidadId) {
		return getDelegateService().predictFirstSynchronization(entidadId);
	}
	
	@Override
	@RolesAllowed("**")
	public List<UnitatOrganitzativaDto> findByEntitatAndFiltre(String entitatCodi, String filtre, boolean ambArrel, boolean nomesAmbBusties, boolean isUsuari) {
		return getDelegateService().findByEntitatAndFiltre(entitatCodi, filtre, ambArrel, nomesAmbBusties, isUsuari);
	}
	
	@Override
	@RolesAllowed("**")
	public List<UnitatOrganitzativaDto> findByEntitatAndCodiUnitatSuperiorAndFiltre(String entitatCodi, String codiUnitatSuperior, String filtre, boolean ambArrel, boolean nomesAmbBusties) {
		return getDelegateService().findByEntitatAndFiltre(entitatCodi, filtre, ambArrel, nomesAmbBusties, false);
	}
	
	@Override
	@RolesAllowed("**")
	public UnitatOrganitzativaDto getLastHistoricos(UnitatOrganitzativaDto uo) {
		return getDelegateService().getLastHistoricos(uo);
	}

	@Override
	@RolesAllowed("**")
	public List<UnitatOrganitzativaDto> getNewFromWS(Long entitatId) {
		return getDelegateService().getNewFromWS(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<UnitatOrganitzativaDto> findByCodiAndDenominacioFiltre(String filtre) {
		return getDelegateService().findByCodiAndDenominacioFiltre(filtre);
	}

}
