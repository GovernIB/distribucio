/**
 * 
 */
package es.caib.distribucio.ejb;

import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.mail.MessagingException;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ArbreDto;
import es.caib.distribucio.logic.intf.dto.BustiaContingutDto;
import es.caib.distribucio.logic.intf.dto.BustiaDto;
import es.caib.distribucio.logic.intf.dto.BustiaFiltreDto;
import es.caib.distribucio.logic.intf.dto.BustiaFiltreOrganigramaDto;
import es.caib.distribucio.logic.intf.dto.ContingutTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.PermisDto;
import es.caib.distribucio.logic.intf.dto.UnitatOrganitzativaDto;
import es.caib.distribucio.logic.intf.dto.UsuariBustiaFavoritDto;
import es.caib.distribucio.logic.intf.dto.UsuariPermisDto;
import es.caib.distribucio.logic.intf.dto.dadesobertes.BustiaDadesObertesDto;
import es.caib.distribucio.logic.intf.dto.dadesobertes.UsuariDadesObertesDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.registre.RegistreAnotacio;
import es.caib.distribucio.logic.intf.registre.RegistreTipusEnum;
import es.caib.distribucio.logic.intf.service.BustiaService;
import lombok.experimental.Delegate;

/**
 * Implementació de BustiaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class BustiaServiceEjb extends AbstractServiceEjb<BustiaService> implements BustiaService {

	@Delegate
	private BustiaService delegateService = null;

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public BustiaDto create(
			Long entitatId,
			BustiaDto bustia) {
		return delegateService.create(entitatId, bustia);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public BustiaDto update(
			Long entitatId,
			BustiaDto bustia) {
		return delegateService.update(entitatId, bustia);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public BustiaDto updateActiva(
			Long entitatId,
			Long id,
			boolean activa) {
		return delegateService.updateActiva(entitatId, id, activa);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public BustiaDto delete(
			Long entitatId,
			Long id) {
		return delegateService.delete(entitatId, id);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public BustiaDto marcarPerDefecte(
			Long entitatId,
			Long id) {
		return delegateService.marcarPerDefecte(entitatId, id);
	}

	@Override
	@RolesAllowed("**")
	public BustiaDto findById(
			Long entitatId,
			Long id) {
		return delegateService.findById(entitatId, id);
	}
	
	@Override
	@RolesAllowed("**")
	public BustiaDto findById(
			Long id) {
		return delegateService.findById(id);
	}	

	@Override
	@RolesAllowed("**")
	public BustiaDto findByIdAmbPermisosOrdenats(Long entitatId, Long id, PaginacioParamsDto paginacio) {
		return delegateService.findByIdAmbPermisosOrdenats(entitatId, id, paginacio);
	}
	
	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<BustiaDto> findAmbUnitatCodiAdmin(
			Long entitatId,
			String unitatCodi) {
		return delegateService.findAmbUnitatCodiAdmin(entitatId, unitatCodi);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public PaginaDto<BustiaDto> findAmbFiltreAdmin(
			Long entitatId,
			BustiaFiltreDto filtre,
			PaginacioParamsDto paginacioParams) throws NotFoundException {
		return delegateService.findAmbFiltreAdmin(
				entitatId,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<BustiaDto> findActivesAmbEntitat(
			Long entitatId) {
		return delegateService.findActivesAmbEntitat(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<BustiaDto> findBustiesPermesesPerUsuari(
			Long entitatId, 
			boolean mostrarInnactives) {
		return delegateService.findBustiesPermesesPerUsuari(entitatId, mostrarInnactives);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_BUSTIA_WS)
	public Throwable registreAnotacioCrearIProcessar(
			String entitatCodi,
			RegistreTipusEnum tipus,
			String unitatAdministrativa,
			RegistreAnotacio anotacio) {
		return delegateService.registreAnotacioCrearIProcessar(
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
		return delegateService.registreAnotacioCrear(
				entitatCodi,
				tipus,
				unitatAdministrativa,
				anotacio);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_BUSTIA_WS)
	public Throwable registreAnotacioProcessar(
			Long registreId) {
		return delegateService.registreAnotacioProcessar(
				registreId);
	}

	@Override
	@RolesAllowed("**")
	public long contingutPendentBustiesAllCount(
			Long entitatId) {
		return delegateService.contingutPendentBustiesAllCount(entitatId);
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
		delegateService.registreReenviar(
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
		return delegateService.findArbreUnitatsOrganitzatives(
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
		delegateService.updatePermis(entitatId, id, permis);
	}	

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public void deletePermis(
			Long entitatId,
			Long id,
			Long permisId) {
		delegateService.deletePermis(entitatId, id, permisId);
	}

	@Override
	@RolesAllowed("**")
	public List<BustiaDto> findAmbEntitat(Long entitatId) {
		return delegateService.findAmbEntitat(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<BustiaDto> findAmbEntitatAndFiltre(Long entitatId, BustiaFiltreOrganigramaDto bustiaFiltreOrganigramaDto) {
		return delegateService.findAmbEntitatAndFiltre(entitatId, bustiaFiltreOrganigramaDto);
	}

	@Override
	@RolesAllowed("**")
	public List<BustiaContingutDto> findAmbEntitatAndFiltrePerInput(
			Long entitatId, 
			ContingutTipusEnumDto tipus,
			String filtre) {
		return delegateService.findAmbEntitatAndFiltrePerInput(
				entitatId, 
				tipus, 
				filtre);
	}

	@Override
	@RolesAllowed("**")
	public ArbreDto<UnitatOrganitzativaDto> findArbreUnitatsOrganitzativesAmbFiltre(Long entitatId, List<BustiaDto> busties) {
		return delegateService.findArbreUnitatsOrganitzativesAmbFiltre(entitatId, busties);
	}

	@Override
	@RolesAllowed("**")
	public String getApplictionMetrics(){
		return delegateService.getApplictionMetrics();
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
		delegateService.registreAnotacioEnviarPerEmail(
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
		return delegateService.moureAnotacions(entitatId, bustiaId, destiId, comentari);
	}

	@Override
	@RolesAllowed("**")
	public List<UsuariPermisDto> getUsuarisPerBustia(Long bustiaId) {
		return delegateService.getUsuarisPerBustia(bustiaId);
	}

	@Override
	@RolesAllowed("**")
	public Map<String, UsuariPermisDto> getUsuarisPerBustia(Long bustiaId, boolean directe, boolean perRol) {
		return delegateService.getUsuarisPerBustia(bustiaId, directe, perRol);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<BustiaDto> findAmbUnitatId(Long entitatId,
			Long unitatId) {
		return delegateService.findAmbUnitatId(entitatId, unitatId);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<UnitatOrganitzativaDto> findUnitatsSuperiors(Long entitatId, String filtre) {
		return delegateService.findUnitatsSuperiors(entitatId, filtre);
	}

	@Override
	@RolesAllowed("**")
	public boolean isBustiaReadPermitted(Long bustiaId) {
		return delegateService.isBustiaReadPermitted(bustiaId);
	}

	@Override
	@RolesAllowed("**")
	public void addToFavorits(Long entitatId, Long bustiaId) {
		delegateService.addToFavorits(entitatId, bustiaId);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<UsuariBustiaFavoritDto> getBustiesFavoritsUsuariActual(Long entitatId, PaginacioParamsDto paginacioParams) {
		return delegateService.getBustiesFavoritsUsuariActual(entitatId, paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public void removeFromFavorits(Long entitatId, Long usuariBustiaFavoritId) {
		delegateService.removeFromFavorits(entitatId, usuariBustiaFavoritId);
	}

	@Override
	@RolesAllowed("**")
	public boolean checkIfFavoritExists(Long entitatId, Long id) {
		return delegateService.checkIfFavoritExists(entitatId, id);
	}

	@Override
	@RolesAllowed("**")
	public List<Long> getIdsBustiesFavoritsUsuariActual(Long entitatId) {
		return delegateService.getIdsBustiesFavoritsUsuariActual(entitatId);
	}

	@Override
	@RolesAllowed("**")
	public List<BustiaDto> consultaBustiesOrigen(Long entitatId, List<BustiaDto> bustiesPermesesPerUsuari, boolean mostrarInactives) {
		return delegateService.consultaBustiesOrigen(entitatId, bustiesPermesesPerUsuari, mostrarInactives);
	}

	@Override
	@RolesAllowed("**")
	public List<BustiaDto> findBustiesPerUsuari(Long entitatId, boolean mostrarInactives) {
		return delegateService.findBustiesPerUsuari(entitatId, mostrarInactives);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA, BaseConfig.ROLE_REPORT })
	public List<BustiaDadesObertesDto> findBustiesPerDadesObertes(
			Long id,
			String uo,
			String uoSuperior) {
		return delegateService.findBustiesPerDadesObertes(id, uo, uoSuperior);
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
		return delegateService.findBustiesUsuarisPerDadesObertes(usuari, id, uoDir3Entitat, uo, uoSuperior, rol, permis);
	}

	protected void setDelegateService(BustiaService delegateService) {
		this.delegateService = delegateService;
	}

}