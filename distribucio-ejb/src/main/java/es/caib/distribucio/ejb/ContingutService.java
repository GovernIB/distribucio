/**
 * 
 */
package es.caib.distribucio.ejb;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.*;
import es.caib.distribucio.logic.intf.dto.dadesobertes.LogsDadesObertesDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

/**
 * Implementació de ContenidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ContingutService extends AbstractService<es.caib.distribucio.logic.intf.service.ContingutService> implements es.caib.distribucio.logic.intf.service.ContingutService {

	@Override
	@RolesAllowed("**")
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions, 
			String rolActual,
			boolean isVistaMoviments) {
		return getDelegateService().findAmbIdUser(
				entitatId,
				contingutId,
				ambFills,
				ambVersions, 
				rolActual,
				isVistaMoviments);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public ContingutDto findAmbIdAdmin(
			Long entitatId,
			Long contingutId,
			boolean ambFills) {
		return getDelegateService().findAmbIdAdmin(
				entitatId,
				contingutId,
				ambFills);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<ContingutLogDto> findLogsPerContingutAdmin(
			Long entitatId,
			Long contingutId) {
		return getDelegateService().findLogsPerContingutAdmin(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("**")
	public List<ContingutLogDto> findLogsPerContingutUser(
			Long entitatId,
			Long contingutId) {
		return getDelegateService().findLogsPerContingutUser(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("**")
	public ContingutLogDetallsDto findLogDetallsPerContingutUser(
			Long entitatId,
			Long contingutId,
			Long contingutLogId) throws NotFoundException {
		return getDelegateService().findLogDetallsPerContingutUser(
				entitatId,
				contingutId,
				contingutLogId);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public List<ContingutMovimentDto> findMovimentsPerContingutAdmin(
			Long entitatId,
			Long contingutId) {
		return getDelegateService().findMovimentsPerContingutAdmin(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("**")
	public List<ContingutMovimentDto> findMovimentsPerContingutUser(
			Long entitatId,
			Long contingutId) {
		return getDelegateService().findMovimentsPerContingutUser(entitatId, contingutId);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA })
	public PaginaDto<ContingutDto> findAdmin(
			Long entitatId,
			ContingutFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return getDelegateService().findAdmin(
				entitatId,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<ContingutComentariDto> findComentarisPerContingut(Long entitatId, Long contingutId)
			throws NotFoundException {
		return getDelegateService().findComentarisPerContingut(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("**")
	public RespostaPublicacioComentariDto publicarComentariPerContingut(Long entitatId, Long contingutId, String text)
			throws NotFoundException {
		return getDelegateService().publicarComentariPerContingut(entitatId, contingutId, text);
	}

	@Override
	@RolesAllowed("**")
	public boolean marcarProcessat(Long entitatId, Long contingutId, String text, String rolActual) throws NotFoundException {
		return getDelegateService().marcarProcessat(entitatId, contingutId, text, rolActual);
	}

	@Override
	@RolesAllowed("**")
	public List<ContingutLogDetallsDto> findLogsDetallsPerContingutUser(Long entitatId,
			Long contingutId) {
		return getDelegateService().findLogsDetallsPerContingutUser(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("**")
	public boolean hasPermisSobreBustia(Long entitatId, Long contingutId) throws NotFoundException {
		return getDelegateService().hasPermisSobreBustia(entitatId, contingutId);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_ADMIN_LECTURA, BaseConfig.ROLE_REPORT })
	public List<LogsDadesObertesDto> findLogsPerDadesObertes(
			Date dataInici, 
			Date dataFi, 
			LogTipusEnumDto tipus, 
			String usuari,
			Long anotacioId, 
			String anotacioNumero,
			RegistreProcesEstatEnum anotacioEstat, 
			Boolean errorEstat, 
			Boolean pendent, 
			Long bustiaOrigen,
			Long bustiaDesti, 
			String uoOrigen, 
			String uoSuperior, 
			String uoDesti, 
			String uoDestiSuperior) {
		return getDelegateService().findLogsPerDadesObertes(
				dataInici, dataFi, tipus, usuari,
				anotacioId, anotacioNumero, anotacioEstat,
				errorEstat, pendent, bustiaOrigen, bustiaDesti,
				uoOrigen, uoSuperior, uoDesti, uoDestiSuperior);
	}

}
