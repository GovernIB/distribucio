/**
 * 
 */
package es.caib.distribucio.ejb.base;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;

import lombok.extern.slf4j.Slf4j;

/**
 * Funcionalitat bàsica pels EJBs.
 *
 * @author Límit Tecnologies
 */
@Slf4j
public abstract class AbstractServiceEjb<S> {

	private Class<S> serviceClass;

	@Resource
	protected SessionContext sessionContext;

	@PostConstruct
	public void postConstruct() {
		log.debug("EJB instance created for " + getClass().getSimpleName());
		S delegateService = EjbContextConfig.getApplicationContext().getBean(getServiceClass());
		log.debug("EJB instance delegate configured for " + getClass().getSimpleName() + ": " + delegateService);
		setDelegateService(delegateService);
	}

	abstract protected void setDelegateService(S delegateService);

	@SuppressWarnings("unchecked")
	protected Class<S> getServiceClass() {
		if (serviceClass == null) {
			Type genericSuperClass = getClass().getGenericSuperclass();
			while (genericSuperClass != null && !(genericSuperClass instanceof ParameterizedType)) {
				genericSuperClass = ((Class<?>)genericSuperClass).getGenericSuperclass();
			}
			ParameterizedType parameterizedType = (ParameterizedType)genericSuperClass;
			serviceClass = (Class<S>)parameterizedType.getActualTypeArguments()[0];
		}
		return serviceClass;
	}

}
