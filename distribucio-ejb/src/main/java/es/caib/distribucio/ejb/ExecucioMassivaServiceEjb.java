package es.caib.distribucio.ejb;

import java.util.List;
import javax.ejb.Stateless;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaAccioDto;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaContingutDto;
import es.caib.distribucio.logic.intf.dto.ExecucioMassivaDto;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.service.ExecucioMassivaService;
import lombok.experimental.Delegate;

/**
 * Implementació de ExecucioMassivaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExecucioMassivaServiceEjb implements ExecucioMassivaService {

	@Delegate
	private ExecucioMassivaService delegateService = null;
	
	@Override
	@RolesAllowed("**")
	public void crearExecucioMassiva(Long entitatId, ExecucioMassivaDto dto) throws NotFoundException {
		delegateService.crearExecucioMassiva(
				entitatId, 
				dto);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN })
	public void updateExecucioMassiva(ExecucioMassivaAccioDto accio, Long exm_id) throws NotFoundException {
		delegateService.updateExecucioMassiva(
				accio, 
				exm_id);
	}

	@Override
	@RolesAllowed("**")
	public List<ExecucioMassivaDto> findExecucionsMassivesPerUsuari(Long entitatId, UsuariDto usuari, int pagina)
			throws NotFoundException {
		return delegateService.findExecucionsMassivesPerUsuari(
				entitatId, 
				usuari, 
				pagina);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN })
	public List<ExecucioMassivaContingutDto> findContingutPerExecucioMassiva(Long exm_id) throws NotFoundException {
		return delegateService.findContingutPerExecucioMassiva(exm_id);
	}

	@Override
	@RolesAllowed("**")
	public List<String> findElementNomExecucioPerContingut(List<Long> continguts) throws NotFoundException {
		return delegateService.findElementNomExecucioPerContingut(continguts);
	}

	@Override
	@RolesAllowed("**")
	public List<ExecucioMassivaContingutDto> findExecucioPerContingut(List<Long> continguts) throws NotFoundException {
		return delegateService.findExecucioPerContingut(continguts);
	}

	@Override
	@PermitAll
	public void executeNextMassiveScheduledTask(Long entitatId) {
		delegateService.executeNextMassiveScheduledTask(entitatId);
	}

}
