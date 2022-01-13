/**
 * 
 */
package es.caib.distribucio.core.ejb;

import java.util.List;
import java.util.Map;

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
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.core.api.dto.UsuariBustiaFavoritDto;
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
			Long registreId, 
			String adresses, 
			String motiu,
			boolean isVistaMoviments,
			String rolActual) throws MessagingException {
		delegate.registreAnotacioEnviarPerEmail(
				entitatId, 
				registreId, 
				adresses, 
				motiu,
				isVistaMoviments,
				rolActual);
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
			Long[] bustiaDestiIds,
			Long contingutId,
			boolean deixarCopia,
			String comentari,
			Long[] perConeixement,
			Long destiLogic) throws NotFoundException {
		delegate.registreReenviar(
				entitatId,
				bustiaDestiIds,
				contingutId,
				deixarCopia,
				comentari,
				perConeixement,
				destiLogic);
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
	public List<UsuariPermisDto> getUsuarisPerBustia(Long bustiaId) {
		return delegate.getUsuarisPerBustia(bustiaId);
	}

	@Override
	@RolesAllowed("tothom")
	public Map<String, UsuariPermisDto> getUsuarisPerBustia(Long bustiaId, boolean directe, boolean perRol) {
		return delegate.getUsuarisPerBustia(bustiaId, directe, perRol);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public List<BustiaDto> findAmbUnitatId(Long entitatId,
			Long unitatId) {
		return delegate.findAmbUnitatId(entitatId, unitatId);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public List<UnitatOrganitzativaDto> findUnitatsSuperiors(Long entitatId, String filtre) {
		return delegate.findUnitatsSuperiors(entitatId, filtre);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean isBustiaReadPermitted(Long bustiaId) {
		return delegate.isBustiaReadPermitted(bustiaId);
	}

	@Override
	@RolesAllowed("tothom")
	public void addToFavorits(Long entitatId, Long bustiaId) {
		delegate.addToFavorits(entitatId, bustiaId);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<UsuariBustiaFavoritDto> getBustiesFavoritsUsuariActual(Long entitatId, PaginacioParamsDto paginacioParams) {
		return delegate.getBustiesFavoritsUsuariActual(entitatId, paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public void removeFromFavorits(Long entitatId, Long usuariBustiaFavoritId) {
		delegate.removeFromFavorits(entitatId, usuariBustiaFavoritId);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean checkIfFavoritExists(Long entitatId, Long id) {
		return delegate.checkIfFavoritExists(entitatId, id);
	}

	@Override
	@RolesAllowed("tothom")
	public List<Long> getIdsBustiesFavoritsUsuariActual(Long entitatId) {
		return delegate.getIdsBustiesFavoritsUsuariActual(entitatId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<BustiaDto> consultaBustiesOrigen(Long entitatId, List<BustiaDto> bustiesPermesesPerUsuari, boolean mostrarInactives) {
		return delegate.consultaBustiesOrigen(entitatId, bustiesPermesesPerUsuari, mostrarInactives);
	}

	@Override
	@RolesAllowed("tothom")
	public List<BustiaDto> findBustiesPerUsuari(Long entitatId, boolean mostrarInactives) {
		return delegate.findBustiesPerUsuari(entitatId, mostrarInactives);
	}
}