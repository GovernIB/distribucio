/**
 * 
 */
package es.caib.distribucio.ejb;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ArxiuDetallDto;
import es.caib.distribucio.logic.intf.dto.BustiaDto;
import es.caib.distribucio.logic.intf.dto.ClassificacioResultatDto;
import es.caib.distribucio.logic.intf.dto.ContingutDto;
import es.caib.distribucio.logic.intf.dto.FitxerDto;
import es.caib.distribucio.logic.intf.dto.HistogramPendentsEntryDto;
import es.caib.distribucio.logic.intf.dto.PaginaDto;
import es.caib.distribucio.logic.intf.dto.PaginacioParamsDto;
import es.caib.distribucio.logic.intf.dto.ProcedimentDto;
import es.caib.distribucio.logic.intf.dto.RegistreAnnexDto;
import es.caib.distribucio.logic.intf.dto.RegistreAnnexFirmaDto;
import es.caib.distribucio.logic.intf.dto.RegistreDto;
import es.caib.distribucio.logic.intf.dto.RegistreFiltreDto;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.registre.ValidacioFirmaEnum;
import es.caib.distribucio.logic.intf.service.RegistreService;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.logic.intf.service.ws.backoffice.Estat;
import lombok.experimental.Delegate;

/**
 * Implementació de RegistreService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class RegistreServiceEjb extends AbstractServiceEjb<RegistreService> implements RegistreService {

	@Delegate
	private RegistreService delegateService = null;

	@Override
	@RolesAllowed("**")
	public RegistreDto findOne(
			Long entitatId,
			Long registreId,
			boolean isVistaMoviments) {
		return delegateService.findOne(
				entitatId,
				registreId,
				isVistaMoviments);
	}

	@Override
	@RolesAllowed("**")
	public RegistreDto findOne(
			Long entitatId,
			Long registreId,
			boolean isVistaMoviments,
			String rolActual) {
		return delegateService.findOne(
				entitatId,
				registreId,
				isVistaMoviments,
				rolActual);
	}

	@Override
	@RolesAllowed("**")
	public List<RegistreDto> findMultiple(
			Long entitatId,
			List<Long> multipleRegistreIds,
			boolean isAdmin)
			throws NotFoundException {
		return delegateService.findMultiple(
				entitatId,
				multipleRegistreIds,
				isAdmin);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<ContingutDto> findRegistre(
			Long entitatId,
			List<BustiaDto> bustiesUsuari,
			RegistreFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			boolean isAdmin) {
		return delegateService.findRegistre(
				entitatId,
				bustiesUsuari,
				filtre,
				paginacioParams, 
				isAdmin);
	}

	@Override
	@RolesAllowed("**")
	public List<Long> findRegistreIds(
			Long entitatId,
			List<BustiaDto> bustiesUsuari,
			RegistreFiltreDto filtre,
			boolean onlyAmbMoviments,
			boolean isAdmin) {
		return delegateService.findRegistreIds(
				entitatId,
				bustiesUsuari,
				filtre,
				onlyAmbMoviments,
				isAdmin);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public boolean reintentarBustiaPerDefecte(
			Long entitatId,
			Long registreId) {
		return delegateService.reintentarBustiaPerDefecte(
				entitatId,
				registreId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public boolean reintentarProcessamentAdmin(
			Long entitatId,
			Long registreId) {
		return delegateService.reintentarProcessamentAdmin(
				entitatId,
				registreId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public boolean processarAnnexosAdmin(
			Long entitatId,
			Long registreId) {
		return delegateService.processarAnnexosAdmin(
				entitatId,
				registreId);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto getAnnexFitxer(Long annexId, boolean ambVersioImprimible) throws NotFoundException {
		return delegateService.getAnnexFitxer(annexId, ambVersioImprimible);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto getJustificant(Long registreId) throws NotFoundException {
		return delegateService.getJustificant(registreId);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto getAnnexFirmaFitxer(Long annexId,
			int indexFirma) throws NotFoundException {
		return delegateService.getAnnexFirmaFitxer(annexId, indexFirma);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto getZipDocumentacio(Long registreId, String rolActual) throws Exception{
		return delegateService.getZipDocumentacio(registreId, rolActual);
	}

	@Override
	@RolesAllowed("**")
	public RegistreDto marcarLlegida(
			Long entitatId,
			Long registreId) {
		return delegateService.marcarLlegida(
				entitatId,
				registreId);
	}

	@Override
	@RolesAllowed("**")
	public ArxiuDetallDto getArxiuDetall(Long registreAnotacioId) {
		return delegateService.getArxiuDetall(registreAnotacioId);
	}

	@Override
	@RolesAllowed("**")
	public RegistreAnnexDto getRegistreJustificant(
			Long entitatId,
			Long registreId,
			boolean isVistaMoviments) {
		return delegateService.getRegistreJustificant(entitatId, registreId, isVistaMoviments);
	}

	@Override
	@RolesAllowed("**")
	public RegistreAnnexDto getAnnexSenseFirmes(
			Long entitatId,
			Long registreId,
			Long annexId,
			boolean isVistaMoviments) throws NotFoundException {
		return delegateService.getAnnexSenseFirmes(entitatId, registreId, annexId, isVistaMoviments);
	}

	@Override
	@RolesAllowed("**")
	public RegistreAnnexDto getAnnexAmbFirmes(
			Long entitatId,
			Long registreId,
			Long annexId,
			boolean isVistaMoviments) throws NotFoundException {
		return delegateService.getAnnexAmbFirmes(entitatId, registreId, annexId, isVistaMoviments);
	}

	@Override
	@RolesAllowed("**")
	public AnotacioRegistreEntrada findOneForBackoffice(AnotacioRegistreId id) {
		return delegateService.findOneForBackoffice(id);
	}
	
	@Override
	@RolesAllowed("**")
	public void canviEstatComunicadaARebuda(
			long registreId,			
			String observacions) {
		delegateService.canviEstatComunicadaARebuda(registreId, observacions);
	}

	@Override
	@RolesAllowed("**")
	public void canviEstat(
			long id,
			Estat estat,
			String observacions) {
		delegateService.canviEstat(id, estat, observacions);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public boolean reintentarEnviamentBackofficeAdmin(
			Long entitatId,
			Long registreId) {
		return delegateService.reintentarEnviamentBackofficeAdmin(entitatId, registreId);
	}

	@Override
	@RolesAllowed("**")
	public ClassificacioResultatDto classificar(
			Long entitatId,
			Long registreId,
			String procedimentCodi,
			String serveiCodi,
			String titol) throws NotFoundException {
		return delegateService.classificar(
				entitatId,
				registreId,
				procedimentCodi,
				serveiCodi,
				titol);
	}

	@Override
	@RolesAllowed("**")
	public List<ProcedimentDto> classificarFindProcediments(
			Long entitatId,
			Long bustiaId) {
		return delegateService.classificarFindProcediments(
				entitatId,
				bustiaId);
	}

	@Override
	@RolesAllowed("**")
	public List<HistogramPendentsEntryDto> getHistogram() {
		return delegateService.getHistogram();
	}

	@Override
	@RolesAllowed("**")
	public int getNumberThreads() {
		return delegateService.getNumberThreads();
	}

	@Override
	@RolesAllowed("**")
	public void bloquejar(Long entitatId, Long id) {
		delegateService.bloquejar(entitatId, id);
	}

	@Override
	@RolesAllowed("**")
	public void alliberar(Long entitatId, Long id) {
		delegateService.alliberar(entitatId, id);
	}

	@Override
	@RolesAllowed("**")
	public List<String> findRegistreMovimentsIds(
			Long entitatId,
			List<BustiaDto> bustiesUsuari,
			RegistreFiltreDto filtre,
			boolean isAdmin) {
		return delegateService.findRegistreMovimentsIds(
				entitatId,
				bustiesUsuari,
				filtre,
				isAdmin);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<ContingutDto> findMovimentsRegistre(
			Long entitatId,
			List<BustiaDto> bustiesPermesesPerUsuari,
			RegistreFiltreDto filtre,
			PaginacioParamsDto paginacioParams) {
		return delegateService.findMovimentsRegistre(
				entitatId,
				bustiesPermesesPerUsuari,
				filtre, 
				paginacioParams);
	}

	@Override
	@RolesAllowed("**")
	public PaginaDto<ContingutDto> findMovimentRegistre(
			Long entitatId,
			List<BustiaDto> bustiesUsuari,
			RegistreFiltreDto filtre,
			PaginacioParamsDto paginacioParams,
			boolean isAdmin) throws NotFoundException {
		return delegateService.findMovimentRegistre(
				entitatId,
				bustiesUsuari,
				filtre,
				paginacioParams,
				isAdmin);
	}

	@Override
	@RolesAllowed("**")
	public List<ContingutDto> getPathContingut(Long entitatId, Long bustiaId) throws NotFoundException {
		return delegateService.getPathContingut(entitatId, bustiaId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public void marcarSobreescriure(
			Long entitatId,
			Long registreId) {
		delegateService.marcarSobreescriure(
				entitatId, 
				registreId);
	}

	@Override
	@RolesAllowed("**")
	public boolean marcarPendent(
			Long entitatId,
			Long registreId,
			String text,
			String rolActual) {
		return delegateService.marcarPendent(
				entitatId,
				registreId,
				text,
				rolActual);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public ValidacioFirmaEnum validarFirmes(
			Long entitatId,
			Long registreId,
			Long annexId) {
		return delegateService.validarFirmes(entitatId, registreId, annexId);
	}

	@Override
	@RolesAllowed("**")
	public List<RegistreAnnexFirmaDto> getDadesAnnexFirmesSenseDetall(Long annexId) {
		return delegateService.getDadesAnnexFirmesSenseDetall(annexId);
	}

	@Override
	public String obtenirRegistreIdEncriptat(Long registreId) {
		return delegateService.obtenirRegistreIdEncriptat(registreId);
	}

	@Override
	public String obtenirRegistreIdDesencriptat(String clau) throws Exception{
		return delegateService.obtenirRegistreIdDesencriptat(clau);
	}

	@Override
	public boolean reintentarProcessamentUser(Long entitatId, Long registreId) {
		return delegateService.reintentarProcessamentUser(entitatId, registreId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public void dadaSave(
			Long entitatId,
			Long registreId,
			Map<String, Object> valors) throws NotFoundException {
		delegateService.dadaSave(
				entitatId, 
				registreId, 
				valors);
	}

	@Override
	@RolesAllowed("**")
	public RegistreDto findOneAmbDades(
			Long entitatId,
			Long registreId,
			boolean isVistaMoviments,
			String rolActual)
			throws NotFoundException {
		return delegateService.findOneAmbDades(
				entitatId,
				registreId, 
				isVistaMoviments, 
				rolActual);
	}

	@Override
	@RolesAllowed("**")
	public void assignar(Long entitatId, Long registreId, String usuariCodi, String comentari) {
		delegateService.assignar(
				entitatId,
				registreId,
				usuariCodi,
				comentari);
	}
	
	@Override
	@RolesAllowed("**")
	public List<AnotacioRegistreEntrada> findForBackoffice(String identificador, Date dataRegistre) {
		return delegateService.findForBackoffice(
				identificador, 
				dataRegistre);
	}

	protected void setDelegateService(RegistreService delegateService) {
		this.delegateService = delegateService;
	}

}
