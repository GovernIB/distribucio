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
import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDetallDto;
import es.caib.distribucio.core.api.dto.RegistreAnotacioDto;
import es.caib.distribucio.core.api.exception.NotFoundException;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatEnum;
import es.caib.distribucio.core.api.registre.RegistreProcesEstatSistraEnum;
import es.caib.distribucio.core.api.service.RegistreService;

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
	RegistreService delegate;



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
	@RolesAllowed("tothom")
	public void rebutjar(
			Long entitatId,
			Long bustiaId,
			Long registreId,
			String motiu) {
		delegate.rebutjar(entitatId, bustiaId, registreId, motiu);
	}

	@Override
	@RolesAllowed("tothom")
	public void reglaAplicarPendentsBackofficeSistra() {
		delegate.reglaAplicarPendentsBackofficeSistra();
	}
	
	@Override
	@RolesAllowed("DIS_ADMIN")
	public boolean reglaReintentarAdmin(
			Long entitatId,
			Long bustiaId,
			Long registreId) {
		return delegate.reglaReintentarAdmin(
				entitatId,
				bustiaId,
				registreId);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean reglaReintentarUser(
			Long entitatId,
			Long bustiaId,
			Long registreId) {
		return delegate.reglaReintentarUser(
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
	public void distribuirAnotacionsPendents() {
		delegate.distribuirAnotacionsPendents();
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
	public void tancarExpedientsPendents() {
		delegate.tancarExpedientsPendents();
	}

	@Override
	@RolesAllowed("tothom")
	public ArxiuDetallDto getArxiuDetall(Long registreAnotacioId) {
		return delegate.getArxiuDetall(registreAnotacioId);
	}
	
}
