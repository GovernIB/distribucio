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

import es.caib.distribucio.core.api.dto.ArxiuDetallDto;
import es.caib.distribucio.core.api.dto.ClassificacioResultatDto;
import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.ProcedimentDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDetallDto;
import es.caib.distribucio.core.api.dto.RegistreAnotacioDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatSistraEnum;
import es.caib.distribucio.core.api.service.RegistreService;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.core.api.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.core.api.service.ws.backoffice.Estat;

/**
 * Implementació de RegistreService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class RegistreServiceBean implements RegistreService {

	@Autowired
	private RegistreService delegate;

	@Override
	@RolesAllowed("tothom")
	public RegistreAnotacioDto findOne(
			Long entitatId,
			Long contenidorId,
			Long registreId) {
		return delegate.findOne(
				entitatId,
				contenidorId,
				registreId);
	}

	@Override
	public List<RegistreAnotacioDto> findMultiple(
			Long entitatId,
			List<Long> multipleRegistreIds)
			throws NotFoundException {
		return delegate.findMultiple(
				entitatId,
				multipleRegistreIds);
	}

	@Override
	@RolesAllowed("tothom")
	public void rebutjar(
			Long entitatId,
			Long bustiaId,
			Long registreId,
			String motiu) {
		delegate.rebutjar(entitatId, bustiaId, registreId, motiu);
	}
	
	@Override
	@RolesAllowed("DIS_ADMIN")
	public boolean reintentarProcessamentAdmin(
			Long entitatId,
			Long bustiaId,
			Long registreId) {
		return delegate.reintentarProcessamentAdmin(
				entitatId,
				bustiaId,
				registreId);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean reintentarProcessamentUser(
			Long entitatId,
			Long bustiaId,
			Long registreId) {
		return delegate.reintentarProcessamentUser(
				entitatId,
				bustiaId,
				registreId);
	}

	@Override
	@RolesAllowed("tothom")
	public List<RegistreAnnexDetallDto> getAnnexosAmbArxiu(
			Long entitatId, 
			Long contingutId, 
			Long registreId)
			throws NotFoundException {
		return delegate.getAnnexosAmbArxiu(
				entitatId, 
				contingutId, 
				registreId);
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto getArxiuAnnex(Long annexId) throws NotFoundException {
		return delegate.getArxiuAnnex(annexId);
	}
	
	@Override
	@RolesAllowed("tothom")
	public FitxerDto getAnnexFirmaContingut(Long annexId,
			int indexFirma) throws NotFoundException {
		return delegate.getAnnexFirmaContingut(annexId, indexFirma);
	}

	@Override
	@RolesAllowed("tothom")
	public RegistreAnotacioDto findAmbIdentificador(String identificador) {
		return delegate.findAmbIdentificador(identificador);
	}

	@Override
	@RolesAllowed("tothom")
	public void updateProces(Long registreId, RegistreProcesEstatEnum procesEstat,
			RegistreProcesEstatSistraEnum procesEstatSistra, String resultadoProcesamiento) {
		delegate.updateProces(registreId, procesEstat, procesEstatSistra, resultadoProcesamiento);
	}

	@Override
	@RolesAllowed("tothom")
	public List<String> findPerBackofficeSistra(String identificadorProcediment, String identificadorTramit,
			RegistreProcesEstatSistraEnum procesEstatSistra, Date desdeDate, Date finsDate) {
		return delegate.findPerBackofficeSistra(identificadorProcediment, identificadorTramit, procesEstatSistra, desdeDate, finsDate);
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto getJustificant(Long registreId) throws NotFoundException {
		return delegate.getJustificant(registreId);
	}

	@Override
	public RegistreAnotacioDto marcarLlegida(
			Long entitatId,
			Long contingutId,
			Long registreId) {
		return delegate.marcarLlegida(
				entitatId,
				contingutId,
				registreId);
	}

	@Override
	@RolesAllowed("tothom")
	public RegistreAnnexDetallDto getRegistreJustificant(Long entitatId, Long contingutId, Long registreId) {
		return delegate.getRegistreJustificant(entitatId, contingutId, registreId);
	}

	@Override
	@RolesAllowed("tothom")
	public RegistreAnnexDetallDto getAnnexAmbArxiu(Long entitatId, Long contingutId, Long registreId,
			String fitxerArxiuUuid) throws NotFoundException {
		return delegate.getAnnexAmbArxiu(entitatId, contingutId, registreId, fitxerArxiuUuid);
	}

	@Override
	@RolesAllowed("tothom")
	public RegistreAnnexDetallDto getAnnexFirmesAmbArxiu(Long entitatId, Long contingutId, Long registreId,
			String fitxerArxiuUuid) throws NotFoundException {
		return delegate.getAnnexFirmesAmbArxiu(entitatId, contingutId, registreId, fitxerArxiuUuid);
	}
	
	@Override
	@RolesAllowed("tothom")
	public List<RegistreAnnexDetallDto> getAnnexos(Long entitatId, Long contingutId, Long registreId)
			throws NotFoundException {
		return delegate.getAnnexos(entitatId, contingutId, registreId);
	}


	@Override
	@RolesAllowed("tothom")
	public ArxiuDetallDto getArxiuDetall(Long registreAnotacioId) {
		return delegate.getArxiuDetall(registreAnotacioId);
	}

	@Override
	@RolesAllowed("tothom")
	public AnotacioRegistreEntrada findOneForBackoffice(AnotacioRegistreId id) {
		return delegate.findOneForBackoffice(id);
	}

	@Override
	public void canviEstat(AnotacioRegistreId id,
			Estat estat,
			String observacions) {
		delegate.canviEstat(id, estat, observacions);		
	}

	@Override
	public boolean reintentarEnviamentBackofficeAdmin(Long entitatId,
			Long bustiaId,
			Long registreId) {
		return delegate.reintentarEnviamentBackofficeAdmin(entitatId, bustiaId, registreId);
	}

	@Override
	@RolesAllowed("tothom")
	public ClassificacioResultatDto classificar(
			Long entitatId,
			Long contingutId,
			Long registreId,
			String procedimentCodi) throws NotFoundException {
		return delegate.classificar(
				entitatId,
				contingutId,
				registreId,
				procedimentCodi);
	}

	@Override
	@RolesAllowed("tothom")
	public List<ProcedimentDto> classificarFindProcediments(
			Long entitatId,
			Long bustiaId) {
		return delegate.classificarFindProcediments(
				entitatId,
				bustiaId);
	}

}
