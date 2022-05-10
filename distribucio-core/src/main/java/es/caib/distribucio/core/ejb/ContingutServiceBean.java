/**
 * 
 */
package es.caib.distribucio.core.ejb;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.distribucio.core.api.dto.ContingutComentariDto;
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.ContingutFiltreDto;
import es.caib.distribucio.core.api.dto.ContingutLogDetallsDto;
import es.caib.distribucio.core.api.dto.ContingutLogDto;
import es.caib.distribucio.core.api.dto.ContingutMovimentDto;
import es.caib.distribucio.core.api.dto.LogTipusEnumDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.RespostaPublicacioComentariDto;
import es.caib.distribucio.core.api.dto.dadesobertes.LogsDadesObertesDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.service.ContingutService;

/**
 * Implementació de ContenidorService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ContingutServiceBean implements ContingutService {

	@Autowired
	ContingutService delegate;



	@Override
	@RolesAllowed("tothom")
	public ContingutDto findAmbIdUser(
			Long entitatId,
			Long contingutId,
			boolean ambFills,
			boolean ambVersions, 
			String rolActual,
			boolean isVistaMoviments) {
		return delegate.findAmbIdUser(
				entitatId,
				contingutId,
				ambFills,
				ambVersions, 
				rolActual,
				isVistaMoviments);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public ContingutDto findAmbIdAdmin(
			Long entitatId,
			Long contingutId,
			boolean ambFills) {
		return delegate.findAmbIdAdmin(
				entitatId,
				contingutId,
				ambFills);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public List<ContingutLogDto> findLogsPerContingutAdmin(
			Long entitatId,
			Long contingutId) {
		return delegate.findLogsPerContingutAdmin(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ContingutLogDto> findLogsPerContingutUser(
			Long entitatId,
			Long contingutId) {
		return delegate.findLogsPerContingutUser(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public ContingutLogDetallsDto findLogDetallsPerContingutUser(
			Long entitatId,
			Long contingutId,
			Long contingutLogId) throws NotFoundException {
		return delegate.findLogDetallsPerContingutUser(
				entitatId,
				contingutId,
				contingutLogId);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public List<ContingutMovimentDto> findMovimentsPerContingutAdmin(
			Long entitatId,
			Long contingutId) {
		return delegate.findMovimentsPerContingutAdmin(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ContingutMovimentDto> findMovimentsPerContingutUser(
			Long entitatId,
			Long contingutId) {
		return delegate.findMovimentsPerContingutUser(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("DIS_ADMIN")
	public PaginaDto<ContingutDto> findAdmin(
			Long entitatId,
			ContingutFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegate.findAdmin(
				entitatId,
				filtre,
				paginacioParams);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ContingutComentariDto> findComentarisPerContingut(Long entitatId, Long contingutId)
			throws NotFoundException {
		return delegate.findComentarisPerContingut(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public RespostaPublicacioComentariDto publicarComentariPerContingut(Long entitatId, Long contingutId, String text)
			throws NotFoundException {
		return delegate.publicarComentariPerContingut(entitatId, contingutId, text);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean marcarProcessat(Long entitatId, Long contingutId, String text, String rolActual) throws NotFoundException {
		return delegate.marcarProcessat(entitatId, contingutId, text, rolActual);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ContingutLogDetallsDto> findLogsDetallsPerContingutUser(Long entitatId,
			Long contingutId) {
		return delegate.findLogsDetallsPerContingutUser(entitatId, contingutId);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean hasPermisSobreBustia(Long entitatId, Long contingutId) throws NotFoundException {
		return delegate.hasPermisSobreBustia(entitatId, contingutId);
	}
	

	@Override
	@RolesAllowed({"DIS_REPORT", "DIS_ADMIN"})
	public List<LogsDadesObertesDto> findLogsPerDadesObertes(
			Date dataInici, 
			Date dataFi, 
			LogTipusEnumDto tipus, 
			String usuari,
			Long anotacioId, 
			RegistreProcesEstatEnum anotacioEstat, 
			Boolean errorEstat, 
			Boolean pendent, 
			Long bustiaOrigen,
			Long bustiaDesti, 
			String uoOrigen, 
			String uoSuperior, 
			String uoDesti, 
			String uoDestiSuperior) {
		return delegate.findLogsPerDadesObertes(dataInici, dataFi, tipus, usuari, anotacioId, anotacioEstat, errorEstat, 
				pendent, bustiaOrigen, bustiaDesti, uoOrigen, uoSuperior, uoDesti, uoDestiSuperior);
	}


}