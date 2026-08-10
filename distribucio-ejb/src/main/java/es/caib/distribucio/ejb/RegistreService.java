/**
 * 
 */
package es.caib.distribucio.ejb;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.*;
import es.caib.distribucio.logic.intf.exception.NotFoundException;
import es.caib.distribucio.logic.intf.registre.ValidacioFirmaEnum;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreEntrada;
import es.caib.distribucio.logic.intf.service.ws.backoffice.AnotacioRegistreId;
import es.caib.distribucio.logic.intf.service.ws.backoffice.Estat;

/**
 * Implementació de RegistreService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class RegistreService extends AbstractService<es.caib.distribucio.logic.intf.service.RegistreService> implements es.caib.distribucio.logic.intf.service.RegistreService {

	@Override
	@RolesAllowed("**")
	public RegistreDto findOne(
			Long entitatId,
			Long registreId,
			boolean isVistaMoviments) {
		return getDelegateService().findOne(
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
		return getDelegateService().findOne(
				entitatId,
				registreId,
				isVistaMoviments,
				rolActual);
	}

	@Override
	@RolesAllowed("**")
	public List<RegistreDto> findByEntitatCodiAndNumero(
			Long entitatId,
			String numero) throws NotFoundException {
		return getDelegateService().findByEntitatCodiAndNumero(
				entitatId,
				numero);
	}

	@Override
	@RolesAllowed("**")
	public List<RegistreDto> findMultiple(
			Long entitatId,
			List<Long> multipleRegistreIds,
			boolean isAdmin)
			throws NotFoundException {
		return getDelegateService().findMultiple(
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
		return getDelegateService().findRegistre(
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
		return getDelegateService().findRegistreIds(
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
		return getDelegateService().reintentarBustiaPerDefecte(
				entitatId,
				registreId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public boolean reintentarProcessamentAdmin(
			Long entitatId,
			Long registreId) {
		return getDelegateService().reintentarProcessamentAdmin(
				entitatId,
				registreId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public boolean processarAnnexosAdmin(
			Long entitatId,
			Long registreId) {
		return getDelegateService().processarAnnexosAdmin(
				entitatId,
				registreId);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto getAnnexFitxer(Long annexId, boolean ambVersioImprimible) throws NotFoundException {
		return getDelegateService().getAnnexFitxer(annexId, ambVersioImprimible);
	}

	@Override
	@RolesAllowed("**")
	public FitxerDto getJustificant(Long registreId) throws Exception {
		return getDelegateService().getJustificant(registreId);
	}

	@Override
	@RolesAllowed("**")
	public String getNumeroById(Long registreId) throws NotFoundException {
		return getDelegateService().getNumeroById(registreId);
	}
	
	@Override
	@RolesAllowed("**")
	public FitxerDto getAnnexFirmaFitxer(Long annexId,
			int indexFirma) throws NotFoundException {
		return getDelegateService().getAnnexFirmaFitxer(annexId, indexFirma);
	}

	@Override	
	@PermitAll
	public FitxerDto getZipDocumentacio(Long registreId, String rolActual, boolean ambVersioImprimible) throws Exception{
		return getDelegateService().getZipDocumentacio(registreId, rolActual, ambVersioImprimible);
	}

	@Override
	@RolesAllowed("**")
	public RegistreDto marcarLlegida(
			Long entitatId,
			Long registreId) {
		return getDelegateService().marcarLlegida(
				entitatId,
				registreId);
	}

	@Override
	@RolesAllowed("**")
	public ArxiuDetallDto getArxiuDetall(Long registreAnotacioId) {
		return getDelegateService().getArxiuDetall(registreAnotacioId);
	}

	@Override
	@RolesAllowed("**")
	public RegistreAnnexDto getRegistreJustificant(
			Long entitatId,
			Long registreId,
			boolean isVistaMoviments) {
		return getDelegateService().getRegistreJustificant(entitatId, registreId, isVistaMoviments);
	}

	@Override
	@RolesAllowed("**")
	public RegistreAnnexDto getAnnexSenseFirmes(
			Long entitatId,
			Long registreId,
			Long annexId,
			boolean isVistaMoviments) throws NotFoundException {
		return getDelegateService().getAnnexSenseFirmes(entitatId, registreId, annexId, isVistaMoviments);
	}

	@Override
	@RolesAllowed("**")
	public RegistreAnnexDto getAnnexAmbFirmes(
			Long entitatId,
			Long registreId,
			Long annexId,
			boolean isVistaMoviments) throws NotFoundException {
		return getDelegateService().getAnnexAmbFirmes(entitatId, registreId, annexId, isVistaMoviments);
	}

	@Override
	@RolesAllowed("**")
	public AnotacioRegistreEntrada findOneForBackoffice(AnotacioRegistreId id) {
		return getDelegateService().findOneForBackoffice(id);
	}

	@Override
	@RolesAllowed("**")
	public List<Long> findRegistresPerIdentificador(AnotacioRegistreId id) throws Exception {
		return getDelegateService().findRegistresPerIdentificador(id);
	}
	
	@Override
	@RolesAllowed("**")
	public void canviEstatComunicadaARebuda(
			long registreId,			
			String observacions) {
		getDelegateService().canviEstatComunicadaARebuda(registreId, observacions);
	}

	@Override
	@RolesAllowed("**")
	public void canviEstat(
			long id,
			Estat estat,
			String observacions) {
		getDelegateService().canviEstat(id, estat, observacions);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public Throwable reintentarEnviamentBackofficeAdmin(
			Long entitatId,
			Long registreId) {
		return getDelegateService().reintentarEnviamentBackofficeAdmin(entitatId, registreId);
	}

	@Override
	@RolesAllowed("**")
	public ClassificacioResultatDto classificar(
			Long entitatId,
			Long registreId,
            String tipus,
			String procedimentCodi,
			String serveiCodi,
			String titol) throws NotFoundException {
		return getDelegateService().classificar(
				entitatId,
				registreId,
                tipus,
				procedimentCodi,
				serveiCodi,
				titol);
	}

	@Override
	@RolesAllowed("**")
	public List<ProcedimentDto> classificarFindProcediments(
			Long entitatId,
			Long bustiaId) {
		return getDelegateService().classificarFindProcediments(
				entitatId,
				bustiaId);
	}
	
	@Override
	@RolesAllowed("**")
	public List<ServeiDto> classificarFindServeis(
			Long entitatId,
			Long bustiaId) {
		return getDelegateService().classificarFindServeis(
				entitatId,
				bustiaId);
	}

	@Override
	@RolesAllowed("**")
	public List<HistogramPendentsEntryDto> getHistogram() {
		return getDelegateService().getHistogram();
	}

	@Override
	@RolesAllowed("**")
	public int getNumberThreads() {
		return getDelegateService().getNumberThreads();
	}

	@Override
	@RolesAllowed("**")
	public void bloquejar(Long entitatId, Long id) {
		getDelegateService().bloquejar(entitatId, id);
	}

	@Override
	@RolesAllowed("**")
	public void alliberar(Long entitatId, Long id) {
		getDelegateService().alliberar(entitatId, id);
	}

	@Override
	@RolesAllowed("**")
	public List<String> findRegistreMovimentsIds(
			Long entitatId,
			List<BustiaDto> bustiesUsuari,
			RegistreFiltreDto filtre,
			boolean isAdmin) {
		return getDelegateService().findRegistreMovimentsIds(
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
		return getDelegateService().findMovimentsRegistre(
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
		return getDelegateService().findMovimentRegistre(
				entitatId,
				bustiesUsuari,
				filtre,
				paginacioParams,
				isAdmin);
	}

	@Override
	@RolesAllowed("**")
	public List<ContingutDto> getPathContingut(Long entitatId, Long bustiaId) throws NotFoundException {
		return getDelegateService().getPathContingut(entitatId, bustiaId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public void marcarSobreescriure(
			Long entitatId,
			Long registreId) {
		getDelegateService().marcarSobreescriure(
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
		return getDelegateService().marcarPendent(
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
		return getDelegateService().validarFirmes(entitatId, registreId, annexId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public void custodiarAnnex(
			Long entitatId,
			Long registreId,
			Long annexId) {
		getDelegateService().custodiarAnnex(entitatId, registreId, annexId);
	}

	@Override
	@RolesAllowed("**")
	public List<RegistreAnnexFirmaDto> getDadesAnnexFirmesSenseDetall(Long annexId) {
		return getDelegateService().getDadesAnnexFirmesSenseDetall(annexId);
	}

	@Override
	public String obtenirRegistreIdEncriptat(Long registreId) {
		return getDelegateService().obtenirRegistreIdEncriptat(registreId);
	}
	
	@Override
	@PermitAll
	public String obtenirRegistreIdDesencriptat(String clau) throws Exception{
		return getDelegateService().obtenirRegistreIdDesencriptat(clau);
	}

	@Override
	public boolean reintentarProcessamentUser(Long entitatId, Long registreId) {
		return getDelegateService().reintentarProcessamentUser(entitatId, registreId);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_ADMIN)
	public void dadaSave(
			Long entitatId,
			Long registreId,
			Map<String, Object> valors) throws NotFoundException {
		getDelegateService().dadaSave(
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
		return getDelegateService().findOneAmbDades(
				entitatId,
				registreId, 
				isVistaMoviments, 
				rolActual);
	}

	@Override
	@RolesAllowed("**")
	public void assignar(Long entitatId, Long registreId, String usuariCodi, String comentari) {
		getDelegateService().assignar(
				entitatId,
				registreId,
				usuariCodi,
				comentari);
	}
	
	@Override
	@RolesAllowed("**")
	public List<AnotacioRegistreEntrada> findForBackoffice(String identificador, Date dataRegistre) {
		return getDelegateService().findForBackoffice(
				identificador, 
				dataRegistre);
	}

}
