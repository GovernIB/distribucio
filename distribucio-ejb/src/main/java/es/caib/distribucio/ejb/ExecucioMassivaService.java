package es.caib.distribucio.ejb;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.*;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import org.springframework.ui.Model;

/**
 * Implementació de ExecucioMassivaService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ExecucioMassivaService extends AbstractService<es.caib.distribucio.logic.intf.service.ExecucioMassivaService> implements es.caib.distribucio.logic.intf.service.ExecucioMassivaService {

	
	@Override
	@RolesAllowed("**")
	public void crearExecucioMassiva(Long entitatId, ExecucioMassivaDto dto) throws NotFoundException {
		getDelegateService().crearExecucioMassiva(
				entitatId, 
				dto);
	}

	@Override
	@RolesAllowed({ BaseConfig.ROLE_ADMIN })
	public void updateExecucioMassiva(ExecucioMassivaAccioDto accio, Long exm_id) throws NotFoundException {
		getDelegateService().updateExecucioMassiva(
				accio, 
				exm_id);
	}

	@Override
	@RolesAllowed("**")
	public List<ExecucioMassivaDto> findExecucionsMassivesPerFiltre(Long entitatId, ExecucioMassivaFiltreDto filtre, int pagina)
			throws NotFoundException {
		return getDelegateService().findExecucionsMassivesPerFiltre(
				entitatId,
                filtre,
				pagina);
	}

	@Override
	@RolesAllowed("**")
	public List<ExecucioMassivaContingutDto> findContingutPerExecucioMassiva(Long exm_id) throws NotFoundException {
		return getDelegateService().findContingutPerExecucioMassiva(exm_id);
	}

	@Override
	@RolesAllowed("**")
	public List<String> findElementNomExecucioPerContingut(List<Long> continguts) throws NotFoundException {
		return getDelegateService().findElementNomExecucioPerContingut(continguts);
	}

	@Override
	@RolesAllowed("**")
	public List<ExecucioMassivaContingutDto> findExecucioPerContingut(List<Long> continguts) throws NotFoundException {
		return getDelegateService().findExecucioPerContingut(continguts);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto descarregarDocumentExecMassiva(Long entitatId, Long execucioId) {
		return getDelegateService().descarregarDocumentExecMassiva(entitatId, execucioId);
	}

	@Override
	@RolesAllowed("**")
	public boolean chechFormDescargaMassiva(List<RegistreDto> registres, Model model) {
		return getDelegateService().chechFormDescargaMassiva(registres, model);
	}

	@Override
	@PermitAll
	public void executeNextMassiveScheduledTask(Long entitatId) {
		getDelegateService().executeNextMassiveScheduledTask(entitatId);
	}

}
