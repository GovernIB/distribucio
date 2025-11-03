package es.caib.distribucio.ejb;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;

import es.caib.distribucio.ejb.base.AbstractServiceEjb;
import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ConfigDto;
import es.caib.distribucio.logic.intf.dto.ConfigGroupDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;
import es.caib.distribucio.logic.intf.service.ConfigService;
import lombok.experimental.Delegate;

/**
 * Implementació de ConfigService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ConfigServiceEjb extends AbstractServiceEjb<ConfigService> implements ConfigService {

	@Delegate
	private ConfigService delegateService = null;

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public ConfigDto updateProperty(ConfigDto property) throws Exception{
		return delegateService.updateProperty(property);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public List<ConfigGroupDto> findAll(){
		return delegateService.findAll();
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public List<ConfigDto> findAllPerEntitat(EntitatDto entitat) {
		return delegateService.findAllPerEntitat(entitat);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public void synchronize() {
		delegateService.synchronize();
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public void reiniciarTasquesEnSegonPla() {
		delegateService.reiniciarTasquesEnSegonPla();
		
	}

	@Override
	@RolesAllowed("**")
	public String getConcsvBaseUrl() {
		return delegateService.getConcsvBaseUrl();
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public ConfigDto findByKey(String key) {
		return delegateService.findByKey(key);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public List<ConfigDto> findEntitatsConfigByKey(String key) {
		return delegateService.findEntitatsConfigByKey(key);
	}

	@Override
	@RolesAllowed("**")
	public void setEntitatPerPropietat(EntitatDto entitatDto) {
		delegateService.setEntitatPerPropietat(entitatDto);
	}

	@Override
	@RolesAllowed("**")
	public String getTempsErrorsMonitorIntegracio() {
		return delegateService.getTempsErrorsMonitorIntegracio();
	}
	
	@Override
	@RolesAllowed("**")
	public String getConfig(String key) {
		return delegateService.getConfig(key);
	}

	@Override
	@RolesAllowed("**")
	public Long getConfigAsLong(String key) {
		return delegateService.getConfigAsLong(key);
	}

	protected void setDelegateService(ConfigService delegateService) {
		this.delegateService = delegateService;
	}

}
