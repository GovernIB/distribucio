/**
 * 
 */
package es.caib.distribucio.api.externa.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.service.BustiaService;
import es.caib.distribucio.logic.intf.service.ContingutService;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuració d'accés als services de Spring mitjançant EJBs.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Configuration("apiExternaEjbClientConfig")
public class EjbClientConfig {

	static final String EJB_JNDI_PREFIX = "java:app/" + BaseConfig.APP_NAME + "-ejb/";
	static final String EJB_JNDI_SUFFIX = "Ejb";

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean bustiaService() {
		return getLocalEjbFactoyBean(BustiaService.class);
	}

	@Bean
	@ConditionalOnWarDeployment
	public LocalStatelessSessionProxyFactoryBean contingutService() {
		return getLocalEjbFactoyBean(ContingutService.class);
	}

	private LocalStatelessSessionProxyFactoryBean getLocalEjbFactoyBean(Class<?> serviceClass) {
		String jndiName = jndiServiceName(serviceClass, false);
		log.info("Creating EJB proxy for " + serviceClass.getSimpleName() + " with JNDI name " + jndiName);
		LocalStatelessSessionProxyFactoryBean factoryBean = new LocalStatelessSessionProxyFactoryBean();
		factoryBean.setBusinessInterface(serviceClass);
		factoryBean.setExpectedType(serviceClass);
		factoryBean.setJndiName(jndiName);
		return factoryBean;
	}

	private String jndiServiceName(Class<?> serviceClass, boolean addServiceClassName) {
		return EJB_JNDI_PREFIX + serviceClass.getSimpleName() + EJB_JNDI_SUFFIX + (addServiceClassName ? "!" + serviceClass.getName() : "");
	}

}