/**
 * 
 */
package es.caib.distribucio.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.distribucio.core.api.dto.ArbreDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaFiltreDto;
import es.caib.distribucio.core.api.exception.SistemaExternException;
import es.caib.distribucio.core.api.service.UnitatOrganitzativaService;

/**
 * Implementació de UnitatsOrganitzativesService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class UnitatOrganitzativaServiceBean implements UnitatOrganitzativaService {

	@Autowired
	private UnitatOrganitzativaService delegate;

	@Override
	@RolesAllowed("tothom")
	public List<UnitatOrganitzativaDto> findByEntitat(
			String entitatCodi) {
		return delegate.findByEntitat(entitatCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public UnitatOrganitzativaDto findByCodi(
			String codi) {
		return delegate.findByCodi(codi);
	}

	@Override
	@RolesAllowed("tothom")
	public List<UnitatOrganitzativaDto> findByFiltre(
			String codiDir3, 
			String denominacio, 
			String nivellAdm,
			String comunitat, 
			String provincia, 
			String municipi, 
			Boolean arrel) {
		return delegate.findByFiltre(
				codiDir3,
				denominacio,
				nivellAdm,
				comunitat,
				provincia,
				municipi,
				arrel);
	}

	@Override
	@RolesAllowed("tothom")
	public void synchronize (
			Long entitatId
			) throws SistemaExternException {
		delegate.synchronize(entitatId);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<UnitatOrganitzativaDto> findAmbFiltre(Long entitatId, UnitatOrganitzativaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAmbFiltre(entitatId,  filtre, paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public UnitatOrganitzativaDto findById(Long id) {
		return delegate.findById(id);
	}

	@Override
	@RolesAllowed("tothom")
	public ArbreDto<UnitatOrganitzativaDto> findTree(Long id) {
		return delegate.findTree(id);
	}

	@Override
	@RolesAllowed("tothom")
	public List<UnitatOrganitzativaDto> getObsoletesFromWS(Long entitatId) {
		return delegate.getObsoletesFromWS(entitatId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<UnitatOrganitzativaDto> getVigentsFromWebService(Long entidadId) {
		return delegate.getVigentsFromWebService(entidadId);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean isFirstSincronization(Long entidadId) {
		return delegate.isFirstSincronization(entidadId);
	}
	
	@Override
	@RolesAllowed("tothom")
	public List<UnitatOrganitzativaDto> predictFirstSynchronization(Long entidadId) {
		return delegate.predictFirstSynchronization(entidadId);
	}
	
	@Override
	@RolesAllowed("tothom")
	public List<UnitatOrganitzativaDto> findByEntitatAndFiltre(String entitatCodi, String filtre, boolean ambArrel, boolean nomesAmbBusties) {
		return delegate.findByEntitatAndFiltre(entitatCodi, filtre, ambArrel, nomesAmbBusties);
	}
	
	@Override
	@RolesAllowed("tothom")
	public List<UnitatOrganitzativaDto> findByEntitatAndCodiUnitatSuperiorAndFiltre(String entitatCodi, String codiUnitatSuperior, String filtre, boolean ambArrel, boolean nomesAmbBusties) {
		return delegate.findByEntitatAndFiltre(entitatCodi, filtre, ambArrel, nomesAmbBusties);
	}
	
	@Override
	@RolesAllowed("tothom")
	public UnitatOrganitzativaDto getLastHistoricos(UnitatOrganitzativaDto uo) {
		return delegate.getLastHistoricos(uo);
	}

	@Override
	@RolesAllowed("tothom")
	public List<UnitatOrganitzativaDto> getNewFromWS(Long entitatId) {
		return delegate.getNewFromWS(entitatId);
	}

	@Override
	public List<UnitatOrganitzativaDto> findByCodiAndDenominacioFiltre(String filtre) {
		return delegate.findByCodiAndDenominacioFiltre(filtre);
	}

}
