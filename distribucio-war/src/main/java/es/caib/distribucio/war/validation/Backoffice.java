/**
 * 
 */
package es.caib.distribucio.war.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi de backoffice i altres validacions per al backoffice.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=BackofficeValidator.class)
public @interface Backoffice {

	String message() default "Error en la validació del backoffice.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};


}
