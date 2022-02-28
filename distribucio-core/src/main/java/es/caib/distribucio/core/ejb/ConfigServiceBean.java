package es.caib.distribucio.core.ejb;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import es.caib.distribucio.core.api.dto.ConfigDto;
import es.caib.distribucio.core.api.dto.ConfigGroupDto;
import es.caib.distribucio.core.api.service.ConfigService;

/**
 * Implementació de ConfigService com a EJB que empra una clase
 * delegada per accedir a la funcionalitat del servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class ConfigServiceBean implements ConfigService {

	@Autowired
	ConfigService delegate;

	@Override
	@RolesAllowed({"DIS_SUPER"})
	public ConfigDto updateProperty(ConfigDto property) throws Exception{
		return delegate.updateProperty(property);
	}
	@Override
	@RolesAllowed({"DIS_SUPER"})
	public List<ConfigGroupDto> findAll(){
		return delegate.findAll();
	}
	@Override
	@RolesAllowed({"DIS_SUPER"})
	public void synchronize() {
		delegate.synchronize();
	}
	@Override
	@RolesAllowed({"DIS_SUPER", "DIS_ADMIN", "tothom"})
	public String getConcsvBaseUrl() {
		return delegate.getConcsvBaseUrl();
	}
	@Override
	@RolesAllowed({"DIS_SUPER"})
	public ConfigDto findByKey(String key) {
		return delegate.findByKey(key);
	}
}
