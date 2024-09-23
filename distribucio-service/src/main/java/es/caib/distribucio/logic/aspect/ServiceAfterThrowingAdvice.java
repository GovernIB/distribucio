/**
 * 
 */
package es.caib.distribucio.logic.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import es.caib.distribucio.logic.intf.service.AplicacioService;

/**
 * Advice AspectJ que intercepta les excepcions llençades des dels
 * services.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Aspect
@Order(300)
@Component
public class ServiceAfterThrowingAdvice {

	@Autowired
	private AplicacioService aplicacioService;
	
	@AfterThrowing(pointcut="execution(* es.caib.distribucio.logic.service.*Service*.*(..))", throwing="exception")
	public void doAfterThrowing(JoinPoint joinPoint, Throwable exception) {
		aplicacioService.excepcioSave(exception, "Main application service");
	}
	
}
