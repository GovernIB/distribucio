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
 * Constraint de validació a l'hora de guardar la configuració que controla 
 * que el valor sigui correcte per a la propietat que es vol modificar.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ConfigValidator.class)
public @interface Config {

	String message() default "Error en la validació de la configuració.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};


}
