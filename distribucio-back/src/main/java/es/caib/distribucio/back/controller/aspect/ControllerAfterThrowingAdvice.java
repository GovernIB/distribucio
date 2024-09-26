/**
 * 
 */
package es.caib.distribucio.back.controller.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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

//	@Autowired
//	private AplicacioService aplicacioService;
	
	@Autowired
	private ApplicationContext context;
	private static AplicacioService aplicacioService = null;
	
	@AfterThrowing(pointcut="execution(* es.caib.distribucio.back.controller.*Controller*.*(..))", throwing="exception")
	public void doAfterThrowing(JoinPoint joinPoint, Throwable exception) {
//		aplicacioService.excepcioSave(exception, "Main application controller");
		if (aplicacioService == null) {
			try {
				aplicacioService = context.getBean(AplicacioService.class);
			} catch(Exception e) {
				System.err.println("Error obtenint el bean AplicacioService per l'advice ControllerAfterThrowingAdvice.");
			}
		}
		if (aplicacioService != null) {
			aplicacioService.excepcioSave(exception, "Main application controller");			
		} else {
			System.out.println("aplicacioService.excepcioSave(exception, \"Main application controller\"");
		}
	}
	
}
