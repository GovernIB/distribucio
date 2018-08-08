/**
 * 
 */
package es.caib.distribucio.core.ejb;

import java.util.List;
import javax.mail.MessagingException;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.distribucio.core.api.dto.ArbreDto;
import es.caib.distribucio.core.api.dto.BustiaContingutDto;
import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.BustiaFiltreDto;
import es.caib.distribucio.core.api.dto.BustiaUserFiltreDto;
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.PermisDto;
import es.caib.distribucio.core.api.dto.UnitatOrganitzativaDto;
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
	@RolesAllowed("tothom")
	public PaginaDto<BustiaDto> findPermesesPerUsuari(
			Long entitatId,
			PaginacioParamsDto paginacioParams) {
		return delegate.findPermesesPerUsuari(
				entitatId,
				paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public ContingutDto enviarContingut(
			Long entitatId,
			Long bustiaId,
			Long contingutId,
			String comentari) {
		return delegate.enviarContingut(
				entitatId,
				bustiaId,
				contingutId,
				comentari);
	}

	@Override
	@RolesAllowed("DIS_BSTWS")
	public Long registreAnotacioCrear(
			String entitatCodi,
			RegistreTipusEnum tipus,
			String unitatAdministrativa,
			RegistreAnotacio anotacio) {
		return delegate.registreAnotacioCrear(
				entitatCodi,
				tipus,
				unitatAdministrativa,
				anotacio);
	}

	@Override
	@RolesAllowed("DIS_BSTWS")
	public void registreAnotacioCrear(
			String entitatCodi,
			String referencia) {
		delegate.registreAnotacioCrear(
				entitatCodi,
				referencia);
	}

	@Override
	@RolesAllowed("tothom")
	public PaginaDto<BustiaContingutDto> contingutPendentFindByDatatable(
			Long entitatId,
			List<BustiaDto> bustiesUsuari,
			BustiaUserFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.contingutPendentFindByDatatable(
				entitatId,
				bustiesUsuari,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public BustiaContingutDto contingutPendentFindOne(
			Long entitatId,
			Long bustiaId,
			Long contingutId) {
		return delegate.contingutPendentFindOne(
				entitatId,
				bustiaId,
				contingutId);
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
	public List<BustiaDto> findPermesesPerUsuari(Long entitatId) {
		return delegate.findPermesesPerUsuari(entitatId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<BustiaDto> findAmbEntitat(Long entitatId) {
		return delegate.findAmbEntitat(entitatId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<BustiaDto> findAmbEntitatAndFiltre(Long entitatId, String bustiaNom, Long unitatIdFiltre) {
		return delegate.findAmbEntitatAndFiltre(entitatId, bustiaNom, unitatIdFiltre);
	}

	@Override
	@RolesAllowed("tothom")
	public ArbreDto<UnitatOrganitzativaDto> findArbreUnitatsOrganitzativesAmbFiltre(Long entitatId,
			String bustiaNomFiltre, Long unitatIdFiltre) {
		return delegate.findArbreUnitatsOrganitzativesAmbFiltre(entitatId, bustiaNomFiltre, unitatIdFiltre);
	}

	@Override
	@RolesAllowed("tothom")
	public void contingutPendentReenviar(
			Long entitatId,
			Long bustiaOrigenId,
			Long[] bustiaDestiIds,
			Long contingutId,
			boolean deixarCopia,
			String comentari) throws NotFoundException {
		delegate.contingutPendentReenviar(
				entitatId,
				bustiaOrigenId,
				bustiaDestiIds,
				contingutId,
				deixarCopia,
				comentari);
	}
	
	@Override
	@RolesAllowed("tothom")
	public void enviarRegistreByEmail(Long entitatId, Long contingutId, Long registreId, String adresses) throws MessagingException {
		delegate.enviarRegistreByEmail(entitatId, contingutId, registreId, adresses);
	}

	
}