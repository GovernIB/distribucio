/**
 * 
 */
package es.caib.distribucio.ejb;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.dto.ContingutComentariDto;
import es.caib.distribucio.logic.intf.dto.ContingutDto;
import es.caib.distribucio.logic.intf.dto.ContingutFiltreDto;
import es.caib.distribucio.logic.intf.dto.ContingutLogDetallsDto;
import es.caib.distribucio.logic.intf.dto.ContingutLogDto;
import es.caib.distribucio.logic.intf.dto.ContingutMovimentDto;
import es.caib.distribucio.logic.intf.dto.LogTipusEnumDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.RespostaPublicacioComentariDto;
import es.caib.distribucio.logic.intf.dto.dadesobertes.LogsDadesObertesDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.logic.intf.service.ContingutService;
import lombok.experimental.Delegate;

/**
 * Implementació de ContenidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ContingutServiceEjb extends AbstractServiceEjb<ContingutService> implements ContingutService {

	@Delegate
	private ContingutService delegateService = null;

	@Override
	@RolesAllowed("**")
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions, 
			String rolActual,
			boolean isVistaMoviments) {
		return delegateService.findAmbIdUser(
				entitatId,
				contingutId,
				ambFills,
				ambVersions, 
				rolActual,
				isVistaMoviments);
	}

	@Override
	@RolesAllowed({"DIS_ADMIN", "DIS_ADMIN_LECTURA"})
	public ContingutDto findAmbIdAdmin(
			Long entitatId,
			Long contingutId,
			boolean ambFills) {
		return delegateService.findAmbIdAdmin(
				entitatId,
				contingutId,
				ambFills);
	}

	@Override
	@RolesAllowed({"DIS_ADMIN", "DIS_ADMIN_LECTURA"})
	public List<ContingutLogDto> findLogsPerContingutAdmin(
			Long entitatId,
			Long contingutId) {
		return delegateService.findLogsPerContingutAdmin(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("**")
	public List<ContingutLogDto> findLogsPerContingutUser(
			Long entitatId,
			Long contingutId) {
		return delegateService.findLogsPerContingutUser(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("**")
	public ContingutLogDetallsDto findLogDetallsPerContingutUser(
			Long entitatId,
			Long contingutId,
			Long contingutLogId) throws NotFoundException {
		return delegateService.findLogDetallsPerContingutUser(
				entitatId,
				contingutId,
				contingutLogId);
	}

	@Override
	@RolesAllowed({"DIS_ADMIN", "DIS_ADMIN_LECTURA"})
	public List<ContingutMovimentDto> findMovimentsPerContingutAdmin(
			Long entitatId,
			Long contingutId) {
		return delegateService.findMovimentsPerContingutAdmin(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("**")
	public List<ContingutMovimentDto> findMovimentsPerContingutUser(
			Long entitatId,
			Long contingutId) {
		return delegateService.findMovimentsPerContingutUser(entitatId, contingutId);
	}

	@Override
	@RolesAllowed({"DIS_ADMIN", "DIS_ADMIN_LECTURA"})
	public PaginaDto<ContingutDto> findAdmin(
			Long entitatId,
			ContingutFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findAdmin(
				entitatId,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public List<ContingutComentariDto> findComentarisPerContingut(Long entitatId, Long contingutId)
			throws NotFoundException {
		return delegateService.findComentarisPerContingut(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("**")
	public RespostaPublicacioComentariDto publicarComentariPerContingut(Long entitatId, Long contingutId, String text)
			throws NotFoundException {
		return delegateService.publicarComentariPerContingut(entitatId, contingutId, text);
	}

	@Override
	@RolesAllowed("**")
	public boolean marcarProcessat(Long entitatId, Long contingutId, String text, String rolActual) throws NotFoundException {
		return delegateService.marcarProcessat(entitatId, contingutId, text, rolActual);
	}

	@Override
	@RolesAllowed("**")
	public List<ContingutLogDetallsDto> findLogsDetallsPerContingutUser(Long entitatId,
			Long contingutId) {
		return delegateService.findLogsDetallsPerContingutUser(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("**")
	public boolean hasPermisSobreBustia(Long entitatId, Long contingutId) throws NotFoundException {
		return delegateService.hasPermisSobreBustia(entitatId, contingutId);
	}

	@Override
	@RolesAllowed({"DIS_REPORT", "DIS_ADMIN", "DIS_ADMIN_LECTURA"})
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
		return delegateService.findLogsPerDadesObertes(
				dataInici, dataFi, tipus, usuari,
				anotacioId, anotacioNumero, anotacioEstat,
				errorEstat, pendent, bustiaOrigen, bustiaDesti,
				uoOrigen, uoSuperior, uoDesti, uoDestiSuperior);
	}

	protected void setDelegateService(ContingutService delegateService) {
		this.delegateService = delegateService;
	}

}