/**
 * 
 */
package es.caib.distribucio.back.controller.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import es.caib.distribucio.logic.intf.service.AplicacioService;

/**
 * Advice AspectJ que intercepta les excepcions llençades des dels
 * controllers de l'aplicació principal.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Aspect
@Order(300)
@Component
public class ControllerAfterThrowingAdvice {

	@Autowired
	private AplicacioService aplicacioService;
	
	@AfterThrowing(pointcut="execution(* es.caib.distribucio.back.controller.*Controller*.*(..))", throwing="exception")
	public void doAfterThrowing(JoinPoint joinPoint, Throwable exception) {
		aplicacioService.excepcioSave(exception, "Main application controller");
	}
	
}
