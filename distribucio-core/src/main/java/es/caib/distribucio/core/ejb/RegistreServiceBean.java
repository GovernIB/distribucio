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
import es.caib.distribucio.core.api.dto.BustiaDto;
import es.caib.distribucio.core.api.dto.RegistreFiltreDto;
import es.caib.distribucio.core.api.dto.ClassificacioResultatDto;
import es.caib.distribucio.core.api.dto.ContingutDto;
import es.caib.distribucio.core.api.dto.FitxerDto;
import es.caib.distribucio.core.api.dto.HistogramPendentsEntryDto;
import es.caib.distribucio.core.api.dto.PaginaDto;
import es.caib.distribucio.core.api.dto.PaginacioParamsDto;
import es.caib.distribucio.core.api.dto.ProcedimentDto;
import es.caib.distribucio.core.api.dto.RegistreAnnexDto;
import es.caib.distribucio.core.api.dto.RegistreDto;
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
	public RegistreDto findOne(
			Long entitatId,
			Long registreId) {
		return delegate.findOne(
				entitatId,
				registreId);
	}

	@Override
	public List<RegistreDto> findMultiple(
			Long entitatId,
			List<Long> multipleRegistreIds)
			throws NotFoundException {
		return delegate.findMultiple(
				entitatId,
				multipleRegistreIds);
	}
	
	@Override
	@RolesAllowed("tothom")
	public PaginaDto<ContingutDto> findRegistre(
			Long entitatId,
			List<BustiaDto> bustiesUsuari,
			RegistreFiltreDto filtre,
			boolean onlyAmbMoviments,
			PaginacioParamsDto paginacioParams, 
			boolean isAdmin) {
		return delegate.findRegistre(
				entitatId,
				bustiesUsuari,
				filtre,
				onlyAmbMoviments,
				paginacioParams, 
				isAdmin);
	}

	

	@Override
	@RolesAllowed("tothom")
	public List<Long> findRegistreIds(
			Long entitatId,
			List<BustiaDto> bustiesUsuari,
			RegistreFiltreDto filtre,
			boolean onlyAmbMoviments, 
			boolean isAdmin) {
		return delegate.findRegistreIds(
				entitatId,
				bustiesUsuari,
				filtre,
				onlyAmbMoviments, 
				isAdmin);
	}
	
	@Override
	@RolesAllowed("DIS_ADMIN")
	public boolean reintentarBustiaPerDefecte(
			Long entitatId,
			Long registreId) {
		return delegate.reintentarBustiaPerDefecte(
				entitatId,
				registreId);
	}
	
	@Override
	@RolesAllowed("DIS_ADMIN")
	public boolean reintentarProcessamentAdmin(
			Long entitatId,
			Long registreId) {
		return delegate.reintentarProcessamentAdmin(
				entitatId,
				registreId);
	}

	@Override
	@RolesAllowed("tothom")
	public boolean reintentarProcessamentUser(
			Long entitatId,
			Long registreId) {
		return delegate.reintentarProcessamentUser(
				entitatId,
				registreId);
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto getAnnexFitxer(Long annexId) throws NotFoundException {
		return delegate.getAnnexFitxer(annexId);
	}
	
	@Override
	@RolesAllowed("tothom")
	public FitxerDto getAnnexFirmaFitxer(Long annexId,
			int indexFirma) throws NotFoundException {
		return delegate.getAnnexFirmaFitxer(annexId, indexFirma);
	}
	
	@Override
	@RolesAllowed("tothom")
	public FitxerDto getZipDocumentacio(Long registreId, String rolActual) throws Exception{
		return delegate.getZipDocumentacio(registreId, rolActual);
	}


	@Override
	@RolesAllowed("tothom")
	public RegistreDto findAmbIdentificador(String identificador) {
		return delegate.findAmbIdentificador(identificador);
	}

	@Override
	@RolesAllowed("tothom")
	public FitxerDto getJustificant(Long registreId) throws NotFoundException {
		return delegate.getJustificant(registreId);
	}

	@Override
	public RegistreDto marcarLlegida(
			Long entitatId,
			Long registreId) {
		return delegate.marcarLlegida(
				entitatId,
				registreId);
	}

	@Override
	@RolesAllowed("tothom")
	public RegistreAnnexDto getRegistreJustificant(Long entitatId, Long registreId) {
		return delegate.getRegistreJustificant(entitatId, registreId);
	}

	@Override
	@RolesAllowed("tothom")
	public RegistreAnnexDto getAnnexSenseFirmes(Long entitatId, Long registreId,
			Long annexId) throws NotFoundException {
		return delegate.getAnnexSenseFirmes(entitatId, registreId, annexId);
	}

	@Override
	@RolesAllowed("tothom")
	public RegistreAnnexDto getAnnexAmbFirmes(Long entitatId, Long registreId,
			Long annexId) throws NotFoundException {
		return delegate.getAnnexAmbFirmes(entitatId, registreId, annexId);
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
	@RolesAllowed("DIS_ADMIN")
	public boolean reintentarEnviamentBackofficeAdmin(Long entitatId,
			Long registreId) {
		return delegate.reintentarEnviamentBackofficeAdmin(entitatId, registreId);
	}

	@Override
	@RolesAllowed("tothom")
	public ClassificacioResultatDto classificar(
			Long entitatId,
			Long registreId,
			String procedimentCodi) throws NotFoundException {
		return delegate.classificar(
				entitatId,
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

	@Override
	@RolesAllowed("tothom")
	public List<HistogramPendentsEntryDto> getHistogram() {
		return delegate.getHistogram();
	}

	@Override
	@RolesAllowed("tothom")
	public int getNumberThreads() {
		return delegate.getNumberThreads();
	}

	@Override
	@RolesAllowed("tothom")
	public void bloquejar(Long entitatId, Long id) {
		delegate.bloquejar(entitatId, id);
	}

	@Override
	@RolesAllowed("tothom")
	public void alliberar(Long entitatId, Long id) {
		delegate.alliberar(entitatId, id);
	}

}
