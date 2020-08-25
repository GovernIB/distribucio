/**
 * 
 */
package es.caib.distribucio.core.ejb;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.distribucio.core.api.dto.ArbreDto;
import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.BustiaFiltreDto;
import es.caib.distribucio.core.api.dto.BustiaFiltreOrganigramaDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.PermisDto;
import es.caib.distribucio.core.api.dto.RegistreFiltreDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.dto.UsuariPermisDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.registre.RegistreAnotacio;
import es.caib.distribucio.core.api.registre.RegistreTipusEnum;
import es.caib.distribucio.core.api.service.BustiaService;

/**
 * Implementació de BustiaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class BustiaServiceBean implements BustiaService {

	@Autowired
	BustiaService delegate;

	@Override
	@RolesAllowed("DIS_ADMIN")
	public BustiaDto create(
			Long entitatId,
			BustiaDto bustia) {
		return delegate.create(entitatId, bustia);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public BustiaDto update(
			Long entitatId,
			BustiaDto bustia) {
		return delegate.update(entitatId, bustia);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public BustiaDto updateActiva(
			Long entitatId,
			Long id,
			boolean activa) {
		return delegate.updateActiva(entitatId, id, activa);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public BustiaDto delete(
			Long entitatId,
			Long id) {
		return delegate.delete(entitatId, id);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public BustiaDto marcarPerDefecte(
			Long entitatId,
			Long id) {
		return delegate.marcarPerDefecte(entitatId, id);
	}

	@Override
	@RolesAllowed({"DIS_ADMIN", "tothom"})
	public BustiaDto findById(
			Long entitatId,
			Long id) {
		return delegate.findById(entitatId, id);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public List<BustiaDto> findAmbUnitatCodiAdmin(
			Long entitatId,
			String unitatCodi) {
		return delegate.findAmbUnitatCodiAdmin(entitatId, unitatCodi);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public PaginaDto<BustiaDto> findAmbFiltreAdmin(
			Long entitatId,
			BustiaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		return delegate.findAmbFiltreAdmin(
				entitatId,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed({"DIS_ADMIN", "tothom"})
	public List<BustiaDto> findActivesAmbEntitat(
			Long entitatId) {
		return delegate.findActivesAmbEntitat(entitatId);
	}




	@Override
	@RolesAllowed("DIS_BSTWS")
	public Exception registreAnotacioCrearIProcessar(
			String entitatCodi,
			RegistreTipusEnum tipus,
			String unitatAdministrativa,
			RegistreAnotacio anotacio) {
		return delegate.registreAnotacioCrearIProcessar(
				entitatCodi,
				tipus,
				unitatAdministrativa,
				anotacio);
	}

	@Override
	@RolesAllowed("tothom")
	public void registreAnotacioEnviarPerEmail(
			Long entitatId, 
			Long contingutId, 
			Long registreId, 
			String adresses) throws MessagingException {
		delegate.registreAnotacioEnviarPerEmail(
				entitatId, 
				contingutId, 
				registreId, 
				adresses);
	}



	@Override
	@RolesAllowed("tothom")
	public List<Long> findIdsAmbFiltre(
			Long entitatId, 
			List<BustiaDto> bustiesUsuari, 
			RegistreFiltreDto filtre) {
		return delegate.findIdsAmbFiltre(
				entitatId, 
				bustiesUsuari, 
				filtre);
	}



	@Override
	@RolesAllowed("tothom")
	public long contingutPendentBustiesAllCount(
			Long entitatId) {
		return delegate.contingutPendentBustiesAllCount(entitatId);
	}


	@Override
	@RolesAllowed({"DIS_ADMIN", "tothom"})
	public ArbreDto<UnitatOrganitzativaDto> findArbreUnitatsOrganitzatives(
			Long entitatId,
			boolean nomesBusties,
			boolean nomesBustiesPermeses,
			boolean comptarElementsPendents) {
		return delegate.findArbreUnitatsOrganitzatives(
				entitatId,
				nomesBusties,
				nomesBustiesPermeses,
				comptarElementsPendents);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public void updatePermis(
			Long entitatId,
			Long id,
			PermisDto permis) {
		delegate.updatePermis(entitatId, id, permis);
	}	

	@Override
	@RolesAllowed("DIS_ADMIN")
	public void deletePermis(
			Long entitatId,
			Long id,
			Long permisId) {
		delegate.deletePermis(entitatId, id, permisId);
	}

	@Override
	@RolesAllowed({"DIS_ADMIN", "tothom"})
	public List<BustiaDto> findBustiesPermesesPerUsuari(Long entitatId, boolean mostrarInnactives) {
		return delegate.findBustiesPermesesPerUsuari(entitatId, mostrarInnactives);
	}

	@Override
	@RolesAllowed("tothom")
	public List<BustiaDto> findAmbEntitat(Long entitatId) {
		return delegate.findAmbEntitat(entitatId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<BustiaDto> findAmbEntitatAndFiltre(Long entitatId, BustiaFiltreOrganigramaDto bustiaFiltreOrganigramaDto) {
		return delegate.findAmbEntitatAndFiltre(entitatId, bustiaFiltreOrganigramaDto);
	}

	@Override
	@RolesAllowed("tothom")
	public ArbreDto<UnitatOrganitzativaDto> findArbreUnitatsOrganitzativesAmbFiltre(Long entitatId, List<BustiaDto> busties) {
		return delegate.findArbreUnitatsOrganitzativesAmbFiltre(entitatId, busties);
	}

	@Override
	@RolesAllowed("tothom")
	public void registreReenviar(
			Long entitatId,
			Long bustiaOrigenId,
			Long[] bustiaDestiIds,
			Long contingutId,
			boolean deixarCopia,
			String comentari) throws NotFoundException {
		delegate.registreReenviar(
				entitatId,
				bustiaOrigenId,
				bustiaDestiIds,
				contingutId,
				deixarCopia,
				comentari);
	}

	@Override
	@RolesAllowed("tothom")
	public String getApplictionMetrics(){
		return delegate.getApplictionMetrics();
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public int moureAnotacions(long entitatId, long bustiaId, long destiId, String comentari) {
		return delegate.moureAnotacions(entitatId, bustiaId, destiId, comentari);
	}

	@Override
	@RolesAllowed("tothom")
	public List<UsuariPermisDto> getUsersPermittedForBustia(Long bustiaId) {
		return delegate.getUsersPermittedForBustia(bustiaId);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public List<BustiaDto> findAmbUnitatId(Long entitatId,
			Long unitatId) {
		return delegate.findAmbUnitatId(entitatId, unitatId);
	}

}