/**
 * 
 */
package es.caib.distribucio.ejb;

import java.util.List;
import java.util.Properties;

import javax.annotation.security.RolesAllowed;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.BustiaDto;
import es.caib.distribucio.logic.intf.dto.ExcepcioLogDto;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import es.caib.distribucio.logic.intf.service.AplicacioService;
import lombok.experimental.Delegate;

/**
 * Implementació de AplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AplicacioServiceEjb extends AbstractServiceEjb<AplicacioService> implements AplicacioService {

	@Delegate
	private AplicacioService delegateService = null;

	@Override
	@RolesAllowed("**")
	public String getVersioActual() {
		return delegateService.getVersioActual();
	}

	@Override
	@RolesAllowed("**")
	public String getVersioData() {
		return delegateService.getVersioData();
	}

	@Override
	@RolesAllowed("**")
	public void processarAutenticacioUsuari() {
		delegateService.processarAutenticacioUsuari();
	}

	@Override
	@RolesAllowed("**")
	public UsuariDto getUsuariActual() {
		return delegateService.getUsuariActual();
	}

	@Override
	@RolesAllowed("**")
	public UsuariDto updateUsuariActual(UsuariDto usuari, Long entitatId) {
		return delegateService.updateUsuariActual(usuari, entitatId);
	}

	@Override
	@RolesAllowed("**")
	public UsuariDto findUsuariAmbCodi(String codi) {
		return delegateService.findUsuariAmbCodi(codi);
	}

	@Override
	@RolesAllowed("**")
	public List<UsuariDto> findUsuariAmbText(String text) {
		return delegateService.findUsuariAmbText(text);
	}

	@Override
	@RolesAllowed("**")
	public List<UsuariDto> findUsuariAmbCodiAndNom(String text) {
		return delegateService.findUsuariAmbCodiAndNom(text);
	}

	@Override
	@PermitAll
	public void excepcioSave(Throwable exception, String source) {
		delegateService.excepcioSave(exception, source);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public ExcepcioLogDto excepcioFindOne(Long index) {
		return delegateService.excepcioFindOne(index);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public List<ExcepcioLogDto> excepcioFindAll() {
		return delegateService.excepcioFindAll();
	}

	@Override
	public List<String> permisosFindRolsDistinctAll() {
		return delegateService.permisosFindRolsDistinctAll();
	}

	@RolesAllowed("**")
	public boolean isPluginArxiuActiu() {
		return delegateService.isPluginArxiuActiu();
	}

	@Override
	@RolesAllowed("**")
	public String propertyBaseUrl() {
		return delegateService.propertyBaseUrl();
	}

	@Override
	@RolesAllowed("**")
	public Properties propertyFindByPrefix(String prefix) {
		return delegateService.propertyFindByPrefix(prefix);
	}

	@Override
	@RolesAllowed("**")
	public String propertyPluginPassarelaFirmaIgnorarModalIds() {
		return delegateService.propertyPluginPassarelaFirmaIgnorarModalIds();
	}

	@Override
	@RolesAllowed("**")
	public String propertyFindByNom(String nom) {
		return delegateService.propertyFindByNom(nom);
	}

	@Override
	@RolesAllowed("**")
	public BustiaDto getBustiaPerDefecte(UsuariDto usuari, Long entitatId) {
		return delegateService.getBustiaPerDefecte(usuari, entitatId);
	}

	@Override
	@RolesAllowed("**")
	public void setRolUsuariActual(String rolActual) {
		delegateService.setRolUsuariActual(rolActual);
	}

	@Override
	@RolesAllowed("**")
	public UsuariDto updateUsuari(String codi) {
		return delegateService.updateUsuari(codi);
	}

	protected void setDelegateService(AplicacioService delegateService) {
		this.delegateService = delegateService;
	}

}
