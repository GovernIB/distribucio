package es.caib.distribucio.ejb;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.dto.ConfigDto;
import es.caib.distribucio.logic.intf.dto.ConfigGroupDto;
import es.caib.distribucio.logic.intf.dto.EntitatDto;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Implementació de ConfigService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
public class ConfigService extends AbstractService<es.caib.distribucio.logic.intf.service.ConfigService> implements es.caib.distribucio.logic.intf.service.ConfigService {

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public ConfigDto updateProperty(ConfigDto property) throws Exception{
		return getDelegateService().updateProperty(property);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public List<ConfigGroupDto> findAll(){
		return getDelegateService().findAll();
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public List<ConfigDto> findAllPerEntitat(EntitatDto entitat) {
		return getDelegateService().findAllPerEntitat(entitat);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public void synchronize() {
		getDelegateService().synchronize();
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public void reiniciarTasquesEnSegonPla() {
		getDelegateService().reiniciarTasquesEnSegonPla();
		
	}

	@Override
	@RolesAllowed("**")
	public String getConcsvBaseUrl() {
		return getDelegateService().getConcsvBaseUrl();
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public ConfigDto findByKey(String key) {
		return getDelegateService().findByKey(key);
	}

	@Override
	@RolesAllowed(BaseConfig.ROLE_SUPER)
	public List<ConfigDto> findEntitatsConfigByKey(String key) {
		return getDelegateService().findEntitatsConfigByKey(key);
	}

	@Override
	@RolesAllowed("**")
	public void setEntitatPerPropietat(EntitatDto entitatDto) {
		getDelegateService().setEntitatPerPropietat(entitatDto);
	}
	
	@Override
	@RolesAllowed("**")
	public String getConfig(String key) {
		return getDelegateService().getConfig(key);
	}

	@Override
	@RolesAllowed("**")
	public Long getConfigAsLong(String key) {
		return getDelegateService().getConfigAsLong(key);
	}

}
