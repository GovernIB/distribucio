/**
 * 
 */
package es.caib.distribucio.ejb;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.BustiaDto;
import es.caib.distribucio.logic.intf.dto.ExcepcioLogDto;
import es.caib.distribucio.logic.intf.dto.UsuariDto;
import org.springframework.security.core.Authentication;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Properties;

/**
 * Implementació de AplicacioService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class AplicacioService extends AbstractService<es.caib.distribucio.logic.intf.service.AplicacioService> implements es.caib.distribucio.logic.intf.service.AplicacioService {

	@Override
	@RolesAllowed("**")
	public String getVersioActual() {
		return getDelegateService().getVersioActual();
	}

	@Override
	@RolesAllowed("**")
	public String getVersioData() {
		return getDelegateService().getVersioData();
	}

	@Override
	@RolesAllowed("**")
	public void processarAutenticacioUsuari() {
		getDelegateService().processarAutenticacioUsuari();
	}

	@Override
	@RolesAllowed("**")
	public UsuariDto getUsuariActual() {
		return getDelegateService().getUsuariActual();
	}

	@Override
	@RolesAllowed("**")
	public UsuariDto updateUsuariActual(UsuariDto usuari, Long entitatId) {
		return getDelegateService().updateUsuariActual(usuari, entitatId);
	}

	@Override
	@RolesAllowed("**")
	public UsuariDto findUsuariAmbCodi(String codi) {
		return getDelegateService().findUsuariAmbCodi(codi);
	}

	@Override
	@RolesAllowed("**")
	public List<UsuariDto> findUsuariAmbText(String text) {
		return getDelegateService().findUsuariAmbText(text);
	}

	@Override
	@RolesAllowed("**")
	public List<UsuariDto> findUsuariAmbCodiAndNom(String text) {
		return getDelegateService().findUsuariAmbCodiAndNom(text);
	}

	@Override
	@PermitAll
	public void excepcioSave(Throwable exception, String source) {
		getDelegateService().excepcioSave(exception, source);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public ExcepcioLogDto excepcioFindOne(Long index) {
		return getDelegateService().excepcioFindOne(index);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public List<ExcepcioLogDto> excepcioFindAll() {
		return getDelegateService().excepcioFindAll();
	}

	@Override
	public List<String> permisosFindRolsDistinctAll() {
		return getDelegateService().permisosFindRolsDistinctAll();
	}

	@RolesAllowed("**")
	public boolean isPluginArxiuActiu() {
		return getDelegateService().isPluginArxiuActiu();
	}

	@Override
	@RolesAllowed("**")
	public String propertyBaseUrl() {
		return getDelegateService().propertyBaseUrl();
	}

	@Override
	@RolesAllowed("**")
	public Properties propertyFindByPrefix(String prefix) {
		return getDelegateService().propertyFindByPrefix(prefix);
	}

	@Override
	@RolesAllowed("**")
	public String propertyPluginPassarelaFirmaIgnorarModalIds() {
		return getDelegateService().propertyPluginPassarelaFirmaIgnorarModalIds();
	}

	@Override
	@RolesAllowed("**")
	public String propertyFindByNom(String nom) {
		return getDelegateService().propertyFindByNom(nom);
	}

	@Override
	@RolesAllowed("**")
	public BustiaDto getBustiaPerDefecte(UsuariDto usuari, Long entitatId) {
		return getDelegateService().getBustiaPerDefecte(usuari, entitatId);
	}

	@Override
	@RolesAllowed("**")
	public void setRolUsuariActual(String rolActual) {
		getDelegateService().setRolUsuariActual(rolActual);
	}

	@Override
	@RolesAllowed("**")
	public UsuariDto updateUsuari(String codi) {
		return getDelegateService().updateUsuari(codi);
	}
	
	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public Long updateUsuariCodi(String codiAntic, String codiNou) {
		return getDelegateService().updateUsuariCodi(codiAntic, codiNou);
	}

	@Override
	@RolesAllowed("**")
	public List<String> getRolsUsuariActual() {
		return getDelegateService().getRolsUsuariActual();
	}

	@Override
	@RolesAllowed("**")
	public Authentication getAuthentication() {
		return getDelegateService().getAuthentication();
	}

}
