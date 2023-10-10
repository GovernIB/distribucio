/**
 * 
 */
package es.caib.distribucio.back.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWarDeployment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ejb.access.LocalStatelessSessionProxyFactoryBean;

import es.caib.distribucio.logic.intf.config.BaseConfig;
import es.caib.distribucio.logic.intf.service.AlertaService;
import es.caib.distribucio.logic.intf.service.AplicacioService;
import es.caib.distribucio.logic.intf.service.AvisService;
import es.caib.distribucio.logic.intf.service.BackofficeService;
import es.caib.distribucio.logic.intf.service.BustiaService;
import es.caib.distribucio.logic.intf.service.ConfigService;
import es.caib.distribucio.logic.intf.service.ContingutService;
import es.caib.distribucio.logic.intf.service.DominiService;
import es.caib.distribucio.logic.intf.service.EntitatService;
import es.caib.distribucio.logic.intf.service.HistoricService;
import es.caib.distribucio.logic.intf.service.MetaDadaService;
import es.caib.distribucio.logic.intf.service.MonitorIntegracioService;
import es.caib.distribucio.logic.intf.service.MonitorTasquesService;
import es.caib.distribucio.logic.intf.service.ProcedimentService;
import es.caib.distribucio.logic.intf.service.RegistreService;
import es.caib.distribucio.logic.intf.service.ReglaService;
import es.caib.distribucio.logic.intf.service.SegonPlaService;
import es.caib.distribucio.logic.intf.service.UnitatOrganitzativaService;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuració d'accés als services de Spring mitjançant EJBs.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Configuration
@ConditionalOnWarDeployment
public class EjbClientConfig {

	static final String EJB_JNDI_PREFIX = "java:app/" + BaseConfig.APP_NAME + "-ejb/";
	static final String EJB_JNDI_SUFFIX = "Ejb";

	@Bean
	public LocalStatelessSessionProxyFactoryBean alertaService() {
		return getLocalEjbFactoyBean(AlertaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean aplicacioService() {
		return getLocalEjbFactoyBean(AplicacioService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean avisService() {
		return getLocalEjbFactoyBean(AvisService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean backofficeService() {
		return getLocalEjbFactoyBean(BackofficeService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean bustiaService() {
		return getLocalEjbFactoyBean(BustiaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean configService() {
		return getLocalEjbFactoyBean(ConfigService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean contingutService() {
		return getLocalEjbFactoyBean(ContingutService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean dominiService() {
		return getLocalEjbFactoyBean(DominiService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean entitatService() {
		return getLocalEjbFactoyBean(EntitatService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean historicService() {
		return getLocalEjbFactoyBean(HistoricService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean metaDadaService() {
		return getLocalEjbFactoyBean(MetaDadaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean monitorIntegracioService() {
		return getLocalEjbFactoyBean(MonitorIntegracioService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean monitorTasquesService() {
		return getLocalEjbFactoyBean(MonitorTasquesService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean procedimentService() {
		return getLocalEjbFactoyBean(ProcedimentService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean registreService() {
		return getLocalEjbFactoyBean(RegistreService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean reglaService() {
		return getLocalEjbFactoyBean(ReglaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean segonPlaService() {
		return getLocalEjbFactoyBean(SegonPlaService.class);
	}

	@Bean
	public LocalStatelessSessionProxyFactoryBean unitatOrganitzativaService() {
		return getLocalEjbFactoyBean(UnitatOrganitzativaService.class);
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
