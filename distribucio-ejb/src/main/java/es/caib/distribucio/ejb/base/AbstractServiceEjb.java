/**
 * 
 */
package es.caib.distribucio.ejb.base;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.SessionContext;

import lombok.extern.slf4j.Slf4j;

/**
 * Funcionalitat bàsica pels EJBs.
 *
 * @author Límit Tecnologies
 */
@Slf4j
public abstract class AbstractServiceEjb<S> {

	@Resource
	protected EJBContext ejbContext;
	@Resource
	protected SessionContext sessionContext;

	private Class<S> serviceClass;
	//private AplicacioService aplicacioService = null;

	@PostConstruct
	public void postConstruct() {
		log.debug("EJB instance created for " + getClass().getSimpleName());
		S delegateService = EjbContextConfig.getApplicationContext().getBean(getServiceClass());
		//aplicacioService = EjbContextConfig.getApplicationContext().getBean(AplicacioService.class);
		log.debug("EJB instance delegate configured for " + getClass().getSimpleName() + ": " + delegateService);
		setDelegateService(delegateService);
	}

	/*@AroundInvoke
	protected Object beanAroundInvoke(InvocationContext ic) throws Exception {
		Authentication auth = aplicacioService.getAuthentication();
		if (auth != null) {
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
		return ic.proceed();
	}*/

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

	abstract protected void setDelegateService(S delegateService);

}
