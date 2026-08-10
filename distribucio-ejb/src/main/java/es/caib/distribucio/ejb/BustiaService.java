/**
 * 
 */
package es.caib.distribucio.ejb;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.*;
import es.caib.distribucio.logic.intf.dto.dadesobertes.BustiaDadesObertesDto;
import es.caib.distribucio.logic.intf.dto.dadesobertes.UsuariDadesObertesDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.registre.RegistreAnotacio;
import es.caib.distribucio.logic.intf.registre.RegistreTipusEnum;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import java.util.List;
import java.util.Map;

/**
 * Implementació de BustiaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class BustiaService extends AbstractService<es.caib.distribucio.logic.intf.service.BustiaService> implements es.caib.distribucio.logic.intf.service.BustiaService {

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public BustiaDto create(
			Long entitatId,
			BustiaDto bustia) {
		return getDelegateService().create(entitatId, bustia);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public BustiaDto update(
			Long entitatId,
			BustiaDto bustia) {
		return getDelegateService().update(entitatId, bustia);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public BustiaDto updateActiva(
			Long entitatId,
			Long id,
			boolean activa) {
		return getDelegateService().updateActiva(entitatId, id, activa);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public BustiaDto delete(
			Long entitatId,
			Long id) {
		return getDelegateService().delete(entitatId, id);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public BustiaDto marcarPerDefecte(
			Long entitatId,
			Long id) {
		return getDelegateService().marcarPerDefecte(entitatId, id);
	}

	@Override
	@RolesAllowed("**")
	public BustiaDto findById(
			Long entitatId,
			Long id) {
		return getDelegateService().findById(entitatId, id);
	}
	
	@Override
	@RolesAllowed("**")
	public BustiaDto findById(
			Long id) {
		return getDelegateService().findById(id);
	}	

	@Override
	@RolesAllowed("**")
	public BustiaDto findByIdAmbPermisosOrdenats(Long entitatId, Long id, PaginacioParamsDto paginacio) {
		return getDelegateService().findByIdAmbPermisosOrdenats(entitatId, id, paginacio);
	}
	
	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<BustiaDto> findAmbUnitatCodiAdmin(
			Long entitatId,
			String unitatCodi) {
		return getDelegateService().findAmbUnitatCodiAdmin(entitatId, unitatCodi);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public PaginaDto<BustiaDto> findAmbFiltreAdmin(
			Long entitatId,
			BustiaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		return getDelegateService().findAmbFiltreAdmin(
				entitatId,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<BustiaDto> findActivesAmbEntitat(
			Long entitatId) {
		return getDelegateService().findActivesAmbEntitat(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<BustiaDto> findBustiesPermesesPerUsuari(
			Long entitatId, 
			boolean mostrarInnactives) {
		return getDelegateService().findBustiesPermesesPerUsuari(entitatId, mostrarInnactives);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_BUSTIA_WS)
	public Throwable registreAnotacioCrearIProcessar(
			String entitatCodi,
			RegistreTipusEnum tipus,
			String unitatAdministrativa,
			RegistreAnotacio anotacio) {
		return getDelegateService().registreAnotacioCrearIProcessar(
				entitatCodi,
				tipus,
				unitatAdministrativa,
				anotacio);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_BUSTIA_WS)
	public long registreAnotacioCrear(
			String entitatCodi,
			RegistreTipusEnum tipus,
			String unitatAdministrativa,
			RegistreAnotacio anotacio) throws Exception {
		return getDelegateService().registreAnotacioCrear(
				entitatCodi,
				tipus,
				unitatAdministrativa,
				anotacio);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_BUSTIA_WS)
	public Throwable registreAnotacioProcessar(
			Long registreId) {
		return getDelegateService().registreAnotacioProcessar(
				registreId);
	}

	@Override
	@RolesAllowed("**")
	public long contingutPendentBustiesAllCount(
			Long entitatId) {
		return getDelegateService().contingutPendentBustiesAllCount(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public void registreReenviar(
			Long entitatId,
			Long[] bustiaDestiIds,
			Long contingutId,
			boolean deixarCopia,
			String comentari,
			Long[] perConeixement,
			Map<Long, String> destinsUsuari,
			Long destiLogic) throws NotFoundException {
		getDelegateService().registreReenviar(
				entitatId,
				bustiaDestiIds,
				contingutId,
				deixarCopia,
				comentari,
				perConeixement,
				destinsUsuari,
				destiLogic);
	}

	@Override
	@RolesAllowed("**")
	public ArbreDto<UnitatOrganitzativaDto> findArbreUnitatsOrganitzatives(
			Long entitatId,
			boolean nomesBusties,
			boolean nomesBustiesPermeses,
			boolean comptarElementsPendents) {
		return getDelegateService().findArbreUnitatsOrganitzatives(
				entitatId,
				nomesBusties,
				nomesBustiesPermeses,
				comptarElementsPendents);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public void updatePermis(
			Long entitatId,
			Long id,
			PermisDto permis) {
		getDelegateService().updatePermis(entitatId, id, permis);
	}	

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public void deletePermis(
			Long entitatId,
			Long id,
			Long permisId) {
		getDelegateService().deletePermis(entitatId, id, permisId);
	}

	@Override
	@RolesAllowed("**")
	public List<BustiaDto> findAmbEntitat(Long entitatId) {
		return getDelegateService().findAmbEntitat(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<BustiaDto> findAmbEntitatAndFiltre(Long entitatId, BustiaFiltreDto bustiaFiltreDto) {
		return getDelegateService().findAmbEntitatAndFiltre(entitatId, bustiaFiltreDto);
	}

	@Override
	@RolesAllowed("**")
	public List<BustiaContingutDto> findAmbEntitatAndFiltrePerInput(
			Long entitatId, 
			ContingutTipusEnumDto tipus,
			String filtre) {
		return getDelegateService().findAmbEntitatAndFiltrePerInput(
				entitatId, 
				tipus, 
				filtre);
	}

	@Override
	@RolesAllowed("**")
	public ArbreDto<UnitatOrganitzativaDto> findArbreUnitatsOrganitzativesAmbFiltre(Long entitatId, List<BustiaDto> busties) {
		return getDelegateService().findArbreUnitatsOrganitzativesAmbFiltre(entitatId, busties);
	}

	@Override
	@RolesAllowed("**")
	public String getApplictionMetrics(){
		return getDelegateService().getApplictionMetrics();
	}

	@Override
	@RolesAllowed("**")
	public void registreAnotacioEnviarPerEmail(
			Long entitatId, 
			Long registreId, 
			String adresses, 
			String motiu,
			boolean isVistaMoviments,
			String rolActual) throws MessagingException {
		getDelegateService().registreAnotacioEnviarPerEmail(
				entitatId, 
				registreId, 
				adresses, 
				motiu,
				isVistaMoviments,
				rolActual);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public int moureAnotacions(long entitatId, long bustiaId, long destiId, String comentari) {
		return getDelegateService().moureAnotacions(entitatId, bustiaId, destiId, comentari);
	}

	@Override
	@RolesAllowed("**")
	public List<UsuariPermisDto> getUsuarisPerBustia(Long bustiaId) {
		return getDelegateService().getUsuarisPerBustia(bustiaId);
	}

	@Override
	@RolesAllowed("**")
	public Map<String, UsuariPermisDto> getUsuarisPerBustia(Long bustiaId, boolean directe, boolean perRol) {
		return getDelegateService().getUsuarisPerBustia(bustiaId, directe, perRol);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<BustiaDto> findAmbUnitatId(Long entitatId,
			Long unitatId) {
		return getDelegateService().findAmbUnitatId(entitatId, unitatId);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<UnitatOrganitzativaDto> findUnitatsSuperiors(Long entitatId, String filtre) {
		return getDelegateService().findUnitatsSuperiors(entitatId, filtre);
	}

	@Override
	@RolesAllowed("**")
	public boolean isBustiaReadPermitted(Long bustiaId) {
		return getDelegateService().isBustiaReadPermitted(bustiaId);
	}

	@Override
	@RolesAllowed("**")
	public void addToFavorits(Long entitatId, Long bustiaId) {
		getDelegateService().addToFavorits(entitatId, bustiaId);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<UsuariBustiaFavoritDto> getBustiesFavoritsUsuariActual(Long entitatId, PaginacioParamsDto paginacioParams) {
		return getDelegateService().getBustiesFavoritsUsuariActual(entitatId, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public void removeFromFavorits(Long entitatId, Long usuariBustiaFavoritId) {
		getDelegateService().removeFromFavorits(entitatId, usuariBustiaFavoritId);
	}

	@Override
	@RolesAllowed("**")
	public boolean checkIfFavoritExists(Long entitatId, Long id) {
		return getDelegateService().checkIfFavoritExists(entitatId, id);
	}

	@Override
	@RolesAllowed("**")
	public List<Long> getIdsBustiesFavoritsUsuariActual(Long entitatId) {
		return getDelegateService().getIdsBustiesFavoritsUsuariActual(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<BustiaDto> consultaBustiesOrigen(Long entitatId, List<BustiaDto> bustiesPermesesPerUsuari, boolean mostrarInactives) {
		return getDelegateService().consultaBustiesOrigen(entitatId, bustiesPermesesPerUsuari, mostrarInactives);
	}

	@Override
	@RolesAllowed("**")
	public List<BustiaDto> findBustiesPerUsuari(Long entitatId, boolean mostrarInactives) {
		return getDelegateService().findBustiesPerUsuari(entitatId, mostrarInactives);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA, BaseConfig.ROLE_REPORT })
	public List<BustiaDadesObertesDto> findBustiesPerDadesObertes(
			Long id,
			String uo,
			String uoSuperior) {
		return getDelegateService().findBustiesPerDadesObertes(id, uo, uoSuperior);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA, BaseConfig.ROLE_REPORT })
	public List<UsuariDadesObertesDto> findBustiesUsuarisPerDadesObertes(
			String usuari,
			Long id,
			String uoDir3Entitat, 
			String uo,
			String uoSuperior,
			Boolean rol,
			Boolean permis) {
		return getDelegateService().findBustiesUsuarisPerDadesObertes(usuari, id, uoDir3Entitat, uo, uoSuperior, rol, permis);
	}

	@Override
	@RolesAllowed("**")
	public List<Long> getIdsBustiesInactives(Long entitatId) {
		return getDelegateService().getIdsBustiesInactives(entitatId);
	}

}
